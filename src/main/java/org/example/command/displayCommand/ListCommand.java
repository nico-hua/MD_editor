package org.example.command.displayCommand;

import org.example.command.Editor;

import java.io.Serializable;

public class ListCommand extends DisplayCommand implements Serializable {
    protected Editor editor;
    public ListCommand(Editor editor) {
        this.editor = editor;
    }

    @Override
    public void execute() {
        this.editor.list();
    }
}
