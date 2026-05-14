package com.example.tyokin

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.example.gardenapp.ImageDisplayFragment
import com.example.tyokin.databinding.ActivityGardenBinding
import java.time.LocalDateTime


class GardenActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGardenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGardenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        binding.itemButton.setOnClickListener {
            val intent = Intent(this, ItemActivity::class.java)
            startActivity(intent)
        }

        val fragment = getSeasonalTime()
        supportFragmentManager.commit {
            replace(R.id.fragment_container, fragment)
        }

        if (PigState.SaveComplete == 1){
        }
        else{
            // OpenGL非表示
            val fragmentManager = supportFragmentManager
            val transaction = fragmentManager.beginTransaction()
            val glfragment = supportFragmentManager.findFragmentById(R.id.kobutaGarden) as? OpenGLFragmentGarden
            if (glfragment != null) {
                // フラグメントにアクセスできた場合の処理
                transaction.hide(glfragment) // フラグメントを非表示にする
                transaction.commit()
            }
        }
    }
}

@SuppressLint("NewApi")
fun getSeasonalTime(): ImageDisplayFragment {
    val now = LocalDateTime.now()
    val month = now.monthValue // MMの値 (1-12)
    val hour = now.hour        // HHの値 (0-23)

    val season = when (month) {
        in 3..5 -> "spring"   // 3月〜5月
        in 6..8 -> "summer"   // 6月〜8月
        in 9..11 -> "autumn"  // 9月〜11月
        else -> "winter"      // 12月〜2月
    }

    val timeOfDay = when (hour) {
        in 5..10 -> "morning"    // 5時〜10時
        in 11..17 -> "noon"   // 11時〜17時
        else -> "night"        // 19時〜翌4時
    }

    val imageResource = when {
        season == "spring" && timeOfDay == "morning" -> R.drawable.spring_morning
        season == "spring" && timeOfDay == "noon" -> R.drawable.spring_noon
        season == "spring" && timeOfDay == "night" -> R.drawable.spring_night
        season == "summer" && timeOfDay == "morning" -> R.drawable.summer_morning
        season == "summer" && timeOfDay == "noon" -> R.drawable.summer_noon
        season == "summer" && timeOfDay == "night" -> R.drawable.summer_night
        season == "autumn" && timeOfDay == "morning" -> R.drawable.autumn_morning
        season == "autumn" && timeOfDay == "noon" -> R.drawable.autumn_noon
        season == "autumn" && timeOfDay == "night" -> R.drawable.autumn_night
        season == "winter" && timeOfDay == "morning" -> R.drawable.winter_morning
        season == "winter" && timeOfDay == "noon" -> R.drawable.winter_noon
        else -> R.drawable.winter_night
    }
    return ImageDisplayFragment.newInstance(imageResource)

}



