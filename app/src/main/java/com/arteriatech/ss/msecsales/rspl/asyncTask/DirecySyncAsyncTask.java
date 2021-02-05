package com.arteriatech.ss.msecsales.rspl.asyncTask;

import android.content.Context;
import android.os.AsyncTask;

import com.arteriatech.mutils.common.OnlineODataStoreException;
import com.arteriatech.mutils.common.UIListener;
import com.arteriatech.mutils.log.TraceLog;
import com.arteriatech.ss.msecsales.rspl.R;
import com.arteriatech.ss.msecsales.rspl.common.Constants;
import com.arteriatech.ss.msecsales.rspl.interfaces.AsyncTaskCallBack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

/**
 * Created by e10769 on 15-05-2017.
 */

public class DirecySyncAsyncTask extends AsyncTask<Void, Boolean, Boolean> {
    private Context mContext;
    private UIListener uiListener = null;
    private Hashtable dbHeadTable = null;
    private ArrayList<HashMap<String, String>> arrtable = null;
    private int type = 0;

    public DirecySyncAsyncTask(Context mContext, AsyncTaskCallBack asyncTaskCallBack, UIListener uiListener, Hashtable dbHeadTable, ArrayList<HashMap<String, String>> arrtable, int type) {
        this.mContext = mContext;
        this.uiListener = uiListener;
        this.dbHeadTable = dbHeadTable;
        this.arrtable = arrtable;
        this.type = type;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        boolean storeOpened = false;
        return storeOpened;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        if (!aBoolean) {
            if (uiListener != null) {
                uiListener.onRequestError(0, new Exception(mContext.getString(R.string.no_network_conn)));
            }
        }
    }
}
