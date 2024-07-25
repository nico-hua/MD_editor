package org.example.composite;

import org.example.text.Text;

import java.util.ArrayList;
import java.util.List;

public abstract class Component {
    protected Text commandText;//命令
    protected int level;//命令中’#‘的数量，文件作为伪数量，根据其数量显示不同数量的空格
    protected int index;//在parent数组中的索引，判断其前为"├── "还是"└── "
    protected int num;//parent数组的数量
    protected List<Integer> parentIndex=new ArrayList<>();//存储与上级目录同级的"│"的位置
    public abstract void print();
    public void addChild(Component component){
        throw new UnsupportedOperationException();
    }
    public List<Component> getComponentList() {
        throw new UnsupportedOperationException();
    }

    public void setNum(int num) {
        this.num = num;
    }

    public void removeChild(Component component){
        throw new UnsupportedOperationException();
    }

    public void setParent(int num){
        parentIndex.add(num);
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}
