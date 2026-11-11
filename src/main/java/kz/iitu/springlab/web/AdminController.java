package kz.iitu.springlab.web;

import kz.iitu.springlab.catalog.AppUserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final AppUserRepository users;

    public AdminController(AppUserRepository users) {
        this.users = users;
    }

    @GetMapping
    public String adminPage(Model model) {
        model.addAttribute("users", users.findAll());
        return "admin";
    }
}