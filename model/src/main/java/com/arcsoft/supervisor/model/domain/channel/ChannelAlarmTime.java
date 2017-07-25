package com.arcsoft.supervisor.model.domain.channel;

import javax.persistence.*;

/**
 * Created by wwj on 2017/5/3.
 */
@Entity
@Table(name = "Channel_alarm_time")
public class ChannelAlarmTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    //add alarmTime
    private boolean enableTime1 = false;
    private String alarmStartTime1;
    private String alarmEndTime1;

    private boolean enableTime2 = false;
    private String alarmStartTime2;
    private String alarmEndTime2;

    private boolean enableTime3 = false;
    private String alarmStartTime3;
    private String alarmEndTime3;

    private boolean enableTime4 = false;
    private String alarmStartTime4;
    private String alarmEndTime4;

    private boolean enableTime5 = false;
    private String alarmStartTime5;
    private String alarmEndTime5;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAlarmStartTime1() {
        return alarmStartTime1;
    }

    public void setAlarmStartTime1(String alarmStartTime1) {
        this.alarmStartTime1 = alarmStartTime1;
    }

    public String getAlarmEndTime1() {
        return alarmEndTime1;
    }

    public void setAlarmEndTime1(String alarmEndTime1) {
        this.alarmEndTime1 = alarmEndTime1;
    }

    public String getAlarmStartTime2() {
        return alarmStartTime2;
    }

    public void setAlarmStartTime2(String alarmStartTime2) {
        this.alarmStartTime2 = alarmStartTime2;
    }

    public String getAlarmEndTime2() {
        return alarmEndTime2;
    }

    public void setAlarmEndTime2(String alarmEndTime2) {
        this.alarmEndTime2 = alarmEndTime2;
    }

    public String getAlarmStartTime3() {
        return alarmStartTime3;
    }

    public void setAlarmStartTime3(String alarmStartTime3) {
        this.alarmStartTime3 = alarmStartTime3;
    }

    public String getAlarmEndTime3() {
        return alarmEndTime3;
    }

    public void setAlarmEndTime3(String alarmEndTime3) {
        this.alarmEndTime3 = alarmEndTime3;
    }

    public boolean isEnableTime1() {
        return enableTime1;
    }

    public void setEnableTime1(boolean enableTime1) {
        this.enableTime1 = enableTime1;
    }

    public boolean isEnableTime2() {
        return enableTime2;
    }

    public void setEnableTime2(boolean enableTime2) {
        this.enableTime2 = enableTime2;
    }

    public boolean isEnableTime3() {
        return enableTime3;
    }

    public void setEnableTime3(boolean enableTime3) {
        this.enableTime3 = enableTime3;
    }


    public boolean isEnableTime4() {
        return enableTime4;
    }

    public void setEnableTime4(boolean enableTime4) {
        this.enableTime4 = enableTime4;
    }

    public String getAlarmStartTime4() {
        return alarmStartTime4;
    }

    public void setAlarmStartTime4(String alarmStartTime4) {
        this.alarmStartTime4 = alarmStartTime4;
    }

    public String getAlarmEndTime4() {
        return alarmEndTime4;
    }

    public void setAlarmEndTime4(String alarmEndTime4) {
        this.alarmEndTime4 = alarmEndTime4;
    }

    public boolean isEnableTime5() {
        return enableTime5;
    }

    public void setEnableTime5(boolean enableTime5) {
        this.enableTime5 = enableTime5;
    }

    public String getAlarmStartTime5() {
        return alarmStartTime5;
    }

    public void setAlarmStartTime5(String alarmStartTime5) {
        this.alarmStartTime5 = alarmStartTime5;
    }

    public String getAlarmEndTime5() {
        return alarmEndTime5;
    }

    public void setAlarmEndTime5(String alarmEndTime5) {
        this.alarmEndTime5 = alarmEndTime5;
    }
}
