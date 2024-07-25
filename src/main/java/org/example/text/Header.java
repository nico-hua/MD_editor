package org.example.text;

import java.io.Serializable;

public class Header implements Text, Serializable {
    private int level;
    private String content;
    public Header(int level,String content){
        this.level=level;
        this.content=content;
    }
    @Override
    public String getContent() {
        return this.content;
    }

    public int getType() {
        return this.level;
    }

    @Override
    public String getRawText() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < this.level; i++) {
            stringBuilder.append('#');
        }
        String str = stringBuilder.toString();
        return str+" "+this.content;
    }

    @Override
    public void list() {
        System.out.println(this.getRawText());
    }

    @Override
    public void list_tree() {
        System.out.println(this.content);
    }

}
