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
name|java
operator|.
name|text
operator|.
name|DecimalFormat
import|;
end_import
begin_import
import|import
name|java
operator|.
name|text
operator|.
name|DecimalFormatSymbols
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
name|Locale
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
name|index
operator|.
name|RandomIndexWriter
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
name|util
operator|.
name|_TestUtil
import|;
end_import
begin_comment
comment|/**  * Create an index with terms from 0000-9999.  * Generates random wildcards according to patterns,  * and validates the correct number of hits are returned.  */
end_comment
begin_class
DECL|class|TestWildcardRandom
specifier|public
class|class
name|TestWildcardRandom
extends|extends
name|LuceneTestCase
block|{
DECL|field|searcher
specifier|private
name|Searcher
name|searcher
decl_stmt|;
DECL|field|random
specifier|private
name|Random
name|random
decl_stmt|;
DECL|field|dir
specifier|private
name|Directory
name|dir
decl_stmt|;
annotation|@
name|Override
DECL|method|setUp
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|random
operator|=
name|newRandom
argument_list|()
expr_stmt|;
name|dir
operator|=
operator|new
name|RAMDirectory
argument_list|()
expr_stmt|;
name|RandomIndexWriter
name|writer
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|,
name|dir
argument_list|)
decl_stmt|;
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|Field
name|field
init|=
operator|new
name|Field
argument_list|(
literal|"field"
argument_list|,
literal|""
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
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|NumberFormat
name|df
init|=
operator|new
name|DecimalFormat
argument_list|(
literal|"0000"
argument_list|,
operator|new
name|DecimalFormatSymbols
argument_list|(
name|Locale
operator|.
name|ENGLISH
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
literal|10000
condition|;
name|i
operator|++
control|)
block|{
name|field
operator|.
name|setValue
argument_list|(
name|df
operator|.
name|format
argument_list|(
name|i
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
name|IndexReader
name|reader
init|=
name|writer
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|searcher
operator|=
operator|new
name|IndexSearcher
argument_list|(
name|reader
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|N
specifier|private
name|char
name|N
parameter_list|()
block|{
return|return
call|(
name|char
call|)
argument_list|(
literal|0x30
operator|+
name|random
operator|.
name|nextInt
argument_list|(
literal|10
argument_list|)
argument_list|)
return|;
block|}
DECL|method|fillPattern
specifier|private
name|String
name|fillPattern
parameter_list|(
name|String
name|wildcardPattern
parameter_list|)
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
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
name|wildcardPattern
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
switch|switch
condition|(
name|wildcardPattern
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
condition|)
block|{
case|case
literal|'N'
case|:
name|sb
operator|.
name|append
argument_list|(
name|N
argument_list|()
argument_list|)
expr_stmt|;
break|break;
default|default:
name|sb
operator|.
name|append
argument_list|(
name|wildcardPattern
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|assertPatternHits
specifier|private
name|void
name|assertPatternHits
parameter_list|(
name|String
name|pattern
parameter_list|,
name|int
name|numHits
parameter_list|)
throws|throws
name|Exception
block|{
comment|// TODO: run with different rewrites
name|Query
name|wq
init|=
operator|new
name|WildcardQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
name|fillPattern
argument_list|(
name|pattern
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|TopDocs
name|docs
init|=
name|searcher
operator|.
name|search
argument_list|(
name|wq
argument_list|,
literal|25
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Incorrect hits for pattern: "
operator|+
name|pattern
argument_list|,
name|numHits
argument_list|,
name|docs
operator|.
name|totalHits
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|tearDown
specifier|protected
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|searcher
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
DECL|method|testWildcards
specifier|public
name|void
name|testWildcards
parameter_list|()
throws|throws
name|Exception
block|{
empty_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|100
operator|*
name|_TestUtil
operator|.
name|getRandomMultiplier
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|assertPatternHits
argument_list|(
literal|"NNNN"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertPatternHits
argument_list|(
literal|"?NNN"
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|assertPatternHits
argument_list|(
literal|"N?NN"
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|assertPatternHits
argument_list|(
literal|"NN?N"
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|assertPatternHits
argument_list|(
literal|"NNN?"
argument_list|,
literal|10
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|10
operator|*
name|_TestUtil
operator|.
name|getRandomMultiplier
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|assertPatternHits
argument_list|(
literal|"??NN"
argument_list|,
literal|100
argument_list|)
expr_stmt|;
name|assertPatternHits
argument_list|(
literal|"N??N"
argument_list|,
literal|100
argument_list|)
expr_stmt|;
name|assertPatternHits
argument_list|(
literal|"NN??"
argument_list|,
literal|100
argument_list|)
expr_stmt|;
name|assertPatternHits
argument_list|(
literal|"???N"
argument_list|,
literal|1000
argument_list|)
expr_stmt|;
name|assertPatternHits
argument_list|(
literal|"N???"
argument_list|,
literal|1000
argument_list|)
expr_stmt|;
name|assertPatternHits
argument_list|(
literal|"????"
argument_list|,
literal|10000
argument_list|)
expr_stmt|;
name|assertPatternHits
argument_list|(
literal|"NNN*"
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|assertPatternHits
argument_list|(
literal|"NN*"
argument_list|,
literal|100
argument_list|)
expr_stmt|;
name|assertPatternHits
argument_list|(
literal|"N*"
argument_list|,
literal|1000
argument_list|)
expr_stmt|;
name|assertPatternHits
argument_list|(
literal|"*"
argument_list|,
literal|10000
argument_list|)
expr_stmt|;
name|assertPatternHits
argument_list|(
literal|"*NNN"
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|assertPatternHits
argument_list|(
literal|"*NN"
argument_list|,
literal|100
argument_list|)
expr_stmt|;
name|assertPatternHits
argument_list|(
literal|"*N"
argument_list|,
literal|1000
argument_list|)
expr_stmt|;
name|assertPatternHits
argument_list|(
literal|"N*NN"
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|assertPatternHits
argument_list|(
literal|"NN*N"
argument_list|,
literal|10
argument_list|)
expr_stmt|;
comment|// combo of ? and * operators
name|assertPatternHits
argument_list|(
literal|"?NN*"
argument_list|,
literal|100
argument_list|)
expr_stmt|;
name|assertPatternHits
argument_list|(
literal|"N?N*"
argument_list|,
literal|100
argument_list|)
expr_stmt|;
name|assertPatternHits
argument_list|(
literal|"NN?*"
argument_list|,
literal|100
argument_list|)
expr_stmt|;
name|assertPatternHits
argument_list|(
literal|"?N?*"
argument_list|,
literal|1000
argument_list|)
expr_stmt|;
name|assertPatternHits
argument_list|(
literal|"N??*"
argument_list|,
literal|1000
argument_list|)
expr_stmt|;
name|assertPatternHits
argument_list|(
literal|"*NN?"
argument_list|,
literal|100
argument_list|)
expr_stmt|;
name|assertPatternHits
argument_list|(
literal|"*N??"
argument_list|,
literal|1000
argument_list|)
expr_stmt|;
name|assertPatternHits
argument_list|(
literal|"*???"
argument_list|,
literal|10000
argument_list|)
expr_stmt|;
name|assertPatternHits
argument_list|(
literal|"*?N?"
argument_list|,
literal|1000
argument_list|)
expr_stmt|;
name|assertPatternHits
argument_list|(
literal|"*??N"
argument_list|,
literal|1000
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
