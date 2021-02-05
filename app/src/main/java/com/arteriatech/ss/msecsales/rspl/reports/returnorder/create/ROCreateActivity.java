package com.arteriatech.ss.msecsales.rspl.reports.returnorder.create;

import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.arteriatech.mutils.adapter.AdapterViewInterface;
import com.arteriatech.mutils.adapter.SimpleRecyclerViewTypeAdapter;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.interfaces.DialogCallBack;
import com.arteriatech.ss.msecsales.rspl.R;
import com.arteriatech.ss.msecsales.rspl.common.Constants;
import com.arteriatech.ss.msecsales.rspl.common.ConstantsUtils;
import com.arteriatech.ss.msecsales.rspl.mbo.ConfigTypesetTypesBean;
import com.arteriatech.ss.msecsales.rspl.mbo.ROCreateBean;
import com.arteriatech.ss.msecsales.rspl.mbo.ReturnOrderBean;
import com.arteriatech.ss.msecsales.rspl.mbo.SKUGroupBean;
import com.arteriatech.ss.msecsales.rspl.reports.invoicelist.SOItemDetailsVH1;
import com.arteriatech.ss.msecsales.rspl.so.SOUtils;

import java.util.ArrayList;
import java.util.List;

public class ROCreateActivity extends AppCompatActivity implements ROCreateView, AdapterViewInterface<ReturnOrderBean> {
    // data members
    private String mStrBundleRetID = "";
    private String beatGUID = "";
    private String parentId = "";
    private int maxLength = 0;
    private String mStrBundleRetName = "", mStrBundleCPGUID = "";
    private String mStrBundleRetailerUID = "", mStrBundleCPGUID32 = "", mStrComingFrom = "",mStrComingFromInv="";
    private ROCreateBean rocreatebean = null;
    private ArrayList<ReturnOrderBean> returnOrderBeanArrayList = null;
    private ROCreatePresenterImpl presenter;
    private Toolbar mToolbar;
    private MenuItem menuItem = null;
    private ArrayList<ReturnOrderBean> list;
    private RecyclerView recyclerView;
    private TextView noRecordFound;
    private SimpleRecyclerViewTypeAdapter<ReturnOrderBean> simpleRVAdapter;
    private List<ConfigTypesetTypesBean> configTypesetTypesBeanList = new ArrayList<>();
    private ArrayList<ReturnOrderBean> returnOrderListFinalList = new ArrayList<>();
    private TextView tvRetailerName;
    private TextView tvRetailerID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_return_order_create);

        // fetching data sent by other component through bundle
        Bundle bundleExtras = getIntent().getExtras();
        if (bundleExtras != null) {
            rocreatebean = bundleExtras.getParcelable(Constants.ROList);
            beatGUID = bundleExtras.getString(Constants.BeatGUID);
            parentId = bundleExtras.getString(Constants.ParentId);
//            returnOrderBeanArrayList = bundleExtras.getParcelableArrayList(Constants.EXTRA_BEAN);
            if(rocreatebean==null) {
                mStrBundleRetID = bundleExtras.getString(Constants.CPNo);
                mStrBundleRetName = bundleExtras.getString(Constants.RetailerName);
                mStrBundleCPGUID = bundleExtras.getString(Constants.CPGUID);
                mStrBundleCPGUID32 = bundleExtras.getString(Constants.CPGUID32);
                mStrBundleRetailerUID = bundleExtras.getString(Constants.CPUID);
                mStrComingFrom = bundleExtras.getString(Constants.comingFrom);
                mStrComingFromInv = "";
                // adding details from bundle to competitors bean class
                rocreatebean = new ROCreateBean();
                rocreatebean.setCPGUID(mStrBundleCPGUID);
                rocreatebean.setCPGUID32(mStrBundleCPGUID32);
                rocreatebean.setCPNo(mStrBundleRetID);
                rocreatebean.setCPName(mStrBundleRetName);
                rocreatebean.setCPUID(mStrBundleRetailerUID);
                rocreatebean.setComingFrom(mStrComingFrom);
                rocreatebean.setComingFromInv(mStrComingFromInv);
            }else{
                mStrBundleRetID = rocreatebean.getCPNo();
                mStrBundleRetName = rocreatebean.getCPName();
                mStrBundleCPGUID = rocreatebean.getCPGUID();
                mStrBundleCPGUID32 = rocreatebean.getCPGUID32();
                mStrBundleRetailerUID = rocreatebean.getCPUID();
                mStrComingFrom = rocreatebean.getComingFrom();
                mStrComingFromInv = rocreatebean.getComingFromInv();
//                returnOrderListFinalList.addAll(rocreatebean.getRoList());
            }
        }

        try {
            maxLength=Constants.quantityLength();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Initializing presenter(MVP)
        initiateUI();
        presenter = new ROCreatePresenterImpl(ROCreateActivity.this, this, true, ROCreateActivity.this, rocreatebean,beatGUID,parentId);
        if (!Constants.restartApp(ROCreateActivity.this)) {
            presenter.onStart();
        }

    }

    /**
     * @desc for initialization of UI components
     */
    private void initiateUI() {
        tvRetailerName = (TextView) findViewById(R.id.tv_RetailerName);
        tvRetailerID = (TextView) findViewById(R.id.tv_RetailerID);
        tvRetailerName.setText(mStrBundleRetName);
        tvRetailerID.setText(mStrBundleRetID);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        // setting toolbar title
        ConstantsUtils.initActionBarView(this, mToolbar, true, getString(R.string.title_ro_create), 0);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        noRecordFound = (TextView) findViewById(R.id.no_record_found);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
        simpleRVAdapter = new SimpleRecyclerViewTypeAdapter<ReturnOrderBean>(ROCreateActivity.this, R.layout.ro_create_item, this, recyclerView, noRecordFound);
        recyclerView.setAdapter(simpleRVAdapter);
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
    public void conformationDialog(String message, int from) {

    }

    @Override
    public void showMessage(String message, boolean isSimpleDialog) {
        ConstantsUtils.displayLongToast(ROCreateActivity.this, message);
    }

    @Override
    public void displaySO(ArrayList<SKUGroupBean> alCRSSKUGrpList) {

    }

    @Override
    public void searchResult(ArrayList<SKUGroupBean> skuSearchList) {

    }

    @Override
    public void displayCat(String[][] strCats) {

    }

    @Override
    public void displayBrands(String[][] strBrands) {

    }

    @Override
    public void displayMustSells(String[][] strMustSells) {

    }

    @Override
    public void showProgressDialog() {

    }

    @Override
    public void RODetails(ArrayList<ReturnOrderBean> list, String stackOwner, List<ConfigTypesetTypesBean> configTypesetTypesBeanList) {
        this.list = list;
        this.configTypesetTypesBeanList = configTypesetTypesBeanList;
        rocreatebean.setStackOwner(stackOwner);
    }

    @Override
    public void setFilterDate(String filterType) {

    }

    @Override
    public void roDisplayList(ArrayList<ReturnOrderBean> returnOrderList) {
        this.returnOrderListFinalList=returnOrderList;
        simpleRVAdapter.refreshAdapter(returnOrderList);
    }

    @Override
    public void errormessageQTY(String message) {
        try {
            int childCount = returnOrderListFinalList.size();
            for (int i = 0; i < childCount; i++) {
                ReturnOrderBean returnOrderBean = returnOrderListFinalList.get(i);
                ROCreateVH childHolder = (ROCreateVH) recyclerView.findViewHolderForLayoutPosition(i);
                if (childHolder != null) {
                    if (TextUtils.isEmpty(returnOrderBean.getReturnQty()) || Double.parseDouble(returnOrderBean.getReturnQty()) == 0) {
                        childHolder.etQty.setError(message);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void errormessageBatch(String message) {
        try {
            int childCount = returnOrderListFinalList.size();
            for (int i = 0; i < childCount; i++) {
                ReturnOrderBean returnOrderBean = returnOrderListFinalList.get(i);
                ROCreateVH childHolder = (ROCreateVH) recyclerView.findViewHolderForLayoutPosition(i);
                if (childHolder != null) {
                    if (TextUtils.isEmpty(returnOrderBean.getReturnBatchNumber())) {
                        childHolder.tv_cust_batch_number.setErrorEnabled(true);
                        childHolder.tv_cust_batch_number.setError(message);
                    }else {
                        childHolder.tv_cust_batch_number.setErrorEnabled(false);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void errormessageReason(String message) {
        try {
            int childCount = returnOrderListFinalList.size();
            for (int i = 0; i < childCount; i++) {
                ReturnOrderBean returnOrderBean = returnOrderListFinalList.get(i);
                ROCreateVH childHolder = (ROCreateVH) recyclerView.findViewHolderForLayoutPosition(i);
                if (childHolder != null) {
                    if (TextUtils.isEmpty(returnOrderBean.getReturnReason())) {
                        childHolder.spSelectReason.setError(message);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_return_order_create, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView mSearchView = (SearchView) menu.findItem(R.id.menu_search_item).getActionView();
        mSearchView.setMaxWidth(Integer.MAX_VALUE);
        View view = mSearchView.findViewById(androidx.appcompat.R.id.search_plate);
        view.setBackgroundColor(ContextCompat.getColor(this, R.color.transperant));
        SearchableInfo searchInfo = searchManager.getSearchableInfo(getComponentName());
        mSearchView = (SearchView) menu.findItem(R.id.menu_search_item).getActionView();
        mSearchView.setSearchableInfo(searchInfo);
        mSearchView.setQueryHint(getString(R.string.lbl_sample_disbursement_search));
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                presenter.onSearch(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                presenter.onSearch(newText);
                return false;
            }
        });
//        menuItem = menu.findItem(R.id.menu_return_add);
//        if (distStockList != null && distStockList.size() > 0) {
//            menuItem.setVisible(true);
//        } else {
//            menuItem.setVisible(false);
//        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.menu_return_add:
                Intent intent = new Intent(ROCreateActivity.this, AddROActivity.class);
                intent.putExtra(Constants.StockOwner, rocreatebean.getStackOwner());
                intent.putExtra(Constants.ItemList, list);
                startActivityForResult(intent, AddROActivity.RETURN_ORDER_RESULT_ID);
                break;
            case R.id.menu_return_review:
                presenter.validateFields();
                break;
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        presenter.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onItemClick(ReturnOrderBean o, View view, int i) {

    }

    @Override
    public int getItemViewType(int i, ArrayList<ReturnOrderBean> arrayList) {
        return 0;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i, View view) {
        return new ROCreateVH(view, new ROBatchTextWatcher(), new ROQtyTextWatcher());
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, int i, final ReturnOrderBean returnOrderBean, ArrayList<ReturnOrderBean> returnOrderList) {
//        ((SOMultiMaterialVH) holder).etQty.setInputType(InputType.TYPE_CLASS_NUMBER /*| InputType.TYPE_NUMBER_FLAG_DECIMAL*/);
//            UtilConstants.editTextDecimalFormat(((SOMultiMaterialVH) holder).etQty, 13, 3);
        ((ROCreateVH) viewHolder).itemDbstkSkuDesc.setText(returnOrderBean.getMaterialDesc());
        ((ROCreateVH) viewHolder).roBatchTextWatcher.updateTextWatcher(returnOrderBean);
        ((ROCreateVH) viewHolder).roQtyTextWatcher.updateTextWatcher(returnOrderBean);
        ((ROCreateVH) viewHolder).etQty.setText(returnOrderBean.getReturnQty());
        UtilConstants.editTextDecimalFormat(((ROCreateVH) viewHolder).etQty, maxLength, 0);
        ((ROCreateVH) viewHolder).etBatchNumber.setText(returnOrderBean.getReturnBatchNumber());
        try {
            ((ROCreateVH) viewHolder).etBatchNumber.setClickable(false);
            ((ROCreateVH) viewHolder).etBatchNumber.setFocusable(false);
            ((ROCreateVH) viewHolder).etBatchNumber.setEnabled(false);
            ((ROCreateVH) viewHolder).etBatchNumber.setTextColor(getResources().getColor(R.color.BLACK));
        } catch (Exception e) {
            e.printStackTrace();
        }
        ((ROCreateVH) viewHolder).tvMRP.setText(Constants.getCurrencySymbol(returnOrderBean.getCurrency(),returnOrderBean.getAltReturnMrp()));


        if(Constants.isCarOrBag(returnOrderBean.getUom())){
            ((ROCreateVH) viewHolder).etQty.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            UtilConstants.editTextDecimalFormat( ((ROCreateVH) viewHolder).etQty, maxLength, 3);
        }else {
            ((ROCreateVH) viewHolder).etQty.setInputType(InputType.TYPE_CLASS_NUMBER );
            UtilConstants.editTextDecimalFormat( ((ROCreateVH) viewHolder).etQty, maxLength, 0);
        }
        System.out.println("Qnty :"+returnOrderBean.getActualQty()+"uom :"+returnOrderBean.getUom());

        try {
         //   ((ROCreateVH) viewHolder).tvInvQty.setText(returnOrderBean.getActualQty() +" "+returnOrderBean.getUom());

            ((ROCreateVH) viewHolder).tvInvQty.setText(UtilConstants.removeLeadingZero(returnOrderBean.getActualQty()) + " " + returnOrderBean.getUom());
        } catch (Throwable e) {
            e.printStackTrace();
        }


        ((ROCreateVH) viewHolder).tvInvNo.setText(returnOrderBean.getInvoiceNo());

        ArrayAdapter<ConfigTypesetTypesBean> adapterShipToList = new ArrayAdapter<ConfigTypesetTypesBean>(ROCreateActivity.this, R.layout.custom_textview,
                R.id.tvItemValue, configTypesetTypesBeanList) {
            @Override
            public View getDropDownView(final int position, final View convertView, final ViewGroup parent) {
                final View v = super.getDropDownView(position, convertView, parent);
                ConstantsUtils.selectedView(v, ((ROCreateVH) viewHolder).spSelectReason, position, getContext());
                return v;
            }
        };
        ((ROCreateVH) viewHolder).spSelectReason.setSelection(SOUtils.getConfigTypeset(configTypesetTypesBeanList, returnOrderBean.getReturnReason()));
        adapterShipToList.setDropDownViewResource(R.layout.spinnerinside);
        ((ROCreateVH) viewHolder).spSelectReason.setAdapter(adapterShipToList);
        ((ROCreateVH) viewHolder).spSelectReason.showFloatingLabel();
        ((ROCreateVH) viewHolder).spSelectReason.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                returnOrderBean.setReturnReason(configTypesetTypesBeanList.get(position).getTypes());
                returnOrderBean.setReturnDesc(configTypesetTypesBeanList.get(position).getTypesName());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        ArrayAdapter<String> adapterUom = new ArrayAdapter<String>(ROCreateActivity.this, R.layout.custom_textview,
                R.id.tvItemValue, returnOrderBean.getUomList()) {
            @Override
            public View getDropDownView(final int position, final View convertView, final ViewGroup parent) {
                final View v = super.getDropDownView(position, convertView, parent);
                ConstantsUtils.selectedView(v, ((ROCreateVH) viewHolder).spUomType, position, getContext());
                return v;
            }
        };

        adapterUom.setDropDownViewResource(R.layout.spinnerinside);
        if (!TextUtils.isEmpty(returnOrderBean.getReturnUOM()))
        ((ROCreateVH) viewHolder).spUomType.setSelection(returnOrderBean.getUomList().indexOf(returnOrderBean.getReturnUOM()));
        ((ROCreateVH) viewHolder).spUomType.setAdapter(adapterUom);
        ((ROCreateVH) viewHolder).spUomType.hideFloatingLabel();
        ((ROCreateVH) viewHolder).spUomType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                returnOrderBean.setReturnUOM(returnOrderBean.getUomList().get(position));
                if(Constants.isCarOrBag(returnOrderBean.getReturnUOM())){
                    ((ROCreateVH) viewHolder).etQty.setText("");
                    ((ROCreateVH) viewHolder).etQty.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                    UtilConstants.editTextDecimalFormat( ((ROCreateVH) viewHolder).etQty, maxLength, 2);
                }else {
                    ((ROCreateVH) viewHolder).etQty.setText("");
                    ((ROCreateVH) viewHolder).etQty.setInputType(InputType.TYPE_CLASS_NUMBER );
                    UtilConstants.editTextDecimalFormat( ((ROCreateVH) viewHolder).etQty, maxLength, 0);
                }
                if (returnOrderBean.getReturnUOM().equalsIgnoreCase(returnOrderBean.getUom())){
                    returnOrderBean.setAltReturnMrp(returnOrderBean.getUnitPrice());
                    ((ROCreateVH) viewHolder).tvMRP.setText(Constants.getCurrencySymbol(returnOrderBean.getCurrency(),returnOrderBean.getAltReturnMrp()));
                }else {
                    double altReturnMrp =0.0;
                    try{
                        double altUomNum = Double.parseDouble(returnOrderBean.getAlternativeUOM1Num());
                        double altUomDen = Double.parseDouble(returnOrderBean.getAlternativeUOM1Den());
                        double returnMrp = Double.parseDouble(returnOrderBean.getUnitPrice());
                        if (altUomNum <= altUomDen) { // Emami and Pal Case
                            altReturnMrp = returnMrp/altUomDen;
                        }else {// RSPL
                            altReturnMrp = returnMrp/altUomNum;
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    returnOrderBean.setAltReturnMrp(String.valueOf(altReturnMrp));
                    ((ROCreateVH) viewHolder).tvMRP.setText(Constants.getCurrencySymbol(returnOrderBean.getCurrency(),returnOrderBean.getAltReturnMrp()));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

    }
    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
        @Override
        public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
            // super.onSelectedChanged(viewHolder, actionState);
            if (viewHolder != null) {
                final View foregroundView = ((ROCreateVH) viewHolder).viewForeground;
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
            final View foregroundView = (((ROCreateVH) viewHolder).viewForeground);
            getDefaultUIUtil().onDrawOver(c, recyclerView, foregroundView, dX, dY, actionState, isCurrentlyActive);
//            }
        }
        @Override
        public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            try {
                final View foregroundView = ((ROCreateVH) viewHolder).viewForeground;
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
            View foregroundView = ((ROCreateVH) viewHolder).viewForeground;
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
            if (viewHolder instanceof ROCreateVH) {
                final int position = viewHolder.getAdapterPosition(); //swiped position
                if (!returnOrderListFinalList.isEmpty()) {
                    ReturnOrderBean retailerStkBean = returnOrderListFinalList.get(position);
                    if (direction == ItemTouchHelper.RIGHT) {
                        ((ROCreateVH) viewHolder).ivRight.setVisibility(View.GONE);
                        ((ROCreateVH) viewHolder).ivLeft.setVisibility(View.VISIBLE);
                        resetMatView(retailerStkBean);
                    } else if (direction == ItemTouchHelper.LEFT) {
                        ((ROCreateVH) viewHolder).ivRight.setVisibility(View.VISIBLE);
                        ((ROCreateVH) viewHolder).ivLeft.setVisibility(View.GONE);
                        resetMatView(retailerStkBean);
                    }
                }
            }
        }
    };
    private void resetMatView(ReturnOrderBean retailerStkBean) {
        returnOrderListFinalList.remove(retailerStkBean);
        presenter.removeItem(retailerStkBean);
        simpleRVAdapter.refreshAdapter(returnOrderListFinalList);
    }

    @Override
    public void onBackPressed() {
        if(rocreatebean.getComingFromInv().equalsIgnoreCase("")){
            UtilConstants.dialogBoxWithCallBack(ROCreateActivity.this, "", getString(R.string.alert_exit_create_ro), getString(R.string.yes), getString(R.string.no), false, new DialogCallBack() {
                @Override
                public void clickedStatus(boolean b) {
                    if (b){
                        finish();
                    }
                }
            });
        }else{
            finish();
        }


    }
}
