package com.example.tyokin

import android.content.Context
import android.content.SharedPreferences

object Bank {
    private const val PREF_NAME = "bank_preferences"
    private const val KEY_BANK_MONEY = "bank_money"
    private const val KEY_SAVE_MONEY = "save_money"
    private const val KEY_GOAL_MONEY = "goal_money"
    private const val KEY_GAME_COIN = "game_coin"
    private const val KEY_KOBUTA_NAME = "kobuta_name"

    private lateinit var preferences: SharedPreferences

    // 初期化　
    fun init(context: Context) {
        // SharedPreferencesを初期化
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        loadValues()
    }

    // 変数をInt型に変更
    var Bankmoney: Int = 0
        set(value) {
            field = value
            save(KEY_BANK_MONEY, value.toDouble()) // 保存時はDoubleに変換
        }

    var Savemoney: Int = 0
        set(value) {
            field = value
            save(KEY_SAVE_MONEY, value.toDouble())
        }

    var Goalmoney: Int = 0
        set(value) {
            field = value
            save(KEY_GOAL_MONEY, value.toDouble())
        }

    var Gamecoin: Int = 0
        set(value) {
            field = value
            save(KEY_GAME_COIN, value.toDouble())
        }

    var moneyProgress: Double = 0.0
        get() = if (Goalmoney > 0) (Savemoney.toDouble() / Goalmoney)  else 0.0

    var usableMoney: Int = 0
        get() = Bankmoney - Savemoney

    var move: Int = 0 //目標額設定画面から画面遷移するときに使用する変数

    var notification: Int = 1 //通知のON/OFF用の変数

    var kobutaName: String = ""
        set(value) {
            field = value
            preferences.edit()
                .putString(KEY_KOBUTA_NAME, value)
                .apply()
        }

    // 値をSharedPreferencesに保存 (Double型で保存)
    private fun save(key: String, value: Double) {
        preferences.edit()
            .putString(key, value.toString())
            .apply()
    }

    // SharedPreferencesから値を読み込み (読み込み時にInt型に変換)
    private fun loadValues() {
        Bankmoney = preferences.getString(KEY_BANK_MONEY, "0.0")?.toDouble()?.toInt() ?: 0
        Savemoney = preferences.getString(KEY_SAVE_MONEY, "0.0")?.toDouble()?.toInt() ?: 0
        Goalmoney = preferences.getString(KEY_GOAL_MONEY, "0.0")?.toDouble()?.toInt() ?: 0
        Gamecoin = preferences.getString(KEY_GAME_COIN, "0.0")?.toDouble()?.toInt() ?: 0
        kobutaName = preferences.getString(KEY_KOBUTA_NAME, "") ?: ""
    }
}