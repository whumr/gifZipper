package com.whumr.gifzipper.util;

import java.util.List;

public class Validator {

	public static boolean isMobilePhone(String number) {
		if (number != null && number.length() == 11 && number.charAt(0) == '1') {
			try {
				Long.parseLong(number);
			} catch (NumberFormatException e) {
				return false;
			}
			return true;
		}
		return false;
	}
	
	public static boolean isPassword(String password) {
		if (password != null && password.length() > 5)
			return true;
		return false;
	}
	
	public static boolean isEmptyString(String str) {
		return str == null || str.trim().length() == 0;
	}

	public static boolean isEmptyList(List<?> list) {
		return list == null || list.isEmpty();
	}
}
