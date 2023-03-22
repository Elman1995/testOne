package com.sesh.testOne;

import com.sesh.testOne.service.Impl.SortingLargeFileServiceImpl;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class TestOneApplication {

	private final SortingLargeFileServiceImpl sortingLargeFileService;

	public TestOneApplication(SortingLargeFileServiceImpl sortingLargeFileService) {
		this.sortingLargeFileService = sortingLargeFileService;
	}

	public static void main(String[] args) {
		SpringApplication.run(TestOneApplication.class);
	}

	@Bean
	public CommandLineRunner CommandLineRunnerBean() {
		return (args) -> {
			sortingLargeFileService.creatingFileAndSortingFile();
		};
	}

}
