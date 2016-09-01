package com.gsg.parser

import org.joda.time.DateTime

/**
 * Created by alwick on 1/29/2015.
 */
class DirectoryParser {
    def DateTime asOfDate;

    def List<File> getExcelFiles( String directory ) {
        File f = new File(directory);
        return f.listFiles( new ExtensionsFilter( [ ".xls", ".xlsx" ] ) );
    }

    def List<File> getFiles( String directory ) {
        File f = new File(directory);
        return getFiles(f);
    }

    def List<File> getFiles( File rootFile ) {
        return new ArrayList<String>(Arrays.asList(rootFile.listFiles()));
    }

    def getDirectories(File file) {
        return file.listFiles( new FileFilter() {
            @Override
            boolean accept(File pathname) {
                return pathname.isDirectory();
            }
        })
    }

    class ExtensionsFilter implements FileFilter {
        private char[][] extensions;

        private ExtensionsFilter(List<String> extensions) {
            int length = extensions.size();
            this.extensions = new char[length][];
            for (String s : extensions) {
                this.extensions[--length] = s.toCharArray();
            }
        }

        @Override
        public boolean accept(File file) {
            if( file.isDirectory()  || ( asOfDate != null && asOfDate.isAfter( new DateTime( file.lastModified() ) ) ) ) {
                return false;
            }

            char[] path = file.getPath().toCharArray();
            for (char[] extension : extensions) {
                if (extension.length > path.length) {
                    continue;
                }
                int pStart = path.length - 1;
                int eStart = extension.length - 1;
                boolean success = true;
                for (int i = 0; i <= eStart; i++) {
                    if ((path[pStart - i]) != (extension[eStart - i])) {
                        success = false;
                        break;
                    }
                }
                if (success)
                    return true;
            }
            return false;
        }
    }
}
