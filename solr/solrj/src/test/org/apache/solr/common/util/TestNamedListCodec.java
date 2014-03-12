begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.common.util
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|util
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
name|util
operator|.
name|LuceneTestCase
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
name|BaseDistributedSearchTestCase
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
name|SolrDocument
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
name|SolrDocumentList
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayOutputStream
import|;
end_import
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
name|Random
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
name|Map
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
begin_class
DECL|class|TestNamedListCodec
specifier|public
class|class
name|TestNamedListCodec
extends|extends
name|LuceneTestCase
block|{
DECL|method|testSimple
specifier|public
name|void
name|testSimple
parameter_list|()
throws|throws
name|Exception
block|{
name|ByteArrayOutputStream
name|baos
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|NamedList
name|nl
init|=
operator|new
name|NamedList
argument_list|()
decl_stmt|;
name|Float
name|fval
init|=
operator|new
name|Float
argument_list|(
literal|10.01f
argument_list|)
decl_stmt|;
name|Boolean
name|bval
init|=
name|Boolean
operator|.
name|TRUE
decl_stmt|;
name|String
name|sval
init|=
literal|"12qwaszx"
decl_stmt|;
comment|// Set up a simple document
name|NamedList
name|r
init|=
operator|new
name|NamedList
argument_list|()
decl_stmt|;
name|nl
operator|.
name|add
argument_list|(
literal|"responseHeader"
argument_list|,
name|r
argument_list|)
expr_stmt|;
name|r
operator|.
name|add
argument_list|(
literal|"status"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|r
operator|.
name|add
argument_list|(
literal|"QTime"
argument_list|,
literal|63
argument_list|)
expr_stmt|;
name|NamedList
name|p
init|=
operator|new
name|NamedList
argument_list|()
decl_stmt|;
name|r
operator|.
name|add
argument_list|(
literal|"params"
argument_list|,
name|p
argument_list|)
expr_stmt|;
name|p
operator|.
name|add
argument_list|(
literal|"rows"
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|p
operator|.
name|add
argument_list|(
literal|"start"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|p
operator|.
name|add
argument_list|(
literal|"indent"
argument_list|,
literal|"on"
argument_list|)
expr_stmt|;
name|p
operator|.
name|add
argument_list|(
literal|"q"
argument_list|,
literal|"ipod"
argument_list|)
expr_stmt|;
name|SolrDocumentList
name|list
init|=
operator|new
name|SolrDocumentList
argument_list|()
decl_stmt|;
name|nl
operator|.
name|add
argument_list|(
literal|"response"
argument_list|,
name|list
argument_list|)
expr_stmt|;
name|list
operator|.
name|setMaxScore
argument_list|(
literal|1.0f
argument_list|)
expr_stmt|;
name|list
operator|.
name|setStart
argument_list|(
literal|10
argument_list|)
expr_stmt|;
name|list
operator|.
name|setNumFound
argument_list|(
literal|12
argument_list|)
expr_stmt|;
name|SolrDocument
name|doc
init|=
operator|new
name|SolrDocument
argument_list|()
decl_stmt|;
name|doc
operator|.
name|addField
argument_list|(
literal|"f"
argument_list|,
name|fval
argument_list|)
expr_stmt|;
name|doc
operator|.
name|addField
argument_list|(
literal|"b"
argument_list|,
name|bval
argument_list|)
expr_stmt|;
name|doc
operator|.
name|addField
argument_list|(
literal|"s"
argument_list|,
name|sval
argument_list|)
expr_stmt|;
name|doc
operator|.
name|addField
argument_list|(
literal|"f"
argument_list|,
literal|100
argument_list|)
expr_stmt|;
name|list
operator|.
name|add
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|doc
operator|=
operator|new
name|SolrDocument
argument_list|()
expr_stmt|;
name|doc
operator|.
name|addField
argument_list|(
literal|"f"
argument_list|,
name|fval
argument_list|)
expr_stmt|;
name|doc
operator|.
name|addField
argument_list|(
literal|"b"
argument_list|,
name|bval
argument_list|)
expr_stmt|;
name|doc
operator|.
name|addField
argument_list|(
literal|"s"
argument_list|,
name|sval
argument_list|)
expr_stmt|;
name|doc
operator|.
name|addField
argument_list|(
literal|"f"
argument_list|,
literal|101
argument_list|)
expr_stmt|;
name|list
operator|.
name|add
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|nl
operator|.
name|add
argument_list|(
literal|"zzz"
argument_list|,
name|doc
argument_list|)
expr_stmt|;
operator|new
name|JavaBinCodec
argument_list|(
literal|null
argument_list|)
operator|.
name|marshal
argument_list|(
name|nl
argument_list|,
name|baos
argument_list|)
expr_stmt|;
name|byte
index|[]
name|arr
init|=
name|baos
operator|.
name|toByteArray
argument_list|()
decl_stmt|;
name|nl
operator|=
operator|(
name|NamedList
operator|)
operator|new
name|JavaBinCodec
argument_list|()
operator|.
name|unmarshal
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|arr
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|nl
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"ipod"
argument_list|,
operator|(
call|(
name|NamedList
call|)
argument_list|(
operator|(
name|NamedList
operator|)
name|nl
operator|.
name|getVal
argument_list|(
literal|0
argument_list|)
argument_list|)
operator|.
name|get
argument_list|(
literal|"params"
argument_list|)
operator|)
operator|.
name|get
argument_list|(
literal|"q"
argument_list|)
argument_list|)
expr_stmt|;
name|list
operator|=
operator|(
name|SolrDocumentList
operator|)
name|nl
operator|.
name|getVal
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|12
argument_list|,
name|list
operator|.
name|getNumFound
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|10
argument_list|,
name|list
operator|.
name|getStart
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|101
argument_list|,
operator|(
operator|(
name|List
operator|)
name|list
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|getFieldValue
argument_list|(
literal|"f"
argument_list|)
operator|)
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testIterator
specifier|public
name|void
name|testIterator
parameter_list|()
throws|throws
name|Exception
block|{
name|ByteArrayOutputStream
name|baos
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|NamedList
name|nl
init|=
operator|new
name|NamedList
argument_list|()
decl_stmt|;
name|Float
name|fval
init|=
operator|new
name|Float
argument_list|(
literal|10.01f
argument_list|)
decl_stmt|;
name|Boolean
name|bval
init|=
name|Boolean
operator|.
name|TRUE
decl_stmt|;
name|String
name|sval
init|=
literal|"12qwaszx"
decl_stmt|;
comment|// Set up a simple document
name|NamedList
name|r
init|=
operator|new
name|NamedList
argument_list|()
decl_stmt|;
name|List
name|list
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
name|SolrDocument
name|doc
init|=
operator|new
name|SolrDocument
argument_list|()
decl_stmt|;
name|doc
operator|.
name|addField
argument_list|(
literal|"f"
argument_list|,
name|fval
argument_list|)
expr_stmt|;
name|doc
operator|.
name|addField
argument_list|(
literal|"b"
argument_list|,
name|bval
argument_list|)
expr_stmt|;
name|doc
operator|.
name|addField
argument_list|(
literal|"s"
argument_list|,
name|sval
argument_list|)
expr_stmt|;
name|doc
operator|.
name|addField
argument_list|(
literal|"f"
argument_list|,
literal|100
argument_list|)
expr_stmt|;
name|list
operator|.
name|add
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|doc
operator|=
operator|new
name|SolrDocument
argument_list|()
expr_stmt|;
name|doc
operator|.
name|addField
argument_list|(
literal|"f"
argument_list|,
name|fval
argument_list|)
expr_stmt|;
name|doc
operator|.
name|addField
argument_list|(
literal|"b"
argument_list|,
name|bval
argument_list|)
expr_stmt|;
name|doc
operator|.
name|addField
argument_list|(
literal|"s"
argument_list|,
name|sval
argument_list|)
expr_stmt|;
name|doc
operator|.
name|addField
argument_list|(
literal|"f"
argument_list|,
literal|101
argument_list|)
expr_stmt|;
name|list
operator|.
name|add
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|nl
operator|.
name|add
argument_list|(
literal|"zzz"
argument_list|,
name|list
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
operator|new
name|JavaBinCodec
argument_list|(
literal|null
argument_list|)
operator|.
name|marshal
argument_list|(
name|nl
argument_list|,
name|baos
argument_list|)
expr_stmt|;
name|byte
index|[]
name|arr
init|=
name|baos
operator|.
name|toByteArray
argument_list|()
decl_stmt|;
name|nl
operator|=
operator|(
name|NamedList
operator|)
operator|new
name|JavaBinCodec
argument_list|()
operator|.
name|unmarshal
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|arr
argument_list|)
argument_list|)
expr_stmt|;
name|List
name|l
init|=
operator|(
name|List
operator|)
name|nl
operator|.
name|get
argument_list|(
literal|"zzz"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|list
operator|.
name|size
argument_list|()
argument_list|,
name|l
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testIterable
specifier|public
name|void
name|testIterable
parameter_list|()
throws|throws
name|Exception
block|{
name|ByteArrayOutputStream
name|baos
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|NamedList
name|r
init|=
operator|new
name|NamedList
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|map
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"junk"
argument_list|,
literal|"funk"
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"ham"
argument_list|,
literal|"burger"
argument_list|)
expr_stmt|;
name|r
operator|.
name|add
argument_list|(
literal|"keys"
argument_list|,
name|map
operator|.
name|keySet
argument_list|()
argument_list|)
expr_stmt|;
name|r
operator|.
name|add
argument_list|(
literal|"more"
argument_list|,
literal|"less"
argument_list|)
expr_stmt|;
name|r
operator|.
name|add
argument_list|(
literal|"values"
argument_list|,
name|map
operator|.
name|values
argument_list|()
argument_list|)
expr_stmt|;
name|r
operator|.
name|add
argument_list|(
literal|"finally"
argument_list|,
literal|"the end"
argument_list|)
expr_stmt|;
operator|new
name|JavaBinCodec
argument_list|(
literal|null
argument_list|)
operator|.
name|marshal
argument_list|(
name|r
argument_list|,
name|baos
argument_list|)
expr_stmt|;
name|byte
index|[]
name|arr
init|=
name|baos
operator|.
name|toByteArray
argument_list|()
decl_stmt|;
try|try
block|{
name|NamedList
name|result
init|=
operator|(
name|NamedList
operator|)
operator|new
name|JavaBinCodec
argument_list|()
operator|.
name|unmarshal
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|arr
argument_list|)
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"result is null and it shouldn't be"
argument_list|,
name|result
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|List
name|keys
init|=
operator|(
name|List
operator|)
name|result
operator|.
name|get
argument_list|(
literal|"keys"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"keys is null and it shouldn't be"
argument_list|,
name|keys
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"keys Size: "
operator|+
name|keys
operator|.
name|size
argument_list|()
operator|+
literal|" is not: "
operator|+
literal|3
argument_list|,
name|keys
operator|.
name|size
argument_list|()
operator|==
literal|3
argument_list|)
expr_stmt|;
name|String
name|less
init|=
operator|(
name|String
operator|)
name|result
operator|.
name|get
argument_list|(
literal|"more"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"less is null and it shouldn't be"
argument_list|,
name|less
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|less
operator|+
literal|" is not equal to "
operator|+
literal|"less"
argument_list|,
name|less
operator|.
name|equals
argument_list|(
literal|"less"
argument_list|)
operator|==
literal|true
argument_list|)
expr_stmt|;
name|List
name|values
init|=
operator|(
name|List
operator|)
name|result
operator|.
name|get
argument_list|(
literal|"values"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"values is null and it shouldn't be"
argument_list|,
name|values
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"values Size: "
operator|+
name|values
operator|.
name|size
argument_list|()
operator|+
literal|" is not: "
operator|+
literal|3
argument_list|,
name|values
operator|.
name|size
argument_list|()
operator|==
literal|3
argument_list|)
expr_stmt|;
name|String
name|theEnd
init|=
operator|(
name|String
operator|)
name|result
operator|.
name|get
argument_list|(
literal|"finally"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"theEnd is null and it shouldn't be"
argument_list|,
name|theEnd
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|theEnd
operator|+
literal|" is not equal to "
operator|+
literal|"the end"
argument_list|,
name|theEnd
operator|.
name|equals
argument_list|(
literal|"the end"
argument_list|)
operator|==
literal|true
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ClassCastException
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
literal|"Received a CCE and we shouldn't have"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|rSz
name|int
name|rSz
parameter_list|(
name|int
name|orderOfMagnitude
parameter_list|)
block|{
name|int
name|sz
init|=
name|r
operator|.
name|nextInt
argument_list|(
name|orderOfMagnitude
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|sz
condition|)
block|{
case|case
literal|0
case|:
return|return
name|r
operator|.
name|nextInt
argument_list|(
literal|10
argument_list|)
return|;
case|case
literal|1
case|:
return|return
name|r
operator|.
name|nextInt
argument_list|(
literal|100
argument_list|)
return|;
case|case
literal|2
case|:
return|return
name|r
operator|.
name|nextInt
argument_list|(
literal|1000
argument_list|)
return|;
default|default:
return|return
name|r
operator|.
name|nextInt
argument_list|(
literal|10000
argument_list|)
return|;
block|}
block|}
DECL|method|rStr
specifier|public
name|String
name|rStr
parameter_list|(
name|int
name|sz
parameter_list|)
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|sz
condition|;
name|i
operator|++
control|)
block|{
name|sb
operator|.
name|appendCodePoint
argument_list|(
name|r
operator|.
name|nextInt
argument_list|(
name|Character
operator|.
name|MIN_HIGH_SURROGATE
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|rNamedList
specifier|public
name|NamedList
name|rNamedList
parameter_list|(
name|int
name|lev
parameter_list|)
block|{
name|int
name|sz
init|=
name|lev
operator|<=
literal|0
condition|?
literal|0
else|:
name|r
operator|.
name|nextInt
argument_list|(
literal|3
argument_list|)
decl_stmt|;
name|NamedList
name|nl
init|=
operator|new
name|NamedList
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|sz
condition|;
name|i
operator|++
control|)
block|{
name|nl
operator|.
name|add
argument_list|(
name|rStr
argument_list|(
literal|2
argument_list|)
argument_list|,
name|makeRandom
argument_list|(
name|lev
operator|-
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|nl
return|;
block|}
DECL|method|rList
specifier|public
name|List
name|rList
parameter_list|(
name|int
name|lev
parameter_list|)
block|{
name|int
name|sz
init|=
name|lev
operator|<=
literal|0
condition|?
literal|0
else|:
name|r
operator|.
name|nextInt
argument_list|(
literal|3
argument_list|)
decl_stmt|;
name|ArrayList
name|lst
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|sz
condition|;
name|i
operator|++
control|)
block|{
name|lst
operator|.
name|add
argument_list|(
name|makeRandom
argument_list|(
name|lev
operator|-
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|lst
return|;
block|}
DECL|field|r
name|Random
name|r
decl_stmt|;
annotation|@
name|Override
DECL|method|setUp
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|r
operator|=
name|random
argument_list|()
expr_stmt|;
block|}
DECL|method|makeRandom
specifier|public
name|Object
name|makeRandom
parameter_list|(
name|int
name|lev
parameter_list|)
block|{
switch|switch
condition|(
name|r
operator|.
name|nextInt
argument_list|(
literal|10
argument_list|)
condition|)
block|{
case|case
literal|0
case|:
return|return
name|rList
argument_list|(
name|lev
argument_list|)
return|;
case|case
literal|1
case|:
return|return
name|rNamedList
argument_list|(
name|lev
argument_list|)
return|;
case|case
literal|2
case|:
return|return
name|rStr
argument_list|(
name|rSz
argument_list|(
literal|4
argument_list|)
argument_list|)
return|;
case|case
literal|3
case|:
return|return
name|r
operator|.
name|nextInt
argument_list|()
return|;
case|case
literal|4
case|:
return|return
name|r
operator|.
name|nextLong
argument_list|()
return|;
case|case
literal|5
case|:
return|return
name|r
operator|.
name|nextBoolean
argument_list|()
return|;
case|case
literal|6
case|:
name|byte
index|[]
name|arr
init|=
operator|new
name|byte
index|[
name|rSz
argument_list|(
literal|4
argument_list|)
index|]
decl_stmt|;
name|r
operator|.
name|nextBytes
argument_list|(
name|arr
argument_list|)
expr_stmt|;
return|return
name|arr
return|;
case|case
literal|7
case|:
return|return
name|r
operator|.
name|nextFloat
argument_list|()
return|;
case|case
literal|8
case|:
return|return
name|r
operator|.
name|nextDouble
argument_list|()
return|;
default|default:
return|return
literal|null
return|;
block|}
block|}
DECL|method|testRandom
specifier|public
name|void
name|testRandom
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Random r = random;
comment|// let's keep it deterministic since just the wrong
comment|// random stuff could cause failure because of an OOM (too big)
name|NamedList
name|nl
decl_stmt|;
name|NamedList
name|res
decl_stmt|;
name|String
name|cmp
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|10000
condition|;
name|i
operator|++
control|)
block|{
comment|// pump up the iterations for good stress testing
name|nl
operator|=
name|rNamedList
argument_list|(
literal|3
argument_list|)
expr_stmt|;
name|ByteArrayOutputStream
name|baos
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
operator|new
name|JavaBinCodec
argument_list|(
literal|null
argument_list|)
operator|.
name|marshal
argument_list|(
name|nl
argument_list|,
name|baos
argument_list|)
expr_stmt|;
name|byte
index|[]
name|arr
init|=
name|baos
operator|.
name|toByteArray
argument_list|()
decl_stmt|;
comment|// System.out.println(arr.length);
name|res
operator|=
operator|(
name|NamedList
operator|)
operator|new
name|JavaBinCodec
argument_list|()
operator|.
name|unmarshal
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|arr
argument_list|)
argument_list|)
expr_stmt|;
name|cmp
operator|=
name|BaseDistributedSearchTestCase
operator|.
name|compare
argument_list|(
name|nl
argument_list|,
name|res
argument_list|,
literal|0
argument_list|,
literal|null
argument_list|)
expr_stmt|;
if|if
condition|(
name|cmp
operator|!=
literal|null
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|nl
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|res
argument_list|)
expr_stmt|;
name|fail
argument_list|(
name|cmp
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class
end_unit
