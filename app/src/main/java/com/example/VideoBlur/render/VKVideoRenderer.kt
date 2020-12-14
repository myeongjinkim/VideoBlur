package com.example.VideoBlur.render

import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView


class VKVideoRenderer : VideoRenderer(), SurfaceHolder.Callback {
    fun init(surface: SurfaceView) {
        Log.d("gdgd", "VKVideoRenderer-초기홛ㄷ")
        surface.holder.addCallback(this)
    }

    fun drawVideoFrame(data: ByteArray?, width: Int, height: Int) {
        Log.d("gdgd", "VKVideoRenderer-드로우프레임")
        draw(data, width, height, 0)
        //draw(data, width, height, rotation);
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        Log.d("gdgd", "VKVideoRenderer-생성")
        create(Type.VK_YUV420.value)
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        Log.d("gdgd", "VKVideoRenderer-surfaceChanged ${width} ${height}")
        init(holder.surface, width, height)
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        Log.d("gdgd", "파괴")
        destroy()
    }
}
