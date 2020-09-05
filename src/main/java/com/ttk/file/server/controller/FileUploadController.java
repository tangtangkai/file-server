package com.ttk.file.server.controller;

import com.ttk.file.server.config.FileConfig;
import com.ttk.file.server.domain.UploadedFile;
import com.ttk.file.server.domain.consts.ApplicationConsts;
import com.ttk.file.server.domain.enums.FileType;
import com.ttk.file.server.repository.UploadFileRepository;
import com.ttk.file.server.utils.GetSuffixUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * @author root
 */
@Slf4j
@RequestMapping("/file")
@RestController
public class FileUploadController {

    @Autowired
    private FileConfig fileConfig;

    @Autowired
    UploadFileRepository uploadFileRepository;

    @PostMapping("/upload")
    public String upload(@RequestParam("file") MultipartFile file, HttpServletRequest request) throws IOException {
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

    @GetMapping("/getAll")
    public List<UploadedFile> getAll() {
        return uploadFileRepository.findAll();
    }

    @GetMapping("/getFileById")
    public void getFileById(@RequestParam long id, HttpServletResponse response) throws IOException {
        UploadedFile one = uploadFileRepository.getOne(id);
        File file = new File(one.getFilePath());
        response.setContentType(one.getContentType());
        byte[] buffer = new byte[ApplicationConsts.MAX_BUFFER];
        try (InputStream in = new FileInputStream(file)) {
            try (OutputStream out = new BufferedOutputStream(response.getOutputStream())) {
                int i = in.read(buffer);
                out.write(buffer, 0, i);
                out.flush();
            }
        }
    }

    private void saveFile(File dest) {
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
        }
        Date now = new Date();
        uploadedFile.setCreateTime(now);
        uploadedFile.setUpdateTime(now);
        uploadFileRepository.save(uploadedFile);
    }
}
