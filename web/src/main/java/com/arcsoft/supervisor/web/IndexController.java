package com.arcsoft.supervisor.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


/**
 * Controller class for handling index.
 *
 * @author zw.
 */
@Controller
@RequestMapping("/index")
public class IndexController extends ControllerSupport {

    private static final String REDIRECT_HOME_INDEX = "redirect:/home/index";

    @RequestMapping(method = RequestMethod.GET)
    public String index() {
        return REDIRECT_HOME_INDEX;
    }
}
