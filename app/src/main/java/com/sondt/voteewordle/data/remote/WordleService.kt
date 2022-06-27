package com.sondt.voteewordle.data.remote

import com.sondt.voteewordle.domain.entity.GuessResult
import io.reactivex.Maybe
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface WordleService {
    @GET("daily")
    fun guessDaily(
        @Query("guess") guess: String,
        @Query("size") size: Int,
    ): Maybe<List<GuessResult>>

    @GET("random")
    fun guessRandom(
        @Query("guess") guess: String,
        @Query("size") size: Int,
        @Query("seed") seed: Int,
    ): Maybe<List<GuessResult>>

    @GET("word")
    fun guessWord(
        @Path("word") word: String,
        @Query("guess") guess: String,
    ): Maybe<List<GuessResult>>
}
