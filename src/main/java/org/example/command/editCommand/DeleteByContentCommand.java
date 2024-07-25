package org.example.command.editCommand;

import org.example.command.Editor;
import org.example.text.Text;

import java.io.Serializable;
import java.util.ArrayList;

public class DeleteByContentCommand extends EditCommand implements Serializable {
    protected Editor editor;
    private ArrayList<Integer> lineNumberList;
    private ArrayList<Text> textList;
    private String content;
    public DeleteByContentCommand(Editor editor, String content) {
        this.editor = editor;
        this.content = content;
        this.lineNumberList = new ArrayList<>();
        this.textList = new ArrayList<>();
    }
    @Override
    public void execute() {
        this.editor.deleteByContent(this.content, this.lineNumberList, this.textList);
    }

    @Override
    public void undo() {
        for (int i = this.lineNumberList.size() - 1; i >= 0; i--) {
            int lineNumber = this.lineNumberList.get(i);
            Text text = this.textList.get(i);
            this.editor.insert(lineNumber, text.getRawText());
        }
    }
}
