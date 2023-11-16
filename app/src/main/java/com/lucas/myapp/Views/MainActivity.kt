package com.lucas.myapp.Views

import com.lucas.myapp.Services.ApiServiceClient
import android.Manifest.permission.CAMERA
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
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
import com.lucas.myapp.R
import com.lucas.myapp.Utils.ArquivosUtil
import com.lucas.myapp.Utils.BitmapUtil
import java.io.File
import java.lang.Float.max
import java.lang.Float.min

class MainActivity : AppCompatActivity() {
    private lateinit var textView: TextView
    private lateinit var selectImageButton: Button
    private lateinit var capturePhotoButton: Button
    private lateinit var startCameraButton: Button
    private lateinit var loadingSpinner: ProgressBar
    private lateinit var contentLayout: LinearLayout
    private lateinit var rectImageView: RectImageView

    private lateinit var scaleGestureDetector: ScaleGestureDetector
    private var scaleFactor = 1.0f

    private val REQUEST_CAMERA_PERMISSION = 123
    private val REQUEST_IMAGE_CAPTURE = 1

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

        startCameraButton = findViewById<Button>(R.id.startCameraButton)

        startCameraButton.setOnClickListener {
            try {
                val intent = Intent(this, CameraActivity::class.java)
                startActivity(intent)
            } catch (excecao: Exception) {
                showToast(excecao.message.toString())
            }
        }
    }

    fun capturarFoto() {
        if (ContextCompat.checkSelfPermission(this, CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(CAMERA), REQUEST_CAMERA_PERMISSION)
        } else {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
            } else {  }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            var imageUri: Uri? = null
            try {
                imageUri = BitmapUtil.saveBitmapAsUri(this, data?.extras?.get("data") as Bitmap)
                if (imageUri != null) {
                    sendImageToAPI(imageUri)
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
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.gravity = Gravity.CENTER
        rectImageView.layoutParams = layoutParams
    }

    private fun sendImageToAPI(imageUri: Uri) {
        loadingSpinner.visibility = View.VISIBLE
        contentLayout.visibility = View.GONE

        val apiServiceClient = ApiServiceClient()

        val imagePath = ArquivosUtil.convertUriToFile(imageUri, contentResolver, cacheDir)

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
                    rectImageView.addRectangle(objeto, false)
                }
            } else {
                Toast.makeText(this, "Erro ao carregar a imagem", Toast.LENGTH_LONG).show()
            }
            contentLayout.visibility = View.VISIBLE
            loadingSpinner.visibility = View.GONE
        }
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
