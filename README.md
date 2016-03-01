# React Native Image Rotation

A React Native module that can create rotate versions of local images.

## Setup

First, install the package:
```
npm install react-native-image-rotation
```

Then, follow those instructions:

### Android

#### Update your gradle files

For **react-native >= v0.15**, this command will do it automatically:
```
react-native link react-native-image-rotation
```

## Usage example

```javascript
var ImageRotation = require('react-native-image-rotation');

ImageRotation.createRotationImage(imageUri).then((rotationImageUri) => {
  // rotationImageUri is the URI of the new image that can now be displayed, uploaded...
});
```

## API

### `promise createRotationImage(uri)`

The promise resolves with a string containing the uri of the new file.
