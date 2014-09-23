begin_unit
begin_package
DECL|package|org.apache.lucene.queries.function.valuesource
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
operator|.
name|valuesource
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
name|Map
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
name|LeafReaderContext
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
name|index
operator|.
name|DocValues
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
name|FieldInfo
operator|.
name|DocValuesType
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
name|FieldInfo
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
name|docvalues
operator|.
name|DocTermsIndexDocValues
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
name|Bits
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
name|BytesRefBuilder
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
name|mutable
operator|.
name|MutableValue
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
name|mutable
operator|.
name|MutableValueStr
import|;
end_import
begin_comment
comment|/**  * An implementation for retrieving {@link FunctionValues} instances for string based fields.  */
end_comment
begin_class
DECL|class|BytesRefFieldSource
specifier|public
class|class
name|BytesRefFieldSource
extends|extends
name|FieldCacheSource
block|{
DECL|method|BytesRefFieldSource
specifier|public
name|BytesRefFieldSource
parameter_list|(
name|String
name|field
parameter_list|)
block|{
name|super
argument_list|(
name|field
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getValues
specifier|public
name|FunctionValues
name|getValues
parameter_list|(
name|Map
name|context
parameter_list|,
name|LeafReaderContext
name|readerContext
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|FieldInfo
name|fieldInfo
init|=
name|readerContext
operator|.
name|reader
argument_list|()
operator|.
name|getFieldInfos
argument_list|()
operator|.
name|fieldInfo
argument_list|(
name|field
argument_list|)
decl_stmt|;
comment|// To be sorted or not to be sorted, that is the question
comment|// TODO: do it cleaner?
if|if
condition|(
name|fieldInfo
operator|!=
literal|null
operator|&&
name|fieldInfo
operator|.
name|getDocValuesType
argument_list|()
operator|==
name|DocValuesType
operator|.
name|BINARY
condition|)
block|{
specifier|final
name|BinaryDocValues
name|binaryValues
init|=
name|DocValues
operator|.
name|getBinary
argument_list|(
name|readerContext
operator|.
name|reader
argument_list|()
argument_list|,
name|field
argument_list|)
decl_stmt|;
specifier|final
name|Bits
name|docsWithField
init|=
name|DocValues
operator|.
name|getDocsWithField
argument_list|(
name|readerContext
operator|.
name|reader
argument_list|()
argument_list|,
name|field
argument_list|)
decl_stmt|;
return|return
operator|new
name|FunctionValues
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|exists
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
name|docsWithField
operator|.
name|get
argument_list|(
name|doc
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|bytesVal
parameter_list|(
name|int
name|doc
parameter_list|,
name|BytesRefBuilder
name|target
parameter_list|)
block|{
name|target
operator|.
name|copyBytes
argument_list|(
name|binaryValues
operator|.
name|get
argument_list|(
name|doc
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|target
operator|.
name|length
argument_list|()
operator|>
literal|0
return|;
block|}
specifier|public
name|String
name|strVal
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
specifier|final
name|BytesRefBuilder
name|bytes
init|=
operator|new
name|BytesRefBuilder
argument_list|()
decl_stmt|;
return|return
name|bytesVal
argument_list|(
name|doc
argument_list|,
name|bytes
argument_list|)
condition|?
name|bytes
operator|.
name|get
argument_list|()
operator|.
name|utf8ToString
argument_list|()
else|:
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|Object
name|objectVal
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
name|strVal
argument_list|(
name|doc
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
name|description
argument_list|()
operator|+
literal|'='
operator|+
name|strVal
argument_list|(
name|doc
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|ValueFiller
name|getValueFiller
parameter_list|()
block|{
return|return
operator|new
name|ValueFiller
argument_list|()
block|{
specifier|private
specifier|final
name|MutableValueStr
name|mval
init|=
operator|new
name|MutableValueStr
argument_list|()
decl_stmt|;
annotation|@
name|Override
specifier|public
name|MutableValue
name|getValue
parameter_list|()
block|{
return|return
name|mval
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|fillValue
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
name|mval
operator|.
name|exists
operator|=
name|docsWithField
operator|.
name|get
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|mval
operator|.
name|value
operator|.
name|clear
argument_list|()
expr_stmt|;
name|mval
operator|.
name|value
operator|.
name|copyBytes
argument_list|(
name|binaryValues
operator|.
name|get
argument_list|(
name|doc
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|;
block|}
block|}
return|;
block|}
else|else
block|{
return|return
operator|new
name|DocTermsIndexDocValues
argument_list|(
name|this
argument_list|,
name|readerContext
argument_list|,
name|field
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|String
name|toTerm
parameter_list|(
name|String
name|readableValue
parameter_list|)
block|{
return|return
name|readableValue
return|;
block|}
annotation|@
name|Override
specifier|public
name|Object
name|objectVal
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
name|strVal
argument_list|(
name|doc
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
name|description
argument_list|()
operator|+
literal|'='
operator|+
name|strVal
argument_list|(
name|doc
argument_list|)
return|;
block|}
block|}
return|;
block|}
block|}
block|}
end_class
end_unit
