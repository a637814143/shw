package com.example.housekeeping.controller;

import com.example.housekeeping.model.ServiceItem;
import com.example.housekeeping.repository.ServiceItemRepository;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/services")
public class ServiceItemController {

    private final ServiceItemRepository serviceItemRepository;

    public ServiceItemController(ServiceItemRepository serviceItemRepository) {
        this.serviceItemRepository = serviceItemRepository;
    }

    @GetMapping
    public List<ServiceItem> listServices() {
        return serviceItemRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
    }
}
