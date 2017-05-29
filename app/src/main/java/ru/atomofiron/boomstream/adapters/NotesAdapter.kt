package ru.atomofiron.boomstream.adapters

import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import kotlinx.android.synthetic.main.item.view.*
import ru.atomofiron.boomstream.I
import ru.atomofiron.boomstream.models.Node
import ru.atomofiron.boomstream.R
import ru.atomofiron.boomstream.models.retrofit.folder.Folder
import ru.atomofiron.boomstream.models.retrofit.folder.Media
import ru.atomofiron.boomstream.models.retrofit.folder.Subfolder

import java.util.*

class NotesAdapter() : RecyclerView.Adapter<NotesAdapter.ViewHolder>() {

    private lateinit var res: Resources
    private var search: Boolean = false
    private val nodes: ArrayList<Node> = ArrayList()
    private var nodesSearch: ArrayList<Node> = ArrayList()
    private lateinit var recyclerView: RecyclerView

    constructor(resources: Resources) : this() {
        res = resources
        this.nodes.addAll(nodes)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView?) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView ?: this.recyclerView
    }

    override fun onBindViewHolder(holder: NotesAdapter.ViewHolder?, position: Int) {
        val node = get(holder!!.adapterPosition)

        if (node is Subfolder) {
            holder.resolutions.visibility = View.GONE
            holder.button.visibility = View.GONE
            holder.progress.visibility = View.GONE

            holder.title.text = node.title
            holder.image.setImageDrawable(res.getDrawable(
                    if (node.fileCount == "0") R.drawable.ic_folder_empty else R.drawable.ic_folder))
        } else if (node is Media) {
            holder.resolutions.visibility = View.VISIBLE
            holder.button.visibility = View.VISIBLE
            holder.progress.visibility = View.VISIBLE

            holder.title.text = node.title
            holder.image.setImageDrawable(res.getDrawable(R.drawable.ic_folder))
        } else
            I.Log("WWWWWWWWWWWTTTTTTTTTTTTTTTTFFFFFFFFFFFFFFFF")
    }

    fun setImage(image: Drawable, position: Int) =
        (recyclerView.findViewHolderForAdapterPosition(position) as ViewHolder).image.setImageDrawable(image)

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
            nodes.filter { it.contains(query) }
                    .forEach { nodesSearch.add(it) }
        }

        notifyDataSetChanged()
    }

	class ViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
		var ll: LinearLayout = itemView!!.layout_item
        var image = ll.image
        var title = ll.title
        var resolutions = ll.resolutions
        var button = ll.button
        var progress = ll.progress
    }

}
