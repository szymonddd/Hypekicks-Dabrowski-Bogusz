package com.example.hypekicks_dabrowski_bogusz

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class AdminPanelActivity : AppCompatActivity() {

    private lateinit var editBrand: EditText
    private lateinit var editModel: EditText
    private lateinit var editYear: EditText
    private lateinit var editPrice: EditText
    private lateinit var editUrl: EditText
    private lateinit var addButton: Button
    private lateinit var adminListView: ListView

    private val db = FirebaseFirestore.getInstance()
    private val sneakerList = mutableListOf<Sneaker>()
    private lateinit var adapter: ArrayAdapter<String>
    private val displayList = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_panel)

        editBrand = findViewById(R.id.editBrand)
        editModel = findViewById(R.id.editModel)
        editYear = findViewById(R.id.editYear)
        editPrice = findViewById(R.id.editPrice)
        editUrl = findViewById(R.id.editUrl)
        addButton = findViewById(R.id.addButton)
        adminListView = findViewById(R.id.adminListView)

        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, displayList)
        adminListView.adapter = adapter

        loadSneakers()

        addButton.setOnClickListener { addSneaker() }

        adminListView.setOnItemLongClickListener { _, _, position, _ ->
            val sneaker = sneakerList[position]
            AlertDialog.Builder(this)
                .setTitle("Usuń buta")
                .setMessage("Czy na pewno chcesz usunąć ${sneaker.brand} ${sneaker.modelName}?")
                .setPositiveButton("Usuń") { _, _ -> deleteSneaker(sneaker) }
                .setNegativeButton("Anuluj", null)
                .show()
            true
        }
    }

    private fun loadSneakers() {
        db.collection("sneakers")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Toast.makeText(this, "Błąd ładowania danych", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }
                sneakerList.clear()
                displayList.clear()
                snapshot?.documents?.forEach { doc ->
                    val sneaker = Sneaker(
                        id = doc.id,
                        brand = doc.getString("brand") ?: "",
                        modelName = doc.getString("modelName") ?: "",
                        releaseYear = (doc.getLong("releaseYear") ?: 0).toInt(),
                        resellPrice = doc.getDouble("resellPrice") ?: 0.0,
                        imageUrl = doc.getString("imageUrl") ?: ""
                    )
                    sneakerList.add(sneaker)
                    displayList.add("${sneaker.brand} ${sneaker.modelName} (${sneaker.releaseYear}) - ${sneaker.resellPrice} PLN")
                }
                adapter.notifyDataSetChanged()
            }
    }

    private fun addSneaker() {
        val brand = editBrand.text.toString().trim()
        val model = editModel.text.toString().trim()
        val yearStr = editYear.text.toString().trim()
        val priceStr = editPrice.text.toString().trim()
        val url = editUrl.text.toString().trim()

        if (brand.isEmpty() || model.isEmpty() || yearStr.isEmpty() || priceStr.isEmpty() || url.isEmpty()) {
            Toast.makeText(this, "Wypełnij wszystkie pola!", Toast.LENGTH_SHORT).show()
            return
        }

        val year = yearStr.toIntOrNull()
        val price = priceStr.toDoubleOrNull()

        if (year == null || price == null) {
            Toast.makeText(this, "Rok i cena muszą być liczbami!", Toast.LENGTH_SHORT).show()
            return
        }

        val sneakerData = hashMapOf(
            "brand" to brand,
            "modelName" to model,
            "releaseYear" to year,
            "resellPrice" to price,
            "imageUrl" to url
        )

        db.collection("sneakers").add(sneakerData)
            .addOnSuccessListener {
                Toast.makeText(this, "Dodano buta do bazy!", Toast.LENGTH_SHORT).show()
                clearForm()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Błąd: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun deleteSneaker(sneaker: Sneaker) {
        db.collection("sneakers").document(sneaker.id).delete()
            .addOnSuccessListener {
                Toast.makeText(this, "Usunięto ${sneaker.brand} ${sneaker.modelName}", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Błąd usuwania: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun clearForm() {
        editBrand.text.clear()
        editModel.text.clear()
        editYear.text.clear()
        editPrice.text.clear()
        editUrl.text.clear()
    }
}
