package com.ttk.file.server.service.impl;

import com.ttk.file.server.config.FileConfig;
import com.ttk.file.server.domain.UploadedFile;
import com.ttk.file.server.domain.enums.DeleteStatus;
import com.ttk.file.server.domain.enums.FileType;
import com.ttk.file.server.repository.UploadFileRepository;
import com.ttk.file.server.service.IFileService;
import com.ttk.file.server.utils.GetSuffixUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class FileServiceImpl implements IFileService {


    @Autowired
    UploadFileRepository uploadFileRepository;

    @Autowired
    private FileConfig fileConfig;

    @Override
    public String upload(MultipartFile file) throws IOException {
        String realName = fileConfig.getFile().get("path") +
                File.separator + UUID.randomUUID().toString().replace("-", "") + file.getOriginalFilename();
        File dest = new File(realName);
        if (!dest.getParentFile().exists()) {
            boolean mkdir = dest.getParentFile().mkdirs();
            if (mkdir) {
                file.transferTo(dest);
                saveFile(dest);
                log.info("文件上传成功！");
                return dest.getAbsolutePath();
            }
        }
        file.transferTo(dest);
        saveFile(dest);
        log.info("文件上传成功！");
        return dest.getAbsolutePath();
    }

    @Override
    public List<UploadedFile> getAllFiles() {
        return uploadFileRepository.findAll();
    }

    @Override
    public List<UploadedFile> getAllNotDeleted() {
        return uploadFileRepository.findByDeleted(DeleteStatus.NOT_DELETE.getStatus());
    }

    @Override
    public UploadedFile getFileById(long id) {
        return uploadFileRepository.getOne(id);
    }

    @Override
    public void tombstoneById(long id) {
        UploadedFile one = uploadFileRepository.getOne(id);
        one.setDeleted(DeleteStatus.DELETED.getStatus());
        one.setUpdateTime(new Date());
        uploadFileRepository.save(one);
    }

    @Override
    public void deleteById(long id) {
        UploadedFile one = uploadFileRepository.getOne(id);
        File file = new File(one.getFilePath());
        boolean delete = file.delete();
        if (!delete) {
            log.error("文件删除失败，[path]={}", one.getFilePath());
            throw new RuntimeException("文件删除失败");
        }
        log.info("文件删除成功，[path]={}", one.getFilePath());
        uploadFileRepository.deleteById(id);
    }

    @Override
    public void saveFile(File dest) {
        UploadedFile uploadedFile = new UploadedFile();
        String suffix = GetSuffixUtil.getSuffix(dest.getName());
        uploadedFile.setFileName(dest.getName());
        uploadedFile.setFilePath(dest.getAbsolutePath());
        Optional<FileType> fileType = FileType.valueOfBySuffix(suffix);
        if (fileType.isPresent()) {
            FileType type = fileType.get();
            String contentType = type.getContentType();
            uploadedFile.setFileType(type.getCode());
            uploadedFile.setContentType(contentType);
        } else {
            throw new RuntimeException("上传失败，上传的文件类型不存在");
        }
        Date now = new Date();
        uploadedFile.setDeleted(DeleteStatus.NOT_DELETE.getStatus());
        uploadedFile.setCreateTime(now);
        uploadedFile.setUpdateTime(now);
        uploadFileRepository.save(uploadedFile);
    }
}
