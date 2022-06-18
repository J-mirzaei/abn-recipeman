package com.abn.recipeman.application.model.dto;

import com.abn.recipeman.domain.model.entity.RecipeIngredient;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link RecipeIngredient} entity.
 */
public class RecipeIngredientDTO implements Serializable {

    private Long id;

    @NotNull
    private Integer amount;

    private IngredientDTO ingredient;

    private RecipeDTO recipe;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public IngredientDTO getIngredient() {
        return ingredient;
    }

    public void setIngredient(IngredientDTO ingredient) {
        this.ingredient = ingredient;
    }

    public RecipeDTO getRecipe() {
        return recipe;
    }

    public void setRecipe(RecipeDTO recipe) {
        this.recipe = recipe;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RecipeIngredientDTO)) {
            return false;
        }

        RecipeIngredientDTO recipeIngredientDTO = (RecipeIngredientDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, recipeIngredientDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "RecipeIngredientDTO{" +
                "id=" + getId() +
                ", amount=" + getAmount() +
                ", ingredient=" + getIngredient() +
                ", recipe=" + getRecipe() +
                "}";
    }
}
