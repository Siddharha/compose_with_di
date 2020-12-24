package com.app.l_pesa.zoop

import com.app.l_pesa.BuildConfig.*
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import sdk.zoop.one.offline_aadhaar.zoopUtils.ZoopConstantUtils.ZOOP_BASE_URL
import java.util.concurrent.TimeUnit


class ApiClient {
//@Headers(
//        "qt_api_key: 0df72ce0-37ab-4852-a0ab-7b94c7eb8494",
//        "qt_agency_id: caf33c4c-f14b-4c8e-8282-11db5758ed9e",
//            "Content-Type:  application/json"
//    )

        private var retrofit: Retrofit? = null

        fun getClient(): Retrofit {
            val okHttpClient = OkHttpClient().newBuilder().addInterceptor { chain ->
                val originalRequest = chain.request()

                val builder = originalRequest.newBuilder()
           //     builder.header("qt_api_key", "21bf7b62-3b4d-4f11-96a3-f990721d473f") //Pre Prod api key 0df72ce0-37ab-4852-a0ab-7b94c7eb8494
            //    builder.header("qt_agency_id", "caf33c4c-f14b-4c8e-8282-11db5758ed9e") //Pre Pro Agency id caf33c4c-f14b-4c8e-8282-11db5758ed9e
                builder.header("Content-Type", "application/json")
                val newRequest = builder.build()
                chain.proceed(newRequest)
            }
            okHttpClient.readTimeout(60, TimeUnit.SECONDS)
                    .connectTimeout(60 / 2, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS)
                    .cache(null)
                    .build()


            val gson = GsonBuilder()
                    .setLenient()
                    .create()

            if (retrofit == null) {
                retrofit = Retrofit.Builder()
                        .baseUrl(BASE_URL_DEV)
                        .client(okHttpClient.build())
                        .addConverterFactory(GsonConverterFactory.create(gson))
                        .build()
            }

            return retrofit!!
        }
    }



