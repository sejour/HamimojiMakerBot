package sejour.linebot.hmb.hamimoji.sequence;

import sejour.linebot.hmb.hamimoji.CharacterFrame;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

/**
 * 文字要素のフレームシーケンスを表す
 */
public abstract class ElementFrameSequence {

    public final static int CHARACTER_FRAME_COUNT = 5;
    public final static int FACE_FRAME_COUNT = 3;

    protected final List<CharacterFrame> sequence;
    public final int length;

    public ElementFrameSequence(List<CharacterFrame> sequence) {
        this.sequence = sequence;
        this.length = sequence.size();
    }

    public abstract CharacterFrame getFrame(int index);

    public Stream<CharacterFrame> stream() {
        return sequence.stream();
    }

    public static ElementFrameSequence load(String imageFileName) throws Exception {
        Iterator<ImageReader> imageReaders = ImageIO.getImageReadersByFormatName("gif");
        if (!imageReaders.hasNext()) throw new Exception("Image reader is not found.");
        ImageReader reader = imageReaders.next();

        List<CharacterFrame> sequence = new ArrayList<>();
        try (ImageInputStream imageInputStream = ImageIO.createImageInputStream(new FileInputStream(imageFileName))) {
            reader.setInput(imageInputStream);

            for (int i = 0;; ++i) {
                try {
                    sequence.add(new CharacterFrame(reader.read(i), reader.getImageMetadata(i)));
                } catch (IndexOutOfBoundsException e) {
                    break;
                }
            }
        }

        switch (sequence.size()) {
            case FACE_FRAME_COUNT:
                return new FaceFrameSequence(sequence);
            case CHARACTER_FRAME_COUNT:
                return new CharacterFrameSequence(sequence);
            default:
                break;
        }

        throw new Exception("Frame count of the character image is not correspond.");
    }

}
