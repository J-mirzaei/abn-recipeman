package com.abn.recipeman.persistence.repository;

import com.abn.recipeman.domain.model.entity.Recipe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Spring Data SQL repository for the Recipe entity.
 */
@Repository
public interface RecipeRepository extends RecipeRepositoryWithBagRelationships, JpaRepository<Recipe, Long> {
    default Optional<Recipe> findOneWithEagerRelationships(Long id) {
        return this.fetchBagRelationships(this.findById(id));
    }

    default List<Recipe> findAllWithEagerRelationships() {
        return this.fetchBagRelationships(this.findAll());
    }

    default Page<Recipe> findAllWithEagerRelationships(Pageable pageable) {
        return this.fetchBagRelationships(this.findAll(pageable));
    }

/*    @Query("select rec from Recipe rec where " +
            " ( :attributes is null or (select COUNT(att) from rec.attributes as att where att.name in ( :attributes )  )  > 0  ) " +
            " and  ( :excludeIngredients is null or (select count(recIngExcl) from rec.recipeIngredients as recIngExcl inner join recIngExcl.ingredient as ingEx where ingEx.name in ( :excludeIngredients ) ) = 0 )  " +
            " and  ( :includeIngredients is null or (select count(recIngIncl) from rec.recipeIngredients as recIngIncl inner join recIngIncl.ingredient as ingInc where ingInc.name in ( :includeIngredients ) ) > 0 )  " +
            " and ( :servingNumber is null or rec.servingNumber = :servingNumber ) " +
            " and ( :instruction is null or rec.instruction like %:instruction% ) "
    )
    Page<Recipe> inquiryAll(@Param("attributes") Set<String> attributes,
                            @Param("servingNumber") Integer servingNumber,
                            @Param("instruction") String instruction,
                            @Param("includeIngredients") Set<String> includeIngredients,
                            @Param("excludeIngredients") Set<String> excludeIngredients,
                            Pageable pageable);*/

    @Query("select rec from Recipe rec where " +
            " ( :includeRecIds IS NOT NULL OR :servingNumber IS NOT NULL OR  :inInstruction IS NOT NULL ) " +
            " and ( :includeRecIds is NULL OR rec.id IN :includeRecIds ) " +
            " and (:servingNumber is NULL OR rec.servingNumber = :servingNumber) " +
            " and (:inInstruction is NULL OR rec.instruction like concat('%', :inInstruction,'%') ) "
    )
    Page<Recipe> inquiryAllByIds(@Param("includeRecIds") Set<Long> includeRecIds,
                                 @Param("servingNumber") Integer servingNumber,
                                 @Param("inInstruction") String instruction,
                                 Pageable pageable);


    default Page<Recipe> inquiryAllByIdsWithEagerRelationships(@Param("includeRecIds") Set<Long> includeRecIds,
                                                               @Param("servingNumber") Integer servingNumber,
                                                               @Param("instruction") String instruction,
                                                               Pageable pageable) {
        return this.fetchBagRelationships(this.inquiryAllByIds(includeRecIds, servingNumber, instruction, pageable));
    }

}
