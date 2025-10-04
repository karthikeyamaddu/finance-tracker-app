# 💰 Android Expense Tracker - Finance Tracker App

[![Version](https://img.shields.io/badge/version-1.0.0-blue.svg)](https://github.com/karthikeyamaddu/finance-tracker-app)
[![Platform](https://img.shields.io/badge/platform-Android-green.svg)](https://android.com)
[![API](https://img.shields.io/badge/API-24%2B-brightgreen.svg)](https://android-arsenal.com/api?level=24)
[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)

A modern, clean Android expense tracker that automatically parses SMS transactions from Axis Bank and allows manual transaction management with tagging system.

## 📱 Download APK

**[Download Latest APK (v1.0.0)](https://github.com/karthikeyamaddu/finance-tracker-app/raw/main/app/build/outputs/apk/debug/app-debug.apk)**

## ✨ Features

### 🔄 Automatic SMS Parsing
- **Smart SMS Detection**: Automatically detects and parses Axis Bank transaction SMS
- **Real-time Processing**: Processes transactions as SMS arrives
- **Accurate Parsing**: Extracts amount, merchant, transaction type (debit/credit), and timestamp
- **Regex-based**: Uses sophisticated regex patterns for reliable data extraction

### 🏷️ Transaction Management
- **Manual Entry**: Add transactions manually with custom details
- **Tag System**: Organize transactions with custom tags (Salary 💼, Food 🍕, etc.)
- **Untagged Tracking**: Easily identify and tag untagged transactions
- **Transaction History**: View complete transaction history with filtering

### ⚙️ Settings & Customization
- **Time Format Toggle**: Switch between 12-hour and 24-hour formats
- **Notification Management**: Control app notifications
- **Data Export**: Export all transactions to CSV format
- **Permission Management**: Handle SMS and storage permissions gracefully

### 🎨 Modern UI/UX
- **Material Design 3**: Clean, modern interface following Google's design guidelines
- **Smooth Animations**: iOS-inspired smooth transitions
- **Color-coded Transactions**: Green for credits, red for debits
- **Responsive Design**: Optimized for various screen sizes

## 📸 Screenshots

*Screenshots will be added soon*

## 🚀 Installation

### Option 1: Download APK (Recommended)
1. Download the APK from the link above
2. Enable "Install from Unknown Sources" in your Android settings
3. Install the APK
4. Grant SMS and notification permissions when prompted

### Option 2: Build from Source
1. Clone this repository
2. Open in Android Studio
3. Build and run on your device

## 🛠️ Technical Architecture

### Tech Stack
- **Language**: Kotlin
- **UI Framework**: Android Views with ViewBinding + Jetpack Compose (hybrid)
- **Database**: Room (SQLite)
- **Architecture**: MVVM with ViewModels
- **Dependency Injection**: Manual DI
- **Build System**: Gradle with Kotlin DSL

### Key Components

#### SMS Parser
```kotlin
// Sophisticated regex patterns for Axis Bank SMS parsing
private val axisDebitPattern = Regex(
    "Rs\\.?\\s*(\\d+(?:,\\d+)*(?:\\.\\d{2})?).*?debited.*?from.*?account.*?at\\s+(.+?)\\s+on\\s+(\\d{2}-\\d{2}-\\d{2})\\s+(\\d{2}:\\d{2}:\\d{2})"
)
```

#### Database Schema
```kotlin
@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey val id: String,
    val amount: Double,
    val type: TransactionType, // DEBIT or CREDIT
    val merchant: String,
    val timestamp: Long,
    val tag: String? = null,
    val isManual: Boolean = false
)
```

### Project Structure
```
app/
├── src/main/java/com/expensetracker/
│   ├── data/
│   │   ├── database/          # Room database setup
│   │   ├── model/            # Data models
│   │   └── repository/       # Data repository
│   ├── ui/
│   │   ├── home/            # Main dashboard
│   │   ├── settings/        # Settings screen
│   │   ├── history/         # Transaction history
│   │   └── untagged/        # Untagged transactions
│   ├── utils/
│   │   ├── SMSParser.kt     # SMS parsing logic
│   │   ├── SettingsManager.kt # App preferences
│   │   └── Constants.kt     # App constants
│   └── receivers/
│       └── SMSReceiver.kt   # SMS broadcast receiver
```

## 🔧 Setup Instructions

### Prerequisites
- Android Studio Arctic Fox or later
- Android SDK 24+ (Android 7.0)
- Kotlin 1.8+

### Build Steps
1. **Clone the repository**
   ```bash
   git clone https://github.com/karthikeyamaddu/finance-tracker-app.git
   cd finance-tracker-app
   ```

2. **Open in Android Studio**
   - Open Android Studio
   - Select "Open an existing project"
   - Navigate to the cloned directory

3. **Sync dependencies**
   - Android Studio will automatically sync Gradle dependencies
   - Wait for the sync to complete

4. **Run the app**
   - Connect your Android device or start an emulator
   - Click "Run" or press Shift+F10

### Key Dependencies
```kotlin
dependencies {
    // Core Android
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.activity:activity-ktx:1.8.2")
    
    // UI
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    
    // Architecture Components
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")
    
    // Database
    implementation("androidx.room:room-runtime:2.6.0")
    implementation("androidx.room:room-ktx:2.6.0")
    kapt("androidx.room:room-compiler:2.6.0")
    
    // Jetpack Compose (for future features)
    implementation("androidx.compose.ui:ui:1.5.3")
    implementation("androidx.compose.material3:material3:1.1.2")
}
```

## 📋 How SMS Parsing Works

The app uses a sophisticated SMS parsing system specifically designed for Axis Bank transaction messages:

### 1. SMS Reception
- `SMSReceiver` listens for incoming SMS messages
- Filters messages from Axis Bank sender IDs
- Processes messages in real-time

### 2. Pattern Matching
The app uses multiple regex patterns to handle different SMS formats:

**Debit Transactions:**
```
Rs.150.00 debited from account ending 1234 at MANGARAM CHOWDARY on 03-10-25 20:05:30
```

**Credit Transactions:**
```
Rs.5000.00 credited to account ending 1234 from MADDU REV on 03-10-25 20:05:30
```

### 3. Data Extraction
- **Amount**: Extracted with decimal precision
- **Merchant**: Cleaned and formatted merchant name
- **Type**: Automatically determined (DEBIT/CREDIT)
- **Timestamp**: Parsed and converted to system timestamp
- **Transaction ID**: Generated using SMS content hash

### 4. Database Storage
- Duplicate detection prevents multiple entries
- Automatic categorization as SMS-based transactions
- Ready for user tagging and organization

## 💾 Database Schema

### Transaction Table
| Column | Type | Description |
|--------|------|-------------|
| id | String (PK) | Unique transaction identifier |
| amount | Double | Transaction amount |
| type | Enum | DEBIT or CREDIT |
| merchant | String | Merchant/sender name |
| timestamp | Long | Unix timestamp |
| tag | String? | User-assigned tag (nullable) |
| isManual | Boolean | Manual entry flag |

### Settings Storage
- Uses SharedPreferences for app settings
- Stores time format preference
- Manages notification settings
- Tracks first launch state

## 🔒 Permissions

### Required Permissions
- **SMS (READ_SMS, RECEIVE_SMS)**: For automatic transaction parsing
- **WRITE_EXTERNAL_STORAGE**: For CSV export (Android < 10)
- **POST_NOTIFICATIONS**: For transaction notifications

### Privacy Policy
- SMS data is processed locally only
- No data is sent to external servers
- Users can delete all data anytime
- No analytics or tracking implemented

## 🎯 Key Features Explained

### Untagged Transaction Management
- Automatically identifies transactions without tags
- Provides quick access to untagged items
- Yellow/amber accent color for "needs attention" items
- Batch tagging capabilities

### Data Export
- Export all transactions to CSV format
- Timestamped file names for organization
- Shareable via Android's share system
- Includes all transaction details and tags

### Time Format Toggle
- Switch between 12-hour (AM/PM) and 24-hour formats
- Applies to all time displays in the app
- Persistent setting across app restarts

## 🚧 Current Limitations

- **Single Bank Support**: Currently supports Axis Bank SMS format only
- **Manual Tag Management**: No AI-powered tag suggestions
- **Local Storage Only**: No cloud backup functionality
- **Basic Analytics**: No charts or spending insights

## 🔮 Future Enhancements (Roadmap)

- [ ] Multi-bank SMS support (SBI, HDFC, ICICI)
- [ ] AI-powered tag suggestions
- [ ] Spending analytics and charts
- [ ] Budget tracking and alerts
- [ ] Cloud backup and sync
- [ ] Recurring transaction detection
- [ ] Category-based expense tracking
- [ ] Export to multiple formats (PDF, Excel)

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

### Development Guidelines
- Follow Kotlin coding conventions
- Add comments for complex regex patterns
- Update README for new features
- Test SMS parsing with various message formats

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 👨‍💻 Developer

**Karthikeya Maddu**
- GitHub: [@karthikeyamaddu](https://github.com/karthikeyamaddu)
- Project: [Finance Tracker App](https://github.com/karthikeyamaddu/finance-tracker-app)

## 🙏 Acknowledgments

- Material Design 3 guidelines by Google
- Android Jetpack libraries
- Axis Bank for consistent SMS format
- Open source community for inspiration

---

**Made with ❤️ for better financial tracking**24%2B-bright