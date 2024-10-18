package com.fazziclay.fclaypersonstatusclient;

import com.fazziclay.fclaypersonstatusclient.linux.LinuxSong;
import com.fazziclay.fclaypersonstatusclient.linux.SoundMatcher;
import com.fazziclay.fclaysystem.personstatus.api.dto.PlaybackDto;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {
    private static final Gson GSON = new Gson();
    private static FClayClient client;

    public static void main(String[] args) throws InterruptedException, IOException {
        JsonObject config = GSON.fromJson(Files.readString(Path.of(args[0])), JsonObject.class);
        loadConfig(config);

        while (true) {
            LinuxSong currentLinuxSong = SoundMatcher.getCurrentSong();
            PlaybackDto playbackDto = currentLinuxSong == null ? null : currentLinuxSong.asPlaybackDto();
            client.postSong(playbackDto);
            Thread.sleep(2000);
        }
    }

    private static void loadConfig(JsonObject config) {
        String accessToken = config.get("accessToken").getAsString();
        String baseUrl = config.get("baseUrl").getAsString();
        String blackListPath = config.get("blacklist").getAsString();
        String[] allowedPrograms = config.get("allowedPrograms").getAsString().split(",");

        // setting client
        client = new FClayClient(baseUrl, accessToken);
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

}