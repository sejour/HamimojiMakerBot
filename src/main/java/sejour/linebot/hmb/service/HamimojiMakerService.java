package sejour.linebot.hmb.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import sejour.linebot.hmb.hamimoji.HamimojiWriter;

import java.io.FileOutputStream;
import java.util.UUID;

@Service
public class HamimojiMakerService {

    @Autowired
    private HamimojiWriter hamimojiWriter;

    @Value("${sejour.hmb.madeUrlBase}")
    private String madeUrlBase;

    @Value("${sejour.hmb.saveDirectory}")
    private String saveDirectory;

    @Value("${sejour.hmb.defaultColumnNumber}")
    private int defaultColumnNumber;

    public String make(String text, String sender) throws Exception {
        String outFileName = UUID.randomUUID().toString() + ".gif";

        try (FileOutputStream out = new FileOutputStream(saveDirectory + "/" + outFileName)) {
            hamimojiWriter.write(text, out, defaultColumnNumber);
        }

        System.out.println("[SUCCESS MAKING] " + outFileName);
        String imageUrl = madeUrlBase + "/" + outFileName;

        return imageUrl;
    }

}
