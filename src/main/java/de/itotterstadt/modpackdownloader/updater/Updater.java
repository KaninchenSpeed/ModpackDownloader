package de.itotterstadt.modpackdownloader.updater;

import com.google.gson.Gson;
import de.itotterstadt.modpackdownloader.Modpackdownloader;
import de.itotterstadt.modpackdownloader.config.Config;
import de.itotterstadt.modpackdownloader.config.ConfigReader;
import de.itotterstadt.modpackdownloader.ui.RestartPopup;
import de.itotterstadt.modpackdownloader.util.UnzipUtility;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.Map;
import java.util.Scanner;

public class Updater {
    
    private String url;
    private Config config;
    
    public Updater(String url, Config config) {
        this.url = url;
        this.config = config;
    }

    public void checkForUpdates() throws IOException {
        String configString = get(url);
        Gson gson = new Gson();
        UpdateMeta meta = gson.fromJson(configString, UpdateMeta.class);

        if (config.version < meta.version) {
            updatePack(meta, config.version);
        }
    }

    private void updatePack(UpdateMeta meta, int currentVersion) throws IOException {
        System.out.println("updating pack (version " + meta.version + ")");

        if (currentVersion == -1) {
            installZip(meta.freshUrl);
        }
        for (int i = Math.max(currentVersion, 0); i < meta.version; i++) {
            installZip(meta.updateUrls[i]);
        }

        Config newCfg = new Config();
        newCfg.metaUrl = config.metaUrl;
        newCfg.version = meta.version;

        Gson gson = new Gson();
        String newCfgStr = gson.toJson(newCfg);

        File cfgFile = new File(config.gamePath + "/pack.json");
        cfgFile.delete();
        FileWriter versionWriter = new FileWriter(config.gamePath + "/pack.json");
        versionWriter.write(newCfgStr);
        versionWriter.close();


        Modpackdownloader.LOGGER.error("============== This is not a Crash ==============");
        Modpackdownloader.LOGGER.error("=========== please restart Minecraft ============");
        RestartPopup.showRestartPopup();
    }

    private void installZip(String zipUrl) throws IOException {
        downloadFile(zipUrl, config.gamePath + "/pack.zip");

        UnzipUtility unzipUtil = new UnzipUtility();
        unzipUtil.unzip(config.gamePath + "/pack.zip", config.gamePath, "delete.json");

        File deleteFile = new File(config.gamePath + "/delete.json");
        if (deleteFile.exists()) {
            Scanner br = new Scanner(new FileReader(config.gamePath + "/delete.json"));
            StringBuilder sb = new StringBuilder();
            while (br.hasNextLine()) {
                sb.append(br.nextLine());
            }
            br.close();
            String deleteStr = sb.toString();
            Gson gson = new Gson();
            String[] files = gson.fromJson(deleteStr, String[].class);
            for (String file : files) {
                File f = new File(config.gamePath + "/" + file);
                if (f.exists()) {
                    f.delete();
                }
            }
        }
        deleteFile.delete();

        unzipUtil.unzip(config.gamePath + "/pack.zip", config.gamePath);
        File zipFile = new File(config.gamePath + "/pack.zip");
        zipFile.delete();

        File deleteFile2 = new File(config.gamePath + "/delete.json");
        if (deleteFile2.exists()) {
            deleteFile2.delete();
        }

        File dlFile = new File(config.gamePath + "/download.json");
        if (dlFile.exists()) {
            Scanner br = new Scanner(new FileReader(config.gamePath + "/download.json"));
            StringBuilder sb = new StringBuilder();
            while (br.hasNextLine()) {
                sb.append(br.nextLine());
            }
            br.close();
            String downloadStr = sb.toString();
            Gson gson = new Gson();
            Map<String, String> files = (Map<String, String>) gson.fromJson(downloadStr, Map.class);
            for (String path : files.keySet()) {
                String url = files.get(path);
                Modpackdownloader.LOGGER.info("downloading " + url + " to " + path);
                downloadFile(url, config.gamePath + "/" + path);
            }

            dlFile.delete();
        }
    }

    private void downloadFile(String dlUrl, String path) throws IOException {
        URL url = new URL(dlUrl);
        ReadableByteChannel readableByteChannel = Channels.newChannel(url.openStream());
        FileOutputStream fileOutputStream = new FileOutputStream(path);
        FileChannel fileChannel = fileOutputStream.getChannel();
        fileChannel.transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
    }

    private String get(String urlToRead) throws IOException {
        StringBuilder result = new StringBuilder();
        URL url = new URL(urlToRead);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            for (String line; (line = reader.readLine()) != null; ) {
                result.append(line);
            }
        }
        return result.toString();
    }
    
}
