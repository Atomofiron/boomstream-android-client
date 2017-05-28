package ru.atomofiron.boomstream

import android.support.design.widget.Snackbar
import android.util.Log
import android.view.View

class I {
    companion object {
        fun Log(log: String) = Log.e("atomofiron", log)
    }
}

fun View.snack(text: String) =
        Snackbar.make(this, text, Snackbar.LENGTH_LONG).show()

fun View.snack(text: String, action: String, callback: () -> Unit) =
    Snackbar.make(this, text, Snackbar.LENGTH_LONG).setAction(action, { callback() }).show()

fun View.snack(textId: Int, duration: Int, actionId: Int, callback: () -> Unit) =
    Snackbar.make(this, textId, duration).setAction(actionId, { callback() }).show()
