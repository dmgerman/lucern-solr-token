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
name|index
operator|.
name|IndexReader
operator|.
name|AtomicReaderContext
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
name|BooleanClause
operator|.
name|Occur
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
name|Scorer
operator|.
name|ScorerVisitor
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
name|java
operator|.
name|util
operator|.
name|*
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
begin_class
DECL|class|TestSubScorerFreqs
specifier|public
class|class
name|TestSubScorerFreqs
extends|extends
name|LuceneTestCase
block|{
DECL|field|dir
specifier|private
specifier|static
name|Directory
name|dir
decl_stmt|;
DECL|field|s
specifier|private
specifier|static
name|IndexSearcher
name|s
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|makeIndex
specifier|public
specifier|static
name|void
name|makeIndex
parameter_list|()
throws|throws
name|Exception
block|{
name|dir
operator|=
operator|new
name|RAMDirectory
argument_list|()
expr_stmt|;
name|RandomIndexWriter
name|w
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|,
name|dir
argument_list|)
decl_stmt|;
comment|// make sure we have more than one segment occationally
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|31
operator|*
name|RANDOM_MULTIPLIER
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
name|newField
argument_list|(
literal|"f"
argument_list|,
literal|"a b c d b c d c d d"
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
name|w
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|doc
operator|=
operator|new
name|Document
argument_list|()
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|newField
argument_list|(
literal|"f"
argument_list|,
literal|"a b c d"
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
name|w
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
name|s
operator|=
operator|new
name|IndexSearcher
argument_list|(
name|w
operator|.
name|getReader
argument_list|()
argument_list|)
expr_stmt|;
name|w
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|finish
specifier|public
specifier|static
name|void
name|finish
parameter_list|()
throws|throws
name|Exception
block|{
name|s
operator|.
name|getIndexReader
argument_list|()
operator|.
name|close
argument_list|()
expr_stmt|;
name|s
operator|.
name|close
argument_list|()
expr_stmt|;
name|s
operator|=
literal|null
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir
operator|=
literal|null
expr_stmt|;
block|}
DECL|class|CountingCollector
specifier|private
specifier|static
class|class
name|CountingCollector
extends|extends
name|Collector
block|{
DECL|field|other
specifier|private
specifier|final
name|Collector
name|other
decl_stmt|;
DECL|field|docBase
specifier|private
name|int
name|docBase
decl_stmt|;
DECL|field|docCounts
specifier|public
specifier|final
name|Map
argument_list|<
name|Integer
argument_list|,
name|Map
argument_list|<
name|Query
argument_list|,
name|Float
argument_list|>
argument_list|>
name|docCounts
init|=
operator|new
name|HashMap
argument_list|<
name|Integer
argument_list|,
name|Map
argument_list|<
name|Query
argument_list|,
name|Float
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|subScorers
specifier|private
specifier|final
name|Map
argument_list|<
name|Query
argument_list|,
name|Scorer
argument_list|>
name|subScorers
init|=
operator|new
name|HashMap
argument_list|<
name|Query
argument_list|,
name|Scorer
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|visitor
specifier|private
specifier|final
name|ScorerVisitor
argument_list|<
name|Query
argument_list|,
name|Query
argument_list|,
name|Scorer
argument_list|>
name|visitor
init|=
operator|new
name|MockScorerVisitor
argument_list|()
decl_stmt|;
DECL|field|collect
specifier|private
specifier|final
name|EnumSet
argument_list|<
name|Occur
argument_list|>
name|collect
decl_stmt|;
DECL|class|MockScorerVisitor
specifier|private
class|class
name|MockScorerVisitor
extends|extends
name|ScorerVisitor
argument_list|<
name|Query
argument_list|,
name|Query
argument_list|,
name|Scorer
argument_list|>
block|{
annotation|@
name|Override
DECL|method|visitOptional
specifier|public
name|void
name|visitOptional
parameter_list|(
name|Query
name|parent
parameter_list|,
name|Query
name|child
parameter_list|,
name|Scorer
name|scorer
parameter_list|)
block|{
if|if
condition|(
name|collect
operator|.
name|contains
argument_list|(
name|Occur
operator|.
name|SHOULD
argument_list|)
condition|)
name|subScorers
operator|.
name|put
argument_list|(
name|child
argument_list|,
name|scorer
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|visitProhibited
specifier|public
name|void
name|visitProhibited
parameter_list|(
name|Query
name|parent
parameter_list|,
name|Query
name|child
parameter_list|,
name|Scorer
name|scorer
parameter_list|)
block|{
if|if
condition|(
name|collect
operator|.
name|contains
argument_list|(
name|Occur
operator|.
name|MUST_NOT
argument_list|)
condition|)
name|subScorers
operator|.
name|put
argument_list|(
name|child
argument_list|,
name|scorer
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|visitRequired
specifier|public
name|void
name|visitRequired
parameter_list|(
name|Query
name|parent
parameter_list|,
name|Query
name|child
parameter_list|,
name|Scorer
name|scorer
parameter_list|)
block|{
if|if
condition|(
name|collect
operator|.
name|contains
argument_list|(
name|Occur
operator|.
name|MUST
argument_list|)
condition|)
name|subScorers
operator|.
name|put
argument_list|(
name|child
argument_list|,
name|scorer
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|CountingCollector
specifier|public
name|CountingCollector
parameter_list|(
name|Collector
name|other
parameter_list|)
block|{
name|this
argument_list|(
name|other
argument_list|,
name|EnumSet
operator|.
name|allOf
argument_list|(
name|Occur
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|CountingCollector
specifier|public
name|CountingCollector
parameter_list|(
name|Collector
name|other
parameter_list|,
name|EnumSet
argument_list|<
name|Occur
argument_list|>
name|collect
parameter_list|)
block|{
name|this
operator|.
name|other
operator|=
name|other
expr_stmt|;
name|this
operator|.
name|collect
operator|=
name|collect
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setScorer
specifier|public
name|void
name|setScorer
parameter_list|(
name|Scorer
name|scorer
parameter_list|)
throws|throws
name|IOException
block|{
name|other
operator|.
name|setScorer
argument_list|(
name|scorer
argument_list|)
expr_stmt|;
name|scorer
operator|.
name|visitScorers
argument_list|(
name|visitor
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|collect
specifier|public
name|void
name|collect
parameter_list|(
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|Map
argument_list|<
name|Query
argument_list|,
name|Float
argument_list|>
name|freqs
init|=
operator|new
name|HashMap
argument_list|<
name|Query
argument_list|,
name|Float
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|Query
argument_list|,
name|Scorer
argument_list|>
name|ent
range|:
name|subScorers
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|Scorer
name|value
init|=
name|ent
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|int
name|matchId
init|=
name|value
operator|.
name|docID
argument_list|()
decl_stmt|;
name|freqs
operator|.
name|put
argument_list|(
name|ent
operator|.
name|getKey
argument_list|()
argument_list|,
name|matchId
operator|==
name|doc
condition|?
name|value
operator|.
name|freq
argument_list|()
else|:
literal|0.0f
argument_list|)
expr_stmt|;
block|}
name|docCounts
operator|.
name|put
argument_list|(
name|doc
operator|+
name|docBase
argument_list|,
name|freqs
argument_list|)
expr_stmt|;
name|other
operator|.
name|collect
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setNextReader
specifier|public
name|void
name|setNextReader
parameter_list|(
name|AtomicReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|docBase
operator|=
name|docBase
expr_stmt|;
name|other
operator|.
name|setNextReader
argument_list|(
name|context
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|acceptsDocsOutOfOrder
specifier|public
name|boolean
name|acceptsDocsOutOfOrder
parameter_list|()
block|{
return|return
name|other
operator|.
name|acceptsDocsOutOfOrder
argument_list|()
return|;
block|}
block|}
DECL|field|FLOAT_TOLERANCE
specifier|private
specifier|static
specifier|final
name|float
name|FLOAT_TOLERANCE
init|=
literal|0.00001F
decl_stmt|;
annotation|@
name|Test
DECL|method|testTermQuery
specifier|public
name|void
name|testTermQuery
parameter_list|()
throws|throws
name|Exception
block|{
name|TermQuery
name|q
init|=
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"f"
argument_list|,
literal|"d"
argument_list|)
argument_list|)
decl_stmt|;
name|CountingCollector
name|c
init|=
operator|new
name|CountingCollector
argument_list|(
name|TopScoreDocCollector
operator|.
name|create
argument_list|(
literal|10
argument_list|,
literal|true
argument_list|)
argument_list|)
decl_stmt|;
name|s
operator|.
name|search
argument_list|(
name|q
argument_list|,
literal|null
argument_list|,
name|c
argument_list|)
expr_stmt|;
specifier|final
name|int
name|maxDocs
init|=
name|s
operator|.
name|maxDoc
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|maxDocs
argument_list|,
name|c
operator|.
name|docCounts
operator|.
name|size
argument_list|()
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
name|maxDocs
condition|;
name|i
operator|++
control|)
block|{
name|Map
argument_list|<
name|Query
argument_list|,
name|Float
argument_list|>
name|doc0
init|=
name|c
operator|.
name|docCounts
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|doc0
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4.0F
argument_list|,
name|doc0
operator|.
name|get
argument_list|(
name|q
argument_list|)
argument_list|,
name|FLOAT_TOLERANCE
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|Query
argument_list|,
name|Float
argument_list|>
name|doc1
init|=
name|c
operator|.
name|docCounts
operator|.
name|get
argument_list|(
operator|++
name|i
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|doc1
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1.0F
argument_list|,
name|doc1
operator|.
name|get
argument_list|(
name|q
argument_list|)
argument_list|,
name|FLOAT_TOLERANCE
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
annotation|@
name|Test
DECL|method|testBooleanQuery
specifier|public
name|void
name|testBooleanQuery
parameter_list|()
throws|throws
name|Exception
block|{
name|TermQuery
name|aQuery
init|=
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"f"
argument_list|,
literal|"a"
argument_list|)
argument_list|)
decl_stmt|;
name|TermQuery
name|dQuery
init|=
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"f"
argument_list|,
literal|"d"
argument_list|)
argument_list|)
decl_stmt|;
name|TermQuery
name|cQuery
init|=
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"f"
argument_list|,
literal|"c"
argument_list|)
argument_list|)
decl_stmt|;
name|TermQuery
name|yQuery
init|=
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"f"
argument_list|,
literal|"y"
argument_list|)
argument_list|)
decl_stmt|;
name|BooleanQuery
name|query
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
name|BooleanQuery
name|inner
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
name|inner
operator|.
name|add
argument_list|(
name|cQuery
argument_list|,
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|inner
operator|.
name|add
argument_list|(
name|yQuery
argument_list|,
name|Occur
operator|.
name|MUST_NOT
argument_list|)
expr_stmt|;
name|query
operator|.
name|add
argument_list|(
name|inner
argument_list|,
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
name|query
operator|.
name|add
argument_list|(
name|aQuery
argument_list|,
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
name|query
operator|.
name|add
argument_list|(
name|dQuery
argument_list|,
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
name|EnumSet
argument_list|<
name|Occur
argument_list|>
index|[]
name|occurList
init|=
operator|new
name|EnumSet
index|[]
block|{
name|EnumSet
operator|.
name|of
argument_list|(
name|Occur
operator|.
name|MUST
argument_list|)
block|,
name|EnumSet
operator|.
name|of
argument_list|(
name|Occur
operator|.
name|MUST
argument_list|,
name|Occur
operator|.
name|SHOULD
argument_list|)
block|}
decl_stmt|;
for|for
control|(
name|EnumSet
argument_list|<
name|Occur
argument_list|>
name|occur
range|:
name|occurList
control|)
block|{
name|CountingCollector
name|c
init|=
operator|new
name|CountingCollector
argument_list|(
name|TopScoreDocCollector
operator|.
name|create
argument_list|(
literal|10
argument_list|,
literal|true
argument_list|)
argument_list|,
name|occur
argument_list|)
decl_stmt|;
name|s
operator|.
name|search
argument_list|(
name|query
argument_list|,
literal|null
argument_list|,
name|c
argument_list|)
expr_stmt|;
specifier|final
name|int
name|maxDocs
init|=
name|s
operator|.
name|maxDoc
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|maxDocs
argument_list|,
name|c
operator|.
name|docCounts
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|boolean
name|includeOptional
init|=
name|occur
operator|.
name|contains
argument_list|(
name|Occur
operator|.
name|SHOULD
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
name|maxDocs
condition|;
name|i
operator|++
control|)
block|{
name|Map
argument_list|<
name|Query
argument_list|,
name|Float
argument_list|>
name|doc0
init|=
name|c
operator|.
name|docCounts
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|includeOptional
condition|?
literal|5
else|:
literal|4
argument_list|,
name|doc0
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1.0F
argument_list|,
name|doc0
operator|.
name|get
argument_list|(
name|aQuery
argument_list|)
argument_list|,
name|FLOAT_TOLERANCE
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4.0F
argument_list|,
name|doc0
operator|.
name|get
argument_list|(
name|dQuery
argument_list|)
argument_list|,
name|FLOAT_TOLERANCE
argument_list|)
expr_stmt|;
if|if
condition|(
name|includeOptional
condition|)
name|assertEquals
argument_list|(
literal|3.0F
argument_list|,
name|doc0
operator|.
name|get
argument_list|(
name|cQuery
argument_list|)
argument_list|,
name|FLOAT_TOLERANCE
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|Query
argument_list|,
name|Float
argument_list|>
name|doc1
init|=
name|c
operator|.
name|docCounts
operator|.
name|get
argument_list|(
operator|++
name|i
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|includeOptional
condition|?
literal|5
else|:
literal|4
argument_list|,
name|doc1
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1.0F
argument_list|,
name|doc1
operator|.
name|get
argument_list|(
name|aQuery
argument_list|)
argument_list|,
name|FLOAT_TOLERANCE
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1.0F
argument_list|,
name|doc1
operator|.
name|get
argument_list|(
name|dQuery
argument_list|)
argument_list|,
name|FLOAT_TOLERANCE
argument_list|)
expr_stmt|;
if|if
condition|(
name|includeOptional
condition|)
name|assertEquals
argument_list|(
literal|1.0F
argument_list|,
name|doc1
operator|.
name|get
argument_list|(
name|cQuery
argument_list|)
argument_list|,
name|FLOAT_TOLERANCE
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
DECL|method|testPhraseQuery
specifier|public
name|void
name|testPhraseQuery
parameter_list|()
throws|throws
name|Exception
block|{
name|PhraseQuery
name|q
init|=
operator|new
name|PhraseQuery
argument_list|()
decl_stmt|;
name|q
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
literal|"f"
argument_list|,
literal|"b"
argument_list|)
argument_list|)
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
literal|"f"
argument_list|,
literal|"c"
argument_list|)
argument_list|)
expr_stmt|;
name|CountingCollector
name|c
init|=
operator|new
name|CountingCollector
argument_list|(
name|TopScoreDocCollector
operator|.
name|create
argument_list|(
literal|10
argument_list|,
literal|true
argument_list|)
argument_list|)
decl_stmt|;
name|s
operator|.
name|search
argument_list|(
name|q
argument_list|,
literal|null
argument_list|,
name|c
argument_list|)
expr_stmt|;
specifier|final
name|int
name|maxDocs
init|=
name|s
operator|.
name|maxDoc
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|maxDocs
argument_list|,
name|c
operator|.
name|docCounts
operator|.
name|size
argument_list|()
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
name|maxDocs
condition|;
name|i
operator|++
control|)
block|{
name|Map
argument_list|<
name|Query
argument_list|,
name|Float
argument_list|>
name|doc0
init|=
name|c
operator|.
name|docCounts
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|doc0
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2.0F
argument_list|,
name|doc0
operator|.
name|get
argument_list|(
name|q
argument_list|)
argument_list|,
name|FLOAT_TOLERANCE
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|Query
argument_list|,
name|Float
argument_list|>
name|doc1
init|=
name|c
operator|.
name|docCounts
operator|.
name|get
argument_list|(
operator|++
name|i
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|doc1
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1.0F
argument_list|,
name|doc1
operator|.
name|get
argument_list|(
name|q
argument_list|)
argument_list|,
name|FLOAT_TOLERANCE
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
