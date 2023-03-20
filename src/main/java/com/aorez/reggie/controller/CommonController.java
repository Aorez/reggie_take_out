package com.aorez.reggie.controller;

import com.aorez.reggie.common.R;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/common")
public class CommonController {

    @Value("${reggie.path}")
    private String path;

    @RequestMapping("/upload")
    public R<String> upload(MultipartFile file) {
        String directory = System.getProperty("user.dir") + path;
        File dir = new File(directory);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String suffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));

        String filename = UUID.randomUUID().toString() + suffix;

        try {
            file.transferTo(new File(directory + filename));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return R.success(filename);
    }

    @GetMapping("/download")
    public void download(HttpServletResponse response, String name) throws IOException {
//        try {
            FileInputStream fileInputStream = new FileInputStream(new File(System.getProperty("user.dir") + path + name));
            ServletOutputStream outputStream = response.getOutputStream();
            byte[] bytes = new byte[1024];

            int length = 0;
            while ((length = fileInputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, length);
                outputStream.flush();
            }

            fileInputStream.close();
            outputStream.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }
}
