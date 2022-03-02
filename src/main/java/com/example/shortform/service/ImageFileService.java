package com.example.shortform.service;

import com.example.shortform.domain.Challenge;
import com.example.shortform.domain.ImageFile;
import com.example.shortform.domain.Post;
import com.example.shortform.dto.request.ImageFileRequestDto;
import com.example.shortform.repository.ImageFileRepository;
import com.example.shortform.util.S3Uploader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ImageFileService {
    private final ImageFileRepository imageFileRepository;
    private final S3Uploader s3Uploader;

    @Autowired
    public ImageFileService(ImageFileRepository imageFileRepository, S3Uploader s3Uploader) {
        this.imageFileRepository = imageFileRepository;
        this.s3Uploader = s3Uploader;
    }

    @Transactional
    public List<ImageFile> uploadImage(List<MultipartFile> multipartFileList, Challenge challenge) throws IOException {
        List<ImageFile> imageFileList = new ArrayList<>();
        if (challenge.getImageFiles() != null) {
            imageFileRepository.deleteAllByChallenge(challenge);
        }

        for (MultipartFile multipartFile : multipartFileList) {
            String originalFileName = multipartFile.getOriginalFilename();
            String convertedFileName = UUID.randomUUID() + originalFileName;
            String filePath = s3Uploader.upload(multipartFile, convertedFileName);

            ImageFileRequestDto imageFileRequestDto = new ImageFileRequestDto();
            imageFileRequestDto.setOriginalFileName(originalFileName);
            imageFileRequestDto.setConvertedFileName(convertedFileName);
            imageFileRequestDto.setFilePath(String.valueOf(filePath));
            imageFileRequestDto.setFileSize(multipartFile.getSize());

            ImageFile imageFile = imageFileRepository.save(imageFileRequestDto.toEntity(challenge));

            imageFileList.add(imageFile);
        }

        return imageFileList;
    }

    public ImageFile upload(MultipartFile multipartFile, Post post) throws IOException {
        String originalFileName = multipartFile.getOriginalFilename();
        String convertedFileName = UUID.randomUUID() + originalFileName;
        String filePath = s3Uploader.upload(multipartFile, convertedFileName);

        ImageFileRequestDto imageFileRequestDto = new ImageFileRequestDto();
        imageFileRequestDto.setOriginalFileName(originalFileName);
        imageFileRequestDto.setConvertedFileName(convertedFileName);
        imageFileRequestDto.setFilePath(String.valueOf(filePath));
        imageFileRequestDto.setFileSize(multipartFile.getSize());

        ImageFile imageFile = imageFileRepository.save(imageFileRequestDto.toEntity(post));

        return imageFile;
    }

//    public List<ImageFile> modifyImage(List<MultipartFile> multipartFileList, Challenge challenge) {
//        List<ImageFile> imageFileList = new ArrayList<>();
//        for (MultipartFile multipartFile : multipartFileList) {
//            String originalFileName = multipartFile.getOriginalFilename();
//            String convertedFileName = UUID.randomUUID() + originalFileName;
//            String filePath = s3Uploader.upload(multipartFile, convertedFileName);
//
//            ImageFileRequestDto imageFileRequestDto = new ImageFileRequestDto();
//            imageFileRequestDto.setOriginalFileName(originalFileName);
//            imageFileRequestDto.setConvertedFileName(convertedFileName);
//            imageFileRequestDto.setFilePath(String.valueOf(filePath));
//            imageFileRequestDto.setFileSize(multipartFile.getSize());
//
//            ImageFile imageFile = imageFileRepository.save(imageFileRequestDto.toEntity(challenge));
//
//            imageFileList.add(imageFile);
//        }
//
//        return imageFileList;
//    }
}
