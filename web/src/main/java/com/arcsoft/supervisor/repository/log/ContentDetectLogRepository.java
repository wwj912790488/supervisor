package com.arcsoft.supervisor.repository.log;

import com.arcsoft.supervisor.model.domain.log.ContentDetectLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Repository class for content detect result.
 *
 * @author zw.
 */
public interface ContentDetectLogRepository extends JpaRepository<ContentDetectLog, Long>,
        JpaContentDetectLogRepository, QueryDslPredicateExecutor<ContentDetectLog> {

    @Query(value = "select * from content_detect_log log order by log.id desc limit 1", nativeQuery = true)
    public ContentDetectLog findLatestOne();

   /* @Modifying
    @Query("delete from ContentDetectLog as log where log.channelId = :channelId")
    public List<ContentDetectLog> deleteLogToChannelId(@Param("channelId")Integer channelId);*/

    public void deleteByChannelId(Integer channelId);

    public ContentDetectLog findByGuid(String guid);

    public List<ContentDetectLog> findByChannelIdAndTypeAndStartTimeLessThanAndStartTimeGreaterThanAndGuidNot(Integer channelId, int type, long upThreshold, long lowThreshold, String guid);
    
    @Query(value = "select * from content_detect_log log where channel_name like %?1% order by log.id desc limit 20", nativeQuery = true)
    public List<ContentDetectLog> getByChannelName(String channelName);

    public List<ContentDetectLog> findByVideoFilePath(String path);

    public List<ContentDetectLog> findByChannelNameAndTypeAndStartTimeGreaterThanEqualAndStartTimeLessThan(String channelName, String type, long lowThreshold, long upThreshold );

    public List<ContentDetectLog> findByChannelNameAndStartTimeGreaterThanEqualAndStartTimeLessThan(String channelName, long lowThreshold, long upThreshold );


    @Modifying
    @Query("update ContentDetectLog c set c.endTime = :endTime where c.taskId = :taskId and c.endTime = 0 and c.startTime < :endTime")
    public void updateEndTime(@Param("taskId") Integer taskId, @Param("endTime") Long endTime);
    
//    @Query(value = "select COUNT(*) from content_detect_log log where channel_name like %?1% order by log.id desc", nativeQuery = true)
//    public int getCountByChannelName(String channelName);
    
}
