begin_unit
begin_package
DECL|package|org.apache.lucene.search.suggest
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
name|HashMap
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
name|index
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
name|ReaderUtil
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
name|StoredDocument
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
name|FunctionValues
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
name|ValueSource
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
name|BytesRefIterator
import|;
end_import
begin_comment
comment|/**  *<p>  * Dictionary with terms and optionally payload information   * taken from stored fields in a Lucene index. Similar to   * {@link DocumentDictionary}, except it obtains the weight  * of the terms in a document based on a {@link ValueSource}.  *</p>  *<b>NOTE:</b>   *<ul>  *<li>  *      The term and (optionally) payload fields have to be  *      stored  *</li>  *<li>  *      if the term or (optionally) payload fields supplied  *      do not have a value for a document, then the document is   *      rejected by the dictionary  *</li>  *</ul>  *<p>  *  In practice the {@link ValueSource} will likely be obtained  *  using the lucene expression module. The following example shows  *  how to create a {@link ValueSource} from a simple addition of two  *  fields:  *<code>  *    Expression expression = JavascriptCompiler.compile("f1 + f2");  *    SimpleBindings bindings = new SimpleBindings();  *    bindings.add(new SortField("f1", SortField.Type.LONG));  *    bindings.add(new SortField("f2", SortField.Type.LONG));  *    ValueSource valueSource = expression.getValueSource(bindings);  *</code>  *</p>  *  */
end_comment
begin_class
DECL|class|DocumentValueSourceDictionary
specifier|public
class|class
name|DocumentValueSourceDictionary
extends|extends
name|DocumentDictionary
block|{
DECL|field|weightsValueSource
specifier|private
specifier|final
name|ValueSource
name|weightsValueSource
decl_stmt|;
comment|/**    * Creates a new dictionary with the contents of the fields named<code>field</code>    * for the terms,<code>payloadField</code> for the corresponding payloads    * and uses the<code>weightsValueSource</code> supplied to determine the     * score.    */
DECL|method|DocumentValueSourceDictionary
specifier|public
name|DocumentValueSourceDictionary
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|String
name|field
parameter_list|,
name|ValueSource
name|weightsValueSource
parameter_list|,
name|String
name|payload
parameter_list|)
block|{
name|super
argument_list|(
name|reader
argument_list|,
name|field
argument_list|,
literal|null
argument_list|,
name|payload
argument_list|)
expr_stmt|;
name|this
operator|.
name|weightsValueSource
operator|=
name|weightsValueSource
expr_stmt|;
block|}
comment|/**     * Creates a new dictionary with the contents of the fields named<code>field</code>    * for the terms and uses the<code>weightsValueSource</code> supplied to determine the     * score.    */
DECL|method|DocumentValueSourceDictionary
specifier|public
name|DocumentValueSourceDictionary
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|String
name|field
parameter_list|,
name|ValueSource
name|weightsValueSource
parameter_list|)
block|{
name|super
argument_list|(
name|reader
argument_list|,
name|field
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|this
operator|.
name|weightsValueSource
operator|=
name|weightsValueSource
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getEntryIterator
specifier|public
name|InputIterator
name|getEntryIterator
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|new
name|DocumentValueSourceInputIterator
argument_list|(
name|payloadField
operator|!=
literal|null
argument_list|)
return|;
block|}
DECL|class|DocumentValueSourceInputIterator
specifier|final
class|class
name|DocumentValueSourceInputIterator
extends|extends
name|DocumentDictionary
operator|.
name|DocumentInputIterator
block|{
DECL|field|currentWeightValues
specifier|private
name|FunctionValues
name|currentWeightValues
decl_stmt|;
comment|/** leaves of the reader */
DECL|field|leaves
specifier|private
specifier|final
name|List
argument_list|<
name|AtomicReaderContext
argument_list|>
name|leaves
decl_stmt|;
comment|/** starting docIds of all the leaves */
DECL|field|starts
specifier|private
specifier|final
name|int
index|[]
name|starts
decl_stmt|;
comment|/** current leave index */
DECL|field|currentLeafIndex
specifier|private
name|int
name|currentLeafIndex
init|=
literal|0
decl_stmt|;
DECL|method|DocumentValueSourceInputIterator
specifier|public
name|DocumentValueSourceInputIterator
parameter_list|(
name|boolean
name|hasPayloads
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|hasPayloads
argument_list|)
expr_stmt|;
name|leaves
operator|=
name|reader
operator|.
name|leaves
argument_list|()
expr_stmt|;
name|starts
operator|=
operator|new
name|int
index|[
name|leaves
operator|.
name|size
argument_list|()
operator|+
literal|1
index|]
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
name|leaves
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|starts
index|[
name|i
index|]
operator|=
name|leaves
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|docBase
expr_stmt|;
block|}
name|starts
index|[
name|leaves
operator|.
name|size
argument_list|()
index|]
operator|=
name|reader
operator|.
name|maxDoc
argument_list|()
expr_stmt|;
name|currentWeightValues
operator|=
operator|(
name|leaves
operator|.
name|size
argument_list|()
operator|>
literal|0
operator|)
condition|?
name|weightsValueSource
operator|.
name|getValues
argument_list|(
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
argument_list|,
name|leaves
operator|.
name|get
argument_list|(
name|currentLeafIndex
argument_list|)
argument_list|)
else|:
literal|null
expr_stmt|;
block|}
comment|/**       * Returns the weight for the current<code>docId</code> as computed       * by the<code>weightsValueSource</code>      * */
annotation|@
name|Override
DECL|method|getWeight
specifier|protected
name|long
name|getWeight
parameter_list|(
name|StoredDocument
name|doc
parameter_list|,
name|int
name|docId
parameter_list|)
block|{
if|if
condition|(
name|currentWeightValues
operator|==
literal|null
condition|)
block|{
return|return
literal|0
return|;
block|}
name|int
name|subIndex
init|=
name|ReaderUtil
operator|.
name|subIndex
argument_list|(
name|docId
argument_list|,
name|starts
argument_list|)
decl_stmt|;
if|if
condition|(
name|subIndex
operator|!=
name|currentLeafIndex
condition|)
block|{
name|currentLeafIndex
operator|=
name|subIndex
expr_stmt|;
try|try
block|{
name|currentWeightValues
operator|=
name|weightsValueSource
operator|.
name|getValues
argument_list|(
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
argument_list|,
name|leaves
operator|.
name|get
argument_list|(
name|currentLeafIndex
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|()
throw|;
block|}
block|}
return|return
name|currentWeightValues
operator|.
name|longVal
argument_list|(
name|docId
operator|-
name|starts
index|[
name|subIndex
index|]
argument_list|)
return|;
block|}
block|}
block|}
end_class
end_unit
