package com.example.housekeeping.controller;

import com.example.housekeeping.model.ServiceItem;
import com.example.housekeeping.service.ServiceItemService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/services")
@CrossOrigin(origins = "*")
public class ServiceItemController {

    private final ServiceItemService serviceItemService;

    public ServiceItemController(ServiceItemService serviceItemService) {
        this.serviceItemService = serviceItemService;
    }

    @GetMapping
    public List<ServiceItem> list() {
        return serviceItemService.findAll();
    }

    @PostMapping
    public ResponseEntity<ServiceItem> create(Authentication authentication,
                                              @Valid @RequestBody ServiceItem serviceItem) {
        assertAdmin(authentication);
        ServiceItem created = serviceItemService.create(serviceItem);
        return ResponseEntity
                .created(URI.create("/api/services/" + created.getId()))
                .body(created);
    }

    @GetMapping("/{id}")
    public ServiceItem get(@PathVariable Long id) {
        return serviceItemService.findById(id);
    }

    @PutMapping("/{id}")
    public ServiceItem update(@PathVariable Long id,
                              Authentication authentication,
                              @Valid @RequestBody ServiceItem serviceItem) {
        assertAdmin(authentication);
        return serviceItemService.update(id, serviceItem);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, Authentication authentication) {
        assertAdmin(authentication);
        serviceItemService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private void assertAdmin(Authentication authentication) {
        boolean admin = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals("ROLE_ADMIN"));
        if (!admin) {
            throw new IllegalStateException("只有管理员可以管理服务项目");
        }
    }
}
