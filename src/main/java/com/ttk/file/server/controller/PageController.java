package com.ttk.file.server.controller;

import com.ttk.file.server.domain.UploadedFile;
import com.ttk.file.server.service.IFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.List;

@RequestMapping("/file/page")
@Controller
public class PageController {

    @Autowired
    private IFileService fileService;

    @RequestMapping("/listFiles")
    public String listFiles(Model model) {
        List<UploadedFile> files = fileService.getAllFiles();
        model.addAttribute("files", files);
        return "listFiles";
    }

    @RequestMapping("/listtest")
    public String listtest() {
        return "listFileVue";
    }

    @RequestMapping("/deleteById")
    public String deleteById(long id) {
        fileService.deleteById(id);
        return "redirect:listFiles";
    }

    @RequestMapping("/showById")
    public String showById(Model model, long id) {
        model.addAttribute("id", id);
        return "img";
    }

    @PostMapping("/upload")
    public String upload(@RequestParam("file") MultipartFile file) throws IOException {
        String upload = fileService.upload(file);
        ModelAndView modelAndView = new ModelAndView("listFiles");
        modelAndView.setViewName("listFiles");
        modelAndView.addObject("path", upload);
        return "redirect:listFiles";
    }

}
