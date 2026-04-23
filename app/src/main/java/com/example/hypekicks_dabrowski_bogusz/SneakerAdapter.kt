package com.example.hypekicks_dabrowski_bogusz

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide

class SneakerAdapter(
    private val context: Context,
    private val sneakers: List<Sneaker>
) : BaseAdapter() {

    override fun getCount() = sneakers.size
    override fun getItem(position: Int) = sneakers[position]
    override fun getItemId(position: Int) = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.item_sneaker, parent, false)

        val sneaker = sneakers[position]

        val imageView = view.findViewById<ImageView>(R.id.sneakerImage)
        val brandText = view.findViewById<TextView>(R.id.brandText)
        val modelText = view.findViewById<TextView>(R.id.modelText)

        brandText.text = sneaker.brand
        modelText.text = sneaker.modelName

        Glide.with(context)
            .load(sneaker.imageUrl)
            .placeholder(android.R.drawable.ic_menu_gallery)
            .into(imageView)

        return view
    }
}
