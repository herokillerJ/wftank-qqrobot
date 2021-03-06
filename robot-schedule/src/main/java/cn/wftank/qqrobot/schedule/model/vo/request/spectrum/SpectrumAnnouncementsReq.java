package cn.wftank.qqrobot.schedule.model.vo.request.spectrum;


public class SpectrumAnnouncementsReq {

    private Integer channelId;
    private Integer page;
    private String sort;
    private Integer labelId;

    public Integer getChannelId() {
        return channelId;
    }

    public void setChannelId(Integer channelId) {
        this.channelId = channelId;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public Integer getLabelId() {
        return labelId;
    }

    public void setLabelId(Integer labelId) {
        this.labelId = labelId;
    }

    @Override
    public String toString() {
        return "SpectrumAnnouncementsReq{" +
                "channelId=" + channelId +
                ", page=" + page +
                ", sort='" + sort + '\'' +
                ", labelId=" + labelId +
                '}';
    }
}
