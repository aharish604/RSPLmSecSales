package com.arteriatech.ss.msecsales.rspl.sampledisbursement;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.SystemClock;
import androidx.core.view.MenuItemCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
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
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.arteriatech.mutils.adapter.AdapterInterface;
import com.arteriatech.mutils.adapter.SimpleRecyclerViewAdapter;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.interfaces.DialogCallBack;
import com.arteriatech.ss.msecsales.rspl.R;
import com.arteriatech.ss.msecsales.rspl.common.Constants;
import com.arteriatech.ss.msecsales.rspl.common.ConstantsUtils;
import com.arteriatech.ss.msecsales.rspl.mbo.DMSDivisionBean;
import com.arteriatech.ss.msecsales.rspl.mbo.RetailerStockBean;
import com.arteriatech.ss.msecsales.rspl.sampledisbursement.addsampledisbursement.AddSampleDisbursementActivity;
import com.arteriatech.ss.msecsales.rspl.ui.MaterialDesignSpinner;

import java.util.ArrayList;

public class SampleDisbursementActivity extends AppCompatActivity implements AdapterInterface<RetailerStockBean>,SwipeRefreshLayout.OnRefreshListener,SampleDisbursementView {
    private String mStrBundleRetID = "";
    private String mStrBundleRetName = "";
    private String mStrBundleRetUID = "";
    private String mStrBundleCPGUID = "";
    private String distubutorID = "";
    private String beatGUID = "";
    private String parentId = "";
    private String mStrBundleCPGUID32 = "";
    private int maxLength = 0;
    private String division = "";
    private SwipeRefreshLayout swipeRefresh;
    private RecyclerView recyclerView;
    private TextView no_record_found;
    private TextView textInfo;
    MaterialDesignSpinner spDMSDivision;
    private SimpleRecyclerViewAdapter<RetailerStockBean> recyclerViewAdapter;
    SampleDisbursementPresenterImpl sampleDisbursementPresenter;
    private ArrayList<RetailerStockBean> retailerStockBeanTotalArrayList = new ArrayList<>();
    private ArrayList<RetailerStockBean> retailerStockBeanArrayList = new ArrayList<>();
    private Toolbar toolbar;
    private boolean isClickable = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample_disbursement);
        Bundle bundleExtras = getIntent().getExtras();
        if (bundleExtras != null) {
            mStrBundleRetID = bundleExtras.getString(Constants.CPNo);
            mStrBundleRetName = bundleExtras.getString(Constants.RetailerName);
            mStrBundleRetUID = bundleExtras.getString(Constants.CPUID);
            mStrBundleCPGUID = bundleExtras.getString(Constants.CPGUID);
            mStrBundleCPGUID32 = bundleExtras.getString(Constants.CPGUID32);
            distubutorID = bundleExtras.getString(Constants.DistubutorID,"");
            beatGUID = bundleExtras.getString(Constants.BeatGUID,"");
            parentId = bundleExtras.getString(Constants.ParentId,"");
        }
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        spDMSDivision = (MaterialDesignSpinner) findViewById(R.id.spDMSDivision);
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);
        textInfo = (TextView)findViewById(R.id.textInfo);
        swipeRefresh.setOnRefreshListener(this);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        no_record_found = (TextView) findViewById(R.id.no_record_found);
        ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string        .sample_disbursement_title), 0);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
        try {
            maxLength=Constants.quantityLength();
        } catch (Exception e) {
            e.printStackTrace();
        }
        recyclerViewAdapter = new SimpleRecyclerViewAdapter<RetailerStockBean>(this, R.layout.sample_disbursement_item, this, recyclerView, no_record_found);
        recyclerView.setAdapter(recyclerViewAdapter);
        sampleDisbursementPresenter = new SampleDisbursementPresenterImpl(SampleDisbursementActivity.this, SampleDisbursementActivity.this, mStrBundleCPGUID32, mStrBundleCPGUID,mStrBundleRetID,distubutorID,parentId);
        sampleDisbursementPresenter.getCPSPRelationDivisions(parentId);
//        sampleDisbursementPresenter.start(mStrBundleRetID,mStrBundleRetName);
    }

    DMSDivisionBean dmsDivisionBean = null;
    @Override
    public void displayDMSDivision(final ArrayList<DMSDivisionBean> alDMSDiv) {
        ArrayAdapter<DMSDivisionBean> adapterShipToList = new ArrayAdapter<DMSDivisionBean>(this, R.layout.custom_textview,
                R.id.tvItemValue, alDMSDiv) {
            @Override
            public View getDropDownView(final int position, final View convertView, final ViewGroup parent) {
                final View v = super.getDropDownView(position, convertView, parent);
                ConstantsUtils.selectedView(v, spDMSDivision, position, getContext());
                return v;
            }
        };
        adapterShipToList.setDropDownViewResource(R.layout.spinnerinside);
        spDMSDivision.setAdapter(adapterShipToList);
        spDMSDivision.showFloatingLabel();
        spDMSDivision.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                dmsDivisionBean = alDMSDiv.get(position);
                if(dmsDivisionBean!=null) {
                    if(!dmsDivisionBean.getDMSDivisionID().equalsIgnoreCase("")) {
                        retailerStockBeanArrayList.clear();
                        displayList(new ArrayList<RetailerStockBean>());
                        division=dmsDivisionBean.getDMSDivisionID();
                        sampleDisbursementPresenter.start(mStrBundleRetID,mStrBundleRetName,division);
                        textInfo.setVisibility(View.VISIBLE);
                        no_record_found.setVisibility(View.GONE);
                    }else {
                        division = "";
                        displayList(new ArrayList<RetailerStockBean>());
                        textInfo.setVisibility(View.GONE);
                        no_record_found.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    @Override
    public void onRefresh() {
        swipeRefresh.setRefreshing(false);
    }
    @Override
    public void onItemClick(RetailerStockBean retailerStockBean, View view, int i) {

    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i, View view) {
        return new SampleDisbursementVH(view,new SampleDisbursementRetailerStockCrtQtyTxtWtchr(),new SDRemarksTextWatcher());
    }
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, int i, final RetailerStockBean retailerStockBean) {
        ((SampleDisbursementVH) viewHolder).tvMaterailName.setText(retailerStockBean.getMaterialDesc());
        ((SampleDisbursementVH) viewHolder).tvDBStock.setText(retailerStockBean.getUnrestrictedQty() + " " + retailerStockBean.getUom());
        ((SampleDisbursementVH) viewHolder).qtyTextWatcher.updateTextWatcher(retailerStockBean, viewHolder);
        ((SampleDisbursementVH) viewHolder).remarksTextWatcher.updateTextWatcher(retailerStockBean, viewHolder);
        ((SampleDisbursementVH) viewHolder).edMaterialQty.setInputType(InputType.TYPE_CLASS_NUMBER);
        UtilConstants.editTextDecimalFormat(((SampleDisbursementVH) viewHolder).edMaterialQty, maxLength, 0);
        ((SampleDisbursementVH) viewHolder).edMaterialQty.setText(retailerStockBean.getEnterdQty());
        try {
            if(retailerStockBean.getRemarks().isEmpty()) {
                ((SampleDisbursementVH) viewHolder).etRemarks.setText("");
            }else {
                ((SampleDisbursementVH) viewHolder).etRemarks.setText(""+retailerStockBean.getRemarks());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(retailerStockBean.getEnterdQty().isEmpty()) {
           ((SampleDisbursementVH) viewHolder).edMaterialQty.setVisibility(View.GONE);
           ((SampleDisbursementVH) viewHolder).btAdd.setVisibility(View.VISIBLE);
       }
       else
       {
           ((SampleDisbursementVH) viewHolder).edMaterialQty.setVisibility(View.VISIBLE);
           ((SampleDisbursementVH) viewHolder).btAdd.setVisibility(View.GONE);
       }
        ((SampleDisbursementVH) viewHolder).btAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((SampleDisbursementVH) viewHolder).edMaterialQty.setVisibility(View.VISIBLE);
                ((SampleDisbursementVH) viewHolder).btAdd.setVisibility(View.GONE);
                ((SampleDisbursementVH) viewHolder).edMaterialQty.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN , 0, 0, 0));
                ((SampleDisbursementVH) viewHolder).edMaterialQty.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_UP , 0, 0, 0));
              //  ConstantsUtils.showKeyboard(SampleDisbursementActivity.this,((SampleDisbursementVH) viewHolder).edMaterialQty);
            }
        });

         final ArrayAdapter<String> adapter = new ArrayAdapter<String>(SampleDisbursementActivity.this,android.R.layout.simple_dropdown_item_1line, retailerStockBean.getUomList());
        ((SampleDisbursementVH) viewHolder).autUOM.setThreshold(1);
        ((SampleDisbursementVH) viewHolder).autUOM.setAdapter(adapter);
        ((SampleDisbursementVH) viewHolder).autUOM.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View paramView, MotionEvent paramMotionEvent) {
                if (retailerStockBean.getUomList().size() > 0) {
                    if (!((SampleDisbursementVH) viewHolder).autUOM.getText().toString().equals(""))
                        adapter.getFilter().filter(null);
                    ((SampleDisbursementVH) viewHolder).autUOM.showDropDown();
                }
                return true;
            }
        });
        ((SampleDisbursementVH) viewHolder).autUOM.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long rowId) {
                retailerStockBean.setEnterdUOM((String) parent.getItemAtPosition(position));
            }
        });
        int pos = retailerStockBean.getUomList().indexOf(retailerStockBean.getEnterdUOM());
        if (pos >= 0)
            ((SampleDisbursementVH) viewHolder).autUOM.setText(retailerStockBean.getUomList().get(pos));
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_sample_disbursement, menu);
        MenuItem searchItem = menu.findItem(R.id.menu_search_item);
        SearchView mSearchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        SearchManager searchManager = (SearchManager)getSystemService(Context.SEARCH_SERVICE);
        SearchableInfo searchInfo = searchManager.getSearchableInfo(getComponentName());
        mSearchView.setSearchableInfo(searchInfo);
        mSearchView.setQueryHint(getString(R.string.lbl_sample_disbursement_search));
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if(Constants.addedSeaerch){
                    sampleDisbursementPresenter.seaarchAddedItem(query);
                }
                else
                {
                    sampleDisbursementPresenter.filter(query);

                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(Constants.addedSeaerch){
                    sampleDisbursementPresenter.seaarchAddedItem(newText);
                }
                else
                {
                    sampleDisbursementPresenter.filter(newText);

                }

                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.menu_add:
                if(!TextUtils.isEmpty(division)) {
                    sampleDisbursementPresenter.add(retailerStockBeanTotalArrayList,division);
                }else {
                    spDMSDivision.setError("Select Division");
                }
                return true;
                case R.id.menu_save:
                    if (!isClickable) {
                        isClickable = true;
                        if (!retailerStockBeanArrayList.isEmpty()) {
                            UtilConstants.dialogBoxWithCallBack(SampleDisbursementActivity.this, "", getString(R.string.so_save_SampleDisbursement_msg), getString(R.string.ok), getString(R.string.cancel), false, new DialogCallBack() {
                                @Override
                                public void clickedStatus(boolean b) {
                                    isClickable =false;
                                    if (b) {
                                        sampleDisbursementPresenter.validateFields(retailerStockBeanArrayList);
                                    }
                                }
                            });
                        }else {
                            isClickable =false;
                            displayMsg(getString(R.string.retailer_items_selected));
                        }
                    }

                    return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    public void showProgress() {
        swipeRefresh.setRefreshing(true);
    }
    @Override
    public void hideProgress() {
        swipeRefresh.setRefreshing(false);
    }
    @Override
    public void displayMsg(String msg) {
        ConstantsUtils.displayLongToast(SampleDisbursementActivity.this,msg );
    }
    @Override
    public void displayList(ArrayList<RetailerStockBean> list) {
        retailerStockBeanTotalArrayList=list;
        if(list!=null && list.size()>0){

            if (Constants.checkSearch) {
                recyclerViewAdapter.refreshAdapter(list);
                textInfo.setVisibility(View.GONE);
            }
            else
            {
                textInfo.setVisibility(View.VISIBLE);
            }

        }
        else
        {
            recyclerViewAdapter.refreshAdapter(list);

        }

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AddSampleDisbursementActivity.SD_RESULT_ID) {
            if (data != null) {
                ArrayList<RetailerStockBean> retailerStockBeenlist = (ArrayList<RetailerStockBean>) data.getSerializableExtra(ConstantsUtils.EXTRA_ARRAY_LIST);
                if (!retailerStockBeenlist.isEmpty()) {
                    for (RetailerStockBean retailerStockBean : retailerStockBeenlist) {
                        if (retailerStockBean.getSelected()) {
                            retailerStockBeanArrayList.add(retailerStockBean);
                            RetailerStockBean retailerStockBeenMain = retailerStockBeanTotalArrayList.get(retailerStockBean.getRetailerPos());
                            retailerStockBeenMain.setSelected(true);
                        }
                    }
                    recyclerViewAdapter.refreshAdapter(retailerStockBeanArrayList);
                    sampleDisbursementPresenter.addedList(retailerStockBeanArrayList);
                    Constants.addedSeaerch=true;
                    textInfo.setVisibility(View.GONE);

                }
            }

        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
        @Override
        public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
            if (viewHolder != null) {
                final View foregroundView = ((SampleDisbursementVH) viewHolder).viewForeground;
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
            final View foregroundView = (((SampleDisbursementVH) viewHolder).viewForeground);
            getDefaultUIUtil().onDrawOver(c, recyclerView, foregroundView, dX, dY, actionState, isCurrentlyActive);

        }
        @Override
        public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            try {
                final View foregroundView = ((SampleDisbursementVH) viewHolder).viewForeground;
                getDefaultUIUtil().clearView(foregroundView);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        @Override
        public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            int position = viewHolder.getAdapterPosition();
            View foregroundView = ((SampleDisbursementVH) viewHolder).viewForeground;

            getDefaultUIUtil().onDraw(c, recyclerView, foregroundView, dX, dY,
                    actionState, isCurrentlyActive);
        }
        @Override
        public int convertToAbsoluteDirection(int flags, int layoutDirection) {
            return super.convertToAbsoluteDirection(flags, layoutDirection);
        }
        @Override
        public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {
            if (viewHolder instanceof SampleDisbursementVH) {
                final int position = viewHolder.getAdapterPosition(); //swiped position
                if (!retailerStockBeanArrayList.isEmpty()) {
                    RetailerStockBean retailerStkBean = retailerStockBeanArrayList.get(position);
                    if (direction == ItemTouchHelper.RIGHT) {
                        ((SampleDisbursementVH) viewHolder).ivRight.setVisibility(View.GONE);
                        ((SampleDisbursementVH) viewHolder).ivLeft.setVisibility(View.VISIBLE);
                        resetMatView(retailerStkBean, viewHolder,position);
                    } else if (direction == ItemTouchHelper.LEFT) {
                        ((SampleDisbursementVH) viewHolder).ivRight.setVisibility(View.VISIBLE);
                        ((SampleDisbursementVH) viewHolder).ivLeft.setVisibility(View.GONE);
                        resetMatView(retailerStkBean, viewHolder,position);
                    }
                }
            }
        }
    };
    private void resetMatView(RetailerStockBean retailerStkBean, final RecyclerView.ViewHolder holder,int position) {
        try {
//        if (retailerStkBean.isChecked() && !retailerStkBean.isHide()) {
            retailerStkBean.setChecked(false);
            retailerStkBean.setEnterdQty("0");
//        ((SampleDisbursementListVH) holder).clView.setVisibility(View.GONE);
//        ((SampleDisbursementListVH) holder).spUom.setVisibility(View.GONE);
//        ((SampleDisbursementListVH) holder).btAdd.setVisibility(View.VISIBLE);
////            presenter.getCheckedCount();
//        ((SampleDisbursementListVH) holder).etQty.clearFocus();
//        ((SampleDisbursementListVH) holder).etQty.setFocusable(false);
//        }
            retailerStockBeanTotalArrayList.get(retailerStkBean.getRetailerPos()).setSelected(false);
          //  retailerStockBeanTotalArrayList.get(position).setSelected(false);
            retailerStockBeanArrayList.remove(retailerStkBean);
//        retailerStockBeanArrayList.remove(position);
            //  hideKeyboard();
            recyclerViewAdapter.refreshAdapter(retailerStockBeanArrayList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {

            AlertDialog.Builder builder = new AlertDialog.Builder(SampleDisbursementActivity.this, R.style.MyTheme);
            builder.setMessage(R.string.alert_exit_create_sampleDisbursement).setCancelable(false)
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            finish();
                        }
                    })
                    .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }

                    });
            builder.show();

    }
}
