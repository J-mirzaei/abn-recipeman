package com.abn.recipeman.domain.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * A Recipe.
 */
@Entity
@Table(name = "recipe",uniqueConstraints = {@UniqueConstraint(name = "uni_recep_con",columnNames = {"name"})})
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Recipe implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull
    @Column(name = "serving_number", nullable = false)
    private Integer servingNumber;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    @Column(name = "instruction", nullable = false)
    private String instruction;

    @OneToMany(mappedBy = "recipe",cascade = CascadeType.PERSIST)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "recipe", "ingredients" }, allowSetters = true)
    private Set<RecipeIngredient> recipeIngredients = new HashSet<>();

    @ManyToMany(cascade = CascadeType.PERSIST)
    @JoinTable(
        name = "rel_recipe__attribute",
        joinColumns = @JoinColumn(name = "recipe_id"),
        inverseJoinColumns = @JoinColumn(name = "attribute_id")
    )
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "recipes" }, allowSetters = true)
    private Set<Attribute> attributes = new HashSet<>();

    public Long getId() {
        return this.id;
    }

    public Recipe id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Recipe name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getServingNumber() {
        return this.servingNumber;
    }

    public Recipe servingNumber(Integer servingNumber) {
        this.setServingNumber(servingNumber);
        return this;
    }

    public void setServingNumber(Integer servingNumber) {
        this.servingNumber = servingNumber;
    }

    public String getInstruction() {
        return this.instruction;
    }

    public Recipe instruction(String instruction) {
        this.setInstruction(instruction);
        return this;
    }

    public void setInstruction(String instruction) {
        this.instruction = instruction;
    }

    public Set<RecipeIngredient> getRecipeIngredients() {
        return this.recipeIngredients;
    }

    public void setRecipeIngredients(Set<RecipeIngredient> recipeIngredients) {
        if (this.recipeIngredients != null) {
            this.recipeIngredients.forEach(i -> i.setRecipe(null));
        }
        if (recipeIngredients != null) {
            recipeIngredients.forEach(i -> i.setRecipe(this));
        }
        this.recipeIngredients = recipeIngredients;
    }

    public Recipe recipeIngredients(Set<RecipeIngredient> recipeIngredients) {
        this.setRecipeIngredients(recipeIngredients);
        return this;
    }

    public Recipe addRecipeIngredient(RecipeIngredient recipeIngredient) {
        this.recipeIngredients.add(recipeIngredient);
        recipeIngredient.setRecipe(this);
        return this;
    }

    public Recipe removeRecipeIngredient(RecipeIngredient recipeIngredient) {
        this.recipeIngredients.remove(recipeIngredient);
        recipeIngredient.setRecipe(null);
        return this;
    }

    public Set<Attribute> getAttributes() {
        return this.attributes;
    }

    public void setAttributes(Set<Attribute> attributes) {
        this.attributes = attributes;
    }

    public Recipe attributes(Set<Attribute> attributes) {
        this.setAttributes(attributes);
        return this;
    }

    public Recipe addAttribute(Attribute attribute) {
        this.attributes.add(attribute);
        attribute.getRecipes().add(this);
        return this;
    }

    public Recipe removeAttribute(Attribute attribute) {
        this.attributes.remove(attribute);
        attribute.getRecipes().remove(this);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Recipe)) {
            return false;
        }
        return id != null && id.equals(((Recipe) o).id);
    }

    @Override
    public int hashCode() {

        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Recipe{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", servingNumber=" + getServingNumber() +
            ", instruction='" + getInstruction() + "'" +
            "}";
    }
}
