package com.example.tyokin

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.PixelFormat
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.GLUtils
import android.opengl.Matrix
import android.util.Log
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
import kotlin.math.cos
import kotlin.math.sin

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [OpenGLFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class OpenGLFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, kobuta: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // openGLの設定とview設置
        val glSurfaceView = GLSurfaceView(requireContext())
        glSurfaceView.setEGLContextClientVersion(2)
        glSurfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 0) // RGBA(8ビット)を使用
        glSurfaceView.setZOrderOnTop(true) // このビューを最上層に設定
        glSurfaceView.holder.setFormat(PixelFormat.TRANSLUCENT) // ピクセルフォーマットを透過に設定
        val mRenderer = GLRenderer(requireContext())
        glSurfaceView.setRenderer(mRenderer)
        glSurfaceView.renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY

        // フラグメント背景を透明に設定
        glSurfaceView.setBackgroundColor(0)

        return glSurfaceView
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment OpenGLFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            OpenGLFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
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
    private var angleCos = 0.0

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES20.glEnable(GLES20.GL_DEPTH_TEST)
        GLES20.glEnable(GLES20.GL_BLEND) // ブレンドを有効にする
        // シェーダープログラムの初期化
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA) // アルファブレンディングの設定
        model = Model(context, "model.obj")
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
        val ratio: Float = width.toFloat() / height.toFloat()
        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1f, 1f, 3f, 20f)
    }

    override fun onDrawFrame(gl: GL10?) {
        GLES20.glClearColor(0f, 0f, 0f, 0f) // 背景色を透明に設定
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)

        // テクスチャの更新
        model.setTexture(context)

        // カメラ行列を設定
        Matrix.setLookAtM(viewMatrix, 0, 0f, 0f, -5f, 0f, 0f, 0f, 0f, 1f, 0f)

        // モデル行列をリセットしてから回転を適用
        Matrix.setIdentityM(modelMatrix, 0)
        Matrix.rotateM(modelMatrix, 0, angle, 0f, 1f, 0f)  // Y軸周りの回転

        // MVP行列を計算
        Matrix.multiplyMM(mvpMatrix, 0, viewMatrix, 0, modelMatrix, 0)
        Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, mvpMatrix, 0)

        val fixedLightDir = floatArrayOf(-0.2f, -0.8f, 1.0f, 0f)
        val lightDirInViewSpace = FloatArray(4)
        Matrix.multiplyMV(lightDirInViewSpace, 0, viewMatrix, 0, fixedLightDir, 0)

        //angleCos += 0.01f
        angleCos = 0.0
        angle = (30 * Math.cos(angleCos.toDouble())).toFloat() + 192
        val angleRad = Math.toRadians(angle.toDouble())
        // ライト方向の設定 (毎フレーム更新)
        val lightDir = floatArrayOf(20 * cos(angleRad - 1.570f).toFloat(), 4.4f, 20 * sin(angleRad - 1.570f).toFloat())
        model.setLightDirection(lightDir) // ライトの方向を設定

        // モデルを描画
        model.draw(mvpMatrix)
    }

}

class Model(context: Context, objFileName: String) {
    private val vertexBuffer: FloatBuffer
    private val normalBuffer: FloatBuffer
    private val textureBuffer: FloatBuffer
    private val indexBuffer: ShortBuffer
    public var shaderProgram: Int
    private var textureId: Int = 0

    // シェーダーコード
    private val vertexShaderCode = """
        uniform mat4 uMVPMatrix;
        uniform mat4 uNormalMatrix; // 法線変換行列
        attribute vec4 vPosition;
        attribute vec3 vNormal;
        attribute vec2 aTexCoord;
        varying vec3 fragNormal;
        varying vec2 vTexCoord;
        void main() {
            gl_Position = uMVPMatrix * vPosition;
            
            // 法線を法線行列で変換
            fragNormal = (uNormalMatrix * vec4(vNormal, 0.0)).xyz;
            
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
            float intensity = max(dot(normalize(fragNormal), normalize(uLightDir)), 0.0);
            vec4 texColor = texture2D(uTexture, vTexCoord);
            vec3 ambientColor = vec3(0.2);
            vec3 color = texColor.rgb * intensity + ambientColor;
            
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
        val inputStream = context.assets.open("pigBank.obj")
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
                            val key = "$vertexIndex/$textureIndex/$normalIndex"
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
                            Log.d("OpenGLFlagment", "Texture UV: u=${textures[textureIndex * 2]}, v=${textures[textureIndex * 2 + 1]}");
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
        when(PigState.PigCostume){
            0 -> textureId = loadTexture(context, "cloth0.png")
            1 -> textureId = loadTexture(context, "cloth1.png")
            2 -> textureId = loadTexture(context, "cloth2.png")
            3 -> textureId = loadTexture(context, "cloth3.png")
            4 -> textureId = loadTexture(context, "cloth4.png")
            5 -> textureId = loadTexture(context, "cloth5.png")
            6 -> textureId = loadTexture(context, "cloth6.png")
            7 -> textureId = loadTexture(context, "cloth7.png")
            8 -> textureId = loadTexture(context, "cloth8.png")
            9 -> textureId = loadTexture(context, "cloth9.png")
            10 -> textureId = loadTexture(context, "cloth10.png")
            11 -> textureId = loadTexture(context, "cloth11.png")
        }
        if(Bank.Bankmoney < Bank.Savemoney){
            textureId = loadTexture(context, "cloth_weak.png")
        }
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

    fun setTexture(context: Context){
        fun setTexture(){
            when(PigState.PigCostume){
                0 -> textureId = loadTexture(context, "cloth0.png")
                1 -> textureId = loadTexture(context, "cloth1.png")
                2 -> textureId = loadTexture(context, "cloth2.png")
                3 -> textureId = loadTexture(context, "cloth3.png")
                4 -> textureId = loadTexture(context, "cloth4.png")
                5 -> textureId = loadTexture(context, "cloth5.png")
                6 -> textureId = loadTexture(context, "cloth6.png")
                7 -> textureId = loadTexture(context, "cloth7.png")
                8 -> textureId = loadTexture(context, "cloth8.png")
                9 -> textureId = loadTexture(context, "cloth9.png")
                10 -> textureId = loadTexture(context, "cloth10.png")
                11 -> textureId = loadTexture(context, "cloth11.png")
            }
            if(Bank.Bankmoney < Bank.Savemoney){
                textureId = loadTexture(context, "cloth_weak.png")
            }
        }
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
