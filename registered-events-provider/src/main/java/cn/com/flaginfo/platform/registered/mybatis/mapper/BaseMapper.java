package cn.com.flaginfo.platform.registered.mybatis.mapper;

import cn.com.flaginfo.platform.registered.mybatis.base.BaseEntity;
import cn.com.flaginfo.platform.registered.mybatis.entity.example.BaseExample;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import java.util.List;

public interface  BaseMapper<P extends BaseEntity,Q extends BaseExample> {
    int countByExample(Q example);

    int deleteByExample(Q example);

    int insert(P entity);

    int insertSelective(P entity);

    List<P> selectByExampleWithRowbounds(Q example, RowBounds rowBounds);

    List<P> selectByExample(Q example);

    int updateByExampleSelective(@Param("record") P record, @Param("example") Q example);

    P selectByPrimaryKey(@Param("id") Long id);

}
