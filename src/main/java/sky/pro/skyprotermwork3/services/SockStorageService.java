package sky.pro.skyprotermwork3.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import sky.pro.skyprotermwork3.model.Color;
import sky.pro.skyprotermwork3.model.Size;
import sky.pro.skyprotermwork3.model.Socks;


import java.util.HashMap;

@Service
public class SockStorageService {
    private HashMap<Long, Socks> socksStorage = new HashMap<>();
    private long socksBatchId = 0;
    private final SocksFileService socksFileService;

    public SockStorageService(SocksFileService socksFileService) {
        this.socksFileService = socksFileService;
    }

    public void addSocks(Socks socks) {
//        int qty;
//        if (socks.getCottonPercentage() < 0 || socks.getCottonPercentage() > 100) {
//            return false;
//        }
//        if (socks.getQuantity() <= 0) {
//            return false;
//        } else {
//            qty = socks.getQuantity();
//        }

        if (socksStorage.size() == 0) {
            socksStorage.put(socksBatchId++, socks);
        } else {
            if (socksStorage.containsValue(socks)) {
                for (Socks socksValue : socksStorage.values()) {
                    if (socksValue.equals(socks)) {
                        socksValue.setQuantity(socksValue.getQuantity() + socks.getQuantity());
                    }
                }
            } else {
                socksStorage.put(socksBatchId++, socks);
            }
        }
        saveFile();

    }

    public boolean decreaseSocksQty(Socks socks) {
        if (socksStorage.containsValue(socks)) {
            for (Socks socksValue : socksStorage.values()) {
                if (socksValue.equals(socks) && socksValue.getQuantity() >= socks.getQuantity()) {
                    socksValue.setQuantity(socksValue.getQuantity() - socks.getQuantity());
                    return true;
                }
            }
        }
        saveFile();
        return false;
    }

    public int getSocksByParamCottonMin(Color color, Size size, int minCotton) {
        int qty = 0;
        for (Socks socks : socksStorage.values()) {
            if (socks.getColor() == color && socks.getSize() == size && socks.getCotton() >= minCotton) {
                qty += socks.getQuantity();
            }
        }
        return qty;
    }

    public int getSocksByParamCottonMax(Color color, Size size, int maxCotton) {
        int qty = 0;
        for (Socks socks : socksStorage.values()) {
            if (socks.getColor() == color && socks.getSize() == size && socks.getCotton() <= maxCotton) {
                qty += socks.getQuantity();
            }
        }
        return qty;
    }

    public HashMap<Long, Socks> getSocksStorage() {
        return socksStorage;
    }

    public void saveFile() {
        try {
            String socksJson = new ObjectMapper().writeValueAsString(socksStorage);
            socksFileService.saveFile(socksJson);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

    }

    private void readFile() {
        String socksJson = socksFileService.readFile();
        try {
            socksStorage = new ObjectMapper().readValue(socksJson, new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }


}


