package com.karan.fokotest.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.karan.fokotest.R;
import com.karan.fokotest.ui.ProgressHub;
import com.neovisionaries.i18n.CountryCode;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class Utility {

    private static ProgressHub mProgressHub;

    public static boolean isNullOrEmpty(String string) {
        return string == null || string.equalsIgnoreCase("")
                || string.equalsIgnoreCase("null");
    }

    public static boolean checkIntIsNull(int n) {
        Integer number = new Integer(n);
        return number == null;
    }

    @SuppressWarnings("deprecation")
    public static boolean isInternetAvailable(@NonNull Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (NetworkInfo value : info)
                    if (value.getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
        }
        return false;
    }

    public static void showToast(Context mCont, String message) {
        Toast.makeText(mCont, message, Toast.LENGTH_LONG).show();
    }

    public static void replaceFragment(FragmentManager fragmentManager, Fragment fragment) {
        fragmentManager.
                beginTransaction().
                replace(R.id.nav_host_fragment, fragment).
                addToBackStack(null).
                commitAllowingStateLoss();
    }

    public static String getCountryName(String countryCode) {
        if (!Utility.isNullOrEmpty(countryCode)) {
            CountryCode code = CountryCode.getByCode(countryCode);
            return code.getName();
        }
        return "";
    }

    public static String getCountryCode(String countryCode) {
        if (!Utility.isNullOrEmpty(countryCode)) {
            CountryCode code = CountryCode.getByCode(countryCode);
            return code.getAlpha2();
        }
        return "";
    }

    public static ProgressHub getProgressDialog(@NonNull Context context) {
        try {
            mProgressHub = ProgressHub.show(context, "Loading", new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    mProgressHub.dismiss();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mProgressHub;
    }

    public static void stopProgress(ProgressHub mProgressHub) {
        try {

            if (mProgressHub != null) {
                mProgressHub.dismiss();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
