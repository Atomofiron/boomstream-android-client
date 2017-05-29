package ru.atomofiron.boomstream.mvp.models

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.atomofiron.boomstream.App
import ru.atomofiron.boomstream.I
import ru.atomofiron.boomstream.models.Node
import ru.atomofiron.boomstream.models.retrofit.folder.Folder
import ru.atomofiron.boomstream.models.retrofit.folder.Media
import ru.atomofiron.boomstream.mvp.presenters.MainPresenter

class MainModel(val presenter: MainPresenter) {

    fun loadNodes(folderCode: String) {
        App.api.getFolder(App.apikey!!, "json", 1024, 0, folderCode).enqueue(object : Callback<Folder> {
            override fun onResponse(call: Call<Folder>, response: Response<Folder>) =
                    collectNodes(response)

            override fun onFailure(call: Call<Folder>, t: Throwable) {
                I.Log("onFailure(): "+t.message)
                presenter.onFailure(t.message ?: "null message")
            }
        })
    }

    fun loadNodes() {
        App.api.getRoot(App.apikey!!, "json", 1024, 0).enqueue(object : Callback<Folder> {
            override fun onResponse(call: Call<Folder>, response: Response<Folder>) {
                collectNodes(response)
            }

            override fun onFailure(call: Call<Folder>, t: Throwable) {
                I.Log("onFailure(): "+t.message)
                presenter.onFailure(t.message ?: "null message")
            }
        })
    }

    private fun collectNodes(response: Response<Folder>) {
        val list = ArrayList<Node>()
        val folder = response.body()

        list += folder?.folders ?: list

        if (folder != null)
            for (media in folder.medias) {
                list += media
            }

        presenter.onNodesLoaded(list)
        I.Log("list: "+list.size)
    }
}
