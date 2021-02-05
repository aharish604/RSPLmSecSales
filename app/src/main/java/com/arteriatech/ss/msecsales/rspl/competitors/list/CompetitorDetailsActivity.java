package com.arteriatech.ss.msecsales.rspl.competitors.list;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.ss.msecsales.rspl.R;
import com.arteriatech.ss.msecsales.rspl.common.Constants;
import com.arteriatech.ss.msecsales.rspl.common.ConstantsUtils;
import com.arteriatech.ss.msecsales.rspl.mbo.CompetitorBean;


public class CompetitorDetailsActivity extends AppCompatActivity {
    // data members
    private Context mContext = this;
    private CompetitorBean competitorBean = null;
    private Toolbar mToolbar;
    private LinearLayout llCompetitorName, llProductName, llMrp,llwholesalePrice,llLandgPrice, llTradeOffer, llConsumerOffer, llSchemeName, llRemarks, llRetailerMargin, llShelfLife;
    private TextView tvCompetitorName, tvProductName, tvwholesalePriceValue,tvLandgPriceValue,tvMrp, tvTradeOffer, tvConsumerOffer, tvSchemeName, tvRemarks, tvRetailerMargin, tvShelfLife;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compititor_details);

        // getting competitors data through intent
        Intent intent = getIntent();
        if (intent != null) {
            competitorBean = intent.getParcelableExtra(Constants.ItemList);
        }
        initViews();
    }

    /**
     * @desc initializing views
     */
    private void initViews() {
        try {
            mToolbar = (Toolbar) findViewById(R.id.toolbar);
            llCompetitorName = (LinearLayout) findViewById(R.id.llCompetitorName);
            llProductName = (LinearLayout) findViewById(R.id.llProductName);
            llMrp = (LinearLayout) findViewById(R.id.llMRP);
            llLandgPrice = (LinearLayout) findViewById(R.id.llLandgPrice);
            llwholesalePrice = (LinearLayout) findViewById(R.id.llwholesalePrice);
            llTradeOffer = (LinearLayout) findViewById(R.id.llTradeOffer);
            llConsumerOffer = (LinearLayout) findViewById(R.id.llConsumerOffer);
            llSchemeName = (LinearLayout) findViewById(R.id.llSchemeName);
            llRemarks = (LinearLayout) findViewById(R.id.llRemarks);
            llRetailerMargin = (LinearLayout) findViewById(R.id.llRetailerMargin);
            llShelfLife = (LinearLayout) findViewById(R.id.llShelfLife);
            tvCompetitorName = (TextView) findViewById(R.id.tvCompetitorName);
            tvProductName = (TextView) findViewById(R.id.tvProductName);
            tvMrp = (TextView) findViewById(R.id.tvMRP);
            tvLandgPriceValue = (TextView) findViewById(R.id.tvLandgPriceValue);
            tvwholesalePriceValue = (TextView) findViewById(R.id.tvwholesalePriceValue);
            tvTradeOffer = (TextView) findViewById(R.id.tvTradeOffer);
            tvConsumerOffer = (TextView) findViewById(R.id.tvConsumerOffer);
            tvSchemeName = (TextView) findViewById(R.id.tvSchemeName);
            tvRemarks = (TextView) findViewById(R.id.tvRemarks);
            tvRetailerMargin = (TextView) findViewById(R.id.tvRetailerMargin);
            tvShelfLife = (TextView) findViewById(R.id.tvShelfLife);

            // to set title to toolbar
            ConstantsUtils.initActionBarView(this, mToolbar, true, getString(R.string.lbl_competitor_details), 0);

            setUI();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @desc set data to UI
     */
    @SuppressLint("SetTextI18n")
    private void setUI() {
        try {
            String competitorName = competitorBean.getCompanyName();
            if (competitorName != null && !competitorName.equalsIgnoreCase("")) {
                tvCompetitorName.setText(competitorName);
            } else {
                llCompetitorName.setVisibility(View.GONE);
//                deleteView(llCompetitorName);
            }

            String productName = competitorBean.getMaterialDesc();
            if (productName != null && !productName.equalsIgnoreCase("")) {
                tvProductName.setText(productName);
            } else {
                llProductName.setVisibility(View.GONE);
//                deleteView(llProductName);
            }

            String mrp = competitorBean.getMRP();
            String currency = competitorBean.getCurrency();
            if (!mrp.equalsIgnoreCase("") && !currency.equalsIgnoreCase("")) {
                tvMrp.setText(UtilConstants.getCurrencyFormat(currency, mrp));
            } else {
                llMrp.setVisibility(View.GONE);
//                deleteView(llMrp);
            }
            String landingPrice = competitorBean.getLandingPrice();
            if (!landingPrice.equalsIgnoreCase("") && !landingPrice.equalsIgnoreCase("0.0") && !currency.equalsIgnoreCase("")) {
                tvLandgPriceValue.setText(UtilConstants.getCurrencyFormat(currency, landingPrice));
            } else {
                llLandgPrice.setVisibility(View.GONE);
//                deleteView(llLandgPrice);
            }

            String wholesalerPrice = competitorBean.getWholesalerLandingPrice();
            if (!wholesalerPrice.equalsIgnoreCase("") && !wholesalerPrice.equalsIgnoreCase("0.0") && !currency.equalsIgnoreCase("")) {
                tvwholesalePriceValue.setText(UtilConstants.getCurrencyFormat(currency, wholesalerPrice));
            } else {
                llwholesalePrice.setVisibility(View.GONE);
//                deleteView(llwholesalePrice);
            }

            String tradeOffer = competitorBean.getTradeOffer();
            if (tradeOffer != null && !tradeOffer.equalsIgnoreCase("")) {
                tvTradeOffer.setText(tradeOffer);
            } else {
                llTradeOffer.setVisibility(View.GONE);
//                deleteView(llTradeOffer);
            }

            String consumerOffer = competitorBean.getConsumerOffer();
            if (consumerOffer != null && !consumerOffer.equalsIgnoreCase("")) {
                tvConsumerOffer.setText(consumerOffer);
            } else {
                llConsumerOffer.setVisibility(View.GONE);
//                deleteView(llConsumerOffer);
            }

            String schemeName = competitorBean.getSchemeName();
            if (schemeName != null && !schemeName.equalsIgnoreCase("")) {
                tvSchemeName.setText(schemeName);
            } else {
                llSchemeName.setVisibility(View.GONE);
//                deleteView(llSchemeName);
            }

            String shelfLife = competitorBean.getShelfLife();
            String[] shelfArr = shelfLife.split("\\.");
            String days = shelfArr[0];
            if (days != null && !days.equalsIgnoreCase("")) {
                tvShelfLife.setText(days);
            } else {
                llShelfLife.setVisibility(View.GONE);
//                deleteView(llShelfLife);
            }

            String remarks = competitorBean.getRemarks();
            if (remarks != null && !remarks.equalsIgnoreCase("")) {
                tvRemarks.setText(remarks);
            } else {
                llRemarks.setVisibility(View.GONE);
//                deleteView(llRemarks);
            }

            String retailerMargin = competitorBean.getMargin();
            if (retailerMargin != null && !retailerMargin.equalsIgnoreCase("")) {
                tvRetailerMargin.setText(retailerMargin + Constants.PERCENTAGE);
            } else {
                llRetailerMargin.setVisibility(View.GONE);
//                deleteView(llRetailerMargin);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param llItemView
     * @desc deleting view if there is no data
     */
    private void deleteView(LinearLayout llItemView) {
        llItemView.setVisibility(View.GONE);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }
}
