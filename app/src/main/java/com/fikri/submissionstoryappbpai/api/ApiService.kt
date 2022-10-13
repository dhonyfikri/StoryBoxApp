package com.fikri.submissionstoryappbpai.api

import com.fikri.submissionstoryappbpai.data_model.AddStoryResponseModel
import com.fikri.submissionstoryappbpai.data_model.AllStoryResponseModel
import com.fikri.submissionstoryappbpai.data_model.LoginResponseModel
import com.fikri.submissionstoryappbpai.data_model.RegisterResponseModel
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @FormUrlEncoded
    @POST("register")
    fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String,
    ): Call<RegisterResponseModel>

    @FormUrlEncoded
    @POST("login")
    fun login(
        @Field("email") email: String,
        @Field("password") password: String,
    ): Call<LoginResponseModel>

    @GET("stories")
    fun getAllStories(
        @Header("Authorization") authorization: String,
        @Query("size") size: Int,
        @Query("location") locationEnable: Int
    ): Call<AllStoryResponseModel>

    @Multipart
    @POST("stories")
    fun addStory(
        @Header("Authorization") authorization: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody
    ): Call<AddStoryResponseModel>
}