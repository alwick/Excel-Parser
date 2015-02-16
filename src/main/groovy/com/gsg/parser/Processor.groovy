package com.gsg.parser

import org.joda.time.DateTime

/**
 * Created by alwick on 8/18/2014.
 */
class Processor {

    public static void main( String[] inputs) {
        Processor p = new Processor();
        if( inputs.size() == 4 ) {
            p.convert(inputs[0], inputs[1], inputs[2], inputs[3] );
        }
        else {
            p.convert(inputs[0], inputs[1], inputs[2], null );
        }
    }

    public Processor() {}

    public void convert(String rootDirectory, String resultsFilename, String startDate, String replacementFilename ) {
        def replacementsFile = new File( replacementFilename );
        replacementsFile.mkdirs();

        def replacements = new FileReplacementMap();
        replacements.parseFile( replacementFilename );
        def factory = new WorkbookAnalysisFactory(map:replacements, outputDirectory: replacementsFile.getPath() );

        def results = factory.buildForDirectory( new File( rootDirectory ), DateTime.parse( startDate ) );
        new ResultsExport().writeResults( new File( resultsFilename ), results );
    }
}
