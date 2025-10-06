package com.example.housekeeping.controller;

import com.example.housekeeping.domain.entity.ServiceProvider;
import com.example.housekeeping.repository.ServiceProviderRepository;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/providers")
public class ProviderManagementController {

    private final ServiceProviderRepository serviceProviderRepository;

    public ProviderManagementController(ServiceProviderRepository serviceProviderRepository) {
        this.serviceProviderRepository = serviceProviderRepository;
    }

    @GetMapping
    public List<ServiceProvider> listProviders() {
        return serviceProviderRepository.findAll();
    }

    @GetMapping("/{id}")
    public ServiceProvider getProvider(@PathVariable Long id) {
        return serviceProviderRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "家政人员不存在"));
    }

    @PutMapping("/{id}/enabled")
    public ServiceProvider updateProviderStatus(@PathVariable Long id, @RequestParam boolean enabled) {
        ServiceProvider provider = getProvider(id);
        provider.setEnabled(enabled);
        return provider;
    }

    @PutMapping("/{id}/certified")
    public ServiceProvider updateCertified(@PathVariable Long id, @RequestParam boolean certified) {
        ServiceProvider provider = getProvider(id);
        provider.setCertified(certified);
        return provider;
    }
}
