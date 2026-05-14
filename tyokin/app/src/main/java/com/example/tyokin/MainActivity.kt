package com.example.tyokin

import NotificationWorker
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.media.SoundPool
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.Gravity
import android.view.View

import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet

import com.example.tyokin.databinding.ActivityMainBinding
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.abs
import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.content.ContextCompat
import java.util.Calendar

import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Data
import java.util.concurrent.TimeUnit
import kotlin.random.Random


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private var soundPool = SoundPool(10, AudioManager.STREAM_MUSIC, 0) // 効果音用
    var soundEffect1 = 0   // 効果音用ハンドル
    var soundEffect2 = 0   // 効果音用ハンドル
    val CHANNEL_ID = "CHANNEL_ID"
    val NOTIFY_ID = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // セリフのリストを用意(リストの最後に預金破綻文)
        val phrases = listOf(
            "貯めないブタはただのブタだブー",
            "大学生の貯金額の平均は\n15,895円/月だブー",
            "10万あったら沖縄旅行ができるブー",
            "3万あったら温泉旅行に行けるブー",
            "お金は裏切らないブー",
            "僕のこと割らないでほしいブー",
            "将来はビッグなブタになるんだブー",
            "貯金頑張るブー",
            "銀行の預金額を超えて貯金されてるブー..."
        )
        // 画面遷移時にランダムなセリフを表示
        displayRandomPhrase(phrases)

        // 許可ダイアログを表示
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        //通知時間設定
        if(Bank.notification == 1) {
            scheduleNotification(15, 0)
        }

        // チャンネルの生成

        val assetsManager = resources.assets
        val inputStream = assetsManager.open("bank.json")
        val bufferedReader = BufferedReader(InputStreamReader(inputStream))
        val jsonStr = bufferedReader.readText()

        var jsonObj = JSONObject(jsonStr)
        var jsonArray = jsonObj.getJSONArray("account_balances")

        Bank.init(this) // Bankの初期化

        //jsonファイルから口座残高情報を持ってくるコードをここに追加
        // JSONデータから最新（最も近い日付）の残高を取得
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val today = Date()
        var closestBalance = 0
        var minDifference = Long.MAX_VALUE

        for (i in 0 until jsonArray.length()) {
            val accountObj = jsonArray.getJSONObject(i)
            val balance = accountObj.getDouble("balance").toInt() // 残高を取得
            val dateStr = accountObj.getString("date") // 日付を取得
            try {
                val accountDate = dateFormat.parse(dateStr) ?: continue
                val difference = abs(today.time - accountDate.time)

                if (difference < minDifference) {
                    minDifference = difference
                    closestBalance = balance
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        Bank.Bankmoney = closestBalance

        updateSaveMoneyText()

        // サウンドプールの設定
        soundEffect1 = soundPool.load(this, R.raw.coin, 1);
        soundEffect2 = soundPool.load(this, R.raw.beep, 1);

        val pref = PreferenceManager.getDefaultSharedPreferences(this)
        loadData()

        // モデルの大きさ変更
        changeModelSize()

        binding.missionButton.setOnClickListener { onMissionButtonTapped(it) }
        binding.settingButton.setOnClickListener { onSettingButtonTapped(it) }
        binding.gardenButton.setOnClickListener { onGardenButtonTapped(it) }
        binding.itemButton.setOnClickListener { onItemButtonTapped(it) }
        binding.detailButton.setOnClickListener { onDetailButtonTapped(it) }
        binding.coinButton10.setOnClickListener { on10CoinButtonTapped(it) }
        binding.coinButton100.setOnClickListener { on100CoinButtonTapped(it) }
        binding.coinButton500.setOnClickListener { on500CoinButtonTapped(it) }
        binding.coinButton1000.setOnClickListener { on1000CoinButtonTapped(it) }

        supportFragmentManager.beginTransaction().apply{
            addToBackStack(null)
            commit()
        }
    }

    private val launcher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { result ->
            // ダイアログの結果で処理を分岐
            if (result) {
                Toast.makeText(this, "通知権限が許可されました", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "通知権限が拒否されました", Toast.LENGTH_SHORT).show()
            }
        }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "チャンネル名"
            val descriptionText = "チャンネルの説明"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // チャンネルをシステムに登録
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    // 通知の作成と送信
    private fun sendNotify() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED) {
                // 権限がある場合に通知を送信
                showNotification()
            } else {
                // 権限をリクエスト
                launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            // Android 13 未満は通知権限不要
            showNotification()
        }
    }

    private fun showNotification() {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("こぶた貯金からのおしらせ")
            .setContentText("通知の本文")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        NotificationManagerCompat.from(this).notify(NOTIFY_ID, builder.build())
        Log.d("NotificationTest", "通知を送信しました")
    }

    //時間で通知を送る設定
    fun scheduleNotification(hour: Int, minute: Int) {
        // 現在時刻とターゲット時刻の差を計算
        val now = Calendar.getInstance()
        val target = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
        }
        if (target.before(now)) {
            target.add(Calendar.DAY_OF_YEAR, 1) // もし現在時刻より過去なら翌日に設定
        }
        val delay = target.timeInMillis - now.timeInMillis

        // WorkManagerのタスクを作成
        val workRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .build()

        // WorkManagerに登録
        WorkManager.getInstance(this).enqueue(workRequest)
    }

    private fun onGoalTapped(){
        val pref = PreferenceManager.getDefaultSharedPreferences(this)

        return
    }

    fun on10CoinButtonTapped(view: View?){
        if(Bank.Bankmoney - Bank.Savemoney < 10){
            playSoundEffect(soundEffect2)
            return
        }
        Bank.Savemoney += 10
        updateSaveMoneyText()
        playSoundEffect(soundEffect1)
        val amount = 10
        SavingsManager.saveDailySavings(this, amount)
        changeModelSize()

        if(Bank.Savemoney >= Bank.Goalmoney)
            screentransition()
    }

    fun on100CoinButtonTapped(view: View?){
        if(Bank.Bankmoney - Bank.Savemoney < 100){
            playSoundEffect(soundEffect2)
            return
        }
        Bank.Savemoney += 100
        updateSaveMoneyText()
        playSoundEffect(soundEffect1)

        val amount = 100
        SavingsManager.saveDailySavings(this, amount)
        changeModelSize()

        if(Bank.Savemoney >= Bank.Goalmoney)
            screentransition()
    }

    fun on500CoinButtonTapped(view: View?){
        if(Bank.Bankmoney - Bank.Savemoney < 500){
            playSoundEffect(soundEffect2)
            return
        }
        Bank.Savemoney += 500
        updateSaveMoneyText()
        playSoundEffect(soundEffect1)

        val amount = 500
        SavingsManager.saveDailySavings(this, amount)
        changeModelSize()

        if(Bank.Savemoney >= Bank.Goalmoney)
            screentransition()
    }

    fun on1000CoinButtonTapped(view: View?){
        if(Bank.Bankmoney - Bank.Savemoney < 1000){
            playSoundEffect(soundEffect2)
            return
        }
        Bank.Savemoney += 1000
        updateSaveMoneyText()
        playSoundEffect(soundEffect1)

        val amount = 1000
        SavingsManager.saveDailySavings(this, amount)
        changeModelSize()

        if(Bank.Savemoney >= Bank.Goalmoney)
            screentransition()
    }

    fun onMissionButtonTapped(view: View?){
        supportFragmentManager.beginTransaction().apply{
            replace(R.id.container, MissionFragment())
            addToBackStack(null)
            commit()
        }
        // OpenGL非表示
        val fragmentManager = supportFragmentManager
        val transaction = fragmentManager.beginTransaction()
        val glfragment = supportFragmentManager.findFragmentById(R.id.kobuta) as? OpenGLFragment
        if (glfragment != null) {
            // フラグメントにアクセスできた場合の処理
            transaction.hide(glfragment) // フラグメントを非表示にする
            transaction.commit()
        }
    }

    fun onSettingButtonTapped(view: View?){
        supportFragmentManager.beginTransaction().apply{
            replace(R.id.container, SettingFragment())
            addToBackStack(null)
            commit()
        }
        // OpenGL非表示
        val fragmentManager = supportFragmentManager
        val transaction = fragmentManager.beginTransaction()
        val glfragment = supportFragmentManager.findFragmentById(R.id.kobuta) as? OpenGLFragment
        if (glfragment != null) {
            // フラグメントにアクセスできた場合の処理
            transaction.hide(glfragment) // フラグメントを非表示にする
            transaction.commit()
        }
    }

    fun onGardenButtonTapped(view: View?){
        var intent = Intent(this, GardenActivity::class.java)
        startActivity((intent))
    }

    fun onItemButtonTapped(view: View?) {
        var intent = Intent(this, ItemActivity::class.java)
        startActivity((intent))
    }

    fun onDetailButtonTapped(view: View?){
        supportFragmentManager.beginTransaction().apply{
            replace(R.id.container, GraphFragment())
            addToBackStack(null)
            commit()
        }
        // OpenGL非表示
        val fragmentManager = supportFragmentManager
        val transaction = fragmentManager.beginTransaction()
        val glfragment = supportFragmentManager.findFragmentById(R.id.kobuta) as? OpenGLFragment
        if (glfragment != null) {
            // フラグメントにアクセスできた場合の処理
            transaction.hide(glfragment) // フラグメントを非表示にする
            transaction.commit()
        }
    }

    private fun updateSaveMoneyText() {
        binding.goalText2.text = "${Bank.Goalmoney}"
        binding.saveMoneyText2.text = "${Bank.Savemoney}/"
        binding.saveBar.progress = ((Bank.Savemoney/ Bank.Goalmoney.toFloat()) * 100).toInt()
        binding.usableMoneyText2.text = "${Bank.usableMoney}円"
        binding.pointText.text = "${Bank.Gamecoin}ポイント"
        binding.nametext.text = "${Bank.kobutaName}"
    }

    private fun loadData() {
        updateSaveMoneyText()
    }

    private fun reset(){
        Bank.Savemoney = 0
        // SharedPreferences の貯金データをリセット
        val pref = PreferenceManager.getDefaultSharedPreferences(this)
        val editor = pref.edit()

        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val today = dateFormat.format(Date())

        // 今日の日付の貯金額のみリセット
        editor.putInt(today, 0)
        editor.apply()

        updateSaveMoneyText()
    }

    // サウンドハンドルを指定し効果音再生
    private fun playSoundEffect(soundEffectHandle : Int){
        soundPool.play(soundEffectHandle, 1.0f, 1.0f, 0, 0, 1.0f)
    }

    // ぶたの大きさを変更する
    private fun changeModelSize(){
        val achieveMoneyRate = Bank.Savemoney / Bank.Goalmoney.toFloat()
        val constRate = 0.4
        val v = findViewById<View>(R.id.kobuta) as View
        // サイズ変更
        v.layoutParams = v.layoutParams.apply {
            width = (2000 * (constRate + (1 - constRate) * achieveMoneyRate)).toInt()
            height = (2000 * (constRate + (1 - constRate) * achieveMoneyRate)).toInt()
        }

        // 親レイアウトがConstraintLayoutの場合
        val parent = v.parent as ConstraintLayout
        val constraintSet = ConstraintSet()
        constraintSet.clone(parent)

        // ConstraintSetを適用
        constraintSet.applyTo(parent)
    }

    object SavingsManager {
        private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        // 日ごとの貯金額を保存
        fun saveDailySavings(context: Context, amount: Int) {
            val pref = PreferenceManager.getDefaultSharedPreferences(context)
            val editor = pref.edit()

            val today = dateFormat.format(Date())
            val allSavings = getAllSavings(context)

            // 今日の累積額（初期値は0）
            val currentTotal = allSavings[today] ?: 0

            // 前日の累積額を取得（初期値は0）
            val previousDate = Calendar.getInstance().apply {
                add(Calendar.DAY_OF_YEAR, -1)
            }.let { dateFormat.format(it.time) }

            val previousTotal = allSavings[previousDate] ?: 0

            // 当日の累積額を計算
            val newTotal = Bank.Savemoney

            // 計算結果を保存
            editor.putInt(today, newTotal)
            editor.apply()
        }

        // 全データを取得
        fun getAllSavings(context: Context): Map<String, Int> {
            val pref = PreferenceManager.getDefaultSharedPreferences(context)
            return pref.all.filterValues { it is Int }
                .mapKeys { it.key }
                .mapValues { it.value as Int }
        }

    }
    // ランダムなセリフを表示する関数
    private fun displayRandomPhrase(phrases: List<String>) {
        var randomPhrase = phrases[Random.nextInt(phrases.size - 1)]
        // 所持金がマイナスなら固定
        if (Bank.Bankmoney - Bank.Savemoney < 0){
            randomPhrase = phrases[8]
        }
        binding.textView.text = randomPhrase
    }

    //貯金達成後の画面遷移
    private fun screentransition(){
        PigState.SaveComplete = 1
        var intent = Intent(this, SuccessActivity::class.java)
        startActivity((intent))
    }
}