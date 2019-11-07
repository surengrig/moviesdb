package app.example.movies.model


import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize


@JsonClass(generateAdapter = true)
data class TrendingMovies(
    @Json(name = "page")
    val page: Int,
    @Json(name = "results")
    val results: List<Movie>,
    @Json(name = "total_pages")
    val totalPages: Int,
    @Json(name = "total_results")
    val totalResults: Int
) {
    @Parcelize
    @JsonClass(generateAdapter = true)
    data class Movie(
        @Json(name = "id")
        val id: Int,

        @Json(name = "adult")
        val adult: Boolean?,
        @Json(name = "backdrop_path")
        val backdropPath: String?,
        @Json(name = "first_air_date")
        val firstAirDate: String?,
        @Json(name = "genre_ids")
        val genreIds: List<Int>?,
        @Json(name = "media_type")
        val mediaType: String?,
        @Json(name = "name")
        val name: String?,
        @Json(name = "origin_country")
        val originCountry: List<String>?,
        @Json(name = "original_language")
        val originalLanguage: String?,
        @Json(name = "original_name")
        val originalName: String?,
        @Json(name = "original_title")
        val originalTitle: String?,
        @Json(name = "overview")
        val overview: String?,
        @Json(name = "popularity")
        val popularity: Double?,
        @Json(name = "poster_path")
        val posterPath: String?,
        @Json(name = "release_date")
        val releaseDate: String?,
        @Json(name = "title")
        val title: String?,
        @Json(name = "video")
        val video: Boolean?,
        @Json(name = "vote_average")
        val voteAverage: Double?,
        @Json(name = "vote_count")
        val voteCount: Int?
    ) : Parcelable
}