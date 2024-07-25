package org.example.composite;

import org.example.text.Header;

import java.util.ArrayList;
import java.util.List;

public class Composite extends Component{
    private List<Component> componentList=new ArrayList<>();
    public Composite(Header header, int level, int index, int num){
        this.commandText=header;
        this.level=level;
        this.index=index;
        this.num=num;
    }
    @Override
    public void addChild(Component component) {
        componentList.add(component);
    }

    @Override
    public void removeChild(Component component) {
        componentList.remove(component);
    }

    public List<Component> getComponentList() {
        return componentList;
    }


    @Override
    public void print() {
        //打印与上级目录同级需要的"│"
        StringBuilder lines = new StringBuilder();
        for (int i = 0; i < 3 * (level - 1); i++) {
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
        if(this.commandText!=null){
            if(this.index!=this.num-1&&this.num>1){
                System.out.print("├── ");
                this.commandText.list_tree();
            }
            else{
                System.out.print("└── ");
                this.commandText.list_tree();
            }
        }
        //打印子树
        for(Component component:componentList){
            if(this.parentIndex.size()!=0){
                for (Integer integer : this.parentIndex) {
                    component.setParent(integer);
                }
            }
            if(this.num>1&&this.index!=this.num-1){
                component.setParent(level);
            }
            component.print();
        }
    }
}
