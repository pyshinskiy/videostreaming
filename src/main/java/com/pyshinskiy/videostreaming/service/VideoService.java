package com.pyshinskiy.videostreaming.service;

import org.springframework.web.multipart.MultipartFile;
import com.pyshinskiy.videostreaming.utl.Range;

import java.util.UUID;

public interface VideoService {

    UUID save(MultipartFile video);

    DefaultVideoService.ChunkWithMetadata fetchChunk(UUID uuid, Range range);
}
