package com.abn.recipeman.domain.model.entity;

import com.abn.recipeman.application.service.RecipeAttributeType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * A Attribute.
 */
@Entity
@Table(name = "attribute", uniqueConstraints = {@UniqueConstraint(name = "unique_att_cons", columnNames = {"recipeAttributeType"})})
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Attribute implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "recipeAttributeType", nullable = false)
    @Enumerated(EnumType.STRING)
    private RecipeAttributeType recipeAttributeType;

    @ManyToMany(mappedBy = "attributes")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = {"recipeIngredients", "attributes"}, allowSetters = true)
    private Set<Recipe> recipes = new HashSet<>();


    public Long getId() {
        return this.id;
    }

    public Attribute id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RecipeAttributeType getRecipeAttributeType() {
        return this.recipeAttributeType;
    }

    public Attribute recipeAttributeType(RecipeAttributeType recipeAttributeType) {
        this.setRecipeAttributeType(recipeAttributeType);
        return this;
    }

    public void setRecipeAttributeType(RecipeAttributeType recipeAttributeType) {
        this.recipeAttributeType = recipeAttributeType;
    }

    public Set<Recipe> getRecipes() {
        return this.recipes;
    }

    public void setRecipes(Set<Recipe> recipes) {
        if (this.recipes != null) {
            this.recipes.forEach(i -> i.removeAttribute(this));
        }
        if (recipes != null) {
            recipes.forEach(i -> i.addAttribute(this));
        }
        this.recipes = recipes;
    }

    public Attribute recipes(Set<Recipe> recipes) {
        this.setRecipes(recipes);
        return this;
    }

    public Attribute addRecipe(Recipe recipe) {
        this.recipes.add(recipe);
        recipe.getAttributes().add(this);
        return this;
    }

    public Attribute removeRecipe(Recipe recipe) {
        this.recipes.remove(recipe);
        recipe.getAttributes().remove(this);
        return this;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Attribute)) {
            return false;
        }
        return id != null && id.equals(((Attribute) o).id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Attribute{" +
                "id=" + getId() +
                ", recipeAttributeType='" + getRecipeAttributeType() + "'" +
                "}";
    }
}
