package com.arcsoft.supervisor.web.master;


import com.arcsoft.supervisor.model.domain.master.Master;
import com.arcsoft.supervisor.model.domain.server.Server;
import com.arcsoft.supervisor.repository.master.MasterRepository;
import com.arcsoft.supervisor.repository.master.MasterRepositoryFlag;
import com.arcsoft.supervisor.repository.master.impl.MasterRepositoryImpl;
import com.arcsoft.supervisor.service.server.ServerService;
import com.arcsoft.supervisor.utils.ShellLinux;
import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wwj on 2016/6/28.
 */

@Controller
@RequestMapping("/master")
public class MasterController {
    @Autowired
    MasterRepository masterDao;
    @Autowired
    MasterRepositoryFlag masterFlag;
    @Autowired
    ServerService serverDao;

    private static final String VIEW_MASTER = "/master/master";
    private static String masterstatus = "CONNECT_MYSQL=\"mysql -h localhost -uroot -proot \";SQL=\"show master status;\";echo \"${SQL}\" | ${CONNECT_MYSQL}";
    // private static String masterMyCnf = "cat <<-EOF > /etc/my.cnf;\n[mysqld]\nlog-bin=mysql-bin\nserver-id=1\nbinlog-do-db=supervisordb\nbinlog-ignore-db=mysql\nEOF";
    //private static String backupMycnf = "cat <<-EOF > /etc/my.cnf;\n[mysqld]\nlog-bin=mysql-bin\nserver-id=226\nreplicate-wild-ignore-table=supervisordb.server%\nreplicate-wild-ignore-table=supervisordb.task%\nreplicate-ignore-db=mysql\nEOF";
    private static String backupsqlon = "CONNECT_MYSQL=\"mysql -h localhost -uroot -proot \";SQL=\"start slave;\";echo \"${SQL}\" | ${CONNECT_MYSQL}";
    private static String backupsqlclose = "CONNECT_MYSQL=\"mysql -h localhost -uroot -proot \";SQL=\"stop slave;\";echo \"${SQL}\" | ${CONNECT_MYSQL}";

    @RequestMapping(value = "/master", method = RequestMethod.GET)
    @ResponseBody
    public ModelAndView showmaster() {
        List<Master> list = masterDao.findAll();
        Map<String, Object> model = new LinkedHashMap<String, Object>();
        model.put("resultList", list);
        return new ModelAndView(VIEW_MASTER, model);
    }

    @RequestMapping(value = "/index", method = RequestMethod.GET)
    @ResponseBody
    public List<Master> index() {
        System.out.println("master/index");
        List<Master> list = masterDao.findAll();
        return list;
    }


    @RequestMapping(value = "/delete", method = RequestMethod.GET)
    public String delete(@RequestParam String masterId) {
        JSONArray results = JSONArray.fromObject(masterId);
        for (int i = 0; i < results.size(); i++) {
            JSONObject result = results.getJSONObject(i);
            String id = result.getString("id");
            System.out.println("delete/" + id);
            Master m = masterFlag.findById(Integer.valueOf(id));
            masterFlag.delete(Integer.valueOf(id));
            ShellLinux tool = new ShellLinux(m.getIp(), m.getPort(), m.getUserName(),
                    m.getPassWord(), "utf-8");
            tool.exec(backupsqlclose);

        }
        return "redirect:/master/master";

    }

    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @ResponseBody
    public HashMap save(@RequestParam String ip, @RequestParam Integer port, @RequestParam String username, @RequestParam String password, @RequestParam String flag) {
        {
            //判断传过来的参数，主的话 执行masterstatus显示标识，存入数据库 ，执行脚本先设置主
            //判断传过来的参数，从的话 执行存入数据库，取主的标识 ，存入数据库，执行脚本
            HashMap map = new HashMap();
            map.put("status", true);
            map.put("msg", "安装成功!");
            int isMaster = Integer.valueOf(flag);
            ShellLinux tool = new ShellLinux(ip, port, username,
                    password, "utf-8");
            if (tool.login()) {
                if (isMaster == 1) {  //1是
                    String masterstat = tool.exec(masterstatus);
                    String backupFlag = masterstat.split("\\n")[1].split("\\s")[0];
                    String backupAdress = masterstat.split("\\n")[1].split("\\s")[1];
                    Master m = new Master(ip, port, username, password, isMaster, backupFlag, backupAdress);
                    masterDao.save(m);
                } else {

                    Master backup = new Master(ip, port, username, password, isMaster);
                    masterDao.save(backup);
                    //return "redirect:/master/master";

                }
            } else {
                map.put("status", false);
                map.put("msg", "服务器连接失败，请检查参数!");
                return map;
            }
            return map;
        }

    }


    @RequestMapping(value = "/doshell", method = RequestMethod.GET)
    @ResponseBody
    public void dosehll(@RequestParam String masterId) {
        //判断传过来的参数，主的话 执行masterstatus显示标识，存入数据库 ，执行脚本先设置主
        //判断传过来的参数，从的话 执行存入数据库，取主的标识 ，存入数据库，执行脚本
        //查询
        JSONArray results = JSONArray.fromObject(masterId);
        for (int i = 0; i < results.size(); i++) {
            JSONObject result = results.getJSONObject(i);
            String id = result.getString("id");
            System.out.println("doshell/" + id);
            Master m = masterFlag.findById(Integer.valueOf(id));
            int isMaster = Integer.valueOf(m.getFlag());
            if (isMaster == 1) {  //1是
                /*ShellLinux tool = new ShellLinux(m.getIp(), m.getUserName(),
                        m.getPassWord(), "utf-8");
                tool.exec(masterMyCnf);   //执行脚本*/

            } else {
                ShellLinux tool = new ShellLinux(m.getIp(), m.getPort(), m.getUserName(),
                        m.getPassWord(), "utf-8");
                Master master = masterFlag.findByFlag();
                String backupsql = "CONNECT_MYSQL=\"mysql -h localhost -uroot -proot \";SQL=\"change master to master_host='" + master.getIp() + "',master_user='root',master_password='root', master_log_file='" + master.getBackupFlag() + "',master_log_pos=" + master.getBackupAdress() + ";\";echo \"${SQL}\" | ${CONNECT_MYSQL}";
                tool.exec(backupsqlclose);
               /* tool.exec(backupMycnf);*/
                tool.exec(backupsql);
                tool.exec(backupsqlon);
                //判断是否备服，是的话ping主服，  //  /task/screen/start
                //Server server= serverDao.getServerName("247");


            }
        }
    }
}
