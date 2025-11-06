package az.tarkhangurbanli.saga.controller;

import az.tarkhangurbanli.saga.model.dto.request.StartSagaRequest;
import az.tarkhangurbanli.saga.model.dto.response.SagaResponse;
import az.tarkhangurbanli.saga.service.SagaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/sagas")
@RequiredArgsConstructor
@Slf4j
public class SagaController {

    private final SagaService sagaService;

    @PostMapping
    public ResponseEntity<SagaResponse> startSaga(@Valid @RequestBody StartSagaRequest request) {
        log.info("Received request to start saga for customer: {}", request.customerId());
        SagaResponse response = sagaService.startSaga(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{sagaId}")
    public ResponseEntity<SagaResponse> getSaga(@PathVariable String sagaId) {
        SagaResponse response = sagaService.getSaga(sagaId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<SagaResponse>> getCustomerSagas(@PathVariable String customerId) {
        List<SagaResponse> sagas = sagaService.getCustomerSagas(customerId);
        return ResponseEntity.ok(sagas);
    }

    @GetMapping
    public ResponseEntity<List<SagaResponse>> getAllSagas() {
        List<SagaResponse> sagas = sagaService.getAllSagas();
        return ResponseEntity.ok(sagas);
    }

}
