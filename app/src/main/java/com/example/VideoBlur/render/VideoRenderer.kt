package com.example.VideoBlur.render

import android.view.Surface


abstract class VideoRenderer {
    protected enum class Type(val value: Int) {
        GL_YUV420(0), VK_YUV420(1), GL_YUV420_FILTER(2);

    }

    private val mNativeContext: Long = 0

    protected open external fun create(type: Int)
    protected open external fun destroy()
    protected open external fun init(surface: Surface?, width: Int, height: Int)
    protected open external fun render()
    protected open external fun draw(data: ByteArray?, width: Int, height: Int, rotation: Int)
    protected open external fun setParameters(params: Int)
    protected open external fun getParameters(): Int

    companion object {
        init {
            System.loadLibrary("media-lib")
        }
    }
}
