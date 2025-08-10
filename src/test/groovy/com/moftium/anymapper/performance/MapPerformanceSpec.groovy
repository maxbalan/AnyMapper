package com.moftium.anymapper.performance


import com.moftium.anymapper.AnyMapper
import spock.lang.Specification

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

class MapPerformanceSpec extends Specification {

    def "benchmark anymapper flat map performance"() {
        given:
        int iterations = 1000
        int warmup = 100
        int keys = 20
        def times = []

        Map<String, Object> source = [
                person: (1..keys).collectEntries { i ->
                    [("key$i".toString()): "value$i".toString()]
                }
        ]

        Map<String, Object> mappingConfig = (1..keys).collectEntries { i ->
            [("person.key$i".toString()): [destination: "user.field$i".toString()]]
        }

        AnyMapper mapper = new AnyMapper(mappingConfig)

        // warm up
        1.upto(warmup) {
            mapper.transform(source)
        }

        when:
        iterations.times {
            long start = System.nanoTime()

            mapper.transform(source)

            long end = System.nanoTime()
            times << (end - start) / 1_000_000.0
        }

        then:
        saveCsv("benchmark_map.csv", times)
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