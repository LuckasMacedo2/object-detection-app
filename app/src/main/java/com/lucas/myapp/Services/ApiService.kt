package com.lucas.myapp.Services

import com.lucas.myapp.Data.DetectedObject
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @GET("/index")
    fun getIndex(): Call<ResponseBody>

    @Multipart
    @POST("enviar-imagem")
    fun enviarImagem(@Part image: MultipartBody.Part): Call<List<DetectedObject>>
}
