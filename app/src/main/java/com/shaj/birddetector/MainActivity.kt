package com.shaj.birddetector

import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.AssetManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.googlecode.tesseract.android.TessBaseAPI
import com.shaj.birddetector.databinding.ActivityMainBinding
import com.shaj.birddetector.ml.CIFAR
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.common.ops.NormalizeOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.lang.RuntimeException


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var imageView: ImageView
    private lateinit var button: Button
    private lateinit var outputTextView: TextView
    private var GALLERY_REQUEST_CODE = 123
    var imageProcessor = ImageProcessor.Builder()
        .add(NormalizeOp(0.0f,255.0f))
        .add(ResizeOp(32,32, ResizeOp.ResizeMethod.BILINEAR ))
        .build()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        Assets.extractAssets(this)

        imageView = binding.imageView
        button = binding.bntCaptureImage
        outputTextView = binding.outputTextView
        val buttonLoad = binding.btnLoadImage

        button.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                takePicturePreview.launch(null)
            } else {
                requestPermission.launch(android.Manifest.permission.CAMERA)
            }
        }
        buttonLoad.setOnClickListener {
            if (
//                ContextCompat.checkSelfPermission(
//                    this,
//                    android.Manifest.permission.READ_EXTERNAL_STORAGE
//                )
//                == PackageManager.PERMISSION_GRANTED
                true
            ) {
                val intent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                intent.type = "image/*"
                val mimeTypes = arrayOf("image/jpeg", "image/png", "image/jpg")
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
                intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                onResult.launch(intent)
            } else {
                requestPermission.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }

        //to download image when longPress on ImageView
        imageView.setOnLongClickListener {
            requestPermissionLauncher.launch(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            return@setOnLongClickListener true
        }
    }

    //request camera permission
    private val requestPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                takePicturePreview.launch(null)
            } else {
                Toast.makeText(this, "Permission Denied! Try Again.", Toast.LENGTH_SHORT).show()
            }
        }

    //launch camera and take picture
    private val takePicturePreview =
        registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
            if (bitmap != null) {
                imageView.setImageBitmap(bitmap)
                outputGenerator(bitmap)
            }
        }

    //get image
    private val onResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            Log.i("TAG", "This is the result: ${result.data} ${result.resultCode}")
            onResultReceived(GALLERY_REQUEST_CODE, result)
        }

    private fun onResultReceived(requestCode: Int, result: ActivityResult?) {
        when (requestCode) {
            GALLERY_REQUEST_CODE -> {
                if (result?.resultCode == Activity.RESULT_OK) {
                    result.data?.data?.let { uri ->
                        Log.i("TAG", "onResultRecived: $uri")
                        val bitmap =
                            BitmapFactory.decodeStream(contentResolver.openInputStream(uri))
                        imageView.setImageBitmap(bitmap)
                        outputGenerator(bitmap)
                    }
                } else {
                    Log.e("TAG", "onActivityResult: error in selecting image")
                }
            }
        }
    }

    private fun outputGenerator(bitmap: Bitmap) {
        /*
        //declaring tensorflow lite model veriable
        val birdsmodel = BirdsModel.newInstance(this)
//        var mnist = ImageClassifier()
        // Converting bitmap into tensorflow image
        val newBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        val tfimage = TensorImage.fromBitmap(newBitmap)

        // Process the image using trained model and sort it in descending order
        val outputs = birdsmodel.process(tfimage)
            .probabilityAsCategoryList.apply {
                sortByDescending { it.score }
            }

        // Getting result having high probability
        val highProbabilityOutput = outputs[0]

        // Setting output text
        outputTextView.text = highProbabilityOutput.label
        Log.i("TAG", "outputGenerator: $highProbabilityOutput")
        // Releases model resources if no longer used.
        birdsmodel.close()
        */

        //GITHUB PUSH 1
        /*
        var tfimage = TensorImage(DataType.FLOAT32)
        tfimage.load(bitmap)

        tfimage = imageProcessor.process(tfimage)

        val model = CIFAR.newInstance(this)

// Creates inputs for reference.
        val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 32, 32, 3), DataType.FLOAT32)
        inputFeature0.loadBuffer(tfimage.buffer)

// Runs model inference and gets result.
        val outputs = model.process(inputFeature0)
        val outputFeature0 = outputs.outputFeature0AsTensorBuffer.floatArray
        val maxIndex = outputFeature0.indexOfFirst { it == outputFeature0.maxOrNull() }

        val cifar10Classes = arrayOf(
            "Airplane",
            "Automobile",
            "Bird",
            "Cat",
            "Deer",
            "Dog",
            "Frog",
            "Horse",
            "Ship",
            "Truck"
        )

        outputTextView.text = cifar10Classes[maxIndex]


// Releases model resources if no longer used.
        model.close()
        */


        //GITHUB PUSH 2



        var tess = TessBaseAPI()


        var dataPath = this.filesDir.absolutePath


        outputTextView.text=dataPath

        if (!tess.init(dataPath, "eng")) {
            // Error initializing Tesseract (wrong/inaccessible data path or not existing language file)
            tess.recycle()
            outputTextView.text="Error loading Eng"
            return
        }
        if (!tess.init(dataPath, "ben")) {
            // Error initializing Tesseract (wrong/inaccessible data path or not existing language file)
            tess.recycle()
            outputTextView.text="Error loading Ben"
            return
        }

        outputTextView.text="Pass 1"
        tess.setImage(bitmap)
        var text = tess.utF8Text
        outputTextView.text=text
        tess.recycle()


    }

    //to download to device
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                AlertDialog.Builder(this).setTitle("Download Image?")
                    .setMessage("Do you want to download this image to your device?")
                    .setPositiveButton("Yes") { _, _ ->
                        val drawable: BitmapDrawable = imageView.drawable as BitmapDrawable
                        val bitmap = drawable.bitmap
                        downloadImage(bitmap)
                    }
                    .setNegativeButton("No") { dialog, _ ->
                        dialog.dismiss()
                    }.show()
            } else {
                Toast.makeText(this, "Please allow permission to download image", Toast.LENGTH_LONG)
                    .show()
            }
        }

    //fun that takes a bitmap and store to user's device
    private fun downloadImage(mBitmap: Bitmap): Uri? {
        val contentValues = ContentValues().apply {
            put(
                MediaStore.Images.Media.DISPLAY_NAME,
                "Birds_Images" + System.currentTimeMillis() / 1000
            )
            put(MediaStore.Images.Media.MIME_TYPE, "image/png")
        }
        val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        if (uri != null) {
            contentResolver.insert(uri, contentValues)?.also {
                contentResolver.openOutputStream(it).use { outputStream ->
                    if (!mBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)) {
                        throw IOException("Could'nt save the bitmap")
                    } else {
                        Toast.makeText(applicationContext, "Image Saved", Toast.LENGTH_LONG).show()
                    }
                }
                return it
            }
        }
        return null
    }
}


object Config {
    const val TESS_ENGINE = TessBaseAPI.OEM_LSTM_ONLY
    const val TESS_LANG = "eng"
    const val IMAGE_NAME = "sample.jpg"
}

object Assets {
    /**
     * Returns locally accessible directory where our assets are extracted.
     */

    fun getLocalDir(context: Context): File {
        return context.filesDir
    }

    /**
     * Returns locally accessible directory path which contains the "tessdata" subdirectory
     * with *.traineddata files.
     */

    fun getTessDataPath( context: Context): String {
        return getLocalDir(context).absolutePath
    }


    fun getImageFile( context: Context): File {
        return File(getLocalDir(context), Config.IMAGE_NAME)
    }

    fun getImageBitmap( context: Context): Bitmap {
        return BitmapFactory.decodeFile(getImageFile(context).absolutePath)
    }

    fun extractAssets( context: Context) {
        val am = context.assets
        val localDir = getLocalDir(context)
        if (!localDir.exists() && !localDir.mkdir()) {
            throw RuntimeException("Can't create directory $localDir")
        }
        val tessDir = File(getTessDataPath(context), "tessdata")
        if (!tessDir.exists() && !tessDir.mkdir()) {
            throw RuntimeException("Can't create directory $tessDir")
        }

        // Extract all assets to our local directory.
        // All *.traineddata into "tessdata" subdirectory, other files into root.
        try {
            for (assetName in am.list("")!!) {

                Log.i("my files", assetName)

                val targetFile: File = if (assetName.endsWith(".traineddata")) {
                    File(tessDir, assetName)
                } else {
                    File(localDir, assetName)
                }
                if (!targetFile.exists()) {
                    copyFile(am, assetName, targetFile)
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun copyFile(
        am: AssetManager,  assetName: String,
        outFile: File
    ) {
        try {
            am.open(assetName).use { `in` ->
                FileOutputStream(outFile).use { out ->
                    val buffer = ByteArray(1024)
                    var read: Int
                    while (`in`.read(buffer).also { read = it } != -1) {
                        out.write(buffer, 0, read)
                    }
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}
