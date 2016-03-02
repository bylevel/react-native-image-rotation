export default {
    createRotationImage: ( image ) => {
        return new Promise( ( resolve, reject ) => {
            if ( !image || !image.uri || !image.width || !image.height ) {
                reject( 'Required uri、 width、 height' );
            }
            resolve( image );
        } );
    },
    getDegrees( imageUri ) {
        return 0;
    }
};
