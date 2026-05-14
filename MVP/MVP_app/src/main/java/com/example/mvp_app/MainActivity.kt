package com.example.mvp_app

import android.icu.text.DateTimePatternGenerator.PatternInfo.OK
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import androidx.preference.PreferenceManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.mvp_app.databinding.ActivityMainBinding
import java.security.AccessController.getContext


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val pref = PreferenceManager.getDefaultSharedPreferences(this)

        // 目標金額を設定
        val goalValue = pref.getString("GOALMONEY", "")
        binding.goalButton.setOnClickListener{ onGoalTapped() }

        //　現在の貯金額を設定
        val allValue = pref.getString("ALLMONEY", "")

        binding.goalValue.setText(goalValue)

        binding.allmoney.setText(allValue)
        binding.haveMoneyButton.setOnClickListener { onBalanceTapped()}

    }

    private fun onGoalTapped(){
        val pref = PreferenceManager.getDefaultSharedPreferences(this)

        binding.goalValue.isEnabled = true
        binding.goalValue.requestFocus()

        return
    }

    private fun onBalanceTapped(){
        val pref = PreferenceManager.getDefaultSharedPreferences(this)

        binding.allmoney.isEnabled = true
        binding.allmoney.requestFocus()

    }

}