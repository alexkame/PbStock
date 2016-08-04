package com.pengbo.mhdzq.data;

import java.io.InputStream;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;

import android.content.Context;
import android.util.Xml;

import com.pengbo.mhdzq.net.MyByteBuffer;
import com.pengbo.mhdzq.tools.L;
import com.pengbo.mhdzq.tools.STD;

public class CPbDataDecode {

	public static ArrayList<CPbDataPackage> g_vDataPackage;

	/**
	 * 加载行情协议
	 * 
	 * @param context
	 * @param strFilePath
	 * @return
	 */
	public static boolean LoadPackageTemplate(Context context,
			String strFilePath) {

		InputStream inStream = null;
		try {
			inStream = context.getResources().getAssets().open(strFilePath);

		} catch (Exception e) {
			e.printStackTrace();
		}

		XmlPullParser parser = Xml.newPullParser();
		CPbDataPackage currentPackage = null;
		CPbDataItem currentDataItem = null;
		try {
			parser.setInput(inStream, "UTF-8");
			int eventType = parser.getEventType();

			boolean bCurrentItemIsArray = false;// 当前是Array
			while (eventType != XmlPullParser.END_DOCUMENT) {

				switch (eventType) {
				case XmlPullParser.START_DOCUMENT:// 文档开始事件,可以进行数据初始化处理
					g_vDataPackage = new ArrayList<CPbDataPackage>();
					break;
				case XmlPullParser.START_TAG:// 开始元素事件
					String name = parser.getName();
					if (name.equalsIgnoreCase("pbpacket")) {
						currentPackage = new CPbDataPackage();
						bCurrentItemIsArray = false;

						currentPackage.m_wPackageID = STD.StringToInt(parser
								.getAttributeValue(null, "id"));// new
																// Integer(parser.getAttributeValue(null,
																// "id"));
						currentPackage.m_szPackageName = parser
								.getAttributeValue(null, "name");
						currentPackage.m_szPackageVersion = parser
								.getAttributeValue(null, "version");
						String mask = parser.getAttributeValue(null, "mask");
						if (mask == null || mask.length() == 0)
							currentPackage.m_bNeedMaskField = true;
						else {
							if (STD.StringToInt(mask) == 0) {
								currentPackage.m_bNeedMaskField = false;
							} else {
								currentPackage.m_bNeedMaskField = true;
							}
						}

					} else if (currentPackage != null) {

						if (name.equalsIgnoreCase("array")) {
							currentDataItem = new CPbDataItem();
							currentDataItem.m_ItemType = CPbDataItem.DIT_ARRAY;
							currentDataItem.m_ArrayField = new ArrayList<CPbDataField>();
							currentDataItem.m_ArrayValue = new ArrayList<CPbDataField>();
							String mask = parser
									.getAttributeValue(null, "mask");
							if (mask == null || mask.length() == 0)
								currentDataItem.m_bNeedMaskField = true;
							else {
								if (STD.StringToInt(mask) == 0) {
									currentDataItem.m_bNeedMaskField = false;
								} else {
									currentDataItem.m_bNeedMaskField = true;
								}
							}
							bCurrentItemIsArray = true;
						} else if (bCurrentItemIsArray == true) {
							if (currentDataItem != null) {
								CPbDataField dataField = new CPbDataField();
								if (name.equalsIgnoreCase("int8")) {
									dataField.m_DataType = CPbDataField.DF_INT8;
									dataField.m_FieldID = STD
											.StringToInt(parser
													.getAttributeValue(null,
															"id"));// new
																	// Integer(parser.getAttributeValue(null,
																	// "id"));
									dataField.m_szFieldName = parser
											.getAttributeValue(null, "name");
									dataField.m_szFieldDesc = parser
											.getAttributeValue(null, "comment");
								} else if (name.equalsIgnoreCase("int16")) {
									dataField.m_DataType = CPbDataField.DF_INT16;
									dataField.m_FieldID = STD
											.StringToInt(parser
													.getAttributeValue(null,
															"id"));// new
																	// Integer(parser.getAttributeValue(null,
																	// "id"));
									dataField.m_szFieldName = parser
											.getAttributeValue(null, "name");
									dataField.m_szFieldDesc = parser
											.getAttributeValue(null, "comment");
								} else if (name.equalsIgnoreCase("int32")) {
									dataField.m_DataType = CPbDataField.DF_INT32;
									dataField.m_FieldID = STD
											.StringToInt(parser
													.getAttributeValue(null,
															"id"));// new
																	// Integer(parser.getAttributeValue(null,
																	// "id"));
									dataField.m_szFieldName = parser
											.getAttributeValue(null, "name");
									dataField.m_szFieldDesc = parser
											.getAttributeValue(null, "comment");
								} else if (name.equalsIgnoreCase("float")) {
									dataField.m_DataType = CPbDataField.DF_FLOAT;
									dataField.m_FieldID = STD
											.StringToInt(parser
													.getAttributeValue(null,
															"id"));// new
																	// Integer(parser.getAttributeValue(null,
																	// "id"));
									dataField.m_szFieldName = parser
											.getAttributeValue(null, "name");
									dataField.m_szFieldDesc = parser
											.getAttributeValue(null, "comment");
								} else if (name.equalsIgnoreCase("double")) {
									dataField.m_DataType = CPbDataField.DF_DOUBLE;
									dataField.m_FieldID = STD
											.StringToInt(parser
													.getAttributeValue(null,
															"id"));// new
																	// Integer(parser.getAttributeValue(null,
																	// "id"));
									dataField.m_szFieldName = parser
											.getAttributeValue(null, "name");
									dataField.m_szFieldDesc = parser
											.getAttributeValue(null, "comment");
								} else if (name.equalsIgnoreCase("string")) {
									String szCharset = parser
											.getAttributeValue(null, "charset");
									if (szCharset != null
											&& szCharset
													.equalsIgnoreCase("unicode")) {
										dataField.m_DataType = CPbDataField.DF_WSTRING;
									} else {
										dataField.m_DataType = CPbDataField.DF_STRING;
									}
									dataField.m_FieldID = STD
											.StringToInt(parser
													.getAttributeValue(null,
															"id"));// new
																	// Integer(parser.getAttributeValue(null,
																	// "id"));
									dataField.m_szFieldName = parser
											.getAttributeValue(null, "name");
									dataField.m_szFieldDesc = parser
											.getAttributeValue(null, "comment");
								}
								currentDataItem.m_ArrayField.add(dataField);
								currentDataItem.nSubFields = (int) currentDataItem.m_ArrayField
										.size();
							}
						} else {
							currentDataItem = new CPbDataItem();
							currentDataItem.m_ItemType = CPbDataItem.DIT_NORMAL;
							currentDataItem.m_NormalField = new CPbDataField();
							currentDataItem.m_NormalField.dVal = 0;
							if (name.equalsIgnoreCase("int8")) {
								currentDataItem.m_NormalField.m_DataType = CPbDataField.DF_INT8;
								currentDataItem.m_NormalField.m_FieldID = STD
										.StringToInt(parser.getAttributeValue(
												null, "id"));// new
																// Integer(parser.getAttributeValue(null,
																// "id"));
								currentDataItem.m_NormalField.m_szFieldName = parser
										.getAttributeValue(null, "name");
								currentDataItem.m_NormalField.m_szFieldDesc = parser
										.getAttributeValue(null, "comment");
							} else if (name.equalsIgnoreCase("int16")) {
								currentDataItem.m_NormalField.m_DataType = CPbDataField.DF_INT16;
								currentDataItem.m_NormalField.m_FieldID = STD
										.StringToInt(parser.getAttributeValue(
												null, "id"));// new
																// Integer(parser.getAttributeValue(null,
																// "id"));
								currentDataItem.m_NormalField.m_szFieldName = parser
										.getAttributeValue(null, "name");
								currentDataItem.m_NormalField.m_szFieldDesc = parser
										.getAttributeValue(null, "comment");
							} else if (name.equalsIgnoreCase("int32")) {
								currentDataItem.m_NormalField.m_DataType = CPbDataField.DF_INT32;
								currentDataItem.m_NormalField.m_FieldID = STD
										.StringToInt(parser.getAttributeValue(
												null, "id"));// new
																// Integer(parser.getAttributeValue(null,
																// "id"));
								currentDataItem.m_NormalField.m_szFieldName = parser
										.getAttributeValue(null, "name");
								currentDataItem.m_NormalField.m_szFieldDesc = parser
										.getAttributeValue(null, "comment");
							} else if (name.equalsIgnoreCase("float")) {
								currentDataItem.m_NormalField.m_DataType = CPbDataField.DF_FLOAT;
								currentDataItem.m_NormalField.m_FieldID = STD
										.StringToInt(parser.getAttributeValue(
												null, "id"));// new
																// Integer(parser.getAttributeValue(null,
																// "id"));
								currentDataItem.m_NormalField.m_szFieldName = parser
										.getAttributeValue(null, "name");
								currentDataItem.m_NormalField.m_szFieldDesc = parser
										.getAttributeValue(null, "comment");
							} else if (name.equalsIgnoreCase("double")) {
								currentDataItem.m_NormalField.m_DataType = CPbDataField.DF_DOUBLE;
								currentDataItem.m_NormalField.m_FieldID = STD
										.StringToInt(parser.getAttributeValue(
												null, "id"));// new
																// Integer(parser.getAttributeValue(null,
																// "id"));
								currentDataItem.m_NormalField.m_szFieldName = parser
										.getAttributeValue(null, "name");
								currentDataItem.m_NormalField.m_szFieldDesc = parser
										.getAttributeValue(null, "comment");
							} else if (name.equalsIgnoreCase("string")) {
								String szCharset = parser.getAttributeValue(
										null, "charset");
								if (szCharset != null
										&& szCharset
												.equalsIgnoreCase("unicode")) {
									currentDataItem.m_NormalField.m_DataType = CPbDataField.DF_WSTRING;
								} else {
									currentDataItem.m_NormalField.m_DataType = CPbDataField.DF_STRING;
								}
								currentDataItem.m_NormalField.m_FieldID = STD
										.StringToInt(parser.getAttributeValue(
												null, "id"));// new
																// Integer(parser.getAttributeValue(null,
																// "id"));
								currentDataItem.m_NormalField.m_szFieldName = parser
										.getAttributeValue(null, "name");
								currentDataItem.m_NormalField.m_szFieldDesc = parser
										.getAttributeValue(null, "comment");
							}
							currentPackage.m_DataItems.add(currentDataItem);
							++currentPackage.m_nItemSize;
							// currentDataItem = null;
						}
					}
					break;

				case XmlPullParser.END_TAG:// 结束元素事件
					if (parser.getName().equalsIgnoreCase("pbpacket")
							&& currentPackage != null) {
						g_vDataPackage.add(currentPackage);
						currentPackage = null;
					} else if (parser.getName().equalsIgnoreCase("array")) {
						currentPackage.m_DataItems.add(currentDataItem);
						++currentPackage.m_nItemSize;
						// currentDataItem = null;
						bCurrentItemIsArray = false;// 数据结束
					}
					break;
				}

				eventType = parser.next();
			}
			inStream.close();
		} catch (Exception e) {
			e.printStackTrace();
			L.e("ERROR_DataDecode", e.toString());
		}
		return true;
	}

	// 字符串转换unicode
	public static String string2Unicode(String string) {
		StringBuffer unicode = new StringBuffer();
		for (int i = 0; i < string.length(); i++) {
			// 取出每一个字符
			char c = string.charAt(i);
			// 转换为unicode
			unicode.append("\\u" + Integer.toHexString(c));
		}
		return unicode.toString();
	}

	// unicode 转字符串
	public static String unicode2String(String unicode) {
		StringBuffer string = new StringBuffer();
		String[] hex = unicode.split("\\\\u");
		for (int i = 1; i < hex.length; i++) {
			// 转换出每一个代码点
			int data = Integer.parseInt(hex[i], 16);
			// 追加成string
			string.append((char) data);
		}
		return string.toString();
	}

	public static int DecodeField(byte[] buf, int offset, CPbDataField pField) {
		int nSize = 0;
		switch (pField.m_DataType) {
		case CPbDataField.DF_INT8:
			nSize = 1;
			byte value8 = MyByteBuffer.getByte(buf, offset);
			pField.SetInt8(value8);
			break;
		case CPbDataField.DF_INT16: {
			nSize = 2;
			int value16 = MyByteBuffer.getUnsignedShort(buf, offset);
			pField.SetInt16(value16);
		}
			break;
		case CPbDataField.DF_INT32: {
			nSize = 4;
			int value32 = MyByteBuffer.getInt(buf, offset);
			pField.SetInt32(value32);
		}
			break;
		case CPbDataField.DF_FLOAT: {
			nSize = 4;
			float fValue = MyByteBuffer.getFloat(buf, offset);
			pField.SetFloat(fValue);
		}
			break;
		case CPbDataField.DF_DOUBLE: {
			nSize = 8;
			double dValue = MyByteBuffer.getDouble(buf, offset);
			pField.SetDouble(dValue);
		}
			break;
		case CPbDataField.DF_STRING:
			nSize = 1024;
			nSize = STD.getByteStringLen(buf, offset, nSize) + 1;
			byte[] pData = new byte[nSize];
			System.arraycopy(buf, offset, pData, 0, nSize);
			// L.e("MyApp", "CPbDataField.DF_STRING:" + new
			// String(pData,0,nSize-1));
			pField.SetString(STD.getStringFromBytes(pData, 0, nSize - 1));
			break;
		case CPbDataField.DF_WSTRING: {
			nSize = 1024;
			nSize = STD.getUnicodeBytesStringLen(buf, offset, nSize) + 1;
			nSize = nSize * 2;
			// char szData[] = STD.getCharsFromUnicodeBytes(buf,offset,nSize);
			// String wStr = new String(szData,0,nSize/2-1);
			String wStr = STD.getStringFromUnicodeBytes(buf, offset, nSize);
			// L.e("MyApp", "CPbDataField.DF_WSTRING:" + wStr);
			pField.SetString(wStr);
		}
			break;
		default:
			break;
		}
		return nSize;
	}

	public static int DecodeOnePackage(byte[] buf, int offset, int size,
			CPbDataPackage pPack) {
		L.i("CPbDataDecode", "Start DecodeOnePackage");
		int tid = MyByteBuffer.getUnsignedShort(buf, offset);
		offset += 2;

		CPbDataPackage pTemplate = null;
		int i;
		for (i = 0; i < (int) g_vDataPackage.size(); ++i) {
			if (g_vDataPackage.get(i).m_wPackageID == tid) {
				pTemplate = g_vDataPackage.get(i);
				break;
			}
		}
		if (null == pTemplate) {
			return 0;
		}

		pPack.m_wPackageID = pTemplate.m_wPackageID;
		pPack.m_szPackageName = pTemplate.m_szPackageName;
		pPack.m_szPackageVersion = pTemplate.m_szPackageVersion;
		pPack.m_bNeedMaskField = pTemplate.m_bNeedMaskField;

		short MaskLen = 0;
		byte[] FieldMask = null;
		if (pPack.m_bNeedMaskField) {
			MaskLen = (short) MyByteBuffer.getUnsignedByte(buf, offset);
			offset += 1;
			FieldMask = new byte[MaskLen];
			System.arraycopy(buf, offset, FieldMask, 0, MaskLen);
			offset += MaskLen;
		}

		pPack.m_nItemSize = pTemplate.m_nItemSize;
		pPack.m_DataItems.clear();

		int nmask = 0;
		for (i = 0; i < pTemplate.m_nItemSize; ++i) {
			pPack.m_DataItems.add(pTemplate.m_DataItems.get(i));
			CPbDataItem item = pPack.m_DataItems.get(i);
			if (item.m_NormalField != null) {
				item.m_NormalField.clearData();
			}
			if (item.m_ItemType == CPbDataItem.DIT_ARRAY) {
				item.nArraySize = MyByteBuffer.getUnsignedShort(buf, offset);
				offset += 2;
				item.m_ArrayValue.clear();
				if (item.nArraySize == 0)
					continue;

				short SubMaskLen = 0;
				byte[] SubFieldMask = null;
				if (item.m_bNeedMaskField) {
					SubMaskLen = (short) MyByteBuffer.getUnsignedByte(buf,
							offset);
					offset += 1;
					SubFieldMask = new byte[SubMaskLen];
					System.arraycopy(buf, offset, SubFieldMask, 0, SubMaskLen);
					offset += SubMaskLen;
				}

				int j, k;
				for (j = 0; j < item.nArraySize; ++j) {
					int sublen = MyByteBuffer.getUnsignedShort(buf, offset);
					offset += 2;
					// item.m_ArrayValue.addAll(item.m_ArrayField);
					for (int p = 0; p < item.m_ArrayField.size(); p++) {
						item.m_ArrayValue.add(new CPbDataField(
								item.m_ArrayField.get(p)));
					}

					int suboffset = 0;
					for (k = 0; k < item.nSubFields; ++k) {
						if (!item.m_bNeedMaskField
								|| ((k < SubMaskLen * 8) && (SubFieldMask[k / 8] & (0x80 >> k % 8)) != 0)) {
							suboffset += DecodeField(
									buf,
									offset + suboffset,
									item.m_ArrayValue.get(j * item.nSubFields
											+ k));
						}
					}
					offset += sublen;
				}
			} else {
				if (!pPack.m_bNeedMaskField
						|| (nmask < MaskLen * 8 && (FieldMask[nmask / 8] & (0x80 >> nmask % 8)) != 0)) {
					offset += DecodeField(buf, offset, item.m_NormalField);
				}
				++nmask;
			}
		}
		L.i("CPbDataDecode", "End DecodeOnePackage");
		return size;
	}

	// public static int DecodePackages(char *buf, int size)
	// {
	// int offset = 0;
	//
	// while(size-offset >= sizeof(unsigned short))
	// {
	// unsigned short wPackSize = *(unsigned short *)(buf+offset);
	// offset += sizeof(unsigned short);
	// if(wPackSize > size-offset)
	// return offset-sizeof(unsigned short);
	//
	// CPbDataPackage Pack;
	// int ret = DecodeOnePackage(buf+offset, wPackSize, &Pack);
	//
	// offset += wPackSize;
	// ret = 0;
	// }
	//
	// return offset;
	// }

}
