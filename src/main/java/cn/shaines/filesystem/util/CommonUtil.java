package cn.shaines.filesystem.util;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.*;
import java.math.BigInteger;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @program: data-interface
 * @description: 基本工具类
 * @author: houyu
 * @create: 2018-12-06 16:13
 */
public class CommonUtil {

    public boolean checkEmpty(Object o) {
        return null == o ? true : (o instanceof String ? ((String) o).length() == 0 : false);
    }

    public boolean isEmpty(Object o) {
        return checkEmpty( o );
    }

    public boolean isNotEmpty(Object o) {
        return !isEmpty( o );
    }

    /**
     * 判断 os 是否为空,支持并多个判断,如果有一个为空都返回true
     *
     * @param os
     * @return
     */
    public boolean isEmptys(Object[] os) {
        if (null == os) { return true; }                            // 判断 os 数组是否为空
        for (Object o : os) {
            if (checkEmpty(o)) { return true; }                     // 判断里边的单个元素是否为空,如果有空,直接return true
        }
        return false;
    }

    public boolean isNotEmptys(Object[] os) {
        return !isEmptys(os);
    }

    public void assertCheckEmpty(Object o, String msg) {
        if (checkEmpty(o)) { throw new IllegalArgumentException(this.checkEmpty(msg) ? "Empty exception : assertCheckEmpty()" : msg); }
    }

    public void assertCheck(boolean b, String msg) {
        if (b) { throw new IllegalArgumentException(this.checkEmpty(msg) ? "AssertCheck exception : assertCheck()" : msg); }
    }

    public <T> T orElse(T t1, Object t2){
        return isEmpty(t1) ? (T) t2 : t1;
    }

    /**
     * Object 转 String, 支持设置默认值
     *
     * @param o
     * @param defVal
     * @return
     */
    public String objectToString(Object o, String... defVal) {
        return isEmpty(o) ? (isEmpty(defVal) ? "" : defVal[0]) : o.toString();
    }

    /**
     * Object 转 Integer, 支持设置默认值
     *
     * @param o
     * @param defVal
     * @return
     */
    public Integer objectToInteger(Object o, Integer... defVal) {
        try {
            return isEmpty(o) ? (isEmpty(defVal) ? -1 : defVal[0]) : Integer.parseInt(o.toString());
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return isEmpty(defVal) ? -1 : defVal[0];
        }
    }

    /**
     * 删除 s 开始 len 长度的字符串
     *
     * @param s
     * @param len
     * @return
     */
    public String deleteStartLenString(String s, int len) {
        return s.length() > len ? s.substring(len, s.length()) : "";
    }

    /**
     * 删除 s 结尾 len 长度的字符串
     *
     * @param s
     * @param len
     * @return
     */
    public String deleteEndLenString(String s, int len) {
        return s.length() > len ? s.substring(0, s.length() - len) : "";
    }

    /**
     * 组装字符串
     */
    public String join(Object[] os, String splitString){
        String s = "";
        if (os != null) {
            StringBuilder sBuffer = new StringBuilder();
            for (int i = 0; i < os.length; i++) {
                sBuffer.append(os[i]).append(splitString);
            }
            // 去掉最后的分隔符 splitString
            s = deleteEndLenString(sBuffer.toString(), splitString.length());
        }
        return s;
    }

    /**
     * 分割字符串,支持正则特殊符号
     */
    public String[] split(String s, String splitString) {
        int i = 0;
        StringTokenizer st = new StringTokenizer(s == null ? "" : s, splitString);
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
    public <T> List<T> arrayToList(T... a){
        return new ArrayList<>(Arrays.asList(a));
    }

    /**
     * list 转 array
     */
    public <T> T[] listToArray(List<T> list){
        T[] array = (T[]) Array.newInstance(list.get(0).getClass(), list.size());
        return list.toArray(array);
    }

    /**
     * list 转 array
     *      支持:List<String> 转 Integer[] | Long[] 等常用转换
     */
    public <T> T[] listToArray(List list, Class<T> clazz) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        T[] array = (T[]) Array.newInstance(clazz, list.size());
        boolean flag = clazz.getName().contains("String");
        Method valueOfMethod = clazz.getMethod("valueOf", flag ? int.class : list.get(0).getClass());
        for(int i = 0; i < list.size(); i++){
            Object invoke = flag ? valueOfMethod.invoke(clazz, list.get(i)) : valueOfMethod.invoke(clazz, list.get(i).toString());
            array[i] = (T) invoke;
        }
        return array;
    }

    /**
     * 逆转 List
     */
    public void reverse(List<?> list) {
        Collections.reverse(list);
    }


    /* ---------------------------------------------------------------------------------------------------------------------------------------------*/
    private final char[] base64EncodeChars = new char[]{'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q',
            'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r',
            's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/'};

    private byte[] base64DecodeChars = new byte[]{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 62, -1, -1, -1, 63, 52, 53, 54, 55, 56, 57, 58, 59, 60,
            61, -1, -1, -1, -1, -1, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1, -1, -1,
            -1, -1, -1, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, -1, -1, -1, -1, -1};

    /**
     * Base64编码
     * byte[] => s
     */
    public String encodeBase64(byte[] data) {
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
    public byte[] decodeBase64(String encodeString, String charsetName) throws UnsupportedEncodingException {
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
    public String encodeUnicode(String encodeString) {
        StringBuffer unicode = new StringBuffer();
        for (int i = 0; i < encodeString.length(); i++) {
            char c = encodeString.charAt(i);  // 取出每一个字符
            unicode.append("\\u" + Integer.toHexString(c));// 转换为unicode
        }
        return unicode.toString();
    }

    private Pattern pattern = Pattern.compile("(\\\\u(\\p{XDigit}{4}))");
    /**
     * Unicode解码
     * decodeString => s
     */
    public String decodeUnicode(String decodeString) {
        //Pattern pattern = Pattern.compile("(\\\\u(\\p{XDigit}{4}))");
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
    public String encodeUrl(String encodeString, String charsetName) throws UnsupportedEncodingException {
        return URLEncoder.encode(encodeString, charsetName).replaceAll("%2F", "/").replaceAll("%3A", ":").replaceAll("%3F", "?").replaceAll("%3D", "=").replaceAll("%26", "&").replaceAll("%23", "#").replaceAll("\\+","%20");
    }

    /**
     * url解码
     * decodeString => s
     */
    public String decodeUrl(String decodeString, String charsetName) throws UnsupportedEncodingException {
        return URLDecoder.decode(decodeString, charsetName);
    }

    /**
     * 16进制编码
     * encodeString => s
     */
    public String encodeHex(String encodeString, String charsetName) throws UnsupportedEncodingException {
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
    public String decodeHex(String decodeString, String charsetName) throws UnsupportedEncodingException {
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
    public int toDigit(char ch) {
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
    public <T> Map<String, Object> objectToMap(T object) {
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
    public <T> T mapToObject(Map<String, Object> map, T object) {
        try {
            Field[] fields = object.getClass().getDeclaredFields();
            for (Field field : fields) {
                int mod = field.getModifiers();
                if (Modifier.isStatic(mod) || Modifier.isFinal(mod)) { continue; }
                field.setAccessible(true);
                field.set(object, map.get(field.getName()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 组装 Map
     */
    public Map collectionsToMap(Collection collectionKey, Collection collectionValue) {
        Map map = new LinkedHashMap();
        if (!isEmptys(new Object[]{collectionKey, collectionValue}) && collectionKey.size() == collectionValue.size()) {
            Iterator keyIterator = collectionKey.iterator();
            Iterator valueIterator = collectionValue.iterator();
            while (keyIterator.hasNext()) {
                map.put(keyIterator.next(), valueIterator.next());
            }
        }
        return map;
    }

    /**
     * 获取默认的解析时间类型
     */
    private SimpleDateFormat getDateFormat(String formatStyle) {
        return !isEmpty(formatStyle) ? new SimpleDateFormat(formatStyle) : new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }

    /**
     * Date 转 String
     *
     * @param date   时期
     * @param format 格式   yyyy-MM-dd HH:mm:ss
     * @return
     */
    public String formatDate(Date date, String... format) {
        return !isEmptys(format) ? getDateFormat(format[0]).format(date) : getDateFormat("").format(date);
    }

    /**
     * StringDate 转 Date
     * parse:yyyy-MM-dd HH:mm:ss<br>
     *
     * @param StringDate 2018-05-11 10:10:10
     * @param format     格式   yyyy-MM-dd HH:mm:ss
     * @return
     */
    public Date parseData(String StringDate, String... format) {
        SimpleDateFormat dateFormat = !isEmpty(format) ? getDateFormat(format[0]) : getDateFormat("");
        try {
            return dateFormat.parse(StringDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new Date();
    }

    /**
     * 获取系统时间   yyyy-MM-dd HH:mm:ss
     *
     * @param formatStyle
     * @return
     */
    public String getSysTime(String... formatStyle) {
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
    public <E> E listReverse(List<E> list, int index) {
        return index < 0 ? list.get(list.size() + index) : list.get(index);
    }

    /**
     * valueLists旋转,行变成列,列变成行<br>
     * 注意的是,valueLists必须是矩形,行数、列数固定的。
     *
     * @param valueLists
     */
    public List<List> listsRotate(List<List> valueLists) {
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
    private final String[] fbsArr = new String[]{"\\", "$", "(", ")", "*", "+", ".", "[", "]", "?", "^", "{", "}", "|"};

    public String transferenceRegexWord(String regexWord) {
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
    public String subStringBetween(String s, String start, String end) {
        int startIndex = s.indexOf(start);
        int endIndex = s.lastIndexOf(end);
        return s.substring(startIndex == -1 ? 0 : startIndex + start.length(), endIndex == -1 ? 0 : endIndex);
    }

    /**
     * 获取 start 和 end 之间的String,组成一个List
     */
    public List<String> subStringsBetween(String s, String start, String end) {
        Matcher matcher = Pattern.compile(transferenceRegexWord(start) + "(.*?)" + transferenceRegexWord(end)).matcher(s);
        List<String> list = new ArrayList<>();
        while (matcher.find()) {
            list.add(matcher.group(1));
        }
        return list;
    }

    /**
     * 删除在内部字符串(一大串)
     *      以prefix开头,suffix结束的字符串()
     *      区别:"test_prefix_我是内部数据1_suffix_我是数据2_prefix_我是内部数据2_suffix_test"      >>     "test_prefixsuffix_test"
     */
    public String deleteBigInside(String s, String prefix, String suffix){
        return s.replace(subStringBetween(s, prefix, suffix), "");
    }

    /**
     * 删除在内部字符串(全局部小串)
     *      以prefix开头,suffix结束的字符串
     *      区别:"test_prefix_我是内部数据1_suffix_我是数据2_prefix_我是内部数据2_suffix_test"      >>     "test_prefixsuffix_我是数据2_prefixsuffix_test"
     */
    public String deleteSmallInside(String s, String prefix, String suffix){
        List<String> list = subStringsBetween(s, prefix, suffix);
        for (String temp : list){
            s = s.replace(temp, "");
        }
        return s;
    }

    /**
     * 文本匹配正则
     */
    public <T> T regexMatcher(String text, String regex, Class<T> clazz) {
        Matcher matcher = Pattern.compile(regex).matcher(text);
        List<String> list = new ArrayList<>();
        while(matcher.find()){
            list.add(regex.contains("(") && regex.contains(")") ? matcher.group(1) : matcher.group());
        }
        if (clazz.equals(String.class)){
            return (T) (list.size() > 0 ?  list.get(0) : "");
        }else if (clazz.getTypeName().contains("List")){
            return (T) list;
        }else {
            return (T) list;
        }
    }

    /**
     * 生成MD5
     *
     * @param bytes
     * @return
     */
    public String md5(byte[] bytes) {
        byte[] secretBytes = null;
        try {
            secretBytes = MessageDigest.getInstance("md5").digest(bytes);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        String md5code = new BigInteger(1, secretBytes).toString(16);
        for (int i = 0; i < 32 - md5code.length(); i++) {
            md5code = "0" + md5code;
        }
        return md5code;
    }

    /**
     * 获取 list 中的重复出现的个数
     *
     * @param list
     * @return Map
     */
    public Map<String, Integer> getAloneCountInList(List<String> list) {

        if (list == null){ return new HashMap(); }

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
    public Map<String, Integer> getSortDescMapByValue(final Map<String, Integer> map) {
        // -- 根据value的数量排序
        Map<String, Integer> orderByValueTreeMap = new TreeMap<String, Integer>(
                new Comparator<String>() {
                    @Override
                    public int compare(String obj1, String obj2) {
                        // return map.get(obj2).compareTo(map.get(obj1)) == 0 ? 1 : map.get(obj2).compareTo(map.get(obj1));
                        return map.get(obj1).equals(map.get(obj2)) ?  map.get(obj2).compareTo(map.get(obj1)) : 1;
                    }
                }
        );
        orderByValueTreeMap.putAll(map);
        return orderByValueTreeMap;
    }

    /**
     * 在src中随机获取len长度的串
     */
    public String random(int len, String src){
        StringBuilder stringBuilder = new StringBuilder();
        int length = src.length();
        for(int i = 0; i < len; i++){
            int index = (int)(Math.random() * length);
            stringBuilder.append(src.charAt(index));
        }
        return stringBuilder.toString();
    }

    /* ---------------------------------------单例模式---------------------------------------*/
    private CommonUtil() {}

    private static class SingletonHolder {
        private static final CommonUtil INSTANCE = new CommonUtil();
    }

    public static CommonUtil get() {
        return SingletonHolder.INSTANCE;
    }
    /* ---------------------------------------单例模式---------------------------------------*/



}
