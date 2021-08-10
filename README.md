# Desk360 Live Chat Android SDK

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

```
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
```

Add the dependency

```
dependencies {
        implementation 'com.github.Teknasyon-Teknoloji:desk360-livechat-android-sdk:1.0.5'
}
```

(Please change latest_release with : https://jitpack.io/#Teknasyon-Teknoloji/desk360-livechat-android-sdk)

Or Maven

**Step 1.** Add the JitPack repository to your build file

```markup
	<repositories>
		<repository>
		    <id>jitpack.io</id>
		    <url>https://jitpack.io</url>
		</repository>
	</repositories>
```

**Step 2.** Add the dependency


```markup
	<dependency>
	    <groupId>com.github.Teknasyon-Teknoloji</groupId>
	    <artifactId>desk360-livechat-android-sdk</artifactId>
	    <version>Tag</version>
	</dependency>
```


### Usage

```
import com.desk360.livechat.manager.Desk360LiveChat
import com.desk360.livechat.manager.LiveChatHelper
import com.desk360.livechat.manager.LiveChatManager
```

```
        val liveChatManager = LiveChatManager.Builder()
                .setDomainAddress("https://your.domain.address/")
                .setToken("your_private_token")
                .setLanguageCode("language_code")
                .build()

        Desk360LiveChat.init(activity.applicationContext, liveChatManager) { isActive ->
           // your code
        }

        // when you want to start Live Chat
        Desk360LiveChat.start()
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

Copyright Teknasyon 2021.

Desk360 Live Chat SDK is released under the MIT license. See [LICENSE](https://github.com/Teknasyon-Teknoloji/desk360-livechat-android-sdk/blob/master/LICENSE)  for more information.
