package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.User;
import com.itheima.reggie.service.UserService;
import com.itheima.reggie.utils.SMSUtils;
import com.itheima.reggie.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 发送手机短信验证码
     *
     * @param user
     * @return
     */
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session) {
        // 获取手机号
        String phone = user.getPhone();

        if (StringUtils.isNotEmpty(phone)) {
            // 生存随机的4位验证码
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            // 调用阿里云提供的短信服务API完成发送短信
            log.info("code={}", code);
            // 需要将生成的验证码保存到Session
            session.setAttribute(phone, code);
            return R.success("手机验证码短信发送成功："+code);
        }
        return R.error("手机验证码短信发送失败");
    }

    /**
     * 移动端用户登陆
     * @param map
     * @param session
     * @return
     */
    @PostMapping("/login")
    public R<User> login(@RequestBody Map map,HttpSession session){
        log.info("map={}",map);
        // 获取手机号和验证码
        String phone=map.get("phone").toString();
        String code=map.get("code").toString();


        // 从Session中获取保存的验证码
//        String code_session=session.getAttribute("code").toString();
        Object codeSession=session.getAttribute(phone);

        // 进行验证码的对比（页面提交的验证码和Session中保存的验证码对比
        if(codeSession!=null && codeSession.equals(code)){
            // 如果对比成功，说明登录成功

            // 判断当前手机号对应的用户是否为新用户，如果是新用户就自动完成注册
            LambdaQueryWrapper<User> queryWrapper=new LambdaQueryWrapper<>();
            // 添加查询条件，根据分类id进行查询
            queryWrapper.eq(User::getPhone,phone);
//            int userCount=UserService.count(queryWrapper);
//            if(userCount>0){
//                // 插入
//            }
            User user=userService.getOne(queryWrapper);
            if(user==null){
                // 如果用户未注册，则自动注册
                user=new User();
                user.setPhone(phone);
                user.setStatus(1);
                userService.save(user);

            }
            //这一行容易漏。。保存用户登录状态
            session.setAttribute("user",user.getId()); //在session中保存用户的登录状态,这样过滤器的时候就不会被拦截了
            return R.success(user);
        }
        return R.error("登录失败");


    }


}
