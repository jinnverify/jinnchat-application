# ✅ JinnChat - GitHub Push Checklist

## Before Pushing to GitHub

### 1. Remove node_modules (Don't upload)
```bash
cd /data/data/com.termux/files/home/jinnchat-app/backend
rm -rf node_modules
```

### 2. Verify All Files
```bash
cd /data/data/com.termux/files/home/jinnchat-app
ls -la
```

**Must have:**
- ✅ `backend/server.js`
- ✅ `backend/package.json`
- ✅ `android/app/build.gradle`
- ✅ `android/gradlew`
- ✅ `android/gradle/wrapper/gradle-wrapper.jar`
- ✅ `.github/workflows/build.yml`
- ✅ `README.md`

### 3. Initialize Git
```bash
cd /data/data/com.termux/files/home/jinnchat-app
git init
git add .
git status  # Check files
```

### 4. Create First Commit
```bash
git commit -m "Initial commit: JinnChat - Random anonymous chat app

Features:
- Node.js WebSocket backend
- Kotlin Android app
- Real-time random matching
- GitHub Actions CI/CD
- Production server on Render
"
```

### 5. Create GitHub Repository
1. Go to: https://github.com/new
2. Repository name: `jinnchat-app`
3. Visibility: **Public** (or Private)
4. **Don't** check "Initialize with README"
5. Click **Create repository**

### 6. Push to GitHub
```bash
# Replace YOUR_USERNAME with your GitHub username
git remote add origin https://github.com/YOUR_USERNAME/jinnchat-app.git
git branch -M main
git push -u origin main
```

### 7. Check GitHub Actions
1. Go to your repository on GitHub
2. Click **Actions** tab
3. Wait for build to start (automatic)
4. Build takes ~5-10 minutes
5. Download APK from **Artifacts** section

## 📱 After Build Completes

### Download APK:
1. Click on the workflow run (green check or red X)
2. Scroll to **Artifacts** section
3. Click `jinnchat-debug-apk`
4. Extract ZIP file
5. Install APK on Android device

### Install APK:
```bash
adb install app-debug.apk
```

Or transfer to phone and install manually.

## 🔧 Server URL Configuration

**✅ Already Configured!** The app is pre-configured to use:

```kotlin
// Production server (Render)
private val SERVER_URL = "wss://jinnchat-backend.onrender.com"
```

### Backend Status:
- ✅ **Production**: https://jinnchat-backend.onrender.com (Active)
- Health Check: https://jinnchat-backend.onrender.com/health

### For Local Testing:
```kotlin
// Uncomment this line for local development
// private val SERVER_URL = "ws://10.0.2.2:3000"
```

## 🚀 Quick Commands

```bash
# Full push workflow
cd /data/data/com.termux/files/home/jinnchat-app
rm -rf backend/node_modules  # Don't upload node_modules
git add .
git commit -m "Update app"
git push

# Check build status
# Visit: https://github.com/YOUR_USERNAME/jinnchat-app/actions
```

## ⚠️ Common Issues

| Issue | Solution |
|-------|----------|
| Build fails | Check Actions log for errors |
| APK not found | Wait for build to complete |
| Git push rejected | Run `git pull --rebase` first |
| Large file error | Check `.gitignore`, remove large files |
| Can't connect to server | Check Render backend status |

## 📊 Build Status Badge

Add this to your README:

```markdown
![Android Build](https://github.com/YOUR_USERNAME/jinnchat-app/actions/workflows/build.yml/badge.svg)
```

---

**Ready to push?** Run the commands above! 🚀

## 📝 Project Info

- **Backend**: Node.js + WebSocket (Render deployed)
- **Frontend**: Kotlin + Android
- **CI/CD**: GitHub Actions
- **Production URL**: wss://jinnchat-backend.onrender.com
