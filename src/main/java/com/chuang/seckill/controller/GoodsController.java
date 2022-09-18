package com.chuang.seckill.controller;


import com.chuang.seckill.common.RespBean;
import com.chuang.seckill.dto.DetailDto;
import com.chuang.seckill.dto.GoodsDto;
import com.chuang.seckill.entity.User;
import com.chuang.seckill.service.GoodsService;
import com.chuang.seckill.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 商品表 前端控制器
 * </p>
 *
 * @author chuang
 * @since 2022-07-25
 */


@Controller
@RequestMapping("goods")
@Api(value = "商品", tags = "商品")
public class GoodsController {

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private UserService userService;

    @Autowired
    RedisTemplate redisTemplate;


    @Autowired
    private ThymeleafViewResolver thymeleafViewResolver;
    /**
     * 使用redis缓存页面渲染
     * @param model
     * @param user
     * @return
     */
    @ApiOperation("商品列表")
    @ResponseBody
    @GetMapping(value = "/toList", produces = "text/html;charset=utf-8")
    public String toList(Model model, User user, HttpServletRequest request, HttpServletResponse response) {
        String html = (String) redisTemplate.opsForValue().get("goodsList");
        if (!StringUtils.isEmpty(html)) {
            return html;
        }

        model.addAttribute("goodsList", goodsService.findGoodsVo());
        model.addAttribute("user", user);

        WebContext webContext = new WebContext(request, response, request.getServletContext(), request.getLocale(), model.asMap());
        html = thymeleafViewResolver.getTemplateEngine().process("goodsList", webContext);

        //这里可以使用线程池异步更新redis
        if (!StringUtils.isEmpty(html)) {
            redisTemplate.opsForValue().set("goodsList", html, 5, TimeUnit.MINUTES);
        }
        return html;
    }


    @ApiOperation("商品详情")
    @GetMapping(value = "/detail/{goodsId}")
    @ResponseBody
    public RespBean toDetail(User user, @PathVariable Long goodsId) {
        GoodsDto goodsVo = goodsService.findGoodsVobyGoodsId(goodsId);
        Date startDate = goodsVo.getStartDate();
        Date endDate = goodsVo.getEndDate();
        Date nowDate = new Date();
        //秒杀状态
        int seckillStatus = 0;
        //秒杀倒计时
        int remainSeconds = 0;

        if (nowDate.before(startDate)) {
            //秒杀还未开始0
            remainSeconds = (int) ((startDate.getTime() - nowDate.getTime()) / 1000);
        } else if (nowDate.after(endDate)) {
            //秒杀已经结束
            seckillStatus = 2;
            remainSeconds = -1;
        } else {
            //秒杀进行中
            seckillStatus = 1;
            remainSeconds = 0;
        }
        DetailDto detailVo = new DetailDto();
        detailVo.setUser(user);
        detailVo.setGoodsVo(goodsVo);
        detailVo.setRemainSeconds(remainSeconds);
        detailVo.setSecKillStatus(seckillStatus);
        return RespBean.success(detailVo);
    }

//    @ApiOperation("商品详情")
//    @GetMapping(value = "/todetail/{goodsId}",produces = "text/html;charset=utf-8")
//    @ResponseBody
//    public String toDetail(Model model, User user, @PathVariable Long goodsId, HttpServletRequest request, HttpServletResponse response) {
//
//        String html = (String) redisTemplate.opsForValue().get("goodsDetail:" + goodsId);
//        if (!StringUtils.isEmpty(html)) {
//            return html;
//        }
//
//        model.addAttribute("user", user);
//        GoodsDto goodsDto = goodsService.findGoodsVobyGoodsId(goodsId);
//        Date startDate = goodsDto.getStartDate();
//        Date endDate = goodsDto.getEndDate();
//        Date nowDate = new Date();
//        //秒杀状态
//        int seckillStatus = 0;
//        //秒杀倒计时
//        int remainSeconds = 0;
//        if (nowDate.before(startDate)) {
//            //秒杀还未开始0
//            remainSeconds = (int) ((startDate.getTime() - nowDate.getTime()) / 1000);
//        } else if (nowDate.after(endDate)) {
//            //秒杀已经结束
//            seckillStatus = 2;
//            remainSeconds = -1;
//        } else {
//            //秒杀进行中
//            seckillStatus = 1;
//            remainSeconds = 0;
//        }
//        model.addAttribute("goods", goodsService.findGoodsVobyGoodsId(goodsId));
//        model.addAttribute("remainSeconds", remainSeconds);
//        model.addAttribute("seckillStatus", seckillStatus);
//
//        WebContext webContext = new WebContext(request, response, request.getServletContext(), request.getLocale(), model.asMap());
//        html = thymeleafViewResolver.getTemplateEngine().process("goodsDetail", webContext);
//        if (!StringUtils.isEmpty(html)) {
//            redisTemplate.opsForValue().set("goodsDetail:" + goodsId, html, 5, TimeUnit.MINUTES);
//        }
//        return html;
//    }




}

