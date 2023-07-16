package com.pyshinskiy.videostreaming.repository;

import com.pyshinskiy.videostreaming.entity.FileMetadataEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileMetadataRepository extends JpaRepository<FileMetadataEntity, String> {
}
