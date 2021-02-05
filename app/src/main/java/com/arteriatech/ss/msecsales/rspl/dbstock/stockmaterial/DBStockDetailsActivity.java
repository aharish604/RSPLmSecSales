package com.arteriatech.ss.msecsales.rspl.dbstock.stockmaterial;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.arteriatech.mutils.adapter.AdapterInterface;
import com.arteriatech.mutils.adapter.SimpleRecyclerViewAdapter;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.ss.msecsales.rspl.R;
import com.arteriatech.ss.msecsales.rspl.common.Constants;
import com.arteriatech.ss.msecsales.rspl.common.ConstantsUtils;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class DBStockDetailsActivity extends AppCompatActivity implements AdapterInterface<DBStockBean> {

    public static final String KEY_STOCKTYPE = "stockType";
    private Toolbar toolbar;
    private ArrayList<DBStockBean> alDBStockList = null;
    private RecyclerView recyclerView;
    private TextView noRecordFound;
    private SimpleRecyclerViewAdapter<DBStockBean> simpleAdapter;
    private TextView tvMatNo;
    private TextView tvMatDesc, tvQty;
    private String strStockType = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dbstock_details);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.db_stocks), 0);
        Intent intent = getIntent();
        if (intent != null) {
            alDBStockList = (ArrayList<DBStockBean>) intent.getSerializableExtra(Constants.EXTRA_BEAN);
            strStockType = intent.getStringExtra(KEY_STOCKTYPE);
        }
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        tvMatNo = (TextView) findViewById(R.id.tvMatNo);
        tvMatDesc = (TextView) findViewById(R.id.tvMatDesc);
        tvQty = (TextView) findViewById(R.id.expenseAmount);
        noRecordFound = (TextView) findViewById(R.id.no_record_found);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        simpleAdapter = new SimpleRecyclerViewAdapter<DBStockBean>(this, R.layout.db_stock_detail_item, this, recyclerView, noRecordFound);
        recyclerView.setAdapter(simpleAdapter);
        if (alDBStockList != null) {
            simpleAdapter.refreshAdapter(alDBStockList);
            if (!alDBStockList.isEmpty()) {
                if (strStockType.equalsIgnoreCase(Constants.str_01)) {
                    tvMatNo.setText(alDBStockList.get(0).getMaterialNo());
                    tvMatDesc.setText(alDBStockList.get(0).getMaterialDesc());
                } else {
                    tvMatNo.setText(alDBStockList.get(0).getOrderMaterialGroupID());
                    tvMatDesc.setText(alDBStockList.get(0).getOrderMaterialGroupDesc());
                }
                BigDecimal bigDecimal = new BigDecimal(0);
                for (DBStockBean dbStockBean : alDBStockList) {
                    bigDecimal = bigDecimal.add(new BigDecimal(dbStockBean.getQAQty()));
                }
                tvQty.setText(String.valueOf(bigDecimal) + " " + alDBStockList.get(0).getUom());
            }
        }
    }

    @Override
    public void onItemClick(DBStockBean dbStockBean, View view, int i) {

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i, View view) {
        return new DBStockDetailVH(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i, DBStockBean stock) {
        ((DBStockDetailVH) viewHolder).tvMaterialDesc.setText(getString(R.string.po_details_display_value, stock.getMaterialDesc(), stock.getMaterialNo()));
        ((DBStockDetailVH) viewHolder).tvBrand.setText(stock.getBatch());
        ((DBStockDetailVH) viewHolder).tvMfd.setText(stock.getMFD());
        ((DBStockDetailVH) viewHolder).tvQty.setText(stock.getQAQty() + " " + stock.getUom());
        Double mDouRetLanPriceCal = 0.0;
        try {
            mDouRetLanPriceCal = Double.parseDouble(stock.getFirstMrpLandingPrice()) / Double.parseDouble(stock.getFirstMrpQty());
        } catch (NumberFormatException e) {
            mDouRetLanPriceCal = 0.0;
        }
        if (mDouRetLanPriceCal.isNaN() || mDouRetLanPriceCal.isInfinite()) {
            mDouRetLanPriceCal = 0.0;
        }

        ((DBStockDetailVH) viewHolder).tvMrp.setText(UtilConstants.getCurrencyFormat(stock.getCurrency(), mDouRetLanPriceCal.toString()));
        ((DBStockDetailVH) viewHolder).tvAmount.setText(UtilConstants.getCurrencyFormat(stock.getCurrency(), stock.getMRP()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_dbstk_detl, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.menu_mfd:
                sortByDate();
                return true;
            case R.id.menu_mrp:
                sortBymrp();
                return true;
            case R.id.menu_rl_price:
                sortByPrice();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void sortByDate() {
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        if (alDBStockList != null) {
            Collections.sort(alDBStockList, new Comparator<DBStockBean>() {
                public int compare(DBStockBean one, DBStockBean other) {
                    try {
                        return simpleDateFormat.parse(one.getMFD()).compareTo(simpleDateFormat.parse(other.getMFD()));
                    } catch (ParseException e) {
                        e.printStackTrace();
                        return other.getMFD().compareTo(one.getMFD());
                    }
                }
            });
            simpleAdapter.refreshAdapter(alDBStockList);
        }

    }

    private void sortBymrp() {
        if (alDBStockList != null) {
            Collections.sort(alDBStockList, new Comparator<DBStockBean>() {
                public int compare(DBStockBean one, DBStockBean other) {
                    return other.getMRP().compareTo(one.getMRP());
                }
            });
            simpleAdapter.refreshAdapter(alDBStockList);
        }
    }

    private void sortByPrice() {
        if (alDBStockList != null) {
            Collections.sort(alDBStockList, new Comparator<DBStockBean>() {
                public int compare(DBStockBean one, DBStockBean other) {
                    return other.getFirstMrpLandingPrice().compareTo(one.getFirstMrpLandingPrice());
                }
            });
            simpleAdapter.refreshAdapter(alDBStockList);
        }
    }
}
