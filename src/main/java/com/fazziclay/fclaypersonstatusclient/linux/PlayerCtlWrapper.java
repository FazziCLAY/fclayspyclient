package com.fazziclay.fclaypersonstatusclient.linux;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
public class PlayerCtlWrapper {
    private final List<LinuxSong> linuxSongs;

    public static PlayerCtlWrapper create() throws IOException {
        var output = OsCommandExecutor.execute("playerctl", "-a", "--format={{playerName}}\t\t|\t\t{{position}}\t\t|\t\t{{status}}\t\t|\t\t{{volume}}\t\t|\t\t{{album}}\t\t|\t\t{{artist}}\t\t|\t\t{{title}}\t\t|\t\t{{mpris:length}}\t\t|\t\tendl", "metadata");
        var columns = output.split("\n");

        List<LinuxSong> arr = new ArrayList<>();
        for (String s : columns) {
            String[] split = s.split("\t\t\\|\t\t");
            LinuxSong source = getSong(split);
            if (source != null) {
                arr.add(source);
            }
        }

        return new PlayerCtlWrapper(arr);
    }

    private static LinuxSong getSong(String[] split) {
        if (split.length < 6) {
            return null;
        }

        String program = split[0];

        String title = split[6];
        String length = split[7];
        String artist = split[5];
        String album = split[4];

        String position = split[1];
        String status = split[2];
        String volume = split[3];

        return new LinuxSong(program, title, artist, album, cpl(position), cpl(length), status, cpf(volume));
    }

    // custom parse long
    private static Long cpl(String s) {
        if (s.trim().isEmpty()) return null;
        try {
            return Long.parseLong(s);
        } catch (Exception e) {
            return null;
        }
    }

    // custom parse long
    private static Float cpf(String s) {
        if (s.trim().isEmpty()) return null;
        try {
            return Float.parseFloat(s);
        } catch (Exception e) {

            return null;
        }
    }
}
