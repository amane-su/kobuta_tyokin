package com.example.tyokin;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class ItemDialogFragment2 extends DialogFragment {

    private static final String ARG_ITEM_NAME = "item_name";
    private static final String ARG_ITEM_NUMBER = "item_number";

    public static ItemDialogFragment2 newInstance(String itemName, int itemNumber) {
        ItemDialogFragment2 fragment = new ItemDialogFragment2();
        Bundle args = new Bundle();
        args.putString(ARG_ITEM_NAME, itemName);
        args.putInt(ARG_ITEM_NUMBER, itemNumber);
        fragment.setArguments(args);
        return fragment;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        String itemName = getArguments() != null ? getArguments().getString(ARG_ITEM_NAME) : "アイテム";
        int itemNumber = getArguments() != null ? getArguments().getInt(ARG_ITEM_NUMBER) : 0;

        return new AlertDialog.Builder(requireActivity())
                .setTitle("10ポイント消費して「" + itemName + "」を購入しますか？")
                .setPositiveButton("OK", (dialog, id) -> {
                    if (Bank.INSTANCE.getGamecoin() >= 10){
                        Bank.INSTANCE.setGamecoin(Bank.INSTANCE.getGamecoin() - 10);
                        ItemActivity activity = (ItemActivity) requireActivity();
                        updateItemData(itemNumber);  // アイテムを所持状態に更新
                        activity.getItemhave()[itemNumber] = true;  // アイテムを所持状態に更新

                        activity.updateItemUI();
                    }
                })
                .setNeutralButton("キャンセル", null)
                .create();
    }

    private void updateItemData(int itemNumber) {
        // アイテム購入状態をSharedPreferencesに保存
        Context context = requireContext();
        SharedPreferences sharedPreferences = context.getSharedPreferences("ItemData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // アイテムを所有済みに設定
        editor.putBoolean("item_" + itemNumber, true);
        editor.apply();

        // 親ActivityでUIを更新
        if (getActivity() instanceof ItemActivity) {
            ItemActivity activity = (ItemActivity) getActivity();
            activity.getItemhave()[itemNumber] = true; // 配列を更新
            activity.updateItemUI(); // UIを更新
        }
    }
}