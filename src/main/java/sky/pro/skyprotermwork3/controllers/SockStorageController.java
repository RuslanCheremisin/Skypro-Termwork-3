package sky.pro.skyprotermwork3.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sky.pro.skyprotermwork3.exceptions.IncorrectSockDataEntryException;
import sky.pro.skyprotermwork3.model.Color;
import sky.pro.skyprotermwork3.model.Size;
import sky.pro.skyprotermwork3.model.Socks;
import sky.pro.skyprotermwork3.services.SockStorageService;
import sky.pro.skyprotermwork3.services.SocksFileService;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/socks")
@Tag(name = "Склад носков", description = "Здесь вы можете произвести операции с парами носков различного цвета, размера и состава ткани")
public class SockStorageController {
    private SockStorageService sockStorageService;
    private SocksFileService socksFileService;
    @Value("${pathToSocksJsonSchema}")
    private String schemaPath;
    @Value("${nameOfSocksJsonSchema}")
    private String schemaName;
    @Value("${pathToSocksJson}")
    private String socksJsonPath;
    @Value("${nameOfSocksJson}")
    private String socksJsonName;

    public SockStorageController(SockStorageService sockServiceService, SocksFileService socksFileService) {
        this.sockStorageService = sockServiceService;
        this.socksFileService = socksFileService;
    }

    @PostMapping
    @Operation(summary = "Здесь можно отгрузить носки на склад")
    public ResponseEntity addSocks(@RequestBody Socks socks) throws IOException {
        if (jsonIsValid(socks)) {
            sockStorageService.addSocks(socks);
            return ResponseEntity.ok().build();
        }
        throw new IncorrectSockDataEntryException("json does not match with schema");
//        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @PutMapping
    @Operation(summary = "Здесь можно отправить носки со склада")
    public ResponseEntity releaseSocks(@RequestBody Socks socks) {
        try {
            if(jsonIsValid(socks)){
                if (!sockStorageService.decreaseSocksQty(socks)) {
                    return ResponseEntity.badRequest().build();
                }
                return ResponseEntity.ok().build();
            }
        } catch (IOException e) {
            throw new IncorrectSockDataEntryException("");
        }
        return ResponseEntity.noContent().build();
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

    @GetMapping("/export")
    public ResponseEntity<InputStreamResource> downloadSockStorage(){
        File file = socksFileService.readFile();
        InputStreamResource resource = null;
        if (file.exists()){
            try{
                resource = new InputStreamResource(new FileInputStream(file));
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
            return ResponseEntity.ok()
                    .contentLength(file.length())
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"sockStorage.json\"")
                    .body(resource);
        }
        return ResponseEntity.noContent().build();

    }
    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> uploadSockStorage(@RequestParam MultipartFile sockStorageFile){
        socksFileService.cleanFile();
        try (FileOutputStream fos = new FileOutputStream(socksFileService.readFile())) {
            IOUtils.copy(sockStorageFile.getInputStream(), fos);
            return ResponseEntity.ok().build();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
