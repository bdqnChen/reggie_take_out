package com.bdqn.reggie.controller;

import com.bdqn.reggie.service.DishFlavorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/dishFlavor")
public class DishFlavorController {
    @Autowired
    private DishFlavorService dishFlavorService;
}
