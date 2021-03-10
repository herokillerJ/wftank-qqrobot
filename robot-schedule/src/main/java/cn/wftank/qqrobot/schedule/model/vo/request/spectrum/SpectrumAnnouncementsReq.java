package cn.wftank.qqrobot.schedule.model.vo.request.spectrum;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class SpectrumAnnouncementsReq {

    private Integer channelId;
    private Integer page;
    private String sort;
    private Integer labelId;

}
