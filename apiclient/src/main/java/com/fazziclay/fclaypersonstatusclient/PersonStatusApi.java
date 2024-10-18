package com.fazziclay.fclaypersonstatusclient;

import com.fazziclay.fclaysystem.personstatus.api.dto.PlaybackDto;
import org.jetbrains.annotations.NotNull;
import retrofit2.Call;
import retrofit2.http.*;

public interface PersonStatusApi {
    @PUT("person/status/headphones")
    Call<PlaybackDto> putMusic(@NotNull @Header("Authorization") String authorization,
                               @NotNull @Body PlaybackDto song);


    @PATCH("person/status/headphones")
    Call<PlaybackDto> patchMusic(@NotNull @Header("Authorization") String authorization,
                                  @NotNull @Body PlaybackDto song);


    @DELETE("person/status/headphones")
    Call<Object> deleteMusic(@Header("Authorization") String authorization);
}