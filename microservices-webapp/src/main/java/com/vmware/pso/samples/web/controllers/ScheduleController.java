package com.vmware.pso.samples.web.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class ScheduleController extends AbstractController {

    private static final String MENU_ITEM = "schedule";

    @RequestMapping(value = { "/", "/schedule" }, method = RequestMethod.GET)
    public String schedule(final Model model) {
        model.addAttribute(MENU_ITEM_PARAM, MENU_ITEM);
        return "schedule";
    }
}
