package com.lucas.myapp

import ApiServiceClient
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.ScaleGestureDetector
import android.view.View
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.lucas.myapp.Data.DetectedObject
import com.squareup.picasso.Picasso
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.lang.Float.max
import java.lang.Float.min

class MainActivity : AppCompatActivity() {
    private lateinit var textView: TextView
    private lateinit var selectImageButton: Button
    private lateinit var loadingSpinner: ProgressBar
    private lateinit var contentLayout: LinearLayout
    private lateinit var rectImageView: RectImageView

    private val tempFilesToDelete = mutableListOf<File>()

    private lateinit var scaleGestureDetector: ScaleGestureDetector
    private var scaleFactor = 1.0f

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
        loadingSpinner = findViewById(R.id.loadingSpinner)
        contentLayout = findViewById(R.id.contentLayout)

        addimageView()

        selectImageButton.setOnClickListener {
            imagePicker.launch("image/*")
        }
    }

    private fun addimageView() {
        rectImageView = RectImageView(this, null)
        contentLayout.addView(rectImageView)

        scaleGestureDetector = ScaleGestureDetector(this, ScaleListener())
        rectImageView.setOnTouchListener { v, event ->
            scaleGestureDetector.onTouchEvent(event)
            true
        }
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

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        for (tempFile in tempFilesToDelete) {
            if (tempFile.exists()) {
                tempFile.delete()
            }
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
