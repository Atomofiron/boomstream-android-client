
package ru.atomofiron.boomstream.models.retrofit.folder;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.NotNull;

public class Folder {

    @SerializedName("countTotal")
    @Expose
    private Integer countTotal;
    @SerializedName("Folders")
    @Expose
    private List<Subfolder> folders = new ArrayList<>();
    @SerializedName("Medias")
    @Expose
    private List<Media> medias = new ArrayList<>();
    @SerializedName("Status")
    @Expose
    private String status;

    public Integer getCountTotal() {
        return countTotal;
    }

    public void setCountTotal(Integer countTotal) {
        this.countTotal = countTotal;
    }

    @NotNull
    public List<Subfolder> getFolders() {
        return folders == null ? new ArrayList<Subfolder>() : folders;
    }

    public void setFolders(List<Subfolder> folders) {
        this.folders = folders;
    }

    @NotNull
    public List<Media> getMedias() {
        return medias == null ? new ArrayList<Media>() : medias;
    }

    public void setMedias(List<Media> medias) {
        this.medias = medias;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
