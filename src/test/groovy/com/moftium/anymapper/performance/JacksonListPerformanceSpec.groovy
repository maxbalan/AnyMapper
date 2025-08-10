package com.moftium.anymapper.performance

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import com.moftium.anymapper.AnyMapper
import spock.lang.Specification

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

class JacksonListPerformanceSpec extends Specification {

    def "benchmark list performance"() {
        given:
        def mapper = new ObjectMapper()
        int iterations = 1000
        int warmup = 100
        int keys = 20
        def times = []

        Map<String, Object> source = (1..keys).collectEntries { i ->
            [
                    ("rootList$i".toString()): nestedMap(i)
            ]
        }

        String jSource = mapper.writeValueAsString(source)

        // Warm-up phase
        1.upto(warmup) {
            mapper.readValue(jSource, DestinationStructure)
        }

        when:
        iterations.times {
            long start = System.nanoTime()

            mapper.readValue(jSource, DestinationStructure)

            long end = System.nanoTime()
            times << (end - start) / 1_000_000.0 // ms
        }

        then:
        saveCsv("benchmark_jackson_list.csv", times)
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

    static class DestinationStructure {

        @JsonProperty("mappedRootList1")
        private List<MappedRootList1Item> mappedRootList1;

        public List<MappedRootList1Item> getMappedRootList1() {
            return mappedRootList1;
        }

        public void setMappedRootList1(List<MappedRootList1Item> mappedRootList1) {
            this.mappedRootList1 = mappedRootList1;
        }

        public static class MappedRootList1Item {

            @JsonProperty("nestedListLvl1")
            private List<NestedListLvl1Item> nestedListLvl1;

            @JsonProperty("flatKeyL1")
            private String flatKeyL1;

            public List<NestedListLvl1Item> getNestedListLvl1() {
                return nestedListLvl1;
            }

            public void setNestedListLvl1(List<NestedListLvl1Item> nestedListLvl1) {
                this.nestedListLvl1 = nestedListLvl1;
            }

            public String getFlatKeyL1() {
                return flatKeyL1;
            }

            public void setFlatKeyL1(String flatKeyL1) {
                this.flatKeyL1 = flatKeyL1;
            }
        }

        public static class NestedListLvl1Item {

            @JsonProperty("nestedListLvl2")
            private List<NestedListLvl2Item> nestedListLvl2;

            @JsonProperty("flatKeyL2")
            private String flatKeyL2;

            public List<NestedListLvl2Item> getNestedListLvl2() {
                return nestedListLvl2;
            }

            public void setNestedListLvl2(List<NestedListLvl2Item> nestedListLvl2) {
                this.nestedListLvl2 = nestedListLvl2;
            }

            public String getFlatKeyL2() {
                return flatKeyL2;
            }

            public void setFlatKeyL2(String flatKeyL2) {
                this.flatKeyL2 = flatKeyL2;
            }
        }

        public static class NestedListLvl2Item {

            @JsonProperty("nestedListLvl3")
            private List<NestedListLvl3Item> nestedListLvl3;

            @JsonProperty("flatKeyL3")
            private String flatKeyL3;

            public List<NestedListLvl3Item> getNestedListLvl3() {
                return nestedListLvl3;
            }

            public void setNestedListLvl3(List<NestedListLvl3Item> nestedListLvl3) {
                this.nestedListLvl3 = nestedListLvl3;
            }

            public String getFlatKeyL3() {
                return flatKeyL3;
            }

            public void setFlatKeyL3(String flatKeyL3) {
                this.flatKeyL3 = flatKeyL3;
            }
        }

        public static class NestedListLvl3Item {

            @JsonProperty("nestedListLvl4")
            private List<NestedListLvl4Item> nestedListLvl4;

            @JsonProperty("flatKeyL4")
            private String flatKeyL4;

            public List<NestedListLvl4Item> getNestedListLvl4() {
                return nestedListLvl4;
            }

            public void setNestedListLvl4(List<NestedListLvl4Item> nestedListLvl4) {
                this.nestedListLvl4 = nestedListLvl4;
            }

            public String getFlatKeyL4() {
                return flatKeyL4;
            }

            public void setFlatKeyL4(String flatKeyL4) {
                this.flatKeyL4 = flatKeyL4;
            }
        }

        public static class NestedListLvl4Item {

            @JsonProperty("leafKey1")
            private String leafKey1;

            @JsonProperty("leafKey2")
            private String leafKey2;

            public String getLeafKey1() {
                return leafKey1;
            }

            public void setLeafKey1(String leafKey1) {
                this.leafKey1 = leafKey1;
            }

            public String getLeafKey2() {
                return leafKey2;
            }

            public void setLeafKey2(String leafKey2) {
                this.leafKey2 = leafKey2;
            }
        }
    }
}