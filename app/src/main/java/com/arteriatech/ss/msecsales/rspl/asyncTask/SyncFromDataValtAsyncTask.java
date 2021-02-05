package com.arteriatech.ss.msecsales.rspl.asyncTask;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.arteriatech.mutils.common.OnlineODataStoreException;
import com.arteriatech.mutils.common.UIListener;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.ss.msecsales.rspl.common.Constants;
import com.arteriatech.ss.msecsales.rspl.common.ConstantsUtils;
import com.arteriatech.ss.msecsales.rspl.interfaces.MessageWithBooleanCallBack;
import com.arteriatech.ss.msecsales.rspl.store.OnlineManager;
import com.arteriatech.ss.msecsales.rspl.sync.SyncSelectionActivity;
import com.arteriatech.ss.msecsales.rspl.sync.syncSelectionView.SyncHistoryInfoFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

/**
 * Created by e10769 on 04-03-2017.
 */

public class SyncFromDataValtAsyncTask extends AsyncTask<String, Boolean, Boolean> {
    private Context mContext;
    private UIListener uiListener;
    private Hashtable dbHeadTable;
    private ArrayList<HashMap<String, String>> arrtable;
    private String[] invKeyValues = null;
    private MessageWithBooleanCallBack dialogCallBack = null;
    private String errorMsg = "";

    public SyncFromDataValtAsyncTask(Context context, String[] invKeyValues, UIListener uiListener,
                                     MessageWithBooleanCallBack dialogCallBack) {
        this.mContext = context;
        this.uiListener = uiListener;
        this.invKeyValues = invKeyValues;
        this.dialogCallBack = dialogCallBack;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }


    @Override
    protected Boolean doInBackground(String... params) {
        boolean onlineStoreOpen = false;
        try {
            Constants.IsOnlineStoreFailed = false;
            Constants.AL_ERROR_MSG.clear();

            Constants.ErrorCode = 0;
            Constants.ErrorNo = 0;
            Constants.ErrorName = "";

            onlineStoreOpen = OnlineManager.openOnlineStore(mContext,true);

            if (onlineStoreOpen) {
                if (invKeyValues != null) {
                    for (int k = 0; k < invKeyValues.length; k++) {

                     /*   while (!Constants.mBoolIsReqResAval) {
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }

                        if (Constants.mBoolIsNetWorkNotAval) {
                            break;
                        }
                        Constants.mBoolIsReqResAval = false;*/


                        String store = null;
                        try {
                            store = ConstantsUtils.getFromDataVault(invKeyValues[k].toString(),mContext);
                        } catch (Throwable e) {
                            e.printStackTrace();
                            errorMsg = e.getMessage();
                        }

                        //Fetch object from data vault
                        try {

                            JSONObject fetchJsonHeaderObject = new JSONObject(store);
                            dbHeadTable = new Hashtable();
                            arrtable = new ArrayList<>();
                             if (fetchJsonHeaderObject.getString(Constants.entityType).equalsIgnoreCase(Constants.SecondarySOCreate)) {
                                Constants.REPEATABLE_REQUEST_ID="";
                                JSONObject dbHeadTable = Constants.getSOHeaderJSONValuesFromJsonObject(fetchJsonHeaderObject);
                                OnlineManager.createEntity(Constants.REPEATABLE_REQUEST_ID,dbHeadTable.toString(), Constants.SSSOs, uiListener, mContext);
                            }  else if (fetchJsonHeaderObject.getString(Constants.entityType).equalsIgnoreCase(Constants.FinancialPostings)) {
                                 Constants.REPEATABLE_REQUEST_ID="";
                                 JSONObject dbHeadTable = Constants.getCollHeaderJSONValuesFromJsonObject(fetchJsonHeaderObject);
                                 OnlineManager.createEntity( Constants.REPEATABLE_REQUEST_ID,dbHeadTable.toString(), Constants.FinancialPostings, uiListener, mContext);
                             }else if (fetchJsonHeaderObject.getString(Constants.entityType).equalsIgnoreCase(Constants.Feedbacks)) {
                                // preparing entity pending
                                Constants.REPEATABLE_REQUEST_ID="";
                                JSONObject dbHeadTable = Constants.getFeedbackJSONHeaderValuesFromJsonObject(fetchJsonHeaderObject);
                                OnlineManager.createEntity( Constants.REPEATABLE_REQUEST_ID,dbHeadTable.toString(), Constants.Feedbacks, uiListener, mContext);
                            }else if(fetchJsonHeaderObject.getString(Constants.entityType).equalsIgnoreCase(Constants.SampleDisbursement)){
                                 Constants.REPEATABLE_REQUEST_ID="";
                                 JSONObject dbHeadTable = Constants.getSSInvoiceJSONHeaderValuesFromJsonObject(fetchJsonHeaderObject);
                                 OnlineManager.createEntity( Constants.REPEATABLE_REQUEST_ID,dbHeadTable.toString(), Constants.SSINVOICES, uiListener, mContext);
                             }else if (fetchJsonHeaderObject.getString(Constants.entityType).equalsIgnoreCase(Constants.ChannelPartners)) {
                                 // preparing entity pending
                                 Constants.REPEATABLE_REQUEST_ID="";
                                 JSONObject dbHeadTable = Constants.getCPHeaderJSONValuesFromJsonObject(fetchJsonHeaderObject);
                                 OnlineManager.createEntity( Constants.REPEATABLE_REQUEST_ID,dbHeadTable.toString(), Constants.ChannelPartners, uiListener, mContext);
                             } else if (fetchJsonHeaderObject.getString(Constants.entityType).equalsIgnoreCase(Constants.ReturnOrderCreate)) {
                                 Constants.REPEATABLE_REQUEST_ID="";
                                 JSONObject dbHeadTable = Constants.getROHeaderJSONValuesFromJsonObject(fetchJsonHeaderObject);
                                 OnlineManager.createEntity(Constants.REPEATABLE_REQUEST_ID,dbHeadTable.toString(), Constants.SSROs, uiListener, mContext);
                             } else if (fetchJsonHeaderObject.getString(Constants.entityType).equalsIgnoreCase(Constants.Expenses)) {
                                 Constants.REPEATABLE_REQUEST_ID="";
                                 JSONObject dbHeadTable = Constants.getExpenseHeaderJSONValuesFromJsonObject(fetchJsonHeaderObject);
                                 OnlineManager.createEntity(Constants.REPEATABLE_REQUEST_ID,dbHeadTable.toString(), Constants.Expenses, uiListener,mContext);
                             }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            errorMsg = e.getMessage();
                        }
                    }
                    onlineStoreOpen = true;
                }
            } else {
                return onlineStoreOpen;
            }


        } catch (Exception e) {
            e.printStackTrace();
            errorMsg = e.getMessage();
        }
        return onlineStoreOpen;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        if (!TextUtils.isEmpty(errorMsg)) {
            setCallBackToUI(false, errorMsg);
        } else if (!aBoolean) {
            setCallBackToUI(aBoolean, Constants.makeMsgReqError(Constants.ErrorNo, mContext, false));
        }

    }


    private void setCallBackToUI(boolean status, String error_Msg) {
        if (dialogCallBack != null) {
            String errorMessage="";
            try {
                if (Constants.Error_Msg.contains("401")) {
                    errorMessage = "Authorization failed,Your Password is expired. To change password go to Setting and click on Change Password";
                } else {
                    errorMessage = error_Msg;
                }
            } catch (Exception e) {
                errorMessage = error_Msg;
                e.printStackTrace();
            }
            dialogCallBack.clickedStatus(status, errorMessage, null);
        }
    }

}
