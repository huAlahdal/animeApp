package com.example.blinkanime.misc

// Recommendation
data class Recommendation(
    val pagination: Pagination,
    val data: List<RecommendationEntry>
)

data class RecommendationEntry(
    val mal_id: String,
    val entry: List<Entry>,
    val content: String,
    val date: String,
    val user: User
)

data class Entry(
    val mal_id: Int,
    val url: String,
    val images: Images,
    val title: String
)

data class Images(
    val jpg: Image,
    val webp: Image
)

data class Image(
    val image_url: String,
    val small_image_url: String,
    val large_image_url: String
)

data class User(
    val url: String,
    val username: String
)

// news
data class Pagination(
    val last_visible_page: Int,
    val has_next_page: Boolean
)

data class NewsData(
    val mal_id: Int,
    val url: String,
    val title: String,
    val date: String,
    val author_username: String,
    val author_url: String,
    val forum_url: String,
    val images: Images,
    val comments: Int,
    val excerpt: String
)

data class MALDataNews(
    val pagination: Pagination,
    val data: List<NewsData>
)

// episode
data class MALDataEpisode (
    val data: List<AnimeEpisode>,
    val pagination: Pagination
)

data class AnimeEpisode(
    val mal_id: Int,
    val url: String,
    val title: String,
    val title_japanese: String,
    val title_romanji: String,
    val duration: Int,
    val aired: String,
    val filler: Boolean,
    val recap: Boolean,
    val forum_url: String
)