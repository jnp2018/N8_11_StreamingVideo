package messengerclient;
import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamDiscoveryEvent;
import com.github.sarxos.webcam.WebcamDiscoveryListener;

import java.util.ArrayList;
import java.util.List;

public class WebcamsDiscoveryListener implements WebcamDiscoveryListener{
    List<String> cameraList = new ArrayList<>();

    public WebcamsDiscoveryListener() {
        for (Webcam webcam : Webcam.getWebcams()) {
            cameraList.add(webcam.getName());
            System.out.println("Webcam detected: " + webcam.getName());
        }
        Webcam.addDiscoveryListener(this);
        System.out.println("\n\nPlease connect additional webcams, or disconnect already connected ones. Listening for events...");
    }
    public List<String> getCameraList(){
        return cameraList;
    }
    @Override
    public void webcamFound(WebcamDiscoveryEvent event) {
        cameraList.add(event.getWebcam().getName());
        System.out.format("Webcam connected: %s \n", event.getWebcam().getName());
    }

    @Override
    public void webcamGone(WebcamDiscoveryEvent event) {
        cameraList.remove(event.getWebcam().getName());
        System.out.format("Webcam disconnected: %s \n", event.getWebcam().getName());
    }

    public static void main(String[] args) throws Throwable {
        new WebcamsDiscoveryListener();
        Thread.sleep(120000);
        System.out.println("Bye!");
    }
}
