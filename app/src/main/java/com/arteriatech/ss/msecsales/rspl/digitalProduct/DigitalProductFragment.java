package com.arteriatech.ss.msecsales.rspl.digitalProduct;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.arteriatech.mutils.adapter.AdapterInterface;
import com.arteriatech.mutils.adapter.SimpleRecyclerViewAdapter;
import com.arteriatech.ss.msecsales.rspl.R;
import com.arteriatech.ss.msecsales.rspl.common.ConstantsUtils;
import com.arteriatech.ss.msecsales.rspl.mbo.DocumentsBean;
import com.arteriatech.ss.msecsales.rspl.ui.GridItemDecoration;
import com.arteriatech.ss.msecsales.rspl.visualaid.VisualAidPresenterImpl;
import com.arteriatech.ss.msecsales.rspl.visualaid.VisualAidView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;

import id.zelory.compressor.Compressor;

/**
 * A simple {@link Fragment} subclass.
 */
public class DigitalProductFragment extends Fragment implements VisualAidView, SwipeRefreshLayout.OnRefreshListener, AdapterInterface<DocumentsBean> {

    private VisualAidPresenterImpl presenter;
    private SwipeRefreshLayout swipeRefresh;
    private RecyclerView recyclerView;
    private TextView noRecordFound;
    private SimpleRecyclerViewAdapter<DocumentsBean> simpleRecyclerViewAdapter;
    private String lastRefresh = "";

    public DigitalProductFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_digital_product, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        swipeRefresh = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefresh);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        noRecordFound = (TextView) view.findViewById(R.id.no_record_found);
        ConstantsUtils.setProgressColor(getContext(), swipeRefresh);
        swipeRefresh.setOnRefreshListener(this);
        recyclerView.setHasFixedSize(true);
        GridLayoutManager linearLayoutManager = new GridLayoutManager(getContext(), 2);
        recyclerView.setLayoutManager(linearLayoutManager);
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.padding_normal_16);
        recyclerView.addItemDecoration(new GridItemDecoration(2, spacingInPixels, true, 0));
        simpleRecyclerViewAdapter = new SimpleRecyclerViewAdapter<DocumentsBean>(getActivity(), R.layout.digital_prd_item, this, recyclerView, noRecordFound);
        recyclerView.setAdapter(simpleRecyclerViewAdapter);

        presenter = new VisualAidPresenterImpl(getActivity(), this);
        presenter.onDigitalPrdt();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void showProgress() {
        swipeRefresh.setRefreshing(true);
    }

    @Override
    public void hideProgress() {
        swipeRefresh.setRefreshing(false);
    }

    @Override
    public void showMessage(String msg) {
        ConstantsUtils.displayLongToast(getContext(), msg);
    }

    @Override
    public void displayList(ArrayList<DocumentsBean> alDocBean) {
        simpleRecyclerViewAdapter.refreshAdapter(alDocBean);
    }

    @Override
    public void onRefreshView() {

    }

    @Override
    public void displayLSTSyncTime(String time) {
        if (!TextUtils.isEmpty(time)) {
            lastRefresh = getString(R.string.po_last_refreshed) + " " + time;
        }
        ((DigitalProductActivity) getActivity()).displaySubTitle(lastRefresh);
    }

    @Override
    public void onDestroyView() {
        presenter.onDestroy();
        super.onDestroyView();
    }

    @Override
    public void onRefresh() {
        presenter.onRefresh();
    }

    @Override
    public void onItemClick(DocumentsBean documentsBean, View view, int i) {
        presenter.onItemClick(documentsBean);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i, View view) {
        return new DigitalPrdVH(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i, DocumentsBean documentsBean) {
        if (!TextUtils.isEmpty(documentsBean.getFileName())) {
            ((DigitalPrdVH) viewHolder).tvName.setText(documentsBean.getDisplayName());
        } else {
            ((DigitalPrdVH) viewHolder).tvName.setText(getString(R.string.visual_aidl_no_file));
        }
        if (!TextUtils.isEmpty(documentsBean.getImagePath())) {
//            BitmapFactory.Options options = new BitmapFactory.Options();
//            options.inSampleSize = 2;
            final Bitmap bitmap = Compressor.getDefault(getContext()).compressToBitmap(new File(documentsBean.getImagePath()));
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            final byte[] imageInByte = stream.toByteArray();
            Bitmap bitmap2 = BitmapFactory.decodeByteArray(imageInByte, 0, imageInByte.length);
            ((DigitalPrdVH) viewHolder).ivThumbNail.setImageBitmap(bitmap2);
        }

    }

}
