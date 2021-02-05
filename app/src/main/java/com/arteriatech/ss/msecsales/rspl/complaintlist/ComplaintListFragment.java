package com.arteriatech.ss.msecsales.rspl.complaintlist;

import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.arteriatech.ss.msecsales.rspl.R;
import com.arteriatech.ss.msecsales.rspl.common.Constants;
import com.arteriatech.ss.msecsales.rspl.common.ConstantsUtils;
import java.util.ArrayList;
public class ComplaintListFragment extends Fragment implements ComplaintListView,SwipeRefreshLayout.OnRefreshListener{
    Context context;
    Toolbar toolbar;
    ComplaintListPresenterImpl compliantListPresenter;
    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    SwipeRefreshLayout swiperefreshlayout;
    ComplaintListAdapter adapter;
    private static final String ARG_CPNO = "argcpno";
    String mcpNo;
    String nRetailerName;
    String comingFrom;
    Bundle bundleExt;
    TextView no_record_found;
    private String syncType = "";
    private String parentId = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bundleExt = getArguments();
        if (bundleExt != null) {
            mcpNo = bundleExt.getString(Constants.CPNo, "");
            nRetailerName = bundleExt.getString(Constants.RetailerName, "");
            comingFrom = bundleExt.getString(Constants.comingFrom);
            syncType = bundleExt.getString(Constants.SyncType);
            parentId = bundleExt.getString(Constants.ParentId);
        }
        setHasOptionsMenu(true);
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_complaint_list, container, false);
        context=container.getContext();
        toolbar=(Toolbar)view.findViewById(R.id.toolbar);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        no_record_found = (TextView) view.findViewById(R.id.no_record_found);
        swiperefreshlayout = (SwipeRefreshLayout) view.findViewById(R.id.swiperefreshlayout);
        linearLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(linearLayoutManager);
        swiperefreshlayout.setOnRefreshListener(this);
        return view;
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getComplaintsList();
    }
    private void getComplaintsList() {
        compliantListPresenter=new ComplaintListPresenterImpl(context, this,bundleExt,mcpNo);
        compliantListPresenter.onStart(mcpNo);
    }
    @Override
    public void setComplaintListDatatoAdapter(ArrayList<ComplaintListModel> list) {
        adapter = new ComplaintListAdapter(context, list,bundleExt);
        if(list.size()==0||list==null)
        {
            no_record_found.setVisibility(View.VISIBLE);
        }
        else
        {
            recyclerView.setAdapter(adapter);

        }

    }
    @Override
    public void loadProgressBar() {
        swiperefreshlayout.setRefreshing(true);
    }
    @Override
    public void hideProgressBar() {
        swiperefreshlayout.setRefreshing(false);
    }

    @Override
    public void setSearchedComplaintListDatatoAdapter(ArrayList<ComplaintListModel> list) {
        adapter = new ComplaintListAdapter(context, list,bundleExt);
        if(list.size()==0||list==null)
        {
            no_record_found.setVisibility(View.VISIBLE);
        }
        else
        {
            recyclerView.setAdapter(adapter);

        }

    }
    @Override
    public void showMessage(String msg) {

        ConstantsUtils.displayLongToast(getContext(), msg);
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        if(comingFrom.equalsIgnoreCase(Constants.Device)) {
            inflater.inflate(R.menu.menu_sync, menu);
        }else
        {
            inflater.inflate(R.menu.menu_search, menu);
            SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
            SearchableInfo searchInfo = searchManager.getSearchableInfo(getActivity().getComponentName());
            SearchView mSearchView = (SearchView) menu.findItem(R.id.menu_search_item).getActionView();
            mSearchView.setSearchableInfo(searchInfo);
            mSearchView.setQueryHint(getString(R.string.lbl_complaint_doc_num_search));
            mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    compliantListPresenter.searchComplaint(query);
                    return false;
                }
                @Override
                public boolean onQueryTextChange(String newText) {
                    compliantListPresenter.searchComplaint(newText);
                    return false;
                }
            });
        }

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.menu_sync:
                    onRefresh();
                    return true;
                default:
                    return super.onOptionsItemSelected(item);
            }

    }
    @Override
    public void onRefresh() {
        if (comingFrom.equalsIgnoreCase(Constants.Device)) {

            compliantListPresenter.onUploadData();
        } else {
            compliantListPresenter.onRefresh();
        }
    }
}