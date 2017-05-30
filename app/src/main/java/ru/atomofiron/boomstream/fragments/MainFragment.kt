package ru.atomofiron.boomstream.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.EditText
import com.arellomobile.mvp.MvpAppCompatFragment
import com.arellomobile.mvp.presenter.InjectPresenter
import com.jakewharton.rxbinding2.widget.RxTextView
import kotlinx.android.synthetic.main.fragment_main.*
import ru.atomofiron.boomstream.models.Node

import ru.atomofiron.boomstream.R
import ru.atomofiron.boomstream.activities.MainActivity
import ru.atomofiron.boomstream.adapters.NotesAdapter
import ru.atomofiron.boomstream.mvp.presenters.FolderPresenter
import ru.atomofiron.boomstream.mvp.views.FolderView
import ru.atomofiron.boomstream.snack

class MainFragment : MvpAppCompatFragment(), FolderView, MainActivity.OnBackPressedListener, NotesAdapter.OnItemClickListener {

    private var c: Int = 0
    private var mainView: View? = null

    companion object {
        const val TAG = "MainFragment"
        fun getIntent(context: Context): Intent = Intent(context, MainFragment::class.java)
    }

    @InjectPresenter
    lateinit var presenter: FolderPresenter
    private lateinit var listAdapter: NotesAdapter

    // Native //

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        retainInstance = true

        (activity as MainActivity).onBackPressedListener = this

        if (mainView != null) {
            (mainView!!.parent as ViewGroup).removeView(mainView)
            return mainView
        }

        val view = inflater!!.inflate(R.layout.fragment_main, container, false)

        val etSearch = view.findViewById(R.id.etSearch) as EditText
        val fab = view.findViewById(R.id.fab) as FloatingActionButton
        val rvNotesList = view.findViewById(R.id.rvNotesList) as RecyclerView

        fab.setOnClickListener {
            fab.snack("Karr $c", "+1", { tvEmpty.text = (++c).toString() })
        }

        RxTextView.textChanges(etSearch)
                .map { text -> text.trim() }
                .filter { isResumed }
                .subscribe { text -> search(text.toString()) }

        listAdapter = NotesAdapter(LayoutInflater.from(context), context.resources)
        listAdapter.onItemClickListener = this

        rvNotesList.layoutManager = LinearLayoutManager(context) as RecyclerView.LayoutManager
        rvNotesList.adapter = listAdapter

        mainView = view
        return view
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
        return listAdapter.popStack()
    }

    // Custom //

    private fun switchSearch() {
        val show = etSearch.visibility != View.VISIBLE
        etSearch.visibility = if (etSearch.visibility != View.VISIBLE) View.VISIBLE else View.GONE

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

    override fun onVideoClick(position: Int) {
    }

    override fun onCloseSearch() {
        switchSearch()
    }

    // MainView implementation //

    override fun onNodesLoaded(nodes: List<Node>) {
        listAdapter.setData(nodes as ArrayList<Node>)

        updateView()
    }

    override fun onUpdateNodes(nodes: List<Node>) {
        listAdapter.setData(nodes as ArrayList<Node>)

        updateView()
    }

    override fun onFailure(message: String) {
        fab.snack(message)
    }
}
