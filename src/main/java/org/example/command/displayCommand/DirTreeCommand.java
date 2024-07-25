package org.example.command.displayCommand;

import org.example.command.Editor;

import java.io.Serializable;

public class DirTreeCommand extends DisplayCommand implements Serializable {
    protected Editor editor;
    private String content;
    public DirTreeCommand(Editor editor, String content) {
        this.editor = editor;
        this.content = content;
    }

    @Override
    public void execute() {
        this.editor.dirtree(this.content);
    }
}
