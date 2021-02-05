package com.arteriatech.ss.msecsales.rspl.visualaid;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import androidx.core.content.FileProvider;
import android.text.TextUtils;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.Operation;
import com.arteriatech.mutils.common.UIListener;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.log.LogManager;
import com.arteriatech.mutils.upgrade.AppUpgradeConfig;
import com.arteriatech.ss.msecsales.rspl.BuildConfig;
import com.arteriatech.ss.msecsales.rspl.R;
import com.arteriatech.ss.msecsales.rspl.asyncTask.RefreshAsyncTask;
import com.arteriatech.ss.msecsales.rspl.common.Constants;
import com.arteriatech.ss.msecsales.rspl.common.ConstantsUtils;
import com.arteriatech.ss.msecsales.rspl.mbo.ConfigTypeValues;
import com.arteriatech.ss.msecsales.rspl.mbo.DocumentsBean;
import com.arteriatech.ss.msecsales.rspl.mbo.ErrorBean;
import com.arteriatech.ss.msecsales.rspl.store.OfflineManager;
import com.arteriatech.ss.msecsales.rspl.sync.SyncUtils;
import com.sap.client.odata.v4.core.GUID;
import com.sap.smp.client.odata.exception.ODataException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class VisualAidPresenterImpl implements VisualAidPresenter, UIListener {
    private Activity mContext;
    private VisualAidView visView = null;
    private ArrayList<DocumentsBean> allDocumentList = new ArrayList<>();
    private ArrayList<String> alAssignColl = new ArrayList<>();
    private int mError = 0;
    private GUID refguid =null;

    public VisualAidPresenterImpl(Activity mContext, VisualAidView visView) {
        this.mContext = mContext;
        this.visView = visView;
    }

    @Override
    public void onStart() {
        if (visView != null) {
            visView.showProgress();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    allDocumentList.clear();
                    String documentIDQry = getDocumentLinkID();
                   /* allDocumentList.addAll(OfflineManager.getDocuments(mContext, Constants.Documents + "?$filter=(" + Constants.DocumentMimeType + "  eq '" + Constants.MimeTypePDF + "' " +
                            "or " + Constants.DocumentMimeType + " eq '" + Constants.MimeTypeDocx + "' " +
                            "or " + Constants.DocumentMimeType + " eq '" + Constants.MimeTypeMsword + "' " +
                            "or " + Constants.DocumentMimeType + " eq '" + Constants.MimeTypePPT + "' " +
                            "or " + Constants.DocumentMimeType + " eq '" + Constants.MimeTypeMP4 + "' " +
                            "or " + Constants.DocumentMimeType + " eq '" + Constants.MimeTypevndmspowerpoint + "') and DocumentTypeID eq 'ZDMS_VAID'"));*/
                    if(!TextUtils.isEmpty(documentIDQry)) {
                        allDocumentList.addAll(OfflineManager.getDocuments(mContext, Constants.Documents + "?$filter=(" + documentIDQry + ") and DocumentTypeID eq 'ZDMS_VAID'"));
                    }
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }
                ((Activity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (visView != null) {
                            visView.hideProgress();
                            visView.displayLSTSyncTime(SyncUtils.getCollectionSyncTime(mContext, Constants.Documents));
                            visView.displayList(allDocumentList);
                        }

                    }
                });
            }
        }).start();
    }

    public String getDocumentLinkID(){
        String concatQry ="";
        String qry = "ConfigTypsetTypeValues?$filter=Typeset eq 'ZDOCTY'";
        ArrayList<ConfigTypeValues> alConfiList = new ArrayList<>();
        try {
            alConfiList = OfflineManager.getConfigTypeValuesDoc(qry);
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
        for (int i=0;i<alConfiList.size();i++){
            if(i==alConfiList.size()-1){
                concatQry = concatQry+Constants.DocumentLink +" eq '"+alConfiList.get(i).getType().toLowerCase() +"'";
            }else {
                concatQry = concatQry+Constants.DocumentLink +" eq '"+alConfiList.get(i).getType().toLowerCase() +"' or ";
            }
        }
        return concatQry;
    }

    @Override
    public void onDigitalPrdt() {
        if (visView != null) {
            visView.showProgress();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                allDocumentList.clear();
                if (ConstantsUtils.checkStoragePermission(mContext)) {
                    try {
                        allDocumentList.addAll(OfflineManager.getDocuments(mContext, Constants.Documents + "?$filter=DocumentTypeID eq 'ZDMS_DGPRD'"));
                    } catch (OfflineODataStoreException e) {
                        e.printStackTrace();
                    }
                }
                ((Activity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (visView != null) {
                            visView.hideProgress();
                            visView.displayLSTSyncTime(SyncUtils.getCollectionSyncTime(mContext, Constants.Documents));
                            visView.displayList(allDocumentList);
                        }

                    }
                });
            }
        }).start();
    }

    @Override
    public void onDestroy() {
        visView = null;
    }

    @Override
    public void onItemClick(DocumentsBean documentsBean) {
        if (ConstantsUtils.checkStoragePermission(mContext)) {
//            getImageDetails(documentsBean.getMediaLink(), documentsBean.getDocumentMimeType(), documentsBean.getFileName().toLowerCase(), documentsBean);
            openFileBasedOnMimeType(documentsBean.getMediaLink(), documentsBean.getDocumentMimeType(), documentsBean.getFileName().toLowerCase(), documentsBean);
        }
    }

    private void openFileBasedOnMimeType(String mStrImagePath, String mimeType, String filename, DocumentsBean documentsBean){
        try {
            byte[] imageByteArray = OfflineManager.getImageList(mStrImagePath);
            if (imageByteArray != null && visView != null) {
                if (Constants.MimeTypePng.equalsIgnoreCase(mimeType) || Constants.MimeTypeJpg.equalsIgnoreCase(mimeType) || Constants.MimeTypeJpeg.equalsIgnoreCase(mimeType)) {
                    if (!TextUtils.isEmpty(documentsBean.getImagePath()))
                        Constants.openImageInGallery(mContext, new File(documentsBean.getImagePath()).getPath());
                }else if(Constants.MimeTypeUrl.equalsIgnoreCase(mimeType)) {
                    if (UtilConstants.isNetworkAvailable(mContext)){
                        String urlLink = new String(imageByteArray, "UTF-8"); // for UTF-8 encoding
                      /*  try {
                            File myDirectory = new File(Environment.getExternalStorageDirectory(), Constants.FolderName);
                            if (!myDirectory.exists()) {
                                myDirectory.mkdirs();
                            }
                            File data = new File(myDirectory, "/" + filename);
                            OutputStream op = new FileOutputStream(data);
                            op.write(imageByteArray);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    File dir = Environment.getExternalStorageDirectory();
                    File file = new File(dir + "/" + Constants.FolderName + "/" + filename);
//                    File file =  new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Constants.FolderName/"+filename);
//                    Uri uri = FileProvider.getUriForFile(mContext, mContext.getApplicationContext().getPackageName() + ".provider", file);
                    Uri uri = Uri.fromFile(file);
                    //   Intent target = new Intent(Intent.ACTION_VIEW);
//                    target.setDataAndType(uri, "application/pdf");
                    String urllink = "";
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        Uri contentUri = FileProvider.getUriForFile(mContext, mContext.getApplicationContext().getPackageName() + ".provider", file);
                        mContext.grantUriPermission(" com.arteriatech.ss.msecsales.rspl.visualaid", contentUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        String filerenamed = renameFileExtension(file.getAbsolutePath(), ".txt");
                        urllink = read_file(mContext, filerenamed);
                        //target.setDataAndType(contentUri, mimeType);
                    } else {
                        //  target.setDataAndType(Uri.fromFile(file), mimeType);
                    }*/
                    Intent retList = new Intent(mContext, WebviewActivity.class);
                    retList.putExtra("urlink", urlLink);
                    mContext.startActivity(retList);
                      /*  target.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        target.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);*/
//                    target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                     /*   Intent intent = Intent.createChooser(target, "Open File");
                        try {
                            intent.getDataString();
                            mContext.startActivity(intent);
                        } catch (ActivityNotFoundException e) {
                            e.printStackTrace();
                            // Instruct the user to install a PDF reader here, or something
                       */    /* if (visView != null) {
                                visView.showMessage(mContext.getString(R.string.visual_aidl_app_not_found));
                            }*/
                    } else {
                        if (visView != null)
                            visView.showMessage(mContext.getString(R.string.inter_nt_available));

                    }
                }else{
                    try {
                        File myDirectory = new File(Environment.getExternalStorageDirectory(), Constants.FolderName);
                        if (!myDirectory.exists()) {
                            myDirectory.mkdirs();
                        }
                        File data = new File(myDirectory, "/" + filename);
                        OutputStream op = new FileOutputStream(data);
                        op.write(imageByteArray);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    File dir = Environment.getExternalStorageDirectory();
                    File file = new File(dir + "/" + Constants.FolderName + "/" + filename);
//                    File file =  new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Constants.FolderName/"+filename);
//                    Uri uri = FileProvider.getUriForFile(mContext, mContext.getApplicationContext().getPackageName() + ".provider", file);
                    Uri uri = Uri.fromFile(file);
                    Intent target = new Intent(Intent.ACTION_VIEW);
//                    target.setDataAndType(uri, "application/pdf");
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        Uri contentUri = FileProvider.getUriForFile(mContext, mContext.getApplicationContext().getPackageName() + ".provider", file);
                        mContext.grantUriPermission(" com.arteriatech.ss.msecsales.rspl.visualaid", contentUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        target.setDataAndType(contentUri, mimeType);
                    } else {
                        target.setDataAndType(Uri.fromFile(file), mimeType);
                    }
                    target.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    target.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                    target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    Intent intent = Intent.createChooser(target, "Open File");
                    try {
                        intent.getDataString();
                        mContext.startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        e.printStackTrace();
                        // Instruct the user to install a PDF reader here, or something
                        if (visView != null) {
                            visView.showMessage(mContext.getString(R.string.visual_aidl_app_not_found));
                        }
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static String renameFileExtension
            (String source, String newExtension) {
        String renamedFile = null;
        String target;
        String currentExtension = getFileExtension(source);

        if (currentExtension.equals("")){
            target = source + "." + newExtension;
        }
        else {
            target = source.replaceAll("." + currentExtension, newExtension);
        }
        if(new File(source).renameTo(new File(target)))
            return target;
        else
            return "";
    }

    public static String getFileExtension(String f) {
        String ext = "";
        int i = f.lastIndexOf('.');
        if (i > 0 &&  i < f.length() - 1) {
            ext = f.substring(i + 1).toLowerCase();
        }
        return ext;
    }


    public String read_file(Context context, String filename) {

        //Read text from file
        StringBuilder text = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(filename));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();
        }
        catch (IOException e) {
            //You'll need to add proper error handling here
        }
        return text.toString();

    }
    private void getImageDetails(String mStrImagePath, String mimeType, String filename, DocumentsBean documentsBean) {
        try {
            byte[] imageByteArray = OfflineManager.getImageList(mStrImagePath);
            if (imageByteArray != null && visView != null) {
                if (Constants.MimeTypePDF.equalsIgnoreCase(mimeType)) {
                    try {
                        File myDirectory = new File(Environment.getExternalStorageDirectory(), Constants.FolderName);
                        if (!myDirectory.exists()) {
                            myDirectory.mkdirs();
                        }
                        File data = new File(myDirectory, "/" + filename);
                        OutputStream op = new FileOutputStream(data);
                        op.write(imageByteArray);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    File dir = Environment.getExternalStorageDirectory();
                    File file = new File(dir + "/" + Constants.FolderName + "/" + filename);
//                    File file =  new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Constants.FolderName/"+filename);
//                    Uri uri = FileProvider.getUriForFile(mContext, mContext.getApplicationContext().getPackageName() + ".provider", file);
                    Uri uri = Uri.fromFile(file);
                    Intent target = new Intent(Intent.ACTION_VIEW);
//                    target.setDataAndType(uri, "application/pdf");
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        Uri contentUri = FileProvider.getUriForFile(mContext, mContext.getApplicationContext().getPackageName() + ".provider", file);
                        mContext.grantUriPermission(" com.arteriatech.ss.msecsales.rspl.visualaid", contentUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);

                        target.setDataAndType(contentUri, "application/pdf");

                    } else {
                        target.setDataAndType(Uri.fromFile(file), "application/pdf");
                    }
                    target.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    target.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                    target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    Intent intent = Intent.createChooser(target, "Open File");
                    try {
                        mContext.startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        e.printStackTrace();
                        // Instruct the user to install a PDF reader here, or something
                        if (visView != null) {
                            visView.showMessage(mContext.getString(R.string.visual_aidl_app_not_found));
                        }
                    }
                } else if (Constants.MimeTypeDocx.equalsIgnoreCase(mimeType) || Constants.MimeTypeMsword.equalsIgnoreCase(mimeType) || Constants.MimeTypeDOCx.equalsIgnoreCase(mimeType)) {
                    try {
                        File myDirectory = new File(Environment.getExternalStorageDirectory(), Constants.FolderName);
                        if (!myDirectory.exists()) {
                            myDirectory.mkdirs();
                        }
                        File data = new File(myDirectory, "/" + filename);
                        OutputStream op = new FileOutputStream(data);
                        op.write(imageByteArray);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    File dir = Environment.getExternalStorageDirectory();
                    File file = new File(dir + "/" + Constants.FolderName + "/" + filename);
                //    Uri uri = FileProvider.getUriForFile(mContext, mContext.getApplicationContext().getPackageName() + ".provider", file);
                    Intent target = new Intent(Intent.ACTION_VIEW);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        Uri contentUri = FileProvider.getUriForFile(mContext, mContext.getApplicationContext().getPackageName() + ".provider", file);
                        mContext.grantUriPermission(" com.arteriatech.ss.msecsales.rspl.visualaid", contentUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);

                        target.setDataAndType(contentUri, "application/msword");

                    } else {
                        target.setDataAndType(Uri.fromFile(file), "application/msword");
                    }


                    target.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    target.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    Intent intent = Intent.createChooser(target, "Open File");
                    try {
                        mContext.startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        e.printStackTrace();
                        // Instruct the user to install a PDF reader here, or something
                        if (visView != null) {
                            visView.showMessage(mContext.getString(R.string.visual_aidl_app_not_found));
                        }
                    }
                } else if (Constants.MimeTypePPT.equalsIgnoreCase(mimeType) || Constants.MimeTypevndmspowerpoint.equalsIgnoreCase(mimeType)|| Constants.MimeTypePPTX.equalsIgnoreCase(mimeType)) {
                    try {
                        File myDirectory = new File(Environment.getExternalStorageDirectory(), Constants.FolderName);
                        if (!myDirectory.exists()) {
                            myDirectory.mkdirs();
                        }
                        File data = new File(myDirectory, "/" + filename);

                        OutputStream op = new FileOutputStream(data);
                        op.write(imageByteArray);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    File dir = Environment.getExternalStorageDirectory();
                    File file = new File(dir + "/" + Constants.FolderName + "/" + filename);
                    Uri uri = FileProvider.getUriForFile(mContext, mContext.getApplicationContext().getPackageName() + ".provider", file);
                    Intent target = new Intent(Intent.ACTION_VIEW);
                    target.setDataAndType(uri, "application/vnd.ms-powerpoint");
                    target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    target.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    Intent intent = Intent.createChooser(target, "Open File");
                    try {
                        mContext.startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        e.printStackTrace();
                        if (visView != null) {
                            visView.showMessage(mContext.getString(R.string.visual_aidl_app_not_found));
                        }
                    }
                } else if (Constants.MimeTypeMP4.equalsIgnoreCase(mimeType)) {
                    try {
                        File myDirectory = new File(Environment.getExternalStorageDirectory(), Constants.FolderName);
                        if (!myDirectory.exists()) {
                            myDirectory.mkdirs();
                        }
                        File data = new File(myDirectory, "/" + filename);
                        OutputStream op = new FileOutputStream(data);
                        op.write(imageByteArray);
                        System.out.println("File Created");
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        System.out.println("Excep:" + ex.toString());
                    }
                    File dir = Environment.getExternalStorageDirectory();
                    File file = new File(dir + "/" + Constants.FolderName + "/" + filename);
                    Uri intentUri = null;
                    Intent intent = new Intent(Intent.ACTION_VIEW);

                    if (Build.VERSION_CODES.N <= Build.VERSION.SDK_INT) {
                        intentUri = FileProvider.getUriForFile(mContext, mContext.getApplicationContext().getPackageName() + ".provider", file);
                        intent.setDataAndType(intentUri, "video/*");
                        List<ResolveInfo> playerList = mContext.getPackageManager().queryIntentActivities(intent, 0);
                        for (ResolveInfo resolveInfo : playerList) {
                            mContext.grantUriPermission(((ActivityInfo) resolveInfo.activityInfo).packageName, intentUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        }
                    } else {
                        file.setReadable(true, false);
                        String videoResource = file.getPath();
                        intentUri = Uri.fromFile(new File(videoResource));
                        intent.setDataAndType(intentUri, "video/*");
                    }
                    intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    try {
                        mContext.startActivity(intent);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        if (visView != null) {
                            visView.showMessage(mContext.getString(R.string.visual_aidl_app_not_found));
                        }
                    }

                } else if (Constants.MimeTypeXLS.equalsIgnoreCase(mimeType) || Constants.MimeTypeXLSX.equalsIgnoreCase(mimeType)) {
                    try {
                        File myDirectory = new File(Environment.getExternalStorageDirectory(), Constants.FolderName);
                        if (!myDirectory.exists()) {
                            myDirectory.mkdirs();
                        }
                        File data = new File(myDirectory, "/" + filename);
                        OutputStream op = new FileOutputStream(data);
                        op.write(imageByteArray);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    File dir = Environment.getExternalStorageDirectory();
                    File file = new File(dir + "/" + Constants.FolderName + "/" + filename);
//                    File file =  new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Constants.FolderName/"+filename);
//                    Uri uri = FileProvider.getUriForFile(mContext, mContext.getApplicationContext().getPackageName() + ".provider", file);
                    Uri uri = Uri.fromFile(file);
                    Intent target = new Intent(Intent.ACTION_VIEW);
//                    target.setDataAndType(uri, "application/pdf");
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        Uri contentUri = FileProvider.getUriForFile(mContext, mContext.getApplicationContext().getPackageName() + ".provider", file);
                        mContext.grantUriPermission(" com.arteriatech.ss.msecsales.rspl.visualaid", contentUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);

                        target.setDataAndType(contentUri, "application/vnd.ms-excel");

                    } else {
                        target.setDataAndType(Uri.fromFile(file), "application/vnd.ms-excel");
                    }
                    target.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    target.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                    target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    Intent intent = Intent.createChooser(target, "Open File");
                    try {
                        mContext.startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        e.printStackTrace();
                        // Instruct the user to install a PDF reader here, or something
                        if (visView != null) {
                            visView.showMessage(mContext.getString(R.string.visual_aidl_app_not_found));
                        }
                    }
                }else if (Constants.MimeTypePng.equalsIgnoreCase(mimeType) || Constants.MimeTypeJpg.equalsIgnoreCase(mimeType) || Constants.MimeTypeJpeg.equalsIgnoreCase(mimeType)) {
                    if (!TextUtils.isEmpty(documentsBean.getImagePath()))
                        Constants.openImageInGallery(mContext, new File(documentsBean.getImagePath()).getPath());
                }
            }
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
            LogManager.writeLogError(Constants.error_txt + e.getMessage());
        }

    }

    @Override
    public void onRefresh() {
        if (visView != null) {
            visView.showProgress();
        }
        mError = 0;
        String concatCollectionStr = "";
        if (UtilConstants.isNetworkAvailable(mContext)) {
            alAssignColl.clear();
            alAssignColl.addAll(SyncUtils.getVisualAid());
            alAssignColl.add(Constants.ConfigTypsetTypeValues);
            concatCollectionStr = UtilConstants.getConcatinatinFlushCollectios(alAssignColl);

            if (Constants.iSAutoSync) {
                if (visView != null) {
                    visView.hideProgress();
                    visView.showMessage(mContext.getString(R.string.alert_auto_sync_is_progress));
                }
            } else {
                try {
                    Constants.isSync = true;
                    refguid = GUID.newRandom();
                    SyncUtils.updatingSyncStartTime(mContext,Constants.VisualAids_sync,Constants.StartSync,refguid.toString().toUpperCase());
                    new RefreshAsyncTask(mContext, concatCollectionStr, this).execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            if (visView != null) {
                visView.hideProgress();
                visView.showMessage(mContext.getString(R.string.no_network_conn));
            }
        }
    }


    @Override
    public void onRequestError(int operation, Exception exception) {
        ErrorBean errorBean = Constants.getErrorCode(operation, exception, mContext);
        if (errorBean.hasNoError()) {
            mError++;
            if (operation == Operation.OfflineFlush.getValue()) {
                Constants.isSync = false;
                if (visView != null) {
                    visView.hideProgress();
                    Constants.displayErrorDialog(mContext, errorBean.getErrorMsg());
                }
            } else if (operation == Operation.OfflineRefresh.getValue()) {
                Constants.isSync = false;
                if (visView != null) {
                    visView.hideProgress();
                    Constants.displayErrorDialog(mContext, errorBean.getErrorMsg());
                }
            } else if (operation == Operation.GetStoreOpen.getValue()) {
                Constants.isSync = false;
                if (visView != null) {
                    visView.hideProgress();
                    visView.showMessage(mContext.getString(R.string.msg_offline_store_failure));
                }
            }
        } else if (errorBean.isStoreFailed()) {
            Constants.mBoolIsReqResAval = true;
            Constants.mBoolIsNetWorkNotAval = true;
            if (UtilConstants.isNetworkAvailable(mContext)) {
                Constants.isSync = true;
                if (visView != null) {
                    visView.hideProgress();
                }
                new RefreshAsyncTask(mContext, "", this).execute();
            } else {
                Constants.isSync = false;
                if (visView != null) {
                    visView.hideProgress();
                    Constants.displayMsgReqError(errorBean.getErrorCode(), mContext);
                }
            }
        } else {
            Constants.isSync = false;
            if (visView != null) {
                visView.hideProgress();
                Constants.displayMsgReqError(errorBean.getErrorCode(), mContext);
            }
        }
    }

    @Override
    public void onRequestSuccess(int operation, String s) throws ODataException, OfflineODataStoreException {
        if (operation == Operation.OfflineRefresh.getValue()) {
            Constants.updateLastSyncTimeToTable(mContext, alAssignColl,Constants.VisualAids_sync,refguid.toString().toUpperCase());
            ConstantsUtils.startAutoSync(mContext, false);
            Constants.isSync = false;
            if (visView != null) {
                visView.hideProgress();
                if (mContext instanceof VisualAidActivity) {
                    onStart();
                }else {
                    onDigitalPrdt();
                }
                AppUpgradeConfig.getUpdateAvlUsingVerCode(OfflineManager.offlineStore, mContext, BuildConfig.APPLICATION_ID, false, Constants.APP_UPGRADE_TYPESET_VALUE);
            }
        } else if (operation == Operation.GetStoreOpen.getValue() && OfflineManager.isOfflineStoreOpen()) {
            try {
                OfflineManager.getAuthorizations(mContext);
            } catch (OfflineODataStoreException e) {
                e.printStackTrace();
            }
            Constants.setSyncTime(mContext, Constants.Sync_All);
            ConstantsUtils.startAutoSync(mContext, false);
            if (visView != null) {
                visView.hideProgress();
                visView.showMessage(mContext.getString(R.string.msg_offline_store_success));
                if (mContext instanceof VisualAidActivity) {
                    onStart();
                }else {
                    onDigitalPrdt();
                }
                AppUpgradeConfig.getUpdateAvlUsingVerCode(OfflineManager.offlineStore, mContext, BuildConfig.APPLICATION_ID, false, Constants.APP_UPGRADE_TYPESET_VALUE);
            }
        }
    }
}
