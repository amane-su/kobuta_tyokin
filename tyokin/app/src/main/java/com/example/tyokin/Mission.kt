package com.example.tyokin

class Mission (
    val name: String,
    val description: String,
    val progress: Int,
    var isButtonEnabled: Boolean = progress >= 100 // ボタンの有効状態
    )