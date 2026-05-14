package com.example.tyokin

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.tyokin.databinding.ActivitySuccessBinding

class SuccessActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySuccessBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySuccessBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.goal.text = "${Bank.Goalmoney}"

        binding.nextGoalButton.setOnClickListener {
            onButtonTapped(it)
            reset()
        }
    }

    fun onButtonTapped(view: View?){
        Bank.move = 0
        var intent = Intent(this, Setgoal::class.java)
        startActivity((intent))
        finish()
    }

    private fun reset(){
        Bank.Savemoney = 0
        Bank.Goalmoney = 0
        Bank.kobutaName = ""
    }
}