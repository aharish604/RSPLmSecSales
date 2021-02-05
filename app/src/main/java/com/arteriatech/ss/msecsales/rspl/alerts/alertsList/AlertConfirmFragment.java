package com.arteriatech.ss.msecsales.rspl.alerts.alertsList;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;
import android.text.TextUtils;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
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
import com.arteriatech.ss.msecsales.rspl.alerts.AlertsActivity;
import com.arteriatech.ss.msecsales.rspl.alerts.alertsHistory.AlertsHistoryVH;
import com.arteriatech.ss.msecsales.rspl.common.Constants;
import com.arteriatech.ss.msecsales.rspl.common.ConstantsUtils;
import com.arteriatech.ss.msecsales.rspl.mbo.BirthdaysBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by e10860 on 3/29/2018.
 */

public class AlertConfirmFragment extends Fragment implements AlertsListView, AdapterInterface<BirthdaysBean> {

    // index is used to animate only the selected row
    // dirty fix, find a better solution
    private static int currentSelectedIndex = -1;
    AlertsListPresenterImpl alertsListPresenter;
    MenuItem item = null;
    private ProgressDialog progressDialog;
    private RecyclerView rvAlertHistory;
    private TextView tvNoRecord;
    private SimpleRecyclerViewAdapter<BirthdaysBean> simpleRecyclerViewAdapter;
    private List<BirthdaysBean> alertsHistoryBeanList;
    private Paint p = new Paint();
    //    private SwipeRefreshLayout swipeRefresh;
    private ItemTouchHelper.SimpleCallback simpleCallback;
    private ItemTouchHelper itemTouchHelper;
    private SparseBooleanArray selectedItems;
    // array used to perform multiple animation at once
    private SparseBooleanArray animationItemsIndex;
    private boolean reverseAllAnimations = false;
    private CoordinatorLayout coordinatorLayout;
    private boolean isPostMethod = false;
    private int AlertCount = 0;

    public AlertConfirmFragment() {
        /*
        empty constructor is required
         */
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_alert_history, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setViewID(view);
    }

    @Override
    public void onShowProgress(String msg) {
//        progressDialog = ConstantsUtils.showProgressDialog(getContext(), msg);
//        swipeRefresh.setRefreshing(true);
    }

    @Override
    public void onHideProgress() {
//        if (progressDialog != null)
//            progressDialog.dismiss();
//        swipeRefresh.setRefreshing(false);
    }

    @Override
    public void showMessage(String errorMsg) {
        ConstantsUtils.displayShortToast(getContext(), errorMsg);
    }

    @Override
    public void showData(List<BirthdaysBean> historyBeanList) {
        if (isPostMethod) {
            isPostMethod = false;
//            clearSelections();
//            hideMenu();
            ConstantsUtils.displayAlertMessage(coordinatorLayout, AlertCount + " Confirmed");
        }
        this.alertsHistoryBeanList = historyBeanList;
        simpleRecyclerViewAdapter = new SimpleRecyclerViewAdapter<>(getContext(), R.layout.snippet_alerts_history, this, rvAlertHistory, tvNoRecord);
        rvAlertHistory.setAdapter(simpleRecyclerViewAdapter);
        simpleRecyclerViewAdapter.refreshAdapter((ArrayList<BirthdaysBean>) historyBeanList);
    }

    private void setViewID(View view) {
        selectedItems = new SparseBooleanArray();
        animationItemsIndex = new SparseBooleanArray();

        rvAlertHistory = (RecyclerView) view.findViewById(R.id.rvAlertHistory);
        tvNoRecord = (TextView) view.findViewById(R.id.no_record_found);
        coordinatorLayout = (CoordinatorLayout) view.findViewById(R.id.coordinatorLayout);
//        swipeRefresh = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefresh);
//        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                alertsListPresenter.loadAlertsList();
//            }
//        });
//        ConstantsUtils.setProgressColor(getContext(), swipeRefresh);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rvAlertHistory.setLayoutManager(linearLayoutManager);
        rvAlertHistory.setNestedScrollingEnabled(false);
        alertsListPresenter = new AlertsListPresenterImpl(getContext(), this, false);
        rvAlertHistory.setNestedScrollingEnabled(false);
       /* rvAlertHistory.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
//                swipeRefresh.setEnabled(linearLayoutManager.findFirstCompletelyVisibleItemPosition() == 0); // 0 is for first item position
            }
        });*/
        initSwipe();
        simpleRecyclerViewAdapter = new SimpleRecyclerViewAdapter<>(getContext(), R.layout.snippet_alerts_history, this, rvAlertHistory, tvNoRecord);
        rvAlertHistory.setAdapter(simpleRecyclerViewAdapter);
        alertsListPresenter.loadAlertsList();
    }

    @Override
    public void onItemClick(BirthdaysBean alertsHistoryBean, View view, int i) {
        if (!alertsHistoryBean.getMobileNo().equalsIgnoreCase("")) {
            Intent dialIntent = new Intent(Intent.ACTION_DIAL, Uri.parse(Constants.tel_txt + (alertsHistoryBean.getMobileNo())));
            getActivity().startActivity(dialIntent);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i, View view) {
        return new AlertsHistoryVH(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position, final BirthdaysBean alertsHistoryBean) {

        ((AlertsHistoryVH) holder).tvAlertMsg.setText(alertsHistoryBean.getRetailerName());
        ((AlertsHistoryVH) holder).tvRetailerOwner.setText(alertsHistoryBean.getOwnerName());
        if (!TextUtils.isEmpty(alertsHistoryBean.getRetailerName()))
            ((AlertsHistoryVH) holder).iconText.setText(alertsHistoryBean.getRetailerName().substring(0, 1));
        if (!"".equalsIgnoreCase(alertsHistoryBean.getAlertGUID())) {
            ((AlertsHistoryVH) holder).tvAlertMsg.setText(alertsHistoryBean.getAlertText());
            ((AlertsHistoryVH) holder).tvRetailerOwner.setText("");
            if (!TextUtils.isEmpty(alertsHistoryBean.getAlertText()))
                ((AlertsHistoryVH) holder).iconText.setText(alertsHistoryBean.getAlertText().substring(0, 1));
        }
        if (alertsHistoryBean.getAppointmentAlert()) {
            String startTime = UtilConstants.convertTimeOnly(alertsHistoryBean.getAppointmentTime()).substring(0, 5);
            String endTime = UtilConstants.convertTimeOnly(alertsHistoryBean.getAppointmentEndTime()).substring(0, 5);
            ((AlertsHistoryVH) holder).tvRetailerOwner.setText(alertsHistoryBean.getAppointmentType() + " "
                    + startTime + " "
                    + endTime);
            ((AlertsHistoryVH) holder).ivAppointment.setImageResource(R.drawable.ic_calender_24dp);
        }
        if (!alertsHistoryBean.getDOB().equalsIgnoreCase("") && ConstantsUtils.showImage(alertsHistoryBean.getDOB())) {//alertsHistoryBean.getDOB().contains(splitDayMonth[1] + "/" + splitDayMonth[0])
            ((AlertsHistoryVH) holder).ivDOB.setVisibility(View.VISIBLE);
            ((AlertsHistoryVH) holder).ivDOB.setImageResource(R.drawable.ic_cake_black_24dp);
        } else {
            ((AlertsHistoryVH) holder).ivDOB.setVisibility(View.GONE);
        }

        if (!alertsHistoryBean.getAnniversary().equalsIgnoreCase("") && ConstantsUtils.showImage(alertsHistoryBean.getAnniversary())) {//alertsHistoryBean.getAnniversary().contains(splitDayMonth[1] + "/" + splitDayMonth[0])
            ((AlertsHistoryVH) holder).ivAnversiry.setVisibility(View.VISIBLE);
            ((AlertsHistoryVH) holder).ivAnversiry.setImageResource(R.drawable.ic_favorite_black_24dp);
        } else {
            ((AlertsHistoryVH) holder).ivAnversiry.setVisibility(View.GONE);
        }


        if (alertsHistoryBean.getAppointmentAlert()) {
            ((AlertsHistoryVH) holder).ivAppointment.setVisibility(View.VISIBLE);
            ((AlertsHistoryVH) holder).ivAppointment.setImageResource(R.drawable.ic_calender_24dp);
            ((AlertsHistoryVH) holder).ivAnversiry.setVisibility(View.GONE);
            ((AlertsHistoryVH) holder).ivDOB.setVisibility(View.GONE);
        } else {
            ((AlertsHistoryVH) holder).ivAppointment.setVisibility(View.GONE);
        }
        if (!"".equalsIgnoreCase(alertsHistoryBean.getAlertGUID())) {
//            ((AlertsHistoryVH) holder).ivMobileNo.setVisibility(View.INVISIBLE);
            ((AlertsHistoryVH) holder).ivAnversiry.setImageResource(R.drawable.ic_notifications_black_24dp);
            ((AlertsHistoryVH) holder).tvRetailerOwner.setVisibility(View.GONE);
            ((AlertsHistoryVH) holder).ivAnversiry.setVisibility(View.VISIBLE);
            ((AlertsHistoryVH) holder).ivDOB.setVisibility(View.GONE);
        }


//        ((AlertsHistoryVH) holder).tvAlertMsg.setText(alertsHistoryBean.getAlertText());
//        ((AlertsHistoryVH) holder).tvCreatedOn.setText(getString(R.string.createdon) + " " + alertsHistoryBean.getCreatedOn());
//        ((AlertsHistoryVH) holder).view.setBackgroundColor(alertsHistoryBean.isSelected() ? Color.CYAN : Color.WHITE);


        // displaying the first letter of From in icon text
//        if (!TextUtils.isEmpty(alertsHistoryBean.getAlertText())) {
//            ((AlertsHistoryVH) holder).iconText.setText(alertsHistoryBean.getAlertText().substring(0, 1));
//        } else {
//            ((AlertsHistoryVH) holder).iconText.setText(" ");
//        }
//
//        ((AlertsHistoryVH) holder).iconContainer.setVisibility(View.GONE);


        /*((AlertsHistoryVH) holder).ll_alerts_list_sel.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                alertsHistoryBean.setSelected(!alertsHistoryBean.isSelected());
//                ((AlertsHistoryVH) holder).view.setBackgroundColor(alertsHistoryBean.isSelected() ? Color.CYAN : Color.WHITE);
                onRowLongClicked(position);
                return true;
            }
        });*/

        /*((AlertsHistoryVH) holder).iconContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onListItemSelect(position);
            }
        });

        ((AlertsHistoryVH) holder).ll_alerts_list_sel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onListItemSelect(position);
            }
        });*/

        // handle icon animation
//        applyIconAnimation(((AlertsHistoryVH) holder), position);

        // display profile image
//        applyProfilePicture(((AlertsHistoryVH) holder), alertsHistoryBean);
    }

    private void initSwipe() {
        simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
                if (viewHolder != null) {
                    final View foregroundView = ((AlertsHistoryVH) viewHolder).viewForeground;
                    getDefaultUIUtil().onSelected(foregroundView);

                }
            }

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }


            @Override
            public void onChildDrawOver(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                final View foregroundView = (((AlertsHistoryVH) viewHolder).viewForeground);
                getDefaultUIUtil().onDrawOver(c, recyclerView, foregroundView, dX, dY, actionState, isCurrentlyActive);
            }

            @Override
            public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                final View foregroundView = ((AlertsHistoryVH) viewHolder).viewForeground;
                getDefaultUIUtil().clearView(foregroundView);
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

               /* Bitmap icon;
                if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {

                    View itemView = viewHolder.itemView;
                    float height = (float) itemView.getBottom() - (float) itemView.getTop();
                    float width = height / 3;

                    if (dX > 0) {
                        p.setColor(Color.parseColor(Constants.red_hex_color_code)); //#D32F2F
                        RectF background = new RectF((float) itemView.getLeft(), (float) itemView.getTop(), dX, (float) itemView.getBottom());
                        c.drawRect(background, p);
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_done_black_24dp);
                        RectF icon_dest = new RectF((float) itemView.getLeft() + width, (float) itemView.getTop() + width, (float) itemView.getLeft() + 2 * width, (float) itemView.getBottom() - width);
                        c.drawBitmap(icon, null, icon_dest, p);
                    } else {
                        p.setColor(Color.parseColor(Constants.red_hex_color_code)); //#D32F2F
                        RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom());
                        c.drawRect(background, p);
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_done_black_24dp);
                        RectF icon_dest = new RectF((float) itemView.getRight() - 2 * width, (float) itemView.getTop() + width, (float) itemView.getRight() - width, (float) itemView.getBottom() - width);
                        if (icon != null)
                            c.drawBitmap(icon, null, icon_dest, p);
                    }
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);*/
                View foregroundView = ((AlertsHistoryVH) viewHolder).viewForeground;
                getDefaultUIUtil().onDraw(c, recyclerView, foregroundView, dX, dY, actionState, isCurrentlyActive);
            }

            @Override
            public int convertToAbsoluteDirection(int flags, int layoutDirection) {
                return super.convertToAbsoluteDirection(flags, layoutDirection);
            }

            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {
                if (viewHolder instanceof AlertsHistoryVH) {
                    final int position = viewHolder.getAdapterPosition(); //swiped position
                    if (!alertsHistoryBeanList.isEmpty()) {
                        if (direction == ItemTouchHelper.RIGHT) {
                            ((AlertsHistoryVH) viewHolder).ivRight.setVisibility(View.GONE);
                            ((AlertsHistoryVH) viewHolder).ivLeft.setVisibility(View.VISIBLE);
                        } else if (direction == ItemTouchHelper.LEFT) {
                            ((AlertsHistoryVH) viewHolder).ivRight.setVisibility(View.VISIBLE);
                            ((AlertsHistoryVH) viewHolder).ivLeft.setVisibility(View.GONE);
                        }
                        if (direction == ItemTouchHelper.LEFT || direction == ItemTouchHelper.RIGHT) {//swipe right
//                            if (UtilConstants.isNetworkAvailable(getContext())) {
                            isPostMethod = true;
                            AlertCount = 1;
//                                alertsListPresenter.callAlertBatch(alertsHistoryBeanList.get(position));
                            alertsListPresenter.updateDataVaultRecord(position);
//                            } else {
//                                showMessage(getString(R.string.no_network_conn));
//                            }
                        }
                    }
                }
            }
        };
        itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(rvAlertHistory);
    }

   /* private void applyProfilePicture(AlertsHistoryVH holder, BirthdaysBean message) {
        holder.imgProfile.setImageResource(R.drawable.bg_circle);
        holder.imgProfile.setColorFilter(R.color.secondaryDarkColor);
        holder.iconText.setVisibility(View.VISIBLE);
    }*/

   /* private void applyIconAnimation(AlertsHistoryVH holder, int position) {
        if (selectedItems.get(position, false)) {
            holder.iconContainer.setVisibility(View.VISIBLE);
            holder.iconFront.setVisibility(View.GONE);
            resetIconYAxis(holder.iconBack);
            holder.iconBack.setVisibility(View.VISIBLE);
            holder.iconBack.setAlpha(1);
            if (currentSelectedIndex == position) {
                FlipAnimator.flipView(getContext(), holder.iconBack, holder.iconFront, true);
                resetCurrentIndex();
            }
        } else {
            holder.iconContainer.setVisibility(View.GONE);
            holder.iconBack.setVisibility(View.GONE);
            resetIconYAxis(holder.iconFront);
            holder.iconFront.setVisibility(View.VISIBLE);
            holder.iconFront.setAlpha(1);
            if ((reverseAllAnimations && animationItemsIndex.get(position, false)) || currentSelectedIndex == position) {
                FlipAnimator.flipView(getContext(), holder.iconBack, holder.iconFront, false);
                resetCurrentIndex();
            }
        }
    }*/

    private void resetCurrentIndex() {
        currentSelectedIndex = -1;
    }

    // As the views will be reused, sometimes the icon appears as
    // flipped because older view is reused. Reset the Y-axis to 0
    private void resetIconYAxis(View view) {
        if (view.getRotationY() != 0) {
            view.setRotationY(0);
        }
    }

    /*//List item select method
    private void onListItemSelect(int position) {


        toggleSelection(position);

        if (getActivity() instanceof AlertsActivity) {
            //set action mode title on item selection
          *//*  if (hasCheckedItems)
                ((AlertsActivity) getActivity()).setActionBarTitle(String.valueOf(getSelectedItemCount()) + " selected");
            else
                ((AlertsActivity) getActivity()).setActionBarTitle(getContext().getString(R.string.alerts));*//*

            ((AlertsActivity) getActivity()).setActionBarTitle(getContext().getString(R.string.alerts));
        }

        hideMenu();
    }

    public void toggleSelection(int pos) {
        currentSelectedIndex = pos;
        if (selectedItems.get(pos, false)) {
            selectedItems.delete(pos);
            animationItemsIndex.delete(pos);
            alertsHistoryBeanList.get(pos).setSelected(false);
        } else {
            selectedItems.put(pos, true);
            animationItemsIndex.put(pos, true);
            alertsHistoryBeanList.get(pos).setSelected(true);
        }
        simpleRecyclerViewAdapter.notifyItemChanged(pos);
    }

    public void clearSelections() {
        try {
            reverseAllAnimations = true;
            selectedItems.clear();
            simpleRecyclerViewAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getSelectedItemCount() {
        return selectedItems.size();
    }

    public void onRowLongClicked(int position) {
        // long press is performed, enable action mode
        onListItemSelect(position);
    }*/

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_confirm_alerts, menu);
        ((AlertsActivity) getActivity()).setActionBarTitle(getResources().getString(R.string.alerts));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_confirm:
//                onConfirmAlerts();
                break;

        }
        return true;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        item = menu.findItem(R.id.menu_confirm);
//        hideMenu();
    }

    /*public void hideMenu() {
        try {
            if (getSelectedItemCount() > 0)
                item.setVisible(true);
            else {
                item.setVisible(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onConfirmAlerts() {
        if (getSelectedItemCount() > 0) {
            isPostMethod = true;
            AlertCount = getSelectedItemCount();
            if (UtilConstants.isNetworkAvailable(getContext())) {
                alertsListPresenter.callAlertBatch(alertsHistoryBeanList);
            } else {
                showMessage(getString(R.string.no_network_conn));
            }
        } else {
            ConstantsUtils.displayShortToast(getContext(), "Please select at least one alert");
        }

    }*/

}