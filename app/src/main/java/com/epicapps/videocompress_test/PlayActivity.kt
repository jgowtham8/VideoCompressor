package com.epicapps.videocompress_test

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_play.*

class PlayActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play)

        videoView?.setVideoPath(MainActivity.compressingVideoFile?.absolutePath)
        videoView?.start()
    }
}