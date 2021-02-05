package com.arteriatech.ss.msecsales.rspl.merchandising;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.core.content.FileProvider;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.Operation;
import com.arteriatech.mutils.common.UIListener;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.interfaces.DialogCallBack;
import com.arteriatech.mutils.location.LocationInterface;
import com.arteriatech.mutils.location.LocationModel;
import com.arteriatech.mutils.log.LogManager;
import com.arteriatech.mutils.store.OnlineODataInterface;
import com.arteriatech.ss.msecsales.rspl.R;
import com.arteriatech.ss.msecsales.rspl.common.Constants;
import com.arteriatech.ss.msecsales.rspl.common.ConstantsUtils;
import com.arteriatech.ss.msecsales.rspl.customers.CustomerDetailsActivity;
import com.arteriatech.ss.msecsales.rspl.mbo.ValueHelpBean;
import com.arteriatech.ss.msecsales.rspl.store.OfflineManager;
import com.sap.client.odata.v4.core.GUID;
import com.sap.smp.client.odata.ODataDuration;
import com.sap.smp.client.odata.ODataEntity;
import com.sap.smp.client.odata.exception.ODataException;
import com.sap.smp.client.odata.impl.ODataDurationDefaultImpl;
import com.sap.smp.client.odata.store.ODataRequestExecution;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.List;

import id.zelory.compressor.Compressor;

/**
 * Created by e10769 on 25-Apr-18.
 */

public class MerchandisingCreatePresenterImpl implements MerchandisingCreatePresenter, OnlineODataInterface, UIListener {

    private final ODataDuration mStartTimeDuration;
    private Activity mContext;
    private MerchandisingCreateView mView = null;
    private ArrayList<ValueHelpBean> alMerchTypeList = new ArrayList<>();
    private String filename = "";
    private int mLongBitmapSize = 0;
    private boolean mBooleanPictureTaken = false;
    private String selectedImagePath = "";
    private String strMimeType = "";
    private String mimeType = "";
    private ValueHelpBean valueHelpBean = null;
    private boolean mBoolHeaderPosted = false;
    private Hashtable tableHdr = null;
    private Hashtable tableItm = null;
    private String mStrVisitActRefID = "";
    private String remark = "";
    private String[][] mArraySPValues = null;
    private String[][] mArrayDistributors = null;
    private String mStrBundleRetID = "";
    private String mStrBundleRetName = "";
    private String mStrBundleCPGUID = "";
    private String mStrBundleRetailerUID = "";
    private String mStrComingFrom = "";
    private String mStrBundleCPGUID32 = "";
    private String beatGUID = "";
    private String parentId = "";

    public MerchandisingCreatePresenterImpl(Activity mContext, MerchandisingCreateView mView, Bundle bundleExtras) {
        this.mContext = mContext;
        this.mView = mView;
        if (bundleExtras != null) {
            mStrBundleRetID = bundleExtras.getString(Constants.CPNo);
            mStrBundleRetName = bundleExtras.getString(Constants.RetailerName);
            mStrBundleCPGUID = bundleExtras.getString(Constants.CPGUID);
            mStrBundleCPGUID32 = bundleExtras.getString(Constants.CPGUID32);
            mStrBundleRetailerUID = bundleExtras.getString(Constants.CPUID);
            mStrComingFrom = bundleExtras.getString(Constants.comingFrom);
            beatGUID = bundleExtras.getString(Constants.BeatGUID);
            parentId = bundleExtras.getString(Constants.ParentId);
        }
        this.mStartTimeDuration = UtilConstants.getOdataDuration();
    }

    @Override
    public void onStart() {
        if (mView != null) {
            mView.showProgress();
        }
        String mStrConfigQry = Constants.ValueHelps + "?$filter=" + Constants.PropName + " eq '" + Constants.MerchReviewType + "' &$orderby=ID asc";
        ConstantsUtils.onlineRequest(mContext, mStrConfigQry, false, 3, ConstantsUtils.SESSION_HEADER, this, false);

    }

    @Override
    public void onDestroy() {
        mView = null;
    }

    @Override
    public void openCameraIntent() {
        try {
          /*  PackageManager packageManager = getPackageManager();
            List<ApplicationInfo> list = packageManager.getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);
            for (int n = 0; n < list.size(); n++) {
                if ((list.get(n).flags & ApplicationInfo.FLAG_SYSTEM) == 1) {
                    if (list.get(n).loadLabel(packageManager).toString().equalsIgnoreCase("Camera")) {
                        defaultCameraPackage = list.get(n).packageName;
                        break;
                    }
                }
            }*/

            Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            File out = Environment.getExternalStorageDirectory();
            filename = (System.currentTimeMillis() + ".jpg");
            out = new File(out, filename);
            if (Build.VERSION_CODES.N <= Build.VERSION.SDK_INT) {
                Uri photoURI = FileProvider.getUriForFile(mContext, mContext.getApplicationContext().getPackageName() + ".provider", out);
                i.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            } else {
                i.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(out));
            }
            mContext.startActivityForResult(i, ConstantsUtils.CAMERA_REQUEST_CODE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ConstantsUtils.CAMERA_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            File f = new File(Environment.getExternalStorageDirectory().toString());
            for (File temp : f.listFiles()) {
                if (temp.getName().equals(filename)) {
                    f = temp;
                    break;
                }
            }
            try {
                final Bitmap bitMap = Compressor.getDefault(mContext).compressToBitmap(f);
//                BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
//                final Bitmap bitMap = BitmapFactory.decodeFile(f.getAbsolutePath(), bitmapOptions);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
//                assert bitMap != null;
                bitMap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
//                bitMap.compress(Bitmap.CompressFormat.JPEG, 150, stream);
                final byte[] imageInByte = stream.toByteArray();
                mLongBitmapSize = imageInByte.length;
//                yourimageview.setImageBitmap(bitmap);

//                String[] projection = {MediaStore.Images.Media.DATA};
//                Cursor cursorMediaValue = mContext.managedQuery(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null, null, null);
//                cursorMediaValue.moveToLast();
                mBooleanPictureTaken = true;
                File fileName = Constants.SaveImageInDevice(filename, bitMap);
                selectedImagePath = fileName.getPath();
                //mime
                strMimeType = MimeTypeMap.getFileExtensionFromUrl(selectedImagePath);
                mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(strMimeType);
                if (mView != null) {
                    mView.displayImages(bitMap, imageInByte);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (ConstantsUtils.GALLERY_REQUEST_CODE == requestCode && resultCode == Activity.RESULT_OK) {
            try {


                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = mContext.getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                selectedImagePath = cursor.getString(columnIndex);
                cursor.close();
                strMimeType = MimeTypeMap.getFileExtensionFromUrl(selectedImagePath);
                mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(strMimeType);

                final Bitmap bitMap = Compressor.getDefault(mContext).compressToBitmap(new File(selectedImagePath));
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitMap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                final byte[] imageInByte = stream.toByteArray();
                mLongBitmapSize = imageInByte.length;
                mBooleanPictureTaken = true;
                if (mView != null) {
                    mView.displayImages(bitMap, imageInByte);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void onSave(String remark) {
        this.remark = remark;
        if (valueHelpBean == null) {
            if (mView != null) {
                mView.displayMerchTypeError("Select type");
            }
        } else if (TextUtils.isEmpty(valueHelpBean.getID())) {
            if (mView != null) {
                mView.displayMerchTypeError("Select type");
            }
        } else if ((valueHelpBean.getTypeValue().equalsIgnoreCase(Constants.X) || valueHelpBean.getDescription().equalsIgnoreCase("Others"))&& remark.trim().equalsIgnoreCase("")) {
            if (mView != null) {
                mView.displayRemarkError("Enter remarks");
            }
        } else {
            if (mBooleanPictureTaken) {
                if (Constants.checkPermission(mContext)) {
                    checkGPS();
                } else {
                    if (mView != null) {
                        mView.requestPermission();
                    }
                }
            } else {
                if (mView != null) {
                    mView.displayMsg(mContext.getString(R.string.take_pic));
                }
            }
        }
    }

    private void checkGPS() {
        if (mView != null) {
            mView.showProgress();
        }
        Constants.getLocation(mContext, new LocationInterface() {
            @Override
            public void location(boolean status, LocationModel locationModel, String errorMsg, int errorCode) {
                if (mView != null) {
                    mView.hideProgress();
                }
                if (status) {
                    mBoolHeaderPosted = false;
                    onSaveDB();
                }
            }
        });
    }

    private void getSalesPersonValues() {
        mArraySPValues = Constants.getSPValesFromCPDMSDivisionByCPGUIDAndDMSDivision(mStrBundleCPGUID,mContext);
    }

    private void getDistributorValues() {
        mArrayDistributors = Constants.getDistributorsByCPGUID(mContext, mStrBundleCPGUID,parentId);
    }

    private void onSaveDB() {
//        if (Constants.isValidTime(UtilConstants.convertTimeOnly(Constants.mStartTimeDuration.toString()),
//                UtilConstants.convertTimeOnly(UtilConstants.getOdataDuration().toString())) && Constants.isValidDate()) {
       /* if (!Constants.onGpsCheck(MerchndisingActivity.this)) {
            return;
        }
        if(!UtilConstants.getLocation(MerchndisingActivity.this)){
            return;
        }*/
        try {
            Constants.writeDebugLog(mContext,"Merchandising Create data saving");

            GUID mStrGuide = GUID.newRandom();
            tableHdr = new Hashtable();
            Hashtable visitActivityTable = new Hashtable();
            //noinspection unchecked
            tableHdr.put(Constants.MerchReviewGUID, mStrGuide.toString());
            //noinspection unchecked
            tableHdr.put(Constants.Remarks, remark);
            //noinspection unchecked
            tableHdr.put(Constants.CPNo, UtilConstants.removeLeadingZeros(mStrBundleRetID));
            //noinspection unchecked
            tableHdr.put(Constants.CPGUID, mStrBundleCPGUID32);

            tableHdr.put(Constants.SPGUID, Constants.getSPGUID());
            tableHdr.put(Constants.ParentID, mArrayDistributors[4][0]);
            tableHdr.put(Constants.ParentNo, mArrayDistributors[4][0]);
            tableHdr.put(Constants.ParentName, mArrayDistributors[7][0]);
            tableHdr.put(Constants.ParentTypeID, mArrayDistributors[5][0]);
            tableHdr.put(Constants.ParentTypeDesc, mArrayDistributors[6][0]);

            //noinspection unchecked
            tableHdr.put(Constants.MerchReviewType, valueHelpBean.getID());


            tableHdr.put(Constants.MerchReviewTypeDesc, valueHelpBean.getDescription());
            //noinspection unchecked
            tableHdr.put(Constants.MerchReviewDate, UtilConstants.getNewDateTimeFormat());
            //noinspection unchecked
            tableHdr.put(Constants.MerchReviewLat, BigDecimal.valueOf(UtilConstants.latitude));
            //noinspection unchecked
            tableHdr.put(Constants.MerchReviewLong, BigDecimal.valueOf(UtilConstants.longitude));

            String mRouteSchGuid = Constants.getRouteSchGUID(Constants.RouteSchedulePlans, Constants.RouteSchGUID, Constants.VisitCPGUID, mStrBundleCPGUID32, mArrayDistributors[5][0]);
            if (!mRouteSchGuid.equalsIgnoreCase("")) {
                tableHdr.put(Constants.RouteGUID, mRouteSchGuid);
            } else {
                tableHdr.put(Constants.RouteGUID, "");
            }


            tableHdr.put(Constants.CPTypeID, Constants.str_02);
            SharedPreferences sharedPreferences = mContext.getSharedPreferences(Constants.PREFS_NAME, 0);

            String loginIdVal = sharedPreferences.getString(Constants.username, "");
            //noinspection unchecked
            tableHdr.put(Constants.LOGINID, loginIdVal);

            final Calendar calCurrentTime = Calendar.getInstance();
            int hourOfDay = calCurrentTime.get(Calendar.HOUR_OF_DAY); // 24 hour clock
            int minute = calCurrentTime.get(Calendar.MINUTE);
            int second = calCurrentTime.get(Calendar.SECOND);
            ODataDuration oDataDuration = null;
            try {
                oDataDuration = new ODataDurationDefaultImpl();
                oDataDuration.setHours(hourOfDay);
                oDataDuration.setMinutes(minute);
                oDataDuration.setSeconds(BigDecimal.valueOf(second));
            } catch (Exception e) {
                e.printStackTrace();
            }

            tableHdr.put(Constants.MerchReviewTime, oDataDuration);

            //Todo set values to data vault
            Constants.saveDeviceDocNoToSharedPref(mContext, Constants.MerchList, mStrGuide.toString().toUpperCase());
//            Constants.storeInDataVault(mStrGuide.toString().toUpperCase(), filename + "." + strMimeType,mContext);

            tableItm = new Hashtable();

            try {
                //noinspection unchecked
                tableItm.put(Constants.MerchReviewGUID, mStrGuide.toString());
                GUID mStrImgGuide = GUID.newRandom();
                //noinspection unchecked
                tableItm.put(Constants.MerchImageGUID, mStrImgGuide.toString());
                //noinspection unchecked
                tableItm.put(Constants.ImageMimeType, mimeType);
                //noinspection unchecked
                tableItm.put(Constants.ImageSize, mLongBitmapSize);
                //noinspection unchecked
                tableItm.put(Constants.Image, "");

                tableItm.put(Constants.ImagePath, selectedImagePath);
                tableItm.put(Constants.FileName, filename + "." + strMimeType);


            } catch (Exception exception) {
                exception.printStackTrace();
            }

            mStrVisitActRefID = mStrGuide.toString36().toUpperCase();


            try {
                //noinspection unchecked
                OfflineManager.createMerChndisingHeader(tableHdr, this, mContext);
            } catch (OfflineODataStoreException e) {
                //                    e.printStackTrace();
                LogManager.writeLogError(Constants.error_txt + e.getMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

//        }else{
//            UtilConstants.showAlert(getString(R.string.validation_app_time_incorrect), MerchndisingActivity.this);
//        }

    }

    @Override
    public void onSnapTypeChanged(ValueHelpBean valueHelpBean) {
        this.valueHelpBean = valueHelpBean;
    }

    @Override
    public void openGalleryIntent() {
        try {
            Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            mContext.startActivityForResult(i, ConstantsUtils.GALLERY_REQUEST_CODE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void responseSuccess(ODataRequestExecution oDataRequestExecution, List<ODataEntity> list, Bundle bundle) {
        getSalesPersonValues();
        getDistributorValues();
        alMerchTypeList.clear();
        alMerchTypeList.addAll(OfflineManager.getConfigListWithDefaultValAndNone(list, Constants.PROP_MER_TYPE));
        ((Activity) mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mView != null) {
                    mView.hideProgress();
                    mView.displayMerchList(alMerchTypeList);
                }
            }
        });
    }

    @Override
    public void responseFailed(ODataRequestExecution oDataRequestExecution, final String s, Bundle bundle) {
        ((Activity) mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mView != null) {
                    mView.hideProgress();
                    mView.displayMsg(s);
                }
            }
        });
    }

    @Override
    public void onRequestError(int i, Exception e) {
        Constants.writeDebugLog(mContext,"Merchandising onRequestError"+i+"--"+e.getMessage());
        if (mView != null) {
            mView.displayMsg(mContext.getString(R.string.error_occured_during_save));
        }
    }

    @Override
    public void onRequestSuccess(int operation, String s) throws ODataException, OfflineODataStoreException {
        if (operation == Operation.Create.getValue() && mBoolHeaderPosted) {
            //========>Start VisitActivity
            SharedPreferences sharedPreferences = mContext.getSharedPreferences(Constants.PREFS_NAME, 0);
            String loginIdVal = sharedPreferences.getString(Constants.username, "");
            Constants.onVisitActivityUpdate(mContext, mStrBundleCPGUID32, mStrVisitActRefID, "01", Constants.Merchendising_Snap, mStartTimeDuration);
            //========>End VisitActivity
            backToVisit();
            Constants.writeDebugLog(mContext,"Merchandising data saved successfully");

        } else if (operation == Operation.Create.getValue() && !mBoolHeaderPosted) {
            mBoolHeaderPosted = true;
            saveItemEntityToTable();
        }
    }

    private void saveItemEntityToTable() {
        try {
            //noinspection unchecked
            OfflineManager.createMerChndisingItem(tableItm, tableHdr, this,mContext);
        } catch (OfflineODataStoreException e) {
            //                    e.printStackTrace();
            LogManager.writeLogError(Constants.error_txt + e.getMessage());
        }
    }

    private void backToVisit() {
        UtilConstants.dialogBoxWithCallBack(mContext, "", mContext.getString(R.string.msg_snap_shot_created), mContext.getString(R.string.ok), "", false, new DialogCallBack() {
            @Override
            public void clickedStatus(boolean b) {
                redirectActivity();
            }
        });
    }

    private void redirectActivity() {
//        Constants.ComingFromCreateSenarios = Constants.X;
        Intent intentNavPrevScreen = new Intent(mContext, CustomerDetailsActivity.class);
        intentNavPrevScreen.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intentNavPrevScreen.putExtra(Constants.CPNo, mStrBundleRetID);
        intentNavPrevScreen.putExtra(Constants.RetailerName, mStrBundleRetName);
        intentNavPrevScreen.putExtra(Constants.CPUID, mStrBundleRetailerUID);
        intentNavPrevScreen.putExtra(Constants.comingFrom, mStrComingFrom);
        intentNavPrevScreen.putExtra(Constants.CPGUID, mStrBundleCPGUID);
        intentNavPrevScreen.putExtra(Constants.BeatGUID, beatGUID);
        intentNavPrevScreen.putExtra(Constants.ParentId, parentId);
//        if (!Constants.OtherRouteNameVal.equalsIgnoreCase("")) {
//            intentNavPrevScreen.putExtra(Constants.OtherRouteGUID, Constants.OtherRouteGUIDVal);
//            intentNavPrevScreen.putExtra(Constants.OtherRouteName, Constants.OtherRouteNameVal);
//        }
        mContext.startActivity(intentNavPrevScreen);
    }
}
