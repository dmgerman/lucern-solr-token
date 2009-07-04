begin_unit
begin_package
DECL|package|org.apache.lucene.search
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
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
name|LuceneTestCase
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
name|document
operator|.
name|DateTools
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
name|index
operator|.
name|Term
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
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import
begin_comment
comment|/**   * DateFilter JUnit tests.   *   *   * @version $Revision$   */
end_comment
begin_class
DECL|class|TestDateFilter
specifier|public
class|class
name|TestDateFilter
extends|extends
name|LuceneTestCase
block|{
DECL|method|TestDateFilter
specifier|public
name|TestDateFilter
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
comment|/**      *      */
DECL|method|testBefore
specifier|public
specifier|static
name|void
name|testBefore
parameter_list|()
throws|throws
name|IOException
block|{
comment|// create an index
name|RAMDirectory
name|indexStore
init|=
operator|new
name|RAMDirectory
argument_list|()
decl_stmt|;
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|indexStore
argument_list|,
operator|new
name|SimpleAnalyzer
argument_list|()
argument_list|,
literal|true
argument_list|,
name|IndexWriter
operator|.
name|MaxFieldLength
operator|.
name|LIMITED
argument_list|)
decl_stmt|;
name|long
name|now
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
comment|// add time that is in the past
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"datefield"
argument_list|,
name|DateTools
operator|.
name|timeToString
argument_list|(
name|now
operator|-
literal|1000
argument_list|,
name|DateTools
operator|.
name|Resolution
operator|.
name|MILLISECOND
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
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"body"
argument_list|,
literal|"Today is a very sunny day in New York City"
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
name|ANALYZED
argument_list|)
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
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|IndexSearcher
name|searcher
init|=
operator|new
name|IndexSearcher
argument_list|(
name|indexStore
argument_list|)
decl_stmt|;
comment|// filter that should preserve matches
comment|//DateFilter df1 = DateFilter.Before("datefield", now);
name|TermRangeFilter
name|df1
init|=
operator|new
name|TermRangeFilter
argument_list|(
literal|"datefield"
argument_list|,
name|DateTools
operator|.
name|timeToString
argument_list|(
name|now
operator|-
literal|2000
argument_list|,
name|DateTools
operator|.
name|Resolution
operator|.
name|MILLISECOND
argument_list|)
argument_list|,
name|DateTools
operator|.
name|timeToString
argument_list|(
name|now
argument_list|,
name|DateTools
operator|.
name|Resolution
operator|.
name|MILLISECOND
argument_list|)
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
decl_stmt|;
comment|// filter that should discard matches
comment|//DateFilter df2 = DateFilter.Before("datefield", now - 999999);
name|TermRangeFilter
name|df2
init|=
operator|new
name|TermRangeFilter
argument_list|(
literal|"datefield"
argument_list|,
name|DateTools
operator|.
name|timeToString
argument_list|(
literal|0
argument_list|,
name|DateTools
operator|.
name|Resolution
operator|.
name|MILLISECOND
argument_list|)
argument_list|,
name|DateTools
operator|.
name|timeToString
argument_list|(
name|now
operator|-
literal|2000
argument_list|,
name|DateTools
operator|.
name|Resolution
operator|.
name|MILLISECOND
argument_list|)
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
decl_stmt|;
comment|// search something that doesn't exist with DateFilter
name|Query
name|query1
init|=
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"body"
argument_list|,
literal|"NoMatchForThis"
argument_list|)
argument_list|)
decl_stmt|;
comment|// search for something that does exists
name|Query
name|query2
init|=
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"body"
argument_list|,
literal|"sunny"
argument_list|)
argument_list|)
decl_stmt|;
name|ScoreDoc
index|[]
name|result
decl_stmt|;
comment|// ensure that queries return expected results without DateFilter first
name|result
operator|=
name|searcher
operator|.
name|search
argument_list|(
name|query1
argument_list|,
literal|null
argument_list|,
literal|1000
argument_list|)
operator|.
name|scoreDocs
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|result
operator|.
name|length
argument_list|)
expr_stmt|;
name|result
operator|=
name|searcher
operator|.
name|search
argument_list|(
name|query2
argument_list|,
literal|null
argument_list|,
literal|1000
argument_list|)
operator|.
name|scoreDocs
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|result
operator|.
name|length
argument_list|)
expr_stmt|;
comment|// run queries with DateFilter
name|result
operator|=
name|searcher
operator|.
name|search
argument_list|(
name|query1
argument_list|,
name|df1
argument_list|,
literal|1000
argument_list|)
operator|.
name|scoreDocs
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|result
operator|.
name|length
argument_list|)
expr_stmt|;
name|result
operator|=
name|searcher
operator|.
name|search
argument_list|(
name|query1
argument_list|,
name|df2
argument_list|,
literal|1000
argument_list|)
operator|.
name|scoreDocs
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|result
operator|.
name|length
argument_list|)
expr_stmt|;
name|result
operator|=
name|searcher
operator|.
name|search
argument_list|(
name|query2
argument_list|,
name|df1
argument_list|,
literal|1000
argument_list|)
operator|.
name|scoreDocs
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|result
operator|.
name|length
argument_list|)
expr_stmt|;
name|result
operator|=
name|searcher
operator|.
name|search
argument_list|(
name|query2
argument_list|,
name|df2
argument_list|,
literal|1000
argument_list|)
operator|.
name|scoreDocs
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|result
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
comment|/**      *      */
DECL|method|testAfter
specifier|public
specifier|static
name|void
name|testAfter
parameter_list|()
throws|throws
name|IOException
block|{
comment|// create an index
name|RAMDirectory
name|indexStore
init|=
operator|new
name|RAMDirectory
argument_list|()
decl_stmt|;
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|indexStore
argument_list|,
operator|new
name|SimpleAnalyzer
argument_list|()
argument_list|,
literal|true
argument_list|,
name|IndexWriter
operator|.
name|MaxFieldLength
operator|.
name|LIMITED
argument_list|)
decl_stmt|;
name|long
name|now
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
comment|// add time that is in the future
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"datefield"
argument_list|,
name|DateTools
operator|.
name|timeToString
argument_list|(
name|now
operator|+
literal|888888
argument_list|,
name|DateTools
operator|.
name|Resolution
operator|.
name|MILLISECOND
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
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"body"
argument_list|,
literal|"Today is a very sunny day in New York City"
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
name|ANALYZED
argument_list|)
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
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|IndexSearcher
name|searcher
init|=
operator|new
name|IndexSearcher
argument_list|(
name|indexStore
argument_list|)
decl_stmt|;
comment|// filter that should preserve matches
comment|//DateFilter df1 = DateFilter.After("datefield", now);
name|TermRangeFilter
name|df1
init|=
operator|new
name|TermRangeFilter
argument_list|(
literal|"datefield"
argument_list|,
name|DateTools
operator|.
name|timeToString
argument_list|(
name|now
argument_list|,
name|DateTools
operator|.
name|Resolution
operator|.
name|MILLISECOND
argument_list|)
argument_list|,
name|DateTools
operator|.
name|timeToString
argument_list|(
name|now
operator|+
literal|999999
argument_list|,
name|DateTools
operator|.
name|Resolution
operator|.
name|MILLISECOND
argument_list|)
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
decl_stmt|;
comment|// filter that should discard matches
comment|//DateFilter df2 = DateFilter.After("datefield", now + 999999);
name|TermRangeFilter
name|df2
init|=
operator|new
name|TermRangeFilter
argument_list|(
literal|"datefield"
argument_list|,
name|DateTools
operator|.
name|timeToString
argument_list|(
name|now
operator|+
literal|999999
argument_list|,
name|DateTools
operator|.
name|Resolution
operator|.
name|MILLISECOND
argument_list|)
argument_list|,
name|DateTools
operator|.
name|timeToString
argument_list|(
name|now
operator|+
literal|999999999
argument_list|,
name|DateTools
operator|.
name|Resolution
operator|.
name|MILLISECOND
argument_list|)
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
decl_stmt|;
comment|// search something that doesn't exist with DateFilter
name|Query
name|query1
init|=
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"body"
argument_list|,
literal|"NoMatchForThis"
argument_list|)
argument_list|)
decl_stmt|;
comment|// search for something that does exists
name|Query
name|query2
init|=
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"body"
argument_list|,
literal|"sunny"
argument_list|)
argument_list|)
decl_stmt|;
name|ScoreDoc
index|[]
name|result
decl_stmt|;
comment|// ensure that queries return expected results without DateFilter first
name|result
operator|=
name|searcher
operator|.
name|search
argument_list|(
name|query1
argument_list|,
literal|null
argument_list|,
literal|1000
argument_list|)
operator|.
name|scoreDocs
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|result
operator|.
name|length
argument_list|)
expr_stmt|;
name|result
operator|=
name|searcher
operator|.
name|search
argument_list|(
name|query2
argument_list|,
literal|null
argument_list|,
literal|1000
argument_list|)
operator|.
name|scoreDocs
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|result
operator|.
name|length
argument_list|)
expr_stmt|;
comment|// run queries with DateFilter
name|result
operator|=
name|searcher
operator|.
name|search
argument_list|(
name|query1
argument_list|,
name|df1
argument_list|,
literal|1000
argument_list|)
operator|.
name|scoreDocs
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|result
operator|.
name|length
argument_list|)
expr_stmt|;
name|result
operator|=
name|searcher
operator|.
name|search
argument_list|(
name|query1
argument_list|,
name|df2
argument_list|,
literal|1000
argument_list|)
operator|.
name|scoreDocs
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|result
operator|.
name|length
argument_list|)
expr_stmt|;
name|result
operator|=
name|searcher
operator|.
name|search
argument_list|(
name|query2
argument_list|,
name|df1
argument_list|,
literal|1000
argument_list|)
operator|.
name|scoreDocs
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|result
operator|.
name|length
argument_list|)
expr_stmt|;
name|result
operator|=
name|searcher
operator|.
name|search
argument_list|(
name|query2
argument_list|,
name|df2
argument_list|,
literal|1000
argument_list|)
operator|.
name|scoreDocs
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|result
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
