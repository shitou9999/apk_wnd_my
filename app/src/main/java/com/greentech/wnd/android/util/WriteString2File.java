package com.greentech.wnd.android.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

public class WriteString2File {
	public static void WriteStringToFile(String filePath, String str) {
		try {
			filePath=	filePath+File.separator+"zhoufazhan.txt";
			File file = new File(filePath);
			PrintStream ps = new PrintStream(new FileOutputStream(file));
			// ps.println("http://www.jb51.net");// 往文件里写入字符串
			ps.append(str);// 在已有的基础上添加字符串
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
