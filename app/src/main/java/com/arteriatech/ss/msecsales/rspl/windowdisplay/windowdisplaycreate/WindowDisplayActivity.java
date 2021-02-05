package com.arteriatech.ss.msecsales.rspl.windowdisplay.windowdisplaycreate;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputLayout;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.ss.msecsales.rspl.R;
import com.arteriatech.ss.msecsales.rspl.adapter.ViewPagerTabAdapter;
import com.arteriatech.ss.msecsales.rspl.common.Constants;
import com.arteriatech.ss.msecsales.rspl.common.ConstantsUtils;
import com.arteriatech.ss.msecsales.rspl.customers.CustomerDetailsActivity;
import com.arteriatech.ss.msecsales.rspl.ui.MaterialDesignSpinner;

import java.util.ArrayList;

public class WindowDisplayActivity extends AppCompatActivity implements WindowDisplayView, KeyboardView.OnKeyboardActionListener {
    Toolbar toolbar;
    private String[][] arrSchemeCps = null;
    private String mStrBundleRetID = "";
    private String mStrBundleRetName = "", mStrBundleCPGUID = "";
    private String mStrSelWinDispType = "", mStrSelWinDispTypeDesc = "", popUpText = "";
    private String[][] mArrayDistributors;
    private String mStrBundleRetailerUID = "", mStrBundleCPGUID32 = "";
    private String defaultCameraPackage = "";
    private String mStrSchemeGUID = "";
    private boolean mBoolHeaderPosted = false;
    String mStrComingFrom = "";
    private String mStrSchemeName = "";
    private boolean mBlIsSecondTime = false;
    private String mStrSchemeTypeId = "";
    private String mStrInvoiceDate = "";
    private String mStrSchemeId = "";
    Context context = this;
    TabLayout tabLayout;
    ViewPager viewpager;
    ViewPagerTabAdapter viewPagerAdapter;
    ContractFragment contractFragment;
    SelfDisplayFragment ShelfDisplayFragment;
    MaterialDesignSpinner sp_displayType;
    MaterialDesignSpinner sp_window_size_type;
    EditText editRemraks;
    TextView ed_promocode;
    private EditText edit_window_size_l;
    private EditText edit_window_size_b;
    private EditText edit_window_size_h;
    private KeyboardView keyboardView;
    private Keyboard keyboard;
    private EditText focusEditText;
    TextInputLayout tiremarks;
    TextInputLayout tilength;
    TextInputLayout tiwidth;
    TextInputLayout tiheight;

    TextView tv_window_size_h;
    TextView tv_window_size_b;
    TextView tv_window_size_l;
    TextView tv_displytype;
    TextView tv_sizetype;
    LinearLayout ll_displaytype;
    LinearLayout ll_sizetype;
    LinearLayout ll_remarks;
    LinearLayout ll_window_size_h;
    LinearLayout ll_window_size_b;
    LinearLayout ll_window_size_l;
    TextView tv_Remarks;


    private ArrayList<ExpenseImageBean> contractImageList = new ArrayList<>();
    private ArrayList<ExpenseImageBean> selfImageBeanList = new ArrayList<>();
    WindowDisplayCreateBean windowDisplayCreateBean = new WindowDisplayCreateBean();
    WindowDisplayPresenterImpl windowDisplayPresenter;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_window_display);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.title_window_display), 0);
        Bundle bundleExtras = getIntent().getExtras();
        if (bundleExtras != null) {
            mStrBundleRetID = bundleExtras.getString(Constants.CPNo);
            mStrBundleRetName = bundleExtras.getString(Constants.RetailerName);
            mStrBundleCPGUID = bundleExtras.getString(Constants.CPGUID);
            mStrBundleCPGUID32 = bundleExtras.getString(Constants.CPGUID32);
            mStrBundleRetailerUID = bundleExtras.getString(Constants.CPUID);
            mStrComingFrom = bundleExtras.getString(Constants.comingFrom);
            mStrSchemeGUID = bundleExtras.getString(Constants.EXTRA_SCHEME_GUID);
            mStrSchemeName = bundleExtras.getString(Constants.EXTRA_SCHEME_NAME);
            mStrSchemeTypeId = bundleExtras.getString(Constants.EXTRA_SCHEME_TYPE_ID);
            mStrInvoiceDate = bundleExtras.getString(Constants.EXTRA_INVOICE_DATE);
            mStrSchemeId = bundleExtras.getString(Constants.EXTRA_SCHEME_ID);
            mBlIsSecondTime = bundleExtras.getBoolean(Constants.EXTRA_SCHEME_IS_SECONDTIME, false);
        }
        windowDisplayPresenter = new WindowDisplayPresenterImpl(context, this, mStrSchemeTypeId, mStrBundleCPGUID, mStrSchemeGUID, mStrBundleCPGUID32, mBlIsSecondTime, mStrBundleRetID);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        if (!Constants.restartApp(WindowDisplayActivity.this)) {
            initializeVies();
            initializeKeyboardDependencies();
            initializeTabLayout();
        }


        UtilConstants.editTextDecimalFormat(edit_window_size_l, 3, 0);
        UtilConstants.editTextDecimalFormat(edit_window_size_b, 3, 0);
        UtilConstants.editTextDecimalFormat(edit_window_size_h, 3, 0);

        edit_window_size_l.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    focusEditText = edit_window_size_l;
                    Constants.showCustomKeyboard(v, keyboardView, WindowDisplayActivity.this);
                } else {
                    Constants.hideCustomKeyboard(keyboardView);
                }
            }
        });
        edit_window_size_l.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.requestFocus();
                Constants.showCustomKeyboard(v, keyboardView, WindowDisplayActivity.this);
                Constants.setCursorPostion(edit_window_size_l, v, event);
                return true;
            }
        });
        edit_window_size_b.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    focusEditText = edit_window_size_b;
                    Constants.showCustomKeyboard(v, keyboardView, WindowDisplayActivity.this);
                } else {
                    Constants.hideCustomKeyboard(keyboardView);
                }
            }
        });
        edit_window_size_b.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.requestFocus();
                Constants.showCustomKeyboard(v, keyboardView, WindowDisplayActivity.this);
                Constants.setCursorPostion(edit_window_size_b, v, event);
                return true;
            }
        });
        edit_window_size_h.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    focusEditText = edit_window_size_h;
                    Constants.showCustomKeyboard(v, keyboardView, WindowDisplayActivity.this);
                } else {
                    Constants.hideCustomKeyboard(keyboardView);
                }
            }
        });
        edit_window_size_h.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.requestFocus();
                Constants.showCustomKeyboard(v, keyboardView, WindowDisplayActivity.this);
                Constants.setCursorPostion(edit_window_size_h, v, event);
                return true;
            }
        });

        windowDisplayPresenter.getWindowDispType();
        windowDisplayPresenter.getDistributorsValues();

        editRemraks.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                windowDisplayCreateBean.setRemarks(s.toString());
                tiremarks.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        edit_window_size_l.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                windowDisplayCreateBean.setLength(s.toString());
                tilength.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        edit_window_size_b.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                windowDisplayCreateBean.setWidth(s.toString());
                tiwidth.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        edit_window_size_h.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                windowDisplayCreateBean.setHeight(s.toString());
                tiheight.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void initializeVies() {
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        viewpager = (ViewPager) findViewById(R.id.viewpager);
        sp_displayType = (MaterialDesignSpinner) findViewById(R.id.sp_displayType);
        sp_window_size_type = (MaterialDesignSpinner) findViewById(R.id.sp_window_size_type);
        ed_promocode = (TextView) findViewById(R.id.ed_promocode);
        editRemraks = (EditText) findViewById(R.id.edit_remarks);
        edit_window_size_l = (EditText) findViewById(R.id.edit_window_size_l);
        edit_window_size_b = (EditText) findViewById(R.id.edit_window_size_b);
        edit_window_size_h = (EditText) findViewById(R.id.edit_window_size_h);
        tiremarks = (TextInputLayout) findViewById(R.id.tiremarks);
        tilength = (TextInputLayout) findViewById(R.id.tilength);
        tiwidth = (TextInputLayout) findViewById(R.id.tiwidth);
        tiheight = (TextInputLayout) findViewById(R.id.tiheight);
        tv_window_size_l = (TextView) findViewById(R.id.tv_window_size_l);
        tv_window_size_b = (TextView) findViewById(R.id.tv_window_size_b);
        tv_window_size_h = (TextView) findViewById(R.id.tv_window_size_h);
        ll_displaytype = (LinearLayout) findViewById(R.id.ll_displaytype);
        ll_sizetype = (LinearLayout) findViewById(R.id.ll_sizetype);
        ll_remarks = (LinearLayout) findViewById(R.id.ll_remarks);
        ll_window_size_h = (LinearLayout) findViewById(R.id.ll_window_size_h);
        ll_window_size_l = (LinearLayout) findViewById(R.id.ll_window_size_l);
        ll_window_size_b = (LinearLayout) findViewById(R.id.ll_window_size_b);
        tv_displytype = (TextView) findViewById(R.id.tv_displytype);
        tv_sizetype = (TextView) findViewById(R.id.tv_sizetype);
        tv_Remarks = (TextView) findViewById(R.id.tv_Remarks);

        ed_promocode.setText(mStrSchemeName);
        windowDisplayCreateBean.setPromocode(mStrSchemeName);
        windowDisplayCreateBean.setInvoicedate(mStrInvoiceDate);
        windowDisplayCreateBean.setSchemeId(mStrSchemeId);

        if (mBlIsSecondTime) {

            editRemraks.setVisibility(View.GONE);
            edit_window_size_l.setVisibility(View.GONE);
            edit_window_size_b.setVisibility(View.GONE);
            edit_window_size_h.setVisibility(View.GONE);
            sp_displayType.setVisibility(View.GONE);
            sp_window_size_type.setVisibility(View.GONE);
            tiremarks.setVisibility(View.GONE);

            tv_window_size_l.setVisibility(View.VISIBLE);
            tv_window_size_b.setVisibility(View.VISIBLE);
            ll_displaytype.setVisibility(View.VISIBLE);
            ll_sizetype.setVisibility(View.VISIBLE);
            ll_remarks.setVisibility(View.VISIBLE);
            ll_window_size_h.setVisibility(View.VISIBLE);
            ll_window_size_b.setVisibility(View.VISIBLE);
            ll_window_size_l.setVisibility(View.VISIBLE);


//            tvWindowType.setVisibility(View.VISIBLE);

            windowDisplayPresenter.getRegistrationData();
            windowDisplayPresenter.getImageFromDb();
            try {

                tv_Remarks.setText(arrSchemeCps[5][1]);
                tv_sizetype.setText(arrSchemeCps[4][1]);
                tv_window_size_l.setText(arrSchemeCps[1][1]);
                tv_window_size_b.setText(arrSchemeCps[2][1]);
                tv_window_size_h.setText(arrSchemeCps[3][1]);
                tv_displytype.setText(arrSchemeCps[6][1]);

               /* if(arrSchemeCps[6][1].equalsIgnoreCase(Constants.Win_Display_Reg_Type_Other)){
                    ll_other_input.setVisibility(View.VISIBLE);
                    tv_other_input.setVisibility(View.VISIBLE);

                    edit_other_input.setVisibility(View.GONE);
                    tv_other_mandatory.setVisibility(View.GONE);
                }else{
                    ll_other_input.setVisibility(View.GONE);
                }*/

                //   ll_other_input.setVisibility(View.GONE);

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
//            tv_other_input.setVisibility(View.GONE);
//            editRemraks.setVisibility(View.VISIBLE);
//            ivTakePicture.setVisibility(View.VISIBLE);
//            btTakePic.setVisibility(View.VISIBLE);

            editRemraks.setVisibility(View.VISIBLE);
            edit_window_size_l.setVisibility(View.VISIBLE);
            edit_window_size_b.setVisibility(View.VISIBLE);
            edit_window_size_h.setVisibility(View.VISIBLE);
            sp_displayType.setVisibility(View.VISIBLE);
            sp_window_size_type.setVisibility(View.VISIBLE);
            tiremarks.setVisibility(View.VISIBLE);
            tv_window_size_l.setVisibility(View.GONE);
            tv_window_size_b.setVisibility(View.GONE);
            ll_displaytype.setVisibility(View.GONE);
            ll_sizetype.setVisibility(View.GONE);
            ll_remarks.setVisibility(View.GONE);
            ll_window_size_h.setVisibility(View.GONE);
            ll_window_size_b.setVisibility(View.GONE);
            ll_window_size_l.setVisibility(View.GONE);

        }

    }

    private void initializeTabLayout() {
        setupViewPager(viewpager, !mBlIsSecondTime);
        tabLayout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
        tabLayout.setupWithViewPager(viewpager);
        viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                Log.d("", "onPageSelected: " + position);

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void setupViewPager(ViewPager viewPager, boolean isFirstTimes) {
        viewPagerAdapter = new ViewPagerTabAdapter(getSupportFragmentManager());
        Fragment contractFragment = new ContractFragment();
        Fragment claimsImage = new SelfDisplayFragment();
        Bundle contractBundle = new Bundle();
        contractBundle.putSerializable(Constants.EXTRA_ARRAY_LIST, contractImageList);
        contractBundle.putBoolean(Constants.EXTRA_SCHEME_IS_SECONDTIME, isFirstTimes);
        contractFragment.setArguments(contractBundle);

        Bundle claimsBundle = new Bundle();
        claimsBundle.putSerializable(Constants.EXTRA_ARRAY_LIST, selfImageBeanList);
        claimsBundle.putBoolean(Constants.EXTRA_SCHEME_IS_SECONDTIME, isFirstTimes);
        if (isFirstTimes) {
            isFirstTimes = false;
        } else {
            isFirstTimes = true;
        }
        claimsBundle.putBoolean(Constants.EXTRA_SCHEME_IS_SECONDTIME, isFirstTimes);
        claimsImage.setArguments(claimsBundle);
        viewPagerAdapter.addFrag(contractFragment, getString(R.string.lbl_contract_from));
        viewPagerAdapter.addFrag(claimsImage, getString(R.string.lbl_shelf_display));
        viewPager.setAdapter(viewPagerAdapter);
    }
    @Override
    public void setWindowDisplayType(final String[][] data) {
        ArrayAdapter<String> compAdapter = new ArrayAdapter<String>(this, R.layout.custom_textview, R.id.tvItemValue, data[1]) {
            @Override
            public View getDropDownView(final int position, final View convertView, final ViewGroup parent) {
                final View v = super.getDropDownView(position, convertView, parent);
                ConstantsUtils.selectedView(v, sp_displayType, position, getContext());
                return v;
            }
        };
        compAdapter.setDropDownViewResource(R.layout.spinnerinside);
        sp_displayType.setAdapter(compAdapter);
        sp_displayType.showFloatingLabel();
        final String[][] finalMArrayComplaints = data;
        sp_displayType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                windowDisplayCreateBean.setDisplayType(data[1][position]);
                windowDisplayCreateBean.setDisplayTypeId(data[0][position]);
//                if(windowDisplayCreateBean.getDisplayTypeId().equalsIgnoreCase("0000000005")){
//                   sdfsd;
//                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    @Override
    public void setWindowSizeType(final String[][] data) {
        ArrayAdapter<String> compAdapter = new ArrayAdapter<String>(this, R.layout.custom_textview, R.id.tvItemValue, data[1]) {
            @Override
            public View getDropDownView(final int position, final View convertView, final ViewGroup parent) {
                final View v = super.getDropDownView(position, convertView, parent);
                ConstantsUtils.selectedView(v, sp_window_size_type, position, getContext());
                return v;
            }
        };
        compAdapter.setDropDownViewResource(R.layout.spinnerinside);
        sp_window_size_type.setAdapter(compAdapter);
        sp_window_size_type.showFloatingLabel();
        final String[][] finalMArrayComplaints = data;
        sp_window_size_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                windowDisplayCreateBean.setSizeType(data[1][position]);
                windowDisplayCreateBean.setSizeTypeId(data[0][position]);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }
    @Override
    public void setDistributorValues(String[][] data) {
        mArrayDistributors = data;
    }

    @Override
    public void setgetRegistrationData(String[][] data) {
        arrSchemeCps = data;
    }

    @Override
    public void setContractImageList(ArrayList<ExpenseImageBean> data) {

        contractImageList = data;
        // String s= data.get(0).getImagePath();
    }

    @Override
    public void setSelfImageBeanList(ArrayList<ExpenseImageBean> data) {
        selfImageBeanList = data;


    }
    @Override
    public void errorDisplayType(String s) {
        sp_displayType.setError(s);
    }

    @Override
    public void errorSizeType(String s) {
        sp_window_size_type.setError(s);

    }

    @Override
    public void errorLenght(String s) {
        tilength.setErrorEnabled(true);
        tilength.setError(s);
    }

    @Override
    public void errorwidht(String s) {
        tiwidth.setErrorEnabled(true);
        tiwidth.setError(s);
    }

    @Override
    public void errorheight(String s) {
        tiheight.setErrorEnabled(true);
        tiheight.setError(s);
    }

    @Override
    public void errorRemarks(String s) {
        tiremarks.setErrorEnabled(true);
        tiremarks.setError(s);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_window_display, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_window_save:
                if (mBlIsSecondTime) {
                   WindowDisplayCreateBean windowDisplayCreateBean=new WindowDisplayCreateBean();
                    windowDisplayCreateBean.setRemarks(arrSchemeCps[5][1]);
                    windowDisplayCreateBean.setSizeType(arrSchemeCps[4][1]);
                    windowDisplayCreateBean.setLength(arrSchemeCps[1][1]);
                    windowDisplayCreateBean.setWidth(arrSchemeCps[2][1]);
                    windowDisplayCreateBean.setHeight(arrSchemeCps[3][1]);
                    windowDisplayCreateBean.setHeight(arrSchemeCps[6][1]);

                    save(windowDisplayCreateBean);
                }
                else {
                    save(windowDisplayCreateBean);
                }


                break;
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return false;
    }

    public void save(WindowDisplayCreateBean windowDisplayCreateBean) {
        windowDisplayPresenter.onSave(windowDisplayCreateBean);
    }

    public void initializeKeyboardDependencies() {

        keyboardView = (KeyboardView) findViewById(R.id.keyboard_custom_invoice_sel);
        keyboard = new Keyboard(WindowDisplayActivity.this, R.xml.ll_plus_minuus_updown_keyboard);
        keyboardView.setKeyboard(keyboard);
        keyboardView.setPreviewEnabled(false);
        keyboardView.setOnKeyboardActionListener(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

    }

    @Override
    public void onPress(int i) {

    }

    @Override
    public void onRelease(int i) {

    }

    @Override
    public void onKey(int primaryCode, int[] ints) {
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
                changeCursor(0, focusEditText);
                break;
            case 2:
                changeCursor(1, focusEditText);
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
    public void onText(CharSequence charSequence) {

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

    private void changeCursor(int fromTop, EditText selectedEditText) {
        switch (selectedEditText.getId()) {
            case R.id.edit_window_size_l:
                if (fromTop == 1) {
                    edit_window_size_b.requestFocus();
                }
                break;
            case R.id.edit_window_size_b:
                if (fromTop == 1) {
                    edit_window_size_h.requestFocus();
                } else {
                    edit_window_size_l.requestFocus();
                }
                break;
            case R.id.edit_window_size_h:
                if (fromTop == 1) {
                } else {
                    edit_window_size_b.requestFocus();
                }
                break;
        }
    }

    @Override
    public void navigateToRetDetailsActivity() {
        Constants.ComingFromCreateSenarios = Constants.X;
        Intent intentNavPrevScreen = new Intent(WindowDisplayActivity.this, CustomerDetailsActivity.class);
        intentNavPrevScreen.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intentNavPrevScreen.putExtra(Constants.CPNo, mStrBundleRetID);
        intentNavPrevScreen.putExtra(Constants.RetailerName, mStrBundleRetName);
        intentNavPrevScreen.putExtra(Constants.CPUID, mStrBundleRetailerUID);
        intentNavPrevScreen.putExtra(Constants.comingFrom, mStrComingFrom);
        intentNavPrevScreen.putExtra(Constants.CPGUID, mStrBundleCPGUID);
        if (!Constants.OtherRouteNameVal.equalsIgnoreCase("")) {
            intentNavPrevScreen.putExtra(Constants.OtherRouteGUID, Constants.OtherRouteGUIDVal);
            intentNavPrevScreen.putExtra(Constants.OtherRouteName, Constants.OtherRouteNameVal);
        }
        startActivity(intentNavPrevScreen);
    }

    @Override
    public void onBackPressed() {
        if(isCustomKeyboardVisible())
        {
            hideCustomKeyboard();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(WindowDisplayActivity.this, R.style.MyTheme);
            builder.setMessage(R.string.alert_exit_display_create).setCancelable(false)
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            navigateToRetDetailsActivity();
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
    public boolean isCustomKeyboardVisible() {
        return keyboardView.getVisibility() == View.VISIBLE;
    }
    public void hideCustomKeyboard() {
        keyboardView.setVisibility(View.GONE);
        keyboardView.setEnabled(false);
    }
}
