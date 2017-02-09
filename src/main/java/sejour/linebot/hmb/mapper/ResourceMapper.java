package sejour.linebot.hmb.mapper;

import org.apache.ibatis.annotations.*;
import sejour.linebot.hmb.domain.Resource;

@Mapper
public interface ResourceMapper {

    @Insert("INSERT INTO resources (name, sender, text, url) VALUES (#{name}, #{sender}, #{text}, #{url})")
    @Options(useGeneratedKeys = true)
    void insert(Resource resource);

    @Select("SELECT id, name, sender, text, url FROM resources WHERE sender = #{sender} AND text = #{text}")
    Resource selectBySenderAndText(@Param("sender") String sender, @Param("text") String text);

    @Select("SELECT EXISTS(SELECT 1 FROM resources WHERE name = #{name})")
    boolean nameIsUsed(@Param("name") String name);

}
