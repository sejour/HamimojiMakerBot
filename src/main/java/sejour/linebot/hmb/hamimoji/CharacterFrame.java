package sejour.linebot.hmb.hamimoji;

import com.sun.imageio.plugins.gif.GIFImageMetadata;

import javax.imageio.metadata.IIOMetadata;
import java.awt.image.BufferedImage;

/**
 * Hamimojiの生成に使用されるGIF形式の文字画像
 */
public class CharacterFrame {

    public final BufferedImage image;
    public final GIFImageMetadata metadata;

    public CharacterFrame(BufferedImage image, IIOMetadata metadata) {
        this.image = image;
        this.metadata = (GIFImageMetadata) metadata;
    }

}