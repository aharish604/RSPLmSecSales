package com.arteriatech.ss.msecsales.rspl.original;

import android.content.Context;

import com.arteriatech.ss.msecsales.rspl.mbo.SKUGroupBean;
import com.arteriatech.ss.msecsales.rspl.scroll.BaseTableAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by miguel on 12/02/2016.
 *
 */
public class OriginalTableFixHeader {
    private Context context;
    private ArrayList<SKUGroupBean> alCRSSKUList = null;
    private TableFixHeaderAdapter.ClickListener clickListener = null;
    private TableFixHeaderAdapter.SpinnerSelectionListener spinnerListener = null;
    private TableFixHeaderAdapter.TextTypeListener textTypeListener=null;
    private String typeValue="";

    public OriginalTableFixHeader(Context context, ArrayList<SKUGroupBean> alCRSSKUList, TableFixHeaderAdapter.ClickListener clickListener,
                                  TableFixHeaderAdapter.TextTypeListener textTypeListener,
                                  TableFixHeaderAdapter.SpinnerSelectionListener spinnerListener,
                                  String typevalue) {
        this.context = context;
        this.alCRSSKUList = alCRSSKUList;
        this.clickListener =clickListener;
        this.spinnerListener =spinnerListener;
        this.textTypeListener=textTypeListener;
        this.typeValue=typevalue;
    }

    public BaseTableAdapter getInstance() {
        OriginalTableFixHeaderAdapter adapter = new OriginalTableFixHeaderAdapter(context);
        List<SKUGroupBean> body = alCRSSKUList;

        adapter.setFirstHeader(typeValue);
        adapter.setHeader(getHeader());
        adapter.setFirstBody(body);
        adapter.setBody(alCRSSKUList);
        adapter.setEditTextBody(alCRSSKUList);
        adapter.setSpinnerTextBody(alCRSSKUList);
        adapter.setClickListenerFirstBody(clickListener);
        adapter.setTextChangeListener(textTypeListener);
        adapter.setClickOnSpinner(spinnerListener);
        return adapter;
    }


    private List<String> getHeader() {
        final String headers[] = {
                "UNIT \nPRICE",
                "UOM",
                "QTY",
                "RL Stock"
               /* "MRP",*/
                /*"DB Stock",*/

        };

        return Arrays.asList(headers);
    }

}
