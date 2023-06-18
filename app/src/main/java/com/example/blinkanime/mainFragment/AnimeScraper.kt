package com.example.blinkanime.mainFragment

import android.preference.PreferenceManager
import android.util.Log
import androidx.lifecycle.Observer
import com.example.blinkanime.MainActivity
import com.example.blinkanime.misc.MALScraper
import com.example.blinkanime.misc.RealDebridAPI
import com.example.blinkanime.scheduleFragment.AnimeSchedule
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.coroutines.*
import okhttp3.ResponseBody
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Url
import java.net.URLDecoder
import java.time.LocalDate
import java.util.concurrent.CountDownLatch

class AnimeScraper() {

    private val MAIN_PAGE = "/"
    private val MOST_WATCHED_ANIME_HEADER_TEXT = "أكثر أنميات الموسم مشاهدة"
    private val WITANIME_SCHEDULE_PAGE = "/مواعيد-الحلقات/"
    private val ANIME4UP_SCHEDULE_PAGE = "/anime-show-dates-4/"

    fun getLatestEpisodes(callback: (List<Anime>?) -> Unit) {
        enqueueAnimeListCall(MAIN_PAGE, ::parseLatestEpisodesFromResponse, callback)
    }

    fun getSeasonalAnimes(callback: (List<Anime>?) -> Unit) {
        val seasonLink = getCurrentYearSeason()
        enqueueAnimeListCall(seasonLink, ::parseSeasonalAnimesFromResponse, callback)
    }

    fun getMostWatched(callback: (List<Anime>?) -> Unit) {
        enqueueAnimeListCall(MAIN_PAGE, ::parseMostWatchedFromResponse, callback)
    }

    fun getAnimeSchedule(callback: (List<AnimeSchedule>?) -> Unit) {
        val baseUrl = RetrofitInstance.getBaseUrl()
        val schedulePage = when (baseUrl) {
            "https://witanime.com/" -> WITANIME_SCHEDULE_PAGE
            "https://w1.anime4up.tv/" -> ANIME4UP_SCHEDULE_PAGE
            else -> WITANIME_SCHEDULE_PAGE
        }
        enqueueAnimeListCall(schedulePage, ::parseAnimeScheduleFromResponse, callback)
    }

    suspend fun getAnimeDetalisPage(link: String, callback: (List<AnimeDetails>?) -> Unit) {
        enqueueSuspendableAnimeListCall(link, ::parseAnimeDetalisPageFromResponse, callback)
    }

    private fun <T> enqueueAnimeListCall(
        path: String,
        parseFunction: (ResponseBody?, Document?) -> List<T>?,
        callback: (List<T>) -> Unit
    ) {
        val call = RetrofitInstance.api.getMainPage(path)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: retrofit2.Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val animeList = parseFunction(response.body(),
                        response.body()?.byteStream()?.let { Jsoup.parse(it, null, "/") })
                    callback(animeList?: emptyList())
                } else {
                    callback(emptyList())
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("AnimeNewsScraper", "Error getting anime list", t)
                callback(emptyList())
            }
        })
    }

    private suspend fun <T> enqueueSuspendableAnimeListCall(
        path: String,
        parseFunction: suspend (ResponseBody?, Document?) -> List<T>?,
        callback: (List<T>) -> Unit
    ) {
        val response = RetrofitInstance.api.getMainPageSuspendable(path)
        if (response.isSuccessful) {
            val animeList = parseFunction(response.body(),
                response.body()?.byteStream()?.let { Jsoup.parse(it, null, "/") })
            callback(animeList ?: emptyList())
        } else {
            callback(emptyList())
        }
    }

    private fun parseLatestEpisodesFromResponse(response: ResponseBody?, document: Document?): List<Anime>? {
        val elements = document?.select("div.episodes-card-container")

        return elements?.mapNotNull { element ->
            val title = element.selectFirst("div.ep-card-anime-title h3")?.text()
            val animeLink = element.selectFirst("div.ep-card-anime-title h3 a")?.attr("href")
            val episodeNumber = element.selectFirst("div.episodes-card-title h3")?.text()
            val imageUrl = element.selectFirst("div.ehover6 img")?.attr("src")?.replace("-323x470", "")
            val link = element.selectFirst("div.ehover6 a")?.attr("href")
            when {
                title != null -> Anime(title, imageUrl ?: "", link ?: "", episodeNumber ?: "", "", animeLink ?: "")
                else -> null
            }
        }
    }

    private fun parseSeasonalAnimesFromResponse(response: ResponseBody?, document: Document?): List<Anime>? {
        val elements = document?.select(".col-mobile-no-padding")

        return elements?.mapNotNull { element ->
            val title = element.select(".anime-card-title h3 a").text()
            val imageUrl = element.selectFirst("div.ehover6 img")?.attr("src")?.substringBefore("?")
            val link = element.selectFirst("a")?.attr("href")
            Anime(title, imageUrl ?: "", link ?: "", "")
        }
    }

    private fun parseMostWatchedFromResponse(response: ResponseBody?, document: Document?): List<Anime>? {
        val animeListContainer = document?.select("div.main-didget-head h3")
            ?.firstOrNull { it.text() == MOST_WATCHED_ANIME_HEADER_TEXT }
            ?.parent()?.parent()
        val animeList = animeListContainer?.select("div.anime-card-container")

        return animeList?.mapNotNull { animeElement ->
            val title = animeElement.selectFirst(".anime-card-title h3 a")?.text()
            val imageUrl = animeElement.selectFirst("div.ehover6 img")?.attr("src")?.substringBefore("?")?.replace("-323x470", "")
            val link = animeElement.selectFirst("div.ehover6 a")?.attr("href")
            title?.let { Anime(it, imageUrl ?: "", link ?: "", "") }
        }
    }

    private fun parseAnimeScheduleFromResponse(response: ResponseBody?, document: Document?): List<AnimeSchedule>? {
        return document?.select("div.main-widget")?.map { element ->
            val dayElement = element.selectFirst("div.main-didget-head h3")?.text()
            val animeElements = element.select("div.anime-card-container")
            val animeList = parseAnimeElements(animeElements)
            AnimeSchedule(dayElement ?: "", animeList)
        }
    }

    private fun parseAnimeElements(animeElements: Elements): List<Anime> {
        return animeElements.mapNotNull { animeElement ->
            val animeImage = animeElement.selectFirst("div.ehover6 img")?.attr("src")?.substringBefore("?")?.replace("-323x470", "")
            val animeName = animeElement.selectFirst("div.anime-card-title h3")?.text()
            val animeDescription = animeElement.selectFirst("div.anime-card-title")?.attr("data-content")
            val animeLink = animeElement.selectFirst("div.anime-card-title h3 a")?.attr("href")
            animeName?.let { Anime(it, animeImage ?: "", animeLink ?: "", "",animeDescription?:"") }
        }
    }

    private suspend fun parseAnimeDetalisPageFromResponse(response: ResponseBody?, document: Document?): List<AnimeDetails>? {
        val animeDetails = mutableListOf<AnimeDetails>()
        val animePic = document?.selectFirst("div.anime-thumbnail img")?.attr("src")?.substringBefore("?")?.replace("-413x559", "")
        val animeName = document?.selectFirst("h1.anime-details-title")?.text()
        val animeDescription = document?.selectFirst("p.anime-story")?.text()
        // ul anime-genres and make it lsit of strings
        val animeGenres = document?.select("ul.anime-genres li")?.mapNotNull { it.text() }
        val animeType = document?.select("div.anime-details div.row div:nth-child(1) div.anime-info a")?.text()
        val animeYear = document?.select("div.anime-details div.row div:nth-child(2) div.anime-info")?.text()
        val animeStatus = document?.select("div.anime-details div.row div:nth-child(3) div.anime-info a")?.text()
        val animeEpNumber = document?.select("div.anime-details div.row div:nth-child(4) div.anime-info")?.text()
        val animeLength = document?.select("div.anime-details div.row div:nth-child(5) div.anime-info")?.text()
        val animeSeason = document?.select("div.anime-details div.row div:nth-child(6) div.anime-info a")?.text()
        val animeSource = document?.select("div.anime-details div.row div:nth-child(7) div.anime-info")?.text()
        val trailerLink = document?.selectFirst("div.anime-external-links a:nth-child(1)")?.attr("href")
        val MALLink = document?.selectFirst("div.anime-external-links a:nth-child(2)")?.attr("href")


        val url = MALLink?:""
        val regex = """.*/anime/(\d+).*""".toRegex()
        val matchResult = regex.find(url)
        val id = matchResult?.groups?.get(1)?.value

        val malAnimeData = MALScraper().getAnime(id?.toInt()?:0)

        val EpisodesContiner = document?.select("div.DivEpisodeContainer")

        // map through EpisodesContiner and get link from episodes-card-title h3 text and link from ehover6 a href
        val episodes = EpisodesContiner?.mapIndexedNotNull  { index, element ->
            val title = element.selectFirst("div.episodes-card-title h3")?.text()
            val link = element.selectFirst("div.ehover6 a")?.attr("href")
            when {
                title != null -> Episode(title, link ?: "", animePic ?: "", null)
                else -> null
            }
        }

        animeDetails.add(AnimeDetails(
            animeName,
            animePic,
            episodes,
            animeDescription,
            animeGenres,
            animeType,
            animeStatus,
            animeLength,
            animeYear,
            animeEpNumber,
            animeSeason,
            animeSource,
            trailerLink,
            MALLink,
            malAnimeData?.get(0)?.malDetails
        ))

        return animeDetails
    }

    private fun getCurrentYearSeason(): String {
        val today = LocalDate.now()
        val year = today.year
        val month = today.monthValue

        val arabicSpring = "ربيع"
        val arabicSummer = "صيف"
        val arabicFall = "خريف"
        val arabicWinter = "شتاء"

        return when (month) {
            in 3..5 -> "/anime-season/$arabicSpring-$year"
            in 6..8 -> "/anime-season/$arabicSummer-$year"
            in 9..11 -> "/anime-season/$arabicFall-$year"
            else -> "/anime-season/$arabicWinter-$year"
        }
    }

    suspend fun getAllLinks(episodeLink: String): List<EpisodeLink>? = withContext(Dispatchers.IO) {
        val linkList = mutableListOf<EpisodeLink>()
        val call = RetrofitInstance.api.getMainPage(episodeLink)
        try {
            val response = call.execute()
            if (response.isSuccessful) {
                val html: String = response.body()?.string() ?: ""
                // Parse the HTML content
                val doc: Document = Jsoup.parse(html)
                // Select the desired elements and extract the information
                val elements = doc.select("div.tab-content div.col-md-6 ul.quality-list li")
                elements.map { element ->
                    val type = element.parent()?.select("li:first-of-type")?.text()
                    var host = element.select("a").text()
                    val link = element.select("a").attr("href")
                    linkList.add(EpisodeLink(type ?: "", host, link))
                }
                // Create a list of deferreds for the checkLink calls
                val deferredList = linkList.map { link ->
                    async(Dispatchers.IO) {
                        val isAvailable = RealDebridAPI().checkLink(link.link).await()
                        if (isAvailable == "supported") {
                            link.host += " (RD)"
                        }
                    }
                }
                // Wait for all the deferreds to complete
                deferredList.awaitAll()
                linkList
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    // get size
    suspend fun getAllAnimes(): List<Anime>? = withContext(Dispatchers.IO) {
        val decodedLink = URLDecoder.decode("/anime-category/%D8%A7%D9%84%D8%A7%D9%86%D9%85%D9%8A-%D8%A7%D9%84%D9%85%D8%AA%D8%B1%D8%AC%D9%85/", "UTF-8")
        val call = RetrofitInstance.api.getMainPage(decodedLink)
        try {
            val response = call.execute()
            if (response.isSuccessful) {
                val animeList = mutableListOf<Anime>()
                val html: String = response.body()?.string() ?: ""
                // Parse the HTML content
                val doc: Document = Jsoup.parse(html)
                // Select the desired elements and extract the information
                val elements = doc.select("div.anime-card-container")

                elements.map { element ->
                    val animeName = element.selectFirst("div.anime-card-title h3 a")?.text()
                    val animeLink = element.selectFirst("div.anime-card-title h3 a")?.attr("href")
                    val animePic = element.selectFirst("div.ehover6 img")?.attr("src")?.replace("-323x470", "")
                    val animeStatus = element.selectFirst("div.anime-card-status a")?.text()
                    val animeType = element.selectFirst("div.anime-card-type a")?.text()

                    animeList.add(Anime(
                        animeName?:"",
                        animePic?:"",
                        animeLink?:"",
                        animeStatus?:"",
                        animeType?:""))
                }

                animeList
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }


    fun separateLinksByQuality(links: List<EpisodeLink>): Triple<List<EpisodeLink>, List<EpisodeLink>, List<EpisodeLink>> {
        val fhdLinks = mutableListOf<EpisodeLink>()
        val hdLinks = mutableListOf<EpisodeLink>()
        val sdLinks = mutableListOf<EpisodeLink>()
        // remove links that is empty string
        for (link in links) {
            if (link.link == "") {
                continue
            }
            when {
                link.quality.contains("FHD") -> fhdLinks.add(link)
                link.quality.contains("HD") -> hdLinks.add(link)
                link.quality.contains("SD") -> sdLinks.add(link)
            }
        }

        return Triple(fhdLinks, hdLinks, sdLinks)
    }
}

data class EpisodeLink(
    val quality: String,
    var host : String,
    val link: String,
    val isStreamable: Boolean = false,
    val id: String = ""
)

interface WitanimeApi {
    @GET
    fun getMainPage(@Url url: String): Call<ResponseBody>

    @GET
    suspend fun getMainPageSuspendable(@Url path: String): Response<ResponseBody>
}

object RetrofitInstance {
    private var BASE_URL = "https://witanime.com"

    private var gson: Gson = GsonBuilder()
        .setLenient()
        .create()

    var api: WitanimeApi = createApi()

    private fun createApi(): WitanimeApi {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(WitanimeApi::class.java)
    }

    fun updateBaseUrl(newBaseUrl: String) {
        BASE_URL = newBaseUrl
        api = createApi()
    }

    fun getBaseUrl(): String {
        return BASE_URL
    }
}






/*

suspend fun updateMainPageDoc(type:Int = 0, notify: Boolean = false, helper: NotificationHelper? = null) = withContext(Dispatchers.IO) {
        try {

            mainPageDoc = Jsoup.connect("https://witanime.com").get()
            when(type) {
                1 -> scrapeAnimeData(mainPageDoc?.select("#menu-item-107 a")?.attr("href"), notify)
                2 -> scrapeLatestEpisodesData(mainPageDoc, notify, helper)
                3 -> scrapeMostWatchedAnimes(mainPageDoc, notify)
                else -> {
                    scrapeAnimeData(mainPageDoc?.select("#menu-item-107 a")?.attr("href"), notify)
                    scrapeLatestEpisodesData(mainPageDoc, notify)
                    scrapeMostWatchedAnimes(mainPageDoc, notify)
                }
            }

        } catch (e: IOException) {
            e.printStackTrace()
            Log.e("AnimeScraper", e.message.toString())
        }
    }

suspend fun updateScheduleData() = withContext(Dispatchers.IO) {
        try {
            val doc = Jsoup.connect(
                URLDecoder.decode(
                    "https://witanime.com/%D9%85%D9%88%D8%A7%D8%B9%D9%8A%D8%AF-%D8%A7%D9%84%D8%AD%D9%84%D9%82%D8%A7%D8%AA/",
                    "UTF-8"))
                .get()
            scrapeScheduleData(doc)
        } catch (e: IOException) {
            e.printStackTrace()
            Log.e("AnimeScraper", e.message.toString())
        }
    }

    private fun scrapeAnimeData(link: String?, notify: Boolean = false) : List<Anime> {
        val animeList = mutableListOf<Anime>()
        try {
            //val link = URLDecoder.decode(getSeasonLink(), "UTF-8")
            val doc = Jsoup.connect( URLDecoder.decode(link, "UTF-8")?:"").get()

            val elements = doc.select(".col-mobile-no-padding")

            for (element in elements) {
                val title = element.select(".anime-card-title h3 a").text()
                val imageUrl = element.select(".hover.ehover6 img").attr("src")
                val link = element.select(".anime-card-title h3 a").attr("href")

                val anime = Anime(title, imageUrl, link)
                animeList.add(anime)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        MainPageFragment.mainFragmentViewModel.updateSeasonData(animeList)
        return animeList
    }

    private fun scrapeMostWatchedAnimes(doc: Document?, notify: Boolean = false) : List<Anime> {
        val animeList = mutableListOf<Anime>()
        try {

            // Split the chain of method calls into smaller parts for better readability
            val selectedElement = doc?.select("div.main-didget-head h3")
                ?.filter { element -> element.text() == "أكثر أنميات الموسم مشاهدة" }
                ?.firstOrNull()?.parent()?.parent()?.parent()

            // Use elvis operator ?: to handle the case where the element is not found
            val elements = selectedElement?.select("div.anime-card-container") ?: emptyList()

            if (elements != null) {
                for (element in elements) {
                    val title = element.selectFirst("div.anime-card-title h3 a")?.text()
                    val episodeNumber = element.selectFirst("div.anime-card-type a")?.text()
                    val imageUrl = element.selectFirst("div.ehover6 img")?.attr("src")?.substringBefore("?")?.replace("-323x470", "")
                    val link = element.selectFirst("div.ehover6 a")?.attr("href")

                    val anime = Anime(title?:"", imageUrl?:"", link?:"", episodeNumber?:"")
                    animeList.add(anime)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        MainPageFragment.mainFragmentViewModel.updateMostWatchedData(animeList)
        return animeList
    }

    private fun scrapeScheduleData(doc: Document?) {
        val scheduleList = mutableListOf<AnimeSchedule>()
        try {
            val elements = doc?.select("div.main-widget")

            if (elements != null) {
                for (element in elements) {
                    val dayElement = element.selectFirst("div.main-didget-head h3")?.text()
                    val animeElements = element.select("div.anime-card-container")
                    val animeList = parseAnimeElements(animeElements)
                    scheduleList.addAll(listOf(AnimeSchedule(dayElement ?: "", animeList)))
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        DayFragment.scheduleViewModel.updateScheduleData(scheduleList)
    }

    private fun parseAnimeElements(animeElements: Elements): List<Anime> {
        val animeElementsList = mutableListOf<Anime>()
        animeElements.forEach { animeElement ->
            val animeImage = animeElement.selectFirst("div.ehover6 img")?.attr("src")?.substringBefore("?")?.replace("-323x470", "")
            val animeName = animeElement.selectFirst("div.anime-card-title h3")?.text()
            val animeDescription = animeElement.selectFirst("div.anime-card-title")?.attr("data-content")
            val animeLink = animeElement.selectFirst("div.anime-card-title h3 a")?.attr("href")
            if (animeImage != null && animeName != null && animeDescription != null) {
                animeElementsList.add(Anime(animeName, animeImage, animeLink?:"", "", animeDescription))
            }
        }
        return animeElementsList
    }

    private fun createBitmap(imageUrl: String): Bitmap {
        return try {
            Picasso.get().load(imageUrl).get()
        } catch (e: IOException) {
            e.printStackTrace()
            Log.e("AnimeScraper", e.message.toString())
            Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
        }
    }
 */


