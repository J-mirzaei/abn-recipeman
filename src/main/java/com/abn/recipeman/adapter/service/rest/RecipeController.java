package com.abn.recipeman.adapter.service.rest;

import com.abn.recipeman.adapter.model.errors.BadRequestAlertException;
import com.abn.recipeman.application.model.dto.FilterRecipeDto;
import com.abn.recipeman.application.model.dto.RecipeDTO;
import com.abn.recipeman.application.service.RecipeService;
import com.abn.recipeman.persistence.repository.RecipeRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * REST controller for managing {@link com.abn.recipeman.domain.model.entity.Recipe}.
 */
@RestController
@RequestMapping("/api")
public class RecipeController implements BaseController {

    private final Logger log = LoggerFactory.getLogger(RecipeController.class);

    private static final String ENTITY_NAME = "recipe";


    private final RecipeService recipeService;

    private final RecipeRepository recipeRepository;

    public RecipeController(RecipeService recipeService, RecipeRepository recipeRepository) {
        this.recipeService = recipeService;
        this.recipeRepository = recipeRepository;
    }

    /**
     * {@code POST  /recipes} : Create a new recipe.
     *
     * @param recipeDTO the recipeDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new recipeDTO, or with status {@code 400 (Bad Request)} if the recipe has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/recipes")
    public ResponseEntity<RecipeDTO> createRecipe(@Valid @RequestBody RecipeDTO recipeDTO) throws URISyntaxException {
        log.debug("REST request to save Recipe : {}", recipeDTO);
        if (recipeDTO.getId() != null) {
            throw new BadRequestAlertException("A new recipe cannot already have an ID", ENTITY_NAME, "idexists");
        }
        RecipeDTO result = recipeService.save(recipeDTO);
        return ResponseEntity
                .created(new URI("/api/recipes/" + result.getId()))

                .body(result);
    }

    /**
     * {@code PUT  /recipes/:id} : Updates an existing recipe.
     *
     * @param id        the id of the recipeDTO to save.
     * @param recipeDTO the recipeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated recipeDTO,
     * or with status {@code 400 (Bad Request)} if the recipeDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the recipeDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/recipes/{id}")
    public ResponseEntity<RecipeDTO> updateRecipe(
            @PathVariable(value = "id", required = false) final Long id,
            @Valid @RequestBody RecipeDTO recipeDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Recipe : {}, {}", id, recipeDTO);
        if (recipeDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, recipeDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!recipeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        RecipeDTO result = recipeService.update(recipeDTO);
        return ResponseEntity
                .ok()
                .body(result);
    }

    /**
     * {@code PATCH  /recipes/:id} : Partial updates given fields of an existing recipe, field will ignore if it is null
     *
     * @param id        the id of the recipeDTO to save.
     * @param recipeDTO the recipeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated recipeDTO,
     * or with status {@code 400 (Bad Request)} if the recipeDTO is not valid,
     * or with status {@code 404 (Not Found)} if the recipeDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the recipeDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/recipes/{id}", consumes = {"application/json", "application/merge-patch+json"})
    public ResponseEntity<RecipeDTO> partialUpdateRecipe(
            @PathVariable(value = "id", required = false) final Long id,
            @NotNull @RequestBody RecipeDTO recipeDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Recipe partially : {}, {}", id, recipeDTO);
        if (recipeDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, recipeDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!recipeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<RecipeDTO> result = recipeService.partialUpdate(recipeDTO);

        return wrapOrNotFound(
                result);
    }

    /**
     * {@code GET  /recipes} : get all the recipes.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of recipes in body.
     */
    @GetMapping("/recipes")
    public List<RecipeDTO> getAllRecipes(@RequestParam(required = false, defaultValue = "false") boolean eagerload) {
        log.debug("REST request to get all Recipes");
        return recipeService.findAll();
    }

    /**
     * {@code GET  /recipes} : get all the recipes.
     *
     * @param filterRecipeDto flag to eager load entities from relationships (This is applicable for many-to-many).
     * @param page
     * @param size
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of recipes in body.
     */
    @Operation(description = "Partial updates given fields of an existing attribute, field will ignore if it is null", responses = {
            @ApiResponse(description = "The updated attribute",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = FilterRecipeDto.class))),
            @ApiResponse(responseCode = "400", description = "if the filterRecipeDto is not valid"),
            @ApiResponse(responseCode = "404", description = "if the RecipeDTO is not found"),
            @ApiResponse(responseCode = "500", description = "if couldn't search for Recipes")
    })
    @PostMapping("/recipes/inquiry")
    public List<RecipeDTO> inquiryRecipes(
            @RequestBody FilterRecipeDto filterRecipeDto
            , @RequestParam(name = "page", required = false, defaultValue = "0") int page
            , @RequestParam(name = "size", required = false, defaultValue = "10000000") int size) {
        log.debug("REST request to inquiry Recipes");
        return recipeService.inquiryRecipes(filterRecipeDto, PageRequest.of(page, size));
    }

    /**
     * {@code GET  /recipes/:id} : get the "id" recipe.
     *
     * @param id the id of the recipeDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the recipeDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/recipes/{id}")
    public ResponseEntity<RecipeDTO> getRecipe(@PathVariable Long id) {
        log.debug("REST request to get Recipe : {}", id);
        Optional<RecipeDTO> recipeDTO = recipeService.findOne(id);
        return wrapOrNotFound(recipeDTO);
    }

    /**
     * {@code DELETE  /recipes/:id} : delete the "id" recipe.
     *
     * @param id the id of the recipeDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/recipes/{id}")
    public ResponseEntity<Void> deleteRecipe(@PathVariable Long id) {
        log.debug("REST request to delete Recipe : {}", id);
        recipeService.delete(id);
        return ResponseEntity
                .noContent()
                .build();
    }
}
