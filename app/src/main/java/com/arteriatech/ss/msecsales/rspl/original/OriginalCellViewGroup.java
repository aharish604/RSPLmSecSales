package com.arteriatech.ss.msecsales.rspl.original;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.ss.msecsales.rspl.R;
import com.arteriatech.ss.msecsales.rspl.common.Constants;
import com.arteriatech.ss.msecsales.rspl.common.ConstantsUtils;
import com.arteriatech.ss.msecsales.rspl.mbo.SKUGroupBean;
import com.arteriatech.ss.msecsales.rspl.ui.CustomAutoComplete;

import java.math.BigDecimal;
import java.util.List;


/**
 * Created by miguel on 09/02/2016.
 *
 */
public class OriginalCellViewGroup extends FrameLayout
        implements
        TableFixHeaderAdapter.HeaderBinder<String>,
        TableFixHeaderAdapter.BodyBinder<SKUGroupBean>,
        TableFixHeaderAdapter.SectionBinder<SKUGroupBean>,
        TableFixHeaderAdapter.BodyEditTextBinder<SKUGroupBean>,
        TableFixHeaderAdapter.BodySpinnerTextBinder<SKUGroupBean> {

    private Context context;
    public TextView textView = null;
    public View vg_root = null;
    public EditText hEditText = null;
    public Spinner hSpinnerText = null;
    private LinearLayout ll_header_body_area=null;
    public LinearLayout lHeaderSpinner=null;
    private boolean isTyping = false;
    private static String TAG = "OriginalCellViewGroup";
    public CustomAutoComplete autUOM=null;
    public TextView tvItemUOM=null;

    public OriginalCellViewGroup(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public OriginalCellViewGroup(Context context, int type) {
        super(context);
        this.context = context;
        if (type == 1) {
            initEditText();
        }else if(type ==2){
            initSpinnerText();
        }
    }

    public OriginalCellViewGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    private void init() {
        LayoutInflater.from(context).inflate(R.layout.text_view_group, this, true);
        textView = (TextView) findViewById(R.id.tv_text);
        vg_root = findViewById(R.id.vg_root);
        ll_header_body_area = (LinearLayout) findViewById(R.id.ll_header_body_area_right);

    }

    private void initEditText() {
        LayoutInflater.from(context).inflate(R.layout.edit_text_group, this, true);
        hEditText = (EditText) findViewById(R.id.etQty);
        vg_root = findViewById(R.id.vg_root);
        ll_header_body_area = (LinearLayout) findViewById(R.id.ll_header_body_area_right);
    }
    private void initSpinnerText() {
        LayoutInflater.from(context).inflate(R.layout.spinner_text_group, this, true);
//        hSpinnerText = (Spinner) findViewById(R.id.sp_uom_sel);
        autUOM = (CustomAutoComplete) findViewById(R.id.autUOM);
        vg_root = findViewById(R.id.vg_root);
        lHeaderSpinner = (LinearLayout) findViewById(R.id.lHeaderSpinner);
        tvItemUOM = (TextView) findViewById(R.id.tvItemUOM);
    }
    @Override
    public void bindHeader(String headerName, int column) {
        if (textView != null) {
            textView.setText(headerName.toUpperCase());
            textView.setTypeface(null, Typeface.BOLD);
            textView.setGravity(Gravity.CENTER);
        }
        if (vg_root != null) {
            vg_root.setBackgroundResource(R.drawable.cell_header_border_bottom_right_gray);
        }
    }

    @Override
    public void bindBody(SKUGroupBean item, int row, int column) {
        if (textView != null) {
            textView.setText(item.getSKUGroupDesc());
            textView.setTypeface(null, Typeface.NORMAL);
            textView.setGravity(Gravity.RIGHT);
            if (item.isHeader()){

                Double calUOM=0.0,calUnitPrice=0.0;
                String calUnitPriceRoundOff = "0.0";
                if(!item.getSelectedUOM().equalsIgnoreCase(item.getUOM()) && !item.getSelectedUOM().equalsIgnoreCase("")){
                    try {
                        calUOM = Double.parseDouble(item.getAlternativeUOM1Den())/Double.parseDouble(item.getAlternativeUOM2Num());
                    } catch (NumberFormatException e) {
                        calUOM = 0.0;
                        e.printStackTrace();
                    }
                    if(calUOM!=0){
                        try {
                            calUnitPrice = ConstantsUtils.decimalRoundOff(new BigDecimal(calUOM),3).doubleValue()
                                    * Double.parseDouble(item.getMRP());
                        } catch (NumberFormatException e) {
                            calUnitPrice = 0.0;
                            e.printStackTrace();
                        }
                    }
                    if(calUnitPrice!=0){
                        calUnitPriceRoundOff = String.valueOf(ConstantsUtils.decimalRoundOff(new BigDecimal(calUnitPrice),2));//alternativeUOMQty+"";
                    }
                        textView.setText((column==0)?UtilConstants.removeLeadingZerowithTwoDecimal(calUnitPriceRoundOff):(column==2)? UtilConstants.removeLeadingZerowithTwoDecimal(item.getMRP()):(column==4)?item.getDBSTK()+" "+ item.getUOM():(column==3)?item.getRETSTK()+" "+ item.getRETSTKUOM():"");
                    }else{
                        textView.setText((column==0)?UtilConstants.removeLeadingZerowithTwoDecimal(item.getMRP()):(column==2)? UtilConstants.removeLeadingZerowithTwoDecimal(item.getMRP()):(column==4)?item.getDBSTK()+" "+ item.getUOM():(column==3)?item.getRETSTK()+" "+ item.getRETSTKUOM():"");
                    }


//                textView.setText((column==0)?item.getSOQ()+" "+ item.getUOM():(column==2)? UtilConstants.removeLeadingZerowithTwoDecimal(item.getMRP()):(column==3)?item.getDBSTK()+" "+ item.getUOM():(column==4)?item.getRETSTK()+" "+ item.getUOM():"");
//                textView.setText((column==0)?UtilConstants.removeLeadingZerowithTwoDecimal(item.getMRP()):(column==2)? UtilConstants.removeLeadingZerowithTwoDecimal(item.getMRP()):(column==3)?item.getDBSTK()+" "+ item.getUOM():(column==4)?item.getRETSTK()+" "+ item.getUOM():"");
            }
            else {

                Double calUOM=0.0,calUnitPrice=0.0;
                String calUnitPriceRoundOff = "0.0";
                if(!item.getSelectedUOM().equalsIgnoreCase(item.getUOM()) && !item.getSelectedUOM().equalsIgnoreCase("")){
                    try {
                        calUOM = Double.parseDouble(item.getAlternativeUOM1Den())/Double.parseDouble(item.getAlternativeUOM2Num());
                    } catch (NumberFormatException e) {
                        calUOM = 0.0;
                        e.printStackTrace();
                    }
                    if(calUOM!=0){
                        try {
                            calUnitPrice = ConstantsUtils.decimalRoundOff(new BigDecimal(calUOM),3).doubleValue()
                                    * Double.parseDouble(item.getMRP());
                        } catch (NumberFormatException e) {
                            calUnitPrice = 0.0;
                            e.printStackTrace();
                        }
                    }
                    if(calUnitPrice!=0){
                        calUnitPriceRoundOff = String.valueOf(ConstantsUtils.decimalRoundOff(new BigDecimal(calUnitPrice),2));//alternativeUOMQty+"";
                    }
                    textView.setText((column==0)?UtilConstants.removeLeadingZerowithTwoDecimal(calUnitPriceRoundOff):(column==2)? UtilConstants.removeLeadingZerowithTwoDecimal(item.getMRP()):(column==4)?item.getDBSTK()+" "+ item.getUOM():(column==3)?item.getRETSTK()+" "+ item.getRETSTKUOM():"");
                }else{
                    textView.setText((column==0)?UtilConstants.removeLeadingZerowithTwoDecimal(item.getMRP()):(column==2)? UtilConstants.removeLeadingZerowithTwoDecimal(item.getMRP()):(column==4)?item.getDBSTK()+" "+ item.getUOM():(column==3)?item.getRETSTK()+" "+ item.getRETSTKUOM():"");
                }

//                textView.setText((column==0)?"":(column==2)? UtilConstants.removeLeadingZerowithTwoDecimal(item.getMRP()):(column==3)?"":"");
//                textView.setText((column==0)?UtilConstants.removeLeadingZerowithTwoDecimal(item.getMRP()):(column==2)? UtilConstants.removeLeadingZerowithTwoDecimal(item.getMRP()):(column==3)?item.getDBSTK()+" "+ item.getUOM():(column==4)?item.getRETSTK()+" "+ item.getUOM():"");
            }
        }
//        if (vg_root != null) {
//            vg_root.setBackgroundResource(row % 2 == 0 ? R.drawable.cell_lightgray_border_bottom_right_gray : R.drawable.cell_white_border_bottom_right_gray);
//        }

        if(ll_header_body_area!=null){
            if (item.isHeader()){

                /*if (item.getMatTypeVal().equalsIgnoreCase(Constants.DR)){
                    ll_header_body_area.setBackgroundColor(getResources().getColor(R.color.light_green));
                }else if(item.getMatTypeVal().equalsIgnoreCase(Constants.US)){ // Orange Colour
                    ll_header_body_area.setBackgroundColor(getResources().getColor(R.color.ORANGE));
                } else if(item.getMatTypeVal().equalsIgnoreCase(Constants.CS)){ // Blue Colour
                    ll_header_body_area.setBackgroundColor(getResources().getColor(R.color.HeaderTileBackground));
                }*/

               /* try {
                    if (item.getMatTypeVal().equalsIgnoreCase(Constants.str_01) || item.getMatTypeVal().equalsIgnoreCase(Constants.str_02)) {
                        ll_header_body_area.setBackgroundColor(getResources().getColor(R.color.ORANGE));
                    }else

                        //  new launched products are blue color
                        if (item.getMatTypeVal().equalsIgnoreCase(Constants.str_03)) {
                            ll_header_body_area.setBackgroundColor(getResources().getColor(R.color.light_blue_color));
                        }
                } catch (Resources.NotFoundException e) {
                    e.printStackTrace();
                }*/

            }

        }
    }

    @Override
    public void bindSection(SKUGroupBean item, int row, int column) {
        if (textView != null) {
            textView.setTypeface(null, Typeface.BOLD);
        }
    }

    @Override
    public void bindEditTextBody(final SKUGroupBean item, final int row, int column, final TableFixHeaderAdapter tableFixHeaderAdapter, final List editextBody,TableFixHeaderAdapter.TextTypeListener textTypeListener,OriginalCellViewGroup viewGroup) {
        if (hEditText!=null) {
            textTypeListener.onTextChangeItem(item,row,column,tableFixHeaderAdapter,hEditText,viewGroup);
        }

        if(ll_header_body_area!=null){
            if (item.isHeader()){
              /*  if (item.getMatTypeVal().equalsIgnoreCase(Constants.DR)){
                    ll_header_body_area.setBackgroundColor(getResources().getColor(R.color.light_green));
                }else if(item.getMatTypeVal().equalsIgnoreCase(Constants.US)){ // Orange Colour
                    ll_header_body_area.setBackgroundColor(getResources().getColor(R.color.ORANGE));
                } else if(item.getMatTypeVal().equalsIgnoreCase(Constants.CS)){ // Blue Colour
                    ll_header_body_area.setBackgroundColor(getResources().getColor(R.color.HeaderTileBackground));
                }*/
                // Must sell and focused products are orange color
               /* try {
                    if (item.getMatTypeVal().equalsIgnoreCase(Constants.str_01) || item.getMatTypeVal().equalsIgnoreCase(Constants.str_02)) {
                        ll_header_body_area.setBackgroundColor(getResources().getColor(R.color.ORANGE));
                    }else

                        //  new launched products are blue color
                        if (item.getMatTypeVal().equalsIgnoreCase(Constants.str_03)) {
                            ll_header_body_area.setBackgroundColor(getResources().getColor(R.color.light_blue_color));
                        }
                } catch (Resources.NotFoundException e) {
                    e.printStackTrace();
                }*/
            }

        }
    }

    @Override
    public void bindSpinnerBody(final SKUGroupBean item, int row, int column, TableFixHeaderAdapter tableFixHeaderAdapter, List editextBody,
                                TableFixHeaderAdapter.SpinnerSelectionListener spinnerSelectionListener, OriginalCellViewGroup viewGroup) {
        if (hSpinnerText!=null) {
//            if (item.isHeader()){
//                ArrayAdapter<String> adapterSpinnerUOM = new ArrayAdapter<String>(context, R.layout.custom_textview_spinner, item.getAlUOM());
//                adapterSpinnerUOM.setDropDownViewResource(R.layout.spinnerinside_text);
//                hSpinnerText.setAdapter(adapterSpinnerUOM);
//                hSpinnerText.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//                    @Override
//                    public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
//                        item.setSelectedUOM(item.getAlUOM().get(position));
////                    baseTableAdapter.notifyDataSetChanged();
//                    }
//
//                    @Override
//                    public void onNothingSelected(AdapterView<?> arg0) {
//                    }
//                });
//            }
//            spinnerSelectionListener.onTextChangeItem(item,row,column,tableFixHeaderAdapter,hSpinnerText,viewGroup);
        }
    }

    /*@Override
    public void bindEditTextBody(Object item, int row, int column, TableFixHeaderAdapter tableFixHeaderAdapter, List editextBody, TableFixHeaderAdapter.TextTypeListener textTypeListener, Object vbody) {

    }*/
}
