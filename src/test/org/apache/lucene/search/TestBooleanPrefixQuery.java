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
name|junit
operator|.
name|framework
operator|.
name|Test
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
name|store
operator|.
name|RAMDirectory
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
name|index
operator|.
name|IndexReader
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
name|WhitespaceAnalyzer
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
name|search
operator|.
name|PrefixQuery
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
name|BooleanQuery
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
comment|/**  *  * @version $Id$  **/
end_comment
begin_class
DECL|class|TestBooleanPrefixQuery
specifier|public
class|class
name|TestBooleanPrefixQuery
extends|extends
name|LuceneTestCase
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
block|{
name|TestRunner
operator|.
name|run
argument_list|(
name|suite
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|suite
specifier|public
specifier|static
name|Test
name|suite
parameter_list|()
block|{
return|return
operator|new
name|TestSuite
argument_list|(
name|TestBooleanPrefixQuery
operator|.
name|class
argument_list|)
return|;
block|}
DECL|method|TestBooleanPrefixQuery
specifier|public
name|TestBooleanPrefixQuery
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
DECL|method|testMethod
specifier|public
name|void
name|testMethod
parameter_list|()
block|{
name|RAMDirectory
name|directory
init|=
operator|new
name|RAMDirectory
argument_list|()
decl_stmt|;
name|String
index|[]
name|categories
init|=
operator|new
name|String
index|[]
block|{
literal|"food"
block|,
literal|"foodanddrink"
block|,
literal|"foodanddrinkandgoodtimes"
block|,
literal|"food and drink"
block|}
decl_stmt|;
name|Query
name|rw1
init|=
literal|null
decl_stmt|;
name|Query
name|rw2
init|=
literal|null
decl_stmt|;
try|try
block|{
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|directory
argument_list|,
operator|new
name|WhitespaceAnalyzer
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
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|categories
operator|.
name|length
condition|;
name|i
operator|++
control|)
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
literal|"category"
argument_list|,
name|categories
index|[
name|i
index|]
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
name|UN_TOKENIZED
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
block|}
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|IndexReader
name|reader
init|=
name|IndexReader
operator|.
name|open
argument_list|(
name|directory
argument_list|)
decl_stmt|;
name|PrefixQuery
name|query
init|=
operator|new
name|PrefixQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"category"
argument_list|,
literal|"foo"
argument_list|)
argument_list|)
decl_stmt|;
name|rw1
operator|=
name|query
operator|.
name|rewrite
argument_list|(
name|reader
argument_list|)
expr_stmt|;
name|BooleanQuery
name|bq
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
name|bq
operator|.
name|add
argument_list|(
name|query
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
name|rw2
operator|=
name|bq
operator|.
name|rewrite
argument_list|(
name|reader
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|fail
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|BooleanQuery
name|bq1
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|rw1
operator|instanceof
name|BooleanQuery
condition|)
block|{
name|bq1
operator|=
operator|(
name|BooleanQuery
operator|)
name|rw1
expr_stmt|;
block|}
name|BooleanQuery
name|bq2
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|rw2
operator|instanceof
name|BooleanQuery
condition|)
block|{
name|bq2
operator|=
operator|(
name|BooleanQuery
operator|)
name|rw2
expr_stmt|;
block|}
else|else
block|{
name|fail
argument_list|(
literal|"Rewrite"
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|"Number of Clauses Mismatch"
argument_list|,
name|bq1
operator|.
name|getClauses
argument_list|()
operator|.
name|length
argument_list|,
name|bq2
operator|.
name|getClauses
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
