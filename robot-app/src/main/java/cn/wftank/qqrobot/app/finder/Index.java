package cn.wftank.qqrobot.app.finder;


import java.util.LinkedList;
import java.util.List;

public class Index {

    private String version;
    private List<IndexEntity> index = new LinkedList<>();

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public List<IndexEntity> getIndex() {
        return index;
    }

    public void setIndex(List<IndexEntity> index) {
        this.index = index;
    }
}
