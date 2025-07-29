package com.moftium.anymapper.example

import com.moftium.anymapper.AnyMapper
import spock.lang.Specification

class AnyMapperExample extends Specification {
    def "example mapping call"() {
        given:
        def source = ["id"              : "5abbe4b7ddc1b351ef961414",
                      "address"         : "42 Oxford Street",
                      "dateLastActivity": "2019-09-16T16:19:17.156Z",
                      "location"        : true,
                      "votes"           : 2154,
                      "badges"          : ["attachmentsByType": ["meta": ["project": 12,
                                                                          "task"   : 34]]],
                      "checkItemStates" : ["completed",
                                           "in_progress",
                                           "not_started"],
                      "idChecklists"    : [["id": "cl_01"],
                                           ["id": "cl_02"]],
                      "idLabels"        : [["id"       : "lab_01",
                                            "labelName": "Important",
                                            "idMembers": ["mem_01"],
                                            "cover"    : ["color"               : "yellow",
                                                          "idUploadedBackground": true,
                                                          "coversIds"           : ["cov_01"]]]]
        ]

        def mapping = [
                "id"                                   : [destination: "record.identifier"],
                "address"                              : [destination: "record.location.address"],
                "dateLastActivity"                     : [destination: "record.timestamps.lastSeen"],
                "badges.attachmentsByType.meta.project": [destination: "record.metadata.projectId"],
                "badges.attachmentsByType.meta.task"   : [destination: "record.metadata.taskId"],
                "checkItemStates"                      : [destination: "record.checks.statuses"],
                "idChecklists"                         : [destination: "record.checks.checklists",
                                                          type       : "list",
                                                          id         : [destination: "checklistId"]],
                "idLabels"                             : [destination      : "record.labels",
                                                          type             : "list",
                                                          id               : [destination: "id"],
                                                          labelName        : [destination: "name"],
                                                          idMembers        : [destination: "members"],
                                                          "cover.color"    : [destination: "visual.color"],
                                                          "cover.coversIds": [destination: "visual.covers"]]
        ]

        when:
        def result = new AnyMapper(mapping).transform(source)

        then:
        println result
        assert result.toString().replace("{", "[").replace("}", ']').replace("=", ":") == '[record:[identifier:5abbe4b7ddc1b351ef961414, metadata:[projectId:12, taskId:34], checks:[checklists:[[checklistId:cl_01], [checklistId:cl_02]], statuses:[completed, in_progress, not_started]], timestamps:[lastSeen:2019-09-16T16:19:17.156Z], location:[address:42 Oxford Street], labels:[[members:[mem_01], name:Important, visual:[color:yellow, covers:[cov_01]], id:lab_01]]]]'
    }
}
