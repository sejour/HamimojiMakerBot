package sejour.linebot.hmb.mapper;

import org.apache.ibatis.annotations.*;
import sejour.linebot.hmb.domain.Resource;

@Mapper
public interface ResourceMapper {

    @Insert("INSERT INTO resources (name, text, text_code, column_number) VALUES (#{name}, #{text}, #{textCode}, #{columnNumber})")
    @Options(useGeneratedKeys = true)
    void insert(Resource resource);

    @Select("SELECT id, name, text, text_code, column_number FROM resources WHERE text_code = #{textCode} AND column_number = #{columnNumber}")
    Resource selectByTextAndColumn(@Param("textCode") String textCode, @Param("columnNumber") int columnNumber);

    @Select("SELECT EXISTS(SELECT 1 FROM resources WHERE name = #{name})")
    boolean nameIsUsed(@Param("name") String name);

    @Update("UPDATE resources SET use_count = use_count + 1 WHERE text_code = #{textCode} AND column_number = #{columnNumber}")
    void reused(@Param("textCode") String textCode, @Param("columnNumber") int columnNumber);

}
