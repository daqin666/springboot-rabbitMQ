package com.example.dead;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("test")
public class TestController {

    @Resource
    private BusinessProducer businessProducer;

    @RequestMapping(value = "sendMsg")
    public void sendMsg() {
        Order order = new Order();
        order.setId("20221126000001");
        order.setType("1");
        businessProducer.send(order);
    }

    @RequestMapping(value = "sendMsg2")
    public void sendMsg2() {
        for (int i = 1; i <= 6; i++) {
            Order order = new Order();
            order.setId("2022112600000" + i);
            order.setType(i + "");
            order.setStatus(i + "");
            businessProducer.send(order);
        }
    }
}
