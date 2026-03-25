# GitHub Actions Build Guide for JinnChat

## ✅ Setup Complete!

Your GitHub Actions workflow is now ready. When you push to GitHub, the APK will be automatically built.

## 📋 Steps to Build on GitHub

### 1. Initialize Git Repository
```bash
cd /data/data/com.termux/files/home/jinnchat-app
git init
git add .
git commit -m "Initial commit - JinnChat app"
```

### 2. Create GitHub Repository
- Go to https://github.com/new
- Create a new repository named `jinnchat-app`
- **Don't** initialize it with README (you already have one)

### 3. Push to GitHub
```bash
# Replace YOUR_USERNAME with your GitHub username
git remote add origin https://github.com/YOUR_USERNAME/jinnchat-app.git
git branch -M main
git push -u origin main
```

### 4. Check Build Status
- Go to your repository on GitHub
- Click on **Actions** tab
- You'll see the build running
- Wait for it to complete (green checkmark)

### 5. Download APK
- Click on the workflow run
- Scroll down to **Artifacts** section
- Download `jinnchat-debug-apk` or `jinnchat-release-apk`
- Extract the ZIP file to get the APK

## 📁 What Gets Built

| Artifact | Description |
|----------|-------------|
| `jinnchat-debug-apk` | Debug version (for testing) |
| `jinnchat-release-apk` | Release version (for distribution) |

## ⚙️ Workflow Configuration

The workflow (`.github/workflows/build.yml`) does:
1. ✅ Sets up JDK 17
2. ✅ Sets up Gradle
3. ✅ Builds the project
4. ✅ Creates Debug APK
5. ✅ Creates Release APK
6. ✅ Uploads both as artifacts

## 🔧 Troubleshooting

### Build Fails?
Check these common issues:

1. **Gradle version mismatch**
   - Update `gradle-wrapper.properties` if needed

2. **Android SDK not found**
   - GitHub Actions has Android SDK pre-installed

3. **Build timeout**
   - Increase timeout in workflow if needed

### Want to Customize?

Edit `.github/workflows/build.yml`:
- Change `java-version` for different JDK
- Add tests with `./gradlew test`
- Add signing for release builds

## 📝 Notes

- **Build Time**: ~5-10 minutes on first run
- **Storage**: Artifacts expire after 90 days
- **Limits**: GitHub Free has 2000 minutes/month

## 🚀 Quick Push Command

```bash
cd /data/data/com.termux/files/home/jinnchat-app
git add .
git commit -m "Update app"
git push
```

Then check Actions tab on GitHub!

---

Made with ❤️ for JinnChat
