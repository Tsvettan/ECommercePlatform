package bg.softuni.ECommercePlatform.service;

import bg.softuni.ECommercePlatform.model.CategoryEntity;
import bg.softuni.ECommercePlatform.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<CategoryEntity> getAllCategories() {
        return categoryRepository.findAll();
    }

    public CategoryEntity getCategoryById(Long id) {
        return categoryRepository.findById(id).orElseThrow();
    }

    public void saveCategory(CategoryEntity category) {
        categoryRepository.save(category);
    }

    public void updateCategory(Long id, CategoryEntity updatedCategory) {
        CategoryEntity existingCategory = getCategoryById(id);
        existingCategory.setName(updatedCategory.getName());
        categoryRepository.save(existingCategory);
    }

    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }
}
