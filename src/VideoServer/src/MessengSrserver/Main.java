
package MessengSrserver;

public class Main
{

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        ServerManager serverManager=new ServerManager();
        ServerMonitor monitor=new ServerMonitor(serverManager);

        monitor.setVisible(true);
    }

}
