package shop.ichinomiya221156.wings.android.miniumquiclstart;

public class FileModel {
    String fileName;
    String id;
    Boolean isFavorite;

    public FileModel(String fileName, String id, Boolean isFavorite) {
        this.fileName = fileName;
        this.id = id;
        this.isFavorite = isFavorite;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getid() {
        return id;
    }

    public void setid(String id) {
        this.id = id;
    }

    public Boolean getFavorite() {
        return isFavorite;
    }

    public void setFavorite(Boolean favorite) {
        isFavorite = favorite;
    }
}
