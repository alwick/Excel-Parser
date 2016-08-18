package com.gsg.parser

import org.joda.time.DateTime
import spock.lang.Ignore
import spock.lang.Specification

/**
 * Created by alwick on 1/29/2015.
 */
class WorkbookAnalysisFactorySpec extends Specification {

    def 'Find links'() {
        when:
        def factory = new WorkbookAnalysisFactory();
        def results = factory.buildForDirectory( new File( "src/test/resources" ), DateTime.parse('2015-01-01') );

        and:
        new ResultsExport().writeResults( new File( 'src/test/resources/changesOutput.csv' ), results );

        then:
        results;
        results.size() == 4;
        results[0].linkedFiles.size() == 0;
        results[1].linkedFiles.size() == 0;
        results[2].linkedFiles.size() == 1;
    }

    def 'Replace links'() {
        when:
        def output = new File('src/test/resources');
        output.mkdirs();

        and:
        def replacements = new FileReplacementMap();
        replacements.parseFile( 'src/test/resources/changesInput.csv' );
        def factory = new WorkbookAnalysisFactory(map: replacements, outputDirectory: output.getPath() );
        def results = factory.buildForDirectory( new File( "src/test/resources" ), DateTime.parse('2015-01-01') );

        and:
        new ResultsExport().writeResults( new File( 'src/test/resources/search.csv' ), results );

        then:
        results;
        results.size() == 4;
        results[0].linkedFiles.size() == 0;
        results[1].linkedFiles.size() == 0;
        results[2].linkedFiles.size() == 1;

        output.listFiles().size() == 7;
    }

    @Ignore
    def 'Test GQT files'() {
        when:
        def factory = new WorkbookAnalysisFactory();
        def results = factory.buildForDirectory( new File( "Z://Accounting/Budgets/2013/GSG" ) );

        and:

        then:
        results;
    }

    @Ignore
    def 'Test linked path'() {
        when:
        def factory = new WorkbookAnalysisFactory();
        def results = factory.buildForDirectory( new File( "Z://Accounting/Budgets" ) );

        and:
        new ResultsExport().writeResults( new File( "results.csv" ), results )

        then:
        results;
    }
}
