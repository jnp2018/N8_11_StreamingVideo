import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Client {

    public static void calc(String hostName, int port, int num1, int num2, char sym) throws IOException {
        try (Socket socket = new Socket(hostName, port); ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream())) {
            oos.writeObject(num1);
            oos.writeObject(num2);
            oos.writeObject(sym);
        }
    }

    public static void main(String[] args) throws Exception {
        int port = 1107;
        String host = "localhost";
        Socket socket = new Socket("localhost", 1107);


        int num1[] = {1, 6, 3, 14, 7};
        int num2[] = {2, 7, 2, 7, 4};
        char sym[] = {'+', '-', '*', '/', '+'};

        try {
            for (int i = 0; i < num1.length; i++) {
                calc(host, port, num1[i], num2[i], sym[i]);
            }
        } catch (IOException ex) {
        }
    }
}