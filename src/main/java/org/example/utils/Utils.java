package org.example.utils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {

    //生成系统当前的时间
    public static String generateCurrentTime(){
        // 获取当前时间戳
        long timestamp = System.currentTimeMillis();
        // 将时间戳转换为日期对象
        Date date = new Date(timestamp);
        // 创建格式化器，并指定格式
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
        // 格式化日期对象
        return sdf.format(date);
    }

    //计算使用的时间
    public static String caculateUsingTime(long startTime,long endTime) {
        long runTime=endTime-startTime;
        //转换时分秒
        long hours=0;
        long minutes=0;
        long seconds;
        if(runTime>1000*60*60){
            hours=runTime/(1000*60*60);
            runTime=runTime%(1000*60*60);
        }
        if(runTime>1000*60){
            minutes=runTime/(1000*60);
            runTime=runTime%(1000*60);
        }
        seconds=runTime/1000;
        return hours+"小时"+minutes+"分钟"+seconds+"秒"+'\n';
    }

    //获取.md前的文件名
    public static String extractNameWithoutExtension(String filePath){
        int index=filePath.indexOf(".");
        return filePath.substring(0,index);
    }

    //获取文件路径中的文件名
    public static String extractFileName(String filePath) {
        // 创建一个File对象，以便处理文件路径
        File file = new File(filePath);
        // 使用File对象的getName()方法获取文件名
        String fileName = file.getName();
        return fileName;
    }

}
