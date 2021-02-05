package com.arteriatech.ss.msecsales.rspl.sync.syncSelectionView;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by e10769 on 04-07-2017.
 */

public class SyncSelectionViewBean implements Serializable {
    private boolean isAll=false;
    private String displayName="";
    private boolean isChecked=false;
    private ArrayList<String> collectionName = new ArrayList<>();

    public boolean isAll() {
        return isAll;
    }

    public void setAll(boolean all) {
        isAll = all;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public ArrayList<String> getCollectionName() {
        return collectionName;
    }

    public void setCollectionName(ArrayList<String> collectionName) {
        this.collectionName = collectionName;
    }
}
