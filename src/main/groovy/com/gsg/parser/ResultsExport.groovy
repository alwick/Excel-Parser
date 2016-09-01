package com.gsg.parser

/**
 * Created by alwick on 1/29/2015.
 */
class ResultsExport {

    def writeResults( File file, results ) {
        FileWriter output = new FileWriter(file);
        BufferedWriter writer = new BufferedWriter(output);

        results.each { analysis ->
            analysis.linkedFiles.each { link ->
                writer.write(analysis.path);
                writer.write(",");
                writer.write(analysis.name);
                writer.write(",");
                writer.write(link);
                writer.newLine();
            }

            analysis.urlLinkFiles.each { link ->
                writer.write(analysis.path);
                writer.write(",");
                writer.write(analysis.name);
                writer.write(",");
                writer.write(link);
                writer.newLine();
            }
        }

        writer.flush();
        writer.close();
    }
}