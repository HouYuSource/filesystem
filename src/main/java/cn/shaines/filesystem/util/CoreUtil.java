package cn.shaines.filesystem.util;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.math.BigInteger;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @description 公共工具类
 * @date created in 2019-08-24 16:57:47
 * @author houyu for.houyu@foxmail.com
 */
public class CoreUtil {

    public static boolean isEmpty(Object o) {
        if(o == null) {
            return true;
        }
        if(o instanceof String) {
            return ((String) o).isEmpty();
        } else if(o instanceof Collection) {
            return ((Collection) o).isEmpty();
        } else if(o instanceof Map) {
            return ((Map) o).isEmpty();
        } else if(o instanceof Object[]) {
            return ((Object[]) o).length == 0;
        } else {
            return false;
        }
    }

    public static boolean isNotEmpty(Object o) {
        return !isEmpty(o);
    }

    public static void assertCheck(boolean b, String msg) {
        if (b) {
            throw new IllegalArgumentException(isEmpty(msg) ? "AssertCheck exception : assertCheck()" : msg);
        }
    }

    public static void assertCheck(boolean b, Exception e) throws Exception {
        if (b) {
            throw e;
        }
    }

    public static <T> T orElse(T t1, T t2) {
        return isEmpty(t1) ? t2 : t1;
    }

    /**
     * Object 转 String, 支持设置默认值
     */
    public static String toString(Object o, String defaultValue) {
        return isEmpty(o) ? defaultValue : o.toString();
    }

    /**
     * 删除 s 开始 len 长度的字符串
     */
    public static String deleteStartLenString(String s, int len) {
        return s.length() > len ? s.substring(len) : "";
    }

    /**
     * 删除 s 结尾 len 长度的字符串
     */
    public static String deleteEndLenString(String s, int len) {
        return s.length() > len ? s.substring(0, s.length() - len) : "";
    }

    /**
     * 组装字符串
     */
    public static String join(Object[] os, String split) {
        if (os != null) {
            if (os.length == 1) {
                return String.valueOf(os[0]);
            }
            StringJoiner joiner = new StringJoiner(split);
            for (Object o : os) {
                joiner.add(String.valueOf(o));
            }
            return joiner.toString();
        }
        return "";
    }

    public static String join(Object... o) {
        return join(o, "");
    }

    /**
     * 分割字符串,支持正则特殊符号
     */
    public static String[] split(String s, String split) {
        int i = 0;
        StringTokenizer st = new StringTokenizer(s == null ? "" : s, split);
        String[] tokens = new String[st.countTokens()];
        while (st.hasMoreElements()) {
            tokens[i] = st.nextToken();
            i++;
        }
        return tokens;
    }

    /**
     * array 转 List
     */
    public static <T> List<T> arrayToList(T... a) {
        return new ArrayList<>(Arrays.asList(a));
    }

    /**
     * list 转 array
     */
    public static <T> T[] listToArray(List<T> list) {
        T[] array = (T[]) Array.newInstance(list.get(0).getClass(), list.size());
        return list.toArray(array);
    }

    /**
     * list 转 array
     * 支持:List<String> 转 Integer[] | Long[] 等常用转换
     */
    public static <T> T[] listToArray(List list, Class<T> clazz) {
        T[] array = (T[]) Array.newInstance(clazz, list.size());
        for (int i = 0; i < list.size(); i++) {
            array[i] = castType(list.get(i), clazz);
        }
        return array;
    }

    /**
     * 逆转 List
     */
    public static void reverse(List<?> list) {
        Collections.reverse(list);
    }


    /* ---------------------------------------------------------------------------------------------------------------------------------------------*/
    private static final char[] base64EncodeChars = new char[]{'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q',
            'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r',
            's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/'};

    private static byte[] base64DecodeChars = new byte[]{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 62, -1, -1, -1, 63, 52, 53, 54, 55, 56, 57, 58, 59, 60,
            61, -1, -1, -1, -1, -1, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1, -1, -1,
            -1, -1, -1, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, -1, -1, -1, -1, -1};

    /**
     * Base64编码
     * byte[] => s
     */
    public static String encodeBase64(byte[] data) {
        StringBuffer sb = new StringBuffer();
        int len = data.length;
        int i = 0;
        int b1, b2, b3;

        while (i < len) {
            b1 = data[i++] & 0xff;
            if (i == len) {
                sb.append(base64EncodeChars[b1 >>> 2]);
                sb.append(base64EncodeChars[(b1 & 0x3) << 4]);
                sb.append("==");
                break;
            }
            b2 = data[i++] & 0xff;
            if (i == len) {
                sb.append(base64EncodeChars[b1 >>> 2]);
                sb.append(base64EncodeChars[((b1 & 0x03) << 4) | ((b2 & 0xf0) >>> 4)]);
                sb.append(base64EncodeChars[(b2 & 0x0f) << 2]);
                sb.append("=");
                break;
            }
            b3 = data[i++] & 0xff;
            sb.append(base64EncodeChars[b1 >>> 2]);
            sb.append(base64EncodeChars[((b1 & 0x03) << 4) | ((b2 & 0xf0) >>> 4)]);
            sb.append(base64EncodeChars[((b2 & 0x0f) << 2) | ((b3 & 0xc0) >>> 6)]);
            sb.append(base64EncodeChars[b3 & 0x3f]);
        }
        return sb.toString();
    }

    /**
     * Base64解码
     * s => byte[]
     */
    @SuppressWarnings("Duplicates")
    public static byte[] decodeBase64(String encodeString, String charsetName) throws UnsupportedEncodingException {
        byte[] data = encodeString.getBytes(charsetName);
        int len = data.length;
        ByteArrayOutputStream buf = new ByteArrayOutputStream(len);
        int i = 0;
        int b1, b2, b3, b4;

        while (i < len) {
            /* b1 */
            do {
                b1 = base64DecodeChars[data[i++]];
            } while (i < len && b1 == -1);
            if (b1 == -1) {
                break;
            }

            /* b2 */
            do {
                b2 = base64DecodeChars[data[i++]];
            } while (i < len && b2 == -1);
            if (b2 == -1) {
                break;
            }
            buf.write((b1 << 2) | ((b2 & 0x30) >>> 4));

            /* b3 */
            do {
                b3 = data[i++];
                if (b3 == 61) {
                    return buf.toByteArray();
                }
                b3 = base64DecodeChars[b3];
            } while (i < len && b3 == -1);
            if (b3 == -1) {
                break;
            }
            buf.write(((b2 & 0x0f) << 4) | ((b3 & 0x3c) >>> 2));

            /* b4 */
            do {
                b4 = data[i++];
                if (b4 == 61) {
                    return buf.toByteArray();
                }
                b4 = base64DecodeChars[b4];
            } while (i < len && b4 == -1);
            if (b4 == -1) {
                break;
            }
            buf.write(((b3 & 0x03) << 6) | b4);
        }
        return buf.toByteArray();
    }

    /* ---------------------------------------------------------------------------------------------------------------------------------------------*/

    /**
     * Unicode编码
     * encodeString => s
     */
    public static String encodeUnicode(String encodeString) {
        StringBuffer unicode = new StringBuffer();
        for (int i = 0; i < encodeString.length(); i++) {
            char c = encodeString.charAt(i);
            // 转换为unicode
            unicode.append("\\u" + Integer.toHexString(c));
        }
        return unicode.toString();
    }

    private static Pattern pattern = Pattern.compile("(\\\\u(\\p{XDigit}{4}))");

    /**
     * Unicode解码
     * decodeString => s
     */
    public static String decodeUnicode(String decodeString) {
        Matcher matcher = pattern.matcher(decodeString);
        char ch;
        while (matcher.find()) {
            ch = (char) Integer.parseInt(matcher.group(2), 16);
            decodeString = decodeString.replace(matcher.group(1), ch + "");
        }
        return decodeString;
    }

    /**
     * url编码
     * encodeString => s
     */
    public static String encodeUrl(String encodeString, String charsetName) throws UnsupportedEncodingException {
        return URLEncoder.encode(encodeString, charsetName).replaceAll("%2F", "/").replaceAll("%3A", ":").replaceAll("%3F", "?").replaceAll("%3D", "=").replaceAll("%26", "&").replaceAll("%23", "#").replaceAll("\\+", "%20");
    }

    /**
     * url解码
     * decodeString => s
     */
    public static String decodeUrl(String decodeString, String charsetName) throws UnsupportedEncodingException {
        return URLDecoder.decode(decodeString, charsetName);
    }

    /**
     * 16进制编码
     * encodeString => s
     */
    public static String encodeHex(String encodeString, String charsetName) throws UnsupportedEncodingException {
        StringBuilder sb = new StringBuilder();
        byte[] bytes = encodeString.getBytes(charsetName);
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(bytes[i] & 0xFF);
            if (hex.length() < 2) {
                sb.append(0);
            }
            sb.append(hex);
        }
        return sb.toString();
    }

    /**
     * 16进制解码
     * decodeString => s
     */
    public static String decodeHex(String decodeString, String charsetName) throws UnsupportedEncodingException {
        char[] hexData = decodeString.toCharArray();
        int len = hexData.length;
        if ((len & 1) != 0) {
            throw new RuntimeException("Odd number of characters.");
        } else {
            byte[] out = new byte[len >> 1];
            int i = 0;
            int f;
            for (int j = 0; j < len; ++i) {
                f = toDigit(hexData[j]) << 4;
                ++j;
                f |= toDigit(hexData[j]);
                ++j;
                out[i] = (byte) (f & 255);
            }
            return new String(out, charsetName);
        }
    }

    /**
     * char => int
     */
    public static int toDigit(char ch) {
        int digit = Character.digit(ch, 16);
        if (digit == -1) {
            throw new RuntimeException("Illegal hexadecimal character " + ch);
        } else {
            return digit;
        }
    }

    /**
     * object => map
     */
    public static <T> Map<String, Object> objectToMap(T object) {
        Map<String, Object> map = new HashMap<>();
        Field[] fields = object.getClass().getDeclaredFields();
        try {
            for (Field field : fields) {
                field.setAccessible(true);
                map.put(field.getName(), field.get(object));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    /**
     * map => object
     */
    public static <T> T mapToObject(Map<String, Object> map, T object) {
        try {
            Field[] fields = object.getClass().getDeclaredFields();
            for (Field field : fields) {
                int mod = field.getModifiers();
                if (Modifier.isStatic(mod) || Modifier.isFinal(mod)) {
                    continue;
                }
                field.setAccessible(true);
                field.set(object, map.get(field.getName()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * @description 复制属性
     * @date 2019-08-07 09:41:23
     * @author houyu for.houyu@qq.com
     */
    public static void copyProperties(Object source, Object target){
        try{
            PropertyDescriptor[] sourcePropertyDescriptors = Introspector.getBeanInfo(source.getClass()).getPropertyDescriptors();
            PropertyDescriptor[] targetPropertyDescriptors = Introspector.getBeanInfo(target.getClass()).getPropertyDescriptors();
            //
            for(PropertyDescriptor sourceProperty : sourcePropertyDescriptors){
                if(!"class".equals(sourceProperty.getName())) { // 跳过class属性, 这个属性不可以复制的
                    for(PropertyDescriptor targetProperty : targetPropertyDescriptors){
                        if(sourceProperty.getName().equals(targetProperty.getName())){
                            // Method setter = targetProperty.getWriteMethod();
                            targetProperty.getWriteMethod().invoke(target, sourceProperty.getReadMethod().invoke(source));
                        }
                    }
                }
            }
        }catch(IntrospectionException | IllegalAccessException | InvocationTargetException e){
            e.printStackTrace();
        }
    }

    /**
     * 组装 Map
     */
    public Map collectionsToMap(Collection collectionKey, Collection collectionValue) {
        Map map = new LinkedHashMap();
        if (!isEmpty(new Object[]{collectionKey, collectionValue}) && collectionKey.size() == collectionValue.size()) {
            Iterator keyIterator = collectionKey.iterator();
            Iterator valueIterator = collectionValue.iterator();
            while (keyIterator.hasNext()) {
                map.put(keyIterator.next(), valueIterator.next());
            }
        }
        return map;
    }


    /**
     * @description 数据类型转换
     * @date 2019-08-08 14:56:48
     * @author houyu for.houyu@qq.com
     */
    public static <T> T castType(Object value, Class<T> targetType) {
        if(value.getClass().equals(targetType)) {   // 类型相同不需要转换了
            return (T) value;
        }
        value = String.valueOf(value);              // 统一转为String, 然后调用具体的String的构造方法 or valueOf()
        try {
            return targetType.getDeclaredConstructor(String.class).newInstance(value);  // 获取构造参数是String的构造方法并且执行
        } catch(NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            System.err.println("castType() has warn: " + e.getMessage());// 报错说明该方法不存在或者不允许访问
        }
        try {                                       // 这里必须分开try, 因为反射获取不到构造或者方法的时候直接报错的, 导致没有办法尝试使用valueOf()
            return (T) targetType.getDeclaredMethod("valueOf", String.class).invoke(targetType, value);    // 获取valueOf方法并且执行
        } catch(NoSuchMethodException | SecurityException | IllegalAccessException | InvocationTargetException e) {
            System.err.println("castType() has warn: " + e.getMessage());
        }
        return null;
    }

    /**
     * 获取默认的解析时间类型
     */
    private static SimpleDateFormat getDateFormat(String formatStyle) {
        return !isEmpty(formatStyle) ? new SimpleDateFormat(formatStyle) : new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }

    /**
     * Date 转 String
     *
     * @param date   时期
     * @param format 格式   yyyy-MM-dd HH:mm:ss
     * @return
     */
    public static String formatDate(Date date, String... format) {
        return !isEmpty(format) ? getDateFormat(format[0]).format(date) : getDateFormat("").format(date);
    }

    /**
     * StringDate 转 Date
     * parse:yyyy-MM-dd HH:mm:ss<br>
     *
     * @param StringDate 2018-05-11 10:10:10
     * @param format     格式   yyyy-MM-dd HH:mm:ss
     * @return
     */
    public static Date parseData(String StringDate, String... format) {
        SimpleDateFormat dateFormat = !isEmpty(format) ? getDateFormat(format[0]) : getDateFormat("");
        try {
            return dateFormat.parse(StringDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取系统时间   yyyy-MM-dd HH:mm:ss
     *
     * @param formatStyle
     * @return
     */
    public static String getSysTime(String... formatStyle) {
        return formatDate(new Date());
    }

    /**
     * List 反向选择,
     * -1 >> 倒数第一个
     * 0  >> 第一个
     * 1  >> 第二个
     *
     * @param list  list
     * @param index 位置索引
     * @return
     */
    public static <E> E listReverse(List<E> list, int index) {
        return index < 0 ? list.get(list.size() + index) : list.get(index);
    }

    /**
     * valueLists旋转,行变成列,列变成行<br>
     * 注意的是,valueLists必须是矩形,行数、列数固定的。
     *
     * @param valueLists
     */
    public static List<List> listsRotate(List<List> valueLists) {
        List<List> bigList = new ArrayList<>();
        int outSize = valueLists.size();
        int inSize = valueLists.get(0).size();
        for (int i = 0; i < inSize; i++) {
            List minList = new ArrayList<>();
            for (int j = 0; j < outSize; j++) {
                minList.add(valueLists.get(j).get(i));
            }
            bigList.add(minList);
        }
        return bigList;
    }

    /**
     * 转义正则特殊字符 （$()*+.[]?\^{},|）
     *
     * @param regexWord
     * @return
     */
    private static final String[] fbsArr = new String[]{"\\", "$", "(", ")", "*", "+", ".", "[", "]", "?", "^", "{", "}", "|"};

    public static String transferenceRegexWord(String regexWord) {
        for (String key : fbsArr) {
            if (regexWord.contains(key)) {
                regexWord = regexWord.replaceAll(key, "\\" + key);
            }
        }
        return regexWord;
    }

    /**
     * 获取 start 和 end 之间的String
     */
    public static String subStringBetween(String s, String start, String end) {
        return subStringBetween(s, start, end, false);
    }

    /**
     * 获取 start 和 end 之间的String
     * @param largerScale 是否大范围
     */
    public static String subStringBetween(String s, String start, String end, boolean largerScale) {
        int startIndex = s.indexOf(start);
        int endIndex = largerScale ? s.lastIndexOf(end) : s.indexOf(end, startIndex + start.length());
        String substring;
        try {
            substring = s.substring(startIndex + start.length(), endIndex);
        } catch(Exception e) {
            substring = "";
        }
        return substring;
    }

    /**
     * 获取 start 和 end 之间的String,组成一个List
     */
    public static List<String> subStringsBetween(String s, String start, String end) {
        List<String> result = new ArrayList<>(16);
        for(int i, j; (i = s.indexOf(start)) > -1 && (j = s.indexOf(end, i + 1)) > -1 ; ) {
            String substring = s.substring(i + start.length(), j);
            result.add(substring);
            s = s.substring(j);//  + end.length()
        }
        return result;
    }

    /**
     * 删除在内部字符串(一大串)
     * 以prefix开头,suffix结束的字符串()
     * 区别:"test_prefix_我是内部数据1_suffix_我是数据2_prefix_我是内部数据2_suffix_test"      >>     "test_prefixsuffix_test"
     */
    public static String deleteBigInside(String s, String prefix, String suffix) {
        return s.replace(subStringBetween(s, prefix, suffix), "");
    }

    /**
     * 删除在内部字符串(全局部小串)
     * 以prefix开头,suffix结束的字符串
     * 区别:"test_prefix_我是内部数据1_suffix_我是数据2_prefix_我是内部数据2_suffix_test"      >>     "test_prefixsuffix_我是数据2_prefixsuffix_test"
     */
    public static String deleteSmallInside(String s, String prefix, String suffix) {
        List<String> list = subStringsBetween(s, prefix, suffix);
        for (String temp : list) {
            s = s.replace(temp, "");
        }
        return s;
    }

    /**
     * 文本匹配正则
     */
    public static <T> T regexMatcher(String text, String regex, Class<T> clazz) {
        Matcher matcher = Pattern.compile(regex).matcher(text);
        List<String> list = new ArrayList<>();
        while (matcher.find()) {
            list.add(regex.contains("(") && regex.contains(")") ? matcher.group(1) : matcher.group());
        }
        if (clazz.equals(String.class)) {
            return (T) (list.size() > 0 ? list.get(0) : "");
        } else if (clazz.getTypeName().contains("List")) {
            return (T) list;
        } else {
            return (T) list;
        }
    }

    /**
     * 生成MD5
     *
     * @param bytes
     * @return
     */
    public static String md5(byte[] bytes) {
        byte[] secretBytes = null;
        try {
            secretBytes = MessageDigest.getInstance("md5").digest(bytes);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        String md5code = new BigInteger(1, secretBytes).toString(16);
        StringBuilder builder = new StringBuilder(md5code);
        for (int i = 0, len = md5code.length(); i < 32 - len; i++) {
            builder.insert(0, 0);
        }
        return builder.toString();
    }

    /**
     * 获取 list 中的重复出现的个数
     *
     * @param list
     * @return Map
     */
    public static Map<String, Integer> getAloneCountInList(List<String> list) {

        if (list == null) {
            return new HashMap(8);
        }
        // -- 获取单独的数量
        final Map<String, Integer> aloneValueHashMap = new HashMap<>();
        for (String s : list) {
            if (aloneValueHashMap.containsKey(s)) {
                aloneValueHashMap.put(s, aloneValueHashMap.get(s).intValue() + 1);
            } else {
                aloneValueHashMap.put(s, 1);
            }
        }
        return getSortDescMapByValue(aloneValueHashMap);
    }

    /**
     * 获取 通过value 降序的Map
     */
    public static Map<String, Integer> getSortDescMapByValue(final Map<String, Integer> map) {
        // -- 根据value的数量排序
        Map<String, Integer> orderByValueTreeMap = new TreeMap<>((obj1, obj2) -> map.get(obj2).compareTo(map.get(obj1)) > 0 ? 1 : -1);
        orderByValueTreeMap.putAll(map);
        return orderByValueTreeMap;
    }

    /**
     * 在src中随机获取len长度的串
     */
    public static String random(String src, int len) {
        StringBuilder stringBuilder = new StringBuilder();
        int length = src.length();
        for (int i = 0; i < len; i++) {
            int index = (int) (Math.random() * length);
            stringBuilder.append(src.charAt(index));
        }
        return stringBuilder.toString();
    }

    /**
     * 获取错误的详细信息
     */
    public static String getDetailMessage(Throwable e) {
        ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
        e.printStackTrace(new PrintWriter(arrayOutputStream, true));
        return arrayOutputStream.toString();
    }

    /**
     * @description 获取设置固定时间在目前之后的时间
     * @date 2019-08-28 09:46:53
     * @author houyu for.houyu@foxmail.com
     */
    public static Date getCurrentAfterFixedTime(String HH_mm_ss) {
        String setDateString = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        Date setDateTime;
        try {
            setDateTime = new SimpleDateFormat("yyyy-MM-dd HH_mm_ss").parse(setDateString + " " + HH_mm_ss);
        } catch(ParseException e) {
            throw new RuntimeException(e);
        }
        //
        if(setDateTime.compareTo(new Date()) <= 0) {
            setDateTime = new Date(setDateTime.getTime() + 1000 * 60 * 60 * 24);    // 加一天
        }
        return setDateTime;
    }

}