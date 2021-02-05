package com.arteriatech.ss.msecsales.rspl.store;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.arteriatech.mutils.common.OnlineODataStoreException;
import com.arteriatech.ss.msecsales.rspl.R;
import com.arteriatech.ss.msecsales.rspl.common.Constants;
import com.arteriatech.ss.msecsales.rspl.interfaces.AsyncTaskCallBack;


/**
 * Created by e10860 on 11/13/2017.
 */

public class OpenOnlineManagerStore extends AsyncTask<String , Boolean,Boolean> {
    Context mContext;
    boolean isOnlineStoreOpened= false;
   private AsyncTaskCallBack asyncTaskCallBack;
    private String errorMessage ="";

    public OpenOnlineManagerStore(Context mContext, AsyncTaskCallBack asyncTaskCallBack){
        this.mContext=mContext;
        this.asyncTaskCallBack=asyncTaskCallBack;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Boolean doInBackground(String... strings) {

        try {
            isOnlineStoreOpened = OnlineManager.openOnlineStore(mContext,false);
        } catch (OnlineODataStoreException e) {
            e.printStackTrace();
            errorMessage = e.getMessage();
        }
        return isOnlineStoreOpened ;
    }

    @Override
    protected void onPostExecute(Boolean s) {
        super.onPostExecute(s);
        if(asyncTaskCallBack!=null){
            if (!isOnlineStoreOpened && TextUtils.isEmpty(errorMessage)){
                try {
                    if (Constants.Error_Msg.equalsIgnoreCase("")) {
                        errorMessage = mContext.getString(R.string.alert_sync_cannot_be_performed);
                    } else {
                        if (Constants.Error_Msg.contains("401")) {
                            errorMessage = "Authorization failed,Your Password is expired. To change password go to Setting and click on Change Password";
                        } else {
                            errorMessage = Constants.Error_Msg;
                        }

                    }
                } catch (Exception e) {
                    errorMessage = mContext.getString(R.string.alert_sync_cannot_be_performed);
                    e.printStackTrace();
                }
            }
            asyncTaskCallBack.onStatus(isOnlineStoreOpened,errorMessage);
        }
    }
}
