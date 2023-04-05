package com.vastly.affairs.util;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * 序列化,反序列化工具
 * 
 * @project iweixin-pay
 * @fileName SerializeUtil.java
 * @Description
 * @author light-zhang
 * @date 2018年5月31日上午9:51:25
 * @version 1.0.0
 */
public class SerializeUtil {
	private static final Log log = LogFactory.getLog(SerializeUtil.class);

	public static byte[] serialize(Object value) {
        if (value == null) {
            throw new NullPointerException("Can't serialize null");
        }
        byte[] rv = null;
        ByteArrayOutputStream bos = null;
        ObjectOutputStream os = null;
        try {
            bos = new ByteArrayOutputStream();
            os = new ObjectOutputStream(bos);
            os.writeObject(value);
            os.close();
            bos.close();
            rv = bos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("serialize error");
        } finally {
            close(os);
            close(bos);
        }
        return rv;
    }
 
    public static Object deserialize(byte[] in) {
        return deserialize(in, Object.class);
    }
 
    @SuppressWarnings("unchecked")
    public static <T> T deserialize(byte[] in, Class<T> requiredType) {
        Object rv = null;
        ByteArrayInputStream bis = null;
        ObjectInputStream is = null;
        try {
            if (in != null) {
                bis = new ByteArrayInputStream(in);
                is = new ObjectInputStream(bis);
                rv = is.readObject();
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error ("deserialize error");
        } finally {
            close(is);
            close(bis);
        }
        return (T) rv;
    }
 
    private static void close(Closeable closeable) {
        if (closeable != null)
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
                log.error("close stream error");
            }
    }
}