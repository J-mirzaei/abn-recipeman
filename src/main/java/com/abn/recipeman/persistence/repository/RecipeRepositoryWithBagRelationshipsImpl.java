package com.abn.recipeman.persistence.repository;

import com.abn.recipeman.domain.model.entity.Recipe;
import org.hibernate.annotations.QueryHints;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Utility repository to load bag relationships based on https://vladmihalcea.com/hibernate-multiplebagfetchexception/
 */
public class RecipeRepositoryWithBagRelationshipsImpl implements RecipeRepositoryWithBagRelationships {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<Recipe> fetchBagRelationships(Optional<Recipe> recipe) {
        return recipe.map(this::fetchAttributes);
    }

    @Override
    public Page<Recipe> fetchBagRelationships(Page<Recipe> recipes) {
        return new PageImpl<>(fetchBagRelationships(recipes.getContent()), recipes.getPageable(), recipes.getTotalElements());
    }

    @Override
    public List<Recipe> fetchBagRelationships(List<Recipe> recipes) {
        return Optional.of(recipes).map(this::fetchAttributes).orElse(Collections.emptyList());
    }

    Recipe fetchAttributes(Recipe result) {
        return entityManager
                .createQuery("select recipe from Recipe recipe left join fetch recipe.attributes where recipe is :recipe", Recipe.class)
                .setParameter("recipe", result)
                .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
                .getSingleResult();
    }

    List<Recipe> fetchAttributes(List<Recipe> recipes) {
        return entityManager
                .createQuery(
                        "select distinct recipe from Recipe recipe left join fetch recipe.attributes where recipe in :recipes",
                        Recipe.class
                )
                .setParameter("recipes", recipes)
                .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
                .getResultList();
    }
}
