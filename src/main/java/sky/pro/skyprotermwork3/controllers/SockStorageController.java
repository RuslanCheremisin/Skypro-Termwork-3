package sky.pro.skyprotermwork3.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sky.pro.skyprotermwork3.exceptions.IncorrectSockDataEntryException;
import sky.pro.skyprotermwork3.model.Color;
import sky.pro.skyprotermwork3.model.Size;
import sky.pro.skyprotermwork3.model.Socks;
import sky.pro.skyprotermwork3.services.SockStorageService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/socks")
@Tag(name = "Склад носков", description = "Здесь вы можете произвести операции с парами носков различного цвета, размера и состава ткани")
public class SockStorageController {
    private SockStorageService sockStorageService;
    @Value("${pathToSocksJsonSchema}")
    private String schemaPath;
    @Value("${nameOfSocksJsonSchema}")
    private String schemaName;
    @Value("${pathToSocksJson}")
    private String socksJsonPath;
    @Value("${nameOfSocksJson}")
    private String socksJsonName;

    public SockStorageController(SockStorageService sockServiceService) {
        this.sockStorageService = sockServiceService;
    }

    @PostMapping
    @Operation(summary = "Здесь можно отгрузить носки на склад")
    public ResponseEntity addSocks(@RequestBody Socks socks) throws IOException {
        if (jsonIsValid(socks)) {
            sockStorageService.addSocks(socks);
            return ResponseEntity.ok().build();
        }
        throw new IllegalArgumentException("json does not match with schema");
//        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @PutMapping
    @Operation(summary = "Здесь можно отправить носки со склада")
    public ResponseEntity releaseSocks(@RequestBody Socks socks) {
        if (!sockStorageService.decreaseSocksQty(socks)) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping
    @Operation(summary = "Здесь можно получить количество носков фильтрацией по заданным параметрам")
    public ResponseEntity getSocks(@RequestParam(required = false, name = "color") Color color,
                                   @RequestParam(required = false, name = "size") Size size,
                                   @RequestParam(required = false, name = "cottonMin") Integer cottonMin,
                                   @RequestParam(required = false, name = "cottonMax") Integer cottonMax) {

        return ResponseEntity.ok().body(sockStorageService.getSocksByParam(color, size, cottonMin, cottonMax));

    }

    @DeleteMapping
    @Operation(summary = "Здесь можно списать бракованные носки со склада")
    public ResponseEntity writeOffSocks(@RequestBody Socks socks) {
        if (!sockStorageService.decreaseSocksQty(socks)) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().build();
    }


    private boolean jsonIsValid(Socks socks) throws IOException {
        File schemaFile = Path.of(schemaPath, schemaName).toFile();
        File socksJson = Path.of(socksJsonPath, socksJsonName).toFile();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            objectMapper.writeValue(socksJson, socks);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        try {
            if (ValidationUtils.isJsonValid(schemaFile, socksJson)) {
                return true;
            }
        } catch (ProcessingException e) {
            e.printStackTrace();
            throw new IncorrectSockDataEntryException("Некорректные данные! Проверьте вводимые данные");
        }
        return false;

    }
}
