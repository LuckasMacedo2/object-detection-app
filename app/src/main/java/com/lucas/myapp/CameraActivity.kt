package com.lucas.myapp

import ApiServiceClient
import android.Manifest.permission.CAMERA
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.hardware.Camera
import android.net.Uri
import android.os.*
import android.view.Surface
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

            val cameraInfo = Camera.CameraInfo()
            Camera.getCameraInfo(0, cameraInfo)
            val rotation = windowManager.defaultDisplay.rotation
            val degrees = when (rotation) {
                Surface.ROTATION_0 -> 0
                Surface.ROTATION_90 -> 90
                Surface.ROTATION_180 -> 180
                Surface.ROTATION_270 -> 270
                else -> 0
            }
            val result = (cameraInfo.orientation - degrees + 360) % 360
            camera?.setDisplayOrientation(result)

            if(surfaceView.width != 0 && surfaceView.height != 0) {
                val parameters = camera?.parameters
                parameters?.setPreviewSize(
                    surfaceView.width,
                    surfaceView.height
                )
                parameters?.setPictureSize(
                    surfaceView.width,
                    surfaceView.height
                )
                camera?.parameters = parameters
            }

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
                                val resizedBitmap = Bitmap.createScaledBitmap(
                                    BitmapFactory.decodeByteArray(data, 0, data.size)
                                    , surfaceView.height, surfaceView.width, true)

                                val imageUri = bitmapToUri(this@CameraActivity, resizedBitmap)
                                sendImageToAPI(imageUri!!)
                            } catch (e: Exception) {
                                showToast(e.toString())
                            }
                            //showToast("Get Image")
                        })
                        camera?.stopPreview()
                        camera?.startPreview()
                    } catch (e: Exception) {
                        //showToast(e.toString())
                    }
                }
            }
        }
    }

    fun bitmapToUri(context: Context, resizedBitmap: Bitmap): Uri? {
        try {
            val file = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "image_${System.currentTimeMillis()}.png")

            FileOutputStream(file).use { fos ->
                // Comprime o Bitmap para o formato PNG
                resizedBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
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

                if(listaDetectedObjects.any()) {
                    rectImageView.clearRectangles()

                    listaDetectedObjects.forEach { objeto ->
                        rectImageView.addRectangle(objeto, 50)
                    }

                    //rectImageView.invalidate()
                }
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
