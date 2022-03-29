package com.example.internalexternalstorages

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.core.content.ContextCompat
import com.example.internalexternalstorages.databinding.ActivityMainBinding
import java.io.*
import java.nio.charset.Charset
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {
    val isPersistent: Boolean = true
    val isInternal: Boolean = false
    lateinit var binding: ActivityMainBinding
    var readPermissionGranted = false
    var writePermissionGranted = false
    var permissionsToRequest = mutableListOf<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initViews()
        checkStoragePaths()
        createInternalFile()
        createExternalFile()

    }
    private fun initViews() {
        requestPermisions()
        binding.bSaveInt.setOnClickListener {
            saveInternalFile("Diyorbek Shukriddinov")
        }
        binding.bReadInt.setOnClickListener {
            readInternalFile()
        }
        binding.bCamera.setOnClickListener {
            takePhoto.launch()
        }
        binding.bSaveExt.setOnClickListener {
            saveExternalFile("Shukriddinov Diyorbek")
        }
        binding.bReadExt.setOnClickListener {
            readExternalFile()
        }
    }

    private fun requestPermisions(){
        val hasReadPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
        val hasWritePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
        val minSdk29 = Build.VERSION.SDK_INT>= Build.VERSION_CODES.Q
        readPermissionGranted = hasReadPermission
        writePermissionGranted = hasWritePermission || minSdk29

        if (!readPermissionGranted){
            permissionsToRequest.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        if (!writePermissionGranted){
            permissionsToRequest.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        if (permissionsToRequest.isNotEmpty()){
            permissionLauncher.launch(permissionsToRequest.toTypedArray())
        }
    }
    private val permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){
        permissions ->
        readPermissionGranted = permissions[Manifest.permission.READ_EXTERNAL_STORAGE] ?: readPermissionGranted
        writePermissionGranted = permissions[Manifest.permission.WRITE_EXTERNAL_STORAGE] ?: writePermissionGranted

        if (readPermissionGranted) Toast.makeText(this, "READ_EXTERNAL_STORAGE", Toast.LENGTH_SHORT).show()
        if (writePermissionGranted) Toast.makeText(this, "WRITE_EXTERNAL_STORAGE", Toast.LENGTH_SHORT).show()
    }

    fun checkStoragePaths() {
        val internal_n1 = getDir("custom", 0)
        val internal_n2 = filesDir

        val external_n1 = getExternalFilesDir(null)
        val external_n2 = externalCacheDir
        val external_n3 = getExternalFilesDir(Environment.DIRECTORY_PICTURES)

        Log.d("@@@", internal_n1.absolutePath)
        Log.d("@@@", internal_n2.absolutePath)
        Log.d("@@@", external_n1!!.absolutePath)
        Log.d("@@@", external_n2!!.absolutePath)
        Log.d("@@@", external_n3!!.absolutePath)
    }

    //internal storage

    fun createInternalFile() {
        val fileName = "pdp_internal.txt"
        val file: File
        file = if (isPersistent) {
            File(filesDir, fileName)
        } else {
            File(cacheDir, fileName)
        }

        if (!file.exists()) {
            try {
                file.createNewFile()
                Toast.makeText(this, "File created", Toast.LENGTH_SHORT).show()
            } catch (e: IOException){
                Toast.makeText(this, "file creation filed", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "File already exists", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveInternalFile(data: String) {
        val fileName = "pdp_internal.txt"
        try {
            val fileOutputStream: FileOutputStream
            fileOutputStream = if (isPersistent) {
                openFileOutput(fileName, MODE_PRIVATE)
            } else {
                val file = File(cacheDir, fileName)
                FileOutputStream(file)
            }
            fileOutputStream.write(data.toByteArray(Charset.forName("UTF-8")))
            Toast.makeText(this, "Write successful", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Write filed", Toast.LENGTH_SHORT).show()
        }
    }
    private fun readInternalFile() {
        val fileName = "pdp_internal.txt"
        try {
            val fileInputStream: FileInputStream
            fileInputStream = if (isPersistent) {
                openFileInput(fileName)
            } else {
                val file = File(cacheDir, fileName)
                FileInputStream(file)
            }
            val inputStreamReader = InputStreamReader(
                fileInputStream,
                Charset.forName("UTF-8")
            )
            val lines: MutableList<String?> = ArrayList()
            val reader = BufferedReader(inputStreamReader)
            var line = reader.readLine()
            while (line != null) {
                lines.add(line)
                line = reader.readLine()
            }
            val readText = TextUtils.join("\n", lines)
            Toast.makeText(this, readText, Toast.LENGTH_SHORT).show()

        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            Toast.makeText(this, "read from file failed", Toast.LENGTH_SHORT).show()
        }
    }
    //external storage
    private fun createExternalFile() {
        val fileName = "pdp_external.txt"
        val file: File
        file = if (isPersistent) {
            File(getExternalFilesDir(null), fileName)
        } else {
            File(externalCacheDir, fileName)
        }
        Log.d("@@@", "absolutePath: " + file.absolutePath)
        if (!file.exists()) {
            try {
                file.createNewFile()
                Toast.makeText(this, "File has been created", Toast.LENGTH_SHORT).show()
            } catch (e: IOException) {
                Toast.makeText(this, "File creation filed", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "File already exists", Toast.LENGTH_SHORT).show()
        }
    }
    private fun saveExternalFile(data: String) {
        val fileName = "pdp_external.txt"
        val file: File
        file = if (isPersistent) {
            File(getExternalFilesDir(null), fileName)
        } else {
            File(externalCacheDir, fileName)
        }
        try {
            val fileOutputStream = FileOutputStream(file)
            fileOutputStream.write(data.toByteArray(Charset.forName("UTF-8")))
            Toast.makeText(this, "write successful", Toast.LENGTH_SHORT).show()

        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            Toast.makeText(this, "write failed", Toast.LENGTH_SHORT).show()
        }
    }
    private fun readExternalFile() {
        val fileName = "pdp_external.txt"
        val file: File
        file = if (isPersistent)
            File(getExternalFilesDir(null), fileName)
        else
            File(externalCacheDir, fileName)

        Log.d("@@@",file.absolutePath)

        try {
            val fileInputStream = FileInputStream(file)
            val inputStreamReader = InputStreamReader(fileInputStream, Charset.forName("UTF-8"))
            val lines: MutableList<String?> = java.util.ArrayList()
            val reader = BufferedReader(inputStreamReader)
            var line = reader.readLine()
            while (line != null) {
                lines.add(line)
                line = reader.readLine()
            }
            val readText = TextUtils.join("\n", lines)
            Log.d("StorageActivity", readText)
            Toast.makeText(this, readText, Toast.LENGTH_SHORT).show()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            Toast.makeText(this, "failed", Toast.LENGTH_SHORT).show()
        }
    }
    //Photo save

    private val takePhoto =
        registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
            val filename = UUID.randomUUID().toString()

            val isPhotoSaved = if (isInternal) {
                savePhotoToInternalStorage(filename, bitmap)
            } else {
                if (writePermissionGranted) {
                    savePhotoToExternalStorage(filename, bitmap)
                } else {
                    false
                }
            }

            if (isPhotoSaved) {
                Toast.makeText(this, "Photo saved successfully", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Failed to save photo", Toast.LENGTH_SHORT).show()
            }
        }


    private fun savePhotoToInternalStorage(filename: String, bitmap: Bitmap?): Boolean {
        return try {
            openFileOutput("$filename.jpg", MODE_PRIVATE).use { stream ->
                if (!bitmap!!.compress(Bitmap.CompressFormat.JPEG, 95, stream)) {
                    throw IOException("Couldn't save bitmap")
                }
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
    private fun savePhotoToExternalStorage(filename: String, bitmap: Bitmap?): Boolean {
        val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } else {
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }

        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "$filename.jpg")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpg")
            put(MediaStore.Images.Media.WIDTH, bitmap!!.width)
            put(MediaStore.Images.Media.HEIGHT, bitmap.height)
        }

        return try {
            contentResolver.insert(collection, contentValues)?.also { uri ->
                contentResolver.openOutputStream(uri).use { outputStream ->
                    if (!bitmap!!.compress(Bitmap.CompressFormat.JPEG, 95, outputStream)) {
                        throw IOException("Couldn't save bitmap")
                    }
                }
            } ?: throw IOException("Couldn't create MediaStore entry")
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}