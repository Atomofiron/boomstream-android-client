package ru.atomofiron.boomstream.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import com.arellomobile.mvp.MvpAppCompatFragment
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.PresenterType
import kotlinx.android.synthetic.main.fragment_folder.*
import ru.atomofiron.boomstream.models.Node

import ru.atomofiron.boomstream.R
import ru.atomofiron.boomstream.activities.MainActivity
import ru.atomofiron.boomstream.adapters.NodesAdapter
import ru.atomofiron.boomstream.mvp.presenters.FolderPresenter
import ru.atomofiron.boomstream.mvp.views.FolderView
import ru.atomofiron.boomstream.snack
import android.provider.MediaStore
import android.support.v7.widget.SearchView
import com.jakewharton.rxbinding2.support.v7.widget.RxSearchView
import kotlinx.android.synthetic.main.fragment_folder.view.*
import ru.atomofiron.boomstream.I


class FolderFragment : MvpAppCompatFragment(), FolderView, MainActivity.OnBackPressedListener, NodesAdapter.OnFolderClickListener {

    private var mainView: View? = null
    private lateinit var searchItem: MenuItem

    companion object {
        const val TAG = "FolderFragment"
        fun getIntent(context: Context): Intent = Intent(context, FolderFragment::class.java)
    }

    @InjectPresenter(type=PresenterType.GLOBAL)
    lateinit var presenter: FolderPresenter
    private lateinit var listAdapter: NodesAdapter

    // Native //

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)

        val view = inflater!!.inflate(R.layout.fragment_folder, container, false)

        view.swipeLayout.setOnRefreshListener {
            presenter.onReloadNodes()
        }

        val fab = view.fab
        // у com.github.clans.fab.FloatingActionButton нет возможности указать цвет в xml
        fab.menu_item_pick.setColorNormalResId(R.color.colorAccent)
        fab.menu_item_pick.setColorPressedResId(R.color.colorAccent)
        fab.menu_item_record.setColorNormalResId(R.color.colorAccent)
        fab.menu_item_record.setColorPressedResId(R.color.colorAccent)
        // у com.github.clans.fab.FloatingActionButton нет возможности указать векторное изображение
        // при vectorDrawables.useSupportLibrary = true
        fab.menu_item_pick.setImageResource(R.drawable.ic_video_library)
        fab.menu_item_record.setImageResource(R.drawable.ic_shutter)
        fab.menu_item_pick.setOnClickListener {
            fab.close(true)

            requestPickVideo()
        }
        fab.menu_item_record.setOnClickListener {
            fab.close(true)

            requestRecordVideo()
        }

        listAdapter = NodesAdapter(LayoutInflater.from(activity), activity.resources)
        listAdapter.onFolderClickListener = this
        listAdapter.onMediaClickListener = activity as MainActivity

        val rvNotesList = view.rvNotesList
        rvNotesList.layoutManager = LinearLayoutManager(activity) as RecyclerView.LayoutManager
        rvNotesList.adapter = listAdapter

        mainView = view
        return view
    }

    private fun requestRecordVideo() {
        val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
        if (intent.resolveActivity(activity.packageManager) != null)
            activity.startActivityForResult(intent, I.ACTION_VIDEO_CAPTURE)
        else
            fab.snack(R.string.no_apps)
    }

    private fun requestPickVideo() {
        // при ACTION_PICK удобнее выбирать видео
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "video/*"

        if (intent.resolveActivity(activity.packageManager) == null)
            intent.action = Intent.ACTION_GET_CONTENT

        if (intent.resolveActivity(activity.packageManager) != null)
            activity.startActivityForResult(Intent.createChooser(intent, getString(R.string.pick_video)), I.ACTION_VIDEO_PICK)
        else
            fab.snack(R.string.no_apps)
    }

    override fun onStart() {
        super.onStart()
        (activity as MainActivity).onBackPressedListener = this

        // если здесь упало, Clean project, Run
        presenter.loadNodesIfNecessary()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.folder, menu)

        searchItem = menu.findItem(R.id.search)

        val searchView = searchItem.actionView as SearchView
        searchView.setOnQueryTextFocusChangeListener { v, hasFocus -> if (!hasFocus) searchItem.collapseActionView() }

        RxSearchView.queryTextChanges(menu.findItem(R.id.search).actionView as SearchView)
                .map { text -> text.trim() }
                .filter { isResumed }
                .subscribe { text -> search(text.toString()) }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean = super.onOptionsItemSelected(item)

    override fun onBackPressed(): Boolean = listAdapter.goUp()

    // Custom //

    private fun collapseSearch() {
        searchItem.collapseActionView()

        listAdapter.setQuery("")
    }

    fun updateView() {
        tvEmpty.visibility = if (listAdapter.itemCount == 0) View.VISIBLE else View.GONE
    }

    fun search(query: String) {
        listAdapter.setQuery(query)

        updateView()
    }

    override fun onFolderClick(code: String) {
        listAdapter.setQuery("")
        presenter.onOpenFolder(code)
    }

    // MainView implementation //

    override fun onNodesLoaded(nodes: List<Node>) {
        listAdapter.setData(nodes as ArrayList<Node>)
        swipeLayout.isRefreshing = false

        updateView()
    }

    override fun onNodesReloading() {
        listAdapter.clearData()
        swipeLayout.isRefreshing = true

        updateView()
    }

    override fun onNodesLoadFail(message: String) {
        swipeLayout.isRefreshing = false

        try {
            fab.snack(getString(message.toInt()))
        } catch (e: Exception) {
            fab.snack(message)
        }
    }

    override fun onOpenFolder(code: String) {
        listAdapter.openFolder(code)

        collapseSearch()
        updateView()
    }

}
