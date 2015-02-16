package com.gsg.parser

/**
 * Created by alwick on 2/16/2015.
 */
class FileReplacementMap {
    def Map<String,HashMap<String,String>> map = new HashMap<>();

    def parseFile( String filename ) {
        if( filename == null ) return;

        String[] data = new File( filename ) as String[];

        for( int index = 0;index < data.length;index ++ ) {
            def row = data[index].split( ',' );

            if( row.size() == 4 ) {
                def replacement = new HashMap<String,String>();
                replacement.put(row[2], row[3] );
                map.put( row[0] + File.separator + row[1], replacement );
            }
        }
    }

    def get( String key ) {
        return map.get( key );
    }
}
