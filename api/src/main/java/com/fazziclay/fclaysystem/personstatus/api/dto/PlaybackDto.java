package com.fazziclay.fclaysystem.personstatus.api.dto;

import lombok.*;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Function;

@ToString
@EqualsAndHashCode
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class PlaybackDto {
    private static final PlaybackDto EMPTY_PATCH = new PlaybackDto();

    private String title;
    private String artist;
    private String album;
    private String player;
    private String artHttpUrl;
    private Long position;
    private Long duration;
    private Float volume;

    // created for call on PATCH object, if patch affects title, artist or album, song changed
    public boolean isDiffSongTouched() {
        return title != null || artist != null || album != null;
    }

    public boolean isOnlyPositionSet() {
        return title == null &&
                artist == null &&
                album == null &&
                player == null &&
                artHttpUrl == null &&
                position != null && // <-- NOT NULL CHECK FOR PATCH OBJECT
                duration == null &&
                volume == null;
    }

    // only for {@link #diff}
    private static <T> T r(PlaybackDto m, PlaybackDto d, Function<PlaybackDto, T> f) {
        Function<PlaybackDto, T> fNullSafe = dto -> {
            if (dto == null) return null;
            return f.apply(dto);
        };
        if (!Objects.equals(fNullSafe.apply(m), fNullSafe.apply(d))) {
            return fNullSafe.apply(m);
        }
        return null;
    }

    // uses for PATCH method
    public static PlaybackDto diff(PlaybackDto left, PlaybackDto base) {
        return new PlaybackDto(
                r(left, base, PlaybackDto::getTitle),
                r(left, base, PlaybackDto::getArtist),
                r(left, base, PlaybackDto::getAlbum),
                r(left, base, PlaybackDto::getPlayer),
                r(left, base, PlaybackDto::getArtHttpUrl),
                r(left, base, PlaybackDto::getPosition),
                r(left, base, PlaybackDto::getDuration),
                r(left, base, PlaybackDto::getVolume)
        );
    }

    // only for {@link #newWithPatch}
    private static <T> T m(PlaybackDto m, PlaybackDto p, Function<PlaybackDto, T> f) {
        T pv = f.apply(p);
        if (pv != null) return pv;
        return f.apply(m);
    }

    @NotNull
    public PlaybackDto newWithPatch(@NotNull PlaybackDto patch) {
        return new PlaybackDto(
                m(this, patch, PlaybackDto::getTitle),
                m(this, patch, PlaybackDto::getArtist),
                m(this, patch, PlaybackDto::getAlbum),
                m(this, patch, PlaybackDto::getPlayer),
                m(this, patch, PlaybackDto::getArtHttpUrl),
                m(this, patch, PlaybackDto::getPosition),
                m(this, patch, PlaybackDto::getDuration),
                m(this, patch, PlaybackDto::getVolume)
        );
    }

    public static PlaybackDto cloneDto(PlaybackDto dto) {
        return dto.newWithPatch(EMPTY_PATCH);
    }
}
