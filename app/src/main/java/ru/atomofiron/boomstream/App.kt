package ru.atomofiron.boomstream

import android.app.Application
import android.content.Context
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatDelegate

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.atomofiron.boomstream.models.retrofit.Api
import com.danikula.videocache.HttpProxyCacheServer



class App : Application() {

    companion object {
        lateinit var cachePath: String
        lateinit var api: Api
            private set

        var apikey: String = ""

        private var videoCache: HttpProxyCacheServer? = null

        fun getVideoCache(context: Context?): HttpProxyCacheServer {
            if (videoCache == null)
                updateVideoCache(context)

            return videoCache!!
        }
        fun updateVideoCache(context: Context?) {
            val sp = PreferenceManager.getDefaultSharedPreferences(context)
            videoCache = HttpProxyCacheServer.Builder(context)
                    .fileNameGenerator { url -> url.substring(url.lastIndexOf('/') + 1) }
                    .maxCacheSize(1024L * 1024L * sp.getInt(I.PREF_CACHE_SIZE_MB, 100))
                    .maxCacheFilesCount(sp.getInt(I.PREF_CACHE_COUNT, 10))
                    .build()
        }
    }

    override fun onCreate() {
        super.onCreate()

        cachePath = baseContext.cacheDir.absolutePath
        val retrofit = Retrofit.Builder()
                .baseUrl("https://boomstream.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        api = retrofit.create(Api::class.java)

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
    }
}
