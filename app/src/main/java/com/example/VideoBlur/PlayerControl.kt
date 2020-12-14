package com.example.VideoBlur

import android.content.Context
import android.graphics.Bitmap
import android.graphics.SurfaceTexture
import android.media.MediaFormat
import android.net.Uri
import android.util.Log
import android.view.TextureView
import com.google.android.exoplayer2.Format
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import android.media.MediaMetadataRetriever
import android.view.Surface
import com.google.android.exoplayer2.video.VideoFrameMetadataListener


class PlayerControl(context: Context): TextureView.SurfaceTextureListener, VideoFrameMetadataListener {

    private lateinit var playerView: PlayerView
    private var textureView:TextureView = TextureView(context)
    private var context:Context
    private val exoPlayer by lazy {
        SimpleExoPlayer.Builder(context).build()
    }

    init{
        this.context = context


    }



    fun prepare(source: Uri?) {
        Log.d("gdgd", "소스 prepare")
        val dataSourceFactory: DataSource.Factory = DefaultDataSourceFactory(
                context,
                Util.getUserAgent(context, context.packageName)
        )

        val videoSource = ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(source)
        exoPlayer.prepare(videoSource)

        exoPlayer.setVideoFrameMetadataListener(this);
    }



    fun setPlayer(exoPlayerView: PlayerView) {
        Log.d("gdgd", "setPlayer")
        playerView = exoPlayerView
        playerView.player = exoPlayer

        textureView.setSurfaceTextureListener(this);

    }
    fun setPlayerRelease(){
        exoPlayer.release()
    }
    fun startPlayer(){
        exoPlayer.playWhenReady = true
    }
    fun stopPlayer(){
        exoPlayer.playWhenReady = false
    }


    override fun onSurfaceTextureAvailable(p0: SurfaceTexture, p1: Int, p2: Int) {
        textureView = playerView.getVideoSurfaceView() as TextureView
        exoPlayer.setVideoSurface(Surface(textureView.surfaceTexture))
        Log.d("gdgd", "onSurfaceTextureAvailable")
//        val bitmap: Bitmap? = textureView.bitmap
//        if(bitmap!=null){
//            Log.d("gdgd", "$bitmap")
//        }
    }

    override fun onSurfaceTextureSizeChanged(p0: SurfaceTexture, p1: Int, p2: Int) {
        Log.d("gdgd", "onSurfaceTextureSizeChanged")

    }

    override fun onSurfaceTextureDestroyed(p0: SurfaceTexture): Boolean {
        Log.d("gdgd", "onSurfaceTextureDestroyed")
        return true
    }

    override fun onSurfaceTextureUpdated(p0: SurfaceTexture) {
        Log.d("gdgd", "onSurfaceTextureUpdated")
    }

    override fun onVideoFrameAboutToBeRendered(presentationTimeUs: Long, releaseTimeNs: Long, format: Format, mediaFormat: MediaFormat?) {
        Log.d("gdgd", "onVideoFrameAboutToBeRendered")
        textureView = playerView.getVideoSurfaceView() as TextureView
        val bitmap: Bitmap? = textureView.bitmap
        if(bitmap!=null){
            Log.d("gdgd", "$bitmap")
        }
    }


}