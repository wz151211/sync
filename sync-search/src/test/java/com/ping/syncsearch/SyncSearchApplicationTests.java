package com.ping.syncsearch;

import com.alibaba.fastjson.JSON;
import com.ping.syncsearch.temp.TempData;
import com.ping.syncsearch.temp.TempDataMapper;
import com.ping.syncsearch.temp.TempMapper;
import com.ping.syncsearch.temp.TempVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;

@SpringBootTest
class SyncSearchApplicationTests {


    @Autowired
    private TempMapper tempMapper;
    @Autowired
    private TempDataMapper tempDataMapper;

    @Test
    public void test1() {

        tempMapper.findCount();
    }

    @Test
    public void test12() {
        Map<String, TempData> map = new HashMap<>();
        Set<String> set = new HashSet<>();
        List<TempData> data = new ArrayList<>();
        List<TempVO> ws_2014 = tempMapper.group("ws_2014");
        List<TempVO> ws_2015 = tempMapper.group("ws_2015");
        List<TempVO> ws_2016 = tempMapper.group("ws_2016");
        List<TempVO> ws_2017 = tempMapper.group("ws_2017");
        List<TempVO> ws_2018 = tempMapper.group("ws_2018");
        List<TempVO> ws_2019 = tempMapper.group("ws_2019");
        List<TempVO> ws_2020 = tempMapper.group("ws_2020");
        List<TempVO> ws_2021 = tempMapper.group("ws_2021");
        List<TempVO> ws_2022 = tempMapper.group("ws_2022");
        List<TempVO> ws_2023 = tempMapper.group("ws_2023");

        build(ws_2014,map,data,2014);
        build(ws_2015,map,data,2015);
        build(ws_2016,map,data,2016);
        build(ws_2017,map,data,2017);
        build(ws_2018,map,data,2018);
        build(ws_2019,map,data,2019);
        build(ws_2020,map,data,2020);
        build(ws_2021,map,data,2021);
        build(ws_2022,map,data,2022);
        build(ws_2023,map,data,2023);
        for (TempData datum : data) {
            tempDataMapper.insert(datum);
            System.out.println(JSON.toJSONString(datum));
        }

    }

    private void build(List<TempVO> list, Map<String, TempData> map, List<TempData> data, int year) {
        for (TempVO tempVO : list) {
            if (map.containsKey(tempVO.getCode())) {
                TempData tempData = map.get(tempVO.getCode());
                if (year == 2014) {
                    tempData.setC2014(tempVO.getCount());
                }
                if (year == 2015) {
                    tempData.setC2015(tempVO.getCount());
                }

                if (year == 2016) {
                    tempData.setC2016(tempVO.getCount());
                }

                if (year == 2017) {
                    tempData.setC2017(tempVO.getCount());
                }

                if (year == 2018) {
                    tempData.setC2018(tempVO.getCount());
                }

                if (year == 2019) {
                    tempData.setC2019(tempVO.getCount());
                }

                if (year == 2020) {
                    tempData.setC2020(tempVO.getCount());
                }

                if (year == 2021) {
                    tempData.setC2021(tempVO.getCount());
                }

                if (year == 2022) {
                    tempData.setC2022(tempVO.getCount());
                }
                if (year == 2023) {
                    tempData.setC2023(tempVO.getCount());
                }

            } else {
                TempData tempData = new TempData();
                tempData.setName(tempVO.getName());
                tempData.setCode(tempVO.getCode());
                if (year == 2014) {
                    tempData.setC2014(tempVO.getCount());
                }
                if (year == 2015) {
                    tempData.setC2015(tempVO.getCount());
                }

                if (year == 2016) {
                    tempData.setC2016(tempVO.getCount());
                }

                if (year == 2017) {
                    tempData.setC2017(tempVO.getCount());
                }

                if (year == 2018) {
                    tempData.setC2018(tempVO.getCount());
                }

                if (year == 2019) {
                    tempData.setC2019(tempVO.getCount());
                }

                if (year == 2020) {
                    tempData.setC2020(tempVO.getCount());
                }

                if (year == 2021) {
                    tempData.setC2021(tempVO.getCount());
                }

                if (year == 2022) {
                    tempData.setC2022(tempVO.getCount());
                }
                if (year == 2023) {
                    tempData.setC2023(tempVO.getCount());
                }
                if (tempVO.getTree() != null && tempVO.getTree().size() > 0) {
                    if (tempVO.getTree().size() == 3) {
                        tempData.setProvince(tempVO.getTree().get(0));
                        tempData.setCity(tempVO.getTree().get(1));
                        tempData.setCounty(tempVO.getTree().get(2));
                    }
                    if (tempVO.getTree().size() == 2) {
                        tempData.setProvince(tempVO.getTree().get(0));
                        tempData.setCity(tempVO.getTree().get(1));
                    }
                    if (tempVO.getTree().size() == 1) {
                        tempData.setProvince(tempVO.getTree().get(0));
                    }
                }
                data.add(tempData);
                map.put(tempVO.getCode(), tempData);
            }
        }
    }

    @Test
    public void test13() {
        List<TempVO> ws_2023 = tempMapper.group("ws_2023");
        for (TempVO tempVO : ws_2023) {
            System.out.println(tempVO);
        }
    }

}
