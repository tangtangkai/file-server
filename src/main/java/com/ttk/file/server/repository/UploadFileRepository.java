package com.ttk.file.server.repository;

import com.ttk.file.server.domain.UploadedFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UploadFileRepository extends JpaRepository<UploadedFile,Long>, CrudRepository<UploadedFile,Long> {

}
