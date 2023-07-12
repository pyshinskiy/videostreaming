package ru.pyshinskiy.photon.service;

import org.springframework.web.multipart.MultipartFile;
import ru.pyshinskiy.photon.utl.Range;

import java.util.List;
import java.util.UUID;

public interface VideoService {

    UUID save(MultipartFile video);

    List<UUID> getAll();

    DefaultVideoService.ChunkWithMetadata fetchChunks(UUID uuid, Range range);
}
