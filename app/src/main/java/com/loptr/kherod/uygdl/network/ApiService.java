package com.loptr.kherod.uygdl.network;

import com.loptr.kherod.uygdl.model.Admob;

import io.reactivex.Single;
import retrofit2.http.GET;

public interface ApiService {

    @GET("admob/readOne.php")
    Single<Admob> getAdmob(
    );
}
