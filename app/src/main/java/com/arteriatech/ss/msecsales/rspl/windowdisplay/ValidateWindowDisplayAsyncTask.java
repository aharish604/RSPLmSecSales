package com.arteriatech.ss.msecsales.rspl.windowdisplay;

import android.os.AsyncTask;
import android.text.TextUtils;

import com.arteriatech.ss.msecsales.rspl.interfaces.AsyncTaskCallBack;
import com.arteriatech.ss.msecsales.rspl.scheme.ValidationQueryLogic;

public class ValidateWindowDisplayAsyncTask extends AsyncTask<String,Boolean,Boolean> {

    private AsyncTaskCallBack dialogCallBack = null;
    private String schmeGuid="";
    private int days=0;
    private String results="";
    private String mStrCPGUID="";

    public ValidateWindowDisplayAsyncTask(AsyncTaskCallBack dialogCallBack, String schmeGuid, int days, String mStrCPGUID) {
        this.dialogCallBack = dialogCallBack;
        this.schmeGuid=schmeGuid;
        this.days=days;
        this.mStrCPGUID=mStrCPGUID;
    }
    @Override
    protected Boolean doInBackground(String... params) {
        results = ValidationQueryLogic.validateStocks(schmeGuid,days,mStrCPGUID);
        if(!TextUtils.isEmpty(results)){
            return true;

        }else {
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        if(dialogCallBack!=null){
            dialogCallBack.onStatus(aBoolean,results);
        }
    }
}
