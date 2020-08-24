package com.my.blog.website.controller.admin;

import com.github.pagehelper.PageInfo;
import com.my.blog.website.constant.WebConst;
import com.my.blog.website.controller.BaseController;
import com.my.blog.website.dto.LogActions;
import com.my.blog.website.dto.Types;
import com.my.blog.website.exception.TipException;
import com.my.blog.website.modal.Bo.RestResponseBo;
import com.my.blog.website.modal.Vo.AttachVo;
import com.my.blog.website.modal.Vo.UserVo;
import com.my.blog.website.service.IAttachService;
import com.my.blog.website.service.ILogService;
import com.my.blog.website.utils.Commons;
import com.my.blog.website.utils.TaleUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 附件管理
 *
 * @author liuyalong
 */
@Controller
@RequestMapping("admin/attach")
public class AttachController extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AttachController.class);

    public static final String CLASSPATH = TaleUtils.getUplodFilePath();

    @Resource
    private IAttachService attachService;

    @Resource
    private ILogService logService;

    /**
     * 附件页面
     */
    @GetMapping(value = "")
    public String index(HttpServletRequest request, @RequestParam(value = "page", defaultValue = "1") int page,
                        @RequestParam(value = "limit", defaultValue = "12") int limit) {
        PageInfo<AttachVo> attachPaginator = attachService.getAttachs(page, limit);
        request.setAttribute("attachs", attachPaginator);
        request.setAttribute(Types.ATTACH_URL.getType(), Commons.site_option(Types.ATTACH_URL.getType(), Commons.site_url()));
        request.setAttribute("max_file_size", WebConst.MAX_FILE_SIZE);
        return "admin/attach";
    }

    /**
     * 上传文件接口
     */
    @PostMapping(value = "upload")
    @ResponseBody
    @Transactional(rollbackFor = TipException.class)
    public RestResponseBo upload(HttpServletRequest request, @RequestParam("file") MultipartFile[] multipartFiles) {
        UserVo users = this.user(request);
        Integer uid = users.getUid();

        //先检查所有文件的大小和其他属性
        for (MultipartFile multipartFile : multipartFiles) {
            String fname = multipartFile.getOriginalFilename();
            //先检查所有文件的大小和其他属性(暂未实现)
            if (multipartFile.getSize() <= WebConst.MAX_FILE_SIZE) {
                return RestResponseBo.fail(fname + "文件太大");
            }
        }

        // 开始写文件
        for (MultipartFile multipartFile : multipartFiles) {
            String fname = multipartFile.getOriginalFilename();
            String fkey = TaleUtils.getFileKey(fname);
            String ftype = null;
            File file = new File(CLASSPATH + fkey);
            try {
                //区分图片文件和其他文件类似
                ftype = TaleUtils.isImage(multipartFile.getInputStream()) ? Types.IMAGE.getType() : Types.FILE.getType();
                FileCopyUtils.copy(multipartFile.getInputStream(), new FileOutputStream(file));
                attachService.save(fname, fkey, ftype, uid);
            } catch (IOException e) {
                e.printStackTrace();
                return RestResponseBo.fail(fname + "上传失败");
            }
        }

        return RestResponseBo.ok();
    }

    /**
     * 删除附件
     */
    @RequestMapping(value = "delete")
    @ResponseBody
    @Transactional(rollbackFor = TipException.class)
    public RestResponseBo delete(@RequestParam Integer id, HttpServletRequest request) {
        String msg = "附件删除失败";
        try {
            AttachVo attach = attachService.selectById(id);
            if (null == attach) {
                return RestResponseBo.fail("不存在该附件");
            }
            attachService.deleteById(id);
            boolean delete = new File(CLASSPATH + attach.getFkey()).delete();
            if (!delete) {
                return RestResponseBo.fail(msg);
            }
            logService.insertLog(LogActions.DEL_ARTICLE.getAction(), attach.getFkey(), request.getRemoteAddr(), this.getUid(request));
        } catch (Exception e) {

            if (e instanceof TipException) {
                msg = e.getMessage();
            } else {
                LOGGER.error(msg, e);
            }
            return RestResponseBo.fail(msg);
        }
        return RestResponseBo.ok();
    }

}
