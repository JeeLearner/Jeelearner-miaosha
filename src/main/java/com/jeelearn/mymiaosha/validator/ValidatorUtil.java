package com.jeelearn.mymiaosha.validator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

public class ValidatorUtil {

	private static final Pattern mobile_pattern = Pattern.compile("1\\d{10}");
	
	public static boolean isMobile(String value) {
		if(StringUtils.isEmpty(value)){
			return false;
		} 
		Matcher matcher = mobile_pattern.matcher(value);
		return matcher.matches();
	}

}
