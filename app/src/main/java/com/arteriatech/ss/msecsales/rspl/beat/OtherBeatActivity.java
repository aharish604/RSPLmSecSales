package com.arteriatech.ss.msecsales.rspl.beat;

import android.os.Bundle;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.arteriatech.ss.msecsales.rspl.R;
import com.arteriatech.ss.msecsales.rspl.common.Constants;
import com.arteriatech.ss.msecsales.rspl.common.ConstantsUtils;

public class OtherBeatActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private String cpGuid = "";
    private OtherBeatListFragment otherBeatListFragment = null;
    private boolean isRefreshView=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_beat);
        Log.d("VistSartorStop","OtherBeatActivity_oncreate");

        if (getIntent() != null)
            cpGuid = getIntent().getStringExtra(Constants.EXTRA_CPGUID);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.lbl_other_beats), 0);
        if (!Constants.restartApp(OtherBeatActivity.this)) {
            if (savedInstanceState == null) {
                otherBeatListFragment = new OtherBeatListFragment();
                Bundle bundle = new Bundle();
                bundle.putBoolean(Constants.BeatPlan, true);
                bundle.putString(Constants.CPGUID, cpGuid);
                otherBeatListFragment.setArguments(bundle);
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.flContainer, otherBeatListFragment);
                fragmentTransaction.commit();
            }
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("VistSartorStop","onRestart");
      /*  Log.d("VistSartorStop","onRestart_OBA");
        if (otherBeatListFragment != null) {
            Log.d("VistSartorStop","onRestart_OBAF_If");
            otherBeatListFragment.onRefreshView();
        }else{
            Log.d("VistSartorStop","onRestart_OBAF_else");
        }*/
    }
    @Override
    public void onResume(){
        super.onResume();
        if (isRefreshView) {
            Log.d("VistSartorStop", "onresume"+isRefreshView);
            Log.d("VistSartorStop", "onresume_OBA");
            if (otherBeatListFragment != null) {
                Log.d("VistSartorStop", "onresume_OBAF_If");
                otherBeatListFragment.onRefreshView();
            } else {
                Log.d("VistSartorStop", "onresume_OBAF_else");
            }
        } else {
            Log.d("VistSartorStop", "onresume"+isRefreshView);
            isRefreshView = true;
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void setActionBarSubTitle(String subTitle) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setSubtitle(subTitle);
        }
    }
}
