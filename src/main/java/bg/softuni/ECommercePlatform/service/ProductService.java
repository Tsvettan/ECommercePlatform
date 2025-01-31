package bg.softuni.ECommercePlatform.service;

import bg.softuni.ECommercePlatform.model.ProductEntity;
import bg.softuni.ECommercePlatform.model.ReviewEntity;
import bg.softuni.ECommercePlatform.model.UserEntity;
import bg.softuni.ECommercePlatform.repository.ProductRepository;
import bg.softuni.ECommercePlatform.repository.ReviewRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ReviewRepository reviewRepository;

    public ProductService(ProductRepository productRepository, ReviewRepository reviewRepository) {
        this.productRepository = productRepository;
        this.reviewRepository = reviewRepository;
    }

    public void addReview(ProductEntity product, ReviewEntity review) {
        reviewRepository.save(review);

        List<ReviewEntity> reviews = reviewRepository.findByProductId(product.getId());
        double averageRatings = reviews
                .stream()
                .mapToInt(ReviewEntity::getRating)
                .average()
                .orElse(0.0);

        product.setRating(averageRatings);
        productRepository.save(product);
    }

    public void addOrUpdateReview(ProductEntity product, UserEntity user, int rating, String comment) {
        List<ReviewEntity> existingReviews = reviewRepository.findByProductId(product.getId());
        ReviewEntity existingReview = existingReviews.stream()
                .filter(review -> review.getUser().getId().equals(user.getId()))
                .findFirst()
                .orElse(null);

        if (existingReview != null) {
            existingReview.setRating(rating);
            existingReview.setComment(comment);
            reviewRepository.save(existingReview);
        } else {
            // Add a new review
            ReviewEntity newReview = new ReviewEntity(product, user, rating, comment);
            reviewRepository.save(newReview);
        }

        // Recalculate and update product's average rating
        List<ReviewEntity> reviews = reviewRepository.findByProductId(product.getId());
        double averageRating = reviews.stream()
                .mapToInt(ReviewEntity::getRating)
                .average()
                .orElse(0.0);
        product.setRating(averageRating);
        productRepository.save(product);
    }

    public ProductEntity getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product with ID " + id + " not found"));
    }

    public void saveProduct(ProductEntity product) {
        productRepository.save(product);
    }

    public List<ProductEntity> getFilteredProducts(String search, Long categoryId, Double priceMin, Double priceMax, Integer rating) {
        List<ProductEntity> products = productRepository.findAll();

        if (search != null && !search.isEmpty()) {
            products = products.stream()
                    .filter(product -> product.getName().toLowerCase().contains(search.toLowerCase()))
                    .collect(Collectors.toList());
        }

        if (categoryId != null) {
            products = products.stream()
                    .filter(product -> product.getCategory().getId().equals(categoryId))
                    .collect(Collectors.toList());
        }

        if (priceMin != null) {
            products = products.stream()
                    .filter(product -> product.getPrice() >= priceMin)
                    .collect(Collectors.toList());
        }
        if (priceMax != null) {
            products = products.stream()
                    .filter(product -> product.getPrice() <= priceMax)
                    .collect(Collectors.toList());
        }

        if (rating != null) {
            products = products.stream()
                    .filter(product -> product.getRating() != null && product.getRating() >= rating)
                    .collect(Collectors.toList());
        }

        return products;
    }

    public List<ReviewEntity> getReviewsForProduct(Long productId) {
        return reviewRepository.findByProductId(productId);
    }

    public void updateProduct(Long id, ProductEntity updatedProduct) {
        ProductEntity existingProduct = getProductById(id);
        existingProduct.setName(updatedProduct.getName());
        existingProduct.setDescription(updatedProduct.getDescription());
        existingProduct.setPrice(updatedProduct.getPrice());
        existingProduct.setStock(updatedProduct.getStock());
        existingProduct.setCategory(updatedProduct.getCategory());
        productRepository.save(existingProduct);
    }

    public List<ProductEntity> getProductsByCategory(String category) {
        return productRepository.findByCategoryName(category);
    }

    public List<ProductEntity> getAllProducts() {
        return productRepository.findAll();
    }

    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }
}
