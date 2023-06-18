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
import com.example.blinkanime.MainActivity
import com.example.blinkanime.R
import com.example.blinkanime.mainFragment.detailsActivity.DetailsActivity
import com.example.blinkanime.misc.MiscFunc
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso

class MostWatchedAnimeAdapter(private val dataSample: List<Anime>) : RecyclerView.Adapter<MostWatchedAnimeAdapter.MostWatchedAnimeViewHolder>() {
    private var data = dataSample

    class MostWatchedAnimeViewHolder(private val itemView: View) : RecyclerView.ViewHolder(itemView) {
        val animeName: TextView = itemView.findViewById(R.id.anime_name)
        val animeImage: ImageView = itemView.findViewById(R.id.anime_image)
        val animeCard: ConstraintLayout = itemView.findViewById(R.id.anime_card)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MostWatchedAnimeViewHolder {
        val textView = LayoutInflater.from(parent.context)
            .inflate(R.layout.anime_recycle_item, parent, false) as CardView
        return MostWatchedAnimeViewHolder(textView)
    }

    override fun onBindViewHolder(holder: MostWatchedAnimeViewHolder, position: Int) {
        if (data[position].image.isEmpty() or data[position].name.isEmpty()) {
            return
        }

        holder.animeName.text = data[position].name
        // use picasso to load image
        Picasso.get()
            .load(data[position].image)
            .resize(400, 600)
            .into(holder.animeImage)

        // set on click listener
        holder.animeCard.setOnClickListener {
            detailsActivity(holder, position)
        }

    }

    private fun detailsActivity(holder: MostWatchedAnimeViewHolder, position: Int) {
        // start details activity
        var det_link = data[position].link

        val isLoading = MainActivity.mainActivityViewModel.loadingData.value



        if (det_link == "") {
            if (isLoading == true) {
                // snackbar
                Snackbar.make(holder.animeCard, "Please wait for data to load", Snackbar.LENGTH_SHORT).show()
                return
            }

            val foundAnime = MainActivity.mainActivityViewModel.animeList.value?.filter {
                // check for name contain and ignore case
                data[position].name.contains(it.name, true)
            }
            if (foundAnime != null) {
                if (foundAnime.isNotEmpty())
                    det_link = foundAnime[0].link
                else {
                    // snackbar
                    //Snackbar.make(holder.animeCard, "Anime not found", Snackbar.LENGTH_SHORT).show()
                    MiscFunc().openWebPage(holder.animeCard.context, data[position].animeLink)
                    return
                }
            }
        }

        val intent = Intent(holder.animeCard.context, DetailsActivity::class.java)
        intent.putExtra("animeLink", det_link)
        holder.animeCard.context.startActivity(intent)
    }

    override fun getItemCount() = data.size

    fun updateData(newData: List<Anime?>) {
        data = newData.filterNotNull() ?: emptyList()
        notifyDataSetChanged()
    }
}