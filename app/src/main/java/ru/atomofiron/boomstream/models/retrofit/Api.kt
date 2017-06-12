package ru.atomofiron.boomstream.models.retrofit

import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*
import ru.atomofiron.boomstream.models.retrofit.folder.Folder
import ru.atomofiron.boomstream.models.retrofit.media.UploadResult


interface Api {

    companion object {
        val STATUS_SUCCESS = "Success"
        val STATUS_FAILED = "Failed"
    }

    @GET("/api/media/folder")
    fun getFolder(@Query("apikey") apikey: String,
                  @Query("format") format: String,
                  @Query("limit") limit: Int,
                  @Query("offset") offset: Int,
                  @Query("code") code: String
    ): Call<Folder>

    @GET
    fun loadData(@Url url: String): Call<ResponseBody>

}
