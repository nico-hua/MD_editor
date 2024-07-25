package org.example.utils;

import org.example.command.CommandManager;
import org.example.command.WorkspaceManager;
import org.example.composite.FileComponent;
import org.example.composite.FileComposite;
import org.example.composite.FileLeaf;
import org.example.observer.HistoryCommandObserver;
import org.example.observer.StatsCommandObserver;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Terminal {
    private WorkspaceManager workspaceManager;//workspace管理者
    private HistoryCommandObserver historyCommandObserver;//历史命令观察者
    private StatsCommandObserver statsCommandObserver;//文件时长观察者
    private CommandManager currentCommandManager;//当前活跃的工作区
    private static final String commandManagerPath="commandManager.txt";//保存当前活跃的工作区数据的文件
    private static final String historyPath="history.txt";//保存历史命令观察者数据的文件
    private static final String statsPath="stats.txt";//保存文件时长观察者数据的文件
    private static boolean shouldExit = false; // 添加一个标志变量
    public Terminal() throws IOException {
        workspaceManager=new WorkspaceManager();
        historyCommandObserver=loadHistoryData();
        statsCommandObserver=loadStatsData();
        historyCommandObserver.sessionStart();
        statsCommandObserver.sessionStart();
        currentCommandManager=loadCommandManagerData();
        currentCommandManager.setHistoryCommandObserver(historyCommandObserver);
        currentCommandManager.setStatsCommandObserver(statsCommandObserver);
        if(!currentCommandManager.getFilePath().equals("")){
            currentCommandManager.notifyStatsCommandObserver(currentCommandManager.getFilePath());
        }
    }
    public void run() throws IOException {
        //读取输入的指令
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String line;
        while (!shouldExit) {
            System.out.print("$ ");
            line = reader.readLine();
            if (line.trim().isEmpty()) {
                continue;
            }
            parseCommand(line);
        }
    }


    public void parseCommand(String line) throws IOException {
        String[] parts=line.split(" ",2);
        String commandType=parts[0];
        switch (commandType){
            case "load":
                if(parts.length==2){
                    load(line);
                    currentCommandManager.notifyHistoryCommandObserver(line);
                    currentCommandManager.notifyStatsCommandObserver(parts[1]);
                }else {
                    System.out.println("Invalid load command format.");
                }
                break;
            case "list-workspace":
                if(parts.length==1){
                    listworkspace();
                    currentCommandManager.notifyHistoryCommandObserver(line);
                }else {
                    System.out.println("Invalid list-workspace command format.");
                }
                break;
            case "open":
                if(parts.length==2){
                    open(parts[1]);
                    currentCommandManager.notifyHistoryCommandObserver(line);
                    currentCommandManager.notifyStatsCommandObserver(parts[1]+".md");
                }else {
                    System.out.println("Invalid open command format.");
                }
                break;
            case "close":
                if(parts.length==1){
                    close();
                    currentCommandManager.notifyHistoryCommandObserver(line);
                    currentCommandManager.notifyStatsCommandObserver("close");
                }else {
                    System.out.println("Invalid close command format.");
                }
                break;
            case "exit":
                if(parts.length==1){
                    exit();
                    currentCommandManager.notifyHistoryCommandObserver(line);
                    currentCommandManager.notifyStatsCommandObserver("close");
                    shouldExit=true;
                }else {
                    System.out.println("Invalid exit command format.");
                }
                break;
            case "ls":
                if(parts.length==1){
                    ls();
                    currentCommandManager.notifyHistoryCommandObserver(line);
                }else {
                    System.out.println("Invalid ls command format.");
                }
                break;
            default:
                if(!currentCommandManager.getFilePath().equals("")||commandType.equals("history")||commandType.equals("stats")){
                    currentCommandManager.parseCommand(line);
                }
                else {
                    System.out.println("There are currently no active workspaces.");
                }
                break;
        }
    }

    //获取当前工作目录下所有的md文件的路径
    public List<String> listMdFilesInDirectory(File directory, File baseDirectory) {
        List<String> mdFilePaths = new ArrayList<>();
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    // 递归处理子目录
                    mdFilePaths.addAll(listMdFilesInDirectory(file, baseDirectory));
                } else if (file.getName().endsWith(".md")) {
                    // 如果是 .md 文件，获取相对路径
                    String relativePath = baseDirectory.toURI().relativize(file.toURI()).getPath();
                    mdFilePaths.add(relativePath);
                }
            }
        }
        return mdFilePaths;
    }

    public void load(String line) throws IOException {
        String[] parts=line.split(" ",2);
        //如果不为空，说明不是第一次load
        if(!currentCommandManager.getFilePath().equals("")){
            String filePath=currentCommandManager.getFilePath();
            currentCommandManager.setActive(false);
            workspaceManager.getWorkspaceMap().put(filePath,currentCommandManager);
        }
        CommandManager commandManager=new CommandManager();
        commandManager.setHistoryCommandObserver(historyCommandObserver);
        commandManager.setStatsCommandObserver(statsCommandObserver);
        commandManager.parseCommand(line);
        currentCommandManager=commandManager;
        String currentFilePath=parts[1];
        workspaceManager.getWorkspaceMap().put(currentFilePath,currentCommandManager);
    }

    public void listworkspace(){
        String currentFilePath=currentCommandManager.getFilePath();
        if(!currentFilePath.equals("")){
            workspaceManager.getWorkspaceMap().put(currentFilePath,currentCommandManager);
        }
        Map<String,CommandManager> workspaceMap=workspaceManager.getWorkspaceMap();
        workspaceMap.forEach((filePath, commandManager) -> {
            if(commandManager.isActive()){
                System.out.print("->");
            }
            else {
                System.out.print("  ");
            }
            System.out.print(Utils.extractNameWithoutExtension(filePath));
            if(!commandManager.isSaved()){
                System.out.print(" *");
            }
            System.out.println();
        });
    }

    public void open(String filePathToOpen){
        Map<String,CommandManager> workspaceMap=workspaceManager.getWorkspaceMap();
        boolean isFound=false;
        for(Map.Entry<String,CommandManager> entry: workspaceMap.entrySet()){
            String filePath=entry.getKey();
            if(Utils.extractNameWithoutExtension(filePath).equals(filePathToOpen)){
                isFound=true;
                //判断当前是否有活跃的工作区，如果有需要保存
                String currentFilePath= currentCommandManager.getFilePath();
                if(!currentFilePath.equals("")){
                    currentCommandManager.setActive(false);
                    workspaceManager.getWorkspaceMap().put(currentFilePath,currentCommandManager);
                }
                CommandManager commandManager=entry.getValue();
                commandManager.setActive(true);
                commandManager.setHistoryCommandObserver(historyCommandObserver);
                commandManager.setStatsCommandObserver(statsCommandObserver);
                currentCommandManager=commandManager;
            }
        }
        if(!isFound){
            System.out.println("Filepath "+filePathToOpen+" not founded");
        }
    }


    public void close() throws IOException {
        if(!currentCommandManager.getFilePath().equals("")){
            String filePath=currentCommandManager.getFilePath();
            //当前工作区未保存
            if(!currentCommandManager.isSaved()){
                System.out.println("Do you want to save the current workspace [Y\\N] ?");
                Scanner scanner = new Scanner(System.in);
                while (true){
                    String userInput=scanner.next();
                    if(userInput.equalsIgnoreCase("Y")){
                        currentCommandManager.parseCommand("save");
                        currentCommandManager.setFilePath("");
                        workspaceManager.getWorkspaceMap().remove(filePath);
                        break;
                    } else if (userInput.equalsIgnoreCase("N")) {
                        currentCommandManager.setFilePath("");
                        workspaceManager.getWorkspaceMap().remove(filePath);
                        break;
                    }else {
                        System.out.println("Invalid input. Please enter again [Y\\N] .");
                    }
                }
            }
            //已保存
            else {
                currentCommandManager.setFilePath("");
                workspaceManager.getWorkspaceMap().remove(filePath);
            }
        }
    }

    public void exit(){
        boolean isSaved=true;
        String currentFilePath=currentCommandManager.getFilePath();
        if(!currentFilePath.equals("")){
            workspaceManager.getWorkspaceMap().put(currentFilePath,currentCommandManager);
        }
        Map<String,CommandManager> workspaceMap=workspaceManager.getWorkspaceMap();
        for(Map.Entry<String,CommandManager> entry: workspaceMap.entrySet()){
            if (!entry.getValue().isSaved()) {
                isSaved = false;
                break;
            }
        }
        if(!isSaved){
            Scanner scanner = new Scanner(System.in);
            System.out.println("Do you want to save the unsaved workspace [Y\\N] ?");
            while (true){
                String userInput=scanner.next();
                if(userInput.equalsIgnoreCase("Y")){
                    workspaceMap.forEach((filePath,commandManager)->{
                        if(!commandManager.isSaved()){
                            try {
                                commandManager.parseCommand("save");
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    });
                    break;
                } else if (userInput.equalsIgnoreCase("N")) {
                    break;
                }else {
                    System.out.println("Invalid input. Please enter again [Y\\N] .");
                }
            }
        }
        workspaceManager.saveWorkSpaceDataToFile();
        saveCommandManagerData();
        saveHistoryData();
        saveStataData();
    }

    public void ls(){
        if(!currentCommandManager.getFilePath().equals("")){
            String loadedMdFilePath = currentCommandManager.getFilePath();
            String currentDirectory = System.getProperty("user.dir");
            loadedMdFilePath=currentDirectory+File.separator+loadedMdFilePath;
            // 提取该文件的父目录
            File loadedMdFile = new File(loadedMdFilePath);
            File parentDirectory = loadedMdFile.getParentFile();
            String filename=Utils.extractFileName(loadedMdFilePath);
            // 获取该目录下的所有 md 文件的相对路径
            List<String> relativeMdFilePaths = listMdFilesInDirectory(parentDirectory, parentDirectory);
            //利用组合模式构建树形结构
            FileComposite menu=new FileComposite("",0,0,0);
            for(String relativeMdFilePath:relativeMdFilePaths){
                //文件
                if(!relativeMdFilePath.contains("/")){
                    FileLeaf fileLeaf=new FileLeaf(relativeMdFilePath,1,menu.getComponentList().size(),menu.getComponentList().size());
                    if(relativeMdFilePath.equals(filename)){
                        fileLeaf.setActive(true);
                    }
                    menu.addChild(fileLeaf);
                    for(FileComponent fileComponent:menu.getComponentList()){
                        fileComponent.setNum(menu.getComponentList().size());
                    }
                }
                //目录
                else {
                    String[] parts1=relativeMdFilePath.split("/");
                    FileComposite currentComposite= menu;
                    for(int i=0;i<parts1.length-1;i++){
                        String contentName=parts1[i];
                        FileComponent fileComponent= menu.findChild(contentName);
                        if(fileComponent==null){
                            FileComposite fileComposite=new FileComposite(contentName,i+1,currentComposite.getComponentList().size(),currentComposite.getComponentList().size());
                            currentComposite.addChild(fileComposite);
                            for(FileComponent fileComponent1:currentComposite.getComponentList()){
                                fileComponent1.setNum(currentComposite.getComponentList().size());
                            }
                            currentComposite=fileComposite;
                        }
                        else {
                            currentComposite= (FileComposite) fileComponent;
                        }
                    }
                    String fileName1=parts1[parts1.length-1];
                    FileLeaf fileLeaf=new FileLeaf(fileName1, parts1.length, currentComposite.getComponentList().size(),currentComposite.getComponentList().size());
                    currentComposite.addChild(fileLeaf);
                    for(FileComponent fileComponent:currentComposite.getComponentList()){
                        fileComponent.setNum(currentComposite.getComponentList().size());
                    }
                }
            }
            menu.print();
        }
        else {
            System.out.println("Please load a file first.");
        }
    }
    //存储currentCommandManager
    public void saveCommandManagerData() {
        try (FileOutputStream fileOutputStream = new FileOutputStream(commandManagerPath);
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream)) {
             // 使用对象输出流将对象保存到磁盘
             objectOutputStream.writeObject(currentCommandManager);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //加载currentCommandManager
    public CommandManager loadCommandManagerData() {
        CommandManager currentCommandManager = null;
        File commandManagerFile=new File(commandManagerPath);
        if(commandManagerFile.length()==0){
            return new CommandManager();
        }
        try (FileInputStream fileInputStream = new FileInputStream(commandManagerPath);
             ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream)) {
            // 使用对象输入流从磁盘加载对象
            currentCommandManager = (CommandManager) objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return currentCommandManager;
    }
    //存储historyCommandObserver
    public void saveHistoryData() {
        try (FileOutputStream fileOutputStream = new FileOutputStream(historyPath);
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream)) {
            // 使用对象输出流将对象保存到磁盘
            objectOutputStream.writeObject(historyCommandObserver);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //加载historyCommandObserver
    public HistoryCommandObserver loadHistoryData() throws IOException {
        HistoryCommandObserver historyCommandObserver = null;
        File historyFile=new File(historyPath);
        if(historyFile.length()==0){
            return new HistoryCommandObserver();
        }
        try (FileInputStream fileInputStream = new FileInputStream(historyPath);
             ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream)) {
            // 使用对象输入流从磁盘加载对象
            historyCommandObserver = (HistoryCommandObserver) objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return historyCommandObserver;
    }
    //存储statsCommandObserver
    public void saveStataData() {
        try (FileOutputStream fileOutputStream = new FileOutputStream(statsPath);
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream)) {
            // 使用对象输出流将对象保存到磁盘
            objectOutputStream.writeObject(statsCommandObserver);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //加载statsCommandObserver
    public StatsCommandObserver loadStatsData() throws IOException {
        StatsCommandObserver statsCommandObserver= null;
        File statsFile=new File(statsPath);
        if(statsFile.length()==0){
            return new StatsCommandObserver();
        }
        try (FileInputStream fileInputStream = new FileInputStream(statsPath);
             ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream)) {
            // 使用对象输入流从磁盘加载对象
            statsCommandObserver = (StatsCommandObserver) objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return statsCommandObserver;
    }

}









