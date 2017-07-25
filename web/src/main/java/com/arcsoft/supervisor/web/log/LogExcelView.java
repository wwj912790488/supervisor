package com.arcsoft.supervisor.web.log;

import com.arcsoft.supervisor.model.domain.channel.Channel;
import com.arcsoft.supervisor.model.domain.channel.ChannelGroup;
import com.arcsoft.supervisor.model.domain.log.ContentDetectLog;
import com.arcsoft.supervisor.model.domain.log.ServiceLog;
import com.arcsoft.supervisor.model.domain.log.SystemLog;
import com.arcsoft.supervisor.model.vo.task.MediaCheckType;
import com.arcsoft.supervisor.repository.channel.ChannelRepository;
import com.arcsoft.supervisor.service.log.ContentDetectLogService;
import com.arcsoft.supervisor.service.log.ServiceLogService;
import com.arcsoft.supervisor.service.log.SystemLogService;
import com.arcsoft.supervisor.service.log.impl.ContentDetectQueryParams;
import com.arcsoft.supervisor.service.log.impl.ServiceLogQueryParams;
import com.arcsoft.supervisor.service.log.impl.SystemLogQueryParams;
import com.arcsoft.supervisor.thirdparty.charts.ChartsContentDetectLogService;
import com.arcsoft.supervisor.utils.DateHelper;
import com.google.common.base.Function;
import com.google.common.base.Predicates;
import com.google.common.collect.FluentIterable;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.servlet.view.document.AbstractExcelView;

import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

public class LogExcelView extends AbstractExcelView {

    private static final Integer PAGE_SIZE = 10000;

    @Autowired
    private SystemLogService systemLogService;
    @Autowired
    private ContentDetectLogService contentDetectLogService;
    @Autowired
    private ServiceLogService serviceLogService;
    @Autowired
    private ChartsContentDetectLogService chartsContentDetectLogService;
    @Autowired
    private ChannelRepository channelRepository;

    @Override
    protected void buildExcelDocument(Map<String, Object> model,
                                      HSSFWorkbook workbook, HttpServletRequest request,
                                      HttpServletResponse response) throws Exception {
        String type = (String) model.get("type");
        if (type.equals("cd")) {
            ContentDetectQueryParams params = (ContentDetectQueryParams) model.get("q");
            Long size = contentDetectLogService.getCount(params);
            HSSFSheet excelSheet = null;
            for (long i = 0; i * PAGE_SIZE < size; i++) {
                PageRequest pageable = new PageRequest((int) i, PAGE_SIZE, new Sort(Sort.Direction.DESC, "id"));
                Page<ContentDetectLog> logs = contentDetectLogService.paginate(params, pageable);
                excelSheet = workbook.createSheet("Logs" + i);
                setExcelHeader(workbook, excelSheet, type);
                setExcelRows(workbook, excelSheet, logs, type);
            }

        } else if (type.equals("s")) {
            ServiceLogQueryParams params = (ServiceLogQueryParams) model.get("q");
            Long size = serviceLogService.getCount(params);
            for (long i = 0; i * PAGE_SIZE < size; i++) {
                PageRequest pageable = new PageRequest((int) i, PAGE_SIZE, new Sort(Sort.Direction.DESC, "time"));
                Page<ServiceLog> logs = serviceLogService.paginate(params, pageable);
                HSSFSheet excelSheet = workbook.createSheet("Logs" + i);
                setExcelHeader(workbook, excelSheet, type);
                setExcelRows(workbook, excelSheet, logs, type);
            }
        } else if (type.equals("op")) {
            SystemLogQueryParams params = (SystemLogQueryParams) model.get("q");
            Long size = systemLogService.getCount(params);
            for (long i = 0; i * PAGE_SIZE < size; i++) {
                PageRequest pageable = new PageRequest(0, PAGE_SIZE, new Sort(Sort.Direction.DESC, "id"));
                Page<SystemLog> logs = systemLogService.paginate(params, pageable);
                HSSFSheet excelSheet = workbook.createSheet("Logs" + i);
                setExcelHeader(workbook, excelSheet, type);
                setExcelRows(workbook, excelSheet, logs, type);
            }
        } else if (type.equals("chart")) {
            ContentDetectQueryParams params = (ContentDetectQueryParams) model.get("q");
            HSSFSheet excelSheet = null;
            if (params.getStartTime() != null && params.getEndTime() != null) {
                int diff = DateHelper.dateDiff(params.getEndTime(), params.getStartTime());
                if (diff > 0) {
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(params.getStartTime());
                    for (int i = 0; i <= diff; i++) {
                        setGroupByChannel(excelSheet, workbook, DateHelper.createDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH) + i),params);
                    }

                } else if (diff == 0) {
                    setGroupByChannel(excelSheet, workbook, params.getStartTime(),params);
                }
            } else
                setGroupByChannel(excelSheet, workbook, new Date(),params);

            response.setContentType(super.getContentType());
            response.setHeader("Content-disposition", "attachment;filename=" + "ContentChart.xls");

        }

    }

    private void setGroupByChannel(HSSFSheet excelSheet, HSSFWorkbook workbook, Date date,ContentDetectQueryParams q) {
        try {
            excelSheet = workbook.createSheet("频道统计" + DateFormatUtils.format(date, "yyyy-MM-dd"));
            HSSFRow excelHeader = excelSheet.createRow(0);
            excelHeader.createCell(0).setCellValue("频道");
            for (int i = 0; i < 24; i++) {
                excelHeader.createCell(i + 1).setCellValue(i + "时");
            }

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
           // List<Channel> ChannelList = channelRepository.findAll();
            List<HashMap<String, Object>> list1 = null;
            if (ChannelList != null && ChannelList.size() > 0) {
                int[][] num = new int[ChannelList.size()][25];
                for (int i = 0; i < ChannelList.size(); i++) {
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(date);
                    list1 = chartsContentDetectLogService.CountEveryDay(ChannelList.get(i).getName(), cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH));
                    HSSFRow exec = excelSheet.createRow(i + 1);
                    //row 0
                    exec.createCell(0).setCellValue(ChannelList.get(i).getName());
                    int rowSum = 0;
                    for (int j = 0; j < list1.size(); j++) {
                        int cellDouble = Integer.valueOf(list1.get(j).get("count").toString());
                        num[i][j] = cellDouble;
                        rowSum = rowSum + cellDouble;
                        exec.createCell(j + 1).setCellValue(cellDouble);
                        //exec.createCell(i+1).setCellValue((String) list1.get(i).get("count"));
                    }
                    //row sum
                    exec.createCell(list1.size() + 1).setCellValue(rowSum);
                    //add sum
                    num[i][list1.size()] = rowSum;

                }
                //add lastrow
                HSSFRow exec = excelSheet.createRow(ChannelList.size() + 1);
                exec.createCell(0).setCellValue("总和");
                for (int n = 0; n <= list1.size(); n++) {
                    int cellSum = 0;
                    for (int m = 0; m < ChannelList.size(); m++) {
                        cellSum = cellSum + num[m][n];
                    }
                    exec.createCell(n + 1).setCellValue(cellSum);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void setExcelHeader(HSSFWorkbook workbook, HSSFSheet excelSheet, String type) {
        HSSFRow excelHeader = excelSheet.createRow(0);
        if (type.equals("cd")) {
            excelHeader.createCell(0).setCellValue("TaskId");
            excelHeader.createCell(1).setCellValue("ChannelName");
            excelHeader.createCell(2).setCellValue("Type");
            excelHeader.createCell(3).setCellValue("StartTime");
            excelHeader.createCell(4).setCellValue("EndTime");
        } else if (type.equals("s")) {
            excelHeader.createCell(0).setCellValue("Module");
            excelHeader.createCell(1).setCellValue("IP");
            excelHeader.createCell(2).setCellValue("Level");
            excelHeader.createCell(3).setCellValue("Time");
            excelHeader.createCell(4).setCellValue("Discription");
        } else if (type.equals("op")) {
            excelHeader.createCell(0).setCellValue("Id");
            excelHeader.createCell(1).setCellValue("Username");
            excelHeader.createCell(2).setCellValue("Module");
            excelHeader.createCell(3).setCellValue("Operation");
            excelHeader.createCell(4).setCellValue("Time");
            excelHeader.createCell(5).setCellValue("Result");
        }
    }

    private void setExcelRows(HSSFWorkbook workbook, HSSFSheet excelSheet, Iterable<?> logs, String type) {
        int record = 1;
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setDataFormat(
                workbook.getCreationHelper().createDataFormat().getFormat("yyyy-MM-dd HH:mm:ss.SSS"));
        if (type.equals("cd")) {
            for (Object log : logs) {
                ContentDetectLog cdLog = (ContentDetectLog) log;
                HSSFRow excelRow = excelSheet.createRow(record++);
                excelRow.createCell(0).setCellValue(cdLog.getTaskId());
                excelRow.createCell(1).setCellValue(cdLog.getChannelName());
                excelRow.createCell(2).setCellValue(getFriendlyContentDetectTypeName(cdLog.getType()));
                Cell startTime = excelRow.createCell(3);
                startTime.setCellStyle(cellStyle);
                if (cdLog.getStartTimeAsDate() != null) {
                    startTime.setCellValue(cdLog.getStartTimeAsDate());
                }

                Cell endTime = excelRow.createCell(4);
                endTime.setCellStyle(cellStyle);
                if (cdLog.getEndTimeAsDate() != null) {
                    endTime.setCellValue(cdLog.getEndTimeAsDate());
                }

                if (record == 2) {
                    for (int i = 0; i < 5; i++) {
                        excelSheet.autoSizeColumn(i);
                    }
                }
            }
        } else if (type.equals("s")) {
            for (Object log : logs) {
                ServiceLog sLog = (ServiceLog) log;
                HSSFRow excelRow = excelSheet.createRow(record++);
                excelRow.createCell(0).setCellValue(getFriendlyServiceLogModuleName(sLog.getModule()));
                excelRow.createCell(1).setCellValue(sLog.getIp());
                excelRow.createCell(2).setCellValue(getWarningLevel(sLog.getLevel()));
                Cell time = excelRow.createCell(3);
                time.setCellStyle(cellStyle);
                time.setCellValue(sLog.getTime());
                excelRow.createCell(4).setCellValue(sLog.getDescription());
                if (record == 2) {
                    for (int i = 0; i < 5; i++) {
                        excelSheet.autoSizeColumn(i);
                    }
                }
            }
        } else if (type.equals("op")) {
            for (Object log : logs) {
                SystemLog sLog = (SystemLog) log;

                HSSFRow excelRow = excelSheet.createRow(record++);
                excelRow.createCell(0).setCellValue(sLog.getId());
                excelRow.createCell(1).setCellValue(sLog.getUserName());
                excelRow.createCell(2).setCellValue(getFriendlyOperationModuleName(sLog.getFuncType()));
                excelRow.createCell(3).setCellValue(sLog.getOperationInfo());
                Cell time = excelRow.createCell(4);
                time.setCellStyle(cellStyle);
                time.setCellValue(sLog.getRealDateTime());
                excelRow.createCell(5).setCellValue(sLog.getOperationResult());
                if (record == 2) {
                    for (int i = 0; i < 6; i++) {
                        excelSheet.autoSizeColumn(i);
                    }
                }
            }
        }
    }

    private String getFriendlyContentDetectTypeName(int type) {
        switch (type) {
            case MediaCheckType.CHECK_TYPE_BLACK_FIELD_INDEX:
                return "黑场";
            case MediaCheckType.CHECK_TYPE_MUTE_THRESHOLD_INDEX:
                return "静音";
            case MediaCheckType.CHECK_TYPE_BREAK_INDEX:
                return "爆音";
            case MediaCheckType.CHECK_TYPE_STATIC_FRAME_INDEX:
                return "静帧";
            case MediaCheckType.CHECK_TYPE_GREEN_FIELD_INDEX:
                return "绿场";
            case MediaCheckType.SIGNAL_STREAM_CCERROR:
                return "CC错误";
            case MediaCheckType.SIGNAL_STREAM_NOAUDIO:
                return "Audio丢失";
            case MediaCheckType.SIGNAL_STREAM_NOVIDEO:
                return "Video丢失";
            case MediaCheckType.SIGNAL_STREAM_INTERRUPT:
                return "信源中断";

            default:
                return Integer.valueOf(type).toString();
        }
    }

    private String getFriendlyServiceLogModuleName(int module) {
        switch (module) {
            case 0:
                return "频道管理";
            case 1:
                return "画面管理";
            default:
                return Integer.valueOf(module).toString();
        }

    }

    private String getWarningLevel(int level) {
        switch (level) {
            case 9:
                return "警告";
            case 10:
                return "错误";
            default:
                return Integer.valueOf(level).toString();
        }
    }

    private String getFriendlyOperationModuleName(int module) {
        switch (module) {
            case 1:
                return "设备管理";
            case 2:
                return "画面管理";
            case 3:
                return "频道管理";
            case 4:
                return "用户管理";
            case 5:
                return "消息管理";
            case 6:
                return "日志管理";
            default:
                return Integer.valueOf(module).toString();

        }
    }

}
