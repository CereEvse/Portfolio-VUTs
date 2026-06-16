package ru.accouting.student.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import ru.accouting.student.model.Student;
import ru.accouting.student.repository.StudentRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class StudentPhotoService {

    private final StudentRepository studentRepository;

    @Value("${app.photos.upload-dir:uploads/photos}")
    private String photosDir;

    @Transactional
    public void uploadPhoto(Long studentId, MultipartFile file) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Студент не найден"));
        deleteOldPhoto(student.getPhotoPath());
        String newFilename = saveFile(file);
        student.setPhotoPath(newFilename);
        studentRepository.save(student);
    }

    @Transactional
    public void deletePhoto(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Студент не найден"));
        deleteOldPhoto(student.getPhotoPath());
        student.setPhotoPath(null);
        studentRepository.save(student);
    }

    public Path getPhotoPath(String relativePath) {
        return Paths.get(photosDir).resolve(relativePath).normalize();
    }

    private String saveFile(MultipartFile file) {
        try {
            Path uploadPath = Paths.get(photosDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String newFilename = UUID.randomUUID() + extension;
            Path targetPath = uploadPath.resolve(newFilename);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
            return newFilename;
        } catch (IOException e) {
            log.error("Ошибка сохранения фото", e);
            throw new RuntimeException("Не удалось сохранить фото", e);
        }
    }

    private void deleteOldPhoto(String filename) {
        if (filename != null && !filename.isBlank()) {
            try {
                Path filePath = Paths.get(photosDir).resolve(filename);
                Files.deleteIfExists(filePath);
            } catch (IOException e) {
                log.warn("Не удалось удалить старое фото {}: {}", filename, e.getMessage());
            }
        }
    }
}