package cn.wftank.qqrobot.app;


import cn.wftank.qqrobot.app.finder.SCDataFinder;
import cn.wftank.qqrobot.common.util.JsonUtil;
import org.junit.jupiter.api.Test;


public class FinderTest {

    private SCDataFinder scDataFinder = new SCDataFinder();


    @Test
    public void autoFind() {
        System.out.println(JsonUtil.toPrettyJson(scDataFinder.autoFind("先锋重哪买")));
    }
}
