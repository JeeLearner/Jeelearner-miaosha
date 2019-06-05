package com.jeelearn.mymiaosha.utils;

import java.io.File;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.jeelearn.mymiaosha.domain.MiaoshaUser;

public class UserUtil {
	
	public static void createUser(int count) throws Exception{
		List<MiaoshaUser> users = new ArrayList<MiaoshaUser>(count);
		for(int i=0; i<count; i++){
			MiaoshaUser user = new MiaoshaUser();
			user.setId(13000000000L+i);
			user.setLoginCount(1);
			user.setNickname("user"+i);
			user.setRegisterDate(new Date());
			user.setSalt("1a2b3c");
			user.setPassword(MD5Util.inputPassToDbPass("123456", user.getSalt()));
			users.add(user);
		}
		System.out.println("create user finished");
		
		//插入数据库
		Connection conn = DBUtil.getConn();
		String sql = "insert into miaosha_user(id,nickname,password,salt,register_date,login_count) values(?,?,?,?,?,?)";
		PreparedStatement pstmt = conn.prepareStatement(sql);
		int userSize = users.size();
		for(int i=0;i<userSize;i++){
			MiaoshaUser user = users.get(i);
			pstmt.setLong(1, user.getId());
			pstmt.setString(2, user.getNickname());
			pstmt.setString(3, user.getPassword());
			pstmt.setString(4, user.getSalt());
			pstmt.setTimestamp(5, new Timestamp(user.getRegisterDate().getTime()));
			pstmt.setInt(6, user.getLoginCount());
			pstmt.addBatch();
		}
		pstmt.executeBatch();
		pstmt.close();
		conn.close();
		System.out.println("insert to db finished");
		
		//登录，生成token
		String urlString = "http://localhost:8001/login/do_login";
		File file = new File("D:/tokens.txt");
		if(file.exists()){
			file.delete();
		}
		RandomAccessFile raf = new RandomAccessFile(file, "rw");
		file.createNewFile();
		raf.seek(0);
		for(int i=0;i<userSize;i++){
			MiaoshaUser user = users.get(i);
			URL url = new URL(urlString);
			HttpURLConnection co = (HttpURLConnection)url.openConnection();
			co.setRequestMethod("POST");
			co.setDoOutput(true);
			OutputStream out = co.getOutputStream();
			String params = "mobile="+user.getId()+"&password="+MD5Util.inputPassToFormPass("123456");
			out.write(params.getBytes());
			out.flush();
			
		
		}
	}
	
	public static void main(String[] args) throws Exception {
		UserUtil.createUser(2);
	}
}
