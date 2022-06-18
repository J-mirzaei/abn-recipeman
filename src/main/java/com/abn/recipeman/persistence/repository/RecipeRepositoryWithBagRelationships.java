package com.abn.recipeman.persistence.repository;

import com.abn.recipeman.domain.model.entity.Recipe;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface RecipeRepositoryWithBagRelationships {
    Optional<Recipe> fetchBagRelationships(Optional<Recipe> recipe);

    List<Recipe> fetchBagRelationships(List<Recipe> recipes);

    Page<Recipe> fetchBagRelationships(Page<Recipe> recipes);
}
