package com.example.housekeeping.service;

import com.example.housekeeping.common.PageResult;
import com.example.housekeeping.entity.SystemNotice;
import com.example.housekeeping.repository.SystemNoticeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 系统公告服务类
 */
@Service
public class SystemNoticeService {
    
    @Autowired
    private SystemNoticeRepository systemNoticeRepository;
    
    /**
     * 分页查询公告
     */
    public PageResult<SystemNotice> getNotices(int page, int size, String title, Integer type, Integer status) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createTime"));
        Page<SystemNotice> noticePage = systemNoticeRepository.findByConditions(title, type, status, pageable);
        return PageResult.of(noticePage);
    }
    
    /**
     * 获取启用的公告列表
     */
    public List<SystemNotice> getActiveNotices() {
        return systemNoticeRepository.findByStatusOrderByCreateTimeDesc(1);
    }
    
    /**
     * 根据ID获取公告
     */
    public SystemNotice getNoticeById(Long id) {
        return systemNoticeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("公告不存在"));
    }
    
    /**
     * 创建公告
     */
    public SystemNotice createNotice(SystemNotice notice) {
        return systemNoticeRepository.save(notice);
    }
    
    /**
     * 更新公告
     */
    public SystemNotice updateNotice(Long id, SystemNotice notice) {
        SystemNotice existingNotice = getNoticeById(id);
        existingNotice.setTitle(notice.getTitle());
        existingNotice.setContent(notice.getContent());
        existingNotice.setType(notice.getType());
        existingNotice.setStatus(notice.getStatus());
        return systemNoticeRepository.save(existingNotice);
    }
    
    /**
     * 删除公告
     */
    public void deleteNotice(Long id) {
        if (!systemNoticeRepository.existsById(id)) {
            throw new RuntimeException("公告不存在");
        }
        systemNoticeRepository.deleteById(id);
    }
    
    /**
     * 启用/禁用公告
     */
    public void toggleNoticeStatus(Long id) {
        SystemNotice notice = getNoticeById(id);
        notice.setStatus(notice.getStatus() == 1 ? 0 : 1);
        systemNoticeRepository.save(notice);
    }
}
