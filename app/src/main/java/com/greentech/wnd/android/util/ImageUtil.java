package com.greentech.wnd.android.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import com.greentech.wnd.android.constant.Constant;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

/**
 * 图片工具类
 * @author wlj
 *
 */
public final class ImageUtil {

	private ImageUtil() {
	}

	/**
	 * 获得调整后的图片大小，如果宽高都是0，则直接返回原尺寸Bitmap
	 * @return
	 */
	public static Bitmap getAjustedBitmap(Resources res, int resId, int reqWidth, int reqHeight) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		//仅返回图片的bounds属性，实际图片无
		options.inJustDecodeBounds = true;
		//仅返回图片的bounds属性，实际图片无，options取得了bounds
		BitmapFactory.decodeResource(res, resId, options);
		
		//计算InSampleSize缩放比例，
		options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
		//重新对来源图片进行解码
		options.inJustDecodeBounds = false;
		
		return BitmapFactory.decodeResource(res, resId, options);
	}


	/**
	 * 获得调整后的图片大小，如果宽高都是0，则直接返回原尺寸Bitmap
	 * @return
	 */
	public static Bitmap getAjustedBitmap(InputStream is, int reqWidth, int reqHeight) {
		//is只能读取一次
		byte[] buffer = InputStream2Bytes(is);
		BitmapFactory.Options options = new BitmapFactory.Options();
		//仅返回图片的bounds属性，实际图片无
		options.inJustDecodeBounds = true;
		Rect rect = null;
		try {
			//仅返回图片的bounds属性，实际图片无，options取得了bounds
			BitmapFactory.decodeStream(new ByteArrayInputStream(buffer), rect, options);
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		
		//计算InSampleSize缩放比例，
		options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
		//重新对来源图片进行解码
		options.inJustDecodeBounds = false;
		
		return BitmapFactory.decodeStream(new ByteArrayInputStream(buffer), rect, options);
	}


	/**
	 * 获得调整后的图片大小，如果宽高都是0，则直接返回原尺寸Bitmap
	 * @return
	 */
	public static Bitmap getAjustedBitmap(String imgPath, int reqWidth, int reqHeight) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		//仅返回图片的bounds属性，实际图片无
		options.inJustDecodeBounds = true;
		try {
			//仅返回图片的bounds属性，实际图片无，options取得了bounds
			BitmapFactory.decodeFile(imgPath, options);
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		
		//计算InSampleSize缩放比例，
		options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
		
		//重新对来源图片进行解码
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeFile(imgPath, options);
	}


	/**
	 * 获得调整后的图片大小，如果宽高都是0，则直接返回原尺寸Bitmap
	 * @return
	 */
	public static Bitmap getAjustedBitmap(Bitmap bitmap, int reqWidth, int reqHeight) {
		InputStream is = Bitmap2InputStream(bitmap, 100);
		BitmapFactory.Options options = new BitmapFactory.Options();
		//仅返回图片的bounds属性，实际图片无
		options.inJustDecodeBounds = true;
		try {
			//仅返回图片的bounds属性，实际图片无，options取得了bounds
			BitmapFactory.decodeStream(is, null, options);
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		
		//计算InSampleSize缩放比例，
		options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
		//重新对来源图片进行解码
		options.inJustDecodeBounds = false;
		
		return BitmapFactory.decodeStream(is, null, options);
	}
	
	/**
	 * 计算缩放比例，返回宽、高缩放比例中较小的取整来缩放，
	 * @param options
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
	private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
		int width = options.outWidth;
		int height = options.outHeight;
		int inSampleSize = 1;//默认原尺寸
		if(width > reqWidth && reqWidth > 0 || height > reqHeight && reqHeight > 0) {
			int wr = 0;
			int hr = 0;
			if(reqWidth > 0) {
				wr = Math.round(width / reqWidth);
			}
			if(reqHeight > 0) {
				hr = Math.round(height / reqHeight);
			}
			inSampleSize = Math.max(wr, hr);
		}
		return inSampleSize;
	}
	
	/**
	 * 压缩图片大小  至100kb以内
	 * @param image
	 * @return
	 */
    public static Bitmap compressImage(Bitmap image) {  
  
        ByteArrayOutputStream baos = new ByteArrayOutputStream();  
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中  
        int options = 100;  
        while ( baos.toByteArray().length / 1024>100) {  //循环判断如果压缩后图片是否大于100kb,大于继续压缩         
            baos.reset();//重置baos即清空baos  
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中  
            options -= 10;//每次都减少10  
        }  
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中  
        Bitmap bitmap = BitmapFactory.decodeStream(isBm);
        return bitmap;  
    }
	

	/**
	 * 1、将byte[]转换成InputStream  
	 * @param b
	 * @return
	 */
    public static InputStream Byte2InputStream(byte[] b) {  
        ByteArrayInputStream bais = new ByteArrayInputStream(b);  
        return bais;  
    }  
  
/**
 * 2、 将InputStream转换成byte[]
 * 两种方式：
 * 一、
 */
 public static final byte[] InputStream2Bytes(InputStream inStream) {
    ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
    byte[] buff = new byte[100];
    int rc = 0;
    try {
  while ((rc = inStream.read(buff, 0, 100)) > 0) {
  swapStream.write(buff, 0, rc);
  }
  } catch (IOException e) {
  // TODO Auto-generated catch block
  e.printStackTrace();
  }
    byte[] in2b = swapStream.toByteArray();
    return in2b;
    }
//	/**
//	 * 二、
//	 * @param is
//	 * @return
//	 */
//    public static byte[] InputStream2Bytes(InputStream is) {  
//        String str = "";  
//        byte[] readByte = new byte[1024];  
//        int readCount = -1;  
//        try {  
//            while ((readCount = is.read(readByte, 0, 1024)) != -1) {  
//                str += new String(readByte).trim();  
//            }  
//            return str.getBytes();  
//        } catch (Exception e) {  
//            e.printStackTrace();  
//        }  
//        return null;  
//    }  
  

	/**
	 * 3、 将Bitmap转换成InputStream  
	 * @param bm
	 * @param quality
	 * @return
	 */
    public static InputStream Bitmap2InputStream(Bitmap bm, int quality) {  
        ByteArrayOutputStream baos = new ByteArrayOutputStream();  
        bm.compress(Bitmap.CompressFormat.JPEG, quality, baos);  
        InputStream is = new ByteArrayInputStream(baos.toByteArray());  
        return is;  
    }  
  
	/**
	 * 4、 将InputStream转换成Bitmap  
	 * @param is
	 * @return
	 */
    public static Bitmap InputStream2Bitmap(InputStream is) {  
        return BitmapFactory.decodeStream(is);  
    }  
  
	/**
	 * 5、 Drawable转换成InputStream  
	 * @param d
	 * @return
	 */
    public static InputStream Drawable2InputStream(Drawable d) {  
        Bitmap bitmap = drawable2Bitmap(d);  
        return Bitmap2InputStream(bitmap, 100);  
    }  
  
	/**
	 * 6、 InputStream转换成Drawable  
	 * @param is
	 * @return
	 */
    public static Drawable InputStream2Drawable(InputStream is) {  
        Bitmap bitmap = InputStream2Bitmap(is);  
        return bitmap2Drawable(bitmap);  
    }  
  
	/**
	 * 7、 Drawable转换成byte[]  
	 * @param d
	 * @return
	 */
    public static byte[] Drawable2Bytes(Drawable d) {  
        Bitmap bitmap = drawable2Bitmap(d);  
        return Bitmap2Bytes(bitmap);  
    }  
	/**
	 * 8、 byte[]转换成Drawable  
	 * @param b
	 * @return
	 */
    public static Drawable Bytes2Drawable(byte[] b) {  
        Bitmap bitmap = Bytes2Bitmap(b);  
        return bitmap2Drawable(bitmap);  
    }  
  
	/**
	 * 8、 Bitmap转换成byte[]  
	 * @param bm
	 * @return
	 */
    public static byte[] Bitmap2Bytes(Bitmap bm) {  
        ByteArrayOutputStream baos = new ByteArrayOutputStream();  
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);  
        return baos.toByteArray();  
    }  
  
	/**
	 * 9、 byte[]转换成Bitmap  
	 * @param b
	 * @return
	 */
    public static Bitmap Bytes2Bitmap(byte[] b) {  
        if (b.length != 0) {  
            return BitmapFactory.decodeByteArray(b, 0, b.length);  
        }  
        return null;  
    }  
  
	/**
	 * 10、Drawable转换成Bitmap  
	 * @param drawable
	 * @return
	 */
    public static Bitmap drawable2Bitmap(Drawable drawable) {  
        Bitmap bitmap = Bitmap  
                .createBitmap(  
                        drawable.getIntrinsicWidth(),  
                        drawable.getIntrinsicHeight(),  
                        drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888  
                                : Bitmap.Config.RGB_565);  
        Canvas canvas = new Canvas(bitmap);  
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),  
                drawable.getIntrinsicHeight());  
        drawable.draw(canvas);  
        return bitmap;  
    }  
  
	/**
	 * 11、 Bitmap转换成Drawable  
	 * @param bitmap
	 * @return
	 */
    public static Drawable bitmap2Drawable(Bitmap bitmap) {  
        BitmapDrawable bd = new BitmapDrawable(bitmap);  
        Drawable d = (Drawable) bd;  
        return d;  
    } 
}
