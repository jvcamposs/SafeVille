package com.fiap.safeville.controller;

import com.fiap.safeville.model.Crime;
import com.fiap.safeville.service.CrimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class WebController {

    @Autowired
    private CrimeService service;

    @GetMapping("/")
    public String index(Model model) {
        List<Crime> crimes = service.listarTodos();
        model.addAttribute("crimes", crimes);
        return "index";
    }
}
