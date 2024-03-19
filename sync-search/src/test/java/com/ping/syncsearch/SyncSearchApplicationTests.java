package com.ping.syncsearch;

import com.alibaba.fastjson.JSON;
import com.ping.syncsearch.entity.AreaEntity;
import com.ping.syncsearch.service.AreaService;
import com.ping.syncsearch.temp.TempData;
import com.ping.syncsearch.temp.TempDataMapper;
import com.ping.syncsearch.temp.TempMapper;
import com.ping.syncsearch.temp.TempVO;
import org.ansj.domain.Term;
import org.ansj.library.DicLibrary;
import org.ansj.splitWord.analysis.ToAnalysis;
import org.ansj.util.MyStaticValue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StringUtils;

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
        MyStaticValue.ENV.put(DicLibrary.DEFAULT, "library/default.dic");
        Map<String, TempData> map = new HashMap<>();
        Set<String> set = new HashSet<>();
        List<TempData> data = new ArrayList<>();
        String type = "002";
        List<TempVO> ws_2010 = tempMapper.find("2010", type);
        List<TempVO> ws_2011 = tempMapper.find("2011", type);
        List<TempVO> ws_2012 = tempMapper.find("2012", type);
        List<TempVO> ws_2013 = tempMapper.find("2013", type);
        List<TempVO> ws_2014 = tempMapper.find("2014", type);
        List<TempVO> ws_2015 = tempMapper.find("2015", type);
        List<TempVO> ws_2016 = tempMapper.find("2016", type);
        List<TempVO> ws_2017 = tempMapper.find("2017", type);
        List<TempVO> ws_2018 = tempMapper.find("2018", type);
        List<TempVO> ws_2019 = tempMapper.find("2019", type);
        List<TempVO> ws_2020 = tempMapper.find("2020", type);
        List<TempVO> ws_2021 = tempMapper.find("2021", type);
        List<TempVO> ws_2022 = tempMapper.find("2022", type);
        List<TempVO> ws_2023 = tempMapper.find("2023", type);
        List<TempVO> ws_2024 = tempMapper.find("2024", type);

        build(ws_2010, map, data, 2010);
        build(ws_2011, map, data, 2011);
        build(ws_2012, map, data, 2012);
        build(ws_2013, map, data, 2013);
        build(ws_2014, map, data, 2014);
        build(ws_2015, map, data, 2015);
        build(ws_2016, map, data, 2016);
        build(ws_2017, map, data, 2017);
        build(ws_2018, map, data, 2018);
        build(ws_2019, map, data, 2019);
        build(ws_2020, map, data, 2020);
        build(ws_2021, map, data, 2021);
        build(ws_2022, map, data, 2022);
        build(ws_2023, map, data, 2023);
        build(ws_2024, map, data, 2024);
        for (TempData datum : data) {
            String name = datum.getName();
            if (StringUtils.hasLength(name)) {
                if (name.contains("（") && name.contains("）")) {
                    int start = name.indexOf("（");
                    int end = name.indexOf("）");
                    name = name.substring(0, start) + name.substring(end + 1);
                    datum.setName(name);
                }
            }
            parseAddress(datum);
            tempDataMapper.insert(datum);
            System.out.println(JSON.toJSONString(datum));
        }

    }

    private void build(List<TempVO> list, Map<String, TempData> map, List<TempData> data, int year) {
        for (TempVO tempVO : list) {
            tempVO.setCount(tempVO.getValue());
            if (map.containsKey(tempVO.getId())) {
                TempData tempData = map.get(tempVO.getId());
                if (year == 2010) {
                    tempData.setC2010(tempVO.getCount());
                }
                if (year == 2011) {
                    if (tempData == null || tempVO == null) {
                        System.out.println("");
                    }
                    tempData.setC2011(tempVO.getCount());
                }
                if (year == 2012) {
                    tempData.setC2012(tempVO.getCount());
                }
                if (year == 2013) {
                    tempData.setC2013(tempVO.getCount());
                }
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
                if (year == 2024) {
                    tempData.setC2024(tempVO.getCount());
                }

            } else {
                TempData tempData = new TempData();
                tempData.setName(tempVO.getName());
                tempData.setCode(tempVO.getCode());
                tempData.setId(tempVO.getId());
                tempData.setYear(tempVO.getYear());
                tempData.setValue(tempVO.getValue());
                tempData.setType(tempVO.getType());
                if (year == 2010) {
                    tempData.setC2010(tempVO.getCount());
                }
                if (year == 2011) {
                    tempData.setC2011(tempVO.getCount());
                }
                if (year == 2012) {
                    tempData.setC2012(tempVO.getCount());
                }
                if (year == 2013) {
                    tempData.setC2013(tempVO.getCount());
                }
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
                if (year == 2024) {
                    tempData.setC2024(tempVO.getCount());
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
                map.put(tempVO.getId(), tempData);
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

    @Test
    public void test14() {
        MyStaticValue.ENV.put(DicLibrary.DEFAULT, "library/default.dic");
        List<TempData> data = tempDataMapper.findAll();
        for (TempData datum : data) {
            boolean flag = false;
            if (StringUtils.isEmpty(datum.getProvince())) {
                flag = true;
            }

            parseAddress(datum);
            if (flag) {
                System.out.println(JSON.toJSONString(datum));
            }
            tempDataMapper.update(datum);
        }
    }

    @Autowired
    private AreaService areaService;

    private void parseAddress(TempData party) {
        if (party == null || !StringUtils.hasText(party.getName())) {
            return;
        }
        String address = party.getName();

        for (Term term : ToAnalysis.parse(address)) {
            if (term.getRealName().contains("省")) {
                party.setProvince(term.getRealName());
            }
            if (term.getRealName().contains("市") || term.getRealName().contains("盟") || term.getRealName().contains("自治州")) {
                if (term.getRealName().length() == 1) {
                    party.setCity(term.from().getRealName() + term.getRealName());
                } else {
                    party.setCity(term.getRealName());
                }
            }

            if (term.getRealName().contains("县") || term.getRealName().contains("旗")) {
                if (term.getRealName().length() == 1) {
                    party.setCounty(term.from().getRealName() + term.getRealName());

                } else {
                    party.setCounty(term.getRealName());

                }
            }
            if (term.getRealName().contains("区")) {

                String name = "";
                if (term.getRealName().length() == 1) {
                    name = term.from().getRealName() + term.getRealName();
                } else {
                    name = term.getRealName();
                }
                for (String s : provinceList) {
                    if (s.equals(name)) {
                        party.setProvince(name);
                    } else {
                        party.setCounty(name);
                    }
                }

            }
            AreaEntity entity = areaService.find(party.getCity(), party.getCounty());
            if (entity != null) {


                if (StringUtils.isEmpty(party.getProvince())) {
                    party.setProvince(entity.getProvince());
                }
                if (StringUtils.isEmpty(party.getCity())) {
                    party.setCity(entity.getCity());
                }
                if (StringUtils.isEmpty(party.getCounty())) {
                    party.setCounty(entity.getCounty());
                }
            }

        }


    }

    private List<String> provinceList = new ArrayList<>();

    {
        provinceList.add("北京市");
        provinceList.add("天津市");
        provinceList.add("河北省");
        provinceList.add("山西省");
        provinceList.add("内蒙古自治区");
        provinceList.add("辽宁省");
        provinceList.add("吉林省");
        provinceList.add("黑龙江省");
        provinceList.add("上海市");
        provinceList.add("江苏省");
        provinceList.add("浙江省");
        provinceList.add("安徽省");
        provinceList.add("福建省");
        provinceList.add("江西省");
        provinceList.add("山东省");
        provinceList.add("河南省");
        provinceList.add("湖北省");
        provinceList.add("湖南省");
        provinceList.add("广东省");
        provinceList.add("广西壮族自治区");
        provinceList.add("海南省");
        provinceList.add("重庆市");
        provinceList.add("四川省");
        provinceList.add("贵州省");
        provinceList.add("云南省");
        provinceList.add("西藏自治区");
        provinceList.add("陕西省");
        provinceList.add("甘肃省");
        provinceList.add("青海省");
        provinceList.add("宁夏回族自治区");
    }
}
