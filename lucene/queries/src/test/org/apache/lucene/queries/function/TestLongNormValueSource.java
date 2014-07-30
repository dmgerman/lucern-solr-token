begin_unit
begin_package
DECL|package|org.apache.lucene.queries.function
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queries
operator|.
name|function
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|TextField
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
name|FieldInvertState
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
name|queries
operator|.
name|function
operator|.
name|valuesource
operator|.
name|NormValueSource
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
name|CheckHits
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
name|ScoreDoc
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
name|Sort
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
name|SortField
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
name|TopDocs
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
name|TFIDFSimilarity
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
DECL|class|TestLongNormValueSource
specifier|public
class|class
name|TestLongNormValueSource
extends|extends
name|LuceneTestCase
block|{
DECL|field|dir
specifier|static
name|Directory
name|dir
decl_stmt|;
DECL|field|reader
specifier|static
name|IndexReader
name|reader
decl_stmt|;
DECL|field|searcher
specifier|static
name|IndexSearcher
name|searcher
decl_stmt|;
DECL|field|sim
specifier|private
specifier|static
name|Similarity
name|sim
init|=
operator|new
name|PreciseDefaultSimilarity
argument_list|()
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
name|IndexWriterConfig
name|iwConfig
init|=
name|newIndexWriterConfig
argument_list|(
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|iwConfig
operator|.
name|setMergePolicy
argument_list|(
name|newLogMergePolicy
argument_list|()
argument_list|)
expr_stmt|;
name|iwConfig
operator|.
name|setSimilarity
argument_list|(
name|sim
argument_list|)
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
argument_list|,
name|iwConfig
argument_list|)
decl_stmt|;
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
name|TextField
argument_list|(
literal|"text"
argument_list|,
literal|"this is a test test test"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|iw
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
operator|new
name|TextField
argument_list|(
literal|"text"
argument_list|,
literal|"second test"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|iw
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|reader
operator|=
name|iw
operator|.
name|getReader
argument_list|()
expr_stmt|;
name|searcher
operator|=
name|newSearcher
argument_list|(
name|reader
argument_list|)
expr_stmt|;
name|iw
operator|.
name|shutdown
argument_list|()
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
name|searcher
operator|=
literal|null
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|reader
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
DECL|method|testNorm
specifier|public
name|void
name|testNorm
parameter_list|()
throws|throws
name|Exception
block|{
name|Similarity
name|saved
init|=
name|searcher
operator|.
name|getSimilarity
argument_list|()
decl_stmt|;
try|try
block|{
comment|// no norm field (so agnostic to indexed similarity)
name|searcher
operator|.
name|setSimilarity
argument_list|(
name|sim
argument_list|)
expr_stmt|;
name|assertHits
argument_list|(
operator|new
name|FunctionQuery
argument_list|(
operator|new
name|NormValueSource
argument_list|(
literal|"text"
argument_list|)
argument_list|)
argument_list|,
operator|new
name|float
index|[]
block|{
literal|0f
block|,
literal|0f
block|}
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|searcher
operator|.
name|setSimilarity
argument_list|(
name|saved
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|assertHits
name|void
name|assertHits
parameter_list|(
name|Query
name|q
parameter_list|,
name|float
name|scores
index|[]
parameter_list|)
throws|throws
name|Exception
block|{
name|ScoreDoc
name|expected
index|[]
init|=
operator|new
name|ScoreDoc
index|[
name|scores
operator|.
name|length
index|]
decl_stmt|;
name|int
name|expectedDocs
index|[]
init|=
operator|new
name|int
index|[
name|scores
operator|.
name|length
index|]
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
name|expected
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|expectedDocs
index|[
name|i
index|]
operator|=
name|i
expr_stmt|;
name|expected
index|[
name|i
index|]
operator|=
operator|new
name|ScoreDoc
argument_list|(
name|i
argument_list|,
name|scores
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|TopDocs
name|docs
init|=
name|searcher
operator|.
name|search
argument_list|(
name|q
argument_list|,
literal|2
argument_list|,
operator|new
name|Sort
argument_list|(
operator|new
name|SortField
argument_list|(
literal|"id"
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|STRING
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
comment|/*     for (int i=0;i<docs.scoreDocs.length;i++) {       System.out.println(searcher.explain(q, docs.scoreDocs[i].doc));     }     */
name|CheckHits
operator|.
name|checkHits
argument_list|(
name|random
argument_list|()
argument_list|,
name|q
argument_list|,
literal|""
argument_list|,
name|searcher
argument_list|,
name|expectedDocs
argument_list|)
expr_stmt|;
name|CheckHits
operator|.
name|checkHitsQuery
argument_list|(
name|q
argument_list|,
name|expected
argument_list|,
name|docs
operator|.
name|scoreDocs
argument_list|,
name|expectedDocs
argument_list|)
expr_stmt|;
name|CheckHits
operator|.
name|checkExplanations
argument_list|(
name|q
argument_list|,
literal|""
argument_list|,
name|searcher
argument_list|)
expr_stmt|;
block|}
block|}
end_class
begin_comment
comment|/** Encodes norm as 4-byte float. */
end_comment
begin_class
DECL|class|PreciseDefaultSimilarity
class|class
name|PreciseDefaultSimilarity
extends|extends
name|TFIDFSimilarity
block|{
comment|/** Sole constructor: parameter-free */
DECL|method|PreciseDefaultSimilarity
specifier|public
name|PreciseDefaultSimilarity
parameter_list|()
block|{}
comment|/** Implemented as<code>overlap / maxOverlap</code>. */
annotation|@
name|Override
DECL|method|coord
specifier|public
name|float
name|coord
parameter_list|(
name|int
name|overlap
parameter_list|,
name|int
name|maxOverlap
parameter_list|)
block|{
return|return
name|overlap
operator|/
operator|(
name|float
operator|)
name|maxOverlap
return|;
block|}
comment|/** Implemented as<code>1/sqrt(sumOfSquaredWeights)</code>. */
annotation|@
name|Override
DECL|method|queryNorm
specifier|public
name|float
name|queryNorm
parameter_list|(
name|float
name|sumOfSquaredWeights
parameter_list|)
block|{
return|return
call|(
name|float
call|)
argument_list|(
literal|1.0
operator|/
name|Math
operator|.
name|sqrt
argument_list|(
name|sumOfSquaredWeights
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Encodes a normalization factor for storage in an index.    *<p>    * The encoding uses a three-bit mantissa, a five-bit exponent, and the    * zero-exponent point at 15, thus representing values from around 7x10^9 to    * 2x10^-9 with about one significant decimal digit of accuracy. Zero is also    * represented. Negative numbers are rounded up to zero. Values too large to    * represent are rounded down to the largest representable value. Positive    * values too small to represent are rounded up to the smallest positive    * representable value.    *    * @see org.apache.lucene.document.Field#setBoost(float)    * @see org.apache.lucene.util.SmallFloat    */
annotation|@
name|Override
DECL|method|encodeNormValue
specifier|public
specifier|final
name|long
name|encodeNormValue
parameter_list|(
name|float
name|f
parameter_list|)
block|{
return|return
name|Float
operator|.
name|floatToIntBits
argument_list|(
name|f
argument_list|)
return|;
block|}
comment|/**    * Decodes the norm value, assuming it is a single byte.    *    * @see #encodeNormValue(float)    */
annotation|@
name|Override
DECL|method|decodeNormValue
specifier|public
specifier|final
name|float
name|decodeNormValue
parameter_list|(
name|long
name|norm
parameter_list|)
block|{
return|return
name|Float
operator|.
name|intBitsToFloat
argument_list|(
operator|(
name|int
operator|)
name|norm
argument_list|)
return|;
block|}
comment|/** Implemented as    *<code>state.getBoost()*lengthNorm(numTerms)</code>, where    *<code>numTerms</code> is {@link org.apache.lucene.index.FieldInvertState#getLength()} if {@link    *  #setDiscountOverlaps} is false, else it's {@link    *  org.apache.lucene.index.FieldInvertState#getLength()} - {@link    *  org.apache.lucene.index.FieldInvertState#getNumOverlap()}.    *    *  @lucene.experimental */
annotation|@
name|Override
DECL|method|lengthNorm
specifier|public
name|float
name|lengthNorm
parameter_list|(
name|FieldInvertState
name|state
parameter_list|)
block|{
specifier|final
name|int
name|numTerms
decl_stmt|;
if|if
condition|(
name|discountOverlaps
condition|)
block|{
name|numTerms
operator|=
name|state
operator|.
name|getLength
argument_list|()
operator|-
name|state
operator|.
name|getNumOverlap
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|numTerms
operator|=
name|state
operator|.
name|getLength
argument_list|()
expr_stmt|;
block|}
return|return
name|state
operator|.
name|getBoost
argument_list|()
operator|*
operator|(
call|(
name|float
call|)
argument_list|(
literal|1.0
operator|/
name|Math
operator|.
name|sqrt
argument_list|(
name|numTerms
argument_list|)
argument_list|)
operator|)
return|;
block|}
comment|/** Implemented as<code>sqrt(freq)</code>. */
annotation|@
name|Override
DECL|method|tf
specifier|public
name|float
name|tf
parameter_list|(
name|float
name|freq
parameter_list|)
block|{
return|return
operator|(
name|float
operator|)
name|Math
operator|.
name|sqrt
argument_list|(
name|freq
argument_list|)
return|;
block|}
comment|/** Implemented as<code>1 / (distance + 1)</code>. */
annotation|@
name|Override
DECL|method|sloppyFreq
specifier|public
name|float
name|sloppyFreq
parameter_list|(
name|int
name|distance
parameter_list|)
block|{
return|return
literal|1.0f
operator|/
operator|(
name|distance
operator|+
literal|1
operator|)
return|;
block|}
comment|/** The default implementation returns<code>1</code> */
annotation|@
name|Override
DECL|method|scorePayload
specifier|public
name|float
name|scorePayload
parameter_list|(
name|int
name|doc
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|end
parameter_list|,
name|BytesRef
name|payload
parameter_list|)
block|{
return|return
literal|1
return|;
block|}
comment|/** Implemented as<code>log(numDocs/(docFreq+1)) + 1</code>. */
annotation|@
name|Override
DECL|method|idf
specifier|public
name|float
name|idf
parameter_list|(
name|long
name|docFreq
parameter_list|,
name|long
name|numDocs
parameter_list|)
block|{
return|return
call|(
name|float
call|)
argument_list|(
name|Math
operator|.
name|log
argument_list|(
name|numDocs
operator|/
call|(
name|double
call|)
argument_list|(
name|docFreq
operator|+
literal|1
argument_list|)
argument_list|)
operator|+
literal|1.0
argument_list|)
return|;
block|}
comment|/**    * True if overlap tokens (tokens with a position of increment of zero) are    * discounted from the document's length.    */
DECL|field|discountOverlaps
specifier|protected
name|boolean
name|discountOverlaps
init|=
literal|true
decl_stmt|;
comment|/** Determines whether overlap tokens (Tokens with    *  0 position increment) are ignored when computing    *  norm.  By default this is true, meaning overlap    *  tokens do not count when computing norms.    *    *  @lucene.experimental    *    *  @see #computeNorm    */
DECL|method|setDiscountOverlaps
specifier|public
name|void
name|setDiscountOverlaps
parameter_list|(
name|boolean
name|v
parameter_list|)
block|{
name|discountOverlaps
operator|=
name|v
expr_stmt|;
block|}
comment|/**    * Returns true if overlap tokens are discounted from the document's length.    * @see #setDiscountOverlaps    */
DECL|method|getDiscountOverlaps
specifier|public
name|boolean
name|getDiscountOverlaps
parameter_list|()
block|{
return|return
name|discountOverlaps
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"DefaultSimilarity"
return|;
block|}
block|}
end_class
end_unit
