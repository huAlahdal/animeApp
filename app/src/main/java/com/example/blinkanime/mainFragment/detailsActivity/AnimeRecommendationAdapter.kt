package com.example.blinkanime.mainFragment.detailsActivity

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.blinkanime.R
import com.example.blinkanime.mainFragment.Anime
import com.example.blinkanime.misc.MiscFunc

class AnimeRecommendationAdapter(animeEpisodes: List<Anime>) : RecyclerView.Adapter<AnimeRecommendationAdapter.RecommendationViewHolder>() {

    private var data = animeEpisodes

    class RecommendationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val animeImage: ImageView = itemView.findViewById(R.id.anime_image)
        val animeTitle: TextView = itemView.findViewById(R.id.anime_title)
        val animeReason: TextView = itemView.findViewById(R.id.anime_reason)
        val showButton: Button = itemView.findViewById(R.id.show_more)
        val recoCount: TextView = itemView.findViewById(R.id.anime_recommended)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecommendationViewHolder {
        val textView = LayoutInflater.from(parent.context)
            .inflate(R.layout.similer_anime_item, parent, false) as CardView
        return RecommendationViewHolder(textView)
    }

    override fun onBindViewHolder(holder: RecommendationViewHolder, position: Int) {
        MiscFunc().loadAndTransformImage(data[position].image, holder.animeImage, "normal", 400, 600)
        holder.animeTitle.text = data[position].name
        holder.animeReason.text = data[position].description
        val recoCount = data[position].episodeNumber.toInt() + 1
        holder.recoCount.text = "Recommended $recoCount times"

        val density = holder.animeReason.context.resources.displayMetrics.density
        val pixelHeight = (130 * density).toInt()

        holder.showButton.setOnClickListener {
            // set animeReason height to wrap_content
            if (holder.animeReason.layoutParams.height == ViewGroup.LayoutParams.WRAP_CONTENT) {
                holder.animeReason.layoutParams.height = pixelHeight
                holder.showButton.text = "Show More"
            } else {
                holder.animeReason.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
                holder.showButton.text = "Show Less"
            }
            holder.animeReason.requestLayout()
        }

        // if text is too long set button to visible
        if (holder.animeReason.text.length > 300)
            holder.showButton.visibility = View.VISIBLE
        else
            holder.showButton.visibility = View.GONE

    }

    override fun getItemCount() = data.size

    fun updateData(newData: List<Anime>) {
        data = newData
        notifyDataSetChanged()
    }
}