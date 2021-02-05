package com.arteriatech.ss.msecsales.rspl.asyncTask;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.UIListener;
import com.arteriatech.mutils.log.LogManager;
import com.arteriatech.ss.msecsales.rspl.common.Constants;
import com.arteriatech.ss.msecsales.rspl.store.OfflineManager;

public class RefreshAsyncTask extends AsyncTask<String, Boolean, Boolean> {
    private Context mContext;
    private String refreshList;
    private UIListener uiListener;

    public RefreshAsyncTask(Context context, String refreshList, UIListener uiListener) {
        this.mContext = context;
        this.refreshList = refreshList;
        this.uiListener = uiListener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Boolean doInBackground(String... params) {
        if (!OfflineManager.isOfflineStoreOpen()) {
            try {
                OfflineManager.openOfflineStore(mContext, uiListener);
            } catch (OfflineODataStoreException e) {
                e.printStackTrace();
                LogManager.writeLogError(Constants.error_txt + e.getMessage());
            }
        } else {
            if (!TextUtils.isEmpty(refreshList)) {
                try {
                    OfflineManager.refreshStoreSync(mContext, uiListener, Constants.Fresh, refreshList);
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                    LogManager.writeLogError(Constants.error_txt + e.getMessage());
                }
            }else {
                try {
                    OfflineManager.refreshStoreSync(mContext, uiListener, Constants.ALL, refreshList);
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                    LogManager.writeLogError(Constants.error_txt + e.getMessage());
                }
            }
        }
        return true;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);

    }
}
