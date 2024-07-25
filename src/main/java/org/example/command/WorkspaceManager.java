package org.example.command;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class WorkspaceManager implements Serializable{
    private Map<String, CommandManager> workspaceMap=new HashMap<>();//用于映射文件路径与不同的workspace
    private final String workspaceFilePath="workspace.txt";//存储workspaceMap的文件路径

    public WorkspaceManager(){
        loadWorkSpaceDataFromFile();
    }

    public Map<String, CommandManager> getWorkspaceMap() {
        return workspaceMap;
    }

    public void setWorkspaceMap(Map<String, CommandManager> workspaceMap) {
        this.workspaceMap = workspaceMap;
    }

    //从硬盘中加载workspace的数据
    public void loadWorkSpaceDataFromFile(){
        File file=new File(workspaceFilePath);
        if(file.length()!=0){
            try {
                FileInputStream fileInputStream = new FileInputStream(workspaceFilePath);
                ObjectInputStream inputStream = new ObjectInputStream(fileInputStream);
                this.workspaceMap = (Map<String, CommandManager>) inputStream.readObject();
                inputStream.close();
                fileInputStream.close();
            }catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    //将workspace的数据保存到硬盘中
    public void saveWorkSpaceDataToFile(){
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(workspaceFilePath,false);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(workspaceMap);
            objectOutputStream.close();
            fileOutputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
