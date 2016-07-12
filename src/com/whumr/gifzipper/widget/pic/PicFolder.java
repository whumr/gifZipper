package com.whumr.gifzipper.widget.pic;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PicFolder {
	/**
	 * ͼƬ���ļ���·��
	 */
	private String dir;

	/**
	 * �ļ��е�����
	 */
	private String name;
	
	/**
	 * ͼƬ·��
	 */
	private List<String> pics = new ArrayList<String>();

	public String getDir() {
		return dir;
	}

	public void setDir(String dir) {
		this.dir = dir;
		dir = dir.trim();
		if (dir.endsWith(File.separator))
			dir = dir.substring(0, dir.length() - File.separator.length());
		this.name = dir.substring(dir.lastIndexOf(File.separator) + File.separator.length());
	}

	public String getName() {
		return name;
	}

	public int getCount() {
		return pics.size();
	}
	
	public List<String> getPics() {
		return pics;
	}
}