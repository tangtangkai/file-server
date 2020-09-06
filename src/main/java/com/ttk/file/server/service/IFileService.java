package com.ttk.file.server.service;

import com.ttk.file.server.domain.UploadedFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

public interface IFileService {

    String upload(MultipartFile file) throws IOException;

    List<UploadedFile> getAllFiles();

    List<UploadedFile> getAllNotDeleted();

    UploadedFile getFileById(long id);

    void tombstoneById(long id);

    void deleteById(long id);

    void saveFile(File file);
}
