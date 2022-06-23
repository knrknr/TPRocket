package com.mrhi2022.tpquickplaceforkakaoknr.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

//1)
class RetrofitHelper {
    companion object{//클래스와 동반되어 있는 객체
        fun getRetrofitInstance(baseUrl:String): Retrofit{
            val retrofit= Retrofit.Builder()
                                    .baseUrl(baseUrl)
                                    .addConverterFactory(ScalarsConverterFactory.create())
                                    .addConverterFactory(GsonConverterFactory.create())
                                    .build()
            return retrofit
        }
    }
}