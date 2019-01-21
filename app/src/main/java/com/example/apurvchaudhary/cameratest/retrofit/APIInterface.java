package com.example.apurvchaudhary.cameratest.retrofit;

import com.example.apurvchaudhary.cameratest.models.APIFaceData;
import com.example.apurvchaudhary.cameratest.models.UploadResult;


import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface APIInterface {

    @Multipart
    @POST("/face_search")
    Call<APIFaceData> faceSearch(
            @Part MultipartBody.Part image,
            @Part("secretKey") RequestBody secretKey,
            @Part("apiKey") RequestBody apiKey,
            @Part("folderKey") RequestBody folderKey);


    @Multipart
    @POST("/face_search/train")
    Call<UploadResult> singleImageUpload(@Part MultipartBody.Part image,
                                         @Part("secretKey") RequestBody secretKey,
                                         @Part("apiKey") RequestBody apiKey,
                                         @Part("folderKey") RequestBody folderKey,
                                         @Part("label") RequestBody label);

    @Multipart
    @POST("/face_search/dataset")
    Call<ResponseBody> getDataset(@Part("secretKey") RequestBody secretKey,
                                  @Part("apiKey") RequestBody apiKey, @Part("folderKey") RequestBody folderKey);

    @Multipart
    @POST("/face_search/delete/image")
    Call<ResponseBody> deleteImage(@Part("secretKey") RequestBody secretKey,
                                   @Part("apiKey") RequestBody apiKey,
                                   @Part("imageKey") RequestBody imageKey,
                                   @Part("folderKey") RequestBody folderKey
    );

    @Multipart
    @POST("/face_search/delete/dir")
    Call<ResponseBody> deleteAll(@Part("secretKey") RequestBody secretKey,
                                 @Part("apiKey") RequestBody apiKey,
                                 @Part("folderKey") RequestBody folderKey
    );

    @GET("/api/sendhttp.php")
    Call<ResponseBody> sendMessage(@Query("message") String message,
                                   @Query("authkey") String authkey,
                                   @Query("mobiles") String mobiles,
                                   @Query("route") int route,
                                   @Query("sender") String sender,
                                   @Query("country") String country);
}
