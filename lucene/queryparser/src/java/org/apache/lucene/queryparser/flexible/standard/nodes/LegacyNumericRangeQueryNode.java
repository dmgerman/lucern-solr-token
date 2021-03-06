begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.queryparser.flexible.standard.nodes
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queryparser
operator|.
name|flexible
operator|.
name|standard
operator|.
name|nodes
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
name|document
operator|.
name|FieldType
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
name|FieldType
operator|.
name|LegacyNumericType
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
name|queryparser
operator|.
name|flexible
operator|.
name|core
operator|.
name|QueryNodeException
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
name|queryparser
operator|.
name|flexible
operator|.
name|core
operator|.
name|messages
operator|.
name|QueryParserMessages
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
name|queryparser
operator|.
name|flexible
operator|.
name|messages
operator|.
name|MessageImpl
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
name|queryparser
operator|.
name|flexible
operator|.
name|standard
operator|.
name|config
operator|.
name|LegacyNumericConfig
import|;
end_import
begin_comment
comment|/**  * This query node represents a range query composed by {@link LegacyNumericQueryNode}  * bounds, which means the bound values are {@link Number}s.  *   * @see LegacyNumericQueryNode  * @see AbstractRangeQueryNode  * @deprecated Index with Points instead and use {@link PointRangeQueryNode} instead.  */
end_comment
begin_class
annotation|@
name|Deprecated
DECL|class|LegacyNumericRangeQueryNode
specifier|public
class|class
name|LegacyNumericRangeQueryNode
extends|extends
name|AbstractRangeQueryNode
argument_list|<
name|LegacyNumericQueryNode
argument_list|>
block|{
DECL|field|numericConfig
specifier|public
name|LegacyNumericConfig
name|numericConfig
decl_stmt|;
comment|/**    * Constructs a {@link LegacyNumericRangeQueryNode} object using the given    * {@link LegacyNumericQueryNode} as its bounds and {@link LegacyNumericConfig}.    *     * @param lower the lower bound    * @param upper the upper bound    * @param lowerInclusive<code>true</code> if the lower bound is inclusive, otherwise,<code>false</code>    * @param upperInclusive<code>true</code> if the upper bound is inclusive, otherwise,<code>false</code>    * @param numericConfig the {@link LegacyNumericConfig} that represents associated with the upper and lower bounds    *     * @see #setBounds(LegacyNumericQueryNode, LegacyNumericQueryNode, boolean, boolean, LegacyNumericConfig)    */
DECL|method|LegacyNumericRangeQueryNode
specifier|public
name|LegacyNumericRangeQueryNode
parameter_list|(
name|LegacyNumericQueryNode
name|lower
parameter_list|,
name|LegacyNumericQueryNode
name|upper
parameter_list|,
name|boolean
name|lowerInclusive
parameter_list|,
name|boolean
name|upperInclusive
parameter_list|,
name|LegacyNumericConfig
name|numericConfig
parameter_list|)
throws|throws
name|QueryNodeException
block|{
name|setBounds
argument_list|(
name|lower
argument_list|,
name|upper
argument_list|,
name|lowerInclusive
argument_list|,
name|upperInclusive
argument_list|,
name|numericConfig
argument_list|)
expr_stmt|;
block|}
DECL|method|getNumericDataType
specifier|private
specifier|static
name|LegacyNumericType
name|getNumericDataType
parameter_list|(
name|Number
name|number
parameter_list|)
throws|throws
name|QueryNodeException
block|{
if|if
condition|(
name|number
operator|instanceof
name|Long
condition|)
block|{
return|return
name|FieldType
operator|.
name|LegacyNumericType
operator|.
name|LONG
return|;
block|}
elseif|else
if|if
condition|(
name|number
operator|instanceof
name|Integer
condition|)
block|{
return|return
name|FieldType
operator|.
name|LegacyNumericType
operator|.
name|INT
return|;
block|}
elseif|else
if|if
condition|(
name|number
operator|instanceof
name|Double
condition|)
block|{
return|return
name|LegacyNumericType
operator|.
name|DOUBLE
return|;
block|}
elseif|else
if|if
condition|(
name|number
operator|instanceof
name|Float
condition|)
block|{
return|return
name|FieldType
operator|.
name|LegacyNumericType
operator|.
name|FLOAT
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|QueryNodeException
argument_list|(
operator|new
name|MessageImpl
argument_list|(
name|QueryParserMessages
operator|.
name|NUMBER_CLASS_NOT_SUPPORTED_BY_NUMERIC_RANGE_QUERY
argument_list|,
name|number
operator|.
name|getClass
argument_list|()
argument_list|)
argument_list|)
throw|;
block|}
block|}
comment|/**    * Sets the upper and lower bounds of this range query node and the    * {@link LegacyNumericConfig} associated with these bounds.    *     * @param lower the lower bound    * @param upper the upper bound    * @param lowerInclusive<code>true</code> if the lower bound is inclusive, otherwise,<code>false</code>    * @param upperInclusive<code>true</code> if the upper bound is inclusive, otherwise,<code>false</code>    * @param numericConfig the {@link LegacyNumericConfig} that represents associated with the upper and lower bounds    *     */
DECL|method|setBounds
specifier|public
name|void
name|setBounds
parameter_list|(
name|LegacyNumericQueryNode
name|lower
parameter_list|,
name|LegacyNumericQueryNode
name|upper
parameter_list|,
name|boolean
name|lowerInclusive
parameter_list|,
name|boolean
name|upperInclusive
parameter_list|,
name|LegacyNumericConfig
name|numericConfig
parameter_list|)
throws|throws
name|QueryNodeException
block|{
if|if
condition|(
name|numericConfig
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"numericConfig must not be null!"
argument_list|)
throw|;
block|}
name|LegacyNumericType
name|lowerNumberType
decl_stmt|,
name|upperNumberType
decl_stmt|;
if|if
condition|(
name|lower
operator|!=
literal|null
operator|&&
name|lower
operator|.
name|getValue
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|lowerNumberType
operator|=
name|getNumericDataType
argument_list|(
name|lower
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|lowerNumberType
operator|=
literal|null
expr_stmt|;
block|}
if|if
condition|(
name|upper
operator|!=
literal|null
operator|&&
name|upper
operator|.
name|getValue
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|upperNumberType
operator|=
name|getNumericDataType
argument_list|(
name|upper
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|upperNumberType
operator|=
literal|null
expr_stmt|;
block|}
if|if
condition|(
name|lowerNumberType
operator|!=
literal|null
operator|&&
operator|!
name|lowerNumberType
operator|.
name|equals
argument_list|(
name|numericConfig
operator|.
name|getType
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"lower value's type should be the same as numericConfig type: "
operator|+
name|lowerNumberType
operator|+
literal|" != "
operator|+
name|numericConfig
operator|.
name|getType
argument_list|()
argument_list|)
throw|;
block|}
if|if
condition|(
name|upperNumberType
operator|!=
literal|null
operator|&&
operator|!
name|upperNumberType
operator|.
name|equals
argument_list|(
name|numericConfig
operator|.
name|getType
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"upper value's type should be the same as numericConfig type: "
operator|+
name|upperNumberType
operator|+
literal|" != "
operator|+
name|numericConfig
operator|.
name|getType
argument_list|()
argument_list|)
throw|;
block|}
name|super
operator|.
name|setBounds
argument_list|(
name|lower
argument_list|,
name|upper
argument_list|,
name|lowerInclusive
argument_list|,
name|upperInclusive
argument_list|)
expr_stmt|;
name|this
operator|.
name|numericConfig
operator|=
name|numericConfig
expr_stmt|;
block|}
comment|/**    * Returns the {@link LegacyNumericConfig} associated with the lower and upper bounds.    *     * @return the {@link LegacyNumericConfig} associated with the lower and upper bounds    */
DECL|method|getNumericConfig
specifier|public
name|LegacyNumericConfig
name|getNumericConfig
parameter_list|()
block|{
return|return
name|this
operator|.
name|numericConfig
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
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|(
literal|"<numericRange lowerInclusive='"
argument_list|)
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|isLowerInclusive
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"' upperInclusive='"
argument_list|)
operator|.
name|append
argument_list|(
name|isUpperInclusive
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"' precisionStep='"
operator|+
name|numericConfig
operator|.
name|getPrecisionStep
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"' type='"
operator|+
name|numericConfig
operator|.
name|getType
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"'>\n"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|getLowerBound
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|'\n'
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|getUpperBound
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|'\n'
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"</numericRange>"
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class
end_unit
