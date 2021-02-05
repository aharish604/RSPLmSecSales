package com.arteriatech.ss.msecsales.rspl.log;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.arteriatech.mutils.log.LogListFragment;
import com.arteriatech.ss.msecsales.rspl.R;
import com.arteriatech.ss.msecsales.rspl.common.Constants;
import com.arteriatech.ss.msecsales.rspl.common.ConstantsUtils;

public class LogActivity extends AppCompatActivity {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.log_menu), 0);
        //Calling LogList fragment
        if (!Constants.restartApp(LogActivity.this)) {
            getFragmentManager().beginTransaction().replace(R.id.fl_log_view, new LogListFragment()).commit();
        }
    }
}
