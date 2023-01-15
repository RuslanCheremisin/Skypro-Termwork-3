package sky.pro.skyprotermwork3.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class SocksFileService {
    @Value("${pathToSockStorageJson}")
    private String sockStorageJsonPath;
    @Value("${nameOfSockStorageJson}")
    private String sockStorageJsonName;

    public boolean saveFile(String json) {
        try {
            cleanFile();
            Files.writeString(Path.of(sockStorageJsonPath, sockStorageJsonName), json);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public File readFile() {
        File json = Path.of(sockStorageJsonPath, sockStorageJsonName).toFile();
        return json;

    }

    public void cleanFile() {
        try {
            Files.deleteIfExists(Path.of(sockStorageJsonPath, sockStorageJsonName));
            Files.createFile(Path.of(sockStorageJsonPath, sockStorageJsonName));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
