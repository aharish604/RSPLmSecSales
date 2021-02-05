package com.arteriatech.ss.msecsales.rspl.merchandising;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.widget.NestedScrollView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.InputFilter;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.interfaces.DialogCallBack;
import com.arteriatech.ss.msecsales.rspl.R;
import com.arteriatech.ss.msecsales.rspl.common.Constants;
import com.arteriatech.ss.msecsales.rspl.common.ConstantsUtils;
import com.arteriatech.ss.msecsales.rspl.customers.CustomerDetailsActivity;
import com.arteriatech.ss.msecsales.rspl.mbo.ValueHelpBean;
import com.arteriatech.ss.msecsales.rspl.ui.MaterialDesignSpinner;

import java.util.ArrayList;

public class MerchandisingCreateActivity extends AppCompatActivity implements MerchandisingCreateView, View.OnClickListener {

    private Toolbar toolbar;
    private MaterialDesignSpinner spSnapType;
    private EditText etRemarks;
    private MerchandisingCreatePresenterImpl presenter;
    private ProgressDialog progressDialog = null;
    private ImageView ivThumbnailPhoto;
    private TextInputLayout tiRemarks;
    private Bundle bundleExtras;
//    private NestedScrollView nestedScroll;
    private String mStrBundleRetID = "";
    private String mStrBundleRetName = "";
    private String beatGUID = "";
    private String parentId = "";
    private String mStrBundleRetailerUID = "", mStrBundleCPGUID32 = "",mStrBundleCPGUID="",mStrComingFrom="";
    private TextView tvRetailerName, tvRetailerID;
    private boolean isClickable = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merchandising_create);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.lbl_merchndising), 0);
        bundleExtras = getIntent().getExtras();
        if (bundleExtras != null) {
            mStrBundleRetID = bundleExtras.getString(Constants.CPNo);
            mStrBundleRetName = bundleExtras.getString(Constants.RetailerName);
            mStrBundleCPGUID = bundleExtras.getString(Constants.CPGUID);
            mStrBundleCPGUID32 = bundleExtras.getString(Constants.CPGUID32);
            mStrBundleRetailerUID = bundleExtras.getString(Constants.CPUID);
            mStrComingFrom = bundleExtras.getString(Constants.comingFrom);
            try {
                beatGUID = bundleExtras.getString(Constants.BeatGUID,"");
            } catch (Exception e) {
                beatGUID="";
                e.printStackTrace();
            }
            try {
                parentId = bundleExtras.getString(Constants.ParentId,"");
            } catch (Exception e) {
                parentId="";
                e.printStackTrace();
            }
        }
        if (!Constants.restartApp(MerchandisingCreateActivity.this)) {
            initUI();
        }
    }

    private void initUI() {
        tvRetailerName = (TextView) findViewById(R.id.tv_RetailerName);
        tvRetailerID = (TextView) findViewById(R.id.tv_RetailerID);
        spSnapType = (MaterialDesignSpinner) findViewById(R.id.sp_snap_type);
        tiRemarks = (TextInputLayout) findViewById(R.id.tiRemarks);
        etRemarks = (EditText) findViewById(R.id.etRemarks);
        InputFilter[] FilterArray = new InputFilter[2];
        FilterArray[0] = new InputFilter.LengthFilter(60);
        FilterArray[1] = Constants.getNumberAlphabetOnly();
        etRemarks.setFilters(FilterArray);
        // set retailer name
        tvRetailerName.setText(mStrBundleRetName);
        // set retailer id
        tvRetailerID.setText(mStrBundleRetID);
        ivThumbnailPhoto = (ImageView) findViewById(R.id.ivThumbnailPhoto);
//        nestedScroll = (NestedScrollView) findViewById(R.id.nestedScroll);
        ivThumbnailPhoto.setOnClickListener(this);
        presenter = new MerchandisingCreatePresenterImpl(MerchandisingCreateActivity.this, this, bundleExtras);
        presenter.onStart();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.menu_save:
                if (!isClickable) {
                    isClickable = true;
                    UtilConstants.dialogBoxWithCallBack(MerchandisingCreateActivity.this, "", getString(R.string.so_save_Merchandisingmsg), getString(R.string.ok), getString(R.string.cancel), false, new DialogCallBack() {
                        @Override
                        public void clickedStatus(boolean b) {
                            isClickable =false;
                            if (b) {
                                presenter.onSave(etRemarks.getText().toString());
                            }
                        }
                    });
                }

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    public void onBackPressed() {
            AlertDialog.Builder builder = new AlertDialog.Builder(MerchandisingCreateActivity.this, R.style.MyTheme);
            builder.setMessage(R.string.alert_exit_create_snap_shot).setCancelable(false)
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            redirectActivity();
                        }
                    })
                    .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }

                    });
            builder.show();
    }
    private void redirectActivity(){
        Intent intentNavPrevScreen = new Intent(this, CustomerDetailsActivity.class);
        intentNavPrevScreen.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intentNavPrevScreen.putExtra(Constants.CPNo, mStrBundleRetID);
        intentNavPrevScreen.putExtra(Constants.RetailerName, mStrBundleRetName);
        intentNavPrevScreen.putExtra(Constants.CPUID, mStrBundleRetailerUID);
        intentNavPrevScreen.putExtra(Constants.comingFrom, mStrComingFrom);
        intentNavPrevScreen.putExtra(Constants.CPGUID, mStrBundleCPGUID);
        intentNavPrevScreen.putExtra(Constants.BeatGUID, beatGUID);
        intentNavPrevScreen.putExtra(Constants.ParentId, parentId);
        startActivity(intentNavPrevScreen);
    }
    @Override
    public void showProgress() {
        progressDialog = Constants.showProgressDialog(MerchandisingCreateActivity.this, "", getString(R.string.app_loading));
    }

    @Override
    public void hideProgress() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void displayMsg(String msg) {
        ConstantsUtils.displayLongToast(MerchandisingCreateActivity.this, msg);
    }

    @Override
    public void displayMerchList(final ArrayList<ValueHelpBean> alMerchTypeList) {
//        nestedScroll.setVisibility(View.VISIBLE);
        ArrayAdapter<ValueHelpBean> adapter = new ArrayAdapter<ValueHelpBean>(MerchandisingCreateActivity.this, R.layout.custom_textview, R.id.tvItemValue, alMerchTypeList) {
            @Override
            public View getDropDownView(final int position, final View convertView, final ViewGroup parent) {
                final View v = super.getDropDownView(position, convertView, parent);
                ConstantsUtils.selectedView(v, spSnapType, position, getContext());
                return v;
            }
        };
        adapter.setDropDownViewResource(R.layout.spinnerinside);
        spSnapType.setAdapter(adapter);
        spSnapType.showFloatingLabel();
        spSnapType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                presenter.onSnapTypeChanged(alMerchTypeList.get(position));
                tiRemarks.setErrorEnabled(false);
                tiRemarks.setError(null);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    @Override
    public void displayImages(Bitmap bitMap, final byte[] imageInByte) {
        ivThumbnailPhoto.setColorFilter(null);
        ivThumbnailPhoto.clearColorFilter();
        ivThumbnailPhoto.setImageBitmap(bitMap);
        ivThumbnailPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                        openImagePopUp(bitMap);
                final Dialog dialog = new Dialog(MerchandisingCreateActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.img_expand);
                // set the custom dialog components - text, image and
                // button
                ImageView image = (ImageView) dialog.findViewById(R.id.imageView1);

                image.setImageBitmap(BitmapFactory.decodeByteArray(imageInByte, 0,
                        imageInByte.length));
                dialog.show();
            }
        });
    }

    @Override
    public void displayMerchTypeError(String eror) {
        if (spSnapType.getVisibility() == View.VISIBLE)
            spSnapType.setError(eror);
    }

    @Override
    public void requestPermission() {
        requestPermission(MerchandisingCreateActivity.this);
    }

    @Override
    public void displayRemarkError(String eror) {
        tiRemarks.setErrorEnabled(true);
        tiRemarks.setError(eror);
    }

    private void openCameraIntent(final Activity mActivity) {
        if (Build.VERSION_CODES.M <= android.os.Build.VERSION.SDK_INT) {
            if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(mActivity, Manifest.permission.CAMERA)
                            && ActivityCompat.shouldShowRequestPermissionRationale(mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.CAMERA,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, Constants.CAMERA_PERMISSION_CONSTANT);
                    } else if (Constants.getPermissionStatus(mActivity, Manifest.permission.CAMERA)
                            && Constants.getPermissionStatus(mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        UtilConstants.dialogBoxWithCallBack(mActivity, "", getString(R.string.this_app_needs_camera_storage_permission), getString(R.string.enable), getString(R.string.later), false, new DialogCallBack() {
                            @Override
                            public void clickedStatus(boolean b) {
                                if (b) {
                                    Constants.navigateToAppSettingsScreen(mActivity);
                                }
                            }
                        });
                        UtilConstants.dialogBoxWithCallBack(mActivity, "", getString(R.string.this_app_needs_camera_storage_permission), getString(R.string.enable), getString(R.string.later), false, new DialogCallBack() {
                            @Override
                            public void clickedStatus(boolean b) {
                                if (b)
                                    Constants.navigateToAppSettingsScreen(mActivity);
                            }
                        });
                    } else {
                        ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.CAMERA,
                                        Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                                Constants.CAMERA_PERMISSION_CONSTANT);
                    }
                    Constants.setPermissionStatus(mActivity, Manifest.permission.CAMERA, true);
                    Constants.setPermissionStatus(mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE, true);
                } else if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(mActivity, Manifest.permission.CAMERA)) {
                        ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.CAMERA}, Constants.CAMERA_PERMISSION_CONSTANT);
                    } else if (Constants.getPermissionStatus(mActivity, Manifest.permission.CAMERA)) {
                        UtilConstants.dialogBoxWithCallBack(mActivity, "", getString(R.string.this_app_needs_camera_permission), getString(R.string.enable), getString(R.string.later), false, new DialogCallBack() {
                            @Override
                            public void clickedStatus(boolean b) {
                                if (b) {
                                    Constants.navigateToAppSettingsScreen(mActivity);
                                }
                            }
                        });
                    } else {
                        ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.CAMERA},
                                Constants.CAMERA_PERMISSION_CONSTANT);
                    }
                    Constants.setPermissionStatus(mActivity, Manifest.permission.CAMERA, true);
                } else if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                        || ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        ActivityCompat.requestPermissions(mActivity, new String[]{
                                Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, Constants.CAMERA_PERMISSION_CONSTANT);
                    } else if (Constants.getPermissionStatus(mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        UtilConstants.dialogBoxWithCallBack(mActivity, "", getString(R.string.this_app_needs_storage_permission), getString(R.string.enable), getString(R.string.later), false, new DialogCallBack() {
                            @Override
                            public void clickedStatus(boolean b) {
                                if (b) {
                                    Constants.navigateToAppSettingsScreen(mActivity);
                                }
                            }
                        });
                    } else {
                        ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                                Constants.CAMERA_PERMISSION_CONSTANT);
                    }
                    Constants.setPermissionStatus(mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE, true);
                }
            } else {
                //You already have the permission, just go ahead.
                presenter.openCameraIntent();
//                openConformationDialog();
            }
        } else {
            presenter.openCameraIntent();
//            openConformationDialog();
        }
    }

    private void openConformationDialog() {
        final BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(MerchandisingCreateActivity.this);
        View sheetView = getLayoutInflater().inflate(R.layout.image_bottom_sheet, null);
        mBottomSheetDialog.setContentView(sheetView);
        LinearLayout llCamera = (LinearLayout) sheetView.findViewById(R.id.ll_camera);
        llCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBottomSheetDialog.dismiss();
                presenter.openCameraIntent();
            }
        });
        LinearLayout llGalley = (LinearLayout) sheetView.findViewById(R.id.ll_gallery);
        llGalley.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBottomSheetDialog.dismiss();
                presenter.openGalleryIntent();
            }
        });
        mBottomSheetDialog.show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        presenter.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivThumbnailPhoto:
                openCameraIntent(MerchandisingCreateActivity.this);
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_back_save, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void requestPermission(final Activity activity) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_FINE_LOCATION)) {
            ActivityCompat.requestPermissions(
                    activity,
                    Constants.PERMISSIONS_LOCATION, Constants.PERMISSION_REQUEST_CODE
            );
        } else if (Constants.getPermissionStatus(activity, Manifest.permission.ACCESS_FINE_LOCATION)
                && Constants.getPermissionStatus(activity, Manifest.permission.ACCESS_COARSE_LOCATION)) {
            Constants.dialogBoxWithButton(activity, "",
                    getString(R.string.this_app_needs_location_permission), getString(R.string.enable),
                    getString(R.string.later), new DialogCallBack() {
                        @Override
                        public void clickedStatus(boolean clickedStatus) {
                            if (clickedStatus) {
                                Constants.navigateToAppSettingsScreen(activity);
                            }
                        }
                    });

        } else {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION},
                    Constants.PERMISSION_REQUEST_CODE);
        }

        Constants.setPermissionStatus(activity, Manifest.permission.ACCESS_FINE_LOCATION, true);
        Constants.setPermissionStatus(activity, Manifest.permission.ACCESS_COARSE_LOCATION, true);
    }
}
