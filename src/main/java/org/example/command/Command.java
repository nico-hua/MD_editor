package org.example.command;

import java.io.FileNotFoundException;

public interface Command {
    void execute() throws FileNotFoundException;
    void undo();
}
