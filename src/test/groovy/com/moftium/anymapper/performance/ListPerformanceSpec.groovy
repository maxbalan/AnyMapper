package com.moftium.anymapper.performance

import com.moftium.anymapper.AnyMapper
import spock.lang.Specification

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

class ListPerformanceSpec extends Specification {

    def "benchmark list map performance"() {
        given:
        int iterations = 1000
        int warmup = 100
        int keys = 20
        def times = []

        Map<String, Object> source = (1..keys).collectEntries { i ->
            [
                    ("rootList$i".toString()): nestedMap(i)
            ]
        }

        // Define mapping for list items
        Map<String, Object> mappingConfig = (1..keys).collectEntries { i ->
            [
                    ("root$i".toString()): nestedMapConfig(i)
            ]
        }

        AnyMapper mapper = new AnyMapper(mappingConfig)

        // Warm-up phase
        1.upto(warmup) {
            mapper.transform(source)
        }

        when:
        iterations.times {
            long start = System.nanoTime()
            mapper.transform(source)
            long end = System.nanoTime()
            times << (end - start) / 1_000_000.0 // ms
        }

        then:
        saveCsv("benchmark_list.csv", times)
    }

    Map<String, Object> generateNestedListMap(int keys, int listKeys, int nestingLevels) {
        (1..keys).collectEntries { keyIndex ->
            String rootKey = ("rootList$keyIndex".toString())
            [
                    (rootKey): (1..2).collect { itemIndex ->
                        generateNestedEntry(("v${keyIndex}_${itemIndex}".toString()), listKeys, nestingLevels)
                    }
            ]
        }
    }

    private Map<String, Object> generateNestedEntry(String baseValue, int listKeys, int nestingLevel) {
        if (nestingLevel == 0) {
            return (1..listKeys).collectEntries { i ->
                [("key$i".toString()): "$baseValue-$i"]
            }
        }

        (1..listKeys).collectEntries { i ->
            if (i == 1) {
                // This key will recurse deeper
                [("key$i".toString()): (1..2).collect { idx ->
                    generateNestedEntry(("${baseValue}_lvl$nestingLevel-$idx".toString()), listKeys, nestingLevel - 1)
                }]
            } else {
                // Flat key
                [("key$i".toString()): ("$baseValue-flat$i".toString())]
            }
        }
    }

    private Map<String, Object> nestedMap(int i) {
        [
                ("key$i".toString()): [ // level 1
                                        [
                                                ("key$i".toString()): [ // level 2
                                                                        [
                                                                                ("key$i".toString()): [ // level 3
                                                                                                        [
                                                                                                                ("key$i".toString()): [ // level 4
                                                                                                                                        [
                                                                                                                                                key1: "val-l5-a",
                                                                                                                                                key2: "val-l5-b"
                                                                                                                                        ]
                                                                                                                ],
                                                                                                                key2                : "val-l4-flat"
                                                                                                        ]
                                                                                ],
                                                                                key2                : "val-l3-flat"
                                                                        ]
                                                ],
                                                key2                : "val-l2-flat"
                                        ]
                ],
                key2                : "val-l1-flat"
        ]
    }

    private Map<String, Object> nestedMapConfig(int i) {
        [
                type       : "list",
                destination: "mappedRootList1",
                item       : [
                        destination         : "rootItemDest",
                        ("key$i".toString()): [
                                type       : "list",
                                destination: "nestedListLvl1",
                                item       : [
                                        destination         : "nestedItemLvl1",
                                        ("key$i".toString()): [
                                                type       : "list",
                                                destination: "nestedListLvl2",
                                                item       : [
                                                        destination         : "nestedItemLvl2",
                                                        ("key$i".toString()): [
                                                                type       : "list",
                                                                destination: "nestedListLvl3",
                                                                item       : [
                                                                        destination         : "nestedItemLvl3",
                                                                        ("key$i".toString()): [
                                                                                type       : "list",
                                                                                destination: "nestedListLvl4",
                                                                                item       : [
                                                                                        destination: "nestedItemLvl4",
                                                                                        key1       : [destination: "leafKey1"],
                                                                                        key2       : [destination: "leafKey2"]
                                                                                ]
                                                                        ],
                                                                        key2                : [destination: "flatKeyL4"]
                                                                ]
                                                        ],
                                                        key2                : [destination: "flatKeyL3"]
                                                ]
                                        ],
                                        key2                : [destination: "flatKeyL2"]
                                ]
                        ],
                        key2                : [destination: "flatKeyL1"]
                ]
        ]
    }

    private static void saveCsv(String filename, List<Double> times) {
        Path reportDir = Paths.get("resources/benchmark/data")
        Files.createDirectories(reportDir)
        Path file = reportDir.resolve(filename)

        List<String> lines = ["Iteration,ExecutionTimeMs"]
        times.eachWithIndex { t, i ->
            lines << "${i + 1},${t.round(4)}"
        }

        Files.write(file, lines)
        println "Results written to: ${file.toAbsolutePath()}"
        println "Average: ${(times.sum() / times.size()).round(4)} ms"
    }
}