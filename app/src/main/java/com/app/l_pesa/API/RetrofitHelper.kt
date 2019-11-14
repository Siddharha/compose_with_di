package com.app.l_pesa.API

import com.app.l_pesa.BuildConfig
import com.app.l_pesa.BuildConfig.BASE_URL
import com.app.l_pesa.BuildConfig.BASE_URL_DEV
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


class RetrofitHelper {
    companion object {


        private const val APPLICATION_JSON = "application/json"
        private const val CLIENT_TYPE = "A"

        private fun getOkHttpClient(accessToken: String): OkHttpClient {
            val okHttpClient = OkHttpClient.Builder()
            okHttpClient.readTimeout(120, TimeUnit.SECONDS)
            okHttpClient.connectTimeout(120, TimeUnit.SECONDS)

            okHttpClient.addInterceptor { chain ->
                val original = chain.request()

                val requestBuilder = original.newBuilder()
                        .header("Accept", APPLICATION_JSON)
                        .header("Content-Type", APPLICATION_JSON)
                        .header("Client-Type", CLIENT_TYPE)
                        .header("Build", BuildConfig.VERSION_CODE.toString())
                        .header("Version", BuildConfig.VERSION_NAME)

                if (accessToken.isNotEmpty()) {
                    requestBuilder.header("Authorization", accessToken)
                }

                val request = requestBuilder.build()

                chain.proceed(request)
            }

            return okHttpClient.build()
        }



        fun <T> getRetrofit(service: Class<T>): T {
           /* if(BuildConfig.DEBUG)
            {
                val retrofit = Retrofit.Builder()
                        .baseUrl(BASE_URL_DEV)
                        .addConverterFactory(GsonConverterFactory.create())
                        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                        .build()
                return retrofit.create(service)
            }
            else
            {*/
                val retrofit = Retrofit.Builder()
                        .baseUrl(BASE_URL_DEV)//BASE_URL_DEV//BASE_URL
                        .addConverterFactory(GsonConverterFactory.create())
                        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                        .build()
                return retrofit.create(service)
           // }

        }

        fun <T> getRetrofitToken(service: Class<T>, accessToken: String = ""): T {

           /* if(BuildConfig.DEBUG)
            {
                val retrofit = Retrofit.Builder()
                        .baseUrl(BASE_URL_DEV)
                        .client(getOkHttpClient(accessToken))
                        .addConverterFactory(GsonConverterFactory.create())
                        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                        .build()
                return retrofit.create(service)
            }
            else
            {
              */  val retrofit = Retrofit.Builder()
                        .baseUrl(BASE_URL_DEV) //BASE_URL_DEV//BASE_URL
                        .client(getOkHttpClient(accessToken))
                        .addConverterFactory(GsonConverterFactory.create())
                        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                        .build()
                return retrofit.create(service)
            //}

        }

    }
}



