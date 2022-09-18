package com.chuang.seckill.controller;


import com.chuang.seckill.common.RespBean;
import com.chuang.seckill.entity.User;
import com.chuang.seckill.service.UserService;
import com.chuang.seckill.utils.MD5Util;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author chuang
 * @since 2022-07-25
 */
@RestController
@RequestMapping("/user")
@Api(value = "用户表", tags = "用户表")
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/info", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation("返回用户信息")
    public RespBean info(User user) {
        return RespBean.success(user);
    }



    /**
     * 测试创建用户
     */
    @GetMapping("/createusers")
    @ResponseBody
    public RespBean createUsers (){
        Long a = 15387194871L;
        for (int i = 1; i <= 2000; i++) {
            User user = new User();
            user.setNickname("chuang" + i);
            user.setPassword(MD5Util.inputPassToDBPass("12345","123456"));
            user.setSalt("123456");
            user.setHead("https://img0.baidu.com/it/u=1694074520,2517635995&fm=253&fmt=auto&app=138&f=JPEG?w=500&h=500");
            user.setRegisterDate(LocalDateTime.now());
            userService.save(user);
        }
        return RespBean.success();
    }

    @GetMapping("updateusers")
    @ResponseBody
    public RespBean updateusers (){
        List<User> list = userService.list();
        for (User user : list) {
            user.setPassword("e8404cf4ddcbd05f976aca9de53d1bc6");
            userService.updateById(user);
        }
        return RespBean.success();
    }

}

