package com.lucas.myapp

import ApiServiceClient
import android.Manifest.permission.CAMERA
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.hardware.Camera
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.lucas.myapp.Data.DetectedObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        surfaceView = findViewById(R.id.surfaceView)
        rootView = findViewById(R.id.rootLayout) // Substitua "rootLayout" pelo ID do layout raiz no seu XML

        // Adicione o RectImageView ao layout

        // Solicita permissões de câmera
        if (ContextCompat.checkSelfPermission(this, CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(CAMERA), CAMERA_PERMISSION_REQUEST)
        } else {
            // Se as permissões já foram concedidas, inicialize a câmera
            initializeCamera(surfaceView.holder)
        }

        surfaceView.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                // Inicialize a câmera e comece a exibir o feed de vídeo
                initializeCamera(holder)
            }

            override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}

            override fun surfaceDestroyed(holder: SurfaceHolder) {
                // Libere recursos da câmera
            }
        })

        // Inicialize a câmera imediatamente
        surfaceView.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                // Inicialize a câmera e comece a exibir o feed de vídeo
                initializeCamera(holder)

                // Capture uma imagem da câmera imediatamente
                captureImage()
            }

            override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}

            override fun surfaceDestroyed(holder: SurfaceHolder) {
                // Libere recursos da câmera
            }
        })

        rectImageView = findViewById(R.id.rectImageView)
        // Crie o RectImageView dinamicamente
        /*rectImageView = RectImageView(this, null)
        rootView.addView(rectImageView)

        rectImageView.layoutParams = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.MATCH_PARENT,
            RelativeLayout.LayoutParams.MATCH_PARENT
        )*/

        startImageCaptureTimer()
    }

    private fun startImageCaptureTimer() {
        val imageCaptureInterval = 10 * 1000 // 10 segundos em milissegundos
        handler.postDelayed(object : Runnable {
            override fun run() {
                captureImage()
                handler.postDelayed(this, imageCaptureInterval.toLong())
            }
        }, imageCaptureInterval.toLong())
    }


    private fun initializeCamera(holder: SurfaceHolder) {
        try {
            // Abre a câmera (use a câmera traseira, índice 0, por padrão)
            camera = Camera.open(0)
            camera?.setPreviewDisplay(holder)
            camera?.startPreview()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun captureImage() {

            if (camera != null) {

                // Inicia a exibição da câmera
                //camera?.startPreview()

                /*if (!isProcessingImage) {
                    // Configura a captura de uma única imagem
                    camera?.takePicture(null, null, Camera.PictureCallback { data, camera ->
                        isProcessingImage = true
                        // Aqui você pode processar a imagem capturada
                        //val imageData = processImageData(data) // Implemente essa função

                        val imageUri = byteArrayToUri(this, data)

                        // Envie a imagem para a API
                        sendImageToAPI(imageUri!!)
                        // Após o processamento, pare a exibição da câmera
                        camera.stopPreview()
                    })
                }*/

                // Use Coroutines para capturar a imagem da câmera
                GlobalScope.launch(Dispatchers.Default) {
                    if (!isProcessingImage) {
                        // Configura a captura de uma única imagem
                        camera?.takePicture(null, null, Camera.PictureCallback { data, camera ->
                            isProcessingImage = true
                            val imageUri = byteArrayToUri(this@CameraActivity, data)

                            // Envie a imagem para a API
                            sendImageToAPI(imageUri!!)
                            // Após o processamento, pare a exibição da câmera
                            //camera.stopPreview()
                            isProcessingImage = false
                        })
                    }
                }
            }
    }

    fun byteArrayToUri(context: Context, byteArray: ByteArray): Uri? {
        try {
            // Crie um arquivo temporário para armazenar o ByteArray
            val file = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "image_${System.currentTimeMillis()}.png")

            // Escreva o ByteArray no arquivo
            FileOutputStream(file).use { fos ->
                fos.write(byteArray)
            }

            // Obtenha a URI do arquivo
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
            apiServiceClient.enviarImagem(imagePath) { listaDetectedObjects -> drawImage(listaDetectedObjects, imageUri) }
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

    private fun drawImage(listaDetectedObjects: List<DetectedObject>?, imageUri: Uri) {
        runOnUiThread {
            if (listaDetectedObjects != null) {

                rectImageView.clearRectangles()
                //rectImageView.setImageURI(imageUri)

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
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}