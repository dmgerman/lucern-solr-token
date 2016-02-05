begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.response
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|response
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
name|java
operator|.
name|io
operator|.
name|OutputStream
import|;
end_import
begin_import
import|import
name|java
operator|.
name|math
operator|.
name|BigDecimal
import|;
end_import
begin_import
import|import
name|java
operator|.
name|math
operator|.
name|BigInteger
import|;
end_import
begin_import
import|import
name|com
operator|.
name|fasterxml
operator|.
name|jackson
operator|.
name|dataformat
operator|.
name|smile
operator|.
name|SmileFactory
import|;
end_import
begin_import
import|import
name|com
operator|.
name|fasterxml
operator|.
name|jackson
operator|.
name|dataformat
operator|.
name|smile
operator|.
name|SmileGenerator
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|util
operator|.
name|NamedList
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|request
operator|.
name|SolrQueryRequest
import|;
end_import
begin_class
DECL|class|SmileResponseWriter
specifier|public
class|class
name|SmileResponseWriter
extends|extends
name|BinaryResponseWriter
block|{
annotation|@
name|Override
DECL|method|write
specifier|public
name|void
name|write
parameter_list|(
name|OutputStream
name|out
parameter_list|,
name|SolrQueryRequest
name|request
parameter_list|,
name|SolrQueryResponse
name|response
parameter_list|)
throws|throws
name|IOException
block|{
operator|new
name|SmileWriter
argument_list|(
name|out
argument_list|,
name|request
argument_list|,
name|response
argument_list|)
operator|.
name|writeResponse
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|NamedList
name|args
parameter_list|)
block|{    }
comment|//smile format is an equivalent of JSON format . So we extend JSONWriter and override the relevant methods
DECL|class|SmileWriter
specifier|public
specifier|static
class|class
name|SmileWriter
extends|extends
name|JSONWriter
block|{
DECL|field|gen
specifier|protected
specifier|final
name|SmileGenerator
name|gen
decl_stmt|;
DECL|field|out
specifier|protected
specifier|final
name|OutputStream
name|out
decl_stmt|;
DECL|method|SmileWriter
specifier|public
name|SmileWriter
parameter_list|(
name|OutputStream
name|out
parameter_list|,
name|SolrQueryRequest
name|req
parameter_list|,
name|SolrQueryResponse
name|rsp
parameter_list|)
block|{
name|super
argument_list|(
literal|null
argument_list|,
name|req
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
name|this
operator|.
name|out
operator|=
name|out
expr_stmt|;
name|SmileFactory
name|smileFactory
init|=
operator|new
name|SmileFactory
argument_list|()
decl_stmt|;
name|smileFactory
operator|.
name|enable
argument_list|(
name|SmileGenerator
operator|.
name|Feature
operator|.
name|CHECK_SHARED_NAMES
argument_list|)
expr_stmt|;
try|try
block|{
name|gen
operator|=
name|smileFactory
operator|.
name|createGenerator
argument_list|(
name|this
operator|.
name|out
argument_list|,
literal|null
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
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|writeResponse
specifier|public
name|void
name|writeResponse
parameter_list|()
throws|throws
name|IOException
block|{
comment|//we always write header , it is just 4 bytes and not worth optimizing
name|gen
operator|.
name|writeHeader
argument_list|()
expr_stmt|;
name|super
operator|.
name|writeNamedList
argument_list|(
literal|null
argument_list|,
name|rsp
operator|.
name|getValues
argument_list|()
argument_list|)
expr_stmt|;
name|gen
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeNumber
specifier|protected
name|void
name|writeNumber
parameter_list|(
name|String
name|name
parameter_list|,
name|Number
name|val
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|val
operator|instanceof
name|Integer
condition|)
block|{
name|gen
operator|.
name|writeNumber
argument_list|(
name|val
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|val
operator|instanceof
name|Long
condition|)
block|{
name|gen
operator|.
name|writeNumber
argument_list|(
name|val
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|val
operator|instanceof
name|Float
condition|)
block|{
name|gen
operator|.
name|writeNumber
argument_list|(
name|val
operator|.
name|floatValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|val
operator|instanceof
name|Double
condition|)
block|{
name|gen
operator|.
name|writeNumber
argument_list|(
name|val
operator|.
name|floatValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|val
operator|instanceof
name|Short
condition|)
block|{
name|gen
operator|.
name|writeNumber
argument_list|(
name|val
operator|.
name|shortValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|val
operator|instanceof
name|Byte
condition|)
block|{
name|gen
operator|.
name|writeNumber
argument_list|(
name|val
operator|.
name|byteValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|val
operator|instanceof
name|BigInteger
condition|)
block|{
name|gen
operator|.
name|writeNumber
argument_list|(
operator|(
name|BigInteger
operator|)
name|val
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|val
operator|instanceof
name|BigDecimal
condition|)
block|{
name|gen
operator|.
name|writeNumber
argument_list|(
operator|(
name|BigDecimal
operator|)
name|val
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|gen
operator|.
name|writeString
argument_list|(
name|val
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|':'
operator|+
name|val
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
comment|// default... for debugging only
block|}
block|}
annotation|@
name|Override
DECL|method|writeBool
specifier|public
name|void
name|writeBool
parameter_list|(
name|String
name|name
parameter_list|,
name|Boolean
name|val
parameter_list|)
throws|throws
name|IOException
block|{
name|gen
operator|.
name|writeBoolean
argument_list|(
name|val
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeNull
specifier|public
name|void
name|writeNull
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
name|gen
operator|.
name|writeNull
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeStr
specifier|public
name|void
name|writeStr
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|val
parameter_list|,
name|boolean
name|needsEscaping
parameter_list|)
throws|throws
name|IOException
block|{
name|gen
operator|.
name|writeString
argument_list|(
name|val
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeLong
specifier|public
name|void
name|writeLong
parameter_list|(
name|String
name|name
parameter_list|,
name|long
name|val
parameter_list|)
throws|throws
name|IOException
block|{
name|gen
operator|.
name|writeNumber
argument_list|(
name|val
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeInt
specifier|public
name|void
name|writeInt
parameter_list|(
name|String
name|name
parameter_list|,
name|int
name|val
parameter_list|)
throws|throws
name|IOException
block|{
name|gen
operator|.
name|writeNumber
argument_list|(
name|val
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeBool
specifier|public
name|void
name|writeBool
parameter_list|(
name|String
name|name
parameter_list|,
name|boolean
name|val
parameter_list|)
throws|throws
name|IOException
block|{
name|gen
operator|.
name|writeBoolean
argument_list|(
name|val
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeFloat
specifier|public
name|void
name|writeFloat
parameter_list|(
name|String
name|name
parameter_list|,
name|float
name|val
parameter_list|)
throws|throws
name|IOException
block|{
name|gen
operator|.
name|writeNumber
argument_list|(
name|val
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeArrayCloser
specifier|public
name|void
name|writeArrayCloser
parameter_list|()
throws|throws
name|IOException
block|{
name|gen
operator|.
name|writeEndArray
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeArraySeparator
specifier|public
name|void
name|writeArraySeparator
parameter_list|()
throws|throws
name|IOException
block|{
comment|//do nothing
block|}
annotation|@
name|Override
DECL|method|writeArrayOpener
specifier|public
name|void
name|writeArrayOpener
parameter_list|(
name|int
name|size
parameter_list|)
throws|throws
name|IOException
throws|,
name|IllegalArgumentException
block|{
name|gen
operator|.
name|writeStartArray
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeMapCloser
specifier|public
name|void
name|writeMapCloser
parameter_list|()
throws|throws
name|IOException
block|{
name|gen
operator|.
name|writeEndObject
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeMapSeparator
specifier|public
name|void
name|writeMapSeparator
parameter_list|()
throws|throws
name|IOException
block|{
comment|//do nothing
block|}
annotation|@
name|Override
DECL|method|writeMapOpener
specifier|public
name|void
name|writeMapOpener
parameter_list|(
name|int
name|size
parameter_list|)
throws|throws
name|IOException
throws|,
name|IllegalArgumentException
block|{
name|gen
operator|.
name|writeStartObject
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeKey
specifier|protected
name|void
name|writeKey
parameter_list|(
name|String
name|fname
parameter_list|,
name|boolean
name|needsEscaping
parameter_list|)
throws|throws
name|IOException
block|{
name|gen
operator|.
name|writeFieldName
argument_list|(
name|fname
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeByteArr
specifier|public
name|void
name|writeByteArr
parameter_list|(
name|String
name|name
parameter_list|,
name|byte
index|[]
name|buf
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|len
parameter_list|)
throws|throws
name|IOException
block|{
name|gen
operator|.
name|writeBinary
argument_list|(
name|buf
argument_list|,
name|offset
argument_list|,
name|len
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setLevel
specifier|public
name|void
name|setLevel
parameter_list|(
name|int
name|level
parameter_list|)
block|{
comment|//do nothing
block|}
annotation|@
name|Override
DECL|method|level
specifier|public
name|int
name|level
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
annotation|@
name|Override
DECL|method|indent
specifier|public
name|void
name|indent
parameter_list|()
throws|throws
name|IOException
block|{
comment|//do nothing
block|}
annotation|@
name|Override
DECL|method|indent
specifier|public
name|void
name|indent
parameter_list|(
name|int
name|lev
parameter_list|)
throws|throws
name|IOException
block|{
comment|//do nothing
block|}
annotation|@
name|Override
DECL|method|incLevel
specifier|public
name|int
name|incLevel
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
annotation|@
name|Override
DECL|method|decLevel
specifier|public
name|int
name|decLevel
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
block|}
block|}
end_class
end_unit
