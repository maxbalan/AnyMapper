package com.moftium.anymapper.performance

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import spock.lang.Specification

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

class JacksonMapPerformanceSpec extends Specification {

    def "benchmark jackson flat map performance"() {
        given:
        def mapper = new ObjectMapper()
        int iterations = 1000
        int warmup = 100
        int keys = 20
        def times = []

        Map<String, Object> source = [
                person: (1..keys).collectEntries { i ->
                    [("key$i".toString()): "value$i".toString()]
                }
        ]

        String jSource = mapper.writeValueAsString(source)

        // warm up
        1.upto(warmup) {
            mapper.readValue(jSource, Result)
        }

        when:
        iterations.times {
            long start = System.nanoTime()

            mapper.readValue(jSource, Result)

            long end = System.nanoTime()
            times << (end - start) / 1_000_000.0
        }

        then:
        saveCsv("benchmark_jackson_map.csv", times)
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

    static class Result {
        @JsonProperty("person")
        User user
    }

    static class User {
        @JsonProperty("key1")
        String field1
        @JsonProperty("key2")
        String field2
        @JsonProperty("key3")
        String field3
        @JsonProperty("key4")
        String field4
        @JsonProperty("key5")
        String field5
        @JsonProperty("key6")
        String field6
        @JsonProperty("key7")
        String field7
        @JsonProperty("key8")
        String field8
        @JsonProperty("key9")
        String field9
        @JsonProperty("key10")
        String field10
        @JsonProperty("key11")
        String field11
        @JsonProperty("key12")
        String field12
        @JsonProperty("key13")
        String field13
        @JsonProperty("key14")
        String field14
        @JsonProperty("key15")
        String field15
        @JsonProperty("key16")
        String field16
        @JsonProperty("key17")
        String field17
        @JsonProperty("key18")
        String field18
        @JsonProperty("key19")
        String field19
        @JsonProperty("key20")
        String field20
    }
}
