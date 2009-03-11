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
name|lucene
operator|.
name|search
operator|.
name|Filter
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
name|SortField
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
name|lucene
operator|.
name|search
operator|.
name|ConstantScoreQuery
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
name|trie
operator|.
name|IntTrieRangeFilter
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
name|trie
operator|.
name|LongTrieRangeFilter
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
name|trie
operator|.
name|TrieUtils
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
name|analysis
operator|.
name|*
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
name|request
operator|.
name|TextResponseWriter
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
name|util
operator|.
name|Map
import|;
end_import
begin_comment
comment|/**  * Provides field types to support for Lucene's Trie Range Queries. See {@linkplain org.apache.lucene.search.trie  * package description} for more details. It supports integer, float, long, double and date types.  *<p/>  * For each number being added to this field, multiple terms are generated as per the algorithm described in the above  * link. The possible number of terms increases dramatically with higher precision steps (factor 2^precisionStep). For  * the fast range search to work, trie fields must be indexed.  *<p/>  * Trie fields are<b>not</b> sortable in numerical order. Also, they cannot be used in function queries. If one needs  * sorting as well as fast range search, one should create a copy field specifically for sorting. Same workaround is  * suggested for using trie fields in function queries as well.  *<p/>  * Note that if you use a precisionStep of 32 for int/float and 64 for long/double, then multiple terms will not be  * generated, range search will be no faster than any other number field, but sorting will be possible.  *  * @version $Id$  * @see org.apache.lucene.search.trie.TrieUtils  * @since solr 1.4  */
end_comment
begin_class
DECL|class|TrieField
specifier|public
class|class
name|TrieField
extends|extends
name|FieldType
block|{
DECL|field|DEFAULT_PRECISION_STEP
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_PRECISION_STEP
init|=
literal|8
decl_stmt|;
DECL|field|precisionStep
specifier|protected
name|int
name|precisionStep
init|=
name|TrieField
operator|.
name|DEFAULT_PRECISION_STEP
decl_stmt|;
DECL|field|type
specifier|protected
name|TrieTypes
name|type
decl_stmt|;
comment|/**    * Used for handling date types following the same semantics as DateField    */
DECL|field|dateField
specifier|private
specifier|static
specifier|final
name|DateField
name|dateField
init|=
operator|new
name|DateField
argument_list|()
decl_stmt|;
annotation|@
name|Override
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
name|String
name|p
init|=
name|args
operator|.
name|remove
argument_list|(
literal|"precisionStep"
argument_list|)
decl_stmt|;
if|if
condition|(
name|p
operator|!=
literal|null
condition|)
block|{
name|precisionStep
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|p
argument_list|)
expr_stmt|;
block|}
name|String
name|t
init|=
name|args
operator|.
name|remove
argument_list|(
literal|"type"
argument_list|)
decl_stmt|;
if|if
condition|(
name|t
operator|==
literal|null
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
name|SERVER_ERROR
argument_list|,
literal|"Invalid type specified in schema.xml for field: "
operator|+
name|args
operator|.
name|get
argument_list|(
literal|"name"
argument_list|)
argument_list|)
throw|;
block|}
else|else
block|{
try|try
block|{
name|type
operator|=
name|TrieTypes
operator|.
name|valueOf
argument_list|(
name|t
operator|.
name|toUpperCase
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
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
literal|"Invalid type specified in schema.xml for field: "
operator|+
name|args
operator|.
name|get
argument_list|(
literal|"name"
argument_list|)
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
name|CharFilterFactory
index|[]
name|filterFactories
init|=
operator|new
name|CharFilterFactory
index|[
literal|0
index|]
decl_stmt|;
name|TokenFilterFactory
index|[]
name|tokenFilterFactories
init|=
operator|new
name|TokenFilterFactory
index|[
literal|0
index|]
decl_stmt|;
name|analyzer
operator|=
operator|new
name|TokenizerChain
argument_list|(
name|filterFactories
argument_list|,
operator|new
name|TrieIndexTokenizerFactory
argument_list|(
name|type
argument_list|,
name|precisionStep
argument_list|)
argument_list|,
name|tokenFilterFactories
argument_list|)
expr_stmt|;
name|queryAnalyzer
operator|=
operator|new
name|TokenizerChain
argument_list|(
name|filterFactories
argument_list|,
operator|new
name|TrieQueryTokenizerFactory
argument_list|(
name|type
argument_list|)
argument_list|,
name|tokenFilterFactories
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|toObject
specifier|public
name|Object
name|toObject
parameter_list|(
name|Fieldable
name|f
parameter_list|)
block|{
name|String
name|s
init|=
name|f
operator|.
name|stringValue
argument_list|()
decl_stmt|;
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
name|s
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
name|s
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
name|s
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
name|s
argument_list|)
return|;
case|case
name|DATE
case|:
return|return
name|dateField
operator|.
name|toObject
argument_list|(
name|f
argument_list|)
return|;
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
literal|"Unknown type for trie field: "
operator|+
name|f
operator|.
name|name
argument_list|()
argument_list|)
throw|;
block|}
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
name|top
parameter_list|)
block|{
switch|switch
condition|(
name|type
condition|)
block|{
case|case
name|INTEGER
case|:
case|case
name|FLOAT
case|:
return|return
name|TrieUtils
operator|.
name|getIntSortField
argument_list|(
name|field
operator|.
name|getName
argument_list|()
argument_list|,
name|top
argument_list|)
return|;
case|case
name|LONG
case|:
case|case
name|DOUBLE
case|:
case|case
name|DATE
case|:
return|return
name|TrieUtils
operator|.
name|getLongSortField
argument_list|(
name|field
operator|.
name|getName
argument_list|()
argument_list|,
name|top
argument_list|)
return|;
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
literal|"Unknown type for trie field: "
operator|+
name|field
operator|.
name|name
argument_list|)
throw|;
block|}
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
name|writeVal
argument_list|(
name|name
argument_list|,
name|toObject
argument_list|(
name|f
argument_list|)
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
name|writer
operator|.
name|writeVal
argument_list|(
name|name
argument_list|,
name|toObject
argument_list|(
name|f
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|isTokenized
specifier|public
name|boolean
name|isTokenized
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
comment|/**    * @return the precisionStep used to index values into the field    */
DECL|method|getPrecisionStep
specifier|public
name|int
name|getPrecisionStep
parameter_list|()
block|{
return|return
name|precisionStep
return|;
block|}
comment|/**    * @return the type of this field    */
DECL|method|getType
specifier|public
name|TrieTypes
name|getType
parameter_list|()
block|{
return|return
name|type
return|;
block|}
annotation|@
name|Override
DECL|method|getRangeQuery
specifier|public
name|Query
name|getRangeQuery
parameter_list|(
name|String
name|field
parameter_list|,
name|String
name|min
parameter_list|,
name|String
name|max
parameter_list|,
name|boolean
name|inclusive
parameter_list|)
block|{
name|Filter
name|filter
init|=
literal|null
decl_stmt|;
switch|switch
condition|(
name|type
condition|)
block|{
case|case
name|INTEGER
case|:
name|filter
operator|=
operator|new
name|IntTrieRangeFilter
argument_list|(
name|field
argument_list|,
name|field
argument_list|,
name|precisionStep
argument_list|,
literal|"*"
operator|.
name|equals
argument_list|(
name|min
argument_list|)
condition|?
literal|null
else|:
name|Integer
operator|.
name|parseInt
argument_list|(
name|min
argument_list|)
argument_list|,
literal|"*"
operator|.
name|equals
argument_list|(
name|max
argument_list|)
condition|?
literal|null
else|:
name|Integer
operator|.
name|parseInt
argument_list|(
name|max
argument_list|)
argument_list|,
name|inclusive
argument_list|,
name|inclusive
argument_list|)
expr_stmt|;
break|break;
case|case
name|FLOAT
case|:
name|filter
operator|=
operator|new
name|IntTrieRangeFilter
argument_list|(
name|field
argument_list|,
name|field
argument_list|,
name|precisionStep
argument_list|,
literal|"*"
operator|.
name|equals
argument_list|(
name|min
argument_list|)
condition|?
literal|null
else|:
name|TrieUtils
operator|.
name|floatToSortableInt
argument_list|(
name|Float
operator|.
name|parseFloat
argument_list|(
name|min
argument_list|)
argument_list|)
argument_list|,
literal|"*"
operator|.
name|equals
argument_list|(
name|max
argument_list|)
condition|?
literal|null
else|:
name|TrieUtils
operator|.
name|floatToSortableInt
argument_list|(
name|Float
operator|.
name|parseFloat
argument_list|(
name|max
argument_list|)
argument_list|)
argument_list|,
name|inclusive
argument_list|,
name|inclusive
argument_list|)
expr_stmt|;
break|break;
case|case
name|LONG
case|:
name|filter
operator|=
operator|new
name|LongTrieRangeFilter
argument_list|(
name|field
argument_list|,
name|field
argument_list|,
name|precisionStep
argument_list|,
literal|"*"
operator|.
name|equals
argument_list|(
name|min
argument_list|)
condition|?
literal|null
else|:
name|Long
operator|.
name|parseLong
argument_list|(
name|min
argument_list|)
argument_list|,
literal|"*"
operator|.
name|equals
argument_list|(
name|max
argument_list|)
condition|?
literal|null
else|:
name|Long
operator|.
name|parseLong
argument_list|(
name|max
argument_list|)
argument_list|,
name|inclusive
argument_list|,
name|inclusive
argument_list|)
expr_stmt|;
break|break;
case|case
name|DOUBLE
case|:
name|filter
operator|=
operator|new
name|LongTrieRangeFilter
argument_list|(
name|field
argument_list|,
name|field
argument_list|,
name|precisionStep
argument_list|,
literal|"*"
operator|.
name|equals
argument_list|(
name|min
argument_list|)
condition|?
literal|null
else|:
name|TrieUtils
operator|.
name|doubleToSortableLong
argument_list|(
name|Double
operator|.
name|parseDouble
argument_list|(
name|min
argument_list|)
argument_list|)
argument_list|,
literal|"*"
operator|.
name|equals
argument_list|(
name|max
argument_list|)
condition|?
literal|null
else|:
name|TrieUtils
operator|.
name|doubleToSortableLong
argument_list|(
name|Double
operator|.
name|parseDouble
argument_list|(
name|max
argument_list|)
argument_list|)
argument_list|,
name|inclusive
argument_list|,
name|inclusive
argument_list|)
expr_stmt|;
break|break;
case|case
name|DATE
case|:
name|filter
operator|=
operator|new
name|LongTrieRangeFilter
argument_list|(
name|field
argument_list|,
name|field
argument_list|,
name|precisionStep
argument_list|,
literal|"*"
operator|.
name|equals
argument_list|(
name|min
argument_list|)
condition|?
literal|null
else|:
name|dateField
operator|.
name|parseMath
argument_list|(
literal|null
argument_list|,
name|min
argument_list|)
operator|.
name|getTime
argument_list|()
argument_list|,
literal|"*"
operator|.
name|equals
argument_list|(
name|max
argument_list|)
condition|?
literal|null
else|:
name|dateField
operator|.
name|parseMath
argument_list|(
literal|null
argument_list|,
name|max
argument_list|)
operator|.
name|getTime
argument_list|()
argument_list|,
name|inclusive
argument_list|,
name|inclusive
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
return|return
operator|new
name|ConstantScoreQuery
argument_list|(
name|filter
argument_list|)
return|;
block|}
DECL|enum|TrieTypes
specifier|public
enum|enum
name|TrieTypes
block|{
DECL|enum constant|INTEGER
name|INTEGER
block|,
DECL|enum constant|LONG
name|LONG
block|,
DECL|enum constant|FLOAT
name|FLOAT
block|,
DECL|enum constant|DOUBLE
name|DOUBLE
block|,
DECL|enum constant|DATE
name|DATE
block|}
block|}
end_class
end_unit
