package com.moftium.anymapper

import com.moftium.anymapper.exception.AnyMapperConfigParserException
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class AnyMapperExceptionSpec extends Specification {
    def "when configuration is not of the expected type then expect an exception"() {
        given:
        def source = ['key1': ['key2': ['key3': 4]]]
        def mapping = ['key1.key2.key3': 4]

        when:
        new AnyMapper(mapping).transform(source)

        then:
        def e = thrown(AnyMapperConfigParserException)
        e.getMessage() == "config key [key1.key2.key3] is not a map"
    }

    def "when configuration is missing destination then expect an exception"() {
        given:
        def source = ['key1': ['key2': ['key3': 4]]]
        def mapping = ['key1.key2.key3': ['t': 'test']]

        when:
        new AnyMapper(mapping).transform(source)

        then:
        def e = thrown(AnyMapperConfigParserException)
        e.getMessage() == "config key [key1.key2.key3] missing 'destination' field"
    }
}
