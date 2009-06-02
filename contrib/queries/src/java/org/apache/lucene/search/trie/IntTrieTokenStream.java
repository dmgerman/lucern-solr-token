begin_unit
begin_package
DECL|package|org.apache.lucene.search.trie
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|trie
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
name|analysis
operator|.
name|TokenStream
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
name|Token
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
name|tokenattributes
operator|.
name|TermAttribute
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
name|tokenattributes
operator|.
name|TypeAttribute
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
name|tokenattributes
operator|.
name|PositionIncrementAttribute
import|;
end_import
begin_comment
comment|/**  * This class provides a {@link TokenStream} for indexing<code>int</code> values  * that can be queried by {@link IntTrieRangeFilter}. This stream is not intended  * to be used in analyzers, its more for iterating the different precisions during  * indexing a specific numeric value.  *<p>A<code>int</code> value is indexed as multiple string encoded terms, each reduced  * by zeroing bits from the right. Each value is also prefixed (in the first char) by the  *<code>shift</code> value (number of bits removed) used during encoding.  *<p>The number of bits removed from the right for each trie entry is called  *<code>precisionStep</code> in this API. For comparing the different step values, see the  * {@linkplain org.apache.lucene.search.trie package description}.  *<p>The usage pattern is (it is recommened to switch off norms and term frequencies  * for numeric fields; it does not make sense to have them):  *<pre>  *  Field field = new Field(name, new IntTrieTokenStream(value, precisionStep));  *  field.setOmitNorms(true);  *  field.setOmitTermFreqAndPositions(true);  *  document.add(field);  *</pre>  *<p>For optimal performance, re-use the TokenStream and Field instance  * for more than one document:  *<pre>  *<em>// init</em>  *  IntTrieTokenStream stream = new IntTrieTokenStream(precisionStep);  *  Field field = new Field(name, stream);  *  field.setOmitNorms(true);  *  field.setOmitTermFreqAndPositions(true);  *  Document doc = new Document();  *  document.add(field);  *<em>// use this code to index many documents:</em>  *  stream.setValue(value1)  *  writer.addDocument(document);  *  stream.setValue(value2)  *  writer.addDocument(document);  *  ...  *</pre>  *<p><em>Please note:</em> Token streams are read, when the document is added to index.  * If you index more than one numeric field, use a separate instance for each.  *<p>For more information, how trie fields work, see the  * {@linkplain org.apache.lucene.search.trie package description}.  */
end_comment
begin_class
DECL|class|IntTrieTokenStream
specifier|public
class|class
name|IntTrieTokenStream
extends|extends
name|TokenStream
block|{
comment|/** The full precision token gets this token type assigned. */
DECL|field|TOKEN_TYPE_FULL_PREC
specifier|public
specifier|static
specifier|final
name|String
name|TOKEN_TYPE_FULL_PREC
init|=
literal|"fullPrecTrieInt"
decl_stmt|;
comment|/** The lower precision tokens gets this token type assigned. */
DECL|field|TOKEN_TYPE_LOWER_PREC
specifier|public
specifier|static
specifier|final
name|String
name|TOKEN_TYPE_LOWER_PREC
init|=
literal|"lowerPrecTrieInt"
decl_stmt|;
comment|/**    * Creates a token stream for indexing<code>value</code> with the given    *<code>precisionStep</code>. As instance creating is a major cost,    * consider using a {@link #IntTrieTokenStream(int)} instance once for    * indexing a large number of documents and assign a value with    * {@link #setValue} for each document.    * To index float values use the converter {@link TrieUtils#doubleToSortableLong}.    */
DECL|method|IntTrieTokenStream
specifier|public
name|IntTrieTokenStream
parameter_list|(
specifier|final
name|int
name|value
parameter_list|,
specifier|final
name|int
name|precisionStep
parameter_list|)
block|{
if|if
condition|(
name|precisionStep
argument_list|<
literal|1
operator|||
name|precisionStep
argument_list|>
literal|32
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"precisionStep may only be 1..32"
argument_list|)
throw|;
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
name|this
operator|.
name|precisionStep
operator|=
name|precisionStep
expr_stmt|;
name|termAtt
operator|=
operator|(
name|TermAttribute
operator|)
name|addAttribute
argument_list|(
name|TermAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
name|typeAtt
operator|=
operator|(
name|TypeAttribute
operator|)
name|addAttribute
argument_list|(
name|TypeAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
name|posIncrAtt
operator|=
operator|(
name|PositionIncrementAttribute
operator|)
name|addAttribute
argument_list|(
name|PositionIncrementAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
name|shiftAtt
operator|=
operator|(
name|ShiftAttribute
operator|)
name|addAttribute
argument_list|(
name|ShiftAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates a token stream for indexing values with the given    *<code>precisionStep</code>. This stream is initially&quot;empty&quot;    * (using a numeric value of 0), assign a value before indexing    * each document using {@link #setValue}.    */
DECL|method|IntTrieTokenStream
specifier|public
name|IntTrieTokenStream
parameter_list|(
specifier|final
name|int
name|precisionStep
parameter_list|)
block|{
name|this
argument_list|(
literal|0
argument_list|,
name|precisionStep
argument_list|)
expr_stmt|;
block|}
comment|/**    * Resets the token stream to deliver prefix encoded values    * for<code>value</code>. Use this method to index the same    * numeric field for a large number of documents and reuse the    * current stream instance.    * To index float values use the converter {@link TrieUtils#doubleToSortableLong}.    */
DECL|method|setValue
specifier|public
name|void
name|setValue
parameter_list|(
specifier|final
name|int
name|value
parameter_list|)
block|{
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
name|reset
argument_list|()
expr_stmt|;
block|}
comment|// @Override
DECL|method|reset
specifier|public
name|void
name|reset
parameter_list|()
block|{
name|shift
operator|=
literal|0
expr_stmt|;
block|}
comment|// @Override
DECL|method|incrementToken
specifier|public
name|boolean
name|incrementToken
parameter_list|()
block|{
if|if
condition|(
name|shift
operator|>=
literal|32
condition|)
return|return
literal|false
return|;
specifier|final
name|char
index|[]
name|buffer
init|=
name|termAtt
operator|.
name|resizeTermBuffer
argument_list|(
name|TrieUtils
operator|.
name|INT_BUF_SIZE
argument_list|)
decl_stmt|;
name|termAtt
operator|.
name|setTermLength
argument_list|(
name|TrieUtils
operator|.
name|intToPrefixCoded
argument_list|(
name|value
argument_list|,
name|shift
argument_list|,
name|buffer
argument_list|)
argument_list|)
expr_stmt|;
name|shiftAtt
operator|.
name|setShift
argument_list|(
name|shift
argument_list|)
expr_stmt|;
if|if
condition|(
name|shift
operator|==
literal|0
condition|)
block|{
name|typeAtt
operator|.
name|setType
argument_list|(
name|TOKEN_TYPE_FULL_PREC
argument_list|)
expr_stmt|;
name|posIncrAtt
operator|.
name|setPositionIncrement
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|typeAtt
operator|.
name|setType
argument_list|(
name|TOKEN_TYPE_LOWER_PREC
argument_list|)
expr_stmt|;
name|posIncrAtt
operator|.
name|setPositionIncrement
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
name|shift
operator|+=
name|precisionStep
expr_stmt|;
return|return
literal|true
return|;
block|}
comment|// @Override
comment|/** @deprecated */
DECL|method|next
specifier|public
name|Token
name|next
parameter_list|(
specifier|final
name|Token
name|reusableToken
parameter_list|)
block|{
if|if
condition|(
name|shift
operator|>=
literal|32
condition|)
return|return
literal|null
return|;
name|reusableToken
operator|.
name|clear
argument_list|()
expr_stmt|;
specifier|final
name|char
index|[]
name|buffer
init|=
name|reusableToken
operator|.
name|resizeTermBuffer
argument_list|(
name|TrieUtils
operator|.
name|INT_BUF_SIZE
argument_list|)
decl_stmt|;
name|reusableToken
operator|.
name|setTermLength
argument_list|(
name|TrieUtils
operator|.
name|intToPrefixCoded
argument_list|(
name|value
argument_list|,
name|shift
argument_list|,
name|buffer
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|shift
operator|==
literal|0
condition|)
block|{
name|reusableToken
operator|.
name|setType
argument_list|(
name|TOKEN_TYPE_FULL_PREC
argument_list|)
expr_stmt|;
name|reusableToken
operator|.
name|setPositionIncrement
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|reusableToken
operator|.
name|setType
argument_list|(
name|TOKEN_TYPE_LOWER_PREC
argument_list|)
expr_stmt|;
name|reusableToken
operator|.
name|setPositionIncrement
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
name|shift
operator|+=
name|precisionStep
expr_stmt|;
return|return
name|reusableToken
return|;
block|}
comment|// @Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
specifier|final
name|StringBuffer
name|sb
init|=
operator|new
name|StringBuffer
argument_list|(
literal|"(trie-int,value="
argument_list|)
operator|.
name|append
argument_list|(
name|value
argument_list|)
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|",precisionStep="
argument_list|)
operator|.
name|append
argument_list|(
name|precisionStep
argument_list|)
operator|.
name|append
argument_list|(
literal|')'
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
comment|// members
DECL|field|termAtt
specifier|private
specifier|final
name|TermAttribute
name|termAtt
decl_stmt|;
DECL|field|typeAtt
specifier|private
specifier|final
name|TypeAttribute
name|typeAtt
decl_stmt|;
DECL|field|posIncrAtt
specifier|private
specifier|final
name|PositionIncrementAttribute
name|posIncrAtt
decl_stmt|;
DECL|field|shiftAtt
specifier|private
specifier|final
name|ShiftAttribute
name|shiftAtt
decl_stmt|;
DECL|field|shift
specifier|private
name|int
name|shift
init|=
literal|0
decl_stmt|;
DECL|field|value
specifier|private
name|int
name|value
decl_stmt|;
DECL|field|precisionStep
specifier|private
specifier|final
name|int
name|precisionStep
decl_stmt|;
block|}
end_class
end_unit
