package com.ping.syncsearch;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.ping.syncsearch.utils.CauseUtils;
import org.junit.jupiter.api.Test;

public class TestCause {

    @Test
    public void test1() {
        System.out.println(CauseUtils.getCauseList("118"));
        String url = "https://so.baobeihuijia.com/api/search/contents/actions/list";
        String params = "{\"siteId\":1,\"channelId\":1,\"page\":2,\"searchType\":2,\"searchText\":\"\",\"isAdvanced\":false,\"checkedStates\":[],\"sex\":\"9\",\"isPhotos\":false,\"isSample\":false,\"isReport\":false,\"isDna\":false,\"birthdayRange\":null,\"lostdayRange\":null,\"adddayRange\":null,\"lostAddressCode\":null,\"liveAddressCode\":null,\"lostAddress\":null,\"liveAddress\":null,\"lowerHeight\":0,\"higherHeight\":0}";

        String post = HttpUtil.post(url, JSON.parseObject(params).toJSONString());
        System.out.println(post);

    }
}
