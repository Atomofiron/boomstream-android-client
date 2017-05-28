package ru.atomofiron.boomstream.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import kotlinx.android.synthetic.main.item.view.*
import ru.atomofiron.boomstream.models.Node
import ru.atomofiron.boomstream.R

import java.util.*

class NotesAdapter() :
        RecyclerView.Adapter<NotesAdapter.ViewHolder>() {

    private var search: Boolean = false
    private var nodes: ArrayList<Node> = ArrayList()
    private var nodesSearch: ArrayList<Node> = ArrayList()

    constructor(nodes: ArrayList<Node>) : this() {
        this.nodes.addAll(nodes)
    }

    override fun onBindViewHolder(holder: NotesAdapter.ViewHolder?, position: Int) {
        val note = get(holder!!.adapterPosition)
        holder.title.text = note.title
    }

    fun setData(nodes: ArrayList<Node>) {
        this.nodes.clear()
        this.nodes.addAll(nodes)
        notifyDataSetChanged()
    }

    fun clearData() {
        nodes.clear()
        notifyDataSetChanged()
    }

    fun add(node: Node) {
        nodes.add(node)
        notifyDataSetChanged()
    }
    fun remove(position: Int) {
        nodes.removeAt(position)
        notifyDataSetChanged()
    }


    fun get(n: Int) : Node = if (search) nodesSearch[n] else nodes[n]

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : NotesAdapter.ViewHolder =
            ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item, parent, false) as LinearLayout)

	override fun getItemCount() : Int = if (search) nodesSearch.size else nodes.size

    fun setQuery(query: String) {
        search = !query.isEmpty()

        if (search) {
            nodesSearch.clear()
            nodes.filter { it.contains(query, true) }
                    .forEach { nodesSearch.add(it) }
        }

        notifyDataSetChanged()
    }

	class ViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
		var ll: LinearLayout = itemView!!.layout_item
        var title: TextView = ll.title
    }

}
