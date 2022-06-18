package com.abn.recipeman.application.model.dto;

import com.abn.recipeman.application.service.RecipeAttributeType;
import com.abn.recipeman.domain.model.entity.Attribute;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link Attribute} entity.
 */
public class AttributeDTO implements Serializable {

    private Long id;

    @NotNull
    private RecipeAttributeType recipeAttributeType;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RecipeAttributeType getRecipeAttributeType() {
        return recipeAttributeType;
    }

    public void setRecipeAttributeType(RecipeAttributeType recipeAttributeType) {
        this.recipeAttributeType = recipeAttributeType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AttributeDTO)) {
            return false;
        }

        AttributeDTO attributeDTO = (AttributeDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, attributeDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AttributeDTO{" +
            "id=" + getId() +
            ", recipeAttributeType='" + getRecipeAttributeType() + "'" +
            "}";
    }
}
