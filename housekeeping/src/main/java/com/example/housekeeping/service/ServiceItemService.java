package com.example.housekeeping.service;

import com.example.housekeeping.model.ServiceItem;
import com.example.housekeeping.repository.ServiceItemRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServiceItemService {

    private final ServiceItemRepository serviceItemRepository;

    public ServiceItemService(ServiceItemRepository serviceItemRepository) {
        this.serviceItemRepository = serviceItemRepository;
    }

    public List<ServiceItem> findAll() {
        return serviceItemRepository.findAll(Sort.by(Sort.Direction.ASC, "name"));
    }

    public ServiceItem findById(Long id) {
        return serviceItemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("服务项目不存在"));
    }

    public ServiceItem create(ServiceItem item) {
        if (serviceItemRepository.existsByNameIgnoreCase(item.getName())) {
            throw new IllegalArgumentException("服务名称已存在");
        }
        return serviceItemRepository.save(item);
    }
}

