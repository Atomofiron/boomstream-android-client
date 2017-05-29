package ru.atomofiron.boomstream.mvp.presenters

import android.graphics.drawable.Drawable
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.atomofiron.boomstream.App
import ru.atomofiron.boomstream.I
import ru.atomofiron.boomstream.models.Node
import ru.atomofiron.boomstream.models.retrofit.folder.Folder
import ru.atomofiron.boomstream.mvp.models.MainModel
import ru.atomofiron.boomstream.mvp.views.MainView
import java.util.*
import kotlin.collections.ArrayList

@InjectViewState
class MainPresenter: MvpPresenter<MainView>() {

    val mainModel: MainModel = MainModel(this)
    var mNodesList: ArrayList<Node> = ArrayList()

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        I.Log("onFirstViewAttach()")

        mainModel.loadNodes()
    }

    fun deleteNodeByPosition(position: Int) {
        mNodesList.removeAt(position)
        viewState.onNodeDeleted(position)
    }

    fun search(query: String) {
        viewState.onSearch(query)
    }

    fun onFailure(message: String) {
        viewState.onFailure(message)
    }

    fun onNodesLoaded(nodes: ArrayList<Node>) {
        mNodesList = nodes
        viewState.onNodesLoaded(mNodesList)
    }

    fun onImageLoaded(image: Drawable, pos: Int) {
        viewState.onImageLoaded(image, pos)
    }
}
