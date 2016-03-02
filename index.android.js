import React from 'react-native';

import { ImageRotationAndroid } from 'NativeModules';

export default {
    createRotationImage: ( image ) => {
        return new Promise( ( resolve, reject ) => {
            if ( !image || !image.uri || !image.width || !image.height ) {
                reject( 'Required uri、 width、 height' );
            }

            let success = ( imgInfo ) => {
                resolve( { ...image, ...imgInfo } );
            };

            ImageRotationAndroid.createRotationImage( image, success, reject );
        } );
    },
    getDegrees( imageUri ) {
        return ImageRotationAndroid.getDegrees( imageUri );
    }
};
