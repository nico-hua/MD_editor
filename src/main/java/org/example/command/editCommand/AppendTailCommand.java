package org.example.command.editCommand;

import org.example.command.Editor;

import java.io.Serializable;

public class AppendTailCommand extends InsertCommand implements Serializable {
    public AppendTailCommand(Editor textEditor, int lineNumber, String content) {
        super(textEditor, lineNumber, content);
    }
}
