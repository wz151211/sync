package com.ping.syncparse.service;

import com.ping.syncparse.entity.AreaEntity;
import com.ping.syncparse.mapper.AreaMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AreaService {
    @Autowired
    private AreaMapper areaMapper;

    public AreaEntity find(String city, String county) {
        return areaMapper.find(city, county);
    }

    public AreaEntity findCounty(String city, String county) {
        return areaMapper.findCounty(city, county);
    }

    public AreaEntity findCity(String city) {
        return areaMapper.findCity(city);
    }

    public List<AreaEntity> findCityChild(String city) {
        return areaMapper.findCityChild(city);
    }

    public AreaEntity findProvince(String province) {
        return areaMapper.findProvince(province);
    }

    public List<AreaEntity> findProvinceChild(String province) {
        return areaMapper.findProvinceChild(province);
    }
}
