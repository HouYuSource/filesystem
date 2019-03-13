package cn.shaines.filesystem.controller;

import cn.shaines.filesystem.annotation.ChainRequired;
import cn.shaines.filesystem.entity.Fileobject;
import cn.shaines.filesystem.service.FileobjectService;
import cn.shaines.filesystem.util.IdWorker;
import cn.shaines.filesystem.util.QiniuUtil;
import cn.shaines.filesystem.vo.Result;
import com.qiniu.common.QiniuException;
import com.qiniu.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author houyu
 * @createTime 2019/3/10 0:52
 */
@CrossOrigin(origins = "*", maxAge = 3600) 				// 允许所有域名访问
@Controller
@RequestMapping("/file")
public class FileobjectController {

    @Value("${hostname}")
    private String hostname;

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private FileobjectService fileobjectService;

    // ------------------------------------------------------------------------------------- //
    // 页面跳转
    @RequestMapping("")
    public String empty(){
        return "redirect:/file/index";
    }
    @RequestMapping("/index")
    public String index(){
        return "/file/index";
    }

    // ------------------------------------------------------------------------------------- //

    /**
     * 分页查询文件
     */
    @GetMapping("/page")
    @ResponseBody
    public Result page(@RequestParam(defaultValue = "0") int pageIndex, @RequestParam(defaultValue = "5") int pageSize, @RequestParam String name) {
        Sort sort = new Sort(Sort.Direction.DESC, "date", "id");
        Pageable pageable = PageRequest.of(pageIndex, pageSize, sort);
        Page<Fileobject> page = "".equals(name) ? fileobjectService.findAll(pageable) : fileobjectService.findAllByNameIsContaining(name, pageable);
        return Result.success("请求成功", page);
    }

    /**
     * 在线显示文件
     */
    @GetMapping("/view/{name}")
    @ResponseBody
    @ChainRequired
    public ResponseEntity<Object> view(@PathVariable String name) {
        Fileobject fileobject = fileobjectService.findByName(name);
        if (fileobject != null ){
            try {
                byte[] bytes = QiniuUtil.findByKey(fileobject.getName());

                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, String.format("fileName=\"%s\"", fileobject.getName()))
                        .header(HttpHeaders.CONTENT_TYPE, fileobject.getType())
                        .header(HttpHeaders.CONTENT_LENGTH, fileobject.getSize() + "").header("Connection", "close")
                        .body(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("File cannot be found");
    }

    /**
     * 下载文件
     */
    @GetMapping("/download/{name}")
    @ResponseBody
    public ResponseEntity<Object> download(@PathVariable String name) {
        Fileobject fileobject = fileobjectService.findByName(name);
        if (fileobject != null ){
            try {
                byte[] bytes = QiniuUtil.findByKey(fileobject.getName());

                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; fileName=" + new String(fileobject.getName().getBytes("utf-8"),"ISO-8859-1"))
                        .header(HttpHeaders.CONTENT_TYPE, "application/octet-stream")
                        .header(HttpHeaders.CONTENT_LENGTH, fileobject.getSize() + "").header("Connection", "close")
                        .body(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("File cannot be found");
    }

    /**
     * 删除,根据名称删除
     */
    @DeleteMapping("/delete/{name}")
    @ResponseBody
    public Result delete(@PathVariable String name) throws QiniuException {

        Fileobject fileobject = fileobjectService.findByName(name);
        if (fileobject == null){
            return Result.error("文件不存在", null);
        }

        QiniuUtil.delete(name);

        fileobjectService.deleteById(fileobject.getId());

        return Result.SUCCESS;
    }

    /**
     * 删除,根据name集合删除
     * JSON.stringify(data)
     */
    @ResponseBody
    @RequestMapping(value = "/delete", method = RequestMethod.POST, consumes = "application/json")
    public Result delete(@RequestBody Map<String, Object> paramMap) throws QiniuException {

        List<String> nameList = (List<String>)paramMap.get("names");
        String[] nameArray = nameList.toArray(new String[nameList.size()]);

        fileobjectService.deleteAllByNameIn(nameArray);
        QiniuUtil.delete(nameArray);

        return Result.SUCCESS;
    }

    /**
     * 上传
     */
    @PostMapping("/upload")
    @ResponseBody
    @ChainRequired(ChainRequired.Type.CHECK)
    public Result upload(@RequestParam("file") MultipartFile file, HttpServletRequest request) throws IOException {
        // 获取文件名
        String filename = file.getOriginalFilename();
        if (fileobjectService.findByName(filename) != null){
            return Result.error("文件名重复，请更名再上传", null);
        }

        byte[] bytes = file.getBytes();
        QiniuUtil.upload(filename, bytes);

        Fileobject fileobject = new Fileobject();
        fileobject.setId(idWorker.nextId() + "");
        fileobject.setName(filename);
        fileobject.setType(file.getContentType());
        fileobject.setSize(bytes.length);
        fileobject.setDate(new Date());

        String mapping = StringUtils.join(new String[] { hostname,
                ("".equals(request.getContextPath()) ? "" : "/" + request.getContextPath()),
                "/file/view/",
                fileobject.getName() }, "");
        fileobject.setMapping(mapping);

        fileobjectService.save(fileobject);

        Map<String, String> dataMap = new HashMap<>(2);
        dataMap.put("url", mapping);

        return Result.success("操作成功", dataMap);
    }

}
