# Praktikum Mobile Sensing for Fitness and Wellbeing

![structure](pictures/app_logo.png "Overview")

### Installation

1. Check out repositiory 

2. Import with android studio

3. Build and deploy to device

4. Take the audio files from giantsteps-tempo-dataset/audio directory and put them on a folder on the device

5. Take the bpm files form giantsteps-tempo-dataset/annotations/tempo and put them in the same folder like the audio files

6. Take the genre files from giantsteps-tempo-dataset/annotations/gener and put them in the same folder like the audio files

7. Start application and configure the media directory in the settings if necessary (app has to be restarted afterwards!)

IMPORTANT: Every .mp3 file has to have a corresponding .bpm and .genre file, otherwise the app will crash.


### Idea
This project aims to build a music player application for mobile devices. Unlike other music player apps, that involve users choice of music, it interacts with several sensors to provide songs, which have BPM (beats per minute) rates correlating to the actual moving speed. Thus the user of this application is accompanied with music, that fits his or her walking pace.

### Structure

![structure](pictures/app-design.png "Overview")

### Sensors

To achieve the desired results there are some sensors implemented. Each sensor provides desired BPM values for the current walking pace.
 
#### GPS Sensor
The GPS sensor is part of almost all smartphones and therefore a reliable source of movement data. In this project it's used to obtain the current moving speed. 

The actual speed information is obtained in meters per second. This value than is multiplied with the half of the users height to get a reliable and height-dependent desired BPM value. This equation ensures that smaller users speed are differently valuated than taller ones. This is neccessary because categorizing moving speed in 'slow' or 'fast' isn't a easy task and has strong relationship with the height of the user.

#### Accelerometer

### Services

To give best user experience the application makes use of Android services. Thus the application can be put to background or the device can be locked, without any influence to the media playback and sensor logic.

There are two services:

1. Media service: Plays media files, listenes for new media to play and provides information of current playback (e.g. progression).

2. Sensor service: Polls sensor data and decides whether to change the currently played song or not. Also holds the music library which is responsable for choosing the next suitable song if requested.

These services have to interact with eachother and with the GUI (activity). This communicaion is implemented using the broadcast pattern of the Android system.