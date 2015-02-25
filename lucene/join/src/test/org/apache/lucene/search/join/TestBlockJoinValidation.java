begin_unit
begin_package
DECL|package|org.apache.lucene.search.join
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|join
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|List
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
name|index
operator|.
name|DirectoryReader
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
name|IndexWriterConfig
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
name|search
operator|.
name|BooleanClause
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|CachingWrapperFilter
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
name|Filter
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
name|FilteredQuery
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
name|QueryWrapperFilter
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
name|TermQuery
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
name|WildcardQuery
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
name|junit
operator|.
name|After
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Rule
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|rules
operator|.
name|ExpectedException
import|;
end_import
begin_class
DECL|class|TestBlockJoinValidation
specifier|public
class|class
name|TestBlockJoinValidation
extends|extends
name|LuceneTestCase
block|{
DECL|field|AMOUNT_OF_SEGMENTS
specifier|public
specifier|static
specifier|final
name|int
name|AMOUNT_OF_SEGMENTS
init|=
literal|5
decl_stmt|;
DECL|field|AMOUNT_OF_PARENT_DOCS
specifier|public
specifier|static
specifier|final
name|int
name|AMOUNT_OF_PARENT_DOCS
init|=
literal|10
decl_stmt|;
DECL|field|AMOUNT_OF_CHILD_DOCS
specifier|public
specifier|static
specifier|final
name|int
name|AMOUNT_OF_CHILD_DOCS
init|=
literal|5
decl_stmt|;
DECL|field|AMOUNT_OF_DOCS_IN_SEGMENT
specifier|public
specifier|static
specifier|final
name|int
name|AMOUNT_OF_DOCS_IN_SEGMENT
init|=
name|AMOUNT_OF_PARENT_DOCS
operator|+
name|AMOUNT_OF_PARENT_DOCS
operator|*
name|AMOUNT_OF_CHILD_DOCS
decl_stmt|;
DECL|field|directory
specifier|private
name|Directory
name|directory
decl_stmt|;
DECL|field|indexReader
specifier|private
name|IndexReader
name|indexReader
decl_stmt|;
DECL|field|indexSearcher
specifier|private
name|IndexSearcher
name|indexSearcher
decl_stmt|;
DECL|field|parentsFilter
specifier|private
name|BitDocIdSetFilter
name|parentsFilter
decl_stmt|;
annotation|@
name|Rule
DECL|field|thrown
specifier|public
name|ExpectedException
name|thrown
init|=
name|ExpectedException
operator|.
name|none
argument_list|()
decl_stmt|;
annotation|@
name|Before
DECL|method|before
specifier|public
name|void
name|before
parameter_list|()
throws|throws
name|Exception
block|{
name|directory
operator|=
name|newDirectory
argument_list|()
expr_stmt|;
specifier|final
name|IndexWriterConfig
name|config
init|=
operator|new
name|IndexWriterConfig
argument_list|(
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|IndexWriter
name|indexWriter
init|=
operator|new
name|IndexWriter
argument_list|(
name|directory
argument_list|,
name|config
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
name|AMOUNT_OF_SEGMENTS
condition|;
name|i
operator|++
control|)
block|{
name|List
argument_list|<
name|Document
argument_list|>
name|segmentDocs
init|=
name|createDocsForSegment
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|indexWriter
operator|.
name|addDocuments
argument_list|(
name|segmentDocs
argument_list|)
expr_stmt|;
name|indexWriter
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
name|indexReader
operator|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|indexWriter
argument_list|,
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
argument_list|)
expr_stmt|;
name|indexWriter
operator|.
name|close
argument_list|()
expr_stmt|;
name|indexSearcher
operator|=
operator|new
name|IndexSearcher
argument_list|(
name|indexReader
argument_list|)
expr_stmt|;
name|parentsFilter
operator|=
operator|new
name|BitDocIdSetCachingWrapperFilter
argument_list|(
operator|new
name|QueryWrapperFilter
argument_list|(
operator|new
name|WildcardQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"parent"
argument_list|,
literal|"*"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testNextDocValidationForToParentBjq
specifier|public
name|void
name|testNextDocValidationForToParentBjq
parameter_list|()
throws|throws
name|Exception
block|{
name|Query
name|parentQueryWithRandomChild
init|=
name|createChildrenQueryWithOneParent
argument_list|(
name|getRandomChildNumber
argument_list|(
literal|0
argument_list|)
argument_list|)
decl_stmt|;
name|ToParentBlockJoinQuery
name|blockJoinQuery
init|=
operator|new
name|ToParentBlockJoinQuery
argument_list|(
name|parentQueryWithRandomChild
argument_list|,
name|parentsFilter
argument_list|,
name|ScoreMode
operator|.
name|None
argument_list|)
decl_stmt|;
name|thrown
operator|.
name|expect
argument_list|(
name|IllegalStateException
operator|.
name|class
argument_list|)
expr_stmt|;
name|thrown
operator|.
name|expectMessage
argument_list|(
literal|"child query must only match non-parent docs"
argument_list|)
expr_stmt|;
name|indexSearcher
operator|.
name|search
argument_list|(
name|blockJoinQuery
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testAdvanceValidationForToParentBjq
specifier|public
name|void
name|testAdvanceValidationForToParentBjq
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|randomChildNumber
init|=
name|getRandomChildNumber
argument_list|(
literal|0
argument_list|)
decl_stmt|;
comment|// we need to make advance method meet wrong document, so random child number
comment|// in BJQ must be greater than child number in Boolean clause
name|int
name|nextRandomChildNumber
init|=
name|getRandomChildNumber
argument_list|(
name|randomChildNumber
argument_list|)
decl_stmt|;
name|Query
name|parentQueryWithRandomChild
init|=
name|createChildrenQueryWithOneParent
argument_list|(
name|nextRandomChildNumber
argument_list|)
decl_stmt|;
name|ToParentBlockJoinQuery
name|blockJoinQuery
init|=
operator|new
name|ToParentBlockJoinQuery
argument_list|(
name|parentQueryWithRandomChild
argument_list|,
name|parentsFilter
argument_list|,
name|ScoreMode
operator|.
name|None
argument_list|)
decl_stmt|;
comment|// advance() method is used by ConjunctionScorer, so we need to create Boolean conjunction query
name|BooleanQuery
name|conjunctionQuery
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
name|WildcardQuery
name|childQuery
init|=
operator|new
name|WildcardQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"child"
argument_list|,
name|createFieldValue
argument_list|(
name|randomChildNumber
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|conjunctionQuery
operator|.
name|add
argument_list|(
operator|new
name|BooleanClause
argument_list|(
name|childQuery
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST
argument_list|)
argument_list|)
expr_stmt|;
name|conjunctionQuery
operator|.
name|add
argument_list|(
operator|new
name|BooleanClause
argument_list|(
name|blockJoinQuery
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST
argument_list|)
argument_list|)
expr_stmt|;
name|thrown
operator|.
name|expect
argument_list|(
name|IllegalStateException
operator|.
name|class
argument_list|)
expr_stmt|;
name|thrown
operator|.
name|expectMessage
argument_list|(
literal|"child query must only match non-parent docs"
argument_list|)
expr_stmt|;
name|indexSearcher
operator|.
name|search
argument_list|(
name|conjunctionQuery
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testNextDocValidationForToChildBjq
specifier|public
name|void
name|testNextDocValidationForToChildBjq
parameter_list|()
throws|throws
name|Exception
block|{
name|Query
name|parentQueryWithRandomChild
init|=
name|createParentsQueryWithOneChild
argument_list|(
name|getRandomChildNumber
argument_list|(
literal|0
argument_list|)
argument_list|)
decl_stmt|;
name|ToChildBlockJoinQuery
name|blockJoinQuery
init|=
operator|new
name|ToChildBlockJoinQuery
argument_list|(
name|parentQueryWithRandomChild
argument_list|,
name|parentsFilter
argument_list|)
decl_stmt|;
name|thrown
operator|.
name|expect
argument_list|(
name|IllegalStateException
operator|.
name|class
argument_list|)
expr_stmt|;
name|thrown
operator|.
name|expectMessage
argument_list|(
name|ToChildBlockJoinQuery
operator|.
name|INVALID_QUERY_MESSAGE
argument_list|)
expr_stmt|;
name|indexSearcher
operator|.
name|search
argument_list|(
name|blockJoinQuery
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testValidationForToChildBjqWithChildFilterQuery
specifier|public
name|void
name|testValidationForToChildBjqWithChildFilterQuery
parameter_list|()
throws|throws
name|Exception
block|{
name|Query
name|parentQueryWithRandomChild
init|=
name|createParentQuery
argument_list|()
decl_stmt|;
name|ToChildBlockJoinQuery
name|blockJoinQuery
init|=
operator|new
name|ToChildBlockJoinQuery
argument_list|(
name|parentQueryWithRandomChild
argument_list|,
name|parentsFilter
argument_list|)
decl_stmt|;
name|Filter
name|childFilter
init|=
operator|new
name|CachingWrapperFilter
argument_list|(
operator|new
name|QueryWrapperFilter
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"common_field"
argument_list|,
literal|"1"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|thrown
operator|.
name|expect
argument_list|(
name|IllegalStateException
operator|.
name|class
argument_list|)
expr_stmt|;
name|thrown
operator|.
name|expectMessage
argument_list|(
name|ToChildBlockJoinQuery
operator|.
name|ILLEGAL_ADVANCE_ON_PARENT
argument_list|)
expr_stmt|;
name|indexSearcher
operator|.
name|search
argument_list|(
operator|new
name|FilteredQuery
argument_list|(
name|blockJoinQuery
argument_list|,
name|childFilter
argument_list|)
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testAdvanceValidationForToChildBjq
specifier|public
name|void
name|testAdvanceValidationForToChildBjq
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|randomChildNumber
init|=
name|getRandomChildNumber
argument_list|(
literal|0
argument_list|)
decl_stmt|;
comment|// we need to make advance method meet wrong document, so random child number
comment|// in BJQ must be greater than child number in Boolean clause
name|int
name|nextRandomChildNumber
init|=
name|getRandomChildNumber
argument_list|(
name|randomChildNumber
argument_list|)
decl_stmt|;
name|Query
name|parentQueryWithRandomChild
init|=
name|createParentsQueryWithOneChild
argument_list|(
name|nextRandomChildNumber
argument_list|)
decl_stmt|;
name|ToChildBlockJoinQuery
name|blockJoinQuery
init|=
operator|new
name|ToChildBlockJoinQuery
argument_list|(
name|parentQueryWithRandomChild
argument_list|,
name|parentsFilter
argument_list|)
decl_stmt|;
comment|// advance() method is used by ConjunctionScorer, so we need to create Boolean conjunction query
name|BooleanQuery
name|conjunctionQuery
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
name|WildcardQuery
name|childQuery
init|=
operator|new
name|WildcardQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"child"
argument_list|,
name|createFieldValue
argument_list|(
name|randomChildNumber
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|conjunctionQuery
operator|.
name|add
argument_list|(
operator|new
name|BooleanClause
argument_list|(
name|childQuery
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST
argument_list|)
argument_list|)
expr_stmt|;
name|conjunctionQuery
operator|.
name|add
argument_list|(
operator|new
name|BooleanClause
argument_list|(
name|blockJoinQuery
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST
argument_list|)
argument_list|)
expr_stmt|;
name|thrown
operator|.
name|expect
argument_list|(
name|IllegalStateException
operator|.
name|class
argument_list|)
expr_stmt|;
name|thrown
operator|.
name|expectMessage
argument_list|(
name|ToChildBlockJoinQuery
operator|.
name|INVALID_QUERY_MESSAGE
argument_list|)
expr_stmt|;
name|indexSearcher
operator|.
name|search
argument_list|(
name|conjunctionQuery
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|after
specifier|public
name|void
name|after
parameter_list|()
throws|throws
name|Exception
block|{
name|indexReader
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
DECL|method|createDocsForSegment
specifier|private
specifier|static
name|List
argument_list|<
name|Document
argument_list|>
name|createDocsForSegment
parameter_list|(
name|int
name|segmentNumber
parameter_list|)
block|{
name|List
argument_list|<
name|List
argument_list|<
name|Document
argument_list|>
argument_list|>
name|blocks
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|AMOUNT_OF_PARENT_DOCS
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
name|AMOUNT_OF_PARENT_DOCS
condition|;
name|i
operator|++
control|)
block|{
name|blocks
operator|.
name|add
argument_list|(
name|createParentDocWithChildren
argument_list|(
name|segmentNumber
argument_list|,
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|List
argument_list|<
name|Document
argument_list|>
name|result
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|AMOUNT_OF_DOCS_IN_SEGMENT
argument_list|)
decl_stmt|;
for|for
control|(
name|List
argument_list|<
name|Document
argument_list|>
name|block
range|:
name|blocks
control|)
block|{
name|result
operator|.
name|addAll
argument_list|(
name|block
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
DECL|method|createParentDocWithChildren
specifier|private
specifier|static
name|List
argument_list|<
name|Document
argument_list|>
name|createParentDocWithChildren
parameter_list|(
name|int
name|segmentNumber
parameter_list|,
name|int
name|parentNumber
parameter_list|)
block|{
name|List
argument_list|<
name|Document
argument_list|>
name|result
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|AMOUNT_OF_CHILD_DOCS
operator|+
literal|1
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
name|AMOUNT_OF_CHILD_DOCS
condition|;
name|i
operator|++
control|)
block|{
name|result
operator|.
name|add
argument_list|(
name|createChildDoc
argument_list|(
name|segmentNumber
argument_list|,
name|parentNumber
argument_list|,
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|result
operator|.
name|add
argument_list|(
name|createParentDoc
argument_list|(
name|segmentNumber
argument_list|,
name|parentNumber
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
DECL|method|createParentDoc
specifier|private
specifier|static
name|Document
name|createParentDoc
parameter_list|(
name|int
name|segmentNumber
parameter_list|,
name|int
name|parentNumber
parameter_list|)
block|{
name|Document
name|result
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|result
operator|.
name|add
argument_list|(
name|newStringField
argument_list|(
literal|"id"
argument_list|,
name|createFieldValue
argument_list|(
name|segmentNumber
operator|*
name|AMOUNT_OF_PARENT_DOCS
operator|+
name|parentNumber
argument_list|)
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|)
argument_list|)
expr_stmt|;
name|result
operator|.
name|add
argument_list|(
name|newStringField
argument_list|(
literal|"parent"
argument_list|,
name|createFieldValue
argument_list|(
name|parentNumber
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
name|result
operator|.
name|add
argument_list|(
name|newStringField
argument_list|(
literal|"common_field"
argument_list|,
literal|"1"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
DECL|method|createChildDoc
specifier|private
specifier|static
name|Document
name|createChildDoc
parameter_list|(
name|int
name|segmentNumber
parameter_list|,
name|int
name|parentNumber
parameter_list|,
name|int
name|childNumber
parameter_list|)
block|{
name|Document
name|result
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|result
operator|.
name|add
argument_list|(
name|newStringField
argument_list|(
literal|"id"
argument_list|,
name|createFieldValue
argument_list|(
name|segmentNumber
operator|*
name|AMOUNT_OF_PARENT_DOCS
operator|+
name|parentNumber
argument_list|,
name|childNumber
argument_list|)
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|)
argument_list|)
expr_stmt|;
name|result
operator|.
name|add
argument_list|(
name|newStringField
argument_list|(
literal|"child"
argument_list|,
name|createFieldValue
argument_list|(
name|childNumber
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
name|result
operator|.
name|add
argument_list|(
name|newStringField
argument_list|(
literal|"common_field"
argument_list|,
literal|"1"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
DECL|method|createFieldValue
specifier|private
specifier|static
name|String
name|createFieldValue
parameter_list|(
name|int
modifier|...
name|documentNumbers
parameter_list|)
block|{
name|StringBuilder
name|stringBuilder
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|documentNumber
range|:
name|documentNumbers
control|)
block|{
if|if
condition|(
name|stringBuilder
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|stringBuilder
operator|.
name|append
argument_list|(
literal|"_"
argument_list|)
expr_stmt|;
block|}
name|stringBuilder
operator|.
name|append
argument_list|(
name|documentNumber
argument_list|)
expr_stmt|;
block|}
return|return
name|stringBuilder
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|createChildrenQueryWithOneParent
specifier|private
specifier|static
name|Query
name|createChildrenQueryWithOneParent
parameter_list|(
name|int
name|childNumber
parameter_list|)
block|{
name|TermQuery
name|childQuery
init|=
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"child"
argument_list|,
name|createFieldValue
argument_list|(
name|childNumber
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|Query
name|randomParentQuery
init|=
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"id"
argument_list|,
name|createFieldValue
argument_list|(
name|getRandomParentId
argument_list|()
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|BooleanQuery
name|childrenQueryWithRandomParent
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
name|childrenQueryWithRandomParent
operator|.
name|add
argument_list|(
operator|new
name|BooleanClause
argument_list|(
name|childQuery
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
argument_list|)
expr_stmt|;
name|childrenQueryWithRandomParent
operator|.
name|add
argument_list|(
operator|new
name|BooleanClause
argument_list|(
name|randomParentQuery
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|childrenQueryWithRandomParent
return|;
block|}
DECL|method|createParentsQueryWithOneChild
specifier|private
specifier|static
name|Query
name|createParentsQueryWithOneChild
parameter_list|(
name|int
name|randomChildNumber
parameter_list|)
block|{
name|BooleanQuery
name|childQueryWithRandomParent
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
name|Query
name|parentsQuery
init|=
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"parent"
argument_list|,
name|createFieldValue
argument_list|(
name|getRandomParentNumber
argument_list|()
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|childQueryWithRandomParent
operator|.
name|add
argument_list|(
operator|new
name|BooleanClause
argument_list|(
name|parentsQuery
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
argument_list|)
expr_stmt|;
name|childQueryWithRandomParent
operator|.
name|add
argument_list|(
operator|new
name|BooleanClause
argument_list|(
name|randomChildQuery
argument_list|(
name|randomChildNumber
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|childQueryWithRandomParent
return|;
block|}
DECL|method|createParentQuery
specifier|private
specifier|static
name|Query
name|createParentQuery
parameter_list|()
block|{
return|return
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"id"
argument_list|,
name|createFieldValue
argument_list|(
name|getRandomParentId
argument_list|()
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
DECL|method|getRandomParentId
specifier|private
specifier|static
name|int
name|getRandomParentId
parameter_list|()
block|{
return|return
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|AMOUNT_OF_PARENT_DOCS
operator|*
name|AMOUNT_OF_SEGMENTS
argument_list|)
return|;
block|}
DECL|method|getRandomParentNumber
specifier|private
specifier|static
name|int
name|getRandomParentNumber
parameter_list|()
block|{
return|return
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|AMOUNT_OF_PARENT_DOCS
argument_list|)
return|;
block|}
DECL|method|randomChildQuery
specifier|private
specifier|static
name|Query
name|randomChildQuery
parameter_list|(
name|int
name|randomChildNumber
parameter_list|)
block|{
return|return
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"id"
argument_list|,
name|createFieldValue
argument_list|(
name|getRandomParentId
argument_list|()
argument_list|,
name|randomChildNumber
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
DECL|method|getRandomChildNumber
specifier|private
specifier|static
name|int
name|getRandomChildNumber
parameter_list|(
name|int
name|notLessThan
parameter_list|)
block|{
return|return
name|notLessThan
operator|+
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|AMOUNT_OF_CHILD_DOCS
operator|-
name|notLessThan
argument_list|)
return|;
block|}
block|}
end_class
end_unit
