package com.loptr.kherod.uygdl.model;

import androidx.annotation.Keep;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

@Keep
public class Admob implements Serializable {

    @SerializedName("app_id")
    @Expose
    private String app_id = "";

    @SerializedName("banner")
    @Expose
    private String banner = "";

    @SerializedName("interstitial")
    @Expose
    private String interstitial = "";

    @SerializedName("interstitial2")
    @Expose
    private String interstitial2 = "";

    @SerializedName("ima_ad")
    @Expose
    private String imaAd = "";

    @SerializedName("native_ad")
    @Expose
    private String nativeAd = "";

    public Admob() {
    }

    public Admob(String app_id, String banner, String interstitial, String interstitial2, String imaAd, String nativeAd) {
        this.app_id = app_id;
        this.banner = banner;
        this.interstitial = interstitial;
        this.interstitial2 = interstitial2;
        this.imaAd = imaAd;
        this.nativeAd = nativeAd;
    }

    public String getApp_id() {
        return app_id;
    }

    public void setApp_id(String app_id) {
        this.app_id = app_id;
    }

    public String getBanner() {
        return banner;
    }

    public void setBanner(String banner) {
        this.banner = banner;
    }

    public String getInterstitial() {
        return interstitial;
    }

    public void setInterstitial(String interstitial) {
        this.interstitial = interstitial;
    }

    public String getInterstitial2() {
        return interstitial2;
    }

    public void setInterstitial2(String interstitial2) {
        this.interstitial2 = interstitial2;
    }

    public String getImaAd() {
        return imaAd;
    }

    public void setImaAd(String imaAd) {
        this.imaAd = imaAd;
    }

    public String getNativeAd() {
        return nativeAd;
    }

    public void setNativeAd(String nativeAd) {
        this.nativeAd = nativeAd;
    }
}
