package com.example.housekeeping.controller;

import com.example.housekeeping.common.Result;
import com.example.housekeeping.util.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 文件上传控制器
 */
@RestController
@RequestMapping("/api/upload")
public class FileController {

    private static final Logger log = LoggerFactory.getLogger(FileController.class);

    /**
     * 上传图片
     */
    @PostMapping("/image")
    public Result<Map<String, String>> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return Result.badRequest("文件不能为空");
            }

            // 检查文件类型
            if (!FileUtil.isImageType(file.getContentType())) {
                return Result.badRequest("只支持图片格式：jpg、jpeg、png、gif、webp");
            }

            // 检查文件大小（5MB）
            if (file.getSize() > 5 * 1024 * 1024) {
                return Result.badRequest("图片大小不能超过5MB");
            }

            String filePath = FileUtil.saveFile(file, "images");
            
            Map<String, String> result = new HashMap<>();
            result.put("filePath", filePath);
            result.put("fileName", file.getOriginalFilename());
            result.put("fileSize", FileUtil.formatFileSize(file.getSize()));
            
            return Result.success("图片上传成功", result);
            
        } catch (IOException e) {
            log.error("图片上传失败：{}", e.getMessage(), e);
            return Result.error("图片上传失败");
        } catch (IllegalArgumentException e) {
            return Result.badRequest(e.getMessage());
        }
    }

    /**
     * 上传文档
     */
    @PostMapping("/document")
    public Result<Map<String, String>> uploadDocument(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return Result.badRequest("文件不能为空");
            }

            // 检查文件大小（10MB）
            if (file.getSize() > 10 * 1024 * 1024) {
                return Result.badRequest("文档大小不能超过10MB");
            }

            String filePath = FileUtil.saveFile(file, "documents");
            
            Map<String, String> result = new HashMap<>();
            result.put("filePath", filePath);
            result.put("fileName", file.getOriginalFilename());
            result.put("fileSize", FileUtil.formatFileSize(file.getSize()));
            
            return Result.success("文档上传成功", result);
            
        } catch (IOException e) {
            log.error("文档上传失败：{}", e.getMessage(), e);
            return Result.error("文档上传失败");
        } catch (IllegalArgumentException e) {
            return Result.badRequest(e.getMessage());
        }
    }

    /**
     * 上传头像
     */
    @PostMapping("/avatar")
    public Result<Map<String, String>> uploadAvatar(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return Result.badRequest("文件不能为空");
            }

            // 检查文件类型
            if (!FileUtil.isImageType(file.getContentType())) {
                return Result.badRequest("只支持图片格式：jpg、jpeg、png、gif、webp");
            }

            // 检查文件大小（2MB）
            if (file.getSize() > 2 * 1024 * 1024) {
                return Result.badRequest("头像大小不能超过2MB");
            }

            String filePath = FileUtil.saveFile(file, "avatars");
            
            Map<String, String> result = new HashMap<>();
            result.put("filePath", filePath);
            result.put("fileName", file.getOriginalFilename());
            result.put("fileSize", FileUtil.formatFileSize(file.getSize()));
            
            return Result.success("头像上传成功", result);
            
        } catch (IOException e) {
            log.error("头像上传失败：{}", e.getMessage(), e);
            return Result.error("头像上传失败");
        } catch (IllegalArgumentException e) {
            return Result.badRequest(e.getMessage());
        }
    }

    /**
     * 删除文件
     */
    @DeleteMapping("/{filePath}")
    public Result<String> deleteFile(@PathVariable String filePath) {
        try {
            boolean deleted = FileUtil.deleteFile(filePath);
            if (deleted) {
                return Result.success("文件删除成功");
            } else {
                return Result.error("文件删除失败");
            }
        } catch (Exception e) {
            log.error("文件删除失败：{}", e.getMessage(), e);
            return Result.error("文件删除失败");
        }
    }
}
