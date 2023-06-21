package com.example.blinkanime.seasonalanime

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.blinkanime.R
import com.example.blinkanime.mainFragment.AnimeDetails
import com.example.blinkanime.mainFragment.detailsActivity.DetailsActivity
import com.example.blinkanime.misc.MiscFunc
import me.bush.translator.Translator

class SeasonalAnimeAdapter(animeList: List<AnimeDetails>, private val type:String): RecyclerView.Adapter<SeasonalAnimeAdapter.SeaonsalAnimeHolder>() {

    private var data = animeList

    private val translator = Translator()

    class SeaonsalAnimeHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val animeTitle: TextView = itemView.findViewById(R.id.animeName)
        val animeType: TextView = itemView.findViewById(R.id.animeType)
        val animeStatus: TextView = itemView.findViewById(R.id.animeStatus)
        val animeImage: ImageView = itemView.findViewById(R.id.animeImage)
        val animeCard: RelativeLayout = itemView.findViewById(R.id.animeItemLayout)
        val extraInfo: TextView = itemView.findViewById(R.id.extra_info)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SeaonsalAnimeHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.anime_list_item, parent, false)
        return SeaonsalAnimeHolder(view)
    }

    override fun onBindViewHolder(holder: SeaonsalAnimeHolder, position: Int) {

        if (data == null)
            return

        val currentItem = data[position]
        holder.animeTitle.text = currentItem.name
        holder.animeType.text = type
        holder.animeStatus.text = currentItem.malDetails?.score
        holder.extraInfo.visibility = View.VISIBLE
        holder.extraInfo.text = currentItem.malDetails?.MALId + " | " + currentItem.malDetails?.MALName

        MiscFunc().loadAndTransformImage(currentItem.image?:"", holder.animeImage, "normal", 900, 1300)

        holder.animeCard.setOnClickListener {
            startActivity(it, currentItem.MAL_Link?:"")
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