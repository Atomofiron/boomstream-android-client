package ru.atomofiron.boomstream.mvp.presenters

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import ru.atomofiron.boomstream.App
import ru.atomofiron.boomstream.I
import ru.atomofiron.boomstream.models.Node
import ru.atomofiron.boomstream.mvp.models.FolderModel
import ru.atomofiron.boomstream.mvp.views.FolderView
import kotlin.collections.ArrayList

@InjectViewState()
class FolderPresenter: MvpPresenter<FolderView>() {

    private var mNodesList: ArrayList<Node> = ArrayList()
    private var apiKey = ""

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        I.Log("onFirstViewAttach()")
    }

    fun loadNodesIfNecessary(): Boolean {
        if (apiKey != App.apikey) {
            apiKey = App.apikey
            viewState.onNodesReloading()
            FolderModel.loadNodes({ list -> onNodesLoaded(list) }, { message -> viewState.onNodesLoadFail(message) })
            return true
        }
        return false
    }

    private fun onNodesLoaded(nodes: ArrayList<Node>) {
        mNodesList = nodes
        viewState.onNodesLoaded(mNodesList)
    }

    fun onOpenFolder(code: String) {
        viewState.onOpenFolder(code)
    }

    fun onReloadNodes() {
        FolderModel.loadNodes({ list -> onNodesLoaded(list) }, { message -> viewState.onNodesLoadFail(message) })
    }
}
