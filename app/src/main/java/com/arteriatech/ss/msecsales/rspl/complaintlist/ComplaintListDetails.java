package com.arteriatech.ss.msecsales.rspl.complaintlist;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.ss.msecsales.rspl.R;
import com.arteriatech.ss.msecsales.rspl.common.Constants;
import com.arteriatech.ss.msecsales.rspl.common.ConstantsUtils;

public class ComplaintListDetails extends AppCompatActivity {

    Toolbar toolbar;
    ComplaintListModel complaintListModel;
    String complaintNo;
    String complaintCategoryId;
    String complaintType;
    String complaintCatDesc;
    String CRSSKU;
    String itemDescription;
    String remarks;
    String mfd;
    String batchNumber;
    String quantity;
    String retailerName;
    String CPNo;
    TextView tv_complaintType;
    TextView tv_cat_desc;
    TextView tv_Remarks;
    TextView tv_mfd;
    TextView tv_batchnumber;
    TextView tv_item_description;
    TextView tv_so_create_sku_grp;
    TextView tv_quantity;
    LinearLayout detailLayout;
    TextView tv_RetailerName;
    TextView tv_RetailerID;
    Bundle bundleUserDetails;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complaint_list_details);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.title_complai_details),0);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_OK, null);
                onBackPressed();
            }
        });
        initializeView();
        getExtarData();
    }
    private void initializeView() {
        tv_complaintType = (TextView) findViewById(R.id.tv_complaintType);
        tv_cat_desc = (TextView) findViewById(R.id.tv_cat_desc);
        tv_Remarks = (TextView) findViewById(R.id.tv_Remarks);
        tv_mfd = (TextView) findViewById(R.id.tv_mfd);
        tv_batchnumber = (TextView) findViewById(R.id.tv_batchnumber);
        tv_item_description = (TextView) findViewById(R.id.tv_item_description);
        tv_so_create_sku_grp = (TextView) findViewById(R.id.tv_so_create_sku_grp);
        tv_quantity = (TextView) findViewById(R.id.tv_quantity);
        detailLayout = (LinearLayout) findViewById(R.id.detailLayout);
       // tv_RetailerID = (TextView) findViewById(R.id.tv_RetailerID);
        //tv_RetailerName = (TextView) findViewById(R.id.tv_RetailerName);
    }
    private void getExtarData() {
        complaintListModel=getIntent().getParcelableExtra(Constants.COMPLAINTLISTMODEL);
        complaintNo=complaintListModel.getComplaintNo();
        complaintCategoryId=complaintListModel.getComplaintCategoryID();
        complaintType=complaintListModel.getComplaintTypeDesc();
        complaintCatDesc=complaintListModel.getComplainCategoryDesc();
        remarks=complaintListModel.getRemarks();
        bundleUserDetails=getIntent().getBundleExtra("UserDetial");
        retailerName=bundleUserDetails.getString(Constants.RetailerName,"");
        CPNo=bundleUserDetails.getString(Constants.CPNo,"");

        if(complaintCategoryId.equals("00000001"))
        {
            mfd=complaintListModel.getComplaintDate();
            batchNumber=complaintListModel.getBatch();
            CRSSKU=complaintListModel.getOrderMaterialGroupDesc();
            itemDescription=complaintListModel.getMaterialDesc();
            quantity=complaintListModel.getQuantiity();

            tv_mfd.setText(mfd);
            tv_batchnumber.setText(batchNumber);
            tv_so_create_sku_grp.setText(CRSSKU);
            tv_item_description.setText(itemDescription);
            tv_quantity.setText(UtilConstants.removeDecimalPoints(quantity));
            detailLayout.setVisibility(View.VISIBLE);
        }
        tv_complaintType.setText(complaintType);
        tv_cat_desc.setText(complaintCatDesc);
        tv_Remarks.setText(remarks);
        // tv_RetailerID.setText(CPNo);
        //tv_RetailerName.setText(retailerName);

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
