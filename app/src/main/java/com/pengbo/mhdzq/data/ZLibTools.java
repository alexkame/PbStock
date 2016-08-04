package com.pengbo.mhdzq.data;

import java.io.ByteArrayOutputStream;   
import java.io.IOException;   
import java.io.InputStream;   
import java.io.OutputStream;   
import java.util.zip.Deflater;   
import java.util.zip.DeflaterOutputStream;   
import java.util.zip.Inflater;   
import java.util.zip.InflaterInputStream;   

public class ZLibTools {
	 public static int compress(byte[] inData,int inSize,byte[] outData) {   
	  
		 	int nLength = 0;
	        Deflater compresser = new Deflater(6);   
	  
	        compresser.reset();   
	        compresser.setInput(inData);   
	        compresser.finish();   
	        ByteArrayOutputStream bos = new ByteArrayOutputStream(inData.length);   
	        try {   
	            byte[] buf = new byte[1024];   
	            while (!compresser.finished()) {   
	                int i = compresser.deflate(buf);   
	                bos.write(buf, 0, i);   
	            }   
//	            outData = bos.toByteArray();   
	            nLength = bos.size();
	            System.arraycopy(bos.toByteArray(), 0, outData, 0, nLength);
	        } catch (Exception e) {   
	        	outData = inData;  
	        	nLength = inSize;
	        	System.arraycopy(inData, 0, outData, 0, nLength);
	            e.printStackTrace();   
	        } finally {   
	            try {   
	                bos.close();   
	            } catch (IOException e) {   
	                e.printStackTrace();   
	            }   
	        }   
	        compresser.end();   
	        return nLength;   
	    }   
	  
	    /**  
	     * 压缩  
	     *   
	     * @param data  
	     *            待压缩数据  
	     *   
	     * @param os  
	     *            输出流  
	     */  
	    public static void compress(byte[] data, OutputStream os) {   
	        DeflaterOutputStream dos = new DeflaterOutputStream(os);   
	  
	        try {   
	            dos.write(data, 0, data.length);   
	  
	            dos.finish();   
	  
	            dos.flush();   
	        } catch (IOException e) {   
	            e.printStackTrace();   
	        }   
	    }   
	  
	    /**  
	     * 解压缩  
	     *   
	     * @param data  
	     *            待压缩的数据  
	     * @return byte[] 解压缩后的数据  
	     */  
	    public static int decompress(byte[] inData,int inSize,byte[] outData) {   
	    	int nLength = 0;
	  
	        Inflater decompresser = new Inflater();   
	        decompresser.reset();   
	        decompresser.setInput(inData);   
	  
	        ByteArrayOutputStream o = new ByteArrayOutputStream(inData.length);   
	        try {   
	            byte[] buf = new byte[1024];   
	            while (!decompresser.finished()) {   
	                int i = decompresser.inflate(buf);   
	                o.write(buf, 0, i);   
	            }   
//	            outData = o.toByteArray();  
	            nLength = o.size();
	            System.arraycopy(o.toByteArray(), 0, outData, 0, nLength);
	            
	        } catch (Exception e) {   
	        	outData = inData;   
	        	nLength = inSize;
	        	System.arraycopy(inData, 0, outData, 0, nLength);
	            e.printStackTrace();   
	        } finally {   
	            try {   
	                o.close();   
	            } catch (IOException e) {   
	                e.printStackTrace();   
	            }   
	        }   
	  
	        decompresser.end();   
	        return nLength;   
	    }   
	  
	    /**  
	     * 解压缩  
	     *   
	     * @param is  
	     *            输入流  
	     * @return byte[] 解压缩后的数据  
	     */  
	    public static byte[] decompress(InputStream is) {   
	        InflaterInputStream iis = new InflaterInputStream(is);   
	        ByteArrayOutputStream o = new ByteArrayOutputStream(1024);   
	        try {   
	            int i = 1024;   
	            byte[] buf = new byte[i];   
	  
	            while ((i = iis.read(buf, 0, i)) > 0) {   
	                o.write(buf, 0, i);   
	            }   
	  
	        } catch (IOException e) {   
	            e.printStackTrace();   
	        }   
	        return o.toByteArray();   
	    }  
	    
//	    public static String decompressData(String encdata) {
//	        try {
//	             ByteArrayOutputStream bos = new ByteArrayOutputStream();
//	             InflaterOutputStream zos = new InflaterOutputStream(bos);
//	             
//	             zos.write(encdata.getBytes());
//	             zos.close();
//	             return new String(bos.toByteArray());
//	         } catch (Exception ex) {
//	            ex.printStackTrace();
//	            return "UNZIP_ERR";
//	        }
//	    }
	    	
//	    //压缩
//	    public static String compressData(String data) {
//	        try {
//	            ByteArrayOutputStream bos = new ByteArrayOutputStream();
//	            DeflaterOutputStream zos = new DeflaterOutputStream(bos);
//	            zos.write(data.getBytes());
//	            zos.close();
//	            return new String(bos.toByteArray());
//	        } catch (Exception ex) {
//	            ex.printStackTrace();
//	            return "ZIP_ERR";
//	        }
//	    }
}
