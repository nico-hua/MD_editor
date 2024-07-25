package org.example.observer;

import org.example.utils.Utils;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class HistoryCommandObserver implements CommandObserver, Serializable {
    private List<String> historyCommands;//用于存储历史命令
    private final Integer maxHistoryCommandNum=10;//可存储的最大历史命令条数
    private final String historyFilePath="history.log";

    public HistoryCommandObserver() {
        this.historyCommands=new ArrayList<>();
    }

    @Override
    public void update(String text) throws IOException {//需要传入命令
        String commandStr= Utils.generateCurrentTime()+" "+text+"\n";
        //写入history.log中
        FileWriter writer = new FileWriter(this.historyFilePath, true); // true 表示追加写入
        writer.write(commandStr); // 将字符串写入文件
        writer.close(); // 关闭文件流
        //写入historyCommands中
        if(this.historyCommands.size()==maxHistoryCommandNum){
            historyCommands.remove(0);
            historyCommands.add(commandStr);
        }
        else{
            historyCommands.add(commandStr);
        }
    }

    @Override
    public void sessionStart() throws IOException {
        String str="session start at "+Utils.generateCurrentTime()+"\n";
        historyCommands.add(str);
        FileWriter writer = new FileWriter(this.historyFilePath, true); // true 表示追加写入
        writer.write(str); // 将字符串写入文件
        writer.close(); // 关闭文件流
    }

    public void printHistoryCommands(int n){
        int num=historyCommands.size();
        //大于记录条数则全部打印
        if(n>=num){
            for(int i=historyCommands.size()-1;i>=0;i--){
                System.out.print(historyCommands.get(i));
            }
        }
        else{
            for(int i=historyCommands.size()-1;i>=historyCommands.size()-n;i--){
                System.out.print(historyCommands.get(i));
            }
        }
    }
}
