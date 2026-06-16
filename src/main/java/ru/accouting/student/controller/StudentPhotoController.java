package ru.accouting.student.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.accouting.student.config.SecurityUtils;
import ru.accouting.student.model.Student;
import ru.accouting.student.repository.StudentRepository;
import ru.accouting.student.service.StudentPhotoService;

import java.net.MalformedURLException;
import java.nio.file.Path;

@RestController
@RequestMapping("/api/students/{studentId}/photo")
@RequiredArgsConstructor
public class StudentPhotoController {

    private final StudentPhotoService photoService;
    private final StudentRepository studentRepository;
    private final SecurityUtils securityUtils;

    @GetMapping
    public ResponseEntity<Resource> getPhoto(@PathVariable Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Студент не найден"));
        String photoPath = student.getPhotoPath();
        if (photoPath == null || photoPath.isBlank()) {
            return ResponseEntity.notFound().build();
        }
        try {
            Path path = photoService.getPhotoPath(photoPath);
            Resource resource = new UrlResource(path.toUri());
            if (resource.exists() && resource.isReadable()) {
                String contentType = "image/jpeg";
                try {
                    contentType = java.nio.file.Files.probeContentType(path);
                } catch (Exception ignored) {}
                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadPhoto(@PathVariable Long studentId,
                                         @RequestParam("file") MultipartFile file) {
        if (!securityUtils.isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        photoService.uploadPhoto(studentId, file);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    public ResponseEntity<?> deletePhoto(@PathVariable Long studentId) {
        if (!securityUtils.isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        photoService.deletePhoto(studentId);
        return ResponseEntity.noContent().build();
    }
}