package com.ttk.file.server.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.Date;

/**
 * 数据库对应的实体类
 *
 * @author root
 */
@Getter
@Setter
@ToString
@Entity(name = "uploaded_file")
public class UploadedFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long fileId;
    @Column(name = "file_type", nullable = false, length = 6)
    private Integer fileType;
    @Column(name = "content_type", nullable = false, length = 63)
    private String contentType;
    @Column(name = "file_name", nullable = false)
    private String fileName;
    @Column(name = "file_path", nullable = false)
    private String filePath;
    @Column(name = "deleted", nullable = false, length = 6)
    private int deleted = 0;
    @Column(name = "create_time", nullable = false)
    private Date createTime;
    @Column(name = "update_time", nullable = false)
    private Date updateTime;
}
