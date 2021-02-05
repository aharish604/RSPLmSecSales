package com.arteriatech.ss.msecsales.rspl.expense;


import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.textfield.TextInputLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.arteriatech.mutils.interfaces.DialogCallBack;
import com.arteriatech.ss.msecsales.rspl.R;
import com.arteriatech.ss.msecsales.rspl.common.Constants;
import com.arteriatech.ss.msecsales.rspl.common.ConstantsUtils;
import com.arteriatech.ss.msecsales.rspl.windowdisplay.windowdisplaycreate.SelfDisplayFragment;

import java.lang.reflect.Field;
import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 */
public class ExpenseMonthlyFragment extends Fragment implements View.OnClickListener, ExpenseMonthlyView {

    private Bundle bundle = null;
    private EditText etExpenseDate, etDailyAllowance, tvMobileBill, etOtherExpenses, etRemarks, tvOtherExpense, etOtherExpense;
    private String mStrCurrentDate = "";
    private int mMonth = 0;
    private ExpenseMonthPresImpl presenter;
    private TextInputLayout tiDailyAllowance, tiViewMobBill, tiViewOtherExpense, tiOtherExpense, tiRemarks;
    private LinearLayout llPhotoEdit;
    private int mDay = 0;
    private int fiscalYear = 0;
    private int monthConfigs = 0;
    private TextView tvOtherExpenseUOM;
    private LinearLayout llOtherExpenses;
    public ExpenseMonthlyFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bundle = this.getArguments();
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_expense_monthly, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        etExpenseDate = (EditText) view.findViewById(R.id.et_expense_date);
        tiDailyAllowance = (TextInputLayout) view.findViewById(R.id.ti_daily_allowance);
        etDailyAllowance = (EditText) view.findViewById(R.id.et_daily_allowance);
        tiViewMobBill = (TextInputLayout) view.findViewById(R.id.tiv_mobile_bill);
        tvMobileBill = (EditText) view.findViewById(R.id.tv_mobile_bill);
        etOtherExpenses = (EditText) view.findViewById(R.id.et_other_expenses);
        llPhotoEdit = (LinearLayout) view.findViewById(R.id.ll_photo_edit);

        tiRemarks = (TextInputLayout) view.findViewById(R.id.ti_remarks);
        etRemarks = (EditText) view.findViewById(R.id.et_remarks);

        tiViewOtherExpense = (TextInputLayout) view.findViewById(R.id.tiv_other_expense);
        tvOtherExpense = (EditText) view.findViewById(R.id.tv_other_expense);

        tiOtherExpense = (TextInputLayout) view.findViewById(R.id.ti_other_expenses);
        tvOtherExpenseUOM = (TextView) view.findViewById(R.id.tv_other_expense_uom);
        llOtherExpenses = (LinearLayout) view.findViewById(R.id.ll_other_expenses);
        etOtherExpense = (EditText) view.findViewById(R.id.et_other_expenses);

        etExpenseDate.setOnClickListener(this);
        initUI();
        final Calendar c = Calendar.getInstance();
        fiscalYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        presenter = new ExpenseMonthPresImpl(getContext(), bundle, this);
        setDateIntoTextView(mMonth, mDay, fiscalYear);

        monthConfigs = ExpenseDailyPresenterImpl.getDayMonthConfig(bundle.getString(Constants.ExpenseFreq));
        presenter.onStart();
        openImageFragment();
    }

    private void openImageFragment() {
        Fragment fragment = new SelfDisplayFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ConstantsUtils.EXTRA_FROM, 1);
        bundle.putBoolean(Constants.EXTRA_SCHEME_IS_SECONDTIME, true);
        fragment.setArguments(bundle);
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fl_item_container, fragment);
        fragmentTransaction.commit();
    }

    private void initUI() {
        etDailyAllowance.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                presenter.onAllowanceChange(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        etRemarks.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                presenter.onRemarksChange(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        etOtherExpense.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                presenter.onOtherExpChange(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.et_expense_date:
                initMonthAndYearPicker();
                break;
        }
    }
    public void initMonthAndYearPicker(){
        final View dialogView = View.inflate(getContext(), R.layout.dialog_date_picker, null);
        final AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
        final DatePicker datePicker = (DatePicker)dialogView. findViewById(R.id.date_picker);

        int year    = datePicker.getYear();
        int month   = datePicker.getMonth();
        int day     = datePicker.getDayOfMonth();
        alertDialog.setView(dialogView);

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, cal.getMinimum(Calendar.HOUR_OF_DAY));
        cal.set(Calendar.MINUTE, cal.getMinimum(Calendar.MINUTE));
        cal.set(Calendar.SECOND, cal.getMinimum(Calendar.SECOND));
        cal.set(Calendar.MILLISECOND, cal.getMinimum(Calendar.MILLISECOND));

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DATE, 1);
        calendar.add(Calendar.MONTH, -(monthConfigs - 1));//TODO need to add
        datePicker.setMinDate(calendar.getTimeInMillis());
        datePicker.setMaxDate(cal.getTimeInMillis());
        datePicker.init(year, month, day, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
//                month_i = monthOfYear + 1;
//                setDateIntoTextView(datePicker.getMonth(), datePicker.getDayOfMonth(), datePicker.getYear());
//                Log.e("selected month:", Integer.toString(month_i));
                //Add whatever you need to handle Date changes
            }
        });
        dialogView.findViewById(R.id.date_time_set).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePicker datePicker = (DatePicker) dialogView.findViewById(R.id.date_picker);

//                mCalendar = new GregorianCalendar(datePicker.getYear(),
//                        datePicker.getMonth(),
//                        datePicker.getDayOfMonth());
                mMonth = datePicker.getMonth();
                setDateIntoTextView(datePicker.getMonth(), datePicker.getDayOfMonth(), datePicker.getYear());

                alertDialog.dismiss();
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            int daySpinnerId = Resources.getSystem().getIdentifier("day", "id", "android");
            if (daySpinnerId != 0)
            {
                View daySpinner = datePicker.findViewById(daySpinnerId);
                if (daySpinner != null)
                {
                    daySpinner.setVisibility(View.GONE);
                }
            }

            int monthSpinnerId = Resources.getSystem().getIdentifier("month", "id", "android");
            if (monthSpinnerId != 0)
            {
                View monthSpinner = datePicker.findViewById(monthSpinnerId);
                if (monthSpinner != null)
                {
                    monthSpinner.setVisibility(View.VISIBLE);
                }
            }

            int yearSpinnerId = Resources.getSystem().getIdentifier("year", "id", "android");
            if (yearSpinnerId != 0)
            {
                View yearSpinner = datePicker.findViewById(yearSpinnerId);
                if (yearSpinner != null)
                {
                    yearSpinner.setVisibility(View.VISIBLE);
                }
            }
        } else { //Older SDK versions
            Field f[] = datePicker.getClass().getDeclaredFields();
            for (Field field : f)
            {
                if(field.getName().equals("mDayPicker") || field.getName().equals("mDaySpinner"))
                {
                    field.setAccessible(true);
                    Object dayPicker = null;
                    try {
                        dayPicker = field.get(datePicker);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                    ((View) dayPicker).setVisibility(View.GONE);
                }

                if(field.getName().equals("mMonthPicker") || field.getName().equals("mMonthSpinner"))
                {
                    field.setAccessible(true);
                    Object monthPicker = null;
                    try {
                        monthPicker = field.get(datePicker);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                    ((View) monthPicker).setVisibility(View.VISIBLE);
                }

                if(field.getName().equals("mYearPicker") || field.getName().equals("mYearSpinner"))
                {
                    field.setAccessible(true);
                    Object yearPicker = null;
                    try {
                        yearPicker = field.get(datePicker);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                    ((View) yearPicker).setVisibility(View.VISIBLE);
                }
            }
        }
        alertDialog.show();
    }

    private void setDateIntoTextView(int mMonth, int mDay, int mYear) {
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
        etExpenseDate.setText(new StringBuilder().append(mMonth + 1).append("/").append(mYear).append(" "));
        presenter.expenseDate(mStrCurrentDate, mnt);
       /* if (!checkValidationAndShowDialogs()) {
            initUI();
            getExpenseConfigs();
            openImageFragment();
        }*/
    }

    @Override
    public void showProgress() {

    }

    @Override
    public void hideProgress() {

    }

    @Override
    public void showMessage(String message) {
        ConstantsUtils.displayLongToast(getContext(), message);
    }

    @Override
    public void showMobile(String displayMobileBill, int etDailyAllowanc, int tvMobileBil) {
        if (tvMobileBil == View.VISIBLE) {
            tiDailyAllowance.setVisibility(View.GONE);
            etDailyAllowance.setVisibility(View.GONE);

            tiViewMobBill.setVisibility(View.VISIBLE);
            tvMobileBill.setVisibility(View.VISIBLE);
            tvMobileBill.setText(displayMobileBill);
        } else {
            tiDailyAllowance.setVisibility(View.VISIBLE);
            etDailyAllowance.setVisibility(View.VISIBLE);

            tiViewMobBill.setVisibility(View.GONE);
            tvMobileBill.setVisibility(View.GONE);
            tvMobileBill.setText("");
        }
    }

    @Override
    public void showOtherExp(int displayRemarks, int displayPhoto, int tvOtherExpens, int etOtherExpenses, String uom, String otherExpanse) {
        llPhotoEdit.setVisibility(displayPhoto);
        tiRemarks.setVisibility(displayRemarks);

        if (tvOtherExpens == View.VISIBLE) {
            llOtherExpenses.setVisibility(View.GONE);
            etOtherExpense.setVisibility(View.GONE);

            tiViewOtherExpense.setVisibility(View.VISIBLE);
            tvOtherExpense.setVisibility(View.VISIBLE);
            tvOtherExpense.setText(otherExpanse);
        } else {
            llOtherExpenses.setVisibility(View.VISIBLE);
            etOtherExpense.setVisibility(View.VISIBLE);
            tvOtherExpenseUOM.setText(uom);
            tiViewOtherExpense.setVisibility(View.GONE);
            tvOtherExpense.setVisibility(View.GONE);
            tvOtherExpense.setText("");
        }
    }

    @Override
    public void errorDailyAlowce(String s) {

    }

    @Override
    public void errorOtherExp(String s) {

    }

    @Override
    public void errorRemarks(String s) {

    }

    @Override
    public void showSuccessMsg(String message) {
        Constants.dialogBoxWithButton(getContext(), "", message, getString(R.string.ok), "", new DialogCallBack() {
            @Override
            public void clickedStatus(boolean clickedStatus) {
                if (clickedStatus) {
                    getActivity().finish();
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        presenter.onDestroy();
        super.onDestroyView();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_back_save, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_save:
                presenter.onSaveData(tiRemarks, llPhotoEdit, fiscalYear);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
