package com.manha.decorsender

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.manha.decorsender.data.AppDatabase
import com.manha.decorsender.data.Category
import com.manha.decorsender.data.SentHistory
import com.manha.decorsender.databinding.ActivityMainBinding
import com.manha.decorsender.drive.DriveDownloader
import com.manha.decorsender.utils.PhoneValidator
import com.manha.decorsender.whatsapp.SendResult
import com.manha.decorsender.whatsapp.WhatsAppSender
import kotlinx.coroutines.launch
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var db: AppDatabase
    private var categories: List<Category> = emptyList()
    private var selectedCategory: Category? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        db = AppDatabase.getInstance(this)

        binding.dropdownCategory.setOnItemClickListener { _, _, position, _ ->
            selectedCategory = categories[position]
            updatePhotoCount()
        }

        binding.btnManageCategories.setOnClickListener {
            startActivity(Intent(this, CategoryManagerActivity::class.java))
        }

        binding.btnHistory.setOnClickListener {
            startActivity(Intent(this, HistoryActivity::class.java))
        }

        binding.btnSend.setOnClickListener { onSendClicked() }
    }

    override fun onResume() {
        super.onResume()
        loadCategories()
    }

    private fun loadCategories() {
        lifecycleScope.launch {
            categories = db.categoryDao().getAll()
            val names = categories.map { it.name }
            val adapter = ArrayAdapter(this@MainActivity, android.R.layout.simple_list_item_1, names)
            binding.dropdownCategory.setAdapter(adapter)

            // keep selection if still valid
            selectedCategory = categories.find { it.id == selectedCategory?.id }
            if (selectedCategory == null) {
                binding.dropdownCategory.setText("", false)
                binding.tvPhotoCount.text = ""
            } else {
                binding.dropdownCategory.setText(selectedCategory!!.name, false)
                updatePhotoCount()
            }
        }
    }

    private fun updatePhotoCount() {
        val cat = selectedCategory ?: return
        lifecycleScope.launch {
            val count = db.photoLinkDao().countForCategory(cat.id)
            binding.tvPhotoCount.text = "$count photo(s) is category me hai"
        }
    }

    private fun onSendClicked() {
        val rawPhone = binding.etPhone.text?.toString().orEmpty()
        val clientName = binding.etClientName.text?.toString()?.trim().orEmpty()
        val normalized = PhoneValidator.normalizeIndianNumber(rawPhone)

        if (normalized == null) {
            binding.etPhone.error = "Valid 10-digit Indian mobile number likhein"
            return
        }
        val cat = selectedCategory
        if (cat == null) {
            Toast.makeText(this, "Pehle category select karein", Toast.LENGTH_SHORT).show()
            return
        }

        setLoading(true)
        lifecycleScope.launch {
            val links = db.photoLinkDao().getForCategory(cat.id)
            if (links.isEmpty()) {
                setLoading(false)
                Toast.makeText(this@MainActivity, "Is category me koi photo link nahi hai", Toast.LENGTH_SHORT).show()
                return@launch
            }

            val files = mutableListOf<File>()
            for ((index, link) in links.withIndex()) {
                val file = DriveDownloader.downloadToCache(this@MainActivity, link.driveUrl, "${cat.name}_$index")
                if (file != null) files.add(file)
            }

            setLoading(false)

            if (files.isEmpty()) {
                Toast.makeText(this@MainActivity, "Photos download nahi ho payi. Drive links check karein (Anyone with link access hona chahiye).", Toast.LENGTH_LONG).show()
                db.sentHistoryDao().insert(
                    SentHistory(clientName = clientName, phoneNumber = normalized, categoryName = cat.name, photoCount = 0, status = "FAILED")
                )
                return@launch
            }

            val result = WhatsAppSender.sendImages(this@MainActivity, normalized, files)
            when (result) {
                is SendResult.Success -> {
                    db.sentHistoryDao().insert(
                        SentHistory(clientName = clientName, phoneNumber = normalized, categoryName = cat.name, photoCount = files.size, status = "SUCCESS")
                    )
                    Toast.makeText(this@MainActivity, "WhatsApp khul gaya, ab Send tap karein.", Toast.LENGTH_LONG).show()
                }
                is SendResult.WhatsAppNotInstalled -> {
                    Toast.makeText(this@MainActivity, "WhatsApp is device par install nahi hai.", Toast.LENGTH_LONG).show()
                }
                is SendResult.NoImages -> {
                    Toast.makeText(this@MainActivity, "Koi photo download nahi hui.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setLoading(loading: Boolean) {
        binding.progressOverlay.visibility = if (loading) View.VISIBLE else View.GONE
        binding.btnSend.isEnabled = !loading
        binding.btnSend.text = if (loading) "Photos taiyar ho rahi hain..." else "Send Photos on WhatsApp"
    }
}
