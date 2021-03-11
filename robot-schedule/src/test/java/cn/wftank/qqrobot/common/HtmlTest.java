package cn.wftank.qqrobot.common;


import cn.wftank.qqrobot.common.util.JsonUtil;
import cn.wftank.qqrobot.common.util.OKHttpUtil;
import cn.wftank.qqrobot.schedule.model.vo.request.spectrum.SpectrumAnnouncementsReq;
import cn.wftank.qqrobot.schedule.model.vo.response.spectrum.SpectrumResp;
import cn.wftank.qqrobot.schedule.model.vo.response.spectrum.ThreadsItem;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.Test;

/**
 * Unit test for simple App.
 */
public class HtmlTest
{
    /**
     * Rigorous Test :-)
     */
    @Test
    public void test(){
        SpectrumAnnouncementsReq req = new SpectrumAnnouncementsReq();
        req.setChannelId(1);
        req.setPage(1);
        req.setSort("newest");
        SpectrumResp<ThreadsItem> resp = OKHttpUtil.postJson("https://robertsspaceindustries.com/api/spectrum/forum/channel/threads"
                , req, new TypeReference<SpectrumResp<ThreadsItem>>() {});
        System.out.println(JsonUtil.toJson(resp));
    }

    @Test
    public void tiebaTest(){
        SpectrumAnnouncementsReq req = new SpectrumAnnouncementsReq();
        req.setChannelId(1);
        req.setPage(1);
        req.setSort("newest");
        SpectrumResp<ThreadsItem> resp = OKHttpUtil.postJson("https://robertsspaceindustries.com/api/spectrum/forum/channel/threads"
                , req, new TypeReference<SpectrumResp<ThreadsItem>>() {});
        System.out.println(JsonUtil.toJson(resp));
    }
}
