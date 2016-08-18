# Excel File Parsing Tool 

## Overview

This tool is used to identify missing links in excel documents

## Usage

Update the *run.bat* file in the *bin* directory.

```
java -cp .;./lib/* com.qsg.parser.Processor <RootDirectory> <OutputFilename> <LastModifiedDate> <ReplacementsFilename> <OutputDirectory>
```

Where:
*Required*:
**RootDirectory** - Starting directory for the report
**OutputFilename** - The report output filename.
**LastModifiedDate** - The date after which all files must have been modified to qualify.

*Optional*:
**ReplacementsFilename** - The name of the file that contains the replacement column
**OutputDirectory** - Output for the updated file