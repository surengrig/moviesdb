package app.example.movies.model


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

//Todo remove not used fields
@JsonClass(generateAdapter = true)
data class Movie(
    @Json(name = "id")
    val id: Int,

    @Json(name = "adult")
    val adult: Boolean? = null,
    @Json(name = "backdrop_path")
    val backdropPath: String? = null,
    @Json(name = "belongs_to_collection")
    val belongsToCollection: BelongsToCollection? = null,
    @Json(name = "budget")
    val budget: Int? = null,
    @Json(name = "genres")
    val genres: List<Genre>? = null,
    @Json(name = "homepage")
    val homepage: String? = null,
    @Json(name = "imdb_id")
    val imdbId: String? = null,
    @Json(name = "original_language")
    val originalLanguage: String? = null,
    @Json(name = "original_title")
    val originalTitle: String? = null,
    @Json(name = "overview")
    val overview: String? = null,
    @Json(name = "popularity")
    val popularity: Double? = null,
    @Json(name = "poster_path")
    val posterPath: String? = null,
    @Json(name = "production_companies")
    val productionCompanies: List<ProductionCompany>? = null,
    @Json(name = "production_countries")
    val productionCountries: List<ProductionCountry>? = null,
    @Json(name = "release_date")
    val releaseDate: String? = null,
    @Json(name = "revenue")
    val revenue: Int? = null,
    @Json(name = "runtime")
    val runtime: Int? = null,
    @Json(name = "spoken_languages")
    val spokenLanguages: List<SpokenLanguage>? = null,
    @Json(name = "status")
    val status: String? = null,
    @Json(name = "tagline")
    val tagline: String? = null,
    @Json(name = "title")
    val title: String? = null,
    @Json(name = "video")
    val video: Boolean? = null,
    @Json(name = "vote_average")
    val voteAverage: Double? = null,
    @Json(name = "vote_count")
    val voteCount: Int? = null
) {
    @JsonClass(generateAdapter = true)
    data class BelongsToCollection(
        @Json(name = "id")
        val id: Int,

        @Json(name = "backdrop_path")
        val backdropPath: String? = null,
        @Json(name = "name")
        val name: String? = null,
        @Json(name = "poster_path")
        val posterPath: String? = null
    )

    @JsonClass(generateAdapter = true)
    data class Genre(
        @Json(name = "id")
        val id: Int,

        @Json(name = "name")
        val name: String? = null
    )

    @JsonClass(generateAdapter = true)
    data class ProductionCompany(
        @Json(name = "id")
        val id: Int,

        @Json(name = "logo_path")
        val logoPath: String? = null,
        @Json(name = "name")
        val name: String? = null,
        @Json(name = "origin_country")
        val originCountry: String
    )

    @JsonClass(generateAdapter = true)
    data class ProductionCountry(
        @Json(name = "iso_3166_1")
        val iso31661: String? = null,
        @Json(name = "name")
        val name: String? = null
    )

    @JsonClass(generateAdapter = true)
    data class SpokenLanguage(
        @Json(name = "iso_639_1")
        val iso6391: String? = null,
        @Json(name = "name")
        val name: String? = null
    )
}