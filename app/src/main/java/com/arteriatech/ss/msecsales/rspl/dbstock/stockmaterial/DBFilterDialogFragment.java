package com.arteriatech.ss.msecsales.rspl.dbstock.stockmaterial;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;

import com.arteriatech.ss.msecsales.rspl.R;
import com.arteriatech.ss.msecsales.rspl.common.Constants;
import com.arteriatech.ss.msecsales.rspl.common.ConstantsUtils;
import com.arteriatech.ss.msecsales.rspl.mbo.BrandBean;
import com.arteriatech.ss.msecsales.rspl.mbo.SKUGroupBean;
import com.arteriatech.ss.msecsales.rspl.so.SOUtils;
import com.arteriatech.ss.msecsales.rspl.ui.CustomAutoComplete;
import com.arteriatech.ss.msecsales.rspl.ui.MaterialDesignSpinner;

import java.util.ArrayList;

public class DBFilterDialogFragment extends DialogFragment implements IDealerStockListViewPresenter, View.OnClickListener {

    public static final String KEY_DIVISION = "divisionKey";
    public static final String KEY_BRAND = "brandKey";
    public static final String KEY_CATEGORY = "categoryKey";
    public static final String KEY_SKUGRP = "skuGrpKey";
    public static final String KEY_MUSTSELL = "MUSTSELL";
    private static String TAG = DBFilterDialogFragment.class.getSimpleName();
    private OnFragmentFilterListener mListener;
    private MaterialDesignSpinner spCategory, spDistributor;
    private MaterialDesignSpinner spBrand;
    private MaterialDesignSpinner spCRSSKU;
    private DealerStockListPresenter presenter;
    private String stockType = "";
    private String strBrandId = "";
    private String strBrandName = "";
    private String strDivisionId = "";
    private String strDivisionQry = "";
    private String strDivisionName = "";
    private String strCategoryId = "";
    private String strCategoryName = "";
    private String strSKUGrpId = "";
    private String strSKUGrpName = "";
    private DMSDivisionBean dmsDivisionBean = null;
    private Button btApply;
    private String strOldBrand = "";
    private String strOldDivision = "";
    private String strOldCategory = "";
    private String strOldSkuGrp = "";
    private Button btCancel;
    private boolean isDivFirstTime = true;
    private boolean isCatFirstTime = true;
    private boolean isBrandFirstTime = false;
    private boolean isCRSSKUFirstTime = true;
    private Button btClear;
    private CustomAutoComplete autBrand;
    private boolean isDBStockCategoryDisplay = false;
//    private SwipeRefreshLayout swipeRefresh;

    public DBFilterDialogFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return inflater.inflate(R.layout.fragment_dbfilter_dialog, container, false);
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        spDistributor = (MaterialDesignSpinner) view.findViewById(R.id.spDistributor);
        spCategory = (MaterialDesignSpinner) view.findViewById(R.id.spCategory);
        spBrand = (MaterialDesignSpinner) view.findViewById(R.id.spBrand);
        spCRSSKU = (MaterialDesignSpinner) view.findViewById(R.id.spCRSSKU);
        autBrand = (CustomAutoComplete) view.findViewById(R.id.autBrand);
//        swipeRefresh = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefresh);
//        ConstantsUtils.setProgressColor(getContext(), swipeRefresh);
        btApply = (Button) view.findViewById(R.id.btApply);
        btCancel = (Button) view.findViewById(R.id.btCancel);
        btClear = (Button) view.findViewById(R.id.btClear);
        btApply.setOnClickListener(this);
        btCancel.setOnClickListener(this);
        btClear.setOnClickListener(this);
       /* view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        try {
                            if (view != null) {
                                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(getContext().INPUT_METHOD_SERVICE);
                                if (!imm.hideSoftInputFromWindow(autBrand.getWindowToken(), WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)) {
                                    //call dialog dismiss code here
                                    Log.d(TAG, "keyboard showing");
                                    return true;
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }
                return false;
            }
        });*/
       /* view.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {

                }
                return false;
            }
        });*/
        isDBStockCategoryDisplay = Constants.isDBStockCategoryDisplay(getContext());
        spCRSSKU.setFloatingLabelText(Constants.getTypesetValueForSkugrp(getContext()));
        if (isDBStockCategoryDisplay) {
            spCategory.setVisibility(View.VISIBLE);
        } else {
            spCategory.setVisibility(View.GONE);
        }
        presenter = new DealerStockListPresenter(getContext(), getActivity(), this);
        presenter.loadDistributor();
    }

    public void onSubmitData() {
        if (mListener != null) {
            mListener.onFragmentFilterInteraction(dmsDivisionBean, strDivisionId, strDivisionName, strBrandId, strBrandName, strCategoryId, strCategoryName, strSKUGrpId, strSKUGrpName);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);

        // request a window without the title
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            strOldBrand = bundle.getString(KEY_BRAND, "");
            strOldDivision = bundle.getString(KEY_DIVISION, "");
            strOldCategory = bundle.getString(KEY_CATEGORY, "");
            strOldSkuGrp = bundle.getString(KEY_SKUGRP, "");
        }

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentFilterListener) {
            mListener = (OnFragmentFilterListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void initializeUI() {

    }

    @Override
    public void initializeClickListeners() {

    }

    @Override
    public void initializeObjects() {

    }

    @Override
    public void initializeRecyclerViewAdapter(LinearLayoutManager layoutManager) {

    }

    @Override
    public void showProgressDialog() {
//        swipeRefresh.setRefreshing(true);
    }

    @Override
    public void hideProgressDialog() {
//        swipeRefresh.setRefreshing(false);
    }

    @Override
    public void showMessage(String message) {

    }

    @Override
    public void refreshAdapter(ArrayList<?> arrayList, String stockType) {

    }

    @Override
    public void displayRefreshTime(String refreshTime) {

    }

    @Override
    public void loadIntentData(Intent intent) {

    }

    @Override
    public void searchResult(ArrayList<DBStockBean> searchBeanArrayList) {

    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void brandList(final ArrayList<BrandBean> arrBrand) {
       /* ArrayAdapter<String> adapterShipToList = new ArrayAdapter<String>(getContext(), R.layout.custom_textview,
                R.id.tvItemValue, arrBrand[1]) {
            @Override
            public View getDropDownView(final int position, final View convertView, final ViewGroup parent) {
                final View v = super.getDropDownView(position, convertView, parent);
                ConstantsUtils.selectedView(v, spBrand, position, getContext());
                return v;
            }
        };
        adapterShipToList.setDropDownViewResource(R.layout.spinnerinside);
        if (!TextUtils.isEmpty(strOldBrand) && !strOldBrand.equalsIgnoreCase(Constants.None)) {
            spBrand.setSelection(SOUtils.getPoss(arrBrand, strOldBrand, 0));
        } else if (!TextUtils.isEmpty(strBrandId) && !strBrandId.equalsIgnoreCase(Constants.None)) {
            spBrand.setSelection(SOUtils.getPoss(arrBrand, strBrandId, 0));
        }
        spBrand.setAdapter(adapterShipToList);
//        spBrand.showFloatingLabel();
        spBrand.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                strBrandId = arrBrand[0][position];
                strBrandName = arrBrand[1][position];
                if (isBrandFirstTime) {
                    isBrandFirstTime = false;
                } else {
                    presenter.loadCategory(strDivisionQry, strBrandId);
                }
                presenter.loadCRSSKU(strDivisionQry, strBrandId, strCategoryId);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });*/
        final ArrayAdapter<BrandBean> adapter = new ArrayAdapter<BrandBean>(getContext(),
                android.R.layout.simple_dropdown_item_1line, arrBrand);
        autBrand.setThreshold(1);
        autBrand.setAdapter(adapter);
//        autBrand.showDropDown();
        autBrand.setOnTouchListener(new View.OnTouchListener() {

            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View paramView, MotionEvent paramMotionEvent) {
                if (arrBrand.size() > 0) {
                    // show all suggestions
                    if (!autBrand.getText().toString().equals(""))
                        adapter.getFilter().filter(null);
                    autBrand.showDropDown();
                }
                return false;
            }
        });
        /*else if (!TextUtils.isEmpty(strBrandId) && !strBrandId.equalsIgnoreCase(Constants.None)) {
            spBrand.setSelection(SOUtils.getPoss(arrBrand, strBrandId, 0));
        }*/
        autBrand.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long rowId) {
                strBrandId = ((BrandBean) parent.getItemAtPosition(position)).getBrandID();
                strBrandName = ((BrandBean) parent.getItemAtPosition(position)).getBrandDesc();
                if (isBrandFirstTime) {
                    isBrandFirstTime = false;
                } else {
                    presenter.loadCategory(strDivisionQry, strBrandId);
                }
                presenter.loadCRSSKU(strDivisionQry, strBrandId, strCategoryId);

            }
        });
        if (!TextUtils.isEmpty(strOldBrand) && !strOldBrand.equalsIgnoreCase(Constants.None)) {
            int getPOs = SOUtils.getBrandPos(arrBrand, strOldBrand);
            if (getPOs > -1) {
                {
                    autBrand.setText(arrBrand.get(getPOs).getBrandDesc());
                    strBrandId = arrBrand.get(getPOs).getBrandID();
                    strBrandName = arrBrand.get(getPOs).getBrandDesc();
                    if (isBrandFirstTime) {
                        isBrandFirstTime = false;
                    } else {
                        presenter.loadCategory(strDivisionQry, strBrandId);
                    }
                    presenter.loadCRSSKU(strDivisionQry, strBrandId, strCategoryId);
                }
            }
        }
    }

    @Override
    public void divisionList(final ArrayList<DMSDivisionBean> finalDistListDms) {
//        if (!finalDistListDms.isEmpty()) {
           /* if (finalDistListDms.size()==1){
                spDistributor.setVisibility(View.GONE);
                strDivisionId = finalDistListDms.get(0).getDistributorId();
                strDivisionQry = finalDistListDms.get(0).getDMSDivisionQuery();
                dmsDivisionBean = finalDistListDms.get(0);
                strDivisionName = finalDistListDms.get(0).getDistributorName();
                presenter.loadBrands(strDivisionQry, finalDistListDms.get(0));
            }else {*/
//            spDistributor.setVisibility(View.VISIBLE);
//            }
//        }
        ArrayAdapter<DMSDivisionBean> adapterShipToList = new ArrayAdapter<DMSDivisionBean>(getContext(), R.layout.custom_textview,
                R.id.tvItemValue, finalDistListDms) {
            @Override
            public View getDropDownView(final int position, final View convertView, final ViewGroup parent) {
                final View v = super.getDropDownView(position, convertView, parent);
                ConstantsUtils.selectedView(v, spDistributor, position, getContext());
                return v;
            }
        };
        adapterShipToList.setDropDownViewResource(R.layout.spinnerinside);
        if (!TextUtils.isEmpty(strOldDivision) && !strOldDivision.equalsIgnoreCase(Constants.None)) {
            spDistributor.setSelection(SOUtils.getDivisionPos(finalDistListDms, strOldDivision));
        }
        spDistributor.setAdapter(adapterShipToList);
        spDistributor.showFloatingLabel();
        spDistributor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                strDivisionId = finalDistListDms.get(position).getDistributorId();
                strDivisionQry = finalDistListDms.get(position).getDMSDivisionQuery();
                dmsDivisionBean = finalDistListDms.get(position);
                strDivisionName = finalDistListDms.get(position).getDistributorName();
                if (isDivFirstTime){
                    isDivFirstTime=false;
                }else {
                    presenter.loadBrands(strDivisionQry, dmsDivisionBean, strBrandId, strCategoryId);
                    if (isDBStockCategoryDisplay) {
                        presenter.loadCategory(strDivisionQry, strBrandId);
                    }
                    presenter.loadCRSSKU(strDivisionQry, strBrandId, strCategoryId);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        if (isDivFirstTime){
            if (!finalDistListDms.isEmpty()) {
                strDivisionId = finalDistListDms.get(0).getDistributorId();
                strDivisionQry = finalDistListDms.get(0).getDMSDivisionQuery();
                dmsDivisionBean = finalDistListDms.get(0);
                strDivisionName = finalDistListDms.get(0).getDistributorName();
                presenter.loadBrands(strDivisionQry, dmsDivisionBean, strBrandId, strCategoryId);
                if (isDBStockCategoryDisplay) {
                    presenter.loadCategory(strDivisionQry, strBrandId);
                }
                presenter.loadCRSSKU(strDivisionQry, strBrandId, strCategoryId);
            }
        }

    }

    @Override
    public void categoryList(final String[][] arrCategory) {
        ArrayAdapter<String> adapterShipToList = new ArrayAdapter<String>(getContext(), R.layout.custom_textview,
                R.id.tvItemValue, arrCategory[1]) {
            @Override
            public View getDropDownView(final int position, final View convertView, final ViewGroup parent) {
                final View v = super.getDropDownView(position, convertView, parent);
                ConstantsUtils.selectedView(v, spCategory, position, getContext());
                return v;
            }
        };
        adapterShipToList.setDropDownViewResource(R.layout.spinnerinside);
        if (!TextUtils.isEmpty(strOldCategory) && !strOldCategory.equalsIgnoreCase(Constants.None)) {
            spCategory.setSelection(SOUtils.getPoss(arrCategory, strOldCategory, 0));
        } else if (!TextUtils.isEmpty(strCategoryId) && !strCategoryId.equalsIgnoreCase(Constants.None)) {
            spCategory.setSelection(SOUtils.getPoss(arrCategory, strCategoryId, 0));
        }
        spCategory.setAdapter(adapterShipToList);
        spCategory.showFloatingLabel();
        spCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                strCategoryId = arrCategory[0][position];
                strCategoryName = arrCategory[1][position];
                if (isCatFirstTime) {
                    isCatFirstTime = false;
                } else if (strCategoryId.equalsIgnoreCase(Constants.None) && strBrandId.equalsIgnoreCase(Constants.None)) {
                    isBrandFirstTime = true;
                    presenter.loadBrands(strDivisionQry, dmsDivisionBean, strBrandId, strCategoryId);
                } else {
                    if (strCategoryId.equalsIgnoreCase(Constants.None)) {

                    } else {
//                        isBrandFirstTime = true;
//                        presenter.loadBrands(strDivisionQry, dmsDivisionBean, strBrandId, strCategoryId);
                    }

                }
                presenter.loadCRSSKU(strDivisionQry, strBrandId, strCategoryId);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    @Override
    public void crsSKUList(final ArrayList<SKUGroupBean> arrCrsSku) {
        ArrayAdapter<SKUGroupBean> adapterShipToList = new ArrayAdapter<SKUGroupBean>(getContext(), R.layout.custom_textview,
                R.id.tvItemValue, arrCrsSku) {
            @Override
            public View getDropDownView(final int position, final View convertView, final ViewGroup parent) {
                final View v = super.getDropDownView(position, convertView, parent);
                ConstantsUtils.selectedView(v, spCRSSKU, position, getContext());
                return v;
            }
        };
        adapterShipToList.setDropDownViewResource(R.layout.spinnerinside);
        if (!TextUtils.isEmpty(strOldSkuGrp) && !strOldSkuGrp.equalsIgnoreCase(Constants.None)) {
            int selPos = SOUtils.getOrderMatPos(arrCrsSku, strOldSkuGrp);
            if (arrCrsSku.size()>selPos) {
                spCRSSKU.setSelection(selPos);
            }
        }
        spCRSSKU.setAdapter(adapterShipToList);
        spCRSSKU.showFloatingLabel();
        spCRSSKU.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (arrCrsSku.size()>position) {
                    strSKUGrpId = arrCrsSku.get(position).getOrderMaterialGroupID();
                    strSKUGrpName = arrCrsSku.get(position).getOrderMaterialGroupDesc();
                }
                if (isCRSSKUFirstTime) {
                    isCRSSKUFirstTime = false;
                } else {
                    strOldCategory = "";
                    strOldDivision = "";
                    strOldBrand = "";
                    strOldSkuGrp = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    @Override
    public void setFilterDate(String filterType) {

    }

    @Override
    public void onDestroyView() {
        presenter.onDestroy();
        super.onDestroyView();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btApply:
                onSubmitData();
                getDialog().dismiss();
                break;
            case R.id.btCancel:
                getDialog().dismiss();
                break;
            case R.id.btClear:
                strOldCategory = "";
                strOldDivision = "";
                strOldBrand = "";
                strOldSkuGrp = "";
                strBrandId = "";
                strBrandName = "";
                strCategoryId = "";
                strCategoryName = "";
                autBrand.setText("");
                presenter.loadDistributor();
                break;
        }
    }

    public interface OnFragmentFilterListener {
        void onFragmentFilterInteraction(DMSDivisionBean dmsDivisionBean, String distributor, String divisionName, String brand, String brandName, String category, String categoryName, String creskuGrp, String creskuGrpName);
    }
}
