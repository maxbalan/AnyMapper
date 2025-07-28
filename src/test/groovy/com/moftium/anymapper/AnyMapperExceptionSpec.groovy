package com.moftium.anymapper

import com.moftium.anymapper.config.AnyMapperConfig
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

    def "when exceeding nesting level of configuration then expect an exception"() {
        given:
        def mapping = ['key1.key2.key3': ['destination': 'test1'],
                       'key1.key2.key4': ['destination': 'test2'],
                       'key1.key2.key5': ['destination': 'test3'],
                       'key1.key2.key6': ['destination': 'level2',
                                          type         : 'list',
                                          k1           : [destination: 'lvl2_k1'],
                                          k2           : [destination: 'level3',
                                                          type       : 'list',
                                                          k3         : [destination: 'lvl3_k1'],
                                                          k4         : [destination: 'level4',
                                                                        type       : 'list',
                                                                        k5         : [destination: 'lvl4_k1']]],
                       ]
        ]

        when:
        new AnyMapper(mapping, new AnyMapperConfig(2))

        then:
        def e = thrown(AnyMapperConfigParserException)
        e.getMessage() == "config nesting level is > 2"
    }
}
