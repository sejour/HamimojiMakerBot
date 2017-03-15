package sejour.linebot.hmb.mapper;

import org.apache.ibatis.annotations.*;
import sejour.linebot.hmb.domain.Resource;

@Mapper
public interface ResourceMapper {

    @Insert("INSERT INTO resources (name, sender, text, text_code, column_number, url) VALUES (#{name}, #{sender}, #{text}, #{textCode}, #{columnNumber}, #{url})")
    @Options(useGeneratedKeys = true)
    void insert(Resource resource);

    @Select("SELECT id, name, sender, text, text_code, column_number, url FROM resources WHERE sender = #{sender} AND text_code = #{textCode} AND column_number = #{columnNumber}")
    Resource selectBySenderAndTextAndColumn(@Param("sender") String sender, @Param("textCode") String textCode, @Param("columnNumber") int columnNumber);

    @Select("SELECT EXISTS(SELECT 1 FROM resources WHERE name = #{name})")
    boolean nameIsUsed(@Param("name") String name);

}
