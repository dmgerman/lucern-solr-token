begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.search.function
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
operator|.
name|function
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
name|search
operator|.
name|FieldCache
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
comment|/**  * Obtains float field values from the {@link org.apache.lucene.search.FieldCache}  * using<code>getFloats()</code>  * and makes those values available as other numeric types, casting as needed.  *  * @version $Id:$  */
end_comment
begin_class
DECL|class|DoubleFieldSource
specifier|public
class|class
name|DoubleFieldSource
extends|extends
name|FieldCacheSource
block|{
DECL|field|parser
specifier|protected
name|FieldCache
operator|.
name|DoubleParser
name|parser
decl_stmt|;
DECL|method|DoubleFieldSource
specifier|public
name|DoubleFieldSource
parameter_list|(
name|String
name|field
parameter_list|)
block|{
name|this
argument_list|(
name|field
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|DoubleFieldSource
specifier|public
name|DoubleFieldSource
parameter_list|(
name|String
name|field
parameter_list|,
name|FieldCache
operator|.
name|DoubleParser
name|parser
parameter_list|)
block|{
name|super
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|this
operator|.
name|parser
operator|=
name|parser
expr_stmt|;
block|}
DECL|method|description
specifier|public
name|String
name|description
parameter_list|()
block|{
return|return
literal|"double("
operator|+
name|field
operator|+
literal|')'
return|;
block|}
DECL|method|getValues
specifier|public
name|DocValues
name|getValues
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|double
index|[]
name|arr
init|=
operator|(
name|parser
operator|==
literal|null
operator|)
condition|?
operator|(
operator|(
name|FieldCache
operator|)
name|cache
operator|)
operator|.
name|getDoubles
argument_list|(
name|reader
argument_list|,
name|field
argument_list|)
else|:
operator|(
operator|(
name|FieldCache
operator|)
name|cache
operator|)
operator|.
name|getDoubles
argument_list|(
name|reader
argument_list|,
name|field
argument_list|,
name|parser
argument_list|)
decl_stmt|;
return|return
operator|new
name|DocValues
argument_list|()
block|{
specifier|public
name|float
name|floatVal
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
operator|(
name|float
operator|)
name|arr
index|[
name|doc
index|]
return|;
block|}
specifier|public
name|int
name|intVal
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
operator|(
name|int
operator|)
name|arr
index|[
name|doc
index|]
return|;
block|}
specifier|public
name|long
name|longVal
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
operator|(
name|long
operator|)
name|arr
index|[
name|doc
index|]
return|;
block|}
specifier|public
name|double
name|doubleVal
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
name|arr
index|[
name|doc
index|]
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
return|return
name|Double
operator|.
name|toString
argument_list|(
name|arr
index|[
name|doc
index|]
argument_list|)
return|;
block|}
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
name|floatVal
argument_list|(
name|doc
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|ValueSourceScorer
name|getRangeScorer
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|String
name|lowerVal
parameter_list|,
name|String
name|upperVal
parameter_list|,
name|boolean
name|includeLower
parameter_list|,
name|boolean
name|includeUpper
parameter_list|)
block|{
name|double
name|lower
decl_stmt|,
name|upper
decl_stmt|;
if|if
condition|(
name|lowerVal
operator|==
literal|null
condition|)
block|{
name|lower
operator|=
name|Double
operator|.
name|NEGATIVE_INFINITY
expr_stmt|;
block|}
else|else
block|{
name|lower
operator|=
name|Double
operator|.
name|parseDouble
argument_list|(
name|lowerVal
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|upperVal
operator|==
literal|null
condition|)
block|{
name|upper
operator|=
name|Double
operator|.
name|POSITIVE_INFINITY
expr_stmt|;
block|}
else|else
block|{
name|upper
operator|=
name|Double
operator|.
name|parseDouble
argument_list|(
name|upperVal
argument_list|)
expr_stmt|;
block|}
specifier|final
name|double
name|l
init|=
name|lower
decl_stmt|;
specifier|final
name|double
name|u
init|=
name|upper
decl_stmt|;
if|if
condition|(
name|includeLower
operator|&&
name|includeUpper
condition|)
block|{
return|return
operator|new
name|ValueSourceScorer
argument_list|(
name|reader
argument_list|,
name|this
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|matchesValue
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
name|double
name|docVal
init|=
name|doubleVal
argument_list|(
name|doc
argument_list|)
decl_stmt|;
return|return
name|docVal
operator|>=
name|l
operator|&&
name|docVal
operator|<=
name|u
return|;
block|}
block|}
return|;
block|}
elseif|else
if|if
condition|(
name|includeLower
operator|&&
operator|!
name|includeUpper
condition|)
block|{
return|return
operator|new
name|ValueSourceScorer
argument_list|(
name|reader
argument_list|,
name|this
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|matchesValue
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
name|double
name|docVal
init|=
name|doubleVal
argument_list|(
name|doc
argument_list|)
decl_stmt|;
return|return
name|docVal
operator|>=
name|l
operator|&&
name|docVal
operator|<
name|u
return|;
block|}
block|}
return|;
block|}
elseif|else
if|if
condition|(
operator|!
name|includeLower
operator|&&
name|includeUpper
condition|)
block|{
return|return
operator|new
name|ValueSourceScorer
argument_list|(
name|reader
argument_list|,
name|this
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|matchesValue
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
name|double
name|docVal
init|=
name|doubleVal
argument_list|(
name|doc
argument_list|)
decl_stmt|;
return|return
name|docVal
operator|>
name|l
operator|&&
name|docVal
operator|<=
name|u
return|;
block|}
block|}
return|;
block|}
else|else
block|{
return|return
operator|new
name|ValueSourceScorer
argument_list|(
name|reader
argument_list|,
name|this
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|matchesValue
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
name|double
name|docVal
init|=
name|doubleVal
argument_list|(
name|doc
argument_list|)
decl_stmt|;
return|return
name|docVal
operator|>
name|l
operator|&&
name|docVal
operator|<
name|u
return|;
block|}
block|}
return|;
block|}
block|}
block|}
return|;
block|}
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|o
operator|.
name|getClass
argument_list|()
operator|!=
name|DoubleFieldSource
operator|.
name|class
condition|)
return|return
literal|false
return|;
name|DoubleFieldSource
name|other
init|=
operator|(
name|DoubleFieldSource
operator|)
name|o
decl_stmt|;
return|return
name|super
operator|.
name|equals
argument_list|(
name|other
argument_list|)
operator|&&
name|this
operator|.
name|parser
operator|==
literal|null
condition|?
name|other
operator|.
name|parser
operator|==
literal|null
else|:
name|this
operator|.
name|parser
operator|.
name|getClass
argument_list|()
operator|==
name|other
operator|.
name|parser
operator|.
name|getClass
argument_list|()
return|;
block|}
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
name|int
name|h
init|=
name|parser
operator|==
literal|null
condition|?
name|Double
operator|.
name|class
operator|.
name|hashCode
argument_list|()
else|:
name|parser
operator|.
name|getClass
argument_list|()
operator|.
name|hashCode
argument_list|()
decl_stmt|;
name|h
operator|+=
name|super
operator|.
name|hashCode
argument_list|()
expr_stmt|;
return|return
name|h
return|;
block|}
block|}
end_class
end_unit
