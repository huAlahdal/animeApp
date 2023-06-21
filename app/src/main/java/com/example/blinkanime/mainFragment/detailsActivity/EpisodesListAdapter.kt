package com.example.blinkanime.mainFragment.detailsActivity

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.blinkanime.R
import com.example.blinkanime.episodecomments.EpisodeCommentsActivity
import com.example.blinkanime.mainFragment.AnimeScraper
import com.example.blinkanime.mainFragment.Episode
import com.example.blinkanime.mainFragment.EpisodeLink
import com.example.blinkanime.misc.CheckLinkResult
import com.example.blinkanime.misc.MiscFunc
import com.example.blinkanime.misc.RealDebridAPI
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.URLDecoder

class EpisodesListAdapter(animeEpisodes: List<Episode>) : RecyclerView.Adapter<EpisodesListAdapter.EpisodesViewHolder>() {

    private var data = animeEpisodes

    class EpisodesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val animeCard: LinearLayout = itemView.findViewById(R.id.episode_item)
        val epImage: ImageView = itemView.findViewById(R.id.episode_image)
        val epTitle: TextView = itemView.findViewById(R.id.episode_name)
        val epName: TextView = itemView.findViewById(R.id.episode_title)
        val epScore: TextView = itemView.findViewById(R.id.ep_score)
        val airDate: TextView = itemView.findViewById(R.id.ep_air_date)
        val epComments: TextView = itemView.findViewById(R.id.episode_views)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EpisodesViewHolder {
        val textView = LayoutInflater.from(parent.context)
            .inflate(R.layout.details_ep_item, parent, false) as CardView
        return EpisodesViewHolder(textView)
    }

    override fun onBindViewHolder(holder: EpisodesViewHolder, position: Int) {

        holder.epTitle.text = data[position].name

        if (data[position].extraData == null) {
            holder.epName.text = "N/A"
            holder.epScore.text = "N/A"
            holder.airDate.text = "تاريخ العرض: " + "N/A"
            holder.epComments.text = "التعليقات: " + "N/A"
        } else {
            holder.epName.text = data[position].extraData?.epTitle
            holder.epScore.text = data[position].extraData?.epScore
            holder.airDate.text = ("تاريخ العرض: " + data[position].extraData?.dateAired)
            holder.epComments.text = ("التعليقات: " + data[position].extraData?.epCommentsCount)

            holder.epComments.setOnClickListener {
                if (data[position].extraData?.epCommentsLink != null) {
                    // open comments activity with the link
                    val intent = Intent(holder.animeCard.context, EpisodeCommentsActivity::class.java)
                    intent.putExtra("episodeComments", data[position].extraData?.epCommentsLink)
                    holder.animeCard.context.startActivity(intent)

                    //MiscFunc().openWebPage(holder.itemView.context, data[position].extraData?.epCommentsLink!!)
                }
            }
        }


        MiscFunc().loadAndTransformImage(data[position].image, holder.epImage, "noRound", 600, 300)
        setupLinksSheet(holder, position)
    }


    private fun setupLinksSheet(holder: EpisodesViewHolder, position: Int) {
        //set button click listener
        holder.animeCard.setOnClickListener {
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
        progressBar: CircularProgressIndicator
    ) = launch(Dispatchers.Main) {
        val linkList = AnimeScraper().getAllLinks(episodeLink)
        if (linkList != null) {
            val (fhdLinks, hdLinks, sdLinks) = AnimeScraper().separateLinksByQuality(linkList)
            showTabContent(tabLayout, fhdLinks, hdLinks, sdLinks)
            progressBar.visibility = View.GONE
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
                                MiscFunc().openVideoPlayer(context, result.link)
                            }
                            else -> {
                                MiscFunc().openWebPage(context, quality.link)
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

    fun updateData(newData: List<Episode>) {
        data = newData
        notifyDataSetChanged()
    }
}