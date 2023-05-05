package com.ping.syncparse.service;

import cn.hutool.core.date.DateUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.handler.CellWriteHandler;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.ping.syncparse.entity.PartyEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Service
public class ExportEasyService {

    @Autowired
    private CaseXsMapper caseMapper;
    private int pageSize = 10000;

    public void export() {
        String path = "E:\\导出\\刑事案件11.xlsx";
        try {
            File file = new File(path);
            if (file.exists()) {
                file.delete();
            } else {
                file.createNewFile();
            }

            try (ExcelWriter excelWriter = EasyExcel.write(path, CaseVo.class).writeExcelOnException(true).build()) {
                // 这里注意 如果同一个sheet只要创建一次
                WriteSheet caseSheet = EasyExcel.writerSheet("案件信息").build();
                WriteSheet partSheet = EasyExcel.writerSheet("当事人信息").build();
                // 去调用写入,这里我调用了五次，实际使用时根据数据库分页的总的页数来
                Criteria criteria = Criteria.where("refereeDate").gte(DateUtil.parse("2022-01-01 00:00:00").toJdkDate()).lte(DateUtil.parse("2022-12-31 23:59:59").toJdkDate());
                long total = caseMapper.getCount(criteria);
                long page = 0;
                page = total % pageSize > 0 ? total / pageSize + 1 : total + pageSize;
                List<PartyEntity> partys = new ArrayList<>();
                for (long index = 0; index < page; index++) {
                    System.out.println("-----------" + index + "---------------");
                    List<CaseVo> vos = caseMapper.findList((int) index, pageSize, criteria);
                    if (vos.size() == 0) {
                        break;
                    }
                    try {
                        excelWriter.write(vos, caseSheet);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
             /*       for (CaseVo vo : vos) {
                        partys.addAll(vo.getParty());
                    }

                    try {
                        excelWriter.write(partys, partSheet);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }*/
                }
            }
            System.out.println("导出完成");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
