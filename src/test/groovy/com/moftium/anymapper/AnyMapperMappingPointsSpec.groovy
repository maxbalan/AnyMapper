package com.moftium.anymapper

import spock.lang.Specification

class AnyMapperMappingPointsSpec extends Specification {

    def "when initialising a config then validate the mapping points"() {
        given:
        def source = ['key1': ['key2': ['key3': 1]]]
        def mapping = ['key1.key2.key3': ['destination': 'test']]

        when:
        def result = new AnyMapper(mapping)

        then:
        println result.mappingPoints
        assert result.mappingPoints.size() == 1
        assert result.mappingPoints[0].sourcePath().size() == 3
        assert result.mappingPoints[0].sourcePath().contains('key1')
        assert result.mappingPoints[0].sourcePath().contains('key2')
        assert result.mappingPoints[0].sourcePath().contains('key3')
        assert result.mappingPoints[0].destinationPath().size() == 1
        assert result.mappingPoints[0].destinationPath().contains('test')
        assert result.mappingPoints[0].isList() == false
    }

    def "when initialising a config and destination is nested then validate the mapping points"() {
        given:
        def source = ['key1': ['key2': ['key3': 1]]]
        def mapping = ['key1.key2.key3': ['destination': 'test.t1.t2.t3']]

        when:
        def result = new AnyMapper(mapping)

        then:
        println result.mappingPoints
        assert result.mappingPoints.size() == 1
        assert result.mappingPoints[0].sourcePath().size() == 3
        assert result.mappingPoints[0].sourcePath().contains('key1')
        assert result.mappingPoints[0].sourcePath().contains('key2')
        assert result.mappingPoints[0].sourcePath().contains('key3')
        assert result.mappingPoints[0].destinationPath().size() == 4
        assert result.mappingPoints[0].destinationPath().contains('test')
        assert result.mappingPoints[0].destinationPath().contains('t1')
        assert result.mappingPoints[0].destinationPath().contains('t2')
        assert result.mappingPoints[0].destinationPath().contains('t3')
        assert result.mappingPoints[0].isList() == false
    }


    // this is useless
    def "when key value is a list then validate the mapping points"() {
        given:
        def source = ['key1': ['key2': ['key3': [1, 2, 2, 3]]]]
        def mapping = ['key1.key2.key3': ['destination': 'test']]

        when:
        def result = new AnyMapper(mapping)

        then:
        println result.mappingPoints
        assert result.mappingPoints.size() == 1
        assert result.mappingPoints[0].sourcePath().size() == 3
        assert result.mappingPoints[0].sourcePath().contains('key1')
        assert result.mappingPoints[0].sourcePath().contains('key2')
        assert result.mappingPoints[0].sourcePath().contains('key3')
        assert result.mappingPoints[0].destinationPath().size() == 1
        assert result.mappingPoints[0].destinationPath().contains('test')
        assert result.mappingPoints[0].isList() == false
    }

    def "when key value is a map then validate the mapping points"() {
        given:
        def source = ['key1': ['key2': ['key3': [1, 2, 2, 3]]]]
        def mapping = ['key1.key2': ['destination': 'test']]

        when:
        def result = new AnyMapper(mapping)

        then:
        println result.mappingPoints
        assert result.mappingPoints.size() == 1
        assert result.mappingPoints[0].sourcePath().size() == 2
        assert result.mappingPoints[0].sourcePath().contains('key1')
        assert result.mappingPoints[0].sourcePath().contains('key2')
        assert result.mappingPoints[0].destinationPath().size() == 1
        assert result.mappingPoints[0].destinationPath().contains('test')
        assert result.mappingPoints[0].isList() == false
    }

    def "when parsing a list config then validate the mapping points"() {
        given:
        def source = ['key1': ['key2': ['key3': [[lk1: 1], [lk2: 2]],
                                        'key4': 4]]]
        def mapping = ['key1.key2.key3': [destination: 'test.list',
                                          items      : [
                                                  lk1: [destination: 'test.k1'],
                                                  lk2: [destination: 'test.k2']
                                          ]]]

        when:
        def result = new AnyMapper(mapping)

        then:
        println result.mappingPoints
        assert result.mappingPoints.size() == 1
        assert result.mappingPoints[0].sourcePath().size() == 3
        assert result.mappingPoints[0].sourcePath().contains('key1')
        assert result.mappingPoints[0].sourcePath().contains('key2')
        assert result.mappingPoints[0].sourcePath().contains('key3')
        assert result.mappingPoints[0].destinationPath().size() == 2
        assert result.mappingPoints[0].destinationPath().contains('test')
        assert result.mappingPoints[0].destinationPath().contains('list')
        assert result.mappingPoints[0].isList() == true
        //children
        assert result.mappingPoints[0].children().size() == 2
        assert result.mappingPoints[0].children()[0].sourcePath().size() == 1
        assert result.mappingPoints[0].children()[0].sourcePath().contains('lk2')
        assert result.mappingPoints[0].children()[0].destinationPath().size() == 2
        assert result.mappingPoints[0].children()[0].destinationPath().contains('test')
        assert result.mappingPoints[0].children()[0].destinationPath().contains('k2')
        assert result.mappingPoints[0].children()[0].isList() == false
        assert result.mappingPoints[0].children()[1].sourcePath().size() == 1
        assert result.mappingPoints[0].children()[1].sourcePath().contains('lk1')
        assert result.mappingPoints[0].children()[1].destinationPath().size() == 2
        assert result.mappingPoints[0].children()[1].destinationPath().contains('test')
        assert result.mappingPoints[0].children()[1].destinationPath().contains('k1')
        assert result.mappingPoints[0].children()[1].isList() == false
    }

    def "when parsing a list config and changing the structure for one then validate the mapping points"() {
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
        def result = new AnyMapper(mapping)

        then:
        println result.mappingPoints
        assert result.mappingPoints.size() == 1
        assert result.mappingPoints[0].sourcePath().size() == 3
        assert result.mappingPoints[0].sourcePath().contains('key1')
        assert result.mappingPoints[0].sourcePath().contains('key2')
        assert result.mappingPoints[0].sourcePath().contains('key3')
        assert result.mappingPoints[0].destinationPath().size() == 2
        assert result.mappingPoints[0].destinationPath().contains('test')
        assert result.mappingPoints[0].destinationPath().contains('list')
        assert result.mappingPoints[0].isList() == true
        //children
        assert result.mappingPoints[0].children().size() == 3
        assert result.mappingPoints[0].children()[0].sourcePath().size() == 1
        assert result.mappingPoints[0].children()[0].sourcePath().contains('lk2')
        assert result.mappingPoints[0].children()[0].destinationPath().size() == 2
        assert result.mappingPoints[0].children()[0].destinationPath().contains('test')
        assert result.mappingPoints[0].children()[0].destinationPath().contains('k2')
        assert result.mappingPoints[0].children()[0].isList() == false
        assert result.mappingPoints[0].children()[1].sourcePath().size() == 1
        assert result.mappingPoints[0].children()[1].sourcePath().contains('lk1')
        assert result.mappingPoints[0].children()[1].destinationPath().size() == 2
        assert result.mappingPoints[0].children()[1].destinationPath().contains('test')
        assert result.mappingPoints[0].children()[1].destinationPath().contains('k1')
        assert result.mappingPoints[0].children()[1].isList() == false
        println result.mappingPoints[0].children()[2]
        assert result.mappingPoints[0].children()[2].sourcePath().size() == 1
        assert result.mappingPoints[0].children()[2].sourcePath().contains('lk3')
        assert result.mappingPoints[0].children()[2].destinationPath().size() == 2
        assert result.mappingPoints[0].children()[2].destinationPath().contains('test')
        assert result.mappingPoints[0].children()[2].destinationPath().contains('k3')
        assert result.mappingPoints[0].children()[2].isList() == true
        assert result.mappingPoints[0].children()[2].children().size() == 1
        assert result.mappingPoints[0].children()[2].children()[0].sourcePath().size() == 1
        assert result.mappingPoints[0].children()[2].children()[0].sourcePath().contains('lk4')
        assert result.mappingPoints[0].children()[2].children()[0].destinationPath().size() == 2
        assert result.mappingPoints[0].children()[2].children()[0].destinationPath().contains('test')
        assert result.mappingPoints[0].children()[2].children()[0].destinationPath().contains('k4')
        assert result.mappingPoints[0].children()[2].children()[0].isList() == false
    }

}
