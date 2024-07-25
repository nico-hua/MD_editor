package org.example.command;

import org.example.command.displayCommand.DirTreeCommand;
import org.example.command.displayCommand.ListCommand;
import org.example.command.displayCommand.ListTreeCommand;
import org.example.command.editCommand.*;
import org.example.command.fileCommand.FileCommand;
import org.example.command.fileCommand.LoadCommand;
import org.example.command.fileCommand.SaveCommand;
import org.example.observer.HistoryCommandObserver;
import org.example.observer.StatsCommandObserver;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.Stack;
import java.util.regex.Pattern;

public class CommandManager implements Serializable {
    private Stack<Command> undoStack;//存储undo的命令
    private Stack<Command> redoStack;//存储redo的命令
    private HistoryCommandObserver historyCommandObserver;//history功能观察者
    private StatsCommandObserver statsCommandObserver;//stats功能观察者
    private Editor editor;//文件编辑
    private String filePath;//文件路径
    private boolean undoTag;//用于判断undo之后是否执行了除redo的编辑操作
    private final Integer maxHistoryCommandNum=10;//可存储的最大历史命令条数
    private boolean isSaved=true;//用于判断文件是否保存了
    private boolean isActive;//用于判断当前workspace是否活跃
    public CommandManager(){
        this.undoStack=new Stack<>();
        this.redoStack=new Stack<>();
        this.editor=new Editor();
        this.isActive=true;
        this.filePath="";
    }

    public void setHistoryCommandObserver(HistoryCommandObserver historyCommandObserver) {
        this.historyCommandObserver = historyCommandObserver;
    }

    public void setStatsCommandObserver(StatsCommandObserver statsCommandObserver) {
        this.statsCommandObserver = statsCommandObserver;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public boolean isSaved() {
        return isSaved;
    }

    public void setSaved(boolean saved) {
        isSaved = saved;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    //解析命令
    public void parseCommand(String text) throws IOException {
        String[] parts = text.split(" ", 2);
        String commandType = parts[0];
        String filePath;
        String content;
        if(!(commandType.equals("load")||commandType.equals("history")||commandType.equals("stats"))&&this.editor.getFilePath().equals("")){
            System.out.println("please load a file first!");
            return;
        }

        switch (commandType){
            case "load":
                if (parts.length == 2 && isValidFilePath(parts[1])) {
                    filePath = parts[1];
                    Command loadCommand = new LoadCommand(this.editor, filePath);
                    this.execute(loadCommand);
                    // 清空undo/redo
//                    this.undoStack.clear();
//                    this.redoStack.clear();
                    this.filePath=filePath;
                } else {
                    System.out.println("Invalid load command format.");
                }
                break;
            case "save":
                if (parts.length == 1) {
                    Command saveCommand = new SaveCommand(this.editor);
                    this.execute(saveCommand);
                    //清空redo栈
//                    this.undoStack.clear();
//                    this.redoStack.clear();
                    this.notifyHistoryCommandObserver(text);
                    this.isSaved=true;
                } else {
                    System.out.println("Invalid save command format.");
                }
                break;
            case "insert":
                if (parts.length == 2) {
                    String[] contentParts = parts[1].split(" ",2);
                    int lineNumber = 0;
                    if (contentParts[0].matches("\\d+")) {
                        lineNumber = Integer.parseInt(contentParts[0]);
                        content = contentParts[1];
                    } else {
                        content = parts[1];
                    }
                    try {
                        Command insertCommand = new InsertCommand(this.editor, lineNumber-1, content);
                        this.execute(insertCommand);
                        if(this.undoTag){
                            this.undoTag=false;
                            this.redoStack.clear();
                        }
                        this.notifyHistoryCommandObserver(text);
                        this.isSaved=false;
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid insert command format.");
                    } catch (RuntimeException e) {
                        System.out.println(e.getMessage());
                    }
                } else {
                    System.out.println("Invalid insert command format.");
                }
                break;
            case "append-head":
                if (parts.length == 2) {
                    try {
                        Command appendHeadCommand = new AppendHeadCommand(this.editor, parts[1]);
                        this.execute(appendHeadCommand);
                        if(this.undoTag){
                            this.undoTag=false;
                            this.redoStack.clear();
                        }
                        this.notifyHistoryCommandObserver(text);
                        this.isSaved=false;
                    } catch (RuntimeException e) {
                        System.out.println(e.getMessage());
                    }
                } else {
                    System.out.println("Invalid append-head command format.");
                }
                break;
            case "append-tail":
                if (parts.length == 2) {
                    try {
                        Command appendTailCommand = new AppendTailCommand(this.editor, this.editor.getFileLength(), parts[1]);
                        this.execute(appendTailCommand);
                        if(this.undoTag){
                            this.undoTag=false;
                            this.redoStack.clear();
                        }
                        this.notifyHistoryCommandObserver(text);
                        this.isSaved=false;
                    } catch (RuntimeException e) {
                        System.out.println(e.getMessage());
                    }
                } else {
                    System.out.println("Invalid append-tail command format.");
                }
                break;
            case "delete":
                if (parts.length == 2) {
                    // delete by lineNumber
                    if (parts[1].matches("\\d+")){
                        try {
                            int lineNumber = Integer.parseInt(parts[1]);
                            Command deleteByLineNumberCommand = new DeleteByLineNumberCommand(this.editor, lineNumber-1);
                            this.execute(deleteByLineNumberCommand);
                            if(this.undoTag){
                                this.undoTag=false;
                                this.redoStack.clear();
                            }
                            this.notifyHistoryCommandObserver(text);
                            this.isSaved=false;
                        } catch (RuntimeException e) {
                            System.out.println(e.getMessage());
                        }
                    }
                    // delete by content
                    else {
                        try {
                            Command deleteByContentCommand = new DeleteByContentCommand(this.editor, parts[1]);
                            this.execute(deleteByContentCommand);
                            if(this.undoTag){
                                this.undoTag=false;
                                this.redoStack.clear();
                            }
                            this.notifyHistoryCommandObserver(text);
                            this.isSaved=false;
                        } catch (RuntimeException e) {
                            System.out.println(e.getMessage());
                        }
                    }
                } else {
                    System.out.println("Invalid delete command format.");
                }
                break;
            case "list":
                if (parts.length == 1) {
                    Command listCommand = new ListCommand(this.editor);
                    this.execute(listCommand);
                    this.notifyHistoryCommandObserver(text);
                } else {
                    System.out.println("Invalid list command format.");
                }
                break;
            case "list-tree":
                if (parts.length == 1) {
                    Command listTreeCommand = new ListTreeCommand(this.editor);
                    this.execute(listTreeCommand);
                    this.notifyHistoryCommandObserver(text);
                } else {
                    System.out.println("Invalid list-tree command format.");
                }
                break;
            case "dir-tree":
                try {
                    if (parts.length == 1) {
                        Command listTreeCommand = new ListTreeCommand(this.editor);
                        this.execute(listTreeCommand);
                        this.notifyHistoryCommandObserver(text);
                    } else {
                        Command dirTreeCommand = new DirTreeCommand(this.editor, parts[1]);
                        this.execute(dirTreeCommand);
                        this.notifyHistoryCommandObserver(text);
                    }
                } catch (RuntimeException e) {
                    // invalid content/no such content
                    System.out.println(e.getMessage());
                }
                break;
            case "undo":
                if (parts.length == 1) {
                    if(!this.undoStack.isEmpty()){
                        Command command=this.undoStack.pop();
                        if(!(command instanceof FileCommand)){
                            command.undo();
                            this.undoTag=true;
                            this.redoStack.push(command);
                            this.isSaved=false;
                            this.notifyHistoryCommandObserver(text);
                        }
                    }
                } else {
                    System.out.println("Invalid undo command format.");
                }
                break;
            case "redo":
                if (parts.length == 1) {
                    if(!this.redoStack.isEmpty()){
                        Command command=this.redoStack.pop();
                        command.execute();
                        if(this.redoStack.isEmpty()){
                            this.undoTag=false;
                        }
                        this.undoStack.push(command);
                        this.isSaved=false;
                        this.notifyHistoryCommandObserver(text);
                    }
                } else {
                    System.out.println("Invalid redo command format.");
                }
                break;
            case "history":
                if(parts.length==1){
                    this.history(maxHistoryCommandNum);
                    this.notifyHistoryCommandObserver(text);
                }
                else {
                    if (parts[1].matches("\\d+")) {
                        int num = Integer.parseInt(parts[1]);
                        this.history(num);
                        this.notifyHistoryCommandObserver(text);
                    } else {
                        System.out.println("Invalid history command format.\"");
                    }
                }
                break;
            case "stats":
                if (parts.length == 1 || parts[1].equals("current")) {
                    this.stats(this.editor.getFilePath());
                    this.notifyHistoryCommandObserver(text);
                } else if (parts[1].equals("all")) {
                    this.stats("");
                    this.notifyHistoryCommandObserver(text);
                } else {
                    System.out.println("Invalid stats command format.\"");
                }
                break;
            default:
                System.out.println("Invalid command!");
                break;
        }
    }

    private boolean isValidFilePath(String filePath) {
        String pattern = "^[a-zA-Z0-9_.-]+.md$";
        return Pattern.matches(pattern, filePath);
    }


    private void execute(Command command) throws FileNotFoundException {
        if (command instanceof EditCommand) {
            this.undoStack.push(command);
            command.execute();
        } else {
            command.execute();
        }
    }


    //通知history观察者
    public void notifyHistoryCommandObserver(String text) throws IOException {
        this.historyCommandObserver.update(text);
    }

    public void notifyStatsCommandObserver(String text) throws IOException {
        this.statsCommandObserver.update(text);
    }

    private void history(int n){
        this.historyCommandObserver.printHistoryCommands(n);
    }

    private void stats(String text){
        this.statsCommandObserver.printStats(text);
    }
}
