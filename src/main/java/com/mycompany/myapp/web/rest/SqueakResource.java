package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.Squeak;
import com.mycompany.myapp.repository.SqueakRepository;
import com.mycompany.myapp.service.SqueakService;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.mycompany.myapp.domain.Squeak}.
 */
@RestController
@RequestMapping("/api")
public class SqueakResource {

    private final Logger log = LoggerFactory.getLogger(SqueakResource.class);

    private static final String ENTITY_NAME = "squeak";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SqueakService squeakService;

    private final SqueakRepository squeakRepository;

    public SqueakResource(SqueakService squeakService, SqueakRepository squeakRepository) {
        this.squeakService = squeakService;
        this.squeakRepository = squeakRepository;
    }

    /**
     * {@code POST  /squeaks} : Create a new squeak.
     *
     * @param squeak the squeak to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new squeak, or with status {@code 400 (Bad Request)} if the squeak has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/squeaks")
    public ResponseEntity<Squeak> createSqueak(@Valid @RequestBody Squeak squeak) throws URISyntaxException {
        log.debug("REST request to save Squeak : {}", squeak);
        if (squeak.getId() != null) {
            throw new BadRequestAlertException("A new squeak cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Squeak result = squeakService.save(squeak);
        return ResponseEntity
            .created(new URI("/api/squeaks/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /squeaks/:id} : Updates an existing squeak.
     *
     * @param id the id of the squeak to save.
     * @param squeak the squeak to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated squeak,
     * or with status {@code 400 (Bad Request)} if the squeak is not valid,
     * or with status {@code 500 (Internal Server Error)} if the squeak couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/squeaks/{id}")
    public ResponseEntity<Squeak> updateSqueak(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Squeak squeak
    ) throws URISyntaxException {
        log.debug("REST request to update Squeak : {}, {}", id, squeak);
        if (squeak.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, squeak.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!squeakRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Squeak result = squeakService.update(squeak);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, squeak.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /squeaks/:id} : Partial updates given fields of an existing squeak, field will ignore if it is null
     *
     * @param id the id of the squeak to save.
     * @param squeak the squeak to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated squeak,
     * or with status {@code 400 (Bad Request)} if the squeak is not valid,
     * or with status {@code 404 (Not Found)} if the squeak is not found,
     * or with status {@code 500 (Internal Server Error)} if the squeak couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/squeaks/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Squeak> partialUpdateSqueak(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Squeak squeak
    ) throws URISyntaxException {
        log.debug("REST request to partial update Squeak partially : {}, {}", id, squeak);
        if (squeak.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, squeak.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!squeakRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Squeak> result = squeakService.partialUpdate(squeak);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, squeak.getId().toString())
        );
    }

    /**
     * {@code GET  /squeaks} : get all the squeaks.
     *
     * @param pageable the pagination information.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of squeaks in body.
     */
    @GetMapping("/squeaks")
    public ResponseEntity<List<Squeak>> getAllSqueaks(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        @RequestParam(required = false, defaultValue = "false") boolean eagerload
    ) {
        log.debug("REST request to get a page of Squeaks");
        Page<Squeak> page;
        if (eagerload) {
            page = squeakService.findAllWithEagerRelationships(pageable);
        } else {
            page = squeakService.findAll(pageable);
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /squeaks/:id} : get the "id" squeak.
     *
     * @param id the id of the squeak to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the squeak, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/squeaks/{id}")
    public ResponseEntity<Squeak> getSqueak(@PathVariable Long id) {
        log.debug("REST request to get Squeak : {}", id);
        Optional<Squeak> squeak = squeakService.findOne(id);
        return ResponseUtil.wrapOrNotFound(squeak);
    }

    /**
     * {@code DELETE  /squeaks/:id} : delete the "id" squeak.
     *
     * @param id the id of the squeak to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/squeaks/{id}")
    public ResponseEntity<Void> deleteSqueak(@PathVariable Long id) {
        log.debug("REST request to delete Squeak : {}", id);
        squeakService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
