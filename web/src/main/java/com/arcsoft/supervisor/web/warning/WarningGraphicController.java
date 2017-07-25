package com.arcsoft.supervisor.web.warning;

import com.arcsoft.supervisor.commons.json.JsonMapper;
import com.arcsoft.supervisor.model.domain.channel.ChannelGroup;
import com.arcsoft.supervisor.model.domain.channel.ChannelTag;
import com.arcsoft.supervisor.model.domain.log.ContentDetectLog;
import com.arcsoft.supervisor.repository.channel.ChannelTagRepository;
import com.arcsoft.supervisor.service.channel.ChannelGroupService;
import com.arcsoft.supervisor.service.channel.ChannelQueryParams;
import com.arcsoft.supervisor.service.channel.ChannelService;
import com.arcsoft.supervisor.service.log.ContentDetectLogService;
import com.arcsoft.supervisor.service.log.impl.ChannelsContentDetectQueryParams;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Nullable;
import java.util.*;

import static com.arcsoft.supervisor.commons.SupervisorDefs.Constants.PAGER;
import static com.arcsoft.supervisor.commons.SupervisorDefs.Constants.QUERY_PARAMS;

@Controller
@RequestMapping("/warning/graphic")
public class WarningGraphicController {

    private static final String VIEW_INDEX="/warning/warning-graphic";

    @Autowired
    private ChannelService channelService;

    @Autowired
    private ChannelTagRepository channelTagRepository;

    @Autowired
    private ChannelGroupService channelGroupService;

    @Autowired
    private ContentDetectLogService contentDetectLogService;

    @RequestMapping()
    public String index(Model model, ChannelQueryParams q, Pageable pageable) throws Exception {
        if(q.getTypes().isEmpty()) {
            q.setTypes(Arrays.asList(0, 2, 27, 28, 29, 30, 32, 33, 34));
        }
        List<ChannelGroup> groups = channelGroupService.listAll();
        List<Integer> groupList = new ArrayList<>();
        if(q.getGroups().isEmpty()) {
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
//        List<ChannelTag> tags = channelTagRepository.findChannelsNotEmpty();
//        if(q.getTags().isEmpty()) {
//            List<Integer> queryTags = FluentIterable.from(tags).transform(new Function<ChannelTag, Integer>() {
//                @Nullable
//                @Override
//                public Integer apply(@Nullable ChannelTag tag) {
//                    return tag.getId();
//                }
//            }).toList();
//            q.setTags(queryTags);
//        }
        model.addAttribute(PAGER, channelService.paginate(q, (PageRequest) pageable));
        model.addAttribute(QUERY_PARAMS, q);
        model.addAttribute("groups", groups);
        model.addAttribute("types", JsonMapper.getMapper().writeValueAsString(q.getTypes()));
        //model.addAttribute("tags", tags);
        return VIEW_INDEX;
    }

    @RequestMapping(value = "/channels" , method=RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> getChannelsContentDetectLog(@RequestBody ChannelsContentDetectQueryParams params) {
        LocalDateTime start = new LocalDateTime(params.getStartTime(), DateTimeZone.forTimeZone(TimeZone.getDefault()));
        LocalDateTime end = new LocalDateTime(params.getEndTime(), DateTimeZone.forTimeZone(TimeZone.getDefault()));
        Date startTime = start.toDate(TimeZone.getDefault());
        Date endTime = end.toDate(TimeZone.getDefault());
        params.setStartTime(startTime);
        params.setEndTime(endTime);
        List<ContentDetectLog> logs = contentDetectLogService.findAll(params);
        Map<Integer, ArrayList<ContentDetectLog>> channelLogMap = new HashMap<>();
        for(Integer channelId : params.getChannelIds()) {
            channelLogMap.put(channelId, new ArrayList<ContentDetectLog>());
        }
        for(ContentDetectLog log: logs) {
            ArrayList<ContentDetectLog> channelLogs = channelLogMap.get(log.getChannelId());
            channelLogs.add(log);
        }
        Map<String, Object> results = new HashMap<>();
        results.put("dataset", channelLogMap);
        results.put("start", startTime);
        results.put("end", endTime);
        return results;
    }
}
