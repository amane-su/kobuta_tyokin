package com.example.mvp_app

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.icu.text.DateTimePatternGenerator.PatternInfo.OK
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import androidx.preference.PreferenceManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
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
        val goalValue = pref.getString("GOALMONEY", "0")
        binding.goalButton.setOnClickListener{ onGoalTapped() }

        binding.openGL.setOnClickListener{ onOpenGLTapped() }


        //　現在の貯金額を設定
        val allValue = pref.getString("ALLMONEY", "")

        binding.goalValue.text = goalValue

        binding.allmoney.setText(allValue)
        binding.haveMoneyButton.setOnClickListener { onBalanceTapped()}

    }

    private fun onGoalTapped(){
        // 目標額ボタンが押されたとき
        binding.missionButton.setOnClickListener { onMissionButtonTapped(it) }
        binding.settingButton.setOnClickListener { onSettingButtonTapped(it) }
        binding.gardenButton.setOnClickListener { onGardenButtonTapped(it) }
        binding.itemButton.setOnClickListener { onItemButtonTapped(it) }
        binding.detailButton.setOnClickListener { onDetailButtonTapped(it) }
    }

    private fun onGoalTapped() {
        val pref = PreferenceManager.getDefaultSharedPreferences(this)
        val editor = pref.edit()

        val editText = AppCompatEditText(this)
        editText.inputType.dec()

        // ダイアログの作成
        val dialog = AlertDialog.Builder(this)
            .setTitle("目標額をいれてね！")
            .setMessage("目標貯金額")
            .setView(editText)
            .setPositiveButton("OK") { dialog, _ ->
                // OKボタンを押したときの処理
                editor.putInt("GOALMONEY", editText.text.toString().toInt())
                binding.goalValue.text = editText.text
                dialog.dismiss()
            }
            .setNegativeButton("キャンセル") { dialog, _ ->
                // キャンセルボタンを押したときの処理
                dialog.dismiss()
            }
            .create()

        editText.addTextChangedListener( object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                // 1~32文字の時だけOKボタンを有効化する
                if (s.isNullOrEmpty() || s.length > 32) {
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = false
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getColor(R.color.gray))
                } else {
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = true
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getColor(R.color.black))
                }
            }
        })
        dialog.show()
    }

    private fun onOpenGLTapped(){
        val intent = Intent(this, OpenGL::class.java)
        startActivity(intent)
    }


    private fun onBalanceTapped(){
        val pref = PreferenceManager.getDefaultSharedPreferences(this)

        binding.allmoney.isEnabled = true
        binding.allmoney.requestFocus()

    }

    fun onMissionButtonTapped(view: View?) {
        var intent = Intent(this, MissionActivity::class.java)
        startActivity((intent))
    }

    fun onSettingButtonTapped(view: View?) {
        var intent = Intent(this, SettingActivity::class.java)
        startActivity((intent))
    }

    fun onGardenButtonTapped(view: View?) {
        var intent = Intent(this, GardenActivity::class.java)
        startActivity((intent))
    }

    fun onItemButtonTapped(view: View?) {
        var intent = Intent(this, ItemActivity::class.java)
        startActivity((intent))
    }

    fun onDetailButtonTapped(view: View?) {
        var intent = Intent(this, DetailActivity::class.java)
        startActivity((intent))
    }
}

