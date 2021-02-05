package com.arteriatech.ss.msecsales.rspl.sampledisbursement.addsampledisbursement;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;

import com.arteriatech.ss.msecsales.rspl.R;
import com.arteriatech.ss.msecsales.rspl.common.Constants;
import com.arteriatech.ss.msecsales.rspl.common.ConstantsUtils;
import com.arteriatech.ss.msecsales.rspl.mbo.BrandBean;
import com.arteriatech.ss.msecsales.rspl.so.SOUtils;
import com.arteriatech.ss.msecsales.rspl.ui.CustomAutoComplete;
import com.arteriatech.ss.msecsales.rspl.ui.MaterialDesignSpinner;

import java.util.ArrayList;

public class AddSampleDisbursermentDialogFragment extends DialogFragment implements View.OnClickListener,AddSampleDisbursementDialogFragmentView {

    private MaterialDesignSpinner spCategory, spDistributor;
    private MaterialDesignSpinner spBrand;
    private MaterialDesignSpinner spCRSSKU;
    private Button btCancel;
    private Button btClear;
    private Button btApply;
    private CustomAutoComplete autBrand;
    private ArrayAdapter<String> brandAdapter = null;
    private String previousBrandId = "";
    AddSampleDisbursementModel addSampleDisbursementModel;
    AddSampleDisbursementDialogFragmentPresenterImpl addSampleDisbursementDialogFragmentPresenter;
    private String mStrSelOrderMaterialID = "";
    private String mStrSelOrderMaterialDescription = "";
    AddSampleDisbursermentDialogFragmentListener addSampleDisbursermentDialogFragmentListener;
    private String previousBrandDescription="";
    Bundle bundle;
    String oldBrandName ="";
    String divisionID ="";
    String oldBrandID ="";
    String oldOrderMaterialType;
    String oldOrderMaterialTypeID;
    private boolean isBrandFirstTime = false;
    private boolean isCatFirstTime = true;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_sampledisbursement_filter_dialog, container, false);
        return view;
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        spCategory = (MaterialDesignSpinner) view.findViewById(R.id.spCategory);
        spBrand = (MaterialDesignSpinner) view.findViewById(R.id.spBrand);
        spCRSSKU = (MaterialDesignSpinner) view.findViewById(R.id.spCRSSKU);
        autBrand = (CustomAutoComplete) view.findViewById(R.id.autBrand);
        btApply = (Button) view.findViewById(R.id.btApply);
        btCancel = (Button) view.findViewById(R.id.btCancel);
        btClear = (Button) view.findViewById(R.id.btClear);
        btApply.setOnClickListener(this);
        btCancel.setOnClickListener(this);
        btClear.setOnClickListener(this);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            divisionID = bundle.getString(Constants.DivisionID,"");
        }
        addSampleDisbursementModel = new AddSampleDisbursementModel();
        addSampleDisbursementDialogFragmentPresenter = new AddSampleDisbursementDialogFragmentPresenterImpl(AddSampleDisbursermentDialogFragment.this);
        addSampleDisbursementDialogFragmentPresenter.getBrandList(divisionID);
       // addSampleDisbursementDialogFragmentPresenter.updateMaterialGroup(addSampleDisbursementModel);

        //isDBStockCategoryDisplay = Constants.isDBStockCategoryDisplay(getContext());
        // spCRSSKU.setFloatingLabelText(Constants.getTypesetValueForSkugrp(getContext()));
        // if (isDBStockCategoryDisplay) {
        //     spCategory.setVisibility(View.VISIBLE);
        //  } else {
        //       spCategory.setVisibility(View.GONE);
        // }
        // presenter = new DealerStockListPresenter(getContext(), getActivity(), this);
        // presenter.loadDistributor();
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof AddSampleDisbursermentDialogFragmentListener) {
            addSampleDisbursermentDialogFragmentListener = (AddSampleDisbursermentDialogFragmentListener) context;
        }
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btApply:
                if(addSampleDisbursermentDialogFragmentListener!=null){
                    addSampleDisbursermentDialogFragmentListener.sendIds(addSampleDisbursementModel);
                    getDialog().dismiss();
                }
                return;
            case R.id.btCancel:
                    getDialog().dismiss();
                return;
            case R.id.btClear:
                addSampleDisbursementDialogFragmentPresenter.getBrandList(divisionID);
                return;
        }
    }
        @SuppressLint("ClickableViewAccessibility")
        @Override
        public void DisplayBrandList ( final String[][] brandList){
        }
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void DisplayBrandDataList(final ArrayList<BrandBean> arrBrand) {
        final ArrayAdapter<BrandBean> adapter = new ArrayAdapter<BrandBean>(getContext(), R.layout.custom_textview,
                R.id.tvItemValue,arrBrand) {
            @Override
            public View getDropDownView(final int position, final View convertView, final ViewGroup parent) {
                final View v = super.getDropDownView(position, convertView, parent);
                ConstantsUtils.selectedView(v, spBrand, position, getContext());
                return v;
            }
        };
        /*final ArrayAdapter<BrandBean> adapter = new ArrayAdapter<BrandBean>(getContext(),
                android.R.layout.simple_dropdown_item_1line, arrBrand);*/
//        spBrand.setThreshold(1);
        spBrand.setAdapter(adapter);
        adapter.setDropDownViewResource(R.layout.spinnerinside);
        /*autBrand.setOnTouchListener(new View.OnTouchListener() {
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
        });*/
        /*else if (!TextUtils.isEmpty(strBrandId) && !strBrandId.equalsIgnoreCase(Constants.None)) {
            spBrand.setSelection(SOUtils.getPoss(arrBrand, strBrandId, 0));
        }*/
        spBrand.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                previousBrandId = ((BrandBean) parent.getItemAtPosition(position)).getBrandID();
                previousBrandDescription = ((BrandBean) parent.getItemAtPosition(position)).getBrandDesc();
                addSampleDisbursementModel.setBrandId(previousBrandId);
                addSampleDisbursementModel.setBrandDescription(previousBrandDescription);
                addSampleDisbursementDialogFragmentPresenter.updateMaterialGroup(addSampleDisbursementModel);
                if (isBrandFirstTime) {
                    isBrandFirstTime = false;
                }
                else {
                    addSampleDisbursementDialogFragmentPresenter.updateMaterialGroup(addSampleDisbursementModel);

                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
       /* spBrand.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view, int position, long rowId) {
                previousBrandId = ((BrandBean) parent.getItemAtPosition(position)).getBrandID();
                previousBrandDescription = ((BrandBean) parent.getItemAtPosition(position)).getBrandDesc();
                addSampleDisbursementModel.setBrandId(previousBrandId);
                addSampleDisbursementModel.setBrandDescription(previousBrandDescription);
                addSampleDisbursementDialogFragmentPresenter.updateMaterialGroup(addSampleDisbursementModel);
                if (isBrandFirstTime) {
                    isBrandFirstTime = false;
                }
                else {
                    addSampleDisbursementDialogFragmentPresenter.updateMaterialGroup(addSampleDisbursementModel);

                }

            }
        });*/
             bundle=getArguments();
             if(bundle!=null)
            {
                oldBrandName =bundle.getString(Constants.BrandName);
                oldBrandID =bundle.getString(Constants.BrandID);
                }
        if (!TextUtils.isEmpty(oldBrandID) && !oldBrandID.equalsIgnoreCase(Constants.None)) {
            int getPOs = SOUtils.getBrandPos(arrBrand, oldBrandID);
            spBrand.setSelection(getPOs);


            if (getPOs > -1) {
                {
//                    autBrand.setText(arrBrand.get(getPOs).getBrandDesc());
                    previousBrandId = arrBrand.get(getPOs).getBrandID();
                    previousBrandDescription = arrBrand.get(getPOs).getBrandDesc();
                    addSampleDisbursementModel.setBrandId(previousBrandId);
                    addSampleDisbursementModel.setBrandDescription(previousBrandDescription);

                    if (isBrandFirstTime) {
                        isBrandFirstTime = false;
                    }
                    else {
                        addSampleDisbursementDialogFragmentPresenter.updateMaterialGroup(addSampleDisbursementModel);

                    }
                }
            }
        }
    }
    @Override
        public void DisplaygetOrderedMaterialGroupsTemp ( final String[][] arrCategory){
            ArrayAdapter<String> productOrderGroupAdapter = new ArrayAdapter<String>(getContext(), R.layout.custom_textview,
                    R.id.tvItemValue, arrCategory[1]) {
                @Override
                public View getDropDownView(final int position, final View convertView, final ViewGroup parent) {
                    final View v = super.getDropDownView(position, convertView, parent);
                    ConstantsUtils.selectedView(v, spCategory, position, getContext());
                    return v;
                }
            };
            productOrderGroupAdapter.setDropDownViewResource(R.layout.spinnerinside);
        bundle=getArguments();
        if(bundle!=null)
        {
            oldOrderMaterialType =bundle.getString(Constants.OrderMaterialGroupDesc);
            oldOrderMaterialTypeID =bundle.getString(Constants.OrderMaterialGroupID);
        }
        /*if (!TextUtils.isEmpty(oldOrderMaterialType) && !oldOrderMaterialType.equalsIgnoreCase(Constants.None)) {
            spCategory.setSelection(SOUtils.getPoss(arrCategory, oldOrderMaterialType, 1));
            addSampleDisbursementModel.setOrderMaterialDesc(oldOrderMaterialType);
            addSampleDisbursementModel.setOrderMaterialID(oldOrderMaterialTypeID);
        } else*/ if (!TextUtils.isEmpty(oldOrderMaterialTypeID) && !oldOrderMaterialTypeID.equalsIgnoreCase(Constants.None)) {
            spCategory.setSelection(SOUtils.getPoss(arrCategory, oldOrderMaterialTypeID, 0));
            addSampleDisbursementModel.setOrderMaterialDesc(oldOrderMaterialType);
            addSampleDisbursementModel.setOrderMaterialID(oldOrderMaterialTypeID);
        }
        spCategory.setAdapter(productOrderGroupAdapter);
        spCategory.showFloatingLabel();
        spCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mStrSelOrderMaterialID = arrCategory[0][position];
                mStrSelOrderMaterialDescription = arrCategory[1][position];
                addSampleDisbursementModel.setOrderMaterialID(mStrSelOrderMaterialID);
                addSampleDisbursementModel.setOrderMaterialDesc(mStrSelOrderMaterialDescription);
                isBrandFirstTime = true;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

//            spCategory.setAdapter(productOrderGroupAdapter);
//            spCategory.showFloatingLabel();
//            spCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//                @Override
//                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                    mStrSelOrderMaterialID = arrCategory[0][position];
//                    mStrSelOrderMaterialDescription = arrCategory[1][position];
//                    addSampleDisbursementModel.setOrderMaterialID(mStrSelOrderMaterialID);
//                    addSampleDisbursementModel.setOrderMaterialDesc(mStrSelOrderMaterialDescription);
//                }
//                @Override
//                public void onNothingSelected(AdapterView<?> parent) {
//                }
//            });

        }
    public interface AddSampleDisbursermentDialogFragmentListener {
        void sendIds(AddSampleDisbursementModel addSampleDisbursementModel);
    }
    }

