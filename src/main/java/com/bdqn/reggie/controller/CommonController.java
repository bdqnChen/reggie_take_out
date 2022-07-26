package com.bdqn.reggie.controller;

import com.bdqn.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.UUID;

/**
 * 负责文件的上传与下载
 */
@Slf4j
@RestController
@RequestMapping("/common")
public class CommonController {

    @Value("${reggie.path}")
    private String basePath;

    /**
     * 文件上传
     * @param file
     * @return
     */
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file){

        log.info("file:{}",file.toString());

        //获取原始文件名
        String originalFilename = file.getOriginalFilename();
        String suffx = originalFilename.substring(originalFilename.lastIndexOf("."));

        //使用UUID创建新文件名
        String fileName = UUID.randomUUID().toString() + suffx;

        //获取当前文件夹
        File dir = new File(basePath);
        //判断当前目录是否存在
        if (!dir.exists()){
            //目录不存在，创建一个目录
            dir.mkdirs();
        }

        //当前file是一个临时文件，需要转存到一个位置，否则本次请求结束后就会删除
        try {
            file.transferTo(new File(basePath + fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return R.success(fileName);
    }

    /**
     * 文件下载
     * @param name
     * @param response
     */
    @GetMapping("/download")
    public void download(String name , HttpServletResponse response){

        try {
            //通过输入流下载文件
            FileInputStream fileInputStream = new FileInputStream(new File(basePath + name));
            //通过输出流将文件写文浏览器，并展示
            ServletOutputStream outputStream = response.getOutputStream();

            response.setContentType("image/jpeg");

            int len = 0;
            byte[] bytes = new byte[1024];
            while ((len = fileInputStream.read(bytes)) != -1){
                outputStream.write(bytes,0,len);
                outputStream.flush();
            }

            //关闭输入出流
            outputStream.close();
            fileInputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
