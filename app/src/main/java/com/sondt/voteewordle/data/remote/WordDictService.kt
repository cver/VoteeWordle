package com.sondt.voteewordle.data.remote

import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Streaming

interface WordDictService {
    @Streaming
    @GET("mwiens91/english-words-py/master/english_words/__init__.py")
    fun downloadEnglishWordsFile(): Observable<ResponseBody>

    // another dict source for testing
//    @Streaming
//    @GET("lorenbrichter/Words/master/Words/en.txt")
//    fun downloadEnglishWordsFile2(): Observable<ResponseBody>
}
