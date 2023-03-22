package com.sesh.testOne.service.Impl;

import com.sesh.testOne.config.ApplicationConfig;
import com.sesh.testOne.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileServiceImpl implements FileService {

    final ApplicationConfig applicationConfig;

    @Override
    @SneakyThrows
    public File creatingAndGenerateFile(String fileSavePath) {
        log.info("Начало создания нового файла и генерации текста");
        File newFile = resetTheFile(fileSavePath);
        log.info("Новый файл создан по пути: " + fileSavePath);
        IntStream.range(0, applicationConfig.getAmountLine()).forEach(e -> addLineToFile(
                RandomStringUtils.randomAlphanumeric(1, applicationConfig.getMaxLineLength()),
                fileSavePath, true));
        log.info("Сгенерирован текст в новый файл");
        log.info("Конец создания нового файла и генерации текста");
        return newFile;
    }

    @Override
    @SneakyThrows
    public File resetTheFile(String fileSavePath) {
        if (Files.exists(Path.of(fileSavePath))) {
            removeFile(fileSavePath);
        }
        var newFile = new File(fileSavePath);
        newFile.createNewFile();
        log.info("Файл пересоздан по пути: " + fileSavePath);
        return newFile;
    }

    @Override
    @SneakyThrows
    public File createFile(String fileSavePath) {
        var file = new File(fileSavePath);
        if (file.createNewFile()) {
            log.debug("Новый файл создан по пути: " + fileSavePath);
        }
        return file;
    }

    @Override
    @SneakyThrows
    public void removeFile(String filePath) {
        Files.deleteIfExists(Paths.get(filePath));
        log.debug("Файл: '{}' удален", filePath);
    }

    @Override
    @SneakyThrows
    public void addLineToFile(String addLine, String filePath, boolean append) {
        try (var file = new FileWriter(filePath, append);
             var bufferWriter = new BufferedWriter(file)) {
            bufferWriter.write(addLine + "\n");
            log.debug("Строка '{}' успешно записана в файл", addLine);
        }
    }
}
