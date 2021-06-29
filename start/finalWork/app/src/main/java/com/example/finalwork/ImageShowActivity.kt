package com.example.finalwork

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_image_show.*
import java.io.File

class ImageShowActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_show)
        val path = intent.getStringExtra("path")
        imageShow.setImageURI(Uri.fromFile(File(path)))
        returnImage.setOnClickListener {
            finish()
        }
    }

}