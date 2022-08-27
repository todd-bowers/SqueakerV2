package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.Mentions;
import com.mycompany.myapp.repository.MentionsRepository;
import com.mycompany.myapp.service.MentionsService;
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
 * REST controller for managing {@link com.mycompany.myapp.domain.Mentions}.
 */
@RestController
@RequestMapping("/api")
public class MentionsResource {

    private final Logger log = LoggerFactory.getLogger(MentionsResource.class);

    private static final String ENTITY_NAME = "mentions";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final MentionsService mentionsService;

    private final MentionsRepository mentionsRepository;

    public MentionsResource(MentionsService mentionsService, MentionsRepository mentionsRepository) {
        this.mentionsService = mentionsService;
        this.mentionsRepository = mentionsRepository;
    }

    /**
     * {@code POST  /mentions} : Create a new mentions.
     *
     * @param mentions the mentions to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new mentions, or with status {@code 400 (Bad Request)} if the mentions has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/mentions")
    public ResponseEntity<Mentions> createMentions(@Valid @RequestBody Mentions mentions) throws URISyntaxException {
        log.debug("REST request to save Mentions : {}", mentions);
        if (mentions.getId() != null) {
            throw new BadRequestAlertException("A new mentions cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Mentions result = mentionsService.save(mentions);
        return ResponseEntity
            .created(new URI("/api/mentions/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /mentions/:id} : Updates an existing mentions.
     *
     * @param id the id of the mentions to save.
     * @param mentions the mentions to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated mentions,
     * or with status {@code 400 (Bad Request)} if the mentions is not valid,
     * or with status {@code 500 (Internal Server Error)} if the mentions couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/mentions/{id}")
    public ResponseEntity<Mentions> updateMentions(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Mentions mentions
    ) throws URISyntaxException {
        log.debug("REST request to update Mentions : {}, {}", id, mentions);
        if (mentions.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, mentions.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!mentionsRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Mentions result = mentionsService.update(mentions);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, mentions.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /mentions/:id} : Partial updates given fields of an existing mentions, field will ignore if it is null
     *
     * @param id the id of the mentions to save.
     * @param mentions the mentions to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated mentions,
     * or with status {@code 400 (Bad Request)} if the mentions is not valid,
     * or with status {@code 404 (Not Found)} if the mentions is not found,
     * or with status {@code 500 (Internal Server Error)} if the mentions couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/mentions/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Mentions> partialUpdateMentions(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Mentions mentions
    ) throws URISyntaxException {
        log.debug("REST request to partial update Mentions partially : {}, {}", id, mentions);
        if (mentions.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, mentions.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!mentionsRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Mentions> result = mentionsService.partialUpdate(mentions);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, mentions.getId().toString())
        );
    }

    /**
     * {@code GET  /mentions} : get all the mentions.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of mentions in body.
     */
    @GetMapping("/mentions")
    public ResponseEntity<List<Mentions>> getAllMentions(@org.springdoc.api.annotations.ParameterObject Pageable pageable) {
        log.debug("REST request to get a page of Mentions");
        Page<Mentions> page = mentionsService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /mentions/:id} : get the "id" mentions.
     *
     * @param id the id of the mentions to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the mentions, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/mentions/{id}")
    public ResponseEntity<Mentions> getMentions(@PathVariable Long id) {
        log.debug("REST request to get Mentions : {}", id);
        Optional<Mentions> mentions = mentionsService.findOne(id);
        return ResponseUtil.wrapOrNotFound(mentions);
    }

    /**
     * {@code DELETE  /mentions/:id} : delete the "id" mentions.
     *
     * @param id the id of the mentions to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/mentions/{id}")
    public ResponseEntity<Void> deleteMentions(@PathVariable Long id) {
        log.debug("REST request to delete Mentions : {}", id);
        mentionsService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
