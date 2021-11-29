package com.example.youtubevideo

import android.app.AlertDialog
import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.nuwaapplication.R

class UpdateActivity : AppCompatActivity() {

    private lateinit var title_input: EditText
    private lateinit var author_input: EditText
    private lateinit var pages_input: EditText
    private lateinit var update_button: Button

    lateinit var id: String
    lateinit var title: String
    lateinit var author: String
    lateinit var pages: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_youtubevideo_update)

        title_input = findViewById(R.id.title_input2)
        author_input = findViewById(R.id.author_input2)
        pages_input = findViewById(R.id.pages_input2)
        update_button = findViewById(R.id.update_button)

        //First we call this
        getAndSetIntentData()

        //Set actionbar title after getAndSetIntentData method
        supportActionBar?.title = "$title Data"


        update_button.setOnClickListener {
            //And only then we call this
            val myDB = MyDatabaseHelper(this)

            title = title_input.text.toString().trim()
            author = author_input.text.toString().trim()
            pages = pages_input.text.toString().trim()

            myDB.updateData(id, title, author, pages)
        }


    }

    private fun getAndSetIntentData() {
        if (intent.hasExtra("id") && intent.hasExtra("title") && intent.hasExtra("author") && intent.hasExtra("pages")) {

            //Getting Data from Intent
            id = intent.getStringExtra("id").toString()
            title = intent.getStringExtra("title").toString()
            author = intent.getStringExtra("author").toString()
            pages = intent.getStringExtra("pages").toString()

            //Setting Intent Data
            title_input.setText(title)
            author_input.setText(author)
            pages_input.setText(pages)
        } else {
            Toast.makeText(this, "No data.", Toast.LENGTH_SHORT).show()
        }
    }
    
    /*fun confirmDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Delete $title ?")
        builder.setMessage("Are you sure you want to delete $title ?")
        builder.setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, which ->
            val myDB = MyDatabaseHelper(this)
            myDB.deleteOneRow(id)
            finish()
        })
        builder.setNegativeButton("No", DialogInterface.OnClickListener { dialog, which ->

        })
        builder.create().show()
    }*/

    /*fun confirmDialogFav() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Delete $title ?")
        builder.setMessage("Are you sure you want to delete $title ?")
        builder.setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, which ->
            val myDB = FavoriteMyDatabaseHelper(this)
            myDB.deleteOneRow(id)
            finish()
        })
        builder.setNegativeButton("No", DialogInterface.OnClickListener { dialog, which ->

        })
        builder.create().show()
    }*/
}