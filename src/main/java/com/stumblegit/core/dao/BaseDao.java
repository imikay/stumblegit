package com.stumblegit.core.dao;

import org.apache.ibatis.annotations.Param;

import java.io.Serializable;
import java.util.List;

public interface BaseDao<T, PK extends Serializable, E> {
    long countByExample(E example);

    int deleteByExample(E example);

    int deleteByPrimaryKey(PK id);

    int insert(T record);

    int insertSelective(T record);

    List<T> selectByExample(E example);
    List<T> selectByExampleWithBLOBs(E example);

    T selectByPrimaryKey(PK id);

    int updateByExampleWithBLOBs(@Param("record") T record, @Param("example") E example);

    int updateByExampleSelective(@Param("record") T record, @Param("example") E example);

    int updateByExample(@Param("record") T record, @Param("example") E example);

    int updateByPrimaryKeySelective(T record);

    int updateByPrimaryKey(T record);
    int updateByPrimaryKeyWithBLOBs(T record);
}