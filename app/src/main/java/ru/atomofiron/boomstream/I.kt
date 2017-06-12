package ru.atomofiron.boomstream

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.preference.PreferenceManager
import android.support.design.widget.Snackbar
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import java.io.File
import java.io.FileOutputStream

class I {
    companion object {
        val PREF_API_KEY = "PREF_API_KEY"
        val PREF_CACHE_SIZE_MB = "pref_cache_size_mb"
        val PREF_CACHE_COUNT = "pref_cache_count"
        val PREF_FTP_LOGIN = "pref_ftp_login"
        val ACTION_VIDEO_CAPTURE = 101
        val ACTION_VIDEO_PICK = 102

        fun Log(log: String) = Log.e("atomofiron", log)
        fun SP(co: Context) : SharedPreferences = PreferenceManager.getDefaultSharedPreferences(co)

        /**
         * Показывает окно с полем ввода, пока пользователь не введёт хоть что-то.
         */
        fun showTextRequest(co: Context, dialogTitle: Int, defaultText: String, callback: (input: String) -> Unit) {
            val et = EditText(co)
            et.setText(defaultText)
            AlertDialog.Builder(co)
                    .setTitle(dialogTitle)
                    .setView(et)
                    .setNegativeButton(R.string.cancel, null)
                    .setPositiveButton(R.string.ok, { _, _ ->
                        if (et.length() > 0)
                            callback(et.text.toString())
                        else
                            // в данном случае рекурсия с использованием анонимного класса не переполняет память,
                            // проверено в Android Monitor
                            showTextRequest(co, dialogTitle, defaultText, callback)
                    }).create().show()
        }
    }
}

/**
 * Капирует файл по Uri в папку с кэшем.
 *
 * @return File, если скопировалось успешно, и null, если нет.
 */
fun Uri.getTempFile(co: Context): File? {
    val input = co.contentResolver.openInputStream(this)
    val tmpPath = co.externalCacheDir.absolutePath + File.separator + System.currentTimeMillis()
    var length: Long

    try {
        val output = FileOutputStream(tmpPath)
        length = input.copyTo(output, 1024)
        input.close()
        output.close()
    } catch (e: Exception) {
        I.Log(e.toString())
        length = -1L
    }

    return if (length == -1L) null else File(tmpPath)
}

fun View.snack(text: String) =
        Snackbar.make(this, text, Snackbar.LENGTH_LONG).show()

fun View.snack(stringId: Int) =
        Snackbar.make(this, stringId, Snackbar.LENGTH_SHORT).show()

fun View.snack(text: String, action: String, callback: () -> Unit) =
    Snackbar.make(this, text, Snackbar.LENGTH_LONG).setAction(action, { callback() }).show()

fun View.snack(textId: Int, duration: Int, actionId: Int, callback: () -> Unit) =
    Snackbar.make(this, textId, duration).setAction(actionId, { callback() }).show()

fun Context.toast(text: String) =
        Toast.makeText(this, text, Toast.LENGTH_LONG).show()
