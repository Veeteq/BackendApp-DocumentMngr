package com.veeteq.documentmngr.poc;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/doc")
public class DocController {

    private final DocService docService;

    public DocController(DocService docService) {
        this.docService = docService;
    }

    @PostMapping(path = {"", "/"})
    public ResponseEntity<DocDto> save(@RequestBody DocDto dto) {
        var response = docService.save(dto);
        return ResponseEntity.ok(response);
    }

    @GetMapping(path = {"", "/"})
    public ResponseEntity<List<DocDto>> findAll() {
        var response = docService.findAll();
        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<DocDto> findById(@PathVariable(value = "id") Long id) {
        var response = docService.findById(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping(path = "/{id}")
    public ResponseEntity<DocDto> update(@PathVariable(value = "id") Long id, @RequestBody DocDto dto) {
        var response = docService.update(id, dto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable(value = "id") Long id) {
        docService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(value = {EntityNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ErrorResponse> handleNotFoundException(EntityNotFoundException exc) {
        return ResponseEntity.badRequest().body(new ErrorResponse() {
            @Override
            public HttpStatusCode getStatusCode() {
                return HttpStatus.NOT_FOUND;
            }

            @Override
            public ProblemDetail getBody() {
                return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, exc.getMessage());
            }
        });
    }

    @ExceptionHandler(value = {ObjectOptimisticLockingFailureException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleBadRequestException(ObjectOptimisticLockingFailureException exc) {
        var errResponse = ErrorResponse.create(exc, HttpStatus.BAD_REQUEST, exc.getMessage());

        return ResponseEntity.badRequest().body(errResponse);
    }
}
