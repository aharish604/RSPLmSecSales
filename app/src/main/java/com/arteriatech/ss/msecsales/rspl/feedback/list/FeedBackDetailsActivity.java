package com.arteriatech.ss.msecsales.rspl.feedback.list;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.arteriatech.ss.msecsales.rspl.R;
import com.arteriatech.ss.msecsales.rspl.common.Constants;
import com.arteriatech.ss.msecsales.rspl.common.ConstantsUtils;

public class FeedBackDetailsActivity extends AppCompatActivity {
    private String mStrBundleFeedbackGuid = "", mStrStatus = "", mStrFeedBackDesc = "",
            mStrRemarks = "", mStrLocation = "",
            mStrBundleDeviceStatus = "", mStrDeviceNo = "";
    private String mStrBundleRetName = "", mStrBundleRetID = "";
    private String mStrBundleFeedbackNo = "", mStrBtsID = "";
    private TextView tvFeedType;
    private TextView tvRemrks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_back_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.title_feed_back), 0);
        Bundle bundleExtras = getIntent().getExtras();
        if (bundleExtras != null) {
            mStrBundleRetID = bundleExtras.getString(Constants.CPNo);
            mStrBundleRetName = bundleExtras.getString(Constants.RetailerName);
            mStrBundleFeedbackNo = bundleExtras.getString(Constants.FeedbackNo);
            mStrBundleFeedbackGuid = bundleExtras.getString(Constants.FeedBackGuid);
            mStrFeedBackDesc = bundleExtras.getString(Constants.FeedbackDesc);
            mStrBtsID = bundleExtras.getString(Constants.BTSID);
            mStrLocation = bundleExtras.getString(Constants.Location);
            mStrRemarks = bundleExtras.getString(Constants.Remarks);
            mStrBundleDeviceStatus = bundleExtras.getString(Constants.DeviceStatus);
            mStrDeviceNo = bundleExtras.getString(Constants.DeviceNo);
        }
        tvFeedType = (TextView)findViewById(R.id.tv_feed_type);
        tvRemrks = (TextView)findViewById(R.id.tv_remrks);
        tvFeedType.setText(mStrFeedBackDesc);
        tvRemrks.setText(mStrRemarks);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }
}
