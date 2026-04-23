package com.example.hypekicks_dabrowski_bogusz

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.hypekicks_dabrowski_bogusz.databinding.ActivityDetailsBinding

class DetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sneaker = intent.getSerializableExtra("SNEAKER") as? Sneaker

        sneaker?.let {
            binding.brandDetail.text = it.brand
            binding.modelDetail.text = it.modelName
            binding.priceDetail.text = "${it.resellPrice} PLN"

            Glide.with(this).load(it.imageUrl).into(binding.bigSneakerImage)
        }

        binding.backButton.setOnClickListener { finish() }
    }
}