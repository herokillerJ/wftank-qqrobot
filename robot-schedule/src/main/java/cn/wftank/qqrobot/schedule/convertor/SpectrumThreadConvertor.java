package cn.wftank.qqrobot.schedule.convertor;

import cn.wftank.qqrobot.common.model.event.SpectrumThread;
import cn.wftank.qqrobot.schedule.model.vo.response.spectrum.ThreadsItem;
import org.springframework.beans.BeanUtils;

public class SpectrumThreadConvertor {

    public static final String PREFIX = "https://robertsspaceindustries.com/spectrum/community/SC/forum/";

    public static SpectrumThread convert(ThreadsItem threadsItem){
        SpectrumThread spectrumThread = new SpectrumThread();
        BeanUtils.copyProperties(threadsItem, spectrumThread);
        spectrumThread.setUrl(buildUrl(threadsItem));
        return spectrumThread;
    }

    public static String buildUrl(ThreadsItem threadsItem){
        return PREFIX+threadsItem.getChannelId()+"/thread/"+threadsItem.getSlug();
    }

}
