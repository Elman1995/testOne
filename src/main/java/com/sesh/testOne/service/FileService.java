package com.sesh.testOne.service;

import java.io.File;

public interface FileService {
    File creatingAndGenerateFile(String fileSavePath);

    File resetTheFile(String fileSavePath);

    File createFile(String fileSavePath);

    void removeFile(String filePath);

    void addLineToFile(String addLine, String filePath, boolean append);
}
