package com.jeelearn.mymiaosha.result;

public class Result<T> {

	private int code;
	private String msg;
	private T data;
	
	/*
	 * sucess
	 */
	public static <T> Result<T> success(T data){
		return new Result<T>(data);
	}
	/*
	 * error
	 */
	public static <T> Result<T> error(CodeMsg codeMsg){
		return new Result<T>(codeMsg);
	}

	
	private Result(T data) {
		this.code = 0;
		this.msg = "success";
		this.data = data;
	}
	
	private Result(int code, String msg) {
		this.code = code;
		this.msg = msg;
	}
	
	private Result(CodeMsg codeMsg) {
		if(codeMsg == null){
			return ;
		}
		this.code = codeMsg.getCode();
		this.msg = codeMsg.getMsg();
	}
	
	
	
	public int getCode() {
		return code;
	}

	public String getMsg() {
		return msg;
	}

	public T getData() {
		return data;
	}
}
