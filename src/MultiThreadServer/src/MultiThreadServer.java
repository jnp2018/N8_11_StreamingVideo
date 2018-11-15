import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MultiThreadServer implements Runnable {
    Socket csocket;
    MultiThreadServer(Socket csocket) {
        this.csocket = csocket;
   }
    public static void main(String args[]) { 
        try {
            ServerSocket ssock = new ServerSocket(1107);
            System.out.println("Listening");
            
            while (true) {
                Socket sock = ssock.accept();
                new Thread(new MultiThreadServer(sock)).start();
                Thread.sleep(1000);
            }
        } catch (Exception ex) {
            Logger.getLogger(MultiThreadServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    @Override
    public void run() {
        try {
            ObjectInputStream ois = new ObjectInputStream(csocket.getInputStream());
            
            int num1 = (int) ois.readObject();
            int num2 = (int) ois.readObject();
            char sym = (char) ois.readObject();
            
            String result = Thread.currentThread().getName() + " : " + num1 + " " + sym + " " + num2 + " = " + calc(num1, num2, sym);
            System.out.println(result);

            ObjectOutputStream oos = new ObjectOutputStream(csocket.getOutputStream());
            oos.writeObject(result);

            csocket.close();
        }catch (IOException e) {
      }catch (Exception ex) {
            Logger.getLogger(MultiThreadServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public int calc(int num1, int num2, char sym){
        int temp;
        switch(sym){
            case '+':
                temp = num1 + num2;
                break;
            case '-':
                temp = num1 - num2;
                break;
            case '*':
                temp = num1 * num2;
                break;
            case '/':
                temp = num1 / num2;
                break;
            default:
                return 0;
        }
        return temp;
    }
}