package com.example.tyokin

import android.content.Context
import android.content.SharedPreferences

object PigState {
    private const val PREF_NAME = "pig_preferences"
    private const val KEY_PIG_COSTUME = "pig_costume"
    private const val SAVE_COMPLETE = "save_complete"   // 貯金が一度でも達成されたかのフラグ　適当

    private lateinit var preferences: SharedPreferences

    // 初期化　
    fun init(context: Context) {
        // SharedPreferencesを初期化
        PigState.preferences = context.getSharedPreferences(PigState.PREF_NAME, Context.MODE_PRIVATE)
        loadValues()
    }

    var PigCostume: Int = 0
        set(value) {
            field = value
            save(KEY_PIG_COSTUME, value.toDouble()) // 保存時はDoubleに変換
        }

    var SaveComplete: Int = 0
        set(value){
            field = value
            save(SAVE_COMPLETE, value.toDouble())
        }

    // 値をSharedPreferencesに保存 (Double型で保存)
    private fun save(key: String, value: Double) {
        PigState.preferences.edit()
            .putString(key, value.toString())
            .apply()
    }

    // SharedPreferencesから値を読み込み (読み込み時にInt型に変換)
    private fun loadValues() {
        PigCostume = PigState.preferences.getString(KEY_PIG_COSTUME, "0.0")?.toDouble()?.toInt() ?: 0
    }
}