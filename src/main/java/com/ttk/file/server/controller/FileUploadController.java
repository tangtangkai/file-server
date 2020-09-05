package com.ttk.file.server.controller;

import com.ttk.file.server.config.FileConfig;
import com.ttk.file.server.domain.Resp;
import com.ttk.file.server.domain.UploadedFile;
import com.ttk.file.server.domain.enums.DeleteStatus;
import com.ttk.file.server.domain.enums.FileType;
import com.ttk.file.server.repository.UploadFileRepository;
import com.ttk.file.server.utils.GetSuffixUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
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

    private final static String OPERATION_SUCCESS = "操作成功";


    @Autowired
    private FileConfig fileConfig;

    @Autowired
    UploadFileRepository uploadFileRepository;

    @PostMapping("/upload")
    public Resp<String> upload(@RequestParam("file") MultipartFile file) throws IOException {
        String realName = fileConfig.getFile().get("path") +
                File.separator + UUID.randomUUID().toString().replace("-", "") + file.getOriginalFilename();
        File dest = new File(realName);
        if (!dest.getParentFile().exists()) {
            boolean mkdir = dest.getParentFile().mkdirs();
            if (mkdir) {
                file.transferTo(dest);
                saveFile(dest);
                log.info("文件上传成功！");
                return Resp.ofSuccess(dest.getAbsolutePath());
            }
        }
        file.transferTo(dest);
        saveFile(dest);
        log.info("文件上传成功！");
        return Resp.ofSuccess(dest.getAbsolutePath());
    }

    /**
     * 查询全部，包括已经逻辑删除的文件
     *
     * @return 查询结果
     */
    @GetMapping("/getAll")
    public Resp<List<UploadedFile>> getAll() {
        List<UploadedFile> all = uploadFileRepository.findAll();
        return Resp.ofSuccess(all);
    }

    /**
     * 查询全部，不包括已经逻辑删除的文件
     *
     * @return
     */
    @GetMapping("/getAllNotDeleted")
    public Resp<List<UploadedFile>> getAllNotDeleted() {
        List<UploadedFile> files = uploadFileRepository.findByDeleted(DeleteStatus.NOT_DELETE.getStatus());
        return Resp.ofSuccess(files);
    }

    /**
     * 根据id查询图片并展示
     *
     * @param id       图片id
     * @param response http-response
     * @return 操作结果
     * @throws IOException io异常
     */
    @GetMapping("/getFileById")
    public Resp<String> getFileById(@RequestParam long id, HttpServletResponse response) throws IOException {
        UploadedFile one = uploadFileRepository.getOne(id);
        RandomAccessFile file = new RandomAccessFile(one.getFilePath(), "rw");
        int bufferSize = new Integer(fileConfig.getFile().get("buffer"));
        byte[] buffer = new byte[bufferSize];
        /*获得文件锁，当锁不可用，会被阻塞，trylock()方法则会在锁不可用时返回null*/
        FileChannel channel = file.getChannel();
        FileLock lock = channel.lock();
        try (OutputStream out = new BufferedOutputStream(response.getOutputStream())) {
            int i = file.read(buffer);
            out.write(buffer, 0, i);
            out.flush();
        } finally {
            /*关闭文件,关闭文件的同时，会将锁释放*,相当于同时执行了,lock.release()*/
            file.close();
        }
        return Resp.ofSuccess(OPERATION_SUCCESS);
    }

    /**
     * 逻辑删除tombstone
     *
     * @param id
     * @return
     */
    @PostMapping("/tombstoneById")
    public Resp<String> tombstoneById(@RequestParam long id) {
        UploadedFile one = uploadFileRepository.getOne(id);
        one.setDeleted(DeleteStatus.DELETED.getStatus());
        one.setUpdateTime(new Date());
        uploadFileRepository.save(one);
        return Resp.ofSuccess(OPERATION_SUCCESS);
    }

    /**
     * 文件物理删除
     *
     * @param id
     * @return
     * @throws IOException
     */
    @PostMapping("/deleteById")
    public Resp<String> deleteById(@RequestParam long id) throws IOException {
        UploadedFile one = uploadFileRepository.getOne(id);
        File file = new File(one.getFilePath());
        boolean delete = file.delete();
        if (!delete) {
            log.error("文件删除失败，[path]={}", one.getFilePath());
            throw new RuntimeException("文件删除失败");
        }
        log.info("文件删除成功，[path]={}", one.getFilePath());
        uploadFileRepository.deleteById(id);
        return Resp.ofSuccess(OPERATION_SUCCESS);
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
