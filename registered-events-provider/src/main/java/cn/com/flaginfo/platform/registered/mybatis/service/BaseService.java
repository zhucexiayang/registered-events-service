package cn.com.flaginfo.platform.registered.mybatis.service;

import cn.com.flaginfo.platform.registered.commons.util.PageForm;
import cn.com.flaginfo.platform.registered.mybatis.base.BaseEntity;
import cn.com.flaginfo.platform.registered.mybatis.entity.example.BaseExample;
import org.apache.ibatis.session.RowBounds;

import java.util.List;

public interface BaseService<Q extends BaseEntity,P extends BaseExample> {
    int countByExample(P example);

    int deleteByExample(P example);

    int insert(Q record);

    int insertSelective(Q record);

    Long insertReturnKey(Q record);

    void insertBatch(List<Q> list);

    int updateByExmapleSelective(Q record ,P example);

    List<Q> selectByExampleWithRowbounds(P example, RowBounds rowBounds);

    List<Q> selectByExample(P example);

    /**
     *
     * 条件分页查询
     *
     *@author HaiFeng.Yang
     *@param  * @param null
     *@return
     *@date 10:15 2019/7/19
     */
    PageForm<Q> listPage(P exmaple,PageForm<Q> pageParams);

    Q findOne(Long id);

}
