package com.ping.syncparse.service;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.ping.syncparse.entity.PartyEntity;
import com.ping.syncparse.utils.ExcelUtils;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class ExportMsService {

    @Autowired
    private CaseMsMapper caseMapper;
    private int pageSize = 1000;
    private AtomicInteger pageNum = new AtomicInteger(-1);

    public void export() {
        pageNum.getAndIncrement();
        Criteria criteria = Criteria.where("name").regex("判决书");

        List<CaseMsVo> vos = caseMapper.findList(pageNum.get(), pageSize, criteria);
        Workbook wb = new XSSFWorkbook();
        String[] head = {"案件信息", "序号", "id", "案件名称", "案号", "法院名称", "裁判日期", "案由", "审判程序"
                , "省份", "当事人", "结婚日期", "结婚日期内容", "是否有孩子", "是否有孩子内容", "是否再婚", "是否再婚内容", "是否同意离婚", "是否同意离婚内容", "HTML内容", "JSON内容"};
        Sheet sheet = wb.createSheet("案件信息");
        List<Map<Integer, Object>> list = vos.parallelStream().map(this::toMap).collect(Collectors.toList());
        FileOutputStream out = null;
        try {
            File file = new File("E:\\导出\\民事案件.xlsx");
            if (file.exists()) {
                file.delete();
            } else {
                file.createNewFile();
            }
            out = new FileOutputStream(file);
            ExcelUtils.export(wb, sheet, list, head);
            List<Map<Integer, Object>> partyList = new ArrayList<>();
            List<String> partyHead = new ArrayList<>();
            partyHead.add("当事人信息");
            partyHead.add("序号");
            partyHead.add("案号");
            List<String> temp = new ArrayList<>();
            temp.add("类型");
            temp.add("姓名");
            temp.add("性别");
            temp.add("年龄");
            temp.add("出生日期");
            temp.add("民族");
            temp.add("地址");
            temp.add("文化水平");
            temp.add("内容");
            partyHead.addAll(temp);
            partyHead.addAll(temp);
            partyHead.addAll(temp);
            partyHead.addAll(temp);
            partyHead.addAll(temp);
            partyHead.addAll(temp);
            partyHead.addAll(temp);
            partyHead.addAll(temp);
            partyHead.addAll(temp);
            partyHead.addAll(temp);
            for (CaseMsVo vo : vos) {
                List<PartyEntity> party = vo.getParty();
                if (party != null) {
                    Map<Integer, Object> partyMap = new HashMap<>();
                    partyMap.put(1, vo.getCaseNo());
                    partyList.add(partyMap);
                    int start = 0;
                    for (int i = 0; i < party.size(); i++) {
                        if (i >= 10) {
                            break;
                        }
                        PartyEntity entity = party.get(i);
                        toParty(start, partyMap, entity);
                        start += 9;
                    }
                } else {
                    partyList.add(new HashMap<>());
                }
            }
            Sheet partySheet = wb.createSheet("当事人信息");
            ExcelUtils.export(wb, partySheet, partyList, partyHead.toArray());

            wb.write(out);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                wb.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private Map<Integer, Object> toCrime(int start, Map<Integer, Object> map, CrimeVO vo) {
        map.put(start + 2, vo.getName());
        map.put(start + 3, vo.getCrime());
        map.put(start + 4, vo.getImprisonmentTerm());
        return map;

    }


    private Map<Integer, Object> toParty(int start, Map<Integer, Object> map, PartyEntity party) {
        map.put(start + 2, party.getType());
        map.put(start + 3, party.getName());
        map.put(start + 4, party.getSex());
        map.put(start + 5, party.getAge());
        map.put(start + 6, party.getBirthday());
        map.put(start + 7, party.getNation());
        map.put(start + 8, party.getAddress());
        map.put(start + 9, party.getEduLevel());
        map.put(start + 10, party.getContent());
        return map;

    }

    private Map<Integer, Object> toMap(CaseMsVo vo) {
        Map<Integer, Object> map = new HashMap<>();
        map.put(1, vo.getId());
        map.put(2, vo.getName());
        map.put(3, vo.getCaseNo());
        map.put(4, vo.getCourtName());
        if (vo.getRefereeDate() != null) {
            map.put(5, DateUtil.format(vo.getRefereeDate(), DateTimeFormatter.ISO_LOCAL_DATE));
        } else {
            map.put(5, "");
        }
        map.put(6, vo.getCause());
        map.put(7, vo.getTrialProceedings());
        map.put(8, vo.getProvince());
        if (vo.getParty() != null) {
            map.put(9, JSON.toJSONString(vo.getParty()));
        } else {
            map.put(9, "");
        }
        map.put(10, vo.getMarriagDate());
        map.put(11, vo.getMarriagContent());
        map.put(12, vo.getHaveChildren());
        map.put(13, vo.getChildrenContent());
        map.put(14, vo.getRemarry());
        map.put(15, vo.getRemarryContent());
        map.put(16, vo.getJudgmenResult());
        map.put(17, vo.getJudgmenResultContent());
        map.put(18, vo.getHtmlContent());
        if (vo.getJsonContent() != null) {
            map.put(19, vo.getJsonContent().toJSONString());
        } else {
            map.put(19, "");
        }


        return map;

    }

}
