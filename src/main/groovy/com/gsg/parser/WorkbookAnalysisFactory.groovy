package com.gsg.parser

import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.openxml4j.opc.PackagePart
import org.apache.poi.xssf.model.ExternalLinksTable
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.joda.time.DateTime

/**
 * Created by alwick on 1/29/2015.
 */
class WorkbookAnalysisFactory {
    def FileReplacementMap map;
    def String outputDirectory;

    def List<WorkbookAnalysis> buildForDirectory( File directory ) {
        buildForDirectory( directory, new DirectoryParser() );
    }

    def List<WorkbookAnalysis> buildForDirectory( File directory, DateTime lastModifiedDate ) {
        def results = [];
        def parser = new DirectoryParser( asOfDate: lastModifiedDate );
        results.addAll( buildForDirectory( directory, parser ) );

        return results;
    }

    def List<WorkbookAnalysis> buildForDirectory( File directory, parser ) {
        System.out.println( "Analyzing Directory: " + directory.getPath() );

        def results = [];
        def files = parser.getExcelFiles(directory.getPath());
        for (File file : files) {
            try {
                results.add(analyzeFile(file));
            }
            catch (Exception e ) {
                System.out.println( "Error parsing workbook: " + file.getPath() );
                e.printStackTrace();
            }
        }

        parseSubdirectories(results, parser, directory);

        System.gc();
        System.out.println( "Done with: " + directory.getPath() );

        return results;
    }

    def parseSubdirectories( List<WorkbookAnalysis> results, DirectoryParser parser, File directory ) {
        def directories = parser.getDirectories( directory );

        for( File subDirectory : directories ) {
            results.addAll( buildForDirectory(subDirectory, parser) );
        }
    }

    def analyzeFile( File file ) {
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
        XSSFWorkbook workbook = new XSSFWorkbook( fs );

        def linkedFiles = [];
        def tables = workbook.getExternalLinksTable();
        def updated = false;
        for( ExternalLinksTable table : tables ) {
            def link = table.getLinkedFileName();
            if( updates != null ) {
                def replacement = updates.get( link );
                if( replacement != null ) {
                    table.setLinkedFileName( replacement );
                    updated = true;
                    System.out.println( "Replaced: " + link + " with: " + replacement );
                }
            }

            linkedFiles.add( link );
        }

        if( updated ) {
            def filename = file.getName();
            def separatorIndex = filename.lastIndexOf('.');
            def newFilename = outputDirectory + File.separator + filename.substring( 0, separatorIndex ) + '_new' + filename.substring( separatorIndex );
            workbook.write( new FileOutputStream( newFilename ) );
            System.out.println( "Saved updated file: " + newFilename );
        }

        analysis.setName( file.getName() );
        analysis.setPath( file.getParent() );
        analysis.setLinkedFiles( linkedFiles );

        return analysis;
    }
}
