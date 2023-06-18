package com.example.blinkanime.mainFragment

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.blinkanime.R
import com.example.blinkanime.mainFragment.detailsActivity.DetailsActivity
import com.squareup.picasso.Picasso

class SeasonAnimeAdapter(private val dataSample: List<Anime>) : RecyclerView.Adapter<SeasonAnimeAdapter.SeasonAnimeViewHolder>() {

    private var data = dataSample

    class SeasonAnimeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val animeName: TextView = itemView.findViewById(R.id.anime_name)
        val animeImage: ImageView = itemView.findViewById(R.id.anime_image)
        val animeCard: ConstraintLayout = itemView.findViewById(R.id.anime_card)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SeasonAnimeViewHolder {
        val textView = LayoutInflater.from(parent.context)
            .inflate(R.layout.anime_recycle_item, parent, false) as CardView
        return SeasonAnimeViewHolder(textView)
    }

    override fun onBindViewHolder(holder: SeasonAnimeViewHolder, position: Int) {
        holder.animeName.text = data[position].name
        // use picasso to load image
        Picasso.get()
            .load(data[position].image)
            .resize(400, 600)
            .into(holder.animeImage)

        holder.animeCard.setOnClickListener {
            detailsActivity(holder, position)
        }

    }

    private fun detailsActivity(holder: SeasonAnimeViewHolder, position: Int) {
        // start details activity
        val intent = Intent(holder.animeCard.context, DetailsActivity::class.java)
        intent.putExtra("animeLink", data[position].link)
        holder.animeCard.context.startActivity(intent)
    }

    override fun getItemCount() = data.size

    fun updateData(newData: List<Anime>) {
        data = newData
        notifyDataSetChanged()
    }
}