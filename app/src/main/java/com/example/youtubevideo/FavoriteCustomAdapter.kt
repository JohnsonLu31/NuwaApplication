package com.example.youtubevideo

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.nuwaapplication.R

class FavoriteCustomAdapter(private var activity: Activity, private var context: Context, private var book_id: ArrayList<String>, private var book_title: ArrayList<String>, private var book_author: ArrayList<String>, private var book_pages: ArrayList<String>): RecyclerView.Adapter<FavoriteCustomAdapter.MyViewHolder>() {


    @SuppressLint("InflateParams")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.youtubevideo_my_row, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.book_title_txt.text = book_title[position]
        holder.book_author_txt.text = book_author[position]
        holder.book_pages_txt.text = book_pages[position]

        //short click
        shortClickAction(holder, position)

        //long click
        longClickAction(holder, position)
    }

    override fun getItemCount(): Int {
        return book_id.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var book_title_txt: TextView = itemView.findViewById(R.id.book_title_txt)
        var book_author_txt: TextView = itemView.findViewById(R.id.book_author_txt)
        var book_pages_txt: TextView = itemView.findViewById(R.id.book_pages_txt)
        var mainLayout: LinearLayout = itemView.findViewById(R.id.mainLayout)
    }

    private fun longClickAction(holder: MyViewHolder, position: Int) {
        val myDB = FavoriteMyDatabaseHelper(context)
        holder.mainLayout.setOnLongClickListener { v ->
            val popup = PopupMenu(context, v)
            popup.inflate(R.menu.popup_menu)
            popup.show()
            popup.setOnMenuItemClickListener(object: PopupMenu.OnMenuItemClickListener{
                override fun onMenuItemClick(item: MenuItem): Boolean {
                    when (item.itemId) {
                        R.id.add_to_favorite -> myDB.addBook(book_title[position].trim(), book_author[position].trim(), book_pages[position].trim())
                        R.id.delete -> confirmDialog(activity, position)
                    }
                    return false
                }

            })
            false
        }
    }

    private fun shortClickAction(holder: MyViewHolder, position: Int) {
        holder.mainLayout.setOnClickListener {
            val intent = Intent(context, FavoriteYoutubeActivity::class.java)
            intent.putExtra("id", book_id[position])
            intent.putExtra("title", book_title[position])
            intent.putExtra("author", book_author[position])
            intent.putExtra("pages", book_pages[position])
            activity.startActivityForResult(intent, 1)
        }
    }

    fun confirmDialog(activity: Activity, position: Int) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Delete ${book_title[position]} ?")
        builder.setMessage("Are you sure you want to delete ${book_title[position]} ?")
        builder.setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, which ->
            val myDB = FavoriteMyDatabaseHelper(context)
            myDB.deleteOneRow(book_id[position])
            Log.d("confirm ID", "ID -> ${book_id[position]}")
            activity.recreate()
        })
        builder.setNegativeButton("No", DialogInterface.OnClickListener { dialog, which ->

        })
        builder.create().show()
    }
}


