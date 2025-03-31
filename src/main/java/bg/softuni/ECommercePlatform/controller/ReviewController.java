package bg.softuni.ECommercePlatform.controller;

import bg.softuni.ECommercePlatform.model.ProductEntity;
import bg.softuni.ECommercePlatform.model.ReviewEntity;
import bg.softuni.ECommercePlatform.model.UserEntity;
import bg.softuni.ECommercePlatform.repository.ProductRepository;
import bg.softuni.ECommercePlatform.repository.ReviewRepository;
import bg.softuni.ECommercePlatform.service.ProductService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/products")
public class ReviewController {

    private final ProductService productService;
    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;

    public ReviewController(ProductService productService, ReviewRepository reviewRepository, ProductRepository productRepository) {
        this.productService = productService;
        this.reviewRepository = reviewRepository;
        this.productRepository = productRepository;
    }

    @PostMapping("/{id}/reviews")
    public String addOrUpdateReview(
            @PathVariable Long id,
            @RequestParam int rating,
            @RequestParam String comment,
            @AuthenticationPrincipal UserEntity user) {

        ProductEntity product = productService.getProductById(id);
        productService.addOrUpdateReview(product, user, rating, comment);

        return "redirect:/products/" + id;
    }

    @PostMapping("/{id}/reviews/{reviewId}/delete")
    public String deleteReview(
            @PathVariable Long id,
            @PathVariable Long reviewId,
            @AuthenticationPrincipal UserEntity user) {

        ReviewEntity review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Review not found"));

        if (!review.getUser().getId().equals(user.getId())) {
            throw new IllegalStateException("You are not allowed to delete this review");
        }

        reviewRepository.delete(review);

        ProductEntity product = review.getProduct();
        List<ReviewEntity> reviews = reviewRepository.findByProductId(product.getId());
        double averageRating = reviews.stream()
                .mapToInt(ReviewEntity::getRating)
                .average()
                .orElse(0.0);
        product.setRating(averageRating);
        productRepository.save(product);

        return "redirect:/products/" + id;
    }
}
