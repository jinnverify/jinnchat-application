# JinnChat - Random Anonymous Chat App

A lightweight Android app for random anonymous chatting with strangers, similar to Omegle. Built with Node.js backend and Kotlin Android frontend.

## Features

- 🔥 **Random Matching**: Connect with random strangers instantly
- 💬 **Real-time Chat**: WebSocket-based instant messaging
- 👤 **Anonymous**: No registration required
- 🎨 **Modern UI**: Clean, lightweight Material Design interface
- ⚡ **Fast**: Optimized for performance and low resource usage
- 📱 **Typing Indicator**: See when your partner is typing
- 🟢 **Online Counter**: Live user count

## Tech Stack

### Backend
- **Node.js** with Express
- **WebSocket** (ws library) for real-time communication
- **UUID** for user identification

### Frontend (Android)
- **Kotlin**
- **Material Design** components
- **OkHttp** WebSocket client
- **RecyclerView** for chat messages
- **ViewBinding**

## Project Structure

```
jinnchat-app/
├── backend/
│   ├── server.js          # Node.js WebSocket server
│   └── package.json       # Backend dependencies
├── android/
│   ├── app/
│   │   ├── src/main/
│   │   │   ├── java/com/jinnchat/app/
│   │   │   │   ├── MainActivity.kt
│   │   │   │   ├── adapter/
│   │   │   │   ├── model/
│   │   │   │   └── websocket/
│   │   │   ├── res/
│   │   │   └── AndroidManifest.xml
│   │   └── build.gradle
│   └── build.gradle
└── README.md
```

## Setup Instructions

### Backend Setup

**Production Server:** https://jinnchat-backend.onrender.com

The backend is already deployed on Render. You can use it directly or deploy your own:

1. Navigate to backend folder:
```bash
cd backend
```

2. Install dependencies:
```bash
npm install
```

3. Start the server:
```bash
npm start
```

The server will run on `http://localhost:3000`

### Android App Setup

1. Open Android Studio

2. Open the `android` folder as a project

3. Wait for Gradle sync to complete

4. The server URL is already configured to production:
```kotlin
// Production (Render)
private val SERVER_URL = "wss://jinnchat-backend.onrender.com"

// For local testing:
// private val SERVER_URL = "ws://10.0.2.2:3000"
```

5. Build and run the app

## Building APK

### Debug APK
```bash
cd android
./gradlew assembleDebug
```
APK will be in: `android/app/build/outputs/apk/debug/app-debug.apk`

### Release APK
```bash
cd android
./gradlew assembleRelease
```
APK will be in: `android/app/build/outputs/apk/release/app-release.apk`

## GitHub Build/CI Setup

To build on GitHub Actions, create `.github/workflows/build.yml`:

```yaml
name: Android Build

on: [push, pull_request]

jobs:
  build:
    runs-on: ubuntu-latest
    
    steps:
    - uses: actions/checkout@v3
    
    - name: Set up Node.js
      uses: actions/setup-node@v3
      with:
        node-version: '18'
    
    - name: Install backend dependencies
      run: |
        cd backend
        npm install
    
    - name: Test backend
      run: |
        cd backend
        npm start &
        sleep 5
        curl http://localhost:3000/health
    
    - name: Set up JDK
      uses: actions/setup-java@v3
      with:
        java-version: '17'
        distribution: 'temurin'
    
    - name: Build Android APK
      run: |
        cd android
        chmod +x gradlew
        ./gradlew assembleDebug
    
    - name: Upload APK
      uses: actions/upload-artifact@v3
      with:
        name: app-debug
        path: android/app/build/outputs/apk/debug/app-debug.apk
```

## API Reference

### WebSocket Events

**Client → Server:**
- `find_partner` - Find a random partner
- `chat_message` - Send a chat message
- `end_chat` - End current chat
- `typing` - Send typing status

**Server → Client:**
- `connected` - Connection established
- `matched` - Partner found
- `waiting` - Waiting for partner
- `chat_message` - Received message
- `chat_ended` - Chat ended
- `partner_ended` - Partner ended chat
- `partner_disconnected` - Partner disconnected
- `typing` - Partner typing status
- `user_count` - Total online users

### REST Endpoints

- `GET /health` - Health check

## Configuration

### Server Environment Variables

- `PORT` - Server port (default: 3000)

### App Configuration

- `compileSdk`: 34
- `minSdk`: 24
- `targetSdk`: 34

## Permissions

The app requires:
- `INTERNET` - For WebSocket connection
- `ACCESS_NETWORK_STATE` - To check network status

## Development Tips

1. **Testing with Emulator**: Use `ws://10.0.2.2:3000` to connect to your local machine

2. **Testing with Physical Device**: 
   - Find your computer's IP: `ipconfig` (Windows) or `ifconfig` (Mac/Linux)
   - Use `ws://YOUR_IP:3000`

3. **Debugging**: Check Logcat for WebSocket logs with tag "WebSocket"

## Troubleshooting

**Connection Issues:**
- Make sure server is running
- Check firewall settings
- Verify correct IP address

**Build Errors:**
- Sync Gradle files
- Clean and rebuild project
- Update Android Studio

## License

MIT License - Feel free to use and modify!

## Contributing

Contributions are welcome! Feel free to submit PRs.

---

Made with ❤️ for anonymous chat lovers
# Jinnchat-app
# jinnchat-application
# jinnchat-application
