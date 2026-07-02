package com.manha.decorsender.drive

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern

/**
 * Handles converting a Google Drive "share" link into a direct-download link
 * and downloading the actual image bytes to local cache storage so it can
 * be attached to a WhatsApp share intent.
 *
 * IMPORTANT: The Drive files must be shared as "Anyone with the link -> Viewer".
 */
object DriveDownloader {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .followRedirects(true)
        .followSslRedirects(true)
        .build()

    private val idPatterns = listOf(
        Pattern.compile("/file/d/([a-zA-Z0-9_-]+)"),
        Pattern.compile("[?&]id=([a-zA-Z0-9_-]+)"),
        Pattern.compile("/d/([a-zA-Z0-9_-]+)")
    )

    fun extractFileId(url: String): String? {
        for (p in idPatterns) {
            val m = p.matcher(url)
            if (m.find()) return m.group(1)
        }
        return null
    }

    fun isValidDriveLink(url: String): Boolean {
        return url.contains("drive.google.com") && extractFileId(url) != null
    }

    /**
     * Downloads a single Drive file to app cache and returns the local File.
     * Returns null if the download failed.
     */
    suspend fun downloadToCache(context: Context, driveUrl: String, fileNamePrefix: String): File? =
        withContext(Dispatchers.IO) {
            val fileId = extractFileId(driveUrl) ?: return@withContext null
            val dir = File(context.cacheDir, "drive_images").apply { mkdirs() }
            val outFile = File(dir, "${fileNamePrefix}_${System.currentTimeMillis()}.jpg")

            try {
                var body = fetch("https://drive.google.com/uc?export=download&id=$fileId")
                    ?: return@withContext null

                // Large files show an interstitial "can't scan for viruses" HTML page
                // with a confirm token. Detect that and retry with the token.
                if (looksLikeHtml(body)) {
                    val token = extractConfirmToken(String(body, Charsets.ISO_8859_1))
                    if (token != null) {
                        body = fetch("https://drive.google.com/uc?export=download&confirm=$token&id=$fileId")
                            ?: return@withContext null
                    }
                }

                if (looksLikeHtml(body) || body.isEmpty()) return@withContext null

                outFile.writeBytes(body)
                outFile
            } catch (e: Exception) {
                null
            }
        }

    private fun fetch(url: String): ByteArray? {
        val request = Request.Builder().url(url).build()
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) return null
            return response.body?.bytes()
        }
    }

    private fun looksLikeHtml(bytes: ByteArray): Boolean {
        if (bytes.size < 15) return true
        val head = String(bytes, 0, minOf(200, bytes.size), Charsets.ISO_8859_1).lowercase()
        return head.contains("<!doctype html") || head.contains("<html")
    }

    private fun extractConfirmToken(html: String): String? {
        val m = Pattern.compile("confirm=([0-9A-Za-z_]+)").matcher(html)
        if (m.find()) return m.group(1)
        return null
    }
}
