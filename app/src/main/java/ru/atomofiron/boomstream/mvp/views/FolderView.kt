package ru.atomofiron.boomstream.mvp.views

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import ru.atomofiron.boomstream.models.Node
import ru.atomofiron.boomstream.mvp.AddToEndSingleStrategyButNotifs

@StateStrategyType(value = AddToEndSingleStrategyButNotifs::class)
interface FolderView : MvpView {

    fun onNodesReloading()

    fun onNodesLoaded(nodes: List<Node>)

    fun onOpenFolder(code: String)

    fun onNodesLoadFail(message: String)
}