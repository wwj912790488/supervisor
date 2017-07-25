package com.arcsoft.supervisor.web.message;

import com.arcsoft.supervisor.commons.SupervisorDefs;
import com.arcsoft.supervisor.commons.json.JsonMapper;
import com.arcsoft.supervisor.exception.message.MessageException;
import com.arcsoft.supervisor.model.domain.message.Message;
import com.arcsoft.supervisor.service.message.MessageService;
import com.arcsoft.supervisor.web.ControllerSupport;
import com.arcsoft.supervisor.web.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


/**
 * @author jt.
 * @author zw
 */
@Controller
@RequestMapping("/msg")
public class MessageController extends ControllerSupport {

    private static final String VIEW_INDEX = "/msg/index";

    @Autowired
    private MessageService messageService;

    @RequestMapping(value = "/index", method = RequestMethod.GET)
    public String getMessages(Model model,@PageableDefault(sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable) {
        model.addAttribute(SupervisorDefs.Constants.PAGER, messageService.paginate((PageRequest) pageable));
        return VIEW_INDEX;
    }

    private String getAddr(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        return ip;
    }

    @RequestMapping(value = "/postMessage")
    public String PostMessage(Model model, Message message, HttpServletRequest request) {
        try {
            String ipAdd = getAddr(request);
            Date datetime = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String strdatetime = sdf.format(datetime);
            messageService.add(message.getUserName(), message.getRealName(), message.getMessage(), strdatetime, ipAdd);
        } catch (MessageException e) {
            e.printStackTrace();
        }
        return "redirect:index";
    }

    @RequestMapping(value = "/delete")
    @ResponseBody
    public JsonResult deleteMessages(@RequestParam(value = "ids", required = false) String ids){
        JsonResult result = JsonResult.fromSuccess();
        try {
            List<Message> msgs = JsonMapper.getMapper().readValue(ids, JsonMapper.getMapper().getTypeFactory().constructCollectionType(List.class, Message.class));
            if (msgs != null) {
                messageService.deleteByIds(msgs);
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.error();
        }
        return result;
    }

    @RequestMapping(value = "/clear", method = RequestMethod.POST)
    @ResponseBody
    public JsonResult clear() {
        JsonResult result = JsonResult.fromSuccess();
        try {
            messageService.clearMessage();
        } catch (Exception e) {
            result.error();
        }
        return result;
    }
}
