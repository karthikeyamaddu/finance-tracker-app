# Release Notes

## Version 1.0.0 (October 4, 2025)

### 🎉 Initial Release

This is the first stable release of the Android Expense Tracker app with comprehensive SMS parsing and manual transaction management capabilities.

### ✨ Features

#### Core Functionality
- **Automatic SMS Parsing**: Real-time parsing of Axis Bank transaction SMS messages
- **Manual Transaction Entry**: Add transactions manually with custom details
- **Transaction Tagging**: Organize transactions with custom tags and emojis
- **Transaction History**: Complete history with filtering and search capabilities
- **Untagged Management**: Easy identification and tagging of untagged transactions

#### User Interface
- **Modern Material Design 3**: Clean, intuitive interface following Google's latest design guidelines
- **Responsive Layout**: Optimized for various Android screen sizes
- **Color-coded Transactions**: Green for credits, red for debits, yellow for untagged items
- **Smooth Animations**: iOS-inspired transitions and interactions

#### Settings & Customization
- **Time Format Toggle**: Switch between 12-hour and 24-hour time formats
- **Notification Management**: Control transaction notifications
- **Data Export**: Export all transactions to CSV format with timestamp
- **Permission Management**: Graceful handling of SMS and storage permissions

#### Technical Features
- **Local Database**: Room database for fast, offline transaction storage
- **Privacy-First**: All data processed locally, no external servers
- **Duplicate Prevention**: Smart detection to prevent duplicate SMS entries
- **Error Handling**: Comprehensive error handling and user feedback

### 📱 Supported Platforms
- **Minimum Android Version**: Android 7.0 (API 24)
- **Target Android Version**: Android 14 (API 34)
- **Architecture**: ARM64, ARM32, x86_64

### 🏦 Supported Banks
- **Axis Bank**: Full SMS parsing support with multiple message formats

### 🔒 Permissions Required
- **SMS Access**: For automatic transaction parsing
- **Storage Access**: For CSV export (Android < 10)
- **Notifications**: For transaction alerts

### 📊 App Statistics
- **APK Size**: ~12 MB
- **Installation Size**: ~25 MB
- **Memory Usage**: ~50 MB average
- **Battery Impact**: Minimal (SMS receiver only)

### 🐛 Known Issues
- None reported in this release

### 🔮 Coming Soon
- Multi-bank SMS support (SBI, HDFC, ICICI)
- AI-powered tag suggestions
- Spending analytics and charts
- Budget tracking features
- Cloud backup and sync

### 📥 Download
- **Direct APK**: [Download v1.0.0](https://github.com/karthikeyamaddu/finance-tracker-app/raw/main/app/build/outputs/apk/debug/app-debug.apk)
- **Source Code**: [GitHub Repository](https://github.com/karthikeyamaddu/finance-tracker-app)

### 🙏 Acknowledgments
Special thanks to the Android development community and Material Design team for the excellent resources and guidelines.

---

**Developer**: Karthikeya Maddu  
**Release Date**: October 4, 2025  
**Build**: 1.0.0 (1)