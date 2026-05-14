package com.example.gardenapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.example.tyokin.R

class ImageDisplayFragment : Fragment() {

    companion object {
        private const val ARG_IMAGE_RES = "image_resource"

        // Fragmentのインスタンスを生成するファクトリーメソッド
        fun newInstance(imageRes: Int): ImageDisplayFragment {
            val fragment = ImageDisplayFragment()
            val args = Bundle()
            args.putInt(ARG_IMAGE_RES, imageRes)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // レイアウトを膨らませる
        val view = inflater.inflate(R.layout.fragment_image_display, container, false)

        // 画像リソースを取得してImageViewに設定
        val imageView: ImageView = view.findViewById(R.id.imageView)
        arguments?.getInt(ARG_IMAGE_RES)?.let { imageRes ->
            imageView.setImageResource(imageRes)
        }

        return view
    }
}
