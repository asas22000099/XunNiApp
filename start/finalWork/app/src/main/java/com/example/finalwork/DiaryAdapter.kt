package com.example.finalwork

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.finalwork.data.DishDiary
import com.example.finalwork.data.ShopDiary

class ShopDiaryAdapter(val context: DiaryListActivity, val shopDiaryList: ArrayList<ShopDiary>) :
    RecyclerView.Adapter<ShopDiaryAdapter.ViewHolder>(){

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val pushTime: TextView = view.findViewById(R.id.diaryTime)
        val diary: TextView = view.findViewById(R.id.diaryText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShopDiaryAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.diary_item, parent, false)
        val viewHolder = ViewHolder(view)
        return viewHolder
    }

    override fun onBindViewHolder(holder: ShopDiaryAdapter.ViewHolder, position: Int) {
        val diary = shopDiaryList[position]
        holder.pushTime.text = diary.publishTime.toString()
        holder.diary.text = diary.diary
        holder.itemView.setOnClickListener {
            var intent = Intent(context, NewDiaryActivity::class.java)
            intent.putExtra("createNew", false)
            intent.putExtra("index", position)
            intent.putExtra("type", "shop")
            intent.putExtra("id", diary.shopID)
            intent.putExtra("diaryID", diary.id)
            intent.putExtra("diary", diary.diary)
            intent.putExtra("name", context.name)
            intent.putExtra("publishTime", diary.publishTime.toString())
            context.startActivityLauncher.launch(intent)
        }
    }

    override fun getItemCount() = shopDiaryList.size
}

class DishDiaryAdapter(val context: DiaryListActivity, val dishDiaryList: ArrayList<DishDiary>) :
    RecyclerView.Adapter<DishDiaryAdapter.ViewHolder>(){

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val pushTime: TextView = view.findViewById(R.id.diaryTime)
        val diary: TextView = view.findViewById(R.id.diaryText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DishDiaryAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.diary_item, parent, false)
        val viewHolder = ViewHolder(view)
        return viewHolder
    }

    override fun onBindViewHolder(holder: DishDiaryAdapter.ViewHolder, position: Int) {
        val diary = dishDiaryList[position]
        holder.pushTime.text = diary.publishTime.toString()
        holder.diary.text = diary.diary
        holder.itemView.setOnClickListener {
            var intent = Intent(context, NewDiaryActivity::class.java)
            intent.putExtra("createNew", false)
            intent.putExtra("index", position)
            intent.putExtra("type", "dish")
            intent.putExtra("id", diary.dishID)
            intent.putExtra("diaryID", diary.id)
            intent.putExtra("diary", diary.diary)
            intent.putExtra("publishTime", diary.publishTime)
            intent.putExtra("name", context.name)
            context.startActivityLauncher.launch(intent)
        }
    }

    override fun getItemCount() = dishDiaryList.size
}