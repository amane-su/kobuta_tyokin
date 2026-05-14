package com.example.tyokin

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.tyokin.databinding.ActivitySetnameBinding

class Setname : AppCompatActivity() {
    private lateinit var binding: ActivitySetnameBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetnameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.button.setOnClickListener {
            // エディットテキストのテキストを取得
            val inputname = binding.editText.text?.toString()

            Bank.kobutaName = inputname.toString()

            onButtonTapped( it )
        }
    }

    fun onButtonTapped(view: View?){
        var intent = Intent(this, MainActivity::class.java)
        startActivity((intent))
        finish()
    }
}