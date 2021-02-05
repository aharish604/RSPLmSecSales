package com.arteriatech.ss.msecsales.rspl.merchandising.list;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.log.LogManager;
import com.arteriatech.ss.msecsales.rspl.R;
import com.arteriatech.ss.msecsales.rspl.common.Constants;
import com.arteriatech.ss.msecsales.rspl.common.ConstantsUtils;
import com.arteriatech.ss.msecsales.rspl.store.OfflineManager;

public class MerchanDetailsActivity extends AppCompatActivity {

    private String mStrBundleRetUID = "";
    private String mStrBundleRetName = "", mStrRemarks = "", mStrEtag = "", mStrMerchReviewGUID = "", mStrMerchReviewTypeDesc = "", mStrImagePath = "", mStrImgData = "";
    private ImageView imageViewFront;
    private TextView tv_remrks;
    private TextView tv_mer_type;
    private byte[] imageByteArray = null;
    private String mStrMediaLink = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merchan_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.lbl_merchndising_list), 0);
        Bundle bundleExtras = getIntent().getExtras();
        if (bundleExtras != null) {
            mStrBundleRetUID = bundleExtras.getString(Constants.CPUID);
            mStrBundleRetName = bundleExtras.getString(Constants.RetailerName);
            mStrMerchReviewGUID = bundleExtras.getString(Constants.MerchReviewGUID);
            mStrMerchReviewTypeDesc = bundleExtras.getString(Constants.MerchReviewTypeDesc);
            mStrRemarks = bundleExtras.getString(Constants.Remarks);
            mStrEtag = bundleExtras.getString(Constants.Etag);
            mStrImgData = bundleExtras.getString(Constants.Image);
            mStrImagePath = bundleExtras.getString(Constants.ImagePath);
            mStrMediaLink = bundleExtras.getString(Constants.MediaLink);
        }
        if (!Constants.restartApp(MerchanDetailsActivity.this)) {
            initUI();
            setValuesToUI();
            setImage();
        }

    }

    void initUI() {
        imageViewFront = (ImageView) findViewById(R.id.iv_image_front);
        tv_remrks = (TextView) findViewById(R.id.tv_remrks);
        tv_mer_type = (TextView) findViewById(R.id.tv_mer_type);
    }

    private void setValuesToUI() {
        tv_remrks.setText(mStrRemarks);
        tv_mer_type.setText(mStrMerchReviewTypeDesc);
    }

    private void setImage() {
        if (!TextUtils.isEmpty(mStrMediaLink)) {
            try {
                imageByteArray = OfflineManager.getImageList(mStrMediaLink);
                if (imageByteArray != null)
                    setImageToImageView();
                else
                    showMessage(getString(R.string.error_occured_during_get_image));
            } catch (OfflineODataStoreException e) {
                e.printStackTrace();
                showMessage(getString(R.string.error_occured_during_get_image));
            }

        } else {
            getImageDetails();
            setImageToImageView();
        }
    }

    private void showMessage(String msg) {
        ConstantsUtils.displayLongToast(MerchanDetailsActivity.this, msg);
    }

    private void getImageDetails() {
        try {
            if (!TextUtils.isEmpty(mStrImagePath))
                imageByteArray = OfflineManager.getImageList(mStrImagePath);
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
            LogManager.writeLogError(Constants.error_txt + e.getMessage());
        }
    }

    private void setImageToImageView() {
        if (imageByteArray != null) {
            try {
                Bitmap bitmap = BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.length);
                imageViewFront.setImageBitmap(bitmap);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            imageViewFront.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    Dialog dialog = new Dialog(MerchanDetailsActivity.this);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.img_expand);
                    ImageView image = (ImageView) dialog.findViewById(R.id.imageView1);
                    image.setImageBitmap(BitmapFactory.decodeByteArray(imageByteArray, 0,
                            imageByteArray.length));
                    dialog.show();
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }
}
