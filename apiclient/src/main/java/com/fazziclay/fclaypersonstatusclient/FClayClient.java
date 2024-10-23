package com.fazziclay.fclaypersonstatusclient;

import com.fazziclay.fclaysystem.personstatus.api.dto.PlaybackDto;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.Objects;

public class FClayClient {
    @Setter
    private boolean isDebug;

    @Getter
    private final Retrofit retrofit;
    private final PersonStatusApi api;
    private final String personName;
    private final String accessToken;
    private PlaybackDto serverSong;
    private long ssPostTime;
    private long ssPostTimeResponse;
    private boolean failedPost;
    private long latestApiPatch;

    public FClayClient(String baseUrl, String personName, String accessToken) {
        this.personName = personName;
        this.accessToken = accessToken;
        this.retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        this.api = retrofit.create(PersonStatusApi.class);
    }

    public void postSong(@Nullable PlaybackDto playbackDto) {
        if (playbackDto == null) {
            delete();
        } else {
            playing(playbackDto);
        }
    }

    public void playing(@NotNull PlaybackDto playbackDto) {
        // wait for response on previous call
        if (ssPostTimeResponse < ssPostTime && (System.currentTimeMillis() - ssPostTime < 30000)) {
            return;
        }

        PlaybackDto diff = PlaybackDto.diff(playbackDto, serverSong);
        debug("diff=" + diff);

        if (failedPost || diff.isDiffSongTouched() || System.currentTimeMillis() - ssPostTime > 50000) {
            apiPut(playbackDto);

        } else if ((!Objects.equals(serverSong, playbackDto)) && (!diff.isOnlyPositionSet() || System.currentTimeMillis() - latestApiPatch > 15000)) {
            apiPatch(diff);
            latestApiPatch = System.currentTimeMillis();
        }
    }

    public void delete() {
        if (serverSong == null) return;
        apiDelete();
    }


    private void debug(Object msg) {
        if (isDebug) {
            System.out.println(msg);
        }
    }


    // ============= A P I ===================
    private void apiDelete() {
        api.deleteMusic(personName, accessToken).enqueue(new DeleteMusicCallback());
        ssPostTime = System.currentTimeMillis();
    }

    private void apiPut(@NotNull PlaybackDto playback) {
        api.putMusic(personName, accessToken, playback).enqueue(new PostMusicCallback());
        ssPostTime = System.currentTimeMillis();
    }

    private void apiPatch(@NotNull PlaybackDto playback) {
        api.patchMusic(personName, accessToken, playback).enqueue(new PostMusicCallback());
        ssPostTime = System.currentTimeMillis();
    }


    private static class EmptyCallback implements retrofit2.Callback<PlaybackDto> {
        @Override
        public void onResponse(@NotNull Call<PlaybackDto> call, @NotNull Response<PlaybackDto> response) {}

        @Override
        public void onFailure(@NotNull Call<PlaybackDto> call, @NotNull Throwable throwable) {}
    }

    private class PostMusicCallback implements retrofit2.Callback<PlaybackDto> {
        @Override
        public void onResponse(@NotNull Call<PlaybackDto> call, @NotNull Response<PlaybackDto> response) {
            debug("onResponse = " + response.message());
            if (response.code() == 200) {
                serverSong = response.body();
                failedPost = false;
            } else {
                failedPost = true;
            }
            ssPostTimeResponse = System.currentTimeMillis();
        }

        @Override
        public void onFailure(@NotNull Call<PlaybackDto> call, @NotNull Throwable throwable) {
            throwable.printStackTrace();
            ssPostTimeResponse = System.currentTimeMillis();
        }
    }

    private class DeleteMusicCallback implements retrofit2.Callback<Object> {

        @Override
        public void onResponse(@NotNull Call<Object> call, @NotNull Response<Object> response) {
            if (response.code() == 204) {
                serverSong = null;
            }
            ssPostTimeResponse = System.currentTimeMillis();
        }

        @Override
        public void onFailure(@NotNull Call<Object> call, @NotNull Throwable throwable) {
            throwable.printStackTrace();
            ssPostTimeResponse = System.currentTimeMillis();
        }
    }

}
