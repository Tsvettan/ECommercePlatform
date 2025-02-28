package bg.softuni.ECommercePlatform.controller;

import bg.softuni.ECommercePlatform.model.ProductEntity;
import bg.softuni.ECommercePlatform.model.ReviewEntity;
import bg.softuni.ECommercePlatform.service.CategoryService;
import bg.softuni.ECommercePlatform.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;
    private final CategoryService categoryService;

    public ProductController(ProductService productService, CategoryService categoryService) {
        this.productService = productService;
        this.categoryService = categoryService;
    }

    @GetMapping("/{id}")
    public String productDetails(@PathVariable Long id, Model model) {
        ProductEntity product = productService.getProductById(id);
        model.addAttribute("product", product);
        model.addAttribute("reviews", productService.getReviewsForProduct(id));
        model.addAttribute("newReview", new ReviewEntity());
        return "product-details";
    }

    @GetMapping
    public String listProducts(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) Double priceMin,
            @RequestParam(required = false) Double priceMax,
            @RequestParam(required = false) Integer rating,
            Model model) {

        List<ProductEntity> products = productService.getAllProducts();

        // Filter by search
        if (search != null && !search.isEmpty()) {
            products = products.stream()
                    .filter(product -> product.getName().toLowerCase().contains(search.toLowerCase()))
                    .collect(Collectors.toList());
        }

        // Filter by category
        if (category != null && !category.isEmpty()) {
            products = productService.getProductsByCategory(category);
        }

        // Filter by price range
        if (priceMin != null || priceMax != null) {
            double min = (priceMin != null) ? priceMin : 0;
            double max = (priceMax != null) ? priceMax : Double.MAX_VALUE;

            products = products.stream()
                    .filter(product -> product.getPrice() >= min && product.getPrice() <= max)
                    .collect(Collectors.toList());
        }

        // Filter by rating
        if (rating != null) {
            products = products.stream()
                    .filter(product -> product.getRating() >= rating)
                    .collect(Collectors.toList());
        }

        // Apply sorting
        if (sort != null) {
            switch (sort) {
                case "price-asc":
                    products.sort(Comparator.comparing(ProductEntity::getPrice));
                    break;
                case "price-desc":
                    products.sort(Comparator.comparing(ProductEntity::getPrice).reversed());
                    break;
                case "rating":
                    products.sort(Comparator.comparing(ProductEntity::getRating).reversed());
                    break;
            }
        }

        model.addAttribute("products", productService.getAllProducts());
        return "products";
    }

    @PreAuthorize(value = "hasRole('ADMIN')")
    @GetMapping("/create")
    public String showCreateProductForm(Model model) {
        model.addAttribute("product", new ProductEntity());
        model.addAttribute("categories", categoryService.getAllCategories());
        return "create-product";
    }

    @PreAuthorize(value = "hasRole('ADMIN')")
    @PostMapping("/create")
    public String createProduct(
            @Valid @ModelAttribute("product") ProductEntity product,
            BindingResult bindingResult,
            Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", categoryService.getAllCategories());
            return "create-product";
        }

        productService.saveProduct(product);
        return "redirect:/products";
    }

    @GetMapping("/edit/{id}")
    public String showEditProductForm(@PathVariable Long id, Model model) {
        model.addAttribute("product", productService.getProductById(id));
        model.addAttribute("categories", categoryService.getAllCategories());
        return "edit-product";
    }

    @PostMapping("/edit/{id}")
    public String editProduct(
            @PathVariable Long id,
            @Valid @ModelAttribute("product") ProductEntity updatedProduct,
            BindingResult bindingResult,
            Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", categoryService.getAllCategories());
            return "edit-product"; // Return form with errors
        }

        productService.updateProduct(id, updatedProduct);
        return "redirect:/products";
    }

    @PreAuthorize("hasRole('ADMIN')") // next with value = "hasRole('ADMIN')"
    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return "redirect:/products";
    }
}
