package com.example.blinkanime.mainFragment

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.compose.material3.Snackbar
import androidx.recyclerview.widget.RecyclerView
import com.example.blinkanime.R
import com.example.blinkanime.mainFragment.detailsActivity.DetailsActivity
import com.example.blinkanime.mainFragment.detailsActivity.EpisodesListAdapter
import com.example.blinkanime.misc.CheckLinkResult
import com.example.blinkanime.misc.MiscFunc
import com.example.blinkanime.misc.RealDebridAPI
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.tabs.TabLayout
import com.squareup.picasso.Picasso
import com.squareup.picasso.Transformation
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.URLDecoder


class LatestEpisodesAdapter(private val dataSample: List<Anime>) : RecyclerView.Adapter<LatestEpisodesAdapter.LatestEpisodesViewHolder>() {

    private var data = dataSample
    private val miscFunc = MiscFunc() // Create a single instance of MiscFunc

    class LatestEpisodesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val animeCard: CardView = itemView.findViewById(R.id.latest_episode_card)
        val animeName: TextView = itemView.findViewById(R.id.latest_episode_title)
        val epNumber: TextView = itemView.findViewById(R.id.latest_episode_number)
        val animeImage: ImageView = itemView.findViewById(R.id.latest_episode_image)
        val watchButton: Button = itemView.findViewById(R.id.watch_button)
        val moreButton: Button = itemView.findViewById(R.id.more_episodes_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LatestEpisodesViewHolder {
        val textView = LayoutInflater.from(parent.context)
            .inflate(R.layout.latest_recycle_list, parent, false) as CardView
        return LatestEpisodesViewHolder(textView)
    }



    override fun onBindViewHolder(holder: LatestEpisodesViewHolder, position: Int) {
        val currentItem = data[position] // Store the current item in a variable

        holder.animeName.text = currentItem.name
        holder.epNumber.text = currentItem.episodeNumber

        // Use a more efficient image loading library like Glide or Coil
        miscFunc.loadAndTransformImage(currentItem.image, holder.animeImage, "normal", 1400, 500)

        // Set onClickListener with a lambda expression
        holder.animeCard.setOnClickListener { detailsActivity(holder, position) }
        holder.moreButton.setOnClickListener { detailsActivity(holder, position, 1) }

        setupLinksSheet(holder, position)
    }

    private fun detailsActivity(holder: LatestEpisodesViewHolder, position: Int, tab: Int = 0) {
        // start details activity
        val intent = Intent(holder.animeCard.context, DetailsActivity::class.java)
        intent.putExtra("animeLink", data[position].animeLink)
        intent.putExtra("tabID", tab)
        holder.animeCard.context.startActivity(intent)
    }

    private fun setupLinksSheet(holder: LatestEpisodesViewHolder, position: Int) {
        //set button click listener
        holder.watchButton.setOnClickListener {
            val x = (URLDecoder.decode(data[position].link, "UTF-8"))
            val bottomSheetView = LayoutInflater.from(holder.itemView.context)
                .inflate(R.layout.modal_bottom_sheet, null)



            // material3 bottom sheet
            val bottomSheetDialog = BottomSheetDialog(holder.itemView.context)
            val tabLayout = bottomSheetView.findViewById<TabLayout>(R.id.tabLayout)
            val progressBar: CircularProgressIndicator = bottomSheetView.findViewById(R.id.progressIndicator)
            progressBar.visibility = View.VISIBLE
            // get the links using launchGetAllLinksAndShowTabContent
            CoroutineScope(Dispatchers.Main).launchGetAllLinksAndShowTabContent(
                URLDecoder.decode(data[position].link, "UTF-8"),
                tabLayout,
                progressBar
            )


            bottomSheetDialog.setContentView(bottomSheetView)
            bottomSheetDialog.show()
        }
    }

    private fun showTabContent(
        tabLayout: TabLayout,
        fhdLinks: List<EpisodeLink>,
        hdLinks: List<EpisodeLink>,
        sdLinks: List<EpisodeLink>
    ) {
        val container = tabLayout.parent as ViewGroup
        val tabContent = container.findViewById<FrameLayout>(R.id.frameLayout)
        tabContent.removeAllViews()

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when(tab?.position) {
                    0 -> {
                        val linearLayout = generateButtons(tabContent.context, fhdLinks)
                        // add the linearLayout to the tabContent
                        tabContent.removeAllViews()
                        tabContent.addView(linearLayout)
                    }
                    1 -> {
                        val linearLayout = generateButtons(tabContent.context, hdLinks)
                        // add the linearLayout to the tabContent
                        tabContent.removeAllViews()
                        tabContent.addView(linearLayout)
                    }
                    2 -> {
                        val linearLayout = generateButtons(tabContent.context, sdLinks)
                        // add the linearLayout to the tabContent
                        tabContent.removeAllViews()
                        tabContent.addView(linearLayout)
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabReselected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> {
                        val linearLayout = generateButtons(tabContent.context, fhdLinks)
                        tabContent.removeAllViews()
                        tabContent.addView(linearLayout)
                    }
                }
            }
        })
        tabLayout.selectTab(tabLayout.getTabAt(0))
    }


    private fun CoroutineScope.launchGetAllLinksAndShowTabContent(
        episodeLink: String,
        tabLayout: TabLayout,
        progressIndicator: CircularProgressIndicator? = null
    ) = launch(Dispatchers.Main) {
        val linkList = AnimeScraper().getAllLinks(episodeLink)
        if (linkList != null) {
            val (fhdLinks, hdLinks, sdLinks) = AnimeScraper().separateLinksByQuality(linkList)
            showTabContent(tabLayout, fhdLinks, hdLinks, sdLinks)
            progressIndicator?.visibility = View.GONE
        }
    }

    private fun generateButtons(context: Context, links: List<EpisodeLink>): LinearLayout {
        val linearLayout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
        }

        for (quality in links) {
            val button = Button(context).apply {
                text = quality.host
                setOnClickListener {
                    RealDebridAPI().getGeneratedLink(quality.link) { result ->
                        when (result) {
                            is CheckLinkResult.Success -> {
                                miscFunc.openVideoPlayer(context, result.link)
                            }
                            else -> {
                                miscFunc.openWebPage(context, quality.link)
                            }
                        }
                    }
                }
            }
            linearLayout.addView(button)
        }

        return linearLayout
    }

    override fun getItemCount() = data.size

    fun updateData(newData: List<Anime>) {
        data = newData
        notifyDataSetChanged()
    }
}