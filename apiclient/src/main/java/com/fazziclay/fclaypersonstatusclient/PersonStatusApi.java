package com.fazziclay.fclaypersonstatusclient;

import com.fazziclay.fclaysystem.personstatus.api.dto.PlaybackDto;
import org.jetbrains.annotations.NotNull;
import retrofit2.Call;
import retrofit2.http.*;

public interface PersonStatusApi {
    @PUT("person/{person_name}/status/headphones")
    Call<PlaybackDto> putMusic(@Path("person_name") String personName,
                               @NotNull @Header("Authorization") String authorization,
                               @NotNull @Body PlaybackDto song);


    @PATCH("person/{person_name}/status/headphones")
    Call<PlaybackDto> patchMusic(@Path("person_name") String personName,
                                 @NotNull @Header("Authorization") String authorization,
                                 @NotNull @Body PlaybackDto song);


    @DELETE("person/{person_name}/status/headphones")
    Call<Object> deleteMusic(@Path("person_name") String personName,
                             @Header("Authorization") String authorization);

}
