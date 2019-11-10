package com.bundle.conversion;

public class Convert {

	// precondition:  d is a nonnegative integer
	public static String decimal2Hex(int decimal) {
		String digits = "0123456789ABCDEF";
		if (decimal == 0) return "0";
		String hex = "";
		while (decimal > 0) {
			int digit = decimal % 16; // rightmost digit
			hex = digits.charAt(digit) + hex;  // string concatenation
			decimal = decimal / 16;
		}
		return hex;
	}
	
	public static int hex2Decimal(String hexaDecimal) {
		hexaDecimal=hexaDecimal.replace("0x","00000000" );
		String digits = "0123456789ABCDEF";
		hexaDecimal = hexaDecimal.toUpperCase();
		int intValue = 0;
		for (int count = 0; count < hexaDecimal.length(); count++) {
			char chr = hexaDecimal.charAt(count);
			int dig = digits.indexOf(chr);
			intValue = 16*intValue + dig;
		}
		return intValue;
	}

	public static boolean hex2Boolean(String hexaDecimal) {
		int intValue=hex2Decimal(hexaDecimal);
		return (intValue!=0)?true:false;
	}

	public static float hex2Float(String hexaDecimal) {
		int intValue=hex2Decimal(hexaDecimal);
		return Float.intBitsToFloat(intValue);
	}

	public static String hex2Color(String hexaDecimal) {
		int intValue=hex2Decimal(hexaDecimal);
		return String.format("#%08X",intValue);
	}

	public static String hex2Dimension(String hexaDecimal) {
		int data=hex2Decimal(hexaDecimal);
		int type=hex2Decimal(Constant.DIMENSION);
		return TypedValue.typeValueToString(type, data);
	}

	public static int string2Decimal(String string) {
		return Integer.parseInt(string.trim());
	}

	public static String getHexFormat(String hexaDecimal) {
		int decimal=hex2Decimal(hexaDecimal);
		return  getHexFormat(decimal);
	}

	public static String getHexFormat(int decimal) {
		return  String.format("0x%08x", decimal);
	}

	public static String hex2Fraction(String hexDecimal) {
		int data=hex2Decimal(hexDecimal);
		int type=hex2Decimal(Constant.FRACTION);
		return TypedValue.typeValueToString(type, data);
	}
	
	public static String getTypedValue(String mResType,String mResValue) {
		if(mResValue.contains("#")) {
			mResValue=mResValue.replace("#", "");	
		}
		int type=hex2Decimal(mResType.trim());
		int data=hex2Decimal(mResValue.trim());
		return TypedValue.typeValueToString(type, data);
	}
}
