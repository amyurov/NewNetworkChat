import org.example.simple.parser.SimpleFileSettingsParser;
import org.junit.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class SimpleFileSettingsParserTest {
    private static File fileToRead;
    private static SimpleFileSettingsParser parser;

    @BeforeClass
    public static void createFile() {
        fileToRead = new File(".", "settings.txt");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileToRead))) {
            writer.write("set_host = localhost\n" +
                    "set_port = 8989");
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @AfterClass
    public static void deleteFile() {
        fileToRead.delete();
    }

    @Before
    public void createInstance() {
        parser = new SimpleFileSettingsParser(fileToRead);
    }

    @Test
    public void shouldReturnHostFromFile() {
        final String expected = "localhost";
        final String actual = parser.getHost();

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void shouldReturnPortFromFile() {
        final int expected = 8989;
        final int actual = parser.getPort();

        Assert.assertEquals(expected, actual);
    }
}
