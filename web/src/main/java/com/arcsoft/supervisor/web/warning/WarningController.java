package com.arcsoft.supervisor.web.warning;

import com.arcsoft.supervisor.commons.profile.Production;
import com.arcsoft.supervisor.model.domain.system.MailSenderInfoConfiguration;
import com.arcsoft.supervisor.model.domain.system.ReportWarningConfiguration;
import com.arcsoft.supervisor.model.domain.system.WarningPushConfiguration;
import com.arcsoft.supervisor.model.domain.user.User;
import com.arcsoft.supervisor.service.device.RemoteShellExecutorService;
import com.arcsoft.supervisor.service.settings.impl.EmailWarningConfigurationService;
import com.arcsoft.supervisor.service.settings.impl.ReportWarningConfigurationService;
import com.arcsoft.supervisor.service.settings.impl.SmsWarningConfigurationService;
import com.arcsoft.supervisor.service.settings.impl.WarningPushConfigurationService;
import com.arcsoft.supervisor.service.user.ProductionUserService;
import com.arcsoft.supervisor.thirdparty.email.SendMail;
import com.arcsoft.supervisor.utils.app.Environment;
import com.arcsoft.supervisor.web.ControllerSupport;
import com.arcsoft.supervisor.web.JsonResult;
import com.arcsoft.supervisor.web.settings.SmsCfg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

/**
 * Controller class for warning settings.
 *
 * @author zw.
 */
@Controller
@RequestMapping("/warning")
@Production
public class WarningController extends ControllerSupport {

    public static final String VIEW_SMS_INDEX = "/warning/warning-sms";

    public static final String VIEW_PUSH_INDEX = "/warning/warning-push";

    public static final String VIEW_REPORT_INDEX = "/warning/warning-report";

    public static final String VIEW_EMAIL_INDEX = "/warning/warning-email";

    private final WarningPushConfigurationService warningPushConfigurationService;

    private final SmsWarningConfigurationService smsWarningConfigurationService;

    private final ReportWarningConfigurationService reportWarningConfigurationService;

    private final EmailWarningConfigurationService emailWarningConfigurationService;

    private final ProductionUserService userService;

    private final RemoteShellExecutorService remoteShellExecutorService;

    @Autowired
    public WarningController(
            WarningPushConfigurationService warningPushConfigurationService,
            SmsWarningConfigurationService smsWarningConfigurationService,
            ReportWarningConfigurationService reportWarningConfigurationService,
            ProductionUserService userService, EmailWarningConfigurationService emailService,RemoteShellExecutorService remoteShellExecutorService) {
        this.warningPushConfigurationService = warningPushConfigurationService;
        this.smsWarningConfigurationService = smsWarningConfigurationService;
        this.reportWarningConfigurationService = reportWarningConfigurationService;
        this.userService = userService;
        this.emailWarningConfigurationService = emailService;
        this.remoteShellExecutorService=remoteShellExecutorService;
    }

    @RequestMapping(value = "/downVoice", produces = "application/json;charset=UTF-8")
    public void getAliveServers(HttpServletRequest request, HttpServletResponse response) {
        File file = new File("/home/backup/voicealarm/VASetup.exe");
        if (file != null && file.exists()) {
            try {
                remoteShellExecutorService.readFileContent(request, response, file);
            } catch (IOException e) {
                e.printStackTrace();

            }
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

    @RequestMapping(method = RequestMethod.GET, value = {"/sms", "/index"})
    public String toSmsIndex(Model model, HttpServletRequest request) {
        User user = getUserFromSession(request.getSession(false));
        if (user != null) {
            User persistUser = userService.findUser(user.getId());
            if (persistUser != null) {
                model.addAttribute("phoneNumber", persistUser.getPhoneNumber());
            }
        }
        model.addAttribute("cfg", smsWarningConfigurationService.find());
        return VIEW_SMS_INDEX;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/push")
    public String toPushIndex(Model model) {
        model.addAttribute("cfg", warningPushConfigurationService.find());
        return VIEW_PUSH_INDEX;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/report")
    public String toReportIndex(Model model) {
        model.addAttribute("cfg", reportWarningConfigurationService.find());
        return VIEW_REPORT_INDEX;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/email")
    public String toEmailIndex(Model model) {
        model.addAttribute("cfg", emailWarningConfigurationService.find());
        return VIEW_EMAIL_INDEX;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/saveEmailCfg")
    @ResponseBody
    public JsonResult saveEmailCfg(@RequestBody MailSenderInfoConfiguration cfg) {
        emailWarningConfigurationService.saveOrUpdate(cfg);
        return JsonResult.fromSuccess();
    }

    @RequestMapping(method = RequestMethod.POST, value = "/sendTestMail")
    @ResponseBody
    public JsonResult sendTestMail(@ModelAttribute MailSenderInfoConfiguration cfg) {
        MailSenderInfoConfiguration mailInfo = new MailSenderInfoConfiguration();
        mailInfo.setMailServerHost(cfg.getMailServerHost());
        mailInfo.setMailServerPort(cfg.getMailServerPort());
        mailInfo.setValidate(true);
        mailInfo.setUserName(cfg.getUserName());
        mailInfo.setPassword(cfg.getPassword());//邮箱密码
        mailInfo.setFromAddress(cfg.getUserName());
        mailInfo.setToAddress(cfg.getToAddress());
        mailInfo.setSubject("This is a Test Email from Supervisor");
        mailInfo.setContent("Test alarm from Supervisor!");
        //这个类主要来发送邮件
        SendMail sms = new SendMail();
        try {
            boolean flag = sms.sendMessage(mailInfo);//是否ssl
            if (flag) {
                return JsonResult.fromSuccess();
            }
            return JsonResult.fromError();
        } catch (MessagingException e) {
            e.printStackTrace();
            return JsonResult.fromError();
        }

    }


    @RequestMapping(method = RequestMethod.POST, value = "/saveSmsCfg")
    @ResponseBody
    public JsonResult saveSmsCfg(@RequestBody SmsCfg cfg, HttpServletRequest request) {
        User user = getUserFromSession(request.getSession(false));
        if (user != null) {
            smsWarningConfigurationService.saveOrUpdateSmsConfigurationAndUserPhoneNumber(
                    cfg.getSmsCfg(),
                    user.getId(),
                    cfg.getPhoneNumber()
            );
        } else {
            return JsonResult.fromError();
        }
        return JsonResult.fromSuccess();
    }

    @RequestMapping(method = RequestMethod.POST, value = "/saveWarningPushCfg")
    @ResponseBody
    public JsonResult saveWarningPushCfg(@RequestBody WarningPushConfiguration cfg) {
        warningPushConfigurationService.saveOrUpdate(cfg);
        return JsonResult.fromSuccess();
    }


    @RequestMapping(method = RequestMethod.POST, value = "/saveReportCfg")
    @ResponseBody
    public JsonResult saveWarningPushCfg(@RequestBody ReportWarningConfiguration cfg) {
        reportWarningConfigurationService.saveOrUpdateReport(cfg);
        return JsonResult.fromSuccess();
    }

}
