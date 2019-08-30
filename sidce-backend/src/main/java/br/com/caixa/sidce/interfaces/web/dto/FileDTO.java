package br.com.caixa.sidce.interfaces.web.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FileDTO {
    private String fileName;
    private String fileDownloadUri;
    private String fileType;
    private long size;
    private String step;

    public FileDTO(String fileName, String fileDownloadUri, String fileType, long size) {
        this.fileName = fileName;
        this.fileDownloadUri = fileDownloadUri;
        this.fileType = fileType;
        this.size = size;
    }
    public FileDTO(String fileName, String fileDownloadUri, String step) {
    	this.fileName = fileName;
    	this.fileDownloadUri = fileDownloadUri;
    	this.step = step;

    }
}
