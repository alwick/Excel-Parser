package com.gsg.parser

/**
 * Created by alwick on 1/29/2015.
 */
class WorkbookAnalysis {
    String name;
    String path;
    List<String> linkedFiles;

    String getName() {
        return name
    }

    void setName(String name) {
        this.name = name
    }

    String getPath() {
        return path
    }

    void setPath(String path) {
        this.path = path
    }

    List<String> getLinkedFiles() {
        return linkedFiles
    }

    void setLinkedFiles(List<String> linkedFiles) {
        this.linkedFiles = linkedFiles
    }
}
