begin_unit
begin_package
DECL|package|org.apache.lucene.facet.search
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|search
package|;
end_package
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
name|util
operator|.
name|UnsafeByteArrayInputStream
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
name|encoding
operator|.
name|IntDecoder
import|;
end_import
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_comment
comment|/**  * A payload deserializer comes with its own working space (buffer). One need to  * define the {@link IndexReader} and {@link Term} in which the payload resides.  * The iterator then consumes the payload information of each document and  * decodes it into categories. A typical use case of this class is:  *   *<pre>  * IndexReader reader = [open your reader];  * Term t = new Term(&quot;field&quot;,&quot;where-payload-exists&quot;);  * CategoryListIterator cli = new PayloadIntDecodingIterator(reader, t);  * if (!cli.init()) {  *   // it means there are no payloads / documents associated with that term.  *   // Usually a sanity check. However, init() must be called.  * }  * DocIdSetIterator disi = [you usually iterate on something else, such as a Scorer];  * int doc;  * while ((doc = disi.nextDoc()) != DocIdSetIterator.NO_MORE_DOCS) {  *   cli.setdoc(doc);  *   long category;  *   while ((category = cli.nextCategory())&lt; Integer.MAX_VALUE) {  *   }  * }  *</pre>  *   * @lucene.experimental  */
end_comment
begin_class
DECL|class|PayloadIntDecodingIterator
specifier|public
class|class
name|PayloadIntDecodingIterator
implements|implements
name|CategoryListIterator
block|{
DECL|field|ubais
specifier|private
specifier|final
name|UnsafeByteArrayInputStream
name|ubais
decl_stmt|;
DECL|field|decoder
specifier|private
specifier|final
name|IntDecoder
name|decoder
decl_stmt|;
DECL|field|indexReader
specifier|private
specifier|final
name|IndexReader
name|indexReader
decl_stmt|;
DECL|field|term
specifier|private
specifier|final
name|Term
name|term
decl_stmt|;
DECL|field|pi
specifier|private
specifier|final
name|PayloadIterator
name|pi
decl_stmt|;
DECL|field|hashCode
specifier|private
specifier|final
name|int
name|hashCode
decl_stmt|;
DECL|method|PayloadIntDecodingIterator
specifier|public
name|PayloadIntDecodingIterator
parameter_list|(
name|IndexReader
name|indexReader
parameter_list|,
name|Term
name|term
parameter_list|,
name|IntDecoder
name|decoder
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|indexReader
argument_list|,
name|term
argument_list|,
name|decoder
argument_list|,
operator|new
name|byte
index|[
literal|1024
index|]
argument_list|)
expr_stmt|;
block|}
DECL|method|PayloadIntDecodingIterator
specifier|public
name|PayloadIntDecodingIterator
parameter_list|(
name|IndexReader
name|indexReader
parameter_list|,
name|Term
name|term
parameter_list|,
name|IntDecoder
name|decoder
parameter_list|,
name|byte
index|[]
name|buffer
parameter_list|)
throws|throws
name|IOException
block|{
name|pi
operator|=
operator|new
name|PayloadIterator
argument_list|(
name|indexReader
argument_list|,
name|term
argument_list|,
name|buffer
argument_list|)
expr_stmt|;
name|ubais
operator|=
operator|new
name|UnsafeByteArrayInputStream
argument_list|()
expr_stmt|;
name|this
operator|.
name|decoder
operator|=
name|decoder
expr_stmt|;
name|hashCode
operator|=
name|indexReader
operator|.
name|hashCode
argument_list|()
operator|^
name|term
operator|.
name|hashCode
argument_list|()
expr_stmt|;
name|this
operator|.
name|term
operator|=
name|term
expr_stmt|;
name|this
operator|.
name|indexReader
operator|=
name|indexReader
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|other
parameter_list|)
block|{
if|if
condition|(
operator|!
operator|(
name|other
operator|instanceof
name|PayloadIntDecodingIterator
operator|)
condition|)
block|{
return|return
literal|false
return|;
block|}
name|PayloadIntDecodingIterator
name|that
init|=
operator|(
name|PayloadIntDecodingIterator
operator|)
name|other
decl_stmt|;
if|if
condition|(
name|hashCode
operator|!=
name|that
operator|.
name|hashCode
condition|)
block|{
return|return
literal|false
return|;
block|}
comment|// Hash codes are the same, check equals() to avoid cases of hash-collisions.
return|return
name|indexReader
operator|.
name|equals
argument_list|(
name|that
operator|.
name|indexReader
argument_list|)
operator|&&
name|term
operator|.
name|equals
argument_list|(
name|that
operator|.
name|term
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|hashCode
return|;
block|}
DECL|method|init
specifier|public
name|boolean
name|init
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|pi
operator|.
name|init
argument_list|()
return|;
block|}
DECL|method|nextCategory
specifier|public
name|long
name|nextCategory
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|decoder
operator|.
name|decode
argument_list|()
return|;
block|}
DECL|method|skipTo
specifier|public
name|boolean
name|skipTo
parameter_list|(
name|int
name|docId
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|pi
operator|.
name|setdoc
argument_list|(
name|docId
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
comment|// Initializing the decoding mechanism with the new payload data
name|ubais
operator|.
name|reInit
argument_list|(
name|pi
operator|.
name|getBuffer
argument_list|()
argument_list|,
literal|0
argument_list|,
name|pi
operator|.
name|getPayloadLength
argument_list|()
argument_list|)
expr_stmt|;
name|decoder
operator|.
name|reInit
argument_list|(
name|ubais
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
end_class
end_unit
