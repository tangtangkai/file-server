package com.ttk.file.server;

import com.ttk.file.server.domain.UploadedFile;
import com.ttk.file.server.domain.enums.DeleteStatus;
import com.ttk.file.server.repository.UploadFileRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {
        FileServerApplication.class
})
public class FileTest {

    @Autowired
    UploadFileRepository fileRepository;

    @Test
    public void fun() {
        List<UploadedFile> files = fileRepository.findByDeleted(DeleteStatus.DELETED.getStatus());
        for (UploadedFile file : files) {
            System.out.println(file.toString());
        }
    }
}
