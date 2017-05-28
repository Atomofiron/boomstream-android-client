package ru.atomofiron.boomstream.mvp.views

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import ru.atomofiron.boomstream.models.Node

@StateStrategyType(value = AddToEndSingleStrategy::class)
interface MainView : MvpView {

    fun onNodesLoaded(nodes: List<Node>)

    fun onSearch(query: String)

    fun onNodeDeleted(position: Int)

    fun onNodeAdded(node: Node)

    fun updateList(nodes: List<Node>)
}