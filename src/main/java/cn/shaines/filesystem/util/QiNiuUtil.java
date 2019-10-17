package cn.shaines.filesystem.util;

import com.alibaba.fastjson.JSON;
import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * @author houyu
 * @createTime 2019/3/9 23:07
 */
public class QiNiuUtil {

    private static final String accessKey = "your accessKey";
    private static final String secretKey = "your secretKey";
    private static final String bucket = "your bucket";
    private static final Configuration cfg = new Configuration(Zone.zone0());
    private static final String domainOfBucket = "your domainOfBucket";
    /** 1小时，可以自定义链接过期时间 */
    private static final long expireInSeconds = 3600;

    public static int delete(String... ids) throws QiniuException {
        if (ids.length == 0) { return 0; }
        Auth auth = Auth.create(accessKey, secretKey);
        BucketManager bucketManager = new BucketManager(auth, cfg);
        BucketManager.BatchOperations batchOperations = new BucketManager.BatchOperations();
        batchOperations.addDeleteOp(bucket, ids);
        Response response = bucketManager.batch(batchOperations);
        //BatchStatus[] batchStatusList = response.jsonToObject(BatchStatus[].class);
        //BatchStatus status;
        //for (int i = 0; i < ids.length; i++) {
        //    status = batchStatusList[i];
        //    String key = ids[i];
        //    System.out.print(key + "\t");
        //    if (status.code == 200) {
        //        System.out.println("delete success");
        //    } else {
        //        System.out.println(status.data.error);
        //    }
        //}
        return ids.length;
    }

    public static byte[] findByKey(String key) throws IOException {
        String encodedFileName = CoreUtil.encodeUrl(key, "UTF-8");
        String url = String.format("%s/%s", domainOfBucket, encodedFileName);
        //Auth auth = Auth.create(accessKey, secretKey);
        //url = auth.privateDownloadUrl(url, expireInSeconds);
        InputStream inputStream = new URL(url).openConnection().getInputStream();
        return IOUtil.toByteArray(inputStream);
    }

    public static DefaultPutRet upload(String key, byte[] bytes) throws QiniuException {
        // 构造一个带指定Zone对象的配置类
        UploadManager uploadManager = new UploadManager(cfg);
        // 默认不指定key的情况下，以文件内容的hash值作为文件名
        Auth auth = Auth.create(accessKey, secretKey);
        String upToken = auth.uploadToken(bucket);
        // Response response = uploadManager.put(inputStream, key, upToken, null, null);
        // uploadManager.asyncPut(bytes, key, upToken, null, null, false, null);
        Response response = uploadManager.put(bytes, key, upToken);
        // 解析上传成功的结果
        DefaultPutRet putRet = JSON.parseObject(response.bodyString(), DefaultPutRet.class);
        //System.out.println(putRet.key);
        //System.out.println(putRet.hash);
        return putRet;
    }

//    public static void main(String[] args) throws Exception {
//        byte[] bytes = IoUtil.toByteArray(new FileInputStream("C:\\Users\\houyu\\Desktop\\testpng1.png"));
//        DefaultPutRet upload = upload("testpng1.png", bytes);
//        System.out.println(upload.hash);
//        byte[] byKey = findByKey("testpng1.png");
//        IoUtil.toFile(IoUtil.toInputStream(byKey), new File("C:\\Users\\houyu\\Desktop\\testpng666666.png"));
//        delete("2.jpg", "testpng1.png");
//    }

}
