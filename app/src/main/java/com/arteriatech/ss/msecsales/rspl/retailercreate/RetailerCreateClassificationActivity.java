package com.arteriatech.ss.msecsales.rspl.retailercreate;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.arteriatech.mutils.adapter.AdapterInterface;
import com.arteriatech.mutils.adapter.SimpleRecyclerViewAdapter;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.interfaces.DialogCallBack;
import com.arteriatech.ss.msecsales.rspl.R;
import com.arteriatech.ss.msecsales.rspl.common.Constants;
import com.arteriatech.ss.msecsales.rspl.common.ConstantsUtils;
import com.arteriatech.ss.msecsales.rspl.mbo.DMSDivisionBean;
import com.arteriatech.ss.msecsales.rspl.mbo.RetailerClassificationBean;
import com.arteriatech.ss.msecsales.rspl.mbo.RetailerCreateBean;
import com.arteriatech.ss.msecsales.rspl.mbo.RoutePlanBean;
import com.arteriatech.ss.msecsales.rspl.mbo.ValueHelpBean;

import java.util.ArrayList;

/**
 * Created by e10526 on 02-08-2018.
 *
 */

public class RetailerCreateClassificationActivity extends AppCompatActivity implements RetailerCreateView,AdapterInterface<RetailerClassificationBean> {

    private Toolbar toolbar;
    private Context mContext;
    RetailerCreatePresenterImpl presenter;
    RetailerCreateBean retailerCreateBean =new RetailerCreateBean();
    ProgressDialog progressDialog=null;
    ArrayList<RetailerClassificationBean> alRetClassfication =new ArrayList<>();

    private RecyclerView recyclerView;
    private TextView noRecordFound;
    private SimpleRecyclerViewAdapter<RetailerClassificationBean> simpleRecyclerViewAdapter;
    public static final int INTENT_RESULT_RETAILER_CREATE = 111;
    private RetailerClassificationBean retailerClassificationBean=new RetailerClassificationBean();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ret_classfication);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            retailerCreateBean = (RetailerCreateBean) extras.getSerializable(Constants.RetailerList);
        }

        initUI();
        if(retailerCreateBean==null){
            retailerCreateBean = new RetailerCreateBean();
        }
        alRetClassfication.addAll(retailerCreateBean.getAlRetClassfication());
        presenter = new RetailerCreatePresenterImpl(RetailerCreateClassificationActivity.this, this, true, RetailerCreateClassificationActivity.this, retailerCreateBean);
        if (!Constants.restartApp(RetailerCreateClassificationActivity.this)) {
//            presenter.onReqSalesData();
        }
    }

    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
        @Override
        public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
            // super.onSelectedChanged(viewHolder, actionState);
            if (viewHolder != null) {
                final View foregroundView = ((DMSDivisionSelctionVH) viewHolder).viewForeground;
                getDefaultUIUtil().onSelected(foregroundView);
            }
        }
        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            return false;
        }
        @Override
        public void onChildDrawOver(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            int position = viewHolder.getAdapterPosition();
//            DealerStockBean retailerStkBean = retailerStckList.get(position);
//            if (retailerStkBean.isChecked()) {
            final View foregroundView = (((DMSDivisionSelctionVH) viewHolder).viewForeground);
            getDefaultUIUtil().onDrawOver(c, recyclerView, foregroundView, dX, dY, actionState, isCurrentlyActive);
//            }
        }
        @Override
        public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            try {
                final View foregroundView = ((DMSDivisionSelctionVH) viewHolder).viewForeground;
                getDefaultUIUtil().clearView(foregroundView);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        @Override
        public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            int position = viewHolder.getAdapterPosition();
            View foregroundView = ((DMSDivisionSelctionVH) viewHolder).viewForeground;
            getDefaultUIUtil().onDraw(c, recyclerView, foregroundView, dX, dY,
                    actionState, isCurrentlyActive);
//            }
        }
        @Override
        public int convertToAbsoluteDirection(int flags, int layoutDirection) {
            return super.convertToAbsoluteDirection(flags, layoutDirection);
        }
        @Override
        public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {
            if (viewHolder instanceof DMSDivisionSelctionVH) {
                final int position = viewHolder.getAdapterPosition(); //swiped position
                if (!alRetClassfication.isEmpty()) {
                    RetailerClassificationBean retailerStkBean = alRetClassfication.get(position);
                    if (direction == ItemTouchHelper.RIGHT) {
                        ((DMSDivisionSelctionVH) viewHolder).ivRight.setVisibility(View.GONE);
                        ((DMSDivisionSelctionVH) viewHolder).ivLeft.setVisibility(View.VISIBLE);
                        resetMatView(retailerStkBean, viewHolder);
                    } else if (direction == ItemTouchHelper.LEFT) {
                        ((DMSDivisionSelctionVH) viewHolder).ivRight.setVisibility(View.VISIBLE);
                        ((DMSDivisionSelctionVH) viewHolder).ivLeft.setVisibility(View.GONE);
                        resetMatView(retailerStkBean, viewHolder);
                    }
                }
            }
        }
    };
    private void resetMatView(RetailerClassificationBean retailerStkBean, final RecyclerView.ViewHolder holder) {
        alRetClassfication.remove(retailerStkBean);
//        presenter.removeItem(retailerStkBean);
        simpleRecyclerViewAdapter.refreshAdapter(alRetClassfication);
    }
    private void initUI(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        mContext = RetailerCreateClassificationActivity.this;
        ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.title_retailer_create), 0);
        if (getSupportActionBar() != null)
            getSupportActionBar().setSubtitle(getString(R.string.lbl_step_three_classfication));

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        noRecordFound = (TextView) findViewById(R.id.add_mat_lay);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
        simpleRecyclerViewAdapter = new SimpleRecyclerViewAdapter<RetailerClassificationBean>(RetailerCreateClassificationActivity.this, R.layout.dms_division_line_item, this, recyclerView, noRecordFound);
        recyclerView.setAdapter(simpleRecyclerViewAdapter);
        refreshAdapter(alRetClassfication);
    }

    @Override
    public void showProgressDialog(String message) {
        progressDialog = ConstantsUtils.showProgressDialog(RetailerCreateClassificationActivity.this, message);
    }

    @Override
    public void hideProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void displayMessage(String message) {
        UtilConstants.showAlert(message,mContext);
    }

    @Override
    public void showMessage(String message, final boolean isSimpleDialog) {
        UtilConstants.dialogBoxWithCallBack(RetailerCreateClassificationActivity.this, "", message, getString(R.string.ok), "", false, new DialogCallBack() {
            @Override
            public void clickedStatus(boolean b) {
                if (!isSimpleDialog) {
//                    redirectActivity();
                }
            }
        });
    }


    @Override
    public void displayCPType(final ArrayList<ValueHelpBean> cpType,final ArrayList<ValueHelpBean> alCountry,final ArrayList<ValueHelpBean> alState,final ArrayList<RoutePlanBean> routePlanBeanArrayList,String defaultStateID) {

    }

    @Override
    public void displayWeeklyOff(final ArrayList<ValueHelpBean> alWeeklyOff,final ArrayList<ValueHelpBean> alTaxRegStatus) {

    }

    @Override
    public void displayDMSDivision(ArrayList<DMSDivisionBean> alDmsDiv, final ArrayList<ValueHelpBean> alGrpOne,ArrayList<ValueHelpBean> alGrpFour,ArrayList<ValueHelpBean> alGrpFive) {

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
        UtilConstants.dialogBoxWithCallBack(RetailerCreateClassificationActivity.this, "", message, getString(R.string.ok), getString(R.string.cancel), false, new DialogCallBack() {
            @Override
            public void clickedStatus(boolean b) {
                if (b) {
                    presenter.onSaveData();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_next_save, menu);
        menu.removeItem(R.id.menu_next);
        menu.removeItem(R.id.menu_save);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.menu_review:
                //next step
                if (ConstantsUtils.isAutomaticTimeZone(RetailerCreateClassificationActivity.this)) {
                    retailerCreateBean.setAlRetClassfication(alRetClassfication);
                    if (presenter.validateDMSDiv(retailerCreateBean)) {
                        Intent intent = new Intent(mContext, RetailerCreateReviewActivity.class);
                        intent.putExtra(Constants.RetailerList, retailerCreateBean);
                        startActivity(intent);
                    }
                } else {
                    ConstantsUtils.showAutoDateSetDialog(RetailerCreateClassificationActivity.this);
                }
                return true;
            case R.id.menu_add:
                Intent intent = new Intent(mContext, AddDmsDivisionsActivity.class);
                intent.putExtra(Constants.DMSDivisionID, "");
                intent.putExtra(Constants.DMSDivisionDesc, "");
                intent.putExtra(Constants.Group1, "");
                intent.putExtra(Constants.Group2, "");
                intent.putExtra(Constants.Group3, "");
                intent.putExtra(Constants.Group4, "");
                intent.putExtra(Constants.Group5, "");
                intent.putExtra(Constants.DiscountPer, "");
                intent.putExtra(Constants.CreditLimit, "");
                intent.putExtra(Constants.CreditPeriod, "");
                intent.putExtra(Constants.CreditBills, "");
                intent.putExtra(Constants.CPGUID, retailerCreateBean.getRouteDist());
                startActivityForResult(intent, RetailerCreateClassificationActivity.INTENT_RESULT_RETAILER_CREATE);

                /*presenter.addRetailerStock(retailerStckList);*/
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onItemClick(RetailerClassificationBean item, View view, int position) {


    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType, View viewItem) {
        return new DMSDivisionSelctionVH(viewItem);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position, final RetailerClassificationBean retailerClassificationBean) {

        ((DMSDivisionSelctionVH) holder).tvDmsDivisionDesc.setText(retailerClassificationBean.getDMSDivisionDesc());
        ((DMSDivisionSelctionVH) holder).textViewNoOfBills.setText(retailerClassificationBean.getGroup1Desc());
        ((DMSDivisionSelctionVH) holder).tvGroupOneVal.setText(retailerClassificationBean.getGroup1Desc());
        ((DMSDivisionSelctionVH) holder).tvGroupTwoVal.setText(retailerClassificationBean.getGroup2Desc());
        ((DMSDivisionSelctionVH) holder).tvGroupThreeVal.setText(retailerClassificationBean.getGroup3Desc());
        ((DMSDivisionSelctionVH) holder).tvGroupFourVal.setText(retailerClassificationBean.getGroup4Desc());
        ((DMSDivisionSelctionVH) holder).tvGroupFiveVal.setText(retailerClassificationBean.getGroup5Desc());


        ((DMSDivisionSelctionVH) holder).iv_expand_div_val.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((DMSDivisionSelctionVH) holder).detailsLayout.getVisibility() == View.VISIBLE) {
                    ((DMSDivisionSelctionVH) holder).iv_expand_div_val.setImageResource(R.drawable.ic_arrow_down_black_24dp);
                    ((DMSDivisionSelctionVH) holder).detailsLayout.setVisibility(View.GONE);
                    ((DMSDivisionSelctionVH) holder).ll_line_div.setVisibility(View.GONE);
                } else {
                    ((DMSDivisionSelctionVH) holder).ll_line_div.setVisibility(View.VISIBLE);
                    ((DMSDivisionSelctionVH) holder).detailsLayout.setVisibility(View.VISIBLE);
                    ((DMSDivisionSelctionVH) holder).iv_expand_div_val.setImageResource(R.drawable.ic_arrow_up_black_24dp);

                }
            }
        });


        /*((DMSDivisionSelctionVH) holder).tvDiscountPer.setText(UtilConstants.removeLeadingZero(retailerClassificationBean.getDiscountPer())+" %");
        ((DMSDivisionSelctionVH) holder).tvCreditLimit.setText(UtilConstants.getCurrencyFormat("INR", retailerClassificationBean.getCreditLimit()));
        if(Double.parseDouble(!retailerClassificationBean.getCreditDays().equalsIgnoreCase("")?retailerClassificationBean.getCreditDays():"0")>1){
            ((DMSDivisionSelctionVH) holder).tvNoOfDays.setText(retailerClassificationBean.getCreditDays() + " " + getString(R.string.days));
        }else{
            ((DMSDivisionSelctionVH) holder).tvNoOfDays.setText(retailerClassificationBean.getCreditDays() + " " + getString(R.string.day));
        }

        if(Double.parseDouble(!retailerClassificationBean.getCreditBills().equalsIgnoreCase("")?retailerClassificationBean.getCreditBills():"0")>1){
            ((DMSDivisionSelctionVH) holder).textViewNoOfBills.setText(retailerClassificationBean.getCreditBills() + " " + getString(R.string.bills));
        }else{
            ((DMSDivisionSelctionVH) holder).textViewNoOfBills.setText(retailerClassificationBean.getCreditBills() + " " + getString(R.string.bill));
        }*/
    }

    @SuppressWarnings("all")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RetailerCreateClassificationActivity.INTENT_RESULT_RETAILER_CREATE) {
            try {
                retailerClassificationBean = (RetailerClassificationBean) data.getSerializableExtra(Constants.CPDMSDivisions);
                alRetClassfication.add(retailerClassificationBean);
                refreshAdapter(alRetClassfication);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    private void refreshAdapter(ArrayList<RetailerClassificationBean> alRetClassfication) {
//        retailerStckList = retailerItemList;
        simpleRecyclerViewAdapter.refreshAdapter(alRetClassfication);
    }
}
