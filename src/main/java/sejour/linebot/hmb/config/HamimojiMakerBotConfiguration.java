package sejour.linebot.hmb.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import sejour.linebot.hmb.hamimoji.HamimojiMaker;
import sejour.linebot.hmb.hamimoji.HamimojiWriter;

@Configuration
public class HamimojiMakerBotConfiguration {

    @Value("${sejour.hmb.hamimojiAssetsDirectory}")
    private String hamimojiAssetsDirectory;

    @Bean
    public HamimojiWriter hamimojiWriter() throws Exception {
        return new HamimojiWriter(new HamimojiMaker(hamimojiAssetsDirectory));
    }

}
