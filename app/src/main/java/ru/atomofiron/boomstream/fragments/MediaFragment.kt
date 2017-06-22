package ru.atomofiron.boomstream.fragments

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.*

import ru.atomofiron.boomstream.R
import ru.atomofiron.boomstream.activities.MainActivity
import ru.atomofiron.boomstream.models.retrofit.folder.Media
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_media.*
import kotlinx.android.synthetic.main.fragment_media.view.*
import android.util.DisplayMetrics
import android.widget.*
import ru.atomofiron.boomstream.App
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener

class MediaFragment : Fragment(), OnItemSelectedListener {

    private lateinit var co: Context
    private lateinit var media: Media

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

        val view = inflater!!.inflate(R.layout.fragment_media, container, false)

        init(view)

        return view
    }

    private fun init(view: View) {
        media = Gson().fromJson(arguments.getString(KEY_MEDIA), Media::class.java)
        view.media_title.text = media.title
        view.media_resolution.text = co.getString(R.string.media_resolution, media.width, media.height)
        var transcodes = ""
        media.transcodes.forEach { transcodes += it.title + " " }
        view.media_quality.text = co.getString(R.string.media_quality, transcodes)
        view.media_duration.text = co.getString(R.string.media_duration, media.duration)
        view.media_mediastatus.text = co.getString(R.string.media_mediastatus, media.mediaStatus)

        val videoContainer = view.video_container
        val videoView = videoContainer.video_view

        val metrics = DisplayMetrics()
        activity.windowManager.defaultDisplay.getMetrics(metrics)
        val params = videoContainer.layoutParams
        params.height = metrics.widthPixels * 9 / 16 + 2
        videoContainer.layoutParams = params

        val mediaController = MediaController(context)
        mediaController.setAnchorView(videoView)
        videoView.setMediaController(mediaController)

        view.spinner.adapter = ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, android.R.id.text1, media.transcodesTitles)
        view.spinner.onItemSelectedListener = this
    }

    override fun onNothingSelected(arg0: AdapterView<*>) {}
    override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
        playVideo(media.transcodes[pos].pseudoMP4)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        co = context ?: return
    }

    override fun onStart() {
        super.onStart()
        (activity as MainActivity).onBackPressedListener = null
    }

    private fun playVideo(url: String) {
        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE)
            (activity as AppCompatActivity).supportActionBar?.hide()

        video_view.setVideoPath(App.getVideoCache(context).getProxyUrl(url))
    }

    override fun onDestroy() {
        super.onDestroy()
        (activity as AppCompatActivity).supportActionBar?.show()
    }
}
