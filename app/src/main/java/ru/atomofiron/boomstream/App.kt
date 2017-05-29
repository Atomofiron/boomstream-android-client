package ru.atomofiron.boomstream

import android.app.Application

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.atomofiron.boomstream.models.retrofit.Api

class App : Application() {

    companion object {
        lateinit var api: Api
            private set

        var apikey: String? = null
    }

    override fun onCreate() {
        super.onCreate()

        val retrofit = Retrofit.Builder()
                .baseUrl("https://boomstream.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        api = retrofit.create(Api::class.java)
    }
}
