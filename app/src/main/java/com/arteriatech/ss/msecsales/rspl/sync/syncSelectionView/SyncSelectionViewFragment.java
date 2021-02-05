package com.arteriatech.ss.msecsales.rspl.sync.syncSelectionView;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.arteriatech.mutils.adapter.AdapterInterface;
import com.arteriatech.mutils.adapter.SimpleRecyclerViewAdapter;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.interfaces.DialogCallBack;
import com.arteriatech.mutils.log.LogManager;
import com.arteriatech.ss.msecsales.rspl.R;
import com.arteriatech.ss.msecsales.rspl.common.Constants;
import com.arteriatech.ss.msecsales.rspl.common.ConstantsUtils;
import com.arteriatech.ss.msecsales.rspl.networkmonitor.ITrafficSpeedListener;
import com.arteriatech.ss.msecsales.rspl.networkmonitor.TrafficSpeedMeasurer;
import com.arteriatech.ss.msecsales.rspl.networkmonitor.Utils;

import java.io.IOException;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class SyncSelectionViewFragment extends Fragment implements AdapterInterface<SyncSelectionViewBean> {
    private ArrayList<SyncSelectionViewBean> syncSelectionViewBeanArrayList = null;
    private RecyclerView recyclerView;
    //    private TextView tvNoRecordFound;
    private SimpleRecyclerViewAdapter<SyncSelectionViewBean> simpleRecyclerViewAdapter;
    private SyncSelectionViewInterface syncSelectionInterface = null;
    private boolean onBind = false;
    private boolean isClickable = false;
    private TrafficSpeedMeasurer mTrafficSpeedMeasurer;
    private static final boolean SHOW_SPEED_IN_BITS = false;
    private int networkErrorCount=0,networkError=0;
    Thread networkThread;
    private boolean isMonitoringStopped = false;
    public SyncSelectionViewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            syncSelectionInterface = (SyncSelectionViewInterface) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            syncSelectionViewBeanArrayList = (ArrayList<SyncSelectionViewBean>) bundle.getSerializable(Constants.EXTRA_BEAN_LIST);
        }
        if (syncSelectionViewBeanArrayList != null) {
            SyncSelectionViewBean syncSelectionViewBean = new SyncSelectionViewBean();
            syncSelectionViewBean.setChecked(false);
            syncSelectionViewBean.setAll(true);
            syncSelectionViewBean.setDisplayName("All");
            syncSelectionViewBeanArrayList.add(0, syncSelectionViewBean);
        }

        SharedPreferences mSharedPrefs = getActivity().getSharedPreferences(Constants.LOGPREFS_NAME, 0);
        if (mSharedPrefs.getBoolean("writeDBGLog", false)) {
            Constants.writeDebug = mSharedPrefs.getBoolean("writeDBGLog", false);
        }
        mTrafficSpeedMeasurer = new TrafficSpeedMeasurer(TrafficSpeedMeasurer.TrafficType.ALL);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sync_selection_view, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
//        tvNoRecordFound = (TextView) view.findViewById(R.id.no_record_found);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        simpleRecyclerViewAdapter = new SimpleRecyclerViewAdapter<SyncSelectionViewBean>(getContext(), R.layout.sync_selection_item, this, recyclerView, null);
        recyclerView.setAdapter(simpleRecyclerViewAdapter);
        simpleRecyclerViewAdapter.refreshAdapter(syncSelectionViewBeanArrayList);
        setHasOptionsMenu(true);
    }

    @Override
    public void onItemClick(SyncSelectionViewBean item, View view, int position) {

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType, View viewItem) {
        return new SyncSelectionVH(viewItem);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position, final SyncSelectionViewBean syncSelectionViewBean) {
        ((SyncSelectionVH) holder).cbCollection.setText(syncSelectionViewBean.getDisplayName());
        ((SyncSelectionVH) holder).cbCollection.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                syncSelectionViewBean.setChecked(isChecked);
                if (syncSelectionViewBean.isAll()) {
                    selectAllItems(syncSelectionViewBeanArrayList, isChecked);
                }
            }
        });
        onBind = true;
        ((SyncSelectionVH) holder).cbCollection.setChecked(syncSelectionViewBean.isChecked());
        onBind = false;
    }

    private void selectAllItems(ArrayList<SyncSelectionViewBean> syncSelectionViewBeanArrayList, boolean selectStatus) {
        for (SyncSelectionViewBean syncSelectionViewBean : syncSelectionViewBeanArrayList) {
            syncSelectionViewBean.setChecked(selectStatus);
        }
        if (!onBind)
            simpleRecyclerViewAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_sync, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_sync:
                if (UtilConstants.isNetworkAvailable(getContext())) {
                    if (Constants.isPullDownSync||Constants.iSAutoSync||Constants.isBackGroundSync) {
                        if (Constants.iSAutoSync){
                            showAlert(getString(R.string.alert_auto_sync_is_progress));
                        }else{
                            showAlert(getString(R.string.alert_backgrounf_sync_is_progress));
                        }
                    }else{
                        if(!isClickable) {
                            isClickable = true;
                            onSync();
                        }
                    }
                } else {
                    showAlert(getString(R.string.data_conn_lost_during_sync));
                }
                break;
        }
        return true;
    }

    private void startNetworkMonitoring(){
        mTrafficSpeedMeasurer.startMeasuring();
        isMonitoringStopped = true;
        checkNetwork(getContext(), new OnNetworkInfoListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onNetworkFailureListener(boolean isFailed) {
                if (isFailed) {
                   /* ((Activity) getContext()).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //  Toast.makeText(SyncSelectionActivity.this, "Network dropped / unable to connect.", Toast.LENGTH_SHORT).show();
                        }
                    });*/
                }
            }
        },false,0);

    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        mTrafficSpeedMeasurer.stopMeasuring();
    }

    @Override
    public void onResume() {
        super.onResume();
        isClickable=false;
        mTrafficSpeedMeasurer.registerListener(mStreamSpeedListener);

    }

    @Override
    public void onPause() {
        super.onPause();
        mTrafficSpeedMeasurer.removeListener(mStreamSpeedListener);
    }
    private ITrafficSpeedListener mStreamSpeedListener = new ITrafficSpeedListener() {

        @Override
        public void onTrafficSpeedMeasured(final double upStream, final double downStream) {
            if (getActivity() != null){
                ((Activity) getContext()).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String upStreamSpeed = Utils.parseSpeed(upStream, SHOW_SPEED_IN_BITS);
                        String downStreamSpeed = Utils.parseSpeed(downStream, SHOW_SPEED_IN_BITS);
                        if (upStream <= 0 || downStream <= 0)
                            networkErrorCount++;
                        else
                            networkErrorCount = 0;

                        if ((upStream != 0 && upStream < 1) || (downStream != 0 && downStream < 1))
                            networkError++;
                        else
                            networkError = 0;

                        if (networkErrorCount >= 3) {
                            networkErrorCount = 0;
                            isMonitoringStopped = false;
                            mTrafficSpeedMeasurer.stopMeasuring();
                            mTrafficSpeedMeasurer.removeListener(mStreamSpeedListener);
                            syncSelectionInterface.closeProgrDialog();
                            UtilConstants.dialogBoxWithCallBack(getContext(), "", "Sync can't perform due to network unavailability", getString(R.string.ok), getString(R.string.cancel), false, new DialogCallBack() {
                                @Override
                                public void clickedStatus(boolean b) {
                                    if (b) {
                                        onBackPressed();
                                    }
                                }
                            });
                        } else if (networkError >= 3) {
                            networkError = 0;
                            isMonitoringStopped = false;
                            mTrafficSpeedMeasurer.stopMeasuring();
                            mTrafficSpeedMeasurer.removeListener(mStreamSpeedListener);
                            syncSelectionInterface.closeProgrDialog();
                            UtilConstants.dialogBoxWithCallBack(getContext(), "", "Sync can't perform due to low network bandwidth", getString(R.string.ok), getString(R.string.cancel), false, new DialogCallBack() {
                                @Override
                                public void clickedStatus(boolean b) {
                                    if (b) {
                                        onBackPressed();
                                    }
                                }
                            });
                        }

                        Log.d("Network_Bandwidth", "Values" + upStreamSpeed + "--" + downStreamSpeed);
                    }
                });
        }
        }
    };


    private void onBackPressed() {
        getActivity().finish();
    }

    private static boolean isActiveNetwork(Context context){
        return isConnected(context);
    }
    private static boolean isConnected(Context context){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isWiFi = false;
        boolean isMobile = false;
        boolean isConnected = false;
        if (activeNetwork != null) {
            isWiFi = activeNetwork.getType() == ConnectivityManager.TYPE_WIFI;
            isMobile = activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE;
            isConnected = activeNetwork.isConnectedOrConnecting();
        }
        if (isConnected) {
            if (isWiFi) {
                return isConnectedToThisServer();
            }
            if (isMobile) {
                return isConnectedToThisServer();
            }
        } else {
            return false;
        }
        return false;
    }
    private static boolean isConnectedToThisServer() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }
    public interface OnNetworkInfoListener{
        void onNetworkFailureListener(boolean isFailed);
    }
    private static boolean isNetworkStopped;
    public  void checkNetwork(final Context context, final OnNetworkInfoListener networkInfoListener, boolean isInterupted, final int delayInSec){
        if (!isInterupted) {
            isNetworkStopped=false;
            networkThread  = new Thread(new Runnable() {
                @Override
                public void run() {
                    check(context, networkInfoListener,delayInSec);
                }
            });
            networkThread.start();
        }else{
            isNetworkStopped =true;
        }
    }
    private  void check(Context context,OnNetworkInfoListener networkInfoListener, int delayInSec){
        if (!isNetworkStopped) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            boolean isError = isActiveNetwork(context);
            if (!isError) {
                networkErrorCount++;
                if(networkErrorCount>=3){
                    networkError=0;
                    isMonitoringStopped = false;
                    mTrafficSpeedMeasurer.stopMeasuring();
                    mTrafficSpeedMeasurer.removeListener(mStreamSpeedListener);
                    syncSelectionInterface.closeProgrDialog();
                    UtilConstants.dialogBoxWithCallBack(getContext(), "", "Sync can't perform due to network unavailability", getString(R.string.ok), getString(R.string.cancel), false, new DialogCallBack() {
                        @Override
                        public void clickedStatus(boolean b) {
                            if (b) {
                                onBackPressed();
                            }
                        }
                    });
                }

                Log.e("CHECKING NETWORK", "NETWORK ERROR");

                if (networkInfoListener != null && isMonitoringStopped) {
                    networkInfoListener.onNetworkFailureListener(true);
                    check(context, networkInfoListener,delayInSec);
                }
            } else {
                if (networkInfoListener != null && isMonitoringStopped) {
                    Log.e("CHECKING NETWORK", "NETWORK ACTIVE");
                    networkInfoListener.onNetworkFailureListener(false);
                    check(context, networkInfoListener, delayInSec);
                }
            }
        }
    }

    private void onSync() {

        ArrayList<String> selectedCollection = new ArrayList<>();
        for (SyncSelectionViewBean syncSelectionViewBean : syncSelectionViewBeanArrayList) {
            if (syncSelectionViewBean.isChecked()) {
                selectedCollection.addAll(syncSelectionViewBean.getCollectionName());
            }
        }
        if (!selectedCollection.isEmpty()) {
            for (int i = 0; i < selectedCollection.size(); i++) {
                if(Constants.writeDebug){
                    LogManager.writeLogDebug(" Download Sync Starts : "+selectedCollection.get(i));
                }

            }
          //  startNetworkMonitoring();

            syncSelectionInterface.onSelectedCollection(selectedCollection);
        } else {
            UtilConstants.showAlert(getString(R.string.plz_select_one_coll), getContext());
        }
    }
    private void showAlert(String message){
        ConstantsUtils.showAlert(message, getContext(), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                isClickable=false;
                dialog.cancel();
            }
        });
    }
}
