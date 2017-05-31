package ru.atomofiron.boomstream.fragments

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.Button

import ru.atomofiron.boomstream.R
import ru.atomofiron.boomstream.activities.MainActivity
import ru.atomofiron.boomstream.models.retrofit.folder.Media
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_media.*
import kotlinx.android.synthetic.main.fragment_media.view.*
import ru.atomofiron.boomstream.I


class MediaFragment : Fragment() {

    private lateinit var co: Context
    private var mainView: View? = null
    private var media = Media()

    fun setMedia(media: Media): MediaFragment {
        mainView = null
        this.media = media

        return this
    }

    companion object {
        private val KEY_MEDIA: String = "KEY_MEDIA"

        fun newInstance(media: Media): MediaFragment {
            val fragment = MediaFragment()
            val bundle = Bundle()
            bundle.putString(KEY_MEDIA, Gson().toJson(media))

            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)

        if (mainView != null) {
            if (mainView!!.parent != null)
                (mainView!!.parent as ViewGroup).removeView(mainView)
            return mainView
        }

        val view = inflater!!.inflate(R.layout.fragment_media, container, false)

        init(view)

        mainView = view
        return view
    }

    private fun init(view: View) {
        //media = Gson().fromJson(arguments.getString(KEY_MEDIA), Media::class.java)
        view.media_title.text = media.title
        view.media_resolution.text = co.getString(R.string.media_resolution, media.width, media.height)
        var transcodes = ""
        media.transcodes.forEach { transcodes += it.title + " " }
        view.media_quality.text = co.getString(R.string.media_quality, transcodes)
        view.media_duration.text = co.getString(R.string.media_duration, media.duration)
        view.media_mediastatus.text = co.getString(R.string.media_mediastatus, media.mediaStatus)

        view.video_container.removeAllViews()
        val button = Button(activity)
        button.text = "Play"
        button.setOnClickListener { playVideo() }
        view.video_container.addView(button)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        co = context ?: return
    }

    override fun onStart() {
        super.onStart()
        (activity as MainActivity).onBackPressedListener = null
    }

    private fun playVideo() { // не не, WebView не пойдёт
        video_container.removeAllViews()
        val webView = WebView(co)
        webView.settings.javaScriptEnabled = true
        val player = media.playerCode.replace("{{width}}", "100%").replace("{{height}}", "100%")
        I.Log("WTF2: "+player)
        webView.loadDataWithBaseURL("", "<html><head><style>html, body {padding:0px;margin:0px;}</style></head><body>${Html.fromHtml(player)}</body></html>", "text/html", "UTF-8", "")
        video_container.addView(webView)
    }
}
