package com.arteriatech.ss.msecsales.rspl.common;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Base64;

import com.arteriatech.mutils.interfaces.DialogCallBack;
import com.arteriatech.mutils.registration.UtilRegistrationActivity;
import com.arteriatech.ss.msecsales.rspl.R;
import com.arteriatech.ss.msecsales.rspl.registration.Configuration;

import java.nio.charset.Charset;


/**
 * Created by e10769 on 03-02-2017.
 */

public class MyUtils {

    /**
     * SHOW PROGRESS DIALOG
     *
     * @param context
     * @param title
     * @param message
     * @return
     */
    public static ProgressDialog showProgressDialog(Context context, String title, String message) {
        ProgressDialog progressDialog = null;
        try {
            progressDialog = new ProgressDialog(context, R.style.DialogTheme);
            progressDialog.setMessage(message);
            progressDialog.setTitle(title);
            progressDialog.setCancelable(false);
            progressDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return progressDialog;
    }


    /**
     * HIDE PROGRESS DIALOG
     *
     * @param progressDialog
     */
    public static void hideProgressDialog(ProgressDialog progressDialog) {
        try {
            if (progressDialog != null)
                progressDialog.hide();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void dialogConformButton(Context context, String message, final DialogCallBack dialogCallBack) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DialogTheme);
        builder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int id) {

                                dialog.cancel();
                                Constants.Amount = "";
                                Constants.PaymentMethod = "";
                                Constants.IssuingBank = "";
                                if (dialogCallBack != null)
                                    dialogCallBack.clickedStatus(true);

                            }


                        });
                /*.setNegativeButton("Later",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int id) {
                                Constants.Amount = "";
                                Constants.PaymentMethod = "";
                                Constants.IssuingBank = "";
                                if (dialogCallBack != null)
                                    dialogCallBack.clickedStatus(false);

                            }

                        });*/
        builder.show();
    }

    public static void dialogBoxWithButton(Context context, String title, String message, String positiveButton, String negativeButton, final DialogCallBack dialogCallBack) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DialogTheme);
        if (!title.equalsIgnoreCase("")) {
            builder.setTitle(title);
        }
        builder.setMessage(message).setCancelable(false).setPositiveButton(positiveButton, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
                if (dialogCallBack != null)
                    dialogCallBack.clickedStatus(true);
            }
        });
        if (!negativeButton.equalsIgnoreCase("")) {
            builder.setNegativeButton(negativeButton, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                    if (dialogCallBack != null)
                        dialogCallBack.clickedStatus(false);
                }
            });
        }
        builder.show();
    }

    //SMP 3.1
    public static String getDefaultEndPointURL(){
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(Configuration.IS_HTTPS?"https":"http")
                .encodedAuthority(Configuration.server_Text+":"+Configuration.port_Text)
                .appendPath(Configuration.APP_ID);
//                .fragment(applicationName);
        return builder.build().toString();
    }
    public static String getDefaultOnlineQryURL(){
        return getDefaultEndPointURL()+"/";
    }

    public static String getBasicAuthCredential(Context mContext){
        SharedPreferences sharedPref = mContext.getSharedPreferences(Constants.PREFS_NAME, 0);
        // Define the entire conversation with one long method chain and kick-off the
        // request with 'start()'.
        final String user = sharedPref.getString(UtilRegistrationActivity.KEY_username,"");
        final String password = sharedPref.getString(UtilRegistrationActivity.KEY_password,"");
        return "Basic "+ Base64.encodeToString((user + ":" + password).getBytes(Charset.forName("UTF-8")), Base64.NO_WRAP);
    }
}
