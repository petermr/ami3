package org.contentmine.svg2xml;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConvertPngNames {

	public static void main(String[] args) {
		File dir = new File("../../cm-ucl/table-imgs-scope/");
		File[] files = dir.listFiles();
		Pattern patern = Pattern.compile("table(\\d+)\\.png\\.\\~(\\d+)\\~");
		Pattern patern1 = Pattern.compile("table(\\d+)\\.png\\~");
		for (File file : files) {
			String name = file.getName();
			 //table1.png.~100~   
//			System.out.println(name);
			Matcher matcher = patern.matcher(name);
			String name1 = null;
			if (matcher.matches()) {
				name1 = "table"+matcher.group(1)+"."+matcher.group(2)+".png";
				System.out.println(name1);
			} else {
				matcher = patern1.matcher(name);
				if (matcher.matches()) {
					name1 = "table"+matcher.group(1)+".x.png";
				}
			}
			if (name1 == null) {
			} else {
				System.out.println(name1);
			}
			if (name1 != null) {
				file.renameTo(new File(dir, name1));
			}
		}
	}
}
