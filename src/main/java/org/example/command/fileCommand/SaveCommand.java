package org.example.command.fileCommand;

import org.example.command.Editor;

import java.io.Serializable;

public class SaveCommand extends FileCommand implements Serializable {
    protected Editor editor;
    public SaveCommand(Editor editor){
        this.editor=editor;
    }

    @Override
    public void execute() {
        this.editor.save();
    }
}
