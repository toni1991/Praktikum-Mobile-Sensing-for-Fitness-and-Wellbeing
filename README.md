# Praktikum Mobile Sensing for Fitness and Wellbeing

### Application


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
