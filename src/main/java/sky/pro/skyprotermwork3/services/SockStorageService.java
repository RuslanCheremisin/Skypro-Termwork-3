package sky.pro.skyprotermwork3.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import sky.pro.skyprotermwork3.model.Color;
import sky.pro.skyprotermwork3.model.Size;
import sky.pro.skyprotermwork3.model.Socks;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class SockStorageService {
    private HashMap<Socks, Long> socksStorage = new HashMap<>();
    private long socksBatchId = 0;
    private final SocksFileService socksFileService;

    public SockStorageService(SocksFileService socksFileService) {
        this.socksFileService = socksFileService;
    }

    public void addSocks(Socks socks) {
        readFile();
        if (socksStorage.size() == 0) {
            socksStorage.put(socks, socksBatchId++);
        } else {
            if (socksStorage.containsKey(socks)) {
                for (Socks socksKey : socksStorage.keySet()) {
                    if (socksKey.equals(socks)) {
                        socksKey.setQuantity(socksKey.getQuantity() + socks.getQuantity());
                    }
                }
            } else {
                socksStorage.put(socks, socksBatchId++);
            }
        }
        saveFile();
    }

    public boolean decreaseSocksQty(Socks socks) {
        if (socksStorage.containsKey(socks)) {
            for (Socks socksKey : socksStorage.keySet()) {
                if (socksKey.equals(socks) && socksKey.getQuantity() >= socks.getQuantity()) {
                    socksKey.setQuantity(socksKey.getQuantity() - socks.getQuantity());
                    return true;
                }
            }
        }
        saveFile();
        return false;
    }

    public int getSocksByParam(Color color, Size size, Integer minCotton, Integer maxCotton) {
        readFile();
        int qty = 0;
        for (Socks socks : socksStorage.keySet()) {
            if (color != null && !color.equals(socks.getColor())) {
                continue;
            }
            if (size != null && !size.equals(socks.getSize())) {
                continue;
            }
            if (minCotton != null && minCotton > socks.getCotton()) {
                continue;
            }
            if (maxCotton != null && maxCotton < socks.getCotton()) {
                continue;
            }
            qty += socks.getQuantity();

        }
        return qty;
    }

    private void saveFile() {
        List<Socks> socksList = new ArrayList<>();
        for (Socks socks : socksStorage.keySet()) {
            socksList.add(new Socks(socks.getColor(), socks.getSize(), socks.getCotton(), socks.getQuantity()));
        }
        try {
            String socksJson = new ObjectMapper().writeValueAsString(socksList);
            socksFileService.saveFile(socksJson);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

    }

    private void readFile() {
        File sockStorageJson = socksFileService.readFile();
        try {
            List<Socks> socksList = new ObjectMapper().readValue(sockStorageJson, new TypeReference<List<Socks>>() {
            });
            for (Socks socks : socksList) {
                socksStorage.put(socks, socksBatchId++);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }




}


