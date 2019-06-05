package com.jeelearn.mymiaosha.exception;

import com.jeelearn.mymiaosha.result.CodeMsg;

public class GlobalException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	private CodeMsg codeMsg;
	
	public GlobalException(CodeMsg cm){
		super(cm.toString());
		this.codeMsg = cm;
	}
	
	public CodeMsg getCodeMsg() {
		return codeMsg;
	}
	

}
