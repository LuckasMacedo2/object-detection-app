package com.lucas.myapp.Services

import com.lucas.myapp.APIConstantes
import com.lucas.myapp.Data.DetectedObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import okhttp3.*
import java.util.concurrent.TimeUnit

class ApiServiceClient {
    private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(5, TimeUnit.MINUTES)
        .readTimeout(5, TimeUnit.MINUTES)
        .writeTimeout(5, TimeUnit.MINUTES)
        .build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(APIConstantes.URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(okHttpClient)
        .build()

    private val apiService: ApiService = retrofit.create(ApiService::class.java)

    fun enviarImagem(imageFile: File, callback: (List<DetectedObject>?) -> Unit) {
        val requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), imageFile)
        val imagePart = MultipartBody.Part.createFormData("image", imageFile.name, requestFile)

        val call = apiService.enviarImagem(imagePart)

        apiService.enviarImagem(imagePart).enqueue(object : Callback<List<DetectedObject>> {
            override fun onResponse(call: Call<List<DetectedObject>>, response: Response<List<DetectedObject>>) {
                if (response.isSuccessful) {
                    val detectedObjects: List<DetectedObject>? = response.body()
                    callback(detectedObjects)
                } else {
                    callback(null)
                }
            }

            override fun onFailure(call: Call<List<DetectedObject>>, t: Throwable) {
                callback(null)
            }
        })

    }
}
