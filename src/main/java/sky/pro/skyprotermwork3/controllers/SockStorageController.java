package sky.pro.skyprotermwork3.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.models.SpecVersion;
import org.everit.json.schema.Schema;
import org.everit.json.schema.ValidationException;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sky.pro.skyprotermwork3.model.Color;
import sky.pro.skyprotermwork3.model.Size;
import sky.pro.skyprotermwork3.model.Socks;
import sky.pro.skyprotermwork3.services.SockStorageService;

import javax.xml.validation.SchemaFactory;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;

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
    public ResponseEntity addSocks(@RequestBody Socks socks) {
            if (jsonBodyIsValidAgainstSchema(socks)) {
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

    @GetMapping("/minCotton")
    @Operation(summary = "Здесь можно получить количество носков по заданным параметрам(минимальное содержание хлопка)")
    public ResponseEntity getSocksByParamCottonMin(@RequestParam Color color,
                                                   @RequestParam Size size,
                                                   @RequestParam int cottonMin) {
        if (cottonMin > 0) {
            return ResponseEntity.ok().body(sockStorageService.getSocksByParamCottonMin(color, size, cottonMin));
        }
        return ResponseEntity.badRequest().build();
    }

    @GetMapping("/maxCotton")
    @Operation(summary = "Здесь можно получить количество носков по заданным параметрам(максимальное содержание хлопка)")

    public ResponseEntity getSocksByParamCottonMax(@RequestParam Color color,
                                                   @RequestParam Size size,
                                                   @RequestParam int cottonMax) {
        if (cottonMax > 0) {
            return ResponseEntity.ok().body(sockStorageService.getSocksByParamCottonMax(color, size, cottonMax));
        }
        return ResponseEntity.badRequest().build();
    }

    @GetMapping
    @Operation(summary = "Здесь можно получить список всех носков на складе(json)")
    public ResponseEntity<HashMap> getAllSocks() {
        return ResponseEntity.ok().body(sockStorageService.getSocksStorage());
    }

    @DeleteMapping
    @Operation(summary = "Здесь можно списать бракованные носки со склада")
    public ResponseEntity writeOffSocks(@RequestBody Socks socks) {
        if (!sockStorageService.decreaseSocksQty(socks)) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().build();
    }
    private boolean jsonIsValidAgainstSchema(Socks socks) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        String socksJsonStr = objectMapper.writeValueAsString(socks);
        String socksSchemaStr = Files.readString(Path.of(schemaPath, schemaName));
        JSONObject socksJsonObj = new JSONObject(socksJsonStr);
        JSONObject socksSchema = new JSONObject(socksSchemaStr);
        Schema schema = SchemaLoader.load(socksSchema);
        try{
            schema.validate(socksJsonObj);
            return true;
        }catch (ValidationException e){
            e.printStackTrace();
            return false;
        }

    }

    private boolean jsonBodyIsValidAgainstSchema(Socks socks) {
        File schemaFile = new File("C:\\Users\\rus\\IdeaProjects\\SkyproTermwork3\\src\\main\\resources\\socksSchema.json");
        File socksJson = new File("C:\\Users\\rus\\IdeaProjects\\SkyproTermwork3\\src\\main\\resources\\socks.json");
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            objectMapper.writeValue(socksJson, socks);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        try {
            if(ValidationUtils.isJsonValid(schemaFile,socksJson)){
                return true;
            }
        } catch (ProcessingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return false;

    }


}
