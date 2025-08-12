package com.art.artsea.controller;

import com.art.artsea.model.Category;
import com.art.artsea.model.User;
import com.art.artsea.service.CategoryService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    // Add Category
    @PostMapping("/admin-save-categories")
    public String addCategory(@ModelAttribute Category category, HttpSession session) {
        if (!isAdmin(session)) return "redirect:/";
        categoryService.saveCategory(category);
        return "redirect:/admin-manage-category";
    }

    // Delete category by ID
    @GetMapping("/admin-delete-category/{id}")
    public String deleteCategory(@PathVariable("id") Long id, HttpSession session) {
        if (!isAdmin(session)) return "redirect:/";
        categoryService.deleteCategoryById(id);
        return "redirect:/admin-manage-category";
    }

    // Edit Category Form
    @GetMapping("/admin-category-edit/{id}")
    public String editCategoryForm(@PathVariable("id") Long id, Model model, HttpSession session) {
        if (!isAdmin(session)) return "redirect:/";
        Category category = categoryService.getCategoryById(id);
        model.addAttribute("category", category);
        return "admin/edit-category";
    }

    // Update Category
    @PostMapping("/admin-category-update")
    public String updateCategory(@ModelAttribute Category category, HttpSession session) {
        if (!isAdmin(session)) return "redirect:/";
        categoryService.saveCategory(category); // JPA will update if ID exists
        return "redirect:/admin-manage-category";
    }

    //    Helper method
    private boolean isAdmin(HttpSession session) {
        Object user = session.getAttribute("user");
        if (user instanceof User loggedInUser) {
            return "ADMIN".equalsIgnoreCase(loggedInUser.getRole().name());
        }
        return false;
    }


}
