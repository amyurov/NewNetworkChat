import org.example.server.ClientHandler;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.powermock.reflect.Whitebox;

import java.io.*;
import java.net.Socket;

public class ClientHandlerTest {

    private static File inputFile;
    private static ClientHandler cl;
    private static Socket socketMock;
    private static BufferedWriter writer;
    private static BufferedReader reader;

    @BeforeClass
    public static void setUp() {
        inputFile = new File(".", "dataTest.txt");
        socketMock = Mockito.mock(Socket.class);
        try {
            Mockito.when(socketMock.getOutputStream()).thenReturn(new FileOutputStream(inputFile));
            Mockito.when(socketMock.getInputStream()).thenReturn(new FileInputStream(inputFile));

            writer = new BufferedWriter(new OutputStreamWriter(socketMock.getOutputStream()));
            reader = new BufferedReader(new InputStreamReader(socketMock.getInputStream()));

            cl = new ClientHandler(socketMock);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void messageToClientShouldBeSent() {
        String sentMessage = "Сообщение клиенту";
        String writtenMessage = " ";
        try {
            Whitebox.invokeMethod(cl, "messageToClient", sentMessage);
            String currentLine;
            while ((currentLine = reader.readLine()) != null) {
                writtenMessage = currentLine;
            }
            Assert.assertEquals(sentMessage, writtenMessage);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void broadcastMessageShouldBeSent() {
        String sentMessage = "Сообщение всем";
        String writtenMessage = " ";
        try {
            Whitebox.invokeMethod(cl, "broadcastMessage", sentMessage);
            String currentLine;
            while ((currentLine = reader.readLine()) != null) {
                writtenMessage = currentLine.substring(5);
            }
            Assert.assertEquals(sentMessage, writtenMessage);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void messageFromServerShouldBeSent() {
        ClientHandler secondCl = new ClientHandler(socketMock);
        String sentMessage = "Сообщение всем от сервера";
        String writtenMessage = " ";
        try {
            Whitebox.invokeMethod(secondCl, "broadcastMessageFromServer", sentMessage);
            String currentLine;
            while ((currentLine = reader.readLine()) != null) {
                writtenMessage = currentLine.substring(8);
            }
            Assert.assertEquals(sentMessage, writtenMessage);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
