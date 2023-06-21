package com.example.blinkanime.misc

import com.example.blinkanime.mainFragment.Anime
import com.example.blinkanime.mainFragment.AnimeDetails

data class MALAnimeData(
    val score: String,
    val ranked: String,
    val popularity: String,
    val animeLink: String,
    val animeImage: String,
    val animeName: String,
    val description: String,
    val translatedDescription: String,
    val type: String,
    val episodes: String,
    val status: String,
    val aired: String,
    val premiered: String,
    val studio: String,
    val source: String,
    val duration: String,
    val rating: String,
    val genres: List<String>,
    val themes: List<String>,
    val animeID: String,
    val animeLinkName: String
)

data class ThreadData(
    val comments: List<Comments>? = null,
    val reviews: List<AnimeReview>? = null,
    val pages: List<ThreadPages>
)

data class ThreadPages (
    val page: String,
    val url: String
)

data class Comments(
    val date: String,
    val message: String,
    val user: String,
    val userImage: String
)

data class RecommendationData(
    val anime: List<List<Anime>>,
    val nextPage: Int?,
    val prevPage: Int?
)

data class Season(
    val season: String,
    val year: Int,
    val seasonLink: String
)

data class SeasonalAnime(
    val type: String,
    val list: List<AnimeDetails>
)

data class AnimeReview(
    val username: String,
    val profilePic: String,
    val date: String,
    val tags: List<String>,
    val message: String,
    val reactionNumbers: String,
    val dataReaction: List<String>,
    val reviewerRating: String
)