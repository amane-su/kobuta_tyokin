package com.example.tyokin

import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import com.example.tyokin.Bank.Goalmoney
import com.example.tyokin.databinding.ActivityTitleBinding

class TitleActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTitleBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Bankの初期化を行う
        Bank.init(this)
        PigState.init(this)

        binding = ActivityTitleBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
            val intent = if (Goalmoney == 0) {
                Intent(this, Setgoal::class.java) // Goalmoneyが0.0ならSetgoalへ遷移
            } else {
                Intent(this, MainActivity::class.java) // それ以外はMainActivityへ遷移
            }

            startActivity(intent) // アクティビティを開始
            return true

    }
}