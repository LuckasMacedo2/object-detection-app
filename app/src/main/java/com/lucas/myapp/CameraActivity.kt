package com.lucas.myapp

import ApiServiceClient
import android.Manifest.permission.CAMERA
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Camera
import android.net.Uri
import android.os.*
import androidx.appcompat.app.AppCompatActivity
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.lucas.myapp.Data.DetectedObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.*

class CameraActivity : AppCompatActivity() {
    private lateinit var surfaceView: SurfaceView
    private val CAMERA_PERMISSION_REQUEST = 5
    private lateinit var rectImageView: RectImageView
    private lateinit var rootView: RelativeLayout
    private var camera: Camera? = null
    private val handler = Handler(Looper.getMainLooper())
    private var isProcessingImage = false
    private val captureInterval = 10 * 1000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        surfaceView = findViewById(R.id.surfaceView)
        rootView = findViewById(R.id.rootLayout)


        if (ContextCompat.checkSelfPermission(this, CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(CAMERA), CAMERA_PERMISSION_REQUEST)
        } else {
            initializeCamera(surfaceView.holder)
        }

        surfaceView.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                initializeCamera(holder)
            }

            override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}

            override fun surfaceDestroyed(holder: SurfaceHolder) { }
        })

        surfaceView.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                initializeCamera(holder)

            }

            override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}

            override fun surfaceDestroyed(holder: SurfaceHolder) { }
        })

        rectImageView = findViewById(R.id.rectImageView)
    }

    private val captureImageRunnable = object : Runnable {
        override fun run() {
            captureImage()
            handler.postDelayed(this, captureInterval.toLong())
        }
    }


    private fun initializeCamera(holder: SurfaceHolder) {
        try {
            camera = Camera.open(0)
            camera?.setPreviewDisplay(holder)
            camera?.startPreview()

            handler.postDelayed(captureImageRunnable, captureInterval.toLong())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun captureImage() {

        if (camera != null) {

            if (!isProcessingImage) {
                CoroutineScope(Dispatchers.IO).launch {
                    //
                    try {
                        camera?.startPreview()
                        camera?.takePicture(null, null, Camera.PictureCallback { data, _ ->
                            isProcessingImage = true

                            try {
                                val imageUri = byteArrayToUri(this@CameraActivity, data)
                                sendImageToAPI(imageUri!!)
                            } catch (e: Exception) {
                                showToast(e.toString())
                            }
                            showToast("Get Image")
                        })
                        camera?.stopPreview()
                        camera?.startPreview()
                    } catch (e: Exception) {
                        showToast(e.toString())
                    }
                }
            }
        }
    }

    fun byteArrayToUri(context: Context, byteArray: ByteArray): Uri? {
        try {
            val file = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "image_${System.currentTimeMillis()}.png")

            FileOutputStream(file).use { fos ->
                fos.write(byteArray)
            }

            return Uri.fromFile(file)

        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }
    }

    private fun sendImageToAPI(imageUri: Uri) {
        val apiServiceClient = ApiServiceClient()

        val imagePath = convertUriToFile(imageUri)

        if (imagePath != null) {
            apiServiceClient.enviarImagem(imagePath) { listaDetectedObjects -> drawImage(listaDetectedObjects) }
        }
    }

    private fun convertUriToFile(uri: Uri): File? {
        try {
            val inputStream: InputStream? = contentResolver.openInputStream(uri)
            val file = File(cacheDir, "temp_image.jpg")
            val outputStream = FileOutputStream(file)
            inputStream?.copyTo(outputStream)
            inputStream?.close()
            outputStream.close()
            return file
        } catch (e: Exception) {
            showToast("Erro ao converter URI para arquivo: ${e.message}")
            return null
        }
    }

    private fun drawImage(listaDetectedObjects: List<DetectedObject>?) {
            if (listaDetectedObjects != null) {

                rectImageView.clearRectangles()

                listaDetectedObjects.forEach { objeto ->
                    rectImageView.addRectangle(objeto)
                }

                rectImageView.invalidate()
                isProcessingImage = false
            } else {
                Toast.makeText(this, "Erro ao carregar a imagem", Toast.LENGTH_LONG).show()
                isProcessingImage = false
            }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
