begin_unit
begin_package
DECL|package|org.apache.lucene
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
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
name|util
operator|.
name|*
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
name|*
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
name|*
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
name|*
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
name|*
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
name|*
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
name|*
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
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
begin_class
DECL|class|ThreadSafetyTest
class|class
name|ThreadSafetyTest
block|{
DECL|field|ANALYZER
specifier|private
specifier|static
specifier|final
name|Analyzer
name|ANALYZER
init|=
operator|new
name|SimpleAnalyzer
argument_list|()
decl_stmt|;
DECL|field|RANDOM
specifier|private
specifier|static
specifier|final
name|Random
name|RANDOM
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
DECL|field|SEARCHER
specifier|private
specifier|static
name|Searcher
name|SEARCHER
decl_stmt|;
DECL|field|ITERATIONS
specifier|private
specifier|static
name|int
name|ITERATIONS
init|=
literal|1
decl_stmt|;
DECL|method|random
specifier|private
specifier|static
name|int
name|random
parameter_list|(
name|int
name|i
parameter_list|)
block|{
comment|// for JDK 1.1 compatibility
name|int
name|r
init|=
name|RANDOM
operator|.
name|nextInt
argument_list|()
decl_stmt|;
if|if
condition|(
name|r
operator|<
literal|0
condition|)
name|r
operator|=
operator|-
name|r
expr_stmt|;
return|return
name|r
operator|%
name|i
return|;
block|}
DECL|class|IndexerThread
specifier|private
specifier|static
class|class
name|IndexerThread
extends|extends
name|Thread
block|{
DECL|field|reopenInterval
specifier|private
specifier|final
name|int
name|reopenInterval
init|=
literal|30
operator|+
name|random
argument_list|(
literal|60
argument_list|)
decl_stmt|;
DECL|field|writer
name|IndexWriter
name|writer
decl_stmt|;
DECL|method|IndexerThread
specifier|public
name|IndexerThread
parameter_list|(
name|IndexWriter
name|writer
parameter_list|)
block|{
name|this
operator|.
name|writer
operator|=
name|writer
expr_stmt|;
block|}
DECL|method|run
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|boolean
name|useCompoundFiles
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
literal|1024
operator|*
name|ITERATIONS
condition|;
name|i
operator|++
control|)
block|{
name|Document
name|d
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|int
name|n
init|=
name|RANDOM
operator|.
name|nextInt
argument_list|()
decl_stmt|;
name|d
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"id"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|n
argument_list|)
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|NOT_ANALYZED
argument_list|)
argument_list|)
expr_stmt|;
name|d
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"contents"
argument_list|,
name|English
operator|.
name|intToEnglish
argument_list|(
name|n
argument_list|)
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
name|ANALYZED
argument_list|)
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Adding "
operator|+
name|n
argument_list|)
expr_stmt|;
comment|// Switch between single and multiple file segments
name|useCompoundFiles
operator|=
name|Math
operator|.
name|random
argument_list|()
operator|<
literal|0.5
expr_stmt|;
name|writer
operator|.
name|setUseCompoundFile
argument_list|(
name|useCompoundFiles
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|d
argument_list|)
expr_stmt|;
if|if
condition|(
name|i
operator|%
name|reopenInterval
operator|==
literal|0
condition|)
block|{
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|writer
operator|=
operator|new
name|IndexWriter
argument_list|(
literal|"index"
argument_list|,
name|ANALYZER
argument_list|,
literal|false
argument_list|,
name|IndexWriter
operator|.
name|MaxFieldLength
operator|.
name|LIMITED
argument_list|)
expr_stmt|;
block|}
block|}
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|e
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|e
operator|.
name|printStackTrace
argument_list|()
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
block|}
DECL|class|SearcherThread
specifier|private
specifier|static
class|class
name|SearcherThread
extends|extends
name|Thread
block|{
DECL|field|searcher
specifier|private
name|IndexSearcher
name|searcher
decl_stmt|;
DECL|field|reopenInterval
specifier|private
specifier|final
name|int
name|reopenInterval
init|=
literal|10
operator|+
name|random
argument_list|(
literal|20
argument_list|)
decl_stmt|;
DECL|method|SearcherThread
specifier|public
name|SearcherThread
parameter_list|(
name|boolean
name|useGlobal
parameter_list|)
throws|throws
name|java
operator|.
name|io
operator|.
name|IOException
block|{
if|if
condition|(
operator|!
name|useGlobal
condition|)
name|this
operator|.
name|searcher
operator|=
operator|new
name|IndexSearcher
argument_list|(
literal|"index"
argument_list|)
expr_stmt|;
block|}
DECL|method|run
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|512
operator|*
name|ITERATIONS
condition|;
name|i
operator|++
control|)
block|{
name|searchFor
argument_list|(
name|RANDOM
operator|.
name|nextInt
argument_list|()
argument_list|,
operator|(
name|searcher
operator|==
literal|null
operator|)
condition|?
name|SEARCHER
else|:
name|searcher
argument_list|)
expr_stmt|;
if|if
condition|(
name|i
operator|%
name|reopenInterval
operator|==
literal|0
condition|)
block|{
if|if
condition|(
name|searcher
operator|==
literal|null
condition|)
block|{
name|SEARCHER
operator|=
operator|new
name|IndexSearcher
argument_list|(
literal|"index"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|searcher
operator|.
name|close
argument_list|()
expr_stmt|;
name|searcher
operator|=
operator|new
name|IndexSearcher
argument_list|(
literal|"index"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|e
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|e
operator|.
name|printStackTrace
argument_list|()
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
DECL|method|searchFor
specifier|private
name|void
name|searchFor
parameter_list|(
name|int
name|n
parameter_list|,
name|Searcher
name|searcher
parameter_list|)
throws|throws
name|Exception
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Searching for "
operator|+
name|n
argument_list|)
expr_stmt|;
name|QueryParser
name|parser
init|=
operator|new
name|QueryParser
argument_list|(
literal|"contents"
argument_list|,
name|ANALYZER
argument_list|)
decl_stmt|;
name|ScoreDoc
index|[]
name|hits
init|=
name|searcher
operator|.
name|search
argument_list|(
name|parser
operator|.
name|parse
argument_list|(
name|English
operator|.
name|intToEnglish
argument_list|(
name|n
argument_list|)
argument_list|)
argument_list|,
literal|null
argument_list|,
literal|1000
argument_list|)
operator|.
name|scoreDocs
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Search for "
operator|+
name|n
operator|+
literal|": total="
operator|+
name|hits
operator|.
name|length
argument_list|)
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
name|Math
operator|.
name|min
argument_list|(
literal|3
argument_list|,
name|hits
operator|.
name|length
argument_list|)
condition|;
name|j
operator|++
control|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Hit for "
operator|+
name|n
operator|+
literal|": "
operator|+
name|searcher
operator|.
name|doc
argument_list|(
name|hits
index|[
name|j
index|]
operator|.
name|doc
argument_list|)
operator|.
name|get
argument_list|(
literal|"id"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
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
name|Exception
block|{
name|boolean
name|readOnly
init|=
literal|false
decl_stmt|;
name|boolean
name|add
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
block|{
if|if
condition|(
literal|"-ro"
operator|.
name|equals
argument_list|(
name|args
index|[
name|i
index|]
argument_list|)
condition|)
name|readOnly
operator|=
literal|true
expr_stmt|;
if|if
condition|(
literal|"-add"
operator|.
name|equals
argument_list|(
name|args
index|[
name|i
index|]
argument_list|)
condition|)
name|add
operator|=
literal|true
expr_stmt|;
block|}
name|File
name|indexDir
init|=
operator|new
name|File
argument_list|(
literal|"index"
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|indexDir
operator|.
name|exists
argument_list|()
condition|)
name|indexDir
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|IndexReader
operator|.
name|unlock
argument_list|(
name|FSDirectory
operator|.
name|getDirectory
argument_list|(
name|indexDir
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|readOnly
condition|)
block|{
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|indexDir
argument_list|,
name|ANALYZER
argument_list|,
operator|!
name|add
argument_list|,
name|IndexWriter
operator|.
name|MaxFieldLength
operator|.
name|LIMITED
argument_list|)
decl_stmt|;
name|Thread
name|indexerThread
init|=
operator|new
name|IndexerThread
argument_list|(
name|writer
argument_list|)
decl_stmt|;
name|indexerThread
operator|.
name|start
argument_list|()
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
block|}
name|SearcherThread
name|searcherThread1
init|=
operator|new
name|SearcherThread
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|searcherThread1
operator|.
name|start
argument_list|()
expr_stmt|;
name|SEARCHER
operator|=
operator|new
name|IndexSearcher
argument_list|(
name|indexDir
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|SearcherThread
name|searcherThread2
init|=
operator|new
name|SearcherThread
argument_list|(
literal|true
argument_list|)
decl_stmt|;
name|searcherThread2
operator|.
name|start
argument_list|()
expr_stmt|;
name|SearcherThread
name|searcherThread3
init|=
operator|new
name|SearcherThread
argument_list|(
literal|true
argument_list|)
decl_stmt|;
name|searcherThread3
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
block|}
end_class
end_unit
