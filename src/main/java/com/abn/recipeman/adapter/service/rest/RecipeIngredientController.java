package com.abn.recipeman.adapter.service.rest;

import com.abn.recipeman.adapter.model.errors.BadRequestAlertException;
import com.abn.recipeman.application.model.dto.RecipeIngredientDTO;
import com.abn.recipeman.application.service.RecipeIngredientService;
import com.abn.recipeman.persistence.repository.RecipeIngredientRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
 * REST controller for managing {@link com.abn.recipeman.domain.model.entity.RecipeIngredient}.
 */
@RestController
@RequestMapping("/api")
public class RecipeIngredientController implements BaseController {

    private final Logger log = LoggerFactory.getLogger(RecipeIngredientController.class);

    private static final String ENTITY_NAME = "recipeIngredient";


    private final RecipeIngredientService recipeIngredientService;

    private final RecipeIngredientRepository recipeIngredientRepository;

    public RecipeIngredientController(
            RecipeIngredientService recipeIngredientService,
            RecipeIngredientRepository recipeIngredientRepository
    ) {
        this.recipeIngredientService = recipeIngredientService;
        this.recipeIngredientRepository = recipeIngredientRepository;
    }

    /**
     * {@code POST  /recipe-ingredients} : Create a new recipeIngredient.
     *
     * @param recipeIngredientDTO the recipeIngredientDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new recipeIngredientDTO, or with status {@code 400 (Bad Request)} if the recipeIngredient has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/recipe-ingredients")
    public ResponseEntity<RecipeIngredientDTO> createRecipeIngredient(@Valid @RequestBody RecipeIngredientDTO recipeIngredientDTO)
            throws URISyntaxException {
        log.debug("REST request to save RecipeIngredient : {}", recipeIngredientDTO);
        if (recipeIngredientDTO.getId() != null) {
            throw new BadRequestAlertException("A new recipeIngredient cannot already have an ID", ENTITY_NAME, "idexists");
        }
        RecipeIngredientDTO result = recipeIngredientService.save(recipeIngredientDTO);
        return ResponseEntity
                .created(new URI("/api/recipe-ingredients/" + result.getId()))

                .body(result);
    }

    /**
     * {@code PUT  /recipe-ingredients/:id} : Updates an existing recipeIngredient.
     *
     * @param id                  the id of the recipeIngredientDTO to save.
     * @param recipeIngredientDTO the recipeIngredientDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated recipeIngredientDTO,
     * or with status {@code 400 (Bad Request)} if the recipeIngredientDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the recipeIngredientDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/recipe-ingredients/{id}")
    public ResponseEntity<RecipeIngredientDTO> updateRecipeIngredient(
            @PathVariable(value = "id", required = false) final Long id,
            @Valid @RequestBody RecipeIngredientDTO recipeIngredientDTO
    ) throws URISyntaxException {
        log.debug("REST request to update RecipeIngredient : {}, {}", id, recipeIngredientDTO);
        if (recipeIngredientDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, recipeIngredientDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!recipeIngredientRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        RecipeIngredientDTO result = recipeIngredientService.update(recipeIngredientDTO);
        return ResponseEntity
                .ok().body(result);
    }

    /**
     * {@code PATCH  /recipe-ingredients/:id} : Partial updates given fields of an existing recipeIngredient, field will ignore if it is null
     *
     * @param id                  the id of the recipeIngredientDTO to save.
     * @param recipeIngredientDTO the recipeIngredientDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated recipeIngredientDTO,
     * or with status {@code 400 (Bad Request)} if the recipeIngredientDTO is not valid,
     * or with status {@code 404 (Not Found)} if the recipeIngredientDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the recipeIngredientDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/recipe-ingredients/{id}", consumes = {"application/json", "application/merge-patch+json"})
    public ResponseEntity<RecipeIngredientDTO> partialUpdateRecipeIngredient(
            @PathVariable(value = "id", required = false) final Long id,
            @NotNull @RequestBody RecipeIngredientDTO recipeIngredientDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update RecipeIngredient partially : {}, {}", id, recipeIngredientDTO);
        if (recipeIngredientDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, recipeIngredientDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!recipeIngredientRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<RecipeIngredientDTO> result = recipeIngredientService.partialUpdate(recipeIngredientDTO);

        return wrapOrNotFound(
                result);
    }

    /**
     * {@code GET  /recipe-ingredients} : get all the recipeIngredients.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of recipeIngredients in body.
     */
    @GetMapping("/recipe-ingredients")
    public List<RecipeIngredientDTO> getAllRecipeIngredients() {
        log.debug("REST request to get all RecipeIngredients");
        return recipeIngredientService.findAll();
    }

    /**
     * {@code GET  /recipe-ingredients/:id} : get the "id" recipeIngredient.
     *
     * @param id the id of the recipeIngredientDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the recipeIngredientDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/recipe-ingredients/{id}")
    public ResponseEntity<RecipeIngredientDTO> getRecipeIngredient(@PathVariable Long id) {
        log.debug("REST request to get RecipeIngredient : {}", id);
        Optional<RecipeIngredientDTO> recipeIngredientDTO = recipeIngredientService.findOne(id);
        return wrapOrNotFound(recipeIngredientDTO);
    }

    /**
     * {@code DELETE  /recipe-ingredients/:id} : delete the "id" recipeIngredient.
     *
     * @param id the id of the recipeIngredientDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/recipe-ingredients/{id}")
    public ResponseEntity<Void> deleteRecipeIngredient(@PathVariable Long id) {
        log.debug("REST request to delete RecipeIngredient : {}", id);
        recipeIngredientService.delete(id);
        return ResponseEntity
                .noContent().build();
    }
}
