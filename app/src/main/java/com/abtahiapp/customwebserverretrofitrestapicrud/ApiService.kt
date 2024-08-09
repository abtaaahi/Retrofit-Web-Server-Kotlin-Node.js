package com.abtahiapp.customwebserverretrofitrestapicrud

import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @GET("get")
    fun getItems(): Call<List<Item>>

    @GET("get/{id}")
    fun getItem(@Path("id") id: Int): Call<Item>

    @POST("post")
    fun createItem(@Body item: Item): Call<Item>

    @PUT("update/{id}")
    fun updateItem(@Path("id") id: Int, @Body item: Item): Call<Item>

    @DELETE("delete/{id}")
    fun deleteItem(@Path("id") id: Int): Call<Void>
}