package com.arteriatech.ss.msecsales.rspl.collection;

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
import com.arteriatech.ss.msecsales.rspl.mbo.CollectionBean;
import com.arteriatech.ss.msecsales.rspl.mbo.DMSDivisionBean;
import com.arteriatech.ss.msecsales.rspl.mbo.InvoiceBean;
import com.arteriatech.ss.msecsales.rspl.mbo.OutstandingBean;
import com.arteriatech.ss.msecsales.rspl.mbo.ValueHelpBean;
import com.arteriatech.ss.msecsales.rspl.store.OfflineManager;
import com.sap.client.odata.v4.core.GUID;
import com.sap.smp.client.odata.ODataDuration;
import com.sap.smp.client.odata.ODataEntity;
import com.sap.smp.client.odata.store.ODataRequestExecution;

import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

/**
 * Created by e10526 on 21-04-2018.
 */

public class CollectionCreatePresenterImpl implements CollectionCreatePresenter, OnlineODataInterface {
    ArrayList<HashMap<String, String>> arrtable;
    double mDoubleTotalInvSum = 0.0;
    private Context mContext;
    private Activity mActivity;
    private CollectionCreateView collCreateView;
    private boolean isSessionRequired;
    private ArrayList<ValueHelpBean> alCollRefType = new ArrayList<>();
    private ArrayList<ValueHelpBean> alCollPaymentMode = new ArrayList<>();
    private ArrayList<ValueHelpBean> alCollBankNames = new ArrayList<>();
    private Hashtable<String, String> masterHeaderTable = new Hashtable<>();
    private ODataDuration mStartTimeDuration;
    private String[][] mArrayDistributors, mArraySPValues = null;
    private CollectionBean collectionBean = null;
    private int totalRequest = 0;
    private int currentRequest = 0;
    private ArrayList<OutstandingBean> alOutstandingsBean;
    private ArrayList<InvoiceBean> alInvoiceList = new ArrayList<>();
    private ArrayList<DMSDivisionBean> alDmsDivision = new ArrayList<>();
    private BigDecimal totalOutVal = new BigDecimal(0.0);

    public CollectionCreatePresenterImpl(Context mContext, CollectionCreateView collCreateView, boolean isSessionRequired, Activity mActivity, CollectionBean collectionBean) {
        this.mContext = mContext;
        this.mActivity = mActivity;
        this.collCreateView = collCreateView;
        this.isSessionRequired = isSessionRequired;
        this.mStartTimeDuration = UtilConstants.getOdataDuration();
        this.collectionBean = collectionBean;
    }


    @Override
    public void onStart() {
        requestCollType();
    }

    @Override
    public void getInvoices(String divisionID) {
        if (collCreateView != null) {
            collCreateView.showProgressDialog(mContext.getString(R.string.app_loading));
        }
        currentRequest = 0;
        totalRequest = 1;

        String mStrInvQry = Constants.SSOutstandingInvoices + "?$orderby=" + Constants.InvoiceNo + " asc&$filter=SoldToID eq '" + collectionBean.getCPNo() + "' and (StatusID eq '03' or StatusID eq '04' or StatusID eq '05') and "+Constants.DMSDivisionID+" eq '"+divisionID+"'";
        ConstantsUtils.onlineRequest(mContext, mStrInvQry, isSessionRequired, 4, ConstantsUtils.SESSION_HEADER, this, false);
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public boolean validateFields(CollectionBean collectionBean, String syncType) {
        boolean isNotError = true;
        if (TextUtils.isEmpty(collectionBean.getRefTypeID())) {
            collCreateView.errorRefType("Select Collection Method");
            isNotError = false;
        }
        if (TextUtils.isEmpty(collectionBean.getPaymentModeID())) {
            collCreateView.errorPaymentMode("Select Payment Mode");
            isNotError = false;
        }

        if (TextUtils.isEmpty(collectionBean.getDivision())) {
            collCreateView.errorDivision(mContext.getString(R.string.coll_val_division));
            isNotError = false;
        }

        if (!TextUtils.isEmpty(collectionBean.getPaymentModeID()) && !collectionBean.getPaymentModeID().equalsIgnoreCase(Constants.str_04)) {


            if (TextUtils.isEmpty(collectionBean.getBankID())) {
                collCreateView.errorBankName(mContext.getString(R.string.coll_val_bank));
                isNotError = false;
            }
            if (TextUtils.isEmpty(collectionBean.getChequeDate())) {
                collCreateView.errorChequeDate(mContext.getString(R.string.coll_val_date));
                isNotError = false;
            }
            if (collectionBean.getPaymentModeID().equalsIgnoreCase("03")) {
                if (TextUtils.isEmpty(collectionBean.getUTRNo())) {
                    collCreateView.errorUTRNoOrChequeDD(mContext.getString(R.string.coll_val_utr));
                    isNotError = false;
                } else if (collectionBean.getUTRNo().trim().length() < 8 || collectionBean.getUTRNo().trim().length() > 22) {
                    collCreateView.errorUTRNoOrChequeDD(mContext.getString(R.string.coll_val_utr_valid));
                    isNotError = false;
                }

            } else if (collectionBean.getPaymentModeID().equalsIgnoreCase("02")) {
                if (TextUtils.isEmpty(collectionBean.getUTRNo())) {
                    collCreateView.errorUTRNoOrChequeDD(mContext.getString(R.string.coll_val_card));
                    isNotError = false;
                } else if (collectionBean.getUTRNo().trim().length() < 12 || collectionBean.getUTRNo().trim().length() > 19) {
                    collCreateView.errorUTRNoOrChequeDD(mContext.getString(R.string.coll_val_card_valid));
                    isNotError = false;
                }
            } else if (collectionBean.getPaymentModeID().equalsIgnoreCase("01")) {
                if (TextUtils.isEmpty(collectionBean.getUTRNo())) {
                    collCreateView.errorUTRNoOrChequeDD(mContext.getString(R.string.coll_val_cheque));
                    isNotError = false;
                } else if (collectionBean.getUTRNo().trim().length() != 6) {
                    collCreateView.errorUTRNoOrChequeDD(mContext.getString(R.string.coll_val_cheque_valid));
                    isNotError = false;
                }
            }
        }


        if (TextUtils.isEmpty(collectionBean.getAmount())) {
            collCreateView.errorAmount("Enter Amount");
            isNotError = false;
        } else {
            if (!TextUtils.isEmpty(collectionBean.getRefTypeID()) && collectionBean.getRefTypeID().equalsIgnoreCase("01")) {
                if (collectionBean.getAmount().equalsIgnoreCase("") || collectionBean.getAmount().equalsIgnoreCase(".")) {
                    isNotError = false;
                    collCreateView.errorCollScreen(mContext.getString(R.string.alert_enter_valid_amount));
                } else if (Double.parseDouble(collectionBean.getAmount()) <= 0) {
                    isNotError = false;
                    collCreateView.errorCollScreen(mContext.getString(R.string.alert_enter_valid_amount));
                } else if (Double.parseDouble(collectionBean.getOutstandingAmount()) <= 0) {
                    isNotError = false;
                    collCreateView.errorCollScreen(mContext.getString(R.string.alert_enter_outstnding_amount_not_there));
                } else if (Double.parseDouble(collectionBean.getOutstandingAmount()) < Double.parseDouble(collectionBean.getAmount())) {
                    isNotError = false;
                    collCreateView.errorCollScreen(mContext.getString(R.string.alert_amt_greater_than_out_amt));
                }
            } else {
                if (collectionBean.getAmount().equalsIgnoreCase("") || collectionBean.getAmount().equalsIgnoreCase(".")) {
                    isNotError = false;
                    collCreateView.errorCollScreen(mContext.getString(R.string.alert_enter_valid_amount));
                } else if (Double.parseDouble(collectionBean.getAmount()) <= 0) {
                    isNotError = false;
                    collCreateView.errorCollScreen(mContext.getString(R.string.alert_enter_valid_amount));
                }
            }
        }
        return isNotError;
    }

    @Override
    public boolean validateOutstanding(ArrayList<InvoiceBean> selectedInvoice, CollectionBean collectionBean) {

        mDoubleTotalInvSum = 0.0;
        boolean errorFlag = false;
        arrtable = new ArrayList<HashMap<String, String>>();
        GUID guid = GUID.newRandom();
        collectionBean.setFIPGUID(guid.toString36().toUpperCase());
        for (int i = 0; i < selectedInvoice.size(); i++) {
            if (selectedInvoice.get(i).isItemSelected() && Double.parseDouble(selectedInvoice.get(i).getInputInvAmount().equalsIgnoreCase("") ? "0" : selectedInvoice.get(i).getInputInvAmount().equalsIgnoreCase(".") ? "0" : selectedInvoice.get(i).getInputInvAmount()) > 0) {
                if (Double.parseDouble(UtilConstants.removeLeadingZero(selectedInvoice.get(i).getInvoiceOutstanding())) >= Double.parseDouble(UtilConstants.removeLeadingZero(selectedInvoice.get(i).getInputInvAmount()).equalsIgnoreCase("") ? "0" : UtilConstants.removeLeadingZero(selectedInvoice.get(i).getInputInvAmount()))) {

                    InvoiceBean invoiceItems = selectedInvoice.get(i);
                    BigDecimal fipAmount = new BigDecimal(invoiceItems.getInputInvAmount());
                    try {
                        if (!TextUtils.isEmpty(invoiceItems.getDiscountAmt())) {
                            fipAmount = fipAmount.subtract(new BigDecimal(invoiceItems.getDiscountAmt()));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (fipAmount.compareTo(new BigDecimal("0")) != -1) {
                        GUID guidItem = GUID.newRandom();


                        HashMap<String, String> singleItem = new HashMap<>();

                        singleItem.put(Constants.ReferenceID, invoiceItems.getInvoiceGUID().replace("-", ""));
                        singleItem.put(Constants.InvoiceNo, invoiceItems.getInvoiceNo());
                        singleItem.put(Constants.Amount, String.valueOf(ConstantsUtils.decimalRoundOff(new BigDecimal(invoiceItems.getInvoiceAmount()), 2)));

                        singleItem.put(Constants.OutstandingAmt, invoiceItems.getInvoiceOutstanding());
                        singleItem.put(Constants.CollectionAmount, String.valueOf(ConstantsUtils.decimalRoundOff(new BigDecimal(invoiceItems.getCollectionAmount()), 2)));

                        singleItem.put(Constants.FIPAmount, String.valueOf(ConstantsUtils.decimalRoundOff(fipAmount, 2)));

                        singleItem.put(Constants.FIPItemGUID, guidItem.toString());
                        singleItem.put(Constants.FIPGUID, collectionBean.getFIPGUID());
                        singleItem.put(Constants.Currency, collectionBean.getCurrency());
                        singleItem.put(Constants.ClearedAmount, String.valueOf(ConstantsUtils.decimalRoundOff(new BigDecimal(invoiceItems.getInputInvAmount()), 2)));
                        singleItem.put(Constants.DebitCredit, Constants.H);
                        singleItem.put(Constants.ReferenceTypeID, collectionBean.getRefTypeID());
                        singleItem.put(Constants.ReferenceTypeDesc, collectionBean.getRefTypeDesc());

                        singleItem.put(Constants.InstrumentNo, collectionBean.getUTRNo());
                        singleItem.put(Constants.PaymentModeID, collectionBean.getPaymentModeID());
                        singleItem.put(Constants.PaymetModeDesc, collectionBean.getPaymentModeDesc());
                        if(!TextUtils.isEmpty(collectionBean.getChequeDate())) {
                            singleItem.put(Constants.InstrumentDate, collectionBean.getChequeDate() + "T00:00:00");
                        }
                        singleItem.put(Constants.FIPDate, collectionBean.getCollDate());
                        singleItem.put(Constants.BeatGUID, collectionBean.getBeatGuid());
                        if (!TextUtils.isEmpty(invoiceItems.getDiscountPer()))
                            singleItem.put(Constants.CashDiscountPercentage, String.valueOf(ConstantsUtils.decimalRoundOff(new BigDecimal(invoiceItems.getDiscountPer()), 3)));
                        if (!TextUtils.isEmpty(invoiceItems.getDiscountAmt()))
                            singleItem.put(Constants.CashDiscount, String.valueOf(ConstantsUtils.decimalRoundOff(new BigDecimal(invoiceItems.getDiscountAmt()), 2)));
//                    singleItem.put(Constants.CashDiscountAmount, invoiceItems.getDiscountEnteredAmt());

                        mDoubleTotalInvSum = mDoubleTotalInvSum + Double.parseDouble(UtilConstants.removeLeadingZero(selectedInvoice.get(i).getInputInvAmount()));

                        arrtable.add(singleItem);
                    } else {
                        errorFlag = true;
                    }
                } else {
                    errorFlag = true;
                }

            }
        }

        double mDoubleBundleTotalInvAmt = 0;
        try {
            mDoubleBundleTotalInvAmt = Double.parseDouble(collectionBean.getBundleTotalInvAmt());
        } catch (NumberFormatException e) {
            mDoubleBundleTotalInvAmt = 0.0;
            e.printStackTrace();
        }
        if (errorFlag) {
            collCreateView.errorInvoiceScreen(mContext.getString(R.string.alert_amt_greater_than_bal_amt));
        } else if (collectionBean.getRefTypeID().equalsIgnoreCase(Constants.str_05)) {
            errorFlag = false;
        } else if (mDoubleBundleTotalInvAmt == Double.parseDouble(UtilConstants.removeLeadingZero(mDoubleTotalInvSum + ""))) {
            errorFlag = false;
        } else {
            errorFlag = true;
            collCreateView.errorInvoiceScreen(mContext.getString(R.string.alert_enter_valid_coll_amount));
        }
        return errorFlag;
    }


    @Override
    public void onAsignData(String save, String strRefType, CollectionBean collectionBean, String comingFrom) {
        if (comingFrom.equalsIgnoreCase(Constants.str_01)) {
            assignDataToHashTable("", strRefType, collectionBean);
        } else {
            assignDataVar("", strRefType, collectionBean);
        }

    }


    @Override
    public void onSaveData() {
        getLocation();
    }

    private void assignDataToHashTable(String save, String strRefType, CollectionBean collectionBean) {
        String doc_no = (System.currentTimeMillis() + "");

        arrtable = new ArrayList<HashMap<String, String>>();
        String[] spGuidName = null;
        String spGuid = "";
        String fristName = "";
        try{
            spGuidName = Constants.getSPGUIDName();
            if(spGuidName != null){
                spGuid = spGuidName[0];
                fristName = spGuidName[1];
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        HashMap<String, String> singleItem = new HashMap<String, String>();

        GUID guidItem = GUID.newRandom();
        GUID guid = GUID.newRandom();

        singleItem.put(Constants.FIPItemGUID, guidItem.toString());
        singleItem.put(Constants.FIPGUID, guid.toString());

        singleItem.put(Constants.ReferenceID, "");
        singleItem.put(Constants.FIPAmount, String.valueOf(ConstantsUtils.decimalRoundOff(new BigDecimal(collectionBean.getAmount()),2)));
        singleItem.put(Constants.ReferenceTypeID, collectionBean.getRefTypeID());
        singleItem.put(Constants.ReferenceTypeDesc, collectionBean.getRefTypeDesc());
        singleItem.put(Constants.DebitCredit, Constants.H);
        singleItem.put(Constants.Currency, collectionBean.getCurrency());
        singleItem.put(Constants.InstrumentNo, collectionBean.getInstrumentNo());
        if (!collectionBean.getRefTypeID().equalsIgnoreCase(Constants.str_05)) {
            singleItem.put(Constants.PaymentModeID, collectionBean.getPaymentModeID());
            singleItem.put(Constants.PaymetModeDesc, collectionBean.getPaymentModeDesc());
        } else {
            singleItem.put(Constants.PaymentModeID, "");
            singleItem.put(Constants.PaymetModeDesc, "");
        }
        singleItem.put(Constants.FIPDate, collectionBean.getCollDate());

        singleItem.put(Constants.BeatGUID, collectionBean.getBeatGuid());
        if (!collectionBean.getPaymentModeID().equalsIgnoreCase(Constants.str_04)) {
            singleItem.put(Constants.InstrumentDate, collectionBean.getInstrumentDate());
        } else {
            singleItem.put(Constants.InstrumentDate, "");
        }
        singleItem.put(Constants.CashDiscountPercentage, "");
        singleItem.put(Constants.CashDiscount, "");
        arrtable.add(singleItem);

        masterHeaderTable = new Hashtable();

        masterHeaderTable.put(Constants.FIPDocNo, doc_no);

        masterHeaderTable.put(Constants.CPNo, collectionBean.getCPNo());
        masterHeaderTable.put(Constants.BankID, collectionBean.getBankID());
        masterHeaderTable.put(Constants.BankName, collectionBean.getBankName());
        masterHeaderTable.put(Constants.InstrumentNo, collectionBean.getUTRNo());
        masterHeaderTable.put(Constants.Amount, Double.parseDouble(collectionBean.getAmount()) + "");
        masterHeaderTable.put(Constants.Remarks, collectionBean.getRemarks());

        masterHeaderTable.put(Constants.FIPDocType, Constants.str_05);

        if (!collectionBean.getRefTypeID().equalsIgnoreCase(Constants.str_05)) {
            masterHeaderTable.put(Constants.PaymentModeID, collectionBean.getPaymentModeID());
            masterHeaderTable.put(Constants.PaymentModeDesc, collectionBean.getPaymentModeDesc());
        } else {
            masterHeaderTable.put(Constants.PaymentModeID, "");
            masterHeaderTable.put(Constants.PaymentModeDesc, "");
        }
        try {
            masterHeaderTable.put(Constants.SPGuid, spGuid);
        } catch (Exception e) {
            masterHeaderTable.put(Constants.SPGuid, Constants.getSPGUID());
            e.printStackTrace();
        }
        masterHeaderTable.put(Constants.FIPDate, collectionBean.getCollDate());

        if (!collectionBean.getPaymentModeID().equalsIgnoreCase(Constants.str_04)) {
            masterHeaderTable.put(Constants.InstrumentDate, collectionBean.getChequeDate());
        } else {
            masterHeaderTable.put(Constants.InstrumentDate, "");
        }
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(Constants.PREFS_NAME, 0);
        String loginIdVal = sharedPreferences.getString(Constants.username, "");

        masterHeaderTable.put(Constants.LOGINID, "");
        masterHeaderTable.put(Constants.BranchName, collectionBean.getBranchName());

        masterHeaderTable.put(Constants.Source, Constants.Source_SFA);

        masterHeaderTable.put(Constants.CPName, collectionBean.getCPName());
        masterHeaderTable.put(Constants.ParentNo, collectionBean.getParentID());
        try {
            masterHeaderTable.put(Constants.ParentTypeID, collectionBean.getParentTypeID());
        } catch (Exception e) {
            e.printStackTrace();
        }
        masterHeaderTable.put(Constants.SPNo, collectionBean.getSpNo());
        try {
            masterHeaderTable.put(Constants.SPFirstName, fristName);
        } catch (Exception e) {
            masterHeaderTable.put(Constants.SPFirstName, fristName);
            e.printStackTrace();
        }
        masterHeaderTable.put(Constants.Currency, collectionBean.getCurrency());
        masterHeaderTable.put(Constants.CPTypeID, Constants.str_02);
        masterHeaderTable.put(Constants.ReferenceTypeDesc, collectionBean.getRefTypeDesc());
        masterHeaderTable.put(Constants.ReferenceTypeID, collectionBean.getRefTypeID());

        masterHeaderTable.put(Constants.FIPGUID, guid.toString());
        masterHeaderTable.put(Constants.CPGUID, collectionBean.getCPGUID());


        if (!collectionBean.getBeatGuid().equalsIgnoreCase("")) {
            masterHeaderTable.put(Constants.BeatGUID, collectionBean.getBeatGuid());
        } else {
            masterHeaderTable.put(Constants.BeatGUID, "");
        }

        masterHeaderTable.put(Constants.CreatedOn, UtilConstants.getNewDateTimeFormat());

        masterHeaderTable.put(Constants.CreatedAt, UtilConstants.getOdataDuration().toString());

        masterHeaderTable.put(Constants.EntityType, Constants.FinancialPostings);

        masterHeaderTable.put(Constants.ITEM_TXT, UtilConstants.convertArrListToGsonString(arrtable));

        if (collCreateView != null) {
            collCreateView.conformationDialog(mContext.getString(R.string.collection_save_conformation_msg), 1);
        }

    }

    private void assignDataVar(String save, String strRefType, CollectionBean collectionBean) {

        String doc_no = (System.currentTimeMillis() + "");
        String[] spGuidName = null;
        String spGuid = "";
        String fristName = "";
        try{
            spGuidName = Constants.getSPGUIDName();
            if(spGuidName != null){
                spGuid = spGuidName[0];
                fristName = spGuidName[1];
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        masterHeaderTable.put(Constants.CPNo, collectionBean.getCPNo());
        masterHeaderTable.put(Constants.BankID, collectionBean.getBankID());
        masterHeaderTable.put(Constants.BankName, collectionBean.getBankName());
        masterHeaderTable.put(Constants.InstrumentNo, collectionBean.getUTRNo());
        masterHeaderTable.put(Constants.Amount, collectionBean.getAmount());
        masterHeaderTable.put(Constants.Remarks, collectionBean.getRemarks());
        masterHeaderTable.put(Constants.ReferenceTypeDesc, collectionBean.getRefTypeDesc());

        masterHeaderTable.put(Constants.FIPDocType, Constants.str_03);
        masterHeaderTable.put(Constants.FIPDocType1, Constants.str_01);
        masterHeaderTable.put(Constants.ReferenceTypeID, collectionBean.getRefTypeID());
        masterHeaderTable.put(Constants.PaymentModeID, collectionBean.getPaymentModeID());
        masterHeaderTable.put(Constants.PaymentModeDesc, collectionBean.getPaymentModeDesc());
        masterHeaderTable.put(Constants.FIPDocNo, doc_no);
        masterHeaderTable.put(Constants.DMSDivision, collectionBean.getDivision());

        masterHeaderTable.put(Constants.FIPDate, collectionBean.getCollDate());
        masterHeaderTable.put(Constants.CPGUID, collectionBean.getCPGUID());
        try {
            masterHeaderTable.put(Constants.SPGuid, spGuid);
        } catch (Exception e) {
            masterHeaderTable.put(Constants.SPGuid, Constants.getSPGUID());
            e.printStackTrace();
        }
        masterHeaderTable.put(Constants.InstrumentDate, collectionBean.getChequeDate());

        masterHeaderTable.put(Constants.CPTypeID, Constants.str_02);
        masterHeaderTable.put(Constants.CPName, collectionBean.getCPName());
        masterHeaderTable.put(Constants.ParentNo, collectionBean.getParentID());
        try {
            masterHeaderTable.put(Constants.ParentTypeID, collectionBean.getParentTypeID());
        } catch (Exception e) {
            e.printStackTrace();
        }
        masterHeaderTable.put(Constants.SPNo, collectionBean.getSpNo());
        try {
            masterHeaderTable.put(Constants.SPFirstName, fristName);
        } catch (Exception e) {
            masterHeaderTable.put(Constants.SPFirstName, fristName);
            e.printStackTrace();
        }
        masterHeaderTable.put(Constants.Currency, collectionBean.getCurrency());
        masterHeaderTable.put(Constants.BranchName, collectionBean.getBranchName());


        SharedPreferences sharedPreferences = mContext.getSharedPreferences(Constants.PREFS_NAME, 0);
        String loginIdVal = sharedPreferences.getString(Constants.username, "");

        masterHeaderTable.put(Constants.LOGINID, loginIdVal);

        masterHeaderTable.put(Constants.CreatedOn, UtilConstants.getNewDateTimeFormat());

        masterHeaderTable.put(Constants.CreatedAt, UtilConstants.getOdataDuration().toString());

        masterHeaderTable.put(Constants.FIPGUID, collectionBean.getFIPGUID());
        masterHeaderTable.put(Constants.EntityType, Constants.FinancialPostings);

        masterHeaderTable.put(Constants.FIPDocNo, doc_no);

        masterHeaderTable.put(Constants.ITEM_TXT, UtilConstants.convertArrListToGsonString(arrtable));

        if (!collectionBean.getBeatGuid().equalsIgnoreCase("")) {
            masterHeaderTable.put(Constants.BeatGUID, collectionBean.getBeatGuid());
        } else {
            masterHeaderTable.put(Constants.BeatGUID, "");
        }

        if (collectionBean.getRefTypeID().equalsIgnoreCase(Constants.str_05)) {
            masterHeaderTable.put(Constants.Amount, mDoubleTotalInvSum + "");
        } else {
            masterHeaderTable.put(Constants.Amount, collectionBean.getBundleTotalInvAmt() + "");
        }

        masterHeaderTable.put(Constants.Source, Constants.Source_SFA);

        if (collCreateView != null) {
            collCreateView.conformationDialog(mContext.getString(R.string.collection_save_conformation_msg), 1);
        }
    }

    private void finalSaveCondition() {
        Bundle bundle = new Bundle();
        if (collCreateView != null) {
            collCreateView.showProgressDialog(mContext.getString(R.string.saving_data_wait));
        }
        bundle.putInt(Constants.BUNDLE_REQUEST_CODE, 1);
        onSave();
    }

    private void onSave() {

        Constants.saveDeviceDocNoToSharedPref(mContext, Constants.FinancialPostings, masterHeaderTable.get(Constants.FIPDocNo));

        JSONObject jsonHeaderObject = new JSONObject(masterHeaderTable);

        ConstantsUtils.storeInDataVault(masterHeaderTable.get(Constants.FIPDocNo), jsonHeaderObject.toString(),mContext);

        Constants.onVisitActivityUpdate(mContext, collectionBean.getCPGUID32(),
                masterHeaderTable.get(Constants.FIPGUID),
                Constants.CollCreateID, Constants.FinancialPostings, mStartTimeDuration);

        navigateToDetails();
    }

    private void navigateToDetails() {
        if (collCreateView != null) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    collCreateView.hideProgressDialog();
                    collCreateView.showMessage(mContext.getString(R.string.msg_coll_created), false);
                }
            });
        }
    }

    private void requestCollType() {
        if (collCreateView != null) {
            collCreateView.showProgressDialog(mContext.getString(R.string.app_loading));
        }
        currentRequest = 0;
        totalRequest = 4;

        String mStrDistQry = Constants.CPDMSDivisions+ "?$filter=" + Constants.CPNo + " eq '"+collectionBean.getCPNo() +"' and "+Constants.ParentID +" eq '"+collectionBean.getParentID()+"' &$orderby=DMSDivision asc";
        ConstantsUtils.onlineRequest(mContext, mStrDistQry, isSessionRequired,5 , ConstantsUtils.SESSION_HEADER, this, false);

        String mStrConfigQry = Constants.ValueHelps + "?$filter=" + Constants.PropName + " eq '" + Constants.ReferenceTypeID + "' and " + Constants.EntityType + " eq 'FinancialPostingItemDetail'";
        ConstantsUtils.onlineRequest(mContext, mStrConfigQry, isSessionRequired, 1, ConstantsUtils.SESSION_HEADER, this, false);

        mStrConfigQry = Constants.ValueHelps + "?$filter=" + Constants.PropName + " eq '" + Constants.PaymentModeID + "' and " + Constants.EntityType + " eq 'FinancialPosting' ";
        ConstantsUtils.onlineRequest(mContext, mStrConfigQry, isSessionRequired, 2, ConstantsUtils.SESSION_HEADER, this, false);

        mStrConfigQry = Constants.ValueHelps + "?$filter=" + Constants.PropName + " eq '" + Constants.BankID + "' " +
                "&$orderby=" + Constants.Description + "%20asc";
        ConstantsUtils.onlineRequest(mContext, mStrConfigQry, isSessionRequired, 3, ConstantsUtils.SESSION_HEADER, this, false);
    }


    @Override
    public void responseSuccess(ODataRequestExecution oDataRequestExecution, List<ODataEntity> list, Bundle bundle) {
        int type = bundle != null ? bundle.getInt(Constants.BUNDLE_REQUEST_CODE) : 0;
        switch (type) {
            case 1:
                currentRequest++;
                alCollRefType.clear();
                try {
                    alCollRefType.addAll(OfflineManager.getConfigListFromValueHelp(list, Constants.ReferenceTypeID));
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }

                break;
            case 2:
                currentRequest++;
                alCollPaymentMode.clear();
                try {
                    alCollPaymentMode.addAll(OfflineManager.getConfigListFromValueHelp(list, Constants.PaymentModeID));
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }

                break;
            case 3:
                currentRequest++;
                alCollBankNames.clear();
                try {
                    alCollBankNames.addAll(OfflineManager.getConfigListFromValueHelp(list, Constants.BankID));
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }

                break;

            case 4:
                currentRequest++;
                alInvoiceList.clear();
                try {
                    alInvoiceList.addAll(OfflineManager.getInvoices(list, "", collectionBean.getCPNo(), mContext));
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }
                break;

            case 5:
                currentRequest++;
                alDmsDivision.clear();
                try {
                    alDmsDivision.addAll(OfflineManager.getRetailerBAseDmsDivision(list));
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }
                break;
        }
        if (totalRequest == currentRequest) {

            if (type == 4) {
                ((Activity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (collCreateView != null) {
                            collCreateView.hideProgressDialog();
                            collCreateView.displayInvoiceData(alInvoiceList);
                        }
                    }
                });
            } else {
                mArrayDistributors = Constants.getDistributorsByCPGUID(mContext, Constants.convertStrGUID32to36(collectionBean.getCPGUID32()),collectionBean.getParentID());
                mArraySPValues = Constants.getSPValesFromCPDMSDivisionByCPGUIDAndDMSDivision(Constants.convertStrGUID32to36(collectionBean.getCPGUID32()),mContext);
                String mRouteSchGuid = Constants.getRouteSchGUID(Constants.RouteSchedulePlans, Constants.RouteSchGUID,
                        Constants.VisitCPGUID, collectionBean.getCPGUID32(), mArrayDistributors[5][0]);
                collectionBean.setCurrency(mArrayDistributors[10][0]);
                collectionBean.setAdvanceAmount(getAdvanceAmt());
                collectionBean.setRouteSchGuid(mRouteSchGuid);
                collectionBean.setSPGUID(mArrayDistributors[0][0]);
                collectionBean.setSpNo(mArrayDistributors[2][0]);
                collectionBean.setCPTypeID(mArrayDistributors[8][0]);
                collectionBean.setParentID(mArrayDistributors[4][0]);
                collectionBean.setParentTypeID(mArrayDistributors[5][0]);
                collectionBean.setCurrency(mArrayDistributors[10][0]);
                collectionBean.setParentName(mArrayDistributors[7][0]);
                collectionBean.setSpFirstName(mArrayDistributors[3][0]);
//                getOutStandingAmount();
                ((Activity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (collCreateView != null) {
                            collCreateView.hideProgressDialog();
                            collCreateView.displayByCollectionData(alCollRefType, alCollPaymentMode, alCollBankNames, String.valueOf(totalOutVal),alDmsDivision);
                        }
                    }
                });
            }

        }
    }

    @Override
    public void responseFailed(ODataRequestExecution oDataRequestExecution, final String s, Bundle bundle) {
        currentRequest++;
        if (totalRequest == currentRequest) {
            ((Activity) mContext).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (collCreateView != null) {
                        collCreateView.hideProgressDialog();
                        collCreateView.displayMessage(s);
                    }
                }
            });
        }
    }

    public void getOutStandingAmount(String divisionID) {

        try {
//            alOutstandingsBean = OfflineManager.getOutstandingList(Constants.SSOutstandingInvoices + "?$filter=" + Constants.SoldToID + " eq " +
//                    "'" + collectionBean.getCPNo() + "'" + " and " + Constants.PaymentStatusID + " ne '" + "03" + "' and StatusID eq '03' and "+Constants.DMSDivisionID+" eq '"+divisionID+"'", mContext, "", collectionBean.getCPNo());
            alOutstandingsBean = OfflineManager.getOutstandingList(Constants.SSOutstandingInvoices + "?$filter=" + Constants.SoldToID + " eq " +
                    "'" + collectionBean.getCPNo() + "'" + " and " + Constants.PaymentStatusID + " ne '" + "03" + "' and (StatusID eq '03' or StatusID eq '04' or StatusID eq '05') and "+Constants.DMSDivisionID+" eq '"+divisionID+"'", mContext, "", collectionBean.getCPNo());
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
       /* BigDecimal mdouDevCollAmt = new BigDecimal(0.0);
        try {
            mdouDevCollAmt = OfflineManager.getDeviceCollAmt(mContext, collectionBean.getCPNo());
        } catch (Exception e) {
            e.printStackTrace();
        }*/


        totalOutVal = new BigDecimal(0.0);
        for (OutstandingBean invoice : alOutstandingsBean) {
            totalOutVal = totalOutVal.add(new BigDecimal(invoice.getInvoiceBalanceAmount()));
        }

        ((Activity) mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (collCreateView != null) {
                    collCreateView.hideProgressDialog();
                    collCreateView.displayOutstandingAmount(String.valueOf(totalOutVal));
                }
            }
        });
//        totalOutVal = totalOutVal.subtract(mdouDevCollAmt);
       /* if (totalOutVal.compareTo(new BigDecimal(0.0))!=1){
            totalOutVal=new BigDecimal("0.0");
        }*/

    }

    private void getLocation() {
        if (collCreateView != null) {
            collCreateView.showProgressDialog(mContext.getString(R.string.checking_pemission));
            LocationUtils.checkLocationPermission(mActivity, new LocationInterface() {
                @Override
                public void location(boolean status, LocationModel location, String errorMsg, int errorCode) {
                    if (collCreateView != null) {
                        collCreateView.hideProgressDialog();
                    }
                    if (status) {
                        locationPerGranted();
                    }
                }
            });
        }
    }

    private void locationPerGranted() {
        if (collCreateView != null) {
            collCreateView.showProgressDialog(mContext.getString(R.string.checking_pemission));
            Constants.getLocation(mActivity, new LocationInterface() {
                @Override
                public void location(boolean status, LocationModel locationModel, String errorMsg, int errorCode) {
                    if (collCreateView != null) {
                        collCreateView.hideProgressDialog();
                    }
                    if (status) {
                        if (ConstantsUtils.isAutomaticTimeZone(mContext)) {
                            finalSaveCondition();
                        } else {
                            if (collCreateView != null)
                                ConstantsUtils.showAutoDateSetDialog(mActivity);
                        }
                    }
                }
            });
        }
    }

    private double getAdvanceAmt() {
        double mDouAdvanceAmt = 0.0;
        try {
            double mDouDevAdvAmt = OfflineManager.getDeviceAdvAmtOrAdjustAmt(mContext, collectionBean.getCPNo(), Constants.str_02);
            double mDouAdvAmtFromCP = OfflineManager.getAdvnceAmtFromCP(Constants.ChannelPartners + "?$filter=" + Constants.CPGUID + " eq guid'"
                    + collectionBean.getCPGUID() + "' ", Constants.OpenAdvanceAmt);
            double mDouDevAdvAdjAmt = OfflineManager.getDeviceAdvAmtOrAdjustAmt(mContext, collectionBean.getCPNo(), Constants.str_05);

            mDouAdvanceAmt = mDouDevAdvAmt + mDouAdvAmtFromCP - mDouDevAdvAdjAmt;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return mDouAdvanceAmt;
    }
}
