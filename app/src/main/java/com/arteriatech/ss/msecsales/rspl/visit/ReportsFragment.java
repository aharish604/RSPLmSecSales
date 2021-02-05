package com.arteriatech.ss.msecsales.rspl.visit;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.arteriatech.ss.msecsales.rspl.R;
import com.arteriatech.ss.msecsales.rspl.SampleDisbursementList.SampleDisbursementListActivity;
import com.arteriatech.ss.msecsales.rspl.common.Constants;
import com.arteriatech.ss.msecsales.rspl.competitors.list.CompetitorListActivity;
import com.arteriatech.ss.msecsales.rspl.complaintlist.ComplainListActivity;
import com.arteriatech.ss.msecsales.rspl.feedback.list.FeedbackListActivity;
import com.arteriatech.ss.msecsales.rspl.reports.collection.header.CollectionListActivity;
import com.arteriatech.ss.msecsales.rspl.reports.invoicelist.InvoiceListActivity;
import com.arteriatech.ss.msecsales.rspl.reports.merchandising.MerchandisingListActivity;
import com.arteriatech.ss.msecsales.rspl.reports.outstndinglist.OutstandingListActivity;
import com.arteriatech.ss.msecsales.rspl.reports.returnorder.list.ReturnOrderListActivity;
import com.arteriatech.ss.msecsales.rspl.reports.salesorder.header.SalesOrderHeaderListActivity;

import com.arteriatech.ss.msecsales.rspl.retailertrends.RetailerTrends;


public class ReportsFragment extends Fragment {

    String mStrCPGUID = "", mStrRetID = "", mStrRetName = "", mStrCPGUID36 = "", mStrRetUID = "", mStrBeatGuid = "", mStrParentId = "";
    private GridView gvRetailerDetails;

    private final String[] mArrStrIconNames = Constants.reportsArray;
    private int[] mArrIntMinVisibility;

    public int[] mArrIntIconPosition = Constants.IconPositionReportFragment;

    public ReportsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mStrCPGUID = getArguments().getString(Constants.CPGUID32);
        mStrRetID = getArguments().getString(Constants.CPNo);
        mStrRetName = getArguments().getString(Constants.RetailerName);
        mStrCPGUID36 = getArguments().getString(Constants.CPGUID);
        mStrRetUID = getArguments().getString(Constants.CPUID);
        mStrBeatGuid = getArguments().getString(Constants.BeatGUID);
        mStrParentId = getArguments().getString(Constants.ParentId);

        // Inflate the layout for this fragment
        View myInflatedView = inflater.inflate(R.layout.fragment_ret_reports, container, false);

        onInitUI(myInflatedView);
        setIconVisibility();
        setValuesToUI(myInflatedView);

        return myInflatedView;
    }

    /*
     *  This method initialize UI
     */
    private void onInitUI(View myInflatedView) {
        gvRetailerDetails = (GridView) myInflatedView.findViewById(R.id.gv_retailer_details);
    }

    /*
         This method set values to UI
        */
    private void setValuesToUI(View myInflatedView) {
        gvRetailerDetails.setAdapter(new ReportsAdapter(myInflatedView.getContext()));
    }

    class ReportsAdapter extends BaseAdapter {


        private Context mContext;

        ReportsAdapter(Context mContext) {
            super();
            this.mContext = mContext;
        }

        @Override
        public int getCount() {
            int count = 0;
            for (int aMinVisibility : mArrIntMinVisibility) {
                if (aMinVisibility == 1) {
                    count++;
                }
            }
            return count;
        }

        @Override
        public Object getItem(int arg0) {
            return null;
        }

        @Override
        public long getItemId(int arg0) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            int mIntIconPos = mArrIntIconPosition[position];
            View view;
            if (convertView == null) {
                LayoutInflater liRelatedLinks = getActivity().getLayoutInflater();
                view = liRelatedLinks.inflate(R.layout.retailer_menu_inside, parent, false);
                view.requestFocus();
                final TextView tvIconName = (TextView) view
                        .findViewById(R.id.icon_text);
                tvIconName.setTextColor(getResources().getColor(R.color.icon_text_blue));
                tvIconName.setText(mArrStrIconNames[mIntIconPos]);
                final ImageView ivIconId = (ImageView) view
                        .findViewById(R.id.ib_must_sell);
                ivIconId.setColorFilter(ContextCompat.getColor(getContext(), R.color.secondaryColor), android.graphics.PorterDuff.Mode.SRC_IN);
                if (mIntIconPos == 0) {
                    ivIconId.setImageResource(R.drawable.ic_shopping_cart_black_24dp);
                    ivIconId.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View arg0) {
                            onSalesOrderList(); //so list
                        }
                    });
                } else if (mIntIconPos == 1) {
                    ivIconId.setImageResource(R.drawable.ic_shopping_cart_black_24dp);
                    ivIconId.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View arg0) {
                            onReturnOrderList();
                        }
                    });
                } else if (mIntIconPos == 2) {
                    ivIconId.setImageResource(R.drawable.ic_account_balance_wallet_black_24dp);
                    ivIconId.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View arg0) {
                            onInvoiceList();
                        }
                    });
                } else if (mIntIconPos == 3) {
                    ivIconId.setImageResource(R.drawable.ic_account_balance_wallet_black_24dp);
                    ivIconId.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View arg0) {
                            onCollList();
                        }
                    });
                } else if (mIntIconPos == 4) {
                    ivIconId.setImageResource(R.drawable.ic_photo_camera_black_24dp);
                    ivIconId.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View arg0) {
                            onMerchList();
                        }
                    });
                } else if (mIntIconPos == 5) {
                    ivIconId.setImageResource(R.drawable.ic_feedback_black_24dp);
                    ivIconId.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View arg0) {
                            onFeedbackList();
                        }
                    });
                } else if (mIntIconPos == 6) {
                    ivIconId.setImageResource(R.drawable.ic_account_balance_wallet_black_24dp);
                    ivIconId.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View arg0) {
                            onOutstandingList();
                        }
                    });
                } else if (mIntIconPos == 7) {
                    ivIconId.setImageResource(R.drawable.ic_competitors_black_24dp);
                    ivIconId.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View arg0) {
                            onCompetitorList();
                        }
                    });
                }

                else  if(mIntIconPos == 8){
                    ivIconId.setImageResource(R.drawable.ic_feedback_black_24dp);
                    ivIconId.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View arg0) {
                            onComplaintList();
                        }
                    });
                }

                else  if(mIntIconPos == 9){
                    ivIconId.setImageResource(R.drawable.ic_trending_up_black_24dp);
                    ivIconId.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View arg0) {
                            onRetailerTrend();
                        }
                    });
                }

                else  if(mIntIconPos == 10){
                    ivIconId.setImageResource(R.drawable.ic_shopping_cart_black_24dp);
                    ivIconId.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View arg0) {
                            onSampleDisbursement();
                        }
                    });
                }
                view.setId(position);
            } else {
                view = convertView;
            }
            return view;
        }

        private void onRetailerTrend() {

            Intent intent = new Intent(getContext(), RetailerTrends.class);
            intent.putExtra(Constants.CPNo, mStrRetID);
            intent.putExtra(Constants.RetailerName, mStrRetName);
            intent.putExtra(Constants.CPGUID, mStrCPGUID);
            intent.putExtra(Constants.CPUID, mStrRetUID);
            intent.putExtra(Constants.BeatGUID, mStrBeatGuid);
            startActivity(intent);
        }

    }

    private void onReturnOrderList() {
        Intent intent = new Intent(getContext(), ReturnOrderListActivity.class);
        intent.putExtra(Constants.CPNo, mStrRetID);
        intent.putExtra(Constants.RetailerName, mStrRetName);
        intent.putExtra(Constants.CPGUID, mStrCPGUID);
        intent.putExtra(Constants.CPUID, mStrRetUID);
        intent.putExtra(Constants.BeatGUID, mStrBeatGuid);
        intent.putExtra(Constants.ParentId, mStrParentId);
        startActivity(intent);
    }

    private void onSalesOrderList() {
        Intent intent = new Intent(getContext(), SalesOrderHeaderListActivity.class);
        intent.putExtra(Constants.CPNo, mStrRetID);
        intent.putExtra(Constants.RetailerName, mStrRetName);
        intent.putExtra(Constants.CPGUID, mStrCPGUID);
        intent.putExtra(Constants.CPUID, mStrRetUID);
        intent.putExtra(Constants.BeatGUID, mStrBeatGuid);
        intent.putExtra(Constants.ParentId, mStrParentId);
        startActivity(intent);
    }

    private void onInvoiceList() {
        Intent intent = new Intent(getContext(), InvoiceListActivity.class);
        intent.putExtra(Constants.CPNo, mStrRetID);
        intent.putExtra(Constants.RetailerName, mStrRetName);
        intent.putExtra(Constants.CPGUID32, mStrCPGUID);
        intent.putExtra(Constants.CPGUID, mStrCPGUID36);
        intent.putExtra(Constants.CPUID, mStrRetUID);
        intent.putExtra(Constants.BeatGUID, mStrBeatGuid);
        intent.putExtra(Constants.ParentId, mStrParentId);
        startActivity(intent);
    }

    private void onCollList() {
        Intent intent = new Intent(getContext(), CollectionListActivity.class);
        intent.putExtra(Constants.CPNo, mStrRetID);
        intent.putExtra(Constants.RetailerName, mStrRetName);
        intent.putExtra(Constants.CPGUID, mStrCPGUID);
        intent.putExtra(Constants.CPUID, mStrRetUID);
        intent.putExtra(Constants.BeatGUID, mStrBeatGuid);
        startActivity(intent);
    }
    private void onMerchList()
    {
        Intent intent = new Intent(getContext(), MerchandisingListActivity.class);
        intent.putExtra(Constants.CPNo, mStrRetID);
        intent.putExtra(Constants.RetailerName, mStrRetName);
        intent.putExtra(Constants.CPGUID, mStrCPGUID);
        intent.putExtra(Constants.CPUID, mStrRetUID);
        intent.putExtra(Constants.BeatGUID, mStrBeatGuid);
        intent.putExtra(Constants.ParentId, mStrParentId);
        startActivity(intent);
    }
    private void onFeedbackList()
    {
        Intent intent = new Intent(getContext(), FeedbackListActivity.class);
        intent.putExtra(Constants.CPNo, mStrRetID);
        intent.putExtra(Constants.RetailerName, mStrRetName);
        intent.putExtra(Constants.CPGUID, mStrCPGUID);
        intent.putExtra(Constants.CPUID, mStrRetUID);
        intent.putExtra(Constants.BeatGUID, mStrBeatGuid);
        intent.putExtra(Constants.ParentId, mStrParentId);
        startActivity(intent);
    }
    private void onOutstandingList()
    {
        Intent intent = new Intent(getContext(), OutstandingListActivity.class);
        intent.putExtra(Constants.CPNo, mStrRetID);
        intent.putExtra(Constants.RetailerName, mStrRetName);
        intent.putExtra(Constants.CPGUID32, mStrCPGUID);
        intent.putExtra(Constants.CPGUID, mStrCPGUID36);
        intent.putExtra(Constants.CPUID, mStrRetUID);
        intent.putExtra(Constants.BeatGUID, mStrBeatGuid);
        intent.putExtra(Constants.ParentId, mStrParentId);
        startActivity(intent);
    }
    private void onCompetitorList() {
        Intent intent = new Intent(getContext(), CompetitorListActivity.class);
        intent.putExtra(Constants.CPNo, mStrRetID);
        intent.putExtra(Constants.RetailerName, mStrRetName);
        intent.putExtra(Constants.CPGUID, mStrCPGUID36);
        intent.putExtra(Constants.CPGUID32, mStrCPGUID);
        intent.putExtra(Constants.BeatGUID, mStrBeatGuid);
        intent.putExtra(Constants.CPUID, mStrRetUID);
        startActivity(intent);
    }
    /*
     enable icons based on authorization t codes
     */
    private void setIconVisibility() {
        mArrIntMinVisibility = Constants.IconVisibiltyReportFragment;
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Constants.PREFS_NAME, 0);
        Constants.setIconVisibiltyReports(sharedPreferences, mArrIntMinVisibility);
        int iconCount = 0;
        for (int iconVisibleCount = 0; iconVisibleCount < mArrIntMinVisibility.length; iconVisibleCount++) {
            if (mArrIntMinVisibility[iconVisibleCount] == 1) {
                mArrIntIconPosition[iconCount] = iconVisibleCount;
                iconCount++;
            }
        }
    }
    private void onComplaintList()
    {
        Intent intent = new Intent(getContext(), ComplainListActivity.class);

        intent.putExtra(Constants.CPNo,mStrRetID);
        intent.putExtra(Constants.RetailerName, mStrRetName);
        intent.putExtra(Constants.CPGUID, mStrCPGUID);
        intent.putExtra(Constants.CPUID, mStrRetUID);
        intent.putExtra(Constants.BeatGUID, mStrBeatGuid);
        intent.putExtra(Constants.ParentId, mStrParentId);
        startActivity(intent);
    }
    private void onSampleDisbursement(){
        Intent intent = new Intent(getContext(), SampleDisbursementListActivity.class);

        intent.putExtra(Constants.CPNo,mStrRetID);
        intent.putExtra(Constants.RetailerName, mStrRetName);
        intent.putExtra(Constants.CPGUID32, mStrCPGUID);
        intent.putExtra(Constants.CPGUID, mStrCPGUID36);
        intent.putExtra(Constants.CPUID, mStrRetUID);
        intent.putExtra(Constants.BeatGUID, mStrBeatGuid);
        intent.putExtra(Constants.ParentId, mStrParentId);
        startActivity(intent);
    }
}
