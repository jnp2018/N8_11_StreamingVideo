package messengerclient;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class VideoManager {
    private List<BufferedImage> bufferedImageList = new ArrayList<>();
    private int curr = 0;

    public VideoManager() {
        this.curr = 0;
    }

    public void addImage(BufferedImage image) {
        this.bufferedImageList.add(image);
    }

    public BufferedImage getImage() {
        if (curr == -1) return null;
        BufferedImage result = this.bufferedImageList.get(curr);
        curr += 1;
        int top = this.bufferedImageList.size() - 1;
        if (curr > top) curr = top;
        return result;
    }

    public void next(int frame) {
        int top = this.bufferedImageList.size() - 1;
        curr += frame;
        if (curr > top) curr = top;
    }

    public void prev(int frame) {
        int bot = 0;
        curr -= frame;
        if (curr < bot) curr = bot;
    }

    public void live() {
        curr = this.bufferedImageList.size() - 1;
    }
}
