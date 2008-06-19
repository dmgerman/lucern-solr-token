begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.schema
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|schema
package|;
end_package
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
name|solr
operator|.
name|search
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
name|solr
operator|.
name|search
operator|.
name|function
operator|.
name|IntFieldSource
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
name|Fieldable
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
name|XMLWriter
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
name|TextResponseWriter
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
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import
begin_comment
comment|/**  * @version $Id$  */
end_comment
begin_class
DECL|class|IntField
specifier|public
class|class
name|IntField
extends|extends
name|FieldType
block|{
DECL|method|init
specifier|protected
name|void
name|init
parameter_list|(
name|IndexSchema
name|schema
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
parameter_list|)
block|{
name|restrictProps
argument_list|(
name|SORT_MISSING_FIRST
operator||
name|SORT_MISSING_LAST
argument_list|)
expr_stmt|;
block|}
DECL|method|getSortField
specifier|public
name|SortField
name|getSortField
parameter_list|(
name|SchemaField
name|field
parameter_list|,
name|boolean
name|reverse
parameter_list|)
block|{
return|return
operator|new
name|SortField
argument_list|(
name|field
operator|.
name|name
argument_list|,
name|SortField
operator|.
name|INT
argument_list|,
name|reverse
argument_list|)
return|;
block|}
DECL|method|getValueSource
specifier|public
name|ValueSource
name|getValueSource
parameter_list|(
name|SchemaField
name|field
parameter_list|)
block|{
return|return
operator|new
name|IntFieldSource
argument_list|(
name|field
operator|.
name|name
argument_list|)
return|;
block|}
DECL|method|write
specifier|public
name|void
name|write
parameter_list|(
name|XMLWriter
name|xmlWriter
parameter_list|,
name|String
name|name
parameter_list|,
name|Fieldable
name|f
parameter_list|)
throws|throws
name|IOException
block|{
name|xmlWriter
operator|.
name|writeInt
argument_list|(
name|name
argument_list|,
name|f
operator|.
name|stringValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|write
specifier|public
name|void
name|write
parameter_list|(
name|TextResponseWriter
name|writer
parameter_list|,
name|String
name|name
parameter_list|,
name|Fieldable
name|f
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|s
init|=
name|f
operator|.
name|stringValue
argument_list|()
decl_stmt|;
name|int
name|len
init|=
name|s
operator|.
name|length
argument_list|()
decl_stmt|;
comment|// these values may be from a legacy lucene index, which may contain
comment|// integer values padded with zeros, or a zero length value.
if|if
condition|(
name|len
operator|>=
literal|2
condition|)
block|{
name|char
name|ch
init|=
name|s
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
operator|(
name|ch
operator|==
literal|'0'
operator|)
operator|||
operator|(
name|ch
operator|==
literal|'-'
operator|&&
name|s
operator|.
name|charAt
argument_list|(
literal|1
argument_list|)
operator|==
literal|'0'
operator|)
condition|)
block|{
name|s
operator|=
name|Integer
operator|.
name|toString
argument_list|(
name|Integer
operator|.
name|parseInt
argument_list|(
name|s
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|len
operator|==
literal|0
condition|)
block|{
comment|// zero length value means someone mistakenly indexed the value
comment|// instead of simply leaving it out.  Write a null value instead
comment|// of an integer value in this case.
name|writer
operator|.
name|writeNull
argument_list|(
name|name
argument_list|)
expr_stmt|;
return|return;
block|}
name|writer
operator|.
name|writeInt
argument_list|(
name|name
argument_list|,
name|s
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|toObject
specifier|public
name|Integer
name|toObject
parameter_list|(
name|Fieldable
name|f
parameter_list|)
block|{
return|return
name|Integer
operator|.
name|valueOf
argument_list|(
name|toExternal
argument_list|(
name|f
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class
end_unit
