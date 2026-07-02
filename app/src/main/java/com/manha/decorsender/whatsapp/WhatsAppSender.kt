package com.manha.decorsender.whatsapp

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import java.io.File

sealed class SendResult {
    object Success : SendResult()
    object WhatsAppNotInstalled : SendResult()
    object NoImages : SendResult()
}

object WhatsAppSender {

    /**
     * Opens WhatsApp directly on the given number's chat with the image(s)
     * already attached. User only needs to tap WhatsApp's own "Send" button
     * once (Android does not allow apps to auto-press buttons inside another
     * app, so this final tap is unavoidable and is NOT automated by this app).
     *
     * normalizedNumber must be in "91XXXXXXXXXX" format (see PhoneValidator).
     */
    fun sendImages(context: Context, normalizedNumber: String, imageFiles: List<File>): SendResult {
        if (imageFiles.isEmpty()) return SendResult.NoImages

        val uris = ArrayList<android.net.Uri>()
        for (f in imageFiles) {
            val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", f)
            uris.add(uri)
        }

        val baseIntent = Intent().apply {
            if (uris.size == 1) {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_STREAM, uris[0])
            } else {
                action = Intent.ACTION_SEND_MULTIPLE
                putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris)
            }
            type = "image/jpeg"
            putExtra("jid", "$normalizedNumber@s.whatsapp.net")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        // Try regular WhatsApp first, then WhatsApp Business.
        for (pkg in listOf("com.whatsapp", "com.whatsapp.w4b")) {
            try {
                val intent = Intent(baseIntent).setPackage(pkg)
                context.startActivity(intent)
                return SendResult.Success
            } catch (e: ActivityNotFoundException) {
                // try next package
            }
        }
        return SendResult.WhatsAppNotInstalled
    }
}
