package com.arteriatech.ss.msecsales.rspl.mbo;

import java.io.InputStream;

public class MerchandisingBean {

    private String ImagePath = "";
    private String MerchReviewGUID = "";
    private String MerchReviewType = "";
    private String MerchReviewTypeDesc = "";
    private String ImageSize = "";
    private String MerchReviewDate = "";
    private String Remarks = "";
    private String Etag = "";
    private InputStream imgInputStream;
    private String mediaLink = "";
    private String DocumentStore = "";
    private String MerchReviewImgGUID = "";
    private String Image = "";
    private String ResourcePath = "";


    public String getImagePath() {
        return ImagePath;
    }

    public void setImagePath(String imagePath) {
        ImagePath = imagePath;
    }

    public InputStream getImgInputStream() {
        return imgInputStream;
    }

    public void setImgInputStream(InputStream imgInputStream) {
        this.imgInputStream = imgInputStream;
    }

    public String getMediaLink() {
        return mediaLink;
    }

    public void setMediaLink(String mediaLink) {
        this.mediaLink = mediaLink;
    }

    public String getDocumentStore() {
        return DocumentStore;
    }

    public void setDocumentStore(String documentStore) {
        DocumentStore = documentStore;
    }

    public String getMerchReviewImgGUID() {
        return MerchReviewImgGUID;
    }

    public void setMerchReviewImgGUID(String merchReviewImgGUID) {
        MerchReviewImgGUID = merchReviewImgGUID;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getResourcePath() {
        return ResourcePath;
    }

    public void setResourcePath(String resourcePath) {
        ResourcePath = resourcePath;
    }

    public String getEtag() {
        return Etag;
    }

    public void setEtag(String etag) {
        Etag = etag;
    }


    public String getRemarks() {
        return Remarks;
    }

    public void setRemarks(String remarks) {
        Remarks = remarks;
    }


    public String getMerchReviewDate() {
        return MerchReviewDate;
    }

    public void setMerchReviewDate(String merchReviewDate) {
        MerchReviewDate = merchReviewDate;
    }

    public String getMerchReviewGUID() {
        return MerchReviewGUID;
    }

    public void setMerchReviewGUID(String merchReviewGUID) {
        MerchReviewGUID = merchReviewGUID;
    }

    public String getMerchReviewType() {
        return MerchReviewType;
    }

    public void setMerchReviewType(String merchReviewType) {
        MerchReviewType = merchReviewType;
    }

    public String getMerchReviewTypeDesc() {
        return MerchReviewTypeDesc;
    }

    public void setMerchReviewTypeDesc(String merchReviewTypeDesc) {
        MerchReviewTypeDesc = merchReviewTypeDesc;
    }

    public String getImageSize() {
        return ImageSize;
    }

    public void setImageSize(String imageSize) {
        ImageSize = imageSize;
    }


}
