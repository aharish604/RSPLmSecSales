package com.arteriatech.ss.msecsales.rspl.windowdisplay.windowdisplaycreate;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;

import com.arteriatech.mutils.interfaces.DialogCallBack;
import com.arteriatech.ss.msecsales.rspl.R;
import com.arteriatech.ss.msecsales.rspl.common.Constants;
import com.arteriatech.ss.msecsales.rspl.common.ConstantsUtils;
import static com.arteriatech.ss.msecsales.rspl.common.Constants.TAKE_PICTURE;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;

import id.zelory.compressor.Compressor;

public class SelfDisplayFragment extends Fragment implements OnLongClickInterFace {
    public static ArrayList<ExpenseImageBean> imageBeanList = new ArrayList<>();
    int maxImageCount = 0;
    private RecyclerView recyclerView;
    private WindowImageDisplayAdapter windowImageDisplayAdapter;
    private boolean isFirstTime = false;
    private int intComingFrom = 0;
    private String filename = "";
    File op = null;


    public SelfDisplayFragment() {

    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        imageBeanList.clear();
        if (bundle != null) {
            ArrayList<ExpenseImageBean> list = (ArrayList<ExpenseImageBean>) bundle.getSerializable(Constants.EXTRA_ARRAY_LIST);
            if (list != null) {
                imageBeanList.addAll(list);
            }
            isFirstTime = bundle.getBoolean(Constants.EXTRA_SCHEME_IS_SECONDTIME);
            intComingFrom = bundle.getInt(ConstantsUtils.EXTRA_FROM, 0);
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_contract, container, false);
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (intComingFrom == 0)
            maxImageCount = ConstantsUtils.getMaxImagesforWindowDis(ConstantsUtils.MAXCLMDOC);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        GridLayoutManager linearLayoutManager = new GridLayoutManager(getContext(), 2);
        recyclerView.setLayoutManager(linearLayoutManager);
        windowImageDisplayAdapter = new WindowImageDisplayAdapter(getContext(), imageBeanList,this);
        recyclerView.setAdapter(windowImageDisplayAdapter);
        refreshRecyclerView("", "", "", "", 0);
    }


    private void refreshRecyclerView(String path, String filename, String strMimeType, String mimeType, int mLongBitmapSize) {
        int totalSize = imageBeanList.size();
        if (checkEmptyImage(totalSize)) {
            if (!path.isEmpty()) {
                ExpenseImageBean expenseImageBean = new ExpenseImageBean();
                expenseImageBean.setImagePath(path);
                expenseImageBean.setDocumentMimeType(mimeType);
                expenseImageBean.setDocumentSize(mLongBitmapSize + "");
                expenseImageBean.setImageExtensions(strMimeType + "");
                expenseImageBean.setFileName(filename + "");
                expenseImageBean.setNewImage(true);
                imageBeanList.add(totalSize - 1, expenseImageBean);
            }
            checkMaxImage(maxImageCount);
        } else {
            if (isFirstTime) {
                imageBeanList.add(totalSize, getEmptyImage());
                if (!path.isEmpty()) {
                    totalSize = imageBeanList.size();
                    ExpenseImageBean expenseImageBeans = new ExpenseImageBean();
                    expenseImageBeans.setImagePath(path);
                    expenseImageBeans.setNewImage(true);
                    expenseImageBeans.setDocumentMimeType(mimeType);
                    expenseImageBeans.setDocumentSize(mLongBitmapSize + "");
                    expenseImageBeans.setImageExtensions(strMimeType + "");
                    expenseImageBeans.setFileName(filename + "");
                    imageBeanList.add(totalSize - 1, expenseImageBeans);
                }
            }
            windowImageDisplayAdapter.notifyDataSetChanged();
        }

    }
    private ExpenseImageBean getEmptyImage() {
        ExpenseImageBean expenseImageBean = new ExpenseImageBean();
        expenseImageBean.setImagePath("");
        return expenseImageBean;
    }

    /*check image is present or not*/
    private boolean checkEmptyImage(int size) {
        boolean emptyImageNotFound = false;
        if (size > 0) {
            ExpenseImageBean expenseImageBean = imageBeanList.get(size - 1);
            if (expenseImageBean.getImagePath().equalsIgnoreCase("")) {
                emptyImageNotFound = true;
            }
        }
        return emptyImageNotFound;

    }

    private void checkMaxImage(int maxImages) {
        if (maxImages > 0) {
            int totalImages = 0;
            for (ExpenseImageBean expenseImageBean : imageBeanList) {
                if (!expenseImageBean.getImagePath().equals("") && !expenseImageBean.getFileName().equals("") && expenseImageBean.isNewImage()) {
                    if (totalImages == maxImages) {
                        break;
                    }
                    totalImages++;
                }
            }
            if (totalImages == maxImages) {
                int totalSize = imageBeanList.size();
                imageBeanList.remove(totalSize - 1);

            }
        }
        windowImageDisplayAdapter.notifyDataSetChanged();
    }
    @Override
    public void onLongClickInterFace(View view, final int position, final ImageView imageView) {
        imageView.setBackgroundResource(R.drawable.select_image_border);
        ExpenseImageBean expenseImageBean = imageBeanList.get(position);
        if (!expenseImageBean.getImagePath().equalsIgnoreCase("") && expenseImageBean.isNewImage()) {
            Constants.dialogBoxWithButton(getContext(), "", "Are you sure delete this image?", getString(R.string.yes), getString(R.string.no), new DialogCallBack() {
                @Override
                public void clickedStatus(boolean clickedStatus) {
                    imageView.setBackgroundResource(R.drawable.unselect_image_border);
                    if (clickedStatus) {
                        imageBeanList.remove(position);
                        refreshRecyclerView("", "", "", "", 0);
                    }
                }
            });
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        try {
            String defaultCameraPackage = "";
           /* PackageManager packageManager = getActivity().getPackageManager();
            List<ApplicationInfo> list = packageManager.getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);
            for (int n = 0; n < list.size(); n++) {
                if ((list.get(n).flags & ApplicationInfo.FLAG_SYSTEM) == 1) {
                    if (list.get(n).loadLabel(packageManager).toString().equalsIgnoreCase("Camera")) {
                        defaultCameraPackage = list.get(n).packageName;
                        break;
                    }
                }
            }*/

//            Intent intentResult = new Intent("android.media.action.IMAGE_CAPTURE");
//            intentResult.setPackage(defaultCameraPackage);
//            startActivityForResult(intentResult, Constants.TAKE_PICTURE);

            Intent i = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            File out = Environment.getExternalStorageDirectory();
            filename = (System.currentTimeMillis() + ".jpg");
            out = new File(out, filename);
            op = out;
            if (Build.VERSION_CODES.N <= android.os.Build.VERSION.SDK_INT) {
                Uri photoURI = FileProvider.getUriForFile(getActivity(),
                        getActivity().getPackageName() + ".provider",
                        out);
                i.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            }
            else {
                i.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(out));
            }
            startActivityForResult(i, TAKE_PICTURE);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == TAKE_PICTURE && resultCode == getActivity().RESULT_OK) {

            File f = new File(Environment.getExternalStorageDirectory().toString());
            for (File temp : f.listFiles()) {
                if (temp.getName().equals(filename)) {
                    f = temp;
                    break;
                }
            }
            try {
                final Bitmap bitMap = Compressor.getDefault(getActivity()).compressToBitmap(f);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitMap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                final byte[] imageInByte = stream.toByteArray();
                String[] projection = {MediaStore.Images.Media.DATA};
//            @SuppressWarnings("deprecation")
                Cursor cursorMediaValue = getActivity().managedQuery(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null, null, null);
                cursorMediaValue.moveToLast();
                File fileName = Constants.SaveImageInDevice(filename, bitMap);
                //mime
                int mLongBitmapSize = imageInByte.length;
                String strMimeType = MimeTypeMap.getFileExtensionFromUrl(fileName.getPath());
                String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                        strMimeType);


                refreshRecyclerView(fileName.getPath(), filename, strMimeType, mimeType, mLongBitmapSize);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}