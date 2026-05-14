package com.example.mvp_app

import android.content.Context
import android.graphics.BitmapFactory
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.GLUtils
import android.opengl.Matrix
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer

class OpenGL : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // openGLの設定とview設置
        val glSurfaceView = GLSurfaceView(this)
        glSurfaceView.setEGLContextClientVersion(2)
        val mRenderer = GLRenderer(this)
        glSurfaceView.setRenderer(mRenderer)
        glSurfaceView.renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY
        setContentView(glSurfaceView)
    }
}

class GLRenderer(val context: Context) : GLSurfaceView.Renderer {
    // 描画用のmodelクラスのインスタンスを保持
    private lateinit var model: Model

    // 変換行列
    private val modelMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)
    private val projectionMatrix = FloatArray(16)
    private val mvpMatrix = FloatArray(16)

    private var angle = 0f

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES20.glEnable(GLES20.GL_DEPTH_TEST)
        model = Model(context, "model.obj")
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
        val ratio: Float = width.toFloat() / height.toFloat()
        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1f, 1f, 3f, 20f)
    }

    override fun onDrawFrame(gl: GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)

        Matrix.setLookAtM(viewMatrix, 0, 0f, 0f, -5f, 0f, 0f, 0f, 0f, 1f, 0f)
        Matrix.setIdentityM(modelMatrix, 0)
        Matrix.rotateM(modelMatrix, 0, angle, 1f, 1f, 0f)  // x軸とy軸周りに回転

        Matrix.multiplyMM(mvpMatrix, 0, viewMatrix, 0, modelMatrix, 0)
        Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, mvpMatrix, 0)

        // 基本のライト方向（カメラと一致するように設定）
        val baseLightDir = floatArrayOf(-0.2f, -0.4f, -1.0f, 0.0f)  // ベクトルとして4次元目は0

        // 回転後のライトの方向（x, y, z成分のみ）をシェーダーに渡す
        model.setLightDirection(floatArrayOf(baseLightDir[0], baseLightDir[1], baseLightDir[2]))

        model.draw(mvpMatrix)
        angle += 0.3f
    }

}

class Model(context: Context, objFileName: String) {
    private val vertexBuffer: FloatBuffer
    private val normalBuffer: FloatBuffer
    private val textureBuffer: FloatBuffer
    private val indexBuffer: ShortBuffer
    private var shaderProgram: Int
    private var textureId: Int = 0

    // シェーダーコード
    private val vertexShaderCode = """
        uniform mat4 uMVPMatrix;
        attribute vec4 vPosition;
        attribute vec3 vNormal;
        attribute vec2 aTexCoord;
        varying vec3 fragNormal;
        varying vec2 vTexCoord;
        void main() {
            gl_Position = uMVPMatrix * vPosition;
            vTexCoord = aTexCoord;
            fragNormal = vNormal;
        }
    """.trimIndent()

    private val fragmentShaderCode = """
        precision mediump float;
        varying vec3 fragNormal;
        varying vec2 vTexCoord;
        uniform vec3 uLightDir;
        uniform sampler2D uTexture;

        void main() {
            float intensity = max(dot(normalize(fragNormal), normalize(uLightDir)), 0.2);
            vec3 color = texture2D(uTexture, vTexCoord).rgb * intensity;
            gl_FragColor = vec4(color, 1.0);
        }
    """.trimIndent()

    init {
        val vertices = mutableListOf<Float>()
        val normals = mutableListOf<Float>()
        val textures = mutableListOf<Float>()
        val vertexData = mutableListOf<Float>()
        val normalData = mutableListOf<Float>()
        val textureData = mutableListOf<Float>()
        val indices = mutableListOf<Short>()
        val vertexNormalIndexMap = mutableMapOf<String, Short>()
        var indexCount: Short = 0

        // .objファイルを読み込み
        val inputStream = context.assets.open("piggy.obj")
        val reader = BufferedReader(InputStreamReader(inputStream))
        reader.useLines { lines ->
            lines.forEach { line ->
                val parts = line.split(" ")
                when (parts[0]) {
                    "v" -> {
                        vertices.add(parts[1].toFloat())
                        vertices.add(parts[2].toFloat())
                        vertices.add(parts[3].toFloat())
                    }
                    "vn" -> {
                        normals.add(parts[1].toFloat())
                        normals.add(parts[2].toFloat())
                        normals.add(parts[3].toFloat())
                    }
                    "vt" ->{
                        textures.add(parts[1].toFloat())
                        textures.add(parts[2].toFloat())
                    }
                    "f" -> {
                        val vertexIndices = parts.subList(1, parts.size).map { it.split("/") }
                        val faceIndices = mutableListOf<Short>()
                        for (indexPair in vertexIndices) {
                            val vertexIndex = indexPair[0].toInt() - 1
                            val textureIndex = indexPair[1].toInt() - 1
                            val normalIndex = indexPair[2].toInt() - 1
                            val key = "$vertexIndex/$normalIndex"
                            val index = vertexNormalIndexMap[key]
                            if (index != null) {
                                faceIndices.add(index)
                            } else {
                                vertexNormalIndexMap[key] = indexCount
                                vertexData.add(vertices[vertexIndex * 3])
                                vertexData.add(vertices[vertexIndex * 3 + 1])
                                vertexData.add(vertices[vertexIndex * 3 + 2])
                                normalData.add(normals[normalIndex * 3])
                                normalData.add(normals[normalIndex * 3 + 1])
                                normalData.add(normals[normalIndex * 3 + 2])
                                textureData.add(textures[textureIndex * 2])
                                textureData.add(1.0f - textures[textureIndex * 2 + 1])
                                faceIndices.add(indexCount)
                                indexCount++
                            }
                        }
                        if (faceIndices.size == 3) {
                            indices.addAll(faceIndices)
                        } else if (faceIndices.size == 4) {
                            indices.add(faceIndices[0])
                            indices.add(faceIndices[1])
                            indices.add(faceIndices[2])
                            indices.add(faceIndices[0])
                            indices.add(faceIndices[2])
                            indices.add(faceIndices[3])
                        }
                    }
                }
            }
        }

        vertexBuffer = ByteBuffer.allocateDirect(vertexData.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .put(vertexData.toFloatArray())
        vertexBuffer.position(0)

        normalBuffer = ByteBuffer.allocateDirect(normalData.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .put(normalData.toFloatArray())
        normalBuffer.position(0)

        textureBuffer = ByteBuffer.allocateDirect(textureData.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .put(textureData.toFloatArray())
        textureBuffer.position(0)

        indexBuffer = ByteBuffer.allocateDirect(indices.size * 2)
            .order(ByteOrder.nativeOrder())
            .asShortBuffer()
            .put(indices.toShortArray())
        indexBuffer.position(0)

        val vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)

        shaderProgram = GLES20.glCreateProgram().also {
            GLES20.glAttachShader(it, vertexShader)
            GLES20.glAttachShader(it, fragmentShader)
            GLES20.glLinkProgram(it)

            // シェーダープログラムのリンクエラーチェック
            val linkStatus = IntArray(1)
            GLES20.glGetProgramiv(it, GLES20.GL_LINK_STATUS, linkStatus, 0)
            if (linkStatus[0] == 0) {
                GLES20.glDeleteProgram(it)
                throw RuntimeException("Shader program linking failed: ${GLES20.glGetProgramInfoLog(it)}")
            }
        }

        // テクスチャのロード
        textureId = loadTexture(context, "pigTexture.png")
    }

    private fun loadShader(type: Int, shaderCode: String): Int {
        return GLES20.glCreateShader(type).also { shader ->
            GLES20.glShaderSource(shader, shaderCode)
            GLES20.glCompileShader(shader)
            val compileStatus = IntArray(1)
            GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compileStatus, 0)
            if (compileStatus[0] == 0) {
                GLES20.glDeleteShader(shader)
                throw RuntimeException("Shader compilation failed: ${GLES20.glGetShaderInfoLog(shader)}")
            }
        }
    }

    fun setLightDirection(lightDir: FloatArray) {
        val lightDirHandle = GLES20.glGetUniformLocation(shaderProgram, "uLightDir")
        GLES20.glUniform3fv(lightDirHandle, 1, lightDir, 0)
    }

    private fun loadTexture(context: Context, fileName: String): Int {
        val textureHandle = IntArray(1)
        GLES20.glGenTextures(1, textureHandle, 0)

        if (textureHandle[0] != 0) {
            val bitmap = BitmapFactory.decodeStream(context.assets.open(fileName))

            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0])
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0)

            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR)
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)

            bitmap.recycle()
        } else {
            throw RuntimeException("Error loading texture.")
        }

        return textureHandle[0]
    }

    fun draw(mvpMatrix: FloatArray) {
        GLES20.glUseProgram(shaderProgram)

        val positionHandle = GLES20.glGetAttribLocation(shaderProgram, "vPosition")
        GLES20.glEnableVertexAttribArray(positionHandle)
        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer)

        val normalHandle = GLES20.glGetAttribLocation(shaderProgram, "vNormal")
        GLES20.glEnableVertexAttribArray(normalHandle)
        GLES20.glVertexAttribPointer(normalHandle, 3, GLES20.GL_FLOAT, false, 0, normalBuffer)

        val mvpMatrixHandle = GLES20.glGetUniformLocation(shaderProgram, "uMVPMatrix")
        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0)

        // Set up texture attribute
        val texCoordHandle = GLES20.glGetAttribLocation(shaderProgram, "aTexCoord")
        GLES20.glEnableVertexAttribArray(texCoordHandle)
        GLES20.glVertexAttribPointer(texCoordHandle, 2, GLES20.GL_FLOAT, false, 0, textureBuffer)

        // Bind the texture
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId)

        GLES20.glDrawElements(GLES20.GL_TRIANGLES, indexBuffer.capacity(), GLES20.GL_UNSIGNED_SHORT, indexBuffer)

        GLES20.glDisableVertexAttribArray(positionHandle)
        GLES20.glDisableVertexAttribArray(normalHandle)
        GLES20.glDisableVertexAttribArray(texCoordHandle)
    }
}
