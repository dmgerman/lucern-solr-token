begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|Reader
import|;
end_import
begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|invoke
operator|.
name|MethodHandles
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedHashMap
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedList
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
name|java
operator|.
name|util
operator|.
name|Map
operator|.
name|Entry
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeMap
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
name|analysis
operator|.
name|TokenStream
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
name|analysis
operator|.
name|tokenattributes
operator|.
name|CharTermAttribute
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
name|analysis
operator|.
name|tokenattributes
operator|.
name|FlagsAttribute
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
name|analysis
operator|.
name|tokenattributes
operator|.
name|OffsetAttribute
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
name|analysis
operator|.
name|tokenattributes
operator|.
name|PayloadAttribute
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
name|analysis
operator|.
name|tokenattributes
operator|.
name|PositionIncrementAttribute
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
name|analysis
operator|.
name|tokenattributes
operator|.
name|TermToBytesRefAttribute
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
name|analysis
operator|.
name|tokenattributes
operator|.
name|TypeAttribute
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
name|Field
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
name|Attribute
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
name|AttributeSource
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
name|AttributeSource
operator|.
name|State
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
name|BytesRef
import|;
end_import
begin_import
import|import
name|org
operator|.
name|noggit
operator|.
name|JSONUtil
import|;
end_import
begin_import
import|import
name|org
operator|.
name|noggit
operator|.
name|JSONWriter
import|;
end_import
begin_import
import|import
name|org
operator|.
name|noggit
operator|.
name|ObjectBuilder
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
name|Base64
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
name|PreAnalyzedField
operator|.
name|ParseResult
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
name|PreAnalyzedField
operator|.
name|PreAnalyzedParser
import|;
end_import
begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import
begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import
begin_class
DECL|class|JsonPreAnalyzedParser
specifier|public
class|class
name|JsonPreAnalyzedParser
implements|implements
name|PreAnalyzedParser
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|MethodHandles
operator|.
name|lookup
argument_list|()
operator|.
name|lookupClass
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|VERSION
specifier|public
specifier|static
specifier|final
name|String
name|VERSION
init|=
literal|"1"
decl_stmt|;
DECL|field|VERSION_KEY
specifier|public
specifier|static
specifier|final
name|String
name|VERSION_KEY
init|=
literal|"v"
decl_stmt|;
DECL|field|STRING_KEY
specifier|public
specifier|static
specifier|final
name|String
name|STRING_KEY
init|=
literal|"str"
decl_stmt|;
DECL|field|BINARY_KEY
specifier|public
specifier|static
specifier|final
name|String
name|BINARY_KEY
init|=
literal|"bin"
decl_stmt|;
DECL|field|TOKENS_KEY
specifier|public
specifier|static
specifier|final
name|String
name|TOKENS_KEY
init|=
literal|"tokens"
decl_stmt|;
DECL|field|TOKEN_KEY
specifier|public
specifier|static
specifier|final
name|String
name|TOKEN_KEY
init|=
literal|"t"
decl_stmt|;
DECL|field|OFFSET_START_KEY
specifier|public
specifier|static
specifier|final
name|String
name|OFFSET_START_KEY
init|=
literal|"s"
decl_stmt|;
DECL|field|OFFSET_END_KEY
specifier|public
specifier|static
specifier|final
name|String
name|OFFSET_END_KEY
init|=
literal|"e"
decl_stmt|;
DECL|field|POSINCR_KEY
specifier|public
specifier|static
specifier|final
name|String
name|POSINCR_KEY
init|=
literal|"i"
decl_stmt|;
DECL|field|PAYLOAD_KEY
specifier|public
specifier|static
specifier|final
name|String
name|PAYLOAD_KEY
init|=
literal|"p"
decl_stmt|;
DECL|field|TYPE_KEY
specifier|public
specifier|static
specifier|final
name|String
name|TYPE_KEY
init|=
literal|"y"
decl_stmt|;
DECL|field|FLAGS_KEY
specifier|public
specifier|static
specifier|final
name|String
name|FLAGS_KEY
init|=
literal|"f"
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
annotation|@
name|Override
DECL|method|parse
specifier|public
name|ParseResult
name|parse
parameter_list|(
name|Reader
name|reader
parameter_list|,
name|AttributeSource
name|parent
parameter_list|)
throws|throws
name|IOException
block|{
name|ParseResult
name|res
init|=
operator|new
name|ParseResult
argument_list|()
decl_stmt|;
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|char
index|[]
name|buf
init|=
operator|new
name|char
index|[
literal|128
index|]
decl_stmt|;
name|int
name|cnt
decl_stmt|;
while|while
condition|(
operator|(
name|cnt
operator|=
name|reader
operator|.
name|read
argument_list|(
name|buf
argument_list|)
operator|)
operator|>
literal|0
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|buf
argument_list|,
literal|0
argument_list|,
name|cnt
argument_list|)
expr_stmt|;
block|}
name|String
name|val
init|=
name|sb
operator|.
name|toString
argument_list|()
decl_stmt|;
comment|// empty string - accept even without version number
if|if
condition|(
name|val
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return
name|res
return|;
block|}
name|Object
name|o
init|=
name|ObjectBuilder
operator|.
name|fromJSON
argument_list|(
name|val
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
operator|(
name|o
operator|instanceof
name|Map
operator|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Invalid JSON type "
operator|+
name|o
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|", expected Map"
argument_list|)
throw|;
block|}
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|map
init|=
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|o
decl_stmt|;
comment|// check version
name|String
name|version
init|=
operator|(
name|String
operator|)
name|map
operator|.
name|get
argument_list|(
name|VERSION_KEY
argument_list|)
decl_stmt|;
if|if
condition|(
name|version
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Missing VERSION key"
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|VERSION
operator|.
name|equals
argument_list|(
name|version
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Unknown VERSION '"
operator|+
name|version
operator|+
literal|"', expected "
operator|+
name|VERSION
argument_list|)
throw|;
block|}
if|if
condition|(
name|map
operator|.
name|containsKey
argument_list|(
name|STRING_KEY
argument_list|)
operator|&&
name|map
operator|.
name|containsKey
argument_list|(
name|BINARY_KEY
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Field cannot have both stringValue and binaryValue"
argument_list|)
throw|;
block|}
name|res
operator|.
name|str
operator|=
operator|(
name|String
operator|)
name|map
operator|.
name|get
argument_list|(
name|STRING_KEY
argument_list|)
expr_stmt|;
name|String
name|bin
init|=
operator|(
name|String
operator|)
name|map
operator|.
name|get
argument_list|(
name|BINARY_KEY
argument_list|)
decl_stmt|;
if|if
condition|(
name|bin
operator|!=
literal|null
condition|)
block|{
name|byte
index|[]
name|data
init|=
name|Base64
operator|.
name|base64ToByteArray
argument_list|(
name|bin
argument_list|)
decl_stmt|;
name|res
operator|.
name|bin
operator|=
name|data
expr_stmt|;
block|}
name|List
argument_list|<
name|Object
argument_list|>
name|tokens
init|=
operator|(
name|List
argument_list|<
name|Object
argument_list|>
operator|)
name|map
operator|.
name|get
argument_list|(
name|TOKENS_KEY
argument_list|)
decl_stmt|;
if|if
condition|(
name|tokens
operator|==
literal|null
condition|)
block|{
return|return
name|res
return|;
block|}
name|int
name|tokenStart
init|=
literal|0
decl_stmt|;
name|int
name|tokenEnd
init|=
literal|0
decl_stmt|;
name|parent
operator|.
name|clearAttributes
argument_list|()
expr_stmt|;
for|for
control|(
name|Object
name|ot
range|:
name|tokens
control|)
block|{
name|tokenStart
operator|=
name|tokenEnd
operator|+
literal|1
expr_stmt|;
comment|// automatic increment by 1 separator
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|tok
init|=
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|ot
decl_stmt|;
name|boolean
name|hasOffsetStart
init|=
literal|false
decl_stmt|;
name|boolean
name|hasOffsetEnd
init|=
literal|false
decl_stmt|;
name|int
name|len
init|=
operator|-
literal|1
decl_stmt|;
for|for
control|(
name|Entry
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|e
range|:
name|tok
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
name|key
init|=
name|e
operator|.
name|getKey
argument_list|()
decl_stmt|;
if|if
condition|(
name|key
operator|.
name|equals
argument_list|(
name|TOKEN_KEY
argument_list|)
condition|)
block|{
name|CharTermAttribute
name|catt
init|=
name|parent
operator|.
name|addAttribute
argument_list|(
name|CharTermAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|String
name|str
init|=
name|String
operator|.
name|valueOf
argument_list|(
name|e
operator|.
name|getValue
argument_list|()
argument_list|)
decl_stmt|;
name|catt
operator|.
name|append
argument_list|(
name|str
argument_list|)
expr_stmt|;
name|len
operator|=
name|str
operator|.
name|length
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|key
operator|.
name|equals
argument_list|(
name|OFFSET_START_KEY
argument_list|)
condition|)
block|{
name|Object
name|obj
init|=
name|e
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|hasOffsetStart
operator|=
literal|true
expr_stmt|;
if|if
condition|(
name|obj
operator|instanceof
name|Number
condition|)
block|{
name|tokenStart
operator|=
operator|(
operator|(
name|Number
operator|)
name|obj
operator|)
operator|.
name|intValue
argument_list|()
expr_stmt|;
block|}
else|else
block|{
try|try
block|{
name|tokenStart
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|obj
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|nfe
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Invalid "
operator|+
name|OFFSET_START_KEY
operator|+
literal|" attribute, skipped: '"
operator|+
name|obj
operator|+
literal|"'"
argument_list|)
expr_stmt|;
name|hasOffsetStart
operator|=
literal|false
expr_stmt|;
block|}
block|}
block|}
elseif|else
if|if
condition|(
name|key
operator|.
name|equals
argument_list|(
name|OFFSET_END_KEY
argument_list|)
condition|)
block|{
name|hasOffsetEnd
operator|=
literal|true
expr_stmt|;
name|Object
name|obj
init|=
name|e
operator|.
name|getValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|obj
operator|instanceof
name|Number
condition|)
block|{
name|tokenEnd
operator|=
operator|(
operator|(
name|Number
operator|)
name|obj
operator|)
operator|.
name|intValue
argument_list|()
expr_stmt|;
block|}
else|else
block|{
try|try
block|{
name|tokenEnd
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|obj
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|nfe
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Invalid "
operator|+
name|OFFSET_END_KEY
operator|+
literal|" attribute, skipped: '"
operator|+
name|obj
operator|+
literal|"'"
argument_list|)
expr_stmt|;
name|hasOffsetEnd
operator|=
literal|false
expr_stmt|;
block|}
block|}
block|}
elseif|else
if|if
condition|(
name|key
operator|.
name|equals
argument_list|(
name|POSINCR_KEY
argument_list|)
condition|)
block|{
name|Object
name|obj
init|=
name|e
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|int
name|posIncr
init|=
literal|1
decl_stmt|;
if|if
condition|(
name|obj
operator|instanceof
name|Number
condition|)
block|{
name|posIncr
operator|=
operator|(
operator|(
name|Number
operator|)
name|obj
operator|)
operator|.
name|intValue
argument_list|()
expr_stmt|;
block|}
else|else
block|{
try|try
block|{
name|posIncr
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|obj
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|nfe
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Invalid "
operator|+
name|POSINCR_KEY
operator|+
literal|" attribute, skipped: '"
operator|+
name|obj
operator|+
literal|"'"
argument_list|)
expr_stmt|;
block|}
block|}
name|PositionIncrementAttribute
name|patt
init|=
name|parent
operator|.
name|addAttribute
argument_list|(
name|PositionIncrementAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|patt
operator|.
name|setPositionIncrement
argument_list|(
name|posIncr
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|key
operator|.
name|equals
argument_list|(
name|PAYLOAD_KEY
argument_list|)
condition|)
block|{
name|String
name|str
init|=
name|String
operator|.
name|valueOf
argument_list|(
name|e
operator|.
name|getValue
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|str
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|byte
index|[]
name|data
init|=
name|Base64
operator|.
name|base64ToByteArray
argument_list|(
name|str
argument_list|)
decl_stmt|;
name|PayloadAttribute
name|p
init|=
name|parent
operator|.
name|addAttribute
argument_list|(
name|PayloadAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
if|if
condition|(
name|data
operator|!=
literal|null
operator|&&
name|data
operator|.
name|length
operator|>
literal|0
condition|)
block|{
name|p
operator|.
name|setPayload
argument_list|(
operator|new
name|BytesRef
argument_list|(
name|data
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
elseif|else
if|if
condition|(
name|key
operator|.
name|equals
argument_list|(
name|FLAGS_KEY
argument_list|)
condition|)
block|{
try|try
block|{
name|int
name|f
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|e
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|,
literal|16
argument_list|)
decl_stmt|;
name|FlagsAttribute
name|flags
init|=
name|parent
operator|.
name|addAttribute
argument_list|(
name|FlagsAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|flags
operator|.
name|setFlags
argument_list|(
name|f
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|nfe
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Invalid "
operator|+
name|FLAGS_KEY
operator|+
literal|" attribute, skipped: '"
operator|+
name|e
operator|.
name|getValue
argument_list|()
operator|+
literal|"'"
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|key
operator|.
name|equals
argument_list|(
name|TYPE_KEY
argument_list|)
condition|)
block|{
name|TypeAttribute
name|tattr
init|=
name|parent
operator|.
name|addAttribute
argument_list|(
name|TypeAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|tattr
operator|.
name|setType
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|e
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Unknown attribute, skipped: "
operator|+
name|e
operator|.
name|getKey
argument_list|()
operator|+
literal|"="
operator|+
name|e
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|// handle offset attr
name|OffsetAttribute
name|offset
init|=
name|parent
operator|.
name|addAttribute
argument_list|(
name|OffsetAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|hasOffsetEnd
operator|&&
name|len
operator|>
operator|-
literal|1
condition|)
block|{
name|tokenEnd
operator|=
name|tokenStart
operator|+
name|len
expr_stmt|;
block|}
name|offset
operator|.
name|setOffset
argument_list|(
name|tokenStart
argument_list|,
name|tokenEnd
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|hasOffsetStart
condition|)
block|{
name|tokenStart
operator|=
name|tokenEnd
operator|+
literal|1
expr_stmt|;
block|}
comment|// capture state and add to result
name|State
name|state
init|=
name|parent
operator|.
name|captureState
argument_list|()
decl_stmt|;
name|res
operator|.
name|states
operator|.
name|add
argument_list|(
name|state
operator|.
name|clone
argument_list|()
argument_list|)
expr_stmt|;
comment|// reset for reuse
name|parent
operator|.
name|clearAttributes
argument_list|()
expr_stmt|;
block|}
return|return
name|res
return|;
block|}
annotation|@
name|Override
DECL|method|toFormattedString
specifier|public
name|String
name|toFormattedString
parameter_list|(
name|Field
name|f
parameter_list|)
throws|throws
name|IOException
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|map
init|=
operator|new
name|LinkedHashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|map
operator|.
name|put
argument_list|(
name|VERSION_KEY
argument_list|,
name|VERSION
argument_list|)
expr_stmt|;
if|if
condition|(
name|f
operator|.
name|fieldType
argument_list|()
operator|.
name|stored
argument_list|()
condition|)
block|{
name|String
name|stringValue
init|=
name|f
operator|.
name|stringValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|stringValue
operator|!=
literal|null
condition|)
block|{
name|map
operator|.
name|put
argument_list|(
name|STRING_KEY
argument_list|,
name|stringValue
argument_list|)
expr_stmt|;
block|}
name|BytesRef
name|binaryValue
init|=
name|f
operator|.
name|binaryValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|binaryValue
operator|!=
literal|null
condition|)
block|{
name|map
operator|.
name|put
argument_list|(
name|BINARY_KEY
argument_list|,
name|Base64
operator|.
name|byteArrayToBase64
argument_list|(
name|binaryValue
operator|.
name|bytes
argument_list|,
name|binaryValue
operator|.
name|offset
argument_list|,
name|binaryValue
operator|.
name|length
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|TokenStream
name|ts
init|=
name|f
operator|.
name|tokenStreamValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|ts
operator|!=
literal|null
condition|)
block|{
name|List
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|tokens
init|=
operator|new
name|LinkedList
argument_list|<>
argument_list|()
decl_stmt|;
while|while
condition|(
name|ts
operator|.
name|incrementToken
argument_list|()
condition|)
block|{
name|Iterator
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|Attribute
argument_list|>
argument_list|>
name|it
init|=
name|ts
operator|.
name|getAttributeClassesIterator
argument_list|()
decl_stmt|;
name|String
name|cTerm
init|=
literal|null
decl_stmt|;
name|String
name|tTerm
init|=
literal|null
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|tok
init|=
operator|new
name|TreeMap
argument_list|<>
argument_list|()
decl_stmt|;
while|while
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Class
argument_list|<
name|?
extends|extends
name|Attribute
argument_list|>
name|cl
init|=
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
name|Attribute
name|att
init|=
name|ts
operator|.
name|getAttribute
argument_list|(
name|cl
argument_list|)
decl_stmt|;
if|if
condition|(
name|att
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
if|if
condition|(
name|cl
operator|.
name|isAssignableFrom
argument_list|(
name|CharTermAttribute
operator|.
name|class
argument_list|)
condition|)
block|{
name|CharTermAttribute
name|catt
init|=
operator|(
name|CharTermAttribute
operator|)
name|att
decl_stmt|;
name|cTerm
operator|=
operator|new
name|String
argument_list|(
name|catt
operator|.
name|buffer
argument_list|()
argument_list|,
literal|0
argument_list|,
name|catt
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|cl
operator|.
name|isAssignableFrom
argument_list|(
name|TermToBytesRefAttribute
operator|.
name|class
argument_list|)
condition|)
block|{
name|TermToBytesRefAttribute
name|tatt
init|=
operator|(
name|TermToBytesRefAttribute
operator|)
name|att
decl_stmt|;
name|tTerm
operator|=
name|tatt
operator|.
name|getBytesRef
argument_list|()
operator|.
name|utf8ToString
argument_list|()
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|cl
operator|.
name|isAssignableFrom
argument_list|(
name|FlagsAttribute
operator|.
name|class
argument_list|)
condition|)
block|{
name|tok
operator|.
name|put
argument_list|(
name|FLAGS_KEY
argument_list|,
name|Integer
operator|.
name|toHexString
argument_list|(
operator|(
operator|(
name|FlagsAttribute
operator|)
name|att
operator|)
operator|.
name|getFlags
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|cl
operator|.
name|isAssignableFrom
argument_list|(
name|OffsetAttribute
operator|.
name|class
argument_list|)
condition|)
block|{
name|tok
operator|.
name|put
argument_list|(
name|OFFSET_START_KEY
argument_list|,
operator|(
operator|(
name|OffsetAttribute
operator|)
name|att
operator|)
operator|.
name|startOffset
argument_list|()
argument_list|)
expr_stmt|;
name|tok
operator|.
name|put
argument_list|(
name|OFFSET_END_KEY
argument_list|,
operator|(
operator|(
name|OffsetAttribute
operator|)
name|att
operator|)
operator|.
name|endOffset
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|cl
operator|.
name|isAssignableFrom
argument_list|(
name|PayloadAttribute
operator|.
name|class
argument_list|)
condition|)
block|{
name|BytesRef
name|p
init|=
operator|(
operator|(
name|PayloadAttribute
operator|)
name|att
operator|)
operator|.
name|getPayload
argument_list|()
decl_stmt|;
if|if
condition|(
name|p
operator|!=
literal|null
operator|&&
name|p
operator|.
name|length
operator|>
literal|0
condition|)
block|{
name|tok
operator|.
name|put
argument_list|(
name|PAYLOAD_KEY
argument_list|,
name|Base64
operator|.
name|byteArrayToBase64
argument_list|(
name|p
operator|.
name|bytes
argument_list|,
name|p
operator|.
name|offset
argument_list|,
name|p
operator|.
name|length
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|cl
operator|.
name|isAssignableFrom
argument_list|(
name|PositionIncrementAttribute
operator|.
name|class
argument_list|)
condition|)
block|{
name|tok
operator|.
name|put
argument_list|(
name|POSINCR_KEY
argument_list|,
operator|(
operator|(
name|PositionIncrementAttribute
operator|)
name|att
operator|)
operator|.
name|getPositionIncrement
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|cl
operator|.
name|isAssignableFrom
argument_list|(
name|TypeAttribute
operator|.
name|class
argument_list|)
condition|)
block|{
name|tok
operator|.
name|put
argument_list|(
name|TYPE_KEY
argument_list|,
operator|(
operator|(
name|TypeAttribute
operator|)
name|att
operator|)
operator|.
name|type
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|tok
operator|.
name|put
argument_list|(
name|cl
operator|.
name|getName
argument_list|()
argument_list|,
name|att
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|String
name|term
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|cTerm
operator|!=
literal|null
condition|)
block|{
name|term
operator|=
name|cTerm
expr_stmt|;
block|}
else|else
block|{
name|term
operator|=
name|tTerm
expr_stmt|;
block|}
if|if
condition|(
name|term
operator|!=
literal|null
operator|&&
name|term
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|tok
operator|.
name|put
argument_list|(
name|TOKEN_KEY
argument_list|,
name|term
argument_list|)
expr_stmt|;
block|}
name|tokens
operator|.
name|add
argument_list|(
name|tok
argument_list|)
expr_stmt|;
block|}
name|map
operator|.
name|put
argument_list|(
name|TOKENS_KEY
argument_list|,
name|tokens
argument_list|)
expr_stmt|;
block|}
return|return
name|JSONUtil
operator|.
name|toJSON
argument_list|(
name|map
argument_list|,
operator|-
literal|1
argument_list|)
return|;
block|}
block|}
end_class
end_unit
