package com.chessigma.app.data.remote.dto

import com.google.gson.annotations.SerializedName

data class LichessPuzzleResponse(
    @SerializedName("puzzle") val puzzle: LichessPuzzleData,
    @SerializedName("game") val game: LichessGameData
)

data class LichessPuzzleData(
    @SerializedName("id") val id: String,
    @SerializedName("rating") val rating: Int,
    @SerializedName("initialFen") val initialFen: String,
    @SerializedName("solution") val solution: List<String>,
    @SerializedName("themes") val themes: List<String>
)

data class LichessGameData(
    @SerializedName("id") val id: String,
    @SerializedName("perf") val perf: LichessPerfData
)

data class LichessPerfData(
    @SerializedName("name") val name: String
)
