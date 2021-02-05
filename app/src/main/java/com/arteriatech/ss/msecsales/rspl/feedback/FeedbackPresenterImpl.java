package com.arteriatech.ss.msecsales.rspl.feedback;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.datavault.UtilDataVault;
import com.arteriatech.mutils.location.LocationInterface;
import com.arteriatech.mutils.location.LocationModel;
import com.arteriatech.mutils.location.LocationUtils;
import com.arteriatech.mutils.store.OnlineODataInterface;
import com.arteriatech.ss.msecsales.rspl.R;
import com.arteriatech.ss.msecsales.rspl.common.Constants;
import com.arteriatech.ss.msecsales.rspl.common.ConstantsUtils;
import com.arteriatech.ss.msecsales.rspl.mbo.FeedbackBean;
import com.arteriatech.ss.msecsales.rspl.mbo.ValueHelpBean;
import com.arteriatech.ss.msecsales.rspl.store.OfflineManager;
import com.sap.client.odata.v4.core.GUID;
import com.sap.smp.client.odata.ODataDuration;
import com.sap.smp.client.odata.ODataEntity;
import com.sap.smp.client.odata.store.ODataRequestExecution;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

/**
 *@desc Created by e10526 on 21-04-2018.
 */
public class FeedbackPresenterImpl implements FeedbackPresenter, OnlineODataInterface {
    private Context mContext;
    private Activity mActivity;
    private FeedbackView feedbackView;
    private boolean isSessionRequired;
    private ArrayList<ValueHelpBean> alFeedBackType = new ArrayList<>();
    private ArrayList<ValueHelpBean> alFeedbackSubType = new ArrayList<>();
    private Hashtable<String, String> masterHeaderTable = new Hashtable<>();
    private ArrayList<HashMap<String, String>> itemTable = new ArrayList<>();
    private ODataDuration mStartTimeDuration;
    private String[][] mArrayDistributors, mArraySPValues = null;
    private FeedbackBean feedbackBean = null;

    public FeedbackPresenterImpl(Context mContext, FeedbackView feedbackView, boolean isSessionRequired, Activity mActivity, FeedbackBean feedbackBean) {
        this.mContext = mContext;
        this.mActivity = mActivity;
        this.feedbackView = feedbackView;
        this.isSessionRequired = isSessionRequired;
        this.mStartTimeDuration = UtilConstants.getOdataDuration();
        this.feedbackBean = feedbackBean;
    }

    @Override
    public void onStart() {
        requestFeedBackType();
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public boolean validateFields(FeedbackBean feedbackBean) {
        boolean isNotError = true;
        if (TextUtils.isEmpty(feedbackBean.getFeedbackType())) {
            feedbackView.errorFeedBackType("Select Feedback Type");
            isNotError = false;
        }

        if (feedbackBean.getFeedbackType().equalsIgnoreCase(Constants.str_05)) {
            if (TextUtils.isEmpty(feedbackBean.getProdRelID())) {
                feedbackView.errorProductRelated("Select Product Related");
                isNotError = false;
            }
        }

        if (feedbackBean.getFeedbackType().equalsIgnoreCase(Constants.str_06)) {
            if (TextUtils.isEmpty(feedbackBean.getOthers())) {
                feedbackView.errorOthers("Enter Others");
                isNotError = false;
            }
        }

        if (TextUtils.isEmpty(feedbackBean.getRemarks()) || feedbackBean.getRemarks().trim().length() == 0) {
            feedbackView.errorRemarks("Enter Remarks");
            isNotError = false;
        }
        return isNotError;
    }

    @Override
    public void getProductRelInfo(String FeedbackId) {
        if (feedbackView != null) {
            feedbackView.showProgressDialog(mContext.getString(R.string.app_loading));
        }

        String mStrConfigQry = Constants.ValueHelps + "?$filter=" + Constants.PropName + " eq '"
                + Constants.FeedbackSubType + "' and " + Constants.ParentID + " eq '" + FeedbackId + "' ";
        ConstantsUtils.onlineRequest(mContext, mStrConfigQry, isSessionRequired, 4, ConstantsUtils.SESSION_HEADER, this, false);
    }

    @Override
    public void onAsignData(String save, String strRejReason, String strRejReasonDesc, FeedbackBean feedbackBean) {
        assignDataVar("", "", "", feedbackBean);
    }

    @Override
    public void approveData(String ids, String description, String approvalStatus) {

    }

    @Override
    public void onSaveData() {
        getLocation();
    }

    private void assignDataVar(String save, String strRejReason, String strRejReasonDesc, FeedbackBean feedbackBean) {
        String doc_no = (System.currentTimeMillis() + "");
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(Constants.PREFS_NAME, 0);

        String loginIdVal = sharedPreferences.getString(Constants.username, "");

        GUID mStrGuide = GUID.newRandom();

        masterHeaderTable.clear();
        //noinspection unchecked
        masterHeaderTable.put(Constants.FeebackGUID, mStrGuide.toString());
        //noinspection unchecked
        masterHeaderTable.put(Constants.Remarks, feedbackBean.getRemarks());
        //noinspection unchecked
        masterHeaderTable.put(Constants.CPNo, UtilConstants.removeLeadingZeros(feedbackBean.getCPNo()));
        //noinspection unchecked
        masterHeaderTable.put(Constants.CPGUID, feedbackBean.getCPGUID());
        //noinspection unchecked
        masterHeaderTable.put(Constants.FeedbackType, feedbackBean.getFeedbackType());

        if (feedbackBean.getFeedbackType().equalsIgnoreCase(Constants.str_06)) {
            masterHeaderTable.put(Constants.FeedbackTypeDesc, feedbackBean.getOthers());
        } else {
            masterHeaderTable.put(Constants.FeedbackTypeDesc, feedbackBean.getFeedbackTypeDesc());
        }

        if (feedbackBean.getFeedbackType().equalsIgnoreCase(Constants.str_05)) {
            //noinspection unchecked
            masterHeaderTable.put(Constants.FeedbackSubTypeID, feedbackBean.getProdRelID());

            masterHeaderTable.put(Constants.FeedbackSubTypeDesc, feedbackBean.getProdRelDesc());
        } else {
            masterHeaderTable.put(Constants.FeedbackSubTypeID, "");
            masterHeaderTable.put(Constants.FeedbackSubTypeDesc, "");
        }

        masterHeaderTable.put(Constants.Location1, "");

        masterHeaderTable.put(Constants.CPTypeID, Constants.str_02);
        masterHeaderTable.put(Constants.SPGUID, Constants.getSPGUID());
        masterHeaderTable.put(Constants.SPNo, feedbackBean.getSPNo());
        masterHeaderTable.put(Constants.ParentID, feedbackBean.getParentID());
        masterHeaderTable.put(Constants.ParentName, feedbackBean.getParentName());
        masterHeaderTable.put(Constants.ParentTypeID, feedbackBean.getParentTypeID());
        masterHeaderTable.put(Constants.ParentTypDesc, feedbackBean.getParentTypDesc());
//        masterHeaderTable.put(Constants.ParentID, mArrayDistributors[4][0].toUpperCase());
//        masterHeaderTable.put(Constants.ParentName, mArrayDistributors[7][0].toUpperCase());
//        masterHeaderTable.put(Constants.ParentTypeID, mArrayDistributors[5][0].toUpperCase());
//        masterHeaderTable.put(Constants.ParentTypDesc, mArrayDistributors[6][0].toUpperCase());
        //noinspection unchecked
        masterHeaderTable.put(Constants.LOGINID, loginIdVal);

        masterHeaderTable.put(Constants.CreatedOn, UtilConstants.getNewDateTimeFormat());

        masterHeaderTable.put(Constants.FeedbackDate, UtilConstants.getNewDateTimeFormat());

        masterHeaderTable.put(Constants.CreatedAt, UtilConstants.getOdataDuration() + "");

        masterHeaderTable.put(Constants.entityType, Constants.Feedbacks);

        itemTable = new ArrayList<HashMap<String, String>>();

        HashMap tableItm = new HashMap();

        try {
            // noinspection unchecked
            tableItm.put(Constants.FeebackGUID, mStrGuide.toString());
            mStrGuide = GUID.newRandom();
            // noinspection unchecked
            tableItm.put(Constants.FeebackItemGUID, mStrGuide.toString());
            // noinspection unchecked
            tableItm.put(Constants.FeedbackType, feedbackBean.getFeedbackType());

            if (feedbackBean.getFeedbackType().equalsIgnoreCase(Constants.str_06)) {
                tableItm.put(Constants.FeedbackTypeDesc, feedbackBean.getOthers());
            } else {
                tableItm.put(Constants.FeedbackTypeDesc, feedbackBean.getFeedbackTypeDesc());
            }
            // noinspection unchecked
            tableItm.put(Constants.Remarks, feedbackBean.getRemarks());

            if (feedbackBean.getFeedbackType().equalsIgnoreCase(Constants.str_05)) {
                // noinspection unchecked
                tableItm.put(Constants.FeedbackSubTypeID, feedbackBean.getProdRelID());

                tableItm.put(Constants.FeedbackSubTypeDesc, feedbackBean.getProdRelDesc());
            } else {
                tableItm.put(Constants.FeedbackSubTypeID, "");

                tableItm.put(Constants.FeedbackSubTypeDesc, "");
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        itemTable.add(tableItm);
        masterHeaderTable.put(Constants.ITEM_TXT, UtilConstants.convertArrListToGsonString(itemTable));

        masterHeaderTable.put(Constants.LOGINID, sharedPreferences.getString(Constants.username, "").toUpperCase());

        masterHeaderTable.put(Constants.FeedbackNo, doc_no);

        if (feedbackView != null) {
            feedbackView.conformationDialog(mContext.getString(R.string.feedback_save_conformation_msg), 1);
        }
    }

    private void finalSaveCondition() {
        Bundle bundle = new Bundle();

        if (feedbackView != null) {
            feedbackView.showProgressDialog(mContext.getString(R.string.saving_data_wait));
        }

        bundle.putInt(Constants.BUNDLE_REQUEST_CODE, 1);
        onSave();
    }

    private void onSave() {
        Constants.saveDeviceDocNoToSharedPref(mContext, Constants.Feedbacks, masterHeaderTable.get(Constants.FeedbackNo));
        JSONObject jsonHeaderObject = new JSONObject(masterHeaderTable);

        ConstantsUtils.storeInDataVault(masterHeaderTable.get(Constants.FeedbackNo), jsonHeaderObject.toString(),mContext);

        Constants.onVisitActivityUpdate(mContext, masterHeaderTable.get(Constants.CPGUID),
                masterHeaderTable.get(Constants.FeebackGUID),
                Constants.FeedbackID, Constants.Feedbacks, mStartTimeDuration);

        navigateToDetails();
    }

    private void navigateToDetails() {
        if (feedbackView != null) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    feedbackView.hideProgressDialog();
                    feedbackView.showMessage(mContext.getString(R.string.msg_feedback_created), false);
                }
            });
        }
    }

    private void requestFeedBackType() {
        if (feedbackView != null) {
            feedbackView.showProgressDialog(mContext.getString(R.string.app_loading));
        }

        mArrayDistributors = Constants.getDistributorsByCPGUID(mContext, Constants.convertStrGUID32to36(feedbackBean.getCPGUID()),feedbackBean.getParentId());
        mArraySPValues = Constants.getSPValesFromCPDMSDivisionByCPGUIDAndDMSDivision(Constants.convertStrGUID32to36(feedbackBean.getCPGUID()),mContext);
        String mStrConfigQry = Constants.ValueHelps + "?$filter=" + Constants.PropName + " eq '" + Constants.FeedbackType + "'&$orderby=ID asc";
        ConstantsUtils.onlineRequest(mContext, mStrConfigQry, isSessionRequired, 3, ConstantsUtils.SESSION_HEADER, this, false);
    }


    @Override
    public void responseSuccess(ODataRequestExecution oDataRequestExecution, List<ODataEntity> list, Bundle bundle) {
        int type = bundle != null ? bundle.getInt(Constants.BUNDLE_REQUEST_CODE) : 0;
        switch (type) {
            case 3:
                alFeedBackType.clear();

                try {
                    feedbackBean.setSPGUID(mArraySPValues[4][0].toUpperCase());
                    feedbackBean.setSPNo(mArraySPValues[6][0].toUpperCase());
                    feedbackBean.setParentID(mArrayDistributors[4][0].toUpperCase());
                    feedbackBean.setParentName(mArrayDistributors[7][0].toUpperCase());
                    feedbackBean.setParentName(mArrayDistributors[5][0].toUpperCase());
                    feedbackBean.setParentTypDesc(mArrayDistributors[6][0].toUpperCase());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    alFeedBackType.addAll(OfflineManager.getConfigListFromValueHelp(list, Constants.FeedbackType));
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }
                ((Activity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (feedbackView != null) {
                            feedbackView.hideProgressDialog();
                            feedbackView.displayByFeedback(alFeedBackType);
                        }
                    }
                });
                break;
            case 4:
                alFeedbackSubType.clear();
                try {
                    alFeedbackSubType.addAll(OfflineManager.getConfigListFromValueHelp(list, Constants.FeedbackSubType));
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }
                ((Activity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (feedbackView != null) {
                            feedbackView.hideProgressDialog();
                            feedbackView.displayByProductRelInfo(alFeedbackSubType);
                        }
                    }
                });
                break;
        }
    }

    @Override
    public void responseFailed(ODataRequestExecution oDataRequestExecution, String s, Bundle bundle) {

    }

    private void getLocation() {
        if (feedbackView != null) {
            feedbackView.showProgressDialog(mContext.getString(R.string.checking_pemission));
            LocationUtils.checkLocationPermission(mActivity, new LocationInterface() {
                @Override
                public void location(boolean status, LocationModel location, String errorMsg, int errorCode) {
                    if (feedbackView != null) {
                        feedbackView.hideProgressDialog();
                    }
                    if (status) {
                        locationPerGranted();
                    }
                }
            });
        }
    }

    private void locationPerGranted() {
        if (feedbackView != null) {
            feedbackView.showProgressDialog(mContext.getString(R.string.checking_pemission));
            Constants.getLocation(mActivity, new LocationInterface() {
                @Override
                public void location(boolean status, LocationModel locationModel, String errorMsg, int errorCode) {
                    if (feedbackView != null) {
                        feedbackView.hideProgressDialog();
                    }
                    if (status) {
                        if (ConstantsUtils.isAutomaticTimeZone(mContext)) {
                            finalSaveCondition();
                        } else {
                            if (feedbackView != null)
                                ConstantsUtils.showAutoDateSetDialog(mActivity);
                        }
                    }
                }
            });
        }
    }
}
