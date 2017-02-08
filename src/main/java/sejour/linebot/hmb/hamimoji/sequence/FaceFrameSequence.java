package sejour.linebot.hmb.hamimoji.sequence;

import sejour.linebot.hmb.hamimoji.CharacterFrame;

import java.util.List;

/**
 * 顔文字フレームシーケンスを表す
 */
public class FaceFrameSequence extends ElementFrameSequence {

    public FaceFrameSequence(List<CharacterFrame> sequence) {
        super(sequence);
    }

    @Override
    public CharacterFrame getFrame(int index) {
        // 顔文字はフレームが3枚
        return sequence.get((index <= 0) ? 0 : ((index >= CHARACTER_FRAME_COUNT - 1) ? (FACE_FRAME_COUNT - 1) : 1));
    }

}
