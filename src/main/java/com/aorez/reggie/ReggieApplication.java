package com.aorez.reggie;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Slf4j
@SpringBootApplication
@ServletComponentScan
@EnableTransactionManagement
@EnableCaching
public class ReggieApplication {
    public static void main(String[] args) {
        //关闭热部署
//        System.setProperty("spring.devtools.restart.enabled", "false");
        SpringApplication.run(ReggieApplication.class);
        log.info("Application psvm");

        //访问移动端front时一直Whitelabel Error Page，替换front文件夹后需要删除target
        //切换设备仿真后，要选择一个手机型号
    }
}
