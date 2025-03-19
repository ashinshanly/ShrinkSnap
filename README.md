# ShrinkSnap

**ShrinkSnap** is a modern Android application designed to efficiently compress photos on your device, saving valuable storage space while maintaining reasonable image quality. Built with Jetpack Compose, the app features fluid animations and a highly interactive user interface.

## Features

- **Smart Photo Analysis**: Analyzes photos based on age criteria (days, weeks, months, or years)
- **Efficient Compression**: Reduces photo file sizes while preserving acceptable quality
- **Storage Optimization**: Automatically removes original photos after compression
- **Beautiful UI**: Modern Material 3 design with fluid animations
- **Detailed Statistics**: Shows compression results with clear metrics of space saved
- **Interactive Elements**: Responsive UI with haptic feedback and visual cues

## Screenshots

<p align="center">
  <img src="screenshots/home_screen.png" width="200" alt="Home Screen"/>
  <img src="screenshots/compress_screen.png" width="200" alt="Compress Screen"/>
  <img src="screenshots/results_screen.png" width="200" alt="Results Screen"/>
</p>

## Technical Implementation

### Architecture

ShrinkSnap follows a clean architecture approach with separation of concerns:

- **UI Layer**: Implemented with Jetpack Compose and follows Material 3 design guidelines
- **Domain Layer**: Contains business logic for photo analysis and compression
- **Data Layer**: Handles file management and persistence

### Libraries and Technologies

- **Jetpack Compose**: Modern toolkit for building native Android UI
- **Material 3**: Latest Material Design components and theming
- **Kotlin Coroutines**: For managing background operations
- **Navigation Compose**: For handling in-app navigation
- **Zelory Compressor**: For efficient photo compression
- **Kotlin Flow**: For reactive programming and data streaming
- **Coil/Glide**: For image loading and caching

### Animation Techniques

The app features advanced animation patterns implemented using Compose Animation API:

- **Parallax Scrolling**: Creates depth in scrollable elements
- **Staggered Animations**: Elements enter screens with choreographed timing
- **Physics-Based Animations**: Using spring and exponential easing for natural motion
- **Interactive Animations**: Elements respond to user interactions with hover and press effects
- **Particle System**: Custom particle animation for success celebration
- **Shimmer Effects**: Loading states with gradient animations

## Getting Started

### Prerequisites

- Android Studio Arctic Fox (2021.3.1) or newer
- Android SDK 26 (Android 8.0) or higher
- JDK 17

### Building the App

1. Clone the repository:
```bash
git clone https://github.com/yourusername/ShrinkSnap.git
```

2. Open the project in Android Studio

3. Sync Gradle and build the project:
```bash
./gradlew build
```

4. Run on an emulator or physical device:
```bash
./gradlew installDebug
```

## Usage

1. **Home Screen**: Start your compression journey
2. **Compress Screen**: 
   - Set your time criteria (photos older than X days/weeks/months/years)
   - Analyze your photo library
   - Review potential space savings
3. **Results Screen**:
   - Watch the compression progress
   - View detailed statistics after completion
   - Return to home screen when done

## Important Notes

- Original photos will be **deleted** after compression
- Compressed versions are saved with a "_compressed" suffix in the same directory
- The app requires storage permissions to access and modify photos

## Permissions

The app requires the following permissions:

- `READ_EXTERNAL_STORAGE` (Android 12 and below)
- `READ_MEDIA_IMAGES` (Android 13+)
- `WRITE_EXTERNAL_STORAGE` (Android 9 and below)
- `POST_NOTIFICATIONS` (Android 13+)

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Author

[@ashinshanly](https://github.com/ashinshanly)

## Acknowledgments

- Material Design 3 for UI inspiration
- Android Jetpack team for Compose
- All open-source libraries used in this project 
