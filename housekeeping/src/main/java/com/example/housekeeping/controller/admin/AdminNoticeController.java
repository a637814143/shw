package com.example.housekeeping.controller.admin;

import com.example.housekeeping.common.ApiResponse;
import com.example.housekeeping.common.BusinessException;
import com.example.housekeeping.entity.SystemNotice;
import com.example.housekeeping.repository.SystemNoticeRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/notices")
@RequiredArgsConstructor
public class AdminNoticeController {

    private final SystemNoticeRepository noticeRepository;

    @GetMapping
    public ApiResponse<Page<SystemNotice>> list(Pageable pageable) {
        return ApiResponse.success(noticeRepository.findAll(pageable));
    }

    @PostMapping
    public ApiResponse<SystemNotice> create(@RequestBody @Valid SystemNotice notice) {
        return ApiResponse.success(noticeRepository.save(notice));
    }

    @PutMapping("/{id}")
    public ApiResponse<SystemNotice> update(@PathVariable Long id, @RequestBody @Valid SystemNotice request) {
        SystemNotice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new BusinessException("公告不存在"));
        notice.setTitle(request.getTitle());
        notice.setContent(request.getContent());
        notice.setStatus(request.getStatus());
        notice.setType(request.getType());
        return ApiResponse.success(noticeRepository.save(notice));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        noticeRepository.deleteById(id);
        return ApiResponse.success();
    }
}
