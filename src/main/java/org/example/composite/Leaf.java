package org.example.composite;

import org.example.text.Text;

public class Leaf extends Component{
    public Leaf(Text text, int level, int index, int num){
        this.commandText=text;
        this.level=level;
        this.index=index;
        this.num=num;
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
        if(this.index!=this.num-1&&this.num>1){
            System.out.print("├── ");
            this.commandText.list_tree();
        }
        else{
            System.out.print("└── ");
            this.commandText.list_tree();
        }
    }
}
