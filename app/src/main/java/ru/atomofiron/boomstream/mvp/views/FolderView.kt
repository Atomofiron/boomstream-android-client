package ru.atomofiron.boomstream.mvp.views

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import ru.atomofiron.boomstream.models.Node

@StateStrategyType(value = AddToEndSingleStrategy::class)
interface FolderView : MvpView {

    fun onNodesLoaded(nodes: List<Node>)

    fun onNodeDeleted(position: Int)

    fun onNodeAdded(node: Node)

    fun updateList(nodes: List<Node>)

    fun onFailure(message: String)
}