package sky.pro.skyprotermwork3.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sky.pro.skyprotermwork3.model.Color;
import sky.pro.skyprotermwork3.model.Size;
import sky.pro.skyprotermwork3.model.Socks;
import sky.pro.skyprotermwork3.services.SockStorageService;

import java.util.HashMap;

@RestController
@RequestMapping("/api/socks")
public class SockStorageController {
    SockStorageService sockStorageService;

    public SockStorageController(SockStorageService sockServiceService) {
        this.sockStorageService = sockServiceService;
    }

    @PostMapping
    public ResponseEntity addSocks(@RequestBody Socks socks) {
        if (!sockStorageService.addSocks(socks)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok().build();
    }

    @PutMapping
    public ResponseEntity releaseSocks(@RequestBody Socks socks){
        if (!sockStorageService.decreaseSocksQty(socks)) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().build();
    }
    @GetMapping("/minCotton")
    public ResponseEntity getSocksByParamCottonMin(@RequestParam Color color,
                                                   @RequestParam Size size,
                                                   @RequestParam int cottonMin){
        if (cottonMin>0){
            return ResponseEntity.ok().body(sockStorageService.getSocksByParamCottonMin(color, size, cottonMin));
        }
        return ResponseEntity.badRequest().build();
    }
    @GetMapping("/maxCotton")
    public ResponseEntity getSocksByParamCottonMax(@RequestParam Color color,
                                                   @RequestParam Size size,
                                                   @RequestParam int cottonMax){
        if (cottonMax>0){
            return ResponseEntity.ok().body(sockStorageService.getSocksByParamCottonMax(color, size, cottonMax));
        }
        return ResponseEntity.badRequest().build();
    }

    @GetMapping
    public ResponseEntity<HashMap> getAllSocks(){
        return ResponseEntity.ok().body(sockStorageService.getSocksStorage());
    }
    @DeleteMapping
    public ResponseEntity writeOffSocks(@RequestBody Socks socks){
        if (!sockStorageService.decreaseSocksQty(socks)) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().build();
    }
}
