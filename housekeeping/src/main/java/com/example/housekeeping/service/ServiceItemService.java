package com.example.housekeeping.service;

import com.example.housekeeping.model.ServiceItem;
import com.example.housekeeping.repository.ServiceItemRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServiceItemService {

    private final ServiceItemRepository repository;

    public ServiceItemService(ServiceItemRepository repository) {
        this.repository = repository;
    }

    public List<ServiceItem> findAll() {
        return repository.findAll();
    }

    public ServiceItem create(ServiceItem serviceItem) {
        return repository.save(serviceItem);
    }

    public ServiceItem findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("未找到编号为" + id + "的服务"));
    }

    public ServiceItem update(Long id, ServiceItem updated) {
        ServiceItem existing = findById(id);
        existing.setName(updated.getName());
        existing.setDescription(updated.getDescription());
        existing.setPrice(updated.getPrice());
        return repository.save(existing);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}
