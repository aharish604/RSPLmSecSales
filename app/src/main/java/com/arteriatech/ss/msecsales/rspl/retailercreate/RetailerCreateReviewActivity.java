package com.arteriatech.ss.msecsales.rspl.retailercreate;

import android.content.Intent;
import android.os.Bundle;
import androidx.core.view.ViewCompat;
import androidx.core.widget.NestedScrollView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.arteriatech.mutils.adapter.AdapterInterface;
import com.arteriatech.mutils.adapter.SimpleRecyclerViewAdapter;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.interfaces.DialogCallBack;
import com.arteriatech.ss.msecsales.rspl.R;
import com.arteriatech.ss.msecsales.rspl.common.Constants;
import com.arteriatech.ss.msecsales.rspl.common.ConstantsUtils;
import com.arteriatech.ss.msecsales.rspl.home.MainActivity;
import com.arteriatech.ss.msecsales.rspl.mbo.DMSDivisionBean;
import com.arteriatech.ss.msecsales.rspl.mbo.RetailerClassificationBean;
import com.arteriatech.ss.msecsales.rspl.mbo.RetailerCreateBean;
import com.arteriatech.ss.msecsales.rspl.mbo.RoutePlanBean;
import com.arteriatech.ss.msecsales.rspl.mbo.ValueHelpBean;
import com.arteriatech.ss.msecsales.rspl.so.SOUtils;

import java.util.ArrayList;

/**
 * Created by e10526 on 06-08-2018.
 */

public class RetailerCreateReviewActivity extends AppCompatActivity implements View.OnClickListener, RetailerCreateView {
    RetailerCreateBean retailerCreateBean;
    TextView tvOutletName, tvCPType, tvPricingDetail, tvOutletNameVal, tvOwnerNameVal, tvAnniversaryVal, tvAddressVal, tvDOBVal,
            tvMobileNoVal, tvNoRecordFound, tvMobileNoTwoVal, tvTelNoVal, tvFaxNoVal, tvMailIDVal,
            tvAddress, tvOrderDateDesc, tvWeeklyOffVal, tvSalesGrpVal, tvTaxFourVal,
            tvTaxThreeVal, tvTaxTwoVal, tvTaxOneVal, tvVatNoVal, tvTaxRegStatusVal,
            tvSalesOfficeVal, tvZoneVal, tvIsKeyCPVal, tvID1Val, tvID2Val, tvBussID1Val, tvBussID2Val, tvCPUIDVal, tvBeatNameVal;
    ImageView ivExpandIcon, ivSalesDetails, ivItemDetails;
    LinearLayout llHeader, ll_sales_data, ll_owner_name, llDOB, llMobileNo, llOutletName, llMobileNoTwo, llFaxNo, llAnniversary, llAddress,
            llMailID, llItemList, llTelNo, llOrderDate, llWeeklyOff, llSalesOffice, llSalesGrp, llZone, llTaxRegStatus,
            llVatNo, llTaxOne, llTaxTwo, llTaxThree, llTaxFour, llBeatName, llIsKeyCP, llID1, llID2, llBussID1, llBussID2, llCPUID;
    CardView cvOrderDetails, cvSalesDetails, cvPricingDetails;
    NestedScrollView nestedScroll;
    RecyclerView recyclerView;
    RetailerCreatePresenterImpl presenter;
    private Toolbar toolbar;
    private boolean isClickable = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ret_details);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        Bundle bundleExtras = getIntent().getExtras();
        if (bundleExtras != null) {
            retailerCreateBean = (RetailerCreateBean) bundleExtras.getSerializable(Constants.RetailerList);

        }
        //declare UI

        ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.title_retailer_create), 0);
        if (getSupportActionBar() != null)
            getSupportActionBar().setSubtitle(getString(R.string.lbl_step_four_review));

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        ViewCompat.setNestedScrollingEnabled(recyclerView, false);
        recyclerView.setLayoutManager(linearLayoutManager);

        if (!Constants.restartApp(RetailerCreateReviewActivity.this)) {
            presenter = new RetailerCreatePresenterImpl(RetailerCreateReviewActivity.this, this, true, RetailerCreateReviewActivity.this, retailerCreateBean);
            setUI();
        }
    }


    /**
     * declare UI
     */
    private void setUI() {
        tvOutletName = (TextView) findViewById(R.id.tvOutletName);
        tvCPType = (TextView) findViewById(R.id.tvexpenseType);

        tvPricingDetail = (TextView) findViewById(R.id.tvPricingDetail);
        llHeader = (LinearLayout) findViewById(R.id.headerItem);
        ll_sales_data = (LinearLayout) findViewById(R.id.ll_sales_data);

        tvOutletNameVal = (TextView) findViewById(R.id.tvOutletNameVal);
        tvOwnerNameVal = (TextView) findViewById(R.id.tvOwnerNameVal);
        tvAnniversaryVal = (TextView) findViewById(R.id.tvAnniversaryVal);
        tvAddressVal = (TextView) findViewById(R.id.tvAddressVal);
        tvDOBVal = (TextView) findViewById(R.id.tvDOBVal);
        tvMobileNoVal = (TextView) findViewById(R.id.tvMobileNoVal);
        tvMobileNoTwoVal = (TextView) findViewById(R.id.tvMobileNoTwoVal);
        tvTelNoVal = (TextView) findViewById(R.id.tvTelNoVal);
        tvFaxNoVal = (TextView) findViewById(R.id.tvFaxNoVal);
        tvMailIDVal = (TextView) findViewById(R.id.tvMailIDVal);

        tvTaxFourVal = (TextView) findViewById(R.id.tvTaxFourVal);
        tvTaxThreeVal = (TextView) findViewById(R.id.tvTaxThreeVal);
        tvTaxTwoVal = (TextView) findViewById(R.id.tvTaxTwoVal);
        tvTaxOneVal = (TextView) findViewById(R.id.tvTaxOneVal);
        tvVatNoVal = (TextView) findViewById(R.id.tvVatNoVal);
        tvTaxRegStatusVal = (TextView) findViewById(R.id.tvTaxRegStatusVal);
        tvZoneVal = (TextView) findViewById(R.id.tvZoneVal);
        tvSalesGrpVal = (TextView) findViewById(R.id.tvSalesGrpVal);
        tvSalesOfficeVal = (TextView) findViewById(R.id.tvSalesOfficeVal);
        tvWeeklyOffVal = (TextView) findViewById(R.id.tvWeeklyOffVal);

        tvIsKeyCPVal = (TextView) findViewById(R.id.tvIsKeyCPVal);
        tvID1Val = (TextView) findViewById(R.id.tvID1Val);
        tvID2Val = (TextView) findViewById(R.id.tvID2Val);
        tvBussID1Val = (TextView) findViewById(R.id.tvBussID1Val);
        tvBussID2Val = (TextView) findViewById(R.id.tvBussID2Val);
        tvCPUIDVal = (TextView) findViewById(R.id.tvCPUIDVal);
        tvBeatNameVal = (TextView) findViewById(R.id.tvBeatNameVal);

        tvOrderDateDesc = (TextView) findViewById(R.id.tvExpTypeDesc);
        ll_owner_name = (LinearLayout) findViewById(R.id.ll_owner_name);
        llAddress = (LinearLayout) findViewById(R.id.llAddress);
        llDOB = (LinearLayout) findViewById(R.id.llDOB);
        llAnniversary = (LinearLayout) findViewById(R.id.llAnniversary);
        llMobileNo = (LinearLayout) findViewById(R.id.llMobileNo);
        llOutletName = (LinearLayout) findViewById(R.id.llOutletName);
        llMobileNoTwo = (LinearLayout) findViewById(R.id.llMobileNoTwo);
        llFaxNo = (LinearLayout) findViewById(R.id.llFaxNo);
        llMailID = (LinearLayout) findViewById(R.id.llMailID);
        llTelNo = (LinearLayout) findViewById(R.id.llTelNo);
        llOrderDate = (LinearLayout) findViewById(R.id.llExpenseDate);

        llWeeklyOff = (LinearLayout) findViewById(R.id.llWeeklyOff);
        llSalesOffice = (LinearLayout) findViewById(R.id.llSalesOffice);
        llSalesGrp = (LinearLayout) findViewById(R.id.llSalesGrp);
        llZone = (LinearLayout) findViewById(R.id.llZone);
        llTaxRegStatus = (LinearLayout) findViewById(R.id.llTaxRegStatus);
        llVatNo = (LinearLayout) findViewById(R.id.llVatNo);
        llTaxOne = (LinearLayout) findViewById(R.id.llTaxOne);
        llTaxTwo = (LinearLayout) findViewById(R.id.llTaxTwo);
        llTaxThree = (LinearLayout) findViewById(R.id.llTaxThree);
        llTaxFour = (LinearLayout) findViewById(R.id.llTaxFour);

        llIsKeyCP = (LinearLayout) findViewById(R.id.llIsKeyCP);
        llID1 = (LinearLayout) findViewById(R.id.llID1);
        llID2 = (LinearLayout) findViewById(R.id.llID2);
        llBussID1 = (LinearLayout) findViewById(R.id.llBussID1);
        llBussID2 = (LinearLayout) findViewById(R.id.llBussID2);
        llCPUID = (LinearLayout) findViewById(R.id.llCPUID);
        llBeatName = (LinearLayout) findViewById(R.id.llBeatName);

        cvOrderDetails = (CardView) findViewById(R.id.cvOrderDetails);
        cvSalesDetails = (CardView) findViewById(R.id.cvSalesDetails);
        cvPricingDetails = (CardView) findViewById(R.id.cvPricingDetails);
        ivExpandIcon = (ImageView) findViewById(R.id.ivOrderDetails);
        ivSalesDetails = (ImageView) findViewById(R.id.ivSalesDetails);
        tvAddress = (TextView) findViewById(R.id.tvAddress);
        llHeader = (LinearLayout) findViewById(R.id.headerItem);
        llItemList = (LinearLayout) findViewById(R.id.llItemList);
        tvNoRecordFound = (TextView) findViewById(R.id.no_record_found);
        nestedScroll = (NestedScrollView) findViewById(R.id.nestedScroll);
        ivItemDetails = (ImageView) findViewById(R.id.ivItemDetails);
        ivItemDetails.setOnClickListener(this);
        ivExpandIcon.setOnClickListener(this);
        ivSalesDetails.setOnClickListener(this);
        setData();

    }

    private void setData() {
        tvOutletName.setText(retailerCreateBean.getName());
        tvCPType.setText(getString(R.string.po_details_display_value, retailerCreateBean.getCPTypeDesc(), retailerCreateBean.getCPTypeID()));

        if (!TextUtils.isEmpty(retailerCreateBean.getOwnerName())) {
            tvOutletNameVal.setText(retailerCreateBean.getName());
            llOutletName.setVisibility(View.VISIBLE);
        } else {
            llOutletName.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(retailerCreateBean.getOwnerName())) {
            tvOwnerNameVal.setText(retailerCreateBean.getOwnerName());
            ll_owner_name.setVisibility(View.VISIBLE);
        } else {
            ll_owner_name.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(retailerCreateBean.getDOB())) {
            tvDOBVal.setText(ConstantsUtils.convertDateIntoDisplayFormat(RetailerCreateReviewActivity.this, retailerCreateBean.getDOBTemp()));
            llDOB.setVisibility(View.VISIBLE);
        } else {
            llDOB.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(retailerCreateBean.getAnniversary())) {
            tvAnniversaryVal.setText(ConstantsUtils.convertDateIntoDisplayFormat(RetailerCreateReviewActivity.this, retailerCreateBean.getAnniversaryTemp()));
            llAnniversary.setVisibility(View.VISIBLE);
        } else {
            llAnniversary.setVisibility(View.GONE);
        }

        String mStrAddress = SOUtils.getCustomerDetailsAddressValue(retailerCreateBean);
        if (!TextUtils.isEmpty(mStrAddress)) {
            tvAddressVal.setText(mStrAddress);
            llAddress.setVisibility(View.VISIBLE);
        } else {
            llAddress.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(retailerCreateBean.getMobile1())) {
            tvMobileNoVal.setText(retailerCreateBean.getMobile1());
            llMobileNo.setVisibility(View.VISIBLE);
        } else {
            llMobileNo.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(retailerCreateBean.getMobile2())) {
            tvMobileNoTwoVal.setText(retailerCreateBean.getMobile2());
            llMobileNoTwo.setVisibility(View.VISIBLE);
        } else {
            llMobileNoTwo.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(retailerCreateBean.getLandline())) {
            tvTelNoVal.setText(retailerCreateBean.getLandline());
            llTelNo.setVisibility(View.VISIBLE);
        } else {
            llTelNo.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(retailerCreateBean.getFaxNo())) {
            tvFaxNoVal.setText(retailerCreateBean.getFaxNo());
            llFaxNo.setVisibility(View.VISIBLE);
        } else {
            llFaxNo.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(retailerCreateBean.getEmailID())) {
            tvMailIDVal.setText(retailerCreateBean.getEmailID());
            llMailID.setVisibility(View.VISIBLE);
        } else {
            llMailID.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(retailerCreateBean.getWeeklyOffDesc())) {
            tvWeeklyOffVal.setText(retailerCreateBean.getWeeklyOffDesc());
            llWeeklyOff.setVisibility(View.VISIBLE);
        } else {
            llWeeklyOff.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(retailerCreateBean.getSalesOffDesc())) {
            tvSalesOfficeVal.setText(retailerCreateBean.getSalesOffDesc());
            llSalesOffice.setVisibility(View.VISIBLE);
        } else {
            llSalesOffice.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(retailerCreateBean.getSalesGrpDesc())) {
            tvSalesGrpVal.setText(retailerCreateBean.getSalesGrpDesc());
            llSalesGrp.setVisibility(View.VISIBLE);
        } else {
            llSalesGrp.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(retailerCreateBean.getZoneDesc())) {
            tvZoneVal.setText(retailerCreateBean.getZoneDesc());
            llZone.setVisibility(View.VISIBLE);
        } else {
            llZone.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(retailerCreateBean.getTaxRegStatusDesc())) {
            tvTaxRegStatusVal.setText(retailerCreateBean.getTaxRegStatusDesc());
            llTaxRegStatus.setVisibility(View.VISIBLE);
        } else {
            llTaxRegStatus.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(retailerCreateBean.getVATNo())) {
            tvTaxRegStatusVal.setText(retailerCreateBean.getVATNo());
            llVatNo.setVisibility(View.VISIBLE);
        } else {
            llVatNo.setVisibility(View.GONE);
        }


        if (!TextUtils.isEmpty(retailerCreateBean.getTax1())) {
            tvTaxOneVal.setText(retailerCreateBean.getTax1());
            llTaxOne.setVisibility(View.VISIBLE);
        } else {
            llTaxOne.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(retailerCreateBean.getTax2())) {
            tvTaxTwoVal.setText(retailerCreateBean.getTax2());
            llTaxTwo.setVisibility(View.VISIBLE);
        } else {
            llTaxTwo.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(retailerCreateBean.getTax3())) {
            tvTaxThreeVal.setText(retailerCreateBean.getTax3());
            llTaxThree.setVisibility(View.VISIBLE);
        } else {
            llTaxThree.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(retailerCreateBean.getTax4())) {
            tvTaxFourVal.setText(retailerCreateBean.getTax4());
            llTaxFour.setVisibility(View.VISIBLE);
        } else {
            llTaxFour.setVisibility(View.GONE);
        }

        if (retailerCreateBean.isKeyCP()) {
            tvIsKeyCPVal.setText(Constants.YES);
            llIsKeyCP.setVisibility(View.VISIBLE);
        } else {
            llIsKeyCP.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(retailerCreateBean.getID1())) {
            tvID1Val.setText(retailerCreateBean.getID1());
            llID1.setVisibility(View.VISIBLE);
        } else {
            llID1.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(retailerCreateBean.getID2())) {
            tvID2Val.setText(retailerCreateBean.getID2());
            llID2.setVisibility(View.VISIBLE);
        } else {
            llID2.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(retailerCreateBean.getBusinessID1())) {
            tvBussID1Val.setText(retailerCreateBean.getBusinessID1());
            llBussID1.setVisibility(View.VISIBLE);
        } else {
            llBussID1.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(retailerCreateBean.getBusinessID2())) {
            tvBussID2Val.setText(retailerCreateBean.getBusinessID2());
            llBussID2.setVisibility(View.VISIBLE);
        } else {
            llBussID2.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(retailerCreateBean.getCPUID())) {
            tvCPUIDVal.setText(retailerCreateBean.getCPUID());
            llCPUID.setVisibility(View.VISIBLE);
        } else {
            llCPUID.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(retailerCreateBean.getRouteSchGUID())) {
            tvBeatNameVal.setText(retailerCreateBean.getRouteDesc());
            llBeatName.setVisibility(View.VISIBLE);
        } else {
            llBeatName.setVisibility(View.GONE);
        }

//        displayConditionItemDetails();
        SimpleRecyclerViewAdapter<RetailerClassificationBean> recyclerViewAdapter = new SimpleRecyclerViewAdapter<>(this, R.layout.dms_divi_line_review_item, new AdapterInterface<RetailerClassificationBean>() {
            @Override
            public void onItemClick(RetailerClassificationBean soItemBean, View view, int i) {

            }

            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i, View view) {
                return new DMSDivisionSelctionVH(view);
            }

            @Override
            public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, int i, final RetailerClassificationBean retailerClassificationBean) {
                ((DMSDivisionSelctionVH) viewHolder).tvDmsDivisionDesc.setText(retailerClassificationBean.getDMSDivisionDesc());
                ((DMSDivisionSelctionVH) viewHolder).textViewNoOfBills.setText(retailerClassificationBean.getGroup1Desc());

                ((DMSDivisionSelctionVH) viewHolder).tvGroupOneVal.setText(retailerClassificationBean.getGroup1Desc());
                ((DMSDivisionSelctionVH) viewHolder).tvGroupTwoVal.setText(retailerClassificationBean.getGroup2Desc());
                ((DMSDivisionSelctionVH) viewHolder).tvGroupThreeVal.setText(retailerClassificationBean.getGroup3Desc());
                ((DMSDivisionSelctionVH) viewHolder).tvGroupFourVal.setText(retailerClassificationBean.getGroup4Desc());
                ((DMSDivisionSelctionVH) viewHolder).tvGroupFiveVal.setText(retailerClassificationBean.getGroup5Desc());


                ((DMSDivisionSelctionVH) viewHolder).iv_expand_div_val.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (((DMSDivisionSelctionVH) viewHolder).detailsLayout.getVisibility() == View.VISIBLE) {
                            ((DMSDivisionSelctionVH) viewHolder).iv_expand_div_val.setImageResource(R.drawable.ic_arrow_down_black_24dp);
                            ((DMSDivisionSelctionVH) viewHolder).detailsLayout.setVisibility(View.GONE);
                            ((DMSDivisionSelctionVH) viewHolder).ll_line_div.setVisibility(View.GONE);
                        } else {
                            ((DMSDivisionSelctionVH) viewHolder).ll_line_div.setVisibility(View.VISIBLE);
                            ((DMSDivisionSelctionVH) viewHolder).detailsLayout.setVisibility(View.VISIBLE);
                            ((DMSDivisionSelctionVH) viewHolder).iv_expand_div_val.setImageResource(R.drawable.ic_arrow_up_black_24dp);

                        }
                    }
                });

               /* ((DMSDivisionSelctionVH) viewHolder).tvDiscountPer.setText(UtilConstants.removeLeadingZero(retailerClassificationBean.getDiscountPer())+" %");
                ((DMSDivisionSelctionVH) viewHolder).tvCreditLimit.setText(UtilConstants.getCurrencyFormat("INR", retailerClassificationBean.getCreditLimit()));

                if(Double.parseDouble(!retailerClassificationBean.getCreditDays().equalsIgnoreCase("")?retailerClassificationBean.getCreditDays():"0")>1){
                    ((DMSDivisionSelctionVH) viewHolder).tvNoOfDays.setText(retailerClassificationBean.getCreditDays() + " " + getString(R.string.days));
                }else{
                    ((DMSDivisionSelctionVH) viewHolder).tvNoOfDays.setText(retailerClassificationBean.getCreditDays() + " " + getString(R.string.day));
                }

                if(Double.parseDouble(!retailerClassificationBean.getCreditBills().equalsIgnoreCase("")?retailerClassificationBean.getCreditBills():"0")>1){
                    ((DMSDivisionSelctionVH) viewHolder).textViewNoOfBills.setText(retailerClassificationBean.getCreditBills() + " " + getString(R.string.bills));
                }else{
                    ((DMSDivisionSelctionVH) viewHolder).textViewNoOfBills.setText(retailerClassificationBean.getCreditBills() + " " + getString(R.string.bill));
                }*/

            }
        }, recyclerView, tvNoRecordFound);
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerViewAdapter.refreshAdapter(retailerCreateBean.getAlRetClassfication());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_next_save, menu);
        menu.removeItem(R.id.menu_next);
        menu.removeItem(R.id.menu_review);
        menu.removeItem(R.id.menu_add);
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
                    if (ConstantsUtils.isAutomaticTimeZone(RetailerCreateReviewActivity.this)) {
    //                    UtilConstants.dialogBoxWithCallBack(RetailerCreateReviewActivity.this, "", getString(R.string.so_save_retail_msg), getString(R.string.ok), getString(R.string.cancel), false, new DialogCallBack() {
    //                        @Override
    //                        public void clickedStatus(boolean b) {
    //                            if (b) {
                        item.setEnabled(false);
                        presenter.onSaveData();

    //                            }
    //                        }
    //                    });

                    } else {
                        item.setEnabled(true);
                        isClickable=false;
                        ConstantsUtils.showAutoDateSetDialog(RetailerCreateReviewActivity.this);
                    }
                }
                break;

        }
        return true;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivOrderDetails:
                if (llHeader.getVisibility() == View.VISIBLE) {
                    ivExpandIcon.setImageResource(R.drawable.ic_arrow_down_black_24dp);
                    llHeader.setVisibility(View.GONE);
                    ViewGroup.MarginLayoutParams layoutParams = getLayoutParams(cvOrderDetails);
                    int marginB = ConstantsUtils.dpToPx(8, this);
                    layoutParams.setMargins(ConstantsUtils.dpToPx(8, this), ConstantsUtils.dpToPx(8, this), ConstantsUtils.dpToPx(8, this), marginB);
                    cvOrderDetails.requestLayout();
                } else {
                    ivExpandIcon.setImageResource(R.drawable.ic_arrow_up_black_24dp);
                    llHeader.setVisibility(View.VISIBLE);
//                    tvDate.setVisibility(View.GONE);
                    int marginB = ConstantsUtils.dpToPx(8, this);

                    ViewGroup.MarginLayoutParams layoutParams = getLayoutParams(cvOrderDetails);
                    layoutParams.setMargins(ConstantsUtils.dpToPx(8, this), ConstantsUtils.dpToPx(8, this), ConstantsUtils.dpToPx(8, this), marginB);

                    cvOrderDetails.requestLayout();

                }
                break;
            case R.id.ivSalesDetails:
                if (ll_sales_data.getVisibility() == View.VISIBLE) {
                    ivSalesDetails.setImageResource(R.drawable.ic_arrow_down_black_24dp);
                    ll_sales_data.setVisibility(View.GONE);
                    ViewGroup.MarginLayoutParams layoutParams = getLayoutParams(cvSalesDetails);
                    int marginB = ConstantsUtils.dpToPx(8, this);
                    layoutParams.setMargins(ConstantsUtils.dpToPx(8, this), ConstantsUtils.dpToPx(8, this), ConstantsUtils.dpToPx(8, this), marginB);
                    cvSalesDetails.requestLayout();
                } else {
                    ivSalesDetails.setImageResource(R.drawable.ic_arrow_up_black_24dp);
                    ll_sales_data.setVisibility(View.VISIBLE);
//                    tvDate.setVisibility(View.GONE);
                    int marginB = ConstantsUtils.dpToPx(8, this);

                    ViewGroup.MarginLayoutParams layoutParams = getLayoutParams(cvSalesDetails);
                    layoutParams.setMargins(ConstantsUtils.dpToPx(8, this), ConstantsUtils.dpToPx(8, this), ConstantsUtils.dpToPx(8, this), marginB);

                    cvSalesDetails.requestLayout();

                }
                break;
        }
    }

    private ViewGroup.MarginLayoutParams getLayoutParams(CardView cardView) {
        return (ViewGroup.MarginLayoutParams) cardView.getLayoutParams();
    }

    @Override
    public void showProgressDialog(String message) {

    }

    @Override
    public void hideProgressDialog() {

    }

    @Override
    public void displayMessage(String message) {

    }

    @Override
    public void displayCPType(ArrayList<ValueHelpBean> cpType, ArrayList<ValueHelpBean> alCountryList, ArrayList<ValueHelpBean> alStateList, final ArrayList<RoutePlanBean> routePlanBeanArrayList, String defaultStateID) {

    }

    @Override
    public void displayWeeklyOff(ArrayList<ValueHelpBean> alWeeklyOff, ArrayList<ValueHelpBean> alTaxStatus) {

    }

    @Override
    public void displayDMSDivision(ArrayList<DMSDivisionBean> alDmsDiv, ArrayList<ValueHelpBean> alGrpOne, ArrayList<ValueHelpBean> alGrpFour, ArrayList<ValueHelpBean> alGrpFive) {

    }

    @Override
    public void displayGrpTwoValue(ArrayList<ValueHelpBean> alGrpTwo) {

    }

    @Override
    public void displayGrpThreeValue(ArrayList<ValueHelpBean> alGrpThree) {

    }

    @Override
    public void errorRetailerType(String message) {

    }

    @Override
    public void errorOutletName(String message) {

    }

    @Override
    public void errorOwnerName(String message) {

    }

    @Override
    public void errorAddressOne(String message) {

    }

    @Override
    public void errorLandMArk(String message) {

    }

    @Override
    public void errorCountry(String message) {

    }

    @Override
    public void errorState(String message) {

    }

    @Override
    public void errorRouteName(String message) {

    }

    @Override
    public void errorPostlcode(String message) {

    }

    @Override
    public void errorMobileOne(String message) {

    }

    @Override
    public void errorMobileTwo(String message) {

    }

    @Override
    public void errorID(String message) {

    }

    @Override
    public void errorBussnessID(String message) {

    }

    @Override
    public void errorTelNo(String message) {

    }

    @Override
    public void errorFaxNo(String message) {

    }

    @Override
    public void errorEmailId(String message) {

    }

    @Override
    public void errorAnniversary(String message) {

    }

    @Override
    public void errorRemarks(String message) {

    }

    @Override
    public void errorOthers(String message) {

    }

    @Override
    public void errorDMSDiv(String message) {

    }

    @Override
    public void errorGroupOne(String message) {

    }

    @Override
    public void errorGroupTwo(String message) {

    }

    @Override
    public void errorGroupThree(String message) {

    }

    @Override
    public void errorGroupFour(String message) {

    }

    @Override
    public void errorGroupFive(String message) {

    }

    @Override
    public void errorDiscountPercentage(String message) {

    }

    @Override
    public void conformationDialog(String message, int from) {

    }

    @Override
    public void showMessage(String message, final boolean isSimpleDialog) {
        UtilConstants.dialogBoxWithCallBack(RetailerCreateReviewActivity.this, "", message, getString(R.string.ok), "", false, new DialogCallBack() {
            @Override
            public void clickedStatus(boolean b) {
                if (!isSimpleDialog) {
                    redirectActivity();
                }
            }
        });
    }

    private void redirectActivity() {
        Intent intentNavPrevScreen = new Intent(this, MainActivity.class);
        intentNavPrevScreen.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intentNavPrevScreen);
    }
}
