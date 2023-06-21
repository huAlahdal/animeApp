package com.example.blinkanime.scheduleFragment

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.blinkanime.R
import com.example.blinkanime.mainFragment.Anime
import com.example.blinkanime.mainFragment.LatestEpisodesAdapter
import com.example.blinkanime.mainFragment.detailsActivity.DetailsActivity
import com.example.blinkanime.misc.MiscFunc

class ScheduleAdapter(items: List<Anime>) : RecyclerView.Adapter<ScheduleAdapter.ScheduleHolder>() {

    private var data = items

    class ScheduleHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleView: TextView = itemView.findViewById(R.id.anime_Title)
        val descriptionView: TextView = itemView.findViewById(R.id.anime_description)
        val mainImageView: ImageView = itemView.findViewById(R.id.animeImage)
        val animeCard: View = itemView.findViewById(R.id.cardView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.schedule_content_item, parent, false)
        return ScheduleHolder(view)
    }

    override fun onBindViewHolder(holder: ScheduleHolder, position: Int) {
        val currentItem = data[position]
        holder.titleView.text = currentItem.name
        holder.descriptionView.text = currentItem.description
        MiscFunc().loadAndTransformImage(currentItem.image, holder.mainImageView, "normal", 500, 900)

        holder.animeCard.setOnClickListener {
            detailsActivity(holder, position)
        }
    }

    private fun detailsActivity(holder: ScheduleHolder, position: Int) {
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