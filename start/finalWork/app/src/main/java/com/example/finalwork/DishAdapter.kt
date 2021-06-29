package com.example.finalwork

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.finalwork.data.Dish
import com.example.finalwork.data.Shop
import kotlinx.android.synthetic.main.dish_item.view.*
import java.io.File

class DishAdapter(val context: DishListActivity, val dishList: ArrayList<Dish>) :
    RecyclerView.Adapter<DishAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val dishName: TextView = view.findViewById(R.id.dishName)
        val dishInfo: TextView = view.findViewById(R.id.dishInfo)
        val dishType: TextView = view.findViewById(R.id.dishType)
        val dishHasEaten: TextView = view.findViewById(R.id.dishHasEaten)
        val dishPicture: ImageView = view.findViewById(R.id.dishPicture)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.dish_item, parent, false)
        val viewHolder = ViewHolder(view)
        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val dish = dishList[position]
        holder.dishName.text = dish.name
        holder.dishInfo.text = dish.information

        var changeEaten = 0

        val types = context.resources.getStringArray(R.array.dish_type)
        val eaten = context.resources.getStringArray(R.array.dish_has_eaten)

        holder.dishType.text = types[dish.type]
        holder.dishHasEaten.text = eaten[dish.hasEaten]

        if(dish.hasEaten == 0) {
            holder.itemView.changeEaten.setText("取消打卡")
            changeEaten = 1
        }

        if(dish.picture != "") {
            holder.dishPicture.visibility = View.VISIBLE
            holder.dishPicture.setImageURI(Uri.fromFile(File(dish.picture)))
        }
        else {
            holder.dishPicture.visibility = View.GONE
        }

        holder.itemView.enterDishDiary.setOnClickListener {
            val intent = Intent(context, DiaryListActivity::class.java)
            intent.putExtra("id", dishList[position].id)
            intent.putExtra("type", "dish")
            intent.putExtra("name", dish.name)
            context.startActivity(intent)
        }

        holder.itemView.changeEaten.setOnClickListener {
            if (changeEaten == 0) {
                Toast.makeText(context, "打卡成功~", Toast.LENGTH_LONG).show()
            }
            else {
                Toast.makeText(context, "取消打卡", Toast.LENGTH_LONG).show()
            }
            context.updateEaten(changeEaten, dishList[position].id, position)
        }

        holder.itemView.modifyDish.setOnClickListener {
            var intent = Intent(context, NewDishActivity::class.java)
            intent.putExtra("createNew", false)
            intent.putExtra("dishID", dish.id)
            intent.putExtra("name", dish.name)
            intent.putExtra("info", dish.information)
            intent.putExtra("type", dish.type)
            intent.putExtra("hasEaten", dish.hasEaten)
            intent.putExtra("picture", dish.picture)
            intent.putExtra("index", position)
            context.startActivityLauncher.launch(intent)
        }

        holder.itemView.deleteDish.setOnClickListener {
            AlertDialog.Builder(context)
                    .setTitle("删除菜品")
                    .setMessage("确定要删除该菜品吗？与菜品相关联的所有日记都会被删除哦。")
                    .setPositiveButton("确定", DialogInterface.OnClickListener { dialog, which ->
                        context.deleteDish(position)
                        Toast.makeText(context, "菜品已删除", Toast.LENGTH_LONG).show()
                    })
                    .setNegativeButton("取消", DialogInterface.OnClickListener { dialog, which ->
                        Toast.makeText(context, "取消删除操作", Toast.LENGTH_LONG).show()
                    })
                    .show()
        }

        holder.itemView.setOnClickListener {
            var intent = Intent(context, DishItemActivity::class.java)
            intent.putExtra("dishItem", dish)
            intent.putExtra("position", position)
            context.startDishItemLauncher.launch(intent,
                ActivityOptionsCompat.makeSceneTransitionAnimation(context, holder.dishPicture, "dishPicture"))
        }

    }

    override fun getItemCount() = dishList.size
}