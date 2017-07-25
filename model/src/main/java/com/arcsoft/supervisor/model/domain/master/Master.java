package com.arcsoft.supervisor.model.domain.master;

import javax.persistence.*;

/**
 * Created by wwj on 2016/6/30.
 */
@Entity
@Table(name = "master")
public class Master {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Integer id;
    private  String ip;
    @Column(name = "port")
    private  Integer port;
    private  String userName;
    private  String passWord;
    private  Integer flag;
    private  String backupFlag;
    private String backupAdress;

    public Master(){

    }
    public Master( String ip, Integer port,String userName, String passWord, Integer flag, String backupFlag, String backupAdress ) {
        this.ip = ip;
        this.port=port;
        this.userName = userName;
        this.passWord = passWord;
        this.flag = flag;
        this.backupFlag = backupFlag;
        this.backupAdress = backupAdress;
    }



    public Master( String ip,Integer port, String userName, String passWord, Integer flag ) {
        this.ip = ip;
        this.port=port;
        this.userName = userName;
        this.passWord = passWord;
        this.flag = flag;
    }

    public String getBackupAdress() {
        return backupAdress;
    }

    public void setBackupAdress( String backupAdress ) {
        this.backupAdress = backupAdress;
    }

    public String getBackupFlag() {
        return backupFlag;
    }

    public void setBackupFlag( String backupFlag ) {
        this.backupFlag = backupFlag;
    }

    public Integer getFlag() {
        return flag;
    }

    public void setFlag( Integer flag ) {
        this.flag = flag;
    }

    public Integer getId() {
        return id;
    }

    public void setId( Integer id ) {
        this.id = id;
    }

    public String getIp() {
        return ip;
    }

    public void setIp( String ip ) {
        this.ip = ip;
    }

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord( String passWord ) {
        this.passWord = passWord;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName( String userName ) {
        this.userName = userName;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort( Integer port ) {
        this.port = port;
    }
}


