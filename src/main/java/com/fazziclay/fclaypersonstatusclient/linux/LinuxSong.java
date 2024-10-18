package com.fazziclay.fclaypersonstatusclient.linux;


import com.fazziclay.fclaysystem.personstatus.api.dto.PlaybackDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@ToString
@AllArgsConstructor
@Getter
public class LinuxSong {
    private String program;
    private String title;
    private String artist;
    private String album;

    private Long position;
    private Long duration;
    private String status;
    private Float volume;

    public boolean isPlaying() {
        return status.equalsIgnoreCase("Playing");
    }

    public PlaybackDto asPlaybackDto() {
        return new PlaybackDto(title, artist, album, program, null, nLNanosToMs(position), nLNanosToMs(duration), volume < 0.001 ? null : volume);
    }

    private static Long nLNanosToMs(Long l) {
        if (l == null) return null;
        if (l < 1000) return null;
        return l / 1000;
    }
}
