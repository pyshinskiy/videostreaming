package ru.pyshinskiy.photon.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.pyshinskiy.photon.entity.FileMetadataEntity;

public interface FileMetadataRepository extends JpaRepository<FileMetadataEntity, String> {
}
