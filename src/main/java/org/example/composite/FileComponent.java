package org.example.composite;

import java.util.ArrayList;
import java.util.List;

public abstract class FileComponent {
    protected String contentName;
    protected int count;//命令中’#‘的数量，文件作为伪数量，根据其数量显示不同数量的空格
    protected int index;//在parent数组中的索引，判断其前为"├── "还是"└── "
    protected int num;//parent数组的数量
    protected List<Integer> parentIndex=new ArrayList<>();//存储与上级目录同级的"│"的位置
    public abstract void print();
    public void addChild(FileComponent component){
        throw new UnsupportedOperationException();
    }
    public List<FileComponent> getComponentList() {
        throw new UnsupportedOperationException();
    }

    public void setNum(int num) {
        this.num = num;
    }

    public void removeChild(FileComponent component){
        throw new UnsupportedOperationException();
    }

    public String getContentName() {
        return contentName;
    }

    public void setParent(int num){
        parentIndex.add(num);
    }
}
