package net.lightbody.bmp.core.har.copy;

public class HarNameVersion {
    private String name;
    private String version;
    private volatile String comment = "";

    public HarNameVersion(String name, String version) {
        this.name = name;
        this.version = version;
    }

    public HarNameVersion() {
    }
    
    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
