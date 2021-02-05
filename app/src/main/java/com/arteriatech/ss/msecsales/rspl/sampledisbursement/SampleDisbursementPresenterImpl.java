package com.arteriatech.ss.msecsales.rspl.sampledisbursement;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.text.TextUtils;
import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.datavault.UtilDataVault;
import com.arteriatech.mutils.interfaces.DialogCallBack;
import com.arteriatech.mutils.location.LocationInterface;
import com.arteriatech.mutils.location.LocationModel;
import com.arteriatech.mutils.location.LocationUtils;
import com.arteriatech.mutils.log.LogManager;
import com.arteriatech.ss.msecsales.rspl.R;
import com.arteriatech.ss.msecsales.rspl.common.Constants;
import com.arteriatech.ss.msecsales.rspl.common.ConstantsUtils;
import com.arteriatech.ss.msecsales.rspl.dbstock.stockmaterial.DMSDivisionBean;
import com.arteriatech.ss.msecsales.rspl.mbo.RetailerStockBean;
import com.arteriatech.ss.msecsales.rspl.retailertrends.RetailerTrendView;
import com.arteriatech.ss.msecsales.rspl.sampledisbursement.addsampledisbursement.AddSampleDisbursementActivity;
import com.arteriatech.ss.msecsales.rspl.store.OfflineManager;
import com.sap.client.odata.v4.core.GUID;
import com.sap.smp.client.httpc.events.IReceiveEvent;
import com.sap.smp.client.odata.ODataEntity;

import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

public class SampleDisbursementPresenterImpl implements SampleDisbursementPresenter {
    SampleDisbursementView sampleDisbursementView;
    Context context;
    Activity activity;
    String mStrBundleCPGUID32;
    String mStrBundleCPGUID;
    private Hashtable<String, String> headerTable = new Hashtable<>();
    private ArrayList<DMSDivisionBean> distListDms=null;
    private String[][] mArrayDistributors = null;
    private String[][] mArraySPValues = null;
    private String[][] mArrayCPDMSDivisoins = null;
    private String[][] mArrayInvoiceTypeId = null;
    private ArrayList<RetailerStockBean> retailerSearchStockBeanArrayList = new ArrayList<>();
    private ArrayList<RetailerStockBean> retailerStockBeanTotalArrayList = new ArrayList<>();
    private ArrayList<RetailerStockBean> retailerStockBeanPopupArrayList = new ArrayList<>();
    private ArrayList<RetailerStockBean> retailerStockBeanfinalArrayList = new ArrayList<>();
    private ProgressDialog pdLoadDialog = null;
    private String mStrStockOwner ="";
    String mStrBundleRetName;
    String mStrBundleRetID;
    String division="";
    String cpno="";
    String distubutorID="";
    String parentID="";
    private ArrayList<com.arteriatech.ss.msecsales.rspl.mbo.DMSDivisionBean> alDmsDivision = new ArrayList<>();
    private ArrayList<RetailerStockBean> retailerStockBeanAddedDataArrayList = new ArrayList<>();
    public  SampleDisbursementPresenterImpl(Context context, Activity activity,String mStrBundleCPGUID32,String mStrBundleCPGUID,String cpno,String distubutorID,String parentID)
    {
        this.activity=activity;
        this.context=context;
        this.mStrBundleCPGUID32=mStrBundleCPGUID32;
        this.mStrBundleCPGUID=mStrBundleCPGUID;
        this.parentID=parentID;
        this.distubutorID=distubutorID;
        this.cpno=cpno;
        if (context instanceof SampleDisbursementView) {
            sampleDisbursementView = (SampleDisbursementView) context;
        }
    }

    public void getCPSPRelationDivisions(String parentID) {
//        mArraySPValues = Constants.getSPValesFromCPDMSDivisionByCPGUIDAndDMSDivision(Constants.convertStrGUID32to36(soCreateBean.getCPGUID()),mContext);
//        String mStrDistQry = Constants.CPSPRelations+ "?$filter=" + Constants.CPGUID + " eq '"+mArraySPValues[1][0] +"'";
        ArrayList<String> divisionAl = null;
        try {
            divisionAl = OfflineManager.getSaleAreaFromUsrAth("UserProfileAuthSet?$filter=Application%20eq%20%27MSEC%27"+" &$orderby=AuthOrgTypeID asc");
        } catch (Exception e) {
            e.printStackTrace();
        }
        String divisionQry = "";
        if(divisionAl!=null && !divisionAl.isEmpty()) {
            for (int i = 0; i < divisionAl.size();i++){
                if(i==divisionAl.size()-1) {
                    divisionQry = divisionQry + "DMSDivision eq '" + divisionAl.get(i)+"'";
                }else {
                    divisionQry = divisionQry + "DMSDivision eq '" + divisionAl.get(i) + "' or ";
                }
            }
        }
        String mStrDistQry="";
        List<ODataEntity> entities = null;
        if(!TextUtils.isEmpty(divisionQry)) {
            mStrDistQry = Constants.CPDMSDivisions + "?$filter=" + Constants.CPNo + " eq '" + cpno + "' and ("+divisionQry+") and "+Constants.ParentID +" eq '"+parentID+"' &$orderby=DMSDivision asc";

            try {
                entities = Constants.getListEntities(mStrDistQry, OfflineManager.offlineStore);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        alDmsDivision.clear();
        try {
//            alDmsDivision.addAll(OfflineManager.getDistributorsDmsDivision(entities));
            alDmsDivision.addAll(OfflineManager.getRetailerBAseDmsDivision(entities));
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }

        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (sampleDisbursementView != null) {
//                    soCreateView.hideProgressDialog();
                    sampleDisbursementView.displayDMSDivision(alDmsDivision);
                }
            }
        });
    }

    @Override
    public void start(String mStrBundleRetID,String mStrBundleRetName,String division) {
        this.mStrBundleRetName=mStrBundleRetName;
        this.mStrBundleRetID=mStrBundleRetID;
        this.division=division;
        Constants.mStartTimeDuration = UtilConstants.getOdataDuration();
        new GetSampleDisbursementList().execute();
    }
    private class GetSampleDisbursementList extends AsyncTask<String, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (sampleDisbursementView != null) {
                sampleDisbursementView.showProgress();
            }
        }
        @Override
        protected Void doInBackground(String... params) {
            getDistributorDMS();
            getCPDMSDivisions();
            getSalesPersonValues();
            getDistributorValues();
            getDataFromOfflineDB();
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            try {
                if (sampleDisbursementView != null) {
                    sampleDisbursementView.hideProgress();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            filter("");
        }
    }
    @Override
    public void filter(final String text) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (sampleDisbursementView != null) {
                            sampleDisbursementView.showProgress();
                        }

                    }
                });
//                retailerSearchStockBeanArrayList.clear();
                retailerSearchStockBeanArrayList = new ArrayList<>();
                if (TextUtils.isEmpty(text)) {
                    retailerSearchStockBeanArrayList.addAll(retailerStockBeanTotalArrayList);
                } else {
                    for (RetailerStockBean item : retailerStockBeanTotalArrayList) {

                        if (item.getMaterialDesc().toLowerCase().contains(text.toLowerCase())) {
                            retailerSearchStockBeanArrayList.add(item);
                        }
                    }
                }
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (sampleDisbursementView != null) {
                            Constants.checkSearch=false;
                            sampleDisbursementView.hideProgress();
                            sampleDisbursementView.displayList(retailerSearchStockBeanArrayList);
                        }

                    }
                });

            }
        }).start();
    }
    @Override
    public void seaarchAddedItem(final String text) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (sampleDisbursementView != null) {
                            sampleDisbursementView.showProgress();
                        }

                    }
                });
                retailerSearchStockBeanArrayList.clear();
                if (TextUtils.isEmpty(text)) {
                    retailerSearchStockBeanArrayList.addAll(retailerStockBeanAddedDataArrayList);
                } else {
                    for (RetailerStockBean item : retailerStockBeanAddedDataArrayList) {

                        if (item.getMaterialDesc().toLowerCase().contains(text.toLowerCase())) {
                            retailerSearchStockBeanArrayList.add(item);
                        }
                    }
                }
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (sampleDisbursementView != null) {
                            sampleDisbursementView.hideProgress();
                            Constants.checkSearch=true;
                            sampleDisbursementView.displayList(retailerSearchStockBeanArrayList);
                        }

                    }
                });

            }
        }).start();
    }
    @Override
    public void add(final ArrayList<RetailerStockBean> list, final String divisionID) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                retailerStockBeanTotalArrayList=list;
                retailerStockBeanPopupArrayList.clear();
                for (RetailerStockBean retailerStockBean : list) {
                    if (!retailerStockBean.getSelected()) {
                        retailerStockBeanPopupArrayList.add(retailerStockBean);
                    }
                }
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!retailerStockBeanPopupArrayList.isEmpty()) {
                            //display popup
                            Intent intent = new Intent(context, AddSampleDisbursementActivity.class);
                            intent.putExtra(ConstantsUtils.EXTRA_ARRAY_LIST, retailerStockBeanPopupArrayList);
                            intent.putExtra(Constants.DivisionID, divisionID);
                            activity.startActivityForResult(intent, AddSampleDisbursementActivity.SD_RESULT_ID);
                        } else {
                            Constants.dialogBoxWithButton(context, "", activity.getString(R.string.sample_disbursement_no_data), activity.getString(R.string.ok), "", null);
                        }
                    }
                });
            }
        }).start();
    }
    @Override
    public void validateFields(final ArrayList<RetailerStockBean> retailerStockBean) {
        retailerStockBeanfinalArrayList=retailerStockBean;
        if (sampleDisbursementView != null) {
            sampleDisbursementView.showProgress();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (sampleDisbursementView != null) {
                            sampleDisbursementView.hideProgress();
                            if (!retailerStockBean.isEmpty()) {
                                for (RetailerStockBean soItemBean : retailerStockBean) {
                                    if (TextUtils.isEmpty(soItemBean.getEnterdQty()) || (Double.parseDouble(soItemBean.getEnterdQty()) <=0)) {
                                        sampleDisbursementView.displayMsg(context.getString(R.string.retailer_stock_error_valid_qty));
                                        return;
                                    }else if(!TextUtils.isEmpty(soItemBean.getEnterdQty()) && !TextUtils.isEmpty(soItemBean.getUnrestrictedQty())){
                                        double enteredqty= Double.parseDouble(soItemBean.getEnterdQty());
                                        double unresqty = Double.parseDouble(soItemBean.getUnrestrictedQty());
                                        if(!TextUtils.isEmpty(soItemBean.getEnterdUOM()) && !soItemBean.getEnterdUOM().equalsIgnoreCase("PC")){
                                            if(enteredqty>unresqty) {
                                                sampleDisbursementView.displayMsg(context.getString(R.string.retailer_stock_error_valid_qty));
                                                return;
                                            }
                                        }else if(!TextUtils.isEmpty(soItemBean.getEnterdUOM()) && soItemBean.getEnterdUOM().equalsIgnoreCase("PC")){
                                            double altUomden= Double.parseDouble(soItemBean.getAlternativeUOM1Den());
                                            double altUomNum= Double.parseDouble(soItemBean.getAlternativeUOM1Num());
                                            double resultValue = ((enteredqty*altUomden)/altUomNum);

                                            resultValue = ConstantsUtils.decimalRoundOff(BigDecimal.valueOf(resultValue),2).doubleValue();
                                            if(resultValue>unresqty) {
                                                sampleDisbursementView.displayMsg(context.getString(R.string.retailer_stock_error_valid_qty));
                                                return;
                                            }
                                        }
                                    }
                                    if (TextUtils.isEmpty(soItemBean.getRemarks())) {
                                        sampleDisbursementView.displayMsg(context.getString(R.string.retailer_stock_error_valid_remarks));
                                        return;
                                    }
                                }
                           //      navigateReviewScreen(dlrStockBeanArrayList);
                                checkLocation();
                            } else {
                                sampleDisbursementView.displayMsg(context.getString(R.string.retailer_items_selected));
                            }
                        }
                    }
                });
            }
        }).start();
    }
    @Override
    public void addedList(ArrayList<RetailerStockBean> retailerStockBeanAddedDataArrayList) {
        this.retailerStockBeanAddedDataArrayList=retailerStockBeanAddedDataArrayList;
    }
    public void checkLocation()
    {
        pdLoadDialog = Constants.showProgressDialog(context, "", context.getString(R.string.checking_pemission));
        LocationUtils.checkLocationPermission(activity, new LocationInterface() {
            @Override
            public void location(boolean status, LocationModel location, String errorMsg, int errorCode) {
                closingProgressDialog();
                if (status) {
                    locationPerGranted();
                }
            }
        });
    }
    private void closingProgressDialog(){
        try {
            pdLoadDialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void locationPerGranted(){
        pdLoadDialog = Constants.showProgressDialog(
                context,"",context.getString(R.string.gps_progress));
        Constants.getLocation(activity, new LocationInterface() {
            @Override
            public void location(boolean status, LocationModel locationModel, String errorMsg, int errorCode) {
                closingProgressDialog();
                if(status){
                    onSaveDataIntoDataValt();
                }
            }
        });
    }
    private void onSaveDataIntoDataValt() {
        try {
            GUID sdGUID = GUID.newRandom();
            String mRouteSchGuid = Constants.getRouteSchGUID(Constants.RouteSchedulePlans,Constants.RouteSchGUID,Constants.VisitCPGUID,mStrBundleCPGUID32,mArrayDistributors[5][0]);
//            String doc_no = (System.currentTimeMillis() + "").substring(3, 10);
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
            SharedPreferences sharedPreferences = activity.getSharedPreferences(Constants.PREFS_NAME, 0);
            String userName = sharedPreferences.getString(Constants.username, "");
            headerTable.clear();
            headerTable.put(Constants.InvoiceGUID, sdGUID.toString36().toUpperCase());
            headerTable.put(Constants.LoginID, userName.toUpperCase());
            headerTable.put(Constants.CPGUID, mArrayDistributors[4][0]);
            headerTable.put(Constants.CPNo, mStrBundleRetID);
            headerTable.put(Constants.CPName, mStrBundleRetName);
            headerTable.put(Constants.CPTypeDesc, mArrayDistributors[9][0]);
            headerTable.put(Constants.CPTypeID, mArrayDistributors[5][0]);
            headerTable.put(Constants.Currency, mArrayDistributors[10][0]);
            headerTable.put(Constants.SPNo, mArraySPValues[6][0] != null ? mArraySPValues[6][0] : "");
            try {
                headerTable.put(Constants.SPName, fristName);
            } catch (Exception e) {
                headerTable.put(Constants.SPName, fristName);
                e.printStackTrace();
            }
            headerTable.put(Constants.DmsDivision, division);
//            headerTable.put(Constants.DmsDivision, mArrayCPDMSDivisoins[0][0] != null ? mArrayCPDMSDivisoins[0][0] : "");
//            headerTable.put(Constants.DmsDivisionDesc, mArrayCPDMSDivisoins[1][0] != null ? mArrayCPDMSDivisoins[1][0] : "");
            String mInvNo = doc_no.substring(3, 10);
            headerTable.put(Constants.InvoiceNo, mInvNo);
            headerTable.put(Constants.InvoiceTypeID, mArrayInvoiceTypeId[0][0]);
            headerTable.put(Constants.InvoiceTypeDesc, mArrayInvoiceTypeId[1][0]);
            headerTable.put(Constants.InvoiceDate, UtilConstants.getNewDateTimeFormat());
            headerTable.put(Constants.PONo, "");
            headerTable.put(Constants.PODate, UtilConstants.getNewDateTimeFormat());
            headerTable.put(Constants.BillToGuid, mStrBundleCPGUID);
            headerTable.put(Constants.ShipToCPGUID, mStrBundleCPGUID);
            headerTable.put(Constants.SoldToCPGUID, mStrBundleCPGUID);
            headerTable.put(Constants.Source, "MOBILE");
            headerTable.put(Constants.BeatGUID, mRouteSchGuid);
            headerTable.put(Constants.SoldToID, mStrBundleRetID);
            headerTable.put(Constants.SoldToName, mStrBundleRetName);
            headerTable.put(Constants.SoldToTypeID, Constants.str_02);
            headerTable.put(Constants.SoldToTypeDesc, mArrayDistributors[9][0]);
            try {
                headerTable.put(Constants.SPGUID, spGuid);
            } catch (Exception e) {
                headerTable.put(Constants.SPGUID, Constants.getSPGUID());
                e.printStackTrace();
            }
            ArrayList<HashMap<String, String>> sdItems = new ArrayList<HashMap<String, String>>();
            for (int itemIncVal = 0; itemIncVal < retailerStockBeanfinalArrayList.size(); itemIncVal++) {
                RetailerStockBean retailerStockBean = retailerStockBeanfinalArrayList.get(itemIncVal);
                HashMap<String, String> singleItem = new HashMap<String, String>();
                GUID ssoItemGuid = GUID.newRandom();
                singleItem.put(Constants.InvoiceItemGUID, ssoItemGuid.toString36().toUpperCase());
                singleItem.put(Constants.InvoiceGUID, sdGUID.toString36().toUpperCase());
                singleItem.put(Constants.ItemNo, ConstantsUtils.addZeroBeforeValue(itemIncVal + 1, ConstantsUtils.ITEM_MAX_LENGTH));
                singleItem.put(Constants.InvoiceNo, mInvNo);
                singleItem.put(Constants.Quantity, retailerStockBean.getEnterdQty());
                singleItem.put(Constants.MaterialNo, retailerStockBean.getMaterialNo());
                singleItem.put(Constants.MaterialDesc, retailerStockBean.getMaterialDesc());
                singleItem.put(Constants.StockGuid, retailerStockBean.getCPStockItemGUID());
                singleItem.put(Constants.UOM, retailerStockBean.getEnterdUOM());
                singleItem.put(Constants.Currency, mArrayDistributors[10][0]);
                singleItem.put(Constants.UnitPrice, "0.0");
                singleItem.put(Constants.GrossAmount, "0.0");
                singleItem.put(Constants.Tax, "0.0");
                singleItem.put(Constants.Discount, "0.0");
                singleItem.put(Constants.Freight, "0.0");
                singleItem.put(Constants.NetAmount, "0.0");
                singleItem.put(Constants.DiscountPer, "0.0");
                singleItem.put(Constants.MRP, "0.0");
                singleItem.put(Constants.Remarks, retailerStockBean.getRemarks());
                singleItem.put(Constants.DeletionInd, "");
                singleItem.put(Constants.Batch, "");
                singleItem.put(Constants.Division, "");
                singleItem.put(Constants.InvoiceDate, UtilConstants.getNewDateTimeFormat());
                singleItem.put(Constants.BeatGUID, mRouteSchGuid);
                sdItems.add(singleItem);
            }
            Constants.onVisitActivityUpdate(mStrBundleCPGUID32, userName,
                    sdGUID.toString36().toUpperCase(), Constants.SampleDisbursementID, Constants.SampleDisbursementDesc);
            headerTable.put(Constants.entityType, Constants.SampleDisbursement);
            headerTable.put(Constants.ITEM_TXT, UtilConstants.convertArrListToGsonString(sdItems));
            Constants.saveDeviceDocNoToSharedPref(context, Constants.SampleDisbursement, doc_no);
            JSONObject jsonHeaderObject = new JSONObject(headerTable);

            ConstantsUtils.storeInDataVault(doc_no, jsonHeaderObject.toString(),context);
            navigateToVisit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


        public void navigateToVisit() {
            Constants.dialogBoxWithButton(context, "", context.getString(R.string.msg_sample_disbursement_created), context.getString(R.string.ok), "", new DialogCallBack() {
                @Override
                public void clickedStatus(boolean clickedStatus) {
                    activity.finish();
                }
            });

        }

    public void getDistributorDMS()
    {
        String spGuid = Constants.getSPGUID();
        try {
            String mStrDistQry = "";
            if (ConstantsUtils.getRollInformation(context).equalsIgnoreCase(ConstantsUtils.ROLLID_DSR_06)) {
                mStrDistQry= Constants.CPSPRelations+" ?$filter="+ Constants.SPGUID+" eq '"+spGuid.replace("-","").toUpperCase()+"' ";
            }else{
                mStrDistQry= Constants.CPSPRelations;
            }

//            String mStrDistQry= Constants.CPSPRelations;
            distListDms = OfflineManager.getDistributorsDms(mStrDistQry);
         /*   if(distList==null){
                distList =new String[5][1];
                distList[0][0] = "";
                distList[1][0] = "";
                distList[2][0] = "";
                distList[3][0] = "";
                distList[4][0] = "";
            }*/


        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.error_txt + e.getMessage());
        }
    }
    private void getCPDMSDivisions() {
        mArrayCPDMSDivisoins = Constants.getDMSDivisionByCPGUID(mStrBundleCPGUID,context);
    }
    private void getSalesPersonValues() {
        mArraySPValues = Constants.getSPValesFromCPDMSDivisionByCPGUIDAndDMSDivision(mStrBundleCPGUID,context);
    }
    private void getDistributorValues() {
//        mArrayDistributors = Constants.getDistributorsByCPGUID(mStrBundleCPGUID,context);
        mArrayDistributors = Constants.getDistributorsByCPGUID(context,mStrBundleCPGUID,parentID);
        mArrayInvoiceTypeId = OfflineManager.getInVoidTypeId();
    }
    private void getDataFromOfflineDB() {
        try {
            String qry = Constants.CPDMSDivisions+" ?$filter=CPNo eq '"+cpno+"' and "+Constants.CPGUID+" eq guid'"+mStrBundleCPGUID+"'";
            String parentID = OfflineManager.getParentID(qry);

            try {
                mStrStockOwner = distListDms.get(0).getStockOwner();
            } catch (Exception e) {
                mStrStockOwner = "";
            }
            retailerStockBeanTotalArrayList.clear();
            retailerSearchStockBeanArrayList.clear();
            String strDefaultUOM = Constants.getName(Constants.ConfigTypsetTypeValues, Constants.TypeValue, Constants.Types, Constants.CPSTKAUOM1);
            retailerStockBeanTotalArrayList = OfflineManager.getSampleCollectionList(Constants.CPStockItems + "?$orderby="+Constants.Material_Desc+" &$filter=StockOwner eq '"+mStrStockOwner+"' and "+Constants.DMSDivision+" eq '"+division+"' and CPGUID eq '"+parentID+"'", retailerStockBeanTotalArrayList,strDefaultUOM);
            Constants.addedSeaerch=false;
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
    }



}
