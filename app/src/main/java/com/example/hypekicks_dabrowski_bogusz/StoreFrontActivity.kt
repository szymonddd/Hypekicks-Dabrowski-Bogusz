package com.example.hypekicks_dabrowski_bogusz

import android.content.Intent
import android.os.Bundle
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.hypekicks_dabrowski_bogusz.databinding.ActivityStorefrontBinding
import com.google.firebase.firestore.FirebaseFirestore

class StoreFrontActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStorefrontBinding
    private val db = FirebaseFirestore.getInstance()
    private val allSneakers = mutableListOf<Sneaker>()
    private val filteredSneakers = mutableListOf<Sneaker>()
    private lateinit var adapter: SneakerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStorefrontBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = SneakerAdapter(this, filteredSneakers)
        binding.sneakerGrid.adapter = adapter

        loadSneakers()

        binding.sneakerGrid.setOnItemClickListener { _, _, position, _ ->
            val sneaker = filteredSneakers[position]
            val intent = Intent(this, DetailsActivity::class.java)
            intent.putExtra("SNEAKER", sneaker)
            startActivity(intent)
        }

        binding.adminButton.setOnClickListener {
            startActivity(Intent(this, AdminPanelActivity::class.java))
        }

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?) = false
            override fun onQueryTextChange(newText: String?): Boolean {
                filterSneakers(newText ?: "")
                return true
            }
        })
    }

    override fun onResume() {
        super.onResume()
        binding.searchView.setQuery("", false)
        binding.searchView.clearFocus()
        filterSneakers("")
    }

    private fun loadSneakers() {
        db.collection("sneakers")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Toast.makeText(this, "Błąd ładowania", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }
                allSneakers.clear()
                snapshot?.documents?.forEach { doc ->
                    val sneaker = Sneaker(
                        id = doc.id,
                        brand = doc.getString("brand") ?: "",
                        modelName = doc.getString("modelName") ?: "",
                        releaseYear = (doc.getLong("releaseYear") ?: 0).toInt(),
                        resellPrice = doc.getDouble("resellPrice") ?: 0.0,
                        imageUrl = doc.getString("imageUrl") ?: ""
                    )
                    allSneakers.add(sneaker)
                }
                filterSneakers(binding.searchView.query.toString())
            }
    }

    private fun filterSneakers(query: String) {
        filteredSneakers.clear()
        filteredSneakers.addAll(
            if (query.isEmpty()) allSneakers
            else allSneakers.filter { it.modelName.contains(query, ignoreCase = true) }
        )
        adapter.notifyDataSetChanged()
    }
}
