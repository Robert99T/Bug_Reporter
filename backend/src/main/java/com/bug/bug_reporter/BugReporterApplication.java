package com.bug.bug_reporter;

import com.bug.bug_reporter.dto.CommentResponse;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BugReporterApplication {

	public static void main(String[] args) {

		SpringApplication.run(BugReporterApplication.class, args);

		CommentResponse commentResponse = new CommentResponse();

	}

}
