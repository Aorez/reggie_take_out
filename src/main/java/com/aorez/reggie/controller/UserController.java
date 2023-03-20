package com.aorez.reggie.controller;

import com.aorez.reggie.common.R;
import com.aorez.reggie.entity.User;
import com.aorez.reggie.service.UserService;
import com.aorez.reggie.utils.SMSUtils;
import com.aorez.reggie.utils.ValidateCodeUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpServletRequest request) {
        String phone = user.getPhone();
        if (StringUtils.isEmpty(phone)) {
            return R.error("手机号为空");
        }

        //生成验证码
        String code = ValidateCodeUtils.generateValidateCode(4).toString();
        log.info("code " + code);
        //发送短信
//        SMSUtils.sendMessage("reggie", "", phone, code);
        //测试验证码
//        String code = "1111";

        //存入session
        request.getSession().setAttribute(phone, code);

        return R.success("验证码发送成功");
    }

    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpSession session) {
        //检查手机号和验证码是否匹配
        //不能强转为string
        String phone = (String) map.get("phone");
        String code = (String) map.get("code");
        String codeInSession = (String) session.getAttribute(phone);

        log.info("phone " + phone + " codeInSession " + codeInSession + " code " + code);

        if (codeInSession != null && codeInSession.equals(code)) {
            //验证码正确，判断是否已注册
            LambdaQueryWrapper<User> lqw = new LambdaQueryWrapper<>();
            lqw.eq(User::getPhone, phone);
            User user = userService.getOne(lqw);

            if (user == null) {
                user = new User();
                user.setPhone(phone);
                user.setStatus(1);
                userService.save(user);
            }

            session.setAttribute("user", user.getId());

            return R.success(user);
        }

        return R.error("验证码错误");
    }
}
