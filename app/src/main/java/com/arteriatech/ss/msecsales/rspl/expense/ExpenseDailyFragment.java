package com.arteriatech.ss.msecsales.rspl.expense;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.textfield.TextInputLayout;
import androidx.fragment.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.interfaces.DialogCallBack;
import com.arteriatech.ss.msecsales.rspl.R;
import com.arteriatech.ss.msecsales.rspl.common.Constants;
import com.arteriatech.ss.msecsales.rspl.common.ConstantsUtils;
import com.arteriatech.ss.msecsales.rspl.mbo.RetailerBean;
import com.arteriatech.ss.msecsales.rspl.mbo.ValueHelpBean;
import com.arteriatech.ss.msecsales.rspl.ui.MaterialDesignSpinner;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 */
public class ExpenseDailyFragment extends Fragment implements ExpenseDailyView, View.OnClickListener {


    private static final int DATE_DIALOG_ID = 0;
    private MaterialDesignSpinner spExpenseType, spNonBeatType, spBeatName, spBeatWork, spModeConve;
    private Bundle bundle = null;
    private ExpenseDailyPresenterImpl presenter;
    private String mStrExpDate = "";
    private String[][] arrayExpNonBeatTypeVal = null;
    private int mYear, mMonth, mDay;
    private EditText etExpenseDate, etDailyAllowance, etOtherConv, etDistance, tvFarTotal, tvAllTotalValue, tvDailyAllowance;
    private final DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker v, int year, int monthOfYear, int dayOfMonth) {
            onDateSets(year, monthOfYear, dayOfMonth);
        }
    };
    private TextInputLayout tiModeDistance, tiViewDailyAllowance, tiDailyAllowance, tiOtherConv;
    private int fiscalYear = 0;
    private TextView tvConvUOM;

    public ExpenseDailyFragment() {
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
        return inflater.inflate(R.layout.fragment_expense_daily, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        spExpenseType = (MaterialDesignSpinner) view.findViewById(R.id.sp_expense_type);
        spNonBeatType = (MaterialDesignSpinner) view.findViewById(R.id.sp_non_beat_type);
        spBeatName = (MaterialDesignSpinner) view.findViewById(R.id.sp_beat_name);
        spBeatWork = (MaterialDesignSpinner) view.findViewById(R.id.sp_beat_work);
        spModeConve = (MaterialDesignSpinner) view.findViewById(R.id.sp_modeof_con);
        etExpenseDate = (EditText) view.findViewById(R.id.et_expense_date);
        etDailyAllowance = (EditText) view.findViewById(R.id.et_daily_allowance);
        etOtherConv = (EditText) view.findViewById(R.id.et_other_conv);
        etDistance = (EditText) view.findViewById(R.id.et_mode_distance);
        tvFarTotal = (EditText) view.findViewById(R.id.tv_fare_total);
        tvAllTotalValue = (EditText) view.findViewById(R.id.tv_total_daily_expenses);
        tvDailyAllowance = (EditText) view.findViewById(R.id.tv_daily_allowance);
        tiModeDistance = (TextInputLayout) view.findViewById(R.id.ti_mode_distance);
        tiViewDailyAllowance = (TextInputLayout) view.findViewById(R.id.tiv_daily_allowance);
        tiDailyAllowance = (TextInputLayout) view.findViewById(R.id.ti_daily_allowance);
        tiOtherConv = (TextInputLayout) view.findViewById(R.id.ti_other_conv);
        tvConvUOM = (TextView) view.findViewById(R.id.conv_uom);
        etExpenseDate.setOnClickListener(this);


        presenter = new ExpenseDailyPresenterImpl(getContext(), bundle, this);
        presenter.onStart();
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
    public void displayData(final ArrayList<RetailerBean> beatList, final ArrayList<ValueHelpBean> locationList, final ArrayList<ValueHelpBean> convenyanceList, final ArrayList<ExpenseConfig> expenseConfigArrayList) {
        ArrayAdapter<ExpenseConfig> expenseAdapter = new ArrayAdapter<ExpenseConfig>(getContext(), R.layout.custom_textview, R.id.tvItemValue, expenseConfigArrayList) {
            @Override
            public View getDropDownView(final int position, final View convertView, final ViewGroup parent) {
                final View v = super.getDropDownView(position, convertView, parent);
                ConstantsUtils.selectedView(v, spExpenseType, position, getContext());
                return v;
            }
        };

        expenseAdapter.setDropDownViewResource(R.layout.spinnerinside);
        spExpenseType.setAdapter(expenseAdapter);
        spExpenseType.showFloatingLabel();
        spExpenseType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                presenter.expenseConfig(expenseConfigArrayList.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        ArrayAdapter<RetailerBean> adapterBeat = new ArrayAdapter<RetailerBean>(getContext(), R.layout.custom_textview, R.id.tvItemValue, beatList) {
            @Override
            public View getDropDownView(final int position, final View convertView, final ViewGroup parent) {
                final View v = super.getDropDownView(position, convertView, parent);
                ConstantsUtils.selectedView(v, spBeatName, position, getContext());
                return v;
            }
        };

        adapterBeat.setDropDownViewResource(R.layout.spinnerinside);
        spBeatName.setAdapter(adapterBeat);
        spBeatName.showFloatingLabel();
        spBeatName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                presenter.onBeatNameItemSel(beatList.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        ArrayAdapter<ValueHelpBean> adapterLocation = new ArrayAdapter<ValueHelpBean>(getContext(), R.layout.custom_textview, R.id.tvItemValue, locationList) {
            @Override
            public View getDropDownView(final int position, final View convertView, final ViewGroup parent) {
                final View v = super.getDropDownView(position, convertView, parent);
                ConstantsUtils.selectedView(v, spBeatWork, position, getContext());
                return v;
            }
        };

        adapterLocation.setDropDownViewResource(R.layout.spinnerinside);
        spBeatWork.setAdapter(adapterLocation);
        spBeatWork.showFloatingLabel();
        spBeatWork.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                presenter.onBeatWorkItemSel(locationList.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        if (arrayExpNonBeatTypeVal == null) {
            arrayExpNonBeatTypeVal = new String[2][3];
            arrayExpNonBeatTypeVal[0][0] = "";
            arrayExpNonBeatTypeVal[1][0] = Constants.None;
            arrayExpNonBeatTypeVal[0][1] = "03";
            arrayExpNonBeatTypeVal[1][1] = ConstantsUtils.Meeting;
            arrayExpNonBeatTypeVal[0][2] = "02";
            arrayExpNonBeatTypeVal[1][2] = ConstantsUtils.Training;
        }
        ArrayAdapter<String> adapterNonBeat = new ArrayAdapter<String>(getContext(), R.layout.custom_textview, R.id.tvItemValue, arrayExpNonBeatTypeVal[1]) {
            @Override
            public View getDropDownView(final int position, final View convertView, final ViewGroup parent) {
                final View v = super.getDropDownView(position, convertView, parent);
                ConstantsUtils.selectedView(v, spNonBeatType, position, getContext());
                return v;
            }
        };
        adapterNonBeat.setDropDownViewResource(R.layout.spinnerinside);
        spNonBeatType.setAdapter(adapterNonBeat);
        spNonBeatType.showFloatingLabel();
        spNonBeatType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                presenter.onNonBeatItemSel(arrayExpNonBeatTypeVal, position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        ArrayAdapter<ValueHelpBean> adapterConv = new ArrayAdapter<ValueHelpBean>(getContext(), R.layout.custom_textview, R.id.tvItemValue, convenyanceList) {
            @Override
            public View getDropDownView(final int position, final View convertView, final ViewGroup parent) {
                final View v = super.getDropDownView(position, convertView, parent);
                ConstantsUtils.selectedView(v, spModeConve, position, getContext());
                return v;
            }
        };

        adapterConv.setDropDownViewResource(R.layout.spinnerinside);
        spModeConve.setAdapter(adapterConv);
        spModeConve.showFloatingLabel();
        spModeConve.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (convenyanceList.get(position).getID().equalsIgnoreCase(Constants.Conv_Mode_Type_Other)) {
                    tiOtherConv.setHint(convenyanceList.get(position).getDescription());
                    tiOtherConv.setVisibility(View.VISIBLE);
                    etOtherConv.setText("");
                } else {
                    etOtherConv.setText("");
                    tiOtherConv.setVisibility(View.GONE);
                }
                presenter.onModeConvItemSel(convenyanceList.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        UtilConstants.editTextDecimalFormat(etDailyAllowance, 10, 3);
        etDailyAllowance.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tiDailyAllowance.setErrorEnabled(false);
                presenter.onDailyAllowance(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        UtilConstants.editTextDecimalFormat(etDistance, 10, 3);
        etDistance.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tiModeDistance.setErrorEnabled(false);
                presenter.onDistanceValue(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etOtherConv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tiOtherConv.setErrorEnabled(false);
                presenter.onOtherConv(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        fiscalYear = mYear;
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        onDateSets(mYear, mMonth, mDay);
    }

    @Override
    public void showConveyanceAmt(String displayFarTotal, String displayUOM) {
        tvFarTotal.setText(displayFarTotal);
        tvConvUOM.setText(displayUOM);
    }

    private Dialog onDatePickerDialog(int id) {
        switch (id) {
            case DATE_DIALOG_ID:
                DatePickerDialog datePicker = new DatePickerDialog(getContext(), mDateSetListener, mYear, mMonth, mDay);
              /*  Calendar c = Calendar.getInstance();
                Date newDate = c.getTime();
                datePicker.getDatePicker().setMaxDate(newDate.getTime());*/
                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.HOUR_OF_DAY, cal.getMinimum(Calendar.HOUR_OF_DAY));
                cal.set(Calendar.MINUTE, cal.getMinimum(Calendar.MINUTE));
                cal.set(Calendar.SECOND, cal.getMinimum(Calendar.SECOND));
                cal.set(Calendar.MILLISECOND, cal.getMinimum(Calendar.MILLISECOND));
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.DAY_OF_MONTH, -presenter.getDayMonthConfig(bundle.getString(Constants.ExpenseFreq)));
                datePicker.getDatePicker().setMinDate(calendar.getTimeInMillis());
                datePicker.getDatePicker().setMaxDate(cal.getTimeInMillis());
                return datePicker;
        }
        return null;
    }

    public void onDateSets(int year, int monthOfYear, int dayOfMonth) {
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
        mStrExpDate = mYear + "-" + mon + "-" + day;

        String convertDateFormat = ConstantsUtils.convertDateIntoDisplayFormat(getContext(), String.valueOf(new StringBuilder().append(mDay)
                .append("/").append(UtilConstants.MONTHS_NUMBER[mMonth])
                .append("").append("/").append(mYear)));
        presenter.expenseDate(mStrExpDate);
        etExpenseDate.setText(convertDateFormat);
    }

    @Override
    public void onDestroyView() {
        presenter.onDestroy();
        super.onDestroyView();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.et_expense_date:
                onDatePickerDialog(DATE_DIALOG_ID).show();
                break;
        }
    }

    @Override
    public void setUIBasedOnType(String mStrSeleExpenseTypeId) {
        try {
            if (mStrSeleExpenseTypeId.equalsIgnoreCase("000010")) {
                spBeatName.setSelection(0);
                spBeatWork.setSelection(0);
                spModeConve.setSelection(0);
                spNonBeatType.setSelection(0);
                etDistance.setText("");
                spBeatName.setVisibility(View.VISIBLE);
                spNonBeatType.setVisibility(View.GONE);
                spBeatWork.setFloatingLabelText(getText(R.string.lbl_beat_work_at));
                tiModeDistance.setHint(getText(R.string.lbl_beat_distance));
            } else if (mStrSeleExpenseTypeId.equalsIgnoreCase("000020")) {
                etDistance.setText("");
                spModeConve.setSelection(0);
                spBeatWork.setSelection(0);
                spNonBeatType.setSelection(0);
                spBeatName.setVisibility(View.GONE);
                spBeatWork.setFloatingLabelText(getText(R.string.lbl_non_beat_work_at));
                tiModeDistance.setHint(getText(R.string.lbl_non_beat_distance));
                spNonBeatType.setVisibility(View.VISIBLE);
            } else {
                etDistance.setText("");
                spModeConve.setSelection(0);
                spBeatWork.setSelection(0);
                spNonBeatType.setSelection(0);
                spBeatName.setVisibility(View.GONE);
                spBeatWork.setFloatingLabelText(getText(R.string.lbl_non_beat_work_at));
                tiModeDistance.setHint(getText(R.string.lbl_non_beat_distance));
                spNonBeatType.setVisibility(View.GONE);
            }
            tvFarTotal.setText("");
            tvAllTotalValue.setText("");
            etDailyAllowance.setText("");
            tvConvUOM.setText("");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void showDailyAllowance(String dispDailyAllowance, int etDailyAllowanc, int tvDailyAllowanc) {
        if (tvDailyAllowanc == View.VISIBLE) {
            tiViewDailyAllowance.setVisibility(View.VISIBLE);
            tvDailyAllowance.setVisibility(View.VISIBLE);
            tvDailyAllowance.setText(dispDailyAllowance);

            tiDailyAllowance.setVisibility(View.GONE);
            etDailyAllowance.setVisibility(View.GONE);
        } else {
            tiViewDailyAllowance.setVisibility(View.GONE);
            tvDailyAllowance.setVisibility(View.GONE);

            tiDailyAllowance.setVisibility(View.VISIBLE);
            etDailyAllowance.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void showTotalValue(String displayTotalValue) {
        tvAllTotalValue.setText(displayTotalValue);
    }

    @Override
    public void errorExpenseType(String error) {
        if (spExpenseType.getVisibility() == View.VISIBLE) {
            spExpenseType.setError(error);
        }
    }

    @Override
    public void errorBeatName(String error) {
        if (spBeatName.getVisibility() == View.VISIBLE) {
            spBeatName.setError(error);
        }
    }

    @Override
    public void errorBeatWork(String error) {
        if (spBeatWork.getVisibility() == View.VISIBLE) {
            spBeatWork.setError(error);
        }
    }

    @Override
    public void errorMode(String error) {
        if (spModeConve.getVisibility() == View.VISIBLE) {
            spModeConve.setError(error);
        }
    }

    @Override
    public void errorDistance(String error) {
        if (tiModeDistance.getVisibility() == View.VISIBLE) {
            tiModeDistance.setError(error);
            tiModeDistance.setErrorEnabled(true);
        }
    }

    @Override
    public void errorDailyAllonce(String error) {
        if (tiDailyAllowance.getVisibility() == View.VISIBLE) {
            tiDailyAllowance.setError(error);
            tiDailyAllowance.setErrorEnabled(true);
        }
    }

    @Override
    public void errorConvMode(String error) {
        if (tiOtherConv.getVisibility() == View.VISIBLE) {
            tiOtherConv.setError(error);
            tiOtherConv.setErrorEnabled(true);
        }
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_back_save, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_save:
                presenter.onSaveData(mStrExpDate, fiscalYear);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
