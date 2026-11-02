# BizRyno Auto Reply — Implementation Plan

An advanced, production-quality native Android app built with Kotlin, Jetpack Compose, Material 3, MVVM, Room, WorkManager, Firebase Auth/Firestore, and OpenRouter AI API. It acts as an automated WhatsApp sales assistant replying to customers while the owner sleeps.

---

## User Review & Critical Decisions

> [!IMPORTANT]
> - **Backend Storage**: Firebase Auth (Username mapped to synthetic email) + Firestore for user prompts, settings, and cloud sync.
> - **Local Storage**: Room DB for chat history, messages, and conversation logs.
> - **AI Model**: OpenRouter API (`google/gemma-4-31b-it:free` with automatic fallback to `google/gemma-4-26b-a4b-it:free`).
> - **WhatsApp Integration**: Android `NotificationListenerService` with `RemoteInput` auto-reply injection and robust keep-alive mechanisms.

---

## 1. Overview & Core Concept

- **What It Does**: Listens to incoming WhatsApp and WhatsApp Business notifications, debounces rapid multi-message bursts, invokes OpenRouter AI with custom business context and global rules, and sends polite, conversational human-like replies using `RemoteInput`.
- **Target Audience**: Small business owners in India (salons, clinics, coaching, real estate, stores) automating customer queries on WhatsApp.
- **Key Value**: 24x7 instant customer engagement, lead collection, and sales conversion without missing messages.

---

## 2. User Experience & Visual Design

- **Visual Identity**: Luxury dark theme ("BizRyno Neon Night") with deep navy/near-black background (`#0B1020`), electric violet (`#7C5CFF`) to cyan (`#22D3EE`) gradient accents, success green (`#22C55E`), and rounded 20dp cards with subtle elevation.
- **Navigation & Screens**:
  1. **Splash Screen & Auth Check**
  2. **Onboarding Slides** (3 pages: What it does, How it works, Privacy)
  3. **Login / Sign-up** (Username + Password with Firestore username registry)
  4. **Permission Wizard** (Step-by-step guided flow for Notification Listener, Battery Optimization, Notifications)
  5. **Home Dashboard** (Giant animated power toggle, live health card, today's stats, last 5 chats)
  6. **Business Prompt Editor** (Structured sections, 5 one-tap templates, AI prompt improver, version history)
  7. **Playground** (Customer simulator to test AI replies live)
  8. **Conversations / Logs** (Thread view, status, pause/block controls, export CSV)
  9. **Rules & Settings** (Schedules, delays, cooldowns, caps, allow/block lists, API key rotation, model selection, OEM battery help)

---

## 3. Technical Architecture & Data Strategy

```
┌────────────────────────────────────────────────────────┐
│               WhatsApp / WA Business                   │
└──────────────────────────┬─────────────────────────────┘
                           │ Notification StatusBarNotification
                           ▼
┌────────────────────────────────────────────────────────┐
│       WhatsAppNotificationListener (Service)           │
│  - Filters & extracts MessagingStyle / RemoteInput     │
│  - Debounces bursts (Mutex queue)                      │
└──────────────────────────┬─────────────────────────────┘
                           │
                           ▼
┌────────────────────────────────────────────────────────┐
│            AiReplyOrchestrator Worker                  │
│  - Merges Global Rules + Business Prompt + Chat Hist  │
│  - Calls OpenRouter API (OkHttp + Moshi / kotlinx)     │
└──────────────────────────┬─────────────────────────────┘
                           │
                           ▼
┌────────────────────────────────────────────────────────┐
│          PendingIntent RemoteInput Reply               │
│  - Sends reply back to WhatsApp notification chat      │
└────────────────────────────────────────────────────────┘

Data Layer:
- Room DB: Chats, Messages, Stats (30-day retention)
- Firestore: User profile, business prompt, settings sync
- EncryptedSharedPreferences: API keys and auth tokens
- KeepAliveService (Foreground Service) + WorkManager (15-min health check)
```

---

## 4. Key Implementation Steps

1. **Gradle Dependencies & Manifest**: Configure OkHttp, Retrofit, Room, WorkManager, Firebase, Material 3, and Coroutines. Declare `NotificationListenerService`, `KeepAliveService`, `BootReceiver`, and WhatsApp package queries in `AndroidManifest.xml`.
2. **Local Persistence (Room)**: Entities (`ChatEntity`, `MessageEntity`, `StatEntity`), DAOs, and `BizRynoDatabase`.
3. **Cloud Backend (Firebase)**: Authentication (Username ↔ synthetic email mapping), Firestore repositories for business prompts and user config.
4. **AI Engine (OpenRouter)**: Retrofit API client for OpenRouter chat completions with model fallback, multi-key rotation, and system prompt formatting.
5. **WhatsApp Automation Engine**:
   - `WhatsAppNotificationListener`: Extracts sender, text, and `RemoteInput` action.
   - Debouncer and Mutex queue per chat.
   - `KeepAliveService`: Foreground service with low-priority notification, pause/resume actions, and state sharing.
   - WorkManager periodic health check & BootReceiver.
   - OEM battery helper guides for Xiaomi, Oppo, Vivo, Samsung, OnePlus.
6. **Jetpack Compose UI**: Theme (`Theme.kt`, `Color.kt`, `Type.kt`), Splash, Auth, Permission Wizard, Home, Business Prompt Editor with templates, Playground, Conversation Logs, and Settings.
