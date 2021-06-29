package com.example.finalwork

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.finalwork.data.Shop
import com.example.finalwork.data.ShopViewModel
import kotlinx.android.synthetic.main.shop_item.view.*
import java.io.File
import java.net.URI
import java.sql.Time

//这是之前版本的

class ShopAdapter(val context: ShowListActivity, val shopList: ArrayList<Shop>) :
    RecyclerView.Adapter<ShopAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val shopName: TextView = view.findViewById(R.id.shopName)
        val shopInfo: TextView = view.findViewById(R.id.shopInfo)
        val shopType: TextView = view.findViewById(R.id.shopType)
        val shopLabel: TextView = view.findViewById(R.id.shopLabel)
        val shopPicture: ImageView = view.findViewById(R.id.shopPicture)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.shop_item, parent, false)
        val viewHolder = ViewHolder(view)
        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val shop = shopList[position]
        holder.shopName.text = shop.name
        holder.shopInfo.text = shop.information

        val types = context.resources.getStringArray(R.array.shop_type)
        val labels = context.resources.getStringArray(R.array.shop_label)

//        val types = arrayOf("外卖", "堂食", "均可")
//        val labels = arrayOf("早餐", "早午餐", "正餐", "下午茶", "宵夜", "零食")

        holder.shopType.text = types[shop.type]
        holder.shopLabel.text = labels[shop.label]

        if(shop.picture != "") {
            holder.shopPicture.visibility = VISIBLE
            holder.shopPicture.setImageURI(Uri.fromFile(File(shop.picture)))
        }
        else {
            holder.shopPicture.visibility = GONE
        }

        holder.itemView.enterShopDiary.setOnClickListener {
            val intent = Intent(context, DiaryListActivity::class.java)
            intent.putExtra("id", shopList[position].id)
            intent.putExtra("type", "shop")
            intent.putExtra("name", shop.name)
            context.startActivity(intent)
        }

        holder.itemView.enterDish.setOnClickListener {
            val intent = Intent(context, DishListActivity::class.java)
            intent.putExtra("shopID", shopList[position].id)
            intent.putExtra("name", shop.name)
            context.startActivity(intent)
        }

        //TODO: 2.3: create update shop info


        //TODO: 3.2 delete shop info


        holder.itemView.setOnClickListener {
            var intent = Intent(context, ShopItemActivity::class.java)
            intent.putExtra("shopItem", shop)
            intent.putExtra("position", position)
            context.startShopItemLauncher.launch(intent,
                ActivityOptionsCompat.makeSceneTransitionAnimation(context, holder.shopPicture, "shopPicture"))
        }
    }

    override fun getItemCount() = shopList.size

}
