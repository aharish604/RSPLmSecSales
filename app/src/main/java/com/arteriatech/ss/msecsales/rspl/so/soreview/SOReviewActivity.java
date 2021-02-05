package com.arteriatech.ss.msecsales.rspl.so.soreview;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.interfaces.DialogCallBack;
import com.arteriatech.ss.msecsales.rspl.R;
import com.arteriatech.ss.msecsales.rspl.common.Constants;
import com.arteriatech.ss.msecsales.rspl.common.ConstantsUtils;
import com.arteriatech.ss.msecsales.rspl.customers.CustomerDetailsActivity;
import com.arteriatech.ss.msecsales.rspl.mbo.SKUGroupBean;
import com.arteriatech.ss.msecsales.rspl.mbo.SOCreateBean;
import com.arteriatech.ss.msecsales.rspl.mbo.SchemeBean;
import com.arteriatech.ss.msecsales.rspl.mbo.SchemeCalcuBean;
import com.arteriatech.ss.msecsales.rspl.store.OfflineManager;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by e10526 on 21-04-2018.
 *
 */

public class SOReviewActivity extends AppCompatActivity implements SOReviewView {

    private Toolbar toolbar;
    private Context mContext;
    private String mStrBundleRetID = "";
    private String mStrBundleRetName = "", mStrBundleCPGUID = "";
    private String mStrBundleRetailerUID = "", mStrBundleCPGUID32 = "";
    String mStrComingFrom = "";
    String beatGUID = "";
    String parentId = "";
    SOReviewPresenterImpl presenter;
    ProgressDialog progressDialog = null;
    private ArrayList<SKUGroupBean> alCRSSKUGrpList = new ArrayList<>(), CRSSKUGrpList;
    private SOCreateBean soCreateBean = null;
    TextView tv_so_total_order_val, tvTLSD;

    HashMap<String, String> hashMapFreeMatByOrderMatGrp = new HashMap<>();
    Map<String, Double> mapNetAmt = new HashMap<>();
    Map<String, Double> mapRatioSchDis = new HashMap<>();
    Map<String, Double> mapFreeDisAmt = new HashMap<>();
    Map<String, BigDecimal> mapCRSSKUQTY = new HashMap<>();
    Map<String, Double> mapPriSchemePer = new HashMap<>();
    Map<String, Double> mapSecSchemePer = new HashMap<>();
    Map<String, Double> mapSecSchemeAmt = new HashMap<>();
    private Map<String, SKUGroupBean> mapSKUGRPVal = new HashMap<>();
    private ArrayList<SchemeBean> alSchFreeProd;
    private Double mDobTotalOrderVal = 0.0;
    private Double mDobOrderVal = 0.0;
    HashMap<String, SchemeBean> hashMapFreeMaterialByMaterial = new HashMap<>();
    Map<String, Integer> mapCntMatByCRSKUGRP = new HashMap<>();
    Set<String> mFreeMatScheme = new HashSet<>();
    Set<String> mFreeMatSchemeGuid = new HashSet<>();
    private int tlsdCount= 0;
    private TextView tvRetailerName, tvRetailerID;
    private boolean isClickable = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_so_review_scroll);
        Bundle bundleExtras = getIntent().getExtras();
        if (bundleExtras != null) {
            mStrBundleRetID = bundleExtras.getString(Constants.CPNo);
            mStrBundleRetName = bundleExtras.getString(Constants.RetailerName);
            mStrBundleCPGUID = bundleExtras.getString(Constants.CPGUID);
            mStrBundleCPGUID32 = bundleExtras.getString(Constants.CPGUID32);
            mStrBundleRetailerUID = bundleExtras.getString(Constants.CPUID);
            mStrComingFrom = bundleExtras.getString(Constants.comingFrom);
            beatGUID = bundleExtras.getString(Constants.BeatGUID);
            parentId = bundleExtras.getString(Constants.ParentId);
            soCreateBean = (SOCreateBean) bundleExtras.getSerializable(Constants.EXTRA_SO_HEADER);
            try {
                soCreateBean.setBeatGUID(beatGUID);
                soCreateBean.setFromCPGUID(parentId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        mContext = SOReviewActivity.this;
        ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.title_so_create), 0);
        initUI();
        if (soCreateBean == null) {
            soCreateBean = new SOCreateBean();
            soCreateBean.setCPGUID(mStrBundleCPGUID32);
            soCreateBean.setCPGUID36(mStrBundleCPGUID);
            soCreateBean.setCPNo(mStrBundleRetID);
            soCreateBean.setBeatGUID(beatGUID);
        }
        presenter = new SOReviewPresenterImpl(SOReviewActivity.this, this, true, SOReviewActivity.this, soCreateBean);
        if (!Constants.restartApp(SOReviewActivity.this)) {
            presenter.onStart();
        }
    }

    private void initUI() {
        tv_so_total_order_val = (TextView) findViewById(R.id.tv_so_total_order_val);
        tvTLSD = (TextView) findViewById(R.id.tv_so_create_tlsd_amt);
        tvRetailerName = (TextView) findViewById(R.id.tv_RetailerName);
        tvRetailerID = (TextView) findViewById(R.id.tv_RetailerID);
        // set retailer name
        tvRetailerName.setText(mStrBundleRetName);
        // set retailer id
        tvRetailerID.setText(mStrBundleRetID);
    }
    private void setUI(){
        try{
            mDobTotalOrderVal = mDobOrderVal + mDobTotalOrderVal;
        }catch (Exception e){
            e.printStackTrace();
        }
        try {
            tv_so_total_order_val.setText(UtilConstants.getCurrencyFormat(soCreateBean.getCurrency(),UtilConstants.removeLeadingZerowithTwoDecimal(String.valueOf(mDobTotalOrderVal))));
        } catch (Exception e) {
            e.printStackTrace();
        }
//        tv_so_total_order_val.setText(UtilConstants.getCurrencyFormat(soCreateBean.getCurrency(),String.valueOf(mDobTotalOrderVal)));
        tvTLSD.setText(tlsdCount + "");
    }

    @Override
    public void showProgressDialog(String message) {
        progressDialog = ConstantsUtils.showProgressDialog(SOReviewActivity.this, message);
    }

    @Override
    public void hideProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void displayMessage(String message) {
        isClickable=false;
        UtilConstants.showAlert(message, SOReviewActivity.this);
    }

    @Override
    public void showMessage(String message, final boolean isSimpleDialog) {
        UtilConstants.dialogBoxWithCallBack(SOReviewActivity.this, "", message, getString(R.string.ok), "", false, new DialogCallBack() {
            @Override
            public void clickedStatus(boolean b) {
                isClickable=false;
                if (!isSimpleDialog) {
                    redirectActivity();
                }
            }
        });
    }


    private void redirectActivity() {
        Intent intentNavPrevScreen = new Intent(this, CustomerDetailsActivity.class);
        intentNavPrevScreen.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intentNavPrevScreen.putExtra(Constants.CPNo, mStrBundleRetID);
        intentNavPrevScreen.putExtra(Constants.RetailerName, mStrBundleRetName);
        intentNavPrevScreen.putExtra(Constants.CPUID, mStrBundleRetailerUID);
        intentNavPrevScreen.putExtra(Constants.comingFrom, mStrComingFrom);
        intentNavPrevScreen.putExtra(Constants.CPGUID, mStrBundleCPGUID);
        intentNavPrevScreen.putExtra(Constants.CPGUID32, mStrBundleCPGUID32);
        intentNavPrevScreen.putExtra(Constants.BeatGUID, beatGUID);
        intentNavPrevScreen.putExtra(Constants.ParentId, parentId);
       /* if(!Constants.OtherRouteNameVal.equalsIgnoreCase("")){
            intentNavPrevScreen.putExtra(Constants.OtherRouteGUID, Constants.OtherRouteGUIDVal);
            intentNavPrevScreen.putExtra(Constants.OtherRouteName, Constants.OtherRouteNameVal);
        }*/
        startActivity(intentNavPrevScreen);
    }


    @Override
    public void conformationDialog(String message, int from) {
        UtilConstants.dialogBoxWithCallBack(SOReviewActivity.this, "", message, getString(R.string.ok), getString(R.string.cancel), false, new DialogCallBack() {
            @Override
            public void clickedStatus(boolean b) {
                if (b) {
                    presenter.onSaveData();
                }else {
                    isClickable=false;
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_so_create, menu);
        menu.removeItem(R.id.menu_next);
        MenuItem search = menu.findItem(R.id.menu_search_item);
        search.setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.menu_save:
                if (!isClickable) {
                    isClickable = true;
                    presenter.onAsignData("", "", "", soCreateBean);
                }
                break;
        }
        return true;
    }


    @Override
    public void displaySOReview(Map<String, SKUGroupBean> mapSKUGRPVal, Map<String, BigDecimal> mapCRSSKUQTY,
                                Map<String, Double> mapPriSchemePer, Map<String, Double> mapSecSchemePer,
                                Map<String, Double> mapSecSchemeAmt, Map<String, Integer> mapCntMatByCRSKUGRP,
                                Map<String, Double> mapNetAmt, ArrayList<SchemeBean> alSchFreeProd,
                                HashMap<String, String> hashMapFreeMatByOrderMatGrp,
                                HashMap<String, SchemeBean> hashMapFreeMaterialByMaterial,int tlsdCount,double mDobOrderVal, ArrayList<SKUGroupBean> skuGroupBeanArrayList) {
        this.mapSKUGRPVal =mapSKUGRPVal;
        this.mapCRSSKUQTY =mapCRSSKUQTY;
        this.mapPriSchemePer =mapPriSchemePer;
        this.mapSecSchemePer =mapSecSchemePer;
        this.mapSecSchemeAmt =mapSecSchemeAmt;
        this.mapCntMatByCRSKUGRP =mapCntMatByCRSKUGRP;
        this.mapNetAmt =mapNetAmt;
        this.alSchFreeProd =alSchFreeProd;
        this.hashMapFreeMatByOrderMatGrp =hashMapFreeMatByOrderMatGrp;
        this.hashMapFreeMaterialByMaterial =hashMapFreeMaterialByMaterial;
        this.tlsdCount =tlsdCount;
        this.mDobOrderVal = mDobOrderVal;
        displayReviewPage(skuGroupBeanArrayList);

    }

    private void displayReviewPage(ArrayList<SKUGroupBean> skuGroupBeanArrayList) {
        mFreeMatSchemeGuid.clear();
        try {
            TableLayout tlCRSList = (TableLayout) findViewById(R.id.crs_sku);
            TableLayout tlSOList = (TableLayout) findViewById(R.id.report_table);
            tlCRSList.removeAllViews();
            tlSOList.removeAllViews();

            TableLayout tlSKUGroupItem = (TableLayout) LayoutInflater.from(this)
                    .inflate(R.layout.tl_so_review_item_heading, null, false);
            TableLayout tlCRSKUGroup = (TableLayout) LayoutInflater.from(this)
                    .inflate(R.layout.tl_crsskugrp_heading, null, false);
            TextView tv_sku_grp_title = (TextView) tlCRSKUGroup.findViewById(R.id.tv_crsname);
            tv_sku_grp_title.setText(Constants.getTypesetValueForSkugrp(SOReviewActivity.this));
            tlCRSList.addView(tlCRSKUGroup);
            tlSOList.addView(tlSKUGroupItem);

            LinearLayout llSKUGroupItem = null;
            LinearLayout llCRSKUGroup = null;

            if (!mapSKUGRPVal.isEmpty()) {

    //            Iterator iterator = mapSKUGRPVal.keySet().iterator();
                for (SKUGroupBean skuGrpBean : skuGroupBeanArrayList) {
                    String key = skuGrpBean.getSKUGroup();
                    llSKUGroupItem = (LinearLayout) LayoutInflater.from(this)
                            .inflate(R.layout.ll_so_review_item, null, false);
                    llCRSKUGroup = (LinearLayout) LayoutInflater.from(this)
                            .inflate(R.layout.subitem_so_create_skugroup, null, false);

                    ImageView iv_expand_icon = (ImageView) llCRSKUGroup.findViewById(R.id.iv_expand_icon);
                    ImageView iv_sku_grp_scheme = (ImageView) llCRSKUGroup.findViewById(R.id.iv_sku_grp_scheme);
                    ImageView iv_mat_scheme = (ImageView) llCRSKUGroup.findViewById(R.id.iv_mat_scheme);
                    iv_sku_grp_scheme.setVisibility(View.GONE);
                    iv_mat_scheme.setVisibility(View.GONE);
                    iv_expand_icon.setVisibility(View.INVISIBLE);
                    TextView tv_sku_grp_desc = (TextView) llCRSKUGroup.findViewById(R.id.tv_item_so_create_sku_grp);
                    TextView tv_ord_qty = (TextView) llSKUGroupItem.findViewById(R.id.tv_item_so_review_ord_qty);
                    TextView tv_primary_scheme = (TextView) llSKUGroupItem.findViewById(R.id.tv_item_so_review_primary_scheme);
                    TextView tv_sec_scheme = (TextView) llSKUGroupItem.findViewById(R.id.tv_item_so_review_sec_scheme);
                    TextView tv_sec_scheme_amt = (TextView) llSKUGroupItem.findViewById(R.id.tv_item_so_review_sec_scheme_amt);

                    TextView tv_net_amt = (TextView) llSKUGroupItem.findViewById(R.id.tv_item_so_review_net_amt);
                    final SKUGroupBean skuGroupBean = mapSKUGRPVal.get(key);
                    tv_sku_grp_desc.setText(skuGroupBean.getSKUGroupDesc()!=null?skuGroupBean.getSKUGroupDesc():"");
                    Constants.setFontSizeByMaxText(tv_sku_grp_desc);
                    if (!skuGroupBean.getUOM().equalsIgnoreCase(""))
                        tv_ord_qty.setText(Constants.trimQtyDecimalPlace(mapCRSSKUQTY.get(key).toString()) + " " + (skuGroupBean.getSelectedUOM().equalsIgnoreCase("")?skuGroupBean.getUOM():skuGroupBean.getSelectedUOM()));
                    else
                        tv_ord_qty.setText(String.format("%.3f", mapCRSSKUQTY.get(key)));

                    double avgPriDisVal = 0.0;
                    try {
                        avgPriDisVal = mapPriSchemePer.get(key) / mapCntMatByCRSKUGRP.get(key);
                    } catch (Exception e) {
                        avgPriDisVal = 0.0;
                    }
                    double avgSecDisVal = 0.0;
                    try {
                        avgSecDisVal = mapSecSchemePer.get(key) / mapCntMatByCRSKUGRP.get(key);
                    } catch (Exception e) {
                        avgSecDisVal = 0.0;
                    }
                    double avgSecDisAmtVal = 0.0;
                    try {
                        avgSecDisAmtVal = mapSecSchemeAmt.get(key) / mapCntMatByCRSKUGRP.get(key);
                    } catch (Exception e) {
                        avgSecDisAmtVal = 0.0;
                    }
                    tv_primary_scheme.setText(UtilConstants.removeLeadingZerowithTwoDecimal(avgPriDisVal + ""));
                    tv_sec_scheme.setText(UtilConstants.removeLeadingZerowithTwoDecimal(avgSecDisVal + ""));
                    tv_sec_scheme_amt.setText(UtilConstants.removeLeadingZerowithTwoDecimal(avgSecDisAmtVal + ""));
                    tv_net_amt.setText(UtilConstants.removeLeadingZerowithTwoDecimal(mapNetAmt.get(key).toString()));

                    TextView tv_item_free_qty_dis_amt = (TextView) llSKUGroupItem.findViewById(R.id.tv_item_free_qty_dis_amt);
                    tv_item_free_qty_dis_amt.setVisibility(View.VISIBLE);

                    TextView tv_ratio_discount_amt = (TextView) llSKUGroupItem.findViewById(R.id.tv_item_ratio_dis_amt);
                    tv_ratio_discount_amt.setVisibility(View.GONE);

                    String mStrFreeQtyDisAmt = "0";


                    // display start Scheme Free Qty Price discount 24072017
                    Double mAfterTaxCal = 0.0;
                    for (SchemeCalcuBean schemeCalcuBean : skuGroupBean.getSchemeCalcuBeanArrayList()) {
                        if (!mFreeMatScheme.contains(schemeCalcuBean.getSchemeGuidNo())) {
                            try {
                                mAfterTaxCal = Double.parseDouble(schemeCalcuBean.getmFreeMat() != null ? schemeCalcuBean.getmFreeMat().getFreeMatTax() : "0.00");
                                mFreeMatScheme.add(schemeCalcuBean.getSchemeGuidNo());
                            } catch (Exception ex) {
                                mAfterTaxCal = 0.0;
                            }
                            try {

                                mStrFreeQtyDisAmt = schemeCalcuBean.getmFreeMat() != null ? schemeCalcuBean.getmFreeMat().getFreeMatTax() : "0.00";//hashMapFreeQtyInfoBySchemeGuid.get(skuGroupBean.getSchemeGuidNo()).getFreeMatPrice();
                            } catch (Exception e) {
                                mStrFreeQtyDisAmt = "0";
                            }
                            try {
                                mDobTotalOrderVal = mDobTotalOrderVal + (Double.parseDouble(mStrFreeQtyDisAmt) + mAfterTaxCal);
                            } catch (Exception ex) {
                                mDobTotalOrderVal = mDobTotalOrderVal + 0;
                            }
                        }
                    }
                    tv_item_free_qty_dis_amt.setText(UtilConstants.removeLeadingZerowithTwoDecimal(skuGroupBean.getFreeMatDisAmt()));
                    //  display End Scheme Free Qty Price discount 24072017

                    //  display start Ratio scheme Price discount 24072017
                    if (skuGroupBean.getISFreeTypeID().equalsIgnoreCase(Constants.str_2)) {

                        tv_ratio_discount_amt.setText(UtilConstants.removeLeadingZerowithTwoDecimal(skuGroupBean.getRatioSchDisAmt()));
                        try {
                            mDobTotalOrderVal = mDobTotalOrderVal + Double.parseDouble(skuGroupBean.getRatioSchMatPrice());
                        } catch (Exception ex) {
                            mDobTotalOrderVal = mDobTotalOrderVal + 0;
                        }
                    }
                    //  display end Ratio scheme Price discount 24072017

                    tlSOList.addView(llSKUGroupItem);
                    tlCRSList.addView(llCRSKUGroup);

//                    if (alSchFreeProd.size() > 0) {
                    if (skuGrpBean.isSchemeFreeMaterial()) {
    //                    for (SchemeBean schemeBean : alSchFreeProd) {
                        for (SchemeCalcuBean schemeCalcuBean : skuGrpBean.getSchemeCalcuBeanArrayList()) {
    //                        if (key.equalsIgnoreCase(schemeBean.getOrderMaterialGroupID()) && !schemeBean.isRatioScheme()) {
                            if (schemeCalcuBean.isSchemeFreeQty()) {

                                if(!mFreeMatSchemeGuid.contains(schemeCalcuBean.getSchemeGuidNo())){
                                    mFreeMatSchemeGuid.add(schemeCalcuBean.getSchemeGuidNo());
                                    SchemeBean schemeBean = schemeCalcuBean.getmFreeMat();
                                    llSKUGroupItem = (LinearLayout) LayoutInflater.from(this)
                                            .inflate(R.layout.ll_so_review_item, null, false);
                                    llCRSKUGroup = (LinearLayout) LayoutInflater.from(this)
                                            .inflate(R.layout.subitem_so_create_skugroup, null, false);
                                    iv_expand_icon = (ImageView) llCRSKUGroup.findViewById(R.id.iv_expand_icon);
                                    iv_expand_icon.setVisibility(View.INVISIBLE);
                                    iv_mat_scheme = (ImageView) llCRSKUGroup.findViewById(R.id.iv_mat_scheme);
                                    iv_mat_scheme.setVisibility(View.GONE);
                                    tv_net_amt = (TextView) llSKUGroupItem.findViewById(R.id.tv_item_so_review_net_amt);
                                    tv_sku_grp_desc = (TextView) llCRSKUGroup.findViewById(R.id.tv_item_so_create_sku_grp);
                                    tv_ord_qty = (TextView) llSKUGroupItem.findViewById(R.id.tv_item_so_review_ord_qty);
                                    tv_primary_scheme = (TextView) llSKUGroupItem.findViewById(R.id.tv_item_so_review_primary_scheme);
                                    tv_sec_scheme = (TextView) llSKUGroupItem.findViewById(R.id.tv_item_so_review_sec_scheme);
                                    try {
                                        tv_sku_grp_desc.setText(schemeBean.getFreeMatTxt());
                                        Constants.setFontSizeByMaxText(tv_sku_grp_desc);
                                    } catch (Exception e) {
                                        tv_sku_grp_desc.setText("");
                                        e.printStackTrace();
                                    }
                                    // TODO RSPL and product specific free material and then tax cal not consider // 25-08-2018 Start Tax cal not required
                               /* try {
                                    mAfterTaxCal = Double.parseDouble(schemeBean.getFreeMatPrice()) + Double.parseDouble(schemeBean.getFreeMatTax());
                                } catch (Exception ex) {
                                    mAfterTaxCal = 0.0;
                                }*/
                                    // TODO End
                                    schemeBean.setFreeMatPrice("0.00"); // Free mat is zero
                                    try {
                                        mAfterTaxCal = Double.parseDouble(schemeBean.getFreeMatPrice());
                                    } catch (Exception ex) {
                                        mAfterTaxCal = 0.0;
                                    }

                                    tv_net_amt.setText(UtilConstants.removeLeadingZerowithTwoDecimal(mAfterTaxCal + ""));

                                    if (mAfterTaxCal <= 0) {
                                        tv_sku_grp_desc.setTextColor(getResources().getColor(R.color.BLUE));
                                    }

                                    String stFreeQty = "";
                                    try {
                                        if (OfflineManager.checkNoUOMZero(schemeBean.getFreeQtyUOM()))
                                            stFreeQty = UtilConstants.trimQtyDecimalPlace(schemeBean.getFreeQty());
                                        else
                                            stFreeQty = schemeBean.getFreeQty();
                                    } catch (OfflineODataStoreException e) {
                                        e.printStackTrace();
                                    }
                                    tv_ord_qty.setText(stFreeQty + " " + schemeBean.getFreeQtyUOM());
                                    tv_sec_scheme.setVisibility(View.INVISIBLE);
                                    tv_primary_scheme.setVisibility(View.GONE);
                                    tlSOList.addView(llSKUGroupItem);
                                    tlCRSList.addView(llCRSKUGroup);
                                }

    //                            break;
                            }
                        }
                    }
                    try {
                        if (hashMapFreeMatByOrderMatGrp.containsValue(key)) {

                            Set<String> keys = Constants.getKeysByValue(hashMapFreeMatByOrderMatGrp, key);
                            if (keys != null && !keys.isEmpty()) {
                                Iterator itr = keys.iterator();
                                while (itr.hasNext()) {
                                    SchemeBean ratioSchemeMatBean = hashMapFreeMaterialByMaterial.get(itr.next().toString());

                                    llSKUGroupItem = (LinearLayout) LayoutInflater.from(this)
                                            .inflate(R.layout.ll_so_review_item, null, false);
                                    llCRSKUGroup = (LinearLayout) LayoutInflater.from(this)
                                            .inflate(R.layout.subitem_so_create_skugroup, null, false);
                                    iv_expand_icon = (ImageView) llCRSKUGroup.findViewById(R.id.iv_expand_icon);
                                    iv_expand_icon.setVisibility(View.INVISIBLE);
                                    iv_mat_scheme = (ImageView) llCRSKUGroup.findViewById(R.id.iv_mat_scheme);
                                    iv_mat_scheme.setVisibility(View.GONE);

                                    iv_sku_grp_scheme = (ImageView) llCRSKUGroup.findViewById(R.id.iv_sku_grp_scheme);
                                    iv_sku_grp_scheme.setImageResource(R.drawable.ic_free_mat);

                                    tv_net_amt = (TextView) llSKUGroupItem.findViewById(R.id.tv_item_so_review_net_amt);
                                    tv_sku_grp_desc = (TextView) llCRSKUGroup.findViewById(R.id.tv_item_so_create_sku_grp);
                                    tv_ord_qty = (TextView) llSKUGroupItem.findViewById(R.id.tv_item_so_review_ord_qty);
                                    tv_primary_scheme = (TextView) llSKUGroupItem.findViewById(R.id.tv_item_so_review_primary_scheme);
                                    tv_sec_scheme = (TextView) llSKUGroupItem.findViewById(R.id.tv_item_so_review_sec_scheme);
                                    tv_sku_grp_desc.setText(ratioSchemeMatBean.getFreeMaterialNo());
                                    Constants.setFontSizeByMaxText(tv_sku_grp_desc);


                                    if (ratioSchemeMatBean.getISFreeTypeID().equalsIgnoreCase(Constants.str_2)) {
                                        tv_net_amt.setText(UtilConstants.removeLeadingZerowithTwoDecimal(ratioSchemeMatBean.getRatioSchMatPrice()));
                                    } else {
                                        tv_net_amt.setVisibility(View.INVISIBLE);
                                    }


                                    String stFreeQty = "";
                                    try {
                                        if (OfflineManager.checkNoUOMZero(ratioSchemeMatBean.getUOM()))
                                            stFreeQty = UtilConstants.trimQtyDecimalPlace(ratioSchemeMatBean.getFreeQty());
                                        else
                                            stFreeQty = ratioSchemeMatBean.getFreeQty();
                                    } catch (OfflineODataStoreException e) {
                                        e.printStackTrace();
                                    }
                                    tv_ord_qty.setText(stFreeQty + " " + ratioSchemeMatBean.getUOM());

                                    tv_sec_scheme.setVisibility(View.INVISIBLE);
                                    tv_primary_scheme.setVisibility(View.GONE);
                                    tlSOList.addView(llSKUGroupItem);
                                    tlCRSList.addView(llCRSKUGroup);
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else {

                tlCRSList.removeAllViews();
                tlSOList.removeAllViews();
                tlSOList = (TableLayout) findViewById(R.id.report_table);

                LinearLayout llEmptyLayout = (LinearLayout) LayoutInflater.from(SOReviewActivity.this)
                        .inflate(R.layout.so_review_empty_layout, null);

                tlSOList.addView(llEmptyLayout);
            }
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
        setUI();
    }


}
