package com.abn.recipeman.persistence.repository;

import com.abn.recipeman.domain.model.entity.RecipeIngredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

/**
 * Spring Data SQL repository for the RecipeIngredient entity.
 */
@SuppressWarnings("unused")
@Repository
public interface RecipeIngredientRepository extends JpaRepository<RecipeIngredient, Long> {

    @Query("select ring from RecipeIngredient ring inner join ring.ingredient ing where ing.name in ( :ingredientNames ) ")
    List<RecipeIngredient> findAllByIngredientNames(@Param("ingredientNames") Set<String> includeIngredients);

    @Query("select ring from RecipeIngredient ring where ring.recipe.id = :recipeId ")
    List<RecipeIngredient> findByRecipeId(@Param("recipeId") Long id);
}
