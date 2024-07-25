package org.example.observer;

import org.example.utils.Utils;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class StatsCommandObserver implements CommandObserver, Serializable {
    private List<String> statsCommands;//用于存储历史操作文件信息
    private Long startTime;//文件load时间
    private final String statsFilePath="stats.log";
    private String filePath;//文件路径
    public StatsCommandObserver() {
        this.filePath="";
        this.statsCommands=new ArrayList<>();
    }
    @Override
    public void update(String text) throws IOException {//需要传入文件路径
        if(this.filePath.equals("")){
            this.filePath=text;
            this.startTime=System.currentTimeMillis();
        } else if (text.equals("close")) {
            String statsStr="./"+filePath+" "+Utils.caculateUsingTime(startTime,System.currentTimeMillis());
            this.statsCommands.add(statsStr);
            FileWriter writer = new FileWriter(this.statsFilePath, true); // true 表示追加写入
            writer.write(statsStr); // 将字符串写入文件
            writer.close(); // 关闭文件流
            this.filePath="";
        } else{
            String statsStr="./"+filePath+" "+Utils.caculateUsingTime(startTime,System.currentTimeMillis());
            this.statsCommands.add(statsStr);
            FileWriter writer = new FileWriter(this.statsFilePath, true); // true 表示追加写入
            writer.write(statsStr); // 将字符串写入文件
            writer.close(); // 关闭文件流
            this.startTime=System.currentTimeMillis();
            this.filePath=text;
        }
    }

    @Override
    public void sessionStart() throws IOException {
        String str="session start at "+ Utils.generateCurrentTime()+"\n";
        statsCommands.add(str);
        FileWriter writer = new FileWriter(this.statsFilePath, true); // true 表示追加写入
        writer.write(str); // 将字符串写入文件
        writer.close(); // 关闭文件流
    }

    public void printStats(String filePath){
        if(filePath.equals("")){//打印全部
            for (String statsCommand : this.statsCommands) {
                System.out.print(statsCommand);
            }
        }
        else {//打印当前
            Long currentTime=System.currentTimeMillis();
            System.out.print("./"+filePath+" "+Utils.caculateUsingTime(startTime,currentTime));
        }
    }
}
