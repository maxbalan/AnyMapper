package com.moftium.anymapper

import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class AnyMapperTest extends Specification {

    def "when converting a nest map to single level then validate the result"() {
        given:
        def source = ['key1': ['key2': ['key3': 1]]]
        def mapping = ['key1.key2.key3': ['destination': 'test']]

        when:
        def result = AnyMapper.transform(source, mapping)

        then:
        println result
        assert result.test == 1
    }

    def "when converting a nest map to multi level then validate the result"() {
        given:
        def source = ['key1': ['key2': ['key3': 1]]]
        def mapping = ['key1.key2.key3': ['destination': 'test.1.2.3']]

        when:
        def result = AnyMapper.transform(source, mapping)

        then:
        println result
        assert result.test.'1'.'2'.'3' == 1
    }

    def "when converting a list then validate the result"() {
        given:
        def source = ['key1': ['key2': ['key3': [1, 2, 2, 3]]]]
        def mapping = ['key1.key2.key3': ['destination': 'test']]

        when:
        def result = AnyMapper.transform(source, mapping)

        then:
        println result
        assert result.test.size() == 4
    }

    def "when converting a list of maps then validate the result"() {
        given:
        def source = ['key1': ['key2': ['key3': [[lk1: 1], [lk2: 2]],
                                        'key4': 4]]]
        def mapping = ['key1.key2.key3': [lk1        : ['destination': 'test.k1'],
                                          lk2        : ['destination': 'test.k2'],
                                          destination: 'test.list',
                                          type       : 'list']]

        when:
        def result = AnyMapper.transform(source, mapping)

        then:
        println result
        assert result.test.list[0].test.k1 == 1
        assert result.test.list[1].test.k2 == 2
    }

    def "when converting nested lists then validate the result"() {
        given:
        def source = ['key1': ['key2': ['key3': [[lk1: 1], [lk2: 2], [lk3: [[lk4: 4]]]],
                                        'key4': 4]]]
        def mapping = ['key1.key2.key3': [lk1        : ['destination': 'test.k1'],
                                          lk2        : ['destination': 'test.k2'],
                                          destination: 'test.list',
                                          type       : 'list',
                                          lk3        : ['destination': 'test.k3',
                                                        type         : 'list',
                                                        lk4          : ['destination': 'test.k4']]],
        ]

        when:
        def result = AnyMapper.transform(source, mapping)

        then:
        println result
        assert result.test.list[0].test.k1 == 1
        assert result.test.list[1].test.k2 == 2
        assert result.test.list[2].test.k3[0].test.k4 == 4
    }

    def "when converting a list of maps and extra parameters then validate the result"() {
        given:
        def source = ['key1': ['key2': ['key3': [[lk1: 1], [lk2: 2]],
                                        'key4': 4]]]
        def mapping = ['key1.key2.key3': [lk1        : ['destination': 'test.k1'],
                                          lk2        : ['destination': 'test.k2'],
                                          destination: 'test.list',
                                          type       : 'list'],
                       'key1.key2.key4': ['destination': 'test.x']]

        when:
        def result = AnyMapper.transform(source, mapping)

        then:
        println result
        assert result.test.list[0].test.k1 == 1
        assert result.test.list[1].test.k2 == 2
        assert result.test.x == 4
    }

    def "when converting a list of maps and extra parameters with unmapped fields then validate the result does not contain any extra fields"() {
        given:
        def source = ['key1': ['key2': ['key3': [[lk1: 1], [lk2: 2]],
                                        'key4': 4]],
                      key5  : 3]
        def mapping = ['key1.key2.key3': [lk1        : ['destination': 'test.k1'],
                                          lk2        : ['destination': 'test.k2'],
                                          destination: 'test.list',
                                          type       : 'list'],
                       'key1.key2.key4': ['destination': 'test.x']]

        when:
        def result = AnyMapper.transform(source, mapping)

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
