package com.arteriatech.ss.msecsales.rspl.merchandising;

import android.content.Intent;

import com.arteriatech.ss.msecsales.rspl.mbo.ValueHelpBean;

/**
 * Created by e10769 on 25-Apr-18.
 */

public interface MerchandisingCreatePresenter {
    void onStart();

    void onDestroy();

    void openCameraIntent();

    void onActivityResult(int requestCode, int resultCode, Intent data);

    void onSave(String remark);

    void onSnapTypeChanged(ValueHelpBean valueHelpBean);

    void openGalleryIntent();
}
