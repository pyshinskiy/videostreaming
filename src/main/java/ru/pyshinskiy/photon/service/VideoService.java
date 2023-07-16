package ru.pyshinskiy.photon.service;

import org.springframework.web.multipart.MultipartFile;
import ru.pyshinskiy.photon.utl.Range;

import java.util.UUID;

public interface VideoService {

    UUID save(MultipartFile video);

    DefaultVideoService.ChunkWithMetadata fetchChunk(UUID uuid, Range range);
}
