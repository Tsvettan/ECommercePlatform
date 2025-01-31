package bg.softuni.ECommercePlatform.controller;

import bg.softuni.ECommercePlatform.model.CategoryEntity;
import bg.softuni.ECommercePlatform.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public String listCategories(Model model) {
        model.addAttribute("categories", categoryService.getAllCategories());
        return "categories";
    }

    @PreAuthorize(value = "hasRole('ADMIN')")
    @GetMapping("/create")
    public String showCreateCategoryForm(Model model) {
        model.addAttribute("category", new CategoryEntity());
        return "create-category";
    }

    @PreAuthorize(value = "hasRole('ADMIN')")
    @PostMapping("/create")
    public String createCategory(
            @Valid @ModelAttribute("category") CategoryEntity category,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return "create-category"; // Return form with errors
        }

        categoryService.saveCategory(category);
        return "redirect:/categories";
    }

    @GetMapping("/edit/{id}")
    public String showEditCategoryForm(@PathVariable Long id, Model model) {
        model.addAttribute("category", categoryService.getCategoryById(id));
        return "edit-category";
    }

    @PostMapping("/edit/{id}")
    public String editCategory(
            @PathVariable Long id,
            @Valid @ModelAttribute("category") CategoryEntity updatedCategory,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return "edit-category"; // Return form with errors
        }

        categoryService.updateCategory(id, updatedCategory);
        return "redirect:/categories";
    }

    @GetMapping("/delete/{id}")
    public String deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return "redirect:/categories";
    }
}
