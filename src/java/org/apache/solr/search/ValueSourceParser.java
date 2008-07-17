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
name|search
operator|.
name|function
operator|.
name|BoostedQuery
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
name|DivFloatFunction
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
name|DocValues
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
name|LinearFloatFunction
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
name|MaxFloatFunction
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
name|OrdFieldSource
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
name|PowFloatFunction
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
name|ProductFloatFunction
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
name|QueryValueSource
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
name|RangeMapFloatFunction
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
name|ReciprocalFloatFunction
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
name|ReverseOrdFieldSource
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
name|ScaleFloatFunction
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
name|SimpleFloatFunction
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
name|SumFloatFunction
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
name|util
operator|.
name|plugin
operator|.
name|NamedListInitializedPlugin
import|;
end_import
begin_comment
comment|/**  * A factory that parses user queries to generate ValueSource instances.  * Intented usage is to create pluggable, named functions for use in function queries.  */
end_comment
begin_class
DECL|class|ValueSourceParser
specifier|public
specifier|abstract
class|class
name|ValueSourceParser
implements|implements
name|NamedListInitializedPlugin
block|{
comment|/**    * Initialize the plugin.    */
DECL|method|init
specifier|public
specifier|abstract
name|void
name|init
parameter_list|(
name|NamedList
name|args
parameter_list|)
function_decl|;
comment|/**    * Parse the user input into a ValueSource.    *     * @param fp    * @throws ParseException    */
DECL|method|parse
specifier|public
specifier|abstract
name|ValueSource
name|parse
parameter_list|(
name|FunctionQParser
name|fp
parameter_list|)
throws|throws
name|ParseException
function_decl|;
comment|/* standard functions */
DECL|field|standardValueSourceParsers
specifier|public
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|ValueSourceParser
argument_list|>
name|standardValueSourceParsers
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|ValueSourceParser
argument_list|>
argument_list|()
decl_stmt|;
static|static
block|{
name|standardValueSourceParsers
operator|.
name|put
argument_list|(
literal|"ord"
argument_list|,
operator|new
name|ValueSourceParser
argument_list|()
block|{
specifier|public
name|ValueSource
name|parse
parameter_list|(
name|FunctionQParser
name|fp
parameter_list|)
throws|throws
name|ParseException
block|{
name|String
name|field
init|=
name|fp
operator|.
name|parseId
argument_list|()
decl_stmt|;
return|return
operator|new
name|OrdFieldSource
argument_list|(
name|field
argument_list|)
return|;
block|}
specifier|public
name|void
name|init
parameter_list|(
name|NamedList
name|args
parameter_list|)
block|{       }
block|}
argument_list|)
expr_stmt|;
name|standardValueSourceParsers
operator|.
name|put
argument_list|(
literal|"rord"
argument_list|,
operator|new
name|ValueSourceParser
argument_list|()
block|{
specifier|public
name|ValueSource
name|parse
parameter_list|(
name|FunctionQParser
name|fp
parameter_list|)
throws|throws
name|ParseException
block|{
name|String
name|field
init|=
name|fp
operator|.
name|parseId
argument_list|()
decl_stmt|;
return|return
operator|new
name|ReverseOrdFieldSource
argument_list|(
name|field
argument_list|)
return|;
block|}
specifier|public
name|void
name|init
parameter_list|(
name|NamedList
name|args
parameter_list|)
block|{       }
block|}
argument_list|)
expr_stmt|;
name|standardValueSourceParsers
operator|.
name|put
argument_list|(
literal|"linear"
argument_list|,
operator|new
name|ValueSourceParser
argument_list|()
block|{
specifier|public
name|ValueSource
name|parse
parameter_list|(
name|FunctionQParser
name|fp
parameter_list|)
throws|throws
name|ParseException
block|{
name|ValueSource
name|source
init|=
name|fp
operator|.
name|parseValueSource
argument_list|()
decl_stmt|;
name|float
name|slope
init|=
name|fp
operator|.
name|parseFloat
argument_list|()
decl_stmt|;
name|float
name|intercept
init|=
name|fp
operator|.
name|parseFloat
argument_list|()
decl_stmt|;
return|return
operator|new
name|LinearFloatFunction
argument_list|(
name|source
argument_list|,
name|slope
argument_list|,
name|intercept
argument_list|)
return|;
block|}
specifier|public
name|void
name|init
parameter_list|(
name|NamedList
name|args
parameter_list|)
block|{       }
block|}
argument_list|)
expr_stmt|;
name|standardValueSourceParsers
operator|.
name|put
argument_list|(
literal|"max"
argument_list|,
operator|new
name|ValueSourceParser
argument_list|()
block|{
specifier|public
name|ValueSource
name|parse
parameter_list|(
name|FunctionQParser
name|fp
parameter_list|)
throws|throws
name|ParseException
block|{
name|ValueSource
name|source
init|=
name|fp
operator|.
name|parseValueSource
argument_list|()
decl_stmt|;
name|float
name|val
init|=
name|fp
operator|.
name|parseFloat
argument_list|()
decl_stmt|;
return|return
operator|new
name|MaxFloatFunction
argument_list|(
name|source
argument_list|,
name|val
argument_list|)
return|;
block|}
specifier|public
name|void
name|init
parameter_list|(
name|NamedList
name|args
parameter_list|)
block|{       }
block|}
argument_list|)
expr_stmt|;
name|standardValueSourceParsers
operator|.
name|put
argument_list|(
literal|"recip"
argument_list|,
operator|new
name|ValueSourceParser
argument_list|()
block|{
specifier|public
name|ValueSource
name|parse
parameter_list|(
name|FunctionQParser
name|fp
parameter_list|)
throws|throws
name|ParseException
block|{
name|ValueSource
name|source
init|=
name|fp
operator|.
name|parseValueSource
argument_list|()
decl_stmt|;
name|float
name|m
init|=
name|fp
operator|.
name|parseFloat
argument_list|()
decl_stmt|;
name|float
name|a
init|=
name|fp
operator|.
name|parseFloat
argument_list|()
decl_stmt|;
name|float
name|b
init|=
name|fp
operator|.
name|parseFloat
argument_list|()
decl_stmt|;
return|return
operator|new
name|ReciprocalFloatFunction
argument_list|(
name|source
argument_list|,
name|m
argument_list|,
name|a
argument_list|,
name|b
argument_list|)
return|;
block|}
specifier|public
name|void
name|init
parameter_list|(
name|NamedList
name|args
parameter_list|)
block|{       }
block|}
argument_list|)
expr_stmt|;
name|standardValueSourceParsers
operator|.
name|put
argument_list|(
literal|"scale"
argument_list|,
operator|new
name|ValueSourceParser
argument_list|()
block|{
specifier|public
name|ValueSource
name|parse
parameter_list|(
name|FunctionQParser
name|fp
parameter_list|)
throws|throws
name|ParseException
block|{
name|ValueSource
name|source
init|=
name|fp
operator|.
name|parseValueSource
argument_list|()
decl_stmt|;
name|float
name|min
init|=
name|fp
operator|.
name|parseFloat
argument_list|()
decl_stmt|;
name|float
name|max
init|=
name|fp
operator|.
name|parseFloat
argument_list|()
decl_stmt|;
return|return
operator|new
name|ScaleFloatFunction
argument_list|(
name|source
argument_list|,
name|min
argument_list|,
name|max
argument_list|)
return|;
block|}
specifier|public
name|void
name|init
parameter_list|(
name|NamedList
name|args
parameter_list|)
block|{       }
block|}
argument_list|)
expr_stmt|;
name|standardValueSourceParsers
operator|.
name|put
argument_list|(
literal|"pow"
argument_list|,
operator|new
name|ValueSourceParser
argument_list|()
block|{
specifier|public
name|ValueSource
name|parse
parameter_list|(
name|FunctionQParser
name|fp
parameter_list|)
throws|throws
name|ParseException
block|{
name|ValueSource
name|a
init|=
name|fp
operator|.
name|parseValueSource
argument_list|()
decl_stmt|;
name|ValueSource
name|b
init|=
name|fp
operator|.
name|parseValueSource
argument_list|()
decl_stmt|;
return|return
operator|new
name|PowFloatFunction
argument_list|(
name|a
argument_list|,
name|b
argument_list|)
return|;
block|}
specifier|public
name|void
name|init
parameter_list|(
name|NamedList
name|args
parameter_list|)
block|{       }
block|}
argument_list|)
expr_stmt|;
name|standardValueSourceParsers
operator|.
name|put
argument_list|(
literal|"div"
argument_list|,
operator|new
name|ValueSourceParser
argument_list|()
block|{
specifier|public
name|ValueSource
name|parse
parameter_list|(
name|FunctionQParser
name|fp
parameter_list|)
throws|throws
name|ParseException
block|{
name|ValueSource
name|a
init|=
name|fp
operator|.
name|parseValueSource
argument_list|()
decl_stmt|;
name|ValueSource
name|b
init|=
name|fp
operator|.
name|parseValueSource
argument_list|()
decl_stmt|;
return|return
operator|new
name|DivFloatFunction
argument_list|(
name|a
argument_list|,
name|b
argument_list|)
return|;
block|}
specifier|public
name|void
name|init
parameter_list|(
name|NamedList
name|args
parameter_list|)
block|{       }
block|}
argument_list|)
expr_stmt|;
name|standardValueSourceParsers
operator|.
name|put
argument_list|(
literal|"map"
argument_list|,
operator|new
name|ValueSourceParser
argument_list|()
block|{
specifier|public
name|ValueSource
name|parse
parameter_list|(
name|FunctionQParser
name|fp
parameter_list|)
throws|throws
name|ParseException
block|{
name|ValueSource
name|source
init|=
name|fp
operator|.
name|parseValueSource
argument_list|()
decl_stmt|;
name|float
name|min
init|=
name|fp
operator|.
name|parseFloat
argument_list|()
decl_stmt|;
name|float
name|max
init|=
name|fp
operator|.
name|parseFloat
argument_list|()
decl_stmt|;
name|float
name|target
init|=
name|fp
operator|.
name|parseFloat
argument_list|()
decl_stmt|;
return|return
operator|new
name|RangeMapFloatFunction
argument_list|(
name|source
argument_list|,
name|min
argument_list|,
name|max
argument_list|,
name|target
argument_list|)
return|;
block|}
specifier|public
name|void
name|init
parameter_list|(
name|NamedList
name|args
parameter_list|)
block|{       }
block|}
argument_list|)
expr_stmt|;
name|standardValueSourceParsers
operator|.
name|put
argument_list|(
literal|"sqrt"
argument_list|,
operator|new
name|ValueSourceParser
argument_list|()
block|{
specifier|public
name|ValueSource
name|parse
parameter_list|(
name|FunctionQParser
name|fp
parameter_list|)
throws|throws
name|ParseException
block|{
name|ValueSource
name|source
init|=
name|fp
operator|.
name|parseValueSource
argument_list|()
decl_stmt|;
return|return
operator|new
name|SimpleFloatFunction
argument_list|(
name|source
argument_list|)
block|{
specifier|protected
name|String
name|name
parameter_list|()
block|{
return|return
literal|"sqrt"
return|;
block|}
specifier|protected
name|float
name|func
parameter_list|(
name|int
name|doc
parameter_list|,
name|DocValues
name|vals
parameter_list|)
block|{
return|return
operator|(
name|float
operator|)
name|Math
operator|.
name|sqrt
argument_list|(
name|vals
operator|.
name|floatVal
argument_list|(
name|doc
argument_list|)
argument_list|)
return|;
block|}
block|}
return|;
block|}
specifier|public
name|void
name|init
parameter_list|(
name|NamedList
name|args
parameter_list|)
block|{       }
block|}
argument_list|)
expr_stmt|;
name|standardValueSourceParsers
operator|.
name|put
argument_list|(
literal|"log"
argument_list|,
operator|new
name|ValueSourceParser
argument_list|()
block|{
specifier|public
name|ValueSource
name|parse
parameter_list|(
name|FunctionQParser
name|fp
parameter_list|)
throws|throws
name|ParseException
block|{
name|ValueSource
name|source
init|=
name|fp
operator|.
name|parseValueSource
argument_list|()
decl_stmt|;
return|return
operator|new
name|SimpleFloatFunction
argument_list|(
name|source
argument_list|)
block|{
specifier|protected
name|String
name|name
parameter_list|()
block|{
return|return
literal|"log"
return|;
block|}
specifier|protected
name|float
name|func
parameter_list|(
name|int
name|doc
parameter_list|,
name|DocValues
name|vals
parameter_list|)
block|{
return|return
operator|(
name|float
operator|)
name|Math
operator|.
name|log10
argument_list|(
name|vals
operator|.
name|floatVal
argument_list|(
name|doc
argument_list|)
argument_list|)
return|;
block|}
block|}
return|;
block|}
specifier|public
name|void
name|init
parameter_list|(
name|NamedList
name|args
parameter_list|)
block|{       }
block|}
argument_list|)
expr_stmt|;
name|standardValueSourceParsers
operator|.
name|put
argument_list|(
literal|"abs"
argument_list|,
operator|new
name|ValueSourceParser
argument_list|()
block|{
specifier|public
name|ValueSource
name|parse
parameter_list|(
name|FunctionQParser
name|fp
parameter_list|)
throws|throws
name|ParseException
block|{
name|ValueSource
name|source
init|=
name|fp
operator|.
name|parseValueSource
argument_list|()
decl_stmt|;
return|return
operator|new
name|SimpleFloatFunction
argument_list|(
name|source
argument_list|)
block|{
specifier|protected
name|String
name|name
parameter_list|()
block|{
return|return
literal|"abs"
return|;
block|}
specifier|protected
name|float
name|func
parameter_list|(
name|int
name|doc
parameter_list|,
name|DocValues
name|vals
parameter_list|)
block|{
return|return
operator|(
name|float
operator|)
name|Math
operator|.
name|abs
argument_list|(
name|vals
operator|.
name|floatVal
argument_list|(
name|doc
argument_list|)
argument_list|)
return|;
block|}
block|}
return|;
block|}
specifier|public
name|void
name|init
parameter_list|(
name|NamedList
name|args
parameter_list|)
block|{       }
block|}
argument_list|)
expr_stmt|;
name|standardValueSourceParsers
operator|.
name|put
argument_list|(
literal|"sum"
argument_list|,
operator|new
name|ValueSourceParser
argument_list|()
block|{
specifier|public
name|ValueSource
name|parse
parameter_list|(
name|FunctionQParser
name|fp
parameter_list|)
throws|throws
name|ParseException
block|{
name|List
argument_list|<
name|ValueSource
argument_list|>
name|sources
init|=
name|fp
operator|.
name|parseValueSourceList
argument_list|()
decl_stmt|;
return|return
operator|new
name|SumFloatFunction
argument_list|(
name|sources
operator|.
name|toArray
argument_list|(
operator|new
name|ValueSource
index|[
name|sources
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
return|;
block|}
specifier|public
name|void
name|init
parameter_list|(
name|NamedList
name|args
parameter_list|)
block|{       }
block|}
argument_list|)
expr_stmt|;
name|standardValueSourceParsers
operator|.
name|put
argument_list|(
literal|"product"
argument_list|,
operator|new
name|ValueSourceParser
argument_list|()
block|{
specifier|public
name|ValueSource
name|parse
parameter_list|(
name|FunctionQParser
name|fp
parameter_list|)
throws|throws
name|ParseException
block|{
name|List
argument_list|<
name|ValueSource
argument_list|>
name|sources
init|=
name|fp
operator|.
name|parseValueSourceList
argument_list|()
decl_stmt|;
return|return
operator|new
name|ProductFloatFunction
argument_list|(
name|sources
operator|.
name|toArray
argument_list|(
operator|new
name|ValueSource
index|[
name|sources
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
return|;
block|}
specifier|public
name|void
name|init
parameter_list|(
name|NamedList
name|args
parameter_list|)
block|{       }
block|}
argument_list|)
expr_stmt|;
name|standardValueSourceParsers
operator|.
name|put
argument_list|(
literal|"query"
argument_list|,
operator|new
name|ValueSourceParser
argument_list|()
block|{
comment|// boost(query($q),rating)
specifier|public
name|ValueSource
name|parse
parameter_list|(
name|FunctionQParser
name|fp
parameter_list|)
throws|throws
name|ParseException
block|{
name|Query
name|q
init|=
name|fp
operator|.
name|parseNestedQuery
argument_list|()
decl_stmt|;
name|float
name|defVal
init|=
literal|0.0f
decl_stmt|;
if|if
condition|(
name|fp
operator|.
name|hasMoreArguments
argument_list|()
condition|)
block|{
name|defVal
operator|=
name|fp
operator|.
name|parseFloat
argument_list|()
expr_stmt|;
block|}
return|return
operator|new
name|QueryValueSource
argument_list|(
name|q
argument_list|,
name|defVal
argument_list|)
return|;
block|}
specifier|public
name|void
name|init
parameter_list|(
name|NamedList
name|args
parameter_list|)
block|{       }
block|}
argument_list|)
expr_stmt|;
name|standardValueSourceParsers
operator|.
name|put
argument_list|(
literal|"boost"
argument_list|,
operator|new
name|ValueSourceParser
argument_list|()
block|{
specifier|public
name|ValueSource
name|parse
parameter_list|(
name|FunctionQParser
name|fp
parameter_list|)
throws|throws
name|ParseException
block|{
name|Query
name|q
init|=
name|fp
operator|.
name|parseNestedQuery
argument_list|()
decl_stmt|;
name|ValueSource
name|vs
init|=
name|fp
operator|.
name|parseValueSource
argument_list|()
decl_stmt|;
name|BoostedQuery
name|bq
init|=
operator|new
name|BoostedQuery
argument_list|(
name|q
argument_list|,
name|vs
argument_list|)
decl_stmt|;
return|return
operator|new
name|QueryValueSource
argument_list|(
name|bq
argument_list|,
literal|0.0f
argument_list|)
return|;
block|}
specifier|public
name|void
name|init
parameter_list|(
name|NamedList
name|args
parameter_list|)
block|{       }
block|}
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
