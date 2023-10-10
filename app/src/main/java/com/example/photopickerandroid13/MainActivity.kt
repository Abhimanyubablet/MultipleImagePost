package com.example.photopickerandroid13

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.ktx.Firebase
import android.Manifest
import com.google.firebase.storage.ktx.storage

class MainActivity : AppCompatActivity() {
    // creating variables on below line.
    lateinit var BottomNavigationView: BottomNavigationView

    private lateinit var mediaPickerLauncher: ActivityResultLauncher<Intent>
    private var storageRef = Firebase.storage.reference
    private val READ_EXTERNAL_STORAGE_PERMISSION_REQUEST = 123

    private val selectedImageUris = mutableListOf<Uri>()

    @SuppressLint("IntentReset")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        BottomNavigationView = findViewById(R.id.bottom_navigation)


        // Update the mediaPickerLauncher callback to handle multiple images.
        mediaPickerLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    val data: Intent? = result.data

                    // Check if the data contains multiple selected images.
                    if (data?.clipData != null) {
                        val clipData = data.clipData
                        if (clipData != null) {
                            for (i in 0 until clipData.itemCount) {
                                val item = clipData?.getItemAt(i)
                                val imgUri = item?.uri
                                if (imgUri != null) {
                                    selectedImageUris.add(imgUri)
                                }
                                if (imgUri != null) {
                                    uploadImage(imgUri)
                                }
                            }
                        }
                    } else if (data?.data != null) {
                        val imgUri = data.data
                        if (imgUri != null) {
                            selectedImageUris.add(imgUri)
                        }
                        if (imgUri != null) {
                            uploadImage(imgUri)
                        }
                    }
                }
            }

        // Function to upload a single image to Firebase Storage.
        fun uploadImage(imgUri: Uri) {
            val fileName = System.currentTimeMillis().toString()
            val imageRef = storageRef.child("image/").child(fileName)

            imageRef.putFile(imgUri)
                .addOnSuccessListener { taskSnapshot ->
                    // File uploaded successfully
                    Toast.makeText(this, "Upload success", Toast.LENGTH_SHORT).show()

                    // Get the download URL and do something with it.
                    imageRef.downloadUrl.addOnSuccessListener { uri ->
                        val downloadUrl = uri.toString()
                        // Process the download URL, e.g., save it to a database.
                    }.addOnFailureListener { exception ->
                        Toast.makeText(
                            this,
                            "Download URL retrieval failed: $exception",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                .addOnFailureListener { exception ->
                    // File upload failed, handle the error
                    Toast.makeText(this, "Upload failed: $exception", Toast.LENGTH_SHORT).show()
                }
        }



        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                READ_EXTERNAL_STORAGE_PERMISSION_REQUEST
            )
        }


        // Adding a click listener for the button on below line.



//        pickImageBtn.setOnClickListener {
//            // Create an intent for picking media.
//            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
//            intent.type="*/*"
//            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true)
//            intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
//            // Start the media picker activity using the ActivityResultLauncher.
//            mediaPickerLauncher.launch(intent)
//        }

        loadFragment(HomeFragment())

        BottomNavigationView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.home -> loadFragment(HomeFragment())
                R.id.add -> {
                    val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                    intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
                    intent.type = "image/*"
                    mediaPickerLauncher.launch(intent)
                    loadFragment(AddFragment())
                }
                R.id.setting -> loadFragment(SettingFragment())
            }
            true
        }



    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            READ_EXTERNAL_STORAGE_PERMISSION_REQUEST -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted, you can now launch the image picker.
                } else {
                    // Permission denied, handle it (e.g., show a message or request again).
                }
            }
            // Handle other permission requests if needed...
        }
    }
    private fun loadFragment(fragment: Fragment){
        supportFragmentManager.beginTransaction().replace(R.id.framelaout,fragment).commit()
    }

    // Function to upload a single image to Firebase Storage.
    private fun uploadImage(imgUri: Uri) {
        val fileName = System.currentTimeMillis().toString()
        val imageRef = storageRef.child("image/").child(fileName)

        imageRef.putFile(imgUri)
            .addOnSuccessListener { taskSnapshot ->
                // File uploaded successfully
                Toast.makeText(this, "Upload success", Toast.LENGTH_SHORT).show()

                // Get the download URL and do something with it.
                imageRef.downloadUrl.addOnSuccessListener { uri ->
                    val downloadUrl = uri.toString()
                    // Process the download URL, e.g., save it to a database.
                }.addOnFailureListener { exception ->
                    Toast.makeText(this, "Download URL retrieval failed: $exception", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                // File upload failed, handle the error
                Toast.makeText(this, "Upload failed: $exception", Toast.LENGTH_SHORT).show()
            }
    }
}