package text;

import org.example.text.*;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class TextFactoryTest {
    private TextFactory textFactory = new TextFactory();

    @Test
    @DisplayName("Invalid Text")
    public void testInvalidText() {
        assertThrows(RuntimeException.class, () -> this.textFactory.createText("#Header"));
        assertThrows(RuntimeException.class, () -> this.textFactory.createText("1.OrderedList"));
        assertThrows(RuntimeException.class, () -> this.textFactory.createText("+OrderedList"));
        assertThrows(RuntimeException.class, () -> this.textFactory.createText("Invalid Text"));
    }

    @Test
    @DisplayName("Valid Header Test (level1-6)")
    public void testValidHeader() {
        for (int level = 1; level <= 6; level++) {
            StringBuilder headerTextBuilder = new StringBuilder();
            for (int i = 0; i < level; i++) {
                headerTextBuilder.append("#");
            }
            headerTextBuilder.append(" Header Text");
            String headerText = headerTextBuilder.toString();
            Text header = textFactory.createText(headerText);
            assertTrue(header instanceof Header);
            assertEquals("Header Text", header.getContent());
            assertEquals(level, ((Header) header).getType());
        }
    }

    @Test
    @DisplayName("Invalid Header Test (level>6)")
    public void testInvalidHeader() {
        int level=7;
        StringBuilder headerTextBuilder = new StringBuilder();
        for (int i = 0; i < level; i++) {
            headerTextBuilder.append("#");
        }
        headerTextBuilder.append(" Header Text");
        String headerText = headerTextBuilder.toString();
        assertThrows(RuntimeException.class, () -> textFactory.createText(headerText));
    }

    @Test
    @DisplayName("Valid OrderedList Test")
    public void testOrderedList() {
        Text orderedText = textFactory.createText("1. Item 1");
        assertTrue(orderedText instanceof OrderedText);
        assertEquals("Item 1", orderedText.getContent());
        assertEquals(1, ((OrderedText) orderedText).getLevel());

        orderedText = textFactory.createText("11. Item 1");
        assertTrue(orderedText instanceof OrderedText);
        assertEquals("Item 1", orderedText.getContent());
        assertEquals(11, ((OrderedText) orderedText).getLevel());

        orderedText = textFactory.createText("111. Item 1.2");
        assertTrue(orderedText instanceof OrderedText);
        assertEquals("Item 1.2", orderedText.getContent());
        assertEquals(111, ((OrderedText) orderedText).getLevel());
    }

    @Test
    @DisplayName("Valid UnorderedList Test")
    public void testValidUnorderedList() {
        List<Character> typeList = new ArrayList<>();
        typeList.add('*');
        typeList.add('+');
        typeList.add('-');
        for (Character type:typeList) {
            Text unorderText = textFactory.createText(type + " Item 1");
            assertTrue(unorderText instanceof UnorderedText);
            assertEquals("Item 1", unorderText.getContent());
            assertEquals(type.charValue(), ((UnorderedText) unorderText).getType());
        }
    }
}
