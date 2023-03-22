package com.sesh.testOne.service.Impl;

import com.sesh.testOne.config.ApplicationConfig;
import com.sesh.testOne.service.FileService;
import com.sesh.testOne.service.SortingLargeFileService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SortingLargeFileServiceImpl implements SortingLargeFileService {

    final FileService fileService;
    final ApplicationConfig applicationConfig;

    public void creatingFileAndSortingFile() {
        log.info("Запуск программы");
        File newFile = fileService.creatingAndGenerateFile(applicationConfig.getOriginalFile());
        sortTheFile(newFile);
        log.info("Завершение программы");
    }

    @Override
    @SneakyThrows
    public void sortTheFile(File file) {
        var passedLetters = new HashMap<String, Integer>();
        var lineLimit = applicationConfig.getLineLimit(); // максимально разрешенное количество строк в файле
        var amountLine = applicationConfig.getAmountLine(); // количество строк к файле
        var sizePartLine = applicationConfig.getSortSize(); // минимальное количество символов в одной строке
        var pathSortedFile = applicationConfig.getSortedFile();// путь по которому сохранится отсортированный файл
        var pathToTheFile = file.getAbsolutePath();

        if (amountLine < lineLimit) {
            log.info("Переданный файл меньше, чем '{}'. Можно отсортировать", lineLimit);
            sortLineToFile(pathToTheFile);
        } else {
            log.info("Переданный файл превышает допустимый размер '{}'. Переходим к разделению файла", lineLimit);
            fileSeparator(pathToTheFile, passedLetters, sizePartLine);
        }
        log.info("Добиваемся чтобы все файлы были допустимого размера");
        while (true) {
            sizePartLine = sizePartLine + 2;
            var counter = passedLetters.keySet().size();
            for (Map.Entry<String, Integer> partLine : passedLetters.entrySet()) {
                if (partLine.getValue() > lineLimit) {
                    var fileName = partLine.getKey().toLowerCase(Locale.ROOT);
                    fileSeparator(applicationConfig.getHelpersFilePath() + fileName, passedLetters, sizePartLine);
                } else {
                    counter--;
                }
            }
            if (counter == 0) {
                log.info("Файл разделен, новые файлы созданы");
                break;
            }
        }
        log.info("Сортируем строки в мелких файлах");
        passedLetters.keySet().forEach(s ->
                sortLineToFile(applicationConfig.getHelpersFilePath() + s + ".txt"));

        log.info("Создаем итоговый файл");
        fileService.resetTheFile(pathSortedFile);
        log.info("Сортируем файлы и помещаем в итоговый файл строки");
        passedLetters.keySet().stream().sorted().forEach(e -> {
            writeFromFileToFile(pathSortedFile, e);
            fileService.removeFile(applicationConfig.getHelpersFilePath() + e + ".txt");
        });
    }

    @SneakyThrows
    private void writeFromFileToFile(String pathFinalFile, String pathFileSource) {
        log.debug("Заполняем итоговый файл");
        var partFileName = applicationConfig.getHelpersFilePath() + pathFileSource + ".txt";
        try (var fileReader = new FileReader(partFileName);
             var reader = new BufferedReader(fileReader)) {
            reader.lines().forEach(l -> fileService.addLineToFile(l, pathFinalFile, true));
        }
    }

    @SneakyThrows
    private void sortLineToFile(String pathToFile) {
        List<String> listForSort;
        log.debug("Считываем файл для сортировки");
        try (var fileReader = new FileReader(pathToFile);
             var reader = new BufferedReader(fileReader)) {
            listForSort = reader.lines().sorted(String.CASE_INSENSITIVE_ORDER).collect(Collectors.toList());
        }
        var length = pathToFile.split("/").length;
        var fileName = pathToFile.split("/")[length - 1];
        fileService.addLineToFile(listForSort.get(0), applicationConfig.getHelpersFilePath() + fileName, false);
        for (int i = 1; i < listForSort.size(); i++) {
            fileService.addLineToFile(listForSort.get(i), applicationConfig.getHelpersFilePath() + fileName, true);
        }
    }

    @SneakyThrows
    private void fileSeparator(String pathToFile, Map<String, Integer> lettersPassed, int sizePartLine) {
        log.info("Разделяем файл на более мелкие части");
        try (var fileReader = new FileReader(pathToFile);
             var reader = new BufferedReader(fileReader)) {
            log.info("Сравниваем начало строк");
            String line;
            while ((line = reader.readLine()) != null) {
                String helpersFilePath = applicationConfig.getHelpersFilePath();
                if (line.length() - 1 < sizePartLine) {
                    lettersPassed.put(line.toLowerCase(Locale.ROOT), +1);
                    String fileName = line.toLowerCase(Locale.ROOT) + ".txt";
                    fileService.createFile(helpersFilePath + fileName);
                    fileService.addLineToFile(line, helpersFilePath + fileName, true);
                    continue;
                }
                String startLine = line.substring(0, sizePartLine);
                if (lettersPassed.containsKey(startLine.toLowerCase(Locale.ROOT))) {
                    log.debug("Добавили часть строки '{}' в файл", startLine);
                    fileService.addLineToFile(line,
                            helpersFilePath + startLine.toLowerCase(Locale.ROOT) + ".txt", true);
                    lettersPassed.put(startLine.toLowerCase(Locale.ROOT),
                            (lettersPassed.get(startLine.toLowerCase(Locale.ROOT))) + 1);
                } else {
                    log.debug("Обновили список уникальных строк на '{}'", startLine);
                    lettersPassed.put(startLine.toLowerCase(Locale.ROOT), 1);
                    startLine = startLine.toLowerCase(Locale.ROOT) + ".txt";
                    log.debug("Создаем новый файл с именем '{}'", startLine);
                    fileService.createFile(helpersFilePath + startLine);
                    log.debug("Записываем строку в новый файл");
                    fileService.addLineToFile(line, helpersFilePath + startLine, true);
                }
            }
        }
    }
}
