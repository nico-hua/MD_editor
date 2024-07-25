package org.example.command.editCommand;

import org.example.command.Editor;

import java.io.Serializable;

public class InsertCommand extends EditCommand implements Serializable {
    protected Editor editor;
    private int lineNumber;
    private String content;
    public InsertCommand(Editor editor,int lineNumber,String content){
        this.editor=editor;
        this.lineNumber=lineNumber;
        this.content=content;
    }

    @Override
    public void execute() {
        this.editor.insert(lineNumber,content);
    }

    @Override
    public void undo() {
        int lineNumber=0;
        if(this.lineNumber==-1){
            lineNumber=this.editor.getFileLength()-1;
        }
        else {
            lineNumber=this.lineNumber;
        }
        this.editor.deleteByLineNumber(lineNumber);
    }
}
