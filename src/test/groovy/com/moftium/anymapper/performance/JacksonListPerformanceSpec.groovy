package com.moftium.anymapper.performance

import com.fasterxml.jackson.annotation.JsonAnySetter
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonToken
import com.fasterxml.jackson.core.ObjectCodec
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
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
            mapper.readValue(jSource, SourceStructure)
        }

        when:
        iterations.times {
            long start = System.nanoTime()

            mapper.readValue(jSource, SourceStructure)

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

    @JsonIgnoreProperties(ignoreUnknown = true)
    static class SourceStructure {
        public Node rootList1;
        public Node rootList2;
        public Node rootList3;
        public Node rootList4;
        public Node rootList5;
        public Node rootList6;
        public Node rootList7;
        public Node rootList8;
        public Node rootList9;
        public Node rootList10;
        public Node rootList11;
        public Node rootList12;
        public Node rootList13;
        public Node rootList14;
        public Node rootList15;
        public Node rootList16;
        public Node rootList17;
        public Node rootList18;
        public Node rootList19;
        public Node rootList20;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Node {
        public String key2;
        public Map<String, Value> children = new LinkedHashMap<>();

        @JsonAnySetter
        public void putChild(String key, Value value) {
            if ("key2".equals(key)) return;
            children.put(key, value);
        }
    }

    @JsonDeserialize(using = Value.Deserializer.class)
    public static class Value {
        public String string;
        public List<Node> nodes;

        public boolean isString() { return string != null; }
        public boolean isNodes()  { return nodes  != null; }

        public static class Deserializer extends JsonDeserializer<Value> {
            @Override
            public Value deserialize(JsonParser p, DeserializationContext ctx) throws IOException {
                Value out = new Value();
                ObjectCodec codec = p.getCodec();
                JsonToken t = p.currentToken();
                if (t == JsonToken.VALUE_STRING) {
                    out.string = p.getValueAsString();
                    return out;
                } else if (t == JsonToken.START_ARRAY) {
                    out.nodes = codec.readValue(p, new TypeReference<List<Node>>() {});
                    return out;
                } else if (t == JsonToken.START_OBJECT) {
                    Node single = codec.readValue(p, Node.class);
                    out.nodes = Collections.singletonList(single);
                    return out;
                }
                return (Value) ctx.handleUnexpectedToken(Value.class, p);
            }
        }
    }
}