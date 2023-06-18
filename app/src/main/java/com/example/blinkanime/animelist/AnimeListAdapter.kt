package com.example.blinkanime.animelist

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.blinkanime.R
import com.example.blinkanime.mainFragment.Anime
import com.example.blinkanime.mainFragment.detailsActivity.DetailsActivity
import com.example.blinkanime.misc.MiscFunc

class AnimeListAdapter(animeList: List<Anime>): RecyclerView.Adapter<AnimeListAdapter.AnimeListHolder>() {

    private var data = animeList

    class AnimeListHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val animeTitle: TextView = itemView.findViewById(R.id.animeName)
        val animeType: TextView = itemView.findViewById(R.id.animeType)
        val animeStatus: TextView = itemView.findViewById(R.id.animeStatus)
        val animeImage: ImageView = itemView.findViewById(R.id.animeImage)
        val animeCard: RelativeLayout = itemView.findViewById(R.id.animeItemLayout)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnimeListHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.anime_list_item, parent, false)
        return AnimeListHolder(view)
    }

    override fun onBindViewHolder(holder: AnimeListHolder, position: Int) {
        val currentItem = data[position]
        holder.animeTitle.text = currentItem.name
        holder.animeType.text = currentItem.description
        holder.animeStatus.text = currentItem.episodeNumber
        MiscFunc().loadAndTransformImage(currentItem.image, holder.animeImage, "normal", 800, 1200)

        holder.animeCard.setOnClickListener {
            detailsActivity(holder, position)
        }
    }

    private fun detailsActivity(holder: AnimeListHolder, position: Int) {
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