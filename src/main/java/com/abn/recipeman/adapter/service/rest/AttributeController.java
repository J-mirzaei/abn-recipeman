package com.abn.recipeman.adapter.service.rest;

import com.abn.recipeman.adapter.model.errors.BadRequestAlertException;
import com.abn.recipeman.application.model.dto.AttributeDTO;
import com.abn.recipeman.application.service.AttributeService;
import com.abn.recipeman.persistence.repository.AttributeRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
 * REST controller for managing {@link com.abn.recipeman.domain.model.entity.Attribute}.
 */
@RestController
@RequestMapping("/api")
public class AttributeController implements BaseController {

    private final Logger log = LoggerFactory.getLogger(AttributeController.class);

    private static final String ENTITY_NAME = "attribute";

    private final AttributeService attributeService;

    private final AttributeRepository attributeRepository;

    public AttributeController(AttributeService attributeService, AttributeRepository attributeRepository) {
        this.attributeService = attributeService;
        this.attributeRepository = attributeRepository;
    }

    /**
     * {@code POST  /attributes} : Create a new attribute.
     *
     * @param attributeDTO the attributeDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new attributeDTO, or with status {@code 400 (Bad Request)} if the attribute has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/attributes")
    public ResponseEntity<AttributeDTO> createAttribute(@Valid @RequestBody AttributeDTO attributeDTO) throws URISyntaxException {
        log.debug("REST request to save Attribute : {}", attributeDTO);
        if (attributeDTO.getId() != null) {
            throw new BadRequestAlertException("A new attribute cannot already have an ID", ENTITY_NAME, "idexists");
        }
        AttributeDTO result = attributeService.save(attributeDTO);
        return ResponseEntity
                .created(new URI("/api/attributes/" + result.getId()))

                .body(result);
    }

    /**
     * {@code PUT  /attributes/:id} : Updates an existing attribute.
     *
     * @param id           the id of the attributeDTO to save.
     * @param attributeDTO the attributeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated attributeDTO,
     * or with status {@code 400 (Bad Request)} if the attributeDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the attributeDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/attributes/{id}")
    public ResponseEntity<AttributeDTO> updateAttribute(
            @PathVariable(value = "id", required = false) final Long id,
            @Valid @RequestBody AttributeDTO attributeDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Attribute : {}, {}", id, attributeDTO);
        if (attributeDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, attributeDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!attributeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        AttributeDTO result = attributeService.update(attributeDTO);
        return ResponseEntity
                .ok()
                .body(result);
    }

    /**
     * {@code PATCH  /attributes/:id} : Partial updates given fields of an existing attribute, field will ignore if it is null
     *
     * @param id           the id of the attributeDTO to save.
     * @param attributeDTO the attributeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated attributeDTO,
     * or with status {@code 400 (Bad Request)} if the attributeDTO is not valid,
     * or with status {@code 404 (Not Found)} if the attributeDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the attributeDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @Operation(description = "Partial updates given fields of an existing attribute, field will ignore if it is null", responses = {
            @ApiResponse(description = "The updated attribute",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AttributeDTO.class))),
            @ApiResponse(responseCode = "400", description = "if the attributeDTO is not valid"),
            @ApiResponse(responseCode = "404", description = "if the attributeDTO is not found"),
            @ApiResponse(responseCode = "500", description = "if the attributeDTO couldn't be updated")
    })
    @PatchMapping(value = "/attributes/{id}", consumes = {"application/json", "application/merge-patch+json"})
    public ResponseEntity<AttributeDTO> partialUpdateAttribute(
            @PathVariable(value = "id", required = false) final Long id,
            @NotNull @RequestBody AttributeDTO attributeDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Attribute partially : {}, {}", id, attributeDTO);
        if (attributeDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, attributeDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!attributeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<AttributeDTO> result = attributeService.partialUpdate(attributeDTO);

        return wrapOrNotFound(
                result
        );
    }

    /**
     * {@code GET  /attributes} : get all the attributes.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of attributes in body.
     */
    @GetMapping("/attributes")
    public List<AttributeDTO> getAllAttributes() {
        log.debug("REST request to get all Attributes");
        return attributeService.findAll();
    }

    /**
     * {@code GET  /attributes/:id} : get the "id" attribute.
     *
     * @param id the id of the attributeDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the attributeDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/attributes/{id}")
    public ResponseEntity<AttributeDTO> getAttribute(@PathVariable Long id) {
        log.debug("REST request to get Attribute : {}", id);
        Optional<AttributeDTO> attributeDTO = attributeService.findOne(id);
        return wrapOrNotFound(attributeDTO);
    }

    /**
     * {@code DELETE  /attributes/:id} : delete the "id" attribute.
     *
     * @param id the id of the attributeDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/attributes/{id}")
    public ResponseEntity<Void> deleteAttribute(@PathVariable Long id) {
        log.debug("REST request to delete Attribute : {}", id);
        attributeService.delete(id);
        return ResponseEntity
                .noContent()
                .build();
    }
}
