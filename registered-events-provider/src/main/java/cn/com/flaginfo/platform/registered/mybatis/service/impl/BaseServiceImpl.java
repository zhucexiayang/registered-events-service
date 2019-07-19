package cn.com.flaginfo.platform.registered.mybatis.service.impl;

import cn.com.flaginfo.platform.registered.commons.util.PageForm;
import cn.com.flaginfo.platform.registered.mybatis.base.BaseEntity;
import cn.com.flaginfo.platform.registered.mybatis.entity.example.BaseExample;
import cn.com.flaginfo.platform.registered.mybatis.mapper.BaseMapper;
import cn.com.flaginfo.platform.registered.mybatis.service.BaseService;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public abstract  class BaseServiceImpl<Q extends BaseEntity, P extends BaseExample> implements BaseService<Q,P> {
    @Autowired
    private BaseMapper<Q,P> bpMapper;
    @Override
    public int countByExample(P example) {
        return bpMapper.countByExample(example);
    }

    @Override
    public int deleteByExample(P example) {
        return bpMapper.deleteByExample(example);
    }

    @Override
    public int insert(Q record) {
        return bpMapper.insert(record);
    }

    @Override
    public int insertSelective(Q record) {
        return bpMapper.insertSelective(record);
    }

    @Override
    public Long insertReturnKey(Q record) {
        bpMapper.insertSelective(record);
        return record.getId();
    }

    @Override
    public void insertBatch(List<Q> list) {
        if(list!=null&& list.size()>0){
            list.forEach(item ->{
                bpMapper.insertSelective(item);
            });
        }
    }

    @Override
    public int updateByExmapleSelective(Q record, P example) {
        return bpMapper.updateByExampleSelective(record,example);
    }

    @Override
    public List<Q> selectByExampleWithRowbounds(P example, RowBounds rowBounds) {
        return bpMapper.selectByExampleWithRowbounds(example,rowBounds);
    }

    @Override
    public List<Q> selectByExample(P example) {
        return bpMapper.selectByExample(example);
    }

    @Override
    public PageForm<Q> listPage(P exmaple, PageForm<Q> pageParams) {
        PageForm<Q> result=new PageForm<>();
        int currentPage=pageParams.getPage(),pageSize=pageParams.getPageSize();
        RowBounds rowBounds=new RowBounds((currentPage-1)*pageSize,pageSize);
        List<Q> list=bpMapper.selectByExampleWithRowbounds(exmaple,rowBounds);
        int counts=bpMapper.countByExample(exmaple);
        result.setTotalRows(counts);
        int totalPages=counts%pageSize==0?counts/pageSize:counts%pageSize+1;
        result.setTotalRows(totalPages);
        result.setData(list);
        result.setParams(pageParams.getParams());
        result.setPage(pageParams.getPage());
        result.setPageSize(pageParams.getPageSize());
        return result;
    }

    @Override
    public Q findOne(Long id) {
        return bpMapper.selectByPrimaryKey(id);
    }
}
