package com.example.housekeeping.controller;

import com.example.housekeeping.dto.EvaluationRequest;
import com.example.housekeeping.entity.ServiceEvaluation;
import com.example.housekeeping.exception.ResourceNotFoundException;
import com.example.housekeeping.repository.ServiceEvaluationRepository;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

import java.net.URI;

@RestController
@RequestMapping("/api/evaluations")
public class EvaluationController {

    private final ServiceEvaluationRepository evaluationRepository;

    public EvaluationController(ServiceEvaluationRepository evaluationRepository) {
        this.evaluationRepository = evaluationRepository;
    }

    @GetMapping
    public Page<ServiceEvaluation> list(@RequestParam(required = false) String keyword,
                                        @RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.max(size, 1), Sort.by("evaluationTime").descending());
        String normalizedKeyword = keyword != null && !keyword.isBlank() ? keyword : null;
        return evaluationRepository.search(normalizedKeyword, pageable);
    }

    @GetMapping("/{id}")
    public ServiceEvaluation detail(@PathVariable Long id) {
        return evaluationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("未找到对应的评价"));
    }

    @PostMapping
    public ResponseEntity<ServiceEvaluation> create(@Valid @RequestBody EvaluationRequest request) {
        ServiceEvaluation evaluation = ServiceEvaluation.builder()
                .providerName(request.getProviderName())
                .serviceName(request.getServiceName())
                .userName(request.getUserName())
                .rating(request.getRating())
                .content(request.getContent())
                .evaluationTime(request.getEvaluationTime())
                .build();
        ServiceEvaluation saved = evaluationRepository.save(evaluation);
        return ResponseEntity.created(URI.create("/api/evaluations/" + saved.getId())).body(saved);
    }

    @PutMapping("/{id}")
    public ServiceEvaluation update(@PathVariable Long id, @Valid @RequestBody EvaluationRequest request) {
        ServiceEvaluation evaluation = evaluationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("未找到对应的评价"));
        evaluation.setProviderName(request.getProviderName());
        evaluation.setServiceName(request.getServiceName());
        evaluation.setUserName(request.getUserName());
        evaluation.setRating(request.getRating());
        evaluation.setContent(request.getContent());
        evaluation.setEvaluationTime(request.getEvaluationTime());
        return evaluationRepository.save(evaluation);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        ServiceEvaluation evaluation = evaluationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("未找到对应的评价"));
        evaluationRepository.delete(evaluation);
        return ResponseEntity.noContent().build();
    }
}
