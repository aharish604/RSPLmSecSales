package com.arteriatech.ss.msecsales.rspl.reports.returnorder.review;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.arteriatech.mutils.adapter.AdapterViewInterface;
import com.arteriatech.mutils.adapter.SimpleRecyclerViewTypeAdapter;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.interfaces.DialogCallBack;
import com.arteriatech.ss.msecsales.rspl.R;
import com.arteriatech.ss.msecsales.rspl.common.Constants;
import com.arteriatech.ss.msecsales.rspl.common.ConstantsUtils;
import com.arteriatech.ss.msecsales.rspl.customers.CustomerDetailsActivity;
import com.arteriatech.ss.msecsales.rspl.mbo.ROCreateBean;
import com.arteriatech.ss.msecsales.rspl.mbo.ReturnOrderBean;

import java.util.ArrayList;

public class ROReviewActivity extends AppCompatActivity implements ROReviewView, AdapterViewInterface<ReturnOrderBean> {

    private ArrayList<ReturnOrderBean> roCreateBeanList;
    private ROCreateBean roCreateBean;
    private RecyclerView recyclerView;
    private TextView noRecordFound;
    private SimpleRecyclerViewTypeAdapter<ReturnOrderBean> simpleRVAdapter;
    private ProgressDialog progressDialog = null;
    private ROReviewPresenterImpl presenter;
    private TextView tvRetailerName;
    private TextView tvRetailerID;
    private boolean isClickable = false;
    private String beatGUID="";
    private String parentId="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ro_review);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        ConstantsUtils.initActionBarView(this, mToolbar, true, getString(R.string.title_ro_create), 0);
        Intent intent = getIntent();
        if (intent != null) {
            roCreateBeanList = intent.getParcelableArrayListExtra(Constants.ItemList);
            roCreateBean = intent.getParcelableExtra(Constants.EXTRA_BEAN);
            beatGUID = intent.getExtras().getString(Constants.BeatGUID);
            parentId = intent.getExtras().getString(Constants.ParentId);
        }
        tvRetailerName = (TextView) findViewById(R.id.tv_RetailerName);
        tvRetailerID = (TextView) findViewById(R.id.tv_RetailerID);
        tvRetailerName.setText(roCreateBean.getCPName());
        tvRetailerID.setText(roCreateBean.getCPNo());

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        noRecordFound = (TextView) findViewById(R.id.no_record_found);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        simpleRVAdapter = new SimpleRecyclerViewTypeAdapter<ReturnOrderBean>(ROReviewActivity.this, R.layout.ro_review_item, this, recyclerView, noRecordFound);
        recyclerView.setAdapter(simpleRVAdapter);
        simpleRVAdapter.refreshAdapter(roCreateBeanList);

        presenter = new ROReviewPresenterImpl(ROReviewActivity.this, this, roCreateBeanList, roCreateBean,beatGUID,parentId);

    }

    @Override
    public void showProgressDialog(String message) {
        progressDialog = ConstantsUtils.showProgressDialog(ROReviewActivity.this, message);
    }

    @Override
    public void hideProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void displayMessage(String message) {
        ConstantsUtils.displayLongToast(ROReviewActivity.this, message);
    }

    @Override
    public void showMessage(String message, final boolean isSimpleDialog) {
        UtilConstants.dialogBoxWithCallBack(ROReviewActivity.this, "", message, getString(R.string.ok), "", false, new DialogCallBack() {
            @Override
            public void clickedStatus(boolean b) {
                if (!isSimpleDialog) {
                    navigateToVisit();
                }
            }
        });
    }
    private void navigateToVisit() {
        Intent intentNavPrevScreen = new Intent(ROReviewActivity.this, CustomerDetailsActivity.class);
        intentNavPrevScreen.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intentNavPrevScreen.putExtra(Constants.CPNo, roCreateBean.getCPNo());
        intentNavPrevScreen.putExtra(Constants.RetailerName, roCreateBean.getCPName());
        intentNavPrevScreen.putExtra(Constants.CPUID, roCreateBean.getCPUID());
        intentNavPrevScreen.putExtra(Constants.comingFrom, roCreateBean.getComingFrom());
        intentNavPrevScreen.putExtra(Constants.CPGUID, roCreateBean.getCPGUID());
        intentNavPrevScreen.putExtra(Constants.BeatGUID, beatGUID);
        intentNavPrevScreen.putExtra(Constants.ParentId, parentId);
        startActivity(intentNavPrevScreen);
    }
    @Override
    public void onItemClick(ReturnOrderBean returnOrderBean, View view, int i) {

    }

    @Override
    public int getItemViewType(int i, ArrayList<ReturnOrderBean> arrayList) {
        return 0;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i, View view) {
        return new ROReviewVH(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i, ReturnOrderBean returnOrderBean, ArrayList<ReturnOrderBean> arrayList) {
        ((ROReviewVH) viewHolder).tvBatch.setText(returnOrderBean.getReturnBatchNumber());
        ((ROReviewVH) viewHolder).tvMaterialDesc.setText(returnOrderBean.getMaterialDesc());
        ((ROReviewVH) viewHolder).tvMrp.setText(Constants.getCurrencySymbol(returnOrderBean.getCurrency(),returnOrderBean.getAltReturnMrp()));
        ((ROReviewVH) viewHolder).tvQty.setText(returnOrderBean.getReturnQty()+returnOrderBean.getReturnUOM());
        ((ROReviewVH) viewHolder).tvReason.setText(returnOrderBean.getReturnDesc());
        ((ROReviewVH) viewHolder).tvInvQty.setText(returnOrderBean.getActualQty() +" "+returnOrderBean.getUom());
        ((ROReviewVH) viewHolder).tvInvNo.setText(returnOrderBean.getInvoiceNo());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_ro_review, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.menu_save:
                if (!isClickable) {
                    isClickable = true;
                    UtilConstants.dialogBoxWithCallBack(ROReviewActivity.this, "", getString(R.string.so_save_RO_msg), getString(R.string.ok), getString(R.string.cancel), false, new DialogCallBack() {
                        @Override
                        public void clickedStatus(boolean b) {
                            isClickable=false;
                            if (b) {
                                presenter.onSaveData();
                            }
                        }
                    });
                }

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onDestroy() {
        presenter.onDestroy();
        super.onDestroy();
    }
}
