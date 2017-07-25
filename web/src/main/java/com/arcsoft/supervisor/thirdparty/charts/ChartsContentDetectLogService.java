package com.arcsoft.supervisor.thirdparty.charts;

import com.arcsoft.supervisor.model.domain.channel.Channel;
import com.arcsoft.supervisor.model.domain.log.ContentDetectLog;
import com.arcsoft.supervisor.repository.channel.ChannelRepository;
import com.arcsoft.supervisor.repository.log.ContentDetectLogRepository;
import com.arcsoft.supervisor.utils.DateHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * Created by wwj on 2017/2/20.
 */
@Service
public class ChartsContentDetectLogService {
    @Autowired
    private ContentDetectLogRepository contentDetectLogRepository;
    @Autowired
    private ChannelRepository channelRepository;


    public int findByContentDetectLog(String channelName, long lowThreshold, long upThreshold) {
        List<ContentDetectLog> list = contentDetectLogRepository.findByChannelNameAndStartTimeGreaterThanEqualAndStartTimeLessThan(channelName, lowThreshold, upThreshold);
        return list.size();
    }

    public int countByContentDetectLog(String channelName, long lowThreshold, long upThreshold) {
        List<ContentDetectLog> list = contentDetectLogRepository.findByChannelNameAndStartTimeGreaterThanEqualAndStartTimeLessThan(channelName, lowThreshold, upThreshold);
        return list.size();
    }


    public Long ConverTime(int year, int month, int day, int hour) {
        return DateHelper.createDate(year, month, day, hour, 0, 0).getTime();

    }

    public Long ConverMonth(int year, int month) {
        return DateHelper.createDate(year, month, 0).getTime();

    }

    public List<HashMap<String, Object>> CountEveryDay(String channelName, int year, int month, int day) {
        List list = new ArrayList();
        for (int i = 0; i < 24; i++) {
            HashMap<String, Object> map = new HashMap<>();
            int count = this.findByContentDetectLog(channelName, this.ConverTime(year, month, day, i), this.ConverTime(year, month, day, i + 1));
            map.put("title", String.valueOf(i));
            map.put("count", String.valueOf(count));
            map.put("channelName", channelName);
            list.add(map);
        }
        // int hour0=this.CountContentDetectLog(channelName,this.ConverTime(year,month,day,0),   this.ConverTime(year,month,day,1));
        return list;
    }


    public List<HashMap<String, Object>> CountEveryHour(String channelName, int year, int month, int day, int startHour, int endHour) {
        List list = new ArrayList();
        for (int i = startHour; i <= endHour; i++) {
            HashMap<String, Object> map = new HashMap<>();
            int count = this.findByContentDetectLog(channelName, this.ConverTime(year, month, day, i), this.ConverTime(year, month, day, i + 1));
            map.put("title", String.valueOf(i)+"时");
            map.put("count", String.valueOf(count));
            map.put("channelName", channelName);
            list.add(map);
        }

        return list;
    }

    public List<HashMap<String, Object>> CountEveryDay2(String channelName, int startYear, int startMonth, int startDay, int endYear, int endMoth, int endDay) {
        List list = new ArrayList();
        Calendar start = Calendar.getInstance();
        start.set(startYear, startMonth, startDay);
        Long startTIme = start.getTimeInMillis();

        Calendar end = Calendar.getInstance();
        end.set(endYear, endMoth, endDay);
        Long endTime = end.getTimeInMillis();
        Long oneDay = 1000 * 60 * 60 * 24l;

        Long time = startTIme;
        while (time <= endTime) {
            Date d = new Date(time);
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            HashMap<String, Object> map = new HashMap<>();
            int count = this.countByContentDetectLog(channelName, time, time + oneDay);
            map.put("title", df.format(d));
            map.put("count", String.valueOf(count));
            map.put("channelName", channelName);
            list.add(map);
            time += oneDay;
        }


        // int hour0=this.CountContentDetectLog(channelName,this.ConverTime(year,month,day,0),   this.ConverTime(year,month,day,1));
        return list;
    }

    /**
     * >=365天计算
     *
     * @param channelName
     * @param startYear
     * @param startMonth
     * @param EndYear
     * @param EndMoth
     * @return
     */
    public List<HashMap<String, Object>> CountEveryMonth(String channelName, int startYear, int startMonth, int EndYear, int EndMoth) {
        List list = new ArrayList();
        for (int year = startYear; year <= EndYear; year++) {
            int month;
            if (year == startYear)
                for (month = startMonth; month <= 12; month++) {
                    HashMap<String, Object> map = new HashMap<>();
                    int count = this.countByContentDetectLog(channelName, this.ConverMonth(year, month), this.ConverMonth(year, month + 1));
                    map.put("title", String.valueOf(year) + "-" + String.valueOf(month));
                    map.put("count", String.valueOf(count));
                    map.put("channelName", channelName);
                    list.add(map);
                }
            else if (year < EndYear)
                for (month = 1; month <= 12; month++) {
                    HashMap<String, Object> map = new HashMap<>();
                    int count = this.countByContentDetectLog(channelName, this.ConverMonth(year, month), this.ConverMonth(year, month + 1));
                    map.put("title", String.valueOf(year) + "-" + String.valueOf(month));
                    map.put("count", String.valueOf(count));
                    map.put("channelName", channelName);
                    list.add(map);
                }
            else
                for (month = 1; month <= EndMoth; month++) {
                    HashMap<String, Object> map = new HashMap<>();
                    int count = this.countByContentDetectLog(channelName, this.ConverMonth(year, month), this.ConverMonth(year, month + 1));
                    map.put("title", String.valueOf(year) + "-" + String.valueOf(month));
                    map.put("count", String.valueOf(count));
                    map.put("channelName", channelName);
                    list.add(map);
                }
        }

        return list;
    }


    public List CountLog(String beginDate, String endDate) {
        List<Channel> list = channelRepository.findAll();
        List resultList = new ArrayList();
        if (list != null && list.size() > 0) {
            for (Channel channel : list) {
                //this.CountContentDetectLog(channel.getName(),beginTime,endTime);
                List countList = this.CountEveryDay(channel.getName(), 2017, 02, 19);
                HashMap<String, Object> map = new HashMap<>();
                map.put(channel.getName(), countList);
                resultList.add(map);
            }

        }
        return resultList;
    }

}
