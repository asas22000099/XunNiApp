package com.example.finalwork

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.icu.text.SimpleDateFormat
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.finalwork.data.AppDatabase
import com.example.finalwork.data.DishDiary
import com.example.finalwork.data.ShopDiary
import kotlinx.android.synthetic.main.activity_new_diary.*
import kotlinx.android.synthetic.main.activity_new_shop.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import java.util.*

class NewDiaryActivity() : AppCompatActivity() {

    private lateinit var  appDatabase : AppDatabase
    private val applicationScope = CoroutineScope(SupervisorJob())

    lateinit var shopDiary: ShopDiary
    lateinit var dishDiary: DishDiary

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_diary)

        var name = intent.getStringExtra("name")
        newDiaryBar.setTitle(name + "日记")

        appDatabase = AppDatabase.getDatabase(this, applicationScope)

        var type = intent.getStringExtra("type")
        var id = intent.getLongExtra("id", 0)
        var index = intent.getIntExtra("index", 0)
        var diaryID = intent.getLongExtra("diaryID", 0)
        var createNew = intent.getBooleanExtra("createNew", true)

        if (createNew) {
            diaryPublishTime.setText(Date().toString())
        }
        else {
            editDiary.setText(intent.getStringExtra("diary"))
            diaryPublishTime.setText(intent.getStringExtra("publishTime"))
        }

        storeDiary.setOnClickListener {
            if (TextUtils.isEmpty(editDiary.text)) {
                Toast.makeText(this, "日记中还没有内容，不可保存哦", Toast.LENGTH_LONG).show()
            }
            else {
                var intent = Intent()
                intent.putExtra("createNew", createNew)
                intent.putExtra("diary", editDiary.text.toString())
                intent.putExtra("diaryID", diaryID)
                intent.putExtra("index", index)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
        }

        deleteDiary.setOnClickListener {
            AlertDialog.Builder(this)
                    .setTitle("删除日记")
                    .setMessage("确定要删除该日记吗？删除后不可恢复哦。")
                    .setPositiveButton("确定", DialogInterface.OnClickListener { _, _ ->
                        Toast.makeText(this, "日记已删除", Toast.LENGTH_LONG).show()
                        var intent = Intent()
                        intent.putExtra("delete", true)
                        intent.putExtra("index", index)
                        setResult(Activity.RESULT_OK, intent)
                        finish()
                    })
                    .setNegativeButton("取消", DialogInterface.OnClickListener { _, _ ->
                        Toast.makeText(this, "取消删除操作", Toast.LENGTH_LONG).show()
                    })
                    .show()
        }

    }

    fun getNow(): String {
        if (android.os.Build.VERSION.SDK_INT >= 24){
            return SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(Date())
        }else{
            var tms = Calendar.getInstance()
            return tms.get(Calendar.YEAR).toString() + "-" + tms.get(Calendar.MONTH).toString() + "-" + tms.get(Calendar.DAY_OF_MONTH).toString() + " " + tms.get(Calendar.HOUR_OF_DAY).toString() + ":" + tms.get(Calendar.MINUTE).toString() +":" + tms.get(Calendar.SECOND).toString()
        }

    }
}