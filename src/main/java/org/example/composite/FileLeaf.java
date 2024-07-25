package org.example.composite;

import org.example.composite.FileComponent;

public class FileLeaf extends FileComponent {
    private Boolean isActive;//用于判断是否为当前load的文件
    public FileLeaf(String contentName,int count,int index,int num){
        this.contentName=contentName;
        this.count=count;
        this.index=index;
        this.num=num;
        this.isActive=false;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
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
        //根据文件的index判断打印"├──"还是"└──"以及文件的首位是否为*、+、-或其他
        if(this.index!=this.num-1&&this.num>1){
            System.out.print("├── "+ this.contentName);
        }
        else{
            System.out.print("└── "+ this.contentName);
        }
        if(isActive){
            System.out.print(" *");
        }
        System.out.println();
    }
}
