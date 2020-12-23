package com.example.videoblur

import android.content.Context
import android.net.Uri
import android.opengl.GLSurfaceView
import android.util.Log
import com.example.videoblur.renderer.EConfigChooser
import com.example.videoblur.renderer.EContextFactory
import com.example.videoblur.renderer.PlayerScaleType
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.google.android.exoplayer2.video.VideoListener


class PlayerControl(context: Context) :GLSurfaceView(context), VideoListener {

    private lateinit var glRenderer: GLRenderer
    private lateinit var playerView: PlayerView

    private var videoAspect = 1f
    private val playerScaleType: PlayerScaleType = PlayerScaleType.RESIZE_FIT_WIDTH


    init {
        setEGLContextFactory(EContextFactory())
        setEGLConfigChooser(EConfigChooser())

        glRenderer = GLRenderer(this)
        setRenderer(glRenderer);
    }
    private val exoPlayer by lazy {
        SimpleExoPlayer.Builder(context).build()
    }

    fun prepare(source: Uri?) {
        Log.d("gdgd", "소스 prepare")
        val dataSourceFactory: DataSource.Factory = DefaultDataSourceFactory(
                context, Util.getUserAgent(context, context.packageName)
        )

        val videoSource = ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(source)
        exoPlayer.prepare(videoSource)

    }

    fun setPlayer(exoPlayerView: PlayerView) {
        Log.d("gdgd", "setPlayer")
        playerView = exoPlayerView
        playerView.player = exoPlayer

        exoPlayer.addVideoListener(this)

        glRenderer.setExoPlayer(exoPlayer)

//        var videoDecoderGLSurfaceView= playerView.videoSurfaceView as VideoDecoderGLSurfaceView
//        videoDecoderGLSurfaceView.videoDecoderOutputBufferRenderer

    }
    fun setPlayerRelease(){
        exoPlayer.release()
        glRenderer.release()
    }
    fun startPlayer(){
        exoPlayer.playWhenReady = true
    }
    fun stopPlayer(){
        exoPlayer.playWhenReady = false
    }

    fun setBlurStrength(progress: Int){
        glRenderer.setBlurStrength(progress.toFloat()/100)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val measuredWidth = measuredWidth
        val measuredHeight = measuredHeight
        var viewWidth = measuredWidth
        var viewHeight = measuredHeight
        when (playerScaleType) {
            PlayerScaleType.RESIZE_FIT_WIDTH -> viewHeight = (measuredWidth / videoAspect).toInt()
            PlayerScaleType.RESIZE_FIT_HEIGHT -> viewWidth = (measuredHeight * videoAspect).toInt()
        }

        setMeasuredDimension(viewWidth, viewHeight)
    }

    override fun onVideoSizeChanged(width: Int, height: Int, unappliedRotationDegrees: Int, pixelWidthHeightRatio: Float) {
        Log.d("gdgd", "onVideoSizeChanged $width $height")
        videoAspect = width.toFloat() / height * pixelWidthHeightRatio
        requestLayout()
    }

    override fun onRenderedFirstFrame() {
        Log.d("gdgd", "onRenderedFirstFrame")
    }

}