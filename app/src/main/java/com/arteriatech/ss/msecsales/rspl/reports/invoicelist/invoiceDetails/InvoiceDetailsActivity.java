package com.arteriatech.ss.msecsales.rspl.reports.invoicelist.invoiceDetails;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.core.widget.NestedScrollView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
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
import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.interfaces.DialogCallBack;
import com.arteriatech.ss.msecsales.rspl.R;
import com.arteriatech.ss.msecsales.rspl.common.Constants;
import com.arteriatech.ss.msecsales.rspl.common.ConstantsUtils;
import com.arteriatech.ss.msecsales.rspl.reports.invoicelist.InvoiceListBean;
import com.arteriatech.ss.msecsales.rspl.reports.invoicelist.SOItemDetailsVH1;
import com.arteriatech.ss.msecsales.rspl.so.SOUtils;

import java.util.ArrayList;

public class


InvoiceDetailsActivity extends AppCompatActivity implements InvoiceDetailsView, View.OnClickListener, AdapterInterface<InvoiceItemBean> {

    private InvoiceListBean invoiceListBean = null;
    private int comingFrom = 0;
    private InvoiceDetailsPresenterImpl presenter;
    private boolean isExpand = false;
    private TextView tvCustomerName, tvCustomerId;
    private SimpleRecyclerViewAdapter<InvoiceItemBean> invoiceItemAdapter;
    private TextView tvSoNumber;
    private TextView tvSODate;
    private TextView tvSOAmount;
    private TextView tvSOAmtHint;
    private AppCompatTextView tvNoRecordFoundPartnerFun;
    private AppCompatTextView tvNoRecordFoundItemDetails;
    private ProgressDialog progressDialog;
    private boolean isSessionRequired;
    private Toolbar toolbar;
    private TextView tvInvoiceNo, tvInvoiceCutName;
    private TextView tvOrderDetails, tvDate;
    private TextView tvOrderDateDesc, tvDmsDivisionDesc;
    private ImageView ivOrderDetails;
    private LinearLayout llHeaderSale;
    private InvoiceDetailsActivity mContext;
    private CardView cvOrderDetails, cvItem;
    private LinearLayout llItemList,llDMSDIV;
    private RecyclerView recycler_view_data;
    private ImageView ivDeliveryStatus;
    private NestedScrollView nestedScroll;
    private LinearLayout llRefNo;
    private TextView tvRefNoDesc;
    String ComingFrom;
    ArrayList<InvoiceItemBean> invoiceItemBeanlist;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice_history_details);
        Intent intent = getIntent();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (intent != null) {
            comingFrom = intent.getIntExtra(Constants.EXTRA_COME_FROM, 0);
            ComingFrom = intent.getStringExtra(Constants.comingFrom);
            invoiceListBean = (InvoiceListBean) intent.getSerializableExtra(Constants.INVOICE_ITEM);
            invoiceItemBeanlist= invoiceListBean.getInvoiceItemBeanArrayList();
            isSessionRequired = intent.getBooleanExtra(Constants.EXTRA_SESSION_REQUIRED, false);
            if (ComingFrom.equalsIgnoreCase(Constants.SampleDisbursement)){
                ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.title_sample_disbursement), 0);
            }else{
                ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.invc_details_title), 0);
            }
        }
        if (invoiceListBean == null) {
            invoiceListBean = new InvoiceListBean();
        }
        mContext = InvoiceDetailsActivity.this;
        presenter = new InvoiceDetailsPresenterImpl(InvoiceDetailsActivity.this, this, comingFrom, invoiceListBean, isSessionRequired);
        if (!Constants.restartApp(InvoiceDetailsActivity.this)) {
            initUI();
        }
    }
    private void initUI() {
        /*init header */
        nestedScroll = (NestedScrollView) findViewById(R.id.nestedScroll);
        tvCustomerName = (TextView) findViewById(R.id.tv_header_title);
        tvCustomerId = (TextView) findViewById(R.id.tv_header_id);
        tvInvoiceCutName = (TextView) findViewById(R.id.tvCustomerName);
        tvInvoiceNo = (TextView) findViewById(R.id.tvInvoiceNo);

        tvSoNumber = (TextView) findViewById(R.id.tv_d_so_number);
        tvSODate = (TextView) findViewById(R.id.tv_d_so_date);
        tvSOAmount = (TextView) findViewById(R.id.tv_d_so_amount);
        tvSOAmtHint = (TextView) findViewById(R.id.tv_d_so_dsc_hint);
        tvSOAmount.setVisibility(View.VISIBLE);
        tvSODate.setVisibility(View.VISIBLE);

        tvDate = (TextView) findViewById(R.id.tvDate);
        tvOrderDetails = (TextView) findViewById(R.id.tvOrderDetails);

        llRefNo = (LinearLayout) findViewById(R.id.llRefNo);
        tvRefNoDesc = (TextView) findViewById(R.id.tvRefNoDesc);
        tvOrderDateDesc = (TextView) findViewById(R.id.tvExpTypeDesc);
        tvDmsDivisionDesc = (TextView) findViewById(R.id.tvDmsDivisionDesc);
        ivOrderDetails = (ImageView) findViewById(R.id.ivOrderDetails);
        ivOrderDetails.setOnClickListener(this);
        llHeaderSale = (LinearLayout) findViewById(R.id.headerItem);
        cvOrderDetails = (CardView) findViewById(R.id.cvOrderDetails);
        cvItem = (CardView) findViewById(R.id.cvItem);
        llItemList = (LinearLayout) findViewById(R.id.llItemList);
        llDMSDIV = (LinearLayout) findViewById(R.id.llDMSDIV);
        recycler_view_data = (RecyclerView) findViewById(R.id.recycler_view_data);
        /*item start*/
        View soItemTitelView = findViewById(R.id.soItemTitelView);
        TextView tvItemTitle = (TextView) soItemTitelView.findViewById(R.id.tv_heading);
        tvItemTitle.setText(getString(R.string.item_details_title));
//        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        View noRecordItemDetails = findViewById(R.id.noRecordItemDetails);
        tvNoRecordFoundItemDetails = ((AppCompatTextView) noRecordItemDetails);
        View noRecordPartnerFun = findViewById(R.id.noRecordPartnerFun);
        tvNoRecordFoundPartnerFun = ((AppCompatTextView) noRecordPartnerFun);
        recycler_view_data.setHasFixedSize(true);
        ViewCompat.setNestedScrollingEnabled(recycler_view_data, false);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recycler_view_data.setLayoutManager(linearLayoutManager);
        if (ComingFrom.equalsIgnoreCase(Constants.SampleDisbursement))
        {
            invoiceItemAdapter = new SimpleRecyclerViewAdapter<InvoiceItemBean>(InvoiceDetailsActivity.this, R.layout.sample_disbursement_detail_item_material, this, recycler_view_data, tvNoRecordFoundItemDetails);

        }
        else
        {
            invoiceItemAdapter = new SimpleRecyclerViewAdapter<InvoiceItemBean>(InvoiceDetailsActivity.this, R.layout.so_item_material, this, recycler_view_data, tvNoRecordFoundItemDetails);

        }
        recycler_view_data.setAdapter(invoiceItemAdapter);
        ivDeliveryStatus = (ImageView) findViewById(R.id.ivDeliveryStatus);

        setViewValue();
    }
    private void setViewValue() {

        tvInvoiceCutName.setText(invoiceListBean.getInvoiceTypDesc() + " (" + invoiceListBean.getInvoiceType() + ")");
        tvInvoiceNo.setText(invoiceListBean.getInvoiceNo());
//      tvAmount.setText(UtilConstants.commaSeparator(invoiceListBean.getNetAmount()) + " " + invoiceListBean.getCurrency());
        tvOrderDateDesc.setText(invoiceListBean.getInvoiceDate());
        tvDmsDivisionDesc.setText(invoiceListBean.getDmsDivisionDesc() + " (" + invoiceListBean.getDmsDivision() + ")");
        tvDate.setText(invoiceListBean.getInvoiceDate());

        if (!TextUtils.isEmpty(invoiceListBean.getPONo())){
            tvRefNoDesc.setText(invoiceListBean.getPONo());
            llRefNo.setVisibility(View.VISIBLE);
        }else {
            llRefNo.setVisibility(View.GONE);
        }
        if(ComingFrom.equalsIgnoreCase(Constants.SampleDisbursement))
        {ivDeliveryStatus.setVisibility(View.GONE);}
        else {
            Drawable img = SOUtils.displayInvoiceStatusImage(invoiceListBean.getInvoiceStatus(), invoiceListBean.getDueDateStatus(), this);
            ivDeliveryStatus.setImageDrawable(img);
        }
        invoiceItemAdapter.refreshAdapter(invoiceItemBeanlist);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        return true;
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
    public void displayHeaderList(InvoiceListBean invoiceListBean) {
        tvSoNumber.setText(invoiceListBean.getInvoiceNo());
        tvSODate.setText(invoiceListBean.getInvoiceDate());
        tvSOAmount.setText(UtilConstants.getCurrencyFormat(invoiceListBean.getCurrency(),invoiceListBean.getNetAmount()));

        invoiceItemAdapter.refreshAdapter(invoiceListBean.getInvoiceItemBeanArrayList());
        tvSOAmtHint.setVisibility(View.VISIBLE);
        tvSOAmtHint.setText(getString(R.string.inv_amt) + " " + getString(R.string.colun));
    }


    @Override
    public void showProgressDialog(String s) {
        progressDialog = ConstantsUtils.showProgressDialog(InvoiceDetailsActivity.this, s);
    }

    @Override
    public void hideProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void showMessage(String message, final boolean isSimpleDialog) {
        UtilConstants.dialogBoxWithCallBack(InvoiceDetailsActivity.this, "", message, getString(R.string.ok), "", false, new DialogCallBack() {
            @Override
            public void clickedStatus(boolean b) {
                if (!isSimpleDialog) {
                    redirectActivity();
                }
            }
        });
    }

    private void redirectActivity() {
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivOrderDetails:
                if (llHeaderSale.getVisibility() == View.VISIBLE) {
                    ivOrderDetails.setImageResource(R.drawable.ic_arrow_down_black_24dp);
                    llHeaderSale.setVisibility(View.GONE);
                    tvDate.setVisibility(View.VISIBLE);
                    ViewGroup.MarginLayoutParams layoutParams = ConstantsUtils.getLayoutParams(cvOrderDetails);
                    int marginB = ConstantsUtils.dpToPx(8, mContext);
                    layoutParams.setMargins(ConstantsUtils.dpToPx(8, mContext), ConstantsUtils.dpToPx(8, mContext), ConstantsUtils.dpToPx(8, mContext), marginB);
                    cvOrderDetails.requestLayout();
                } else {
                    ivOrderDetails.setImageResource(R.drawable.ic_arrow_up_black_24dp);
                    llHeaderSale.setVisibility(View.VISIBLE);
                    tvDate.setVisibility(View.GONE);
                    int marginB = ConstantsUtils.dpToPx(8, mContext);
                    ViewGroup.MarginLayoutParams layoutParams = ConstantsUtils.getLayoutParams(cvOrderDetails);
                    layoutParams.setMargins(ConstantsUtils.dpToPx(8, mContext), ConstantsUtils.dpToPx(8, mContext), ConstantsUtils.dpToPx(8, mContext), marginB);
                    cvOrderDetails.requestLayout();
                }
                break;
        }
    }


    @Override
    public void onItemClick(InvoiceItemBean item, View view, int position) {

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType, View viewItem) {
        return new SOItemDetailsVH1(viewItem, InvoiceDetailsActivity.this);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position, InvoiceItemBean invoiceItemBean) {
        Drawable img = SOUtils.displayInvoiceStatusImage(invoiceListBean.getInvoiceStatus(), invoiceItemBean.getItemInvoiceStatus(), this);
        ((SOItemDetailsVH1) holder).ivDelvStatus.setImageDrawable(img);
        ((SOItemDetailsVH1) holder).tvMaterialDesc.setText(invoiceItemBean.getInvoiceMaterialDescAndNo());
//        ((SOItemDetailsVH1) holder).tvQty.setText(invoiceItemBean.getActualInvQty() + " " + invoiceItemBean.getUOM());
        try {
//            ((SOItemDetailsVH1) holder).tvQty.setText(ConstantsUtils.checkNoUOMZero(invoiceItemBean.getUOM(), invoiceItemBean.getActualInvQty()) + " " + invoiceItemBean.getUOM());
            ((SOItemDetailsVH1) holder).tvQty.setText(UtilConstants.removeLeadingZero(invoiceItemBean.getActualInvQty()) + " " + invoiceItemBean.getUOM());
        } catch (Throwable e) {
            ((SOItemDetailsVH1) holder).tvQty.setText(invoiceItemBean.getActualInvQty() + " " + invoiceItemBean.getUOM());
            e.printStackTrace();
        }
        if(ComingFrom.equalsIgnoreCase(Constants.SampleDisbursement))
        {
            ((SOItemDetailsVH1) holder).tvAmount.setVisibility(View.GONE);
        }
        else {
            ((SOItemDetailsVH1) holder).tvAmount.setVisibility(View.VISIBLE);
            ((SOItemDetailsVH1) holder).tvAmount.setText(UtilConstants.getCurrencyFormat(invoiceItemBean.getCurrency(), invoiceItemBean.getNetAmount()));
        }
        if(!TextUtils.isEmpty(invoiceItemBean.getHigherLevelItemNo()) && !invoiceItemBean.getHigherLevelItemNo().equalsIgnoreCase("000000")) {
            ((SOItemDetailsVH1) holder).tvAmount.setText(UtilConstants.getCurrencyFormat(invoiceItemBean.getCurrency(), "0.00"));
            if (img != null) {
                img.setColorFilter(ContextCompat.getColor(this, R.color.colorGray), PorterDuff.Mode.SRC_IN);
                try {
                    ((SOItemDetailsVH1) holder).ivDelvStatus.setImageDrawable(img);
                } catch (Throwable e1) {
                    e1.printStackTrace();
                }
            }
        }
    }
}



