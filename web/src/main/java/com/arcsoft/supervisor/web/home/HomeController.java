package com.arcsoft.supervisor.web.home;


import com.arcsoft.supervisor.model.domain.channel.Channel;
import com.arcsoft.supervisor.model.domain.channel.ChannelGroup;
import com.arcsoft.supervisor.model.domain.log.ContentDetectLog;
import com.arcsoft.supervisor.service.channel.ChannelGroupService;
import com.arcsoft.supervisor.service.channel.ChannelService;
import com.arcsoft.supervisor.service.commons.mediainfo.MediainfoService;
import com.arcsoft.supervisor.service.log.ContentDetectLogService;
import com.arcsoft.supervisor.service.log.impl.ContentDetectQueryParams;
import com.arcsoft.supervisor.service.task.TaskExecutor;
import com.arcsoft.supervisor.service.task.TaskService;
import com.arcsoft.supervisor.utils.app.Environment;
import com.arcsoft.supervisor.web.ControllerSupport;
import com.arcsoft.tmservice.Content;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletContext;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Controller class for {@code home}.
 *
 * @author jt.
 */
@Controller
@RequestMapping("/home")
public class HomeController extends ControllerSupport {

    private static final String VIEW_INDEX = "/home/index";

    @Autowired
    private ChannelGroupService channelGroupService;

    @Autowired
    private ChannelService channelService;

    @Autowired
    @Qualifier("defaultMediainfoService")
    private MediainfoService mediainfoService;

    @Autowired
    private TaskExecutor taskExecutor;

    @Autowired
    private TaskService taskService;
    
    @Autowired
    private ContentDetectLogService contentDetectLogService;

    @Autowired(required = false)
    private ServletContext servletContext;

    @RequestMapping(value = "/index", method = RequestMethod.GET)
    public String toStream(Model model) {
        Boolean mosaic = Environment.getProfiler().isMosaic();
        model.addAttribute("supportmosaic",mosaic);
        return VIEW_INDEX;
    }

    @RequestMapping(value = "/groups", method = RequestMethod.GET)
    @ResponseBody
    public List<ChannelGroup> getGroups() {
        return channelGroupService.listAll();
    }

    @RequestMapping(value = "/channels", method = RequestMethod.GET)
    @ResponseBody
    public List<Channel> getChannels(Integer groupId) {
        if (groupId != null && groupId > 0) {
            return channelService.getByGroupId(groupId);
        }
        return Collections.emptyList();
    }

    @RequestMapping(value = "/unGroupedChannels", method = RequestMethod.GET)
    @ResponseBody
    public List<Channel> getUnGroupedChannels() {
        return channelService.getUngrouped();
    }
    

    
    @RequestMapping(value = "/getchannellogs", method = RequestMethod.GET)
    @ResponseBody
    public List<ContentDetectLog> getChannelLogs(String channelName) {
        if (channelName != null) {
        	// last 20 logs
            return contentDetectLogService.getByChannelName(channelName);
        }

        return Collections.emptyList();
    }

    @RequestMapping(value = "/cdsummary", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> getContentDetectLogs(@RequestBody ContentDetectSummaryQueryParams summaryParams) {
        HashMap<String, Object> result;
        if(summaryParams.getType() == 0) {
            result = getContentDetectRealTime(summaryParams);
        } else {
            result = getContentDetectSummary(summaryParams);
        }
        return result;
    }

    private HashMap<String, Object> getContentDetectRealTime(ContentDetectSummaryQueryParams summaryQueryParams) {
        ContentDetectQueryParams params = summaryQueryParams.getParams();
        LocalDateTime nowTime = LocalDateTime.now(DateTimeZone.forTimeZone(TimeZone.getDefault()));
        LocalDateTime startTime = nowTime.minusSeconds(30);
        params.setStartTime(startTime.toDate(TimeZone.getDefault()));
        params.setEndTime(nowTime.toDate(TimeZone.getDefault()));
        HashMap<String, Object> result = new HashMap<>();
        List<ContentDetectLog> logs = contentDetectLogService.findAll(params);

        ArrayList<String> label = generateRealTimeLabel(nowTime, startTime);
        ArrayList<HashMap<String, Object>> datasets = new ArrayList<>();

        for(Integer type : summaryQueryParams.getErrorTypes()) {
            ArrayList<Long> data = generateRealTimeData(nowTime, startTime, logs, type);
            HashMap<String, Object> dataset = new HashMap<>();
            dataset.put("type", type);
            dataset.put("data", data);
            datasets.add(dataset);
        }
        result.put("label", label);
        result.put("datasets", datasets);
        return result;
    }

    private ArrayList<String> generateRealTimeLabel(LocalDateTime nowTime, LocalDateTime startTime) {
        ArrayList<String> label = new ArrayList<>();
        while(startTime.compareTo(nowTime) < 0) {
            label.add(startTime.toString("HH:mm:ss"));
            startTime = startTime.plusSeconds(1);
        }
        return label;
    }

    private ArrayList<Long> generateRealTimeData(LocalDateTime nowTime, LocalDateTime startTime, List<ContentDetectLog> logs, int type) {
        ArrayList<Long> data = new ArrayList<>();
        while(startTime.compareTo(nowTime) < 0) {
            boolean contain = false;
            for(ContentDetectLog log : logs) {
                if(log.getType() != type) {
                    continue;
                }
                LocalDateTime logStart = new LocalDateTime(log.getStartTime(), DateTimeZone.forTimeZone(TimeZone.getDefault()));
                if(logStart.compareTo(startTime) < 0) {
                    Long logEndDate = log.getEndTime();
                    if(logEndDate == 0l) {
                        contain = true;
                        break;
                    } else {
                        LocalDateTime logEnd = new LocalDateTime(logEndDate, DateTimeZone.forTimeZone(TimeZone.getDefault()));
                        if(logEnd.compareTo(startTime) > 0) {
                            contain = true;
                            break;
                        }
                    }
                }
            }
            if(contain) {
                data.add(1l);
            } else {
                data.add(0l);
            }
            startTime = startTime.plusSeconds(1);
        }
        return data;
    }

    private HashMap<String, Object> getContentDetectSummary(ContentDetectSummaryQueryParams summaryParams) {
        ContentDetectQueryParams params = summaryParams.getParams();
        Date endTime = params.getEndTime();
        LocalDate end;
        if (endTime == null) {
            end = new LocalDate(new Date(), DateTimeZone.forTimeZone(TimeZone.getDefault())).plusDays(1);
        } else {
            end = new LocalDate(endTime, DateTimeZone.forTimeZone(TimeZone.getDefault())).plusDays(1);
        }
        Date startTime = params.getStartTime();
        LocalDate current;
        if (startTime == null) {
            current = end.minusDays(30);
        } else {
            current = new LocalDate(startTime, DateTimeZone.forTimeZone(TimeZone.getDefault()));
        }
        params.setStartTime(current.toDate());
        params.setEndTime(end.toDate());
        List<ContentDetectLog> logs = contentDetectLogService.findAll(params);

        HashMap<String, Object> result = new HashMap<>();
        ArrayList<HashMap<String, Object>> datasets = new ArrayList<>();

        HashMap<Integer, HashMap<LocalDate, Long>> summaryByTypeAndDate = new HashMap<>();
        for(Integer type: summaryParams.getErrorTypes()) {
            summaryByTypeAndDate.put(type, new HashMap<LocalDate, Long>());
        }
        for(ContentDetectLog log : logs) {
            Date startDate = log.getStartTimeAsDate();
            Integer type = log.getType();
            HashMap<LocalDate, Long> summaryByDate = summaryByTypeAndDate.get(type);
            if(summaryByDate == null) {
                continue;
            }
            LocalDate startLocalDate = new LocalDate(startDate, DateTimeZone.forTimeZone(TimeZone.getDefault()));
            Long count = summaryByDate.get(startLocalDate);
            if(count != null ) {
                summaryByDate.put(startLocalDate, count + 1);
            } else {
                summaryByDate.put(startLocalDate, 1l);
            }
        }

        ArrayList<String> label = generateLabel(summaryParams, end, current);
        for(Integer type : summaryByTypeAndDate.keySet()) {
            HashMap<LocalDate, Long> summaryByDate = summaryByTypeAndDate.get(type);
            ArrayList<Long> data = generateData(summaryParams, end, current, summaryByDate);

            HashMap<String, Object> dataset = new HashMap<>();
            dataset.put("type", type);
            dataset.put("data", data);
            datasets.add(dataset);
        }

        result.put("label", label);
        result.put("datasets", datasets);
        return result;
    }

    private ArrayList<String> generateLabel(ContentDetectSummaryQueryParams summaryParams, LocalDate end, LocalDate current) {
        ArrayList<String> label = new ArrayList<>();
        if (summaryParams.getType() == 1) {
            while (current.compareTo(end) < 0) {
                label.add(current.toString());
                current = current.plusDays(1);
            }
        } else if (summaryParams.getType() == 2) {
            LocalDate weekStart = current;
            while (current.compareTo(end) < 0) {
                if(weekStart.getWeekOfWeekyear() != current.getWeekOfWeekyear()) {
                    label.add(weekStart.toString() + "/" + current.minusDays(1).toString());
                    weekStart = current;
                }
                current = current.plusDays(1);
            }
            label.add(weekStart.toString() + "/" + end.minusDays(1).toString());
        }
        return label;
    }

    private ArrayList<Long> generateData(ContentDetectSummaryQueryParams summaryParams, LocalDate end, LocalDate current, HashMap<LocalDate, Long> summaryByDate) {
        ArrayList<Long> data = new ArrayList<>();
        if (summaryParams.getType() == 1) {
            while (current.compareTo(end) < 0) {
                Long count = summaryByDate.get(current);
                data.add(count != null ? count : 0);
                current = current.plusDays(1);
            }
        } else if (summaryParams.getType() == 2) {
            LocalDate weekStart = current;
            Long weekCount = 0l;
            while (current.compareTo(end) < 0) {
                if(weekStart.getWeekOfWeekyear() != current.getWeekOfWeekyear()) {
                    data.add(weekCount);
                    weekStart = current;
                    Long count = summaryByDate.get(current);
                    weekCount = count != null ? count : 0;
                } else {
                    Long count = summaryByDate.get(current);
                    weekCount += count != null ? count : 0;
                }
                current = current.plusDays(1);
            }
            data.add(weekCount);
        }
        return data;
    }

    @RequestMapping(value = "/getRecordInfo", method = RequestMethod.GET)
    @ResponseBody
    public ContentDetectLog getRecordInfo(Integer id) {
        return contentDetectLogService.getById(id);
    }

}
