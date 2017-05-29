
package ru.atomofiron.boomstream.models.retrofit.folder;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Transcode {

    @SerializedName("Code")
    @Expose
    private String code;
    @SerializedName("Title")
    @Expose
    private String title;
    @SerializedName("Width")
    @Expose
    private String width;
    @SerializedName("Height")
    @Expose
    private String height;
    @SerializedName("PseudoFLV")
    @Expose
    private String pseudoFLV;
    @SerializedName("PseudoMP4")
    @Expose
    private String pseudoMP4;
    @SerializedName("AdobeHDS")
    @Expose
    private String adobeHDS;
    @SerializedName("AppleHLS")
    @Expose
    private String appleHLS;
    @SerializedName("MicrosoftSmooth")
    @Expose
    private String microsoftSmooth;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getPseudoFLV() {
        return pseudoFLV;
    }

    public void setPseudoFLV(String pseudoFLV) {
        this.pseudoFLV = pseudoFLV;
    }

    public String getPseudoMP4() {
        return pseudoMP4;
    }

    public void setPseudoMP4(String pseudoMP4) {
        this.pseudoMP4 = pseudoMP4;
    }

    public String getAdobeHDS() {
        return adobeHDS;
    }

    public void setAdobeHDS(String adobeHDS) {
        this.adobeHDS = adobeHDS;
    }

    public String getAppleHLS() {
        return appleHLS;
    }

    public void setAppleHLS(String appleHLS) {
        this.appleHLS = appleHLS;
    }

    public String getMicrosoftSmooth() {
        return microsoftSmooth;
    }

    public void setMicrosoftSmooth(String microsoftSmooth) {
        this.microsoftSmooth = microsoftSmooth;
    }

}
