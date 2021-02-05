package com.arteriatech.ss.msecsales.rspl.competitors;

import com.arteriatech.ss.msecsales.rspl.mbo.CompetitorBean;
import com.arteriatech.ss.msecsales.rspl.mbo.FeedbackBean;

/**
 * Created by e10526 on 21-04-2018.
 */

public interface CompetitorPresenter {
    void onStart();

    void onDestroy();

    boolean validateFields(CompetitorBean competitorBean);

    boolean isValidMargin(String margin);

    void onAsignData(CompetitorBean competitorBean);

    void onSaveData();
}
