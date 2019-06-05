package com.jeelearn.mymiaosha.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jeelearn.mymiaosha.dao.UserDao;
import com.jeelearn.mymiaosha.domain.User;

@Service
public class UserService {

	@Autowired
	private UserDao userDao;
	
	public User getUserById(int id){
		return userDao.getUserById(id);
	}

	@Transactional
	public boolean tx() {
		User u1= new User();
		u1.setId(3);
		u1.setName("2222");
		userDao.insert(u1);
		
		User u2= new User();
		u2.setId(2);
		u2.setName("11111");
		userDao.insert(u2);
		
		return true;
	}
}
