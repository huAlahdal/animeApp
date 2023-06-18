package com.example.blinkanime.newsFragment

data class Tweet(
    val created_at: String,
    val id: Long,
    val id_str: String,
    val full_text: String,
    val truncated: Boolean,
    val display_text_range: List<Int>,
    val entities: TweetEntities,
    val extended_entities: TweetEntities,
    val source: String,
    val retweeted_status: Tweet?,
    val in_reply_to_status_id: Long?,
    val in_reply_to_status_id_str: String?,
    val in_reply_to_user_id: Long?,
    val in_reply_to_user_id_str: String?,
    val in_reply_to_screen_name: String?,
    val user: User,
    var isExpanded: Boolean = false
)

data class TweetEntities(
    val hashtags: List<Any>,
    val symbols: List<Any>,
    val user_mentions: List<Any>,
    val urls: List<Any>,
    val media: List<Media>?
)

data class Media(
    val id: Long,
    val id_str: String,
    val indices: List<Int>,
    val media_url: String,
    val media_url_https: String,
    val url: String,
    val display_url: String,
    val expanded_url: String,
    val `type`: String,
    val sizes: MediaSizes,
    val video_info: VideoInfo
)

data class MediaSizes(
    val large: MediaSize,
    val medium: MediaSize,
    val small: MediaSize,
    val thumb: MediaSize
)

data class MediaSize(
    val w: Int,
    val h: Int,
    val resize: String
)

data class VideoInfo(
    val aspect_ratio: List<Int>,
    val duration_millis: Int,
    val variants: List<Variant>
)

data class Variant(
    val bitrate: Int,
    val content_type: String,
    val url: String
)

data class User(
    val id: Long,
    val id_str: String,
    val name: String,
    val screen_name: String,
    val location: String,
    val description: String,
    val url: String?,
    val entities: UserEntities,
    val protected: Boolean,
    val followers_count: Int,
    val friends_count: Int,
    val listed_count: Int,
    val created_at: String,
    val favourites_count: Int,
    val utc_offset: Int?,
    val time_zone: String?,
    val geo_enabled: Boolean,
    val verified: Boolean,
    val statuses_count: Int,
    val lang: String?,
    val contributors_enabled: Boolean,
    val is_translator: Boolean,
    val is_translation_enabled: Boolean,
    val profile_background_color: String,
    val profile_background_image_url: String?,
    val profile_background_image_url_https: String?,
    val profile_background_tile: Boolean,
    val profile_image_url: String,
    val profile_image_url_https: String,
    val profile_banner_url: String?,
    val profile_link_color: String,
    val profile_sidebar_border_color: String,
    val profile_sidebar_fill_color: String,
    val profile_text_color: String,
    val profile_use_background_image: Boolean,
    val default_profile: Boolean,
    val default_profile_image: Boolean,
    val following: Any?,
    val follow_request_sent: Any?,
    val notifications: Any?
)

data class UserEntities(
    val url: UserUrl?,
    val description: UserDescription
)

data class UserUrl(
    val urls: List<UserUrlDetails>
)

data class UserUrlDetails(
    val url: String,
    val expanded_url: String,
    val display_url: String,
    val indices: List<Int>
)

data class UserDescription(
    val urls: List<Any>
)