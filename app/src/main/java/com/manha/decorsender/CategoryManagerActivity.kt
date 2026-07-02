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
import com.manha.decorsender.data.Category
import com.manha.decorsender.databinding.ActivityCategoryManagerBinding
import com.manha.decorsender.databinding.DialogAddCategoryBinding
import kotlinx.coroutines.launch

class CategoryManagerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCategoryManagerBinding
    private lateinit var db: AppDatabase
    private lateinit var adapter: CategoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCategoryManagerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        binding.toolbar.setNavigationOnClickListener { finish() }

        db = AppDatabase.getInstance(this)

        adapter = CategoryAdapter(
            emptyList(),
            onClick = { category ->
                val intent = android.content.Intent(this, CategoryDetailActivity::class.java)
                intent.putExtra("categoryId", category.id)
                intent.putExtra("categoryName", category.name)
                startActivity(intent)
            },
            onDelete = { category -> confirmDelete(category) }
        )
        binding.rvCategories.layoutManager = LinearLayoutManager(this)
        binding.rvCategories.adapter = adapter

        binding.fabAddCategory.setOnClickListener { showAddDialog() }
    }

    override fun onResume() {
        super.onResume()
        loadCategories()
    }

    private fun loadCategories() {
        lifecycleScope.launch {
            val cats = db.categoryDao().getAll()
            val withCounts = cats.map { it to db.photoLinkDao().countForCategory(it.id) }
            adapter.submitList(withCounts)
            binding.tvEmpty.visibility = if (cats.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    private fun showAddDialog() {
        val dialogBinding = DialogAddCategoryBinding.inflate(LayoutInflater.from(this))
        AlertDialog.Builder(this)
            .setTitle("New Category")
            .setView(dialogBinding.root)
            .setPositiveButton("Add") { _, _ ->
                val name = dialogBinding.etCategoryName.text?.toString()?.trim().orEmpty()
                if (name.isEmpty()) {
                    Toast.makeText(this, "Category name likhein", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }
                lifecycleScope.launch {
                    if (db.categoryDao().countByName(name) > 0) {
                        Toast.makeText(this@CategoryManagerActivity, "Ye category pehle se hai", Toast.LENGTH_SHORT).show()
                    } else {
                        db.categoryDao().insert(Category(name = name))
                        loadCategories()
                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun confirmDelete(category: Category) {
        AlertDialog.Builder(this)
            .setTitle("Delete Category")
            .setMessage("\"${category.name}\" delete karein? Ismein saved saare photo links bhi delete ho jayenge.")
            .setPositiveButton("Delete") { _, _ ->
                lifecycleScope.launch {
                    db.categoryDao().delete(category)
                    loadCategories()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
