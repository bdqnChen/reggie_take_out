package com.bdqn.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.bdqn.reggie.common.R;
import com.bdqn.reggie.pojo.User;
import com.bdqn.reggie.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public R<User> login(@RequestBody User user , HttpServletRequest request){
        log.info("电话号码:{}",user.getPhone());
        if (user.getPhone() != null) {
            //判断用户是否为新用户，如果是则进行自动注册
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone, user.getPhone());
            User userOne = userService.getOne(queryWrapper);
            if (userOne == null) {
                userService.save(user);

            }
            userOne = userService.getOne(queryWrapper);
//            String phone = user.getPhone();
//            userOne = new User();
//            userOne.setPhone(phone);
            request.getSession().setAttribute("user",userOne.getId());
            return R.success(user);
        }
        return R.error("登录失败");
    }
}
