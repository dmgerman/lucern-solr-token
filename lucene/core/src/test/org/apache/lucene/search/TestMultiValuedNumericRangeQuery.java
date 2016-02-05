begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|MockAnalyzer
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
name|document
operator|.
name|LegacyIntField
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
name|TestUtil
import|;
end_import
begin_class
DECL|class|TestMultiValuedNumericRangeQuery
specifier|public
class|class
name|TestMultiValuedNumericRangeQuery
extends|extends
name|LuceneTestCase
block|{
comment|/** Tests LegacyNumericRangeQuery on a multi-valued field (multiple numeric values per document).    * This test ensures, that a classical TermRangeQuery returns exactly the same document numbers as    * LegacyNumericRangeQuery (see SOLR-1322 for discussion) and the multiple precision terms per numeric value    * do not interfere with multiple numeric values.    */
DECL|method|testMultiValuedNRQ
specifier|public
name|void
name|testMultiValuedNRQ
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|directory
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|RandomIndexWriter
name|writer
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
argument_list|,
name|directory
argument_list|,
name|newIndexWriterConfig
argument_list|(
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
operator|.
name|setMaxBufferedDocs
argument_list|(
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|50
argument_list|,
literal|1000
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|DecimalFormat
name|format
init|=
operator|new
name|DecimalFormat
argument_list|(
literal|"00000000000"
argument_list|,
operator|new
name|DecimalFormatSymbols
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
argument_list|)
decl_stmt|;
name|int
name|num
init|=
name|atLeast
argument_list|(
literal|500
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|l
init|=
literal|0
init|;
name|l
operator|<
name|num
condition|;
name|l
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
for|for
control|(
name|int
name|m
init|=
literal|0
init|,
name|c
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|10
argument_list|)
init|;
name|m
operator|<=
name|c
condition|;
name|m
operator|++
control|)
block|{
name|int
name|value
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|newStringField
argument_list|(
literal|"asc"
argument_list|,
name|format
operator|.
name|format
argument_list|(
name|value
argument_list|)
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|LegacyIntField
argument_list|(
literal|"trie"
argument_list|,
name|value
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
block|}
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
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|IndexSearcher
name|searcher
init|=
name|newSearcher
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|num
operator|=
name|atLeast
argument_list|(
literal|50
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
name|num
condition|;
name|i
operator|++
control|)
block|{
name|int
name|lower
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
decl_stmt|;
name|int
name|upper
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
decl_stmt|;
if|if
condition|(
name|lower
operator|>
name|upper
condition|)
block|{
name|int
name|a
init|=
name|lower
decl_stmt|;
name|lower
operator|=
name|upper
expr_stmt|;
name|upper
operator|=
name|a
expr_stmt|;
block|}
name|TermRangeQuery
name|cq
init|=
name|TermRangeQuery
operator|.
name|newStringRange
argument_list|(
literal|"asc"
argument_list|,
name|format
operator|.
name|format
argument_list|(
name|lower
argument_list|)
argument_list|,
name|format
operator|.
name|format
argument_list|(
name|upper
argument_list|)
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|LegacyNumericRangeQuery
argument_list|<
name|Integer
argument_list|>
name|tq
init|=
name|LegacyNumericRangeQuery
operator|.
name|newIntRange
argument_list|(
literal|"trie"
argument_list|,
name|lower
argument_list|,
name|upper
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|TopDocs
name|trTopDocs
init|=
name|searcher
operator|.
name|search
argument_list|(
name|cq
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|TopDocs
name|nrTopDocs
init|=
name|searcher
operator|.
name|search
argument_list|(
name|tq
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Returned count for LegacyNumericRangeQuery and TermRangeQuery must be equal"
argument_list|,
name|trTopDocs
operator|.
name|totalHits
argument_list|,
name|nrTopDocs
operator|.
name|totalHits
argument_list|)
expr_stmt|;
block|}
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
