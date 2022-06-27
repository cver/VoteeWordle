package com.sondt.voteewordle.data.remote

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory

object ApiService {
    fun <T> buildService(url: String, serviceType: Class<T>): T {
        val moshi = Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory())
            .build()
        val logger = HttpLoggingInterceptor().also { it.setLevel(HttpLoggingInterceptor.Level.BODY) }
        val client = OkHttpClient.Builder()
            .addInterceptor(logger)
            .build()
        val retrofit = Retrofit.Builder()
            .client(client)
            .baseUrl(url)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
        return retrofit.create(serviceType)
    }
}
