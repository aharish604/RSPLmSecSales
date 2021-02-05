package com.arteriatech.ss.msecsales.rspl.expenselist.expenselistdetails;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.arteriatech.mutils.adapter.AdapterInterface;
import com.arteriatech.mutils.adapter.SimpleRecyclerViewAdapter;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.ss.msecsales.rspl.R;
import com.arteriatech.ss.msecsales.rspl.common.Constants;
import com.arteriatech.ss.msecsales.rspl.common.ConstantsUtils;
import com.arteriatech.ss.msecsales.rspl.expenselist.ExpenseListBean;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;

import id.zelory.compressor.Compressor;

public class ExpenseListDetailsActivity extends AppCompatActivity implements ExpenseListdetailsView, SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {
    Toolbar toolbar;
    String expenseNo = "";
    String expenseDesc = "";
    String expenseType = "";
    String cpTypeDesc = "";
    String expenseDate = "";
    String ExpenseGUID36 = "";
    TextView tv_expense_no;
    TextView tv_expense_Desc;
    TextView tvDate;
    TextView tvexpenseType;
    TextView tvExpTypeDesc;
    TextView tv_Expense_date;
    ImageView ivOrderDetails;
    View headerItem;
    RecyclerView recyclerView;
    RecyclerView recycler_view_images;
    LinearLayoutManager linearLayoutManager;
    GridLayoutManager gridLayoutManager;
    SwipeRefreshLayout swiperefreshlayout;
    TextView no_record_found;
    SimpleRecyclerViewAdapter<ExpenseDetailsBean> recyclerViewAdapter = null;
    SimpleRecyclerViewAdapter<DocumentBean> recyclerViewAdapterImage = null;
    ExpenseListDetailsPresenterImpl expenseListDetailsPresenter;
    Context context = this;
    ExpenseListBean expenseListBean;
    ArrayList<ExpenseDetailsBean> list;
    private CardView cvItem2;
    private boolean isReadFromDataValt = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_list_details);
        getExtraDate();
        initializeUI();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.title_expense_list_details), 0);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        expenseListDetailsPresenter = new ExpenseListDetailsPresenterImpl(this, ExpenseGUID36);
        if (!isReadFromDataValt) {
            expenseListDetailsPresenter.onStart();
        } else {
            expenseListDetailsPresenter.dataValt();
        }
    }

    private void getExtraDate() {
        expenseListBean = getIntent().getParcelableExtra(Constants.ExpenseListBean);
        ExpenseGUID36 = expenseListBean.getExpenseGUID36();
        expenseNo = expenseListBean.getExpenseNo();
        expenseDesc = expenseListBean.getExpenseTypeDesc();
        expenseType = expenseListBean.getExpenseType();
        expenseDate = expenseListBean.getDate();
        cpTypeDesc = expenseListBean.getCPTypeDesc();
        isReadFromDataValt = getIntent().getBooleanExtra(Constants.EXTRA_IS_OFFLINE, false);

    }

    @SuppressLint("SetTextI18n")
    private void initializeUI() {
        tv_expense_no = (TextView) findViewById(R.id.tv_expense_no);
        tv_expense_Desc = (TextView) findViewById(R.id.tv_expense_Desc);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recycler_view_images = (RecyclerView) findViewById(R.id.recycler_view_images);
        no_record_found = (TextView) findViewById(R.id.no_record_found);
        tvDate = (TextView) findViewById(R.id.tvDate);
        tvexpenseType = (TextView) findViewById(R.id.tvexpenseType);
        tvExpTypeDesc = (TextView) findViewById(R.id.tvExpTypeDesc);
        tv_Expense_date = (TextView) findViewById(R.id.tv_Expense_date);
        ivOrderDetails = (ImageView) findViewById(R.id.ivOrderDetails);
        cvItem2 = (CardView) findViewById(R.id.cvItem2);
        headerItem = findViewById(R.id.headerItem);
        swiperefreshlayout = (SwipeRefreshLayout) findViewById(R.id.swiperefreshlayout);
        linearLayoutManager = new LinearLayoutManager(context);
        gridLayoutManager = new GridLayoutManager(context, 2);
        recyclerView.setLayoutManager(linearLayoutManager);
        recycler_view_images.setLayoutManager(gridLayoutManager);

        recyclerViewAdapter = new SimpleRecyclerViewAdapter<>(context, R.layout.expense_list_list_item_details, new AdapterInterface<ExpenseDetailsBean>() {
            @Override
            public void onItemClick(ExpenseDetailsBean o, View view, int i) {

            }

            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i, View view) {
                return new ExpenseListDetailseVH(view);

            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i, ExpenseDetailsBean expenseDetailsBean) {
                ((ExpenseListDetailseVH) viewHolder).location.setText(expenseDetailsBean.getLocationDetailsDesc());
                if (!TextUtils.isEmpty(expenseDetailsBean.getBeatDistance())) {
                    ((ExpenseListDetailseVH) viewHolder).beatDistance.setText(UtilConstants.removeLeadingZeroVal(expenseDetailsBean.getBeatDistance()) + "\n" + expenseDetailsBean.getUOM());
                } else {
                    ((ExpenseListDetailseVH) viewHolder).beatDistance.setVisibility(View.INVISIBLE);
                }
                ((ExpenseListDetailseVH) viewHolder).convey.setText(expenseDetailsBean.getConveniencemodedesc());
                ((ExpenseListDetailseVH) viewHolder).expenseItemType.setText(expenseDetailsBean.getExpenseItemTypeDesc());
                ((ExpenseListDetailseVH) viewHolder).expenseAmount.setText(UtilConstants.getCurrencyFormat(expenseDetailsBean.getCurrency(), expenseDetailsBean.getAmmount()));

            }
        }, recyclerView, no_record_found);

        recyclerView.setAdapter(recyclerViewAdapter);
        swiperefreshlayout.setOnRefreshListener(this);
        ivOrderDetails.setOnClickListener(this);
        tv_expense_no.setText(expenseNo);
        tv_expense_Desc.setText(expenseDesc + "(" + expenseType + ")");
        tvDate.setText(expenseDate);


        recyclerViewAdapterImage = new SimpleRecyclerViewAdapter<>(context, R.layout.expense_list_details_image, new AdapterInterface<DocumentBean>() {
            @Override
            public void onItemClick(DocumentBean o, View view, int i) {

            }

            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i, View view) {
                return new ExpenseListDetailseImageVH(view);

            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i, DocumentBean expenseDetailsBean) {
                if (!TextUtils.isEmpty(expenseDetailsBean.getFileName())) {
                    ((ExpenseListDetailseImageVH) viewHolder).tvName.setText(expenseDetailsBean.getDisplayName());
                } else {
                    ((ExpenseListDetailseImageVH) viewHolder).tvName.setText(getString(R.string.visual_aidl_no_file));
                }
                if (!TextUtils.isEmpty(expenseDetailsBean.getImagePath())) {
//            BitmapFactory.Options options = new BitmapFactory.Options();
//            options.inSampleSize = 2;
                    try {
                        final Bitmap bitmap = Compressor.getDefault(getApplicationContext()).compressToBitmap(new File(expenseDetailsBean.getImagePath()));
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                        final byte[] imageInByte = stream.toByteArray();
                        Bitmap bitmap2 = BitmapFactory.decodeByteArray(imageInByte, 0, imageInByte.length);
                        ((ExpenseListDetailseImageVH) viewHolder).ivThumbNail.setImageBitmap(bitmap2);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }, recycler_view_images, no_record_found);
        recycler_view_images.setAdapter(recyclerViewAdapterImage);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void displayDetailsListr(ArrayList<ExpenseDetailsBean> list) {
        if (list != null) {
            recyclerViewAdapter.refreshAdapter(list);
            if (!list.isEmpty()) {
                ArrayList<DocumentBean> docList = list.get(0).getDocumentBeanslist();
                if (docList != null) {
                    if (!docList.isEmpty()) {
                        recyclerViewAdapterImage.refreshAdapter(docList);
                        cvItem2.setVisibility(View.VISIBLE);
                    } else {
                        cvItem2.setVisibility(View.GONE);
                    }

                }
            }
        }
        cvItem2.setVisibility(View.GONE);
    }

    private void getImages() {
    }

    public void loadProgressBar() {
        swiperefreshlayout.setRefreshing(true);
    }

    public void hideProgressBar() {
        swiperefreshlayout.setRefreshing(false);
    }

    @Override
    public void showMessage(String msg) {

        ConstantsUtils.displayLongToast(getApplicationContext(), msg);
    }

    @Override
    public void onRefresh() {

    }


    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.ivOrderDetails:
                showHeaderDetails();
                break;
        }
    }

    private void showHeaderDetails() {
        headerItem.setVisibility(headerItem.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
        tvDate.setVisibility(headerItem.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
        tv_Expense_date.setText(expenseDate);
        //tvexpenseType.setText(expenseType);
        tvExpTypeDesc.setText(cpTypeDesc);

    }
}
