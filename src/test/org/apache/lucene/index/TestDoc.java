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
comment|/* ====================================================================  * The Apache Software License, Version 1.1  *  * Copyright (c) 2001 The Apache Software Foundation.  All rights  * reserved.  *  * Redistribution and use in source and binary forms, with or without  * modification, are permitted provided that the following conditions  * are met:  *  * 1. Redistributions of source code must retain the above copyright  *    notice, this list of conditions and the following disclaimer.  *  * 2. Redistributions in binary form must reproduce the above copyright  *    notice, this list of conditions and the following disclaimer in  *    the documentation and/or other materials provided with the  *    distribution.  *  * 3. The end-user documentation included with the redistribution,  *    if any, must include the following acknowledgment:  *       "This product includes software developed by the  *        Apache Software Foundation (http://www.apache.org/)."  *    Alternately, this acknowledgment may appear in the software itself,  *    if and wherever such third-party acknowledgments normally appear.  *  * 4. The names "Apache" and "Apache Software Foundation" and  *    "Apache Lucene" must not be used to endorse or promote products  *    derived from this software without prior written permission. For  *    written permission, please contact apache@apache.org.  *  * 5. Products derived from this software may not be called "Apache",  *    "Apache Lucene", nor may "Apache" appear in their name, without  *    prior written permission of the Apache Software Foundation.  *  * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED  * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES  * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE  * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR  * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,  * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT  * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF  * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND  * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,  * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT  * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF  * SUCH DAMAGE.  * ====================================================================  *  * This software consists of voluntary contributions made by many  * individuals on behalf of the Apache Software Foundation.  For more  * information on the Apache Software Foundation, please see  *<http://www.apache.org/>.  */
end_comment
begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
import|;
end_import
begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestSuite
import|;
end_import
begin_import
import|import
name|junit
operator|.
name|textui
operator|.
name|TestRunner
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
name|analysis
operator|.
name|SimpleAnalyzer
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
name|analysis
operator|.
name|Analyzer
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
name|document
operator|.
name|Document
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
name|search
operator|.
name|Similarity
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
name|demo
operator|.
name|FileDocument
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|*
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
import|;
end_import
begin_comment
comment|/** JUnit adaptation of an older test case DocTest.  * @author dmitrys@earthlink.net  * @version $Id$  */
end_comment
begin_class
DECL|class|TestDoc
specifier|public
class|class
name|TestDoc
extends|extends
name|TestCase
block|{
comment|/** Main for running test case by itself. */
DECL|method|main
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
name|args
index|[]
parameter_list|)
block|{
name|TestRunner
operator|.
name|run
argument_list|(
operator|new
name|TestSuite
argument_list|(
name|TestDoc
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|field|workDir
specifier|private
name|File
name|workDir
decl_stmt|;
DECL|field|indexDir
specifier|private
name|File
name|indexDir
decl_stmt|;
DECL|field|files
specifier|private
name|LinkedList
name|files
decl_stmt|;
comment|/** Set the test case. This test case needs      *  a few text files created in the current working directory.      */
DECL|method|setUp
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|IOException
block|{
name|workDir
operator|=
operator|new
name|File
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"tempDir"
argument_list|)
argument_list|,
literal|"TestDoc"
argument_list|)
expr_stmt|;
name|workDir
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|indexDir
operator|=
operator|new
name|File
argument_list|(
name|workDir
argument_list|,
literal|"testIndex"
argument_list|)
expr_stmt|;
name|indexDir
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|Directory
name|directory
init|=
name|FSDirectory
operator|.
name|getDirectory
argument_list|(
name|indexDir
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|directory
operator|.
name|close
argument_list|()
expr_stmt|;
name|files
operator|=
operator|new
name|LinkedList
argument_list|()
expr_stmt|;
name|files
operator|.
name|add
argument_list|(
name|createFile
argument_list|(
literal|"test.txt"
argument_list|,
literal|"This is the first test file"
argument_list|)
argument_list|)
expr_stmt|;
name|files
operator|.
name|add
argument_list|(
name|createFile
argument_list|(
literal|"test2.txt"
argument_list|,
literal|"This is the second test file"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|createFile
specifier|private
name|File
name|createFile
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|text
parameter_list|)
throws|throws
name|IOException
block|{
name|FileWriter
name|fw
init|=
literal|null
decl_stmt|;
name|PrintWriter
name|pw
init|=
literal|null
decl_stmt|;
try|try
block|{
name|File
name|f
init|=
operator|new
name|File
argument_list|(
name|workDir
argument_list|,
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|f
operator|.
name|exists
argument_list|()
condition|)
name|f
operator|.
name|delete
argument_list|()
expr_stmt|;
name|fw
operator|=
operator|new
name|FileWriter
argument_list|(
name|f
argument_list|)
expr_stmt|;
name|pw
operator|=
operator|new
name|PrintWriter
argument_list|(
name|fw
argument_list|)
expr_stmt|;
name|pw
operator|.
name|println
argument_list|(
name|text
argument_list|)
expr_stmt|;
return|return
name|f
return|;
block|}
finally|finally
block|{
if|if
condition|(
name|pw
operator|!=
literal|null
condition|)
name|pw
operator|.
name|close
argument_list|()
expr_stmt|;
if|if
condition|(
name|fw
operator|!=
literal|null
condition|)
name|fw
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|/** This test executes a number of merges and compares the contents of      *  the segments created when using compound file or not using one.      *      *  TODO: the original test used to print the segment contents to System.out      *        for visual validation. To have the same effect, a new method      *        checkSegment(String name, ...) should be created that would      *        assert various things about the segment.      */
DECL|method|testIndexAndMerge
specifier|public
name|void
name|testIndexAndMerge
parameter_list|()
throws|throws
name|Exception
block|{
name|StringWriter
name|sw
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|PrintWriter
name|out
init|=
operator|new
name|PrintWriter
argument_list|(
name|sw
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|Directory
name|directory
init|=
name|FSDirectory
operator|.
name|getDirectory
argument_list|(
name|indexDir
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|directory
operator|.
name|close
argument_list|()
expr_stmt|;
name|indexDoc
argument_list|(
literal|"one"
argument_list|,
literal|"test.txt"
argument_list|)
expr_stmt|;
name|printSegment
argument_list|(
name|out
argument_list|,
literal|"one"
argument_list|)
expr_stmt|;
name|indexDoc
argument_list|(
literal|"two"
argument_list|,
literal|"test2.txt"
argument_list|)
expr_stmt|;
name|printSegment
argument_list|(
name|out
argument_list|,
literal|"two"
argument_list|)
expr_stmt|;
name|merge
argument_list|(
literal|"one"
argument_list|,
literal|"two"
argument_list|,
literal|"merge"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|printSegment
argument_list|(
name|out
argument_list|,
literal|"merge"
argument_list|)
expr_stmt|;
name|merge
argument_list|(
literal|"one"
argument_list|,
literal|"two"
argument_list|,
literal|"merge2"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|printSegment
argument_list|(
name|out
argument_list|,
literal|"merge2"
argument_list|)
expr_stmt|;
name|merge
argument_list|(
literal|"merge"
argument_list|,
literal|"merge2"
argument_list|,
literal|"merge3"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|printSegment
argument_list|(
name|out
argument_list|,
literal|"merge3"
argument_list|)
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
name|sw
operator|.
name|close
argument_list|()
expr_stmt|;
name|String
name|multiFileOutput
init|=
name|sw
operator|.
name|getBuffer
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|multiFileOutput
argument_list|)
expr_stmt|;
name|sw
operator|=
operator|new
name|StringWriter
argument_list|()
expr_stmt|;
name|out
operator|=
operator|new
name|PrintWriter
argument_list|(
name|sw
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|directory
operator|=
name|FSDirectory
operator|.
name|getDirectory
argument_list|(
name|indexDir
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|directory
operator|.
name|close
argument_list|()
expr_stmt|;
name|indexDoc
argument_list|(
literal|"one"
argument_list|,
literal|"test.txt"
argument_list|)
expr_stmt|;
name|printSegment
argument_list|(
name|out
argument_list|,
literal|"one"
argument_list|)
expr_stmt|;
name|indexDoc
argument_list|(
literal|"two"
argument_list|,
literal|"test2.txt"
argument_list|)
expr_stmt|;
name|printSegment
argument_list|(
name|out
argument_list|,
literal|"two"
argument_list|)
expr_stmt|;
name|merge
argument_list|(
literal|"one"
argument_list|,
literal|"two"
argument_list|,
literal|"merge"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|printSegment
argument_list|(
name|out
argument_list|,
literal|"merge"
argument_list|)
expr_stmt|;
name|merge
argument_list|(
literal|"one"
argument_list|,
literal|"two"
argument_list|,
literal|"merge2"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|printSegment
argument_list|(
name|out
argument_list|,
literal|"merge2"
argument_list|)
expr_stmt|;
name|merge
argument_list|(
literal|"merge"
argument_list|,
literal|"merge2"
argument_list|,
literal|"merge3"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|printSegment
argument_list|(
name|out
argument_list|,
literal|"merge3"
argument_list|)
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
name|sw
operator|.
name|close
argument_list|()
expr_stmt|;
name|String
name|singleFileOutput
init|=
name|sw
operator|.
name|getBuffer
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|multiFileOutput
argument_list|,
name|singleFileOutput
argument_list|)
expr_stmt|;
block|}
DECL|method|indexDoc
specifier|private
name|void
name|indexDoc
parameter_list|(
name|String
name|segment
parameter_list|,
name|String
name|fileName
parameter_list|)
throws|throws
name|Exception
block|{
name|Directory
name|directory
init|=
name|FSDirectory
operator|.
name|getDirectory
argument_list|(
name|indexDir
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|Analyzer
name|analyzer
init|=
operator|new
name|SimpleAnalyzer
argument_list|()
decl_stmt|;
name|DocumentWriter
name|writer
init|=
operator|new
name|DocumentWriter
argument_list|(
name|directory
argument_list|,
name|analyzer
argument_list|,
name|Similarity
operator|.
name|getDefault
argument_list|()
argument_list|,
literal|1000
argument_list|)
decl_stmt|;
name|File
name|file
init|=
operator|new
name|File
argument_list|(
name|workDir
argument_list|,
name|fileName
argument_list|)
decl_stmt|;
name|Document
name|doc
init|=
name|FileDocument
operator|.
name|Document
argument_list|(
name|file
argument_list|)
decl_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|segment
argument_list|,
name|doc
argument_list|)
expr_stmt|;
name|directory
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|merge
specifier|private
name|void
name|merge
parameter_list|(
name|String
name|seg1
parameter_list|,
name|String
name|seg2
parameter_list|,
name|String
name|merged
parameter_list|,
name|boolean
name|useCompoundFile
parameter_list|)
throws|throws
name|Exception
block|{
name|Directory
name|directory
init|=
name|FSDirectory
operator|.
name|getDirectory
argument_list|(
name|indexDir
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|SegmentReader
name|r1
init|=
operator|new
name|SegmentReader
argument_list|(
operator|new
name|SegmentInfo
argument_list|(
name|seg1
argument_list|,
literal|1
argument_list|,
name|directory
argument_list|)
argument_list|)
decl_stmt|;
name|SegmentReader
name|r2
init|=
operator|new
name|SegmentReader
argument_list|(
operator|new
name|SegmentInfo
argument_list|(
name|seg2
argument_list|,
literal|1
argument_list|,
name|directory
argument_list|)
argument_list|)
decl_stmt|;
name|SegmentMerger
name|merger
init|=
operator|new
name|SegmentMerger
argument_list|(
name|directory
argument_list|,
name|merged
argument_list|,
name|useCompoundFile
argument_list|)
decl_stmt|;
name|merger
operator|.
name|add
argument_list|(
name|r1
argument_list|)
expr_stmt|;
name|merger
operator|.
name|add
argument_list|(
name|r2
argument_list|)
expr_stmt|;
name|merger
operator|.
name|merge
argument_list|()
expr_stmt|;
name|directory
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|printSegment
specifier|private
name|void
name|printSegment
parameter_list|(
name|PrintWriter
name|out
parameter_list|,
name|String
name|segment
parameter_list|)
throws|throws
name|Exception
block|{
name|Directory
name|directory
init|=
name|FSDirectory
operator|.
name|getDirectory
argument_list|(
name|indexDir
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|SegmentReader
name|reader
init|=
operator|new
name|SegmentReader
argument_list|(
operator|new
name|SegmentInfo
argument_list|(
name|segment
argument_list|,
literal|1
argument_list|,
name|directory
argument_list|)
argument_list|)
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
name|reader
operator|.
name|numDocs
argument_list|()
condition|;
name|i
operator|++
control|)
name|out
operator|.
name|println
argument_list|(
name|reader
operator|.
name|document
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
name|TermEnum
name|tis
init|=
name|reader
operator|.
name|terms
argument_list|()
decl_stmt|;
while|while
condition|(
name|tis
operator|.
name|next
argument_list|()
condition|)
block|{
name|out
operator|.
name|print
argument_list|(
name|tis
operator|.
name|term
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|" DF="
operator|+
name|tis
operator|.
name|docFreq
argument_list|()
argument_list|)
expr_stmt|;
name|TermPositions
name|positions
init|=
name|reader
operator|.
name|termPositions
argument_list|(
name|tis
operator|.
name|term
argument_list|()
argument_list|)
decl_stmt|;
try|try
block|{
while|while
condition|(
name|positions
operator|.
name|next
argument_list|()
condition|)
block|{
name|out
operator|.
name|print
argument_list|(
literal|" doc="
operator|+
name|positions
operator|.
name|doc
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|print
argument_list|(
literal|" TF="
operator|+
name|positions
operator|.
name|freq
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|print
argument_list|(
literal|" pos="
argument_list|)
expr_stmt|;
name|out
operator|.
name|print
argument_list|(
name|positions
operator|.
name|nextPosition
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|1
init|;
name|j
operator|<
name|positions
operator|.
name|freq
argument_list|()
condition|;
name|j
operator|++
control|)
name|out
operator|.
name|print
argument_list|(
literal|","
operator|+
name|positions
operator|.
name|nextPosition
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|""
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|positions
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
name|tis
operator|.
name|close
argument_list|()
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|directory
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class
end_unit
