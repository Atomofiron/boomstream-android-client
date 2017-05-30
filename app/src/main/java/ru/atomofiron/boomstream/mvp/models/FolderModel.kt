package ru.atomofiron.boomstream.mvp.models

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.atomofiron.boomstream.App
import ru.atomofiron.boomstream.I
import ru.atomofiron.boomstream.models.Node
import ru.atomofiron.boomstream.models.retrofit.folder.Folder
import ru.atomofiron.boomstream.models.retrofit.folder.Media
import android.os.AsyncTask
import ru.atomofiron.boomstream.models.retrofit.folder.Subfolder
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream


object FolderModel {

    fun loadNodes(folderCode: String, collback: (nodes: ArrayList<Node>) -> Unit) {
        App.api.getFolder(App.apikey!!, "json", 1024, 0, folderCode).enqueue(object : Callback<Folder> {
            override fun onResponse(call: Call<Folder>, response: Response<Folder>) =
                    collback(collectNodes(response))

            override fun onFailure(call: Call<Folder>, t: Throwable) {
                I.Log("getFolder(): "+t.message)
            }
        })
    }

    fun loadNodes(callback: (nodes: ArrayList<Node>) -> Unit) {
        object : AsyncTask<Int, ArrayList<Node>, Unit>() {
            override fun onProgressUpdate(vararg values: ArrayList<Node>) {
                super.onProgressUpdate(*values)
                callback(values[0])
            }
            override fun doInBackground(vararg params: Int?): Unit {
                val list = loadChildren("")

                publishProgress(list)
                list.filter { it is Media }
                        .forEach { loadImageIfNoExists(it as Media) }
            }
        }.execute()
    }

    private fun loadChildren(code: String): ArrayList<Node> {
        val response = App.api.getFolder(App.apikey!!, "json", 1024, 0, code).execute()

        if (!response.isSuccessful)
            return ArrayList()

        val folder = collectNodes(response)
        val fullList = ArrayList<Node>(folder)

        folder.forEach {
            it.parentCode = code

            if (it is Subfolder)
                fullList += loadChildren(it.code)
        }
        return fullList
    }

    private fun collectNodes(response: Response<Folder>): ArrayList<Node> {
        val list = ArrayList<Node>()
        val folder = response.body()

        if (folder != null) {
            list += folder.folders
            list += folder.medias
        }

        return list
    }

    private fun loadImageIfNoExists(media: Media) {
        if (!File(App.cachePath + File.separator + media.poster.code + ".tmp").isFile &&
                !File(App.cachePath + File.separator + media.poster.code).isFile) {
            val response = App.api.loadData(media.poster.url).execute()
            if (response.isSuccessful)
                loadImage(response.body(), media.poster.code)
        }
    }

    private fun loadImage(body: ResponseBody?, code: String): Boolean {
        val path = App.cachePath + File.separator + code
        val file = File(path)
        val tmpFile = File(path + ".tmp")

        if (file.exists() && !file.delete() || tmpFile.exists() && !tmpFile.delete())
            return false

        var inp: InputStream? = null
        var out: FileOutputStream? = null
        try {
            inp = body!!.byteStream()
            out = FileOutputStream(tmpFile.absoluteFile)

            val bytes = ByteArray(1024)
            var c = inp.read(bytes)
            while (c != -1) {
                out.write(bytes, 0, c)
                c = inp.read(bytes)
            }

            return tmpFile.renameTo(file)
        } catch (e: Exception) {
            I.Log(e.toString())
            return false
        } finally {
            inp?.close()
            out?.close()
        }
    }

}