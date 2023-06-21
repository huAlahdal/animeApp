package com.example.blinkanime.topanimes

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.blinkanime.R
import com.example.blinkanime.episodecomments.CommentSettings
import com.example.blinkanime.episodecomments.EpisodeCommentsAdapter
import com.example.blinkanime.mainFragment.Anime
import com.example.blinkanime.mainFragment.AnimeDetails
import com.example.blinkanime.mainFragment.detailsActivity.DetailsActivity
import com.example.blinkanime.misc.MiscFunc
import com.example.blinkanime.misc.ThreadData
import com.google.android.material.progressindicator.CircularProgressIndicator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.bush.translator.Language
import me.bush.translator.Translator

class TopAnimeAdapter(animeData: List<AnimeDetails>) : RecyclerView.Adapter<TopAnimeAdapter.TopAnimeViewHolder>() {
    private var data = animeData

    class TopAnimeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val animeTitle: TextView = itemView.findViewById(R.id.anime_name)
        val animeRank: TextView = itemView.findViewById(R.id.anime_rank)
        val animeScore: TextView = itemView.findViewById(R.id.anime_score)
        val animeImage: ImageView = itemView.findViewById(R.id.anime_image)
        val animeType: TextView = itemView.findViewById(R.id.anime_type)
        val animeMembers: TextView = itemView.findViewById(R.id.anime_status)
        val animeEpCount: TextView = itemView.findViewById(R.id.anime_length)
        val animeDate: TextView = itemView.findViewById(R.id.anime_source)
        val animePop: TextView = itemView.findViewById(R.id.anime_popularity)

        val linearLayout: LinearLayout = itemView.findViewById(R.id.top_anime_list)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopAnimeViewHolder {
        val textView = LayoutInflater.from(parent.context)
            .inflate(R.layout.top_anime_list, parent, false) as CardView
        return TopAnimeViewHolder(textView)
    }

    override fun onBindViewHolder(holder: TopAnimeViewHolder, position: Int) {
        holder.animeTitle.text = data[position].name
        holder.animeRank.text = "#" + data[position].malDetails?.ranked
        holder.animeScore.text = data[position].malDetails?.score
        holder.animeType.text = "النوع: " + data[position].malDetails?.MALId
        holder.animeMembers.text = "الشعبية: " + data[position].malDetails?.description
        holder.animePop.text = "الشعبية: " + data[position].malDetails?.description
        holder.animeEpCount.text = "الحلقات: " + data[position].malDetails?.episodesLink
        holder.animeDate.text = data[position].malDetails?.popularity + " - " + data[position].malDetails?.studio

        MiscFunc().loadAndTransformImage(data[position].image?:"", holder.animeImage,
            "noRound", 600, 900)

        holder.linearLayout.setOnClickListener {
            // start details activity
            startActivity(it, data[position].MAL_Link?:"")
        }
    }

    private fun startActivity(view: View, animeLink: String) {
        val intent = Intent(view.context, DetailsActivity::class.java).apply {
            putExtra("MALLink", animeLink)
        }
        view.context.startActivity(intent)
    }


    override fun getItemCount() = data.size

    fun updateData(newData: List<AnimeDetails>) {
        data = newData
        notifyDataSetChanged()
    }
}