package com.example.shortform.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
public class ImageFile {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "original_file_name")
    private String originalFileName;

    @Column(name = "converted_file_name")
    private String convertedFileName;

    @Column(name = "file_path")
    private String filePath;

    @Column(name = "file_size")
    private Long fileSize;


    @ManyToOne
    @JoinColumn(name = "challenge_id")
    private Challenge challenge;

    @OneToOne(orphanRemoval = true)
    @JoinColumn(name = "post_id")
    private Post post;

    public void setPost(Post post) {
        this.post = post;
    }


    public String getOriginalFilename() {
        return this.originalFileName;
    }

    public Long getSize() {
        return this.fileSize;
    }

    public void update(Long fileSize, String filePath, String originalFileName, String convertedFileName) {
        this.fileSize = fileSize;
        this.filePath = filePath;
        this.originalFileName = originalFileName;
        this.convertedFileName = convertedFileName;
    }
}

