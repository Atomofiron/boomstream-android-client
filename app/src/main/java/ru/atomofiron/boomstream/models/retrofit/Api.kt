package ru.atomofiron.boomstream.models.retrofit

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*
import ru.atomofiron.boomstream.models.retrofit.folder.Folder

interface Api {

    companion object {
        val STATUS_SUCCESS = "Success"
        val STATUS_FAILED = "Failed"

        val MEDIA_STATUS_ERROR = "Error"
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
