package com.arteriatech.ss.msecsales.rspl.competitors;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.UIListener;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.location.LocationInterface;
import com.arteriatech.mutils.location.LocationModel;
import com.arteriatech.mutils.location.LocationUtils;
import com.arteriatech.mutils.log.LogManager;
import com.arteriatech.mutils.store.OnlineODataInterface;
import com.arteriatech.ss.msecsales.rspl.R;
import com.arteriatech.ss.msecsales.rspl.common.Constants;
import com.arteriatech.ss.msecsales.rspl.common.ConstantsUtils;
import com.arteriatech.ss.msecsales.rspl.mbo.CompetitorBean;
import com.arteriatech.ss.msecsales.rspl.mbo.ValueHelpBean;
import com.arteriatech.ss.msecsales.rspl.store.OfflineManager;
import com.sap.client.odata.v4.core.GUID;
import com.sap.smp.client.odata.ODataDuration;
import com.sap.smp.client.odata.ODataEntity;
import com.sap.smp.client.odata.exception.ODataException;
import com.sap.smp.client.odata.store.ODataRequestExecution;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.List;

/**
 * @desc Created by e10526 on 21-04-2018.
 */
public class CompetitorPresenterImpl implements CompetitorPresenter, OnlineODataInterface, UIListener {
    // data members
    private Context mContext;
    private Activity mActivity;
    private CompetitorView competitorView;
    private boolean isSessionRequired;
    private ArrayList<ValueHelpBean> alCompetitorNames = new ArrayList<>();
    private Hashtable<String, String> singleItem = null;
    private ODataDuration mStartTimeDuration;
    private String[][] mArrayDistributors, mArraySPValues = null;
    private CompetitorBean competitorBean = null;
    private ArrayList<String> schemeStatus= new ArrayList<>();
    private String[] mArraySchemeLaunchValues;

    /**
     * @desc parameterized constructor to initialize required fields
     */
    public CompetitorPresenterImpl(Context mContext, CompetitorView competitorView, boolean isSessionRequired, Activity mActivity, CompetitorBean competitorBean) {
        this.mContext = mContext;
        this.mActivity = mActivity;
        this.competitorView = competitorView;
        this.isSessionRequired = isSessionRequired;
        this.mStartTimeDuration = UtilConstants.getOdataDuration();
        this.competitorBean = competitorBean;
    }

    @Override
    public void onStart() {
        requestCompNames();
    }

    @Override
    public void onDestroy() {
    }

    /**
     * @desc to get competitor names
     */
    private void requestCompNames() {
        if (competitorView != null) {
            competitorView.showProgressDialog(mContext.getString(R.string.app_loading));
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                String mStrConfigQry = Constants.CompetitorMasters;
                List<ODataEntity> entities=null;
                try {
                    entities = Constants.getListEntities(mStrConfigQry,OfflineManager.offlineStore);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                alCompetitorNames.clear();
                try {
                    alCompetitorNames.addAll(OfflineManager.getCompNames(entities, Constants.CompetitorMasters));
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }

                mArraySPValues = Constants.getSPValesFromCPDMSDivisionByCPGUIDAndDMSDivision(competitorBean.getCPGUID(),mContext);
                mArrayDistributors = Constants.getDistributorsByCPGUID(mContext, competitorBean.getCPGUID());
                competitorBean.setSPGUID(mArraySPValues[4][0].toUpperCase());
                competitorBean.setCurrency(mArrayDistributors[10][0]);
                schemeStatus.add(Constants.None);
                schemeStatus.add(Constants.yes);
                schemeStatus.add(Constants.No);

                ((Activity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (competitorView != null) {
                            competitorView.hideProgressDialog();
                            competitorView.displayByCompetitor(alCompetitorNames);
                            competitorView.displayByScheme(schemeStatus);
                        }
                    }
                });
            }
        }).start();
//        String mStrConfigQry = Constants.CompetitorMasters;
//        ConstantsUtils.onlineRequest(mContext, mStrConfigQry, isSessionRequired, 1, ConstantsUtils.SESSION_HEADER, this, false);
    }

    /**
     * @param competitorBean
     * @return boolean
     * @desc validating fields is empty or not
     */
    @Override
    public boolean validateFields(CompetitorBean competitorBean) {
        boolean isNotError = true;
        try {
            if (TextUtils.isEmpty(competitorBean.getCompanyName())) {
                competitorView.errorCompetitorName(mContext.getResources().getString(R.string.lbl_select_competitor_name));
                isNotError = false;
            }

            if (!TextUtils.isEmpty(competitorBean.getSchemeLaunched()) && competitorBean.getSchemeLaunched().equalsIgnoreCase(Constants.None)) {
                competitorView.errorSchemeLaunched(mContext.getResources().getString(R.string.lbl_select_scheme_status));
                isNotError = false;
            }

            if (competitorBean.getSchemeLaunched().equalsIgnoreCase(Constants.YES)) {
                if (TextUtils.isEmpty(competitorBean.getSchemeName())) {
                    competitorView.errorSchemeName(mContext.getResources().getString(R.string.lbl_select_scheme_name));
                    isNotError = false;
                }
            }

            if (TextUtils.isEmpty(competitorBean.getMaterialDesc())) {
                competitorView.errorProductName(mContext.getResources().getString(R.string.lbl_select_product_name));
                isNotError = false;
            }

            if (TextUtils.isEmpty(competitorBean.getMargin())) {
                competitorView.errorRetailerMargin(mContext.getResources().getString(R.string.lbl_select_margin));
                isNotError = false;
            }

            if (TextUtils.isEmpty(competitorBean.getMRP())) {
                competitorView.errorMrp(mContext.getResources().getString(R.string.lbl_select_mrp));
                isNotError = false;
            }

            if (TextUtils.isEmpty(competitorBean.getRemarks())) {
                competitorView.errorRemarks(mContext.getResources().getString(R.string.lbl_select_remarks));
                isNotError = false;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return isNotError;
    }

    /**
     * @desc checks weather given field is valid or not
     */
    public boolean isValidMargin(String margin) {
        boolean isValid = true;
        try {
            int mar = Integer.parseInt(margin);
            if (mar > 100) {
                isValid = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isValid;
    }

    @Override
    public void onAsignData(CompetitorBean competitorBean) {
        assignDataVar(competitorBean);
    }

    @Override
    public void onSaveData() {
        getLocation();
    }

    /**
     * @param competitorBean
     * @desc assigning competitors data to hash table
     */
    private void assignDataVar(CompetitorBean competitorBean) {

        singleItem = new Hashtable<>();
        GUID guidItem = GUID.newRandom();
        singleItem.put(Constants.CompInfoGUID, guidItem.toString36().toUpperCase());
        singleItem.put(Constants.CPTypeID, Constants.str_02);
        singleItem.put(Constants.CPGUID, competitorBean.getCPGUID32());
        singleItem.put(Constants.SPGUID, Constants.getSPGUID());
        singleItem.put(Constants.CompName, competitorBean.getCompanyName()!=null?competitorBean.getCompanyName():"");
        singleItem.put(Constants.MatGrp1Amount, "");
        singleItem.put(Constants.MatGrp2Amount, "");
        singleItem.put(Constants.MatGrp3Amount, "");
        singleItem.put(Constants.MatGrp4Amount, "");
        singleItem.put(Constants.Earnings, "1");
        singleItem.put(Constants.SchemeName, competitorBean.getSchemeName()!=null?competitorBean.getSchemeName():"");
        singleItem.put(Constants.UpdatedOn, UtilConstants.getNewDateTimeFormat());
        singleItem.put(Constants.MaterialDesc, competitorBean.getMaterialDesc()!=null?competitorBean.getMaterialDesc():"");
        singleItem.put(Constants.ConsumerOffer, competitorBean.getConsumerOffer()!=null?competitorBean.getConsumerOffer():"");
        singleItem.put(Constants.TradeOffer, competitorBean.getTradeOffer()!=null?competitorBean.getTradeOffer():"");
        singleItem.put(Constants.Remarks, competitorBean.getRemarks()!=null?competitorBean.getRemarks():"");
        singleItem.put(Constants.Currency, competitorBean.getCurrency()!=null?competitorBean.getCurrency():"");

        if (!competitorBean.getMRP().equalsIgnoreCase("")) {
            singleItem.put(Constants.MRP, competitorBean.getMRP());
        } else {
            singleItem.put(Constants.MRP, Constants.default_value_double);
        }

        if (!competitorBean.getMargin().equalsIgnoreCase("")) {
            singleItem.put(Constants.Margin, competitorBean.getMargin());
        } else {
            singleItem.put(Constants.Margin, Constants.default_value_double);
        }

        if (!competitorBean.getRetailerLandingPrice().equalsIgnoreCase("")) {
            singleItem.put(Constants.LandingPrice, competitorBean.getRetailerLandingPrice());
        } else {
            singleItem.put(Constants.LandingPrice, Constants.default_value_double);
        }

        if (!competitorBean.getWholesalerLandingPrice().equalsIgnoreCase("")) {
            singleItem.put(Constants.WholeSalesLandingPrice, competitorBean.getWholesalerLandingPrice());
        } else {
            singleItem.put(Constants.WholeSalesLandingPrice, Constants.default_value_double);
        }

        if (!competitorBean.getShelfLife().equalsIgnoreCase("")) {
            singleItem.put(Constants.ShelfLife, competitorBean.getShelfLife());
        } else {
            singleItem.put(Constants.ShelfLife, Constants.default_value_int);
        }

        Calendar c = Calendar.getInstance();
        int month = c.get(Calendar.MONTH);
        month++;
        singleItem.put(Constants.Period, String.valueOf(month));

        if (competitorView != null) {
            competitorView.conformationDialog(mContext.getString(R.string.competitor_save_conformation_msg), 1);
        }
    }

    /**
     * @desc to enable saving status
     */
    private void finalSaveCondition() {
        Bundle bundle = new Bundle();
        if (competitorView != null) {
            competitorView.showProgressDialog(mContext.getString(R.string.saving_data_wait));
        }

        bundle.putInt(Constants.BUNDLE_REQUEST_CODE, 1);
        onSave();
    }

    /**
     * @desc to save data in offline store
     */
    private void onSave() {
        try {
            //noinspection unchecked
            OfflineManager.createCompetitorInfo(singleItem, CompetitorPresenterImpl.this,mContext);

        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.Error + " : " + e.getMessage());
        }

        Constants.onVisitActivityUpdate(mContext, singleItem.get(Constants.CPGUID),
                singleItem.get(Constants.CompInfoGUID),
                Constants.CompInfoCreateID, Constants.CompetitorInfos, mStartTimeDuration);

        navigateToDetails();
    }

    /**
     * @desc method to call next screen ofter success of save or creation
     */
    private void navigateToDetails() {
        if (competitorView != null) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    competitorView.hideProgressDialog();
                }
            });
        }
    }

    /**
     * @desc to check location permission
     */
    private void getLocation() {
        if (competitorView != null) {
            competitorView.showProgressDialog(mContext.getString(R.string.checking_pemission));
            LocationUtils.checkLocationPermission(mActivity, new LocationInterface() {
                @Override
                public void location(boolean status, LocationModel location, String errorMsg, int errorCode) {
                    if (competitorView != null) {
                        competitorView.hideProgressDialog();
                    }
                    if (status) {
                        locationPerGranted();
                    }
                }
            });
        }
    }

    /**
     * @desc to get current location
     */
    private void locationPerGranted() {
        if (competitorView != null) {
            competitorView.showProgressDialog(mContext.getString(R.string.checking_pemission));
            Constants.getLocation(mActivity, new LocationInterface() {
                @Override
                public void location(boolean status, LocationModel locationModel, String errorMsg, int errorCode) {
                    if (competitorView != null) {
                        competitorView.hideProgressDialog();
                    }
                    if (status) {
                        if (ConstantsUtils.isAutomaticTimeZone(mContext)) {
                            finalSaveCondition();
                        } else {
                            if (competitorView != null)
                                ConstantsUtils.showAutoDateSetDialog(mActivity);
                        }
                    }
                }
            });
        }
    }

    /**
     * @desc will get invoke after fetching competitor names successfully
     */
    @Override
    public void responseSuccess(ODataRequestExecution oDataRequestExecution, List<ODataEntity> list, Bundle bundle) {
        int type = bundle != null ? bundle.getInt(Constants.BUNDLE_REQUEST_CODE) : 0;
        switch (type) {
            case 1:
                alCompetitorNames.clear();
                try {
                    alCompetitorNames.addAll(OfflineManager.getCompNames(list, Constants.CompetitorMasters));
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }

                mArraySPValues = Constants.getSPValesFromCPDMSDivisionByCPGUIDAndDMSDivision(competitorBean.getCPGUID(),mContext);
                mArrayDistributors = Constants.getDistributorsByCPGUID(mContext, competitorBean.getCPGUID());
                competitorBean.setSPGUID(mArraySPValues[4][0].toUpperCase());
                competitorBean.setCurrency(mArrayDistributors[10][0]);
                schemeStatus.add(Constants.None);
                schemeStatus.add(Constants.yes);
                schemeStatus.add(Constants.No);

                ((Activity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (competitorView != null) {
                            competitorView.hideProgressDialog();
                            competitorView.displayByCompetitor(alCompetitorNames);
                            competitorView.displayByScheme(schemeStatus);
                        }
                    }
                });
                break;
        }
    }

    /**
     * @desc will get invoke if any error occurs while fetching competitor names
     */
    @Override
    public void responseFailed(ODataRequestExecution oDataRequestExecution, String s, Bundle bundle) {
        //competitorView.showMessage(mContext.getString(R.string.msg_competitors_created_success), false);
    }

    /**
     * @desc method will get invoke if any error while saving
     */
    @Override
    public void onRequestError(int i, Exception e) {
        if (competitorView != null) {
            competitorView.errorMessage(mContext.getString(R.string.msg_error_competitor));
        }
    }

    /**
     * @desc method will get invoke if we get success while saving
     */
    @Override
    public void onRequestSuccess(int i, String s) throws ODataException, OfflineODataStoreException {
        if (competitorView != null) {
            competitorView.showMessage(mContext.getString(R.string.msg_competitors_created_success), false);
        }
    }
}
