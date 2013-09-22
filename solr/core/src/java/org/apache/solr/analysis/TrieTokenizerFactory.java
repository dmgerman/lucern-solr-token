begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.analysis
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|analysis
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
name|analysis
operator|.
name|NumericTokenStream
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
name|CharTermAttributeImpl
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
name|Tokenizer
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
name|util
operator|.
name|TokenizerFactory
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
name|AttributeImpl
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
name|AttributeFactory
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
name|schema
operator|.
name|DateField
import|;
end_import
begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|schema
operator|.
name|TrieField
operator|.
name|TrieTypes
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
name|Reader
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
name|Iterator
import|;
end_import
begin_comment
comment|/**  * Tokenizer for trie fields. It uses NumericTokenStream to create multiple trie encoded string per number.  * Each string created by this tokenizer for a given number differs from the previous by the given precisionStep.  * For query time token streams that only contain the highest precision term, use 32/64 as precisionStep.  *<p/>  * Refer to {@link org.apache.lucene.search.NumericRangeQuery} for more details.  *  *  * @see org.apache.lucene.search.NumericRangeQuery  * @see org.apache.solr.schema.TrieField  * @since solr 1.4  */
end_comment
begin_class
DECL|class|TrieTokenizerFactory
specifier|public
class|class
name|TrieTokenizerFactory
extends|extends
name|TokenizerFactory
block|{
DECL|field|precisionStep
specifier|protected
specifier|final
name|int
name|precisionStep
decl_stmt|;
DECL|field|type
specifier|protected
specifier|final
name|TrieTypes
name|type
decl_stmt|;
DECL|method|TrieTokenizerFactory
specifier|public
name|TrieTokenizerFactory
parameter_list|(
name|TrieTypes
name|type
parameter_list|,
name|int
name|precisionStep
parameter_list|)
block|{
name|super
argument_list|(
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
name|this
operator|.
name|precisionStep
operator|=
name|precisionStep
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|create
specifier|public
name|TrieTokenizer
name|create
parameter_list|(
name|AttributeFactory
name|factory
parameter_list|,
name|Reader
name|input
parameter_list|)
block|{
return|return
operator|new
name|TrieTokenizer
argument_list|(
name|input
argument_list|,
name|type
argument_list|,
name|TrieTokenizer
operator|.
name|getNumericTokenStream
argument_list|(
name|factory
argument_list|,
name|precisionStep
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class
begin_class
DECL|class|TrieTokenizer
specifier|final
class|class
name|TrieTokenizer
extends|extends
name|Tokenizer
block|{
DECL|field|dateField
specifier|protected
specifier|static
specifier|final
name|DateField
name|dateField
init|=
operator|new
name|DateField
argument_list|()
decl_stmt|;
DECL|field|type
specifier|protected
specifier|final
name|TrieTypes
name|type
decl_stmt|;
DECL|field|ts
specifier|protected
specifier|final
name|NumericTokenStream
name|ts
decl_stmt|;
comment|// NumericTokenStream does not support CharTermAttribute so keep it local
DECL|field|termAtt
specifier|private
specifier|final
name|CharTermAttribute
name|termAtt
init|=
operator|new
name|CharTermAttributeImpl
argument_list|()
decl_stmt|;
DECL|field|ofsAtt
specifier|protected
specifier|final
name|OffsetAttribute
name|ofsAtt
init|=
name|addAttribute
argument_list|(
name|OffsetAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|startOfs
DECL|field|endOfs
specifier|protected
name|int
name|startOfs
decl_stmt|,
name|endOfs
decl_stmt|;
DECL|field|hasValue
specifier|protected
name|boolean
name|hasValue
decl_stmt|;
DECL|method|getNumericTokenStream
specifier|static
name|NumericTokenStream
name|getNumericTokenStream
parameter_list|(
name|AttributeFactory
name|factory
parameter_list|,
name|int
name|precisionStep
parameter_list|)
block|{
return|return
operator|new
name|NumericTokenStream
argument_list|(
name|factory
argument_list|,
name|precisionStep
argument_list|)
return|;
block|}
DECL|method|TrieTokenizer
specifier|public
name|TrieTokenizer
parameter_list|(
name|Reader
name|input
parameter_list|,
name|TrieTypes
name|type
parameter_list|,
specifier|final
name|NumericTokenStream
name|ts
parameter_list|)
block|{
comment|// HÃ¤ckidy-Hick-Hack: must share the attributes with the NumericTokenStream we delegate to, so we create a fake factory:
name|super
argument_list|(
operator|new
name|AttributeFactory
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|AttributeImpl
name|createAttributeInstance
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|Attribute
argument_list|>
name|attClass
parameter_list|)
block|{
return|return
operator|(
name|AttributeImpl
operator|)
name|ts
operator|.
name|addAttribute
argument_list|(
name|attClass
argument_list|)
return|;
block|}
block|}
argument_list|,
name|input
argument_list|)
expr_stmt|;
comment|// add all attributes:
for|for
control|(
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
init|;
name|it
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|addAttribute
argument_list|(
name|it
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
name|this
operator|.
name|ts
operator|=
name|ts
expr_stmt|;
comment|// dates tend to be longer, especially when math is involved
name|termAtt
operator|.
name|resizeBuffer
argument_list|(
name|type
operator|==
name|TrieTypes
operator|.
name|DATE
condition|?
literal|128
else|:
literal|32
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|reset
specifier|public
name|void
name|reset
parameter_list|()
throws|throws
name|IOException
block|{
name|super
operator|.
name|reset
argument_list|()
expr_stmt|;
try|try
block|{
name|int
name|upto
init|=
literal|0
decl_stmt|;
name|char
index|[]
name|buf
init|=
name|termAtt
operator|.
name|buffer
argument_list|()
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
specifier|final
name|int
name|length
init|=
name|input
operator|.
name|read
argument_list|(
name|buf
argument_list|,
name|upto
argument_list|,
name|buf
operator|.
name|length
operator|-
name|upto
argument_list|)
decl_stmt|;
if|if
condition|(
name|length
operator|==
operator|-
literal|1
condition|)
break|break;
name|upto
operator|+=
name|length
expr_stmt|;
if|if
condition|(
name|upto
operator|==
name|buf
operator|.
name|length
condition|)
name|buf
operator|=
name|termAtt
operator|.
name|resizeBuffer
argument_list|(
literal|1
operator|+
name|buf
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
name|termAtt
operator|.
name|setLength
argument_list|(
name|upto
argument_list|)
expr_stmt|;
name|this
operator|.
name|startOfs
operator|=
name|correctOffset
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|this
operator|.
name|endOfs
operator|=
name|correctOffset
argument_list|(
name|upto
argument_list|)
expr_stmt|;
if|if
condition|(
name|upto
operator|==
literal|0
condition|)
block|{
name|hasValue
operator|=
literal|false
expr_stmt|;
return|return;
block|}
specifier|final
name|String
name|v
init|=
operator|new
name|String
argument_list|(
name|buf
argument_list|,
literal|0
argument_list|,
name|upto
argument_list|)
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
name|ts
operator|.
name|setIntValue
argument_list|(
name|Integer
operator|.
name|parseInt
argument_list|(
name|v
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
name|FLOAT
case|:
name|ts
operator|.
name|setFloatValue
argument_list|(
name|Float
operator|.
name|parseFloat
argument_list|(
name|v
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
name|LONG
case|:
name|ts
operator|.
name|setLongValue
argument_list|(
name|Long
operator|.
name|parseLong
argument_list|(
name|v
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
name|DOUBLE
case|:
name|ts
operator|.
name|setDoubleValue
argument_list|(
name|Double
operator|.
name|parseDouble
argument_list|(
name|v
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
name|DATE
case|:
name|ts
operator|.
name|setLongValue
argument_list|(
name|dateField
operator|.
name|parseMath
argument_list|(
literal|null
argument_list|,
name|v
argument_list|)
operator|.
name|getTime
argument_list|()
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
name|SERVER_ERROR
argument_list|,
literal|"Unknown type for trie field"
argument_list|)
throw|;
block|}
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|nfe
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
literal|"Invalid Number: "
operator|+
name|v
argument_list|)
throw|;
block|}
name|hasValue
operator|=
literal|true
expr_stmt|;
name|ts
operator|.
name|reset
argument_list|()
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
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|"Unable to create TrieIndexTokenizer"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|super
operator|.
name|close
argument_list|()
expr_stmt|;
if|if
condition|(
name|hasValue
condition|)
block|{
name|ts
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|incrementToken
specifier|public
name|boolean
name|incrementToken
parameter_list|()
block|{
if|if
condition|(
name|hasValue
operator|&&
name|ts
operator|.
name|incrementToken
argument_list|()
condition|)
block|{
name|ofsAtt
operator|.
name|setOffset
argument_list|(
name|startOfs
argument_list|,
name|endOfs
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
annotation|@
name|Override
DECL|method|end
specifier|public
name|void
name|end
parameter_list|()
throws|throws
name|IOException
block|{
name|super
operator|.
name|end
argument_list|()
expr_stmt|;
if|if
condition|(
name|hasValue
condition|)
block|{
name|ts
operator|.
name|end
argument_list|()
expr_stmt|;
block|}
name|ofsAtt
operator|.
name|setOffset
argument_list|(
name|endOfs
argument_list|,
name|endOfs
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
