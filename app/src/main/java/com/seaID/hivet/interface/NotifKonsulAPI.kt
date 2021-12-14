package com.seaID.hivet.`interface`

import com.seaID.hivet.Contants.Contants.Companion.CONTENT_TYPE
import com.seaID.hivet.Contants.Contants.Companion.SERVER_KEY
import com.seaID.hivet.models.PushNotifKonsul
import retrofit2.Response
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface NotifKonsulAPI {
    @Headers("Authorization: key=$SERVER_KEY","Content-type:$CONTENT_TYPE")
    @POST("fcm/send")
    suspend fun pushNotifKonsul(
        @Body notification:PushNotifKonsul
    ):Response<ResponseBody>
}