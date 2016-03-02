export default {
    createRotationImage: function( image ) {
        return new Promise( ( resolve, reject ) => {
            if ( !image || !image.uri || !image.width || !image.height ) {
                reject( 'Required uri、 width、 height' );
            }
            resolve( image );
        } );
    },
    getDegrees: function( imageUri ) {
        return new Promise( ( resolve, reject ) => {
            resolve( 0 );
        } );
    }
};
