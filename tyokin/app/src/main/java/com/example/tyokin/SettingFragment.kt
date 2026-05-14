package com.example.tyokin

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.Switch
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

class SettingFragment : Fragment() {

    private val PREFS_NAME = "settings_preferences" // SharedPreferencesのファイル名
    private val SWITCH_KEY = "switch_state" // Switchの状態を保存するキー

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_setting, container, false)

        // SharedPreferencesの取得
        val sharedPreferences = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val isSwitchOn = sharedPreferences.getBoolean(SWITCH_KEY, true) // デフォルトはtrue

        // Switchの初期化
        val switch = view.findViewById<Switch>(R.id.Switch)
        switch.isChecked = isSwitchOn

        // Switchの変更リスナーを設定
        switch.setOnCheckedChangeListener { _, isChecked ->
            // 状態を保存
            with(sharedPreferences.edit()) {
                putBoolean(SWITCH_KEY, isChecked)
                apply()
            }

            // ON/OFF時の処理
            if (isChecked) {
                Bank.notification = 1// ON時の処理
            } else {
                Bank.notification = 0// OFF時の処理
            }
        }


        val backButton = view.findViewById<ImageButton>(R.id.backButton)
        val goalButton = view.findViewById<Button>(R.id.goalButton)
        val nameButton = view.findViewById<Button>(R.id.nameButton)

        backButton.setOnClickListener {onReturnButtonTapped(it)}
        goalButton.setOnClickListener {onGoalButtonTapped(it)}
        nameButton.setOnClickListener {onNameButtonTapped(it)}

        return view
    }

    fun onReturnButtonTapped(view: View?){
        activity?.supportFragmentManager?.popBackStack(
            null,
            FragmentManager.POP_BACK_STACK_INCLUSIVE
        )
        // OpenGL表示
        val fragmentManager = activity?.supportFragmentManager
        if(fragmentManager != null){
            val transaction = fragmentManager.beginTransaction()
            if(activity != null){
                val glfragment = requireActivity().supportFragmentManager.findFragmentById(R.id.kobuta) as? OpenGLFragment
                if (glfragment != null) {
                    // フラグメントにアクセスできた場合の処理
                    transaction.show(glfragment) // フラグメントを非表示にする
                    transaction.commit()
                }
            }
        }
    }

    fun onGoalButtonTapped(view: View?) {
        Bank.move = 1
        val intent = Intent(requireContext(), Setgoal::class.java)
        startActivity(intent)
    }

    fun onNameButtonTapped(view: View?) {
        val intent = Intent(requireContext(), Setname::class.java)
        startActivity(intent)
    }
}