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


    def "when converting a list of maps then validate the result"() {
        given:
        def source = ['key1': ['key2': ['key3': [[lk1: 1], [lk2: 2]],
                                        'key4': 4]]]
        def mapping = ['key1.key2.key3': [destination: 'test.list',
                                          items      : [
                                                  lk1: [destination: 'test.k1'],
                                                  lk2: [destination: 'test.k2']
                                          ]]]

        when:
        def result = new AnyMapper(mapping).transform(source)

        then:
        println result
        assert result.test.list.size() == 2
        assert result.test.list[0].test.k1 == 1
        assert result.test.list[0].size() == 1
        assert result.test.list[0].test.size() == 1
        assert result.test.list[1].test.k2 == 2
        assert result.test.list[1].size() == 1
        assert result.test.list[1].test.size() == 1
    }

    def "when converting nested lists then validate the result"() {
        given:
        def source = ['key1': ['key2': ['key3': [[lk1: 1], [lk2: 2], [lk3: [[lk4: 4]]]],
                                        'key4': 4]]]
        def mapping = ['key1.key2.key3': [destination: 'test.list',
                                          items      : [
                                                  lk1: [destination: 'test.k1'],
                                                  lk2: [destination: 'test.k2'],
                                                  lk3: [destination: 'test.k3',
                                                        items: [
                                                                lk4: [destination: 'test.k4']
                                                        ]]
                                          ]]]

        when:
        def result = new AnyMapper(mapping).transform(source)

        then:
        println result
        assert result.test.list.size() == 3
        assert result.test.list[0].test.k1 == 1
        assert result.test.list[0].size() == 1
        assert result.test.list[0].test.size() == 1
        assert result.test.list[1].test.k2 == 2
        assert result.test.list[1].size() == 1
        assert result.test.list[1].test.size() == 1
        assert result.test.list[2].test.k3[0].test.k4 == 4
        assert result.test.list[2].size() == 1
        assert result.test.list[2].test.size() == 1
        assert result.test.list[2].test.k3.size() == 1
        assert result.test.list[2].test.k3[0].size() == 1
        assert result.test.list[2].test.k3[0].test.size() == 1
    }

    def "when converting multiple key to the same location in destination then validate the result"() {
        given:
        def source = ['key1': ['key2': ['key3': [[lk1: 1], [lk2: 2]],
                                        'key4': 4]]]
        def mapping = ['key1.key2.key3': [destination: 'test.list',
                                          items      : [
                                                  lk1: [destination: 'test.k1'],
                                                  lk2: [destination: 'test.k2']
                                          ]],
                       'key1.key2.key4': ['destination': 'test.x']]

        when:
        def result = new AnyMapper(mapping).transform(source)

        then:
        println result
        assert result.test.list[0].test.k1 == 1
        assert result.test.list[1].test.k2 == 2
        assert result.test.x == 4
    }

    def "when transforming and there are unmapped parameters then validate the result does not contain any extra fields"() {
        given:
        def source = ['key1': ['key2': ['key3': [[lk1: 1], [lk2: 2]],
                                        'key4': 4]],
                      key5  : 3]
        def mapping = ['key1.key2.key3': [destination: 'test.list',
                                          items      : [
                                                  lk1: [destination: 'test.k1'],
                                                  lk2: [destination: 'test.k2']
                                          ]],
                       'key1.key2.key4': ['destination': 'test.x']]

        when:
        def result = new AnyMapper(mapping).transform(source)

        then:
        println result
        assert result.test.list[0].test.size() == 1
        assert result.test.list[0].size() == 1
        assert result.test.size() == 2
        assert result.test.list[0].test.k1 == 1
        assert result.test.list[1].test.k2 == 2
        assert result.test.x == 4
    }

}
