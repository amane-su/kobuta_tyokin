package com.example.tyokin
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MissionAdapter(private val context: Context, private val missionList: List<Mission>) :
    RecyclerView.Adapter<MissionAdapter.MissionViewHolder>() {

    private val PREFS_NAME = "mission_preferences"

    class MissionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val missionName: TextView = view.findViewById(R.id.missionName)
        val missionDescription: TextView = view.findViewById(R.id.missionDescription)
        val missionProgress: ProgressBar = view.findViewById(R.id.missionProgress)
        val actionButton: Button = view.findViewById(R.id.button2)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MissionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.mission_type, parent, false)
        return MissionViewHolder(view)
    }

    override fun onBindViewHolder(holder: MissionViewHolder, position: Int) {
        val mission = missionList[position]
        holder.missionName.text = mission.name
        holder.missionDescription.text = mission.description
        holder.missionProgress.progress = mission.progress

        // SharedPreferencesの取得
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val missionKey = "mission_state_$position" // ミッションごとのキー

        // ボタンの有効状態を取得して設定
        val isButtonEnabled = sharedPreferences.getBoolean(missionKey, mission.isButtonEnabled)
        mission.isButtonEnabled = isButtonEnabled // データモデルにも反映
        holder.actionButton.isEnabled = isButtonEnabled
        val yellow = Color.argb(128, 255, 210, 0)
        holder.actionButton.setBackgroundColor(
            if (isButtonEnabled) yellow else Color.GRAY
        )
        holder.actionButton.setTextColor(
            if (isButtonEnabled) Color.BLACK else Color.WHITE
        )

        // ボタンのクリック処理
        holder.actionButton.setOnClickListener {
            if (mission.isButtonEnabled) {
                // ボタンが押された時の処理を実行
                Bank.Gamecoin += 16
                mission.isButtonEnabled = false // ボタンを無効化
                notifyItemChanged(position) // UIを更新

                // SharedPreferencesに状態を保存
                with(sharedPreferences.edit()) {
                    putBoolean(missionKey, false)
                    apply()
                }
            }
        }
    }

    override fun getItemCount(): Int = missionList.size
}