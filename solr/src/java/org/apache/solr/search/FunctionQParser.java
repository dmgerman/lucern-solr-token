begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.search
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
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
name|queries
operator|.
name|function
operator|.
name|FunctionQuery
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
name|queryParser
operator|.
name|ParseException
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
name|Query
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
name|SolrParams
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
name|search
operator|.
name|function
operator|.
name|*
import|;
end_import
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
begin_class
DECL|class|FunctionQParser
specifier|public
class|class
name|FunctionQParser
extends|extends
name|QParser
block|{
comment|/** @lucene.internal */
DECL|field|sp
specifier|public
name|QueryParsing
operator|.
name|StrParser
name|sp
decl_stmt|;
DECL|field|parseMultipleSources
name|boolean
name|parseMultipleSources
init|=
literal|true
decl_stmt|;
DECL|field|parseToEnd
name|boolean
name|parseToEnd
init|=
literal|true
decl_stmt|;
DECL|method|FunctionQParser
specifier|public
name|FunctionQParser
parameter_list|(
name|String
name|qstr
parameter_list|,
name|SolrParams
name|localParams
parameter_list|,
name|SolrParams
name|params
parameter_list|,
name|SolrQueryRequest
name|req
parameter_list|)
block|{
name|super
argument_list|(
name|qstr
argument_list|,
name|localParams
argument_list|,
name|params
argument_list|,
name|req
argument_list|)
expr_stmt|;
block|}
DECL|method|setParseMultipleSources
specifier|public
name|void
name|setParseMultipleSources
parameter_list|(
name|boolean
name|parseMultipleSources
parameter_list|)
block|{
name|this
operator|.
name|parseMultipleSources
operator|=
name|parseMultipleSources
expr_stmt|;
block|}
comment|/** parse multiple comma separated value sources */
DECL|method|getParseMultipleSources
specifier|public
name|boolean
name|getParseMultipleSources
parameter_list|()
block|{
return|return
name|parseMultipleSources
return|;
block|}
DECL|method|setParseToEnd
specifier|public
name|void
name|setParseToEnd
parameter_list|(
name|boolean
name|parseToEnd
parameter_list|)
block|{
name|this
operator|.
name|parseToEnd
operator|=
name|parseToEnd
expr_stmt|;
block|}
comment|/** throw exception if there is extra stuff at the end of the parsed valuesource(s). */
DECL|method|getParseToEnd
specifier|public
name|boolean
name|getParseToEnd
parameter_list|()
block|{
return|return
name|parseMultipleSources
return|;
block|}
annotation|@
name|Override
DECL|method|parse
specifier|public
name|Query
name|parse
parameter_list|()
throws|throws
name|ParseException
block|{
name|sp
operator|=
operator|new
name|QueryParsing
operator|.
name|StrParser
argument_list|(
name|getString
argument_list|()
argument_list|)
expr_stmt|;
name|ValueSource
name|vs
init|=
literal|null
decl_stmt|;
name|List
argument_list|<
name|ValueSource
argument_list|>
name|lst
init|=
literal|null
decl_stmt|;
for|for
control|(
init|;
condition|;
control|)
block|{
name|ValueSource
name|valsource
init|=
name|parseValueSource
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|sp
operator|.
name|eatws
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|parseMultipleSources
condition|)
block|{
name|vs
operator|=
name|valsource
expr_stmt|;
break|break;
block|}
else|else
block|{
if|if
condition|(
name|lst
operator|!=
literal|null
condition|)
block|{
name|lst
operator|.
name|add
argument_list|(
name|valsource
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|vs
operator|=
name|valsource
expr_stmt|;
block|}
block|}
comment|// check if there is a "," separator
if|if
condition|(
name|sp
operator|.
name|peek
argument_list|()
operator|!=
literal|','
condition|)
break|break;
name|consumeArgumentDelimiter
argument_list|()
expr_stmt|;
if|if
condition|(
name|lst
operator|==
literal|null
condition|)
block|{
name|lst
operator|=
operator|new
name|ArrayList
argument_list|<
name|ValueSource
argument_list|>
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|lst
operator|.
name|add
argument_list|(
name|valsource
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|parseToEnd
operator|&&
name|sp
operator|.
name|pos
operator|<
name|sp
operator|.
name|end
condition|)
block|{
throw|throw
operator|new
name|ParseException
argument_list|(
literal|"Unexpected text after function: "
operator|+
name|sp
operator|.
name|val
operator|.
name|substring
argument_list|(
name|sp
operator|.
name|pos
argument_list|,
name|sp
operator|.
name|end
argument_list|)
argument_list|)
throw|;
block|}
if|if
condition|(
name|lst
operator|!=
literal|null
condition|)
block|{
name|vs
operator|=
operator|new
name|VectorValueSource
argument_list|(
name|lst
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|FunctionQuery
argument_list|(
name|vs
argument_list|)
return|;
block|}
comment|/**    * Are there more arguments in the argument list being parsed?    *     * @return whether more args exist    * @throws ParseException    */
DECL|method|hasMoreArguments
specifier|public
name|boolean
name|hasMoreArguments
parameter_list|()
throws|throws
name|ParseException
block|{
name|int
name|ch
init|=
name|sp
operator|.
name|peek
argument_list|()
decl_stmt|;
comment|/* determine whether the function is ending with a paren or end of str */
return|return
operator|(
operator|!
operator|(
name|ch
operator|==
literal|0
operator|||
name|ch
operator|==
literal|')'
operator|)
operator|)
return|;
block|}
comment|/**    * TODO: Doc    *     * @throws ParseException    */
DECL|method|parseId
specifier|public
name|String
name|parseId
parameter_list|()
throws|throws
name|ParseException
block|{
name|String
name|value
init|=
name|parseArg
argument_list|()
decl_stmt|;
if|if
condition|(
name|argWasQuoted
condition|)
throw|throw
operator|new
name|ParseException
argument_list|(
literal|"Expected identifier instead of quoted string:"
operator|+
name|value
argument_list|)
throw|;
return|return
name|value
return|;
block|}
comment|/**    * Parse a float.    *     * @return Float    * @throws ParseException    */
DECL|method|parseFloat
specifier|public
name|Float
name|parseFloat
parameter_list|()
throws|throws
name|ParseException
block|{
name|String
name|str
init|=
name|parseArg
argument_list|()
decl_stmt|;
if|if
condition|(
name|argWasQuoted
argument_list|()
condition|)
throw|throw
operator|new
name|ParseException
argument_list|(
literal|"Expected float instead of quoted string:"
operator|+
name|str
argument_list|)
throw|;
name|float
name|value
init|=
name|Float
operator|.
name|parseFloat
argument_list|(
name|str
argument_list|)
decl_stmt|;
return|return
name|value
return|;
block|}
comment|/**    * Parse a Double    * @return double    * @throws ParseException    */
DECL|method|parseDouble
specifier|public
name|double
name|parseDouble
parameter_list|()
throws|throws
name|ParseException
block|{
name|String
name|str
init|=
name|parseArg
argument_list|()
decl_stmt|;
if|if
condition|(
name|argWasQuoted
argument_list|()
condition|)
throw|throw
operator|new
name|ParseException
argument_list|(
literal|"Expected double instead of quoted string:"
operator|+
name|str
argument_list|)
throw|;
name|double
name|value
init|=
name|Double
operator|.
name|parseDouble
argument_list|(
name|str
argument_list|)
decl_stmt|;
return|return
name|value
return|;
block|}
comment|/**    * Parse an integer    * @return An int    * @throws ParseException    */
DECL|method|parseInt
specifier|public
name|int
name|parseInt
parameter_list|()
throws|throws
name|ParseException
block|{
name|String
name|str
init|=
name|parseArg
argument_list|()
decl_stmt|;
if|if
condition|(
name|argWasQuoted
argument_list|()
condition|)
throw|throw
operator|new
name|ParseException
argument_list|(
literal|"Expected double instead of quoted string:"
operator|+
name|str
argument_list|)
throw|;
name|int
name|value
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|str
argument_list|)
decl_stmt|;
return|return
name|value
return|;
block|}
DECL|field|argWasQuoted
specifier|private
name|boolean
name|argWasQuoted
decl_stmt|;
DECL|method|argWasQuoted
specifier|public
name|boolean
name|argWasQuoted
parameter_list|()
block|{
return|return
name|argWasQuoted
return|;
block|}
DECL|method|parseArg
specifier|public
name|String
name|parseArg
parameter_list|()
throws|throws
name|ParseException
block|{
name|argWasQuoted
operator|=
literal|false
expr_stmt|;
name|sp
operator|.
name|eatws
argument_list|()
expr_stmt|;
name|char
name|ch
init|=
name|sp
operator|.
name|peek
argument_list|()
decl_stmt|;
name|String
name|val
init|=
literal|null
decl_stmt|;
switch|switch
condition|(
name|ch
condition|)
block|{
case|case
literal|')'
case|:
return|return
literal|null
return|;
case|case
literal|'$'
case|:
name|sp
operator|.
name|pos
operator|++
expr_stmt|;
name|String
name|param
init|=
name|sp
operator|.
name|getId
argument_list|()
decl_stmt|;
name|val
operator|=
name|getParam
argument_list|(
name|param
argument_list|)
expr_stmt|;
break|break;
case|case
literal|'\''
case|:
case|case
literal|'"'
case|:
name|val
operator|=
name|sp
operator|.
name|getQuotedString
argument_list|()
expr_stmt|;
name|argWasQuoted
operator|=
literal|true
expr_stmt|;
break|break;
default|default:
comment|// read unquoted literal ended by whitespace ',' or ')'
comment|// there is no escaping.
name|int
name|valStart
init|=
name|sp
operator|.
name|pos
decl_stmt|;
for|for
control|(
init|;
condition|;
control|)
block|{
if|if
condition|(
name|sp
operator|.
name|pos
operator|>=
name|sp
operator|.
name|end
condition|)
block|{
throw|throw
operator|new
name|ParseException
argument_list|(
literal|"Missing end to unquoted value starting at "
operator|+
name|valStart
operator|+
literal|" str='"
operator|+
name|sp
operator|.
name|val
operator|+
literal|"'"
argument_list|)
throw|;
block|}
name|char
name|c
init|=
name|sp
operator|.
name|val
operator|.
name|charAt
argument_list|(
name|sp
operator|.
name|pos
argument_list|)
decl_stmt|;
if|if
condition|(
name|c
operator|==
literal|')'
operator|||
name|c
operator|==
literal|','
operator|||
name|Character
operator|.
name|isWhitespace
argument_list|(
name|c
argument_list|)
condition|)
block|{
name|val
operator|=
name|sp
operator|.
name|val
operator|.
name|substring
argument_list|(
name|valStart
argument_list|,
name|sp
operator|.
name|pos
argument_list|)
expr_stmt|;
break|break;
block|}
name|sp
operator|.
name|pos
operator|++
expr_stmt|;
block|}
block|}
name|sp
operator|.
name|eatws
argument_list|()
expr_stmt|;
name|consumeArgumentDelimiter
argument_list|()
expr_stmt|;
return|return
name|val
return|;
block|}
comment|/**    * Parse a list of ValueSource.  Must be the final set of arguments    * to a ValueSource.    *     * @return List<ValueSource>    * @throws ParseException    */
DECL|method|parseValueSourceList
specifier|public
name|List
argument_list|<
name|ValueSource
argument_list|>
name|parseValueSourceList
parameter_list|()
throws|throws
name|ParseException
block|{
name|List
argument_list|<
name|ValueSource
argument_list|>
name|sources
init|=
operator|new
name|ArrayList
argument_list|<
name|ValueSource
argument_list|>
argument_list|(
literal|3
argument_list|)
decl_stmt|;
while|while
condition|(
name|hasMoreArguments
argument_list|()
condition|)
block|{
name|sources
operator|.
name|add
argument_list|(
name|parseValueSource
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|sources
return|;
block|}
comment|/**    * Parse an individual ValueSource.    *     * @throws ParseException    */
DECL|method|parseValueSource
specifier|public
name|ValueSource
name|parseValueSource
parameter_list|()
throws|throws
name|ParseException
block|{
comment|/* consume the delimiter afterward for an external call to parseValueSource */
return|return
name|parseValueSource
argument_list|(
literal|true
argument_list|)
return|;
block|}
comment|/**    * TODO: Doc    *     * @throws ParseException    */
DECL|method|parseNestedQuery
specifier|public
name|Query
name|parseNestedQuery
parameter_list|()
throws|throws
name|ParseException
block|{
name|Query
name|nestedQuery
decl_stmt|;
if|if
condition|(
name|sp
operator|.
name|opt
argument_list|(
literal|"$"
argument_list|)
condition|)
block|{
name|String
name|param
init|=
name|sp
operator|.
name|getId
argument_list|()
decl_stmt|;
name|String
name|qstr
init|=
name|getParam
argument_list|(
name|param
argument_list|)
decl_stmt|;
name|qstr
operator|=
name|qstr
operator|==
literal|null
condition|?
literal|""
else|:
name|qstr
expr_stmt|;
name|nestedQuery
operator|=
name|subQuery
argument_list|(
name|qstr
argument_list|,
literal|null
argument_list|)
operator|.
name|getQuery
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|int
name|start
init|=
name|sp
operator|.
name|pos
decl_stmt|;
name|String
name|v
init|=
name|sp
operator|.
name|val
decl_stmt|;
name|String
name|qs
init|=
name|v
decl_stmt|;
name|HashMap
name|nestedLocalParams
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|int
name|end
init|=
name|QueryParsing
operator|.
name|parseLocalParams
argument_list|(
name|qs
argument_list|,
name|start
argument_list|,
name|nestedLocalParams
argument_list|,
name|getParams
argument_list|()
argument_list|)
decl_stmt|;
name|QParser
name|sub
decl_stmt|;
if|if
condition|(
name|end
operator|>
name|start
condition|)
block|{
if|if
condition|(
name|nestedLocalParams
operator|.
name|get
argument_list|(
name|QueryParsing
operator|.
name|V
argument_list|)
operator|!=
literal|null
condition|)
block|{
comment|// value specified directly in local params... so the end of the
comment|// query should be the end of the local params.
name|sub
operator|=
name|subQuery
argument_list|(
name|qs
operator|.
name|substring
argument_list|(
name|start
argument_list|,
name|end
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// value here is *after* the local params... ask the parser.
name|sub
operator|=
name|subQuery
argument_list|(
name|qs
argument_list|,
literal|null
argument_list|)
expr_stmt|;
comment|// int subEnd = sub.findEnd(')');
comment|// TODO.. implement functions to find the end of a nested query
throw|throw
operator|new
name|ParseException
argument_list|(
literal|"Nested local params must have value in v parameter.  got '"
operator|+
name|qs
operator|+
literal|"'"
argument_list|)
throw|;
block|}
block|}
else|else
block|{
throw|throw
operator|new
name|ParseException
argument_list|(
literal|"Nested function query must use $param or {!v=value} forms. got '"
operator|+
name|qs
operator|+
literal|"'"
argument_list|)
throw|;
block|}
name|sp
operator|.
name|pos
operator|+=
name|end
operator|-
name|start
expr_stmt|;
comment|// advance past nested query
name|nestedQuery
operator|=
name|sub
operator|.
name|getQuery
argument_list|()
expr_stmt|;
block|}
name|consumeArgumentDelimiter
argument_list|()
expr_stmt|;
return|return
name|nestedQuery
return|;
block|}
comment|/**    * Parse an individual value source.    *     * @param doConsumeDelimiter whether to consume a delimiter following the ValueSource      * @throws ParseException    */
DECL|method|parseValueSource
specifier|protected
name|ValueSource
name|parseValueSource
parameter_list|(
name|boolean
name|doConsumeDelimiter
parameter_list|)
throws|throws
name|ParseException
block|{
name|ValueSource
name|valueSource
decl_stmt|;
name|int
name|ch
init|=
name|sp
operator|.
name|peek
argument_list|()
decl_stmt|;
if|if
condition|(
name|ch
operator|>=
literal|'0'
operator|&&
name|ch
operator|<=
literal|'9'
operator|||
name|ch
operator|==
literal|'.'
operator|||
name|ch
operator|==
literal|'+'
operator|||
name|ch
operator|==
literal|'-'
condition|)
block|{
name|Number
name|num
init|=
name|sp
operator|.
name|getNumber
argument_list|()
decl_stmt|;
if|if
condition|(
name|num
operator|instanceof
name|Long
condition|)
block|{
name|valueSource
operator|=
operator|new
name|LongConstValueSource
argument_list|(
name|num
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|num
operator|instanceof
name|Double
condition|)
block|{
name|valueSource
operator|=
operator|new
name|DoubleConstValueSource
argument_list|(
name|num
operator|.
name|doubleValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// shouldn't happen
name|valueSource
operator|=
operator|new
name|ConstValueSource
argument_list|(
name|num
operator|.
name|floatValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|ch
operator|==
literal|'"'
operator|||
name|ch
operator|==
literal|'\''
condition|)
block|{
name|valueSource
operator|=
operator|new
name|LiteralValueSource
argument_list|(
name|sp
operator|.
name|getQuotedString
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|ch
operator|==
literal|'$'
condition|)
block|{
name|sp
operator|.
name|pos
operator|++
expr_stmt|;
name|String
name|param
init|=
name|sp
operator|.
name|getId
argument_list|()
decl_stmt|;
name|String
name|val
init|=
name|getParam
argument_list|(
name|param
argument_list|)
decl_stmt|;
if|if
condition|(
name|val
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|ParseException
argument_list|(
literal|"Missing param "
operator|+
name|param
operator|+
literal|" while parsing function '"
operator|+
name|sp
operator|.
name|val
operator|+
literal|"'"
argument_list|)
throw|;
block|}
name|QParser
name|subParser
init|=
name|subQuery
argument_list|(
name|val
argument_list|,
literal|"func"
argument_list|)
decl_stmt|;
if|if
condition|(
name|subParser
operator|instanceof
name|FunctionQParser
condition|)
block|{
operator|(
operator|(
name|FunctionQParser
operator|)
name|subParser
operator|)
operator|.
name|setParseMultipleSources
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
name|Query
name|subQuery
init|=
name|subParser
operator|.
name|getQuery
argument_list|()
decl_stmt|;
if|if
condition|(
name|subQuery
operator|instanceof
name|FunctionQuery
condition|)
block|{
name|valueSource
operator|=
operator|(
operator|(
name|FunctionQuery
operator|)
name|subQuery
operator|)
operator|.
name|getValueSource
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|valueSource
operator|=
operator|new
name|QueryValueSource
argument_list|(
name|subQuery
argument_list|,
literal|0.0f
argument_list|)
expr_stmt|;
block|}
comment|/***        // dereference *simple* argument (i.e., can't currently be a function)        // In the future we could support full function dereferencing via a stack of ValueSource (or StringParser) objects       ch = val.length()==0 ? '\0' : val.charAt(0);        if (ch>='0'&& ch<='9'  || ch=='.' || ch=='+' || ch=='-') {         QueryParsing.StrParser sp = new QueryParsing.StrParser(val);         Number num = sp.getNumber();         if (num instanceof Long) {           valueSource = new LongConstValueSource(num.longValue());         } else if (num instanceof Double) {           valueSource = new DoubleConstValueSource(num.doubleValue());         } else {           // shouldn't happen           valueSource = new ConstValueSource(num.floatValue());         }       } else if (ch == '"' || ch == '\'') {         QueryParsing.StrParser sp = new QueryParsing.StrParser(val);         val = sp.getQuotedString();         valueSource = new LiteralValueSource(val);       } else {         if (val.length()==0) {           valueSource = new LiteralValueSource(val);         } else {           String id = val;           SchemaField f = req.getSchema().getField(id);           valueSource = f.getType().getValueSource(f, this);         }       }        ***/
block|}
else|else
block|{
name|String
name|id
init|=
name|sp
operator|.
name|getId
argument_list|()
decl_stmt|;
if|if
condition|(
name|sp
operator|.
name|opt
argument_list|(
literal|"("
argument_list|)
condition|)
block|{
comment|// a function... look it up.
name|ValueSourceParser
name|argParser
init|=
name|req
operator|.
name|getCore
argument_list|()
operator|.
name|getValueSourceParser
argument_list|(
name|id
argument_list|)
decl_stmt|;
if|if
condition|(
name|argParser
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|ParseException
argument_list|(
literal|"Unknown function "
operator|+
name|id
operator|+
literal|" in FunctionQuery("
operator|+
name|sp
operator|+
literal|")"
argument_list|)
throw|;
block|}
name|valueSource
operator|=
name|argParser
operator|.
name|parse
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|sp
operator|.
name|expect
argument_list|(
literal|")"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
literal|"true"
operator|.
name|equals
argument_list|(
name|id
argument_list|)
condition|)
block|{
name|valueSource
operator|=
operator|new
name|BoolConstValueSource
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"false"
operator|.
name|equals
argument_list|(
name|id
argument_list|)
condition|)
block|{
name|valueSource
operator|=
operator|new
name|BoolConstValueSource
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|SchemaField
name|f
init|=
name|req
operator|.
name|getSchema
argument_list|()
operator|.
name|getField
argument_list|(
name|id
argument_list|)
decl_stmt|;
name|valueSource
operator|=
name|f
operator|.
name|getType
argument_list|()
operator|.
name|getValueSource
argument_list|(
name|f
argument_list|,
name|this
argument_list|)
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|doConsumeDelimiter
condition|)
name|consumeArgumentDelimiter
argument_list|()
expr_stmt|;
return|return
name|valueSource
return|;
block|}
comment|/**    * Consume an argument delimiter (a comma) from the token stream.    * Only consumes if more arguments should exist (no ending parens or end of string).    *     * @return whether a delimiter was consumed    * @throws ParseException    */
DECL|method|consumeArgumentDelimiter
specifier|protected
name|boolean
name|consumeArgumentDelimiter
parameter_list|()
throws|throws
name|ParseException
block|{
comment|/* if a list of args is ending, don't expect the comma */
if|if
condition|(
name|hasMoreArguments
argument_list|()
condition|)
block|{
name|sp
operator|.
name|expect
argument_list|(
literal|","
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
block|}
end_class
end_unit
