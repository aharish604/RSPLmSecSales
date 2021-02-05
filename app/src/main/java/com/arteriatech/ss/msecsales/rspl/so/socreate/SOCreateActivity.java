package com.arteriatech.ss.msecsales.rspl.so.socreate;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.interfaces.DialogCallBack;
import com.arteriatech.mutils.store.OnlineODataInterface;
import com.arteriatech.ss.msecsales.rspl.R;
import com.arteriatech.ss.msecsales.rspl.common.Constants;
import com.arteriatech.ss.msecsales.rspl.common.ConstantsUtils;
import com.arteriatech.ss.msecsales.rspl.customers.CustomerDetailsActivity;
import com.arteriatech.ss.msecsales.rspl.mbo.DMSDivisionBean;
import com.arteriatech.ss.msecsales.rspl.mbo.SKUGroupBean;
import com.arteriatech.ss.msecsales.rspl.mbo.SOCreateBean;
import com.arteriatech.ss.msecsales.rspl.original.OriginalCellViewGroup;
import com.arteriatech.ss.msecsales.rspl.original.OriginalTableFixHeader;
import com.arteriatech.ss.msecsales.rspl.original.TableFixHeaderAdapter;
import com.arteriatech.ss.msecsales.rspl.scroll.BaseTableAdapter;
import com.arteriatech.ss.msecsales.rspl.scroll.OnScrollViewInterface;
import com.arteriatech.ss.msecsales.rspl.scroll.TableFixHeaders;
import com.arteriatech.ss.msecsales.rspl.so.soreview.SOReviewActivity;
import com.arteriatech.ss.msecsales.rspl.ui.FlowLayout;
import com.arteriatech.ss.msecsales.rspl.ui.MaterialDesignSpinner;
import com.sap.smp.client.odata.ODataEntity;
import com.sap.smp.client.odata.store.ODataRequestExecution;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @desc Created by e10526 on 21-04-2018.
 */

public class SOCreateActivity extends AppCompatActivity implements KeyboardView.OnKeyboardActionListener, SOCreateView,
        TableFixHeaderAdapter.ClickListener, TableFixHeaderAdapter.TextTypeListener,
        OnScrollViewInterface, OnlineODataInterface,
        SOFilterDialogFragment.OnFragmentFilterListener, TableFixHeaderAdapter.SpinnerSelectionListener {

    static EditText mEditTextSelected = null;
    String mStrComingFrom = "";
    String beatGUID = "";
    String parentId = "";
    SOCreatePresenterImpl presenter;
    SOCreateBean soCreateBean = new SOCreateBean();
    ProgressDialog progressDialog = null;
    String skugroupValue = "";
    KeyboardView keyboardView;
    Keyboard keyboard;
    boolean isEditTextClicked = false;
    MaterialDesignSpinner spDMSDivision;
    SearchView mSearchView;
    private Toolbar toolbar;
    private Context mContext;
    private String mStrBundleRetID = "";
    private String mStrBundleRetName = "", mStrBundleCPGUID = "";
    private String mStrBundleRetailerUID = "", mStrBundleCPGUID32 = "";
    private TableFixHeaders tableFixHeaders;
    private BaseTableAdapter tableFixHeadersAdapterFactory;
    private ArrayList<SKUGroupBean> alCRSSKUGrpList = new ArrayList<>();
    private ArrayList<SKUGroupBean> skuGroupBeanArrayListAllData = new ArrayList<>();
    private int lastSelectedEditTextRow = 0;
    private int oldCurPos = -1;
    private BaseTableAdapter salesOrderAdapter = null;
    private boolean isTyping = false;
    private Set<String> mStrCrsSkuCount = new HashSet<>();
    private ArrayList<Integer> expandPossList = new ArrayList<>();
    private LinearLayout linearLayoutFlowLayout;
    private FlowLayout flowLayout;
    private String TAG = SOCreateActivity.class.getSimpleName();
    private int oldPercentage = 0;
    private int maxLength = 0;
    private int getFirstClickedPos = 0;
    private String brand = "";
    private String brandName = "";
    private String category = "";
    private String categoryName = "";
    private String creskuGrp = "";
    private String creskuGrpName = "";
    private String searchText = "";
    private TextView tvRetailerName, tvRetailerID;

    public static void setCursorPostion(EditText editText, View view, MotionEvent motionEvent, SKUGroupBean item) {
        EditText edText = (EditText) view;
        float x = motionEvent.getX();
        float y = motionEvent.getY();
        int touchPosition = editText.getOffsetForPosition(x, y);
        if (touchPosition >= 0) {
            editText.setSelection(touchPosition);
            item.setSetCursorPos(touchPosition);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_so_create);
        Bundle bundleExtras = getIntent().getExtras();
        if (bundleExtras != null) {
            mStrBundleRetID = bundleExtras.getString(Constants.CPNo);
            mStrBundleRetName = bundleExtras.getString(Constants.RetailerName);
            mStrBundleCPGUID = bundleExtras.getString(Constants.CPGUID);
            mStrBundleCPGUID32 = bundleExtras.getString(Constants.CPGUID32);
            mStrBundleRetailerUID = bundleExtras.getString(Constants.CPUID);
            mStrComingFrom = bundleExtras.getString(Constants.comingFrom);
            beatGUID = bundleExtras.getString(Constants.BeatGUID);
            parentId = bundleExtras.getString(Constants.ParentId);
        }
        Constants.writeDebugLog(SOCreateActivity.this,"SO Create for "+mStrBundleRetID);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        mContext = SOCreateActivity.this;
        ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.title_so_create), 0);
        skugroupValue = Constants.getTypesetValueForSkugrp(this);
        initUI();
        soCreateBean = new SOCreateBean();
        soCreateBean.setCPGUID(mStrBundleCPGUID32);
        soCreateBean.setCPGUID36(mStrBundleCPGUID);
        soCreateBean.setCPNo(mStrBundleRetID);
        soCreateBean.setCPUID(mStrBundleRetailerUID != null ? mStrBundleRetailerUID : "");
        soCreateBean.setCPName(mStrBundleRetName);
        soCreateBean.setTempParentID(parentId);
        presenter = new SOCreatePresenterImpl(SOCreateActivity.this, this, true, SOCreateActivity.this, soCreateBean);
        if (!Constants.restartApp(SOCreateActivity.this)) {
            presenter.onStart();
        }
    }

    private void initUI() {

        spDMSDivision = (MaterialDesignSpinner) findViewById(R.id.spDMSDivision);
        tvRetailerName = (TextView) findViewById(R.id.tv_RetailerName);
        tvRetailerID = (TextView) findViewById(R.id.tv_RetailerID);
        // set retailer name
        tvRetailerName.setText(mStrBundleRetName);
        // set retailer id
        tvRetailerID.setText(mStrBundleRetID);
        linearLayoutFlowLayout = (LinearLayout) findViewById(R.id.llFilterLayout);
        flowLayout = (FlowLayout) findViewById(R.id.llFlowLayout);
        Constants.selectedSOItems.clear();
        this.alCRSSKUGrpList.clear();
        skuGroupBeanArrayListAllData.clear();
        Constants.HashMapSubMaterials.clear();
        Constants.MAPSCHGuidByMaterial.clear();
        Constants.MAPQPSSCHGuidByMaterial.clear();
        try {
            maxLength = Constants.quantityLength();
        } catch (Exception e) {
            e.printStackTrace();
        }
        initializeKeyboardDependencies();
        tableFixHeaders = (TableFixHeaders) findViewById(R.id.tablefixheaders);
        createTable(alCRSSKUGrpList);
    }

    private void createTable(ArrayList<SKUGroupBean> alCRSSKUGrpList) {
        tableFixHeadersAdapterFactory = new OriginalTableFixHeader(SOCreateActivity.this,
                alCRSSKUGrpList, this, this, this, skugroupValue).getInstance();
        tableFixHeaders.setAdapter(tableFixHeadersAdapterFactory, this);
    }

    public void initializeKeyboardDependencies() {
        keyboardView = (KeyboardView) findViewById(R.id.keyboard_custom_invoice_sel);
        keyboard = new Keyboard(this, R.xml.ll_with_out_dot_inc_dec_up_down);
        keyboardView.setKeyboard(keyboard);
        keyboardView.setPreviewEnabled(false);
        keyboardView.setOnKeyboardActionListener(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    @Override
    public void showProgressDialog(String message) {
        progressDialog = ConstantsUtils.showProgressDialog(SOCreateActivity.this, message);
    }

    @Override
    public void hideProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void displayMessage(String message) {

    }

    @Override
    public void showMessage(String message, final boolean isSimpleDialog) {
        UtilConstants.dialogBoxWithCallBack(SOCreateActivity.this, "", message, getString(R.string.ok), "", false, new DialogCallBack() {
            @Override
            public void clickedStatus(boolean b) {
                if (!isSimpleDialog) {
                    redirectActivity();
                }
            }
        });
    }

    @Override
    public void displaySO(ArrayList<SKUGroupBean> alList) {
        this.alCRSSKUGrpList.clear();
        skuGroupBeanArrayListAllData.clear();
        this.alCRSSKUGrpList.addAll(alList);
        skuGroupBeanArrayListAllData.addAll(alList);
        createTable(this.alCRSSKUGrpList);

        Constants.hideCustomKeyboard(keyboardView);
        checkAndCloseAllExpandedItem();
    }

    @Override
    public void searchResult(ArrayList<SKUGroupBean> skuSearchList) {
        alCRSSKUGrpList.clear();
        alCRSSKUGrpList.addAll(skuSearchList);

        createTable(alCRSSKUGrpList);
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

    DMSDivisionBean dmsDivisionBean = null;

    @Override
    public void displayDMSDivision(final ArrayList<DMSDivisionBean> alDMSDiv) {
        if (alDMSDiv != null && alDMSDiv.size() > 0)
            for (DMSDivisionBean divData : alDMSDiv)
                if (!TextUtils.isEmpty(divData.getDMSDivisionID()))
                    soCreateBean.getDivisionIds().add(divData.getDMSDivisionID());

        presenter.requestSOData("", "", alDMSDiv,parentId);
        //displaySO(alCRSSKUGrpList);
       /* ArrayAdapter<DMSDivisionBean> adapterShipToList = new ArrayAdapter<DMSDivisionBean>(mContext, R.layout.custom_textview,
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
                        linearLayoutFlowLayout.setVisibility(View.GONE);
                        soCreateBean.setDivision(dmsDivisionBean.getDMSDivisionID());
                        presenter.requestSOData(dmsDivisionBean.getDMSDivisionID(),dmsDivisionBean.getDmsDivsionDesc());
                    }else {
                        displaySO(alCRSSKUGrpList);
                        linearLayoutFlowLayout.setVisibility(View.GONE);
                        soCreateBean.setDivision("");
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });*/
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
            ConstantsUtils.displayFilter(filterTypeArr, flowLayout, SOCreateActivity.this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void redirectActivity() {
        Intent intentNavPrevScreen = new Intent(this, CustomerDetailsActivity.class);
        intentNavPrevScreen.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intentNavPrevScreen.putExtra(Constants.CPNo, mStrBundleRetID);
        intentNavPrevScreen.putExtra(Constants.RetailerName, mStrBundleRetName);
        intentNavPrevScreen.putExtra(Constants.CPUID, mStrBundleRetailerUID);
        intentNavPrevScreen.putExtra(Constants.comingFrom, mStrComingFrom);
        intentNavPrevScreen.putExtra(Constants.CPGUID, mStrBundleCPGUID);
        intentNavPrevScreen.putExtra(Constants.CPGUID32, mStrBundleCPGUID32);
        intentNavPrevScreen.putExtra(Constants.BeatGUID, beatGUID);
        intentNavPrevScreen.putExtra(Constants.ParentId, parentId);
        startActivity(intentNavPrevScreen);
    }

    @Override
    public void conformationDialog(String message, int from) {
        UtilConstants.dialogBoxWithCallBack(SOCreateActivity.this, "", message, getString(R.string.ok), getString(R.string.cancel), false, new DialogCallBack() {
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
        inflater.inflate(R.menu.menu_so_create, menu);
        menu.removeItem(R.id.menu_save);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        mSearchView = (SearchView) menu.findItem(R.id.menu_search_item).getActionView();
        mSearchView.setMaxWidth(Integer.MAX_VALUE);
        View view = mSearchView.findViewById(androidx.appcompat.R.id.search_plate);
        view.setBackgroundColor(ContextCompat.getColor(this, R.color.transperant));
        SearchableInfo searchInfo = searchManager.getSearchableInfo(getComponentName());
        MenuItem dateFilter = menu.findItem(R.id.filter);
        if (TextUtils.isEmpty("")) {
            dateFilter.setVisible(true);
        } else {
            dateFilter.setVisible(false);
        }
        mSearchView.setSearchableInfo(searchInfo);
        mSearchView.setQueryHint(getString(R.string.lbl_cust_name_search));
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                createFilterFunc(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                createFilterFunc(newText);
                return false;
            }
        });

        return true;
    }

    private void createFilterFunc(String srchText) {
        this.searchText = srchText;
        checkAndCloseAllExpandedItem();
        presenter.onFragmentInteraction(brand, brandName, category, categoryName, creskuGrp, creskuGrpName, skuGroupBeanArrayListAllData, searchText);
//        createTable(alCRSSKUGrpList);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.menu_next:
                //  if(dmsDivisionBean!=null) {
                int qantity = 0;
                //   if (!dmsDivisionBean.getDisplayData().equalsIgnoreCase("None")) {
                if (skuGroupBeanArrayListAllData != null && skuGroupBeanArrayListAllData.size() > 0) {
                    for (SKUGroupBean skuGroupBean : skuGroupBeanArrayListAllData) {

//                                if (skuGroupBean.getSkuSubGroupBeanArrayList() != null && skuGroupBean.getSkuSubGroupBeanArrayList().size() > 0) {
//                                    for (SKUGroupBean subItem : skuGroupBean.getSkuSubGroupBeanArrayList()) {
                        if (Double.parseDouble(skuGroupBean.getEtQty().equalsIgnoreCase("")
                                ? "0" : skuGroupBean.getEtQty()) > 0) {
                            qantity = (int) Double.parseDouble(skuGroupBean.getEtQty());
                            break;
                        }
//                                    }
//                                }
                    }
                }
                if (qantity > 0) {
                        Constants.writeDebugLog(mContext,"SO Create after Validation to Review Page");
                    onReviewPage();
                } else {
                    Toast.makeText(SOCreateActivity.this, getResources().getString(R.string.quantity_select), Toast.LENGTH_SHORT).show();
                }
                  /*  } else {
                        spDMSDivision.setError(getResources().getString(R.string.division_select));
                    }*/
                // }
                return true;
            case R.id.filter:
                // if(!TextUtils.isEmpty(soCreateBean.getDivision())) {
                Bundle bundle = presenter.openFilter();
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
                if (prev != null) {
                    ft.remove(prev);
                }
                ft.addToBackStack(null);
                SOFilterDialogFragment soFilterDialogFragment = new SOFilterDialogFragment();
                bundle.putSerializable(Constants.EXTRA_SO_HEADER, soCreateBean);
                soFilterDialogFragment.setArguments(bundle);
                soFilterDialogFragment.show(ft, "dialog");
               /* }else {
                    spDMSDivision.setError(getResources().getString(R.string.division_select));
                }*/
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
//        return false;
    }

    @Override
    public void responseSuccess(ODataRequestExecution oDataRequestExecution, List<ODataEntity> list, Bundle bundle) {

    }

    @Override
    public void responseFailed(ODataRequestExecution oDataRequestExecution, String s, Bundle bundle) {

    }

    @Override
    public void onScrolled(int verticalScroll) {
        if (!isEditTextClicked) {
            if (keyboardView != null && verticalScroll > 0 && (verticalScroll > oldPercentage + 5 || verticalScroll < oldPercentage - 5)) {
                keyboardView.setVisibility(View.GONE);
                keyboardView.setEnabled(false);
                oldPercentage = verticalScroll;
            }
        } else {
            int getFirstRow = tableFixHeaders.getFirstRow();
            Log.d("percentageOfView", " getFirstRow:" + getFirstRow + " getFirstClickedPos :" + getFirstClickedPos);
            if ((getFirstRow > getFirstClickedPos + 1 || getFirstRow < getFirstClickedPos - 1)) {
                isEditTextClicked = false;
            }
        }
    }

    @Override
    public void onClickItem(Object o, Object o2, int row, int column, BaseTableAdapter baseTableAdapter, ImageView ivExpandIcon) {
        SKUGroupBean skuGroupBean = (SKUGroupBean) o;
        Constants.hideCustomKeyboard(keyboardView);
        if (skuGroupBean.isHeader()) {
            int imageDisplay = openCloseItem(skuGroupBean, row);
            if (imageDisplay == 1) {
                ivExpandIcon.setImageResource(R.drawable.up);
                expandPossList.add(row);
            } else if (imageDisplay == 2) {
                ivExpandIcon.setImageResource(R.drawable.down);
                expandPossList.remove((Object) row);
            }
            baseTableAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onTextChangeItem(final SKUGroupBean item, final int row, int column,
                                 final BaseTableAdapter tableFixHeaderAdapter,
                                 final EditText hEditText, OriginalCellViewGroup viewGroup) {
        isTyping = false;
        salesOrderAdapter = tableFixHeaderAdapter;
//        if (!item.getMatTypeVal().equalsIgnoreCase("")) {
//            UtilConstants.editTextDecimalFormatZeroAllow(hEditText, 9, 3);
//        } else {
        UtilConstants.editTextDecimalFormat(hEditText, maxLength, 3);
//        }

        hEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                item.setEtQty(s + "");

                if (isTyping) {
                    setSalesOrderItemData(s + "", item, alCRSSKUGrpList, row);
                    item.setItemTyping(true);
                    tableFixHeaderAdapter.notifyDataSetChanged();
                } else {

                }
                isTyping = true;
            }

            @Override
            public void afterTextChanged(Editable s) {

                try {
                    if (item.isHeader()) {
                        String s1 = s.toString();
                        if (!s1.equalsIgnoreCase("")) {
                            getTLSD(s1.toString(), item.getSKUGroup());
                        } else {
                            getTLSDRemoveSKU(s1, item.getSKUGroup());
                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        hEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Log.d("onFocusChange", "onFocusChange: " + hasFocus + " pos :" + row);
                if (hasFocus) {
                    hEditText.setHint("");
                    mEditTextSelected = hEditText;
                    lastSelectedEditTextRow = row;
                    item.setFocusHeaderText(true);
                    Constants.showCustomKeyboard(v, keyboardView, SOCreateActivity.this);
                } else {
                    hEditText.setHint(getString(R.string.qty));
                    Log.d("onFocusChange", "isFocusHeaderText: " + item.isFocusHeaderText() + " isItemTyping :" + item.isItemTyping() + " pos :" + row);
                    item.setFocusHeaderText(false);
                    if (!item.isFocusHeaderText() && !item.isItemTyping()) {
                        Constants.hideCustomKeyboard(keyboardView);
                    }
                }
            }
        });

        hEditText.setText(item.getEtQty());
        if (item.isFocusHeaderText() || item.isItemTyping()) {
            hEditText.setFocusable(true);
            hEditText.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(hEditText, InputMethodManager.SHOW_IMPLICIT);
            int pos = hEditText.getText().length();

            int itemTypePos = item.getSetCursorPos();
            if (itemTypePos >= 0) {
                if (pos > oldCurPos) {
                    item.setSetCursorPos(itemTypePos + 1);
                } else if (pos < oldCurPos) {
                    item.setSetCursorPos(itemTypePos - 1);
                }
                itemTypePos = item.getSetCursorPos();
                oldCurPos = pos;
                if (pos > itemTypePos && itemTypePos > 0) {
                    pos = itemTypePos;
                }
            } else {
                oldCurPos = pos;
            }
            if (pos == 0) {
                item.setSetCursorPos(-1);
            }
            hEditText.setSelection(pos);

            mEditTextSelected = hEditText;
            lastSelectedEditTextRow = row;
            Log.d("onFocusChange", "onTextChangeItem: isShown" + " pos :" + row);
        } else {
            item.setSetCursorPos(-1);
            hEditText.setFocusable(false);
            Log.d("onFocusChange", "onTextChangeItem: is not Shown" + " pos :" + row);
        }

        hEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
//                v.setFocusable(true);
//                v.requestFocus();
                hEditText.setFocusableInTouchMode(true);
                hEditText.setFocusable(true);
                hEditText.requestFocus();
                hEditText.setHint("");
                lastSelectedEditTextRow = row;
                mEditTextSelected = hEditText;
                getFirstClickedPos = tableFixHeaders.getFirstRow();
                Constants.showCustomKeyboard(v, keyboardView, SOCreateActivity.this);
                item.setFocusHeaderText(true);
                setCursorPostion(hEditText, v, event, item);
                isEditTextClicked = true;
//                tableFixHeaderAdapter.notifyDataSetChanged();
                return true;
            }
        });

        item.setItemTyping(false);
    }

    @Override
    public void onPress(int i) {
        Log.d(TAG, "onPress: ");
    }

    @Override
    public void onRelease(int i) {
        Log.d(TAG, "onRelease: ");
    }

    @Override
    public void onKey(int primaryCode, int[] keyCodes) {
        Log.d(TAG, "onKey: ");
        switch (primaryCode) {

            case 81:
                //Plus
                if (mEditTextSelected != null)
                    Constants.incrementTextValues(mEditTextSelected, Constants.N);
                break;
            case 69:
                //Minus
                if (mEditTextSelected != null)
                    Constants.decrementEditTextVal(mEditTextSelected, Constants.N);
                break;
            case 1:
                changeEditTextFocus(0);
                break;
            case 2:
                changeEditTextFocus(1);
                break;
            case 56:
                KeyEvent event = new KeyEvent(0, 0, KeyEvent.ACTION_DOWN, primaryCode, 0, 0, 0, 0, KeyEvent.FLAG_SOFT_KEYBOARD | KeyEvent.FLAG_KEEP_TOUCH_MODE);
                dispatchKeyEvent(event);

                break;

            default:
                KeyEvent event2 = new KeyEvent(0, 0, KeyEvent.ACTION_DOWN, primaryCode, 0, 0, 0, 0, KeyEvent.FLAG_SOFT_KEYBOARD | KeyEvent.FLAG_KEEP_TOUCH_MODE);
                dispatchKeyEvent(event2);
                break;
        }
    }

    @Override
    public void onText(CharSequence charSequence) {
        Log.d(TAG, "onText: ");
    }

    @Override
    public void swipeLeft() {
        Log.d(TAG, "swipeLeft: ");
    }

    @Override
    public void swipeRight() {
        Log.d(TAG, "swipeRight: ");
    }

    @Override
    public void swipeDown() {
        Log.d(TAG, "swipeDown: ");
    }

    @Override
    public void swipeUp() {
        Log.d(TAG, "swipeUp: ");
    }

    public void changeEditTextFocus(int upDownStatus) {

        if (upDownStatus == 1) {
            int ListSize = alCRSSKUGrpList.size() - 1;
            if (lastSelectedEditTextRow != ListSize) {
                int firstRow = tableFixHeaders.getFirstRow();
                int visibleHeight = tableFixHeaders.getRowHeights() + firstRow;
                int newItemPoss = lastSelectedEditTextRow + 1;
                if (newItemPoss < visibleHeight) {
                    SKUGroupBean skuGroupBean = alCRSSKUGrpList.get(newItemPoss);
                    skuGroupBean.setFocusHeaderText(true);
                    skuGroupBean.setSetCursorPos(skuGroupBean.getEtQty().length());
                    if (salesOrderAdapter != null) {
                        salesOrderAdapter.notifyDataSetChanged();
                    }
                    Constants.showCustomKeyboard(null, keyboardView, SOCreateActivity.this);
                }
            }
        } else {
            if (lastSelectedEditTextRow != 0) {
                int firstRow = tableFixHeaders.getFirstRow();
                if (firstRow <= lastSelectedEditTextRow - 1) {
                    int newItemPoss = lastSelectedEditTextRow - 1;
                    SKUGroupBean skuGroupBean = alCRSSKUGrpList.get(newItemPoss);
                    skuGroupBean.setFocusHeaderText(true);
                    skuGroupBean.setSetCursorPos(skuGroupBean.getEtQty().length());
                    if (salesOrderAdapter != null) {
                        salesOrderAdapter.notifyDataSetChanged();
                    }
                    Constants.showCustomKeyboard(null, keyboardView, SOCreateActivity.this);
                }
            }

        }

    }

    private void setSalesOrderItemData(String s, SKUGroupBean item, ArrayList<SKUGroupBean> skuGroupBeanArrayList, int row) {
        if (item.isHeader()) {
            if (!item.getSkuSubGroupBeanArrayList().isEmpty()) {

                int i = 0;
                for (SKUGroupBean subItem : item.getSkuSubGroupBeanArrayList()) {
                    if (i == 0) {
                        // set header qty
//                        if (Constants.Map_Must_Sell_Mat.containsKey(item.getSKUGroup()) /*&& !item.getMatTypeVal().equalsIgnoreCase("")*/) {  // commented on 19072017
//                            Constants.Map_Must_Sell_Mat.put(item.getSKUGroup(), s + "");
//                        }
                        Constants.MAPORDQtyByCrsSkuGrp.put(item.getSKUGroup(), s.toString());
                        subItem.setEtQty(s);
                    } else {
                        subItem.setEtQty("");
                    }
                    i++;
                }
               /* }else {

                }*/
            } else if (item.isViewOpened()) {
                ArrayList<SKUGroupBean> skuGroupBeanArrayListFinal = new ArrayList<>();
                boolean isStored = false;
                for (int i = row + 1; i < skuGroupBeanArrayList.size(); i++) {
                    SKUGroupBean skuGroupBean = skuGroupBeanArrayList.get(i);
                    if (!skuGroupBean.isHeader()) {
                        if (!isStored) {
                            // set header qty
//                            if (Constants.Map_Must_Sell_Mat.containsKey(skuGroupBean.getSKUGroup()) /*&& !skuGroupBean.getMatTypeVal().equalsIgnoreCase("")*/) { // commented on 19072017
//                                Constants.Map_Must_Sell_Mat.put(skuGroupBean.getSKUGroup(), s + "");
//                            }
                            Constants.MAPORDQtyByCrsSkuGrp.put(skuGroupBean.getSKUGroup(), s.toString());
                            skuGroupBean.setEtQty(s + "");
                            skuGroupBeanArrayListFinal.add(skuGroupBean);
                            isStored = true;
                        } else {
                            // set header qty
//                            if (Constants.Map_Must_Sell_Mat.containsKey(skuGroupBean.getSKUGroup()) && !skuGroupBean.getMatTypeVal().equalsIgnoreCase("")) {
//                                Constants.Map_Must_Sell_Mat.put(skuGroupBean.getSKUGroup(), "");
//                            }
                            Constants.MAPORDQtyByCrsSkuGrp.put(skuGroupBean.getSKUGroup(), s.toString());
                            skuGroupBean.setEtQty("");
                            skuGroupBeanArrayListFinal.add(skuGroupBean);
                        }
                    } else {
                        break;
                    }
                }
                if (!skuGroupBeanArrayListFinal.isEmpty()) {
                    skuGroupBeanArrayList.removeAll(skuGroupBeanArrayListFinal);
                    skuGroupBeanArrayList.addAll(row + 1, skuGroupBeanArrayListFinal);
                }
            }
        } else {
            setHeaderTotalValues(item, skuGroupBeanArrayList, row);
        }
    }

    private void setHeaderTotalValues(SKUGroupBean item, ArrayList<SKUGroupBean> skuGroupBeanArrayList, int row) {
        int totalListCount = skuGroupBeanArrayList.size();
        try {
            if (row == totalListCount - 1) {
                double totalValues = 0;
                for (int i = totalListCount - 1; i >= 0; i--) {
                    SKUGroupBean skuGroupBean = skuGroupBeanArrayList.get(i);
                    if (!skuGroupBean.isHeader()) {
                        if (!TextUtils.isEmpty(skuGroupBean.getEtQty()))
                            totalValues = totalValues + Double.parseDouble(skuGroupBean.getEtQty());
                    } else {
                        String totalString = totalValues + "";
                        // set header qty
//                        if (Constants.Map_Must_Sell_Mat.containsKey(skuGroupBean.getSKUGroup()) /*&& !skuGroupBean.getMatTypeVal().equalsIgnoreCase("")*/) {   // commented on 19072017
//                            Constants.Map_Must_Sell_Mat.put(skuGroupBean.getSKUGroup(), totalString.split("\\.")[0]);
//                        }
                        Constants.MAPORDQtyByCrsSkuGrp.put(skuGroupBean.getSKUGroup(), totalString.split("\\.")[0]);
                        skuGroupBean.setEtQty(totalString.split("\\.")[0]);
                        break;
                    }
                }
            } else {
                double totalValues = 0;
                for (int i = row + 1; i < totalListCount; i++) {
                    SKUGroupBean skuGroupBean = skuGroupBeanArrayList.get(i);
                    if (!skuGroupBean.isHeader()) {
                        if (!TextUtils.isEmpty(skuGroupBean.getEtQty()))
                            totalValues = totalValues + Double.parseDouble(skuGroupBean.getEtQty());
                    } else {
                        break;
                    }
                }
                for (int i = row; i >= 0; i--) {
                    SKUGroupBean skuGroupBean = skuGroupBeanArrayList.get(i);
                    if (!skuGroupBean.isHeader()) {
                        if (!TextUtils.isEmpty(skuGroupBean.getEtQty()))
                            totalValues = totalValues + Double.parseDouble(skuGroupBean.getEtQty());
                    } else {
                        String totalString = totalValues + "";
                        // set header qty
//                        if (Constants.Map_Must_Sell_Mat.containsKey(skuGroupBean.getSKUGroup()) /*&& !skuGroupBean.getMatTypeVal().equalsIgnoreCase("")*/) {  // commented on 19072017
//                            Constants.Map_Must_Sell_Mat.put(skuGroupBean.getSKUGroup(), totalString.split("\\.")[0]);
//                        }
                        Constants.MAPORDQtyByCrsSkuGrp.put(skuGroupBean.getSKUGroup(), totalString.split("\\.")[0]);
                        skuGroupBean.setEtQty(totalString.split("\\.")[0]);
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getTLSD(String s, String mStrSkuGrp) {
        try {
            if (!s.toString().equalsIgnoreCase("")) {
                mStrCrsSkuCount.add(mStrSkuGrp);
            }
//            tvTLSD.setText(UtilConstants.removeDecimalPoints(mStrCrsSkuCount.size() + ""));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getTLSDRemoveSKU(String s, String mStrSkuGrp) {
        try {
            mStrCrsSkuCount.remove(mStrSkuGrp);
//            tvTLSD.setText(UtilConstants.removeDecimalPoints(mStrCrsSkuCount.size() + ""));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int openCloseItem(SKUGroupBean skuGroupBean, int row) {
        row = alCRSSKUGrpList.indexOf(skuGroupBean);
        if (!skuGroupBean.isViewOpened()) {
            ArrayList<SKUGroupBean> skuGroupBeanArrayList = skuGroupBean.getSkuSubGroupBeanArrayList();

            if (!skuGroupBeanArrayList.isEmpty()) {
                alCRSSKUGrpList.addAll(row + 1, skuGroupBeanArrayList);
                skuGroupBean.getSkuSubGroupBeanArrayList().clear();
                skuGroupBean.setViewOpened(true);
                return 1;
            }

        } else {
            skuGroupBean.setSkuSubGroupBeanArrayList(getSkuSubItemGroup(row));
            skuGroupBean.setViewOpened(false);
            return 2;

        }
        return 0;
    }

    private ArrayList<SKUGroupBean> getSkuSubItemGroup(int startPos) {
        ArrayList<SKUGroupBean> skuGroupBeanArrayList = new ArrayList<>();
        for (int i = startPos + 1; i < alCRSSKUGrpList.size(); i++) {
            SKUGroupBean skuGroupBean = alCRSSKUGrpList.get(i);
            if (!skuGroupBean.isHeader()) {
                skuGroupBeanArrayList.add(skuGroupBean);
            } else {
                break;
            }
        }
        if (!skuGroupBeanArrayList.isEmpty()) {
            alCRSSKUGrpList.removeAll(skuGroupBeanArrayList);
        }
        return skuGroupBeanArrayList;
    }

    private void checkAndCloseAllExpandedItem() {
        if (!expandPossList.isEmpty()) {
               /* Collections.reverse(expandPossList);
                for (int pos : expandPossList) {
                    SKUGroupBean lastOpenedBean = alCRSSKUGrpList.get(pos);
                    int imageDisplay = openCloseItem(lastOpenedBean, pos);
                    if (imageDisplay == 1) {
                        lastOpenedBean.setViewOpened(true);
                    } else if (imageDisplay == 2) {
                        lastOpenedBean.setViewOpened(false);
                    }
                }*/
            closeOtherItems();
            expandPossList.clear();
        }
    }

    private void closeOtherItems() {
        for (int pos = 0; pos < alCRSSKUGrpList.size(); pos++) {
            SKUGroupBean skuGroupBean = alCRSSKUGrpList.get(pos);
            if (skuGroupBean.isHeader() && skuGroupBean.isViewOpened()) {
                skuGroupBean.setSkuSubGroupBeanArrayList(getSkuSubItemGroup(pos));
                skuGroupBean.setViewOpened(false);
            }
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
      /*  if (keyCode == KeyEvent.KEYCODE_BACK) {
          onBackPressed();
          return false;
        }
        return true;*/
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public void onBackPressed() {

//        super.onBackPressed();
       /* if (!mSearchView.isIconified()) {
//            mSearchView.setIconified(true);
            onBackPressConfirm();
        } else {*/
        if (isCustomKeyboardVisible()) {
            Constants.hideCustomKeyboard(keyboardView);
        } else {
            onBackPressConfirm();
        }
//        }
    }

    private void onBackPressConfirm() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SOCreateActivity.this, R.style.MyTheme);
        builder.setMessage(R.string.alert_exit_create_so).setCancelable(false)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        onNavigateToRetDetilsActivity();
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }

                });
        builder.show();
    }

    public boolean isCustomKeyboardVisible() {
        return keyboardView.getVisibility() == View.VISIBLE;
    }

    private void onNavigateToRetDetilsActivity() {
//        Constants.ComingFromCreateSenarios = Constants.X;
        Intent intentNavPrevScreen = new Intent(SOCreateActivity.this, CustomerDetailsActivity.class);
        intentNavPrevScreen.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intentNavPrevScreen.putExtra(Constants.CPNo, mStrBundleRetID);
        intentNavPrevScreen.putExtra(Constants.RetailerName, mStrBundleRetName);
        intentNavPrevScreen.putExtra(Constants.CPUID, mStrBundleRetailerUID);
        intentNavPrevScreen.putExtra(Constants.comingFrom, mStrComingFrom);
        intentNavPrevScreen.putExtra(Constants.CPGUID, mStrBundleCPGUID);
        intentNavPrevScreen.putExtra(Constants.BeatGUID, beatGUID);
        intentNavPrevScreen.putExtra(Constants.ParentId, parentId);
//        if (!Constants.OtherRouteNameVal.equalsIgnoreCase("")) {
//            intentNavPrevScreen.putExtra(Constants.OtherRouteGUID, Constants.OtherRouteGUIDVal);
//            intentNavPrevScreen.putExtra(Constants.OtherRouteName, Constants.OtherRouteNameVal);
//        }
        startActivity(intentNavPrevScreen);
    }

    public void hideCustomKeyboard() {
        keyboardView.setVisibility(View.GONE);
        keyboardView.setEnabled(false);
    }

    private void onReviewPage() {
//        Constants.hideCustomKeyboard(keyboardView);
        hideCustomKeyboard();
        checkAndCloseAllExpandedItem();
        createTable(alCRSSKUGrpList);
        Intent intentSOCreate = new Intent(SOCreateActivity.this,
                SOReviewActivity.class);
        Constants.selectedSOItems = skuGroupBeanArrayListAllData;
        intentSOCreate.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intentSOCreate.putExtra(Constants.CPNo, mStrBundleRetID);
        intentSOCreate.putExtra(Constants.CPUID, mStrBundleRetailerUID);
        intentSOCreate.putExtra(Constants.RetailerName, mStrBundleRetName);
        intentSOCreate.putExtra(Constants.CPGUID, mStrBundleCPGUID.toUpperCase());
        intentSOCreate.putExtra(Constants.CPGUID32, mStrBundleCPGUID32.toUpperCase());
        intentSOCreate.putExtra(Constants.comingFrom, mStrComingFrom);
        intentSOCreate.putExtra(Constants.BeatGUID, beatGUID);
        intentSOCreate.putExtra(Constants.ParentId, parentId);
        intentSOCreate.putExtra(Constants.EXTRA_SO_HEADER, soCreateBean);
        startActivity(intentSOCreate);

    }

    @Override
    public void onFragmentFilterInteraction(String brand, String brandName, String category, String categoryName, String creskuGrp, String creskuGrpName) {

        Constants.hideCustomKeyboard(keyboardView);
        checkAndCloseAllExpandedItem();
        this.brand = brand;
        this.brandName = brandName;
        this.category = category;
        this.categoryName = categoryName;
        this.creskuGrp = creskuGrp;
        this.creskuGrpName = creskuGrpName;

        presenter.onFragmentInteraction(brand, brandName, category, categoryName, creskuGrp, creskuGrpName, skuGroupBeanArrayListAllData, searchText);
    }

    @Override
    public void onTextChangeItem(final SKUGroupBean skuGroupBean, int row, int column,
                                 final BaseTableAdapter baseTableAdapter, Spinner spView,
                                 OriginalCellViewGroup viewGroup) {
//        salesOrderAdapter = baseTableAdapter;
        if (skuGroupBean.isHeader()) {
           /* ArrayAdapter<String> adapterSpinnerUOM = new ArrayAdapter<String>(this, R.layout.custom_textview_spinner, skuGroupBean.getAlUOM());
            adapterSpinnerUOM.setDropDownViewResource(R.layout.spinnerinside_text);
            spView.setAdapter(adapterSpinnerUOM);
            spView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
                    skuGroupBean.setSelectedUOM(skuGroupBean.getAlUOM().get(position));
//                    baseTableAdapter.notifyDataSetChanged();
                }

                @Override
                public void onNothingSelected(AdapterView<?> arg0) {
                }
            });*/
        }
    }

}
