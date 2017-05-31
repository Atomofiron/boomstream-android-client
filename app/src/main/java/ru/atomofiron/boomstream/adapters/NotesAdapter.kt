package ru.atomofiron.boomstream.adapters

import android.content.res.Resources
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.AsyncTask
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import kotlinx.android.synthetic.main.item.view.*
import ru.atomofiron.boomstream.App
import ru.atomofiron.boomstream.I
import ru.atomofiron.boomstream.models.Node
import ru.atomofiron.boomstream.R
import ru.atomofiron.boomstream.models.retrofit.folder.Media
import ru.atomofiron.boomstream.models.retrofit.folder.Subfolder
import java.io.File
import java.util.HashMap

import kotlin.collections.ArrayList

class NotesAdapter() : RecyclerView.Adapter<NotesAdapter.ViewHolder>() {

    private lateinit var inflater: LayoutInflater
    private lateinit var res: Resources
    var search: Boolean = false
    private val nodes: ArrayList<Node> = ArrayList()
    private val dirNodes: ArrayList<Node> = ArrayList()
    private var nodesSearch: ArrayList<Node> = ArrayList()
    private lateinit var recyclerView: RecyclerView
    private val drawables = HashMap<String, Drawable>()
    var onFolderClickListener: OnFolderClickListener? = null
        set
    var onMediaClickListener: OnMediaClickListener? = null
        set
    private var curDir = ""

    constructor(inflater: LayoutInflater, resources: Resources) : this() {
        this.inflater = inflater
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
            holder.posterCode = ""
        } else if (node is Media) {
            holder.resolutions.visibility = View.VISIBLE
            holder.button.visibility = View.VISIBLE
            holder.progress.visibility = View.GONE

            holder.title.text = node.title
            holder.posterCode = node.poster.code
            val dr = drawables[holder.posterCode]
            if (dr == null)
                ImageSetter().execute(holder)
            else
                holder.image.setImageDrawable(dr)

            holder.resolutions.removeAllViews()
            for (tc in node.transcodes) {
                val tv = inflater.inflate(R.layout.textview_rez, holder.resolutions, false) as TextView
                tv.text = tc.title
                holder.resolutions.addView(tv)
            }
        }
    }

    fun setData(nodes: ArrayList<Node>) {
        this.nodes.clear()
        this.nodes.addAll(nodes)
        updateDirNodes()
        notifyDataSetChanged()
    }

    fun clearData() {
        curDir = ""
        nodes.clear()
        updateDirNodes()
        notifyDataSetChanged()
    }

    fun add(node: Node) {
        nodes.add(node)
        updateDirNodes()
        notifyDataSetChanged()
    }

    fun remove(position: Int) {
        nodes.removeAt(position)
        updateDirNodes()
        notifyDataSetChanged()
    }

    fun goUp(): Boolean {
        if (curDir == "")
            return false

        var code = "" // на случай если папка была вдруг удалена
        nodes.filter { it is Subfolder && it.code == curDir  && code.isEmpty() }
                .forEach { code = it.parentCode }

        onFolderClickListener?.onFolderClick(code)
        return true
    }

    fun openFolder(code: String) {
        curDir = code
        updateDirNodes()
        notifyDataSetChanged()
    }

    private fun updateDirNodes() {
        dirNodes.clear()

        nodes.filter { it.parentCode == curDir }
                .forEach { dirNodes.add(it) }
    }

    fun getList(): ArrayList<Node> = if (search) nodesSearch else dirNodes

    fun get(n: Int) : Node = getList()[n]

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : NotesAdapter.ViewHolder =
            ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item, parent, false) as LinearLayout)

	override fun getItemCount() : Int = getList().size

    fun setQuery(query: String) {
        val notify = search != query.isEmpty()
        search = !query.isEmpty()

        if (search) {
            nodesSearch.clear()
            nodes.filter { it.contains(query) }
                    .forEach { nodesSearch.add(it) }
        }

        if (notify || search)
            notifyDataSetChanged()
    }

	inner class ViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
		var ll: LinearLayout = itemView!!.layout_item
        var image: ImageView = ll.image
        var title: TextView = ll.title
        var resolutions: LinearLayout = ll.resolutions
        var button: ImageButton = ll.button
        var progress: ProgressBar = ll.progress
        var posterCode: String = ""
        init {
            ll.setOnClickListener {
                I.Log("kek")
                val position = recyclerView.getChildAdapterPosition(ll)
                val node = getList()[position]
                if (node is Subfolder)
                    onFolderClickListener?.onFolderClick(node.code)
                else
                    onMediaClickListener?.onMediaClick(node as Media)
            }
        }
    }
    internal inner class ImageSetter : AsyncTask<ViewHolder, Int, ViewHolder>() {
        lateinit var code: String
        override fun doInBackground(vararg params: ViewHolder): ViewHolder? {
            code = params[0].posterCode
            val path = App.cachePath + File.separator + code
            val file = File(path)
            I.Log("ImageSetter: path: "+path)
            while (true) {
                if (!file.isFile) {
                    Thread.sleep(1000)
                    continue
                }

                if (file.canRead()) {
                    I.Log("file.canRead(yes)")
                    val bitmap = BitmapFactory.decodeFile(path)
                    drawables.put(code, BitmapDrawable(res, bitmap))
                    break
                } else if (!file.canRead()) {
                    I.Log("file.canRead(no)")
                    drawables.put(code, res.getDrawable(R.drawable.ic_broken_image))
                    break
                }
            }
            return params[0]
        }
        override fun onPostExecute(result: ViewHolder) {
            super.onPostExecute(result)

            if (code == result.posterCode)
                result.image.setImageDrawable(drawables[code])
            else
                I.Log("viewHolder.code != code")
        }
    }

    interface OnFolderClickListener {
        fun onFolderClick(code: String)
    }

    interface OnMediaClickListener {
        fun onMediaClick(media: Media)
    }

}
