package org.example.observer;

import java.io.IOException;

public interface CommandObserver {
    void update(String text) throws IOException;
    void sessionStart() throws IOException;
}
