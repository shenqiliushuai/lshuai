package com.les.ls.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;

public final class MD5Util {

	/**
	 * 检查MD5签名
	 * @param param
	 * @param sign
	 * @param md5Key
	 * @return
	 */
	public static Boolean checkMD5Sign(String param ,String sign,String md5Key) {
		String newparams = param + md5Key;
		try {
			String str = MD5Util.createMD5(newparams);
			if (str.equalsIgnoreCase(sign)) {
				return true;
			}else{
				return false;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}

	/**
	 * <p>
	 * 字符串生成MD5
	 * </p>
	 *
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public static String createMD5(String input) {
		try {
			return createMD5(input, null).toLowerCase();
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	/**
	 * <p>
	 * 字符串生成MD5
	 * </p>
	 *
	 * @param input
	 * @param charset 编码(可选)
	 * @return
	 * @throws Exception
	 */
	public static String createMD5(String input, String charset) throws Exception {
		String mdString = null;
		if (input != null) {
			try {
				mdString = getMD5(input.getBytes("UTF-8"));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return mdString;
	}

	/**
	 * 获得MD5加密字符串
	 * @param source 源字节数组
	 * @return 加密后的字符串
	 */
	public static String getMD5(byte[] source) {
		String s = null;
		char [] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8',
				'9', 'A', 'B', 'C', 'D', 'E', 'F'};
		final int temp = 0xf;
		final int arraySize = 32;
		final int strLen = 16;
		final int offset = 4;
		try {
			MessageDigest md = MessageDigest
					.getInstance("MD5");
			md.update(source);
			byte [] tmp = md.digest();
			char [] str = new char[arraySize];
			int k = 0;
			for (int i = 0; i < strLen; i++) {
				byte byte0 = tmp[i];
				str[k++] = hexDigits[byte0 >>> offset & temp];
				str[k++] = hexDigits[byte0 & temp];
			}
			s = new String(str);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}

	/**
	 * 32位MD5
	 * @param data
	 * @return
	 * @throws Exception
     */
	public static String md5_32(String data) throws Exception {
		StringBuffer result = new StringBuffer();
		MessageDigest md5 = MessageDigest.getInstance("MD5");
		md5.update(data.getBytes("UTF-8"));
		byte[] b = md5.digest();
		for (int i = 0; i < b.length; ++i) {
			int x = b[i] & 0xFF;
			int h = x >>> 4;
			int l = x & 0x0F;
			result.append((char) (h + ((h < 10) ? '0' : 'a' - 10)));
			result.append((char) (l + ((l < 10) ? '0' : 'a' - 10)));
		}
		return result.toString();
	}

	/**
	 * 16位MD5
	 * @param data
	 * @return
	 * @throws Exception
     */
	public static String md5_16(String data) throws Exception {
		return md5_32(data).substring(8, 24);
	}
	public static void main(String args[]) throws  Exception{
		long timeStamp = 1503648879000l;
		int accessId = 10004;
		int skipSize = 0;
		String extName = "jpg";
		String storagePath="group1/M00/06/DE/Cjs5WVmgDZ-EAy5-AAAAAAAAAAA680.jpg";

		StringBuffer sb = new StringBuffer();
		sb.append(timeStamp);
		sb.append(accessId);
		sb.append(skipSize);
		sb.append(extName);
		sb.append("3539f7426826410d8b8581195d2d1980");


//		StringBuffer sb = new StringBuffer();
//		sb.append(timeStamp);
//		sb.append(accessId);
//		sb.append(storagePath);
//		sb.append("3539f7426826410d8b8581195d2d1979");

         //6e37ba14d08dac13596a27df4e9b879f
		//c2c3b39e5fe7437281a60ac5df4d79f2
		System.out.println("1111===="+createMD5(sb.toString()));
	}
}
