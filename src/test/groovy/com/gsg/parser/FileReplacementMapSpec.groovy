package com.gsg.parser

import spock.lang.Specification

/**
 * Created by alwick on 2/16/2015.
 */
class FileReplacementMapSpec extends Specification {

    def 'Test parsing input file'() {
        when:
        def map = new FileReplacementMap();
        map.parseFile( 'src/test/resources/changesInput.csv' );

        then:
        def replacements = map.get('src\\test\\resources\\MainWorkbook.xlsx' );
        replacements;
        replacements.get( 'file:///C:/Users/alwick/IdeaProjects/GSG/Excel-Parser/src/test/resources/EmbeddedWorkbook.xlsx' );
    }
}
