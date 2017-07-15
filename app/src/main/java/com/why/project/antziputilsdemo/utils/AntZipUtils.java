package com.why.project.antziputilsdemo.utils;

import android.util.Log;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;
import org.apache.tools.zip.ZipOutputStream;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipException;
/**
 * @Create By HaiyuKing
 * @Used 基于Ant的Zip压缩工具类
 * @参考资料 http://yunzhu.iteye.com/blog/1480293
 * http://www.cnblogs.com/wainiwann/archive/2013/07/17/3196196.html
 * http://blog.csdn.net/growing_tree/article/details/46009813
 * http://www.jb51.net/article/69773.htm
 */
public class AntZipUtils {

	public static final String ENCODING_DEFAULT = "UTF-8";  

	public static final int BUFFER_SIZE_DIFAULT = 1024;  

	/**生成ZIP压缩包【建议异步执行】
	 * @param srcFilePaths - 要压缩的文件路径字符串数组【如果压缩一个文件夹，则只需要把文件夹目录放到一个数组中即可】
	 * @param zipPath - 生成的Zip路径*/
	public static void makeZip(String[] srcFilePaths, String zipPath)throws Exception {
		makeZip(srcFilePaths, zipPath, ENCODING_DEFAULT);
	}  

	/**生成ZIP压缩包【建议异步执行】
	 * @param srcFilePaths - 要压缩的文件路径字符串数组
	 * @param zipPath - 生成的Zip路径
	 * @param encoding - 编码格式*/
	public static void makeZip(String[] srcFilePaths, String zipPath,String encoding) throws Exception {
		File[] inFiles = new File[srcFilePaths.length];
		for (int i = 0; i < srcFilePaths.length; i++) {
			inFiles[i] = new File(srcFilePaths[i]);
		}  
		makeZip(inFiles, zipPath, encoding);  
	}  
	
	/**生成ZIP压缩包【建议异步执行】
	 * @param srcFiles - 要压缩的文件数组
	 * @param zipPath - 生成的Zip路径*/
	public static void makeZip(File[] srcFiles, String zipPath) throws Exception {
		makeZip(srcFiles, zipPath, ENCODING_DEFAULT);
	}  
	
	/**生成ZIP压缩包【建议异步执行】
	 * @param srcFiles - 要压缩的文件数组
	 * @param zipPath - 生成的Zip路径
	 * @param encoding - 编码格式*/
	public static void makeZip(File[] srcFiles, String zipPath, String encoding)
			throws Exception {  
		ZipOutputStream zipOut = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(zipPath)));  
		zipOut.setEncoding(encoding);  
		for (int i = 0; i < srcFiles.length; i++) {
			File file = srcFiles[i];
			doZipFile(zipOut, file, file.getParent());  
		}  
		zipOut.flush();  
		zipOut.close();  
	}  

	private static void doZipFile(ZipOutputStream zipOut, File file, String dirPath) throws FileNotFoundException, IOException {  
		if (file.isFile()) {  
			BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));  
			String zipName = file.getPath().substring(dirPath.length());  
			while (zipName.charAt(0) == '\\' || zipName.charAt(0) == '/') {  
				zipName = zipName.substring(1);  
			}  
			ZipEntry entry = new ZipEntry(zipName);  
			zipOut.putNextEntry(entry);  
			byte[] buff = new byte[BUFFER_SIZE_DIFAULT];  
			int size;  
			while ((size = bis.read(buff, 0, buff.length)) != -1) {  
				zipOut.write(buff, 0, size);  
			}  
			zipOut.closeEntry();  
			bis.close();  
		} else {  
			File[] subFiles = file.listFiles();  
			for (File subFile : subFiles) {  
				doZipFile(zipOut, subFile, dirPath);  
			}  
		}  
	}  

	/**解压ZIP包【建议异步执行】
	 * @param zipFilePath ZIP包的路径
	 * @param targetDirPath 指定的解压缩文件夹地址 */
	public static void unZip(String zipFilePath, String targetDirPath)throws IOException,Exception {  
		unZip(new File(zipFilePath), targetDirPath);  
	}  

	/**解压ZIP包【建议异步执行】
	 * @param zipFile ZIP包的文件
	 * @param targetDirPath 指定的解压缩目录地址 */
	public static void unZip(File zipFile, String targetDirPath) throws IOException,Exception {
		//先删除，后添加
		if (new File(targetDirPath).exists()) {  
			new File(targetDirPath).delete();  
		}  
		new File(targetDirPath).mkdirs();

		ZipFile zip = new ZipFile(zipFile);  
		Enumeration<ZipEntry> entries = (Enumeration<ZipEntry>) zip.getEntries();
		while (entries.hasMoreElements()) {  
			ZipEntry zipEntry = entries.nextElement();  
			if (zipEntry.isDirectory()) {  
				// TODO  
			} else {  
				String zipEntryName = zipEntry.getName();
				if(zipEntryName.contains("../")){//2016-08-25
					throw new Exception("unsecurity zipfile");
				}else{
					if (zipEntryName.indexOf(File.separator) > 0) {  
						String zipEntryDir = zipEntryName.substring(0, zipEntryName.lastIndexOf(File.separator) + 1);  
						String unzipFileDir = targetDirPath + File.separator + zipEntryDir;  
						File unzipFileDirFile = new File(unzipFileDir);  
						if (!unzipFileDirFile.exists()) {
							unzipFileDirFile.mkdirs();  
						}  
					}  
	
					InputStream is = zip.getInputStream(zipEntry);  
					FileOutputStream fos = new FileOutputStream(new File(targetDirPath + File.separator + zipEntryName));  
					byte[] buff = new byte[BUFFER_SIZE_DIFAULT];  
					int size;  
					while ((size = is.read(buff)) > 0) {  
						fos.write(buff, 0, size);  
					}  
					fos.flush();  
					fos.close();  
					is.close();  
				}
			}  
		}  
	}
	
	/** 
	 * 使用Apache工具包解压缩zip文件 【使用Java的zip包可以进行简单的文件压缩和解压缩处理时，但是遇到包含中文汉字目录或者包含多层子目录的复杂目录结构时，容易出现各种各样的问题。】
	 * @param sourceFilePath 指定的解压缩文件地址 
	 * @param targetDirPath  指定的解压缩目录地址 
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 * @throws ZipException 
	 */  
	public static void uncompressFile(String sourceFilePath, String targetDirPath)throws IOException, FileNotFoundException, ZipException,Exception{  
       BufferedInputStream bis;  
       ZipFile zf = new ZipFile(sourceFilePath, "GBK");  
       Enumeration entries = zf.getEntries();  
       while (entries.hasMoreElements()){  
           ZipEntry ze = (ZipEntry) entries.nextElement();  
           String entryName = ze.getName();
           if(entryName.contains("../")){//2016-08-25
				throw new Exception("unsecurity zipfile");
           }else{
	           String path = targetDirPath + File.separator + entryName;
	           Log.d("AntZipUtils", "path="+path);
	           if (ze.isDirectory()){
	        	   Log.d("AntZipUtils","正在创建解压目录 - " + entryName);
	               File decompressDirFile = new File(path);  
	               if (!decompressDirFile.exists()){  
	                   decompressDirFile.mkdirs();  
	               }
	           } else{  
	        	   Log.d("AntZipUtils","正在创建解压文件 - " + entryName);
	               String fileDir = path.substring(0, path.lastIndexOf(File.separator));
	               Log.d("AntZipUtils", "fileDir="+fileDir);
	               File fileDirFile = new File(fileDir);
	               if (!fileDirFile.exists()){  
	                   fileDirFile.mkdirs();  
	               }
	               BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(targetDirPath + File.separator + entryName));  
	               bis = new BufferedInputStream(zf.getInputStream(ze));  
	               byte[] readContent = new byte[1024];  
	               int readCount = bis.read(readContent);  
	               while (readCount != -1){  
	                   bos.write(readContent, 0, readCount);  
	                   readCount = bis.read(readContent);  
	               }  
	               bos.close();
	           }
           }
       }  
       zf.close();  
	}  
	

}
