package org.example.text;

import java.io.Serializable;

public class UnorderedText implements Text, Serializable {
    private char type;
    private String content;
    public UnorderedText(char type,String content){
        this.type=type;
        this.content=content;
    }
    @Override
    public String getContent() {
        return this.content;
    }

    public char getType(){
        return this.type;
    }

    @Override
    public String getRawText() {
        return this.getType() + " " + this.content;
    }

    @Override
    public void list() {
        System.out.println(this.getRawText());
    }

    @Override
    public void list_tree() {
        System.out.print("·");
        System.out.println(this.getContent());
    }

}
