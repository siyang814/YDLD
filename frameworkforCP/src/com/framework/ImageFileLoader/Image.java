package com.framework.ImageFileLoader;

import java.io.Serializable;

import android.graphics.Bitmap;

/**
 * ͼƬ�� ����ͼƬmd5,url,λͼ
 * 
 * @author  Apr 20, 2011
 */
public class Image implements Serializable
{
	private String md5;// ͼƬmd5
	private String url;// ͼƬurl��ַ
	private int resId;// ��Դid
	private Bitmap bitmap;// λͼ

	public String getMd5()
	{
		return md5;
	}

	public void setMd5(String md5)
	{
		this.md5 = md5;
	}

	public String getUrl()
	{
		return url;
	}

	public void setUrl(String url)
	{
		this.url = url;
	}

	public Bitmap getBitmap()
	{
		return bitmap;
	}

	public void setBitmap(Bitmap bitmap)
	{
		this.bitmap = bitmap;
	}

	public int getResId()
	{
		return resId;
	}

	public void setResId(int resId)
	{
		this.resId = resId;
	}

}
