import org.example.utils.Terminal;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class LabTest {
    private Terminal terminal;
    @Before
    public void SetUp() throws IOException {
        clearFile();
        this.terminal=new Terminal();
        deleteFile();
    }

    public void SetUpWithNoClearAndDelete() throws IOException {
        this.terminal=new Terminal();
    }

    @Test
    public void TestCase1() throws IOException {
        List<String> commands = Arrays.asList("load test1.md","append-head # 旅行清单","append-tail ## 亚洲","append-tail 1. 中国",
                "append-tail 2. 日本","load test2.md","append-head # 书籍推荐","append-tail ## 编程","append-tail * 《设计模式的艺术》",
                "append-tail * 《云原生：运用容器、函数计算和数据构建下一代应用》","append-tail * 《深入理解Java虚拟机》");
        runCommands(commands);
        String target =
                "└── 书籍推荐\r\n" +
                "   └── 编程\r\n" +
                "      ├── ·《设计模式的艺术》\r\n" +
                "      ├── ·《云原生：运用容器、函数计算和数据构建下一代应用》\r\n" +
                "      └── ·《深入理解Java虚拟机》\r\n";
        testDisplay("list-tree", target);

        target= "->test2 *\r\n" +
                "  test1 *\r\n";
        testDisplay("list-workspace",target);

        commands=Arrays.asList("save");
        runCommands(commands);
        target= "->test2\r\n" +
                "  test1 *\r\n";
        testDisplay("list-workspace",target);

        commands=Arrays.asList("open test1");
        runCommands(commands);
        target= "  test2\r\n" +
                "->test1 *\r\n";
        testDisplay("list-workspace",target);
    }

    @Test
    public void TestCase2() throws IOException {
        List<String> commands=Arrays.asList("load test3.md","insert ## 程序设计","append-head # 我的资源","append-tail ### 软件设计",
                "load test4.md","append-head # 大学合集","append-tail 1. 复旦大学","append-tail 2. 上海交通大学");
        runCommands(commands);
        String target="Do you want to save the current workspace [Y\\N] ?\r\n";
        testCloseAndExit("Y","close",target);

        commands=Arrays.asList("open test3");
        runCommands(commands);
        target="->test3 *\r\n";
        testDisplay("list-workspace",target);

        target="Do you want to save the unsaved workspace [Y\\N] ?\r\n";
        testCloseAndExit("Y","exit",target);
    }

    @Test
    public void TestCase3() throws IOException {
        List<String> commands=Arrays.asList("load test5.md","append-head # 书籍推荐","append-tail * 《深入理解计算机系统》");
        runCommands(commands);
        String target=
                "├── data\r\n" +
                "│  ├── data1.md\r\n" +
                "│  └── data2\r\n" +
                "│     └── data2.md\r\n"+
                "└── test5.md *\r\n";
        testDisplay("ls",target);
    }

    @Test
    public void TestCase4() throws IOException {
        List<String> commands=Arrays.asList("load test6.md","insert ## 程序设计","append-head # 我的资源","append-tail ### 软件设计",
                "save","load test7.md","append-head # 大学合集","append-tail 1. 复旦大学","append-tail 2. 上海交通大学","undo");
        runCommands(commands);
        String target=
                "└── 大学合集\r\n"+
                "   └── 1. 复旦大学\r\n";
        testDisplay("list-tree",target);

        commands=Arrays.asList("save","exit");
        runCommands(commands);

        SetUpWithNoClearAndDelete();//再次启动程序，加载硬盘中的缓存

        target=
                "  test6\r\n"+
                "->test7\r\n";
        testDisplay("list-workspace",target);

        commands=Arrays.asList("redo");
        runCommands(commands);
        target=
                "└── 大学合集\r\n"+
                "   ├── 1. 复旦大学\r\n"+
                "   └── 2. 上海交通大学\r\n";
        testDisplay("list-tree",target);
    }

    private void testCloseAndExit(String input,String command,String target) throws IOException {
        input=input+"\n";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(input.getBytes());
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setIn(inputStream);
        System.setOut(new PrintStream(outputStream));
        this.terminal.parseCommand(command);
        assertEquals(target, outputStream.toString());
    }

    private void runCommands(List<String> commands) throws IOException {
        for (String command:commands) {
            this.terminal.parseCommand(command);
        }
    }
    private void testDisplay(String command, String target) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        this.terminal.parseCommand(command);
        assertEquals(target, outputStream.toString());
    }

    private void clearFile() throws IOException {
        List<String> filePaths = Arrays.asList("commandManager.txt", "history.txt", "stats.txt", "workspace.txt");
        for(String filePath:filePaths){
            File file = new File(filePath);
            if (file.exists()) {
                file.delete();
            }
        }
        for(String filePath:filePaths){
            File file=new File(filePath);
            file.createNewFile();
        }
    }

    private void deleteFile() {
        List<String> filePaths = Arrays.asList("test1.md", "test2.md", "test3.md", "test4.md", "test5.md","test6.md","test7.md");
        for (String filePath:filePaths) {
            File file = new File(filePath);
            if (file.exists()) {
                file.delete();
            }
        }
    }
}
