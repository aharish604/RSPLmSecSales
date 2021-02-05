package com.arteriatech.ss.msecsales.rspl.reports.collection.header;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.ViewCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.arteriatech.mutils.adapter.AdapterInterface;
import com.arteriatech.mutils.adapter.SimpleRecyclerViewAdapter;
import com.arteriatech.ss.msecsales.rspl.R;
import com.arteriatech.ss.msecsales.rspl.common.Constants;
import com.arteriatech.ss.msecsales.rspl.common.ConstantsUtils;
import com.arteriatech.ss.msecsales.rspl.mbo.CollectionHistoryBean;
import com.arteriatech.ss.msecsales.rspl.mbo.SalesOrderBean;
import com.arteriatech.ss.msecsales.rspl.so.SOUtils;

public class CollectionListDetailsActivity extends AppCompatActivity implements AdapterInterface<SalesOrderBean>, View.OnClickListener {


    CollectionHistoryBean collectionHistoryBean;

    TextView tvCollNo, tvCollValue, tvOrderType, tvNoRecordFound, tvPaymnetMode, tv_RetailerName, tv_RetailerID, tvBankNameDesc, tvBranchNameDesc, tvNumber, tvNumberDesc, tvDateDesc, tvRemarkDesc;
    RecyclerView recyclerView;
    ConstraintLayout cl_ret_det_layout = null;
    private Toolbar toolbar;
    private LinearLayout llItemList = null;
    private ImageView ivPaymentDetails;
    private View headerItem;
    private CardView cvPaymentDetails;
    private LinearLayout llBranch, llOthers, llPayment;
    private CardView cvItem;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coll_details);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        Bundle bundleExtras = getIntent().getExtras();
        if (bundleExtras != null) {
            collectionHistoryBean = (CollectionHistoryBean) bundleExtras.getSerializable(Constants.EXTRA_SO_DETAIL);
        }
        if (collectionHistoryBean == null) {
            collectionHistoryBean = new CollectionHistoryBean();
        }
        //declare UI

//        if(collectionHistoryBean.getReferenceTypeID().equalsIgnoreCase(Constants.str_01)){
        if (!TextUtils.isEmpty(collectionHistoryBean.getFIPDocNo())) {
            ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.title_coll_det_with_coll_no, collectionHistoryBean.getFIPDocNo()), 0);
        } else {
            ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.title_coll_det), 0);
        }

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        llItemList = (LinearLayout) findViewById(R.id.llItemList);
        cvItem = (CardView) findViewById(R.id.cvItem);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        ViewCompat.setNestedScrollingEnabled(recyclerView, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        setUI();
    }


    /**
     * declare UI
     */
    private void setUI() {
        tvCollNo = (TextView) findViewById(R.id.tvCollNo);
        tvCollValue = (TextView) findViewById(R.id.tvCollValue);
        tvOrderType = (TextView) findViewById(R.id.tvOrderType);
        tvPaymnetMode = (TextView) findViewById(R.id.tvPaymnetMode);
        tv_RetailerName = (TextView) findViewById(R.id.tv_RetailerName);
        tv_RetailerID = (TextView) findViewById(R.id.tv_RetailerID);
        cl_ret_det_layout = (ConstraintLayout) findViewById(R.id.cl_ret_det_layout);

        llBranch = (LinearLayout) findViewById(R.id.llBranch);
        llPayment = (LinearLayout) findViewById(R.id.llPayment);
        llOthers = (LinearLayout) findViewById(R.id.llOthers);
        tvBankNameDesc = (TextView) findViewById(R.id.tvBankNameDesc);
        tvBranchNameDesc = (TextView) findViewById(R.id.tvBranchNameDesc);
        tvNumber = (TextView) findViewById(R.id.tvNumber);
        tvNumberDesc = (TextView) findViewById(R.id.tvNumberDesc);
        tvDateDesc = (TextView) findViewById(R.id.tvDateDesc);
        tvRemarkDesc = (TextView) findViewById(R.id.tvRemarkDesc);
        ivPaymentDetails = (ImageView) findViewById(R.id.ivPaymentDetails);
        headerItem = (View) findViewById(R.id.headerItem);
        cvPaymentDetails = (CardView) findViewById(R.id.cvPaymentDetails);
        ivPaymentDetails.setOnClickListener(this);
        llBranch.setVisibility(View.GONE);
        setCollItemData();
    }

    private void setCollItemData() {
        tvCollNo.setText(collectionHistoryBean.getFIPDate());
        tvCollValue.setText(Constants.getCurrencySymbol(collectionHistoryBean.getCurrency(), collectionHistoryBean.getAmount()));
        tvOrderType.setText(collectionHistoryBean.getReferenceTypeDesc());
        tvPaymnetMode.setText(collectionHistoryBean.getPaymentModeDesc());
        tv_RetailerID.setText(collectionHistoryBean.getRetID());
        tv_RetailerName.setText(collectionHistoryBean.getRetName());
        if (!collectionHistoryBean.getPaymentModeID().equalsIgnoreCase("04")) {
            cvPaymentDetails.setVisibility(View.VISIBLE);
            tvBankNameDesc.setText(collectionHistoryBean.getBankName());
            tvNumberDesc.setText(collectionHistoryBean.getInstrumentNo());
            tvDateDesc.setText(collectionHistoryBean.getInstrumentDate());
            if (TextUtils.isEmpty(collectionHistoryBean.getBranchName())) {
                llBranch.setVisibility(View.GONE);
            } else {
                llBranch.setVisibility(View.VISIBLE);
                tvBranchNameDesc.setText(collectionHistoryBean.getBranchName());
            }

            if (collectionHistoryBean.getPaymentModeID().equalsIgnoreCase("03")) {
                tvNumber.setText(getString(R.string.coll_header_utr));
            } else if (collectionHistoryBean.getPaymentModeID().equalsIgnoreCase("02")) {
                tvNumber.setText(getString(R.string.coll_header_crd));
            } else if (collectionHistoryBean.getPaymentModeID().equalsIgnoreCase("01")) {
                tvNumber.setText(getString(R.string.coll_header_cheque));
            }
        } else {
            cvPaymentDetails.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(collectionHistoryBean.getRemarks())) {
            if (cvPaymentDetails.getVisibility()==View.GONE){
                llPayment.setVisibility(View.GONE);
                cvPaymentDetails.setVisibility(View.VISIBLE);
            }
            llOthers.setVisibility(View.VISIBLE);
            tvRemarkDesc.setText(collectionHistoryBean.getRemarks());
        }else {
            llOthers.setVisibility(View.GONE);
        }
        if (!collectionHistoryBean.getReferenceTypeID().equalsIgnoreCase(Constants.str_01)) {
            cvItem.setVisibility(View.GONE);
            onHeaderDisplay();
        }
        SimpleRecyclerViewAdapter<CollectionHistoryBean> recyclerViewAdapter = new SimpleRecyclerViewAdapter<>(this, R.layout.coll_item_details, new AdapterInterface<CollectionHistoryBean>() {
            @Override
            public void onItemClick(CollectionHistoryBean soItemBean, View view, int i) {

            }

            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i, View view) {
                return new CollectionDetailsViewHolder(view, CollectionListDetailsActivity.this);
            }

            @Override
            public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, int i, final CollectionHistoryBean collItemBean) {

                Drawable collStatus = SOUtils.displayCollectionStatusImage(collItemBean.getInvoiceBalanceAmount(), CollectionListDetailsActivity.this);
                if (collStatus != null) {
                    ((CollectionDetailsViewHolder) viewHolder).iv_coll_status.setImageDrawable(collStatus);
                }
                ((CollectionDetailsViewHolder) viewHolder).tvBillNo.setText(getString(R.string.lbl_invoice, collItemBean.getInvoiceNo()));
                ((CollectionDetailsViewHolder) viewHolder).tvBillDate.setText(collItemBean.getInvoiceDate());
                ((CollectionDetailsViewHolder) viewHolder).tvInvAmt.setText(Constants.getCurrencySymbol(collectionHistoryBean.getCurrency(), collItemBean.getInvoiceAmount()));
                ((CollectionDetailsViewHolder) viewHolder).tv_coll_det_paid_amt.setText(Constants.getCurrencySymbol(collectionHistoryBean.getCurrency(), collItemBean.getPaidAmt()));
                ((CollectionDetailsViewHolder) viewHolder).tv_coll_total_paid_amt.setText(Constants.getCurrencySymbol(collectionHistoryBean.getCurrency(), collItemBean.getInvoiceClearedAmount()));
                ((CollectionDetailsViewHolder) viewHolder).tv_coll_det_bal_amt_ex.setText(Constants.getCurrencySymbol(collectionHistoryBean.getCurrency(), collItemBean.getInvoiceBalanceAmount()));
                ((CollectionDetailsViewHolder) viewHolder).tvPayable.setText(Constants.getCurrencySymbol(collectionHistoryBean.getCurrency(), collItemBean.getPayable()));
                ((CollectionDetailsViewHolder) viewHolder).tvDiscountPer.setText(getString(R.string.lbl_paiable_discount)+"("+collItemBean.getCashDiscountPercentage()+"%)");
                ((CollectionDetailsViewHolder) viewHolder).tvDiscountPerAmt.setText(Constants.getCurrencySymbol(collectionHistoryBean.getCurrency(), collItemBean.getCashDiscount()));
                ((CollectionDetailsViewHolder) viewHolder).tvNetPayable.setText(Constants.getCurrencySymbol(collectionHistoryBean.getCurrency(), collItemBean.getNetPayable()));

            }
        }, recyclerView, tvNoRecordFound);
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerViewAdapter.refreshAdapter(collectionHistoryBean.getAlCollItemList());
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }


    @Override
    public void onItemClick(SalesOrderBean salesOrderBean, View view, int i) {

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i, View view) {
        return new CollectionDetailsViewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i, SalesOrderBean salesOrderBean) {
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivPaymentDetails:
                onHeaderDisplay();
                break;
        }
    }

    private void onHeaderDisplay() {
        if (headerItem.getVisibility() == View.VISIBLE) {
            headerItem.setVisibility(View.GONE);
            ivPaymentDetails.setImageResource(R.drawable.ic_arrow_down_black_24dp);
        } else {
            headerItem.setVisibility(View.VISIBLE);
            ivPaymentDetails.setImageResource(R.drawable.ic_arrow_up_black_24dp);
        }
    }
}
