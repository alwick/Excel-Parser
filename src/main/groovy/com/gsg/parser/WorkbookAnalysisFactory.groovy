package com.gsg.parser

import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.joda.time.DateTime

/**
 * Created by alwick on 1/29/2015.
 */
class WorkbookAnalysisFactory {
    def FileReplacementMap map;
    def String outputDirectory;
    def ResultsExport export;
    def File processedDirectories;
    def File largeFiles;

    def List<WorkbookAnalysis> buildForDirectory( File directory ) {
        buildForDirectory( directory, new DirectoryParser() );
    }

    def buildForDirectory( File directory, DateTime lastModifiedDate, resultsPrefix ) {
        def parser = new DirectoryParser( asOfDate: lastModifiedDate );
        def ignoreDirectories = processedDirectories.exists() ? processedDirectories.readLines() : [];

        def rootResults = parseDirectoryFiles(parser, directory.getPath());
        writeResults(rootResults, directory,resultsPrefix);

        parser.getDirectories( directory ).each { rootDirectory ->
            if( !ignoreDirectories.contains( rootDirectory.getPath() ) ) {
                def results = buildForDirectory(rootDirectory, parser);
                writeResults(results, rootDirectory, resultsPrefix);
                processedDirectories.append('\n' + rootDirectory.getPath());
            }
        }
    }

    protected writeResults(List results, File rootDirectory, resultsPrefix) {
        if (results.size() > 0) {
            def filename = resultsPrefix + '-' + rootDirectory.getPath().split("\\\\").last() + '.csv';
            export.writeResults(new File(filename), results);
            System.out.println("Wrote results to: " + filename);
        } else {
            System.out.println("No results to write...");
        }
    }

    def buildForDirectory( File directory, parser ) {
        def directoryPath = directory.getPath();
        System.out.println( "Analyzing Directory: " + directoryPath );

        ArrayList results = parseDirectoryFiles(parser, directoryPath);
        parseSubdirectories(results, parser, directory);

        System.gc();
        System.out.println( "Done with: " + directoryPath + " found: " + results.size() + " files with link(s)" );

        return results;
    }

    protected parseDirectoryFiles(parser, String directoryPath) {
        def results = [];
        parser.getExcelFiles(directoryPath).each { file ->
            def fileSizeInMB = file.length() / 1024 / 1024;
            if( fileSizeInMB > 20 ) {
                System.out.println(file.getPath() + " is too large to process: " + fileSizeInMB + "mb");
                largeFiles.append( file.getPath() );
            }
            else {
                try {
                    def analysis = analyzeFile(file);

                    System.out.println(file.getPath() + " - Linked files: " + analysis.linkedFiles.size() + " - URL Links: " + analysis.urlLinkFiles.size());
                    if (analysis.linkedFiles.size() > 0 || analysis.urlLinkFiles.size() > 0) {
                        results.add(analysis);
                    }
                }
                catch (Exception e) {
                    System.out.println("Error parsing workbook: " + file.getPath());
                    e.printStackTrace();
                }
            }
        }
        results
    }

    def parseSubdirectories( results, DirectoryParser parser, File directory ) {
        parser.getDirectories( directory ).each { subDirectory ->
            results.addAll( buildForDirectory(subDirectory, parser) );
        }
    }

    def WorkbookAnalysis analyzeFile( File file ) {
        System.out.println( "Analysing file: " + file.getName() );

        def updates = null;
        if( map != null ) {
            updates = map.get( file.getPath() )
            if( updates != null ) {
                System.out.println( "Found replacements" );
            }
        };

        WorkbookAnalysis analysis = new WorkbookAnalysis();
        FileInputStream fs = new FileInputStream( file );
        def isHSSF = file.getName().tokenize('.').last().equals("xls");
        def workbook = isHSSF ? new HSSFWorkbook(fs) : new XSSFWorkbook( fs );

        def linkedFiles = [];
        def updated = false;
        if ( !isHSSF ) {
            def tables = workbook.getExternalLinksTable();
            tables.each { table ->
                def link = table.getLinkedFileName();
                if (updates != null) {
                    def replacement = updates.get(link);
                    if (replacement != null) {
                        table.setLinkedFileName(replacement);
                        updated = true;
                        System.out.println("Replaced: " + link + " with: " + replacement);
                    }
                }

                linkedFiles.add(link);
            }
        }

        // look for url links...
        def urlLinks = [];
        for (def sheet : workbook ) {
            for (def row : sheet) {
                for (def cell : row) {
                    if( cell.getHyperlink() != null ) {
                        urlLinks.add( cell.getHyperlink().getAddress() );
                    }
                }
            }
        }

        if( updated ) {
            //def filename = file.getName();
            def filename = file.getPath();
            def separatorIndex = filename.lastIndexOf('.');
            //def newFilename = filename.substring( 0, separatorIndex) + File.separator + filename.substring( 0, separatorIndex ) + '_new' + filename.substring( separatorIndex );
            def newFilename = filename.substring( 0, separatorIndex ) + '_new' + filename.substring( separatorIndex );
            workbook.write( new FileOutputStream( newFilename ) );
            System.out.println( "Saved updated file: " + newFilename );
        }

        analysis.setName( file.getName() );
        analysis.setPath( file.getParent() );
        analysis.setLinkedFiles( linkedFiles );
        analysis.setUrlLinkFiles( urlLinks );

        return analysis;
    }
}
