package ru.atomofiron.boomstream

import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.util.Log

class I {
    companion object {
        fun Log(log: String) = Log.e("atomofiron", log)
    }
}

fun FloatingActionButton.snack(text: String) =
        Snackbar.make(this, text, Snackbar.LENGTH_LONG).show()

fun FloatingActionButton.snack(text: String, action: String, callback: () -> Unit) =
    Snackbar.make(this, text, Snackbar.LENGTH_LONG).setAction(action, { callback() }).show()
