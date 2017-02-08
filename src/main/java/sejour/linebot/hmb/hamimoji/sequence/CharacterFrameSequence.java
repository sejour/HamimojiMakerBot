package sejour.linebot.hmb.hamimoji.sequence;

import sejour.linebot.hmb.hamimoji.CharacterFrame;

import java.util.List;

/**
 * 文字フレームシーケンスを表す
 */
public final class CharacterFrameSequence extends ElementFrameSequence {

    public CharacterFrameSequence(List<CharacterFrame> sequence) {
        super(sequence);
    }

    @Override
    public CharacterFrame getFrame(int index) {
        return sequence.get(index);
    }

}
