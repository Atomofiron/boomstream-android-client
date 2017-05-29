package ru.atomofiron.boomstream.activities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.jakewharton.rxbinding2.widget.RxTextView

import ru.atomofiron.boomstream.R

import kotlinx.android.synthetic.main.activity_apikey.*
import ru.atomofiron.boomstream.App
import ru.atomofiron.boomstream.I

class ApikeyActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_apikey)

        val sp = I.SP(this)

        btnLoad.setOnClickListener {
            val key = etApikey.text.toString()
            App.apikey = key
            sp.edit().putString(I.PREF_API_KEY, key).apply()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        RxTextView.textChanges(etApikey)
                .map { text -> text.trim() }
                .subscribe { text -> btnLoad.isEnabled = text.length == 32 }

        etApikey.setText(sp.getString(I.PREF_API_KEY, ""))
    }
}
