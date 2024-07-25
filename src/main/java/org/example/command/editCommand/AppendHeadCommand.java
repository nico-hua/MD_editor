package org.example.command.editCommand;

import org.example.command.Editor;

import java.io.Serializable;

public class AppendHeadCommand extends InsertCommand implements Serializable {
    public AppendHeadCommand(Editor textEditor, String content) {
        super(textEditor, 0, content);
    }
}
