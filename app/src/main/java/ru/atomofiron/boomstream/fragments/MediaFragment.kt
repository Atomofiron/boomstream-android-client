package ru.atomofiron.boomstream.fragments

import android.content.Context
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.*
import android.widget.Button

import ru.atomofiron.boomstream.R
import ru.atomofiron.boomstream.activities.MainActivity
import ru.atomofiron.boomstream.models.retrofit.folder.Media
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_media.*
import kotlinx.android.synthetic.main.fragment_media.view.*
import android.widget.MediaController
import android.widget.VideoView
import android.util.DisplayMetrics

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
        button.text = getString(R.string.media_play)
        button.setOnClickListener { playVideo() }
        view.video_container.addView(button)

        val metrics = DisplayMetrics()
        activity.windowManager.defaultDisplay.getMetrics(metrics)
        val params = view.video_container.layoutParams
        params.height = metrics.widthPixels * 9 / 16 + 2
        view.video_container.layoutParams = params
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        co = context ?: return
    }

    override fun onStart() {
        super.onStart()
        (activity as MainActivity).onBackPressedListener = null
    }

    private fun playVideo() {
        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE)
            (activity as AppCompatActivity).supportActionBar?.hide()

        video_container.removeAllViews()

        val videoView = VideoView(context)
        val mediaController = MediaController(context)
        mediaController.setAnchorView(videoView)
        videoView.setMediaController(mediaController)
        videoView.setVideoURI(Uri.parse(media.transcodes[0].pseudoMP4))
        videoView.start()

        video_container.addView(videoView)
    }

    override fun onDestroy() {
        super.onDestroy()
        (activity as AppCompatActivity).supportActionBar?.show()
    }
}
