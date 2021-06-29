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

class ScaleImageView(context: Context, attributes: AttributeSet)
    : androidx.appcompat.widget.AppCompatImageView(context, attributes) {
    private var lastX = 0f
    private var lastY = 0f
    private var lastDistance = 0f

    override fun onTouchEvent(event: MotionEvent?): Boolean {

        when(event?.action) {
            MotionEvent.ACTION_DOWN->{
                lastX = event.x
                lastY = event.y
            }
            MotionEvent.ACTION_MOVE->{

            }
        }
        super.onTouchEvent(event)
        return true
    }

    fun scaleImage(scale: Float) {
        var newWidth : Int = (width * scale).toInt()
        var newHeight : Int = (height * scale).toInt()
        height
    }

}