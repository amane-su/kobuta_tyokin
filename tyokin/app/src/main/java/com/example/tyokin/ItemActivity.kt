package com.example.tyokin

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.tyokin.databinding.ActivityItemBinding


class ItemActivity : AppCompatActivity(), ItemDialogFragment.ItemDialogListener {
    private lateinit var binding: ActivityItemBinding

    var itemhave = arrayOf(true,true,false,false,false,false,false,false,false,false,false,false)
    var itemnumber = 0
    var itemwear = PigState.PigCostume
    val itemNames = arrayOf(
        "オリジナル", "パンツ", "ゼッケン", "ボーダー",
        "腹巻き", "囚人服", "日焼け", "レインボー",
        "スイカ", "ロボット", "白塗り", "イベリコ豚"
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityItemBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 保存データのロード
        loadItemData()
        updateItemUI()

        if (itemhave[0] == true){
            binding.item1.setImageResource(R.drawable.icon_original)
        } else {
            binding.item1.setImageResource(android.R.drawable.ic_lock_lock)
        }

        if (itemhave[1] == true){
            binding.item2.setImageResource(R.drawable.icon_pants)
        } else {
            binding.item2.setImageResource(R.drawable.icon_pants_dark)
        }

        if (itemhave[2] == true){
            binding.item3.setImageResource(R.drawable.icon_zekken)
        } else {
            binding.item3.setImageResource(R.drawable.icon_zekken_dark)
        }

        if (itemhave[3] == true){
            binding.item4.setImageResource(R.drawable.icon_border)
        } else {
            binding.item4.setImageResource(R.drawable.icon_border_dark)
        }

        if (itemhave[4] == true){
            binding.item5.setImageResource(R.drawable.icon_haramaki)
        } else {
            binding.item5.setImageResource(R.drawable.icon_haramaki_dark)
        }

        if (itemhave[5] == true){
            binding.item6.setImageResource(R.drawable.icon_prisoner)
        } else {
            binding.item6.setImageResource(R.drawable.icon_prisoner_dark)
        }

        if (itemhave[6] == true){
            binding.item7.setImageResource(R.drawable.icon_sun)
        } else {
            binding.item7.setImageResource(R.drawable.icon_sun_dark)
        }

        if (itemhave[7] == true){
            binding.item8.setImageResource(R.drawable.icon_rainbow)
        } else {
            binding.item8.setImageResource(R.drawable.icon_rainbow_dark)
        }

        if (itemhave[8] == true){
            binding.item9.setImageResource(R.drawable.icon_watermelon)
        } else {
            binding.item9.setImageResource(R.drawable.icon_watermelon_dark)
        }

        if (itemhave[9] == true){
            binding.item10.setImageResource(R.drawable.icon_robot)
        } else {
            binding.item10.setImageResource(R.drawable.icon_robot_dark)
        }

        if (itemhave[10] == true){
            binding.item11.setImageResource(R.drawable.icon_white)
        } else {
            binding.item11.setImageResource(R.drawable.icon_white_dark)
        }

        if (itemhave[11] == true){
            binding.item12.setImageResource(R.drawable.icon_iberiko)
        } else {
            binding.item12.setImageResource(R.drawable.icon_iberiko_dark)
        }

        binding.returnButton.setOnClickListener { onReturnButtonTapped(it) }

        binding.item1.setOnClickListener{
            itemnumber = 0
            onItemButtonTapped(it)
        }

        binding.item2.setOnClickListener{
            itemnumber = 1
            onItemButtonTapped(it)
        }

        binding.item3.setOnClickListener{
            itemnumber = 2
            onItemButtonTapped(it)
        }

        binding.item4.setOnClickListener{
            itemnumber = 3
            onItemButtonTapped(it)
        }

        binding.item5.setOnClickListener{
            itemnumber = 4
            onItemButtonTapped(it)
        }

        binding.item6.setOnClickListener{
            itemnumber = 5
            onItemButtonTapped(it)
        }

        binding.item7.setOnClickListener{
            itemnumber = 6
            onItemButtonTapped(it)
        }

        binding.item8.setOnClickListener{
            itemnumber = 7
            onItemButtonTapped(it)
        }

        binding.item9.setOnClickListener{
            itemnumber = 8
            onItemButtonTapped(it)
        }

        binding.item10.setOnClickListener{
            itemnumber = 9
            onItemButtonTapped(it)
        }

        binding.item11.setOnClickListener{
            itemnumber = 10
            onItemButtonTapped(it)
        }

        binding.item12.setOnClickListener{
            itemnumber = 11
            onItemButtonTapped(it)
        }
    }

    fun onReturnButtonTapped(view: View?) {
        // コスチュームの更新
        PigState.PigCostume = itemwear

        saveItemData()

        var intent = Intent(this, MainActivity::class.java)
        startActivity((intent))
    }

    fun onItemButtonTapped(view: View?){
        if(itemhave[itemnumber] == true) {
            if(itemwear != itemnumber) {
                val itemName = itemNames[itemnumber]
                ItemDialogFragment.newInstance(itemName, itemnumber)
                    .show(supportFragmentManager, "itemDialog")
                saveItemData()
            }
        } else if(itemhave[itemnumber] == false){
            val itemName = itemNames[itemnumber]
            ItemDialogFragment2.newInstance(itemName,itemnumber)
                .show(supportFragmentManager, "itemDialog")
            saveItemData()
        }
    }

    override fun onItemSelected(itemNumber: Int) {
        itemwear = itemNumber
        // 必要ならUIの更新処理をここに書く
    }

    fun updateItemUI() {
        val itemResources = arrayOf(
            R.drawable.icon_original, R.drawable.icon_pants, R.drawable.icon_zekken,
            R.drawable.icon_border, R.drawable.icon_haramaki, R.drawable.icon_prisoner,
            R.drawable.icon_sun, R.drawable.icon_rainbow, R.drawable.icon_watermelon,
            R.drawable.icon_robot, R.drawable.icon_white, R.drawable.icon_iberiko
        )
        val lockedIcon = arrayOf(
            R.drawable.icon_original, R.drawable.icon_pants_dark, R.drawable.icon_zekken_dark,
            R.drawable.icon_border_dark, R.drawable.icon_haramaki_dark, R.drawable.icon_prisoner_dark,
            R.drawable.icon_sun_dark, R.drawable.icon_rainbow_dark, R.drawable.icon_watermelon_dark,
            R.drawable.icon_robot_dark, R.drawable.icon_white_dark, R.drawable.icon_iberiko_dark
        )

        for (i in itemhave.indices) {
            val itemImageView = when (i) {
                0 -> binding.item1
                1 -> binding.item2
                2 -> binding.item3
                3 -> binding.item4
                4 -> binding.item5
                5 -> binding.item6
                6 -> binding.item7
                7 -> binding.item8
                8 -> binding.item9
                9 -> binding.item10
                10 -> binding.item11
                11 -> binding.item12
                else -> null
            }
            itemImageView?.setImageResource(if (itemhave[i]) itemResources[i] else lockedIcon[i])

        }
    }

    private fun saveItemData() {
        val sharedPreferences = getSharedPreferences("ItemData", MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        for (i in itemhave.indices) {
            editor.putBoolean("item_$i", itemhave[i])
        }

        editor.putInt("itemwear", itemwear)
        editor.apply() // 保存を適用
    }

    private fun loadItemData() {
        val sharedPreferences = getSharedPreferences("ItemData", MODE_PRIVATE)

        // itemhave 配列をロード
        for (i in itemhave.indices) {
            itemhave[i] = sharedPreferences.getBoolean("item_$i", false)
        }
        itemhave[0] = true
        itemwear = sharedPreferences.getInt("itemwear", 0)
    }

}