package com.unknown.nationtimes.network

import com.unknown.nationtimes.util.Constants
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitInstance {

    companion object {
        // using lazy to init it just when needed

        private val retrofit by lazy {
            // using interceptor to inject a stable query parameter which is key
            val keyInterceptor  = Interceptor.invoke { chain ->
                val url =chain.request().url.newBuilder().addQueryParameter("apiKey", Constants.API_KEY).build()
                val request = chain.request().newBuilder().url(url).build()
                chain.proceed(request)
            }
            // add key interceptor which is contain api key
            // add logging interceptor to help in debugging
            val client = OkHttpClient
                .Builder()
                .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .addInterceptor(keyInterceptor)
                .build()


            // create and return an object of Retrofit immediately
        Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()


        }
        // using lazy to init it just when needed
        val mApi: NewsApiInterface by lazy {
            retrofit.create(NewsApiInterface::class.java)
        }


    }
}