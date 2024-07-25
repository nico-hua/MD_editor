package observer;

import org.example.observer.HistoryCommandObserver;
import org.example.utils.Terminal;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class HistoryObserverTest {
    private HistoryCommandObserver historyCommandObserver;
    private static final String historyLogFilePath="history.log";
    @Before
    public void SetUp(){
        historyCommandObserver=new HistoryCommandObserver();
    }

    @Test
    @DisplayName("test 'session start' log")
    public void testSessionStart() throws IOException {
        historyCommandObserver.sessionStart();
        //  check if session start has been logged correctly
        String line = readLastLine(historyLogFilePath);
        assertTrue(line.startsWith("session start at "));
        // Check if the timestamp format is correct, e.g., "YYYYMMDD HH:mm:SS"
        String timestamp = line.substring("session start at ".length()).trim();
        checkTimeStamp(timestamp);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        historyCommandObserver.printHistoryCommands(1);
        line=outputStream.toString();
        assertTrue(line.startsWith("session start at "));
        timestamp = line.substring("session start at ".length()).trim();
        checkTimeStamp(timestamp);
    }

    @Test
    @DisplayName("Test command session")
    public void testLogCommand() throws IOException {
        String command = "load books.md";
        historyCommandObserver.update(command);
        String line=readLastLine(historyLogFilePath);
        assertTrue(line.contains(command));
        String timestamp=line.substring(0,17);
        checkTimeStamp(timestamp);

        command = "insert 1 # books";
        historyCommandObserver.update(command);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        historyCommandObserver.printHistoryCommands(1);
        line=outputStream.toString();
        assertTrue(line.contains(command));
        timestamp=line.substring(0,17);
        checkTimeStamp(timestamp);
    }


    private void checkTimeStamp(String timestamp) {
        // Check if the timestamp format is correct, e.g., "YYYYMMDD HH:mm:SS"
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
        try {
            dateFormat.parse(timestamp);
            Date parsedDate = dateFormat.parse(timestamp);
            // Add assertions to check if the parsed date is not null, indicating a valid timestamp format
            assertNotNull(parsedDate);
            // Check if the time is within a reasonable range (e.g., current time +/- a few seconds)
            Date now = new Date();
            long timeDifference = Math.abs(now.getTime() - parsedDate.getTime());
            assertTrue(timeDifference < 5000);
        } catch (ParseException e) {
            System.out.println("Timestamp is incorrect");
        }
    }

    public static String readLastLine(String filePath) throws IOException {
        String lastLine = null;
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String currentLine;
            while ((currentLine = reader.readLine()) != null) {
                lastLine = currentLine;
            }
        }
        return lastLine;
    }
}
