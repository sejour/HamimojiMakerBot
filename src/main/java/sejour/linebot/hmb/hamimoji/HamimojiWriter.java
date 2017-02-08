package sejour.linebot.hmb.hamimoji;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;

/**
 * はみ文字を出力ストリームに書き出すクラス
 */
public class HamimojiWriter {

    private final HamimojiMaker hamimojiMaker;

    public HamimojiWriter(HamimojiMaker hamimojiMaker) {
        this.hamimojiMaker = hamimojiMaker;
    }

    public void write(String text, OutputStream out) throws Exception {
        write(text, out, 0);
    }

    public void write(String text, OutputStream out, int columnNumbers) throws Exception {
        Iterator<ImageWriter> imageWriters = ImageIO.getImageWritersByFormatName("gif");
        if (!imageWriters.hasNext()) throw new Exception("No image writers.");
        ImageWriter writer = imageWriters.next();

        try (ImageOutputStream imageOutputStream = ImageIO.createImageOutputStream(out)) {
            writer.setOutput(imageOutputStream);
            writer.prepareWriteSequence(null);

            hamimojiMaker.build(text, columnNumbers).stream().forEachOrdered(frame -> {
                try {
                    writer.writeToSequence(frame, null);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });

            writer.endWriteSequence();
        }
    }

}