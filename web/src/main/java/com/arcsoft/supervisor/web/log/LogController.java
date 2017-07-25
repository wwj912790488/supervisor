package com.arcsoft.supervisor.web.log;


import com.arcsoft.supervisor.commons.json.JsonMapper;
import com.arcsoft.supervisor.model.domain.channel.Channel;
import com.arcsoft.supervisor.model.domain.channel.ChannelGroup;
import com.arcsoft.supervisor.model.domain.log.ContentDetectLog;
import com.arcsoft.supervisor.model.domain.log.ServiceLog;
import com.arcsoft.supervisor.model.domain.log.SystemLog;
import com.arcsoft.supervisor.repository.channel.ChannelRepository;
import com.arcsoft.supervisor.repository.server.SSHConnectInfoRepository;
import com.arcsoft.supervisor.service.channel.ChannelGroupService;
import com.arcsoft.supervisor.service.device.RemoteShellExecutorService;
import com.arcsoft.supervisor.model.domain.server.SSHConnectInfo;
import com.arcsoft.supervisor.service.log.ContentDetectLogService;
import com.arcsoft.supervisor.service.log.ServiceLogService;
import com.arcsoft.supervisor.service.log.SystemLogService;
import com.arcsoft.supervisor.service.log.impl.ContentDetectQueryParams;
import com.arcsoft.supervisor.service.log.impl.ServiceLogQueryParams;
import com.arcsoft.supervisor.service.log.impl.SystemLogQueryParams;
import com.arcsoft.supervisor.thirdparty.charts.ChartsContentDetectLogService;
import com.arcsoft.supervisor.utils.DateHelper;
import com.arcsoft.supervisor.utils.app.Environment;
import com.arcsoft.supervisor.web.ControllerSupport;
import com.arcsoft.supervisor.web.JsonResult;
import com.google.common.base.Function;
import com.google.common.base.Predicates;
import com.google.common.collect.FluentIterable;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.arcsoft.supervisor.commons.SupervisorDefs.Constants.PAGER;
import static com.arcsoft.supervisor.commons.SupervisorDefs.Constants.QUERY_PARAMS;


/**
 * Controller class for {@code Log} module.
 *
 * @author jt.
 * @author zw
 */
@Controller
@RequestMapping("/log")
public class LogController extends ControllerSupport {

    private static final String VIEW_CONTENT = "/log/ContentMgr";
    private static final String VIEW_SERVICE = "/log/ServiceMgr";
    private static final String VIEW_SYSTEM = "/log/OperationMgr";
    private static final String VIEW_CHARTS = "/log/ContentChart";
    private static String COMMANDER_PATH = Environment.getProperty("commander.path", "/usr/local/arcvideo/supervisor/");

    @Autowired
    private SystemLogService systemLogService;
    @Autowired
    private SSHConnectInfoRepository sshConnectInfoRepository;
    @Autowired
    private ContentDetectLogService contentDetectLogService;
    @Autowired
    private ServiceLogService serviceLogService;
    @Autowired
    private ChannelGroupService channelGroupService;
    @Autowired
    private ChartsContentDetectLogService chartsContentDetectLogService;
    @Autowired
    private ChannelRepository channelRepository;
    @Autowired
    private RemoteShellExecutorService remoteShellExecutorService;
    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Date.class, new CustomDateEditor(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"), true));
    }


    @RequestMapping(value = "/op")
    public String operation(Model model, SystemLogQueryParams q,
                            @PageableDefault(sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable) {
        model.addAttribute(PAGER, systemLogService.paginate(q, (PageRequest) pageable));
        model.addAttribute(QUERY_PARAMS, q);
        Boolean mosaic = Environment.getProfiler().isMosaic();
        model.addAttribute("supportmosaic", mosaic);
        return VIEW_SYSTEM;
    }

    @RequestMapping(value = "/op", params = "export=excel")
    public ModelAndView operationExport(Model model, SystemLogQueryParams q) {
        model.addAttribute("q", q);
        model.addAttribute("type", "op");
        Boolean mosaic = Environment.getProfiler().isMosaic();
        model.addAttribute("supportmosaic", mosaic);
        return new ModelAndView("LogExcel", model.asMap());
    }

    @RequestMapping(value = "/op", params = "delete=true")
    public String operationDelete(Model model, SystemLogQueryParams q) {
        systemLogService.delete(q);
        PageRequest pageable = new PageRequest(0, 10, new Sort(Sort.Direction.DESC, "id"));
        model.addAttribute(PAGER, systemLogService.paginate(null, pageable));
        Boolean mosaic = Environment.getProfiler().isMosaic();
        model.addAttribute("supportmosaic", mosaic);
        return VIEW_SYSTEM;
    }

    @RequestMapping(value = "/deleteSystemLogs", method = RequestMethod.POST)
    @ResponseBody
    public JsonResult deleteOperationLogs(@RequestParam(value = "ids", required = false) String ids) {
        JsonResult result = JsonResult.fromSuccess();
        try {
            List<SystemLog> logs = JsonMapper.getMapper().readValue(ids, JsonMapper.getMapper().getTypeFactory().constructCollectionType(List.class, SystemLog.class));
            if (logs != null) {
                systemLogService.deleteByIds(logs);
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.error();
        }
        return result;
    }

    @RequestMapping(value = "/chart")
    public String contentChart(Model model, ContentDetectQueryParams q,
                               @PageableDefault(sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable) {
        if (q.getTypes().isEmpty()) {
            q.setTypes(Arrays.asList(0, 2, 27, 28, 29, 30, 32, 33, 34));
        }
        List<ChannelGroup> groups = channelGroupService.listAll();
        List<Integer> groupList = new ArrayList<>();
        if (q.getGroups().isEmpty()) {
            List<Integer> queryGroups = FluentIterable.from(groups).transform(new Function<ChannelGroup, Integer>() {
                @Nullable
                @Override
                public Integer apply(ChannelGroup group) {
                    return group.getId();
                }
            }).toList();
            groupList.addAll(queryGroups);
            groupList.add(-1);
            q.setGroups(groupList);
        }
        List<Channel> channelList = channelRepository.findAll();
        model.addAttribute(PAGER, contentDetectLogService.paginate(q, (PageRequest) pageable));
        model.addAttribute(QUERY_PARAMS, q);
        model.addAttribute("groups", groups);
        model.addAttribute("channelList", channelList);
        Boolean mosaic = Environment.getProfiler().isMosaic();
        model.addAttribute("supportmosaic", mosaic);
        return VIEW_CHARTS;
    }

    @RequestMapping(value = "/baiduchart", params = "export=excel")
    public ModelAndView contentChartExport(Model model, ContentDetectQueryParams q) {
        model.addAttribute("q", q);
        model.addAttribute("type", "chart");
        return new ModelAndView("LogExcel", model.asMap());
    }

    @RequestMapping(value = "/baiduchart")
    @ResponseBody
    public List contentChart(Model model, ContentDetectQueryParams q,
                             @RequestParam(value = "startTime") String startTime,
                             @RequestParam(value = "endTime") String endTime
    ) {

        //SimpleDateFormat sp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        List Resultlist = new ArrayList();
        List<HashMap<String, Object>> list1 = null;

        List<ChannelGroup> groups2 = FluentIterable.from(q.getGroups()).transform(new Function<Integer, ChannelGroup>() {
            @Nullable
            @Override
            public ChannelGroup apply(Integer input) {
                if (input != -1) {
                    ChannelGroup group = new ChannelGroup();
                    group.setId(input);
                    return group;
                } else {

                }
                return null;
            }
        }).filter(Predicates.notNull()).toList();

        List<Channel> ChannelList = channelRepository.findByGroupIn(groups2);
        if (q.getGroups().contains(-1)) {
            ChannelList.addAll(channelRepository.findByGroupNull());
        }
        try {
            Calendar start = Calendar.getInstance();
            start.setTime(q.getStartTime());
            Calendar end = Calendar.getInstance();
            end.setTime(q.getEndTime());
            int diff = DateHelper.dateDiff(q.getEndTime(), q.getStartTime());
            if (diff > 365) {
                for (int i = 0; i < ChannelList.size(); i++) {
                    list1 = chartsContentDetectLogService.CountEveryMonth(ChannelList.get(i).getName(), start.get(Calendar.YEAR), start.get(Calendar.MONTH) + 1, end.get(Calendar.YEAR), end.get(Calendar.MONTH) + 1);
                    Resultlist.add(list1);
                }
                return Resultlist;
            } else {
                int hourDiff = DateHelper.hourDiff(q.getEndTime(), q.getStartTime());
                if (hourDiff > 24) {
                    for (int i = 0; i < ChannelList.size(); i++) {
                        list1 = chartsContentDetectLogService.CountEveryDay2(ChannelList.get(i).getName(), start.get(Calendar.YEAR), start.get(Calendar.MONTH), start.get(Calendar.DAY_OF_MONTH), end.get(Calendar.YEAR), end.get(Calendar.MONTH), end.get(Calendar.DAY_OF_MONTH));
                        Resultlist.add(list1);
                    }
                    return Resultlist;
                } else {
                    for (int i = 0; i < ChannelList.size(); i++) {
                        list1 = chartsContentDetectLogService.CountEveryHour(ChannelList.get(i).getName(), start.get(Calendar.YEAR), start.get(Calendar.MONTH) + 1, start.get(Calendar.DAY_OF_MONTH), start.get(Calendar.HOUR_OF_DAY), end.get(Calendar.HOUR_OF_DAY));
                        Resultlist.add(list1);
                    }
                    return Resultlist;
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    @RequestMapping(value = "/cd")
    public String contentDetect(Model model, ContentDetectQueryParams q,
                                @PageableDefault(sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable) {
        if (q.getTypes().isEmpty()) {
            q.setTypes(Arrays.asList(0, 2, 27, 28, 29, 30, 32, 33, 34));
        }
        List<ChannelGroup> groups = channelGroupService.listAll();
        List<Integer> groupList = new ArrayList<>();
        if (q.getGroups().isEmpty()) {
            List<Integer> queryGroups = FluentIterable.from(groups).transform(new Function<ChannelGroup, Integer>() {
                @Nullable
                @Override
                public Integer apply(ChannelGroup group) {
                    return group.getId();
                }
            }).toList();
            groupList.addAll(queryGroups);
            groupList.add(-1);
            q.setGroups(groupList);
        }
        List<Channel> channelList = channelRepository.findAll();
        model.addAttribute(PAGER, contentDetectLogService.paginate(q, (PageRequest) pageable));
        model.addAttribute(QUERY_PARAMS, q);
        model.addAttribute("groups", groups);
        model.addAttribute("channelList", channelList);
        Boolean mosaic = Environment.getProfiler().isMosaic();
        model.addAttribute("supportmosaic", mosaic);
        return VIEW_CONTENT;
    }

    @RequestMapping(value = "/cd", params = "export=excel")
    public ModelAndView contentDetectExport(Model model, ContentDetectQueryParams q) {
        model.addAttribute("q", q);
        model.addAttribute("type", "cd");
        return new ModelAndView("LogExcel", model.asMap());
    }

    @RequestMapping(value = "/cd", params = "delete=true")
    public String contentDetectDelete(Model model, ContentDetectQueryParams q) {
        contentDetectLogService.delete(q);
        PageRequest pageable = new PageRequest(0, 10, new Sort(Sort.Direction.DESC, "id"));
        ContentDetectQueryParams empty = new ContentDetectQueryParams();
        empty.setTypes(Arrays.asList(0, 2, 27, 28, 29, 30, 32, 33, 34));
        List<ChannelGroup> groups = channelGroupService.listAll();
        List<Integer> groupList = new ArrayList<>();
        if (q.getGroups().isEmpty()) {
            List<Integer> queryGroups = FluentIterable.from(groups).transform(new Function<ChannelGroup, Integer>() {
                @Nullable
                @Override
                public Integer apply(ChannelGroup group) {
                    return group.getId();
                }
            }).toList();
            groupList.addAll(queryGroups);
            groupList.add(-1);
            empty.setGroups(groupList);
        }
        model.addAttribute(PAGER, contentDetectLogService.paginate(empty, pageable));
        model.addAttribute(QUERY_PARAMS, empty);
        model.addAttribute("groups", groups);
        Boolean mosaic = Environment.getProfiler().isMosaic();
        model.addAttribute("supportmosaic", mosaic);
        return VIEW_CONTENT;
    }

    @RequestMapping(value = "/deleteContentDetectLogs")
    @ResponseBody
    public JsonResult deleteContentDetectLogs(@RequestParam(value = "ids", required = false) String ids) {
        JsonResult result = JsonResult.fromSuccess();
        try {
            List<ContentDetectLog> logs = JsonMapper.getMapper().readValue(ids, JsonMapper.getMapper().getTypeFactory().constructCollectionType(List.class, ContentDetectLog.class));
            if (logs != null) {
                contentDetectLogService.deleteByIds(logs);
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.error();
        }
        return result;
    }

    @RequestMapping(value = "/s")
    public String serviceLog(Model model, ServiceLogQueryParams q,
                             @PageableDefault(sort = {"time"}, direction = Sort.Direction.DESC) Pageable pageable) {
        model.addAttribute(PAGER, serviceLogService.paginate(q, (PageRequest) pageable));
        model.addAttribute(QUERY_PARAMS, q);
        Boolean mosaic = Environment.getProfiler().isMosaic();
        model.addAttribute("supportmosaic", mosaic);
        return VIEW_SERVICE;
    }

    @RequestMapping(value = "/s", params = "export=excel")
    public ModelAndView serviceLogExport(Model model, ServiceLogQueryParams q) {
        model.addAttribute("q", q);
        model.addAttribute("type", "s");
        return new ModelAndView("LogExcel", model.asMap());
    }

    @RequestMapping(value = "/downLogs")
    @ResponseBody
    public void operationExport(HttpServletRequest request,HttpServletResponse response,
                                   @RequestParam(value = "serviceLogId") Integer id,
                                   @RequestParam(value = "includeSystem",required = false) boolean includeSystem, @RequestParam(value = "includeApplication" ,required = false) boolean includeApplication) {

        StringBuilder sb = new StringBuilder(COMMANDER_PATH + "supervisor-agent/bin/bugreport.sh ");
        Integer taskId = 1;
        ServiceLog serviceLog = serviceLogService.findByid(id);
        serviceLog.setAffix(true);
        DateTime beginTime = new DateTime(serviceLog.getTime()).minusHours(1);
        DateTime endTime = new DateTime(serviceLog.getTime()).plusHours(1);
        String ip=serviceLog.getIp();
        SSHConnectInfo sshConnectInfo=sshConnectInfoRepository.findByIp(ip);
        //SSHConnectInfo sshConnectInfo=new SSHConnectInfo(ip,22,"root","master007");
        String fileName="alertlog"+new DateTime(serviceLog.getTime()).toString("yyyyMMddHHmmss")+".zip";
        if (includeSystem && includeApplication) {
            sb.append(" -r  -s  -l  -w  -a  -c  -n  -o  -i ").append(taskId).append(" -t '").append(beginTime.toString("yyyy-MM-dd HH:mm:ss")).append("' -T '")
                    .append(endTime.toString("yyyy-MM-dd HH:mm:ss")).append("' -p  /tmp ").append(" -f ").append(fileName);
            try {
                if (remoteShellExecutorService.execCommand(sshConnectInfo, sb.toString())) {
                    if(remoteShellExecutorService.getSSHFile(sshConnectInfo,"/root","/tmp/"+fileName)){
                        downLog(request, response, "/root/"+fileName);
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            serviceLogService.save(serviceLog);
            return;
        }

        if (includeSystem) {
            // -r -s -l
            sb.append(" -r  -s  -l ").append(" -t '").append(beginTime.toString("yyyy-MM-dd HH:mm:ss")).append("' -T '")
                    .append(endTime.toString("yyyy-MM-dd HH:mm:ss")).append("' -p  /tmp ").append(" -f ").append(fileName);
            try {
                if (remoteShellExecutorService.execCommand(sshConnectInfo, sb.toString())) {
                    if(remoteShellExecutorService.getSSHFile(sshConnectInfo,"/root","/tmp/"+fileName)){
                        downLog(request, response, "/root/"+fileName);
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            serviceLogService.save(serviceLog);
            return;
        }
        if (includeApplication) {
            sb.append(" -w  -a  -c  -n  -o  -i ").append(taskId).append(" -t '").append(beginTime.toString("yyyy-MM-dd HH:mm:ss")).append("' -T '")
                    .append(endTime.toString("yyyy-MM-dd HH:mm:ss")).append("' -p  /tmp ").append(" -f ").append(fileName);
            try {
                if (remoteShellExecutorService.execCommand(sshConnectInfo, sb.toString())) {
                    if(remoteShellExecutorService.getSSHFile(sshConnectInfo,"/root","/tmp/"+fileName)){
                        downLog(request, response, "/root/"+fileName);
                    }

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            serviceLogService.save(serviceLog);
            return;
        }

    }

    public void downLog(HttpServletRequest request, HttpServletResponse response, String fileName){
        File file = new File(fileName);
        if (file != null && file.exists()) {
            try {
                remoteShellExecutorService.readFileContent(request, response, file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @RequestMapping(value = "/s", params = "delete=true")
    public String serviceLogDelete(Model model, ServiceLogQueryParams q) {
        serviceLogService.delete(q);
        PageRequest pageable = new PageRequest(0, 10, new Sort(Sort.Direction.DESC, "time"));
        model.addAttribute(PAGER, serviceLogService.paginate(null, pageable));
        Boolean mosaic = Environment.getProfiler().isMosaic();
        model.addAttribute("supportmosaic", mosaic);
        return VIEW_SERVICE;
    }


    @RequestMapping(value = "/deleteServiceLogs")
    @ResponseBody
    public JsonResult deleteServiceLogs(@RequestParam(value = "ids", required = false) String ids) {
        JsonResult result = JsonResult.fromSuccess();
        try {
            List<ServiceLog> logs = JsonMapper.getMapper().readValue(ids, JsonMapper.getMapper().getTypeFactory().constructCollectionType(List.class, ServiceLog.class));
            if (logs != null) {
                serviceLogService.deleteByIds(logs);
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.error();
        }
        return result;
    }

    @RequestMapping(value = "/getRecordInfo", method = RequestMethod.GET)
    @ResponseBody
    public ContentDetectLog getRecordInfo(Integer id) {
        return contentDetectLogService.getById(id);
    }

    @RequestMapping(value = "/queryCharts", method = RequestMethod.GET)
    @ResponseBody
    public JsonResult queryChartsByChannelNameAndTime(
            @RequestParam(value = "channelName", required = true) String channelName,
            @RequestParam(value = "startTime", required = true) String startTime,
            @RequestParam(value = "endTime", required = true) String endTime

    ) {
        JsonResult result = JsonResult.fromSuccess();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        if (startTime != null && endTime != null) {

            //int count = chartsContentDetectLogService.CountContentDetectLog(channelName, sdf.parse(startTime).getTime(), sdf.parse(endTime).getTime());
            List list = chartsContentDetectLogService.CountLog(startTime, endTime);
            result.put("result", list);

        }

        return result;
    }

}
