package com.arteriatech.ss.msecsales.rspl.visit;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.ss.msecsales.rspl.R;
import com.arteriatech.ss.msecsales.rspl.collection.CollectionCreateActivity;
import com.arteriatech.ss.msecsales.rspl.common.Constants;
import com.arteriatech.ss.msecsales.rspl.common.ConstantsUtils;
import com.arteriatech.ss.msecsales.rspl.competitors.CompetitorActivity;
import com.arteriatech.ss.msecsales.rspl.complaintcreate.ComplaintCreateActivity;
import com.arteriatech.ss.msecsales.rspl.feedback.FeedbackActivity;
import com.arteriatech.ss.msecsales.rspl.merchandising.MerchandisingCreateActivity;
import com.arteriatech.ss.msecsales.rspl.reports.returnorder.create.ROCreateActivity;
import com.arteriatech.ss.msecsales.rspl.reports.returnorder.invoiceselection.AddInvoiceItemsActivity;
import com.arteriatech.ss.msecsales.rspl.retailerStockEntry.RetailerStkCrtActivity;
import com.arteriatech.ss.msecsales.rspl.sampledisbursement.SampleDisbursementActivity;
import com.arteriatech.ss.msecsales.rspl.so.socreate.SOCreateActivity;
import com.arteriatech.ss.msecsales.rspl.store.OfflineManager;
import com.arteriatech.ss.msecsales.rspl.windowdisplay.WindowDisplayListActivity;

import java.util.HashSet;

/**
 * Created by e10742 on 02-12-2016.
 */
public class VisitFragment extends Fragment implements View.OnClickListener {

    View myInflatedView = null;
    ImageView ibFeedbackCreate, ib_so_create, ib_coll_create, ivCompetitorCreate, ivROCreate;
    String mStrVisitStartedOrNotQuery = "";
    LinearLayout ll_feed_back_create, ll_feed_back_line;
    LinearLayout ll_so_create, ll_so_line, ll_coll_create, ll_coll_line, ll_competitor_create, ll_competitor_line;
    HashSet<String> mSetVisitKeys = new HashSet<>();
    private String mStrBundleRetailerNo = "";
    private String mStrBundleRetailerName = "";
    private String mUID = "";
    private String mComingFrom = "";
    private String distubutorID = "";
    private String beatGUID = "";
    private String parentId = "";
    private String mStrBundleCPGUID32 = "", mStrBundleCPGUID = "";
    private String mStrCurrency = "";
    private String mStrSPGUID = "";
    private LinearLayout ll_merchandising_create;
    private LinearLayout ll_complaint_create;
    private LinearLayout ll_merchandising_line;
    private ImageView ib_merchandising_selection;
    private ImageView ib_retailer_stock_selection;
    private ImageView ib_sample_disbursement;
    private ImageView ib_window_display;
    private ImageView ib_complaint_create_selection;
    private LinearLayout ll_retailer_stock_create;
    private LinearLayout ll_ro_stock_create;
    private LinearLayout ll_retailer_stock_line;
    private LinearLayout ll_complaintcreate_line;
    private LinearLayout ll_ro_line;
    private LinearLayout ll_sample_disbursement;
    private LinearLayout ll_sample_disbursement_line;
    private LinearLayout ll_window_display;
    private LinearLayout ll_window_display_line;
    private LinearLayout ll_ro_create_inv;
    private LinearLayout ll_ro_inv_line;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mStrBundleCPGUID32 = getArguments().getString(Constants.CPGUID32);
        mStrBundleCPGUID = getArguments().getString(Constants.CPGUID);
        mStrBundleRetailerName = getArguments().getString(Constants.RetailerName);
        mUID = getArguments().getString(Constants.CPUID);
        mComingFrom = getArguments().getString(Constants.comingFrom);
        mStrBundleRetailerNo = getArguments().getString(Constants.CPNo);
        mStrCurrency = getArguments().getString(Constants.Currency);
        distubutorID = getArguments().getString(Constants.DistubutorID,"");
        beatGUID = getArguments().getString(Constants.BeatGUID,"");
        parentId = getArguments().getString(Constants.ParentId,"");
        // Inflate the layout for this fragment
        mStrSPGUID = Constants.getSPGUID();
        myInflatedView = inflater.inflate(R.layout.activity_visit_view, container, false);
        return myInflatedView;
    }

    @Override
    public void onStart() {
        initUI();
        super.onStart();
    }

    void initUI() {
        ll_feed_back_create = (LinearLayout) myInflatedView.findViewById(R.id.ll_feed_back_create);
        ll_feed_back_line = (LinearLayout) myInflatedView.findViewById(R.id.ll_feed_back_line);
        ll_feed_back_create.setOnClickListener(this);

        ibFeedbackCreate = (ImageView) myInflatedView.findViewById(R.id.ib_feed_back_create_selection);
        ibFeedbackCreate.setOnClickListener(this);

        ll_so_create = (LinearLayout) myInflatedView.findViewById(R.id.ll_so_create);
        ll_so_line = (LinearLayout) myInflatedView.findViewById(R.id.ll_so_line);
        ll_so_create.setOnClickListener(this);

        ib_so_create = (ImageView) myInflatedView.findViewById(R.id.ib_so_create_selection);
        ib_so_create.setOnClickListener(this);

        ll_coll_create = (LinearLayout) myInflatedView.findViewById(R.id.ll_coll_create);
        ll_coll_line = (LinearLayout) myInflatedView.findViewById(R.id.ll_coll_line);
        ll_coll_create.setOnClickListener(this);

        ib_so_create = (ImageView) myInflatedView.findViewById(R.id.ib_so_create_selection);
        ib_so_create.setOnClickListener(this);

        ib_coll_create = (ImageView) myInflatedView.findViewById(R.id.ib_coll_create_selection);
        ib_coll_create.setOnClickListener(this);

        ib_merchandising_selection = (ImageView) myInflatedView.findViewById(R.id.ib_merchandising_selection);
        ll_merchandising_create = (LinearLayout) myInflatedView.findViewById(R.id.ll_merchandising_create);
        ll_merchandising_line = (LinearLayout) myInflatedView.findViewById(R.id.ll_merchandising_line);
        ll_merchandising_create.setOnClickListener(this);
        ib_merchandising_selection.setOnClickListener(this);

        ib_retailer_stock_selection = (ImageView) myInflatedView.findViewById(R.id.ib_retailer_stock_selection);
        ll_retailer_stock_create = (LinearLayout) myInflatedView.findViewById(R.id.ll_retailer_stock_create);
        ll_retailer_stock_line = (LinearLayout) myInflatedView.findViewById(R.id.ll_retailer_stock_line);
        ib_retailer_stock_selection.setOnClickListener(this);
        ll_retailer_stock_create.setOnClickListener(this);

        ll_so_create = (LinearLayout) myInflatedView.findViewById(R.id.ll_so_create);
        ll_so_line = (LinearLayout) myInflatedView.findViewById(R.id.ll_so_line);
        ll_so_create.setOnClickListener(this);
        ib_so_create = (ImageView) myInflatedView.findViewById(R.id.ib_so_create_selection);
        ib_so_create.setOnClickListener(this);

        ll_competitor_create = (LinearLayout) myInflatedView.findViewById(R.id.ll_competitor_stock_create);
        ll_competitor_line = (LinearLayout) myInflatedView.findViewById(R.id.ll_competitor_stock_line);
        ll_competitor_create.setOnClickListener(this);

        ivCompetitorCreate = (ImageView) myInflatedView.findViewById(R.id.ib_competitor_stock_selection);
        ivCompetitorCreate.setOnClickListener(this);

        ll_ro_stock_create = (LinearLayout) myInflatedView.findViewById(R.id.ll_ro_stock_create);
        ll_ro_line = (LinearLayout) myInflatedView.findViewById(R.id.ll_ro_stock_line);
        ll_ro_stock_create.setOnClickListener(this);

        ivROCreate = (ImageView) myInflatedView.findViewById(R.id.ib_competitor_ro_selection);
        ivROCreate.setOnClickListener(this);

        ll_complaint_create = (LinearLayout) myInflatedView.findViewById(R.id.ll_complaint_create);
        ll_complaintcreate_line = (LinearLayout) myInflatedView.findViewById(R.id.ll_complaintcreate_line);
        ib_complaint_create_selection = (ImageView) myInflatedView.findViewById(R.id.ib_complaint_create_selection);
        ll_complaint_create.setOnClickListener(this);

        ll_sample_disbursement = (LinearLayout) myInflatedView.findViewById(R.id.ll_sample_disbursement);
        ll_sample_disbursement_line = (LinearLayout) myInflatedView.findViewById(R.id.ll_sample_disbursement_line);
        ib_sample_disbursement = (ImageView) myInflatedView.findViewById(R.id.ib_sample_disbursement);
        ll_sample_disbursement.setOnClickListener(this);
        ll_window_display = (LinearLayout) myInflatedView.findViewById(R.id.ll_window_display);
        ll_sample_disbursement_line = (LinearLayout) myInflatedView.findViewById(R.id.ll_sample_disbursement_line);
        ib_window_display = (ImageView) myInflatedView.findViewById(R.id.ib_window_display);
        ll_window_display_line = (LinearLayout) myInflatedView.findViewById(R.id.ll_window_display_line);
        ll_window_display.setOnClickListener(this);

        ll_ro_create_inv = (LinearLayout) myInflatedView.findViewById(R.id.ll_ro_create_inv);
        ll_ro_inv_line = (LinearLayout) myInflatedView.findViewById(R.id.ll_ro_inv_line);
        ll_ro_create_inv.setOnClickListener(this);

        displayView();
    }

    void displayView() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Constants.PREFS_NAME, 0);
        String sharedVal = sharedPreferences.getString(Constants.isFeedbackCreateKey, "");
        if (sharedVal.equalsIgnoreCase(Constants.isFeedbackTcode)) {
            ll_feed_back_create.setVisibility(View.VISIBLE);
            ll_feed_back_line.setVisibility(View.VISIBLE);
        } else {
            ll_feed_back_create.setVisibility(View.GONE);
            ll_feed_back_line.setVisibility(View.GONE);
        }

        sharedVal = sharedPreferences.getString(Constants.isMerchReviewKey, "");
        if (sharedVal.equalsIgnoreCase(Constants.isMerchReviewTcode)) {
            ll_merchandising_create.setVisibility(View.VISIBLE);
            ll_merchandising_line.setVisibility(View.VISIBLE);
        } else {
            ll_merchandising_create.setVisibility(View.GONE);
            ll_merchandising_line.setVisibility(View.GONE);
        }

        sharedVal = sharedPreferences.getString(Constants.isRetailerStockKey, "");
        if (sharedVal.equalsIgnoreCase(Constants.isRetailerStockTcode)) {
            ll_retailer_stock_create.setVisibility(View.VISIBLE);
            ll_retailer_stock_line.setVisibility(View.VISIBLE);
        } else {
            ll_retailer_stock_create.setVisibility(View.GONE);
            ll_retailer_stock_line.setVisibility(View.GONE);
        }

        sharedVal = sharedPreferences.getString(Constants.isSOCreateKey, "");
        if (sharedVal.equalsIgnoreCase(Constants.isSOCreateTcode)) {
            ll_so_create.setVisibility(View.VISIBLE);
            ll_so_line.setVisibility(View.VISIBLE);
        } else {
            ll_so_create.setVisibility(View.GONE);
            ll_so_line.setVisibility(View.GONE);
        }

        sharedVal = sharedPreferences.getString(Constants.isCollCreateEnabledKey, "");
        if (sharedVal.equalsIgnoreCase(Constants.isCollCreateTcode)) {
            ll_coll_create.setVisibility(View.VISIBLE);
            ll_coll_line.setVisibility(View.VISIBLE);
        } else {
            ll_coll_create.setVisibility(View.GONE);
            ll_coll_line.setVisibility(View.GONE);
        }

        sharedVal = sharedPreferences.getString(Constants.isCompInfoEnabled, "");
        if (sharedVal.equalsIgnoreCase(Constants.isCompInfoTcode)) {
            ll_competitor_create.setVisibility(View.VISIBLE);
            ll_competitor_line.setVisibility(View.VISIBLE);
        } else {
            ll_competitor_create.setVisibility(View.GONE);
            ll_competitor_line.setVisibility(View.GONE);
        }

        sharedVal = sharedPreferences.getString(Constants.isCustomerComplaintEnabledKey, "");
        if (sharedVal.equalsIgnoreCase(Constants.isCustomerComplaintCreateTcode)) {
            ll_complaint_create.setVisibility(View.VISIBLE);
            ll_complaintcreate_line.setVisibility(View.VISIBLE);
        } else {
            ll_complaint_create.setVisibility(View.GONE);
            ll_complaintcreate_line.setVisibility(View.GONE);
        }
        sharedVal = sharedPreferences.getString(Constants.isReturnOrderCreateEnabled, "");
        if (sharedVal.equalsIgnoreCase(Constants.isReturnOrderTcode)) {
            ll_ro_stock_create.setVisibility(View.VISIBLE);
            ll_ro_line.setVisibility(View.VISIBLE);
        } else {
            ll_ro_stock_create.setVisibility(View.GONE);
            ll_ro_line.setVisibility(View.GONE);
        }


        sharedVal = sharedPreferences.getString(Constants.isSampleDisbursmentEnabledKey, "");
        if (sharedVal.equalsIgnoreCase(Constants.isSampleDisbursmentCreateTcode)) {
            ll_sample_disbursement.setVisibility(View.VISIBLE);
            ll_sample_disbursement_line.setVisibility(View.VISIBLE);

        } else {
            ll_sample_disbursement.setVisibility(View.GONE);
            ll_sample_disbursement_line.setVisibility(View.GONE);
        }

        sharedVal = sharedPreferences.getString(Constants.isWindowDisplayKey, "");
        if (sharedVal.equalsIgnoreCase(Constants.isWindowDisplayTcode)) {
            ll_window_display.setVisibility(View.VISIBLE);
            ll_window_display_line.setVisibility(View.VISIBLE);

        } else {
            ll_window_display.setVisibility(View.GONE);
            ll_window_display_line.setVisibility(View.GONE);
        }

        ll_ro_create_inv.setVisibility(View.GONE);
        ll_ro_inv_line.setVisibility(View.GONE);

        ImageView ib_feed_back_create_selection = (ImageView) myInflatedView.findViewById(R.id.ib_feed_back_create_selection);
        ib_feed_back_create_selection.setOnClickListener(this);

        String mStrVisitQry = Constants.Visits + "?$filter=StartDate eq datetime'" + UtilConstants.getNewDate() +
                "' and CPGUID eq '" + mStrBundleCPGUID32.toUpperCase() + "' and "+Constants.BeatGUID + " eq guid'"+beatGUID+"' and "+Constants.SPGUID + " eq guid'" + mStrSPGUID+"'";

        mStrVisitStartedOrNotQuery = Constants.Visits + "?$top=1 &$filter=EndDate eq null and CPGUID eq '" + mStrBundleCPGUID32.toUpperCase() + "' " +
                "and StartDate eq datetime'" + UtilConstants.getNewDate() + "' and "+Constants.SPGUID + " eq guid'" + mStrSPGUID+"'";

        try {

            mSetVisitKeys = OfflineManager.getVisitKeysForCustomer(mStrVisitQry);
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }

        checkTodayFeedbackCreateOrNot();
        checkTodaySOCreateOrNot();
        checkTodayMerchReviewCreateOrNot();
        checkTodayCollCreateOrNot();
        checkTodayRetailerStkCreateOrNot();
        checkTodayCompetitorCreateOrNot();
        checkTodaySampleDisbursementCreateOrNot();
        checkTodayWindowDisplayCreateOrNot();
        checkTodayComplaintCreateOrNot();
        checkTodayROCreateOrNot();
        checkTodayROCreateOrNot();
    }

    @Override
    public void onClick(View view) {
        if (ConstantsUtils.isAutomaticTimeZone(getActivity())) {
            switch (view.getId()) {
                case R.id.ll_feed_back_create:
                    onNavToFeedbackCreateActivity();
                    break;
                case R.id.ib_feed_back_create_selection:
                    onNavToFeedbackCreateActivity();
                    break;
                case R.id.ll_so_create:
                    onNavToSOCreateActivity();
                    break;
                case R.id.ib_so_create_selection:
                    onNavToSOCreateActivity();
                    break;
                case R.id.ib_merchandising_selection:
                    onNavToMerchandCreateActivity();
                    break;
                case R.id.ll_merchandising_create:
                    onNavToMerchandCreateActivity();
                    break;
                case R.id.ll_coll_create:
                    onNavToCollCreateActivity();
                    break;
                case R.id.ib_coll_create_selection:
                    onNavToCollCreateActivity();
                    break;
                case R.id.ll_retailer_stock_create:
                    onNavToRetStockEntCrtActivity();
                    break;
                case R.id.ib_retailer_stock_selection:
                    onNavToRetStockEntCrtActivity();
                    break;
                case R.id.ll_complaint_create:
                    onNavToComplaintCreateActivity();
                    break;
                case R.id.ll_competitor_stock_create:
                    onNavToCompetitorStockEntCrtActivity();
                    break;
                case R.id.ll_ro_stock_create:
//                    onNavToROStockEntCrtActivity();
                    onNavToInvSelActivity();
                    break;
                case R.id.ib_competitor_ro_selection:
//                    onNavToROStockEntCrtActivity();
                    onNavToInvSelActivity();
                    break;

                case R.id.ll_sample_disbursement:
                    onNavToSampleDisbursementCrtActivity();
                    break;

                case R.id.ll_window_display:
                    onNavToWindowDisplayCrtActivity();
                    break;
                case R.id.ll_ro_create_inv:
                    onNavToInvSelActivity();
                    break;
            }
        } else {
            ConstantsUtils.showAutoDateSetDialog(getActivity());
        }
    }

    private void checkTodayFeedbackCreateOrNot() {
        try {
            if (OfflineManager.getVisitActivityDoneOrNot(mSetVisitKeys, Constants.FeedbackID)) {
                ibFeedbackCreate.setImageResource(R.drawable.ic_done_black_24dp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkTodaySOCreateOrNot() {
        try {
            if (OfflineManager.getVisitActivityDoneOrNot(mSetVisitKeys, Constants.SOCreateID)) {
                ib_so_create.setImageResource(R.drawable.ic_done_black_24dp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkTodayMerchReviewCreateOrNot() {
        try {
            if (OfflineManager.getVisitActivityDoneOrNot(mSetVisitKeys, Constants.MerchReviewCreateID)) {
                ib_merchandising_selection.setImageResource(R.drawable.ic_done_black_24dp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkTodayCollCreateOrNot() {
        try {
            if (OfflineManager.getVisitActivityDoneOrNot(mSetVisitKeys, Constants.CollCreateID)) {
                ib_coll_create.setImageResource(R.drawable.ic_done_black_24dp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkTodayRetailerStkCreateOrNot() {
        try {
            if (OfflineManager.getVisitActivityDoneOrNot(mSetVisitKeys, Constants.RetailerStockID)) {
                ib_retailer_stock_selection.setImageResource(R.drawable.ic_done_black_24dp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkTodayCompetitorCreateOrNot() {
        try {
            if (OfflineManager.getVisitActivityDoneOrNot(mSetVisitKeys, Constants.CompInfoCreateID)) {
                ivCompetitorCreate.setImageResource(R.drawable.ic_done_black_24dp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void checkTodaySampleDisbursementCreateOrNot() {
        try {
            if (OfflineManager.getVisitActivityDoneOrNot(mSetVisitKeys, Constants.SampleDisbursementID)) {
                ib_sample_disbursement.setImageResource(R.drawable.ic_done_black_24dp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void checkTodayROCreateOrNot() {
        try {
            if (OfflineManager.getVisitActivityDoneOrNot(mSetVisitKeys,  Constants.ROCreateID)) {
                ivROCreate.setImageResource(R.drawable.ic_done_black_24dp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkTodayWindowDisplayCreateOrNot() {
        try {
            if (OfflineManager.getVisitActivityDoneOrNot(mSetVisitKeys, Constants.WindowDisplayID)) {
                ib_window_display.setImageResource(R.drawable.ic_done_black_24dp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkTodayComplaintCreateOrNot() {
        try {
            if (OfflineManager.getVisitActivityDoneOrNot(mSetVisitKeys, Constants.CustomerCompCreateID)) {
                ib_complaint_create_selection.setImageResource(R.drawable.ic_done_black_24dp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void onNavToComplaintCreateActivity() {
        Intent intentComplaintCreate = new Intent(getActivity(), ComplaintCreateActivity.class);
        intentComplaintCreate.putExtra(Constants.CPNo, mStrBundleRetailerNo);
        intentComplaintCreate.putExtra(Constants.CPUID, mUID);
        intentComplaintCreate.putExtra(Constants.RetailerName, mStrBundleRetailerName);
        intentComplaintCreate.putExtra(Constants.CPGUID, mStrBundleCPGUID.toUpperCase());
        intentComplaintCreate.putExtra(Constants.CPGUID32, mStrBundleCPGUID32.toUpperCase());
        intentComplaintCreate.putExtra(Constants.comingFrom, mComingFrom);
        intentComplaintCreate.putExtra(Constants.BeatGUID, beatGUID);
        intentComplaintCreate.putExtra(Constants.ParentId, parentId);
        startActivity(intentComplaintCreate);
    }


    private void onNavToFeedbackCreateActivity() {
        Intent intentFeedBack = new Intent(getActivity(), FeedbackActivity.class);
        intentFeedBack.putExtra(Constants.CPNo, mStrBundleRetailerNo);
        intentFeedBack.putExtra(Constants.CPUID, mUID);
        intentFeedBack.putExtra(Constants.RetailerName, mStrBundleRetailerName);
        intentFeedBack.putExtra(Constants.CPGUID, mStrBundleCPGUID.toUpperCase());
        intentFeedBack.putExtra(Constants.CPGUID32, mStrBundleCPGUID32.toUpperCase());
        intentFeedBack.putExtra(Constants.comingFrom, mComingFrom);
        intentFeedBack.putExtra(Constants.BeatGUID, beatGUID);
        intentFeedBack.putExtra(Constants.ParentId, parentId);
        startActivity(intentFeedBack);
    }

    private void onNavToSOCreateActivity() {
        Intent intentSOCreate = new Intent(getActivity(), SOCreateActivity.class);
        intentSOCreate.putExtra(Constants.CPNo, mStrBundleRetailerNo);
        intentSOCreate.putExtra(Constants.CPUID, mUID);
        intentSOCreate.putExtra(Constants.RetailerName, mStrBundleRetailerName);
        intentSOCreate.putExtra(Constants.CPGUID, mStrBundleCPGUID.toUpperCase());
        intentSOCreate.putExtra(Constants.CPGUID32, mStrBundleCPGUID32.toUpperCase());
        intentSOCreate.putExtra(Constants.comingFrom, mComingFrom);
        intentSOCreate.putExtra(Constants.BeatGUID, beatGUID);
        intentSOCreate.putExtra(Constants.ParentId, parentId);
        startActivity(intentSOCreate);
    }

    private void onNavToCollCreateActivity() {
        Intent intentSOCreate = new Intent(getActivity(), CollectionCreateActivity.class);
        intentSOCreate.putExtra(Constants.CPNo, mStrBundleRetailerNo);
        intentSOCreate.putExtra(Constants.CPUID, mUID);
        intentSOCreate.putExtra(Constants.RetailerName, mStrBundleRetailerName);
        intentSOCreate.putExtra(Constants.CPGUID, mStrBundleCPGUID.toUpperCase());
        intentSOCreate.putExtra(Constants.CPGUID32, mStrBundleCPGUID32.toUpperCase());
        intentSOCreate.putExtra(Constants.comingFrom, mComingFrom);
        intentSOCreate.putExtra(Constants.BeatGUID, beatGUID);
        intentSOCreate.putExtra(Constants.ParentId, parentId);
        startActivity(intentSOCreate);
    }

    private void onNavToRetStockEntCrtActivity() {
        Intent intentSOCreate = new Intent(getActivity(), RetailerStkCrtActivity.class);
        intentSOCreate.putExtra(Constants.CPNo, mStrBundleRetailerNo);
        intentSOCreate.putExtra(Constants.CPUID, mUID);
        intentSOCreate.putExtra(Constants.RetailerName, mStrBundleRetailerName);
        intentSOCreate.putExtra(Constants.CPGUID, mStrBundleCPGUID.toUpperCase());
        intentSOCreate.putExtra(Constants.CPGUID32, mStrBundleCPGUID32.toUpperCase());
        intentSOCreate.putExtra(Constants.comingFrom, mComingFrom);
        intentSOCreate.putExtra(Constants.BeatGUID, beatGUID);
        intentSOCreate.putExtra(Constants.ParentId, parentId);
        startActivity(intentSOCreate);
    }

    private void onNavToCompetitorStockEntCrtActivity() {
        Intent intentSOCreate = new Intent(getActivity(), CompetitorActivity.class);
        intentSOCreate.putExtra(Constants.CPNo, mStrBundleRetailerNo);
        intentSOCreate.putExtra(Constants.CPUID, mUID);
        intentSOCreate.putExtra(Constants.RetailerName, mStrBundleRetailerName);
        intentSOCreate.putExtra(Constants.CPGUID, mStrBundleCPGUID.toUpperCase());
        intentSOCreate.putExtra(Constants.CPGUID32, mStrBundleCPGUID32.toUpperCase());
        intentSOCreate.putExtra(Constants.comingFrom, mComingFrom);
        intentSOCreate.putExtra(Constants.BeatGUID, beatGUID);
        intentSOCreate.putExtra(Constants.ParentId, parentId);
        startActivity(intentSOCreate);
    }

    private void onNavToROStockEntCrtActivity() {
        Intent intentSOCreate = new Intent(getActivity(), ROCreateActivity.class);
        intentSOCreate.putExtra(Constants.CPNo, mStrBundleRetailerNo);
        intentSOCreate.putExtra(Constants.CPUID, mUID);
        intentSOCreate.putExtra(Constants.RetailerName, mStrBundleRetailerName);
        intentSOCreate.putExtra(Constants.CPGUID, mStrBundleCPGUID.toUpperCase());
        intentSOCreate.putExtra(Constants.CPGUID32, mStrBundleCPGUID32.toUpperCase());
        intentSOCreate.putExtra(Constants.comingFrom, mComingFrom);
        startActivity(intentSOCreate);
    }


    private void onNavToMerchandCreateActivity() {
        Intent intentSOCreate = new Intent(getActivity(), MerchandisingCreateActivity.class);
        intentSOCreate.putExtra(Constants.CPNo, mStrBundleRetailerNo);
        intentSOCreate.putExtra(Constants.CPUID, mUID);
        intentSOCreate.putExtra(Constants.RetailerName, mStrBundleRetailerName);
        intentSOCreate.putExtra(Constants.CPGUID, mStrBundleCPGUID.toUpperCase());
        intentSOCreate.putExtra(Constants.CPGUID32, mStrBundleCPGUID32.toUpperCase());
        intentSOCreate.putExtra(Constants.comingFrom, mComingFrom);
        intentSOCreate.putExtra(Constants.BeatGUID, beatGUID);
        intentSOCreate.putExtra(Constants.ParentId, parentId);
        startActivity(intentSOCreate);
    }

    private void onNavToSampleDisbursementCrtActivity() {
        Intent intentSOCreate = new Intent(getActivity(), SampleDisbursementActivity.class);
        intentSOCreate.putExtra(Constants.CPNo, mStrBundleRetailerNo);
        intentSOCreate.putExtra(Constants.CPUID, mUID);
        intentSOCreate.putExtra(Constants.RetailerName, mStrBundleRetailerName);
        intentSOCreate.putExtra(Constants.CPGUID, mStrBundleCPGUID.toUpperCase());
        intentSOCreate.putExtra(Constants.CPGUID32, mStrBundleCPGUID32.toUpperCase());
        intentSOCreate.putExtra(Constants.comingFrom, mComingFrom);
        intentSOCreate.putExtra(Constants.DistubutorID, distubutorID);
        intentSOCreate.putExtra(Constants.BeatGUID, beatGUID);
        intentSOCreate.putExtra(Constants.ParentId, parentId);
        startActivity(intentSOCreate);
    }
    private void onNavToWindowDisplayCrtActivity() {
        Intent intentSOCreate = new Intent(getActivity(), WindowDisplayListActivity.class);
        intentSOCreate.putExtra(Constants.CPNo, mStrBundleRetailerNo);
        intentSOCreate.putExtra(Constants.CPUID, mUID);
        intentSOCreate.putExtra(Constants.RetailerName, mStrBundleRetailerName);
        intentSOCreate.putExtra(Constants.CPGUID, mStrBundleCPGUID.toUpperCase());
        intentSOCreate.putExtra(Constants.CPGUID32, mStrBundleCPGUID32.toUpperCase());
        intentSOCreate.putExtra(Constants.comingFrom, mComingFrom);
        intentSOCreate.putExtra(Constants.BeatGUID, beatGUID);
        startActivity(intentSOCreate);
    }

    private void onNavToInvSelActivity() {
        Intent intentSOCreate = new Intent(getActivity(), AddInvoiceItemsActivity.class);
        intentSOCreate.putExtra(Constants.CPNo, mStrBundleRetailerNo);
        intentSOCreate.putExtra(Constants.CPUID, mUID);
        intentSOCreate.putExtra(Constants.RetailerName, mStrBundleRetailerName);
        intentSOCreate.putExtra(Constants.CPGUID, mStrBundleCPGUID.toUpperCase());
        intentSOCreate.putExtra(Constants.CPGUID32, mStrBundleCPGUID32.toUpperCase());
        intentSOCreate.putExtra(Constants.comingFrom, mComingFrom);
        intentSOCreate.putExtra(Constants.BeatGUID, beatGUID);
        intentSOCreate.putExtra(Constants.ParentId, parentId);
        startActivity(intentSOCreate);
    }

}
