package com.example.videoblur.filter

import android.content.res.Resources
import android.opengl.GLES20
import com.example.videoblur.renderer.EFramebufferObject
import com.example.videoblur.renderer.EglUtil

open class GLFilter constructor(private val vertexShaderSource: String = DEFAULT_VERTEX_SHADER, private val fragmentShaderSource: String = DEFAULT_FRAGMENT_SHADER) {
    private var program = 0
    private var vertexShader = 0
    private var fragmentShader = 0
    var vertexBufferName = 0

        private set
    private val handleMap = HashMap<String, Int>()

    constructor(res: Resources, vertexShaderSourceResId: Int, fragmentShaderSourceResId: Int) : this(res.getString(vertexShaderSourceResId), res.getString(fragmentShaderSourceResId)) {}

    fun setup() {
        release()
        vertexShader = EglUtil.loadShader(vertexShaderSource, GLES20.GL_VERTEX_SHADER)
        fragmentShader = EglUtil.loadShader(fragmentShaderSource, GLES20.GL_FRAGMENT_SHADER)
        program = EglUtil.createProgram(vertexShader, fragmentShader)
        vertexBufferName = EglUtil.createBuffer(VERTICES_DATA)
    }

    fun setFrameSize(width: Int, height: Int) {}
    fun release() {
        GLES20.glDeleteProgram(program)
        program = 0
        GLES20.glDeleteShader(vertexShader)
        vertexShader = 0
        GLES20.glDeleteShader(fragmentShader)
        fragmentShader = 0
        GLES20.glDeleteBuffers(1, intArrayOf(vertexBufferName), 0)
        vertexBufferName = 0
        handleMap.clear()
    }

    fun draw(texName: Int, fbo: EFramebufferObject?) {
        useProgram()
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vertexBufferName)
        GLES20.glEnableVertexAttribArray(getHandle("aPosition"))
        GLES20.glVertexAttribPointer(getHandle("aPosition"), VERTICES_DATA_POS_SIZE, GLES20.GL_FLOAT, false, VERTICES_DATA_STRIDE_BYTES, VERTICES_DATA_POS_OFFSET)
        GLES20.glEnableVertexAttribArray(getHandle("aTextureCoord"))
        GLES20.glVertexAttribPointer(getHandle("aTextureCoord"), VERTICES_DATA_UV_SIZE, GLES20.GL_FLOAT, false, VERTICES_DATA_STRIDE_BYTES, VERTICES_DATA_UV_OFFSET)
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texName)
        GLES20.glUniform1i(getHandle("sTexture"), 0)
        onDraw()
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)
        GLES20.glDisableVertexAttribArray(getHandle("aPosition"))
        GLES20.glDisableVertexAttribArray(getHandle("aTextureCoord"))
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0)
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0)
    }

    open fun onDraw() {}
    fun useProgram() {
        GLES20.glUseProgram(program)
    }

    fun getHandle(name: String): Int {
        val value = handleMap[name]
        if (value != null) {
            return value.toInt()
        }
        var location = GLES20.glGetAttribLocation(program, name)
        if (location == -1) {
            location = GLES20.glGetUniformLocation(program, name)
        }
        check(location != -1) { "Could not get attrib or uniform location for $name" }
        handleMap[name] = Integer.valueOf(location)
        return location
    }

    companion object {
        const val DEFAULT_UNIFORM_SAMPLER = "sTexture"
        const val DEFAULT_VERTEX_SHADER = "attribute vec4 aPosition;\n" +
                "attribute vec4 aTextureCoord;\n" +
                "varying highp vec2 vTextureCoord;\n" +
                "void main() {\n" +
                "gl_Position = aPosition;\n" +
                "vTextureCoord = aTextureCoord.xy;\n" +
                "}\n"
        const val DEFAULT_FRAGMENT_SHADER = "precision mediump float;\n" +
                "varying highp vec2 vTextureCoord;\n" +
                "uniform lowp sampler2D sTexture;\n" +
                "void main() {\n" +
                "gl_FragColor = texture2D(sTexture, vTextureCoord);\n" +
                "}\n"
        private val VERTICES_DATA = floatArrayOf( // X, Y, Z, U, V
                -1.0f, 1.0f, 0.0f, 0.0f, 1.0f,
                1.0f, 1.0f, 0.0f, 1.0f, 1.0f,
                -1.0f, -1.0f, 0.0f, 0.0f, 0.0f,
                1.0f, -1.0f, 0.0f, 1.0f, 0.0f
        )

        const val FLOAT_SIZE_BYTES = 4
        const val VERTICES_DATA_POS_SIZE = 3
        const val VERTICES_DATA_UV_SIZE = 2
        const val VERTICES_DATA_STRIDE_BYTES = (VERTICES_DATA_POS_SIZE + VERTICES_DATA_UV_SIZE) * FLOAT_SIZE_BYTES
        const val VERTICES_DATA_POS_OFFSET = 0 * FLOAT_SIZE_BYTES
        const val VERTICES_DATA_UV_OFFSET = VERTICES_DATA_POS_OFFSET + VERTICES_DATA_POS_SIZE * FLOAT_SIZE_BYTES
    }
}