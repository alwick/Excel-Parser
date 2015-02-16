package com.gsg.parser

import spock.lang.Ignore
import spock.lang.Specification

/**
 * Created by alwick on 1/29/2015.
 */
class ProcessorSpec extends Specification {

    //@Ignore
    def 'Test run'() {
        when:
        new Processor().convert( "z:/ACCT", "2015-01-19-acct.csv", "2011-01-01" );

        then:
        1 == 1
    }
}
