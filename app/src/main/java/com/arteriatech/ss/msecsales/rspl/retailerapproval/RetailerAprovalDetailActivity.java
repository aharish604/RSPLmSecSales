package com.arteriatech.ss.msecsales.rspl.retailerapproval;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

import com.arteriatech.ss.msecsales.rspl.R;
import com.arteriatech.ss.msecsales.rspl.common.ConstantsUtils;

public class RetailerAprovalDetailActivity extends AppCompatActivity {
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retailer_aproval_detail);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        String title = getString(R.string.lbl_retailer_approva_Detail);
        ConstantsUtils.initActionBarView(this, toolbar, true, title, 0);


    }


}
