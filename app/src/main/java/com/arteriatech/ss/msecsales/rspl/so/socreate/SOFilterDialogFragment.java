package com.arteriatech.ss.msecsales.rspl.so.socreate;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;

import com.arteriatech.ss.msecsales.rspl.R;
import com.arteriatech.ss.msecsales.rspl.common.Constants;
import com.arteriatech.ss.msecsales.rspl.common.ConstantsUtils;
import com.arteriatech.ss.msecsales.rspl.dbstock.stockmaterial.DMSDivisionBean;
import com.arteriatech.ss.msecsales.rspl.mbo.DmsDivQryBean;
import com.arteriatech.ss.msecsales.rspl.mbo.SKUGroupBean;
import com.arteriatech.ss.msecsales.rspl.mbo.SOCreateBean;
import com.arteriatech.ss.msecsales.rspl.so.SOUtils;
import com.arteriatech.ss.msecsales.rspl.ui.MaterialDesignSpinner;

import java.util.ArrayList;
import java.util.List;

public class SOFilterDialogFragment extends DialogFragment implements SOCreateView, View.OnClickListener {

    public static final String KEY_DIVISION = "divisionKey";
    public static final String KEY_BRAND = "brandKey";
    public static final String KEY_CATEGORY = "categoryKey";
    public static final String KEY_SKUGRP = "skuGrpKey";
    public static final String KEY_MUSTSELL = "MUSTSELL";
    private OnFragmentFilterListener mListener;
    private MaterialDesignSpinner spCategory, spMustSell;
    private MaterialDesignSpinner spBrand;
//    private MaterialDesignSpinner spCRSSKU;
    private SOCreateFilterPresenterImpl presenter;
    private String stockType = "";
    private String strBrandId = "";
    private String strBrandName = "";
    private String strMustSellID = "";
    private String strMustSellDesc = "";
    private String strDivisionId = "";
    private String strDivisionQry = "";
    private String strDivisionName = "";
    private String strCategoryId = "";
    private String strCategoryName = "";
    private String strMustSelType = "";
    private String strMustSelTypeDesc = "";
    private DMSDivisionBean dmsDivisionBean = null;
    private Button btApply;
//    private String strOldMustSell = "";
    private String strOldBrand = "";
    private String strOldDivision = "";
    private String dividionID = "";
    private List<String> dividionIDS = new ArrayList<>();
    private String strOldCategory = "",strOldMustSellID="";
    private String strOldSkuGrp = "";
    private Button btCancel;

    private boolean isCatFirstTime=true;
    private boolean isBrandFirstTime=true;
    private boolean isCRSSKUFirstTime=true;
    private Button btClear;
    private DmsDivQryBean dmsDivQryBean=null;
    SOCreateBean soCreateBean=null;
    // Below hard code values
    String[][] skuType = {{"", "01"}, {"All", "Must Sell"}};
//    private SwipeRefreshLayout swipeRefresh;

    public SOFilterDialogFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return inflater.inflate(R.layout.fragment_so_filter, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        spMustSell = (MaterialDesignSpinner) view.findViewById(R.id.spMustSell);
        spCategory = (MaterialDesignSpinner) view.findViewById(R.id.spCategory);
        spBrand = (MaterialDesignSpinner) view.findViewById(R.id.spBrand);

//        spCRSSKU = (MaterialDesignSpinner) view.findViewById(R.id.spCRSSKU);
//        swipeRefresh = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefresh);
//        ConstantsUtils.setProgressColor(getContext(), swipeRefresh);
        btApply = (Button) view.findViewById(R.id.btApply);
        btCancel = (Button) view.findViewById(R.id.btCancel);
        btClear = (Button) view.findViewById(R.id.btClear);
        btApply.setOnClickListener(this);
        btCancel.setOnClickListener(this);
        btClear.setOnClickListener(this);
        Bundle b = getArguments();
        soCreateBean =(SOCreateBean) b.getSerializable(Constants.EXTRA_SO_HEADER);
        if(soCreateBean==null){
            soCreateBean = new SOCreateBean();
        }
        dmsDivQryBean =soCreateBean.getDmsDivQryBean();
        //dividionID =soCreateBean.getDivision();
        dividionIDS =soCreateBean.getDivisionIds();
        presenter = new SOCreateFilterPresenterImpl(getContext(), this);
        displayMustSells(skuType);
        presenter.onStart(dmsDivQryBean,"");
//        presenter.loadDistributor();
    }

    public void onSubmitData() {
        if (mListener != null) {
            mListener.onFragmentFilterInteraction(strBrandId, strBrandName, strCategoryId, strCategoryName, strMustSellID, strMustSellDesc);
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
            strOldMustSellID = bundle.getString(KEY_MUSTSELL, "");
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
                strOldCategory="";
                strOldDivision="";
                strOldBrand="";
                strOldSkuGrp="";
                strBrandId="";
                strMustSellID="";
                strOldMustSellID="";
                strCategoryId="";
                displayMustSells(skuType);
                presenter.onStart(dmsDivQryBean,strBrandId);
                break;
        }
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

    }

    @Override
    public void displaySO(ArrayList<SKUGroupBean> alCRSSKUGrpList) {

    }

    @Override
    public void searchResult(ArrayList<SKUGroupBean> skuSearchList) {

    }

    @Override
    public void displayCat(final String[][] strCats) {
        ArrayAdapter<String> adapterShipToList = new ArrayAdapter<String>(getContext(), R.layout.custom_textview,
                R.id.tvItemValue, strCats[1]) {
            @Override
            public View getDropDownView(final int position, final View convertView, final ViewGroup parent) {
                final View v = super.getDropDownView(position, convertView, parent);
                ConstantsUtils.selectedView(v, spCategory, position, getContext());
                return v;
            }
        };
        adapterShipToList.setDropDownViewResource(R.layout.spinnerinside);
        if (!TextUtils.isEmpty(strOldCategory) && !strOldCategory.equalsIgnoreCase(Constants.None)) {
            spCategory.setSelection(SOUtils.getPoss(strCats, strOldCategory, 0));
        }
        spCategory.setAdapter(adapterShipToList);
        spCategory.showFloatingLabel();
        spCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                strCategoryId = strCats[0][position];
                strCategoryName = strCats[1][position];
                presenter.getBrandList(strCategoryId,dividionIDS);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    @Override
    public void displayBrands(final String[][] arrBrand) {
        ArrayAdapter<String> adapterShipToList = new ArrayAdapter<String>(getContext(), R.layout.custom_textview,
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
        }
        spBrand.setAdapter(adapterShipToList);
        spBrand.showFloatingLabel();
        spBrand.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                strBrandId = arrBrand[0][position];
                strBrandName = arrBrand[1][position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    @Override
    public void displayMustSells(final String[][] strMustSells) {
        ArrayAdapter<String> adapterShipToList = new ArrayAdapter<String>(getContext(), R.layout.custom_textview,
                R.id.tvItemValue, strMustSells[1]) {
            @Override
            public View getDropDownView(final int position, final View convertView, final ViewGroup parent) {
                final View v = super.getDropDownView(position, convertView, parent);
                ConstantsUtils.selectedView(v, spMustSell, position, getContext());
                return v;
            }
        };
        adapterShipToList.setDropDownViewResource(R.layout.spinnerinside);
        if (!TextUtils.isEmpty(strOldMustSellID) && !strOldMustSellID.equalsIgnoreCase(Constants.None)) {
            spMustSell.setSelection(SOUtils.getPoss(strMustSells, strOldMustSellID, 0));
        }
        spMustSell.setAdapter(adapterShipToList);
        spMustSell.showFloatingLabel();
        spMustSell.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                strMustSellID = strMustSells[0][position];
                strMustSellDesc = strMustSells[1][position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    @Override
    public void displayDMSDivision(ArrayList<com.arteriatech.ss.msecsales.rspl.mbo.DMSDivisionBean> alDMSDiv) {

    }

    @Override
    public void showProgressDialog() {

    }

    @Override
    public void setFilterDate(String filterType) {

    }

    public interface OnFragmentFilterListener {
        void onFragmentFilterInteraction(String brand, String brandName, String category, String categoryName,String filterType, String filterTypeName);
    }
}
