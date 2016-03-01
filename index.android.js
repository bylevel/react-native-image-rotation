import React from 'react-native';

const ImageRotationAndroid = React.NativeModules.ImageRotationAndroid;

export default {
    createRotationImage: ( image ) => {
        return new Promise( ( resolve, reject ) => {
            if ( !image || !image.uri || image.width || image.height ) {
                reject( 'Required uri、 width、 height' );
            }

            let success = ( imgInfo ) => {
                resolve( { ...image, ...imgInfo } );
            };

            ImageRotationAndroid.createRotationImage( imageUri, success, reject );
        } );
    },
};
