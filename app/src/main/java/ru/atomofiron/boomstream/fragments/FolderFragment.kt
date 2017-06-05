package ru.atomofiron.boomstream.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.EditText
import com.arellomobile.mvp.MvpAppCompatFragment
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.PresenterType
import com.jakewharton.rxbinding2.widget.RxTextView
import kotlinx.android.synthetic.main.fragment_folder.*
import ru.atomofiron.boomstream.models.Node

import ru.atomofiron.boomstream.R
import ru.atomofiron.boomstream.activities.MainActivity
import ru.atomofiron.boomstream.adapters.NotesAdapter
import ru.atomofiron.boomstream.mvp.presenters.FolderPresenter
import ru.atomofiron.boomstream.mvp.views.FolderView
import ru.atomofiron.boomstream.snack

class FolderFragment : MvpAppCompatFragment(), FolderView, MainActivity.OnBackPressedListener, NotesAdapter.OnFolderClickListener {

    private var c: Int = 0
    private var mainView: View? = null

    companion object {
        const val TAG = "FolderFragment"
        fun getIntent(context: Context): Intent = Intent(context, FolderFragment::class.java)
    }

    @InjectPresenter(type=PresenterType.GLOBAL)
    lateinit var presenter: FolderPresenter
    private lateinit var listAdapter: NotesAdapter

    // Native //

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)

        val view = inflater!!.inflate(R.layout.fragment_folder, container, false)

        val etSearch = view.findViewById(R.id.etSearch) as EditText
        val fab = view.findViewById(R.id.fab) as FloatingActionButton
        val rvNotesList = view.findViewById(R.id.rvNotesList) as RecyclerView
        val swipeLayout = view.findViewById(R.id.swipeLayout) as SwipeRefreshLayout
        swipeLayout.setOnRefreshListener {
            presenter.onReloadNodes()
        }

        fab.setOnClickListener {
            fab.snack("Karr $c", "+1", { tvEmpty.text = (++c).toString() })
        }

        RxTextView.textChanges(etSearch)
                .map { text -> text.trim() }
                .filter { text -> isResumed && text.isNotEmpty() }
                .subscribe { text -> search(text.toString()) }

        listAdapter = NotesAdapter(LayoutInflater.from(activity), activity.resources)
        listAdapter.onFolderClickListener = this
        listAdapter.onMediaClickListener = activity as MainActivity

        rvNotesList.layoutManager = LinearLayoutManager(activity) as RecyclerView.LayoutManager
        rvNotesList.adapter = listAdapter

        mainView = view
        return view
    }

    override fun onStart() {
        super.onStart()
        (activity as MainActivity).onBackPressedListener = this
        presenter.loadNodesIfNecessary()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.main, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.search) {
            switchSearch()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed(): Boolean {
        return listAdapter.goUp()
    }

    // Custom //

    private fun switchSearch() {
        showSearch(etSearch.visibility != View.VISIBLE)
    }

    private fun showSearch(show: Boolean) {
        etSearch.visibility = if (show) View.VISIBLE else View.GONE

        listAdapter.setQuery(if (show) etSearch.text.toString() else "")
    }

    fun updateView() {
        tvEmpty.visibility = if (listAdapter.itemCount == 0) View.VISIBLE else View.GONE
    }

    fun search(query: String) {
        etSearch.visibility = if (query.isEmpty()) View.GONE else View.VISIBLE
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

        showSearch(false)
    }

}
