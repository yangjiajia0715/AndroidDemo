package com.example.yang.androiddemo.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.widget.ImageView;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.ref.SoftReference;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BitmapCacheUtil {
	private boolean mDiskCache = true;
	private Context mContext;
	private final static int MAX_CACHE_NUM = 10;
	private String mCacheDir;
	private Handler mHandler;
	private Pattern classicsPattern = Pattern.compile("ABABABAB");
	private ConcurrentHashMap<String, SoftReference<Bitmap>> mSecondCache = new ConcurrentHashMap<String, SoftReference<Bitmap>>();
	private LinkedHashMap<String, Bitmap> mLinked_map = new LinkedHashMap<String, Bitmap>(
			MAX_CACHE_NUM, 0.75f, true) {
		protected boolean removeEldestEntry(
				java.util.Map.Entry<String, Bitmap> eldest) {
			if (this.size() > MAX_CACHE_NUM) {
				// 软连接的方法 存进二级缓存�?
				mSecondCache.put(eldest.getKey(), new SoftReference<Bitmap>(
						eldest.getValue()));
				// 缓存到本�?
				//cacheToDisk(eldest.getKey(), eldest.getValue());

				return true;
			}
			return false;
		};
	};

	public int getCachedSize(){
		return mLinked_map.size();
	}

	public void recycleCache(){
		Iterator iterator = mLinked_map.entrySet().iterator();
		while(iterator.hasNext()) {
			Entry entry = (Entry)iterator.next();
			Bitmap bitmap = (Bitmap) entry.getValue();
			bitmap.recycle();
			bitmap = null;
		}
		System.gc();
		mLinked_map.clear();
	}

	public void recycleCache(String key){
		Bitmap bitmap = mLinked_map.get(key);
		if(bitmap != null){
			bitmap.recycle();
			bitmap = null;
		}
		mLinked_map.remove(key);
	}

	public void setCacheDir(String dir){
		mCacheDir = dir;
	}

	public String getCacheDir(){
		return mCacheDir;
	}

	public BitmapCacheUtil(Context context) {
		mContext = context;
	}

	public  void cacheToDisk(String key, Bitmap bitmap) {
		try {
			String fileName = getMD5Str(key);
			File path = new File(mCacheDir);
			if (!path.exists()) {
				path.mkdirs();
			}
			String filePath = mCacheDir + "/" + fileName;
			FileOutputStream fos = new FileOutputStream(filePath);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public  void cacheToDisk(String key, Bitmap bitmap, boolean md5Enable) {
		try {
			String fileName;
			if(md5Enable){
			    fileName = getMD5Str(key);
			}else{
				fileName = key;
			}
			File path = new File(mCacheDir);
			if (!path.exists()) {
				path.mkdirs();
			}
			String filePath = mCacheDir + "/" + fileName;
			FileOutputStream fos = new FileOutputStream(filePath);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void copyFolder(String oldPath, String newPath) {

	       try {
	           (new File(newPath)).mkdirs(); //如果文件夹不存在 则建立新文件�?
	           File a=new File(oldPath);
	           String[] file=a.list();
	           File temp=null;
	           for (int i = 0; i < file.length; i++) {
	               if(oldPath.endsWith(File.separator)){
	                   temp=new File(oldPath+file[i]);
	               }
	               else{
	                   temp=new File(oldPath+ File.separator+file[i]);
	               }

	               if(temp.isFile()){
	                   FileInputStream input = new FileInputStream(temp);
	                   FileOutputStream output = new FileOutputStream(newPath + "/" +
	                           (temp.getName()).toString());
	                   byte[] b = new byte[1024 * 5];
	                   int len;
	                   while ( (len = input.read(b)) != -1) {
	                       output.write(b, 0, len);
	                   }
	                   output.flush();
	                   output.close();
	                   input.close();
	               }
	               if(temp.isDirectory()){//如果是子文件�?
	                   copyFolder(oldPath+"/"+file[i],newPath+"/"+file[i]);
	               }
	           }
	       }
	       catch (Exception e) {
	           System.out.println("复制整个文件夹内容操作");
	           e.printStackTrace();
	       }

	   }

	public static String getMD5Str(String str) {
		if(TextUtils.isEmpty(str)){
			return null;
		}
		MessageDigest messageDigest = null;
		try {
			messageDigest = MessageDigest.getInstance("MD5");
			messageDigest.reset();
			messageDigest.update(str.getBytes("UTF-8"));
		} catch (NoSuchAlgorithmException e) {
			System.out.println("NoSuchAlgorithmException caught!");
			return null;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}

		byte[] byteArray = messageDigest.digest();
		StringBuffer md5StrBuff = new StringBuffer();
		for (int i = 0; i < byteArray.length; i++) {
			if (Integer.toHexString(0xFF & byteArray[i]).length() == 1)
				md5StrBuff.append("0").append(
						Integer.toHexString(0xFF & byteArray[i]));
			else
				md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
		}
		return md5StrBuff.toString();
	}

	public boolean bindBitmapWithView(String key, ImageView imageView){
		Bitmap bitmap = getBitmapFromCache(key);
		if (null != bitmap) {
			//BitmapDrawable drawable = new BitmapDrawable(bitmap);
			Matrix matrix = new Matrix();
//			int viewWidth = Utility.dpToPixel(400);
			int viewWidth = 400 * 3;
			int width = bitmap.getWidth();
			int height = bitmap.getHeight();
			float x = (((float)viewWidth) / width);
			float viewHeight  = x * height;
			float y = viewHeight / height;
			matrix.postScale(x, y);
			Bitmap tempBitmap = Bitmap.createBitmap(bitmap,0,0,width,height,matrix,true);
			imageView.setImageBitmap(tempBitmap);
			//imageView.setBackgroundDrawable(drawable);
			//imageView.setBackground(drawable);
			bitmap = null;
			return true;
		}else{
			return false;
		}
	}

	//copy bindBitmapWithView(),
	public Bitmap bindBitmapWithView2(String key, ImageView imageView){
		Bitmap bitmap = null;
		Uri uri = Uri.fromFile(new File(Event.IMG_TEMP_PATH, key));
		try {
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = false;
			// options.inSampleSize = 3;
			options.inPurgeable = true;
			options.inPreferredConfig = Bitmap.Config.RGB_565;
			bitmap = BitmapFactory.decodeStream(mContext.getContentResolver().openInputStream(uri));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
		}


//		Bitmap bitmap = getBitmapFromCache(key);
		Bitmap tempBitmap = null;
		if (null != bitmap) {
			//BitmapDrawable drawable = new BitmapDrawable(bitmap);
			Matrix matrix = new Matrix();
//			int viewWidth = Utility.dpToPixel(400);
			int viewWidth = 400*3;///////////////
			int width = bitmap.getWidth();
			int height = bitmap.getHeight();
			float x = (((float)viewWidth) / width);
			float viewHeight  = x * height;
			float y = viewHeight / height;
			matrix.postScale(x, y);
			tempBitmap = Bitmap.createBitmap(bitmap,0,0,width,height,matrix,true);
			imageView.setImageBitmap(tempBitmap);
			//imageView.setBackgroundDrawable(drawable);
			//imageView.setBackground(drawable);
			bitmap = null;
			return tempBitmap;
		}else{
			return tempBitmap;
		}
	}

	public Bitmap getBitmapFromCache(String key) {
		// 1.先在一级缓存中�?
		synchronized (mLinked_map) {
			Bitmap bitmap = mLinked_map.get(key);
			if (null != bitmap) {
				mLinked_map.remove(key);
				// 按照 LRU是Least Recently Used 近期最少使用算�?内存算法 就近 �?原则 放到首位
				mLinked_map.put(key, bitmap);
				System.out.println(" 在缓中找图片=" + key);
				return bitmap;
			}
		}

		// 2. 到二�?缓存�?
		SoftReference<Bitmap> soft = mSecondCache.get(key);
		if (soft != null) {
			// 得到 软连�?中的图片
			Bitmap soft_bitmap = soft.get();
			if (null != soft_bitmap) {
				System.out.println(" 在缓中找图片=" + key);
				return soft_bitmap;
			}
		} else {
			// 没有图片的话 把这个key删除
			mSecondCache.remove(key);
		}

		// 3.都没有的话去从外部缓存文件读�?
		if (mDiskCache) {
			Bitmap bitmap = getBitmapFromFile(key);
			if (bitmap != null) {
				mLinked_map.put(key, bitmap); // 将图片放到一级缓存首�?
				return bitmap;
			}
		}

		return null;
	}

	/**
	 * 从外部文件缓存中获取bitmap
	 *
	 * @param url
	 * @return
	 */
	public Bitmap getBitmapFromFile(String url) {
		Bitmap bitmap = null;
		String fileName = getMD5Str(url);
		if (fileName == null) {
			return null;
		}
		String filePath = mCacheDir + fileName;
		File file = new File(mCacheDir,fileName);
		if(!file.exists()){
			filePath = mCacheDir + url;
		}

		try {
			FileInputStream fis = new FileInputStream(filePath);
			bitmap = BitmapFactory.decodeStream(fis);
			addBitmap(url, bitmap);//2016-5-25
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			bitmap = null;
		}catch (OutOfMemoryError e) {
			e.printStackTrace();
		}
		return bitmap;
	}

    public boolean isBitmapFileExist(String key){
    	boolean result = false;
    	String fileName = getMD5Str(key);
		if (fileName == null) {
			return false;
		}
		String filePath = mCacheDir + "/" + fileName;
		File file = new File(filePath);
		if(file != null && file.isFile()){
			if(file.exists()){
				result = true;
			}else{
				result = false;
			}
		}
    	return result;
    }
	/**
	 * 把图�?添加到缓存中
	 */
	public void addBitmap(String key, Bitmap bitmap) {
		if (null != bitmap) {
			synchronized (mLinked_map) {
				mLinked_map.put(key, bitmap);
				cacheToDisk(key, bitmap);
			}
		}
	}

	public void addBitmap(String key, Bitmap bitmap, boolean md5Enable) {
		if (null != bitmap) {
			synchronized (mLinked_map) { // 添加到一�?缓存�?
				mLinked_map.put(key, bitmap);
				cacheToDisk(key, bitmap,md5Enable);
			}
		}
	}

	public void addNotEncryptedBitmap(String key, Bitmap bitmap) {
		if (null != bitmap) {
			synchronized (mLinked_map) { // 添加到一级缓存目录
				mLinked_map.put(key, bitmap);
				cacheNotEncryptedToDisk(key, bitmap);
			}
		}
	}

	private void cacheNotEncryptedToDisk(String fileName, Bitmap bitmap) {
		try {
			//String fileName = getMD5Str(key);
			File path = new File(mCacheDir);
			if (!path.exists()) {
				path.mkdirs();
			}
			String filePath = mCacheDir + "/" + fileName;
			FileOutputStream fos = new FileOutputStream(filePath);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static boolean removeCache(String dirPath) {
		File dir = new File(dirPath);
		File[] files = dir.listFiles();
		if (files == null || files.length == 0) {
			return true;
		}
		// 这里删除所有的缓存
		int all_ = files.length;
		// 对files 进行排序
//		Arrays.sort(files, new FileLastModifiedSort());
		for (int i = 0; i < all_; i++) {
			files[i].delete();
		}
		return true;
	}

	public void removeFile(String fileName){
		String filePath = mCacheDir + "/" + fileName;
		File file = new File(filePath);
		try {
			if(file.isFile() && file.exists()){
			    file.delete();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 根据文件最后修改时间进行排�?
	 */
	private static class FileLastModifiedSort implements Comparator<File> {
		@Override
		public int compare(File lhs, File rhs) {
			if (lhs.lastModified() > rhs.lastModified()) {
				return 1;
			} else if (lhs.lastModified() == rhs.lastModified()) {
				return 0;
			} else {
				return -1;
			}
		}
	}

	private SpannableStringBuilder replace (String inspan , Bitmap bitmap){
        SpannableStringBuilder builder = new SpannableStringBuilder(inspan);
  
        Matcher matcher = classicsPattern.matcher(inspan);
        
        Matrix matrix = new Matrix();
        matrix.postScale(2.5f,2.5f); //长和宽放大缩小的比例
        Bitmap resizeBmp = Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);
        while (matcher.find()) {
            builder.setSpan(new ImageSpan(mContext ,resizeBmp),
                            matcher.start(), matcher.end(),
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return builder;
	}

	public void moveFile(String oldDir, String newDir, String fileName){
		if(TextUtils.isEmpty(fileName)) return;
		File oldPath = new File(oldDir);
		if(!oldPath.exists()){
			oldPath.mkdirs();
		}
		File newPath = new File(newDir);
		if(!newPath.exists()){
			newPath.mkdirs();
		}
		File oldFile =new File(oldDir + fileName);
		if(oldFile.exists()){
			oldFile.renameTo(new File(newDir + fileName));
			oldFile.delete();
		}
	}
	
	public  void copyFile(String oldDir, String newDir, String oldName, String newName){
		if(TextUtils.isEmpty(oldName)) return;
		File oldPath = new File(oldDir);
		if(!oldPath.exists()){
			oldPath.mkdirs();
		}
		File newPath = new File(newDir);
		if(!newPath.exists()){
			newPath.mkdirs();
		}
		File oldFile =new File(oldDir + oldName);
		if(oldFile.exists()){
			File newFile = new File(newDir + newName);
			if(!newFile.exists()){
			    oldFile.renameTo(newFile);
			}
		}
	}
	
	/**
	 * paste
	 * @param oldFile
	 * @param newFile
	 */
	public static void copyFile(String oldFile, String newFile){
		if(TextUtils.isEmpty(oldFile) || TextUtils.isEmpty(newFile)) return;
		File old_file = new File(oldFile);
		if(!old_file.exists()){
			return;
		}
		File new_ile = new File(newFile);
		if(!new_ile.exists()){
			old_file.renameTo(new_ile);
		}
	}
	
	public static void deleteFileDirectory(File file){
		if(!file.exists()){
			return ;
		}
		if(file.isDirectory()){
			File[] files = file.listFiles();
			if(files == null || files.length == 0){
				file.delete();
				return ;
			}
			for(int i=0,length = files.length; i<length; i++){
				files[i].delete();
			}
			file.delete();
		}
	}
	
	public static void deleteAllDirectory(String path){
		File file = new File(path);
		if(!file.exists()){
			return ;
		}
		File[] fileDir = file.listFiles(new FileFilter() {
			@Override
			public boolean accept(File file) {
				return file.isDirectory() ? true : false;
			}
		});
		for(int i=0; i<fileDir.length; i++){
			deleteFileDirectory(fileDir[i]);
		}
	}
}
