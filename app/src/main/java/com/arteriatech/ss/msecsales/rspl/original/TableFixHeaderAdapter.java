package com.arteriatech.ss.msecsales.rspl.original;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.arteriatech.ss.msecsales.rspl.R;
import com.arteriatech.ss.msecsales.rspl.common.Constants;
import com.arteriatech.ss.msecsales.rspl.mbo.SKUGroupBean;
import com.arteriatech.ss.msecsales.rspl.scroll.BaseTableAdapter;

import java.util.ArrayList;
import java.util.List;

public abstract class TableFixHeaderAdapter<
        TFIRSTHEADER,
        VFIRSTHEADER extends View & TableFixHeaderAdapter.FirstHeaderBinder<TFIRSTHEADER>,
        THEADER,
        VHEADER extends View & TableFixHeaderAdapter.HeaderBinder<THEADER>,
        TBODY,
        VFIRSTBODY extends View & TableFixHeaderAdapter.FirstBodyBinder<TBODY>,
        VBODY extends View & TableFixHeaderAdapter.BodyBinder<TBODY>,
        VEDITTEXTBODY extends View & TableFixHeaderAdapter.BodyEditTextBinder<TBODY>,
        VSPINNERTEXTBODY extends View & TableFixHeaderAdapter.BodySpinnerTextBinder<TBODY>,
        VSECTION extends View & TableFixHeaderAdapter.SectionBinder<TBODY>
        > extends BaseTableAdapter {

    private TFIRSTHEADER firstHeader;
    private List<THEADER> header = new ArrayList<>();
    private List<TBODY> firstBody = new ArrayList<>();
    private List<TBODY> body = new ArrayList<>();
    private List<TBODY> editextBody = new ArrayList<>();
    private List<TBODY> spinnertextBody = new ArrayList<>();
    private List<TBODY> section = new ArrayList<>();
    private ClickListener<TFIRSTHEADER, VFIRSTHEADER> clickListenerFirstHeader;
    private ClickListener<THEADER, VHEADER> clickListenerHeader;
    private ClickListener<TBODY, VFIRSTBODY> clickListenerFirstBody;
    private TextTypeListener textTypeListener;
    private SpinnerSelectionListener spinnerSelectionListener;
    private ClickListener<TBODY, VBODY> clickListenerBody;
    private ClickListener<TBODY, VSECTION> clickListenerSection;
    private LongClickListener<TFIRSTHEADER, VFIRSTHEADER> longClickListenerFirstHeader;
    private LongClickListener<THEADER, VHEADER> longClickListenerHeader;
    private LongClickListener<TBODY, VFIRSTBODY> longClickListenerFirstBody;
    private LongClickListener<TBODY, VBODY> longClickListenerBody;
    private LongClickListener<TBODY, VSECTION> longClickListenerSection;


    public TableFixHeaderAdapter(Context context) {
    }

    protected abstract VFIRSTHEADER inflateFirstHeader();

    protected abstract VHEADER inflateHeader();

    protected abstract VFIRSTBODY inflateFirstBody();

    protected abstract VBODY inflateBody();

    protected abstract VEDITTEXTBODY bindEditTextBody();

    protected abstract VSPINNERTEXTBODY bindSpinnerTextBody();

    protected abstract VSECTION inflateSection();

    protected abstract List<Integer> getHeaderWidths();

    protected abstract int getHeaderHeight();

    protected abstract int getSectionHeight();

    protected abstract int getBodyHeight();

    protected abstract int getEditTextBodyHeight();

    protected abstract boolean isSection(List<TBODY> items, int row);

    public void setClickListenerFirstHeader(ClickListener<TFIRSTHEADER, VFIRSTHEADER> clickListenerFirstHeader) {
        this.clickListenerFirstHeader = clickListenerFirstHeader;
    }

    public void setClickListenerHeader(ClickListener<THEADER, VHEADER> clickListenerHeader) {
        this.clickListenerHeader = clickListenerHeader;
    }

    public void setClickOnSpinner(SpinnerSelectionListener spinnerSelectionListener) {
        this.spinnerSelectionListener = spinnerSelectionListener;
    }

    public void setClickListenerFirstBody(ClickListener<TBODY, VFIRSTBODY> clickListenerFirstBody) {
        this.clickListenerFirstBody = clickListenerFirstBody;
    }

    public void setTextChangeListener(TextTypeListener textTypeListener) {
        this.textTypeListener = textTypeListener;
    }

    public void setClickListenerBody(ClickListener<TBODY, VBODY> clickListenerBody) {
        this.clickListenerBody = clickListenerBody;
    }

    public void setClickListenerSection(ClickListener<TBODY, VSECTION> clickListenerSection) {
        this.clickListenerSection = clickListenerSection;
    }

    public void setLongClickListenerFirstHeader(LongClickListener<TFIRSTHEADER, VFIRSTHEADER> longClickListenerFirstHeader) {
        this.longClickListenerFirstHeader = longClickListenerFirstHeader;
    }

    public void setLongClickListenerHeader(LongClickListener<THEADER, VHEADER> longClickListenerHeader) {
        this.longClickListenerHeader = longClickListenerHeader;
    }

    public void setLongClickListenerFirstBody(LongClickListener<TBODY, VFIRSTBODY> longClickListenerFirstBody) {
        this.longClickListenerFirstBody = longClickListenerFirstBody;
    }

    public void setLongClickListenerBody(LongClickListener<TBODY, VBODY> longClickListenerBody) {
        this.longClickListenerBody = longClickListenerBody;
    }

    public void setLongClickListenerSection(LongClickListener<TBODY, VSECTION> longClickListenerSection) {
        this.longClickListenerSection = longClickListenerSection;
    }

    public void setFirstHeader(TFIRSTHEADER firstHeader) {
        this.firstHeader = firstHeader;
        notifyDataSetChanged();
    }

    public void setFirstBody(List<TBODY> firstBody) {
        this.firstBody = firstBody;
        notifyDataSetChanged();
    }

    public void setEditTextBody(List<TBODY> editextBody) {
        this.editextBody = editextBody;
        notifyDataSetChanged();
    }

    public void setSpinnerTextBody(List<TBODY> spinnertextBody) {
        this.spinnertextBody = spinnertextBody;
//        notifyDataSetChanged();
    }

    public void setSection(List<TBODY> section) {
        this.section = section;
        notifyDataSetChanged();
    }

    public List<THEADER> getHeader() {
        return header;
    }

    public void setHeader(List<THEADER> header) {
        this.header = header;
        notifyDataSetChanged();
    }

    public List<TBODY> getBody() {
        return body;
    }

    public void setBody(List<TBODY> body) {
        this.body = body;
        notifyDataSetChanged();
    }

    @Override
    public int getRowCount() {
        if (body != null) {
            return body.size();
        } else {
            return 0;
        }

    }

    @Override
    public int getColumnCount() {
        return header.size();
    }

    @Override
    public int getWidth(int column) {
        return getHeaderWidths().get(column + 1);
    }

    @Override
    public int getHeight(int row) {
        if (row == -1) return getHeaderHeight();
        else if (isSection(body, row)) return getSectionHeight();
        else return getBodyHeight();
    }

    /**
     * <ul>
     * <li>0 = First header (cell 0,0)</li>
     * <li>1 = Header (row 0)</li>
     * <li>2 = First body (column 0)</li>
     * <li>3 = Body (cell from 1,1 to N,N</li>
     * <li>4 = Section row (separator between rows)</li>
     * </ul>
     */
    @Override
    public int getItemViewType(int row, int column) {
        if (row == -1 && column == -1) return 0;
        else if (row == -1) return 1;
        else if (column == 1) return 6;
        else if (column == 2) return 5;
        else if (isSection(body, row)) return 4;
        else if (column == -1) return 2;
        else return 3;
    }

    /**
     * <ul>
     * <li>0 = First header (cell 0,0)</li>
     * <li>1 = Header (row 0)</li>
     * <li>2 = First body (column 0)</li>
     * <li>3 = Body (cell from 1,1 to N,N</li>
     * <li>4 = Section row (separator between rows)</li>
     * <li>5 = edit Text</li>
     * </ul>
     */
    @Override
    public int getViewTypeCount() {
        return 7;
    }

    @Override
    public View getView(int row, int column, View convertView, ViewGroup parent) {
        switch (getItemViewType(row, column)) {
            case 0:
                return getFirstHeader(row, column, convertView, parent);
            case 1:
                return getHeader(row, column, convertView, parent);
            case 2:
                return getFirstBody(row, column, convertView, parent);
            case 3:
                return getBody(row, column, convertView, parent);
            case 4:
                return getSection(row, column, convertView, parent);
            case 5:
                return getEditTextBody(row, column, convertView, parent);
            case 6:
                return getSpinnerTextBody(row, column, convertView, parent);
            default:
                return null;
        }
    }

    private View getFirstHeader(final int row, final int column, View convertView, ViewGroup parent) {
        /*final VFIRSTHEADER vfirstheader = (convertView == null) ? inflateFirstHeader() : (VFIRSTHEADER) convertView;
        vfirstheader.bindFirstHeader(firstHeader);
        convertView = vfirstheader;
        if (clickListenerFirstHeader != null) convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListenerFirstHeader.onClickItem(firstHeader, vfirstheader, row, column,TableFixHeaderAdapter.this);
            }
        });
        if (longClickListenerFirstHeader != null) convertView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                longClickListenerFirstHeader.onLongClickItem(firstHeader, vfirstheader, row, column);
                return true;
            }
        });*/
//        if (convertView==null){
//            textView = (TextView) findViewById(R.id.tv_text);
//        hEditText = (EditText) findViewById(R.id.etQty);

//        }
        convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.text_view_group, parent, false);
        View vg_root = convertView.findViewById(R.id.vg_root);
        vg_root.setBackgroundResource(R.drawable.cell_header_border_bottom_right_gray);
        TextView textView = (TextView) convertView.findViewById(R.id.tv_text);
        textView.setTypeface(null, Typeface.BOLD);
        textView.setText(firstHeader.toString());
        return convertView;
    }

    private View getHeader(final int row, final int column, View convertView, ViewGroup parent) {
        final VHEADER vheader = (convertView == null) ? inflateHeader() : (VHEADER) convertView;
        vheader.bindHeader(header.get(column), column);
        convertView = vheader;
        /*if (clickListenerHeader != null) convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListenerHeader.onClickItem(header.get(column), vheader, row, column,TableFixHeaderAdapter.this);
            }
        });
        if (longClickListenerHeader != null) convertView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                longClickListenerHeader.onLongClickItem(header.get(column), vheader, row, column);
                return true;
            }
        });*/
        return convertView;
    }

    private View getFirstBody(final int row, final int column, View convertView, ViewGroup parent) {
        final VFIRSTBODY vfirstbody = (convertView == null) ? inflateFirstBody() : (VFIRSTBODY) convertView;
        vfirstbody.bindFirstBody(firstBody.get(row), row, column, TableFixHeaderAdapter.this, clickListenerFirstBody);
        convertView = vfirstbody;
       /* if (clickListenerFirstBody != null) convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListenerFirstBody.onClickItem(firstBody.get(row), vfirstbody, row, column,TableFixHeaderAdapter.this);
            }
        });*/
        if (longClickListenerFirstBody != null)
            convertView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    longClickListenerFirstBody.onLongClickItem(firstBody.get(row), vfirstbody, row, column);
                    return true;
                }
            });
        return convertView;
    }

    private View getBody(final int row, final int column, View convertView, ViewGroup parent) {
        final VBODY vbody = (convertView == null) ? inflateBody() : (VBODY) convertView;
        vbody.bindBody(body.get(row), row, column);
        convertView = vbody;
       /* if (clickListenerBody != null) convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListenerBody.onClickItem(body.get(row), vbody, row, column,TableFixHeaderAdapter.this);
            }
        });
        if (longClickListenerBody != null) convertView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                longClickListenerBody.onLongClickItem(body.get(row), vbody, row, column);
                return true;
            }
        });*/
        return convertView;
    }

    private View getSection(final int row, final int column, View convertView, ViewGroup parent) {
        final VSECTION vsection = (convertView == null) ? inflateSection() : (VSECTION) convertView;
        vsection.bindSection(section.get(row), row, column + 1);
        convertView = vsection;
       /* if (clickListenerSection != null) convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                clickListenerSection.onClickItem(section.get(row), vsection, row, column,TableFixHeaderAdapter.this);
            }
        });
        if (longClickListenerSection != null) convertView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                longClickListenerSection.onLongClickItem(section.get(row), vsection, row, column);
                return true;
            }
        });*/
        return convertView;
    }

    private View getEditTextBody(final int row, final int column, View convertView, ViewGroup parent) {
        final VEDITTEXTBODY vbody = (convertView == null) ? bindEditTextBody() : (VEDITTEXTBODY) convertView;
        OriginalCellViewGroup originalCellViewGroup = null;
        try {
            originalCellViewGroup = (OriginalCellViewGroup) vbody;
        } catch (Exception e) {
            e.printStackTrace();
        }
        vbody.bindEditTextBody(editextBody.get(row), row, column, this, editextBody, textTypeListener, originalCellViewGroup);
        convertView = vbody;
       /* if (clickListenerBody != null) convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListenerBody.onClickItem(body.get(row), vbody, row, column);
            }
        });
        if (longClickListenerBody != null) convertView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                longClickListenerBody.onLongClickItem(body.get(row), vbody, row, column);
                return true;
            }
        });*/
        return convertView;
    }

    private View getSpinnerTextBody(final int row, final int column, View convertView, final ViewGroup parent) {
        try {
            final VSPINNERTEXTBODY vbody = (convertView == null) ? bindSpinnerTextBody() : (VSPINNERTEXTBODY) convertView;
            OriginalCellViewGroup originalCellViewGroup = null;
            try {
                originalCellViewGroup = (OriginalCellViewGroup) vbody;
            } catch (Exception e) {
                e.printStackTrace();
            }
            final SKUGroupBean currentSKUGrpBean = (SKUGroupBean) spinnertextBody.get(row);


            if (originalCellViewGroup != null) {
                if (currentSKUGrpBean.isHeader()) {
                    originalCellViewGroup.lHeaderSpinner.setVisibility(View.VISIBLE);
                    originalCellViewGroup.tvItemUOM.setVisibility(View.GONE);
                    /*try {
                        if (currentSKUGrpBean.getMatTypeVal().equalsIgnoreCase(Constants.str_01) || currentSKUGrpBean.getMatTypeVal().equalsIgnoreCase(Constants.str_02)) {
                            originalCellViewGroup.vg_root.setBackgroundColor(originalCellViewGroup.getResources().getColor(R.color.ORANGE));
                        }else

                            //  new launched products are blue color
                            if (currentSKUGrpBean.getMatTypeVal().equalsIgnoreCase(Constants.str_03)) {
                                originalCellViewGroup.vg_root.setBackgroundColor(originalCellViewGroup.getResources().getColor(R.color.light_blue_color));
                            }
                    } catch (Resources.NotFoundException e) {
                        e.printStackTrace();
                    }*/
                    final ArrayAdapter<String> adapter = new ArrayAdapter<String>(parent.getContext(),
                            android.R.layout.simple_dropdown_item_1line, currentSKUGrpBean.getAlUOM());
                    originalCellViewGroup.autUOM.setThreshold(1);
                    originalCellViewGroup.autUOM.setAdapter(adapter);
                    final OriginalCellViewGroup finalOriginalCellViewGroup = originalCellViewGroup;
                    originalCellViewGroup.autUOM.setOnTouchListener(new View.OnTouchListener() {

                        @SuppressLint("ClickableViewAccessibility")
                        @Override
                        public boolean onTouch(View paramView, MotionEvent paramMotionEvent) {
                            try {
                                InputMethodManager in = (InputMethodManager) parent.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                                if (in != null)
                                    in.hideSoftInputFromWindow(paramView.getWindowToken(), 0);
                                if (((SKUGroupBean) spinnertextBody.get(row)).getAlUOM().size() > 0) {
                                    // show all suggestions
                                    if (!finalOriginalCellViewGroup.autUOM.getText().toString().equals(""))
                                        adapter.getFilter().filter(null);
                                    finalOriginalCellViewGroup.autUOM.showDropDown();
                                }
                            } catch (Throwable e) {
                                e.printStackTrace();
                            }
                            return true;
                        }
                    });
                    originalCellViewGroup.autUOM.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        public void onItemClick(AdapterView<?> parent, View view, int position, long rowId) {
                            InputMethodManager in = (InputMethodManager) parent.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                            if (in != null)
                                in.hideSoftInputFromWindow(view.getWindowToken(), 0);
                            currentSKUGrpBean.setSelectedUOM((String) parent.getItemAtPosition(position));
                            setSalesOrderItemData((String) parent.getItemAtPosition(position),currentSKUGrpBean,(ArrayList<SKUGroupBean>)spinnertextBody,row);
                            notifyDataSetChanged();
    //                        getBody(row, 0, null, null);
                        }
                    });
                    int pos = currentSKUGrpBean.getAlUOM().indexOf(currentSKUGrpBean.getSelectedUOM());
                    if (pos >= 0)
                        originalCellViewGroup.autUOM.setText(currentSKUGrpBean.getAlUOM().get(pos));
    //            originalCellViewGroup.autUOM.setCompoundDrawablesWithIntrinsicBounds(null, null,
    //                    ContextCompat.getDrawable(parent.getContext(), R.drawable.ic_arrow_drop_down_black_24dp), null);
                } else {
                    originalCellViewGroup.lHeaderSpinner.setVisibility(View.GONE);
                    originalCellViewGroup.tvItemUOM.setVisibility(View.VISIBLE);
                    originalCellViewGroup.tvItemUOM.setText(currentSKUGrpBean.getSelectedUOM());
                }
            }
        /*ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                parent.getContext(),
                R.array.sort_types,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        if (originalCellViewGroup != null)
            originalCellViewGroup.hSpinnerText.setAdapter(adapter);*/
//        vbody.bindSpinnerBody(spinnertextBody.get(row),row,column,this,spinnertextBody,spinnerSelectionListener,originalCellViewGroup);
            convertView = vbody;
       /* if (clickListenerBody != null) convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListenerBody.onClickItem(body.get(row), vbody, row, column);
            }
        });
        if (longClickListenerBody != null) convertView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                longClickListenerBody.onLongClickItem(body.get(row), vbody, row, column);
                return true;
            }
        });*/
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return convertView;
    }
    private void setSalesOrderItemData(String uom, SKUGroupBean item, ArrayList<SKUGroupBean> skuGroupBeanArrayList, int row) {
        if (item.isHeader()) {
            if (!item.getSkuSubGroupBeanArrayList().isEmpty()) {
                for (SKUGroupBean subItem : item.getSkuSubGroupBeanArrayList()) {
                    subItem.setSelectedUOM(uom);
                }
            } else if (item.isViewOpened()) {
//                ArrayList<SKUGroupBean> skuGroupBeanArrayListFinal = new ArrayList<>();
                boolean isStored = false;
                for (int i = row + 1; i < skuGroupBeanArrayList.size(); i++) {
                    SKUGroupBean skuGroupBean = skuGroupBeanArrayList.get(i);
                    if (!skuGroupBean.isHeader()) {
                        /*if (!isStored) {
                            skuGroupBean.setSelectedUOM(uom);
//                            skuGroupBeanArrayListFinal.add(skuGroupBean);
                            isStored = true;
                        } else {*/
                            skuGroupBean.setSelectedUOM(uom);
//                            skuGroupBeanArrayListFinal.add(skuGroupBean);
//                        }
                    } else {
                        break;
                    }
                }
               /* if (!skuGroupBeanArrayListFinal.isEmpty()) {
                    skuGroupBeanArrayList.removeAll(skuGroupBeanArrayListFinal);
                    skuGroupBeanArrayList.addAll(row + 1, skuGroupBeanArrayListFinal);
                }*/
            }
        }
    }
    public interface ClickListener<T, V> {
        void onClickItem(T t, V v, int row, int column, BaseTableAdapter baseTableAdapter, ImageView ivExpandIcon);
    }

    public interface TextTypeListener {
        void onTextChangeItem(SKUGroupBean t, int row, int column, BaseTableAdapter baseTableAdapter, EditText etView, OriginalCellViewGroup viewGroup);
    }

    public interface SpinnerSelectionListener {
        void onTextChangeItem(SKUGroupBean t, int row, int column, BaseTableAdapter baseTableAdapter, Spinner spView, OriginalCellViewGroup viewGroup);
    }

    public interface LongClickListener<T, V> {
        void onLongClickItem(T t, V v, int row, int column);
    }

    public interface FirstHeaderBinder<T> {
        void bindFirstHeader(T item);
    }

    public interface HeaderBinder<T> {
        void bindHeader(T item, int column);
    }

    public interface FirstBodyBinder<T> {
        void bindFirstBody(T item, int row, int column, TableFixHeaderAdapter tableFixHeaderAdapter, ClickListener clickListenerFirstBody);
    }

    public interface BodyBinder<T> {
        void bindBody(T item, int row, int column);
    }

    public interface BodyEditTextBinder<T> {
        void bindEditTextBody(T item, int row, int column, TableFixHeaderAdapter tableFixHeaderAdapter, List editextBody, TextTypeListener textTypeListener, OriginalCellViewGroup vbody);
    }

    public interface BodySpinnerTextBinder<T> {
        void bindSpinnerBody(T item, int row, int column, TableFixHeaderAdapter tableFixHeaderAdapter, List editextBody, SpinnerSelectionListener spinnerSelectionListener, OriginalCellViewGroup vbody);
    }

    public interface SectionBinder<T> {
        void bindSection(T item, int row, int column);
    }
}
