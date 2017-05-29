
package ru.atomofiron.boomstream.models.retrofit.folder;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Adaptive {

    @SerializedName("AppleHLS")
    @Expose
    private String appleHLS;

    public String getAppleHLS() {
        return appleHLS;
    }

    public void setAppleHLS(String appleHLS) {
        this.appleHLS = appleHLS;
    }

}
