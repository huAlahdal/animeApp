package com.example.blinkanime.mainFragment.detailsActivity

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.blinkanime.R
import com.example.blinkanime.mainFragment.Anime
import com.example.blinkanime.misc.ThreadData

class AnimeReviewAdapter(animeEpisodes: ThreadData) : RecyclerView.Adapter<AnimeReviewAdapter.AnimeReviewViewHolder>() {

    private var data = animeEpisodes

    class AnimeReviewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnimeReviewViewHolder {
        val textView = LayoutInflater.from(parent.context)
            .inflate(R.layout.similer_anime_item, parent, false) as CardView
        return AnimeReviewViewHolder(textView)
    }

    override fun onBindViewHolder(holder: AnimeReviewViewHolder, position: Int) {


    }

    override fun getItemCount() = data.reviews?.size ?: 0

    fun updateData(newData: ThreadData) {
        data = newData
        notifyDataSetChanged()
    }
}