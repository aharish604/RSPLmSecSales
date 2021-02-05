package com.arteriatech.ss.msecsales.rspl.complaintcreate;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.Bundle;
import com.google.android.material.textfield.TextInputLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.interfaces.DialogCallBack;
import com.arteriatech.ss.msecsales.rspl.R;
import com.arteriatech.ss.msecsales.rspl.common.Constants;
import com.arteriatech.ss.msecsales.rspl.common.ConstantsUtils;
import com.arteriatech.ss.msecsales.rspl.customers.CustomerDetailsActivity;
import com.arteriatech.ss.msecsales.rspl.mbo.ValueHelpBean;
import com.arteriatech.ss.msecsales.rspl.so.SOUtils;
import com.arteriatech.ss.msecsales.rspl.ui.CustomAutoComplete;
import com.arteriatech.ss.msecsales.rspl.ui.MaterialDesignSpinner;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

public class ComplaintCreateActivity extends AppCompatActivity implements ComplaintCreateView, DatePickerDialog.OnDateSetListener, KeyboardView.OnKeyboardActionListener {
    public CustomAutoComplete autUOM;
    String mStrComingFrom = "";
    String beatGUID = "";
    String parentId = "";
    MaterialDesignSpinner spComplaintCategType;
    EditText etRemarks;
    TextInputLayout tiOthers;
    ComplaintCreatePresenterImpl presenter;
    ComplaintCreateBean complainteCreateBean = new ComplaintCreateBean();
    ProgressDialog progressDialog = null;
    TextInputLayout tv_cust_quantity;
    TextInputLayout tv_cust_batch_number;
    TextInputLayout tv_cust_mfd;
    TextView txtV_cust_mfd;
    TextInputLayout tiRemarks;
    TextView tv_RetailerName;
    TextView tv_RetailerID;
    TextView txt_dateHint;
    View line;
    private Toolbar toolbar;
    private Context mContext;
    private String mStrBundleRetID = "";
    private String mStrBundleRetName = "", mStrBundleCPGUID = "";
    private String mStrBundleRetailerUID = "", mStrBundleCPGUID32 = "";
    private MaterialDesignSpinner spSnapType;
    private MaterialDesignSpinner spCrsSkuGroup, spItemDesc, spComplaints;
    private String mStrSeleCategoryId = "", mStrSelectedCatDesc = Constants.None, mStrSelectedCRSID = Constants.None, mStrSelectedCRSItem = Constants.None, mStrSelectedCRSDesc = Constants.None;
    private EditText edQuantity, edBatchNumber;
    private String mStrCurrentDate = "";
    private int mYear = 0;
    private int mMonth = 0;
    private int mDay = 0;
    private int maxLength = 0;
    private KeyboardView keyboardView;
    private Keyboard keyboard;
    private EditText focusEditText;
    private LinearLayout llProduct;
    private String[][] mArrayOrderedMaterialGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complaint_create);
        Bundle bundleComplaintCreation = getIntent().getExtras();
        if (bundleComplaintCreation != null) {
            mStrBundleRetID = bundleComplaintCreation.getString(Constants.CPNo);
            mStrBundleRetName = bundleComplaintCreation.getString(Constants.RetailerName);
            mStrBundleCPGUID = bundleComplaintCreation.getString(Constants.CPGUID);
            mStrBundleCPGUID32 = bundleComplaintCreation.getString(Constants.CPGUID32);
            mStrBundleRetailerUID = bundleComplaintCreation.getString(Constants.CPUID);
            mStrComingFrom = bundleComplaintCreation.getString(Constants.comingFrom);
            beatGUID = bundleComplaintCreation.getString(Constants.BeatGUID);
            parentId = bundleComplaintCreation.getString(Constants.ParentId);
        }
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        mContext = ComplaintCreateActivity.this;
        ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.title_complai_create), 0);
        initializeKeyboardDependencies();
        try {
            maxLength=Constants.quantityLength();
        } catch (Exception e) {
            e.printStackTrace();
        }
        initUI();
        complainteCreateBean = new ComplaintCreateBean();
        complainteCreateBean.setCPGUID32(mStrBundleCPGUID32);
        complainteCreateBean.setCPGUID(mStrBundleCPGUID);
        complainteCreateBean.setCPNo(mStrBundleRetID);
        complainteCreateBean.setComingFrom(mStrComingFrom);
        complainteCreateBean.setmCPUID(mStrBundleRetailerUID);
        complainteCreateBean.setmName(mStrBundleRetName);
        presenter = new ComplaintCreatePresenterImpl(ComplaintCreateActivity.this, this, true, ComplaintCreateActivity.this, complainteCreateBean,parentId);
        presenter.onStart(mStrBundleRetID, mStrBundleRetName, mStrBundleCPGUID32,mStrBundleCPGUID);

            mArrayOrderedMaterialGroup = new String[2][1];
            mArrayOrderedMaterialGroup[0][0] = "";
            mArrayOrderedMaterialGroup[1][0] = Constants.None;
        displayOrderedMaterialGroup(mArrayOrderedMaterialGroup);




    }

    @SuppressLint("ClickableViewAccessibility")
    private void initUI() {
        spSnapType = (MaterialDesignSpinner) findViewById(R.id.sp_snap_type);
        spComplaintCategType = (MaterialDesignSpinner) findViewById(R.id.sp_complaint_category);
        spComplaints = (MaterialDesignSpinner) findViewById(R.id.sp_cust_complaints);
        spItemDesc = (MaterialDesignSpinner) findViewById(R.id.sp_prod_item_desc);
        llProduct = (LinearLayout) findViewById(R.id.ll_cust_product);
        etRemarks = (EditText) findViewById(R.id.edit_cust_remarks);
        spCrsSkuGroup = (MaterialDesignSpinner) findViewById(R.id.sp_prod_crs_group);
        edQuantity = (EditText) findViewById(R.id.edit_cust_quantity);
        edBatchNumber = (EditText) findViewById(R.id.edit_cust_batch_number);
        tv_cust_quantity = (TextInputLayout) findViewById(R.id.tv_cust_quantity);
        tv_cust_batch_number = (TextInputLayout) findViewById(R.id.tv_cust_batch_number);
        txtV_cust_mfd = (TextView) findViewById(R.id.edit_tv_cust_mfdr);
        tiRemarks = (TextInputLayout) findViewById(R.id.tiRemarks);
        tv_RetailerName = (TextView) findViewById(R.id.tv_RetailerName);
        tv_RetailerID = (TextView) findViewById(R.id.tv_RetailerID);
        tv_RetailerID = (TextView) findViewById(R.id.tv_RetailerID);
        txt_dateHint = (TextView) findViewById(R.id.txt_dateHint);
        tv_cust_mfd = (TextInputLayout) findViewById(R.id.tv_cust_mfd);
        line = findViewById(R.id.line);
        autUOM = (CustomAutoComplete) findViewById(R.id.autUOM);


        tv_RetailerName.setText(mStrBundleRetName);
        tv_RetailerID.setText(mStrBundleRetID);

        edQuantity.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.requestFocus();
                Constants.showCustomKeyboard(v, keyboardView, ComplaintCreateActivity.this);
                Constants.setCursorPostion(edQuantity, v, event);
                return true;
            }
        });
        edQuantity.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    focusEditText = edQuantity;
                    Constants.showCustomKeyboard(v, keyboardView, ComplaintCreateActivity.this);
                } else {
                    Constants.hideCustomKeyboard(keyboardView);
                }
            }
        });
        InputFilter[] FilterArray = new InputFilter[1];
        FilterArray[0] = new InputFilter.LengthFilter(250);
//        FilterArray[1] = Constants.getNumberAlphabetOnly();
        etRemarks.setFilters(FilterArray);
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());

        final DatePickerDialog dialog = new DatePickerDialog(ComplaintCreateActivity.this, this,
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));

        tv_cust_mfd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance(TimeZone.getDefault());
                cal.set(Calendar.HOUR_OF_DAY, cal.getMinimum(Calendar.HOUR_OF_DAY));
                cal.set(Calendar.MINUTE, cal.getMinimum(Calendar.MINUTE));
                cal.set(Calendar.SECOND, cal.getMinimum(Calendar.SECOND));
                cal.set(Calendar.MILLISECOND, cal.getMinimum(Calendar.MILLISECOND));
                dialog.getDatePicker().setMaxDate(cal.getTimeInMillis());
                dialog.show();
            }
        });

        etRemarks.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                complainteCreateBean.setRemarks(s.toString());
                tiRemarks.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        UtilConstants.editTextDecimalFormat(edQuantity, maxLength, 3);
        edQuantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                complainteCreateBean.setQuanitiy(charSequence.toString());

            }

            @Override
            public void afterTextChanged(Editable editable) {
                tv_cust_quantity.setErrorEnabled(false);

            }
        });
        edBatchNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                complainteCreateBean.setBatchNumber(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
                tv_cust_batch_number.setErrorEnabled(false);


            }
        });
    }

    @Override
    public void showProgressDialog(String message) {
        progressDialog = ConstantsUtils.showProgressDialog(ComplaintCreateActivity.this, message);
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
    public void displayComplaintCategoryType(final ArrayList<ValueHelpBean> complaintCategoryList) {
        ArrayAdapter<ValueHelpBean> complaintCategoryAdapter = new ArrayAdapter<ValueHelpBean>(mContext, R.layout.custom_textview, R.id.tvItemValue, complaintCategoryList) {
            @Override
            public View getDropDownView(final int position, final View convertView, final ViewGroup parent) {
                final View v = super.getDropDownView(position, convertView, parent);
                ConstantsUtils.selectedView(v, spComplaintCategType, position, getContext());
                return v;
            }
        };
        complaintCategoryAdapter.setDropDownViewResource(R.layout.spinnerinside);
        spComplaintCategType.setAdapter(complaintCategoryAdapter);
        spComplaintCategType.setSelection(SOUtils.getValueHelpset(complaintCategoryList,"00000001"));
        spComplaintCategType.showFloatingLabel();
        spComplaintCategType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                complainteCreateBean.setmStrSeleCatId(complaintCategoryList.get(position).getID());
                complainteCreateBean.setComplaintCategoryID(complaintCategoryList.get(position).getID());
                complainteCreateBean.setComplainCategoryDesc(complaintCategoryList.get(position).getDescription());
                complainteCreateBean.setParentID(complaintCategoryList.get(position).getParentID());
                etRemarks.setText("");
                setCategoryVisibility();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void setCategoryVisibility() {
        mStrSeleCategoryId = complainteCreateBean.getmStrSeleCatId();
        edBatchNumber.setText("");
        edQuantity.setText("");
        etRemarks.setText("");
        txtV_cust_mfd.setText("");
        if (mStrSeleCategoryId.equals("00000001")) {
            llProduct.setVisibility(View.VISIBLE);
        } else if (mStrSeleCategoryId.equals("00000002")) {
            llProduct.setVisibility(View.GONE);
        } else if (mStrSeleCategoryId.equals("00000003")) {
            llProduct.setVisibility(View.GONE);
        } else if (mStrSeleCategoryId.equals("00000004")) {
            llProduct.setVisibility(View.GONE);
        } else if (mStrSeleCategoryId.equals("")) {
            llProduct.setVisibility(View.GONE);
        }

        if (!complainteCreateBean.getmStrSeleCatId().equals("")) {

            presenter.requestComplaintType(complainteCreateBean);
            Log.d("Step 1","Started");
            presenter.requestCompliantOrderMaterialType();
        }
    }

    @Override
    public void displayComplaintType(String[][] mArrayComplaints) {

        ArrayAdapter<String> compAdapter = new ArrayAdapter<String>(this, R.layout.custom_textview, R.id.tvItemValue, mArrayComplaints[1]) {
            @Override
            public View getDropDownView(final int position, final View convertView, final ViewGroup parent) {
                final View v = super.getDropDownView(position, convertView, parent);
                ConstantsUtils.selectedView(v, spComplaints, position, getContext());
                return v;
            }
        };
        compAdapter.setDropDownViewResource(R.layout.spinnerinside);
        spComplaints.setAdapter(compAdapter);
        spComplaints.showFloatingLabel();
        final String[][] finalMArrayComplaints = mArrayComplaints;
        spComplaints.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                complainteCreateBean.setmStrSeleComplaintsId(finalMArrayComplaints[0][position]);
                complainteCreateBean.setmStrSeleComplaintsDesc(finalMArrayComplaints[1][position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void displayOrderedMaterialGroup(final String[][] mArrayOrderedGroup) {

        Log.d("Step 5","Started");

        ArrayAdapter<String> productOrderGroupAdapter = new ArrayAdapter<String>(this, R.layout.custom_textview, R.id.tvItemValue, mArrayOrderedGroup[1]) {
            @Override
            public View getDropDownView(final int position, final View convertView, final ViewGroup parent) {
                final View v = super.getDropDownView(position, convertView, parent);
                ConstantsUtils.selectedView(v, spCrsSkuGroup, position, getContext());
                return v;
            }
        };
        productOrderGroupAdapter.setDropDownViewResource(R.layout.spinnerinside);
        spCrsSkuGroup.setAdapter(productOrderGroupAdapter);
        spCrsSkuGroup.showFloatingLabel();
        spCrsSkuGroup.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                complainteCreateBean.setmStrSelectedCRSID(mArrayOrderedGroup[0][i]);
                complainteCreateBean.setmStrSelectedCRSDesc(mArrayOrderedGroup[1][i]);
                mStrSelectedCRSDesc = mArrayOrderedGroup[1][i];
                mStrSelectedCRSID = mArrayOrderedGroup[0][i];
                Log.d("Step 6","Started");

                presenter.requestItemDescription(complainteCreateBean);
                //   presenter.requestUOM(complainteCreateBean);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void displayMaterialItemDescription(final ArrayList<ComplaintCreateBeanUOMandDescription> orderBy) {
        ArrayAdapter<ComplaintCreateBeanUOMandDescription> productOrderItemAdapter = new ArrayAdapter<ComplaintCreateBeanUOMandDescription>(this,
                R.layout.custom_textview, R.id.tvItemValue, orderBy) {
            @Override
            public View getDropDownView(final int position, final View convertView, final ViewGroup parent) {
                final View v = super.getDropDownView(position, convertView, parent);
                ConstantsUtils.selectedView(v, spItemDesc, position, getContext());
                return v;
            }
        };
        productOrderItemAdapter.setDropDownViewResource(R.layout.spinnerinside);
        spItemDesc.setAdapter(productOrderItemAdapter);
        spItemDesc.showFloatingLabel();
        spItemDesc.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                complainteCreateBean.setmStrSelectedCRSItem(orderBy.get(i).getmStrSelectedCRSItem());
                complainteCreateBean.setmStrSelectedCRSItemDesc(orderBy.get(i).getmStrSelectedCRSItemDesc());
                //   complainteCreateBean.setmStrSelectedCRSItemDesc(orderBy[1][i]);
                getUOM(orderBy.get(i).getUOMList());

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
//
    }

    @SuppressLint("ClickableViewAccessibility")
    public void getUOM(final ArrayList<String> orderBy) {
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(ComplaintCreateActivity.this, android.R.layout.simple_dropdown_item_1line, orderBy);
        autUOM.setThreshold(1);
        autUOM.setAdapter(adapter);
        autUOM.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View paramView, MotionEvent paramMotionEvent) {
                if (orderBy != null) {
                    if (orderBy.size() > 0) {

                        if (!autUOM.getText().toString().equals(""))
                            adapter.getFilter().filter(null);
                        autUOM.showDropDown();
                    }
                }
                return true;
            }
        });
        autUOM.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long rowId) {
                complainteCreateBean.setSetSelectedUOM((String) parent.getItemAtPosition(position));
            }
        });
        if (!TextUtils.isEmpty(complainteCreateBean.getSetSelectedUOM())) {
            try {
                int pos = orderBy.indexOf(complainteCreateBean.getSetSelectedUOM());
                if (pos >= 0) {
                    autUOM.setText(orderBy.get(pos));
                    complainteCreateBean.setSetSelectedUOM(orderBy.get(pos));
                }
            } catch (Exception e) {
                e.printStackTrace();
                autUOM.setText("");
                complainteCreateBean.setSetSelectedUOM("");
            }
        } else if (orderBy != null && !orderBy.isEmpty()) {
            autUOM.setText(orderBy.get(0));
            complainteCreateBean.setSetSelectedUOM(orderBy.get(0));
        }
    }

    @Override
    public void showMessage(String message, final boolean isSimpleDialog) {
        UtilConstants.dialogBoxWithCallBack(ComplaintCreateActivity.this, "", message, getString(R.string.ok), "", false, new DialogCallBack() {
            @Override
            public void clickedStatus(boolean b) {
                if (!isSimpleDialog) {
                    redirectActivity();
                }
            }
        });
    }

    private void redirectActivity() {
        Intent intentNavPrevScreen = new Intent(this, CustomerDetailsActivity.class);
        intentNavPrevScreen.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intentNavPrevScreen.putExtra(Constants.CPNo, mStrBundleRetID);
        intentNavPrevScreen.putExtra(Constants.RetailerName, mStrBundleRetName);
        intentNavPrevScreen.putExtra(Constants.CPUID, mStrBundleRetailerUID);
        intentNavPrevScreen.putExtra(Constants.comingFrom, mStrComingFrom);
        intentNavPrevScreen.putExtra(Constants.CPGUID, mStrBundleCPGUID);
        intentNavPrevScreen.putExtra(Constants.BeatGUID, beatGUID);
        intentNavPrevScreen.putExtra(Constants.ParentId, parentId);
        startActivity(intentNavPrevScreen);
    }


    // @Override
    //  public void displayByProductRelInfo(final ArrayList<ValueHelpBean> alProdRelInfo) {
//        ArrayAdapter<ValueHelpBean> adapterShipToList = new ArrayAdapter<ValueHelpBean>(mContext, R.layout.custom_textview,
//                R.id.tvItemValue, alProdRelInfo) {
//            @Override
//            public View getDropDownView(final int position, final View convertView, final ViewGroup parent) {
//                final View v = super.getDropDownView(position, convertView, parent);
//                ConstantsUtils.selectedView(v, spProductRelated, position, getContext());
//                return v;
//            }
//        };
//        adapterShipToList.setDropDownViewResource(R.layout.spinnerinside);
//        spProductRelated.setAdapter(adapterShipToList);
//        spProductRelated.showFloatingLabel();
//        spProductRelated.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                complainteCreateBean.setProdRelID(alProdRelInfo.get(position).getID());
//                complainteCreateBean.setProdRelDesc(alProdRelInfo.get(position).getDescription());
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//            }
//        });
    //  }

    @Override
    public void navigateRetailer() {
        redirectActivity();
    }

    @Override
    public void errorCategoryType(String message) {
        if (spComplaintCategType.getVisibility() == View.VISIBLE)
            spComplaintCategType.setError(message);
    }

    @Override
    public void errorComplaintType(String message) {
        if (spComplaints.getVisibility() == View.VISIBLE)
            spComplaints.setError(message);
    }

    @Override
    public void errorOrderType(String message) {
        if (spCrsSkuGroup != null && spCrsSkuGroup.getVisibility() == View.VISIBLE) {
            spCrsSkuGroup.setError(message);
        }

    }

    @Override
    public void errorItemDetail(String message) {
        if (spItemDesc != null && spItemDesc.getVisibility() == View.VISIBLE) {
            spItemDesc.setError(message);
        }

    }

    @Override
    public void errorRemarks(String message) {
        tiRemarks.setErrorEnabled(true);
        tiRemarks.setError(message);
    }

    @Override
    public void errorQuantity(String message) {
        tv_cust_quantity.setErrorEnabled(true);
        tv_cust_quantity.setError(message);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void errorDate(String message) {
        tv_cust_mfd.setError(message);
        tv_cust_mfd.setErrorEnabled(true);
        line.setBackgroundColor(Color.parseColor("#ff0000"));
    }
    @Override
    public void errorBatch(String message) {

        tv_cust_batch_number.setErrorEnabled(true);
        tv_cust_batch_number.setError(message);

    }
    @Override
    public void conformationDialog(String message, int from) {
        UtilConstants.dialogBoxWithCallBack(ComplaintCreateActivity.this, "", message, getString(R.string.ok), getString(R.string.cancel), false, new DialogCallBack() {
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
        inflater.inflate(R.menu.menu_next_save, menu);
        menu.removeItem(R.id.menu_review);
        menu.removeItem(R.id.menu_save);
        menu.removeItem(R.id.menu_add);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.menu_next:
                save();
                break;
        }
        return true;
    }
    public boolean isCustomKeyboardVisible() {
        return keyboardView.getVisibility() == View.VISIBLE;
    }
    public void hideCustomKeyboard() {
        keyboardView.setVisibility(View.GONE);
        keyboardView.setEnabled(false);
    }
    @Override
    public void onBackPressed() {
        if (isCustomKeyboardVisible()) {
            hideCustomKeyboard();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(ComplaintCreateActivity.this, R.style.MyTheme);
            builder.setMessage(R.string.alert_exit_create_complaint).setCancelable(false)
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            redirectActivity();
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
    @Override
    public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
        mYear = year;
        mMonth = monthOfYear;
        mDay = dayOfMonth;
        String mon = "";
        String day = "";
        int mnt = 0;
        mnt = mMonth + 1;
        if (mnt < 10)
            mon = "0" + mnt;
        else
            mon = "" + mnt;
        day = "" + mDay;
        if (mDay < 10)
            day = "0" + mDay;
        mStrCurrentDate = mYear + "-" + mon + "-" + day;

        if (!mStrCurrentDate.equalsIgnoreCase(""))
            txtV_cust_mfd.setBackgroundResource(R.drawable.textview_transprent);
        String convertDateFormat = ConstantsUtils.convertDateIntoDisplayFormat(mContext, String.valueOf(new StringBuilder().append(mDay).append("/")
                .append(UtilConstants.MONTHS_NUMBER[mMonth]).append("/").append("").append(mYear)));
        txtV_cust_mfd.setText(convertDateFormat);
        complainteCreateBean.setMfDate(mYear + "-" + mon + "-" + day);
        line.setBackgroundColor(Color.parseColor("#4b6fa3"));
        txt_dateHint.setVisibility(View.VISIBLE);
        tv_cust_mfd.setErrorEnabled(false);
    }

    private void save() {
        if (ConstantsUtils.isAutomaticTimeZone(ComplaintCreateActivity.this)) {

            if (presenter.validateFields(complainteCreateBean)) {
                Intent intent = new Intent(ComplaintCreateActivity.this, CompCreateImageActivity.class);
                intent.putExtra(Constants.EXTRA_BEAN, complainteCreateBean);
                intent.putExtra(Constants.BeatGUID, beatGUID);
                intent.putExtra(Constants.ParentId, parentId);
                startActivity(intent);
//                presenter.onAsignData("", complainteCreateBean);

            }
        } else {
            ConstantsUtils.showAutoDateSetDialog(ComplaintCreateActivity.this);
        }
    }

    public void initializeKeyboardDependencies() {
        keyboardView = (KeyboardView) findViewById(R.id.keyboard_custom_invoice_sel);
        keyboard = new Keyboard(ComplaintCreateActivity.this, R.xml.ll_with_out_dot_inc_dec_up_down);
        keyboardView.setKeyboard(keyboard);
        keyboardView.setPreviewEnabled(false);
        keyboardView.setOnKeyboardActionListener(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    @Override
    public void onPress(int primaryCode) {
    }

    @Override
    public void onRelease(int primaryCode) {
    }

    @Override
    public void onKey(int primaryCode, int[] keyCodes) {
        switch (primaryCode) {

            case 81:
                //Plus
                Constants.incrementTextValues(focusEditText, Constants.N);
                break;
            case 69:
                //Minus
                Constants.decrementEditTextVal(focusEditText, Constants.N);
                break;
            case 1:
                break;
            case 2:
                break;
            case 56:
                KeyEvent event = new KeyEvent(0, 0, KeyEvent.ACTION_DOWN, primaryCode, 0, 0, 0, 0, KeyEvent.FLAG_SOFT_KEYBOARD | KeyEvent.FLAG_KEEP_TOUCH_MODE);
                dispatchKeyEvent(event);
                break;

            default:
                //default numbers
                KeyEvent events = new KeyEvent(0, 0, KeyEvent.ACTION_DOWN, primaryCode, 0, 0, 0, 0, KeyEvent.FLAG_SOFT_KEYBOARD | KeyEvent.FLAG_KEEP_TOUCH_MODE);
                dispatchKeyEvent(events);
                break;
        }
    }

    @Override
    public void onText(CharSequence text) {

    }

    @Override
    public void swipeLeft() {

    }

    @Override
    public void swipeRight() {

    }

    @Override
    public void swipeDown() {

    }

    @Override
    public void swipeUp() {

    }


}

