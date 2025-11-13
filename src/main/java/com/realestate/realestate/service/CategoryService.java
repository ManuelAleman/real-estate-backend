package com.realestate.realestate.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.realestate.realestate.dto.category.CategoryRequest;
import com.realestate.realestate.dto.category.CategoryResponse;
import com.realestate.realestate.entity.Category;
import com.realestate.realestate.repository.CategoryRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    @Transactional
    public CategoryResponse createCategory(CategoryRequest request) {
        Category category = Category.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();
        
        Category saved = categoryRepository.save(category);
        return CategoryResponse.builder()
                .id(saved.getId())
                .name(saved.getName())
                .description(saved.getDescription())
                .build();
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategories() {
       return categoryRepository.findAll()
               .stream()
               .map(cat -> CategoryResponse.builder()
                       .id(cat.getId())
                       .name(cat.getName())
                       .description(cat.getDescription())
                       .build())
               .toList();
    }
}
