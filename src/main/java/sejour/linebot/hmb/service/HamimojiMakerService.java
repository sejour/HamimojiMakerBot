package sejour.linebot.hmb.service;

import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import sejour.linebot.hmb.domain.Resource;
import sejour.linebot.hmb.domain.Room;
import sejour.linebot.hmb.error.UserErrorException;
import sejour.linebot.hmb.hamimoji.HamimojiWriter;
import sejour.linebot.hmb.mapper.ResourceMapper;
import sejour.linebot.hmb.mapper.RoomMapper;

import java.io.FileOutputStream;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

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

    @Value("${sejour.hmb.resourceNameCharas}")
    private String resourceNameCharas;

    @Value("${sejour.hmb.resourceNameLenght}")
    private int resourceNameLength;

    @Autowired
    private RoomMapper roomMapper;

    @Autowired
    private ResourceMapper resourceMapper;

    private SecureRandom random;

    public HamimojiMakerService() throws NoSuchAlgorithmException {
        this.random = SecureRandom.getInstance("SHA1PRNG");
    }

    /**
     * はみ文字画像を生成し、画像のURLを返す
     * @param text 入力テキスト
     * @param sender 送信ユーザID
     * @return 生成された画像のURL
     * @throws Exception
     */
    public String make(@NonNull String text, @NonNull String sender) throws Exception {
        if (text.trim().isEmpty()) {
            throw new UserErrorException("文字を入力してください。");
        }

        // カラム数を取得すためにRoomを取り出す
        Room room = roomMapper.selectBySender(sender);
        if (room == null) {
            room = new Room(sender, defaultColumnNumber);
            roomMapper.insert(room);
        }

        String textCode = getTextCode(text);
        int columnNumber = room.getColumnNumber();

        // リソースが既に存在すれば再利用
        Resource resource = resourceMapper.selectBySenderAndTextAndColumn(sender, textCode, columnNumber);
        if (resource != null) {
            return resource.getUrl();
        }

        String resourceName = generateResourceName();
        while (resourceMapper.nameIsUsed(resourceName)) {
            resourceName = generateResourceName();
        }

        String fileName = resourceName + ".gif";

        // はみ文字を生成してGIFをストレージに保存
        try (FileOutputStream out = new FileOutputStream(saveDirectory + "/" + fileName)) {
            hamimojiWriter.write(text, out, columnNumber);
        }

        String imageUrl = madeUrlBase + "/" + fileName;

        // リソース登録
        resourceMapper.insert(new Resource(resourceName, sender, text, textCode, columnNumber, imageUrl));

        return imageUrl;
    }

    /**
     * ランダム英数字のリソース名を作成する
     * @return リソース名
     */
    private String generateResourceName() {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < resourceNameLength; ++i) {
            builder.append(resourceNameCharas.charAt(random.nextInt(resourceNameCharas.length())));
        }

        return builder.toString();
    }

    /**
     * テキストコード（コードポイント列）を取得する
     * @param text テキスト
     * @return テキストコード
     */
    private String getTextCode(String text) {
        int charsLength = text.length();
        StringBuilder builder = new StringBuilder();

        for (int idx = 0; idx < charsLength; idx = text.offsetByCodePoints(idx, 1)) {
            builder.append(text.codePointAt(idx));
        }

        return builder.toString();
    }


}
