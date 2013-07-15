package com.framework.Util;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

public class DrawableToBitmapAndtoRoundCorner {
	
	private static final int PIXELS = 15;
	
	public static Bitmap toBitmapAndRoundCorner(Drawable imageDrawable) {
		BitmapDrawable bitmapDrawable = (BitmapDrawable) imageDrawable; 

		Bitmap bitmap = bitmapDrawable.getBitmap();
		
		Bitmap b = ImagetoRoundCorner.toRoundCorner(bitmap, PIXELS);
		
		return b;
	} 
}
