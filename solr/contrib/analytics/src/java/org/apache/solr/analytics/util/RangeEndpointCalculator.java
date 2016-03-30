begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.analytics.util
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|analytics
operator|.
name|util
package|;
end_package
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
name|Date
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|EnumSet
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
name|java
operator|.
name|util
operator|.
name|Set
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
name|analytics
operator|.
name|request
operator|.
name|RangeFacetRequest
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
name|SolrException
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
name|params
operator|.
name|FacetParams
operator|.
name|FacetRangeInclude
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
name|params
operator|.
name|FacetParams
operator|.
name|FacetRangeOther
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
name|schema
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
name|solr
operator|.
name|schema
operator|.
name|SchemaField
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
name|schema
operator|.
name|TrieDateField
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
name|schema
operator|.
name|TrieField
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
name|util
operator|.
name|DateMathParser
import|;
end_import
begin_class
DECL|class|RangeEndpointCalculator
specifier|public
specifier|abstract
class|class
name|RangeEndpointCalculator
parameter_list|<
name|T
extends|extends
name|Comparable
parameter_list|<
name|T
parameter_list|>
parameter_list|>
block|{
DECL|field|field
specifier|protected
specifier|final
name|SchemaField
name|field
decl_stmt|;
DECL|field|request
specifier|protected
specifier|final
name|RangeFacetRequest
name|request
decl_stmt|;
DECL|method|RangeEndpointCalculator
specifier|public
name|RangeEndpointCalculator
parameter_list|(
specifier|final
name|RangeFacetRequest
name|request
parameter_list|)
block|{
name|this
operator|.
name|field
operator|=
name|request
operator|.
name|getField
argument_list|()
expr_stmt|;
name|this
operator|.
name|request
operator|=
name|request
expr_stmt|;
block|}
comment|/**    * Formats a Range endpoint for use as a range label name in the response.    * Default Impl just uses toString()    */
DECL|method|formatValue
specifier|public
name|String
name|formatValue
parameter_list|(
specifier|final
name|T
name|val
parameter_list|)
block|{
return|return
name|val
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**    * Parses a String param into an Range endpoint value throwing     * a useful exception if not possible    */
DECL|method|getValue
specifier|public
specifier|final
name|T
name|getValue
parameter_list|(
specifier|final
name|String
name|rawval
parameter_list|)
block|{
try|try
block|{
return|return
name|parseVal
argument_list|(
name|rawval
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"Can't parse value "
operator|+
name|rawval
operator|+
literal|" for field: "
operator|+
name|field
operator|.
name|getName
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**    * Parses a String param into an Range endpoint.     * Can throw a low level format exception as needed.    */
DECL|method|parseVal
specifier|protected
specifier|abstract
name|T
name|parseVal
parameter_list|(
specifier|final
name|String
name|rawval
parameter_list|)
throws|throws
name|java
operator|.
name|text
operator|.
name|ParseException
function_decl|;
comment|/**     * Parses a String param into a value that represents the gap and     * can be included in the response, throwing     * a useful exception if not possible.    *    * Note: uses Object as the return type instead of T for things like     * Date where gap is just a DateMathParser string     */
DECL|method|getGap
specifier|public
specifier|final
name|Object
name|getGap
parameter_list|(
specifier|final
name|String
name|gap
parameter_list|)
block|{
try|try
block|{
return|return
name|parseGap
argument_list|(
name|gap
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"Can't parse gap "
operator|+
name|gap
operator|+
literal|" for field: "
operator|+
name|field
operator|.
name|getName
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**    * Parses a String param into a value that represents the gap and     * can be included in the response.     * Can throw a low level format exception as needed.    *    * Default Impl calls parseVal    */
DECL|method|parseGap
specifier|protected
name|Object
name|parseGap
parameter_list|(
specifier|final
name|String
name|rawval
parameter_list|)
throws|throws
name|java
operator|.
name|text
operator|.
name|ParseException
block|{
return|return
name|parseVal
argument_list|(
name|rawval
argument_list|)
return|;
block|}
comment|/**    * Adds the String gap param to a low Range endpoint value to determine     * the corrisponding high Range endpoint value, throwing     * a useful exception if not possible.    */
DECL|method|addGap
specifier|public
specifier|final
name|T
name|addGap
parameter_list|(
name|T
name|value
parameter_list|,
name|String
name|gap
parameter_list|)
block|{
try|try
block|{
return|return
name|parseAndAddGap
argument_list|(
name|value
argument_list|,
name|gap
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"Can't add gap "
operator|+
name|gap
operator|+
literal|" to value "
operator|+
name|value
operator|+
literal|" for field: "
operator|+
name|field
operator|.
name|getName
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**    * Adds the String gap param to a low Range endpoint value to determine     * the corrisponding high Range endpoint value.    * Can throw a low level format exception as needed.    */
DECL|method|parseAndAddGap
specifier|protected
specifier|abstract
name|T
name|parseAndAddGap
parameter_list|(
name|T
name|value
parameter_list|,
name|String
name|gap
parameter_list|)
throws|throws
name|java
operator|.
name|text
operator|.
name|ParseException
function_decl|;
DECL|class|FacetRange
specifier|public
specifier|static
class|class
name|FacetRange
block|{
DECL|field|name
specifier|public
specifier|final
name|String
name|name
decl_stmt|;
DECL|field|lower
specifier|public
specifier|final
name|String
name|lower
decl_stmt|;
DECL|field|upper
specifier|public
specifier|final
name|String
name|upper
decl_stmt|;
DECL|field|includeLower
specifier|public
specifier|final
name|boolean
name|includeLower
decl_stmt|;
DECL|field|includeUpper
specifier|public
specifier|final
name|boolean
name|includeUpper
decl_stmt|;
DECL|method|FacetRange
specifier|public
name|FacetRange
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|lower
parameter_list|,
name|String
name|upper
parameter_list|,
name|boolean
name|includeLower
parameter_list|,
name|boolean
name|includeUpper
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|lower
operator|=
name|lower
expr_stmt|;
name|this
operator|.
name|upper
operator|=
name|upper
expr_stmt|;
name|this
operator|.
name|includeLower
operator|=
name|includeLower
expr_stmt|;
name|this
operator|.
name|includeUpper
operator|=
name|includeUpper
expr_stmt|;
block|}
block|}
DECL|method|getRanges
specifier|public
name|List
argument_list|<
name|FacetRange
argument_list|>
name|getRanges
parameter_list|()
block|{
specifier|final
name|T
name|start
init|=
name|getValue
argument_list|(
name|request
operator|.
name|getStart
argument_list|()
argument_list|)
decl_stmt|;
name|T
name|end
init|=
name|getValue
argument_list|(
name|request
operator|.
name|getEnd
argument_list|()
argument_list|)
decl_stmt|;
comment|// not final, hardend may change this
if|if
condition|(
name|end
operator|.
name|compareTo
argument_list|(
name|start
argument_list|)
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"range facet 'end' comes before 'start': "
operator|+
name|end
operator|+
literal|"< "
operator|+
name|start
argument_list|)
throw|;
block|}
comment|// explicitly return the gap.  compute this early so we are more
comment|// likely to catch parse errors before attempting math
specifier|final
name|String
index|[]
name|gaps
init|=
name|request
operator|.
name|getGaps
argument_list|()
decl_stmt|;
name|String
name|gap
init|=
name|gaps
index|[
literal|0
index|]
decl_stmt|;
specifier|final
name|EnumSet
argument_list|<
name|FacetRangeInclude
argument_list|>
name|include
init|=
name|request
operator|.
name|getInclude
argument_list|()
decl_stmt|;
name|T
name|low
init|=
name|start
decl_stmt|;
name|List
argument_list|<
name|FacetRange
argument_list|>
name|ranges
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|int
name|gapCounter
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|low
operator|.
name|compareTo
argument_list|(
name|end
argument_list|)
operator|<
literal|0
condition|)
block|{
if|if
condition|(
name|gapCounter
operator|<
name|gaps
operator|.
name|length
condition|)
block|{
name|gap
operator|=
name|gaps
index|[
name|gapCounter
operator|++
index|]
expr_stmt|;
block|}
name|T
name|high
init|=
name|addGap
argument_list|(
name|low
argument_list|,
name|gap
argument_list|)
decl_stmt|;
if|if
condition|(
name|end
operator|.
name|compareTo
argument_list|(
name|high
argument_list|)
operator|<
literal|0
condition|)
block|{
if|if
condition|(
name|request
operator|.
name|isHardEnd
argument_list|()
condition|)
block|{
name|high
operator|=
name|end
expr_stmt|;
block|}
else|else
block|{
name|end
operator|=
name|high
expr_stmt|;
block|}
block|}
if|if
condition|(
name|high
operator|.
name|compareTo
argument_list|(
name|low
argument_list|)
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"range facet infinite loop (is gap negative? did the math overflow?)"
argument_list|)
throw|;
block|}
if|if
condition|(
name|high
operator|.
name|compareTo
argument_list|(
name|low
argument_list|)
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"range facet infinite loop: gap is either zero, or too small relative start/end and caused underflow: "
operator|+
name|low
operator|+
literal|" + "
operator|+
name|gap
operator|+
literal|" = "
operator|+
name|high
argument_list|)
throw|;
block|}
specifier|final
name|boolean
name|includeLower
init|=
operator|(
name|include
operator|.
name|contains
argument_list|(
name|FacetRangeInclude
operator|.
name|ALL
argument_list|)
operator|||
name|include
operator|.
name|contains
argument_list|(
name|FacetRangeInclude
operator|.
name|LOWER
argument_list|)
operator|||
operator|(
name|include
operator|.
name|contains
argument_list|(
name|FacetRangeInclude
operator|.
name|EDGE
argument_list|)
operator|&&
literal|0
operator|==
name|low
operator|.
name|compareTo
argument_list|(
name|start
argument_list|)
operator|)
operator|)
decl_stmt|;
specifier|final
name|boolean
name|includeUpper
init|=
operator|(
name|include
operator|.
name|contains
argument_list|(
name|FacetRangeInclude
operator|.
name|ALL
argument_list|)
operator|||
name|include
operator|.
name|contains
argument_list|(
name|FacetRangeInclude
operator|.
name|UPPER
argument_list|)
operator|||
operator|(
name|include
operator|.
name|contains
argument_list|(
name|FacetRangeInclude
operator|.
name|EDGE
argument_list|)
operator|&&
literal|0
operator|==
name|high
operator|.
name|compareTo
argument_list|(
name|end
argument_list|)
operator|)
operator|)
decl_stmt|;
specifier|final
name|String
name|lowS
init|=
name|formatValue
argument_list|(
name|low
argument_list|)
decl_stmt|;
specifier|final
name|String
name|highS
init|=
name|formatValue
argument_list|(
name|high
argument_list|)
decl_stmt|;
name|ranges
operator|.
name|add
argument_list|(
operator|new
name|FacetRange
argument_list|(
name|lowS
argument_list|,
name|lowS
argument_list|,
name|highS
argument_list|,
name|includeLower
argument_list|,
name|includeUpper
argument_list|)
argument_list|)
expr_stmt|;
name|low
operator|=
name|high
expr_stmt|;
block|}
specifier|final
name|Set
argument_list|<
name|FacetRangeOther
argument_list|>
name|others
init|=
name|request
operator|.
name|getOthers
argument_list|()
decl_stmt|;
if|if
condition|(
literal|null
operator|!=
name|others
operator|&&
literal|0
operator|<
name|others
operator|.
name|size
argument_list|()
condition|)
block|{
comment|// no matter what other values are listed, we don't do
comment|// anything if "none" is specified.
if|if
condition|(
operator|!
name|others
operator|.
name|contains
argument_list|(
name|FacetRangeOther
operator|.
name|NONE
argument_list|)
condition|)
block|{
name|boolean
name|all
init|=
name|others
operator|.
name|contains
argument_list|(
name|FacetRangeOther
operator|.
name|ALL
argument_list|)
decl_stmt|;
if|if
condition|(
name|all
operator|||
name|others
operator|.
name|contains
argument_list|(
name|FacetRangeOther
operator|.
name|BEFORE
argument_list|)
condition|)
block|{
comment|// include upper bound if "outer" or if first gap doesn't already include it
name|ranges
operator|.
name|add
argument_list|(
operator|new
name|FacetRange
argument_list|(
name|FacetRangeOther
operator|.
name|BEFORE
operator|.
name|toString
argument_list|()
argument_list|,
literal|null
argument_list|,
name|formatValue
argument_list|(
name|start
argument_list|)
argument_list|,
literal|false
argument_list|,
name|include
operator|.
name|contains
argument_list|(
name|FacetRangeInclude
operator|.
name|OUTER
argument_list|)
operator|||
name|include
operator|.
name|contains
argument_list|(
name|FacetRangeInclude
operator|.
name|ALL
argument_list|)
operator|||
operator|!
operator|(
name|include
operator|.
name|contains
argument_list|(
name|FacetRangeInclude
operator|.
name|LOWER
argument_list|)
operator|||
name|include
operator|.
name|contains
argument_list|(
name|FacetRangeInclude
operator|.
name|EDGE
argument_list|)
operator|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|all
operator|||
name|others
operator|.
name|contains
argument_list|(
name|FacetRangeOther
operator|.
name|AFTER
argument_list|)
condition|)
block|{
comment|// include lower bound if "outer" or if last gap doesn't already include it
name|ranges
operator|.
name|add
argument_list|(
operator|new
name|FacetRange
argument_list|(
name|FacetRangeOther
operator|.
name|AFTER
operator|.
name|toString
argument_list|()
argument_list|,
name|formatValue
argument_list|(
name|end
argument_list|)
argument_list|,
literal|null
argument_list|,
name|include
operator|.
name|contains
argument_list|(
name|FacetRangeInclude
operator|.
name|OUTER
argument_list|)
operator|||
name|include
operator|.
name|contains
argument_list|(
name|FacetRangeInclude
operator|.
name|ALL
argument_list|)
operator|||
operator|!
operator|(
name|include
operator|.
name|contains
argument_list|(
name|FacetRangeInclude
operator|.
name|UPPER
argument_list|)
operator|||
name|include
operator|.
name|contains
argument_list|(
name|FacetRangeInclude
operator|.
name|EDGE
argument_list|)
operator|)
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|all
operator|||
name|others
operator|.
name|contains
argument_list|(
name|FacetRangeOther
operator|.
name|BETWEEN
argument_list|)
condition|)
block|{
name|ranges
operator|.
name|add
argument_list|(
operator|new
name|FacetRange
argument_list|(
name|FacetRangeOther
operator|.
name|BETWEEN
operator|.
name|toString
argument_list|()
argument_list|,
name|formatValue
argument_list|(
name|start
argument_list|)
argument_list|,
name|formatValue
argument_list|(
name|end
argument_list|)
argument_list|,
name|include
operator|.
name|contains
argument_list|(
name|FacetRangeInclude
operator|.
name|LOWER
argument_list|)
operator|||
name|include
operator|.
name|contains
argument_list|(
name|FacetRangeInclude
operator|.
name|EDGE
argument_list|)
operator|||
name|include
operator|.
name|contains
argument_list|(
name|FacetRangeInclude
operator|.
name|ALL
argument_list|)
argument_list|,
name|include
operator|.
name|contains
argument_list|(
name|FacetRangeInclude
operator|.
name|UPPER
argument_list|)
operator|||
name|include
operator|.
name|contains
argument_list|(
name|FacetRangeInclude
operator|.
name|EDGE
argument_list|)
operator|||
name|include
operator|.
name|contains
argument_list|(
name|FacetRangeInclude
operator|.
name|ALL
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|ranges
return|;
block|}
DECL|method|create
specifier|public
specifier|static
name|RangeEndpointCalculator
argument_list|<
name|?
extends|extends
name|Comparable
argument_list|<
name|?
argument_list|>
argument_list|>
name|create
parameter_list|(
name|RangeFacetRequest
name|request
parameter_list|)
block|{
specifier|final
name|SchemaField
name|sf
init|=
name|request
operator|.
name|getField
argument_list|()
decl_stmt|;
specifier|final
name|FieldType
name|ft
init|=
name|sf
operator|.
name|getType
argument_list|()
decl_stmt|;
specifier|final
name|RangeEndpointCalculator
argument_list|<
name|?
argument_list|>
name|calc
decl_stmt|;
if|if
condition|(
name|ft
operator|instanceof
name|TrieField
condition|)
block|{
specifier|final
name|TrieField
name|trie
init|=
operator|(
name|TrieField
operator|)
name|ft
decl_stmt|;
switch|switch
condition|(
name|trie
operator|.
name|getType
argument_list|()
condition|)
block|{
case|case
name|FLOAT
case|:
name|calc
operator|=
operator|new
name|FloatRangeEndpointCalculator
argument_list|(
name|request
argument_list|)
expr_stmt|;
break|break;
case|case
name|DOUBLE
case|:
name|calc
operator|=
operator|new
name|DoubleRangeEndpointCalculator
argument_list|(
name|request
argument_list|)
expr_stmt|;
break|break;
case|case
name|INTEGER
case|:
name|calc
operator|=
operator|new
name|IntegerRangeEndpointCalculator
argument_list|(
name|request
argument_list|)
expr_stmt|;
break|break;
case|case
name|LONG
case|:
name|calc
operator|=
operator|new
name|LongRangeEndpointCalculator
argument_list|(
name|request
argument_list|)
expr_stmt|;
break|break;
case|case
name|DATE
case|:
name|calc
operator|=
operator|new
name|DateRangeEndpointCalculator
argument_list|(
name|request
argument_list|,
literal|null
argument_list|)
expr_stmt|;
break|break;
default|default:
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"Unable to range facet on tried field of unexpected type:"
operator|+
name|sf
operator|.
name|getName
argument_list|()
argument_list|)
throw|;
block|}
block|}
else|else
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"Unable to range facet on field:"
operator|+
name|sf
argument_list|)
throw|;
block|}
return|return
name|calc
return|;
block|}
DECL|class|FloatRangeEndpointCalculator
specifier|public
specifier|static
class|class
name|FloatRangeEndpointCalculator
extends|extends
name|RangeEndpointCalculator
argument_list|<
name|Float
argument_list|>
block|{
DECL|method|FloatRangeEndpointCalculator
specifier|public
name|FloatRangeEndpointCalculator
parameter_list|(
specifier|final
name|RangeFacetRequest
name|request
parameter_list|)
block|{
name|super
argument_list|(
name|request
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|parseVal
specifier|protected
name|Float
name|parseVal
parameter_list|(
name|String
name|rawval
parameter_list|)
block|{
return|return
name|Float
operator|.
name|valueOf
argument_list|(
name|rawval
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|parseAndAddGap
specifier|public
name|Float
name|parseAndAddGap
parameter_list|(
name|Float
name|value
parameter_list|,
name|String
name|gap
parameter_list|)
block|{
return|return
operator|new
name|Float
argument_list|(
name|value
operator|.
name|floatValue
argument_list|()
operator|+
name|Float
operator|.
name|valueOf
argument_list|(
name|gap
argument_list|)
operator|.
name|floatValue
argument_list|()
argument_list|)
return|;
block|}
block|}
DECL|class|DoubleRangeEndpointCalculator
specifier|public
specifier|static
class|class
name|DoubleRangeEndpointCalculator
extends|extends
name|RangeEndpointCalculator
argument_list|<
name|Double
argument_list|>
block|{
DECL|method|DoubleRangeEndpointCalculator
specifier|public
name|DoubleRangeEndpointCalculator
parameter_list|(
specifier|final
name|RangeFacetRequest
name|request
parameter_list|)
block|{
name|super
argument_list|(
name|request
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|parseVal
specifier|protected
name|Double
name|parseVal
parameter_list|(
name|String
name|rawval
parameter_list|)
block|{
return|return
name|Double
operator|.
name|valueOf
argument_list|(
name|rawval
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|parseAndAddGap
specifier|public
name|Double
name|parseAndAddGap
parameter_list|(
name|Double
name|value
parameter_list|,
name|String
name|gap
parameter_list|)
block|{
return|return
operator|new
name|Double
argument_list|(
name|value
operator|.
name|doubleValue
argument_list|()
operator|+
name|Double
operator|.
name|valueOf
argument_list|(
name|gap
argument_list|)
operator|.
name|doubleValue
argument_list|()
argument_list|)
return|;
block|}
block|}
DECL|class|IntegerRangeEndpointCalculator
specifier|public
specifier|static
class|class
name|IntegerRangeEndpointCalculator
extends|extends
name|RangeEndpointCalculator
argument_list|<
name|Integer
argument_list|>
block|{
DECL|method|IntegerRangeEndpointCalculator
specifier|public
name|IntegerRangeEndpointCalculator
parameter_list|(
specifier|final
name|RangeFacetRequest
name|request
parameter_list|)
block|{
name|super
argument_list|(
name|request
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|parseVal
specifier|protected
name|Integer
name|parseVal
parameter_list|(
name|String
name|rawval
parameter_list|)
block|{
return|return
name|Integer
operator|.
name|valueOf
argument_list|(
name|rawval
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|parseAndAddGap
specifier|public
name|Integer
name|parseAndAddGap
parameter_list|(
name|Integer
name|value
parameter_list|,
name|String
name|gap
parameter_list|)
block|{
return|return
operator|new
name|Integer
argument_list|(
name|value
operator|.
name|intValue
argument_list|()
operator|+
name|Integer
operator|.
name|valueOf
argument_list|(
name|gap
argument_list|)
operator|.
name|intValue
argument_list|()
argument_list|)
return|;
block|}
block|}
DECL|class|LongRangeEndpointCalculator
specifier|public
specifier|static
class|class
name|LongRangeEndpointCalculator
extends|extends
name|RangeEndpointCalculator
argument_list|<
name|Long
argument_list|>
block|{
DECL|method|LongRangeEndpointCalculator
specifier|public
name|LongRangeEndpointCalculator
parameter_list|(
specifier|final
name|RangeFacetRequest
name|request
parameter_list|)
block|{
name|super
argument_list|(
name|request
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|parseVal
specifier|protected
name|Long
name|parseVal
parameter_list|(
name|String
name|rawval
parameter_list|)
block|{
return|return
name|Long
operator|.
name|valueOf
argument_list|(
name|rawval
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|parseAndAddGap
specifier|public
name|Long
name|parseAndAddGap
parameter_list|(
name|Long
name|value
parameter_list|,
name|String
name|gap
parameter_list|)
block|{
return|return
operator|new
name|Long
argument_list|(
name|value
operator|.
name|longValue
argument_list|()
operator|+
name|Long
operator|.
name|valueOf
argument_list|(
name|gap
argument_list|)
operator|.
name|longValue
argument_list|()
argument_list|)
return|;
block|}
block|}
DECL|class|DateRangeEndpointCalculator
specifier|public
specifier|static
class|class
name|DateRangeEndpointCalculator
extends|extends
name|RangeEndpointCalculator
argument_list|<
name|Date
argument_list|>
block|{
DECL|field|now
specifier|private
specifier|final
name|Date
name|now
decl_stmt|;
DECL|method|DateRangeEndpointCalculator
specifier|public
name|DateRangeEndpointCalculator
parameter_list|(
specifier|final
name|RangeFacetRequest
name|request
parameter_list|,
specifier|final
name|Date
name|now
parameter_list|)
block|{
name|super
argument_list|(
name|request
argument_list|)
expr_stmt|;
name|this
operator|.
name|now
operator|=
name|now
expr_stmt|;
if|if
condition|(
operator|!
operator|(
name|field
operator|.
name|getType
argument_list|()
operator|instanceof
name|TrieDateField
operator|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"SchemaField must use field type extending TrieDateField"
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|formatValue
specifier|public
name|String
name|formatValue
parameter_list|(
name|Date
name|val
parameter_list|)
block|{
return|return
name|val
operator|.
name|toInstant
argument_list|()
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|parseVal
specifier|protected
name|Date
name|parseVal
parameter_list|(
name|String
name|rawval
parameter_list|)
block|{
return|return
name|DateMathParser
operator|.
name|parseMath
argument_list|(
name|now
argument_list|,
name|rawval
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|parseGap
specifier|protected
name|Object
name|parseGap
parameter_list|(
specifier|final
name|String
name|rawval
parameter_list|)
block|{
return|return
name|rawval
return|;
block|}
annotation|@
name|Override
DECL|method|parseAndAddGap
specifier|public
name|Date
name|parseAndAddGap
parameter_list|(
name|Date
name|value
parameter_list|,
name|String
name|gap
parameter_list|)
throws|throws
name|java
operator|.
name|text
operator|.
name|ParseException
block|{
specifier|final
name|DateMathParser
name|dmp
init|=
operator|new
name|DateMathParser
argument_list|()
decl_stmt|;
name|dmp
operator|.
name|setNow
argument_list|(
name|value
argument_list|)
expr_stmt|;
return|return
name|dmp
operator|.
name|parseMath
argument_list|(
name|gap
argument_list|)
return|;
block|}
block|}
block|}
end_class
end_unit
