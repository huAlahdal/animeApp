package com.example.blinkanime.misc

import android.util.Log
import androidx.compose.ui.text.toLowerCase
import com.example.blinkanime.mainFragment.*
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.bush.translator.Language
import me.bush.translator.Translator
import okhttp3.ResponseBody
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.*


class MALScraper {
    private var gson: Gson = GsonBuilder()
        .setLenient()
        .create()

    private val service: MALService = Retrofit.Builder()
        .baseUrl("https://myanimelist.net")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(MALService::class.java)

    // get size
    suspend fun getAnime(id: Int): List<AnimeDetails>? = withContext(Dispatchers.IO) {
        val call = service.getAnimeDetails(id)
        val translator = Translator()
        try {
            val response = call.execute()
            if (response.isSuccessful) {
                val html: String = response.body()?.string() ?: ""
                // Parse the HTML content
                val doc: Document = Jsoup.parse(html)
                // Select the desired elements and extract the information
                val score = doc.selectFirst("div.score-label")?.text()
                val ranked = doc.selectFirst("span.ranked")?.text()?.replace("Ranked", "")
                val popularity = doc.selectFirst("span.popularity strong")?.text()
                val animeLink = doc.selectFirst("meta[property=og:url]")?.attr("content")
                // get img from data-src
                val animeImage = doc.selectFirst("div[id=content] td.borderClass div.leftside")
                    ?.selectFirst("a img")?.attr("data-src")
                val animeName = if (doc.selectFirst("h1.title-name") != null) {
                    doc.selectFirst("h1.title-name")?.text()
                } else {
                    doc.selectFirst("h1.title-name > strong")?.text()
                }
                val description = doc.selectFirst("p[itemprop=description]")?.text()?.replace("[Written by MAL Rewrite]", "")
                val translatedDescription = translator.translate(description ?: "", Language.ARABIC, Language.ENGLISH)


                val leftside = doc.select("div.spaceit_pad")
                val type = leftside.firstOrNull { it.selectFirst("span:contains(Type:)") != null }?.selectFirst("a")?.text()
                val episodes = leftside.firstOrNull { it.selectFirst("div.spaceit_pad:contains(Episodes:)") != null }?.text()?.substringAfter("Episodes: ")
                val status = leftside.firstOrNull { it.selectFirst("div.spaceit_pad:contains(Status:)") != null }?.text()?.substringAfter("Status: ")
                val aired = leftside.firstOrNull { it.selectFirst("div.spaceit_pad:contains(Aired:)") != null }?.text()?.substringAfter("Aired: ")
                val premiered = leftside.firstOrNull { it.selectFirst("span:contains(Premiered:)") != null }?.selectFirst("a")?.text()
                val studio = leftside.firstOrNull { it.selectFirst("span:contains(Studios:)") != null }?.selectFirst("a")?.text()
                val source = leftside.firstOrNull { it.selectFirst("div.spaceit_pad:contains(Source:)") != null }?.text()?.substringAfter("Source: ")
                val duration = leftside.firstOrNull { it.selectFirst("div.spaceit_pad:contains(Duration:)") != null }?.text()?.substringAfter("Duration: ")
                val rating = leftside.firstOrNull { it.selectFirst("div.spaceit_pad:contains(Rating:)") != null }?.text()?.substringAfter("Rating: ")
                val genres = leftside.firstOrNull { it.selectFirst("div.spaceit_pad:contains(Genres:)") != null }?.select("a")?.map { it.text() }
                val themes = leftside.firstOrNull { it.selectFirst("span:contains(Themes:)") != null }?.select("a")?.map { it.text() }


                val episodesLink = animeLink?.substringAfter("https://myanimelist.net/anime/")
                val animeID = episodesLink?.substringBefore("/")
                val animeLinkName = episodesLink?.substringAfter("/")

                val detailsList = mutableListOf<AnimeDetails>()

                // split aired to two parts by "to"
//                val formatter = DateTimeFormatter.ofPattern("MMM d, yyyy")
                val dates = aired?.split("to")?.map { it.trim() }?.toMutableList()
                if (dates?.get(0)?.contains("Not available") == true) {
                    dates[0] = "?"
                }

                val date1 = try {
                    val firstDate = dates?.get(0)
                    if (firstDate == "?") "?"
                    else parseDateOrYearMonth(firstDate?.substringBefore(":")?.trim() ?: "")
                } catch (e: DateTimeParseException) {
                    "?"
                }

                val date2 = if (dates?.size == 2) {
                    try {
                        val secondDate = dates[1]
                        if (secondDate.contains("Not available")) "?"
                        else parseDateOrYearMonth(secondDate.substringBefore(":").trim())
                    } catch (e: DateTimeParseException) {
                        "?"
                    }
                } else {
                    "?"
                }


                detailsList.add(AnimeDetails(
                    name = animeName ?: "",
                    image = animeImage?:"",
                    episodes = emptyList(),
                    description = translatedDescription.translatedText,
                    genres = genres ?: emptyList(),
                    type = type ?: "",
                    status = status ?: "",
                    length = "مدة الحلقة: " + duration?.filter { it.isDigit() } + " دقيقة",
                    year = "بداية العرض: " + date1?.toString(),
                    totalEp = "عدد الحلقات: $episodes",
                    season = premiered ?: "",
                    source = "المصدر: $source",
                    trailer = "",
                    MAL_Link = "",
                    malDetails = MalDetails(
                        MALId = animeID?: "",
                        MALName = animeLinkName?: "",
                        description = description ?: "",
                        score = score ?: "",
                        ranked = ranked ?: "",
                        popularity = popularity ?: "",
                        studio = studio ?: "",
                        episodesLink = "$episodesLink/episode"
                    )
                ))

                detailsList



            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("MALScraper", "getAnimeDetails: ${e.message}")
            null
        }
    }

    suspend fun getEpisodes(id: String, name: String): List<MALEpisode>? = withContext(Dispatchers.IO) {
        val call = service.getAnimeEpisodes(id, name)
        try {
            val response = call.execute()
            if (response.isSuccessful) {
                val html: String = response.body()?.string() ?: ""
                // Parse the HTML content
                val doc: Document = Jsoup.parse(html)
                // Select the desired elements and extract the information
                val episodes = doc.select("tr.episode-list-data")
                val episodesList = mutableListOf<MALEpisode>()
                episodes.forEach {
                    val numer = it.selectFirst("td.episode-number")?.text()
                    val name = it.selectFirst("td.episode-title a")?.text()
                    val dateAired = it.selectFirst("td.episode-aired")?.text()
                    val score = it.selectFirst("td.episode-poll div.average span")?.text()
                    val commentsCount = it.selectFirst("td.episode-forum a")?.text()
                    val commentsLink = it.selectFirst("td.episode-forum a")?.attr("href")

                        episodesList.add(MALEpisode(
                            numer?:"",
                            name?:"",
                            dateAired?:"",
                            score?:"",
                            commentsCount?:"",
                            commentsLink?:""))

                }
                episodesList
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getSimilarAnimes(id: String, name: String): List<Anime>? = withContext(Dispatchers.IO) {
        val call = service.getSimilarAnimes(id, name)
        try {
            val response = call.execute()
            if (response.isSuccessful) {
                val html: String = response.body()?.string() ?: ""
                // Parse the HTML content
                val doc: Document = Jsoup.parse(html)
                // Select the desired elements and extract the information
                val episodes = doc.select("div.rightside div.borderClass")
                val episodesList = mutableListOf<Anime>()
                episodes.forEach {
                    val image = it.selectFirst("table > tbody > tr > td:nth-child(1) > div.picSurround img")?.attr("data-src")?.replace("r/50x70/", "")
                    val name = it.selectFirst("table > tbody > tr > td:nth-child(2) > div:nth-child(2) > a:nth-child(1)")?.text()
                    val animeLink = it.selectFirst("table > tbody > tr > td:nth-child(2) > div:nth-child(2) > a:nth-child(1)")?.attr("href")
                    val userReason = it.selectFirst(" table > tbody > tr > td:nth-child(2) > div.borderClass.bgColor1 > div.spaceit_pad.detail-user-recs-text")?.text()
                    val numberOfRecommendation = it.selectFirst("table > tbody > tr > td:nth-child(2) > div.spaceit > a strong")?.text()

                    // log the data
                    if (name != null) {
                        episodesList.add(Anime(
                            name,
                            image?:"",
                            animeLink?:"",
                            numberOfRecommendation?:"0",
                            userReason?:"",
                            ""))
                    }
                }
                episodesList
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getAnimeRecommendations(pageCount: Int): RecommendationData? = withContext(Dispatchers.IO) {
        val call = service.getAnimeRecommendations(pageCount)
        try {
            val response = call.execute()
            if (response.isSuccessful) {
                val html: String = response.body()?.string() ?: ""
                // Parse the HTML content
                val doc: Document = Jsoup.parse(html)
                // Select the desired elements and extract the information
                val episodes = doc.select("div.spaceit.borderClass")
                val recommData = mutableListOf<List<Anime>>()
                val paging = doc.select("span.bgColor1")
                // check for all a herf in paging if contains pag_count + 100
                val nextPage = paging.select("a").any { it.attr("href").contains("show=${pageCount + 100}") }
                val prevPage = paging.select("a").any { it.attr("href").contains("show=${pageCount - 100}") }

                episodes.forEach {
                    val secData = mutableListOf<Anime>()
                    // select First td
                    val leftSide = it.selectFirst("table > tbody > tr > td:nth-child(1)")
                    // select second td
                    val RightSide = it.selectFirst("table > tbody > tr > td:nth-child(2)")

                    val theReason = it.selectFirst("div.recommendations-user-recs-text")?.text()

                    val lefAnimeName = leftSide?.selectFirst("a strong")?.text()
                    val leftAnimeLink = leftSide?.selectFirst("a")?.attr("href")
                    val leftAnimeImg = leftSide?.selectFirst("div.picSurround a img")
                        ?.attr("data-srcset")
                        ?.substringBefore(" ")
                        ?.replace(Regex("(\\d+)t"), "$1")


                    secData.add(Anime(
                        lefAnimeName?:"",
                        leftAnimeImg?:"",
                        leftAnimeLink?:"",
                        "",
                        theReason?:""
                    ))

                    val rightAnimeName = RightSide?.selectFirst("a strong")?.text()
                    val rightAnimeLink = RightSide?.selectFirst("a")?.attr("href")
                    val rightAnimeImg = RightSide?.selectFirst("div.picSurround a img")
                        ?.attr("data-srcset")
                        ?.substringBefore(" ")
                        ?.replace(Regex("(\\d+)t"), "$1")




                    // select data-srcset from img


                    secData.add(Anime(
                        rightAnimeName?:"",
                        rightAnimeImg?:"",
                        rightAnimeLink?:"",
                    ))

                    recommData.add(secData)

                }

                val nxtPage = if (nextPage) pageCount + 100 else null
                val prvPage = if (prevPage) pageCount - 100 else if (pageCount - 100 == 0) 0 else null
                RecommendationData(recommData, nxtPage, prvPage)
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("Error", e.message?:"")
            null
        }
    }

    suspend fun getEpisodeComments(id: String, count: Int = 0): ThreadData? = withContext(Dispatchers.IO) {
        val call = service.getEpisodeComments(id, count)
        try {
            val response = call.execute()
            if (response.isSuccessful) {
                val html: String = response.body()?.string() ?: ""
                // Parse the HTML content
                val doc: Document = Jsoup.parse(html)
                // Select the desired elements and extract the information
                val comments = doc.select("div.forum-topic-message ")
                val commentsList = mutableListOf<Comments>()
                val pagesList = mutableListOf<ThreadPages>()
                val firstDoc = doc.selectFirst("div.right div.pages")
                val pages = firstDoc?.select("a")

                pages?.forEach {item ->
                    if (item.text().isNotEmpty() && (item.text() == "«" || item.text() == "»")) {
                        pagesList.add(ThreadPages(
                            page = if (item.text() == "«") "previous" else "next",
                            item.attr("href")
                        ))
                    }
                }

                comments.forEach {
                    val username = it.selectFirst("div.username a")?.text()
                    val profilePic = it.selectFirst("a.forum-icon img")?.attr("data-src")
                    val date = it.selectFirst("div.message-header div.date")?.text()
                    val message = it.selectFirst("div.message-wrapper div.content div.body.clearfix")?.text()

                    if (message != null)
                        commentsList.add(Comments(
                            date?:"",
                            message,
                            username?:"",
                            profilePic?:""
                        ))
                }
                ThreadData(commentsList, null, pagesList)
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("error", e.message?:"")
            null
        }
    }

    suspend fun getTopAnime(type: String, count: Int = 0): List<AnimeDetails>? = withContext(Dispatchers.IO) {
        val call = service.getTopAnime(type, count)
        try {
            val response = call.execute()
            if (response.isSuccessful) {
                val html: String = response.body()?.string() ?: ""
                // Parse the HTML content
                val doc: Document = Jsoup.parse(html)
                // Select the desired elements and extract the information
                val episodes = doc.select("tr.ranking-list")
                val nextPage = doc.selectFirst("a.link-blue-box.next")?.attr("href")
                val prePage = doc.selectFirst("a.link-blue-box.prev.mr4")?.attr("href")
                val episodesList = mutableListOf<AnimeDetails>()
                episodes.forEach {
                    val number = it.selectFirst("td.rank")?.text()
                    val animePic = it.selectFirst("td.title a.hoverinfo_trigger img")?.attr("data-src")
                        ?.replace("r/50x70/", "")
                    val name = it.selectFirst("td.title div.detail a")?.text()
                    val animeInformation = it.selectFirst("td.title div.detail div.information")?.text()
                    val score = it.selectFirst("td.score")?.text()
                    val animeLink = it.selectFirst("td.title div.detail a")?.attr("href")

                    // get date, members, type and episodes count from animeInformation
                    val animeInfo = splitString(animeInformation?:"")

                        episodesList.add(AnimeDetails(
                            name = name?:"",
                            image = animePic?:"",
                            MAL_Link = animeLink?:"",
                            source = nextPage,
                            malDetails = MalDetails(
                                MALId = animeInfo.type,
                                MALName = prePage?:"",
                                description = animeInfo.members?:"",
                                score = score?:"",
                                ranked = number?:"",
                                popularity = animeInfo.startDate?.replace("-", "")?.trim()?:"?",
                                studio = animeInfo.endDate?:"?",
                                episodesLink = animeInfo.episodes.replace("eps", "").trim(),
                            )
                        ))


                }
                episodesList
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("error", e.message?:"")
            null
        }
    }

    suspend fun getSeasonalAnime(season: String, year: String): List<SeasonalAnime>? = withContext(Dispatchers.IO) {
        val call = if (year == "0") service.getLaterSeasonalAnime() else service.getSeasonalAnime(year, season.lowercase(Locale.ROOT))
        try {
            val response = call.execute()
            if (response.isSuccessful) {
                val html: String = response.body()?.string() ?: ""
                // Parse the HTML content
                val doc: Document = Jsoup.parse(html)
                val emptyList = mutableListOf<SeasonalAnime>()
                // Select the desired elements and extract the information
                val typeTV = doc.select("div.seasonal-anime.js-seasonal-anime.js-anime-type-1")
                val typeOVA = doc.select("div.seasonal-anime.js-seasonal-anime.js-anime-type-2")
                val typeMovie = doc.select("div.seasonal-anime.js-seasonal-anime.js-anime-type-3")
                val typeSpecial = doc.select("div.seasonal-anime.js-seasonal-anime.js-anime-type-4")
                val typeONA = doc.select("div.seasonal-anime.js-seasonal-anime.js-anime-type-5")

                emptyList.add(SeasonalAnime("TV", getSeasonalData(typeTV, "TV")))
                emptyList.add(SeasonalAnime("OVA", getSeasonalData(typeOVA, "OVA")))
                emptyList.add(SeasonalAnime("Movie", getSeasonalData(typeMovie, "Movie")))
                emptyList.add(SeasonalAnime("Special", getSeasonalData(typeSpecial, "Special")))
                emptyList.add(SeasonalAnime("ONA", getSeasonalData(typeONA, "ONA")))

                emptyList
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("error", e.message?:"")
            null
        }
    }

    suspend fun searchAnime(query: String, cat: String = "anime", count: Int = 0): List<Anime>? = withContext(Dispatchers.IO) {
        val call = service.searchAnime(query, cat, count)
        try {
            val response = call.execute()
            if (response.isSuccessful) {
                val html: String = response.body()?.string() ?: ""
                // Parse the HTML content
                val doc: Document = Jsoup.parse(html)
                // Select the desired elements and extract the information
                val episodes = doc.select("div.js-categories-seasonal.js-block-list.list > table > tbody > tr")
                val episodesList = mutableListOf<Anime>()
                // skip the first element
                episodes.forEach {
                    val animePic = it.selectFirst("div.picSurround a img")?.attr("data-src")
                        ?.replace("r/50x70/", "")
                    val name = it.selectFirst("div.title a strong")?.text()
                    val animeLink = it.selectFirst("div.title a")?.attr("href")
                    val type = it.selectFirst("td:nth-child(3)")?.text()
                    val episodes = it.selectFirst("td:nth-child(4)")?.text()
                    val score = it.selectFirst("td:nth-child(5)")?.text()


                    episodesList.add(Anime(
                        name?: "",
                        animePic?: "",
                        animeLink?: "",
                        episodes?: "",
                        type?: "",
                        score?: ""
                    ))
                }
                episodesList
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("error", e.message?:"")
            null
        }
    }

    suspend fun getAnimeReviews(animeId: String, name: String): ThreadData? = withContext(Dispatchers.IO) {
        val call = service.getAnimeReviews(animeId, name)
        try {
            val response = call.execute()
            if (response.isSuccessful) {
                val html: String = response.body()?.string() ?: ""
                // Parse the HTML content
                val doc: Document = Jsoup.parse(html)
                // Select the desired elements and extract the information
                val comments = doc.select("div.review-element.js-review-element")
                val commentsList = mutableListOf<AnimeReview>()
                val pagesList = mutableListOf<ThreadPages>()
                val firstDoc = doc.selectFirst("div.right div.pages")
                val pages = firstDoc?.select("a")

                pages?.forEach {item ->
                    if (item.text().isNotEmpty() && (item.text() == "Previous" || item.text() == "More Reviews")) {
                        pagesList.add(ThreadPages(
                            page = if (item.text() == "Previous") "previous" else "next",
                            item.attr("href")
                        ))
                    }
                }

                comments.forEach {
                    val username = it.selectFirst("div.username a")?.text()
                    val profilePic = it.selectFirst("div.thumb img")?.attr("data-src")
                    val date = it.selectFirst("div.body div.update_at")?.text()
                    val tags = it.select("div.body div.tags div.tag")
                    val message = it.selectFirst("div.body div.text")?.text()
                    val reactionNumbers = it.selectFirst("div.body div.icon-reaction span.num.js-num")?.text()
                    val dataReaction = it.attr("data-reactions")
                    val reviewerRating = it.selectFirst("div.body div.rating span.num")?.text()

                    if (message != null)
                        commentsList.add(AnimeReview(
                            username?:"",
                            profilePic?:"",
                            date?:"",
                            tags?.map { tag -> tag.text() }?: emptyList(),
                            message,
                            reactionNumbers?:"",
                            dataReaction?.map { reaction -> reaction.toString() }?: emptyList(),
                            reviewerRating?:""
                        ))
                }
                ThreadData(null, commentsList, pagesList)
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("error", e.message?:"")
            null
        }
    }

    suspend fun getSeaons(): List<Season>? = withContext(Dispatchers.IO) {

        val call = service.getSeasons()
        try {
            val response = call.execute()
            if (response.isSuccessful) {
                val html: String = response.body()?.string() ?: ""
                // Parse the HTML content
                val doc: Document = Jsoup.parse(html)
                // Select the desired elements and extract the information
                val seasons = doc.select("div.navi-seasonal.js-navi-seasonal div.horiznav_nav ul li")
                val seasonsList = mutableListOf<Season>()
                seasons.forEach {

                    val fullSeason = it.selectFirst("a")?.text() ?: ""
                    // if full season contain year
                    if (fullSeason.split(" ").last().toIntOrNull() != null) {
                        val season = fullSeason.split(" ").first()
                        val year = fullSeason.split(" ").last().toInt()
                        val seasonLink = it.selectFirst("a")?.attr("href")

                        seasonsList.add(
                            Season(
                                season = season,
                                year = year,
                                seasonLink = seasonLink ?: ""
                            )
                        )
                        return@forEach
                    } else if (fullSeason.contains("Later")) {
                        val season = fullSeason.split(" ").first()
                        val year = 0
                        val seasonLink = it.selectFirst("a")?.attr("href")

                        seasonsList.add(
                            Season(
                                season = season,
                                year = year,
                                seasonLink = seasonLink ?: ""
                            )
                        )
                        return@forEach
                    }
                }
                seasonsList
            } else {
                Log.e("error", response.message())
                null
            }
        } catch (e: Exception) {
            Log.e("error", e.message?:"")
            null
        }
    }

    private fun getSeasonalData(data: Elements, type: String): List<AnimeDetails>  {
        val episodesList = mutableListOf<AnimeDetails>()
        data.forEach {
            var animePic = it.selectFirst("div.image a img")?.attr("srcset")
                ?.substringBefore("1x,")?.trim()
            val name = it.selectFirst("div.title div.title-text h2.h2_anime_title a")?.text()
            val animeLink = it.selectFirst("div.title div.title-text h2.h2_anime_title a")?.attr("href")
            val barInfo = it.select("div.prodsrc div.info span")
            val animeDate = barInfo[0].text()
            val epstime = barInfo[1].text()
            val genres = it.select("div.genres-inner.js-genre-inner a")
            val animeDescription = it.selectFirst("div.synopsis.js-synopsis div.preline")?.text()

            val properties = it.select("div.properties div.property")

            val studio = if (properties.size > 0) properties[0].selectFirst("span.item a")?.text() else null
            val source = if (properties.size > 1) properties[1].selectFirst("span.item a")?.text() else null
            val themes = if (properties.size > 2) properties[2].select("span.item") else null
            val demographics = if (properties.size > 3) properties[3].selectFirst("span.item a")?.text() else null


            val score = it.selectFirst("div.scormem-item.score.score-label")?.text()
            val members = it.selectFirst("div.scormem-item.member")?.text()

            if (animePic != null) {
                if (animePic.isEmpty())
                    animePic = it.selectFirst("div.image a img")?.attr("data-srcset")
                        ?.substringBefore("1x,")?.trim()
            }

            episodesList.add(AnimeDetails(
                name = name?:"",
                image = animePic?:"",
                MAL_Link = animeLink?:"",
                malDetails = MalDetails(
                    MALId = animeDate,
                    MALName = epstime,
                    description = animeDescription?:"",
                    score = score?:"",
                    ranked = "",
                    popularity = members?:"",
                    studio = studio?:"",
                    episodesLink = "",
                )
            ))
        }
        return episodesList
    }

    private fun parseDateOrYearMonth(dateString: String): Any? {
        val formatStrings = listOf("MMM d, yyyy", "MMM, yyyy", "MMM yyyy")

        for (formatString in formatStrings) {
            try {
                val date = LocalDate.parse(dateString, DateTimeFormatter.ofPattern(formatString))
                val yearMonthFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy")
                return date.format(yearMonthFormatter)
            } catch (e: DateTimeParseException) {
                // ignore and try next format
                Log.e("error", e.message?:"")
            }
        }

        for (formatString in formatStrings) {
            try {
                val date = YearMonth.parse(dateString, DateTimeFormatter.ofPattern(formatString))
                val yearMonthFormatter = DateTimeFormatter.ofPattern("MMM yyyy")
                return date.format(yearMonthFormatter)
            } catch (e: DateTimeParseException) {
                // ignore and try next format
                Log.e("error", e.message?:"")
            }
        }

        return "?"
    }

    private fun splitString(input: String): TVShow {
        val regex = Regex("""(.+?)\s\((.+?)\)\s(.+?members)""")
        val matchResult = regex.find(input) ?: return TVShow("", "", null, null, null)
        val (type, episodes, rest) = matchResult.destructured

        val parts = rest.split(" ")
        val dateIndex = parts.indexOf("-")
        val dateEndIndex = parts.indexOfFirst { it.contains("members") } - 1
        val (startDate, endDate) = if (dateIndex > 0) {
            val dateParts = parts.slice(0 until dateEndIndex).joinToString(" ").split(" - ")
            Pair(dateParts[0], dateParts.getOrNull(1))
        } else {
            Pair(null, null)
        }

        val membersIndex = parts.indexOfLast { it.contains("members") }
        val members = parts[membersIndex - 1]

        return TVShow(type.trim(), episodes.trim(), startDate?.trim(), endDate?.trim(), members.trim())
    }

}

data class TVShow(val type: String, val episodes: String, val startDate: String?, val endDate: String?, val members: String?)

interface MALService {
    @GET
    fun getMainPage(@Url url: String): Call<ResponseBody>

    @GET("/anime/{id}")
    fun getAnimeDetails(@Path("id") id: Int): Call<ResponseBody>

    @GET("/anime/{id}/{name}/episode")
    fun getAnimeEpisodes(@Path("id") id: String, @Path("name") name: String): Call<ResponseBody>

    @GET("/anime/{id}/{name}/userrecs")
    fun getSimilarAnimes(@Path("id") id: String, @Path("name") name: String): Call<ResponseBody>

    @GET("/anime/{id}/{name}/reviews")
    fun getAnimeReviews(
        @Path("id") id: String,
        @Path("name") name: String,
        @Query("sort") sort: String? = null,
        @Query("filter_check") filter_check: Int? = null,
        @Query("filter_hide") filter_hide: Int? = null,
        @Query("preliminary") preliminary: String? = null,
        @Query("spoiler") spoiler: String? = null,
        @Query("p") page: String? = null

    ): Call<ResponseBody>

    @GET("/recommendations.php?s=recentrecs&t=anime")
    fun getAnimeRecommendations(@Query("show") count: Int): Call<ResponseBody>

    @GET("/forum/")
    fun getEpisodeComments(@Query("topicid") id: String, @Query("show") count: Int): Call<ResponseBody>

    @GET("/topanime.php")
    fun getTopAnime(@Query("type") type: String, @Query("limit") count: Int): Call<ResponseBody>

    @GET("/anime/season/{year}/{season}")
    fun getSeasonalAnime(@Path("year") year: String?, @Path("season") season: String?): Call<ResponseBody>

    @GET("/anime/season/later")
    fun getLaterSeasonalAnime(): Call<ResponseBody>

    @GET("/anime/season")
    fun getSeasons(): Call<ResponseBody>

    // search anime
    @GET("/anime.php")
    fun searchAnime(@Query("q") query: String, @Query("cat") cat: String, @Query("show") count: Int?): Call<ResponseBody>


}
