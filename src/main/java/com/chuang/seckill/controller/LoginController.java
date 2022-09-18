package com.chuang.seckill.controller;
import com.chuang.seckill.common.RespBean;
import com.chuang.seckill.service.UserService;
import com.chuang.seckill.vo.LoginVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

/**
 * @Author chuang
 * @Date 2022/07/25 09:43
 **/

@Controller
@RequestMapping("/login")
@Api(value = "登录", tags = "登录")
@Slf4j
public class LoginController {

    @Autowired
    public UserService userService;


    /**
     * 跳转登录页面
     **/
    @ApiOperation("跳转登录页面")
    @GetMapping(value = "/toLogin")
    public String toLogin() {
        return "login";
    }

    @ApiOperation("登录接口")
    @PostMapping(value = "/doLogin")
    @ResponseBody
    public RespBean doLogin(@Valid LoginVo loginVo, HttpServletRequest request, HttpServletResponse response) {
        log.info("{}", loginVo);
        return userService.doLogin(loginVo, request, response);
    }

}
