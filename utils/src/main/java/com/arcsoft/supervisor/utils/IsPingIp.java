package com.arcsoft.supervisor.utils;


import java.io.IOException;
import java.net.InetAddress;

/**
 * Created by wwj on 2016/6/21.
 */
public  class  IsPingIp  {
    private static final int timeOut = 3000; //超时应该在3钞以上
    private  String ip;

     IsPingIp(String ip){

        this.ip=ip;
    }
    public static synchronized boolean isPing(String ip)
    {
        boolean status = false;
        if(ip != null)
        {

            try {
                status = InetAddress.getByName(ip).isReachable(timeOut);
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println(status+ip);



        }
        return status;
    }

//    public static void main(String[] args){
//        Thread  t1= new Thread(new IsPingIp("172.28.100.22"));
//        Thread t2= new Thread(new IsPingIp("172.28.100.171"));
//        t1.start();
//        t2.start();
//    }
//
//    @Override
//    public   void run() {
//        while (true) {
//            try {
//                Thread.sleep(timeOut);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            isPing(ip);
//        }
//    }
}
