package com.whllow.iot.service;

import com.sun.jdi.ObjectCollectedException;
import com.whllow.iot.dao.DeviceDataMapper;
import com.whllow.iot.dao.DeviceMapper;
import com.whllow.iot.entity.Device;
import com.whllow.iot.entity.DeviceData;
import com.whllow.iot.entity.IotConstance;
import com.whllow.iot.entity.User;
import com.whllow.iot.util.CheckUtil;
import com.whllow.iot.util.ConstantCheckMethod;
import com.whllow.iot.util.HostHolder;
import com.whllow.iot.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class DeviceService implements IotConstance {

    @Autowired
    public DeviceDataMapper deviceDataMapper;

    @Autowired
    public RedisTemplate<String,Object> redisTemplate;


    @Autowired
    public HostHolder hostHolder;

    @Autowired
    public DeviceMapper deviceMapper;


    /**
     * {"device_id":"Esp32A01","time":"2021-3-12 21:26:26",
     * "PH":0,"TDS":0,"Temperature":25.125}
     * */
    //存储数据
    public Map<String,Object> saveData(DeviceData device){
        Map<String,Object> map = null;

        map = CheckUtil.check(device,new ConstantCheckMethod());
        //存储数据
        String deviceKey = RedisKeyUtil.getDeviceData(device.getDeviceId());
        String deviceStatusKey = RedisKeyUtil.getDeviceStatus(device.getDeviceId());
        Device tmp = (Device) redisTemplate.opsForValue().get(deviceStatusKey);
        if(tmp==null){
            Device deviceStatus = deviceMapper.selectDeviceByDeviceId(device.getDeviceId());
            deviceStatus.setStatus(1);
            redisTemplate.opsForValue().set(deviceStatusKey,deviceStatus,10,TimeUnit.SECONDS);
        }else if(tmp.getStatus()==0){
            tmp.setStatus(1);
            redisTemplate.opsForValue().set(deviceStatusKey,tmp);
        }else if(!map.isEmpty()){
            tmp.setStatus(2);
            redisTemplate.opsForValue().set(deviceStatusKey,tmp);
        }
        redisTemplate.opsForValue().set(deviceKey,device,10, TimeUnit.SECONDS);
        deviceDataMapper.insertDeviceData(device);

        return map;
    }

    //获取设备列表
    public List<Device> getDevicesList(){
        User user = hostHolder.getUser();
        if(user==null) return null;
        List<Device> list = deviceMapper.selectDevicesByUserId(user.getId());
        if(list == null || list.isEmpty()) return list;
        for(Device device:list){
            String deviceKey = RedisKeyUtil.getDeviceStatus(device.getDeviceId());
            Device tmp = (Device)redisTemplate.opsForValue().get(deviceKey);
            if(tmp!=null) device.setStatus(tmp.getStatus());
        }
        return list;
    }

    //获取设备的历史数据
    public List<Object> getDevicesDataHistory(String deviceId){
        User user = hostHolder.getUser();
        if(user==null) return null;
        List<DeviceData> datas = deviceDataMapper.selelctDatasByDeviceId(deviceId);
        List<Object> lists = new ArrayList<>();
        List<Float> ph = new ArrayList<>();
        List<Float> tds = new ArrayList<>();
        List<Float> temperature = new ArrayList<>();
        List<Date> dates = new ArrayList<>();
        for(DeviceData data:datas){
            ph.add(data.getPh());
            tds.add(data.getTds());
            temperature.add(data.getTemperature());
            dates.add(data.getDeviceTime());
        }

        lists.add(ph);
        lists.add(tds);
        lists.add(temperature);
        lists.add(dates);
        return lists;
    }


    public List<DeviceData>getMapData(int userId){
        List<DeviceData> datas = new ArrayList<>();
        List<Device> deviceList = deviceMapper.selectDevicesByUserId(userId);
        String deviceDataKey = null;
        for(Device device:deviceList){
            deviceDataKey = RedisKeyUtil.getDeviceData(device.getDeviceId());
            DeviceData data = (DeviceData) redisTemplate.opsForValue().get(deviceDataKey);
            datas.add(data);
        }
        return datas;
    }





}
