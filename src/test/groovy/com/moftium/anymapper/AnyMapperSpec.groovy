package com.moftium.anymapper

import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class AnyMapperSpec extends Specification {
    def "when value is an [ #name ] then validate the result"() {
        given:
        def source = ['key1': ['key2': ['key3': value]]]
        def mapping = ['key1.key2.key3': ['destination': 'test']]

        when:
        def result = new AnyMapper(mapping).transform(source)

        then:
        println result
        assert result.test == value

        where:
        name     | value
        'INT'    | 1
        'STRING' | "test String"
    }

    def "when destination keys are numbers then those should be treated as strings"() {
        given:
        def source = ['key1': ['key2': ['key3': 1]]]
        def mapping = ['key1.key2.key3': ['destination': 'test.1.2.3']]

        when:
        def result = new AnyMapper(mapping).transform(source)

        then:
        println result
        assert result.test.'1'.'2'.'3' == 1
    }

    def "when destination is nested those should be treated as strings"() {
        given:
        def source = ['key1': ['key2': ['key3': 1]]]
        def mapping = ['key1.key2.key3': ['destination': 'test.t1.t2.t3']]

        when:
        def result = new AnyMapper(mapping).transform(source)

        then:
        println result
        assert result.test.t1.t2.t3 == 1
    }

    def "when value is a list then validate the result"() {
        given:
        def source = ['key1': ['key2': ['key3': [1, 2, 2, 3]]]]
        def mapping = ['key1.key2.key3': ['destination': 'test']]

        when:
        def result = new AnyMapper(mapping).transform(source)

        then:
        println result
        assert result.test.size() == 4
        assert result.test[0] == 1
        assert result.test[1] == 2
        assert result.test[2] == 2
        assert result.test[3] == 3
    }

    def "when value is a map then validate the result"() {
        given:
        def source = ['key1': ['key2': ['key3': [1, 2, 2, 3]]]]
        def mapping = ['key1.key2': ['destination': 'test']]

        when:
        def result = new AnyMapper(mapping).transform(source)

        then:
        println result
        assert result.test.key3.size() == 4
        assert result.test.key3[0] == 1
        assert result.test.key3[1] == 2
        assert result.test.key3[2] == 2
        assert result.test.key3[3] == 3
    }



}
