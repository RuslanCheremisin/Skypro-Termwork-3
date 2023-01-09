package sky.pro.skyprotermwork3.services;

import org.springframework.stereotype.Service;
import sky.pro.skyprotermwork3.model.Color;
import sky.pro.skyprotermwork3.model.Size;
import sky.pro.skyprotermwork3.model.Socks;

import java.util.HashMap;

@Service
public class SockStorageService {
    private final HashMap<Long, Socks> socksStorage = new HashMap<>();
    private long socksBatchId = 0;

    public boolean addSocks(Socks socks) {
        int qty;
        if (socks.getCottonPercentage() < 0 && socks.getCottonPercentage() > 100) {
            return false;
        }
        if (socks.getQuantity() <= 0) {
            return false;
        } else {
            qty = socks.getQuantity();
        }

        if (socksStorage.size() == 0) {
            socksStorage.put(socksBatchId++, socks);
        } else {
            if (socksStorage.containsValue(socks)) {
                for (Socks socksValue : socksStorage.values()) {
                    if (socksValue.equals(socks)) {
                        socksValue.setQuantity(socksValue.getQuantity() + qty);
                    }
                }
            } else {
                socksStorage.put(socksBatchId++, socks);
            }
        }
        return true;
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
        return false;
    }

    public int getSocksByParamCottonMin(Color color, Size size, int minCottonPercentage) {
        int qty = 0;
        for (Socks socks : socksStorage.values()) {
            if (socks.getColor() == color && socks.getSize() == size && socks.getCottonPercentage() >= minCottonPercentage) {
                qty += socks.getQuantity();
            }
        }
        return qty;
    }

    public int getSocksByParamCottonMax(Color color, Size size, int maxCottonPercentage) {
        int qty = 0;
        for (Socks socks : socksStorage.values()) {
            if (socks.getColor() == color && socks.getSize() == size && socks.getCottonPercentage() <= maxCottonPercentage) {
                qty += socks.getQuantity();
            }
        }
        return qty;
    }

    public HashMap<Long, Socks> getSocksStorage() {
        return socksStorage;
    }
//    public
}


