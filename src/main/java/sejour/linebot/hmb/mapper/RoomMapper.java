package sejour.linebot.hmb.mapper;

import org.apache.ibatis.annotations.*;
import sejour.linebot.hmb.domain.Room;

@Mapper
public interface RoomMapper {

    @Insert("INSERT INTO rooms (sender, column_number) VALUES (#{sender}, #{columnNumber})")
    @Options(useGeneratedKeys = true)
    void insert(Room room);

    @Select("SELECT id, sender, column_number FROM rooms WHERE sender = #{sender}")
    Room selectBySender(@Param("sender") String sender);

    @Update("UPDATE rooms SET column_number = #{columnNumber} WHERE sender = #{sender}")
    int updateColumn(@Param("sender") String sender, @Param("columnNumber") int columnNumber);

}
