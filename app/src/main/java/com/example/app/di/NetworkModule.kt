package com.example.app.di

import android.annotation.SuppressLint
import com.example.data.remote.WerewolfApi
import com.example.data.repository.WebSocketManagerImpl
import com.example.domain.repository.WebSocketManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.inject.Singleton
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    // 提供 HttpLoggingInterceptor
    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY // 记录请求和响应的body
        }
    }

    // 提供信任所有证书的TrustManager
    @SuppressLint("CustomX509TrustManager")
    @Provides
    @Singleton
    fun provideUnsafeTrustManager(): X509TrustManager {
        return object : X509TrustManager {
            override fun checkClientTrusted(
                chain: Array<out X509Certificate>?,
                authType: String?
            ) {
            }

            override fun checkServerTrusted(
                chain: Array<out X509Certificate>?,
                authType: String?
            ) {
            }

            override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
        }
    }

    // 提供 OkHttpClient (包含心跳配置，防止 WebSocket 断连)
    @Provides
    @Singleton
    fun provideOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        trustManager: X509TrustManager
    ): OkHttpClient {
        // 创建不安全的SSL上下文
        val sslContext = SSLContext.getInstance("SSL")
        sslContext.init(null, arrayOf<TrustManager>(trustManager), java.security.SecureRandom())


        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .pingInterval(30, TimeUnit.SECONDS) // 发送心跳包保持连接
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    // 提供 Retrofit
    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("http://192.168.124.27:8000/wolf/api/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // 提供 WerewolfApi
    @Provides
    @Singleton
    fun provideWerewolfApi(retrofit: Retrofit): WerewolfApi {
        return retrofit.create(WerewolfApi::class.java)
    }

    // 绑定 WebSocketManager 接口到实现类
    @Provides
    @Singleton
    fun provideWebSocketManager(impl: WebSocketManagerImpl): WebSocketManager {
        return impl
    }
}