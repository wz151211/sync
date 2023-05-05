package com.ping.syncparse.service;

import com.ping.syncparse.entity.AreaEntity;
import com.ping.syncparse.mapper.AreaMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AreaService {
    @Autowired
    private AreaMapper areaMapper;

    public AreaEntity find(String city, String county) {
        return areaMapper.find(city, county);
    }
}
