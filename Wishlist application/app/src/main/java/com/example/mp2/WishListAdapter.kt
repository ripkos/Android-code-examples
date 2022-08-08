package com.example.mp2

import android.annotation.SuppressLint
import android.graphics.ImageDecoder
import android.net.Uri
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mp2.data.Wish
import com.example.mp2.data.WishDao
import com.example.mp2.data.WishDatabase
import com.example.mp2.databinding.FragmentWishListBinding
import com.example.mp2.databinding.WishItemBinding
import java.lang.Exception
import kotlin.concurrent.thread

interface IWishClickListener {
    fun onWishClick(id: Int)
}

class WishListViewHolder(val binding: WishItemBinding, val listener: IWishClickListener) :
    RecyclerView.ViewHolder(binding.root), View.OnClickListener {
    override fun onClick(p0: View?) {
        listener.onWishClick(binding.wish!!.id)
    }
    fun bind(wish: Wish) {
        val resolver = binding.root.context.contentResolver
        val uri = Uri.parse(wish.imgPath)
        try{
            binding.image.setImageBitmap(ImageDecoder.decodeBitmap(ImageDecoder.createSource(resolver,uri)))
        }
        catch (e: Exception) {
            binding.image.setImageResource(R.drawable.not_found)
        }

        itemView.setOnClickListener(this)
    }
}

class WishAdapter(private val wishListener: IWishClickListener) :
    RecyclerView.Adapter<WishListViewHolder>() {
    private val data = mutableListOf<Wish>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WishListViewHolder {
        val binding = WishItemBinding.inflate(
            LayoutInflater.from(
                parent.context
            ), parent, false
        )
        return WishListViewHolder(binding, wishListener)
    }


    override fun onBindViewHolder(holder: WishListViewHolder, position: Int) {
        holder.binding.wish = data[position]
        holder.binding.location.setText(
            Wish.getAddressName(
                holder.binding.root.context,
                data[position].getLatLng()
            )
        )
        holder.bind(data[position])
    }

    override fun getItemCount(): Int = data.size

    @SuppressLint("NotifyDataSetChanged")
    fun replace() {

        thread {
            val newData = WishDatabase.db().wishDao().getAll()
            data.clear()
            data.addAll(newData)
        }


        notifyDataSetChanged()
    }


}