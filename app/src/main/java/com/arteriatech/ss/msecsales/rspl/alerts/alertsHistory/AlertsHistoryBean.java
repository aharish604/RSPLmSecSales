package com.arteriatech.ss.msecsales.rspl.alerts.alertsHistory;

import java.io.Serializable;

/**
 * Created by e10860 on 3/29/2018.
 */

public class AlertsHistoryBean implements Serializable {

    private String AlertGUID = "";
    private String PartnerType = "";
    private String PartnerID = "";
    private String AlertType = "";
    private String AlertTypeDesc = "";
    private String ObjectType = "";
    private String ObjectID = "";
    private String ObjectSubID = "";
    private String CreatedBy = "";
    private String CreatedAt = "";
    private String ConfirmedBy = "";
    private String ConfirmedOn = "";
    private String ConfirmedAt = "";
    private String AlertText = "";
    private String CreatedOn = "";

    public String getApplication() {
        return Application;
    }

    public void setApplication(String application) {
        Application = application;
    }

    private String Application = "";

    public String getAlertGUID() {
        return AlertGUID;
    }

    public void setAlertGUID(String alertGUID) {
        AlertGUID = alertGUID;
    }

    public String getPartnerType() {
        return PartnerType;
    }

    public void setPartnerType(String partnerType) {
        PartnerType = partnerType;
    }

    public String getPartnerID() {
        return PartnerID;
    }

    public void setPartnerID(String partnerID) {
        PartnerID = partnerID;
    }

    public String getAlertType() {
        return AlertType;
    }

    public void setAlertType(String alertType) {
        AlertType = alertType;
    }

    public String getAlertTypeDesc() {
        return AlertTypeDesc;
    }

    public void setAlertTypeDesc(String alertTypeDesc) {
        AlertTypeDesc = alertTypeDesc;
    }

    public String getObjectType() {
        return ObjectType;
    }

    public void setObjectType(String objectType) {
        ObjectType = objectType;
    }

    public String getObjectID() {
        return ObjectID;
    }

    public void setObjectID(String objectID) {
        ObjectID = objectID;
    }

    public String getObjectSubID() {
        return ObjectSubID;
    }

    public void setObjectSubID(String objectSubID) {
        ObjectSubID = objectSubID;
    }

    public String getCreatedBy() {
        return CreatedBy;
    }

    public void setCreatedBy(String createdBy) {
        CreatedBy = createdBy;
    }

    public String getCreatedAt() {
        return CreatedAt;
    }

    public void setCreatedAt(String createdAt) {
        CreatedAt = createdAt;
    }

    public String getConfirmedBy() {
        return ConfirmedBy;
    }

    public void setConfirmedBy(String confirmedBy) {
        ConfirmedBy = confirmedBy;
    }

    public String getConfirmedOn() {
        return ConfirmedOn;
    }

    public void setConfirmedOn(String confirmedOn) {
        ConfirmedOn = confirmedOn;
    }

    public String getConfirmedAt() {
        return ConfirmedAt;
    }

    public void setConfirmedAt(String confirmedAt) {
        ConfirmedAt = confirmedAt;
    }

    public String getAlertText() {
        return AlertText;
    }

    public void setAlertText(String alertText) {
        AlertText = alertText;
    }

    public String getCreatedOn() {
        return CreatedOn;
    }

    public void setCreatedOn(String CreatedOn) {
        this.CreatedOn = CreatedOn;
    }

    private boolean isSelected = false;

    public void setSelected(boolean selected) {
        isSelected = selected;
    }


    public boolean isSelected() {
        return isSelected;
    }


}
