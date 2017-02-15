package sejour.linebot.hmb.hamimoji;

import com.sun.imageio.plugins.gif.GIFImageMetadata;
import lombok.NonNull;
import sejour.linebot.hmb.hamimoji.sequence.ElementFrameSequence;

import javax.imageio.IIOImage;
import javax.imageio.metadata.IIOMetadata;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * はみ文字を生成するクラス
 */
public class HamimojiBuilder {

    public final static int FRAME_SEQUENCE_SIZE = 5;
    public final static int ELEMENT_SIZE = 200;

    private Letter letter;

    public HamimojiBuilder(@NonNull Letter letter) {
        this.letter = letter;
    }

    public List<IIOImage> build() {
        List<IIOImage> frames = new ArrayList<>();

        ElementFrameSequence firstCharacter = letter.getFirstCharacter();

        // フレームサイズを計算
        int frameWidth = letter.width() * ELEMENT_SIZE;
        int frameHeight = letter.height() * ELEMENT_SIZE;

        // 結合フレームの生成
        for (int frameIndex = 0; frameIndex < FRAME_SEQUENCE_SIZE; ++frameIndex) {
            // 下地を用意
            BufferedImage theFrame = new BufferedImage(frameWidth, frameHeight, BufferedImage.TYPE_INT_ARGB);
            Graphics graphics = theFrame.getGraphics();
            graphics.setColor(Color.WHITE);
            graphics.fillRect(0, 0, frameWidth, frameHeight);

            CharacterFrame charFrame;
            GIFImageMetadata meta;

            // 行走査
            final int lineNumbers = letter.lineNumbers();
            for (int lineIndex = 0; lineIndex < lineNumbers; ++lineIndex) {
                List<ElementFrameSequence> line = letter.getLine(lineIndex);
                // 文字列分走査して対象のフレームの文字画像を連結する
                final int pointNumbers = line.size();
                for (int point = 0; point < pointNumbers; ++point) {
                    // 現在のインデックスの文字フレームシーケンスを取得し、対象のフレーム画像を取り出す
                    charFrame = line.get(point).getFrame(frameIndex);
                    if (charFrame == null) continue; /* スペース対応 */
                    meta = charFrame.metadata;

                    // 描画
                    graphics.drawImage(
                            charFrame.image,
                            (point * ELEMENT_SIZE) + meta.imageLeftPosition,
                            (lineIndex * ELEMENT_SIZE) + meta.imageTopPosition,
                            meta.imageWidth,
                            meta.imageHeight,
                            null
                    );
                }
            }

            // 1フレーム完成
            frames.add(new IIOImage(theFrame, null, generateFrameMetadata(firstCharacter.getFrame(frameIndex).metadata, frameWidth, frameHeight)));
        }

        return frames;
    }

    /**
     * フレームメタデータを生成する
     * @param source 元となるメタデータ
     * @param frameWidth フレームの横幅
     * @param frameHeight フレームの縦幅
     * @return フレームメタデータ
     */
    private static IIOMetadata generateFrameMetadata(GIFImageMetadata source, int frameWidth, int frameHeight) {
        GIFImageMetadata metadata = new GIFImageMetadata();
        metadata.imageTopPosition = 0;
        metadata.imageLeftPosition = 0;
        metadata.imageWidth = frameWidth;
        metadata.imageHeight = frameHeight;

        metadata.applicationData = source.applicationData;
        metadata.applicationIDs = source.applicationIDs;
        metadata.authenticationCodes = source.authenticationCodes;
        metadata.characterCellHeight = source.characterCellHeight;
        metadata.characterCellWidth = source.characterCellWidth;
        metadata.comments = source.comments;
        metadata.delayTime = source.delayTime;
        metadata.disposalMethod = source.disposalMethod;
        metadata.hasPlainTextExtension = source.hasPlainTextExtension;
        metadata.interlaceFlag = source.interlaceFlag;
        metadata.localColorTable = source.localColorTable;
        metadata.sortFlag = source.sortFlag;
        metadata.text = source.text;
        metadata.textBackgroundColor = source.textBackgroundColor;
        metadata.textForegroundColor = source.textForegroundColor;
        metadata.textGridHeight = source.textGridHeight;
        metadata.textGridLeft = source.textGridLeft;
        metadata.textGridTop = source.textGridTop;
        metadata.textGridWidth = source.textGridWidth;
        metadata.transparentColorFlag = source.transparentColorFlag;
        metadata.transparentColorIndex = source.transparentColorIndex;
        metadata.userInputFlag = source.userInputFlag;

        return metadata;
    }

}
