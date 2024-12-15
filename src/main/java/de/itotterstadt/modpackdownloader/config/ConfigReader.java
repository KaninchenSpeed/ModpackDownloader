package de.itotterstadt.modpackdownloader.config;

import com.google.gson.Gson;
import de.itotterstadt.modpackdownloader.Modpackdownloader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;

public class ConfigReader {

    public ConfigReader() {

    }

    public Config readConfig() throws FileNotFoundException {
        String gamePath = (new File((new File(getClass().getProtectionDomain().getCodeSource().getLocation().toExternalForm())).getParent())).getParent().replaceAll("union:", "");
        Modpackdownloader.LOGGER.info("Minecraft Game Dir: {}", gamePath);

        File configFile = new File(gamePath + "/pack.json");
        if (!configFile.exists()) {
            throw new RuntimeException("can't read pack.json");
        }

        Scanner br = new Scanner(new FileReader(gamePath + "/pack.json"));
        StringBuilder sb = new StringBuilder();
        while (br.hasNextLine()) {
            sb.append(br.nextLine());
        }
        br.close();
        String deleteStr = sb.toString();
        Gson gson = new Gson();
        Config config = gson.fromJson(deleteStr, Config.class);
        config.gamePath = gamePath;
        return config;
    }

}
