package sejour.linebot.hmb.hamimoji.sequence;

import sejour.linebot.hmb.hamimoji.CharacterFrame;

/**
 * Created by Shuka on 2017/02/14.
 */
public class WhiteSpaceFrameSequence extends ElementFrameSequence {

    public final static WhiteSpaceFrameSequence INSTANCE = new WhiteSpaceFrameSequence();

    private WhiteSpaceFrameSequence() {
        super(null);
    }

    @Override
    public CharacterFrame getFrame(int index) {
        return null;
    }

}
