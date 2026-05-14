package com.example.tyokin

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.tyokin.databinding.ActivitySetgoalBinding

class Setgoal : AppCompatActivity() {
    private lateinit var binding: ActivitySetgoalBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetgoalBinding.inflate(layoutInflater) // 初期化
        setContentView(binding.root)

        binding.button.setOnClickListener {
            // エディットテキストのテキストを取得
            val inputText = binding.editText.text?.toString()
            val goalMoney = inputText?.toIntOrNull()

            if ((goalMoney != null && goalMoney > 0) && goalMoney > Bank.Savemoney) {
                // 数値型に変換
                Bank.Goalmoney = goalMoney
                onButtonTapped( it )
            } else if(goalMoney != null && goalMoney < Bank.Savemoney) {
                binding.editText.error = "${Bank.Savemoney}(現在の貯金額)より大きい金額を入力してください"
            }else {
                binding.editText.error = "有効な金額を入力してください"
            }
        }
    }

    fun onButtonTapped(view: View?){
        if(Bank.move == 0) {
            var intent = Intent(this, Setname::class.java)
            startActivity((intent))
        } else if(Bank.move == 1) {
            var intent = Intent(this, MainActivity::class.java)
            startActivity((intent))
        }
        Bank.move = 0
        finish()
    }

}