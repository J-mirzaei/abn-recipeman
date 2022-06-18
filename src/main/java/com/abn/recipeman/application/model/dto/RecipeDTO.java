package com.abn.recipeman.application.model.dto;

import javax.persistence.Lob;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A DTO for the {@link com.abn.recipeman.domain.model.entity.Recipe} entity.
 */
public class RecipeDTO implements Serializable {

    private Long id;

    @NotNull
    private String name;

    @NotNull
    private Integer servingNumber;

    @Lob
    private String instruction;

    private Set<AttributeDTO> attributes = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getServingNumber() {
        return servingNumber;
    }

    public void setServingNumber(Integer servingNumber) {
        this.servingNumber = servingNumber;
    }

    public String getInstruction() {
        return instruction;
    }

    public void setInstruction(String instruction) {
        this.instruction = instruction;
    }

    public Set<AttributeDTO> getAttributes() {
        return attributes;
    }

    public void setAttributes(Set<AttributeDTO> attributes) {
        this.attributes = attributes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RecipeDTO)) {
            return false;
        }

        RecipeDTO recipeDTO = (RecipeDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, recipeDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "RecipeDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", servingNumber=" + getServingNumber() +
            ", instruction='" + getInstruction() + "'" +
            ", attributes=" + getAttributes() +
            "}";
    }
}
