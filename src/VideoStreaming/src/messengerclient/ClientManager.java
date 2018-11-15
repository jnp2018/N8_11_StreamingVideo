
package messengerclient;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class ClientManager {
    ExecutorService clientExecutor;
    Socket clientSocket;
    boolean isConnected = false;

    ObjectInputStream input;
    ObjectOutputStream output;
    MessageRecever messageRecever;

    public ClientManager() {
        clientExecutor = Executors.newCachedThreadPool();
    }

    public void connect(ClientStatusListener clientStatus, String sServerAddress, String sPort) {
        try {
            if (isConnected)
                return;
            else {
                clientSocket = new Socket(sServerAddress, Integer.parseInt(sPort));
                clientStatus.loginStatus("You are connected to :" + sServerAddress);
                isConnected = true;
            }
        } catch (UnknownHostException ex) {
            clientStatus.loginStatus("No Server found");
        } catch (IOException ex) {
            clientStatus.loginStatus("No Server found");
        }
    }

    public void disconnect(ClientStatusListener clientStatus) {
        messageRecever.stopListening();
        try {
            clientStatus.loginStatus("You are no longer connected to Server");
            clientSocket.close();
        } catch (IOException ex) {
        }
    }

    public void sendMessage(String message) {
        clientExecutor.execute(new MessageSender(message));
    }

    boolean flageoutput = true;

    class MessageSender implements Runnable {
        String message;

        public MessageSender(String getMessage) {
            if (flageoutput) {
                try {
                    output = new ObjectOutputStream(clientSocket.getOutputStream());
                    output.flush();
                    flageoutput = false;
                } catch (IOException ex) {
                }
            }
            message = getMessage;
            System.out.println("user is sending   " + message);
        }

        public void run() {
            try {
                output.writeObject(message);
                output.flush();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void receiveMessage(ClientListListener getClientListListener, ClientWindowListener getClientWindowListener) {
        messageRecever = new MessageRecever(clientSocket, getClientListListener, getClientWindowListener, this);
        clientExecutor.execute(messageRecever);
    }
}
