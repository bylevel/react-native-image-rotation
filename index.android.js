import React from 'react-native';

import { ImageRotationAndroid } from 'NativeModules';


export default {
    createRotationImage: function( image ) {
        return new Promise( ( resolve, reject ) => {
            if ( !image || !image.uri || !image.width || !image.height ) {
                reject( 'Required uri、 width、 height' );
            }

            let success = ( imgInfo ) => {
                resolve( { ...image, ...imgInfo } );
            };


            if ( image.degrees == undefined ) {
                image.degrees = 0;
            }

            if ( image.auto == undefined ) {
                image.auto = false;
            }

            ImageRotationAndroid.createRotationImage( image, success, reject );
        } );
    },
    getDegrees: function( imageUri ) {
        return new Promise( ( resolve, reject ) => {
            ImageRotationAndroid.getDegrees( imageUri, resolve );
        } );
    }
};
