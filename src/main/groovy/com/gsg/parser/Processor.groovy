package com.gsg.parser

import org.joda.time.DateTime

/**
 * Created by alwick on 8/18/2014.
 */
class Processor {

    public static void main( String[] inputs) {
        Processor p = new Processor();
        if( inputs.size() == 5 ) {
            p.convert(inputs[0], inputs[1], inputs[2], inputs[3], inputs[4] );
        }
        else {
            p.convert(inputs[0], inputs[1], inputs[2] );
        }
    }

    public Processor() {}

    public void convert(String rootDirectory, String resultsFilename, String startDate ) {
        convert( rootDirectory, resultsFilename, startDate, null, "" );
    }

    public void convert(String rootDirectory, String resultsFilename, String startDate, String replacementFilename, String outputDirectoryName ) {
        def outputDirectory = new File( outputDirectoryName );
        outputDirectory.mkdirs();

        def replacements = new FileReplacementMap();
        replacements.parseFile( replacementFilename );
        def factory = new WorkbookAnalysisFactory(map:replacements, outputDirectory: outputDirectory.getPath() );

        def results = factory.buildForDirectory( new File( rootDirectory ), DateTime.parse( startDate ) );
        new ResultsExport().writeResults( new File( resultsFilename ), results );
    }
}
