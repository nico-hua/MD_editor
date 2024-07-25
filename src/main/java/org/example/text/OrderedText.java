package org.example.text;

import java.io.Serializable;

public class OrderedText implements Text, Serializable {
    private int level;
    private String content;
    public OrderedText(int level,String content){
        this.level=level;
        this.content=content;
    }
    @Override
    public String getContent() {
        return this.content;
    }
    public int getLevel() { return this.level; }

    @Override
    public String getRawText() {
        return this.level + ". " + this.content;
    }

    @Override
    public void list() {
        System.out.println(this.getRawText());
    }

    @Override
    public void list_tree() {
        System.out.println(this.getRawText());
    }

}
