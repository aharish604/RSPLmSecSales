package com.arteriatech.ss.msecsales.rspl.reports.returnorder.invoiceselection;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.UIListener;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.store.OnlineODataInterface;
import com.arteriatech.ss.msecsales.rspl.R;
import com.arteriatech.ss.msecsales.rspl.common.Constants;
import com.arteriatech.ss.msecsales.rspl.mbo.CompetitorBean;
import com.arteriatech.ss.msecsales.rspl.mbo.DMSDivisionBean;
import com.arteriatech.ss.msecsales.rspl.mbo.InvoiceHistoryBean;
import com.arteriatech.ss.msecsales.rspl.mbo.ROCreateBean;
import com.arteriatech.ss.msecsales.rspl.mbo.ReturnOrderBean;
import com.arteriatech.ss.msecsales.rspl.reports.invoicelist.InvoiceListBean;
import com.arteriatech.ss.msecsales.rspl.reports.invoicelist.SOItemDetailsVH1;
import com.arteriatech.ss.msecsales.rspl.reports.returnorder.create.ROCreateActivity;
import com.arteriatech.ss.msecsales.rspl.reports.returnorder.create.ROCreateView;
import com.arteriatech.ss.msecsales.rspl.reports.returnorder.create.ROFilterDialogFragment;
import com.arteriatech.ss.msecsales.rspl.store.OfflineManager;
import com.sap.smp.client.odata.ODataDuration;
import com.sap.smp.client.odata.ODataEntity;
import com.sap.smp.client.odata.exception.ODataException;
import com.sap.smp.client.odata.store.ODataRequestExecution;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AddInvoiceItemCreatePresenterImpl implements AddInvoiceItemCreatePresenter, OnlineODataInterface, UIListener {
    // data members
    private Context mContext = null;
    private AddInvoiceItemCreateView addROCreateView = null;
    private boolean isSessionRequired = false;
    private Activity activity;
    private ArrayList<ReturnOrderBean> list;
    private ArrayList<ReturnOrderBean> searchBeanArrayList = new ArrayList<>();
    private String searchText = "";
    private String mStrSelBrandId = "";
    private String beatGUID = "";
    private String parentId = "";
    private String mStrSelCategoryId = "";
    private String mStrSelOrderMaterialID = "";
    private String dividionID = "";
    HashMap<String, String> mapUOM =null;
    private ROCreateBean roCreateBean;
    private ODataDuration mStartTimeDuration;
    private Activity mActivity;
    private ArrayList<DMSDivisionBean> alDmsDivision = new ArrayList<>();
    /**
     * @desc parameterized constructor to initialize required fields
     */
    public AddInvoiceItemCreatePresenterImpl(Context mContext, AddInvoiceItemCreateView roCreateView, boolean isSessionRequired, Activity mActivity, ROCreateBean roCreateBean,String beatGUID,String parentId) {
        this.mContext = mContext;
        this.mActivity = mActivity;
        this.addROCreateView = roCreateView;
        this.isSessionRequired = isSessionRequired;
        this.mStartTimeDuration = UtilConstants.getOdataDuration();
        this.roCreateBean = roCreateBean;
        this.beatGUID = beatGUID;
        this.parentId = parentId;
    }
    @Override
    public void onRequestError(int i, Exception e) {

    }

    @Override
    public void onRequestSuccess(int i, String s) throws ODataException, OfflineODataStoreException {

    }

    @Override
    public void responseSuccess(ODataRequestExecution oDataRequestExecution, List<ODataEntity> list, Bundle bundle) {

    }

    @Override
    public void responseFailed(ODataRequestExecution oDataRequestExecution, String s, Bundle bundle) {

    }

    @Override
    public void onStart() {
        addROCreateView.displayList(list);
    }

    public void getCPSPRelationDivisions() {
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
        List<ODataEntity> entities = null;
        String mStrDistQry="";
        if(!TextUtils.isEmpty(divisionQry)) {
            mStrDistQry = Constants.CPDMSDivisions + "?$filter=" + Constants.CPNo + " eq '" + roCreateBean.getCPNo() + "' and ("+divisionQry+") and "+Constants.ParentID +" eq '"+parentId+"' &$orderby=DMSDivision asc";
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

        ((Activity) mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (addROCreateView != null) {
//                    soCreateView.hideProgressDialog();
                    addROCreateView.displayDMSDivision(alDmsDivision);
                }
            }
        });
    }

    @Override
    public void getInvoiceList(String dividionID) {
        this.dividionID = dividionID;
        if (addROCreateView != null) {
            addROCreateView.showProgressDialog();
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                getDataFromDB();

                ((Activity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (addROCreateView != null) {
                            addROCreateView.hideProgressDialog();

                            addROCreateView.displayList(list);
                        }
                    }
                });
            }
        }).start();


       /* new Thread(new Runnable() {
            @Override
            public void run() {
                getDataFromDB();
                ((Activity) mActivity).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (addROCreateView != null) {
                            addROCreateView.hideProgressDialog();
                            addROCreateView.displayList(list);
                        }
                    }
                });
            }
        }).start();*/
    }
    private void getDataFromDB() {
        String qry = Constants.ConfigTypesetTypes + "?$filter=" + Constants.Typeset + " eq '" + Constants.UOMNO0 + "' ";
        try {
            mapUOM =new HashMap<>();
            mapUOM = OfflineManager.getUOMMapVal(qry);
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }

        String tempparentId="";
        if(!TextUtils.isEmpty(parentId)){
            tempparentId=String.valueOf(Integer.parseInt(parentId));
        }

        String[][] mArrayInvoiceTypeId = OfflineManager.getInVoidTypeId();
        String invQry= Constants.SSINVOICES + "?$filter=" + Constants.SoldToCPGUID + " eq guid'"
                + roCreateBean.getCPGUID()+ "' and " +Constants.CPGUID + " eq '" + tempparentId+"' and "+Constants.StatusID+" eq '03' and "+Constants.InvoiceTypeID+" ne '"+mArrayInvoiceTypeId[0][0]+"'";//

        System.out.println("SSInVoices Qry:"+invQry);

        ArrayList<InvoiceListBean> alInvList=null;
        try {
             alInvList= OfflineManager.getInvoiceHistoryList(invQry,mContext,"","");
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
        String mStrInvListQry = Constants.makeInvoiceListQry(alInvList,Constants.InvoiceGUID);
        List<ODataEntity> entities=null;
        if(mStrInvListQry!=null && !mStrInvListQry.equalsIgnoreCase("")){
            try {
                entities = Constants.getListEntities(Constants.SSInvoiceItemDetails + "?$filter= " + mStrInvListQry  ,OfflineManager.offlineStore);

//                entities = Constants.getListEntities(Constants.SSInvoiceItemDetails + "?$filter= " + mStrInvListQry+" and (ItemStatus eq'04' or ItemStatus eq'03')"  ,OfflineManager.offlineStore);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        try {
            list =new ArrayList<>();
            list = OfflineManager.getROInvoiceList(entities, list,mapUOM);
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onDestroy() {

    }

    @Override
    public boolean validateFields(ROCreateBean competitorBean) {
        return false;
    }

    @Override
    public boolean isValidMargin(String margin) {
        return false;
    }

    @Override
    public void onAsignData(CompetitorBean competitorBean) {

    }

    @Override
    public void onSaveData() {

    }

    @Override
    public void onSearch(String query) {
        this.searchText = query;
        onFilter();
    }

    private void onFilter() {
        searchBeanArrayList.clear();
        boolean brandStatus = false;
        boolean categoryStatus = false;
        boolean omgStatus = false;
        boolean soDelStatus = false;
        boolean soSearchStatus = false;
        if (list != null) {
            if (TextUtils.isEmpty(searchText) && (TextUtils.isEmpty(mStrSelBrandId) || mStrSelBrandId.equalsIgnoreCase(Constants.None)) && (TextUtils.isEmpty(mStrSelCategoryId) || mStrSelCategoryId.equalsIgnoreCase(Constants.None)) && (TextUtils.isEmpty(mStrSelOrderMaterialID) || mStrSelOrderMaterialID.equalsIgnoreCase(Constants.None))) {
                searchBeanArrayList.addAll(list);
            } else {
                for (ReturnOrderBean item : list) {
                    if (!TextUtils.isEmpty(mStrSelBrandId) && !mStrSelBrandId.equalsIgnoreCase(Constants.None)) {
                        brandStatus = item.getBrand().toLowerCase().contains(mStrSelBrandId.toLowerCase());
                    } else {
                        brandStatus = true;
                    }
                    if (!TextUtils.isEmpty(mStrSelCategoryId) && !mStrSelCategoryId.equalsIgnoreCase(Constants.None)) {
                        categoryStatus = item.getProductCategoryID().toLowerCase().contains(mStrSelCategoryId.toLowerCase());
                    } else {
                        categoryStatus = true;
                    }
                    if (!TextUtils.isEmpty(mStrSelOrderMaterialID) && !mStrSelOrderMaterialID.equalsIgnoreCase(Constants.None)) {
                        omgStatus = item.getOrderMaterialGroupID().toLowerCase().contains(mStrSelOrderMaterialID.toLowerCase());
                    } else {
                        omgStatus = true;
                    }

                    if (!TextUtils.isEmpty(searchText)) {
                        soSearchStatus = (item.getMaterialDesc().toLowerCase().contains(searchText.toLowerCase()) || item.getMaterialNo().toLowerCase().contains(searchText.toLowerCase()));
                    } else {
                        soSearchStatus = true;
                    }
                     if (brandStatus && categoryStatus && omgStatus && soSearchStatus)
                        searchBeanArrayList.add(item);
                }
            }
        }
        if (addROCreateView != null) {
            addROCreateView.searchResult(searchBeanArrayList);
        }
    }

    @Override
    public void onFragmentInteraction(String brand, String brandName, String category, String categoryName, String creskuGrp, String creskuGrpName) {
        this.mStrSelBrandId = brand;
        this.mStrSelCategoryId = category;
        this.mStrSelOrderMaterialID = creskuGrp;
        filterType(brandName, categoryName, creskuGrpName);
        onFilter();
    }

    @Override
    public Bundle openFilter() {
        Bundle bundle = new Bundle();
        bundle.putString(ROFilterDialogFragment.KEY_BRAND, mStrSelBrandId);
        bundle.putString(ROFilterDialogFragment.KEY_CATEGORY, mStrSelCategoryId);
        bundle.putString(ROFilterDialogFragment.KEY_SKUGRP, mStrSelOrderMaterialID);
        return bundle;
    }

    @Override
    public void sendResultToOtherActivity() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final ArrayList<ReturnOrderBean> finalSendResult = new ArrayList<>();
                if (list != null && list.size() > 0) {
                    for (ReturnOrderBean returnOrderBean : list) {
                        if (returnOrderBean.getSelected()) {
                            finalSendResult.add(returnOrderBean);
                        }
                    }
                    ((Activity) mContext).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (finalSendResult.size() > 0) {
                                roCreateBean.setRoList(finalSendResult);
                                roCreateBean.setDivision(dividionID);
                                Intent intentRetailerDetails = new Intent(mContext, ROCreateActivity.class);
                                intentRetailerDetails.putExtra(Constants.ROList, roCreateBean);
                                intentRetailerDetails.putExtra(Constants.BeatGUID, beatGUID);
                                intentRetailerDetails.putExtra(Constants.ParentId, parentId);
//                                intentRetailerDetails.putParcelableArrayListExtra(Constants.EXTRA_BEAN, finalSendResult);
                                mContext.startActivity(intentRetailerDetails);
                            } else {
                                if (addROCreateView!=null){
                                    addROCreateView.showMessage(mContext.getString(R.string.validation_sel_atlest_one_material),false);
                                }
                            }
                        }
                    });
                }

            }
        }).start();
    }

    private void filterType(String brandName, String categoryName, String creskuGrpName) {
        try {
            String filteredResult = "";
            if (!TextUtils.isEmpty(brandName) && !brandName.equalsIgnoreCase(Constants.None)) {
                filteredResult = brandName;
            }
            if (!TextUtils.isEmpty(categoryName) && !categoryName.equalsIgnoreCase(Constants.None)) {
                filteredResult = filteredResult + ", " + categoryName;
            }
            if (!TextUtils.isEmpty(creskuGrpName) && !creskuGrpName.equalsIgnoreCase(Constants.None)) {
                filteredResult = filteredResult + ", " + creskuGrpName;
            }
            if (addROCreateView != null) {
                addROCreateView.setFilterDate(filteredResult);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * @desc adding return order requests
     */
    private void getRequests() {
    }
}
