package com.arteriatech.ss.msecsales.rspl.sync.syncSelectionView;


import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.ss.msecsales.rspl.R;
import com.arteriatech.ss.msecsales.rspl.common.Constants;
import com.arteriatech.ss.msecsales.rspl.store.OfflineManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 */
public class PendingCountFragment extends Fragment {

    private RecyclerView recycler_view_Pending_count;
    public PendingCountFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pending_count, container, false);
        recycler_view_Pending_count = view.findViewById(R.id.recycler_view_Pending_count);
        List<PendingCountBean> pendingCount = getPendingCount();
        /*if (!pendingCount.isEmpty()) {
            recycler_view_Pending_count.setHasFixedSize(false);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getActivity());
            recycler_view_Pending_count.setLayoutManager(linearLayoutManager);
            PendingCountAdapter pendingCountAdapter = new PendingCountAdapter(pendingCount,);
            recycler_view_Pending_count.setAdapter(pendingCountAdapter);
        }*/
        return view;
    }

    private List<PendingCountBean> getPendingCount() {
        ArrayList<PendingCountBean> countBeanArrayList = new ArrayList<>();
        Set<String> set = new HashSet<>();
        PendingCountBean countBean = null;
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Constants.PREFS_NAME, 0);
        set = sharedPreferences.getStringSet(Constants.Feedbacks, null);
        if (set != null && !set.isEmpty()) {
            countBean = new PendingCountBean();
            countBean.setCollection(Constants.Feedbacks);
            countBean.setCount(set.size());
            countBeanArrayList.add(countBean);
        }

        set = sharedPreferences.getStringSet(Constants.SecondarySOCreate, null);
        if (set != null && !set.isEmpty()) {
            countBean = new PendingCountBean();
            countBean.setCollection("Sales Order");
            countBean.setCount(set.size());
            countBeanArrayList.add(countBean);
        }
        set = sharedPreferences.getStringSet(Constants.FinancialPostings, null);
        if (set != null && !set.isEmpty()) {
            countBean = new PendingCountBean();
            countBean.setCollection(Constants.Collections);
            countBean.setCount(set.size());
            countBeanArrayList.add(countBean);
        }
        set = sharedPreferences.getStringSet(Constants.SampleDisbursement, null);
        if (set != null && !set.isEmpty()) {
            countBean = new PendingCountBean();
            countBean.setCollection(Constants.SampleDisbursement);
            countBean.setCount(set.size());
            countBeanArrayList.add(countBean);
        }

        set = sharedPreferences.getStringSet(Constants.CPList, null);
        if (set != null && !set.isEmpty()) {
            int count = 0;
            countBean = new PendingCountBean();
            countBean.setCollection(Constants.Retailer);
            try {
                if (OfflineManager.getPendingCount(Constants.ChannelPartners + "?$filter= sap.islocal() ") > 0) {
                    count = OfflineManager.getPendingCount(Constants.ChannelPartners + "?$filter= sap.islocal() ");
                }
            } catch (OfflineODataStoreException e) {
                e.printStackTrace();
            }
            countBean.setCount(set.size() + count);
            countBeanArrayList.add(countBean);
        }else {
            try {
                if (OfflineManager.getPendingCount(Constants.ChannelPartners + "?$filter= sap.islocal() ") > 0) {
                    int count = OfflineManager.getPendingCount(Constants.ChannelPartners + "?$filter= sap.islocal() ");
                    countBean = new PendingCountBean();
                    countBean.setCollection(Constants.Retailer);
                    countBean.setCount(count);
                    countBeanArrayList.add(countBean);
                }
            } catch (OfflineODataStoreException e) {
                e.printStackTrace();
            }
        }

        set = sharedPreferences.getStringSet(Constants.ROList, null);
        if (set != null && !set.isEmpty()) {
            countBean = new PendingCountBean();
            countBean.setCollection("Return Order");
            countBean.setCount(set.size());
            countBeanArrayList.add(countBean);
        }

        set = sharedPreferences.getStringSet(Constants.Expenses, null);
        if (set != null && !set.isEmpty()) {
            countBean = new PendingCountBean();
            countBean.setCollection(Constants.Expenses);
            countBean.setCount(set.size());
            countBeanArrayList.add(countBean);
        }

        try {


            /*if (OfflineManager.getPendingCount(Constants.ChannelPartners + "?$filter= sap.islocal() ") > 0) {
                int count = OfflineManager.getPendingCount(Constants.ChannelPartners + "?$filter= sap.islocal() ");
                countBean = new PendingCountBean();
                countBean.setCollection(Constants.ChannelPartners);
                countBean.setCount(count);
                countBeanArrayList.add(countBean);
            }*/
           /* if (OfflineManager.getPendingCount(Constants.CPDMSDivisions + "?$filter= sap.islocal() ")>0) {
                int count = OfflineManager.getPendingCount(Constants.CPDMSDivisions + "?$filter= sap.islocal() ");
                countBean = new PendingCountBean();
                countBean.setCollection(Constants.CPDMSDivisions);
                countBean.setCount(count);
                countBeanArrayList.add(countBean);
            }*/

            if (OfflineManager.getPendingCount(Constants.Attendances + "?$filter= sap.islocal() ")>0) {
                int count = OfflineManager.getPendingCount(Constants.Attendances + "?$filter= sap.islocal() ");
                countBean = new PendingCountBean();
                countBean.setCollection(Constants.Attendances);
                countBean.setCount(count);
                countBeanArrayList.add(countBean);
            }
            if (OfflineManager.getPendingCount(Constants.Visits + "?$filter= sap.islocal() ")>0) {
                int count = OfflineManager.getPendingCount(Constants.Visits + "?$filter= sap.islocal() ");
                countBean = new PendingCountBean();
                countBean.setCollection(Constants.Visits);
                countBean.setCount(count);
                countBeanArrayList.add(countBean);
            }
            if (OfflineManager.getPendingCount(Constants.CompetitorInfos + "?$filter= sap.islocal() ")>0) {
                int count = OfflineManager.getPendingCount(Constants.CompetitorInfos + "?$filter= sap.islocal() ");
                countBean = new PendingCountBean();
                countBean.setCollection(Constants.PendingCompetitorInfos);
                countBean.setCount(count);
                countBeanArrayList.add(countBean);
            }

            if (OfflineManager.getPendingCount(Constants.MerchReviews + "?$filter= sap.islocal() ")>0) {
                int count = OfflineManager.getPendingCount(Constants.MerchReviews + "?$filter= sap.islocal() ");
                countBean = new PendingCountBean();
                countBean.setCollection(Constants.PendingMerchReviews);
                countBean.setCount(count);
                countBeanArrayList.add(countBean);
            }

            if (OfflineManager.getPendingCount(Constants.VisitActivities + "?$filter= sap.islocal() ")>0) {
                int count = OfflineManager.getPendingCount(Constants.VisitActivities + "?$filter= sap.islocal() ");
                countBean = new PendingCountBean();
                countBean.setCollection(Constants.PendingVisitActivities);
                countBean.setCount(count);
                countBeanArrayList.add(countBean);
            }
            if (OfflineManager.getPendingCount(Constants.SchemeCPDocuments + "?$filter= sap.islocal() ")>0) {
                int count = OfflineManager.getPendingCount(Constants.SchemeCPDocuments + "?$filter= sap.islocal() ");
                countBean = new PendingCountBean();
                countBean.setCollection(Constants.SchemeCPDocuments);
                countBean.setCount(count);
                countBeanArrayList.add(countBean);
            }
            if (OfflineManager.getPendingCount(Constants.Claims + "?$filter= sap.islocal() ")>0) {
                int count = OfflineManager.getPendingCount(Constants.Claims + "?$filter= sap.islocal() ");
                countBean = new PendingCountBean();
                countBean.setCollection(Constants.Claims);
                countBean.setCount(count);
                countBeanArrayList.add(countBean);
            }
            if (OfflineManager.getPendingCount(Constants.ClaimDocuments + "?$filter= sap.islocal() ")>0) {
                int count = OfflineManager.getPendingCount(Constants.ClaimDocuments + "?$filter= sap.islocal() ");
                countBean = new PendingCountBean();
                countBean.setCollection(Constants.ClaimDocuments);
                countBean.setCount(count);
                countBeanArrayList.add(countBean);
            }

            if (OfflineManager.getPendingCount(Constants.CPStockItems + "?$filter= sap.islocal() ")>0) {
                int count = OfflineManager.getPendingCount(Constants.CPStockItems + "?$filter= sap.islocal() ");
                countBean = new PendingCountBean();
                countBean.setCollection(Constants.PendingRetailerStock);
                countBean.setCount(count);
                countBeanArrayList.add(countBean);
            }
            if (OfflineManager.getPendingCount(Constants.SchemeCPs + "?$filter= sap.islocal() ")>0) {
                int count = OfflineManager.getPendingCount(Constants.SchemeCPs + "?$filter= sap.islocal() ");
                countBean = new PendingCountBean();
                countBean.setCollection(Constants.SchemeCPs);
                countBean.setCount(count);
                countBeanArrayList.add(countBean);
            }

            if (OfflineManager.getPendingCount(Constants.Complaints + "?$filter= sap.islocal() ")>0) {
                int count = OfflineManager.getPendingCount(Constants.Complaints + "?$filter= sap.islocal() ");
                countBean = new PendingCountBean();
                countBean.setCollection(Constants.Complaints);
                countBean.setCount(count);
                countBeanArrayList.add(countBean);
            }
            if (OfflineManager.getPendingCount(Constants.ExpenseDocuments + "?$filter= sap.islocal() ")>0) {
                int count = OfflineManager.getPendingCount(Constants.ExpenseDocuments + "?$filter= sap.islocal() ");
                countBean = new PendingCountBean();
                countBean.setCollection(Constants.ExpenseDocuments);
                countBean.setCount(count);
                countBeanArrayList.add(countBean);
            }
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }

        Collections.sort(countBeanArrayList, new Comparator<PendingCountBean>() {
                    @Override
                    public int compare(PendingCountBean countBean1, PendingCountBean countBean2) {
                        return countBean1.getCollection().compareTo(countBean2.getCollection());
                    }
                } );
        return countBeanArrayList;
    }

}
