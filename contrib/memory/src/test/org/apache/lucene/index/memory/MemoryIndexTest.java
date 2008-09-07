begin_unit
begin_package
DECL|package|org.apache.lucene.index.memory
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|memory
package|;
end_package
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedReader
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayInputStream
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileInputStream
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FilenameFilter
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStream
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStreamReader
import|;
end_import
begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|ByteBuffer
import|;
end_import
begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|Charset
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
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
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedHashSet
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import
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
name|StopAnalyzer
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
name|StopFilter
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
name|standard
operator|.
name|StandardAnalyzer
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
name|document
operator|.
name|Field
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
name|index
operator|.
name|IndexWriter
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
name|queryParser
operator|.
name|ParseException
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
name|queryParser
operator|.
name|QueryParser
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
name|HitCollector
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
name|IndexSearcher
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
name|Query
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
name|Searcher
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
name|RAMDirectory
import|;
end_import
begin_comment
comment|/** Verifies that Lucene MemoryIndex and RAMDirectory have the same behaviour, returning the same results for any given query. Runs a set of queries against a set of files and compares results for identity. Can also be used as a simple benchmark.<p> Example usage:<pre> cd lucene-svn java -server -cp ~/unix/java/share/misc/junit/junit.jar:build/classes:build/lucene-core-2.1-dev.jar:build/contrib/memory/classes/test:build/contrib/memory/classes/java org.apache.lucene.index.memory.MemoryIndexTest 1 1 memram @contrib/memory/src/test/org/apache/lucene/index/memory/testqueries.txt *.txt *.html *.xml xdocs/*.xml src/test/org/apache/lucene/queryParser/*.java contrib/memory/src/java/org/apache/lucene/index/memory/*.java</pre> where testqueries.txt is a file with one query per line, such as:<pre> # # queries extracted from TestQueryParser.java # Apache Apach~ AND Copy*  a AND b (a AND b) c OR (a AND b) a AND NOT b a AND -b a AND !b a&& b a&& ! b  a OR b a || b a OR !b a OR ! b a OR -b  +term -term term foo:term AND field:anotherTerm term AND "phrase phrase" "hello there"  germ term^2.0 (term)^2.0 (germ term)^2.0 term^2.0 term^2 "germ term"^2.0 "term germ"^2  (foo OR bar) AND (baz OR boo) ((a OR b) AND NOT c) OR d +(apple "steve jobs") -(foo bar baz) +title:(dog OR cat) -author:"bob dole"   a&b a&&b .NET  "term germ"~2 "term germ"~2 flork "term"~2 "~2 germ" "term germ"~2^2  3 term 1.0 1 2 term term1 term2  term* term*^2 term~ term~0.7 term~^2 term^2~ term*germ term*germ^3   term* Term* TERM* term* Term* TERM*  // Then 'full' wildcard queries: te?m Te?m TE?M Te?m*gerM te?m Te?m TE?M Te?m*gerM  term term term term +stop term term -stop term drop AND stop AND roll term phrase term term AND NOT phrase term stop   [ a TO c] [ a TO c ] { a TO c} { a TO c } { a TO c }^2.0 [ a TO c] OR bar [ a TO c] AND bar ( bar blar { a TO c})  gack ( bar blar { a TO c})    +weltbank +worlbank +weltbank\n+worlbank weltbank \n+worlbank weltbank \n +worlbank +weltbank\r+worlbank weltbank \r+worlbank weltbank \r +worlbank +weltbank\r\n+worlbank weltbank \r\n+worlbank weltbank \r\n +worlbank weltbank \r \n +worlbank +weltbank\t+worlbank weltbank \t+worlbank weltbank \t +worlbank   term term term term +term term term term +term term +term +term -term term term   on^1.0 "hello"^2.0 hello^2.0 "on"^1.0 the^3</pre>  @author whoschek.AT.lbl.DOT.gov */
end_comment
begin_class
DECL|class|MemoryIndexTest
specifier|public
class|class
name|MemoryIndexTest
extends|extends
name|TestCase
block|{
DECL|field|analyzer
specifier|private
name|Analyzer
name|analyzer
decl_stmt|;
DECL|field|fastMode
specifier|private
name|boolean
name|fastMode
init|=
literal|false
decl_stmt|;
DECL|field|verbose
specifier|private
specifier|final
name|boolean
name|verbose
init|=
literal|false
decl_stmt|;
DECL|field|FIELD_NAME
specifier|private
specifier|static
specifier|final
name|String
name|FIELD_NAME
init|=
literal|"content"
decl_stmt|;
comment|/** Runs the tests and/or benchmark */
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
operator|new
name|MemoryIndexTest
argument_list|()
operator|.
name|run
argument_list|(
name|args
argument_list|)
expr_stmt|;
block|}
comment|/* all files will be open relative to this */
DECL|field|fileDir
specifier|public
name|String
name|fileDir
decl_stmt|;
DECL|method|setUp
specifier|public
name|void
name|setUp
parameter_list|()
block|{
name|fileDir
operator|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"lucene.common.dir"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
comment|//  public void tearDown() {}
DECL|method|testMany
specifier|public
name|void
name|testMany
parameter_list|()
throws|throws
name|Throwable
block|{
name|String
index|[]
name|files
init|=
name|listFiles
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"*.txt"
block|,
literal|"*.html"
block|,
literal|"*.xml"
block|,
literal|"xdocs/*.xml"
block|,
literal|"src/java/test/org/apache/lucene/queryParser/*.java"
block|,
literal|"contrib/memory/src/java/org/apache/lucene/index/memory/*.java"
block|,     }
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"files = "
operator|+
name|java
operator|.
name|util
operator|.
name|Arrays
operator|.
name|asList
argument_list|(
name|files
argument_list|)
argument_list|)
expr_stmt|;
name|String
index|[]
name|xargs
init|=
operator|new
name|String
index|[]
block|{
literal|"1"
block|,
literal|"1"
block|,
literal|"memram"
block|,
literal|"@contrib/memory/src/test/org/apache/lucene/index/memory/testqueries.txt"
block|,     }
decl_stmt|;
name|String
index|[]
name|args
init|=
operator|new
name|String
index|[
name|xargs
operator|.
name|length
operator|+
name|files
operator|.
name|length
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|xargs
argument_list|,
literal|0
argument_list|,
name|args
argument_list|,
literal|0
argument_list|,
name|xargs
operator|.
name|length
argument_list|)
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|files
argument_list|,
literal|0
argument_list|,
name|args
argument_list|,
name|xargs
operator|.
name|length
argument_list|,
name|files
operator|.
name|length
argument_list|)
expr_stmt|;
name|run
argument_list|(
name|args
argument_list|)
expr_stmt|;
block|}
DECL|method|run
specifier|private
name|void
name|run
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|Throwable
block|{
name|int
name|k
init|=
operator|-
literal|1
decl_stmt|;
name|int
name|iters
init|=
literal|1
decl_stmt|;
if|if
condition|(
name|args
operator|.
name|length
operator|>
operator|++
name|k
condition|)
name|iters
operator|=
name|Math
operator|.
name|max
argument_list|(
literal|1
argument_list|,
name|Integer
operator|.
name|parseInt
argument_list|(
name|args
index|[
name|k
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|int
name|runs
init|=
literal|1
decl_stmt|;
if|if
condition|(
name|args
operator|.
name|length
operator|>
operator|++
name|k
condition|)
name|runs
operator|=
name|Math
operator|.
name|max
argument_list|(
literal|1
argument_list|,
name|Integer
operator|.
name|parseInt
argument_list|(
name|args
index|[
name|k
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|cmd
init|=
literal|"memram"
decl_stmt|;
if|if
condition|(
name|args
operator|.
name|length
operator|>
operator|++
name|k
condition|)
name|cmd
operator|=
name|args
index|[
name|k
index|]
expr_stmt|;
name|boolean
name|useMemIndex
init|=
name|cmd
operator|.
name|indexOf
argument_list|(
literal|"mem"
argument_list|)
operator|>=
literal|0
decl_stmt|;
name|boolean
name|useRAMIndex
init|=
name|cmd
operator|.
name|indexOf
argument_list|(
literal|"ram"
argument_list|)
operator|>=
literal|0
decl_stmt|;
name|String
index|[]
name|queries
init|=
block|{
literal|"term"
block|,
literal|"term*"
block|,
literal|"term~"
block|,
literal|"Apache"
block|,
literal|"Apach~ AND Copy*"
block|}
decl_stmt|;
if|if
condition|(
name|args
operator|.
name|length
operator|>
operator|++
name|k
condition|)
block|{
name|String
name|arg
init|=
name|args
index|[
name|k
index|]
decl_stmt|;
if|if
condition|(
name|arg
operator|.
name|startsWith
argument_list|(
literal|"@"
argument_list|)
condition|)
name|queries
operator|=
name|readLines
argument_list|(
operator|new
name|File
argument_list|(
name|fileDir
argument_list|,
name|arg
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
else|else
name|queries
operator|=
operator|new
name|String
index|[]
block|{
name|arg
block|}
expr_stmt|;
block|}
name|File
index|[]
name|files
init|=
operator|new
name|File
index|[]
block|{
operator|new
name|File
argument_list|(
literal|"CHANGES.txt"
argument_list|)
block|,
operator|new
name|File
argument_list|(
literal|"LICENSE.txt"
argument_list|)
block|}
decl_stmt|;
if|if
condition|(
name|args
operator|.
name|length
operator|>
operator|++
name|k
condition|)
block|{
name|files
operator|=
operator|new
name|File
index|[
name|args
operator|.
name|length
operator|-
name|k
index|]
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
name|k
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
block|{
name|files
index|[
name|i
operator|-
name|k
index|]
operator|=
operator|new
name|File
argument_list|(
name|args
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
name|boolean
name|toLowerCase
init|=
literal|true
decl_stmt|;
comment|//    boolean toLowerCase = false;
comment|//    Set stopWords = null;
name|Set
name|stopWords
init|=
name|StopFilter
operator|.
name|makeStopSet
argument_list|(
name|StopAnalyzer
operator|.
name|ENGLISH_STOP_WORDS
argument_list|)
decl_stmt|;
name|Analyzer
index|[]
name|analyzers
init|=
operator|new
name|Analyzer
index|[]
block|{
operator|new
name|SimpleAnalyzer
argument_list|()
block|,
operator|new
name|StopAnalyzer
argument_list|()
block|,
operator|new
name|StandardAnalyzer
argument_list|()
block|,
name|PatternAnalyzer
operator|.
name|DEFAULT_ANALYZER
block|,
comment|//        new WhitespaceAnalyzer(),
comment|//        new PatternAnalyzer(PatternAnalyzer.NON_WORD_PATTERN, false, null),
comment|//        new PatternAnalyzer(PatternAnalyzer.NON_WORD_PATTERN, true, stopWords),
comment|//        new SnowballAnalyzer("English", StopAnalyzer.ENGLISH_STOP_WORDS),
block|}
decl_stmt|;
for|for
control|(
name|int
name|iter
init|=
literal|0
init|;
name|iter
operator|<
name|iters
condition|;
name|iter
operator|++
control|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"\n########### iteration="
operator|+
name|iter
argument_list|)
expr_stmt|;
name|long
name|start
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|long
name|bytes
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|anal
init|=
literal|0
init|;
name|anal
operator|<
name|analyzers
operator|.
name|length
condition|;
name|anal
operator|++
control|)
block|{
name|this
operator|.
name|analyzer
operator|=
name|analyzers
index|[
name|anal
index|]
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
name|files
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|File
name|file
init|=
name|files
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
operator|!
name|file
operator|.
name|exists
argument_list|()
operator|||
name|file
operator|.
name|isDirectory
argument_list|()
condition|)
continue|continue;
comment|// ignore
name|bytes
operator|+=
name|file
operator|.
name|length
argument_list|()
expr_stmt|;
name|String
name|text
init|=
name|toString
argument_list|(
operator|new
name|FileInputStream
argument_list|(
name|file
argument_list|)
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|Document
name|doc
init|=
name|createDocument
argument_list|(
name|text
argument_list|)
decl_stmt|;
if|if
condition|(
name|verbose
condition|)
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"\n*********** FILE="
operator|+
name|file
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|q
init|=
literal|0
init|;
name|q
operator|<
name|queries
operator|.
name|length
condition|;
name|q
operator|++
control|)
block|{
try|try
block|{
name|Query
name|query
init|=
name|parseQuery
argument_list|(
name|queries
index|[
name|q
index|]
argument_list|)
decl_stmt|;
name|boolean
name|measureIndexing
init|=
literal|false
decl_stmt|;
comment|// toggle this to measure query performance
name|MemoryIndex
name|memind
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|useMemIndex
operator|&&
operator|!
name|measureIndexing
condition|)
name|memind
operator|=
name|createMemoryIndex
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|RAMDirectory
name|ramind
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|useRAMIndex
operator|&&
operator|!
name|measureIndexing
condition|)
name|ramind
operator|=
name|createRAMIndex
argument_list|(
name|doc
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|run
init|=
literal|0
init|;
name|run
operator|<
name|runs
condition|;
name|run
operator|++
control|)
block|{
name|float
name|score1
init|=
literal|0.0f
decl_stmt|;
name|float
name|score2
init|=
literal|0.0f
decl_stmt|;
if|if
condition|(
name|useMemIndex
operator|&&
name|measureIndexing
condition|)
name|memind
operator|=
name|createMemoryIndex
argument_list|(
name|doc
argument_list|)
expr_stmt|;
if|if
condition|(
name|useMemIndex
condition|)
name|score1
operator|=
name|query
argument_list|(
name|memind
argument_list|,
name|query
argument_list|)
expr_stmt|;
if|if
condition|(
name|useRAMIndex
operator|&&
name|measureIndexing
condition|)
name|ramind
operator|=
name|createRAMIndex
argument_list|(
name|doc
argument_list|)
expr_stmt|;
if|if
condition|(
name|useRAMIndex
condition|)
name|score2
operator|=
name|query
argument_list|(
name|ramind
argument_list|,
name|query
argument_list|)
expr_stmt|;
if|if
condition|(
name|useMemIndex
operator|&&
name|useRAMIndex
condition|)
block|{
if|if
condition|(
name|verbose
condition|)
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"diff="
operator|+
operator|(
name|score1
operator|-
name|score2
operator|)
operator|+
literal|", query="
operator|+
name|queries
index|[
name|q
index|]
operator|+
literal|", s1="
operator|+
name|score1
operator|+
literal|", s2="
operator|+
name|score2
argument_list|)
expr_stmt|;
if|if
condition|(
name|score1
operator|!=
name|score2
operator|||
name|score1
argument_list|<
literal|0.0f
operator|||
name|score2
argument_list|<
literal|0.0f
operator|||
name|score1
argument_list|>
literal|1.0f
operator|||
name|score2
argument_list|>
literal|1.0f
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"BUG DETECTED:"
operator|+
operator|(
name|i
operator|*
operator|(
name|q
operator|+
literal|1
operator|)
operator|)
operator|+
literal|" at query="
operator|+
name|queries
index|[
name|q
index|]
operator|+
literal|", file="
operator|+
name|file
operator|+
literal|", anal="
operator|+
name|analyzer
argument_list|)
throw|;
block|}
block|}
block|}
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
if|if
condition|(
name|t
operator|instanceof
name|OutOfMemoryError
condition|)
name|t
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Fatal error at query="
operator|+
name|queries
index|[
name|q
index|]
operator|+
literal|", file="
operator|+
name|file
operator|+
literal|", anal="
operator|+
name|analyzer
argument_list|)
expr_stmt|;
throw|throw
name|t
throw|;
block|}
block|}
block|}
block|}
name|long
name|end
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"\nsecs = "
operator|+
operator|(
operator|(
name|end
operator|-
name|start
operator|)
operator|/
literal|1000.0f
operator|)
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"queries/sec= "
operator|+
operator|(
literal|1.0f
operator|*
name|runs
operator|*
name|queries
operator|.
name|length
operator|*
name|analyzers
operator|.
name|length
operator|*
name|files
operator|.
name|length
operator|/
operator|(
operator|(
name|end
operator|-
name|start
operator|)
operator|/
literal|1000.0f
operator|)
operator|)
argument_list|)
expr_stmt|;
name|float
name|mb
init|=
operator|(
literal|1.0f
operator|*
name|bytes
operator|*
name|queries
operator|.
name|length
operator|*
name|runs
operator|)
operator|/
operator|(
literal|1024.0f
operator|*
literal|1024.0f
operator|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"MB/sec = "
operator|+
operator|(
name|mb
operator|/
operator|(
operator|(
name|end
operator|-
name|start
operator|)
operator|/
literal|1000.0f
operator|)
operator|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|useMemIndex
operator|&&
name|useRAMIndex
condition|)
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"No bug found. done."
argument_list|)
expr_stmt|;
else|else
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Done benchmarking (without checking correctness)."
argument_list|)
expr_stmt|;
block|}
comment|// returns file line by line, ignoring empty lines and comments
DECL|method|readLines
specifier|private
name|String
index|[]
name|readLines
parameter_list|(
name|File
name|file
parameter_list|)
throws|throws
name|Exception
block|{
name|BufferedReader
name|reader
init|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
operator|new
name|FileInputStream
argument_list|(
name|file
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|List
name|lines
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
name|String
name|line
decl_stmt|;
while|while
condition|(
operator|(
name|line
operator|=
name|reader
operator|.
name|readLine
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|String
name|t
init|=
name|line
operator|.
name|trim
argument_list|()
decl_stmt|;
if|if
condition|(
name|t
operator|.
name|length
argument_list|()
operator|>
literal|0
operator|&&
name|t
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
operator|!=
literal|'#'
operator|&&
operator|(
operator|!
name|t
operator|.
name|startsWith
argument_list|(
literal|"//"
argument_list|)
operator|)
condition|)
block|{
name|lines
operator|.
name|add
argument_list|(
name|line
argument_list|)
expr_stmt|;
block|}
block|}
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|String
index|[]
name|result
init|=
operator|new
name|String
index|[
name|lines
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
name|lines
operator|.
name|toArray
argument_list|(
name|result
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
DECL|method|createDocument
specifier|private
name|Document
name|createDocument
parameter_list|(
name|String
name|content
parameter_list|)
block|{
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|FIELD_NAME
argument_list|,
name|content
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|TOKENIZED
argument_list|,
name|Field
operator|.
name|TermVector
operator|.
name|WITH_POSITIONS
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|doc
return|;
block|}
DECL|method|createMemoryIndex
specifier|private
name|MemoryIndex
name|createMemoryIndex
parameter_list|(
name|Document
name|doc
parameter_list|)
block|{
name|MemoryIndex
name|index
init|=
operator|new
name|MemoryIndex
argument_list|()
decl_stmt|;
name|Iterator
name|iter
init|=
name|doc
operator|.
name|getFields
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|iter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Field
name|field
init|=
operator|(
name|Field
operator|)
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|index
operator|.
name|addField
argument_list|(
name|field
operator|.
name|name
argument_list|()
argument_list|,
name|field
operator|.
name|stringValue
argument_list|()
argument_list|,
name|analyzer
argument_list|)
expr_stmt|;
block|}
return|return
name|index
return|;
block|}
DECL|method|createRAMIndex
specifier|private
name|RAMDirectory
name|createRAMIndex
parameter_list|(
name|Document
name|doc
parameter_list|)
block|{
name|RAMDirectory
name|dir
init|=
operator|new
name|RAMDirectory
argument_list|()
decl_stmt|;
name|IndexWriter
name|writer
init|=
literal|null
decl_stmt|;
try|try
block|{
name|writer
operator|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|analyzer
argument_list|,
literal|true
argument_list|,
name|IndexWriter
operator|.
name|MaxFieldLength
operator|.
name|UNLIMITED
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|writer
operator|.
name|optimize
argument_list|()
expr_stmt|;
return|return
name|dir
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// should never happen (RAMDirectory)
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
try|try
block|{
if|if
condition|(
name|writer
operator|!=
literal|null
condition|)
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// should never happen (RAMDirectory)
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
DECL|method|query
specifier|private
name|float
name|query
parameter_list|(
name|Object
name|index
parameter_list|,
name|Query
name|query
parameter_list|)
block|{
comment|//    System.out.println("MB=" + (getMemorySize(index) / (1024.0f * 1024.0f)));
name|Searcher
name|searcher
init|=
literal|null
decl_stmt|;
try|try
block|{
if|if
condition|(
name|index
operator|instanceof
name|Directory
condition|)
name|searcher
operator|=
operator|new
name|IndexSearcher
argument_list|(
operator|(
name|Directory
operator|)
name|index
argument_list|)
expr_stmt|;
else|else
name|searcher
operator|=
operator|(
operator|(
name|MemoryIndex
operator|)
name|index
operator|)
operator|.
name|createSearcher
argument_list|()
expr_stmt|;
specifier|final
name|float
index|[]
name|scores
init|=
operator|new
name|float
index|[
literal|1
index|]
decl_stmt|;
comment|// inits to 0.0f
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
operator|new
name|HitCollector
argument_list|()
block|{
specifier|public
name|void
name|collect
parameter_list|(
name|int
name|doc
parameter_list|,
name|float
name|score
parameter_list|)
block|{
name|scores
index|[
literal|0
index|]
operator|=
name|score
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|float
name|score
init|=
name|scores
index|[
literal|0
index|]
decl_stmt|;
comment|//      Hits hits = searcher.search(query);
comment|//      float score = hits.length()> 0 ? hits.score(0) : 0.0f;
return|return
name|score
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// should never happen (RAMDirectory)
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
try|try
block|{
if|if
condition|(
name|searcher
operator|!=
literal|null
condition|)
name|searcher
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// should never happen (RAMDirectory)
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
DECL|method|getMemorySize
specifier|private
name|int
name|getMemorySize
parameter_list|(
name|Object
name|index
parameter_list|)
block|{
if|if
condition|(
name|index
operator|instanceof
name|Directory
condition|)
block|{
try|try
block|{
name|Directory
name|dir
init|=
operator|(
name|Directory
operator|)
name|index
decl_stmt|;
name|int
name|size
init|=
literal|0
decl_stmt|;
name|String
index|[]
name|fileNames
init|=
name|dir
operator|.
name|list
argument_list|()
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
name|fileNames
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|size
operator|+=
name|dir
operator|.
name|fileLength
argument_list|(
name|fileNames
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
return|return
name|size
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// can never happen (RAMDirectory)
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
else|else
block|{
return|return
operator|(
operator|(
name|MemoryIndex
operator|)
name|index
operator|)
operator|.
name|getMemorySize
argument_list|()
return|;
block|}
block|}
DECL|method|parseQuery
specifier|private
name|Query
name|parseQuery
parameter_list|(
name|String
name|expression
parameter_list|)
throws|throws
name|ParseException
block|{
name|QueryParser
name|parser
init|=
operator|new
name|QueryParser
argument_list|(
name|FIELD_NAME
argument_list|,
name|analyzer
argument_list|)
decl_stmt|;
comment|//    parser.setPhraseSlop(0);
return|return
name|parser
operator|.
name|parse
argument_list|(
name|expression
argument_list|)
return|;
block|}
comment|/** returns all files matching the given file name patterns (quick n'dirty) */
DECL|method|listFiles
specifier|static
name|String
index|[]
name|listFiles
parameter_list|(
name|String
index|[]
name|fileNames
parameter_list|)
block|{
name|LinkedHashSet
name|allFiles
init|=
operator|new
name|LinkedHashSet
argument_list|()
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
name|fileNames
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|int
name|k
decl_stmt|;
if|if
condition|(
operator|(
name|k
operator|=
name|fileNames
index|[
name|i
index|]
operator|.
name|indexOf
argument_list|(
literal|"*"
argument_list|)
operator|)
operator|<
literal|0
condition|)
block|{
name|allFiles
operator|.
name|add
argument_list|(
name|fileNames
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|String
name|prefix
init|=
name|fileNames
index|[
name|i
index|]
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|k
argument_list|)
decl_stmt|;
if|if
condition|(
name|prefix
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
name|prefix
operator|=
literal|"."
expr_stmt|;
specifier|final
name|String
name|suffix
init|=
name|fileNames
index|[
name|i
index|]
operator|.
name|substring
argument_list|(
name|k
operator|+
literal|1
argument_list|)
decl_stmt|;
name|File
index|[]
name|files
init|=
operator|new
name|File
argument_list|(
name|prefix
argument_list|)
operator|.
name|listFiles
argument_list|(
operator|new
name|FilenameFilter
argument_list|()
block|{
specifier|public
name|boolean
name|accept
parameter_list|(
name|File
name|dir
parameter_list|,
name|String
name|name
parameter_list|)
block|{
return|return
name|name
operator|.
name|endsWith
argument_list|(
name|suffix
argument_list|)
return|;
block|}
block|}
argument_list|)
decl_stmt|;
if|if
condition|(
name|files
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|files
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|allFiles
operator|.
name|add
argument_list|(
name|files
index|[
name|j
index|]
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
name|String
index|[]
name|result
init|=
operator|new
name|String
index|[
name|allFiles
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
name|allFiles
operator|.
name|toArray
argument_list|(
name|result
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
comment|// trick to detect default platform charset
DECL|field|DEFAULT_PLATFORM_CHARSET
specifier|private
specifier|static
specifier|final
name|Charset
name|DEFAULT_PLATFORM_CHARSET
init|=
name|Charset
operator|.
name|forName
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
operator|new
name|byte
index|[
literal|0
index|]
argument_list|)
argument_list|)
operator|.
name|getEncoding
argument_list|()
argument_list|)
decl_stmt|;
comment|// the following utility methods below are copied from Apache style Nux library - see http://dsd.lbl.gov/nux
DECL|method|toString
specifier|private
specifier|static
name|String
name|toString
parameter_list|(
name|InputStream
name|input
parameter_list|,
name|Charset
name|charset
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|charset
operator|==
literal|null
condition|)
name|charset
operator|=
name|DEFAULT_PLATFORM_CHARSET
expr_stmt|;
name|byte
index|[]
name|data
init|=
name|toByteArray
argument_list|(
name|input
argument_list|)
decl_stmt|;
return|return
name|charset
operator|.
name|decode
argument_list|(
name|ByteBuffer
operator|.
name|wrap
argument_list|(
name|data
argument_list|)
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|toByteArray
specifier|private
specifier|static
name|byte
index|[]
name|toByteArray
parameter_list|(
name|InputStream
name|input
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
comment|// safe and fast even if input.available() behaves weird or buggy
name|int
name|len
init|=
name|Math
operator|.
name|max
argument_list|(
literal|256
argument_list|,
name|input
operator|.
name|available
argument_list|()
argument_list|)
decl_stmt|;
name|byte
index|[]
name|buffer
init|=
operator|new
name|byte
index|[
name|len
index|]
decl_stmt|;
name|byte
index|[]
name|output
init|=
operator|new
name|byte
index|[
name|len
index|]
decl_stmt|;
name|len
operator|=
literal|0
expr_stmt|;
name|int
name|n
decl_stmt|;
while|while
condition|(
operator|(
name|n
operator|=
name|input
operator|.
name|read
argument_list|(
name|buffer
argument_list|)
operator|)
operator|>=
literal|0
condition|)
block|{
if|if
condition|(
name|len
operator|+
name|n
operator|>
name|output
operator|.
name|length
condition|)
block|{
comment|// grow capacity
name|byte
name|tmp
index|[]
init|=
operator|new
name|byte
index|[
name|Math
operator|.
name|max
argument_list|(
name|output
operator|.
name|length
operator|<<
literal|1
argument_list|,
name|len
operator|+
name|n
argument_list|)
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|output
argument_list|,
literal|0
argument_list|,
name|tmp
argument_list|,
literal|0
argument_list|,
name|len
argument_list|)
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|tmp
argument_list|,
name|len
argument_list|,
name|n
argument_list|)
expr_stmt|;
name|buffer
operator|=
name|output
expr_stmt|;
comment|// use larger buffer for future larger bulk reads
name|output
operator|=
name|tmp
expr_stmt|;
block|}
else|else
block|{
name|System
operator|.
name|arraycopy
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|output
argument_list|,
name|len
argument_list|,
name|n
argument_list|)
expr_stmt|;
block|}
name|len
operator|+=
name|n
expr_stmt|;
block|}
if|if
condition|(
name|len
operator|==
name|output
operator|.
name|length
condition|)
return|return
name|output
return|;
name|buffer
operator|=
literal|null
expr_stmt|;
comment|// help gc
name|buffer
operator|=
operator|new
name|byte
index|[
name|len
index|]
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|output
argument_list|,
literal|0
argument_list|,
name|buffer
argument_list|,
literal|0
argument_list|,
name|len
argument_list|)
expr_stmt|;
return|return
name|buffer
return|;
block|}
finally|finally
block|{
if|if
condition|(
name|input
operator|!=
literal|null
condition|)
name|input
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
