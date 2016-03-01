import React from 'react-native';

const ImageRotationAndroid = React.NativeModules.ImageRotationAndroid;

export default {
    createRotationImage: ( imageUri ) => {
        return new Promise( ( resolve, reject ) => {
            ImageRotationAndroid.createRotationImage( imageUri, resolve, reject );
        } );
    },
};
