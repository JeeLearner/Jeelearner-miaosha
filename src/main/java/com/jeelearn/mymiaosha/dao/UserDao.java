package com.jeelearn.mymiaosha.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.jeelearn.mymiaosha.domain.User;

@Mapper
public interface UserDao {

	@Select("select id,name from user where id = #{id}")
	public User getUserById(@Param("id") int id);

	@Insert("insert into user(id,name) values(#{id},#{name})")
	public void insert(User user);
}
