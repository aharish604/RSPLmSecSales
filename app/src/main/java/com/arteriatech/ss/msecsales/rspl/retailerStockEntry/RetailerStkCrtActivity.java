package com.arteriatech.ss.msecsales.rspl.retailerStockEntry;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.arteriatech.mutils.adapter.AdapterInterface;
import com.arteriatech.mutils.adapter.SimpleRecyclerViewAdapter;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.filterlist.SearchFilterInterface;
import com.arteriatech.mutils.interfaces.DialogCallBack;
import com.arteriatech.ss.msecsales.rspl.R;
import com.arteriatech.ss.msecsales.rspl.common.Constants;
import com.arteriatech.ss.msecsales.rspl.common.ConstantsUtils;
import com.arteriatech.ss.msecsales.rspl.mbo.RetailerStockBean;

import java.util.ArrayList;

public class RetailerStkCrtActivity extends AppCompatActivity implements RetailerStockCrtView, AdapterInterface<RetailerStockBean>, SearchFilterInterface, RetailerStkCrtQtyTxtWtchrInterface {
    public static final int INTENT_RESULT_STOCK_CREATE = 111;
    LinearLayout linearLayoutFlowLayout;
    private RetailerStockCrtPresenterImpl presenter;
    private ProgressDialog progressDialog;
    private RecyclerView recyclerView;
    private TextView noRecordFound;
    private SimpleRecyclerViewAdapter<RetailerStockBean> simpleRecyclerViewAdapter;
    private ArrayList<RetailerStockBean> retailerStckList = new ArrayList<>();
    private boolean isSessionRequired = false;
    private Toolbar toolbar;
    private SearchView mSearchView = null;
    private int maxLength=0;
    private boolean isKeyBoardOpen = false;
    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
        @Override
        public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
            // super.onSelectedChanged(viewHolder, actionState);
            if (viewHolder != null) {
                final View foregroundView = ((StockMultiMaterialVH) viewHolder).viewForeground;
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
            final View foregroundView = (((StockMultiMaterialVH) viewHolder).viewForeground);
            getDefaultUIUtil().onDrawOver(c, recyclerView, foregroundView, dX, dY, actionState, isCurrentlyActive);
//            }
        }

        @Override
        public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            try {
                final View foregroundView = ((StockMultiMaterialVH) viewHolder).viewForeground;
                getDefaultUIUtil().clearView(foregroundView);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            int position = viewHolder.getAdapterPosition();
//            DealerStockBean retailerStkBean = retailerStckList.get(position);
//            if (retailerStkBean.isChecked()) {
            View foregroundView = ((StockMultiMaterialVH) viewHolder).viewForeground;
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
            if (viewHolder instanceof StockMultiMaterialVH) {
                final int position = viewHolder.getAdapterPosition(); //swiped position
                if (!retailerStckList.isEmpty()) {
                    RetailerStockBean retailerStkBean = retailerStckList.get(position);
                    if (direction == ItemTouchHelper.RIGHT) {
                        ((StockMultiMaterialVH) viewHolder).ivRight.setVisibility(View.GONE);
                        ((StockMultiMaterialVH) viewHolder).ivLeft.setVisibility(View.VISIBLE);
                        resetMatView(retailerStkBean, viewHolder);
                    } else if (direction == ItemTouchHelper.LEFT) {
                        ((StockMultiMaterialVH) viewHolder).ivRight.setVisibility(View.VISIBLE);
                        ((StockMultiMaterialVH) viewHolder).ivLeft.setVisibility(View.GONE);
                        resetMatView(retailerStkBean, viewHolder);
                    }
                }
            }
        }
    };
    private TextView tvRetailerName, tvRetailerID;
    private String searchHint = "";
    private String mstrCustomerNo = "", mStrCPGUID32 = "", mStrComingFrom = "", mStrUID = "", mStrCustomerName = "";
    private String mStrCPGUID = "";
    private String beatGUID = "";
    private String parentId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retailer_stock);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mstrCustomerNo = bundle.getString(Constants.CPNo);
            mStrCustomerName = bundle.getString(Constants.RetailerName);
            mStrUID = bundle.getString(Constants.CPUID);
            mStrCPGUID = bundle.getString(Constants.CPGUID);
            mStrComingFrom = bundle.getString(Constants.comingFrom);
            mStrCPGUID32 = bundle.getString(Constants.CPGUID32);
            beatGUID = bundle.getString(Constants.BeatGUID);
            parentId = bundle.getString(Constants.ParentId);
        }
        ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.retailer_stock_entry_title), 0);
        if (getSupportActionBar() != null)
            getSupportActionBar().setSubtitle(getString(R.string.retailer_stock_entrys));
        tvRetailerName = (TextView) findViewById(R.id.tv_RetailerName);
        tvRetailerID = (TextView) findViewById(R.id.tv_RetailerID);
        // set retailer name
        tvRetailerName.setText(mStrCustomerName);
        // set retailer id
        tvRetailerID.setText(mstrCustomerNo);
        linearLayoutFlowLayout = (LinearLayout) findViewById(R.id.llFilterLayout);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        noRecordFound = (TextView) findViewById(R.id.no_record_found);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
        simpleRecyclerViewAdapter = new SimpleRecyclerViewAdapter<RetailerStockBean>(RetailerStkCrtActivity.this, R.layout.retailer_stock_item, this, recyclerView, noRecordFound);
        recyclerView.setAdapter(simpleRecyclerViewAdapter);
        final View headerView = findViewById(R.id.llListView);
        try {
            maxLength=Constants.quantityLength();
        } catch (Exception e) {
            e.printStackTrace();
        }
        headerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int heightDiff = headerView.getRootView().getHeight() - headerView.getHeight();
                if (heightDiff > ConstantsUtils.dpToPx(200, RetailerStkCrtActivity.this)) {
                    isKeyBoardOpen = true;
                } else {
                    isKeyBoardOpen = false;
                }
            }
        });
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    hideKeyboard();
                }
            }
        });
        presenter = new RetailerStockCrtPresenterImpl(RetailerStkCrtActivity.this, this, mStrComingFrom, isSessionRequired, mstrCustomerNo, false, mStrCustomerName, mStrCPGUID32, mStrUID, mStrCPGUID,beatGUID,parentId);
        presenter.onStart();
    }

    private void hideKeyboard() {
        if (isKeyBoardOpen) {
            UtilConstants.hideKeyboardFrom(RetailerStkCrtActivity.this);
        }
    }

    @Override
    public void displayList(ArrayList<RetailerStockBean> retailerItemList, String searchHint) {
        refreshAdapter(retailerItemList);
        this.searchHint = searchHint;
        if (mSearchView != null)
            mSearchView.setQueryHint(searchHint);
    }

    @Override
    public void displaySearchList(ArrayList<RetailerStockBean> retailerItemList) {
        refreshAdapter(retailerItemList);
    }

    private void refreshAdapter(ArrayList<RetailerStockBean> retailerItemList) {
        retailerStckList = retailerItemList;
        simpleRecyclerViewAdapter.refreshAdapter(retailerStckList);
    }

    @Override
    public void showProgressDialog(String message) {
        progressDialog = ConstantsUtils.showProgressDialog(RetailerStkCrtActivity.this, message);
    }

    @Override
    public void hideProgressDialog() {
        progressDialog.dismiss();
    }

    @Override
    public void displayMessage(String message) {
        ConstantsUtils.displayShortToast(RetailerStkCrtActivity.this, message);
    }

    @Override
    public void displayTotalSelectedMat(int finalSelectedCount) {
    }


    @Override
    protected void onDestroy() {
        presenter.onDestroy();
        super.onDestroy();
    }

    @Override
    public void openFilter(String startDate, String endDate, String filterType, String status, String delvStatus) {
    }


    @Override
    public void onItemClick(RetailerStockBean item, View view, int position) {


    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType, View viewItem) {
        return new StockMultiMaterialVH(viewItem, new RetailerStockCrtQtyTxtWtchr());
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position, final RetailerStockBean retailerStkBean) {

        ((StockMultiMaterialVH) holder).tvMatDesc.setText(retailerStkBean.getOrderMaterialGroupDesc());
//        ((StockMultiMaterialVH) holder).tvUom.setText(retailerStkBean.getUom());
        if (!TextUtils.isEmpty(retailerStkBean.getQAQty())) {
            ((StockMultiMaterialVH) holder).tvLandingPrice.setText(retailerStkBean.getQAQty() + " " + retailerStkBean.getUom());
            ((StockMultiMaterialVH) holder).tvLandingPrice.setVisibility(View.VISIBLE);
        } else {
            ((StockMultiMaterialVH) holder).tvLandingPrice.setVisibility(View.INVISIBLE);
        }
        ((StockMultiMaterialVH) holder).tvAsOnDate.setText(retailerStkBean.getDisplayAsOnDate());
//        if (!retailerStkBean.isDecimalCheck()) {
        ((StockMultiMaterialVH) holder).etQty.setInputType(InputType.TYPE_CLASS_NUMBER);
        UtilConstants.editTextDecimalFormat(((StockMultiMaterialVH) holder).etQty, maxLength, 0);
//        } else {
//            ((StockMultiMaterialVH) holder).etQty.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
//            UtilConstants.editTextDecimalFormat(((StockMultiMaterialVH) holder).etQty, 13, 3);
//        }
        ((StockMultiMaterialVH) holder).qtyTextWatcher.updateTextWatcher(retailerStkBean, holder, this);
        if (retailerStkBean.isChecked()) {


            if (retailerStkBean.isAddButtonEnabled()) {
                ((StockMultiMaterialVH) holder).etQty.setText(retailerStkBean.getEnterdQty());
                ((StockMultiMaterialVH) holder).clView.setVisibility(View.VISIBLE);
                ((StockMultiMaterialVH) holder).tvUom.setVisibility(View.VISIBLE);
                ((StockMultiMaterialVH) holder).btAdd.setVisibility(View.GONE);
            }else{
                ((StockMultiMaterialVH) holder).clView.setVisibility(View.GONE);
                ((StockMultiMaterialVH) holder).tvUom.setVisibility(View.GONE);
                ((StockMultiMaterialVH) holder).btAdd.setVisibility(View.VISIBLE);
            }

        } else {
//            ((StockMultiMaterialVH) holder).clView.setVisibility(View.GONE);
//            ((StockMultiMaterialVH) holder).tvUom.setVisibility(View.GONE);
//            ((StockMultiMaterialVH) holder).btAdd.setVisibility(View.VISIBLE);
            if (retailerStkBean.isAddButtonEnabled()) {
                ((StockMultiMaterialVH) holder).etQty.setText(retailerStkBean.getEnterdQty());
                ((StockMultiMaterialVH) holder).clView.setVisibility(View.VISIBLE);
                ((StockMultiMaterialVH) holder).tvUom.setVisibility(View.VISIBLE);
                ((StockMultiMaterialVH) holder).btAdd.setVisibility(View.GONE);
            }else{
                ((StockMultiMaterialVH) holder).clView.setVisibility(View.GONE);
                ((StockMultiMaterialVH) holder).tvUom.setVisibility(View.GONE);
                ((StockMultiMaterialVH) holder).btAdd.setVisibility(View.VISIBLE);
            }
        }
        ((StockMultiMaterialVH) holder).btAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (retailerStkBean.isChecked()) {
                    retailerStkBean.setChecked(false);
                } else {
                    retailerStkBean.setChecked(true);
                }

                retailerStkBean.setAddButtonEnabled(true);

                if (retailerStkBean.isChecked()) {
                    ((StockMultiMaterialVH) holder).clView.setVisibility(View.VISIBLE);
                    ((StockMultiMaterialVH) holder).tvUom.setVisibility(View.VISIBLE);
                    ((StockMultiMaterialVH) holder).btAdd.setVisibility(View.GONE);
                    ((StockMultiMaterialVH) holder).etQty.setFocusable(true);
                    ((StockMultiMaterialVH) holder).etQty.setFocusableInTouchMode(true);
                    ((StockMultiMaterialVH) holder).etQty.requestFocus();
                    ((StockMultiMaterialVH) holder).etQty.setImeOptions(EditorInfo.IME_ACTION_DONE);
                    ConstantsUtils.showKeyboard(RetailerStkCrtActivity.this, ((StockMultiMaterialVH) holder).etQty);
                } else {
                    if (retailerStkBean.isAddButtonEnabled()) {
                        ((StockMultiMaterialVH) holder).clView.setVisibility(View.VISIBLE);
                        ((StockMultiMaterialVH) holder).tvUom.setVisibility(View.VISIBLE);
                        ((StockMultiMaterialVH) holder).btAdd.setVisibility(View.GONE);
                        ((StockMultiMaterialVH) holder).etQty.setFocusable(true);
                        ((StockMultiMaterialVH) holder).etQty.setFocusableInTouchMode(true);
                        ((StockMultiMaterialVH) holder).etQty.requestFocus();
                        ((StockMultiMaterialVH) holder).etQty.setImeOptions(EditorInfo.IME_ACTION_DONE);
                        ConstantsUtils.showKeyboard(RetailerStkCrtActivity.this, ((StockMultiMaterialVH) holder).etQty);
                    }else{
                        ((StockMultiMaterialVH) holder).clView.setVisibility(View.GONE);
                        ((StockMultiMaterialVH) holder).tvUom.setVisibility(View.GONE);
                        ((StockMultiMaterialVH) holder).btAdd.setVisibility(View.VISIBLE);
                    }

                }
            }
        });
        ((StockMultiMaterialVH) holder).tvUom.setText(retailerStkBean.getEnterdUOM());

       /* ArrayAdapter<String> adapterShipToList = new ArrayAdapter<String>(RetailerStkCrtActivity.this, R.layout.custom_textview, R.id.tvItemValue, retailerStkBean.getUomList()) {
            @Override
            public View getDropDownView(final int position, final View convertView, final ViewGroup parent) {
                final View v = super.getDropDownView(position, convertView, parent);
                ConstantsUtils.selectedView(v, ((StockMultiMaterialVH) holder).spUom, position, getContext());
                return v;
            }
        };
        adapterShipToList.setDropDownViewResource(R.layout.spinnerinside);
        ((StockMultiMaterialVH) holder).spUom.setAdapter(adapterShipToList);
        if (!TextUtils.isEmpty(retailerStkBean.getEnterdUOM())) {
            ((StockMultiMaterialVH) holder).spUom.setSelection(retailerStkBean.getUomList().indexOf(retailerStkBean.getEnterdUOM()));
        }
        ((StockMultiMaterialVH) holder).spUom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d("Retailer Stk", "onItemSelected: " + position);
                if (!retailerStkBean.getUomList().isEmpty())
                    retailerStkBean.setEnterdUOM(retailerStkBean.getUomList().get(position));

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });*/
        /*final ArrayAdapter<String> adapter = new ArrayAdapter<String>(RetailerStkCrtActivity.this,android.R.layout.simple_dropdown_item_1line, retailerStkBean.getUomList());
        ((StockMultiMaterialVH) holder).spUom.setThreshold(1);
        ((StockMultiMaterialVH) holder).spUom.setAdapter(adapter);
        ((StockMultiMaterialVH) holder).spUom.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View paramView, MotionEvent paramMotionEvent) {
                if (retailerStkBean.getUomList().size() > 0) {
                    if (!((StockMultiMaterialVH) holder).spUom.getText().toString().equals(""))
                        adapter.getFilter().filter(null);
                    ((StockMultiMaterialVH) holder).spUom.showDropDown();
                }
                return true;
            }
        });
        ((StockMultiMaterialVH) holder).spUom.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long rowId) {
                retailerStkBean.setEnterdUOM((String) parent.getItemAtPosition(position));
            }
        });
        int pos = retailerStkBean.getUomList().indexOf(retailerStkBean.getEnterdUOM());
        if (pos >= 0)
            ((StockMultiMaterialVH) holder).spUom.setText(retailerStkBean.getUomList().get(pos));*/

    }

    @Override
    public void onTextChange(String charSequence, RetailerStockBean retailerStkBean, RecyclerView.ViewHolder holder) {

//        presenter.getCheckedCount();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_retailer_stock, menu);
        menu.removeItem(R.id.menu_save);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchableInfo searchInfo = searchManager.getSearchableInfo(getComponentName());
        mSearchView = (SearchView) menu.findItem(R.id.menu_search_item).getActionView();
        mSearchView.setSearchableInfo(searchInfo);
        mSearchView.setQueryHint(searchHint);
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                presenter.onSearchMaterial(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                presenter.onSearchMaterial(newText);
                return false;
            }
        });
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
                presenter.validateItem(0, recyclerView);
                return true;
            case R.id.filter:
                presenter.onFilter();
                return true;
            case R.id.add:
                presenter.addRetailerStock(retailerStckList);
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }

    }

    @Override
    public boolean applyConditionToAdd(Object o) {
        if (mSearchView != null)
            return presenter.onSearch(mSearchView.getQuery().toString(), o);
        else
            return false;
    }


    private void resetMatView(RetailerStockBean retailerStkBean, final RecyclerView.ViewHolder holder) {
//        if (retailerStkBean.isChecked() && !retailerStkBean.isHide()) {
        retailerStkBean.setChecked(false);
        retailerStkBean.setAddButtonEnabled(false);
        retailerStkBean.setEnterdQty("0");
        ((StockMultiMaterialVH) holder).clView.setVisibility(View.GONE);
        ((StockMultiMaterialVH) holder).tvUom.setVisibility(View.GONE);
        ((StockMultiMaterialVH) holder).btAdd.setVisibility(View.VISIBLE);
//            presenter.getCheckedCount();
        ((StockMultiMaterialVH) holder).etQty.clearFocus();
        ((StockMultiMaterialVH) holder).etQty.setFocusable(false);
//        }
        retailerStckList.remove(retailerStkBean);
        presenter.removeItem(retailerStkBean);
        hideKeyboard();
        simpleRecyclerViewAdapter.refreshAdapter(retailerStckList);
    }

    @SuppressWarnings("all")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ConstantsUtils.ACTIVITY_RESULT_FILTER) {
            presenter.startFilter(requestCode, resultCode, data);
        } else {
            presenter.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void setFilterDate(String filterType) {
        try {
            if (filterType != null && !filterType.equalsIgnoreCase("")) {
                linearLayoutFlowLayout.setVisibility(View.VISIBLE);
            } else {
                linearLayoutFlowLayout.setVisibility(View.GONE);
            }
            String[] filterTypeArr = filterType.split(", ");
//            ConstantsUtils.displayFilter(filterTypeArr, flowLayout, RetailerStkCrtActivity.this);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreateUpdateSuccess() {
        Intent intent = new Intent(this, RetailerStkCrtActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(Constants.CPNo, mstrCustomerNo);
        intent.putExtra(Constants.CPUID, mStrUID);
        intent.putExtra(Constants.RetailerName, mStrCustomerName);
        intent.putExtra(Constants.CPGUID32, mStrCPGUID32);
        intent.putExtra(Constants.comingFrom, mStrComingFrom);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        UtilConstants.dialogBoxWithCallBack(this, "", getString(R.string.on_back_press_retailer_stock_msg), getString(R.string.yes), getString(R.string.no), false, new DialogCallBack() {
            @Override
            public void clickedStatus(boolean clickedStatus) {
                if (clickedStatus) {
                    finish();
                }
            }
        });

    }
}
