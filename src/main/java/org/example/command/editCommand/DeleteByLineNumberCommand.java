package org.example.command.editCommand;

import org.example.command.Editor;
import org.example.text.Text;

import java.io.Serializable;

public class DeleteByLineNumberCommand extends EditCommand implements Serializable {
    protected Editor editor;
    private int lineNumber;
    private Text text;

    public DeleteByLineNumberCommand(Editor editor, int lineNumber) {
        this.editor = editor;
        this.lineNumber = lineNumber;
    }
    @Override
    public void execute() {
        this.text = this.editor.deleteByLineNumber(this.lineNumber);
    }

    @Override
    public void undo() {
        this.editor.insert(this.lineNumber, this.text.getRawText());
    }
}
