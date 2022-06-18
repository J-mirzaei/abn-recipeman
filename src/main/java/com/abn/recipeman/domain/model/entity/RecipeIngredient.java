package com.abn.recipeman.domain.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * A RecipeIngredient.
 */
@Entity
@Table(name = "recipe_ingredient")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class RecipeIngredient implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "amount", nullable = false)
    private Integer amount;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JsonIgnoreProperties(value = { "recipeIngredients" }, allowSetters = true)
    private Ingredient ingredient;

    @ManyToOne
    @JsonIgnoreProperties(value = { "recipeIngredients", "attributes" }, allowSetters = true)
    private Recipe recipe;

    public Long getId() {
        return this.id;
    }

    public RecipeIngredient id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getAmount() {
        return this.amount;
    }

    public RecipeIngredient amount(Integer amount) {
        this.setAmount(amount);
        return this;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public Ingredient getIngredient() {
        return this.ingredient;
    }

    public void setIngredient(Ingredient ingredient) {
        this.ingredient = ingredient;
    }

    public RecipeIngredient ingredient(Ingredient ingredient) {
        this.setIngredient(ingredient);
        return this;
    }

    public Recipe getRecipe() {
        return this.recipe;
    }

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }

    public RecipeIngredient recipe(Recipe recipe) {
        this.setRecipe(recipe);
        return this;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RecipeIngredient)) {
            return false;
        }
        return id != null && id.equals(((RecipeIngredient) o).id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "RecipeIngredient{" +
                "id=" + getId() +
                ", amount=" + getAmount() +
                "}";
    }
}
