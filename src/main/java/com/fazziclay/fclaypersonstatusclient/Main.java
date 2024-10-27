package com.fazziclay.fclaypersonstatusclient;

import com.fazziclay.fclaypersonstatusclient.linux.LinuxSong;
import com.fazziclay.fclaypersonstatusclient.linux.SoundMatcher;
import com.fazziclay.fclaysystem.personstatus.api.dto.PlaybackDto;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;
import lombok.val;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {
    private static final Gson GSON = new Gson();
    private static Tray tray;
    private static FClayClient client;
    @Getter
    private static PlaybackDto playbackDto;
    @Setter
    @Getter
    private static boolean disablePosting;
    private static boolean configLoaded;

    public static String playbackDtoString() {
        if (!isPlaying()) {
            return "No music :<";
        }
        return String.format("%s - %s", playbackDto.getArtist(), playbackDto.getTitle());
    }

    public static void main(String[] args) throws InterruptedException, IOException {
        tray = new Tray();
        tray.start();


        val thread = new Thread(() -> {
            while (true) {
                try {
                    tray.tick();
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        thread.setDaemon(true);
        thread.setName("TrayIconUpdaterThread");
        thread.start();

        while (true) {
            if (!configLoaded) {
                try {
                    JsonObject config = GSON.fromJson(Files.readString(Path.of(args[0])), JsonObject.class);
                    loadConfig(config);
                    configLoaded = true;
                    tray.setConfigLoaded(true);

                } catch (Exception ignored) {
                    Debug.debug("Can't be load config :(");
                }
            }

            if (configLoaded) {
                LinuxSong currentLinuxSong = SoundMatcher.getCurrentSong();
                playbackDto = currentLinuxSong == null ? null : currentLinuxSong.asPlaybackDto();
                if (disablePosting) {
                    client.delete();
                } else {
                    client.postSong(playbackDto);
                }
            }

            Thread.sleep(2000);
        }
    }

    private static void loadConfig(JsonObject config) {
        String accessToken = config.get("accessToken").getAsString();
        String baseUrl = config.get("baseUrl").getAsString();
        JsonElement personName = config.get("personName");
        String blackListPath = config.get("blacklist").getAsString();
        String[] allowedPrograms = config.get("allowedPrograms").getAsString().split(",");

        // setting client
        client = new FClayClient(baseUrl, personName == null ? "fazziclay" : personName.getAsString(), accessToken);
        client.setDebug(Debug.DEBUG);

        // allowed programs
        for (String program : allowedPrograms) {
            SoundMatcher.addToAllowedPrograms(program);
        }

        // blocklist
        try {
            String[] words = Files.readString(Path.of(blackListPath)).split("\n");
            for (String word : words) {
                if (word.trim().isEmpty()) continue;
                if (word.trim().startsWith("//")) continue;

                if (word.length() < 4) {
                    Debug.debug("WARN blocklist: '" + word + "': len < 4");
                }
                SoundMatcher.addToBlockList(word);
            }
            Debug.debug("Blocklist loaded: " + words.length + " items");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean isPlaying() {
        return playbackDto != null;
    }
}