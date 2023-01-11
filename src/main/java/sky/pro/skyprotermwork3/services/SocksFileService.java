package sky.pro.skyprotermwork3.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
@Service
public class SocksFileService {
    @Value("${pathToSocksJson}")
    private String socksJsonPath;
    @Value("${nameOfSockStorageJson}")
    private String socksJsonName;
    public boolean saveFile(String json) {
        try {
            cleanFile();
            Files.writeString(Path.of(socksJsonPath, socksJsonName), json);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public String readFile() {
        String json;
        try {
            json = Files.readString(Path.of(socksJsonPath, socksJsonName));
            return json;
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }

    }

    public void cleanFile() {
        try {
            Files.deleteIfExists(Path.of(socksJsonPath, socksJsonName));
            Files.createFile(Path.of(socksJsonPath, socksJsonName));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
