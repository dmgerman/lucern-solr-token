begin_unit
begin_package
DECL|package|org.apache.lucene.index
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
package|;
end_package
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
operator|.
name|FSDirectory
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
operator|.
name|Directory
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
operator|.
name|IndexInput
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
operator|.
name|Document
import|;
end_import
begin_import
import|import
name|java
operator|.
name|text
operator|.
name|NumberFormat
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import
begin_comment
comment|/**  * Basic tool to check the health of an index and write a  * new segments file that removes reference to problematic  * segments.  There are many more checks that this tool  * could do but does not yet, eg: reconstructing a segments  * file by looking for all loadable segments (if no segments  * file is found), removing specifically specified segments,  * listing files that exist but are not referenced, etc.  */
end_comment
begin_class
DECL|class|CheckIndex
specifier|public
class|class
name|CheckIndex
block|{
DECL|method|main
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|Throwable
block|{
name|boolean
name|doFix
init|=
literal|false
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|args
operator|.
name|length
condition|;
name|i
operator|++
control|)
if|if
condition|(
name|args
index|[
name|i
index|]
operator|.
name|equals
argument_list|(
literal|"-fix"
argument_list|)
condition|)
block|{
name|doFix
operator|=
literal|true
expr_stmt|;
break|break;
block|}
if|if
condition|(
name|args
operator|.
name|length
operator|!=
operator|(
name|doFix
condition|?
literal|2
else|:
literal|1
operator|)
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"\nUsage: java org.apache.lucene.index.CheckIndex pathToIndex [-fix]\n"
operator|+
literal|"\n"
operator|+
literal|"  -fix: actually write a new segments_N file, removing any problematic segments\n"
operator|+
literal|"\n"
operator|+
literal|"**WARNING**: -fix should only be used on an emergency basis as it will cause\n"
operator|+
literal|"documents (perhaps many) to be permanently removed from the index.  Always make\n"
operator|+
literal|"a backup copy of your index before running this!  Do not run this tool on an index\n"
operator|+
literal|"that is actively being written to.  You have been warned!\n"
operator|+
literal|"\n"
operator|+
literal|"Run without -fix, this tool will open the index, report version information\n"
operator|+
literal|"and report any exceptions it hits and what action it would take if -fix were\n"
operator|+
literal|"specified.  With -fix, this tool will remove any segments that have issues and\n"
operator|+
literal|"write a new segments_N file.  This means all documents contained in the affected\n"
operator|+
literal|"segments will be removed.\n"
argument_list|)
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
name|NumberFormat
name|nf
init|=
name|NumberFormat
operator|.
name|getInstance
argument_list|()
decl_stmt|;
name|SegmentInfos
name|sis
init|=
operator|new
name|SegmentInfos
argument_list|()
decl_stmt|;
specifier|final
name|String
name|dirName
init|=
name|args
index|[
literal|0
index|]
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"\nOpening index @ "
operator|+
name|dirName
operator|+
literal|"\n"
argument_list|)
expr_stmt|;
name|Directory
name|dir
init|=
literal|null
decl_stmt|;
try|try
block|{
name|dir
operator|=
name|FSDirectory
operator|.
name|getDirectory
argument_list|(
name|dirName
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"ERROR: could not open directory \""
operator|+
name|dirName
operator|+
literal|"\"; exiting"
argument_list|)
expr_stmt|;
name|t
operator|.
name|printStackTrace
argument_list|(
name|System
operator|.
name|out
argument_list|)
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|sis
operator|.
name|read
argument_list|(
name|dir
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"ERROR: could not read any segments file in directory \""
operator|+
name|dirName
operator|+
literal|"\"; exiting"
argument_list|)
expr_stmt|;
name|t
operator|.
name|printStackTrace
argument_list|(
name|System
operator|.
name|out
argument_list|)
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
specifier|final
name|int
name|numSegments
init|=
name|sis
operator|.
name|size
argument_list|()
decl_stmt|;
specifier|final
name|String
name|segmentsFileName
init|=
name|sis
operator|.
name|getCurrentSegmentFileName
argument_list|()
decl_stmt|;
name|IndexInput
name|input
init|=
literal|null
decl_stmt|;
try|try
block|{
name|input
operator|=
name|dir
operator|.
name|openInput
argument_list|(
name|segmentsFileName
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"ERROR: could not open segments file in directory \""
operator|+
name|dirName
operator|+
literal|"\"; exiting"
argument_list|)
expr_stmt|;
name|t
operator|.
name|printStackTrace
argument_list|(
name|System
operator|.
name|out
argument_list|)
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
name|int
name|format
init|=
literal|0
decl_stmt|;
try|try
block|{
name|format
operator|=
name|input
operator|.
name|readInt
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"ERROR: could not read segment file version in directory \""
operator|+
name|dirName
operator|+
literal|"\"; exiting"
argument_list|)
expr_stmt|;
name|t
operator|.
name|printStackTrace
argument_list|(
name|System
operator|.
name|out
argument_list|)
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
name|String
name|sFormat
init|=
literal|""
decl_stmt|;
name|boolean
name|skip
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|format
operator|==
name|SegmentInfos
operator|.
name|FORMAT
condition|)
name|sFormat
operator|=
literal|"FORMAT [Lucene Pre-2.1]"
expr_stmt|;
if|if
condition|(
name|format
operator|==
name|SegmentInfos
operator|.
name|FORMAT_LOCKLESS
condition|)
name|sFormat
operator|=
literal|"FORMAT_LOCKLESS [Lucene 2.1]"
expr_stmt|;
elseif|else
if|if
condition|(
name|format
operator|==
name|SegmentInfos
operator|.
name|FORMAT_SINGLE_NORM_FILE
condition|)
name|sFormat
operator|=
literal|"FORMAT_SINGLE_NORM_FILE [Lucene 2.2]"
expr_stmt|;
elseif|else
if|if
condition|(
name|format
operator|==
name|SegmentInfos
operator|.
name|FORMAT_SHARED_DOC_STORE
condition|)
name|sFormat
operator|=
literal|"FORMAT_SHARED_DOC_STORE [Lucene 2.3]"
expr_stmt|;
elseif|else
if|if
condition|(
name|format
operator|<
name|SegmentInfos
operator|.
name|FORMAT_SHARED_DOC_STORE
condition|)
block|{
name|sFormat
operator|=
literal|"int="
operator|+
name|format
operator|+
literal|" [newer version of Lucene than this tool]"
expr_stmt|;
name|skip
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
name|sFormat
operator|=
name|format
operator|+
literal|" [Lucene 1.3 or prior]"
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Segments file="
operator|+
name|segmentsFileName
operator|+
literal|" numSegments="
operator|+
name|numSegments
operator|+
literal|" version="
operator|+
name|sFormat
argument_list|)
expr_stmt|;
if|if
condition|(
name|skip
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"\nERROR: this index appears to be created by a newer version of Lucene than this tool was compiled on; please re-compile this tool on the matching version of Lucene; exiting"
argument_list|)
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
name|SegmentInfos
name|newSIS
init|=
operator|(
name|SegmentInfos
operator|)
name|sis
operator|.
name|clone
argument_list|()
decl_stmt|;
name|newSIS
operator|.
name|clear
argument_list|()
expr_stmt|;
name|boolean
name|changed
init|=
literal|false
decl_stmt|;
name|int
name|totLoseDocCount
init|=
literal|0
decl_stmt|;
name|int
name|numBadSegments
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numSegments
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|SegmentInfo
name|info
init|=
name|sis
operator|.
name|info
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"  "
operator|+
operator|(
literal|1
operator|+
name|i
operator|)
operator|+
literal|" of "
operator|+
name|numSegments
operator|+
literal|": name="
operator|+
name|info
operator|.
name|name
operator|+
literal|" docCount="
operator|+
name|info
operator|.
name|docCount
argument_list|)
expr_stmt|;
name|int
name|toLoseDocCount
init|=
name|info
operator|.
name|docCount
decl_stmt|;
name|SegmentReader
name|reader
init|=
literal|null
decl_stmt|;
try|try
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"    compound="
operator|+
name|info
operator|.
name|getUseCompoundFile
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"    numFiles="
operator|+
name|info
operator|.
name|files
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"    size (MB)="
operator|+
name|nf
operator|.
name|format
argument_list|(
name|info
operator|.
name|sizeInBytes
argument_list|()
operator|/
operator|(
literal|1024.
operator|*
literal|1024.
operator|)
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|int
name|docStoreOffset
init|=
name|info
operator|.
name|getDocStoreOffset
argument_list|()
decl_stmt|;
if|if
condition|(
name|docStoreOffset
operator|!=
operator|-
literal|1
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"    docStoreOffset="
operator|+
name|docStoreOffset
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"    docStoreSegment="
operator|+
name|info
operator|.
name|getDocStoreSegment
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"    docStoreIsCompoundFile="
operator|+
name|info
operator|.
name|getDocStoreIsCompoundFile
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|final
name|String
name|delFileName
init|=
name|info
operator|.
name|getDelFileName
argument_list|()
decl_stmt|;
if|if
condition|(
name|delFileName
operator|==
literal|null
condition|)
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"    no deletions"
argument_list|)
expr_stmt|;
else|else
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"    has deletions [delFileName="
operator|+
name|delFileName
operator|+
literal|"]"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|print
argument_list|(
literal|"    test: open reader........."
argument_list|)
expr_stmt|;
name|reader
operator|=
name|SegmentReader
operator|.
name|get
argument_list|(
name|info
argument_list|)
expr_stmt|;
specifier|final
name|int
name|numDocs
init|=
name|reader
operator|.
name|numDocs
argument_list|()
decl_stmt|;
name|toLoseDocCount
operator|=
name|numDocs
expr_stmt|;
if|if
condition|(
name|reader
operator|.
name|hasDeletions
argument_list|()
condition|)
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"OK ["
operator|+
operator|(
name|info
operator|.
name|docCount
operator|-
name|numDocs
operator|)
operator|+
literal|" deleted docs]"
argument_list|)
expr_stmt|;
else|else
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"OK"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|print
argument_list|(
literal|"    test: fields, norms......."
argument_list|)
expr_stmt|;
name|Collection
name|fieldNames
init|=
name|reader
operator|.
name|getFieldNames
argument_list|(
name|IndexReader
operator|.
name|FieldOption
operator|.
name|ALL
argument_list|)
decl_stmt|;
name|Iterator
name|it
init|=
name|fieldNames
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
specifier|final
name|String
name|fieldName
init|=
operator|(
name|String
operator|)
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
name|byte
index|[]
name|b
init|=
name|reader
operator|.
name|norms
argument_list|(
name|fieldName
argument_list|)
decl_stmt|;
if|if
condition|(
name|b
operator|.
name|length
operator|!=
name|info
operator|.
name|docCount
condition|)
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"norms for field \""
operator|+
name|fieldName
operator|+
literal|"\" is length "
operator|+
name|b
operator|.
name|length
operator|+
literal|" != maxDoc "
operator|+
name|info
operator|.
name|docCount
argument_list|)
throw|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"OK ["
operator|+
name|fieldNames
operator|.
name|size
argument_list|()
operator|+
literal|" fields]"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|print
argument_list|(
literal|"    test: terms, freq, prox..."
argument_list|)
expr_stmt|;
specifier|final
name|TermEnum
name|termEnum
init|=
name|reader
operator|.
name|terms
argument_list|()
decl_stmt|;
specifier|final
name|TermPositions
name|termPositions
init|=
name|reader
operator|.
name|termPositions
argument_list|()
decl_stmt|;
name|long
name|termCount
init|=
literal|0
decl_stmt|;
name|long
name|totFreq
init|=
literal|0
decl_stmt|;
name|long
name|totPos
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|termEnum
operator|.
name|next
argument_list|()
condition|)
block|{
name|termCount
operator|++
expr_stmt|;
specifier|final
name|Term
name|term
init|=
name|termEnum
operator|.
name|term
argument_list|()
decl_stmt|;
specifier|final
name|int
name|docFreq
init|=
name|termEnum
operator|.
name|docFreq
argument_list|()
decl_stmt|;
name|termPositions
operator|.
name|seek
argument_list|(
name|term
argument_list|)
expr_stmt|;
name|int
name|lastDoc
init|=
operator|-
literal|1
decl_stmt|;
name|int
name|freq0
init|=
literal|0
decl_stmt|;
name|totFreq
operator|+=
name|docFreq
expr_stmt|;
while|while
condition|(
name|termPositions
operator|.
name|next
argument_list|()
condition|)
block|{
name|freq0
operator|++
expr_stmt|;
specifier|final
name|int
name|doc
init|=
name|termPositions
operator|.
name|doc
argument_list|()
decl_stmt|;
specifier|final
name|int
name|freq
init|=
name|termPositions
operator|.
name|freq
argument_list|()
decl_stmt|;
if|if
condition|(
name|doc
operator|<=
name|lastDoc
condition|)
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"term "
operator|+
name|term
operator|+
literal|": doc "
operator|+
name|doc
operator|+
literal|"< lastDoc "
operator|+
name|lastDoc
argument_list|)
throw|;
name|lastDoc
operator|=
name|doc
expr_stmt|;
if|if
condition|(
name|freq
operator|<=
literal|0
condition|)
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"term "
operator|+
name|term
operator|+
literal|": doc "
operator|+
name|doc
operator|+
literal|": freq "
operator|+
name|freq
operator|+
literal|" is out of bounds"
argument_list|)
throw|;
name|int
name|lastPos
init|=
operator|-
literal|1
decl_stmt|;
name|totPos
operator|+=
name|freq
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|freq
condition|;
name|j
operator|++
control|)
block|{
specifier|final
name|int
name|pos
init|=
name|termPositions
operator|.
name|nextPosition
argument_list|()
decl_stmt|;
if|if
condition|(
name|pos
operator|<
literal|0
condition|)
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"term "
operator|+
name|term
operator|+
literal|": doc "
operator|+
name|doc
operator|+
literal|": pos "
operator|+
name|pos
operator|+
literal|" is out of bounds"
argument_list|)
throw|;
if|if
condition|(
name|pos
operator|<=
name|lastPos
condition|)
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"term "
operator|+
name|term
operator|+
literal|": doc "
operator|+
name|doc
operator|+
literal|": pos "
operator|+
name|pos
operator|+
literal|"< lastPos "
operator|+
name|lastPos
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
name|freq0
operator|!=
name|docFreq
condition|)
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"term "
operator|+
name|term
operator|+
literal|" docFreq="
operator|+
name|docFreq
operator|+
literal|" != num docs seen "
operator|+
name|freq0
argument_list|)
throw|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"OK ["
operator|+
name|termCount
operator|+
literal|" terms; "
operator|+
name|totFreq
operator|+
literal|" terms/docs pairs; "
operator|+
name|totPos
operator|+
literal|" tokens]"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|print
argument_list|(
literal|"    test: stored fields......."
argument_list|)
expr_stmt|;
name|int
name|docCount
init|=
literal|0
decl_stmt|;
name|long
name|totFields
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|info
operator|.
name|docCount
condition|;
name|j
operator|++
control|)
if|if
condition|(
operator|!
name|reader
operator|.
name|isDeleted
argument_list|(
name|j
argument_list|)
condition|)
block|{
name|docCount
operator|++
expr_stmt|;
name|Document
name|doc
init|=
name|reader
operator|.
name|document
argument_list|(
name|j
argument_list|)
decl_stmt|;
name|totFields
operator|+=
name|doc
operator|.
name|getFields
argument_list|()
operator|.
name|size
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|docCount
operator|!=
name|reader
operator|.
name|numDocs
argument_list|()
condition|)
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"docCount="
operator|+
name|docCount
operator|+
literal|" but saw "
operator|+
name|docCount
operator|+
literal|" undeleted docs"
argument_list|)
throw|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"OK ["
operator|+
name|totFields
operator|+
literal|" total field count; avg "
operator|+
name|nf
operator|.
name|format
argument_list|(
operator|(
operator|(
operator|(
name|float
operator|)
name|totFields
operator|)
operator|/
name|docCount
operator|)
argument_list|)
operator|+
literal|" fields per doc]"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|print
argument_list|(
literal|"    test: term vectors........"
argument_list|)
expr_stmt|;
name|int
name|totVectors
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|info
operator|.
name|docCount
condition|;
name|j
operator|++
control|)
if|if
condition|(
operator|!
name|reader
operator|.
name|isDeleted
argument_list|(
name|j
argument_list|)
condition|)
block|{
name|TermFreqVector
index|[]
name|tfv
init|=
name|reader
operator|.
name|getTermFreqVectors
argument_list|(
name|j
argument_list|)
decl_stmt|;
if|if
condition|(
name|tfv
operator|!=
literal|null
condition|)
name|totVectors
operator|+=
name|tfv
operator|.
name|length
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"OK ["
operator|+
name|totVectors
operator|+
literal|" total vector count; avg "
operator|+
name|nf
operator|.
name|format
argument_list|(
operator|(
operator|(
operator|(
name|float
operator|)
name|totVectors
operator|)
operator|/
name|docCount
operator|)
argument_list|)
operator|+
literal|" term/freq vector fields per doc]"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|""
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"FAILED"
argument_list|)
expr_stmt|;
name|String
name|comment
decl_stmt|;
if|if
condition|(
name|doFix
condition|)
name|comment
operator|=
literal|"will remove reference to this segment (-fix is specified)"
expr_stmt|;
else|else
name|comment
operator|=
literal|"would remove reference to this segment (-fix was not specified)"
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"    WARNING: "
operator|+
name|comment
operator|+
literal|"; full exception:"
argument_list|)
expr_stmt|;
name|t
operator|.
name|printStackTrace
argument_list|(
name|System
operator|.
name|out
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|totLoseDocCount
operator|+=
name|toLoseDocCount
expr_stmt|;
name|numBadSegments
operator|++
expr_stmt|;
name|changed
operator|=
literal|true
expr_stmt|;
continue|continue;
block|}
finally|finally
block|{
if|if
condition|(
name|reader
operator|!=
literal|null
condition|)
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|// Keeper
name|newSIS
operator|.
name|add
argument_list|(
name|info
operator|.
name|clone
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|changed
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"No problems were detected with this index.\n"
argument_list|)
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"WARNING: "
operator|+
name|numBadSegments
operator|+
literal|" broken segments detected"
argument_list|)
expr_stmt|;
if|if
condition|(
name|doFix
condition|)
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"WARNING: "
operator|+
name|totLoseDocCount
operator|+
literal|" documents will be lost"
argument_list|)
expr_stmt|;
else|else
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"WARNING: "
operator|+
name|totLoseDocCount
operator|+
literal|" documents would be lost if -fix were specified"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|""
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|doFix
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"NOTE: will write new segments file in 5 seconds; this will remove "
operator|+
name|totLoseDocCount
operator|+
literal|" docs from the index. THIS IS YOUR LAST CHANCE TO CTRL+C!"
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|5
condition|;
name|i
operator|++
control|)
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"  "
operator|+
operator|(
literal|5
operator|-
name|i
operator|)
operator|+
literal|"..."
argument_list|)
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|print
argument_list|(
literal|"Writing..."
argument_list|)
expr_stmt|;
try|try
block|{
name|newSIS
operator|.
name|write
argument_list|(
name|dir
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"FAILED; exiting"
argument_list|)
expr_stmt|;
name|t
operator|.
name|printStackTrace
argument_list|(
name|System
operator|.
name|out
argument_list|)
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"OK"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Wrote new segments file \""
operator|+
name|newSIS
operator|.
name|getCurrentSegmentFileName
argument_list|()
operator|+
literal|"\""
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"NOTE: would write new segments file [-fix was not specified]"
argument_list|)
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
