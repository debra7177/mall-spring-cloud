package org.eu.mall.order.web;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Configuration
public class PageController {
    @GetMapping("/{page.html}")
    public String listPage(@PathVariable("page") String page) {
        return page;
    }
}
