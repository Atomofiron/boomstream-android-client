package ru.atomofiron.boomstream.activities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import com.jakewharton.rxbinding2.widget.RxTextView

import ru.atomofiron.boomstream.R

import kotlinx.android.synthetic.main.activity_apikey.*
import ru.atomofiron.boomstream.I
import java.util.regex.Pattern

class ApikeyActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_apikey)

        val sp = I.SP(this)

        btnLoad.setOnClickListener {
            sp.edit().putString(I.PREF_SELECTED_PROJECT, etApikey.text.toString()).apply()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        RxTextView.textChanges(etApikey)
                .map { text -> text.trim() }
                .subscribe { text -> btnLoad.isEnabled = text.length == 32 }

        etApikey.setText(sp.getString(I.PREF_SELECTED_PROJECT, ""))
    }
}
