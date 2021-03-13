package cn.wftank.qqrobot.app;


import cn.wftank.qqrobot.app.finder.SCDataFinder;
import cn.wftank.qqrobot.common.util.JsonUtil;
import org.junit.jupiter.api.Test;


public class FinderTest {

    private SCDataFinder scDataFinder = new SCDataFinder();
//    private SCDataFinder scDataFinder = new SCDataFinder(new MetricLCS());
//    private SCDataFinder scDataFinder = new SCDataFinder(new Damerau());
//    private SCDataFinder scDataFinder = new SCDataFinder(new NormalizedLevenshtein());
//    private SCDataFinder scDataFinder = new SCDataFinder(new Levenshtein());
//    private SCDataFinder scDataFinder = new SCDataFinder(new OptimalStringAlignment());

    @Test
    public void autoFind() {
//        System.out.println(JsonUtil.toPrettyJson(scDataFinder.autoFind("先锋重在哪买")));
//        System.out.println(JsonUtil.toPrettyJson(scDataFinder.autoFind("小矿车在哪买")));
//        System.out.println(JsonUtil.toPrettyJson(scDataFinder.autoFind("蔑视胸甲在哪买")));
        System.out.println(JsonUtil.toPrettyJson(scDataFinder.autoFind("fs9弹夹在哪买")));
//        System.out.println(JsonUtil.toPrettyJson(scDataFinder.autoFind("营养棒牛排在哪买")));
    }
}
