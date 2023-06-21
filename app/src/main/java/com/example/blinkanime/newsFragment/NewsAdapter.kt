package com.example.blinkanime.newsFragment

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.text.util.Linkify
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.VideoView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.media3.ui.PlayerView
import androidx.recyclerview.widget.RecyclerView
import com.example.blinkanime.R
import com.example.blinkanime.misc.MiscFunc
import java.util.*

data class CardSettings (
    val fullImage: Boolean = false,
    val showMoreFullImage: Boolean = false,
)

class NewsAdapter(private val context: Context) : RecyclerView.Adapter<NewsAdapter.MyViewHolder>() {
    // the anime news data
    private var data = listOf<Tweet>()
    var cardSettings = CardSettings()

    private val originalFormat = SimpleDateFormat("EEE MMM dd HH:mm:ss ZZZZZ yyyy", Locale.ENGLISH)
    private val dayMonthFormat = SimpleDateFormat("dd MMM", Locale.ENGLISH)
    private val hourMinuteFormat = SimpleDateFormat("hh:mm a", Locale.ENGLISH)

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val newsTitle: TextView = itemView.findViewById(R.id.titleTextView)
        val newsDescription: TextView = itemView.findViewById(R.id.descriptionTextView)
        val imageContainer: LinearLayout = itemView.findViewById(R.id.imageContainer)
        val constraintLayout: ConstraintLayout = itemView.findViewById(R.id.constraintLayout)
        val mainImageView : ImageView = itemView.findViewById(R.id.mainImageView)
        val playerView : PlayerView = itemView.findViewById(R.id.videoView)
        val showMoreTextView : TextView = itemView.findViewById(R.id.showMoreTextView)
        val showlessTextView : TextView = itemView.findViewById(R.id.showLessTextView)
        val profileLogo : ImageView = itemView.findViewById(R.id.profileLogo)
        val dateCreated : TextView = itemView.findViewById(R.id.createdAt)
        val muteButton : ImageButton = itemView.findViewById(R.id.mute_button)
        val videoFrame: FrameLayout = itemView.findViewById(R.id.videoContainer)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val textView = LayoutInflater.from(parent.context)
            .inflate(R.layout.news_card_item, parent, false) as CardView
        return MyViewHolder(textView)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = data[position].retweeted_status?: data[position]


        holder.newsTitle.text = currentItem.user.name
        holder.newsDescription.text = currentItem.full_text
        holder.dateCreated.visibility = View.VISIBLE

        val date = originalFormat.parse(currentItem.created_at)
        val dayMonth = dayMonthFormat.format(date)
        val hourMinute = hourMinuteFormat.format(date)
        holder.dateCreated.text = date?.let { "$dayMonth - $hourMinute" }
        holder.imageContainer.removeAllViews()

        // use picasoo to add image to profileLogo and make it circular
        MiscFunc().loadAndTransformImage(
            currentItem.user.profile_image_url.replace("http://", "https://"),
            holder.profileLogo,
            "circle", 100, 100)



        Linkify.addLinks(holder.newsDescription, Linkify.WEB_URLS)
        holder.newsDescription.setLinkTextColor(Color.parseColor("#5887BD"))

        if (currentItem.extended_entities?.media?.map { it.media_url_https }?.isNotEmpty() == true) {
            holder.showMoreTextView.visibility = View.GONE
            holder.mainImageView.visibility = View.VISIBLE
            if (currentItem.extended_entities.media[0].type == "video") {
                // add VideoView and load video
                holder.mainImageView.visibility = View.GONE
                holder.videoFrame.visibility = View.VISIBLE
                // load video
                for (currentVideo in currentItem.extended_entities.media[0].video_info.variants) {
                    if (currentVideo.content_type == "video/mp4") {
                        val player = MiscFunc().ExpPlayerLoadVideo(context, holder.playerView, currentVideo.url)
                        holder.muteButton.setOnClickListener {
                            if (player.volume == 0f) {
                                player.volume = 1f
                                // change icon
                                holder.muteButton.setImageResource(R.drawable.ic_volume_up)
                            } else {
                                player.volume = 0f
                                // change icon
                                holder.muteButton.setImageResource(R.drawable.ic_volume_down)
                            }
                        }
                        break
                    }
                }
            } else {
                holder.videoFrame.visibility = View.GONE
                MiscFunc().loadAndTransformImage(currentItem.extended_entities.media[0].media_url_https, holder.mainImageView,
                    if (cardSettings.fullImage) "full" else "normal")
            }

            holder.mainImageView.setOnClickListener { view ->
                MiscFunc().showImageDialog(view.context, currentItem.extended_entities.media.map { it.media_url_https }, 0)
            }
        } else {
            holder.showMoreTextView.visibility = View.GONE
            holder.mainImageView.visibility = View.GONE
        }

        if ((currentItem.extended_entities?.media?.size ?: 0) > 1) {
            holder.showMoreTextView.visibility = View.VISIBLE

            for (i in 1 until currentItem.extended_entities?.media?.size!!) {
                val imageView = ImageView(context).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                    adjustViewBounds = true
                    scaleType = ImageView.ScaleType.CENTER_CROP
                    setPadding(0, 0, 0, 10)
                    MiscFunc().loadAndTransformImage(currentItem.extended_entities.media[i].media_url_https, this,
                        if (cardSettings.showMoreFullImage) "full" else "normal")
                    setOnClickListener {
                        MiscFunc().showImageDialog(context, currentItem.extended_entities.media.map { it.media_url_https }, i)
                    }
                }

                holder.imageContainer.addView(imageView)
            }
        }

        val expandClickListener = View.OnClickListener {
            if (!currentItem.isExpanded) {
                data.filter { it.isExpanded }
                    .forEach { it.isExpanded = false; notifyItemChanged(data.indexOf(it)) }
            }

            currentItem.isExpanded = !currentItem.isExpanded
            notifyItemChanged(position)
        }

        if (currentItem.extended_entities?.media?.size ?: 0 > 1) {
            with(holder) {
                showMoreTextView.setOnClickListener(expandClickListener)
                constraintLayout.setOnClickListener(expandClickListener)
                newsDescription.setOnClickListener(expandClickListener)
                newsTitle.setOnClickListener(expandClickListener)
                showlessTextView.setOnClickListener(expandClickListener)
            }
        }

        with(holder) {
            when {
                currentItem.isExpanded && (currentItem.extended_entities?.media?.size ?: 0) > 1 -> {
                    showMoreTextView.visibility = View.GONE
                    showlessTextView.visibility = View.VISIBLE
                    imageContainer.visibility = View.VISIBLE
                }
                !currentItem.isExpanded && (currentItem.extended_entities?.media?.size ?: 0) > 1 -> {
                    showMoreTextView.visibility = View.VISIBLE
                    showlessTextView.visibility = View.GONE
                    imageContainer.visibility = View.GONE
                }
                else -> {
                    showMoreTextView.visibility = View.GONE
                    showlessTextView.visibility = View.GONE
                    imageContainer.visibility = View.GONE
                }
            }
        }
    }

    override fun getItemCount() = data.size

    fun submitList(newData: List<Tweet>) {
        data = newData
        notifyDataSetChanged()
    }

//     update settings and refresh adapter
    fun updateSettings(newSettings: CardSettings) {
        cardSettings = newSettings
        notifyDataSetChanged()
    }
}