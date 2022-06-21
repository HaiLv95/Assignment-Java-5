package com.assignmentjava.controller.admin;

import com.assignmentjava.dto.CategoryDTO;
import com.assignmentjava.dto.ProductDTO;
import com.assignmentjava.model.Category;
import com.assignmentjava.model.Product;
import com.assignmentjava.repository.CategoryRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequestMapping("/admin/categories-manager")
public class CategoryController {

    @Autowired
    CategoryRepository categoryRepository;

    boolean sortIndex = true;

    @GetMapping("")
    public String getView(@ModelAttribute("message") String message, Model model) {
        //truy van va sap xep
        Pageable pageable = PageRequest.of(0, 10, Sort.by((Sort.Direction.ASC), "id"));
        Page<Category> resultPage = categoryRepository.findAll(pageable);
        if (resultPage.getTotalPages() > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, resultPage.getTotalPages())
                    .boxed()
                    .collect(Collectors.toList());

            model.addAttribute("pageNumbers", pageNumbers);
        }
        model.addAttribute("categoriesPage", resultPage);
        if (message.length() > 0) {
            model.addAttribute("message", message);
        }
        return "admin/category/category-manager";
    }

    @GetMapping("/sort")
    public String sortAccount(@RequestParam("page") Optional<Integer> page,
                              @RequestParam("size") Optional<Integer> size,
                              @RequestParam("sortby") Optional<String> sortType,
                              @RequestParam("sort") Optional<Boolean> sort,
                              Model model,
                              @ModelAttribute("message") String message) {
        int curentPage = page.orElse(1);
        int pageSize = size.orElse(10);
        String sortName = sortType.orElse("id");
        Page<Category> resultPage = null;
        //truy van va sap xep
        Pageable pageable = PageRequest.of(curentPage - 1, pageSize, Sort.by((sortIndex ? Sort.Direction.DESC : Sort.Direction.ASC), sortName));
        resultPage = categoryRepository.findAll(pageable);
        boolean sortPage = sort.orElse(false);
        if (sortPage) sortIndex = !sortIndex;
        if (resultPage.getTotalPages() > 0) {
            //gender số trang ra các trang nhỏ
            List<Integer> pageNumbers = IntStream.rangeClosed(1, resultPage.getTotalPages())
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }
        model.addAttribute("categoriesPage", resultPage);
        if (message.length() > 0) {
            model.addAttribute("message", message);
        }
        return "admin/category/category-manager";
    }

    @GetMapping("/lock/{id}")
    public String deleteAccount(@PathVariable("id") Integer id, RedirectAttributes redirect) {
        Category category = categoryRepository.getById(id);

        category.setActivated(!category.getActivated());
        Category categoryUpdate = categoryRepository.save(category);
        if (categoryUpdate.getActivated()) {
            redirect.addFlashAttribute("message", "Mở khóa category \"" + categoryUpdate.getName() + "\" thành công!");
        } else {
            redirect.addFlashAttribute("message", "Khóa category \"" + categoryUpdate.getName() + "\" thành công!");
        }
        return "redirect:/admin/categories-manager";
    }

    @GetMapping("add")
    public String add(@ModelAttribute("message") String message, Model model) {
        if (message.length() > 0) {
            model.addAttribute("message", message);
        }
        CategoryDTO dto = new CategoryDTO();
        dto.setEdit(false);
        model.addAttribute("category", dto);
        return "/admin/category/add-category";
    }

    @GetMapping("edit/{id}")
    public String edit(@ModelAttribute("message") String message, Model model, @PathVariable("id") Integer id) {
        if (message.length() > 0) {
            model.addAttribute("message", message);
        }
        Category category = categoryRepository.getById(id);
        CategoryDTO dto = new CategoryDTO();
        dto.setEdit(true);
        BeanUtils.copyProperties(category, dto);
        model.addAttribute("category", dto);
        return "/admin/category/add-category";
    }

    @PostMapping("addOrEdit")
    public String addOrEdit(@Valid @ModelAttribute("category") CategoryDTO dto,
                            RedirectAttributes redirect) {
        Category category = new Category();
        BeanUtils.copyProperties(dto, category);
        categoryRepository.save(category);
        redirect.addFlashAttribute("message", "Thêm category " + dto.getName() + " thành công!");
        return "redirect:/admin/categories-manager";
    }
}
