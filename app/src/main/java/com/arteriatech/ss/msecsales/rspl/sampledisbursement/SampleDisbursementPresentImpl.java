package com.arteriatech.ss.msecsales.rspl.sampledisbursement;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.log.LogManager;
import com.arteriatech.ss.msecsales.rspl.common.Constants;
import com.arteriatech.ss.msecsales.rspl.common.ConstantsUtils;
import com.arteriatech.ss.msecsales.rspl.mbo.DMSDivisionBean;
import com.arteriatech.ss.msecsales.rspl.sampledisbursement.addsampledisbursement.AddSampleDisbursementView;
import com.arteriatech.ss.msecsales.rspl.store.OfflineManager;
import com.sap.smp.client.odata.ODataEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SampleDisbursementPresentImpl {
    private AddSampleDisbursementView addSampleDisbursementView= null;
    private Context context;
    private Activity activity;
    String cpno="";
    String cpGUIDID="";
    private ArrayList<com.arteriatech.ss.msecsales.rspl.dbstock.stockmaterial.DMSDivisionBean> distListDms=null;
    private ArrayList<DMSDivisionBean> alDmsDivision = new ArrayList<>();
    private String[][] mArrayCPDMSDivisoins = null;
    public SampleDisbursementPresentImpl(Activity activity, Context context,AddSampleDisbursementView addSampleDisbursementView,String cpno,String cpGUIDID) {
        this.activity = activity;
        this.context = context;
        this.addSampleDisbursementView = addSampleDisbursementView;
        this.cpno = cpno;
        this.cpGUIDID = cpGUIDID;
    }

    public void getCPSPRelationDivisions() {
//        mArraySPValues = Constants.getSPValesFromCPDMSDivisionByCPGUIDAndDMSDivision(Constants.convertStrGUID32to36(soCreateBean.getCPGUID()),mContext);
//        String mStrDistQry = Constants.CPSPRelations+ "?$filter=" + Constants.CPGUID + " eq '"+mArraySPValues[1][0] +"'";
        String mStrDistQry = Constants.CPDMSDivisions+ "?$filter=" + Constants.CPNo + " eq '"+cpno +"'";
        List<ODataEntity> entities=null;
        try {
            entities = Constants.getListEntities(mStrDistQry, OfflineManager.offlineStore);
        } catch (Exception e) {
            e.printStackTrace();
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
                if (addSampleDisbursementView != null) {
//                    soCreateView.hideProgressDialog();
//                    addSampleDisbursementView.displayDMSDivision(alDmsDivision);
                }
            }
        });
    }

    public void requestData(String dividionID) {

    }

    private class GetSampleDisbursementList extends AsyncTask<String, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (addSampleDisbursementView != null) {
                addSampleDisbursementView.showProgress();
            }
        }
        @Override
        protected Void doInBackground(String... params) {
           /* getDistributorDMS();
            getCPDMSDivisions();
            getSalesPersonValues();
            getDistributorValues();
            getDataFromOfflineDB();*/
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            try {
                if (addSampleDisbursementView != null) {
                    addSampleDisbursementView.hideProgress();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
//            filter("");
        }
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

    /*private void getCPDMSDivisions() {
        mArrayCPDMSDivisoins = Constants.getDMSDivisionByCPGUID(cpGUIDID,context);
    }
    private void getSalesPersonValues() {
        mArraySPValues = Constants.getSPValesFromCPDMSDivisionByCPGUIDAndDMSDivision(cpGUIDID,context);
    }
    private void getDistributorValues() {
        mArrayDistributors = Constants.getDistributorsByCPGUID(cpGUIDID,context);
        mArrayInvoiceTypeId = OfflineManager.getInVoidTypeId();
    }
    private void getDataFromOfflineDB() {
        try {
            try {
                mStrStockOwner = distListDms.get(0).getStockOwner();
            } catch (Exception e) {
                mStrStockOwner = "";
            }
            retailerStockBeanTotalArrayList.clear();
            retailerSearchStockBeanArrayList.clear();
            String strDefaultUOM = Constants.getName(Constants.ConfigTypsetTypeValues, Constants.TypeValue, Constants.Types, Constants.CPSTKAUOM1);
            retailerStockBeanTotalArrayList = OfflineManager.getSampleCollectionList(Constants.CPStockItems + "?$orderby="+Constants.Material_Desc+" &$filter=StockOwner eq '"+mStrStockOwner+"'", retailerStockBeanTotalArrayList,strDefaultUOM);
            Constants.addedSeaerch=false;
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
    }*/
}
