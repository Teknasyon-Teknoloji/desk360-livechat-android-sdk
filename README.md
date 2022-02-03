# Desk360 Chat Android SDK

 ![img](https://img.shields.io/badge/kotlin-v1.5.20-brightgreen.svg?logoColor=orange&logo=kotlin)   [![](https://jitpack.io/v/Teknasyon-Teknoloji/desk360-livechat-android-sdk.svg)](https://jitpack.io/#Teknasyon-Teknoloji/desk360-livechat-android-sdk)


# Introduction

Desk360 Live Chat SDK is an open source Android library that provides live support to your customers directly from your application by writing a few lines of code.

![Main Image](https://media.giphy.com/media/dZFXXl2IY6na02Z2hj/giphy.gif)

# Features

- Talk to your customers using our panel and make use of our SDK to identify a user and provide contextual support.
- Powered by Firebase Realtime database
- Username/Email Address login
- Offline Messaging Support
- Text, Image, Video (.mp4) and Document (word, excel, pdf) messages
- Ability to integrate Chatbots.
- Multi-languages support: It supports 40+ languages.
- Chat Feedback
- Smart Plug
- Custom Fields

# Technical Details

- **Coding Language -** %100 Kotlin
- **Persistence -** Room
- **Reactive Programming -** RXJava, RXAndroid
- **Multi-threading**
- **Architecture-** MVVM, Use Cases
- **Binding-** Data Binding, View Binding
- **API Level 21+**

# Setup

To integrate Desk360 Live Chat into your Android project , add below parts to your  build.gradlle

```groovy
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
```

Add the dependency

```groovy
dependencies {
        implementation 'com.github.Teknasyon-Teknoloji:desk360-livechat-android-sdk:1.0.8'
}
```

(Please change latest_release with : https://jitpack.io/#Teknasyon-Teknoloji/desk360-livechat-android-sdk)

Add File Provider to Your AndroidManifest.xml

```xml
	<application>
	...
         	<provider
         	   android:name=".YourFileProvider"
         	   android:authorities="${applicationId}.provider"
         	   android:exported="false"
         	   android:grantUriPermissions="true">
         	   <meta-data
          	      android:name="android.support.FILE_PROVIDER_PATHS"
          	      android:resource="@xml/provider_paths" />
        	 </provider>
	 ....
	 </application>
```

### Usage

```kotlin
import com.desk360.livechat.manager.Desk360LiveChat
import com.desk360.livechat.manager.LiveChatHelper
import com.desk360.livechat.manager.LiveChatManager
```

```kotlin
        val settings = mapOf("age" to "23")
        
        val liveChatManager = LiveChatManager.Builder()
            .setDomainAddress("https://domain.desk360.com")
            .setToken("token")
            .setLanguageCode("tr")
            .setUserName("Luke")
            .setUserEmailAddress("luke@emailadress.com")
            .setSmartPlug(settings)
            .build()

        Desk360LiveChat.init(this, liveChatManager) { isActive ->
            // You can start Desk360LiveChat after initialization completed
            btnLiveChat.visibility = if (isActive) 
                View.VISIBLE
            else 
                View.GONE
        }

        btnLiveChat.setOnClickListener {
            // when you want to start Live Chat
            Desk360LiveChat.start()
        }
```

# ProGuard
```

If you are using proguard you must add this rules to avoid further compile issues.

-keep class com.desk360.livechat.data.** { *; }
-keepnames com.desk360.livechat.data.** { *; }

```


# Versioning

We use [SemVer](http://semver.org/) for versioning.


# Support

If you have any questions or feature requests, please create an issue.

# Licence

Copyright Teknasyon 2022.

Desk360 Live Chat SDK is released under the MIT license. See [LICENSE](https://github.com/Teknasyon-Teknoloji/desk360-livechat-android-sdk/blob/master/LICENSE)  for more information.
