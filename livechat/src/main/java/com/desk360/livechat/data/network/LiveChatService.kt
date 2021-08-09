package com.desk360.livechat.data.network

import com.desk360.livechat.data.model.chatsettings.ChatSettingsResponse
import com.desk360.livechat.data.model.feedback.FeedbackRequest
import com.desk360.livechat.data.model.feedback.FeedbackResponse
import com.desk360.livechat.data.model.media.UploadMediaResponse
import com.desk360.livechat.data.model.message.ChatbotsMessageRequest
import com.desk360.livechat.data.model.message.MessageRequest
import com.desk360.livechat.data.model.message.MessageResponse
import com.desk360.livechat.data.model.session.EndSessionRequest
import com.desk360.livechat.data.model.session.EndSessionResponse
import com.desk360.livechat.data.model.session.SessionRequest
import com.desk360.livechat.data.model.session.SessionResponse
import io.reactivex.Observable
import okhttp3.MultipartBody
import retrofit2.http.*

interface LiveChatService {

    @GET("chat/sdk/setting")
    fun getConfig(
        @Query("language") languageCode: String,
        @Query("token") token: String?
    ): Observable<ChatSettingsResponse>

    @POST("chat/sdk/session")
    fun createSession(@Body body: SessionRequest): Observable<SessionResponse?>

    @POST("chat/sdk/session/ended")
    fun endSession(@Body body: EndSessionRequest): Observable<EndSessionResponse>

    @POST("chatbots/create/session")
    fun createChatbotsSession(@Body body: SessionRequest): Observable<SessionResponse?>

    @POST("chatbots/create/message")
    fun sendChatbotsMessage(@Body body: ChatbotsMessageRequest): Observable<MessageResponse?>

    @POST("chat/sdk/message")
    fun sendMessage(@Body body: MessageRequest): Observable<MessageResponse>

    @POST("chat/sdk/feedback")
    fun feedback(@Body body: FeedbackRequest): Observable<FeedbackResponse>

    @Multipart
    @POST("chat/sdk/file-upload")
    fun uploadMedia(
        @Part attachment: MultipartBody.Part
    ): Observable<UploadMediaResponse>
}