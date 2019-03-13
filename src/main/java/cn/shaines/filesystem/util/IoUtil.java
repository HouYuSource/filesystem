package cn.shaines.filesystem.util;

import java.io.*;

/**
 * @author houyu
 * @createTime 2019/3/9 23:42
 */
public class IoUtil {
    /**
     * 流 转 文件
     */
    public static void toFile(InputStream inputStream, File file) throws IOException {
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(file);
            copy(inputStream, fileOutputStream);
        } finally {
            close(inputStream, fileOutputStream);
        }
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
    public static InputStream toInputStream(final String s, final String charsetName) throws UnsupportedEncodingException {
        return new ByteArrayInputStream(s.getBytes(charsetName));
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
    public static String toString(byte[] bytes, String charsetName) throws UnsupportedEncodingException {
        return new String(bytes, charsetName);
    }

    /**
     * 流 转 字符串
     */
    public static String toString(InputStream inputStream, String charsetName) throws IOException {
        return new String(toByteArray(inputStream), charsetName);
    }

    /**
     * 流 转 byte[]
     */
    public static byte[] toByteArray(InputStream inputStream) throws IOException {
        ByteArrayOutputStream output = null;
        try {
            output = new ByteArrayOutputStream();
            copy(inputStream, output);
        } finally {
            close(inputStream, output);
        }
        return output.toByteArray();
    }

    /**
     * 输入流 转 输出流(两流对接)
     */
    public static void copy(InputStream inputStream, OutputStream outputStream) throws IOException {
        byte[] bytes = new byte[1024 * 3];
        int i;
        while ((i = inputStream.read(bytes)) != -1) {
            outputStream.write(bytes, 0, i);
            outputStream.flush();
        }
    }

    public static void copy(InputStream inputStream, OutputStream outputStream, boolean isClose) throws IOException {
        try {
            copy(inputStream, outputStream);
        } finally {
            if (isClose){
                close(inputStream, outputStream);
            }
        }
    }

    /**
     * 关闭资源
     */
    public static <T extends Closeable> void close(T... ts){
        if (ts != null){
            for (T closeableImpl : ts){
                if (null == closeableImpl) { continue; }
                try {                               // try catch 必须放在 for 里边,否则可能会导致没有完全关闭资源造成内存溢出.
                    closeableImpl.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }finally {
                    // closeableImpl = null;
                }
            }
        }
    }
}
