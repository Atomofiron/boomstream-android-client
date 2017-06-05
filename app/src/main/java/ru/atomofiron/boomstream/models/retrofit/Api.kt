package ru.atomofiron.boomstream.models.retrofit

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import ru.atomofiron.boomstream.models.retrofit.folder.Folder
import retrofit2.http.Url



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

    @GET("/api/media/create")
    fun createMedia(@Query("apikey") apikey: String,
                  @Query("format") format: String,
                  @Query("title") title: String,
                  @Query("link") link: String,
                  @Query("folderCode") folderCode: String
    ): Call<Result>
}
