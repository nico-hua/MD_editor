package org.example.command;

import org.example.composite.Component;
import org.example.composite.Composite;
import org.example.composite.Leaf;
import org.example.text.Header;
import org.example.text.Text;
import org.example.text.TextFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Editor implements Serializable{
    private List<Text> textList;//用于存储文本
    private String filePath;//文件路径

    public Editor(){
        this.textList=new ArrayList<>();
        this.filePath="";
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public int getFileLength() { return this.textList.size(); }

    public void load(String filePath) throws FileNotFoundException {
        File file=new File(filePath);
        if(file.exists()){
            try(BufferedReader reader=new BufferedReader(new FileReader(filePath))){
                this.textList.clear();
                TextFactory textFactory=new TextFactory();
                String line;
                while ((line=reader.readLine())!=null){
                    Text text=textFactory.createText(line);
                    this.textList.add(text);
                }
                this.filePath=filePath;
            } catch (IOException e) {
                 System.out.println(e.getMessage());
            }
        }
        else {
            try{
                file.createNewFile();
                this.filePath=filePath;
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public void save(){
        try {
            FileWriter fileWriter = new FileWriter(this.filePath);
            BufferedWriter writer = new BufferedWriter(fileWriter);
            for (Text text: textList) {
                writer.write(text.getRawText() + "\n");
            }
            writer.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void insert(int lineNum,String content){
        if(lineNum==-1){
            lineNum=textList.size();
        }
        if(lineNum<0||lineNum>textList.size()){
            throw new RuntimeException("LineNumber is out of range.");
        }
        TextFactory textFactory=new TextFactory();
        Text text=textFactory.createText(content);
        textList.add(lineNum,text);
    }

    public Text deleteByLineNumber(int lineNumber) {
        // check whether lineNumber is out of range
        if (lineNumber < 0 || lineNumber >= textList.size()) {
            throw new RuntimeException("lineNumber is out of range!");
        }
        return this.textList.remove(lineNumber);
    }
    public void deleteByContent(String content, ArrayList<Integer> lineNumberList, ArrayList<Text> textList) {
        // iterate from the end of file
        for (int i = this.textList.size()-1; i >= 0; i--) {
            Text text = this.textList.get(i);
            if (content.equals(text.getContent())) {
                lineNumberList.add(i);
                textList.add(text);
                this.textList.remove(i);
            }
        }
    }


    public void list() {
        for(Text text : this.textList){
            text.list();
        }
    }

    public void dirtree(String content) {
        int index=0;
        int i;
        for(i=0;i<textList.size();i++){
            if(textList.get(i).getContent().equals(content)){
                index=i;
                break;
            }
        }
        if(i==textList.size()){
            System.out.println("找不到："+content);
        }
        else{
            Component menu=buildChildTree(textList.subList(index,textList.size()),0);
            if(menu instanceof Composite){
                int num=menu.getLevel()-1;
                subLevelForDir(num, (Composite) menu);
            }
            menu.print();
        }
    }
    public void listtree() {
        Component menu=new Composite(null,0,0,0);
        int i=0;
        while(i<textList.size()){
            //添加的为文件
            if(!(textList.get(i) instanceof Header)){
                if(i>0){
                    menu.addChild(new Leaf(textList.get(i),((Header) textList.get(i-1)).getType()+1,menu.getComponentList().size(),menu.getComponentList().size()));
                    for(Component component:menu.getComponentList()){
                        component.setNum(menu.getComponentList().size());
                    }
                }
                else{
                    menu.addChild(new Leaf(textList.get(i),1,menu.getComponentList().size(),menu.getComponentList().size()));
                    for(Component component:menu.getComponentList()){
                        component.setNum(menu.getComponentList().size());
                    }
                }
                i++;
            }
            //添加的为目录
            else{
                Component chMenu=buildChildTree(textList.subList(i,textList.size()),menu.getComponentList().size());
                for(Component component:chMenu.getComponentList()){
                    component.setNum(chMenu.getComponentList().size());
                }
                menu.addChild(chMenu);
                for(Component component:menu.getComponentList()){
                    component.setNum(menu.getComponentList().size());
                }
                i+=countChildNum(textList.subList(i,textList.size()))+1;
            }
        }
        menu.print();
    }

    //使用递归构建tree
    private Component buildChildTree(List<Text> textList, int cnt){
        Component menu;
        if(textList.get(0) instanceof Header){
            menu=new Composite((Header) textList.get(0),((Header) textList.get(0)).getType(),cnt,cnt);
        }
        else{
            menu=new Leaf(textList.get(0),1,cnt,cnt);
            return menu;
        }
        int index=1;
        while(index< textList.size()){
            if(!(textList.get(index) instanceof Header)){
                menu.addChild(new Leaf(textList.get(index),((Header) textList.get(0)).getType()+1,menu.getComponentList().size(),menu.getComponentList().size()));
                for(Component component:menu.getComponentList()){
                    component.setNum(menu.getComponentList().size());
                }
                index++;
            }
            else{
                if(((Header) textList.get(index)).getType()<=((Header) textList.get(0)).getType()){
                    break;
                }
                else{
                    menu.addChild(buildChildTree(textList.subList(index,textList.size()),menu.getComponentList().size()));
                    for(Component component:menu.getComponentList()){
                        component.setNum(menu.getComponentList().size());
                    }
                    index+=countChildNum(textList.subList(index,textList.size()))+1;
                }
            }
        }
        return menu;
    }

    //获取lines第一行的child数量
    private int countChildNum(List<Text> textList){
        int count=0;
        for(int i=1;i<textList.size();i++){
            if(isChild(textList.get(0),textList.get(i))){
                count++;
            }
            else{
                break;
            }
        }
        return count;
    }

    //判断是否为子节点
    private boolean isChild(Text text1,Text text2){
        boolean result=false;
        if(!(text2 instanceof Header)){
            result=true;
        }
        else if(((Header) text1).getType()<((Header) text2).getType()){
            result=true;
        }
        return result;
    }

    //将level减少
    private void subLevelForDir(int num,Composite menu){
        menu.setLevel(menu.getLevel()-num);
        for(Component component:menu.getComponentList()){
            if(component instanceof Leaf){
                component.setLevel(component.getLevel()-num);
            }
            else {
                subLevelForDir(num, (Composite) component);
            }
        }
    }
}


























































