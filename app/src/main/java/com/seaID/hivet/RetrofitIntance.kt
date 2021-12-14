package com.seaID.hivet

import com.seaID.hivet.Contants.Contants
import com.seaID.hivet.`interface`.NotifKonsulAPI
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

class RetrofitIntance {
    companion object{
        private val retrofit by lazy {
            Retrofit.Builder()
                .baseUrl(Contants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        val api by lazy{
            retrofit.create(NotifKonsulAPI::class.java)
        }
    }
}