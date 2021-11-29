package com.example.youtubevideo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.example.nuwaapplication.R

class AddActivity : AppCompatActivity() {

    lateinit var title_input: EditText
    lateinit var author_input: EditText
    lateinit var pages_input: EditText
    lateinit var add_button: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_youtubevideo_add)

        title_input = findViewById(R.id.title_input)
        author_input = findViewById(R.id.author_input)
        pages_input = findViewById(R.id.pages_input)
        add_button = findViewById(R.id.add_button)

        add_button.setOnClickListener {
            val myDB = MyDatabaseHelper(this)
            myDB.addBook(title_input.text.toString().trim(), author_input.text.toString().trim(), pages_input.text.toString().trim())
            val intent = Intent(this, YoututbevideoMainActivity::class.java)
            startActivity(intent)
        }
    }
}