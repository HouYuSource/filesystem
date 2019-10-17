package cn.shaines.filesystem.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

/**
 * @author houyu
 * @createTime 2019/4/19 22:49
 */
public class IOUtil {

    /**
     * 流 转 文件
     */
    public static void toFile(InputStream inputStream, File file) throws IOException {
        copy(inputStream, new FileOutputStream(file), true);
    }

    /**
     * 文件转流
     */
    public static InputStream toInputStream(final File file) throws FileNotFoundException {
        return new FileInputStream(file);
    }

    /**
     * 字符串 转 流
     */
    public static InputStream toInputStream(final String s, Charset charset) {
        return new ByteArrayInputStream(s.getBytes(charset));
    }

    /**
     * byte[] 转 流
     */
    public static InputStream toInputStream(final byte[] bytes) {
        return new ByteArrayInputStream(bytes);
    }

    /**
     * byte[] 转 字符串
     */
    public static String toString(byte[] bytes, Charset charset) {
        return new String(bytes, charset);
    }

    /**
     * 流 转 字符串
     */
    public static String toString(InputStream inputStream, Charset charset) throws IOException {
        return new String(toByteArray(inputStream), charset);
    }

    /**
     * 流 转 byte[]
     */
    public static byte[] toByteArray(InputStream inputStream) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        copy(inputStream, output, true);
        return output.toByteArray();
    }

    /**
     * 输入流 转 输出流(两流对接)
     */
    public static void copy(InputStream inputStream, OutputStream outputStream) throws IOException {
        byte[] bytes = new byte[1024 * 3];
        for (int i; (i = inputStream.read(bytes)) > -1; outputStream.flush()) {
            outputStream.write(bytes, 0, i);
        }
    }

    public static void copy(InputStream inputStream, OutputStream outputStream, boolean autoClose) throws IOException {
        try {
            copy(inputStream, outputStream);
        } finally {
            if (autoClose) {
                close(inputStream, outputStream);
            }
        }
    }

    /**
     * 关闭资源
     */
    @SafeVarargs
    public static <T extends Closeable> void close(T... ts) {
        if (ts != null) {
            for (T closeableImpl : ts) {
                if (null == closeableImpl) {
                    continue;
                }
                // try catch 必须放在 for 里边,否则可能会导致没有完全关闭资源造成内存溢出.
                try {
                    closeableImpl.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    // } finally {
                    // closeableImpl = null;
                }
            }
        }
    }
}
