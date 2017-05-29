
package ru.atomofiron.boomstream.models.retrofit.folder;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import ru.atomofiron.boomstream.models.Node;

public class Subfolder extends Node {

    @SerializedName("code")
    @Expose
    private String code;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("fileCount")
    @Expose
    private String fileCount;
    @SerializedName("fileSize")
    @Expose
    private String fileSize;
    @SerializedName("added")
    @Expose
    private String added;

    private Folder that;

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

    public String getFileCount() {
        return fileCount;
    }

    public void setFileCount(String fileCount) {
        this.fileCount = fileCount;
    }

    public String getFileSize() {
        return fileSize;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }

    public String getAdded() {
        return added;
    }

    public void setAdded(String added) {
        this.added = added;
    }

    @Override
    public boolean contains(@NotNull String text) {
        return title.toLowerCase().contains(text.toLowerCase());
    }

    @Override
    public boolean equals(Object obj) {
        return !(obj == null || !getClass().equals(obj.getClass())) && getCode().equals(((Subfolder) obj).getCode());
    }
}
