package com.example.finalwork

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_new_shop.*
import java.io.File
import java.io.FileOutputStream

class NewShopActivity : AppCompatActivity() {

    var type = -1
    var label = -1
    var createNew = true
    var shopID:Long = 0

    lateinit var outputImage: File
    lateinit var imageUri: Uri

    lateinit var bitmap: Bitmap

    var hasPic = false

    var path = ""

    var change = false

    //TODO: 5.2 select a picture


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_shop)

        //TODO: 5.1 get permission

        createNew = intent.getBooleanExtra("createNew", true)
        shopID = intent.getLongExtra("shopID", 0)

        shopTypeSpinner.onItemSelectedListener = object:AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                //切换选择
                type = position
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                type = 0
            }
        }

        shopLabelSpinner.onItemSelectedListener = object:AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                //切换选择
                label = position
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

                label = 0
            }
        }

        if (!createNew) {
            editShopName.setText(intent.getStringExtra("name"))
            editShopInfo.setText(intent.getStringExtra("info"))
            shopTypeSpinner.setSelection(intent.getIntExtra("type", 0))
            shopLabelSpinner.setSelection(intent.getIntExtra("label", 0))
            path = intent.getStringExtra("picture")?:""
            if (path != "") {
                shopImageView.setImageURI(Uri.fromFile(File(path)))
                cancelShopPicture.isClickable = true
                hasPic = true
            }
            newShopCommit.setText("确认修改")
        }

        choseShopPicture.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "image/*"
            startActivityLauncher.launch(intent)
        }

        cancelShopPicture.setOnClickListener {
            cancelShopPicture.isClickable = false
            hasPic = false
            shopImageView.setImageResource(R.drawable.dish)
        }
        
        newShopCommit.setOnClickListener {
            val replyIntent = Intent()
            if (type == -1 || label == -1 || TextUtils.isEmpty(editShopName.text) || TextUtils.isEmpty(editShopInfo.text)) {
//                setResult(Activity.RESULT_CANCELED, replyIntent)
                Toast.makeText(this, "除了图片其他不可为空哦", Toast.LENGTH_LONG).show()
            } else {
                replyIntent.putExtra("shopID", shopID)
                replyIntent.putExtra("createNew", createNew)
                replyIntent.putExtra("name", editShopName.text.toString())
                replyIntent.putExtra("info", editShopInfo.text.toString())
                replyIntent.putExtra("type", type)
                replyIntent.putExtra("label", label)
                replyIntent.putExtra("index", intent.getIntExtra("index", 0))
                if (hasPic) {
                    if (path != "" && !change) {
                        replyIntent.putExtra("picture", path)
                    }
                    else {
                        //TODO: 5.2 select a picture

                    }
                }
                else {
                    replyIntent.putExtra("picture", "")
                }
                setResult(Activity.RESULT_OK, replyIntent)
                finish()
            }
        }
    }

    //TODO: 5.1 get permission


}