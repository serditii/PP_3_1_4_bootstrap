package ru.kata.spring.boot_security.demo.controller;


import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.kata.spring.boot_security.demo.entity.Role;
import ru.kata.spring.boot_security.demo.entity.User;
import ru.kata.spring.boot_security.demo.service.UserService;
import ru.kata.spring.boot_security.demo.util.UserValidator;

import javax.validation.Valid;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/")
public class UserController {

    private final UserService userService;

    private final UserValidator userValidator;

    public UserController(UserService userServise, UserValidator userValidator) {
        this.userService = userServise;
        this.userValidator = userValidator;
    }

    @GetMapping("/admin")
    public String adminPage(ModelMap model, Principal principal) {
        model.addAttribute("user1", userService.
                showUser(userService.findByUsername(principal.getName()).getId()));
        List<User> list = userService.getListUsers();
        model.addAttribute("users", list);
        return "admin";
    }

    @GetMapping("/logout")
    public String logout() {
        return "redirect:/logout";
    }

    @GetMapping("/close")
    public String close() {
        return "redirect:/users";
    }

    @GetMapping("/user")
    public String userPage(ModelMap model, Principal principal) {
        model.addAttribute("user", userService.
                showUser(userService.findByUsername(principal.getName()).getId()));
        return "user";
    }

    @GetMapping("/user1")
    public String user1Page(ModelMap model, Principal principal) {
        model.addAttribute("user", userService.
                showUser(userService.findByUsername(principal.getName()).getId()));
        return "user1";
    }

    @GetMapping
    public String printWelcome(ModelMap model) {
        List<String> messages = new ArrayList<>();
        messages.add("Hello!");
        messages.add("I'm Spring MVC application");
        messages.add("5.2.0 version by sep'19 ");
        model.addAttribute("messages", messages);
        return "index";
    }

    @GetMapping("/users")
    public String showAll(ModelMap model) {
        List<User> list = userService.getListUsers();
        model.addAttribute("users", list);
        return "users";
    }

    @PostMapping("/users")
    public String create(@ModelAttribute() @Valid User user,
                         BindingResult bindingResult) {
        userValidator.validate(user, bindingResult);
        if (bindingResult.hasErrors()) {
            return "new";
        }
        if (user.getRole().equals("ROLE_ADMIN")) {
            user.setRoles(Collections.singleton(new Role(2)));
        }
        if (user.getRole().equals("ROLE_USER")) {
            user.setRoles(Collections.singleton(new Role(1)));
        }
        userService.addUser(user);
        return "redirect:/admin";
    }

    @GetMapping("/users/new")
    public String newUser(@ModelAttribute() User user, Principal principal, ModelMap model) {
        model.addAttribute("user1", userService.
                showUser(userService.findByUsername(principal.getName()).getId()));
        return "new";
    }

    @GetMapping("/users/{id}")
    public String show(@PathVariable() long id, ModelMap model) {
        model.addAttribute("user", userService.showUser(id));
        return "delete";
    }

    @GetMapping("/users/{id}/edit")
    public String edit(@PathVariable() long id, ModelMap model) {
        User userUpdate = userService.showUser(id);
        userUpdate.setPassword(null);
        model.addAttribute("user", userUpdate);
        return "edit";
    }

    @PatchMapping("/users/{id}")
    public String update(@ModelAttribute() @Valid User user,
                         BindingResult bindingResult, ModelMap model) {
        userValidator.validate(user, bindingResult);
        if (bindingResult.hasErrors()) {
            return "admin";
        }
        if (user.getRole().equals("ROLE_ADMIN")) {
            user.setRoles(Collections.singleton(new Role(2)));
        } else user.setRoles(Collections.singleton(new Role(1)));
        if (user.getPassword() == null) {
            user.setPassword(userService.showUser(user.getId()).getPassword());
        }
        userService.updateUser(user);
        return "redirect:/admin";
    }

    @DeleteMapping("/users/{id}")
    public String delete(@PathVariable() long id) {
        userService.deleteUser(id);
        return "redirect:/admin";
    }
}

