package com.bdqn.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bdqn.reggie.common.R;
import com.bdqn.reggie.pojo.Employee;
import com.bdqn.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

/**
 * 员工管理
 */
@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    /**
     * 员工登录方法
     * @param request
     * @param employee
     * @return
     */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request , @RequestBody Employee employee){
        //将页面提交的密码进行md加密
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        //更具提交的用户名来查询数据库
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername,employee.getUsername());
        Employee employee1 = employeeService.getOne(queryWrapper);

        //如果没有查到数据则放回登录失败
        if (employee1 == null){
            return R.error("登录失败");
        }

        //密码比对，如果不一致则放回登录失败结果
        if (!employee1.getPassword().equals(password)){
            return R.error("登录失败") ;
        }

        //查看员工状态(status)是否可用
        if (employee1.getStatus() == 0 ){
            return  R.error("账号已被禁用");
        }

        //登录成功，将员工ID放入Session并放回登录结果
        request.getSession().setAttribute("employee",employee1.getId());
        return R.success(employee1);
    }

    /**
     * 员工登出方法
     */
    @PostMapping("/logout")
    public R<String> logOut(HttpServletRequest request){
        //清楚session中保存的员工ID
        request.getSession().removeAttribute("employee");
        return R.success("登出成功");
    }

    /**
     * 新增员工
     * @param employee
     * @return
     */
    @PostMapping
    public R<String> sava(HttpServletRequest request , @RequestBody Employee employee){
        log.info("新增员工,员工信息：{}",employee.toString());
        //设置员工初始密码123456，并且使用MD5加密
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));

        long threadId = Thread.currentThread().getId();
        log.info("线程ID为:{}",threadId);

//        employee.setCreateTime(LocalDateTime.now());
//        employee.setUpdateTime(LocalDateTime.now());
//        employee.setCreateUser((Long) request.getSession().getAttribute("employee"));
//        employee.setUpdateUser((Long) request.getSession().getAttribute("employee"));

        employeeService.save(employee);
        log.info("员工信息添加成功，员工账号：{}",employee.getUsername());
        return R.success("添加员工成功");
    }

    /**
     * 员工分页信息查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page , int pageSize , String name){
        log.info("page = {} , pageSize = {} , name = {}",page,pageSize,name);
        //构造分页构造器
        Page pageinfo = new Page(page,pageSize);

        //构造条件构造器
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper();
        //添加条件
        queryWrapper.like(StringUtils.isNotEmpty(name),Employee::getName,name);
        //添加排序条件
        queryWrapper.orderByDesc(Employee::getUpdateTime);

        //执行查询
        employeeService.page(pageinfo,queryWrapper);

        return R.success(pageinfo);
    }

    /**
     * 根据ID修改员工信息
     * @param employee
     * @return
     */
    @PutMapping
    public R<String> update(HttpServletRequest request ,@RequestBody Employee employee){
        log.info(employee.toString());

        long threadId = Thread.currentThread().getId();
        log.info("线程ID为:{}",threadId);
//        employee.setUpdateTime(LocalDateTime.now());
//        employee.setUpdateUser((Long) request.getSession().getAttribute("employee"));

        employeeService.updateById(employee);

        return R.success("员工状态更改成功");
    }

    /**
     * 根据ID查询员工信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id){
        log.info("查询员工信息......");
        Employee byId = employeeService.getById(id);
        if (byId!=null) {
            return R.success(byId);
        }
        return R.error("没有查询到员工信息");
    }
}
