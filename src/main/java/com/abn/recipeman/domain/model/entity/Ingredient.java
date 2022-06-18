package com.abn.recipeman.domain.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Ingredient.
 */
@Entity
@Table(name = "ingredient", uniqueConstraints = {@UniqueConstraint(name = "uniq_ingrad_cons", columnNames = "name")})
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Ingredient implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @OneToMany(mappedBy = "ingredient", cascade = CascadeType.PERSIST)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = {"ingredient", "recipe"}, allowSetters = true)
    private Set<RecipeIngredient> recipeIngredients = new HashSet<>();

    public Long getId() {
        return this.id;
    }

    public Ingredient id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Ingredient name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<RecipeIngredient> getRecipeIngredients() {
        return this.recipeIngredients;
    }

    public void setRecipeIngredients(Set<RecipeIngredient> recipeIngredients) {
        if (this.recipeIngredients != null) {
            this.recipeIngredients.forEach(i -> i.setIngredient(null));
        }
        if (recipeIngredients != null) {
            recipeIngredients.forEach(i -> i.setIngredient(this));
        }
        this.recipeIngredients = recipeIngredients;
    }

    public Ingredient recipeIngredients(Set<RecipeIngredient> recipeIngredients) {
        this.setRecipeIngredients(recipeIngredients);
        return this;
    }

    public Ingredient addRecipeIngredient(RecipeIngredient recipeIngredient) {
        this.recipeIngredients.add(recipeIngredient);
        recipeIngredient.setIngredient(this);
        return this;
    }

    public Ingredient removeRecipeIngredient(RecipeIngredient recipeIngredient) {
        this.recipeIngredients.remove(recipeIngredient);
        recipeIngredient.setIngredient(null);
        return this;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Ingredient)) {
            return false;
        }
        return id != null && id.equals(((Ingredient) o).id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Ingredient{" +
                "id=" + getId() +
                ", name='" + getName() + "'" +
                "}";
    }
}
