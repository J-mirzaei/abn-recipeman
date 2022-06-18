package com.abn.recipeman.persistence.repository;

import com.abn.recipeman.domain.model.entity.Attribute;
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
public class AttributeRepositoryWithBagRelationshipsImpl implements AttributeRepositoryWithBagRelationships {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<Attribute> fetchBagRelationships(Optional<Attribute> recipe) {
        return recipe.map(this::fetchRecipes);
    }

    @Override
    public Page<Attribute> fetchBagRelationships(Page<Attribute> recipes) {
        return new PageImpl<>(fetchBagRelationships(recipes.getContent()), recipes.getPageable(), recipes.getTotalElements());
    }

    @Override
    public List<Attribute> fetchBagRelationships(List<Attribute> attributes) {
        return Optional.of(attributes).map(this::fetchRecipes).orElse(Collections.emptyList());
    }

    Attribute fetchRecipes(Attribute attribute) {
        return entityManager
                .createQuery("select attribute from Attribute attribute left join fetch attribute.recipes where attribute is :attribute", Attribute.class)
                .setParameter("attribute", attribute)
                .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
                .getSingleResult();
    }

    List<Attribute> fetchRecipes(List<Attribute> attributes) {
        return entityManager
                .createQuery(
                        "select distinct attribute from Attribute attribute left join fetch attribute.recipes where attribute in :attributes",
                        Attribute.class
                )
                .setParameter("attributes", attributes)
                .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
                .getResultList();
    }
}
