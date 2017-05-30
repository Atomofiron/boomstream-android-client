package ru.atomofiron.boomstream.mvp.presenters

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import ru.atomofiron.boomstream.I
import ru.atomofiron.boomstream.models.Node
import ru.atomofiron.boomstream.mvp.models.FolderModel
import ru.atomofiron.boomstream.mvp.views.FolderView
import kotlin.collections.ArrayList

@InjectViewState
class FolderPresenter: MvpPresenter<FolderView>() {

    var mNodesList: ArrayList<Node> = ArrayList()

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        I.Log("onFirstViewAttach()")

        FolderModel.loadNodes({ list -> onNodesLoaded(list) }, { message -> onLoadFail(message) })
    }

    private fun onNodesLoaded(nodes: ArrayList<Node>) {
        mNodesList = nodes
        viewState.onNodesLoaded(mNodesList)
    }

    fun onReloadNodes() {
        FolderModel.loadNodes({ list -> onNodesLoaded(list) }, { message -> onLoadFail(message) })
    }

    private fun onLoadFail(message: Int) {
        viewState.onLoadFail(message)
    }
}
