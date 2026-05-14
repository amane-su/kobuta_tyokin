package com.example.tyokin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MissionFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MissionFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    private lateinit var missionRecyclerView: RecyclerView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_mission, container, false)

        val imageButton = view.findViewById<ImageButton>(R.id.backButton)

        imageButton.setOnClickListener {onReturnButtonTapped(it)}
        // RecyclerViewの初期化
        missionRecyclerView = view.findViewById(R.id.missionList)

        // RecyclerViewのレイアウトマネージャを設定
        missionRecyclerView.layoutManager = LinearLayoutManager(context)

        // サンプルデータを作成
        val missionList = listOf(
            Mission("・貯金を1回成功させよう", "報酬：コイン16枚", if(Bank.Savemoney > 0)100 else 0),
            Mission("・一日に5000円以上貯金しよう", "報酬：コイン16枚", ((Bank.Savemoney / 5000) * 100)),
            Mission("・一日に目標額の10%以上貯金しよう", "報酬：コイン16枚", (Bank.moneyProgress * 1000).toInt()),
            Mission("・「詳細を見る」から貯金額の推移を見てみよう", "報酬：コイン16枚", 0),
            Mission("・目標額の50%に到達しよう", "報酬：コイン16枚", (Bank.moneyProgress * 200).toInt()),
            Mission("・目標額を達成しよう", "報酬：コイン16枚", (Bank.moneyProgress * 100).toInt())
        )

        // Adapterを設定
        val adapter = MissionAdapter(requireContext(), missionList)
        missionRecyclerView.adapter = adapter

        return view
    }

    fun onReturnButtonTapped(view: View?){
        activity?.supportFragmentManager?.popBackStack(
            null,
            FragmentManager.POP_BACK_STACK_INCLUSIVE
        )
        val activity = activity as? MainActivity
        val textView = activity?.findViewById<TextView>(R.id.pointText)
        textView?.text = "${Bank.Gamecoin}ポイント"
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

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MissionFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MissionFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}