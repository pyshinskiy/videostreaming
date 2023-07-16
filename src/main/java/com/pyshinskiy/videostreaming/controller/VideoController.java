package com.pyshinskiy.videostreaming.controller;

import com.pyshinskiy.videostreaming.controller.constants.HttpConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.pyshinskiy.videostreaming.service.DefaultVideoService;
import com.pyshinskiy.videostreaming.service.VideoService;
import com.pyshinskiy.videostreaming.util.Range;

import java.util.UUID;

import static org.springframework.http.HttpHeaders.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/video")
public class VideoController {

    private final VideoService videoService;

    @Value("${photon.streaming.default-chunk-size}")
    public Integer defaultChunkSize;

    @PostMapping
    public ResponseEntity<UUID> save(@RequestParam("file") MultipartFile file) {
        UUID fileUuid = videoService.save(file);
        return ResponseEntity.ok(fileUuid);
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<byte[]> fetchChunk(
            @RequestHeader(value = HttpHeaders.RANGE, required = false) String range,
            @PathVariable UUID uuid
    ) {
        Range parsedRange = Range.parseHttpRangeString(range, defaultChunkSize);
        DefaultVideoService.ChunkWithMetadata chunkWithMetadata = videoService.fetchChunk(uuid, parsedRange);
        return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                .header(CONTENT_TYPE, chunkWithMetadata.metadata().getHttpContentType())
                .header(ACCEPT_RANGES, HttpConstants.ACCEPTS_RANGES_VALUE)
                .header(CONTENT_LENGTH, calculateContentLengthHeader(parsedRange, chunkWithMetadata.metadata().getSize()))
                .header(CONTENT_RANGE, constructContentRangeHeader(parsedRange, chunkWithMetadata.metadata().getSize()))
                .body(chunkWithMetadata.chunk());
    }

    private String calculateContentLengthHeader(Range range, long fileSize) {
        return String.valueOf(range.getRangeEnd(fileSize) - range.getRangeStart() + 1);
    }

    private String constructContentRangeHeader(Range range, long fileSize) {
        return  "bytes " + range.getRangeStart() + "-" + range.getRangeEnd(fileSize) + "/" + fileSize;
    }
}
