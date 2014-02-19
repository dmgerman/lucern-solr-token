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
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|Arrays
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
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
name|SortedSetDocValuesField
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
name|StringField
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
name|AtomicReader
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
name|SortedSetDocValues
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
name|TermContext
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
operator|.
name|BooleanWeight
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
name|similarities
operator|.
name|DefaultSimilarity
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
name|similarities
operator|.
name|Similarity
operator|.
name|SimScorer
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
name|similarities
operator|.
name|Similarity
operator|.
name|SimWeight
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
name|BytesRef
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
operator|.
name|SuppressCodecs
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|AfterClass
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|BeforeClass
import|;
end_import
begin_comment
comment|/** tests BooleanScorer2's minShouldMatch */
end_comment
begin_class
annotation|@
name|SuppressCodecs
argument_list|(
block|{
literal|"Lucene40"
block|,
literal|"Lucene41"
block|}
argument_list|)
DECL|class|TestMinShouldMatch2
specifier|public
class|class
name|TestMinShouldMatch2
extends|extends
name|LuceneTestCase
block|{
DECL|field|dir
specifier|static
name|Directory
name|dir
decl_stmt|;
DECL|field|r
specifier|static
name|DirectoryReader
name|r
decl_stmt|;
DECL|field|reader
specifier|static
name|AtomicReader
name|reader
decl_stmt|;
DECL|field|searcher
specifier|static
name|IndexSearcher
name|searcher
decl_stmt|;
DECL|field|alwaysTerms
specifier|static
specifier|final
name|String
name|alwaysTerms
index|[]
init|=
block|{
literal|"a"
block|}
decl_stmt|;
DECL|field|commonTerms
specifier|static
specifier|final
name|String
name|commonTerms
index|[]
init|=
block|{
literal|"b"
block|,
literal|"c"
block|,
literal|"d"
block|}
decl_stmt|;
DECL|field|mediumTerms
specifier|static
specifier|final
name|String
name|mediumTerms
index|[]
init|=
block|{
literal|"e"
block|,
literal|"f"
block|,
literal|"g"
block|}
decl_stmt|;
DECL|field|rareTerms
specifier|static
specifier|final
name|String
name|rareTerms
index|[]
init|=
block|{
literal|"h"
block|,
literal|"i"
block|,
literal|"j"
block|,
literal|"k"
block|,
literal|"l"
block|,
literal|"m"
block|,
literal|"n"
block|,
literal|"o"
block|,
literal|"p"
block|,
literal|"q"
block|,
literal|"r"
block|,
literal|"s"
block|,
literal|"t"
block|,
literal|"u"
block|,
literal|"v"
block|,
literal|"w"
block|,
literal|"x"
block|,
literal|"y"
block|,
literal|"z"
block|}
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|beforeClass
specifier|public
specifier|static
name|void
name|beforeClass
parameter_list|()
throws|throws
name|Exception
block|{
name|dir
operator|=
name|newDirectory
argument_list|()
expr_stmt|;
name|RandomIndexWriter
name|iw
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
argument_list|,
name|dir
argument_list|)
decl_stmt|;
specifier|final
name|int
name|numDocs
init|=
name|atLeast
argument_list|(
literal|300
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
name|numDocs
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
name|addSome
argument_list|(
name|doc
argument_list|,
name|alwaysTerms
argument_list|)
expr_stmt|;
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|100
argument_list|)
operator|<
literal|90
condition|)
block|{
name|addSome
argument_list|(
name|doc
argument_list|,
name|commonTerms
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|100
argument_list|)
operator|<
literal|50
condition|)
block|{
name|addSome
argument_list|(
name|doc
argument_list|,
name|mediumTerms
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|100
argument_list|)
operator|<
literal|10
condition|)
block|{
name|addSome
argument_list|(
name|doc
argument_list|,
name|rareTerms
argument_list|)
expr_stmt|;
block|}
name|iw
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
name|iw
operator|.
name|forceMerge
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|iw
operator|.
name|close
argument_list|()
expr_stmt|;
name|r
operator|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
expr_stmt|;
name|reader
operator|=
name|getOnlySegmentReader
argument_list|(
name|r
argument_list|)
expr_stmt|;
name|searcher
operator|=
operator|new
name|IndexSearcher
argument_list|(
name|reader
argument_list|)
expr_stmt|;
name|searcher
operator|.
name|setSimilarity
argument_list|(
operator|new
name|DefaultSimilarity
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|float
name|queryNorm
parameter_list|(
name|float
name|sumOfSquaredWeights
parameter_list|)
block|{
return|return
literal|1
return|;
comment|// we disable queryNorm, both for debugging and ease of impl
block|}
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|afterClass
specifier|public
specifier|static
name|void
name|afterClass
parameter_list|()
throws|throws
name|Exception
block|{
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
name|searcher
operator|=
literal|null
expr_stmt|;
name|reader
operator|=
literal|null
expr_stmt|;
name|r
operator|=
literal|null
expr_stmt|;
name|dir
operator|=
literal|null
expr_stmt|;
block|}
DECL|method|addSome
specifier|private
specifier|static
name|void
name|addSome
parameter_list|(
name|Document
name|doc
parameter_list|,
name|String
name|values
index|[]
parameter_list|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|list
init|=
name|Arrays
operator|.
name|asList
argument_list|(
name|values
argument_list|)
decl_stmt|;
name|Collections
operator|.
name|shuffle
argument_list|(
name|list
argument_list|,
name|random
argument_list|()
argument_list|)
expr_stmt|;
name|int
name|howMany
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|1
argument_list|,
name|list
operator|.
name|size
argument_list|()
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
name|howMany
condition|;
name|i
operator|++
control|)
block|{
name|doc
operator|.
name|add
argument_list|(
operator|new
name|StringField
argument_list|(
literal|"field"
argument_list|,
name|list
operator|.
name|get
argument_list|(
name|i
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
name|SortedSetDocValuesField
argument_list|(
literal|"dv"
argument_list|,
operator|new
name|BytesRef
argument_list|(
name|list
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|scorer
specifier|private
name|Scorer
name|scorer
parameter_list|(
name|String
name|values
index|[]
parameter_list|,
name|int
name|minShouldMatch
parameter_list|,
name|boolean
name|slow
parameter_list|)
throws|throws
name|Exception
block|{
name|BooleanQuery
name|bq
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|value
range|:
name|values
control|)
block|{
name|bq
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
name|value
argument_list|)
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
block|}
name|bq
operator|.
name|setMinimumNumberShouldMatch
argument_list|(
name|minShouldMatch
argument_list|)
expr_stmt|;
name|BooleanWeight
name|weight
init|=
operator|(
name|BooleanWeight
operator|)
name|searcher
operator|.
name|createNormalizedWeight
argument_list|(
name|bq
argument_list|)
decl_stmt|;
if|if
condition|(
name|slow
condition|)
block|{
return|return
operator|new
name|SlowMinShouldMatchScorer
argument_list|(
name|weight
argument_list|,
name|reader
argument_list|,
name|searcher
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|weight
operator|.
name|scorer
argument_list|(
name|reader
operator|.
name|getContext
argument_list|()
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|)
return|;
block|}
block|}
DECL|method|assertNext
specifier|private
name|void
name|assertNext
parameter_list|(
name|Scorer
name|expected
parameter_list|,
name|Scorer
name|actual
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|actual
operator|==
literal|null
condition|)
block|{
name|assertEquals
argument_list|(
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
argument_list|,
name|expected
operator|.
name|nextDoc
argument_list|()
argument_list|)
expr_stmt|;
return|return;
block|}
name|int
name|doc
decl_stmt|;
while|while
condition|(
operator|(
name|doc
operator|=
name|expected
operator|.
name|nextDoc
argument_list|()
operator|)
operator|!=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
condition|)
block|{
name|assertEquals
argument_list|(
name|doc
argument_list|,
name|actual
operator|.
name|nextDoc
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expected
operator|.
name|freq
argument_list|()
argument_list|,
name|actual
operator|.
name|freq
argument_list|()
argument_list|)
expr_stmt|;
name|float
name|expectedScore
init|=
name|expected
operator|.
name|score
argument_list|()
decl_stmt|;
name|float
name|actualScore
init|=
name|actual
operator|.
name|score
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|expectedScore
argument_list|,
name|actualScore
argument_list|,
name|CheckHits
operator|.
name|explainToleranceDelta
argument_list|(
name|expectedScore
argument_list|,
name|actualScore
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
argument_list|,
name|actual
operator|.
name|nextDoc
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|assertAdvance
specifier|private
name|void
name|assertAdvance
parameter_list|(
name|Scorer
name|expected
parameter_list|,
name|Scorer
name|actual
parameter_list|,
name|int
name|amount
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|actual
operator|==
literal|null
condition|)
block|{
name|assertEquals
argument_list|(
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
argument_list|,
name|expected
operator|.
name|nextDoc
argument_list|()
argument_list|)
expr_stmt|;
return|return;
block|}
name|int
name|prevDoc
init|=
literal|0
decl_stmt|;
name|int
name|doc
decl_stmt|;
while|while
condition|(
operator|(
name|doc
operator|=
name|expected
operator|.
name|advance
argument_list|(
name|prevDoc
operator|+
name|amount
argument_list|)
operator|)
operator|!=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
condition|)
block|{
name|assertEquals
argument_list|(
name|doc
argument_list|,
name|actual
operator|.
name|advance
argument_list|(
name|prevDoc
operator|+
name|amount
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expected
operator|.
name|freq
argument_list|()
argument_list|,
name|actual
operator|.
name|freq
argument_list|()
argument_list|)
expr_stmt|;
name|float
name|expectedScore
init|=
name|expected
operator|.
name|score
argument_list|()
decl_stmt|;
name|float
name|actualScore
init|=
name|actual
operator|.
name|score
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|expectedScore
argument_list|,
name|actualScore
argument_list|,
name|CheckHits
operator|.
name|explainToleranceDelta
argument_list|(
name|expectedScore
argument_list|,
name|actualScore
argument_list|)
argument_list|)
expr_stmt|;
name|prevDoc
operator|=
name|doc
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
argument_list|,
name|actual
operator|.
name|advance
argument_list|(
name|prevDoc
operator|+
name|amount
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** simple test for next(): minShouldMatch=2 on 3 terms (one common, one medium, one rare) */
DECL|method|testNextCMR2
specifier|public
name|void
name|testNextCMR2
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|int
name|common
init|=
literal|0
init|;
name|common
operator|<
name|commonTerms
operator|.
name|length
condition|;
name|common
operator|++
control|)
block|{
for|for
control|(
name|int
name|medium
init|=
literal|0
init|;
name|medium
operator|<
name|mediumTerms
operator|.
name|length
condition|;
name|medium
operator|++
control|)
block|{
for|for
control|(
name|int
name|rare
init|=
literal|0
init|;
name|rare
operator|<
name|rareTerms
operator|.
name|length
condition|;
name|rare
operator|++
control|)
block|{
name|Scorer
name|expected
init|=
name|scorer
argument_list|(
operator|new
name|String
index|[]
block|{
name|commonTerms
index|[
name|common
index|]
block|,
name|mediumTerms
index|[
name|medium
index|]
block|,
name|rareTerms
index|[
name|rare
index|]
block|}
argument_list|,
literal|2
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|Scorer
name|actual
init|=
name|scorer
argument_list|(
operator|new
name|String
index|[]
block|{
name|commonTerms
index|[
name|common
index|]
block|,
name|mediumTerms
index|[
name|medium
index|]
block|,
name|rareTerms
index|[
name|rare
index|]
block|}
argument_list|,
literal|2
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|assertNext
argument_list|(
name|expected
argument_list|,
name|actual
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
comment|/** simple test for advance(): minShouldMatch=2 on 3 terms (one common, one medium, one rare) */
DECL|method|testAdvanceCMR2
specifier|public
name|void
name|testAdvanceCMR2
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|int
name|amount
init|=
literal|25
init|;
name|amount
operator|<
literal|200
condition|;
name|amount
operator|+=
literal|25
control|)
block|{
for|for
control|(
name|int
name|common
init|=
literal|0
init|;
name|common
operator|<
name|commonTerms
operator|.
name|length
condition|;
name|common
operator|++
control|)
block|{
for|for
control|(
name|int
name|medium
init|=
literal|0
init|;
name|medium
operator|<
name|mediumTerms
operator|.
name|length
condition|;
name|medium
operator|++
control|)
block|{
for|for
control|(
name|int
name|rare
init|=
literal|0
init|;
name|rare
operator|<
name|rareTerms
operator|.
name|length
condition|;
name|rare
operator|++
control|)
block|{
name|Scorer
name|expected
init|=
name|scorer
argument_list|(
operator|new
name|String
index|[]
block|{
name|commonTerms
index|[
name|common
index|]
block|,
name|mediumTerms
index|[
name|medium
index|]
block|,
name|rareTerms
index|[
name|rare
index|]
block|}
argument_list|,
literal|2
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|Scorer
name|actual
init|=
name|scorer
argument_list|(
operator|new
name|String
index|[]
block|{
name|commonTerms
index|[
name|common
index|]
block|,
name|mediumTerms
index|[
name|medium
index|]
block|,
name|rareTerms
index|[
name|rare
index|]
block|}
argument_list|,
literal|2
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|assertAdvance
argument_list|(
name|expected
argument_list|,
name|actual
argument_list|,
name|amount
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
comment|/** test next with giant bq of all terms with varying minShouldMatch */
DECL|method|testNextAllTerms
specifier|public
name|void
name|testNextAllTerms
parameter_list|()
throws|throws
name|Exception
block|{
name|List
argument_list|<
name|String
argument_list|>
name|termsList
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|termsList
operator|.
name|addAll
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|commonTerms
argument_list|)
argument_list|)
expr_stmt|;
name|termsList
operator|.
name|addAll
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|mediumTerms
argument_list|)
argument_list|)
expr_stmt|;
name|termsList
operator|.
name|addAll
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|rareTerms
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|terms
index|[]
init|=
name|termsList
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|minNrShouldMatch
init|=
literal|1
init|;
name|minNrShouldMatch
operator|<=
name|terms
operator|.
name|length
condition|;
name|minNrShouldMatch
operator|++
control|)
block|{
name|Scorer
name|expected
init|=
name|scorer
argument_list|(
name|terms
argument_list|,
name|minNrShouldMatch
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|Scorer
name|actual
init|=
name|scorer
argument_list|(
name|terms
argument_list|,
name|minNrShouldMatch
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|assertNext
argument_list|(
name|expected
argument_list|,
name|actual
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** test advance with giant bq of all terms with varying minShouldMatch */
DECL|method|testAdvanceAllTerms
specifier|public
name|void
name|testAdvanceAllTerms
parameter_list|()
throws|throws
name|Exception
block|{
name|List
argument_list|<
name|String
argument_list|>
name|termsList
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|termsList
operator|.
name|addAll
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|commonTerms
argument_list|)
argument_list|)
expr_stmt|;
name|termsList
operator|.
name|addAll
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|mediumTerms
argument_list|)
argument_list|)
expr_stmt|;
name|termsList
operator|.
name|addAll
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|rareTerms
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|terms
index|[]
init|=
name|termsList
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|amount
init|=
literal|25
init|;
name|amount
operator|<
literal|200
condition|;
name|amount
operator|+=
literal|25
control|)
block|{
for|for
control|(
name|int
name|minNrShouldMatch
init|=
literal|1
init|;
name|minNrShouldMatch
operator|<=
name|terms
operator|.
name|length
condition|;
name|minNrShouldMatch
operator|++
control|)
block|{
name|Scorer
name|expected
init|=
name|scorer
argument_list|(
name|terms
argument_list|,
name|minNrShouldMatch
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|Scorer
name|actual
init|=
name|scorer
argument_list|(
name|terms
argument_list|,
name|minNrShouldMatch
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|assertAdvance
argument_list|(
name|expected
argument_list|,
name|actual
argument_list|,
name|amount
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/** test next with varying numbers of terms with varying minShouldMatch */
DECL|method|testNextVaryingNumberOfTerms
specifier|public
name|void
name|testNextVaryingNumberOfTerms
parameter_list|()
throws|throws
name|Exception
block|{
name|List
argument_list|<
name|String
argument_list|>
name|termsList
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|termsList
operator|.
name|addAll
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|commonTerms
argument_list|)
argument_list|)
expr_stmt|;
name|termsList
operator|.
name|addAll
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|mediumTerms
argument_list|)
argument_list|)
expr_stmt|;
name|termsList
operator|.
name|addAll
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|rareTerms
argument_list|)
argument_list|)
expr_stmt|;
name|Collections
operator|.
name|shuffle
argument_list|(
name|termsList
argument_list|,
name|random
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|numTerms
init|=
literal|2
init|;
name|numTerms
operator|<=
name|termsList
operator|.
name|size
argument_list|()
condition|;
name|numTerms
operator|++
control|)
block|{
name|String
name|terms
index|[]
init|=
name|termsList
operator|.
name|subList
argument_list|(
literal|0
argument_list|,
name|numTerms
argument_list|)
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|minNrShouldMatch
init|=
literal|1
init|;
name|minNrShouldMatch
operator|<=
name|terms
operator|.
name|length
condition|;
name|minNrShouldMatch
operator|++
control|)
block|{
name|Scorer
name|expected
init|=
name|scorer
argument_list|(
name|terms
argument_list|,
name|minNrShouldMatch
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|Scorer
name|actual
init|=
name|scorer
argument_list|(
name|terms
argument_list|,
name|minNrShouldMatch
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|assertNext
argument_list|(
name|expected
argument_list|,
name|actual
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/** test advance with varying numbers of terms with varying minShouldMatch */
DECL|method|testAdvanceVaryingNumberOfTerms
specifier|public
name|void
name|testAdvanceVaryingNumberOfTerms
parameter_list|()
throws|throws
name|Exception
block|{
name|List
argument_list|<
name|String
argument_list|>
name|termsList
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|termsList
operator|.
name|addAll
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|commonTerms
argument_list|)
argument_list|)
expr_stmt|;
name|termsList
operator|.
name|addAll
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|mediumTerms
argument_list|)
argument_list|)
expr_stmt|;
name|termsList
operator|.
name|addAll
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|rareTerms
argument_list|)
argument_list|)
expr_stmt|;
name|Collections
operator|.
name|shuffle
argument_list|(
name|termsList
argument_list|,
name|random
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|amount
init|=
literal|25
init|;
name|amount
operator|<
literal|200
condition|;
name|amount
operator|+=
literal|25
control|)
block|{
for|for
control|(
name|int
name|numTerms
init|=
literal|2
init|;
name|numTerms
operator|<=
name|termsList
operator|.
name|size
argument_list|()
condition|;
name|numTerms
operator|++
control|)
block|{
name|String
name|terms
index|[]
init|=
name|termsList
operator|.
name|subList
argument_list|(
literal|0
argument_list|,
name|numTerms
argument_list|)
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|minNrShouldMatch
init|=
literal|1
init|;
name|minNrShouldMatch
operator|<=
name|terms
operator|.
name|length
condition|;
name|minNrShouldMatch
operator|++
control|)
block|{
name|Scorer
name|expected
init|=
name|scorer
argument_list|(
name|terms
argument_list|,
name|minNrShouldMatch
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|Scorer
name|actual
init|=
name|scorer
argument_list|(
name|terms
argument_list|,
name|minNrShouldMatch
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|assertAdvance
argument_list|(
name|expected
argument_list|,
name|actual
argument_list|,
name|amount
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
comment|// TODO: more tests
comment|// a slow min-should match scorer that uses a docvalues field.
comment|// later, we can make debugging easier as it can record the set of ords it currently matched
comment|// and e.g. print out their values and so on for the document
DECL|class|SlowMinShouldMatchScorer
specifier|static
class|class
name|SlowMinShouldMatchScorer
extends|extends
name|Scorer
block|{
DECL|field|currentDoc
name|int
name|currentDoc
init|=
operator|-
literal|1
decl_stmt|;
comment|// current docid
DECL|field|currentMatched
name|int
name|currentMatched
init|=
operator|-
literal|1
decl_stmt|;
comment|// current number of terms matched
DECL|field|dv
specifier|final
name|SortedSetDocValues
name|dv
decl_stmt|;
DECL|field|maxDoc
specifier|final
name|int
name|maxDoc
decl_stmt|;
DECL|field|ords
specifier|final
name|Set
argument_list|<
name|Long
argument_list|>
name|ords
init|=
operator|new
name|HashSet
argument_list|<
name|Long
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|sims
specifier|final
name|SimScorer
index|[]
name|sims
decl_stmt|;
DECL|field|minNrShouldMatch
specifier|final
name|int
name|minNrShouldMatch
decl_stmt|;
DECL|field|score
name|double
name|score
init|=
name|Float
operator|.
name|NaN
decl_stmt|;
DECL|method|SlowMinShouldMatchScorer
name|SlowMinShouldMatchScorer
parameter_list|(
name|BooleanWeight
name|weight
parameter_list|,
name|AtomicReader
name|reader
parameter_list|,
name|IndexSearcher
name|searcher
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|weight
argument_list|)
expr_stmt|;
name|this
operator|.
name|dv
operator|=
name|reader
operator|.
name|getSortedSetDocValues
argument_list|(
literal|"dv"
argument_list|)
expr_stmt|;
name|this
operator|.
name|maxDoc
operator|=
name|reader
operator|.
name|maxDoc
argument_list|()
expr_stmt|;
name|BooleanQuery
name|bq
init|=
operator|(
name|BooleanQuery
operator|)
name|weight
operator|.
name|getQuery
argument_list|()
decl_stmt|;
name|this
operator|.
name|minNrShouldMatch
operator|=
name|bq
operator|.
name|getMinimumNumberShouldMatch
argument_list|()
expr_stmt|;
name|this
operator|.
name|sims
operator|=
operator|new
name|SimScorer
index|[
operator|(
name|int
operator|)
name|dv
operator|.
name|getValueCount
argument_list|()
index|]
expr_stmt|;
for|for
control|(
name|BooleanClause
name|clause
range|:
name|bq
operator|.
name|getClauses
argument_list|()
control|)
block|{
assert|assert
operator|!
name|clause
operator|.
name|isProhibited
argument_list|()
assert|;
assert|assert
operator|!
name|clause
operator|.
name|isRequired
argument_list|()
assert|;
name|Term
name|term
init|=
operator|(
operator|(
name|TermQuery
operator|)
name|clause
operator|.
name|getQuery
argument_list|()
operator|)
operator|.
name|getTerm
argument_list|()
decl_stmt|;
name|long
name|ord
init|=
name|dv
operator|.
name|lookupTerm
argument_list|(
name|term
operator|.
name|bytes
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|ord
operator|>=
literal|0
condition|)
block|{
name|boolean
name|success
init|=
name|ords
operator|.
name|add
argument_list|(
name|ord
argument_list|)
decl_stmt|;
assert|assert
name|success
assert|;
comment|// no dups
name|TermContext
name|context
init|=
name|TermContext
operator|.
name|build
argument_list|(
name|reader
operator|.
name|getContext
argument_list|()
argument_list|,
name|term
argument_list|)
decl_stmt|;
name|SimWeight
name|w
init|=
name|weight
operator|.
name|similarity
operator|.
name|computeWeight
argument_list|(
literal|1f
argument_list|,
name|searcher
operator|.
name|collectionStatistics
argument_list|(
literal|"field"
argument_list|)
argument_list|,
name|searcher
operator|.
name|termStatistics
argument_list|(
name|term
argument_list|,
name|context
argument_list|)
argument_list|)
decl_stmt|;
name|w
operator|.
name|getValueForNormalization
argument_list|()
expr_stmt|;
comment|// ignored
name|w
operator|.
name|normalize
argument_list|(
literal|1F
argument_list|,
literal|1F
argument_list|)
expr_stmt|;
name|sims
index|[
operator|(
name|int
operator|)
name|ord
index|]
operator|=
name|weight
operator|.
name|similarity
operator|.
name|simScorer
argument_list|(
name|w
argument_list|,
name|reader
operator|.
name|getContext
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|score
specifier|public
name|float
name|score
parameter_list|()
throws|throws
name|IOException
block|{
assert|assert
name|score
operator|!=
literal|0
operator|:
name|currentMatched
assert|;
return|return
operator|(
name|float
operator|)
name|score
operator|*
operator|(
operator|(
name|BooleanWeight
operator|)
name|weight
operator|)
operator|.
name|coord
argument_list|(
name|currentMatched
argument_list|,
operator|(
operator|(
name|BooleanWeight
operator|)
name|weight
operator|)
operator|.
name|maxCoord
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|freq
specifier|public
name|int
name|freq
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|currentMatched
return|;
block|}
annotation|@
name|Override
DECL|method|docID
specifier|public
name|int
name|docID
parameter_list|()
block|{
return|return
name|currentDoc
return|;
block|}
annotation|@
name|Override
DECL|method|nextDoc
specifier|public
name|int
name|nextDoc
parameter_list|()
throws|throws
name|IOException
block|{
assert|assert
name|currentDoc
operator|!=
name|NO_MORE_DOCS
assert|;
for|for
control|(
name|currentDoc
operator|=
name|currentDoc
operator|+
literal|1
init|;
name|currentDoc
operator|<
name|maxDoc
condition|;
name|currentDoc
operator|++
control|)
block|{
name|currentMatched
operator|=
literal|0
expr_stmt|;
name|score
operator|=
literal|0
expr_stmt|;
name|dv
operator|.
name|setDocument
argument_list|(
name|currentDoc
argument_list|)
expr_stmt|;
name|long
name|ord
decl_stmt|;
while|while
condition|(
operator|(
name|ord
operator|=
name|dv
operator|.
name|nextOrd
argument_list|()
operator|)
operator|!=
name|SortedSetDocValues
operator|.
name|NO_MORE_ORDS
condition|)
block|{
if|if
condition|(
name|ords
operator|.
name|contains
argument_list|(
name|ord
argument_list|)
condition|)
block|{
name|currentMatched
operator|++
expr_stmt|;
name|score
operator|+=
name|sims
index|[
operator|(
name|int
operator|)
name|ord
index|]
operator|.
name|score
argument_list|(
name|currentDoc
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|currentMatched
operator|>=
name|minNrShouldMatch
condition|)
block|{
return|return
name|currentDoc
return|;
block|}
block|}
return|return
name|currentDoc
operator|=
name|NO_MORE_DOCS
return|;
block|}
annotation|@
name|Override
DECL|method|advance
specifier|public
name|int
name|advance
parameter_list|(
name|int
name|target
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|doc
decl_stmt|;
while|while
condition|(
operator|(
name|doc
operator|=
name|nextDoc
argument_list|()
operator|)
operator|<
name|target
condition|)
block|{       }
return|return
name|doc
return|;
block|}
annotation|@
name|Override
DECL|method|cost
specifier|public
name|long
name|cost
parameter_list|()
block|{
return|return
name|maxDoc
return|;
block|}
block|}
block|}
end_class
end_unit
