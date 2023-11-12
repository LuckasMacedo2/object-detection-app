package com.lucas.myapp

import ApiServiceClient
import android.Manifest.permission.CAMERA
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Matrix
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.Gravity
import android.view.ScaleGestureDetector
import android.view.View
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.lucas.myapp.Data.DetectedObject
import com.squareup.picasso.Picasso
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.lang.Float.max
import java.lang.Float.min

class MainActivity : AppCompatActivity() {
    private lateinit var textView: TextView
    private lateinit var selectImageButton: Button
    private lateinit var capturePhotoButton: Button
    private lateinit var loadingSpinner: ProgressBar
    private lateinit var contentLayout: LinearLayout
    private lateinit var rectImageView: RectImageView

    private val tempFilesToDelete = mutableListOf<File>()

    private lateinit var scaleGestureDetector: ScaleGestureDetector
    private var scaleFactor = 1.0f

    private val REQUEST_CAMERA_PERMISSION = 123 // Pode ser qualquer número inteiro único
    private val REQUEST_IMAGE_CAPTURE = 1 // Pode ser qualquer número inteiro único

    private val imagePicker: ActivityResultLauncher<String> = registerForActivityResult(
        ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            sendImageToAPI(uri)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        textView = findViewById(R.id.textView)
        selectImageButton = findViewById(R.id.selectImageButton)
        capturePhotoButton = findViewById(R.id.capturePhotoButton)
        loadingSpinner = findViewById(R.id.loadingSpinner)
        contentLayout = findViewById(R.id.contentLayout)

        addImageView()

        selectImageButton.setOnClickListener {
            imagePicker.launch("image/*")
        }

        capturePhotoButton.setOnClickListener {
            try {
                capturarFoto()
            } catch (excecao: Exception) {
                showToast(excecao.message.toString())
            }
        }
    }

    fun capturarFoto() {
        // Verifique se a permissão da câmera já foi concedida
        if (ContextCompat.checkSelfPermission(this, CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // Se a permissão ainda não foi concedida, solicite-a ao usuário
            ActivityCompat.requestPermissions(this, arrayOf(CAMERA), REQUEST_CAMERA_PERMISSION)
        } else {
            // A permissão já foi concedida, inicie a atividade da câmera
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // O usuário concedeu permissão, inicie a atividade da câmera
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
            } else {
                // O usuário negou a permissão, você pode lidar com isso de acordo com suas necessidades
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            // A foto foi capturada com sucesso
            var imageUri: Uri? = null
            try {
                imageUri = saveBitmapAsUri(this, data?.extras?.get("data") as Bitmap)
                if (imageUri != null) {
                    sendImageToAPI(imageUri) // Envie a imagem para a API
                }
            } catch (excecao: Exception) {
                showToast(excecao.message.toString())
            }

        }
    }

    private fun addImageView() {
        rectImageView = RectImageView(this, null)
        contentLayout.addView(rectImageView)

        scaleGestureDetector = ScaleGestureDetector(this, ScaleListener())
        rectImageView.setOnTouchListener { v, event ->
            scaleGestureDetector.onTouchEvent(event)
            true
        }

        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT, // Largura da ImageView
            LinearLayout.LayoutParams.WRAP_CONTENT  // Altura da ImageView
        )
        layoutParams.gravity = Gravity.CENTER
        rectImageView.layoutParams = layoutParams
    }

    private fun sendImageToAPI(imageUri: Uri) {
        loadingSpinner.visibility = View.VISIBLE
        contentLayout.visibility = View.GONE

        val apiServiceClient = ApiServiceClient()

        val imagePath = convertUriToFile(imageUri)

        if (imagePath != null) {
            apiServiceClient.enviarImagem(imagePath) { listaDetectedObjects -> drawImage(listaDetectedObjects, imageUri) }
        }
    }

    private fun drawImage(listaDetectedObjects: List<DetectedObject>?, imageUri: Uri) {
        runOnUiThread {
            if (listaDetectedObjects != null) {

                rectImageView.clearRectangles()
                rectImageView.setImageURI(imageUri)

                listaDetectedObjects.forEach { objeto ->
                    rectImageView.addRectangle(objeto)
                }
            } else {
                Toast.makeText(this, "Erro ao carregar a imagem", Toast.LENGTH_LONG).show()
            }
            contentLayout.visibility = View.VISIBLE
            loadingSpinner.visibility = View.GONE
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


    fun saveBitmapAsUri(context: Context, bitmap: Bitmap, quality: Int = 100, maxWidth: Int = 1024, maxHeight: Int = 1024): Uri? {
        var imageUri: Uri? = null
        try {
            val cachePath = File(context.cacheDir, "images")
            cachePath.mkdirs() // Crie o diretório se não existir

            val uniqueFileName = "image_${System.currentTimeMillis()}.png" // Nome de arquivo único


            val scaledBitmap = scaleBitmap(bitmap, maxWidth, maxHeight)

            val stream: OutputStream = FileOutputStream(cachePath.resolve(uniqueFileName))
            scaledBitmap.compress(Bitmap.CompressFormat.PNG, quality, stream)
            stream.close()
            imageUri = Uri.fromFile(cachePath.resolve(uniqueFileName))
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return imageUri
    }

    // Função para redimensionar o Bitmap
    fun scaleBitmap(bitmap: Bitmap, maxWidth: Int, maxHeight: Int): Bitmap {
        val width = bitmap.width
        val height = bitmap.height

        val scaleWidth = maxWidth.toFloat() / width
        val scaleHeight = maxHeight.toFloat() / height
        val scale = minOf(scaleWidth, scaleHeight)

        val matrix = Matrix()
        matrix.postScale(scale, scale)

        return Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        deleteAllImages(this)
    }

    private fun deleteAllImages(context: Context) {
        try {
            val cachePath = File(context.cacheDir, "images")
            val files = cachePath.listFiles()

            for (file in files) {
                file.delete()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Erro ao excluir imagens", Toast.LENGTH_SHORT).show()
        }
    }

    private inner class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            scaleFactor *= detector.scaleFactor
            scaleFactor = max(0.1f, min(5.0f, scaleFactor))

            rectImageView.scaleX = scaleFactor
            rectImageView.scaleY = scaleFactor

            return true
        }
    }
}
