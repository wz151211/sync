package com.ping.syncparse.service;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.ping.syncparse.entity.PartyEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class ExportEasyService {

    @Autowired
    private CaseXsMapper caseMapper;
    private int pageSize = 10000;
    private AtomicInteger pageNum = new AtomicInteger(-1);

    public void export() {
        pageNum.getAndIncrement();
        String path = "E:\\导出\\刑事案件11.xlsx";
        try {
            File file = new File(path);
            if (file.exists()) {
                file.delete();
            } else {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            try (ExcelWriter excelWriter = EasyExcel.write(path, CaseVo.class).writeExcelOnException(true).build()) {
                // 这里注意 如果同一个sheet只要创建一次
                WriteSheet caseSheet = EasyExcel.writerSheet("案件信息").build();
                WriteSheet partSheet = EasyExcel.writerSheet("当事人信息").build();
                // 去调用写入,这里我调用了五次，实际使用时根据数据库分页的总的页数来
                long total = caseMapper.getCount(null);
                long page = 0;
                page = total % pageSize > 0 ? total / pageSize + 1 : total + pageSize;
                List<PartyEntity> partys = new ArrayList<>();
                for (long index = 0; index < page; index++) {
                    System.out.println("-----------" + index + "---------------");
                    List<CaseVo> vos = caseMapper.findList((int) index, pageSize, null);
                    if (vos.size() == 0) {
                        break;
                    }

                    for (CaseVo vo : vos) {
                        vo.setHtmlContent(null);
                        vo.setJson(null);
                        vo.setJsonContent(null);
                        vo.setJudgmentResult(null);
                        partys.addAll(vo.getParty());
                    }
                    try {


                        excelWriter.write(vos, caseSheet);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        excelWriter.write(partys, partSheet);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            System.out.println("导出完成");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
