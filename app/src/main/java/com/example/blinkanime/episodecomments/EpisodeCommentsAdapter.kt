package com.example.blinkanime.episodecomments

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.blinkanime.R
import com.example.blinkanime.misc.Comments
import com.example.blinkanime.misc.MiscFunc
import com.example.blinkanime.misc.ThreadData
import com.example.blinkanime.newsFragment.CardSettings
import com.google.android.material.progressindicator.CircularProgressIndicator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.bush.translator.Language
import me.bush.translator.Translator

data class CommentSettings (
    val translate: Boolean = false,
)

class EpisodeCommentsAdapter(animeEpisodes: ThreadData) : RecyclerView.Adapter<EpisodeCommentsAdapter.RecommendationViewHolder>() {

    private var data = animeEpisodes
    var commentSettings = CommentSettings()

    private val translator = Translator()

    class RecommendationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val message: TextView = itemView.findViewById(R.id.episdoe_comment)
        val username: TextView = itemView.findViewById(R.id.episode_comment_username)
        val date: TextView = itemView.findViewById(R.id.episdoe_comment_date)
        val profileImg: ImageView = itemView.findViewById(R.id.user_image)
        val progressIndicator: CircularProgressIndicator = itemView.findViewById(R.id.progress_circular)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecommendationViewHolder {
        val textView = LayoutInflater.from(parent.context)
            .inflate(R.layout.comments_item, parent, false) as CardView
        return RecommendationViewHolder(textView)
    }

    override fun onBindViewHolder(holder: RecommendationViewHolder, position: Int) {
        if (data.comments?.get(position)?.message?.isEmpty() == true)
            return

        val currentData = data.comments?.get(position)
        holder.username.text = currentData?.user
        holder.date.text = currentData?.date

        if (currentData?.userImage?.isNotEmpty() == true)
            MiscFunc().loadAndTransformImage(currentData?.userImage?:"", holder.profileImg, "normal", 200, 200)

        holder.progressIndicator.visibility = View.VISIBLE
        if (!commentSettings.translate) {
            holder.progressIndicator.visibility = View.GONE
            holder.message.text = currentData?.message
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            val translated = translator.translate(currentData?.message?:"", Language.ARABIC, Language.ENGLISH)
            withContext(Dispatchers.Main) {
                holder.progressIndicator.visibility = View.GONE
                holder.message.text = translated.translatedText
            }
        }
    }


    override fun getItemCount() = data.comments?.size?:0

    fun updateData(newData: ThreadData) {
        data = newData
        notifyDataSetChanged()
    }
}