package com.example.finalwork

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import kotlinx.android.synthetic.main.activity_new_dish.*
import kotlinx.android.synthetic.main.activity_new_shop.*
import java.io.File
import java.io.FileOutputStream

class NewDishActivity : AppCompatActivity() {
    var type = -1
    var eaten = -1

    lateinit var bitmap: Bitmap

    var hasPic = false

    var path = ""

    var change = false

    val startActivityLauncher: ActivityResultLauncher<Intent> =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == Activity.RESULT_OK) {
                    val data = it.data
                    data?.data?.let {uri->
                        bitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(uri))
                        dishImageView?.setImageBitmap(bitmap)
                    }
                    cancelDishPicture.isClickable = true
                    hasPic = true
                    change = true
                }
            }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_dish)

        GetPermission()

        dishTypeSpinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                //切换选择
                type = position
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                type = 0
            }
        }

        dishEatenSpinner.onItemSelectedListener = object:AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                //切换选择
                eaten = position
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

                eaten = 0
            }
        }

        var createNew = intent.getBooleanExtra("createNew", true)?:true
        var dishID = intent.getLongExtra("dishID", 0)?:0
        if (!createNew) {
            editDishName.setText(intent.getStringExtra("name"))
            editDishInfo.setText(intent.getStringExtra("info"))
            dishTypeSpinner.setSelection(intent.getIntExtra("type", 0))
            dishEatenSpinner.setSelection(intent.getIntExtra("hasEaten", 0))
            path = intent.getStringExtra("picture")?:""
            if (path != "") {
                dishImageView.setImageURI(Uri.fromFile(File(path)))
                cancelDishPicture.isClickable = true
                hasPic = true
            }
            newDishCommit.setText("确认修改")
        }

        choseDishPicture.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "image/*"
            startActivityLauncher.launch(intent)
        }

        cancelDishPicture.setOnClickListener {
            cancelDishPicture.isClickable = false
            hasPic = false
            dishImageView.setImageResource(R.drawable.dish)
        }

        newDishCommit.setOnClickListener {
            val replyIntent = Intent()
            if (type == -1 || eaten == -1 || TextUtils.isEmpty(editDishName.text) || TextUtils.isEmpty(editDishInfo.text)) {
//                setResult(Activity.RESULT_CANCELED, replyIntent)
                Toast.makeText(this, "除了图片其他不可为空哦", Toast.LENGTH_LONG).show()
            } else {
                replyIntent.putExtra("dishID", dishID)
                replyIntent.putExtra("createNew", createNew)
                replyIntent.putExtra("name", editDishName.text.toString())
                replyIntent.putExtra("info", editDishInfo.text.toString())
                replyIntent.putExtra("type", type)
                replyIntent.putExtra("eaten", eaten)
                replyIntent.putExtra("index", intent.getIntExtra("index", 0))
                if (hasPic) {
                    if (path != "" && !change) {
                        replyIntent.putExtra("picture", path)
                    }
                    else {
                        val picturePath = getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString() + "/dish_$dishID.png"
                        val file = File(picturePath)
                        val fileOutputStream = FileOutputStream(file)
                        bitmap.compress(Bitmap.CompressFormat.PNG,100,fileOutputStream)
                        fileOutputStream.flush()
                        fileOutputStream.close()
                        replyIntent.putExtra("picture", picturePath)
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

    fun GetPermission()
    {
        if (Build.VERSION.SDK_INT >= 23) {
            val REQUEST_CODE_CONTACT = 101
            val permissions = arrayOf<String>(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            //验证是否许可权限
            for (str in permissions) {
                if (this.checkSelfPermission(str) != PackageManager.PERMISSION_GRANTED) {
                    //申请权限
                    this.requestPermissions(permissions, REQUEST_CODE_CONTACT)
                }
            }
        }
    }
}