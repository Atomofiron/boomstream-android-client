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

class MainFragment : MvpAppCompatFragment(), FolderView, MainActivity.OnBackPressedListener {

    private var c: Int = 0

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

        val view = inflater!!.inflate(R.layout.fragment_main, container, false)

        val etSearch = view.findViewById(R.id.etSearch) as EditText
        val fab = view.findViewById(R.id.fab) as FloatingActionButton
        val rvNotesList = view.findViewById(R.id.rvNotesList) as RecyclerView

        fab.setOnClickListener {
            fab.snack("Karr $c", "+1", { tvEmpty.text = (++c).toString() })
        }

        RxTextView.textChanges(etSearch)
                .map { text -> text.trim() }
                .subscribe { text -> if (isResumed) presenter.search(text.toString()) }

        (activity as MainActivity).onBackPressedListener = this

        listAdapter = NotesAdapter(LayoutInflater.from(context), context.resources)

        rvNotesList.layoutManager = LinearLayoutManager(context) as RecyclerView.LayoutManager
        rvNotesList.adapter = listAdapter

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
        // todo up by folder
        return false
    }

    // Custom //

    private fun switchSearch() {
        val show = etSearch.visibility != View.VISIBLE
        etSearch.visibility = if (show) View.VISIBLE else View.GONE

        if (!show) // можно ещё сократить, но тогда будет висеть куча запросов в ViewState, но это не точно
            presenter.search("")
        else if (!etSearch.text.isEmpty())
            presenter.search(etSearch.text.toString())
        // прустой query = отключенный поиск
        // не очищаю etSearch, чтобы введённый текст мог быть использован пользователем снова
    }

    fun updateView() {
        tvEmpty.visibility = if (listAdapter.itemCount == 0) View.VISIBLE else View.GONE
    }

    // MainView implementation //

    override fun onNodesLoaded(nodes: List<Node>) {
        listAdapter.setData(nodes as ArrayList<Node>)

        updateView()
    }

    override fun onSearch(query: String) {
        etSearch.visibility = if (query.isEmpty()) View.GONE else View.VISIBLE
        listAdapter.setQuery(query)

        updateView()
    }

    override fun onNodeDeleted(position: Int) {
        listAdapter.remove(position)

        updateView()
    }

    override fun onNodeAdded(node: Node) {
        listAdapter.add(node)

        updateView()
    }

    override fun updateList(nodes: List<Node>) {
        listAdapter.setData(nodes as ArrayList<Node>)

        updateView()
    }

    override fun onFailure(message: String) {
        fab.snack(message)
    }
}
