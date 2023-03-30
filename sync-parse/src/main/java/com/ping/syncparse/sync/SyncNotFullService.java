package com.ping.syncparse.sync;

import com.ping.syncparse.entity.*;
import com.ping.syncparse.mapper.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
public class SyncNotFullService {
    @Autowired
    private Document2014Mapper document2014Mapper;
    @Autowired
    private Document2015Mapper document2015Mapper;
    @Autowired
    private Document2016Mapper document2016Mapper;
    @Autowired
    private Document2017Mapper document2017Mapper;
    @Autowired
    private Document2018Mapper document2018Mapper;
    @Autowired
    private Document2019Mapper document2019Mapper;
    @Autowired
    private Document2020Mapper document2020Mapper;
    @Autowired
    private Document2021Mapper document2021Mapper;
    @Autowired
    private Document2022Mapper document2022Mapper;
    @Autowired
    private Document2023Mapper document2023Mapper;
    @Autowired
    private DocumentOtherMapper otherMapper;

    private AtomicInteger pageNum2014 = new AtomicInteger(-1);
    private AtomicInteger pageNum2015 = new AtomicInteger(-1);
    private AtomicInteger pageNum2016 = new AtomicInteger(-1);
    private AtomicInteger pageNum2017 = new AtomicInteger(-1);
    private AtomicInteger pageNum2018 = new AtomicInteger(-1);
    private AtomicInteger pageNum2019 = new AtomicInteger(-1);
    private AtomicInteger pageNum2020 = new AtomicInteger(-1);
    private AtomicInteger pageNum2021 = new AtomicInteger(-1);
    private AtomicInteger pageNum2022 = new AtomicInteger(-1);
    private AtomicInteger pageNum2023 = new AtomicInteger(-1);

    private final int pageSize = 1000;

    public void sync2014(Criteria criteria) {
        pageNum2014.getAndIncrement();
        log.info("pageNum2014={}", pageNum2014.get());
        List<Document2014Entity> list = document2014Mapper.findList(0, pageSize, criteria);
        log.info("size2014={}", list.size());
        for (Document2014Entity entity : list) {
            DocumentOtherEntity xsEntity = new DocumentOtherEntity();
            BeanUtils.copyProperties(entity, xsEntity);
            try {
                otherMapper.insert(xsEntity);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void sync2015(Criteria criteria) {
        if (pageNum2015.get() > 3) {
            pageNum2015.set(0);
        }
        pageNum2015.getAndIncrement();
        log.info("pageNum2015={}", pageNum2015.get());
        List<Document2015Entity> list = document2015Mapper.findList(0, pageSize, criteria);
        log.info("size2015={}", list.size());
        for (Document2015Entity entity : list) {
            DocumentOtherEntity xsEntity = new DocumentOtherEntity();
            BeanUtils.copyProperties(entity, xsEntity);
            try {
                otherMapper.insert(xsEntity);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                document2015Mapper.delete(entity.getId());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void sync2016(Criteria criteria) {
        pageNum2016.getAndIncrement();
        log.info("pageNum2016={}", pageNum2017.get());
        List<Document2016Entity> list = document2016Mapper.findList(0, pageSize, criteria);
        log.info("size2016={}", list.size());
        for (Document2016Entity entity : list) {
            DocumentOtherEntity xsEntity = new DocumentOtherEntity();
            BeanUtils.copyProperties(entity, xsEntity);
            try {
                otherMapper.insert(xsEntity);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                document2017Mapper.delete(entity.getId());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void sync2017(Criteria criteria) {
        if (pageNum2017.get() > 3) {
            pageNum2017.set(0);
        }
        pageNum2017.getAndIncrement();
        log.info("pageNum2017={}", pageNum2017.get());
        List<Document2017Entity> list = document2017Mapper.findList(0, pageSize, criteria);
        log.info("size2017={}", list.size());
        for (Document2017Entity entity : list) {
            DocumentOtherEntity xsEntity = new DocumentOtherEntity();
            BeanUtils.copyProperties(entity, xsEntity);
            try {
                otherMapper.insert(xsEntity);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                document2017Mapper.delete(entity.getId());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void sync2018(Criteria criteria) {
        if (pageNum2018.get() > 3) {
            pageNum2018.set(0);
        }
        pageNum2018.getAndIncrement();
        log.info("pageNum2018={}", pageNum2018.get());
        List<Document2018Entity> list = document2018Mapper.findList(0, pageSize, criteria);
        log.info("size2018={}", list.size());
        for (Document2018Entity entity : list) {
            DocumentOtherEntity xsEntity = new DocumentOtherEntity();
            BeanUtils.copyProperties(entity, xsEntity);
            try {
                otherMapper.insert(xsEntity);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                document2018Mapper.delete(entity.getId());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void sync2019(Criteria criteria) {
        if (pageNum2019.get() > 3) {
            pageNum2019.set(0);
        }
        pageNum2019.getAndIncrement();
        log.info("pageNum2019={}", pageNum2019.get());
        List<Document2019Entity> list = document2019Mapper.findList(0, pageSize, criteria);
        log.info("size2019={}", list.size());
        for (Document2019Entity entity : list) {
            DocumentOtherEntity xsEntity = new DocumentOtherEntity();
            BeanUtils.copyProperties(entity, xsEntity);
            try {
                otherMapper.insert(xsEntity);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                document2019Mapper.delete(entity.getId());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void sync2020(Criteria criteria) {
        if (pageNum2020.get() > 3) {
            pageNum2020.set(0);
        }
        pageNum2020.getAndIncrement();
        log.info("pageNum2020={}", pageNum2020.get());
        List<Document2020Entity> list = document2020Mapper.findList(0, pageSize, criteria);
        log.info("size2020={}", list.size());
        for (Document2020Entity entity : list) {
            DocumentOtherEntity xsEntity = new DocumentOtherEntity();
            BeanUtils.copyProperties(entity, xsEntity);
            try {
                otherMapper.insert(xsEntity);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                document2020Mapper.delete(entity.getId());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void sync2022(Criteria criteria) {
        if (pageNum2022.get() > 3) {
            pageNum2022.set(0);
        }
        pageNum2022.getAndIncrement();
        log.info("pageNum2022={}", pageNum2022.get());
        List<Document2022Entity> list = document2022Mapper.findList(0, pageSize, criteria);
        log.info("size2022={}", list.size());
        for (Document2022Entity entity : list) {
            DocumentOtherEntity xsEntity = new DocumentOtherEntity();
            BeanUtils.copyProperties(entity, xsEntity);
            try {
                otherMapper.insert(xsEntity);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                document2021Mapper.delete(entity.getId());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
