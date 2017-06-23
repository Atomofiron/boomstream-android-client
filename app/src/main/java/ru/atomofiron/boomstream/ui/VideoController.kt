package ru.atomofiron.boomstream.ui

import android.content.Context
import android.graphics.PorterDuff
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController

import android.widget.SeekBar
import ru.atomofiron.boomstream.R

class VideoController : MediaController {

    constructor(co: Context, attrs: AttributeSet) : super(co, attrs)

    constructor(co: Context, useFastForward: Boolean) : super(co, useFastForward)

    constructor(co: Context) : super(co)

    override fun addView(child: View, params: ViewGroup.LayoutParams) {
        super.addView(child, params)

        // На Huawei Honor 6X замечено, что SeekBar белого цвета, а нужно оранжевого (colorAccent)
        val seekbar = child.findViewById(resources.getIdentifier("mediacontroller_progress", "id", "android")) as SeekBar? ?: return
        val color = resources.getColor(R.color.colorAccent)

        seekbar.thumb.setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
        seekbar.progressDrawable.setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
    }
}
