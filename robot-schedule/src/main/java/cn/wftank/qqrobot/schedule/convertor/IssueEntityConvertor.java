package cn.wftank.qqrobot.schedule.convertor;

import cn.wftank.qqrobot.common.model.event.IssueEntity;
import cn.wftank.qqrobot.schedule.model.vo.response.issue.ResultsetItem;
import org.springframework.beans.BeanUtils;

public class IssueEntityConvertor {

    //https://robertsspaceindustries.com/community/issue-council/star-citizen-alpha-3/STARC-142589-CRU-L1_invisible_wall_left_lift_on_hanger_andamp__habs_level
    public static final String PREFIX = "https://robertsspaceindustries.com/community/issue-council/";

    public static IssueEntity convert(ResultsetItem resultsetItem){
        IssueEntity issueEntity = new IssueEntity();
        BeanUtils.copyProperties(resultsetItem, issueEntity);
        issueEntity.setUrl(buildUrl(resultsetItem));
        issueEntity.setCreateTime(resultsetItem.getTimeCreated());
        return issueEntity;
    }

    public static String buildUrl(ResultsetItem resultsetItem){
        return PREFIX+resultsetItem.getModule().getUrl()
                +"/"+resultsetItem.getModule().getKey()
                +"-"+resultsetItem.getId()
                +"-"+resultsetItem.getTitle().replaceAll(" ","_");
    }

}
