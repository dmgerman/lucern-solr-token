begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.analytics
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|analytics
package|;
end_package
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayInputStream
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileNotFoundException
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
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStream
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
name|Collections
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
name|HashSet
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Scanner
import|;
end_import
begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|DocumentBuilder
import|;
end_import
begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|DocumentBuilderFactory
import|;
end_import
begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|ParserConfigurationException
import|;
end_import
begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|xpath
operator|.
name|XPathConstants
import|;
end_import
begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|xpath
operator|.
name|XPathExpressionException
import|;
end_import
begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|xpath
operator|.
name|XPathFactory
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|lang
operator|.
name|StringUtils
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
name|LuceneTestCase
operator|.
name|SuppressCodecs
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
name|IOUtils
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
name|SolrTestCaseJ4
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
name|util
operator|.
name|MedianCalculator
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
name|util
operator|.
name|PercentileCalculator
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
name|w3c
operator|.
name|dom
operator|.
name|Document
import|;
end_import
begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|InputSource
import|;
end_import
begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|SAXException
import|;
end_import
begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ObjectArrays
import|;
end_import
begin_class
annotation|@
name|SuppressCodecs
argument_list|(
block|{
literal|"Lucene3x"
block|,
literal|"Lucene40"
block|,
literal|"Lucene41"
block|,
literal|"Lucene42"
block|,
literal|"Appending"
block|,
literal|"Asserting"
block|}
argument_list|)
DECL|class|AbstractAnalyticsStatsTest
specifier|public
class|class
name|AbstractAnalyticsStatsTest
extends|extends
name|SolrTestCaseJ4
block|{
DECL|field|BASEPARMS
specifier|protected
specifier|static
specifier|final
name|String
index|[]
name|BASEPARMS
init|=
operator|new
name|String
index|[]
block|{
literal|"q"
block|,
literal|"*:*"
block|,
literal|"indent"
block|,
literal|"true"
block|,
literal|"olap"
block|,
literal|"true"
block|,
literal|"rows"
block|,
literal|"0"
block|}
decl_stmt|;
DECL|field|defaults
specifier|protected
specifier|static
specifier|final
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|defaults
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|enum|VAL_TYPE
specifier|public
specifier|static
enum|enum
name|VAL_TYPE
block|{
DECL|enum constant|INTEGER
name|INTEGER
argument_list|(
literal|"int"
argument_list|)
block|,
DECL|enum constant|LONG
name|LONG
argument_list|(
literal|"long"
argument_list|)
block|,
DECL|enum constant|FLOAT
name|FLOAT
argument_list|(
literal|"float"
argument_list|)
block|,
DECL|enum constant|DOUBLE
name|DOUBLE
argument_list|(
literal|"double"
argument_list|)
block|,
DECL|enum constant|STRING
name|STRING
argument_list|(
literal|"str"
argument_list|)
block|,
DECL|enum constant|DATE
name|DATE
argument_list|(
literal|"date"
argument_list|)
block|;
DECL|method|VAL_TYPE
specifier|private
name|VAL_TYPE
parameter_list|(
specifier|final
name|String
name|text
parameter_list|)
block|{
name|this
operator|.
name|text
operator|=
name|text
expr_stmt|;
block|}
DECL|field|text
specifier|private
specifier|final
name|String
name|text
decl_stmt|;
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|text
return|;
block|}
block|}
DECL|field|doc
specifier|static
specifier|private
name|Document
name|doc
decl_stmt|;
DECL|field|xPathFact
specifier|static
specifier|private
name|XPathFactory
name|xPathFact
init|=
name|XPathFactory
operator|.
name|newInstance
argument_list|()
decl_stmt|;
DECL|field|rawResponse
specifier|static
specifier|private
name|String
name|rawResponse
decl_stmt|;
DECL|method|setResponse
specifier|public
specifier|static
name|void
name|setResponse
parameter_list|(
name|String
name|response
parameter_list|)
throws|throws
name|ParserConfigurationException
throws|,
name|IOException
throws|,
name|SAXException
block|{
name|DocumentBuilderFactory
name|factory
init|=
name|DocumentBuilderFactory
operator|.
name|newInstance
argument_list|()
decl_stmt|;
name|factory
operator|.
name|setNamespaceAware
argument_list|(
literal|true
argument_list|)
expr_stmt|;
comment|// never forget this!
name|DocumentBuilder
name|builder
init|=
name|factory
operator|.
name|newDocumentBuilder
argument_list|()
decl_stmt|;
name|doc
operator|=
name|builder
operator|.
name|parse
argument_list|(
operator|new
name|InputSource
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|response
operator|.
name|getBytes
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|xPathFact
operator|=
name|XPathFactory
operator|.
name|newInstance
argument_list|()
expr_stmt|;
name|rawResponse
operator|=
name|response
expr_stmt|;
block|}
DECL|method|getRawResponse
specifier|protected
name|String
name|getRawResponse
parameter_list|()
block|{
return|return
name|rawResponse
return|;
block|}
DECL|method|getStatResult
specifier|public
name|Object
name|getStatResult
parameter_list|(
name|String
name|section
parameter_list|,
name|String
name|name
parameter_list|,
name|VAL_TYPE
name|type
parameter_list|)
throws|throws
name|XPathExpressionException
block|{
comment|// Construct the XPath expression. The form better not change or all these will fail.
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|(
literal|"/response/lst[@name='stats']/lst[@name='"
argument_list|)
operator|.
name|append
argument_list|(
name|section
argument_list|)
operator|.
name|append
argument_list|(
literal|"']"
argument_list|)
decl_stmt|;
comment|// This is a little fragile in that it demands the elements have the same name as type, i.e. when looking for a
comment|// VAL_TYPE.DOUBLE, the element in question is<double name="blah">47.0</double>.
name|sb
operator|.
name|append
argument_list|(
literal|"/"
argument_list|)
operator|.
name|append
argument_list|(
name|type
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"[@name='"
argument_list|)
operator|.
name|append
argument_list|(
name|name
argument_list|)
operator|.
name|append
argument_list|(
literal|"']"
argument_list|)
expr_stmt|;
name|String
name|val
init|=
name|xPathFact
operator|.
name|newXPath
argument_list|()
operator|.
name|compile
argument_list|(
name|sb
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|evaluate
argument_list|(
name|doc
argument_list|,
name|XPathConstants
operator|.
name|STRING
argument_list|)
operator|.
name|toString
argument_list|()
decl_stmt|;
try|try
block|{
switch|switch
condition|(
name|type
condition|)
block|{
case|case
name|INTEGER
case|:
return|return
name|Integer
operator|.
name|parseInt
argument_list|(
name|val
argument_list|)
return|;
case|case
name|DOUBLE
case|:
return|return
name|Double
operator|.
name|parseDouble
argument_list|(
name|val
argument_list|)
return|;
case|case
name|FLOAT
case|:
return|return
name|Float
operator|.
name|parseFloat
argument_list|(
name|val
argument_list|)
return|;
case|case
name|LONG
case|:
return|return
name|Long
operator|.
name|parseLong
argument_list|(
name|val
argument_list|)
return|;
case|case
name|STRING
case|:
return|return
name|val
return|;
case|case
name|DATE
case|:
return|return
name|val
return|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Caught exception in getStatResult, xPath = "
operator|+
name|sb
operator|.
name|toString
argument_list|()
operator|+
literal|" \nraw data: "
operator|+
name|rawResponse
argument_list|)
expr_stmt|;
block|}
name|fail
argument_list|(
literal|"Unknown type used in getStatResult"
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
comment|// Really can't get here, but the compiler thinks we can!
block|}
DECL|method|calculateNumberStat
specifier|public
parameter_list|<
name|T
extends|extends
name|Number
operator|&
name|Comparable
argument_list|<
name|T
argument_list|>
parameter_list|>
name|Double
name|calculateNumberStat
parameter_list|(
name|ArrayList
argument_list|<
name|T
argument_list|>
name|list
parameter_list|,
name|String
name|stat
parameter_list|)
block|{
name|Double
name|result
decl_stmt|;
if|if
condition|(
name|stat
operator|.
name|equals
argument_list|(
literal|"median"
argument_list|)
condition|)
block|{
name|result
operator|=
name|MedianCalculator
operator|.
name|getMedian
argument_list|(
name|list
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|stat
operator|.
name|equals
argument_list|(
literal|"mean"
argument_list|)
condition|)
block|{
name|double
name|d
init|=
literal|0
decl_stmt|;
for|for
control|(
name|T
name|element
range|:
name|list
control|)
block|{
name|d
operator|+=
name|element
operator|.
name|doubleValue
argument_list|()
expr_stmt|;
block|}
name|result
operator|=
name|Double
operator|.
name|valueOf
argument_list|(
name|d
operator|/
name|list
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|stat
operator|.
name|equals
argument_list|(
literal|"sum"
argument_list|)
condition|)
block|{
name|double
name|d
init|=
literal|0
decl_stmt|;
for|for
control|(
name|T
name|element
range|:
name|list
control|)
block|{
name|d
operator|+=
name|element
operator|.
name|doubleValue
argument_list|()
expr_stmt|;
block|}
name|result
operator|=
name|Double
operator|.
name|valueOf
argument_list|(
name|d
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|stat
operator|.
name|equals
argument_list|(
literal|"sumOfSquares"
argument_list|)
condition|)
block|{
name|double
name|d
init|=
literal|0
decl_stmt|;
for|for
control|(
name|T
name|element
range|:
name|list
control|)
block|{
name|d
operator|+=
name|element
operator|.
name|doubleValue
argument_list|()
operator|*
name|element
operator|.
name|doubleValue
argument_list|()
expr_stmt|;
block|}
name|result
operator|=
name|Double
operator|.
name|valueOf
argument_list|(
name|d
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|stat
operator|.
name|equals
argument_list|(
literal|"stddev"
argument_list|)
condition|)
block|{
name|double
name|sum
init|=
literal|0
decl_stmt|;
name|double
name|sumSquares
init|=
literal|0
decl_stmt|;
for|for
control|(
name|T
name|element
range|:
name|list
control|)
block|{
name|sum
operator|+=
name|element
operator|.
name|doubleValue
argument_list|()
expr_stmt|;
name|sumSquares
operator|+=
name|element
operator|.
name|doubleValue
argument_list|()
operator|*
name|element
operator|.
name|doubleValue
argument_list|()
expr_stmt|;
block|}
name|result
operator|=
name|Math
operator|.
name|sqrt
argument_list|(
name|sumSquares
operator|/
name|list
operator|.
name|size
argument_list|()
operator|-
name|sum
operator|*
name|sum
operator|/
operator|(
name|list
operator|.
name|size
argument_list|()
operator|*
name|list
operator|.
name|size
argument_list|()
operator|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|()
throw|;
block|}
return|return
name|result
return|;
block|}
DECL|method|calculateStat
specifier|public
parameter_list|<
name|T
extends|extends
name|Comparable
argument_list|<
name|T
argument_list|>
parameter_list|>
name|Object
name|calculateStat
parameter_list|(
name|ArrayList
argument_list|<
name|T
argument_list|>
name|list
parameter_list|,
name|String
name|stat
parameter_list|)
block|{
name|Object
name|result
decl_stmt|;
if|if
condition|(
name|stat
operator|.
name|contains
argument_list|(
literal|"perc_"
argument_list|)
condition|)
block|{
name|double
index|[]
name|perc
init|=
operator|new
name|double
index|[]
block|{
name|Double
operator|.
name|parseDouble
argument_list|(
name|stat
operator|.
name|substring
argument_list|(
literal|5
argument_list|)
argument_list|)
operator|/
literal|100
block|}
decl_stmt|;
name|result
operator|=
name|PercentileCalculator
operator|.
name|getPercentiles
argument_list|(
name|list
argument_list|,
name|perc
argument_list|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|stat
operator|.
name|equals
argument_list|(
literal|"count"
argument_list|)
condition|)
block|{
name|result
operator|=
name|Long
operator|.
name|valueOf
argument_list|(
name|list
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|stat
operator|.
name|equals
argument_list|(
literal|"unique"
argument_list|)
condition|)
block|{
name|HashSet
argument_list|<
name|T
argument_list|>
name|set
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
name|set
operator|.
name|addAll
argument_list|(
name|list
argument_list|)
expr_stmt|;
name|result
operator|=
name|Long
operator|.
name|valueOf
argument_list|(
operator|(
name|long
operator|)
name|set
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|stat
operator|.
name|equals
argument_list|(
literal|"max"
argument_list|)
condition|)
block|{
name|Collections
operator|.
name|sort
argument_list|(
name|list
argument_list|)
expr_stmt|;
name|result
operator|=
name|list
operator|.
name|get
argument_list|(
name|list
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|stat
operator|.
name|equals
argument_list|(
literal|"min"
argument_list|)
condition|)
block|{
name|Collections
operator|.
name|sort
argument_list|(
name|list
argument_list|)
expr_stmt|;
name|result
operator|=
name|list
operator|.
name|get
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|result
operator|=
literal|null
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|calculateMissing
specifier|public
parameter_list|<
name|T
extends|extends
name|Comparable
argument_list|<
name|T
argument_list|>
parameter_list|>
name|Long
name|calculateMissing
parameter_list|(
name|ArrayList
argument_list|<
name|T
argument_list|>
name|list
parameter_list|,
name|String
name|type
parameter_list|)
block|{
name|T
name|def
init|=
operator|(
name|T
operator|)
name|defaults
operator|.
name|get
argument_list|(
name|type
argument_list|)
decl_stmt|;
name|long
name|miss
init|=
literal|0
decl_stmt|;
for|for
control|(
name|T
name|element
range|:
name|list
control|)
block|{
if|if
condition|(
name|element
operator|.
name|compareTo
argument_list|(
name|def
argument_list|)
operator|==
literal|0
condition|)
block|{
name|miss
operator|++
expr_stmt|;
block|}
block|}
return|return
name|Long
operator|.
name|valueOf
argument_list|(
name|miss
argument_list|)
return|;
block|}
DECL|method|request
specifier|public
specifier|static
name|SolrQueryRequest
name|request
parameter_list|(
name|String
modifier|...
name|args
parameter_list|)
block|{
return|return
name|SolrTestCaseJ4
operator|.
name|req
argument_list|(
name|ObjectArrays
operator|.
name|concat
argument_list|(
name|BASEPARMS
argument_list|,
name|args
argument_list|,
name|String
operator|.
name|class
argument_list|)
argument_list|)
return|;
block|}
DECL|method|fileToStringArr
specifier|public
specifier|static
name|String
index|[]
name|fileToStringArr
parameter_list|(
name|Class
argument_list|<
name|?
argument_list|>
name|clazz
parameter_list|,
name|String
name|fileName
parameter_list|)
throws|throws
name|FileNotFoundException
block|{
name|InputStream
name|in
init|=
name|clazz
operator|.
name|getResourceAsStream
argument_list|(
name|fileName
argument_list|)
decl_stmt|;
if|if
condition|(
name|in
operator|==
literal|null
condition|)
throw|throw
operator|new
name|FileNotFoundException
argument_list|(
literal|"Resource not found: "
operator|+
name|fileName
argument_list|)
throw|;
name|Scanner
name|file
init|=
operator|new
name|Scanner
argument_list|(
name|in
argument_list|,
literal|"UTF-8"
argument_list|)
decl_stmt|;
try|try
block|{
name|ArrayList
argument_list|<
name|String
argument_list|>
name|strList
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
while|while
condition|(
name|file
operator|.
name|hasNextLine
argument_list|()
condition|)
block|{
name|String
name|line
init|=
name|file
operator|.
name|nextLine
argument_list|()
decl_stmt|;
name|line
operator|=
name|line
operator|.
name|trim
argument_list|()
expr_stmt|;
if|if
condition|(
name|StringUtils
operator|.
name|isBlank
argument_list|(
name|line
argument_list|)
operator|||
name|line
operator|.
name|startsWith
argument_list|(
literal|"#"
argument_list|)
condition|)
block|{
continue|continue;
block|}
name|String
index|[]
name|param
init|=
name|line
operator|.
name|split
argument_list|(
literal|"="
argument_list|)
decl_stmt|;
name|strList
operator|.
name|add
argument_list|(
name|param
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|strList
operator|.
name|add
argument_list|(
name|param
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
block|}
return|return
name|strList
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
literal|0
index|]
argument_list|)
return|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|file
argument_list|,
name|in
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
