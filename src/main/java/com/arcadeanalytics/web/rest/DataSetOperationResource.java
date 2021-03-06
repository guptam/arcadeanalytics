package com.arcadeanalytics.web.rest;

import com.arcadeanalytics.domain.DataSetOperation;
import com.arcadeanalytics.repository.DataSetOperationRepository;
import com.arcadeanalytics.repository.search.DataSetOperationSearchRepository;
import com.arcadeanalytics.service.dto.DataSetOperationDTO;
import com.arcadeanalytics.service.mapper.DataSetOperationMapper;
import com.arcadeanalytics.web.rest.errors.BadRequestAlertException;
import com.arcadeanalytics.web.rest.util.HeaderUtil;
import com.arcadeanalytics.web.rest.util.PaginationUtil;
import com.codahale.metrics.annotation.Timed;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

/**
 * REST controller for managing DataSetOperation.
 */
@RestController
@RequestMapping("/api")
public class DataSetOperationResource {

    private static final String ENTITY_NAME = "dataSetOperation";
    private final Logger log = LoggerFactory.getLogger(DataSetOperationResource.class);
    private final DataSetOperationRepository dataSetOperationRepository;

    private final DataSetOperationMapper dataSetOperationMapper;

    private final DataSetOperationSearchRepository dataSetOperationSearchRepository;

    public DataSetOperationResource(DataSetOperationRepository dataSetOperationRepository, DataSetOperationMapper dataSetOperationMapper, DataSetOperationSearchRepository dataSetOperationSearchRepository) {
        this.dataSetOperationRepository = dataSetOperationRepository;
        this.dataSetOperationMapper = dataSetOperationMapper;
        this.dataSetOperationSearchRepository = dataSetOperationSearchRepository;
    }

    /**
     * POST  /data-set-operations : Create a new dataSetOperation.
     *
     * @param dataSetOperationDTO the dataSetOperationDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new dataSetOperationDTO, or with status 400 (Bad Request) if the dataSetOperation has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/data-set-operations")
    @Timed
    public ResponseEntity<DataSetOperationDTO> createDataSetOperation(@Valid @RequestBody DataSetOperationDTO dataSetOperationDTO) throws URISyntaxException {
        log.debug("REST request to save DataSetOperation : {}", dataSetOperationDTO);
        if (dataSetOperationDTO.getId() != null) {
            throw new BadRequestAlertException("A new dataSetOperation cannot already have an ID", ENTITY_NAME, "idexists");
        }
        DataSetOperation dataSetOperation = dataSetOperationMapper.toEntity(dataSetOperationDTO);
        dataSetOperation = dataSetOperationRepository.save(dataSetOperation);
        DataSetOperationDTO result = dataSetOperationMapper.toDto(dataSetOperation);
        dataSetOperationSearchRepository.save(dataSetOperation);
        return ResponseEntity.created(new URI("/api/data-set-operations/" + result.getId()))
                .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
                .body(result);
    }

    /**
     * PUT  /data-set-operations : Updates an existing dataSetOperation.
     *
     * @param dataSetOperationDTO the dataSetOperationDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated dataSetOperationDTO,
     * or with status 400 (Bad Request) if the dataSetOperationDTO is not valid,
     * or with status 500 (Internal Server Error) if the dataSetOperationDTO couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/data-set-operations")
    @Timed
    public ResponseEntity<DataSetOperationDTO> updateDataSetOperation(@Valid @RequestBody DataSetOperationDTO dataSetOperationDTO) throws URISyntaxException {
        log.debug("REST request to update DataSetOperation : {}", dataSetOperationDTO);
        if (dataSetOperationDTO.getId() == null) {
            return createDataSetOperation(dataSetOperationDTO);
        }
        DataSetOperation dataSetOperation = dataSetOperationMapper.toEntity(dataSetOperationDTO);
        dataSetOperation = dataSetOperationRepository.save(dataSetOperation);
        DataSetOperationDTO result = dataSetOperationMapper.toDto(dataSetOperation);
        dataSetOperationSearchRepository.save(dataSetOperation);
        return ResponseEntity.ok()
                .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, dataSetOperationDTO.getId().toString()))
                .body(result);
    }

    /**
     * GET  /data-set-operations : get all the dataSetOperations.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of dataSetOperations in body
     */
    @GetMapping("/data-set-operations")
    @Timed
    public ResponseEntity<List<DataSetOperationDTO>> getAllDataSetOperations(Pageable pageable) {
        log.debug("REST request to get a page of DataSetOperations");
        Page<DataSetOperation> page = dataSetOperationRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/data-set-operations");
        return new ResponseEntity<>(dataSetOperationMapper.toDto(page.getContent()), headers, HttpStatus.OK);
    }

    /**
     * GET  /data-set-operations/:id : get the "id" dataSetOperation.
     *
     * @param id the id of the dataSetOperationDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the dataSetOperationDTO, or with status 404 (Not Found)
     */
    @GetMapping("/data-set-operations/{id}")
    @Timed
    public ResponseEntity<DataSetOperationDTO> getDataSetOperation(@PathVariable Long id) {
        log.debug("REST request to get DataSetOperation : {}", id);
        DataSetOperation dataSetOperation = dataSetOperationRepository.findOne(id);
        DataSetOperationDTO dataSetOperationDTO = dataSetOperationMapper.toDto(dataSetOperation);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(dataSetOperationDTO));
    }

    /**
     * DELETE  /data-set-operations/:id : delete the "id" dataSetOperation.
     *
     * @param id the id of the dataSetOperationDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/data-set-operations/{id}")
    @Timed
    public ResponseEntity<Void> deleteDataSetOperation(@PathVariable Long id) {
        log.debug("REST request to delete DataSetOperation : {}", id);
        dataSetOperationRepository.delete(id);
        dataSetOperationSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/data-set-operations?query=:query : search for the dataSetOperation corresponding
     * to the query.
     *
     * @param query    the query of the dataSetOperation search
     * @param pageable the pagination information
     * @return the result of the search
     */
    @GetMapping("/_search/data-set-operations")
    @Timed
    public ResponseEntity<List<DataSetOperationDTO>> searchDataSetOperations(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of DataSetOperations for query {}", query);
        Page<DataSetOperation> page = dataSetOperationSearchRepository.search(queryStringQuery(query), pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/data-set-operations");
        return new ResponseEntity<>(dataSetOperationMapper.toDto(page.getContent()), headers, HttpStatus.OK);
    }

}
