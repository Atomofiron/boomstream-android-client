
package ru.atomofiron.boomstream.models.retrofit.folder;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.jetbrains.annotations.NotNull;

import ru.atomofiron.boomstream.models.Node;

public class Media extends Node {

    @SerializedName("Title")
    @Expose
    private String title;
    @SerializedName("Code")
    @Expose
    private String code;
    @SerializedName("Type")
    @Expose
    private String type;
    @SerializedName("Width")
    @Expose
    private String width;
    @SerializedName("Height")
    @Expose
    private String height;
    @SerializedName("MediaStatus")
    @Expose
    private String mediaStatus;
    @SerializedName("Duration")
    @Expose
    private String duration;
    @SerializedName("PlayerCode")
    @Expose
    private String playerCode;
    @SerializedName("DownloadLink")
    @Expose
    private String downloadLink;
    @SerializedName("Poster")
    @Expose
    private Poster poster;
    @SerializedName("Transcodes")
    @Expose
    private List<Transcode> transcodes = null;
    @SerializedName("Adaptive")
    @Expose
    private Adaptive adaptive;
    @SerializedName("Screenshots")
    @Expose
    private List<Screenshot> screenshots = null;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public String getMediaStatus() {
        return mediaStatus;
    }

    public void setMediaStatus(String mediaStatus) {
        this.mediaStatus = mediaStatus;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getPlayerCode() {
        return playerCode;
    }

    public void setPlayerCode(String playerCode) {
        this.playerCode = playerCode;
    }

    public String getDownloadLink() {
        return downloadLink;
    }

    public void setDownloadLink(String downloadLink) {
        this.downloadLink = downloadLink;
    }

    public Poster getPoster() {
        return poster;
    }

    public void setPoster(Poster poster) {
        this.poster = poster;
    }

    @NotNull
    public List<Transcode> getTranscodes() {
        return transcodes == null ? new ArrayList<Transcode>() : transcodes;
    }

    public void setTranscodes(List<Transcode> transcodes) {
        this.transcodes = transcodes;
    }

    public Adaptive getAdaptive() {
        return adaptive;
    }

    public void setAdaptive(Adaptive adaptive) {
        this.adaptive = adaptive;
    }

    public List<Screenshot> getScreenshots() {
        return screenshots;
    }

    public void setScreenshots(List<Screenshot> screenshots) {
        this.screenshots = screenshots;
    }

    @Override
    public boolean contains(@NotNull String text) {
        return title.toLowerCase().contains(text.toLowerCase());
    }

    @Override
    public boolean equals(Object obj) {
        return !(obj == null || !getClass().equals(obj.getClass())) && code.equals(((Media) obj).code);
    }
}
