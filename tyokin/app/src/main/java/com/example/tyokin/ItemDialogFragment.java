package com.example.tyokin;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class ItemDialogFragment extends DialogFragment {

    private static final String ARG_ITEM_NAME = "item_name";
    private static final String ARG_ITEM_NUMBER = "item_number";

    public interface ItemDialogListener {
        void onItemSelected(int itemNumber);
    }

    public static ItemDialogFragment newInstance(String itemName, int itemNumber) {
        ItemDialogFragment fragment = new ItemDialogFragment();
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
                .setTitle("「" + itemName + "」を着用しますか？")
                .setPositiveButton("OK", (dialog, id) -> {
                    if (getActivity() instanceof ItemDialogListener) {
                        ((ItemDialogListener) getActivity()).onItemSelected(itemNumber);
                    }
                })
                .setNeutralButton("キャンセル", null)
                .create();
    }
}