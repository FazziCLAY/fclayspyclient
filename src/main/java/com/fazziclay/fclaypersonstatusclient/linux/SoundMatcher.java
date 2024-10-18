package com.fazziclay.fclaypersonstatusclient.linux;

import com.fazziclay.fclaypersonstatusclient.Debug;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SoundMatcher {
    @Getter private static final Set<String> allowedPrograms = new HashSet<>();
    @Getter private static final Set<String> blockList = new HashSet<>(); // only lowercase

    public static void addToBlockList(String s) {
        blockList.add(s.toLowerCase().trim());
    }

    public static void addToAllowedPrograms(String s) {
        allowedPrograms.add(s);
    }

    public static boolean isInAllowedProgram(LinuxSong s) {
        return allowedPrograms.contains(s.getProgram());
    }

    public static boolean isBlocked(LinuxSong linuxSong) {
        String s = (linuxSong.getTitle() + " " + linuxSong.getAlbum() + " " + linuxSong.getArtist()).toLowerCase();

        for (String block : blockList) {
            if (s.contains(block)) {
                Debug.debug("Blocked by: " + block + " its: " + linuxSong);
                return true;
            }
        }
        return false;
    }

    @Nullable
    public static LinuxSong getCurrentSong() throws IOException {
        List<LinuxSong> songs = PlayerCtlWrapper.create().getLinuxSongs();
        for (LinuxSong song : songs) {
            System.out.println(song);
        }
        for (LinuxSong s : songs) {
            if (s.isPlaying() && isInAllowedProgram(s) && !isBlocked(s)) {
                return s;
            }
        }

        return null;
    }
}
