package com.gsg.parser

import spock.lang.Specification

/**
 * Created by alwick on 1/29/2015.
 */
class DirectoryParserSpec extends Specification {

    def "Test building workbook"() {
        given:
        def DirectoryParser parser = new DirectoryParser();
        def files = parser.getExcelFiles( "src/test/resources" );

        expect:
        files;
        files.size() == 4;
    }
}
