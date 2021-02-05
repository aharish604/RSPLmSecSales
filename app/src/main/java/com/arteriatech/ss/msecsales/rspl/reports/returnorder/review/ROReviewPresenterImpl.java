package com.arteriatech.ss.msecsales.rspl.reports.returnorder.review;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.datavault.UtilDataVault;
import com.arteriatech.mutils.location.LocationInterface;
import com.arteriatech.mutils.location.LocationModel;
import com.arteriatech.mutils.location.LocationUtils;
import com.arteriatech.ss.msecsales.rspl.R;
import com.arteriatech.ss.msecsales.rspl.common.Constants;
import com.arteriatech.ss.msecsales.rspl.common.ConstantsUtils;
import com.arteriatech.ss.msecsales.rspl.customers.CustomerDetailsActivity;
import com.arteriatech.ss.msecsales.rspl.mbo.ROCreateBean;
import com.arteriatech.ss.msecsales.rspl.mbo.ReturnOrderBean;
import com.arteriatech.ss.msecsales.rspl.store.OfflineManager;
import com.sap.client.odata.v4.core.GUID;

import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

public class ROReviewPresenterImpl implements ROReviewPresenter {
    private Activity mContext;
    private ROReviewView roReviewView = null;
    private ArrayList<ReturnOrderBean> roCreateBeanList;
    private ROCreateBean roCreateBean;
    private String[][] mArrayDistributors = null;
    private String[][] mArrayCPDMSDivisoins = null;
    private Hashtable<String, String> headerTable = null;
    private String[][] mArraySPValues = null;
    private String beatGUID = "";
    private String parentId = "";

    public ROReviewPresenterImpl(Activity mContext, ROReviewView roReviewView, ArrayList<ReturnOrderBean> roCreateBeanList, ROCreateBean roCreateBean,String beatGUID,String parentId) {
        this.mContext = mContext;
        this.roReviewView = roReviewView;
        this.roCreateBeanList = roCreateBeanList;
        this.roCreateBean = roCreateBean;
        this.beatGUID = beatGUID;
        this.parentId = parentId;
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onDestroy() {
        roReviewView = null;
    }

    @Override
    public void onSaveData() {
        if (roReviewView != null) {
            roReviewView.showProgressDialog(mContext.getString(R.string.checking_pemission));
        }
        LocationUtils.checkLocationPermission(mContext, new LocationInterface() {
            @Override
            public void location(boolean status, LocationModel location, String errorMsg, int errorCode) {
                if (status) {
                    locationPerGranted();
                } else {
                    if (roReviewView != null) {
                        roReviewView.hideProgressDialog();
                    }
                }
            }
        });

    }

    private void locationPerGranted() {
        Constants.getLocation(mContext, new LocationInterface() {
            @Override
            public void location(boolean status, LocationModel locationModel, String errorMsg, int errorCode) {
                if (status) {
                    onSaveRO();
                } else {
                    if (roReviewView != null) {
                        roReviewView.hideProgressDialog();
                    }
                }
            }
        });
    }

    private void onSaveRO() {
        getDistributorValues();
        getCPDMSDivisions();
        getSalesPersonValues();
        createDataVaultObjects();
    }

    private void createDataVaultObjects() {
        headerTable = new Hashtable<>();
        GUID ssoHeaderGuid = GUID.newRandom();
        String doc_no = (System.currentTimeMillis() + "");
        headerTable.put(Constants.SSROGUID, ssoHeaderGuid.toString36().toUpperCase());
        String ordettype = "";
        try {
            ordettype = OfflineManager.getValueByColumnName(Constants.ValueHelps + "?$select=" + Constants.ID + " &$filter=" + Constants.EntityType + " eq 'SSRO' and  " + Constants.PropName + " eq 'OrderType' and  " + Constants.ParentID + " eq '000020' ", Constants.ID);
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
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
        headerTable.put(Constants.OrderType, ordettype);
        headerTable.put(Constants.OrderTypeDesc, "");
        headerTable.put(Constants.OrderNo, doc_no);
        headerTable.put(Constants.OrderDate, UtilConstants.getNewDateTimeFormat());
      //  headerTable.put(Constants.DmsDivision, roCreateBean.getDivision()); // commented on 18-11-2019 for removing division filter in Return order create.
//        headerTable.put(Constants.DmsDivision, mArrayCPDMSDivisoins[0][0] != null ? mArrayCPDMSDivisoins[0][0] : "");
//        headerTable.put(Constants.DmsDivisionDesc, mArrayCPDMSDivisoins[1][0] != null ? mArrayCPDMSDivisoins[1][0] : "");
        headerTable.put(Constants.FromCPGUID, mArrayDistributors[4][0]);
        headerTable.put(Constants.FromCPNo, mArrayDistributors[4][0]);
        headerTable.put(Constants.FromCPName, mArrayDistributors[7][0]);
        headerTable.put(Constants.FromCPTypId, mArrayDistributors[5][0]);
        headerTable.put(Constants.FromCPTypDs, "");
        headerTable.put(Constants.CPGUID, roCreateBean.getCPGUID());
        headerTable.put(Constants.BeatGuid,beatGUID);
        headerTable.put(Constants.CPNo, roCreateBean.getCPNo());
        headerTable.put(Constants.CPName, roCreateBean.getCPName());
        headerTable.put(Constants.CPTypeID, Constants.str_02);
        headerTable.put(Constants.CPTypeDesc, mArrayDistributors[9][0]);
        headerTable.put(Constants.SoldToCPGUID, roCreateBean.getCPGUID());
        headerTable.put(Constants.SoldToId, roCreateBean.getCPNo());
        headerTable.put(Constants.SoldToUID, roCreateBean.getCPGUID());
        headerTable.put(Constants.SoldToDesc, roCreateBean.getCPName());
        headerTable.put(Constants.SoldToTypeID, mArrayDistributors[8][0]);
        headerTable.put(Constants.SoldToTypDesc, mArrayDistributors[9][0]);
        try {
            headerTable.put(Constants.SPGUID, spGuid);
        } catch (Exception e) {
            headerTable.put(Constants.SPGUID, Constants.getSPGUID());
            e.printStackTrace();
        }
        headerTable.put(Constants.SPNo, mArraySPValues[6][0]);
        try {
            headerTable.put(Constants.FirstName, fristName);
        } catch (Exception e) {
            headerTable.put(Constants.FirstName, fristName);
            e.printStackTrace();
        }
        headerTable.put(Constants.Currency, mArrayDistributors[10][0]);
        headerTable.put(Constants.CreatedOn, UtilConstants.getNewDateTimeFormat());
        headerTable.put(Constants.CreatedAt, Constants.getOdataDuration().toString());
        headerTable.put(Constants.StatusID, "000001");
        headerTable.put(Constants.ApprovalStatusID, "000001");
        headerTable.put(Constants.TestRun, "M");
        ArrayList<HashMap<String, String>> soItems = new ArrayList<HashMap<String, String>>();
        for (int itemIncVal = 0; itemIncVal < roCreateBeanList.size(); itemIncVal++) {
            ReturnOrderBean returnOrderBean = roCreateBeanList.get(itemIncVal);
            HashMap<String, String> singleItem = new HashMap<String, String>();
            GUID ssoItemGuid = GUID.newRandom();
            singleItem.put(Constants.SSROItemGUID, ssoItemGuid.toString36().toUpperCase());
            singleItem.put(Constants.SSROGUID, ssoHeaderGuid.toString36().toUpperCase());
            singleItem.put(Constants.ItemNo, ConstantsUtils.addZeroBeforeValue(itemIncVal + 1, ConstantsUtils.ITEM_MAX_LENGTH));
            singleItem.put(Constants.MaterialNo, returnOrderBean.getMaterialNo());
            singleItem.put(Constants.MaterialDesc, returnOrderBean.getMaterialDesc());
            singleItem.put(Constants.OrderMatGrp, returnOrderBean.getOrderMaterialGroupID());
            singleItem.put(Constants.OrderMatGrpDesc, returnOrderBean.getOrderMaterialGroupDesc());
            singleItem.put(Constants.Quantity, returnOrderBean.getReturnQty());
            singleItem.put(Constants.Currency, mArrayDistributors[10][0]);
            singleItem.put(Constants.Uom, returnOrderBean.getReturnUOM());
            singleItem.put(Constants.Batch, returnOrderBean.getReturnBatchNumber().toUpperCase());
            singleItem.put(Constants.RejectionReasonID, returnOrderBean.getReturnReason());
            singleItem.put(Constants.StockRefGUID, returnOrderBean.getStockRefGUID());
            singleItem.put(Constants.RejectionReasonDesc, returnOrderBean.getReturnDesc());
            String mStrMRP = String.valueOf(ConstantsUtils.decimalRoundOff(new BigDecimal(returnOrderBean.getAltReturnMrp()), 2));//alternativeUOMQty+"";
            singleItem.put(Constants.MRP, mStrMRP);
            String mStrTax = String.valueOf(ConstantsUtils.decimalRoundOff(new BigDecimal(returnOrderBean.getTax()), 2));//alternativeUOMQty+"";
            singleItem.put(Constants.Tax, mStrTax);
            String mStrUnitPrice = String.valueOf(ConstantsUtils.decimalRoundOff(new BigDecimal(returnOrderBean.getUnitPrice()), 2));//alternativeUOMQty+"";
            singleItem.put(Constants.UnitPrice, mStrUnitPrice);
            singleItem.put(Constants.RefdocItmGUID, returnOrderBean.getCPStockItemGUID().toUpperCase());
            try {
                singleItem.put(Constants.RefDocNo, returnOrderBean.getInvoiceNo());
            } catch (Exception e) {
                singleItem.put(Constants.RefDocNo, "");
                e.printStackTrace();
            }

            soItems.add(singleItem);

        }
        headerTable.put(Constants.entityType, Constants.ReturnOrderCreate);
        headerTable.put(Constants.ITEM_TXT, Constants.convertArrListToGsonString(soItems));
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(Constants.PREFS_NAME, 0);
        Constants.onVisitActivityUpdate(mContext, roCreateBean.getCPGUID32(),ssoHeaderGuid.toString36().toUpperCase(), Constants.ROCreateID, Constants.ReturnOrderCreate, UtilConstants.getOdataDuration());

        Constants.saveDeviceDocNoToSharedPref(mContext, Constants.ROList, doc_no);

        headerTable.put(Constants.LOGINID, sharedPreferences.getString(Constants.username, "").toUpperCase());

        JSONObject jsonHeaderObject = new JSONObject(headerTable);

        ConstantsUtils.storeInDataVault(doc_no, jsonHeaderObject.toString(),mContext);

//        if (roReviewView != null) {
//            roReviewView.hideProgressDialog();
            navigateToVisit();
//        }
    }

    private void navigateToVisit() {
        if (roReviewView != null) {
            mContext.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    roReviewView.hideProgressDialog();
                    roReviewView.showMessage(mContext.getString(R.string.msg_ro_created), false);
                }
            });
        }


    }

    /*get distributor values*/
    private void getDistributorValues() {
//        mArrayDistributors = Constants.getDistributorsByCPGUID(roCreateBean.getCPGUID(),mContext);
        mArrayDistributors = Constants.getDistributorsByCPGUID(mContext,roCreateBean.getCPGUID(),parentId);
    }

    /*get cpdms divisions value*/
    private void getCPDMSDivisions() {
        mArrayCPDMSDivisoins = Constants.getDMSDivisionByCPGUID(roCreateBean.getCPGUID(),mContext);
    }

    private void getSalesPersonValues() {
        mArraySPValues = Constants.getSPValesFromCPDMSDivisionByCPGUIDAndDMSDivision(roCreateBean.getCPGUID(),mContext);
    }
}
