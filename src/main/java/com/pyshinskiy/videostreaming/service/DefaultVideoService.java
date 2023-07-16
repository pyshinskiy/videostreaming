package com.pyshinskiy.videostreaming.service;

import com.pyshinskiy.videostreaming.exception.StorageException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.pyshinskiy.videostreaming.binarystorage.MinioStorageService;
import com.pyshinskiy.videostreaming.entity.FileMetadataEntity;
import com.pyshinskiy.videostreaming.repository.FileMetadataRepository;
import com.pyshinskiy.videostreaming.utl.Range;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DefaultVideoService implements VideoService {

    private final MinioStorageService storageService;

    private final FileMetadataRepository fileMetadataRepository;

    @Override
    @Transactional
    public UUID save(MultipartFile video) {
        try {
            UUID fileUuid = UUID.randomUUID();
            FileMetadataEntity metadata = FileMetadataEntity.builder()
                    .id(fileUuid.toString())
                    .size(video.getSize())
                    .httpContentType(video.getContentType())
                    .build();
            fileMetadataRepository.save(metadata);
            storageService.save(video, fileUuid);
            return fileUuid;
        } catch (Exception ex) {
            log.error("Exception occurred when trying to save the file", ex);
            throw new StorageException(ex);
        }
    }

    @Override
    public ChunkWithMetadata fetchChunk(UUID uuid, Range range) {
        FileMetadataEntity fileMetadata = fileMetadataRepository.findById(uuid.toString()).orElseThrow();
        return new ChunkWithMetadata(fileMetadata, readChunk(uuid, range, fileMetadata.getSize()));
    }

    private byte[] readChunk(UUID uuid, Range range, long fileSize) {
        try(InputStream inputStream = storageService.getInputStream(uuid)) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            long startPosition = range.getRangeStart();
            long endPosition = range.getRangeEnd(fileSize);
            int chunkSize = (int) (endPosition - startPosition + 1);
            byte[] data = new byte[chunkSize];
            inputStream.skipNBytes(startPosition);
            inputStream.readNBytes(data, 0, chunkSize);
            byteArrayOutputStream.writeBytes(data);
            byteArrayOutputStream.flush();
            return byteArrayOutputStream.toByteArray();
        } catch (Exception exception) {
            log.error("Exception occurred when trying to read file with ID = {}", uuid);
            throw new StorageException(exception);
        }
    }

    public record ChunkWithMetadata(
            FileMetadataEntity metadata,
            byte[] chunk
    ) {}
}
