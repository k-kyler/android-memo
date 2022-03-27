package com.example.memo.utils;

import android.content.Context;
import android.widget.Toast;

public class SharedMethods {
    public void showToastMessage(Context context, String message) {
        int toastDuration = Toast.LENGTH_SHORT;

        if (!message.matches("")) {
            Toast toast = Toast.makeText(context, message, toastDuration);

            toast.show();
        }
    }
}
