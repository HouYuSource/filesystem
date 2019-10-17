package cn.shaines.filesystem.controller;

import cn.shaines.filesystem.annotation.ChainRequired;
import cn.shaines.filesystem.entity.File;
import cn.shaines.filesystem.service.FileService;
import cn.shaines.filesystem.util.IdWorker;
import cn.shaines.filesystem.util.MvcUtil;
import cn.shaines.filesystem.util.QiNiuUtil;
import cn.shaines.filesystem.vo.Result;
import com.qiniu.common.QiniuException;
import com.qiniu.util.StringUtils;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author houyu
 * @createTime 2019/3/10 0:52
 */
@CrossOrigin(origins = "*", maxAge = 3600) 				// 允许所有域名访问
@Controller
@RequestMapping("/file")
public class FileController {

    @Value("${hostname}")
    private String hostname;

    @Autowired
    private IdWorker idWorker;
    @Autowired
    private FileService fileService;

    // ------------------------------------------------------------------------------------- //
    /**
     * 页面跳转
     */
    @RequestMapping
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
        Page<File> page = "".equals(name) ? fileService.findAll(pageable) : fileService.findAllByNameIsContaining(name, pageable);
        return Result.success("请求成功", page);
    }

    /**
     * 在线显示文件
     */
    @GetMapping("/view/{name}")
    @ResponseBody
    @ChainRequired
    public void view(@PathVariable String name, HttpServletResponse response) throws IOException {
        File file = fileService.findByName(name);
        if (file != null){
            byte[] bytes = QiNiuUtil.findByKey(file.getName());
            MvcUtil.viewData(response, bytes, file.getName());
        }
    }

    /**
     * 下载文件
     */
    @GetMapping("/download/{name}")
    @ResponseBody
    public void download(@PathVariable String name, HttpServletResponse response) throws IOException {
        File file = fileService.findByName(name);
        if (file != null ){
            byte[] bytes = QiNiuUtil.findByKey(file.getName());
            MvcUtil.downloadData(response, bytes, file.getName());
        }
    }

    /**
     * 删除,根据名称删除
     */
    @DeleteMapping("/delete/{name}")
    @ResponseBody
    public Result delete(@PathVariable String name) throws QiniuException {
        File file = fileService.findByName(name);
        if (file == null){
            return Result.error("文件不存在", null);
        }
        QiNiuUtil.delete(name);
        fileService.deleteById(file.getId());
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

        fileService.deleteAllByNameIn(nameArray);
        QiNiuUtil.delete(nameArray);

        return Result.SUCCESS;
    }

    /**
     * 上传
     */
    @PostMapping("/upload")
    @ResponseBody
    @ChainRequired(ChainRequired.Type.CHECK)
    public Result upload(@RequestParam("file") MultipartFile multipartFile, HttpServletRequest request) throws IOException {
        // 获取文件名
        String filename = multipartFile.getOriginalFilename();
        if (fileService.findByName(filename) != null){
            return Result.error("文件名重复，请更名再上传", null);
        }

        byte[] bytes = multipartFile.getBytes();
        QiNiuUtil.upload(filename, bytes);

        File file = new File();
        file.setId(idWorker.nextId() + "");
        file.setName(filename);
        file.setType(multipartFile.getContentType());
        file.setSize(bytes.length);
        file.setDate(new Date());

        String mapping = StringUtils.join(new String[] { hostname,
                ("".equals(request.getContextPath()) ? "" : "/" + request.getContextPath()),
                "/file/view/",
                file.getName() }, "");
        file.setMapping(mapping);

        fileService.save(file);
        Map<String, String> dataMap = new HashMap<>(2);
        dataMap.put("url", mapping);
        return Result.success("操作成功", dataMap);
    }

}
