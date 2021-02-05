package com.arteriatech.ss.msecsales.rspl.reports.returnorder.create;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.store.OnlineODataInterface;
import com.arteriatech.ss.msecsales.rspl.R;
import com.arteriatech.ss.msecsales.rspl.common.Constants;
import com.arteriatech.ss.msecsales.rspl.common.ConstantsUtils;
import com.arteriatech.ss.msecsales.rspl.mbo.CompetitorBean;
import com.arteriatech.ss.msecsales.rspl.mbo.ConfigTypesetTypesBean;
import com.arteriatech.ss.msecsales.rspl.mbo.ROCreateBean;
import com.arteriatech.ss.msecsales.rspl.mbo.RetailerStockBean;
import com.arteriatech.ss.msecsales.rspl.mbo.ReturnOrderBean;
import com.arteriatech.ss.msecsales.rspl.reports.returnorder.review.ROReviewActivity;
import com.arteriatech.ss.msecsales.rspl.store.OfflineManager;
import com.sap.smp.client.odata.ODataDuration;
import com.sap.smp.client.odata.ODataEntity;
import com.sap.smp.client.odata.store.ODataRequestExecution;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by e10526 on 21-04-2018.
 */

public class ROCreatePresenterImpl implements ROCreatePresenter, OnlineODataInterface {
    //data members
    private Context mContext;
    private Activity mActivity;
    private ROCreateView roCreateView;
    private boolean isSessionRequired;
    private ROCreateBean roCreateBean;
    private ODataDuration mStartTimeDuration;
    private String[][] mArrayDistributors, mArraySPValues = null;
    private String stockOwner = "";
    private ArrayList<ReturnOrderBean> distStockList = new ArrayList<>();
    private ArrayList<ReturnOrderBean> returnOrderList = new ArrayList<>();
    private ArrayList<ReturnOrderBean> searchBeanArrayList = new ArrayList<>();
    private List<ConfigTypesetTypesBean> configTypesetTypesBeanArrayList = new ArrayList<>();
    private int totalRequest = 0;
    private int currentRequest = 0;
    private String strSearchTxt = "";
    private String beatGUID = "";
    private String parentId = "";

    /**
     * @desc parameterized constructor to initialize required fields
     */
    public ROCreatePresenterImpl(Context mContext, ROCreateView roCreateView, boolean isSessionRequired, Activity mActivity, ROCreateBean roCreateBean,String beatGUID,String parentId) {
        this.mContext = mContext;
        this.mActivity = mActivity;
        this.roCreateView = roCreateView;
        this.isSessionRequired = isSessionRequired;
        this.mStartTimeDuration = UtilConstants.getOdataDuration();
        this.roCreateBean = roCreateBean;
        this.beatGUID = beatGUID;
        this.parentId = parentId;
    }


    @Override
    public void onStart() {
        getDistributorValues();
    }

    @Override
    public void onDestroy() {
        roCreateView = null;
    }

    @Override
    public boolean validateFields() {
        ArrayList<String> tempList = new ArrayList<>();
        for (ReturnOrderBean returnOrderBean : returnOrderList) {
            try {
                if (TextUtils.isEmpty(returnOrderBean.getReturnQty()) || Double.parseDouble(returnOrderBean.getReturnQty()) == 0) {
                    if (roCreateView != null) {
                        roCreateView.showMessage(mActivity.getString(R.string.validation_plz_enter_mandatory_flds), false);
                        roCreateView.errormessageQTY("Enter quantity");
                    }
                    return false;
                } else if (TextUtils.isEmpty(returnOrderBean.getReturnBatchNumber())) {
                    if (roCreateView != null) {
                        if(returnOrderBean.getHigherLevelItm().equalsIgnoreCase("000000")) {
                            roCreateView.errormessageBatch("Enter Batch");
                            roCreateView.showMessage(mActivity.getString(R.string.validation_plz_enter_mandatory_flds), false);
                            return false;
                        }
                    }
                } else if (TextUtils.isEmpty(returnOrderBean.getReturnReason())) {
                    if (roCreateView != null) {
                        roCreateView.errormessageReason("Select Reason");
                        roCreateView.showMessage(mActivity.getString(R.string.validation_plz_enter_mandatory_flds), false);
                    }
                    return false;
                }
                    if (returnOrderBean.getUom().equalsIgnoreCase(returnOrderBean.getReturnUOM())) {
                        Double mDouRetQty = Double.parseDouble(returnOrderBean.getReturnQty());
                        Double mDouCalInvQty = Double.parseDouble(returnOrderBean.getActualQty());
                        if (mDouRetQty <= mDouCalInvQty) {
                         //   if(!returnOrderBean.getRefDocItmGUID().equalsIgnoreCase("X")) {
                                String matBatch = returnOrderBean.getMaterialNo() + returnOrderBean.getReturnBatchNumber() + returnOrderBean.getInvoiceNo();
                               /* if (tempList.contains(matBatch)) {
                                    if (roCreateView != null) {
                                        roCreateView.showMessage(mActivity.getString(R.string.msg_material_batch_already_exists), false);
                                    }
                                    return false;
                                } else {
                                    tempList.add(matBatch);
                                }*/
                         //   }
                        } else {
                            if (roCreateView != null) {
                                roCreateView.showMessage(mActivity.getString(R.string.alert_ent_qty_not_inv_qty), false);
                            }
                            return false;
                        }
                    } else {
                        double doublAltUom1Num = 0.0;
                        double doublAltUom1Den = 0.0;
                        double douCalInvQty = 0.0;
                        Double mDouInvQty = Double.parseDouble(returnOrderBean.getActualQty());
                        Double mDouCalRetQty = Double.parseDouble(returnOrderBean.getReturnQty());
                        try {
                            doublAltUom1Num = Double.parseDouble(returnOrderBean.getAlternativeUOM1Num());
                        } catch (NumberFormatException e) {
                            doublAltUom1Num = 0.0;
                            e.printStackTrace();
                        }

                        try {
                            doublAltUom1Den = Double.parseDouble(returnOrderBean.getAlternativeUOM1Den());
                        } catch (NumberFormatException e) {
                            doublAltUom1Den = 0.0;
                            e.printStackTrace();
                        }


                        if (doublAltUom1Num > 0) {
                            if (doublAltUom1Num <= doublAltUom1Den) { // Emami and Pal Case
                                douCalInvQty = doublAltUom1Den * mDouInvQty;
                                if (mDouCalRetQty <= douCalInvQty) {
                                  //  if(!returnOrderBean.getRefDocItmGUID().equalsIgnoreCase("X")) {
                                        String matBatch = returnOrderBean.getMaterialNo() + returnOrderBean.getReturnBatchNumber() + returnOrderBean.getInvoiceNo();
                                      /*  if (tempList.contains(matBatch)) {
                                            if (roCreateView != null) {
                                                roCreateView.showMessage(mActivity.getString(R.string.msg_material_batch_already_exists), false);
                                            }
                                            return false;
                                        } else {
                                            tempList.add(matBatch);
                                        }*/
                                  //  }
                                } else {
                                    if (roCreateView != null) {
                                        roCreateView.showMessage(mActivity.getString(R.string.alert_ent_qty_not_inv_qty), false);
                                    }
                                    return false;
                                }
                            } else {
                                douCalInvQty = doublAltUom1Num * mDouInvQty;
                                if (mDouCalRetQty <= douCalInvQty) {
                               //     if(!returnOrderBean.getRefDocItmGUID().equalsIgnoreCase("X")) {
                                        String matBatch = returnOrderBean.getMaterialNo() + returnOrderBean.getReturnBatchNumber() + returnOrderBean.getInvoiceNo();
                                       /* if (tempList.contains(matBatch)) {
                                            if (roCreateView != null) {
                                                roCreateView.showMessage(mActivity.getString(R.string.msg_material_batch_already_exists), false);
                                            }
                                            return false;
                                        } else {
                                            tempList.add(matBatch);
                                        }*/
                                //    }
                                } else {
                                    if (roCreateView != null) {
                                        roCreateView.showMessage(mActivity.getString(R.string.alert_ent_qty_not_inv_qty), false);
                                    }
                                    return false;
                                }
                            }
                        }

                    }

//                String mStrRetQty = Constants.getReturnOrderQty(returnOrderBean,"");
//                Double mDouRetQty = Double .parseDouble(mStrRetQty);
//                Double mDouCalInvQty = 0.00;
//                Double mDouAlterQty = Double .parseDouble(returnOrderBean.getAlterInvQty());
//                Double mDouAlterQtyTemp = Double .parseDouble( Constants.getReturnOrderQty(returnOrderBean,"Yes"));
//
////                mDouCalInvQty = Double.parseDouble(returnOrderBean.getActualQty()) * mDouAlterQty;
//                mDouCalInvQty = mDouAlterQtyTemp;




            } catch (Exception e) {
                e.printStackTrace();
                if (roCreateView != null) {
                    roCreateView.showMessage(mActivity.getString(R.string.validation_plz_enter_mandatory_flds), false);
                }
            }
        }
        if (returnOrderList.isEmpty()) {
            if (roCreateView != null) {
                roCreateView.showMessage(mActivity.getString(R.string.retailer_items_selected), false);
            }
        } else {
            Intent intent = new Intent(mContext, ROReviewActivity.class);
            intent.putExtra(Constants.ItemList, returnOrderList);
            intent.putExtra(Constants.EXTRA_BEAN, roCreateBean);
            intent.putExtra(Constants.BeatGUID, beatGUID);
            intent.putExtra(Constants.ParentId, parentId);
            mContext.startActivity(intent);
        }
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == AddROActivity.RETURN_ORDER_RESULT_ID) {
            try {
                ArrayList<ReturnOrderBean> roCreateBean = data.getParcelableArrayListExtra(ConstantsUtils.EXTRA_ARRAY_LIST);
                returnOrderList.addAll(roCreateBean);
                if (roCreateView != null) {
                    roCreateView.roDisplayList(returnOrderList);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void removeItem(ReturnOrderBean retailerStkBean) {
        returnOrderList.remove(retailerStkBean);
    }

    @Override
    public void onSearch(String query) {
        strSearchTxt = query;
        onSearchMaterial(query);
    }
    public void onSearchMaterial(String searchText) {
        searchBeanArrayList.clear();
        boolean isID = false;
        boolean isName = false;
        if (returnOrderList != null) {
            if (TextUtils.isEmpty(searchText)) {
                searchBeanArrayList.addAll(returnOrderList);
            } else {
                for (ReturnOrderBean  item : returnOrderList) {
                    if (!TextUtils.isEmpty(searchText)) {
                        isID = item.getMaterialDesc().toLowerCase().contains(searchText.toLowerCase());
                        isName = item.getMaterialNo().toLowerCase().contains(searchText.toLowerCase());
                    } else {
                        isID = true;
                        isName = true;
                    }
                    if (isID || isName)
                        searchBeanArrayList.add(item);
                }
            }
        }
        if (roCreateView != null) {
            roCreateView.roDisplayList(searchBeanArrayList);
        }
    }
    /**
     * @desc getting stack owner details
     */
    private void getDistributorValues() {
        mArrayDistributors = Constants.getDistributorsByCPGUID(roCreateBean.getCPGUID(),mContext);
        if (mArrayDistributors != null) {
            try {
                stockOwner = mArrayDistributors[5][0];
                String mStrConfigQry = Constants.ValueHelps + "?$filter=" + Constants.EntityType + " eq 'SSSOItemDetail'&$orderby=" + Constants.DESCRIPTION + " asc";
                try {
                    configTypesetTypesBeanArrayList = OfflineManager.getStatusFromValueHelp(mStrConfigQry, Constants.None);
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }

                if (roCreateView != null) {
                    roCreateView.hideProgressDialog();
                    if (roCreateBean.getRoList() != null) {
                        returnOrderList = roCreateBean.getRoList();
                    } else {
                        returnOrderList = new ArrayList<>();
                    }
                    roCreateView.RODetails(returnOrderList, stockOwner, configTypesetTypesBeanArrayList);
                    roCreateView.roDisplayList(returnOrderList);
                }

//                requestRO();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @desc requesting return order list
     */
    private void requestRO() {
        if (roCreateView != null) {
            roCreateView.showProgressDialog(mContext.getString(R.string.app_loading));
        }
        totalRequest = 2;
        currentRequest = 0;
        String mStrConfigQry = Constants.CPStockItems + "?$orderby=" + Constants.Material_Desc + " &$filter=" + Constants.StockOwner + " eq '" + stockOwner + "' and " + Constants.MaterialNo + " ne '' ";
        ConstantsUtils.onlineRequest(mContext, mStrConfigQry, isSessionRequired, 1, ConstantsUtils.SESSION_HEADER, this, false);

        mStrConfigQry = Constants.CPStockItems + "?$orderby=" + Constants.Material_Desc + " &$filter=" + Constants.CPGUID + " eq '" + roCreateBean.getCPGUID().replace("-", "") + "' and " + Constants.MaterialNo + " ne '' ";

        ConstantsUtils.onlineRequest(mContext, mStrConfigQry, isSessionRequired, 2, ConstantsUtils.SESSION_HEADER, this, false);
    }

    @Override
    public void responseSuccess(ODataRequestExecution oDataRequestExecution, List<ODataEntity> list, Bundle bundle) {
        int type = bundle != null ? bundle.getInt(Constants.BUNDLE_REQUEST_CODE) : 0;
        switch (type) {
            case 1:
                try {
                    distStockList = OfflineManager.getReturnOrderList(list, distStockList);
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }
                mArraySPValues = Constants.getSPValesFromCPDMSDivisionByCPGUIDAndDMSDivision(roCreateBean.getCPGUID(),mContext);
                roCreateBean.setSPGUID(mArraySPValues[4][0].toUpperCase());
                String mStrConfigQry = Constants.ValueHelps + "?$filter=" + Constants.EntityType + " eq 'SSSOItemDetail'&$orderby=" + Constants.DESCRIPTION + " asc";
                try {
                    configTypesetTypesBeanArrayList = OfflineManager.getStatusFromValueHelp(mStrConfigQry, Constants.None);
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }
                currentRequest++;
                break;
            case 2:
                try {
                    returnOrderList.clear();
                    returnOrderList = OfflineManager.getReturnOrderList(list, returnOrderList);
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }
                currentRequest++;
                break;
        }
        if (totalRequest == currentRequest) {
            ((Activity) mContext).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (roCreateView != null) {
                        roCreateView.hideProgressDialog();
                        roCreateView.RODetails(distStockList, stockOwner, configTypesetTypesBeanArrayList);
                        roCreateView.roDisplayList(returnOrderList);
                    }
                }
            });
        }
    }

    @Override
    public void responseFailed(ODataRequestExecution oDataRequestExecution, final String s, Bundle bundle) {
        int type = bundle != null ? bundle.getInt(Constants.BUNDLE_REQUEST_CODE) : 0;
        currentRequest++;
        if (totalRequest == currentRequest) {
            ((Activity) mContext).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (roCreateView != null) {
                        roCreateView.hideProgressDialog();
                        roCreateView.showMessage(s, false);
                    }
                }
            });
        }
    }

}
