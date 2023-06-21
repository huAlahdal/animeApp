package com.example.blinkanime.animeRecommendationActivity

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.blinkanime.R
import com.example.blinkanime.mainFragment.Anime
import com.example.blinkanime.mainFragment.detailsActivity.DetailsActivity
import com.example.blinkanime.misc.MALAnimeData
import com.example.blinkanime.misc.MiscFunc
import com.example.blinkanime.misc.RecommendationData
import com.google.android.material.progressindicator.CircularProgressIndicator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.bush.translator.Language
import me.bush.translator.Translation
import me.bush.translator.Translator

data class ReasonSettings (
    val translate: Boolean = false,
)

class AnimeRecommendationAdapter(animeList: RecommendationData?): RecyclerView.Adapter<AnimeRecommendationAdapter.AnimeRecommendationHolder>() {

    private var data = animeList
    var reasonSettings = ReasonSettings()

    private val translator = Translator()

    class AnimeRecommendationHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val leftImage: ImageView = itemView.findViewById(R.id.left_image)
        val leftTitle: TextView = itemView.findViewById(R.id.left_title)

        val rightImage: ImageView = itemView.findViewById(R.id.right_image)
        val rightTitle: TextView = itemView.findViewById(R.id.right_title)

        val showReason: Button = itemView.findViewById(R.id.showButton)
        val overview: TextView = itemView.findViewById(R.id.recommendation_overview)

        val progressIndicator: CircularProgressIndicator = itemView.findViewById(R.id.progress_circular)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnimeRecommendationHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recommendation_item, parent, false)
        return AnimeRecommendationHolder(view)
    }

    override fun onBindViewHolder(holder: AnimeRecommendationHolder, position: Int) {

        if (data == null)
            return

        holder.leftTitle.text = data!!.anime[position][0].name
        holder.rightTitle.text = data!!.anime[position][1].name

        MiscFunc().loadAndTransformImage(data!!.anime[position][0].image, holder.leftImage, "normal", 350, 600)
        MiscFunc().loadAndTransformImage(data!!.anime[position][1].image, holder.rightImage, "normal", 350, 600)


        if (reasonSettings.translate) {
            if (holder.overview.visibility == View.VISIBLE) {
                holder.progressIndicator.visibility = View.VISIBLE
            }
            CoroutineScope(Dispatchers.IO).launch {
                val translated =
                    translator.translate(data!!.anime[position][0].description, Language.ARABIC, Language.ENGLISH)
                withContext(Dispatchers.Main) {
                    holder.progressIndicator.visibility = View.GONE
                    holder.overview.text = translated.translatedText
                }
            }
        } else {
            holder.progressIndicator.visibility = View.GONE
            holder.overview.text = data!!.anime[position][0].description
        }

        holder.leftImage.setOnClickListener {
            // start anime details activity
            val animeLink = data!!.anime[position][0].link
            startActivity(it, animeLink)
        }

        holder.rightImage.setOnClickListener {
            // start anime details activity
            val animeLink = data!!.anime[position][1].link
            startActivity(it, animeLink)
        }

        holder.showReason.setOnClickListener {
            if (holder.overview.visibility == View.GONE) {
                holder.overview.visibility = View.VISIBLE
                holder.showReason.text = "Hide"

            } else {
                holder.overview.visibility = View.GONE
                holder.showReason.text = "Show Reason"
            }
        }


    }

    private fun startActivity(view: View, animeLink: String) {
        val intent = Intent(view.context, DetailsActivity::class.java).apply {
            putExtra("MALLink", animeLink)
        }
        view.context.startActivity(intent)
    }

    override fun getItemCount() = data?.anime?.size?:0

    fun updateData(newData: RecommendationData) {
        data = newData
        notifyDataSetChanged()
    }
}