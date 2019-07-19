package cn.com.flaginfo.platform.registered.mybatis.mapper;

import cn.com.flaginfo.platform.registered.mybatis.entity.EventFailureRecord;
import cn.com.flaginfo.platform.registered.mybatis.entity.EventFailureRecordExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

public interface EventFailureRecordMapper extends BaseMapper<EventFailureRecord,EventFailureRecordExample> {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table event_failure_record
     *
     * @mbggenerated
     */
    int countByExample(EventFailureRecordExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table event_failure_record
     *
     * @mbggenerated
     */
    int deleteByExample(EventFailureRecordExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table event_failure_record
     *
     * @mbggenerated
     */
    int insert(EventFailureRecord record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table event_failure_record
     *
     * @mbggenerated
     */
    int insertSelective(EventFailureRecord record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table event_failure_record
     *
     * @mbggenerated
     */
    List<EventFailureRecord> selectByExampleWithRowbounds(EventFailureRecordExample example, RowBounds rowBounds);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table event_failure_record
     *
     * @mbggenerated
     */
    List<EventFailureRecord> selectByExample(EventFailureRecordExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table event_failure_record
     *
     * @mbggenerated
     */
    int updateByExampleSelective(@Param("record") EventFailureRecord record, @Param("example") EventFailureRecordExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table event_failure_record
     *
     * @mbggenerated
     */
    int updateByExample(@Param("record") EventFailureRecord record, @Param("example") EventFailureRecordExample example);
}