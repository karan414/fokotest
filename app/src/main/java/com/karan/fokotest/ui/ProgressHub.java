package com.karan.fokotest.ui;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.karan.fokotest.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


public class ProgressHub extends Dialog {
    public ProgressHub(@NonNull Context context) {
        super(context);
    }

    public ProgressHub(@NonNull Context context, int theme) {
        super(context, theme);
    }

    @NonNull
    public static ProgressHub show(@NonNull Context context, @Nullable CharSequence message,
                                   OnCancelListener cancelListener) {
        ProgressHub dialog = new ProgressHub(context, R.style.ProgressHUD);
        dialog.setTitle("");
        dialog.setContentView(R.layout.progress_hub);
        if (message == null || message.length() == 0) {
            dialog.findViewById(R.id.message).setVisibility(View.GONE);
        } else {
            TextView txt = (TextView) dialog.findViewById(R.id.message);
            txt.setText(message);
        }
        dialog.setCancelable(true);
        dialog.setOnCancelListener(cancelListener);
        if (dialog.getWindow().getAttributes() != null) {
            dialog.getWindow().getAttributes().gravity = Gravity.CENTER;
        }
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.dimAmount = 0.2f;
        dialog.getWindow().setAttributes(lp);
        dialog.show();
        return dialog;
    }

    public void onWindowFocusChanged(boolean hasFocus) {
        ImageView imageView = (ImageView) findViewById(R.id.spinnerImageView);
        AnimationDrawable spinner = (AnimationDrawable) imageView.getBackground();
        spinner.start();
    }
}
