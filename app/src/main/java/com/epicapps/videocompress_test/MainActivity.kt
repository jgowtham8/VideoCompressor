package com.epicapps.videocompress_test

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.abedelazizshe.lightcompressorlibrary.CompressionListener
import com.abedelazizshe.lightcompressorlibrary.VideoCompressor
import com.abedelazizshe.lightcompressorlibrary.VideoQuality
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.util.concurrent.ThreadLocalRandom


class MainActivity : AppCompatActivity() {

    private var recordingVideoFile: File? = null

    companion object {
        private const val REQUEST_RECORD_VIDEO_PERMISSION = 100
        private const val REQUEST_VIDEO_CAPTURE = 101

        var compressingVideoFile: File? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupUI()
    }

    @SuppressLint("SetTextI18n")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_VIDEO_CAPTURE) {
            if (resultCode == Activity.RESULT_OK) {
                btnCompressVideo?.isEnabled = true
                val size = (recordingVideoFile?.length()!!) / (1000 * 1000)
                tvSelectedVideoSize?.text = "Original Size : " + size + "MB"

                tvSelectedVideoLength?.text = "Duration : " + getDuration(recordingVideoFile!!)?.take(7)
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

    private fun getDuration(file: File): String? {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(this, Uri.fromFile(file))
        val time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
        val timeInMilliSec: Long? = time?.toLong()
        retriever.release()

        return timeInMilliSec?.toInt()?.toFileDuration()
    }

    private fun setupUI(){
        btnCompressVideo.setOnClickListener {
            startCompress()
        }

        btnPlay.setOnClickListener {
            val intent = Intent(this, PlayActivity::class.java)
            startActivity(intent)
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
    @SuppressLint("SetTextI18n")
    private fun startCompress() {
        val randomNo: Int = ThreadLocalRandom.current().nextInt()
        compressingVideoFile =
            File("${externalCacheDir?.absolutePath}/video$randomNo.mp4")

        VideoCompressor.start(recordingVideoFile?.absolutePath!!, compressingVideoFile?.absolutePath!!, object : CompressionListener{
            override fun onCancelled() {
                Toast.makeText(this@MainActivity, "Cancelled", Toast.LENGTH_SHORT).show()
            }

            override fun onFailure(failureMessage: String) {
                Toast.makeText(this@MainActivity, "Failed", Toast.LENGTH_SHORT).show()
            }

            override fun onProgress(percent: Float) {
                runOnUiThread {
                    tvCompressPercentage?.text = percent.toInt().toString() + "%"
                }
            }

            override fun onStart() {
                Toast.makeText(this@MainActivity, "Compression started", Toast.LENGTH_SHORT).show()
            }

            override fun onSuccess() {
                Toast.makeText(this@MainActivity, "finished", Toast.LENGTH_SHORT).show()
                btnPlay?.isEnabled = true

                val size = (compressingVideoFile?.length()!!) / (1000 * 1000)
                tvCompressedVideoSize?.text = "Compressed Size : " + size + "MB"
            }
        }, VideoQuality.MEDIUM, isMinBitRateEnabled = true, keepOriginalResolution = false)
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

        btnCompressVideo?.isEnabled = false
    }
}