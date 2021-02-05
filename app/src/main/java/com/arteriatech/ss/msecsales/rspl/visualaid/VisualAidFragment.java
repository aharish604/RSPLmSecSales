package com.arteriatech.ss.msecsales.rspl.visualaid;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.provider.MediaStore;
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
import com.arteriatech.ss.msecsales.rspl.common.Constants;
import com.arteriatech.ss.msecsales.rspl.common.ConstantsUtils;
import com.arteriatech.ss.msecsales.rspl.mbo.DocumentsBean;
import com.arteriatech.ss.msecsales.rspl.ui.GridItemDecoration;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class VisualAidFragment extends Fragment implements VisualAidView, SwipeRefreshLayout.OnRefreshListener, AdapterInterface<DocumentsBean> {


    private VisualAidPresenterImpl presenter;
    private SwipeRefreshLayout swipeRefresh;
    private RecyclerView recyclerView;
    private TextView noRecordFound;
    private SimpleRecyclerViewAdapter<DocumentsBean> simpleRecyclerViewAdapter;
    private String lastRefresh = "";
    private int[] imageId = {
            R.drawable.ic_picture_as_pdf_black_24dp,
            R.drawable.ic_insert_drive_file_black_24dp,
            R.drawable.ic_airplay_black_24dp,
            R.drawable.ic_photo_black_24dp,
            R.drawable.ic_video_library_black_24dp,
            R.drawable.ic_xls,
            R.drawable.ic_video_library_black_24dp
            // R.drawable.ic_videocam_black_24dp,
    };

    public VisualAidFragment() {
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
        return inflater.inflate(R.layout.fragment_visual_aid, container, false);
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
        GridLayoutManager linearLayoutManager = new GridLayoutManager(getContext(),2);
        recyclerView.setLayoutManager(linearLayoutManager);
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.padding_normal_8);
        recyclerView.addItemDecoration(new GridItemDecoration(2,spacingInPixels,true,0));
        simpleRecyclerViewAdapter = new SimpleRecyclerViewAdapter<DocumentsBean>(getActivity(), R.layout.visual_list_item, this, recyclerView, noRecordFound);
        recyclerView.setAdapter(simpleRecyclerViewAdapter);

        presenter = new VisualAidPresenterImpl(getActivity(), this);
        presenter.onStart();
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
        ((VisualAidActivity) getActivity()).displaySubTitle(lastRefresh);
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
        return new VisualAidVH(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i, DocumentsBean documentsBean) {
        if (!TextUtils.isEmpty(documentsBean.getFileName())) {
            ((VisualAidVH) viewHolder).tvName.setText(documentsBean.getDisplayName());
        } else {
            ((VisualAidVH) viewHolder).tvName.setText(getString(R.string.visual_aidl_no_file));
        }
        if (Constants.MimeTypePDF.equalsIgnoreCase(documentsBean.getDocumentMimeType())) {
            ((VisualAidVH) viewHolder).ivThumbNail.setImageResource(imageId[0]);
            ((VisualAidVH) viewHolder).ivDataFormat.setImageResource(imageId[0]);
        } else if (Constants.MimeTypeDocx.equalsIgnoreCase(documentsBean.getDocumentMimeType()) || Constants.MimeTypeMsword.equalsIgnoreCase(documentsBean.getDocumentMimeType()) || Constants.MimeTypeDOCx.equalsIgnoreCase(documentsBean.getDocumentMimeType())) {
            ((VisualAidVH) viewHolder).ivThumbNail.setImageResource(imageId[1]);
            ((VisualAidVH) viewHolder).ivDataFormat.setImageResource(imageId[1]);
        } else if (Constants.MimeTypePPT.equalsIgnoreCase(documentsBean.getDocumentMimeType()) || Constants.MimeTypevndmspowerpoint.equalsIgnoreCase(documentsBean.getDocumentMimeType())|| Constants.MimeTypePPTX.equalsIgnoreCase(documentsBean.getDocumentMimeType())) {
            ((VisualAidVH) viewHolder).ivThumbNail.setImageResource(imageId[2]);
            ((VisualAidVH) viewHolder).ivDataFormat.setImageResource(imageId[2]);
        }else if (Constants.MimeTypeXLS.equalsIgnoreCase(documentsBean.getDocumentMimeType()) || Constants.MimeTypeXLSX.equalsIgnoreCase(documentsBean.getDocumentMimeType())) {
            ((VisualAidVH) viewHolder).ivThumbNail.setImageResource(imageId[5]);
            ((VisualAidVH) viewHolder).ivDataFormat.setImageResource(imageId[5]);
        }  else if (Constants.MimeTypevideomp4.equalsIgnoreCase(documentsBean.getDocumentMimeType())|| Constants.MimeTypeMP4.equalsIgnoreCase(documentsBean.getDocumentMimeType())) {
            ((VisualAidVH) viewHolder).ivThumbNail.setImageResource(imageId[4]);
            ((VisualAidVH) viewHolder).ivDataFormat.setImageResource(imageId[4]);
            if (!TextUtils.isEmpty(documentsBean.getImagePath())) {
                try {
                    Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(documentsBean.getImagePath(), MediaStore.Video.Thumbnails.MICRO_KIND);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    final byte[] imageInByte = stream.toByteArray();
                    Bitmap bitmap2 = BitmapFactory.decodeByteArray(imageInByte, 0, imageInByte.length);
                    ((VisualAidVH) viewHolder).ivThumbNail.setImageBitmap(bitmap2);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }else if( !TextUtils.isEmpty(documentsBean.getDocumentMimeType()) && Constants.MimeTypeUrl.equalsIgnoreCase(documentsBean.getDocumentMimeType())){
            ((VisualAidVH) viewHolder).ivThumbNail.setImageResource(imageId[6]);
            ((VisualAidVH) viewHolder).ivDataFormat.setImageResource(imageId[6]);
        }
        ((VisualAidVH) viewHolder).tvDate.setText(documentsBean.getCreatedOn());
    }
}
