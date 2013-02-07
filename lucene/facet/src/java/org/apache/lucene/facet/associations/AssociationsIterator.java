begin_unit
begin_package
DECL|package|org.apache.lucene.facet.associations
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|associations
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
name|BinaryDocValues
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
name|util
operator|.
name|BytesRef
import|;
end_import
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_comment
comment|/**  * An iterator over a document's category associations.  *   * @lucene.experimental  */
end_comment
begin_class
DECL|class|AssociationsIterator
specifier|public
specifier|abstract
class|class
name|AssociationsIterator
parameter_list|<
name|T
extends|extends
name|CategoryAssociation
parameter_list|>
block|{
DECL|field|association
specifier|private
specifier|final
name|T
name|association
decl_stmt|;
DECL|field|dvField
specifier|private
specifier|final
name|String
name|dvField
decl_stmt|;
DECL|field|bytes
specifier|private
specifier|final
name|BytesRef
name|bytes
init|=
operator|new
name|BytesRef
argument_list|(
literal|32
argument_list|)
decl_stmt|;
DECL|field|current
specifier|private
name|BinaryDocValues
name|current
decl_stmt|;
comment|/**    * Construct a new associations iterator. The given    * {@link CategoryAssociation} is used to deserialize the association values.    * It is assumed that all association values can be deserialized with the    * given {@link CategoryAssociation}.    */
DECL|method|AssociationsIterator
specifier|public
name|AssociationsIterator
parameter_list|(
name|String
name|field
parameter_list|,
name|T
name|association
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|association
operator|=
name|association
expr_stmt|;
name|this
operator|.
name|dvField
operator|=
name|field
operator|+
name|association
operator|.
name|getCategoryListID
argument_list|()
expr_stmt|;
block|}
comment|/**    * Sets the {@link AtomicReaderContext} for which {@link #setNextDoc(int)}    * calls will be made. Returns true iff this reader has associations for any    * of the documents belonging to the association given to the constructor.    */
DECL|method|setNextReader
specifier|public
specifier|final
name|boolean
name|setNextReader
parameter_list|(
name|AtomicReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|current
operator|=
name|context
operator|.
name|reader
argument_list|()
operator|.
name|getBinaryDocValues
argument_list|(
name|dvField
argument_list|)
expr_stmt|;
return|return
name|current
operator|!=
literal|null
return|;
block|}
comment|/**    * Skip to the requested document. Returns true iff the document has category    * association values and they were read successfully. Associations are    * handled through {@link #handleAssociation(int, CategoryAssociation)} by    * extending classes.    */
DECL|method|setNextDoc
specifier|protected
specifier|final
name|boolean
name|setNextDoc
parameter_list|(
name|int
name|docID
parameter_list|)
throws|throws
name|IOException
block|{
name|current
operator|.
name|get
argument_list|(
name|docID
argument_list|,
name|bytes
argument_list|)
expr_stmt|;
if|if
condition|(
name|bytes
operator|.
name|length
operator|==
literal|0
condition|)
block|{
return|return
literal|false
return|;
comment|// no associations for the requested document
block|}
name|ByteArrayDataInput
name|in
init|=
operator|new
name|ByteArrayDataInput
argument_list|(
name|bytes
operator|.
name|bytes
argument_list|,
name|bytes
operator|.
name|offset
argument_list|,
name|bytes
operator|.
name|length
argument_list|)
decl_stmt|;
while|while
condition|(
operator|!
name|in
operator|.
name|eof
argument_list|()
condition|)
block|{
name|int
name|ordinal
init|=
name|in
operator|.
name|readInt
argument_list|()
decl_stmt|;
name|association
operator|.
name|deserialize
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|handleAssociation
argument_list|(
name|ordinal
argument_list|,
name|association
argument_list|)
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
comment|/** A hook for extending classes to handle the given association value for the ordinal. */
DECL|method|handleAssociation
specifier|protected
specifier|abstract
name|void
name|handleAssociation
parameter_list|(
name|int
name|ordinal
parameter_list|,
name|T
name|association
parameter_list|)
function_decl|;
block|}
end_class
end_unit
