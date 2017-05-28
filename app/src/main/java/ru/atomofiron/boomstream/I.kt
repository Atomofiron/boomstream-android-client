package ru.atomofiron.boomstream

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.support.design.widget.Snackbar
import android.util.Log
import android.view.View

class I {
    companion object {
        val PREF_PROJECTS = "PREF_PROJECTS"
        val PREF_SELECTED_PROJECT = "PREF_SELECTED_PROJECT"

        fun Log(log: String) = Log.e("atomofiron", log)
        fun SP(co: Context) : SharedPreferences = PreferenceManager.getDefaultSharedPreferences(co)
    }
}

fun View.snack(text: String) =
        Snackbar.make(this, text, Snackbar.LENGTH_LONG).show()

fun View.snack(text: String, action: String, callback: () -> Unit) =
    Snackbar.make(this, text, Snackbar.LENGTH_LONG).setAction(action, { callback() }).show()

fun View.snack(textId: Int, duration: Int, actionId: Int, callback: () -> Unit) =
    Snackbar.make(this, textId, duration).setAction(actionId, { callback() }).show()
