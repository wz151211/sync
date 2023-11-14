package com.ping.syncparse.service.security;

import cn.hutool.core.date.DateUtil;
import com.ping.syncparse.utils.ExcelUtils;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @Author: W.Z
 * @Date: 2023/10/14 15:38
 */
@Service
public class SecurityExportService {

    @Autowired
    private SecurityResultMapper resultMapper;

    private int pageSize = 35000;
    private AtomicInteger pageNum = new AtomicInteger(-1);

    public void export() {
        pageNum.getAndIncrement();
        List<SecurityResultVo> vos = resultMapper.findList(pageNum.get(), pageSize, null);
        Workbook wb = new XSSFWorkbook();
        String[] head = {"案件信息", "序号", "案件名称", "案号", "法院名称", "裁判日期", "案由", "案件类型", "审判程序", "文书类型", "省份", "地市", "区县",
                "事实/审理查明", "判决结果", "理由", "法律依据", "诉讼记录", "HTML内容", "JSON内容",
                "行政处罚决定书及编号", "虚假陈述揭露日", "虚假陈述揭露日内容", "买入均价计算方法", "买入均价计算方法内容", "投资者投资差额损失", "系统性风险", "核定机构名称", "核定机构名内容", "核定文书名称", "核定文书名称内容","全文文本"};
        Sheet sheet = wb.createSheet("案件信息");
        List<Map<Integer, Object>> list = vos.parallelStream().map(this::toMap).collect(Collectors.toList());
        FileOutputStream out = null;
        try {
            File file = new File("/Users/monkey/Desktop/导出/证券虚假陈述责任纠纷-" + (pageNum.get() + 1) + ".xlsx");
            if (file.exists()) {
                file.delete();
            } else {
                file.createNewFile();
            }
            out = new FileOutputStream(file);
            ExcelUtils.export(wb, sheet, list, head);
            wb.write(out);
            System.out.println("导出完成");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                wb.close();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


    private Map<Integer, Object> toMap(SecurityResultVo vo) {
        Map<Integer, Object> map = new HashMap<>();
        map.put(1, vo.getName());
        map.put(2, vo.getCaseNo());
        map.put(3, vo.getCourtName());
        if (vo.getRefereeDate() != null) {
            map.put(4, DateUtil.format(vo.getRefereeDate(), DateTimeFormatter.ISO_LOCAL_DATE));
        } else {
            map.put(4, "");
        }
        map.put(5, vo.getCause());
        map.put(6, vo.getCaseType());
        map.put(7, vo.getTrialProceedings());
        map.put(8, vo.getDocType());
        map.put(9, vo.getProvince());
        map.put(10, vo.getCity());
        map.put(11, vo.getCounty());
        map.put(12, vo.getFact());
        map.put(13, vo.getJudgmentResult());
        map.put(14, vo.getCourtConsidered());
        map.put(15, vo.getLegalBasis());
        map.put(16, vo.getLitigationRecords());
        map.put(17, vo.getHtmlContent());
        if (vo.getJsonContent() != null) {
            map.put(18, vo.getJsonContent());
        } else {
            map.put(18, "");
        }
        if (vo.getPenalty().size() > 0) {
            StringBuilder text = new StringBuilder();
            for (int i = 0; i < vo.getPenalty().size(); i++) {
                text.append(i + 1).append("、").append(vo.getPenalty().get(i)).append("\r\n");
            }
            map.put(19, text.toString());

        } else {
            map.put(19, "");
        }

        map.put(20, vo.getDisclosureDay());
        map.put(21, vo.getDisclosureDayContent());
        map.put(22, vo.getAveragePrice());
        map.put(23, vo.getAveragePriceContent());
        map.put(24, vo.getDifferenceLoss());
        if (vo.getRisk().size() > 0) {
            StringBuilder text = new StringBuilder();
            for (int i = 0; i < vo.getRisk().size(); i++) {
                text.append(i + 1).append("、").append(vo.getRisk().get(i)).append("\r\n");
            }
            map.put(25, text.toString());
        } else {
            map.put(25, "");
        }

        map.put(26, vo.getApprovedBy());
        map.put(27, vo.getApprovedByContent());
        map.put(28, vo.getApprovedDoc());
        map.put(29, vo.getApprovedDocContent());
        map.put(30, vo.getText());
        return map;

    }
}
