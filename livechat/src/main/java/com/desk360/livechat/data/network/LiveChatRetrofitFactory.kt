package com.desk360.livechat.data.network

import android.net.Uri
import com.desk360.livechat.BuildConfig
import com.desk360.livechat.manager.Desk360LiveChat
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class LiveChatRetrofitFactory private constructor() {
    var chatService: LiveChatService? = null

    init {
        chatService = Desk360LiveChat.manager?.domainAddress?.let { domainAddress ->
            val url = Uri.parse(domainAddress)
                .buildUpon()
                .appendPath("api")
                .appendPath("v1")
                .build()
                .toString()
            createService(
                LiveChatService::class.java,
                "$url/",
                Desk360LiveChat.manager?.token
            )
        }
    }

    private fun <T> createService(
        serviceClass: Class<T>,
        baseUrl: String,
        token: String? = null
    ): T {

        val logging = HttpLoggingInterceptor()

        if (BuildConfig.DEBUG)
            logging.level = HttpLoggingInterceptor.Level.BODY

        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(timeOutInterval.toLong(), TimeUnit.SECONDS)
            .readTimeout(timeOutInterval.toLong(), TimeUnit.SECONDS)
            .addInterceptor { chain ->
                var request = chain.request()
                request = request.newBuilder().apply {
                    token?.let {
                        addHeader(AUTHORIZATION, token)
                    }
                }.build()
                chain.proceed(request)
            }
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()

        return retrofit.create(serviceClass)
    }

    companion object {
        const val AUTHORIZATION = "Authorization"
        const val timeOutInterval = 1000

        private var INSTANCE: LiveChatRetrofitFactory? = null
        val instance: LiveChatRetrofitFactory
            get() {
                if (INSTANCE == null) {
                    INSTANCE = LiveChatRetrofitFactory()
                }
                return INSTANCE!!
            }
    }
}