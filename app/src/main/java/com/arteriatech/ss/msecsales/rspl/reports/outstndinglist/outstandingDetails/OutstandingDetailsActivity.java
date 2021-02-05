package com.arteriatech.ss.msecsales.rspl.reports.outstndinglist.outstandingDetails;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.core.widget.NestedScrollView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
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
import com.arteriatech.ss.msecsales.rspl.mbo.OutstandingBean;
import com.arteriatech.ss.msecsales.rspl.reports.outstndinglist.OutStndingVH;
import com.arteriatech.ss.msecsales.rspl.so.SOUtils;

public class OutstandingDetailsActivity extends AppCompatActivity implements OutstndingDetailsView, View.OnClickListener, AdapterInterface<OutstandingBean> {

    private OutstandingBean invoiceListBean = null;
    private int comingFrom = 0;
    private OutstandingDetailsPresenterImpl presenter;
    private boolean isExpand = false;
    private TextView tvCustomerName, tvCustomerId;
    private SimpleRecyclerViewAdapter<OutstandingBean> invoiceItemAdapter;
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
    private OutstandingDetailsActivity mContext;
    private CardView cvOrderDetails, cvItem;
    private LinearLayout llItemList,llDMSDIV;
    private RecyclerView recycler_view_data;
    private ImageView ivDeliveryStatus;
    private NestedScrollView nestedScroll;
    private ConstraintLayout clSalesArea;
    TextView tv_RetailerName,tv_RetailerID;
    private String mStrBundleRetID = "";
    private String mStrBundleRetName = "";
    private TextView tvAmount;
    TextView tv_balance_amount, tv_coll_amount, tv_out_amount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_out_details);
        Intent intent = getIntent();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (intent != null) {
            mStrBundleRetID = intent.getExtras().getString(Constants.CPNo);
            mStrBundleRetName = intent.getExtras().getString(Constants.RetailerName);
            comingFrom = intent.getIntExtra(Constants.EXTRA_COME_FROM, 0);
            invoiceListBean = (OutstandingBean) intent.getSerializableExtra(Constants.INVOICE_ITEM);
            isSessionRequired = intent.getBooleanExtra(Constants.EXTRA_SESSION_REQUIRED, false);
                ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.out_details_title), 0);
        }

        if (invoiceListBean == null) {
            invoiceListBean = new OutstandingBean();
        }
        mContext = OutstandingDetailsActivity.this;
        presenter = new OutstandingDetailsPresenterImpl(OutstandingDetailsActivity.this, this, comingFrom, invoiceListBean, isSessionRequired);
        initUI();
    }

    private void initUI() {
        /*init header */
        tv_RetailerName = (TextView) findViewById(R.id.tv_RetailerName);
        tv_RetailerID = (TextView) findViewById(R.id.tv_RetailerID);
        tv_balance_amount = (TextView) findViewById(R.id.tv_balance_amount);
        tv_coll_amount = (TextView) findViewById(R.id.tv_coll_amount);
        tv_out_amount = (TextView) findViewById(R.id.tv_out_amount);

        tvAmount = (TextView) findViewById(R.id.textViewExpenseTypeDesc);
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

        tvOrderDateDesc = (TextView) findViewById(R.id.tvExpTypeDesc);
        tvDmsDivisionDesc = (TextView) findViewById(R.id.tvDmsDivisionDesc);
        ivOrderDetails = (ImageView) findViewById(R.id.ivOrderDetails);
        ivOrderDetails.setOnClickListener(this);
        llHeaderSale = (LinearLayout) findViewById(R.id.headerItem);
        cvOrderDetails = (CardView) findViewById(R.id.cvOrderDetails);
        cvItem = (CardView) findViewById(R.id.cvItem);
        llItemList = (LinearLayout) findViewById(R.id.llItemList);
        llDMSDIV = (LinearLayout) findViewById(R.id.llDMSDIV);
        clSalesArea = (ConstraintLayout) findViewById(R.id.clSalesArea);
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
        invoiceItemAdapter = new SimpleRecyclerViewAdapter<OutstandingBean>(OutstandingDetailsActivity.this, R.layout.so_item_material, this, recycler_view_data, tvNoRecordFoundItemDetails);
        recycler_view_data.setAdapter(invoiceItemAdapter);
        ivDeliveryStatus = (ImageView) findViewById(R.id.ivDeliveryStatus);
      /*  tv_RetailerID.setText(mStrBundleRetID);
        tv_RetailerName.setText(mStrBundleRetName);*/
        setOutAmountToUI();
        setViewValue();
    }
    private void setOutAmountToUI() {
        tv_out_amount.setText(Constants.getCurrencySymbol(invoiceListBean.getCurrency(), invoiceListBean.getInvoiceAmount() + ""));
        tv_coll_amount.setText(Constants.getCurrencySymbol(invoiceListBean.getCurrency(), invoiceListBean.getCollectionAmount() + ""));
        tv_balance_amount.setText(Constants.getCurrencySymbol(invoiceListBean.getCurrency(), invoiceListBean.getInvoiceBalanceAmount() + ""));
    }

    private void setViewValue() {
        tvAmount.setText(UtilConstants.getCurrencyFormat(invoiceListBean.getCurrency(),invoiceListBean.getInvoiceAmount()));
        clSalesArea.setVisibility(View.GONE);
        tvInvoiceCutName.setText(invoiceListBean.getInvoiceTypeDesc() + " (" + invoiceListBean.getInvoiceTypeID() + ")");
        tvInvoiceNo.setText(invoiceListBean.getInvoiceNo());
//        tvAmount.setText(UtilConstants.commaSeparator(invoiceListBean.getNetAmount()) + " " + invoiceListBean.getCurrency());
        tvOrderDateDesc.setText(invoiceListBean.getInvoiceDate());
//        tvDmsDivisionDesc.setText(invoiceListBean.getDmsDivisionDesc() + " (" + invoiceListBean.getDmsDivision() + ")");
        tvDate.setText(invoiceListBean.getInvoiceDate());

        Drawable img = SOUtils.displayInvoiceStatusImage(invoiceListBean.getInvoiceStatus(), invoiceListBean.getDueDateStatus(), this);
        ivDeliveryStatus.setImageDrawable(img);

        invoiceItemAdapter.refreshAdapter(invoiceListBean.getAlItemList());

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
    public void displayHeaderList(OutstandingBean invoiceListBean) {
        tvSoNumber.setText(invoiceListBean.getInvoiceNo());
        tvSODate.setText(invoiceListBean.getInvoiceDate());
//        tvSOAmount.setText(UtilConstants.getCurrencyFormat(invoiceListBean.getCurrency(),invoiceListBean.getNetAmount()));

//        invoiceItemAdapter.refreshAdapter(invoiceListBean.getInvoiceItemBeanArrayList());
        tvSOAmtHint.setVisibility(View.VISIBLE);
        tvSOAmtHint.setText(getString(R.string.inv_amt) + " " + getString(R.string.colun));
    }


    @Override
    public void showProgressDialog(String s) {
        progressDialog = ConstantsUtils.showProgressDialog(OutstandingDetailsActivity.this, s);
    }

    @Override
    public void hideProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void showMessage(String message, final boolean isSimpleDialog) {
        UtilConstants.dialogBoxWithCallBack(OutstandingDetailsActivity.this, "", message, getString(R.string.ok), "", false, new DialogCallBack() {
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
    public void onItemClick(OutstandingBean item, View view, int position) {

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType, View viewItem) {
        return new OutStndingVH(viewItem, OutstandingDetailsActivity.this);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position, OutstandingBean invoiceItemBean) {
        Drawable img = SOUtils.displayInvoiceStatusImage(invoiceListBean.getInvoiceStatus(), invoiceItemBean.getItemInvoiceStatus(), this);

        ((OutStndingVH) holder).tvMaterialDesc.setText(invoiceItemBean.getMatDesc());
//        ((OutStndingVH) holder).tvQty.setText(invoiceItemBean.getInvQty() + " " + invoiceItemBean.getUom());


        try {
            ((OutStndingVH) holder).tvQty.setText(UtilConstants.removeLeadingZero(invoiceItemBean.getInvQty())  + " " + invoiceItemBean.getUom());
        } catch (Exception e) {
            e.printStackTrace();
            ((OutStndingVH) holder).tvQty.setText(invoiceItemBean.getInvQty() + " " + invoiceItemBean.getUom());

        }
        /*try {
            ((OutStndingVH) holder).tvQty.setText(ConstantsUtils.checkNoUOMZero(invoiceItemBean.getUom(),invoiceItemBean.getInvQty())  + " " + invoiceItemBean.getUom());
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
            ((OutStndingVH) holder).tvQty.setText(invoiceItemBean.getInvQty() + " " + invoiceItemBean.getUom());
        }*/

         ((OutStndingVH) holder).tvAmount.setText(UtilConstants.getCurrencyFormat(invoiceItemBean.getCurrency(),invoiceItemBean.getInvoiceAmount()));

        if(!invoiceItemBean.getHigherLevelItm().equalsIgnoreCase("000000")){
            ((OutStndingVH) holder).tvAmount.setText(UtilConstants.getCurrencyFormat(invoiceItemBean.getCurrency(),"0.00"));
            if(img!=null){
                img.setColorFilter(ContextCompat.getColor(mContext, R.color.colorGray), PorterDuff.Mode.SRC_IN);
            }
        }

        ((OutStndingVH) holder).ivDelvStatus.setImageDrawable(img);

    }
}



