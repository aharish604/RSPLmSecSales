package com.arteriatech.ss.msecsales.rspl.sync;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.arteriatech.mutils.sync.SyncHistoryFragment;
import com.arteriatech.ss.msecsales.rspl.R;
import com.arteriatech.ss.msecsales.rspl.common.Constants;
import com.arteriatech.ss.msecsales.rspl.common.ConstantsUtils;
import com.arteriatech.ss.msecsales.rspl.sync.syncSelectionView.PendingCountFragment;
import com.arteriatech.ss.msecsales.rspl.sync.syncSelectionView.SyncHistoryInfoFragment;

public class SyncHistoryActivity extends AppCompatActivity {

    private String comingFrom = "";
    private Button btn_sync_now = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync_history);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        btn_sync_now = (Button) findViewById(R.id.btn_sync_now);
        btn_sync_now.setVisibility(View.GONE);

        if(getIntent() != null){
            comingFrom = getIntent().getExtras().getString(Constants.comingFrom);
        }

        if(!comingFrom.equals("PendingCount")) {
            ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.sync_hist), 0);
        }else {
            ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.sync_pending_count), 0);
        }

        if(comingFrom.equals("SyncHist")) {
            Fragment fragment = new SyncHistoryFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.flContainer, fragment);
            fragmentTransaction.commit();
        }else if(comingFrom.equals("SyncHistColl")){
            Fragment infoFragment = new SyncHistoryInfoFragment();
            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.replace(R.id.flContainer,infoFragment);
            transaction.commit();
        }else if(comingFrom.equals("PendingCount")){
            Fragment pendingCountFragment = new PendingCountFragment();
            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.replace(R.id.flContainer,pendingCountFragment);
            transaction.commit();
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
