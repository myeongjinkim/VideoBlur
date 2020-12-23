package com.example.videoblur

import android.graphics.SurfaceTexture
import android.opengl.GLES20
import android.opengl.GLES20.*
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.util.Log
import android.view.Surface
import com.example.videoblur.filter.GLBoxBlurFilter
import com.example.videoblur.filter.GLFilter
import com.example.videoblur.filter.GLPreviewFilter
import com.example.videoblur.renderer.*
import com.google.android.exoplayer2.SimpleExoPlayer
import javax.microedition.khronos.egl.EGLConfig


class GLRenderer(glSurfaceView: GLSurfaceView) : EFrameBufferObjectRenderer(), SurfaceTexture.OnFrameAvailableListener{

    private val MVPMatrix = FloatArray(16)
    private val ProjMatrix = FloatArray(16)
    private val MMatrix = FloatArray(16)
    private val VMatrix = FloatArray(16)
    private val STMatrix = FloatArray(16)

    private var filterFramebufferObject: EFramebufferObject? = null
    private var glFilter: GLFilter? = null
    private var glPreviewFilter: GLPreviewFilter? = null

    private var aspectRatio = 1f

    private var glSurfaceView:GLSurfaceView
    private lateinit var surfaceTexture: SurfaceTexture

    private var updateSurface = false
    private var texName = 0

    private lateinit var exoPlayer:SimpleExoPlayer

    init {
        Log.d("gdgd", "GLRenderer")
        Matrix.setIdentityM(STMatrix, 0);
        this.glSurfaceView =glSurfaceView
        setGlFilter()
    }

    fun setGlFilter() {
        Log.d("gdgdgd", "필텉ㅌㅌㅌㅌ")
        var filter: GLFilter = GLBoxBlurFilter()
        glFilter = filter
    }

    fun setBlurStrength(blurStrength:Float){
        (glFilter as GLBoxBlurFilter).blurSize = blurStrength
    }

    override fun onSurfaceCreated(config: EGLConfig?) {
        Log.d("gdgd", "onSurfaceCreated")
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        val args = IntArray(1)

        glGenTextures(args.size, args, 0);
        texName = args[0];
        surfaceTexture = SurfaceTexture(texName)
        surfaceTexture.setOnFrameAvailableListener(this)

        GLES20.glBindTexture(GLPreviewFilter.GL_TEXTURE_EXTERNAL_OES, texName)
        EglUtil.setupSampler(GLPreviewFilter.GL_TEXTURE_EXTERNAL_OES, GL_LINEAR, GL_NEAREST)
        GLES20.glBindTexture(GL_TEXTURE_2D, 0)

        filterFramebufferObject = EFramebufferObject()

        glPreviewFilter = GLPreviewFilter(GLPreviewFilter.GL_TEXTURE_EXTERNAL_OES);
        glPreviewFilter!!.setup();

        val surface = Surface(surfaceTexture)
        this.exoPlayer.setVideoSurface(surface);

        Matrix.setLookAtM(VMatrix, 0,
                0.0f, 0.0f, 5.0f,
                0.0f, 0.0f, 0.0f,
                0.0f, 1.0f, 0.0f
        );

        synchronized(this) {
            updateSurface = false;
        }

        GLES20.glGetIntegerv(GL_MAX_TEXTURE_SIZE, args, 0);
    }


    override fun onSurfaceChanged(width: Int, height: Int) {
        Log.d("gdgd", "onSurfaceChanged $width $height")
        glViewport(0, 0, width, height);

        filterFramebufferObject!!.setup(width, height)
        glPreviewFilter!!.setFrameSize(width, height);
        glFilter!!.setFrameSize(width, height);

        aspectRatio = width.toFloat() / height
        Matrix.frustumM(ProjMatrix, 0, -aspectRatio, aspectRatio, -1f, 1f, 5f, 7f)
        Matrix.setIdentityM(MMatrix, 0)
    }


    override fun onDrawFrame(fbo: EFramebufferObject?) {
        Log.d("gdgd", "onDrawFrame")
        synchronized(this) {
            if (updateSurface) {
                surfaceTexture.updateTexImage();
                surfaceTexture.getTransformMatrix(STMatrix);
                updateSurface = false;
            }
        }
        glFilter!!.setup();
        glFilter!!.setFrameSize(fbo!!.getWidth(), fbo!!.getHeight())

        filterFramebufferObject!!.enable();

        GLES20.glClear(GL_COLOR_BUFFER_BIT);


        Matrix.multiplyMM(MVPMatrix, 0, VMatrix, 0, MMatrix, 0);
        Matrix.multiplyMM(MVPMatrix, 0, ProjMatrix, 0, MVPMatrix, 0);

        glPreviewFilter!!.draw(texName, MVPMatrix, STMatrix, aspectRatio);


        fbo!!.enable()
        GLES20.glClear(GL_COLOR_BUFFER_BIT);
        glFilter!!.draw(filterFramebufferObject!!.getTexName(), fbo);
    }

    fun setExoPlayer(exoPlayer: SimpleExoPlayer) {
        this.exoPlayer = exoPlayer
    }

    @Synchronized
    override fun onFrameAvailable(p0: SurfaceTexture?) {
        Log.d("gdgd", "onFrameAvailable")
        updateSurface = true;
        glSurfaceView.requestRender()
    }

    fun release() {
        if (glFilter != null) {
            glFilter!!.release()
        }
        if (surfaceTexture != null) {
            surfaceTexture.release()
        }
    }


}