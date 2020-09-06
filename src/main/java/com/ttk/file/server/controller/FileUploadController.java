package com.ttk.file.server.controller;

import com.ttk.file.server.config.FileConfig;
import com.ttk.file.server.domain.Resp;
import com.ttk.file.server.domain.UploadedFile;
import com.ttk.file.server.service.IFileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.List;

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
    private IFileService fileService;

    @PostMapping("/upload")
    public Resp<String> upload(@RequestParam("file") MultipartFile file) throws IOException {
        String upload = fileService.upload(file);
        return Resp.ofSuccess(upload);
    }

    /**
     * 查询全部，包括已经逻辑删除的文件
     *
     * @return 查询结果
     */
    @GetMapping("/getAll")
    public Resp<List<UploadedFile>> getAll() {
        List<UploadedFile> all = fileService.getAllFiles();
        return Resp.ofSuccess(all);
    }

    /**
     * 查询全部，不包括已经逻辑删除的文件
     *
     * @return
     */
    @GetMapping("/getAllNotDeleted")
    public Resp<List<UploadedFile>> getAllNotDeleted() {
        List<UploadedFile> files = fileService.getAllNotDeleted();
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
        UploadedFile one = fileService.getFileById(id);
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
        fileService.tombstoneById(id);
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
    public Resp<String> deleteById(@RequestParam Long id) throws IOException {
        fileService.deleteById(id);
        return Resp.ofSuccess(OPERATION_SUCCESS);
    }

}
