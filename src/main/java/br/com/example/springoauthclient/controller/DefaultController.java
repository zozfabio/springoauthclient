package br.com.example.springoauthclient.controller;

import br.com.example.springoauthclient.service.DefaultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
class DefaultController {

    @Autowired
    @SuppressWarnings("SpringJavaAutowiringInspection")
    private DefaultService service;

    @RequestMapping("/")
    public String home() {
        return "home";
    }

    @RequestMapping("/get")
    public String get(Model model) {
        model.addAttribute("data", service.get());

        return "show";
    }

    @RequestMapping("/list")
    public String list(Model model) {
        model.addAttribute("data", service.list());

        return "show";
    }
}
