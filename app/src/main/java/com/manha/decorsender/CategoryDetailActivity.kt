package com.manha.decorsender

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.manha.decorsender.data.AppDatabase
import com.manha.decorsender.data.PhotoLink
import com.manha.decorsender.databinding.ActivityCategoryDetailBinding
import com.manha.decorsender.databinding.DialogAddPhotoLinkBinding
import com.manha.decorsender.drive.DriveDownloader
import kotlinx.coroutines.launch

class CategoryDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCategoryDetailBinding
    private lateinit var db: AppDatabase
    private lateinit var adapter: PhotoLinkAdapter
    private var categoryId: Long = -1
    private var categoryName: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCategoryDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        categoryId = intent.getLongExtra("categoryId", -1)
        categoryName = intent.getStringExtra("categoryName").orEmpty()

        setSupportActionBar(binding.toolbar)
        binding.toolbar.title = categoryName
        binding.toolbar.setNavigationOnClickListener { finish() }

        db = AppDatabase.getInstance(this)

        adapter = PhotoLinkAdapter(emptyList()) { link -> confirmDelete(link) }
        binding.rvLinks.layoutManager = LinearLayoutManager(this)
        binding.rvLinks.adapter = adapter

        binding.fabAddLink.setOnClickListener { showAddDialog() }
    }

    override fun onResume() {
        super.onResume()
        loadLinks()
    }

    private fun loadLinks() {
        lifecycleScope.launch {
            val links = db.photoLinkDao().getForCategory(categoryId)
            adapter.submitList(links)
            binding.tvEmpty.visibility = if (links.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    private fun showAddDialog() {
        val dialogBinding = DialogAddPhotoLinkBinding.inflate(LayoutInflater.from(this))
        AlertDialog.Builder(this)
            .setTitle("Add Google Drive Photo Link")
            .setView(dialogBinding.root)
            .setPositiveButton("Add") { _, _ ->
                val url = dialogBinding.etDriveLink.text?.toString()?.trim().orEmpty()
                if (!DriveDownloader.isValidDriveLink(url)) {
                    Toast.makeText(this, "Valid Google Drive share link paste karein", Toast.LENGTH_LONG).show()
                    return@setPositiveButton
                }
                lifecycleScope.launch {
                    db.photoLinkDao().insert(PhotoLink(categoryId = categoryId, driveUrl = url))
                    loadLinks()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun confirmDelete(link: PhotoLink) {
        AlertDialog.Builder(this)
            .setTitle("Delete Photo Link")
            .setMessage("Ye photo link category se hata dein?")
            .setPositiveButton("Delete") { _, _ ->
                lifecycleScope.launch {
                    db.photoLinkDao().delete(link)
                    loadLinks()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
