package com.arteriatech.ss.msecsales.rspl.visit;


import android.os.Bundle;
import android.app.Fragment;
import androidx.annotation.Nullable;
import com.google.android.material.textfield.TextInputLayout;
import androidx.fragment.app.DialogFragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.ss.msecsales.rspl.R;
import com.arteriatech.ss.msecsales.rspl.common.Constants;
import com.arteriatech.ss.msecsales.rspl.common.ConstantsUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Hashtable;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChangeMobileNoFragment extends DialogFragment implements View.OnClickListener{

    private EditText edMobileNo;
    private Button btSave;
    private Button btNo;
    private TextInputLayout tiMobileNo;
    private String fetchJsonStr = "";
    private JSONObject fetchJsonHeaderObject = null;
    private String docID;

    public ChangeMobileNoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        getDialog().setCancelable(false);
        getDialog().setCanceledOnTouchOutside(false);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return inflater.inflate(R.layout.fragment_change_mobile_no, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        edMobileNo = (EditText) view.findViewById(R.id.edMobileNo);
        edMobileNo.requestFocus();
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        btSave = (Button) view.findViewById(R.id.btSave);
        btNo = (Button) view.findViewById(R.id.btNo);
        tiMobileNo = (TextInputLayout) view.findViewById(R.id.tiMobileNo);
        btSave.setOnClickListener(this);
        btNo.setOnClickListener(this);
        Bundle bundle = this.getArguments();
        if (bundle!= null) {
            fetchJsonStr = bundle.getString("FetchJsonHeaderObject");
            docID = bundle.getString("DOCID");
        }

        try {
            fetchJsonHeaderObject = new JSONObject(fetchJsonStr);
            String mobileNo = fetchJsonHeaderObject.getString(Constants.MobileNo);
            edMobileNo.setText(mobileNo);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        edMobileNo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tiMobileNo.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btSave) {

            if (!TextUtils.isEmpty(edMobileNo.getText().toString())) {
                if (edMobileNo.getText().toString().trim().length()<10) {
                    tiMobileNo.setErrorEnabled(true);
                    tiMobileNo.setError(getString(R.string.val_plz_enter_valid_mobile));
                }else {
                    try {
                        fetchJsonHeaderObject.put(Constants.MobileNo, edMobileNo.getText().toString());
                        onSave();
                        this.dismiss();
                        getActivity().finish();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                tiMobileNo.setErrorEnabled(true);
                tiMobileNo.setError(getString(R.string.save_mobile_no_error));
            }
        }else if(view.getId() == R.id.btNo){
            this.dismiss();
        }
    }


    private void onSave() {
        try {
            String doc_no = (docID + "");
            Hashtable table = new Hashtable();
            table.put(Constants.OutletName, fetchJsonHeaderObject.getString(Constants.OutletName));
            table.put(Constants.OwnerName, fetchJsonHeaderObject.getString(Constants.OwnerName));
            table.put(Constants.CPUID, fetchJsonHeaderObject.getString(Constants.CPUID));
            table.put(Constants.PAN, fetchJsonHeaderObject.getString(Constants.PAN));
            table.put(Constants.VATNo, fetchJsonHeaderObject.getString(Constants.VATNo));
            table.put(Constants.DOB, fetchJsonHeaderObject.getString(Constants.DOB));
            table.put(Constants.Anniversary, fetchJsonHeaderObject.getString(Constants.Anniversary));
            table.put(Constants.EmailID, fetchJsonHeaderObject.getString(Constants.EmailID));
            table.put(Constants.MobileNo, fetchJsonHeaderObject.getString(Constants.MobileNo));
            table.put(Constants.Mobile2, fetchJsonHeaderObject.getString(Constants.Mobile2));
            table.put(Constants.Landline, fetchJsonHeaderObject.getString(Constants.Landline));
            table.put(Constants.PostalCode, fetchJsonHeaderObject.getString(Constants.PostalCode));
            table.put(Constants.Landmark, fetchJsonHeaderObject.getString(Constants.Landmark));
            table.put(Constants.StateID, fetchJsonHeaderObject.getString(Constants.StateID));
            table.put(Constants.StateDesc, fetchJsonHeaderObject.getString(Constants.StateDesc));

            table.put(Constants.CityDesc, fetchJsonHeaderObject.getString(Constants.CityDesc));
            table.put(Constants.CityID, "");
            table.put(Constants.DistrictDesc, fetchJsonHeaderObject.getString(Constants.DistrictDesc));
            table.put(Constants.DistrictID, "");
            table.put(Constants.Address1, fetchJsonHeaderObject.getString(Constants.Address1));
            table.put(Constants.Address2, fetchJsonHeaderObject.getString(Constants.Address2));
            table.put(Constants.Address3, fetchJsonHeaderObject.getString(Constants.Address3));
            table.put(Constants.Address4, fetchJsonHeaderObject.getString(Constants.Address4));
            table.put(Constants.CPTypeID, fetchJsonHeaderObject.getString(Constants.CPTypeID));
            table.put(Constants.CPTypeDesc, fetchJsonHeaderObject.getString(Constants.CPTypeDesc));

            table.put(Constants.Latitude, fetchJsonHeaderObject.getString(Constants.Latitude));
            table.put(Constants.Longitude, fetchJsonHeaderObject.getString(Constants.Longitude));


//            String mStrSPGuid = Constants.getSPGUID();
//            String mStrCurrency = Constants.getNameByCPGUID(Constants.SalesPersons, Constants.Currency, Constants.SPGUID, mStrSPGuid);
            table.put(Constants.PartnerMgrGUID, fetchJsonHeaderObject.getString(Constants.PartnerMgrGUID));

            table.put(Constants.Currency, fetchJsonHeaderObject.getString(Constants.Currency));
            table.put(Constants.Group2, "");
            table.put(Constants.Group4, "");
            table.put(Constants.Country, fetchJsonHeaderObject.getString(Constants.Country));
            table.put(Constants.Tax1, fetchJsonHeaderObject.getString(Constants.Tax1));
            table.put(Constants.Tax2, fetchJsonHeaderObject.getString(Constants.Tax2));
            table.put(Constants.Tax3, fetchJsonHeaderObject.getString(Constants.Tax3));
            table.put(Constants.Tax4, fetchJsonHeaderObject.getString(Constants.Tax4));
            table.put(Constants.TaxRegStatus, fetchJsonHeaderObject.getString(Constants.TaxRegStatus));
            table.put(Constants.WeeklyOff, fetchJsonHeaderObject.getString(Constants.WeeklyOff));

            table.put(Constants.ID1, fetchJsonHeaderObject.getString(Constants.ID1));
            table.put(Constants.ID2, fetchJsonHeaderObject.getString(Constants.ID2));
            table.put(Constants.BusinessID1, fetchJsonHeaderObject.getString(Constants.BusinessID1));
            table.put(Constants.BusinessID2, fetchJsonHeaderObject.getString(Constants.BusinessID2));

            if (!fetchJsonHeaderObject.getString(Constants.IsKeyCP).isEmpty()) {
                table.put(Constants.IsKeyCP, Constants.X);
            } else {
                table.put(Constants.IsKeyCP, "");
            }

            table.put(Constants.Landline, fetchJsonHeaderObject.getString(Constants.Landline));
            table.put(Constants.FaxNo, fetchJsonHeaderObject.getString(Constants.FaxNo));
            table.put(Constants.SalesOfficeID, fetchJsonHeaderObject.getString(Constants.SalesOfficeID));
            table.put(Constants.SalesGroupID, fetchJsonHeaderObject.getString(Constants.SalesGroupID));

//            GUID guid = GUID.newRandom();


            table.put(Constants.CPGUID, fetchJsonHeaderObject.getString(Constants.CPGUID));
            table.put(Constants.RouteID, fetchJsonHeaderObject.getString(Constants.RouteID));
            table.put(Constants.ParentID, fetchJsonHeaderObject.getString(Constants.ParentID));
            table.put(Constants.ParentTypeID, fetchJsonHeaderObject.getString(Constants.ParentTypeID));
            table.put(Constants.ParentName, fetchJsonHeaderObject.getString(Constants.ParentName));
            table.put(Constants.ITEM_TXT, fetchJsonHeaderObject.getString(Constants.ITEM_TXT));
            table.put(Constants.CreatedOn, UtilConstants.getNewDateTimeFormat());
            table.put(Constants.CreatedAt, UtilConstants.getOdataDuration().toString());
            table.put(Constants.entityType, Constants.ChannelPartners);
            Constants.saveDeviceDocNoToSharedPref(getActivity(), Constants.CPList, doc_no);
            JSONObject jsonHeaderObject = new JSONObject(table);

            ConstantsUtils.storeInDataVault(doc_no, jsonHeaderObject.toString(),getActivity());
            Constants.removeFromSharKey(docID,"",getActivity(),true);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
