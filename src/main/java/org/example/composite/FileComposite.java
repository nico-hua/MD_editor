package org.example.composite;

import org.example.composite.FileComponent;

import java.util.ArrayList;
import java.util.List;

public class FileComposite extends FileComponent {
    private List<FileComponent> componentList=new ArrayList<>();
    public FileComposite(String contentName,int count,int index,int num){
        this.contentName=contentName;
        this.count=count;
        this.index=index;
        this.num=num;
    }
    @Override
    public void addChild(FileComponent component) {
        componentList.add(component);
    }

    @Override
    public void removeChild(FileComponent component) {
        componentList.remove(component);
    }

    public FileComponent findChild(String contentName){
        FileComponent fileComponent=null;
        for(FileComponent fileComponentChild:this.componentList){
            if(fileComponentChild.getContentName().equals(contentName)){
                fileComponent=fileComponentChild;
                break;
            }
        }
        return fileComponent;
    }

    public List<FileComponent> getComponentList() {
        return componentList;
    }

    @Override
    public void print() {
        //打印与上级目录同级需要的"│"
        StringBuilder lines = new StringBuilder();
        for (int i = 0; i < 3 * (count - 1); i++) {
            lines.append(" ");
        }
        if (this.parentIndex.size() != 0) {
            for (Integer integer : this.parentIndex) {
                int index = 3 * (integer - 1);
                lines.setCharAt(index, '│');
            }
        }
        System.out.print(lines);
        //根据文件的index判断打印"├──"还是"└──"
        if(!(this.contentName.equals(""))){
            if(this.index!=this.num-1&&this.num>1){
                System.out.println("├── "+ this.contentName);
            }
            else{
                System.out.println("└── "+this.contentName);
            }
        }
        //打印子树
        for(FileComponent component:componentList) {
            if (this.parentIndex.size() != 0) {
                for (Integer integer : this.parentIndex) {
                    component.setParent(integer);
                }
            }
            if (this.num > 1 && this.index != this.num - 1) {
                component.setParent(count);
            }
            component.print();
        }
    }
}
