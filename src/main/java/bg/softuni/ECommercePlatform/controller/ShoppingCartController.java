package bg.softuni.ECommercePlatform.controller;

import bg.softuni.ECommercePlatform.model.CartItemEntity;
import bg.softuni.ECommercePlatform.model.ProductEntity;
import bg.softuni.ECommercePlatform.model.ShoppingCartEntity;
import bg.softuni.ECommercePlatform.model.UserEntity;
import bg.softuni.ECommercePlatform.service.ProductService;
import bg.softuni.ECommercePlatform.service.ShoppingCartService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/cart")
public class ShoppingCartController {

    private final ShoppingCartService shoppingCartService;
    private final ProductService productService;

    public ShoppingCartController(ShoppingCartService shoppingCartService, ProductService productService) {
        this.shoppingCartService = shoppingCartService;
        this.productService = productService;
    }

    @GetMapping
    public String viewCart(@AuthenticationPrincipal UserEntity user, Model model) {
        if (user == null) {
            return "redirect:/login?error=You need to login to access the cart";
        }

        ShoppingCartEntity cart = shoppingCartService.getCartByUser(user);
        double totalCost = shoppingCartService.calculateTotalCost(user);
        model.addAttribute("cart", cart);
        model.addAttribute("totalCost", totalCost);
        return "cart";
    }

    @PostMapping("/add")
    public String addToCart(@RequestParam Long productId, @RequestParam int quantity,
                            @AuthenticationPrincipal UserEntity user, RedirectAttributes redirectAttributes) {

        if (user == null) {
            return "redirect:/login";
        }

        ProductEntity product = productService.getProductById(productId);
        ShoppingCartEntity cart = shoppingCartService.getCartByUser(user);

        shoppingCartService.addToCard(cart, product, quantity);

        redirectAttributes.addFlashAttribute("successMessage" + product.getName()
                + " added to cart!");

        return "redirect:/cart/add-success";
    }

    @PostMapping("/update")
    public String updateQuantity(@RequestParam Long productId, @RequestParam int quantity,
                                 @AuthenticationPrincipal UserEntity user) {
        ShoppingCartEntity cart = shoppingCartService.getCartByUser(user);
        ProductEntity product = productService.getProductById(productId);

        if (quantity <= 0) {
            shoppingCartService.removeFromCart(cart, product);
        } else {
            shoppingCartService.addToCard(cart, product, quantity - cart.getItems().stream()
                    .filter(item -> item.getProduct().equals(product))
                    .findFirst()
                    .map(CartItemEntity::getQuantity)
                    .orElse(0));
        }
        return "redirect:/cart";
    }

    @PostMapping("/remove")
    public String removeFromCart(@RequestParam Long productId,
                                 @AuthenticationPrincipal UserDetails userDetails) {
        UserEntity user = (UserEntity) userDetails;
        ShoppingCartEntity cart = shoppingCartService.getCartByUser(user);
        ProductEntity product = productService.getProductById(productId);
        shoppingCartService.removeFromCart(cart, product);
        return "redirect:/cart";
    }
}
