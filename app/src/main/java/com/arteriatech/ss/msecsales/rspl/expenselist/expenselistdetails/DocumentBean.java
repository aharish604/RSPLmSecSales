package com.arteriatech.ss.msecsales.rspl.expenselist.expenselistdetails;


public class DocumentBean {

    private String DocumentID;
    private String DocumentStore;
    private String Application;
    private String DocumentLink;
    private String DocumentMimeType = "";
    private String FileName = "";
    private String mediaLink = "";
    private String imagePath = "";
    private String displayName = "";
    private String CreatedOn = "";

    public String getDocumentID() {
        return DocumentID;
    }

    public void setDocumentID(String documentID) {
        DocumentID = documentID;
    }

    public String getDocumentStore() {
        return DocumentStore;
    }

    public void setDocumentStore(String documentStore) {
        DocumentStore = documentStore;
    }

    public String getApplication() {
        return Application;
    }

    public void setApplication(String application) {
        Application = application;
    }

    public String getDocumentLink() {
        return DocumentLink;
    }

    public void setDocumentLink(String documentLink) {
        DocumentLink = documentLink;
    }

    public String getDocumentMimeType() {
        return DocumentMimeType;
    }

    public void setDocumentMimeType(String documentMimeType) {
        DocumentMimeType = documentMimeType;
    }

    public String getFileName() {
        return FileName;
    }

    public void setFileName(String fileName) {
        FileName = fileName;
    }

    public String getMediaLink() {
        return mediaLink;
    }

    public void setMediaLink(String mediaLink) {
        this.mediaLink = mediaLink;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getCreatedOn() {
        return CreatedOn;
    }

    public void setCreatedOn(String createdOn) {
        CreatedOn = createdOn;
    }
}
