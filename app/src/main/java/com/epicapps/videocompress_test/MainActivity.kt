package com.epicapps.videocompress_test

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.util.concurrent.ThreadLocalRandom

class MainActivity : AppCompatActivity() {

    private var recordingVideoFile: File? = null

    companion object {
        private const val REQUEST_RECORD_VIDEO_PERMISSION = 100
        private const val REQUEST_VIDEO_CAPTURE = 101
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupUI()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_VIDEO_CAPTURE) {
            if (resultCode == Activity.RESULT_OK) {
                //uploadFileAfterUserValidation(recordingVideoFile!!, subCampaignId!!, true)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_RECORD_VIDEO_PERMISSION) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.CAMERA
                )
                == PackageManager.PERMISSION_GRANTED
            ) {
                recordVideo()
            } else {

            }
        }
    }

    private fun setupUI(){
        btnCompressVideo.setOnClickListener {
            startCompress()
        }

        btnRecordVideo.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    this@MainActivity,
                    Manifest.permission.CAMERA
                )
                == PackageManager.PERMISSION_GRANTED
            ) {
                recordVideo()
            } else {
                ActivityCompat.requestPermissions(
                    this@MainActivity,
                    arrayOf(Manifest.permission.CAMERA),
                    REQUEST_RECORD_VIDEO_PERMISSION
                )
            }
        }
    }

    private fun startCompress() {

    }

    private fun recordVideo() {
        val randomNo: Int = ThreadLocalRandom.current().nextInt()
        recordingVideoFile =
            File("${externalCacheDir?.absolutePath}/video$randomNo.mp4")
        val fileProvider = FileProvider.getUriForFile(
            this,
            BuildConfig.APPLICATION_ID + ".provider",
            recordingVideoFile!!
        )
        //val videoUri = Uri.fromFile(recordingVideoFile)

        Intent(MediaStore.ACTION_VIDEO_CAPTURE).also { takeVideoIntent ->
            takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider)
            takeVideoIntent.putExtra("android.intent.extras.CAMERA_FACING", 1)
            takeVideoIntent.putExtra("android.intent.extras.LENS_FACING_FRONT", 1)
            takeVideoIntent.putExtra("android.intent.extra.USE_FRONT_CAMERA", true)
            //takeVideoIntent.resolveActivity(packageManager)?.also {
            startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE)
            //}
        }
    }
}