package org.example.command.fileCommand;

import org.example.command.Editor;

import java.io.FileNotFoundException;
import java.io.Serializable;

public class LoadCommand extends FileCommand implements Serializable {
    protected Editor editor;
    private String filePath;
    public LoadCommand(Editor editor,String filePath){
        this.editor=editor;
        this.filePath=filePath;
    }

    @Override
    public void execute() throws FileNotFoundException {
        this.editor.load(this.filePath);
    }
}
