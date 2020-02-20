package com.example.demo;

import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.Map;

@Controller
public class HomeController {

    @Autowired
    TodoRepository todoRepository;

    @Autowired
    CloudinaryConfig cloudc;

    @RequestMapping("/")
    public String listTodo(Model model) {
        model.addAttribute("todos", todoRepository.findAll());
        return "list";
    }

    @GetMapping("/add")
    public String todoForm(Model model) {
        model.addAttribute("todo", new Todo());
        return "todoform";
    }

    @PostMapping("/process")
    public String processForm(@Valid Todo todo, BindingResult result,
                              @RequestParam("file") MultipartFile file) {

        if (result.hasErrors()) {
            return "todoform";
        }

        if (file.isEmpty()) {
            return "todoform";
//            return "redirect:/add";
        }
        try {
            Map uploadResult = cloudc.upload(file.getBytes(), ObjectUtils.asMap("resourcetype", "auto"));
            todo.setPic(uploadResult.get("url").toString());
            todoRepository.save(todo);
        } catch (IOException e) {
            e.printStackTrace();
            return "redirect:/add";
        }

        return "redirect:/";

    }


//    @RequestMapping("/list/{id}")
//    public String listMessage(@PathVariable("id") long id, Model model) {
//        model.addAttribute("message", todoRepository.findById(id).get());
//        return "show";
//    }

//    @RequestMapping("/add/{id}")
//    public String addTodo(@PathVariable("id") long id, Model model) {
//        model.addAttribute("todo", todoRepository.findById(id).get());
//        return "todoform";
//    }

    @RequestMapping("/update/{id}")
    public String updateTodo(@PathVariable("id") long id, Model model) {
        model.addAttribute("todo", todoRepository.findById(id).get());
        return "todoform";
    }

    @RequestMapping("/delete/{id}")
    public String delTodo(@PathVariable("id") long id) {
        todoRepository.deleteById(id);
        return "redirect:/";
    }
}
