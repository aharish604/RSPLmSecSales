package com.arteriatech.ss.msecsales.rspl.reports.returnorder.create;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.log.LogManager;
import com.arteriatech.ss.msecsales.rspl.common.Constants;
import com.arteriatech.ss.msecsales.rspl.mbo.BrandBean;
import com.arteriatech.ss.msecsales.rspl.mbo.SKUGroupBean;
import com.arteriatech.ss.msecsales.rspl.store.OfflineManager;

import java.util.ArrayList;

class ROFilterPresenterImpl implements ROFilterPresenter {

    private String mStrSelCategoryId = "";
    private String mStrSelBrandId = "";
    private Context context;
    private ROCreateFilterView roView = null;

    public ROFilterPresenterImpl(Context context, ROCreateFilterView roView) {
        this.context = context;
        this.roView = roView;
    }

    @Override
    public void loadCategory(final String brand) {
        this.mStrSelBrandId = brand;
        if (roView != null) {
            roView.showProgressDialog();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                String[][] mArrayCateogryTypeVal = null;
                if (!brand.equalsIgnoreCase(Constants.None) && !TextUtils.isEmpty(brand)) {
                    try {
                        String mStrConfigQry = Constants.BrandsCategories + "?$select=MaterialCategoryID,MaterialCategoryDesc&$orderby=" + Constants.MaterialCategoryDesc + " &$filter= " + Constants.BrandID + " eq '" + brand + "'";
                        mArrayCateogryTypeVal = OfflineManager.getCategoryListValues(mStrConfigQry);
                    } catch (OfflineODataStoreException e) {
                        LogManager.writeLogError(Constants.error_txt + e.getMessage());
                    }
                    if (mArrayCateogryTypeVal == null) {
                        mArrayCateogryTypeVal = new String[2][1];
                        mArrayCateogryTypeVal[0][0] = "";
                        mArrayCateogryTypeVal[1][0] = "";
                    }
                } else {
                    try {
                        String mStrConfigQry = Constants.MaterialCategories + "?$select=MaterialCategoryID,MaterialCategoryDesc&$orderby=" + Constants.MaterialCategoryDesc;
                        mArrayCateogryTypeVal = OfflineManager.getCategoryListValues(mStrConfigQry);
                    } catch (OfflineODataStoreException e) {
                        LogManager.writeLogError(Constants.error_txt + e.getMessage());
                    }
                    if (mArrayCateogryTypeVal == null) {
                        mArrayCateogryTypeVal = new String[2][1];
                        mArrayCateogryTypeVal[0][0] = "";
                        mArrayCateogryTypeVal[1][0] = "";
                    }
                }
                final String[][] finalMArrayCateogryTypeVal = mArrayCateogryTypeVal;
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (roView != null) {
                            roView.hideProgressDialog();
                            roView.categoryList(finalMArrayCateogryTypeVal);
                        }
                    }
                });
            }
        }).start();
    }

    @Override
    public void loadBrands(final String previousBrandId, final String previousCategoryId) {
        if (roView != null) {
            roView.showProgressDialog();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<BrandBean> brandBeanArrayList = new ArrayList<>();
                if (!previousCategoryId.equalsIgnoreCase(Constants.None) && !TextUtils.isEmpty(previousCategoryId)) {
                    try {
                        brandBeanArrayList = OfflineManager.getBrandListValues(Constants.BrandsCategories + "?$orderby=" + Constants.BrandDesc + " &$filter= " + Constants.MaterialCategoryID + " eq '" + previousCategoryId + "'");
                    } catch (OfflineODataStoreException e) {
                        e.printStackTrace();
                    }
                } else {
                    if (previousBrandId.equalsIgnoreCase(Constants.None) || TextUtils.isEmpty(previousBrandId)) {
                        try {
                            String mStrConfigQry = Constants.Brands + "?$select=BrandID,BrandDesc&$orderby=" + Constants.BrandDesc;// + " &$filter=(" + dmsDivisionId + ")"; TODO need to enable this
                            brandBeanArrayList = OfflineManager.getBrandListValues(mStrConfigQry);
                        } catch (OfflineODataStoreException e) {
                            LogManager.writeLogError(Constants.error_txt + e.getMessage());
                        }
                    }
                }
                if (brandBeanArrayList != null) {
                    final ArrayList<BrandBean> finalMArrayBrandTypeVal = brandBeanArrayList;
                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (roView != null) {
                                roView.hideProgressDialog();
                                roView.brandList(finalMArrayBrandTypeVal);
                            }
                        }
                    });
                }
            }
        }).start();
    }

    @Override
    public void loadCRSSKU(final String previousBrandId, final String previousCategoryId) {
        this.mStrSelCategoryId = previousCategoryId;
        if (roView != null) {
            roView.showProgressDialog();
        }
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
        ArrayList<SKUGroupBean> mArrayOrderedGroup = new ArrayList<>();
        if ((previousCategoryId.equalsIgnoreCase(Constants.None) || TextUtils.isEmpty(previousCategoryId)) && (previousBrandId.equalsIgnoreCase(Constants.None) || TextUtils.isEmpty(previousBrandId))) {
            mArrayOrderedGroup = null;
            try {
                String mStrConfigQry = Constants.OrderMaterialGroups + "?$select=OrderMaterialGroupID,OrderMaterialGroupDesc&$orderby=" + Constants.OrderMaterialGroupDesc;
                mArrayOrderedGroup = OfflineManager.getOrderedMaterialGroups(mStrConfigQry);
            } catch (OfflineODataStoreException e) {
                LogManager.writeLogError(Constants.error_txt + e.getMessage());
            }
        } else if (previousCategoryId.equalsIgnoreCase(Constants.None) || TextUtils.isEmpty(previousCategoryId)) {
            try {
                String mStrConfigQry = Constants.OrderMaterialGroups + "?$select=OrderMaterialGroupID,OrderMaterialGroupDesc&$orderby=" + Constants.OrderMaterialGroupDesc + " &$filter=" + Constants.BrandID + " eq '" + previousBrandId + "'";
                mArrayOrderedGroup = OfflineManager.getOrderedMaterialGroups(mStrConfigQry);
            } catch (OfflineODataStoreException e) {
                LogManager.writeLogError(Constants.error_txt + e.getMessage());
            }
        } else if (previousBrandId.equalsIgnoreCase(Constants.None) || TextUtils.isEmpty(previousBrandId)) {
            try {
                String mStrConfigQry = Constants.OrderMaterialGroups + "?$select=OrderMaterialGroupID,OrderMaterialGroupDesc&$orderby=" + Constants.OrderMaterialGroupDesc + " &$filter=" + Constants.MaterialCategoryID + " eq '" + previousCategoryId + "'";
                mArrayOrderedGroup = OfflineManager.getOrderedMaterialGroups(mStrConfigQry);
            } catch (OfflineODataStoreException e) {
                LogManager.writeLogError(Constants.error_txt + e.getMessage());
            }
        } else {
            try {
                String mStrConfigQry = Constants.OrderMaterialGroups + "?$select=OrderMaterialGroupID,OrderMaterialGroupDesc&$orderby=" + Constants.OrderMaterialGroupDesc + " &$filter=" + Constants.MaterialCategoryID + " eq '" + previousCategoryId + "' and " + Constants.BrandID + " eq '" + previousBrandId + "'";
                mArrayOrderedGroup = OfflineManager.getOrderedMaterialGroups(mStrConfigQry);
            } catch (OfflineODataStoreException e) {
                LogManager.writeLogError(Constants.error_txt + e.getMessage());
            }
        }
        final ArrayList<SKUGroupBean> finalMArrayOrderedGroup = mArrayOrderedGroup;
        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (roView != null) {
                    roView.hideProgressDialog();
                    roView.crsSKUList(finalMArrayOrderedGroup);
                }
            }
        });
//            }
//        }).start();
    }

    @Override
    public void onDestroy() {
        roView=null;
    }
}
