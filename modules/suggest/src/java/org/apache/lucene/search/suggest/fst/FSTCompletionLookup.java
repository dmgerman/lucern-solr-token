begin_unit
begin_package
DECL|package|org.apache.lucene.search.suggest.fst
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|suggest
operator|.
name|fst
package|;
end_package
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
name|search
operator|.
name|spell
operator|.
name|TermFreqIterator
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
name|suggest
operator|.
name|Lookup
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
name|suggest
operator|.
name|fst
operator|.
name|FSTCompletion
operator|.
name|Completion
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
name|suggest
operator|.
name|fst
operator|.
name|Sort
operator|.
name|SortInfo
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
name|suggest
operator|.
name|tst
operator|.
name|TSTLookup
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
name|ByteArrayDataInput
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
name|ByteArrayDataOutput
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
name|util
operator|.
name|fst
operator|.
name|FST
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
name|fst
operator|.
name|NoOutputs
import|;
end_import
begin_comment
comment|/**  * An adapter from {@link Lookup} API to {@link FSTCompletion}.  *   *<p>This adapter differs from {@link FSTCompletion} in that it attempts  * to discretize any "weights" as passed from in {@link TermFreqIterator#freq()}  * to match the number of buckets. For the rationale for bucketing, see  * {@link FSTCompletion}.  *   *<p><b>Note:</b>Discretization requires an additional sorting pass.  *   *<p>The range of weights for bucketing/ discretization is determined   * by sorting the input by weight and then dividing into  * equal ranges. Then, scores within each range are assigned to that bucket.   *   *<p>Note that this means that even large differences in weights may be lost   * during automaton construction, but the overall distinction between "classes"  * of weights will be preserved regardless of the distribution of weights.   *   *<p>For fine-grained control over which weights are assigned to which buckets,  * use {@link FSTCompletion} directly or {@link TSTLookup}, for example.  *   * @see FSTCompletion  */
end_comment
begin_class
DECL|class|FSTCompletionLookup
specifier|public
class|class
name|FSTCompletionLookup
extends|extends
name|Lookup
block|{
comment|/**    * Shared tail length for conflating in the created automaton. Setting this    * to larger values ({@link Integer#MAX_VALUE}) will create smaller (or minimal)     * automata at the cost of RAM for keeping nodes hash in the {@link FST}.     *      *<p>Empirical pick.    */
DECL|field|sharedTailLength
specifier|private
specifier|final
specifier|static
name|int
name|sharedTailLength
init|=
literal|5
decl_stmt|;
comment|/**    * File name for the automaton.    *     * @see #store(File)    * @see #load(File)    */
DECL|field|FILENAME
specifier|private
specifier|static
specifier|final
name|String
name|FILENAME
init|=
literal|"fst.bin"
decl_stmt|;
DECL|field|buckets
specifier|private
name|int
name|buckets
decl_stmt|;
DECL|field|exactMatchFirst
specifier|private
name|boolean
name|exactMatchFirst
decl_stmt|;
comment|/**    * Automaton used for completions with higher weights reordering.    */
DECL|field|higherWeightsCompletion
specifier|private
name|FSTCompletion
name|higherWeightsCompletion
decl_stmt|;
comment|/**    * Automaton used for normal completions.    */
DECL|field|normalCompletion
specifier|private
name|FSTCompletion
name|normalCompletion
decl_stmt|;
comment|/*    *     */
DECL|method|FSTCompletionLookup
specifier|public
name|FSTCompletionLookup
parameter_list|()
block|{
name|this
argument_list|(
name|FSTCompletion
operator|.
name|DEFAULT_BUCKETS
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/*    *     */
DECL|method|FSTCompletionLookup
specifier|public
name|FSTCompletionLookup
parameter_list|(
name|FSTCompletion
name|completion
parameter_list|,
name|int
name|buckets
parameter_list|,
name|boolean
name|exactMatchFirst
parameter_list|)
block|{
name|this
argument_list|(
name|buckets
argument_list|,
name|exactMatchFirst
argument_list|)
expr_stmt|;
name|this
operator|.
name|normalCompletion
operator|=
operator|new
name|FSTCompletion
argument_list|(
name|completion
operator|.
name|getFST
argument_list|()
argument_list|,
literal|false
argument_list|,
name|exactMatchFirst
argument_list|)
expr_stmt|;
name|this
operator|.
name|higherWeightsCompletion
operator|=
operator|new
name|FSTCompletion
argument_list|(
name|completion
operator|.
name|getFST
argument_list|()
argument_list|,
literal|true
argument_list|,
name|exactMatchFirst
argument_list|)
expr_stmt|;
block|}
comment|/*    *     */
DECL|method|FSTCompletionLookup
specifier|public
name|FSTCompletionLookup
parameter_list|(
name|int
name|buckets
parameter_list|,
name|boolean
name|exactMatchFirst
parameter_list|)
block|{
name|this
operator|.
name|buckets
operator|=
name|buckets
expr_stmt|;
name|this
operator|.
name|exactMatchFirst
operator|=
name|exactMatchFirst
expr_stmt|;
block|}
comment|/*    *     */
annotation|@
name|Override
DECL|method|build
specifier|public
name|void
name|build
parameter_list|(
name|TermFreqIterator
name|tfit
parameter_list|)
throws|throws
name|IOException
block|{
name|File
name|tempInput
init|=
name|File
operator|.
name|createTempFile
argument_list|(
name|FSTCompletionLookup
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
argument_list|,
literal|".input"
argument_list|,
name|Sort
operator|.
name|defaultTempDir
argument_list|()
argument_list|)
decl_stmt|;
name|File
name|tempSorted
init|=
name|File
operator|.
name|createTempFile
argument_list|(
name|FSTCompletionLookup
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
argument_list|,
literal|".sorted"
argument_list|,
name|Sort
operator|.
name|defaultTempDir
argument_list|()
argument_list|)
decl_stmt|;
name|Sort
operator|.
name|ByteSequencesWriter
name|writer
init|=
operator|new
name|Sort
operator|.
name|ByteSequencesWriter
argument_list|(
name|tempInput
argument_list|)
decl_stmt|;
name|Sort
operator|.
name|ByteSequencesReader
name|reader
init|=
literal|null
decl_stmt|;
comment|// Push floats up front before sequences to sort them. For now, assume they are non-negative.
comment|// If negative floats are allowed some trickery needs to be done to find their byte order.
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
name|BytesRef
name|tmp1
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
name|byte
index|[]
name|buffer
init|=
operator|new
name|byte
index|[
literal|0
index|]
decl_stmt|;
name|ByteArrayDataOutput
name|output
init|=
operator|new
name|ByteArrayDataOutput
argument_list|(
name|buffer
argument_list|)
decl_stmt|;
while|while
condition|(
name|tfit
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|String
name|key
init|=
name|tfit
operator|.
name|next
argument_list|()
decl_stmt|;
name|UnicodeUtil
operator|.
name|UTF16toUTF8
argument_list|(
name|key
argument_list|,
literal|0
argument_list|,
name|key
operator|.
name|length
argument_list|()
argument_list|,
name|tmp1
argument_list|)
expr_stmt|;
if|if
condition|(
name|tmp1
operator|.
name|length
operator|+
literal|4
operator|>=
name|buffer
operator|.
name|length
condition|)
block|{
name|buffer
operator|=
name|ArrayUtil
operator|.
name|grow
argument_list|(
name|buffer
argument_list|,
name|tmp1
operator|.
name|length
operator|+
literal|4
argument_list|)
expr_stmt|;
block|}
name|output
operator|.
name|reset
argument_list|(
name|buffer
argument_list|)
expr_stmt|;
name|output
operator|.
name|writeInt
argument_list|(
name|FloatMagic
operator|.
name|toSortable
argument_list|(
name|tfit
operator|.
name|freq
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|output
operator|.
name|writeBytes
argument_list|(
name|tmp1
operator|.
name|bytes
argument_list|,
name|tmp1
operator|.
name|offset
argument_list|,
name|tmp1
operator|.
name|length
argument_list|)
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|output
operator|.
name|getPosition
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// We don't know the distribution of scores and we need to bucket them, so we'll sort
comment|// and divide into equal buckets.
name|SortInfo
name|info
init|=
operator|new
name|Sort
argument_list|()
operator|.
name|sort
argument_list|(
name|tempInput
argument_list|,
name|tempSorted
argument_list|)
decl_stmt|;
name|tempInput
operator|.
name|delete
argument_list|()
expr_stmt|;
name|FSTCompletionBuilder
name|builder
init|=
operator|new
name|FSTCompletionBuilder
argument_list|(
name|buckets
argument_list|,
operator|new
name|ExternalRefSorter
argument_list|(
operator|new
name|Sort
argument_list|()
argument_list|)
argument_list|,
name|sharedTailLength
argument_list|)
decl_stmt|;
specifier|final
name|int
name|inputLines
init|=
name|info
operator|.
name|lines
decl_stmt|;
name|reader
operator|=
operator|new
name|Sort
operator|.
name|ByteSequencesReader
argument_list|(
name|tempSorted
argument_list|)
expr_stmt|;
name|long
name|line
init|=
literal|0
decl_stmt|;
name|int
name|previousBucket
init|=
literal|0
decl_stmt|;
name|float
name|previousScore
init|=
literal|0
decl_stmt|;
name|ByteArrayDataInput
name|input
init|=
operator|new
name|ByteArrayDataInput
argument_list|()
decl_stmt|;
name|BytesRef
name|tmp2
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
while|while
condition|(
name|reader
operator|.
name|read
argument_list|(
name|tmp1
argument_list|)
condition|)
block|{
name|input
operator|.
name|reset
argument_list|(
name|tmp1
operator|.
name|bytes
argument_list|)
expr_stmt|;
name|float
name|currentScore
init|=
name|FloatMagic
operator|.
name|fromSortable
argument_list|(
name|input
operator|.
name|readInt
argument_list|()
argument_list|)
decl_stmt|;
name|int
name|bucket
decl_stmt|;
if|if
condition|(
name|line
operator|>
literal|0
operator|&&
name|currentScore
operator|==
name|previousScore
condition|)
block|{
name|bucket
operator|=
name|previousBucket
expr_stmt|;
block|}
else|else
block|{
name|bucket
operator|=
call|(
name|int
call|)
argument_list|(
name|line
operator|*
name|buckets
operator|/
name|inputLines
argument_list|)
expr_stmt|;
block|}
name|previousScore
operator|=
name|currentScore
expr_stmt|;
name|previousBucket
operator|=
name|bucket
expr_stmt|;
comment|// Only append the input, discard the weight.
name|tmp2
operator|.
name|bytes
operator|=
name|tmp1
operator|.
name|bytes
expr_stmt|;
name|tmp2
operator|.
name|offset
operator|=
name|input
operator|.
name|getPosition
argument_list|()
expr_stmt|;
name|tmp2
operator|.
name|length
operator|=
name|tmp1
operator|.
name|length
operator|-
name|input
operator|.
name|getPosition
argument_list|()
expr_stmt|;
name|builder
operator|.
name|add
argument_list|(
name|tmp2
argument_list|,
name|bucket
argument_list|)
expr_stmt|;
name|line
operator|++
expr_stmt|;
block|}
comment|// The two FSTCompletions share the same automaton.
name|this
operator|.
name|higherWeightsCompletion
operator|=
name|builder
operator|.
name|build
argument_list|()
expr_stmt|;
name|this
operator|.
name|normalCompletion
operator|=
operator|new
name|FSTCompletion
argument_list|(
name|higherWeightsCompletion
operator|.
name|getFST
argument_list|()
argument_list|,
literal|false
argument_list|,
name|exactMatchFirst
argument_list|)
expr_stmt|;
name|success
operator|=
literal|true
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|success
condition|)
name|IOUtils
operator|.
name|close
argument_list|(
name|reader
argument_list|,
name|writer
argument_list|)
expr_stmt|;
else|else
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|reader
argument_list|,
name|writer
argument_list|)
expr_stmt|;
name|tempInput
operator|.
name|delete
argument_list|()
expr_stmt|;
name|tempSorted
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|lookup
specifier|public
name|List
argument_list|<
name|LookupResult
argument_list|>
name|lookup
parameter_list|(
name|String
name|key
parameter_list|,
name|boolean
name|higherWeightsFirst
parameter_list|,
name|int
name|num
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|Completion
argument_list|>
name|completions
decl_stmt|;
if|if
condition|(
name|higherWeightsFirst
condition|)
block|{
name|completions
operator|=
name|higherWeightsCompletion
operator|.
name|lookup
argument_list|(
name|key
argument_list|,
name|num
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|completions
operator|=
name|normalCompletion
operator|.
name|lookup
argument_list|(
name|key
argument_list|,
name|num
argument_list|)
expr_stmt|;
block|}
specifier|final
name|ArrayList
argument_list|<
name|LookupResult
argument_list|>
name|results
init|=
operator|new
name|ArrayList
argument_list|<
name|LookupResult
argument_list|>
argument_list|(
name|completions
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|Completion
name|c
range|:
name|completions
control|)
block|{
name|results
operator|.
name|add
argument_list|(
operator|new
name|LookupResult
argument_list|(
name|c
operator|.
name|utf8
operator|.
name|utf8ToString
argument_list|()
argument_list|,
name|c
operator|.
name|bucket
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|results
return|;
block|}
annotation|@
name|Override
DECL|method|add
specifier|public
name|boolean
name|add
parameter_list|(
name|String
name|key
parameter_list|,
name|Object
name|value
parameter_list|)
block|{
comment|// Not supported.
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|get
specifier|public
name|Float
name|get
parameter_list|(
name|String
name|key
parameter_list|)
block|{
name|Integer
name|bucket
init|=
name|normalCompletion
operator|.
name|getBucket
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|bucket
operator|==
literal|null
condition|)
return|return
literal|null
return|;
else|else
return|return
operator|(
name|float
operator|)
name|normalCompletion
operator|.
name|getBucket
argument_list|(
name|key
argument_list|)
operator|/
name|normalCompletion
operator|.
name|getBucketCount
argument_list|()
return|;
block|}
comment|/**    * Deserialization from disk.    */
annotation|@
name|Override
DECL|method|load
specifier|public
specifier|synchronized
name|boolean
name|load
parameter_list|(
name|File
name|storeDir
parameter_list|)
throws|throws
name|IOException
block|{
name|File
name|data
init|=
operator|new
name|File
argument_list|(
name|storeDir
argument_list|,
name|FILENAME
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|data
operator|.
name|exists
argument_list|()
operator|||
operator|!
name|data
operator|.
name|canRead
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
name|this
operator|.
name|higherWeightsCompletion
operator|=
operator|new
name|FSTCompletion
argument_list|(
name|FST
operator|.
name|read
argument_list|(
name|data
argument_list|,
name|NoOutputs
operator|.
name|getSingleton
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|normalCompletion
operator|=
operator|new
name|FSTCompletion
argument_list|(
name|higherWeightsCompletion
operator|.
name|getFST
argument_list|()
argument_list|,
literal|false
argument_list|,
name|exactMatchFirst
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
comment|/**    * Serialization to disk.    */
annotation|@
name|Override
DECL|method|store
specifier|public
specifier|synchronized
name|boolean
name|store
parameter_list|(
name|File
name|storeDir
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|storeDir
operator|.
name|exists
argument_list|()
operator|||
operator|!
name|storeDir
operator|.
name|isDirectory
argument_list|()
operator|||
operator|!
name|storeDir
operator|.
name|canWrite
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|this
operator|.
name|normalCompletion
operator|==
literal|null
condition|)
return|return
literal|false
return|;
name|normalCompletion
operator|.
name|getFST
argument_list|()
operator|.
name|save
argument_list|(
operator|new
name|File
argument_list|(
name|storeDir
argument_list|,
name|FILENAME
argument_list|)
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
end_class
end_unit
