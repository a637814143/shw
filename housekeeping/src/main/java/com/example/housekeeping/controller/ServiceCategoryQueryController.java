package com.example.housekeeping.controller;

import com.example.housekeeping.common.Result;
import com.example.housekeeping.dto.ServiceCategoryResponse;
import com.example.housekeeping.service.ServiceCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/public/service-categories")
public class ServiceCategoryQueryController {

    @Autowired
    private ServiceCategoryService serviceCategoryService;

    @GetMapping
    public Result<List<ServiceCategoryResponse>> listCategories() {
        return Result.success(serviceCategoryService.listAll());
    }
}
