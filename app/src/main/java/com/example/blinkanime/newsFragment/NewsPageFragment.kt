package com.example.blinkanime.newsFragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.blinkanime.MainActivity
import com.example.blinkanime.R
import com.example.blinkanime.databinding.NewsPageBinding
import com.example.blinkanime.mainFragment.AnimeScraper
import com.example.blinkanime.scheduleFragment.ScheduleAdapter
import kotlinx.coroutines.launch
import org.json.JSONArray

class NewsPageFragment : Fragment(), MenuProvider {
    private lateinit var binding: NewsPageBinding
    private var animeNewsAdapter: NewsAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = NewsPageBinding.inflate(inflater, container, false)
        val sharedPref = requireActivity().getPreferences(Context.MODE_PRIVATE)
        val recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(context)
        animeNewsAdapter = NewsAdapter(requireContext())
        recyclerView.adapter = animeNewsAdapter

        newsViewModel.newsData.observe(viewLifecycleOwner) { newsData ->
            animeNewsAdapter?.submitList(newsData)
        }

        val swipeRefreshLayout = binding.newsRefreshLayout
        swipeRefreshLayout.setOnRefreshListener {
            // Refresh your data here
            newsViewModel.refresh()
            swipeRefreshLayout.isRefreshing = false
        }


        // ActionBar Menu
        val actionBarMenu: MenuHost = requireActivity()
        actionBarMenu.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)

        // get card settings from SharedPreferences
        animeNewsAdapter?.cardSettings = CardSettings(
            fullImage = sharedPref.getBoolean("new fullImage", false),
            showMoreFullImage = sharedPref.getBoolean("more fullImage", false)
        )


        return binding.root
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.settings_menu, menu)
        // Set card settings from SharedPreferences
        val sharedPref = requireActivity().getPreferences(Context.MODE_PRIVATE)
        menu.findItem(R.id.card_full_size).isChecked = sharedPref.getBoolean(PREF_KEY_NEW_FULL_IMAGE, false)
        menu.findItem(R.id.more_full_size).isChecked = sharedPref.getBoolean(PREF_KEY_MORE_FULL_IMAGE, false)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        val sharedPref = requireActivity().getPreferences(Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        val menuDataMap = mapOf(
            R.id.card_full_size to Pair(PREF_KEY_NEW_FULL_IMAGE) { isChecked: Boolean ->
                animeNewsAdapter?.cardSettings?.copy(fullImage = isChecked)
            },
            R.id.more_full_size to Pair(PREF_KEY_MORE_FULL_IMAGE) { isChecked: Boolean ->
                animeNewsAdapter?.cardSettings?.copy(showMoreFullImage = isChecked)
            }
        )

        menuDataMap[menuItem.itemId]?.let { (prefKey, adapterUpdate) ->
            menuItem.isChecked = !menuItem.isChecked
            adapterUpdate(menuItem.isChecked)?.let {
                animeNewsAdapter?.cardSettings = it
                animeNewsAdapter?.notifyDataSetChanged()
            }
            editor.putBoolean(prefKey, menuItem.isChecked)
        }

        editor.apply()
        return false
    }

    companion object {
        const val PREF_KEY_NEW_FULL_IMAGE = "new fullImage"
        const val PREF_KEY_MORE_FULL_IMAGE = "more fullImage"
        val newsViewModel = ViewModelProvider.NewInstanceFactory().create(NewsViewModel::class.java)
    }
}