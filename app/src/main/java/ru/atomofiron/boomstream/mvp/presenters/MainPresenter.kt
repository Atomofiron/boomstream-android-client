package ru.atomofiron.boomstream.mvp.presenters

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import ru.atomofiron.boomstream.I
import ru.atomofiron.boomstream.models.Node
import ru.atomofiron.boomstream.mvp.views.MainView
import java.util.*

@InjectViewState
class MainPresenter: MvpPresenter<MainView>() {

    val mNodesList: ArrayList<Node> = ArrayList<Node>()

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        I.Log("onFirstViewAttach()")

        createTestNodes()
    }

    fun deleteNodeByPosition(position: Int) {
        mNodesList.removeAt(position)
        viewState.onNodeDeleted(position)
    }

    fun search(query: String) {
        viewState.onSearch(query)
    }

    fun addNode(node: Node) {
        mNodesList.add(node)

        viewState.onNodeAdded(node)
    }

    fun createTestNodes() {
        val date = Date()
        (0..50).mapTo(mNodesList) { Node("Node $it", "Text $it", date) }

        viewState.onNodesLoaded(mNodesList)
    }

}
