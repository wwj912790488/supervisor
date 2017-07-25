package com.arcsoft.supervisor.utils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;

public class ShellLinux {
    private static String masterstatus = "CONNECT_MYSQL=\"mysql -h localhost -uroot -proot \";SQL=\"show master status;\";echo \"${SQL}\" | ${CONNECT_MYSQL}";
    private static  String master="cat <<-EOF > my.cnf;[mysqld]\nlog-bin=mysql-bin\nserver-id=1\nbinlog-do-db=supervisordb\nbinlog-ignore-db=mysql\nEOF";
    private static String backup="cat <<-EOF > my.cnf;[mysqld]\nlog-bin=mysql-bin\nserver-id=226\nreplicate-wild-ignore-table=supervisordb.server% \nreplicate-ignore-db=mysql\nEOF";
    private static String backupsqlon="CONNECT_MYSQL=\"mysql -h localhost -uroot -proot \";SQL=\"start slave;\";echo \"${SQL}\" | ${CONNECT_MYSQL}";
    private static String backupsqlclose="CONNECT_MYSQL=\"mysql -h localhost -uroot -proot \";SQL=\"stop slave;\";echo \"${SQL}\" | ${CONNECT_MYSQL}";
    private static String backupsql="CONNECT_MYSQL=\"mysql -h localhost -uroot -proot \";SQL=\"change master to master_host='172.28.100.67',master_user='wwj',master_password='123456', master_log_file='mysql-bin.000008',master_log_pos=680;\";echo \"${SQL}\" | ${CONNECT_MYSQL}";
    private static String restartMaster="/etc/init.d/mysql restart";
    private Connection conn;
    private String ipAddr;
    private String charset = Charset.defaultCharset().toString();
    private String userName;
    private String password;
    private int port;
    public ShellLinux(String ipAddr,int port ,String userName, String password,
                           String charset) {
        this.ipAddr = ipAddr;
        this.userName = userName;
        this.password = password;
        this.port=port;
        if (charset != null) {
            this.charset = charset;
        }
    }

    public boolean login()  {
        conn = new Connection(ipAddr,port);
        try {
            conn.connect(); // 连接
            return conn.authenticateWithPassword(userName, password); // 认证
        } catch (IOException e) {
            e.printStackTrace();
            return  false;
        }

    }

    public String exec(String cmds) {
        InputStream in = null;
        String result = "";
        try {
            if (this.login()) {
                Session session = conn.openSession(); // 打开一个会话
                session.execCommand(cmds);

                in = session.getStdout();
                result = this.processStdout(in, this.charset);
                session.close();
                conn.close();
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return result;
    }

    public String processStdout(InputStream in, String charset) {

        byte[] buf = new byte[1024];
        StringBuffer sb = new StringBuffer();
        try {
            while (in.read(buf) != -1) {
                sb.append(new String(buf, charset));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    /**
     * @param args
     */
    public static void main(String[] args) {

        ShellLinux tool = new ShellLinux("172.17.230.47",22, "root",
                "master007", "utf-8");

        System.out.println(tool.login());
          //String masterMyCnf = "cat <<-EOF > /etc/my.cnf;\n[mysqld]\nlog-bin=mysql-bin\nserver-id=1\nbinlog-do-db=supervisordb\nbinlog-ignore-db=mysql\nEOF";

        /*String result = tool.exec(backupsqlclose);
        String result2 = tool.exec(backupsql);
        String result3 = tool.exec(backupsqlon);*/
        //String masterstat = tool.exec(masterstatus);
       /* String backupFlag = masterstat.split("\\n")[1].split("\\s")[0];
        String backupAdress = masterstat.split("\\n")[1].split("\\s")[1];*/
       // tool.exec(restartMaster);
//        String master=tool.exec(masterstatus);
      //  String backupFlag =master.split("\\n")[1].split("\\s")[0];
      //  String backupAdress=master.split("\\n")[1].split("\\s")[1];
       // System.out.println(backupFlag);
        //System.out.println(masterstat);


    }


}
