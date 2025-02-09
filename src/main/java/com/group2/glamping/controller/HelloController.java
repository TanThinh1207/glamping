package com.group2.glamping.controller;

import com.group2.glamping.model.dto.requests.HelloRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/demo")
@Tag(name = "Hello API", description = "Demo Api to return Hello World")
public class HelloController {

    @GetMapping("/hello")
    @Operation(
            summary = "Return Hello World",
            description = "Return a String Hello World to check if server work properly",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Return Hello World successfully")
            }
    )
    public ResponseEntity<?> hello() {
        return new ResponseEntity<>("Hello World!", HttpStatus.OK);
    }

    @GetMapping("/secured")
    public String secured() {
        return "Hello from secured endpoint!";
    }


    @GetMapping("/hello/request")
    @Operation(
            summary = "Return Hello World with request",
            description = "Return a String Hello World with request from user",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Return Successfully")
            }
    )
    public ResponseEntity<?> helloWithRequest(@Valid HelloRequest helloRequest) {
        return new ResponseEntity<>("Hello World! " + helloRequest.getMessage(), HttpStatus.OK);
    }

    @GetMapping("/hello/parameter")
    @Operation(
            summary = "Return Hello World with parameter",
            description = "Return a String Hello World with parameter from user",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Return Successfully")
            }
    )
    public ResponseEntity<?> helloWithParameter(
            @Parameter(description = "name of user", example = "thinh")
            @RequestParam String name
    ) {
        return new ResponseEntity<>("Hello World! " + name, HttpStatus.OK);
    }
}
