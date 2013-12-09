begin_unit
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
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|text
operator|.
name|Collator
import|;
end_import
begin_import
import|import
name|java
operator|.
name|text
operator|.
name|ParseException
import|;
end_import
begin_import
import|import
name|java
operator|.
name|text
operator|.
name|RuleBasedCollator
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
name|List
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
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
name|commons
operator|.
name|io
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
name|lucene
operator|.
name|analysis
operator|.
name|Analyzer
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
name|collation
operator|.
name|CollationKeyAnalyzer
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
name|SortedDocValuesField
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
name|SortedSetDocValuesField
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
name|index
operator|.
name|StorableField
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
name|DocTermOrdsRangeFilter
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
name|FieldCacheRangeFilter
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
name|TermRangeQuery
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
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|Version
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
name|ResourceLoader
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
name|SolrException
operator|.
name|ErrorCode
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
name|response
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
name|search
operator|.
name|QParser
import|;
end_import
begin_comment
comment|/**  * Field for collated sort keys.   * These can be used for locale-sensitive sort and range queries.  *<p>  * This field can be created in two ways:   *<ul>  *<li>Based upon a system collator associated with a Locale.  *<li>Based upon a tailored ruleset.  *</ul>  *<p>  * Using a System collator:  *<ul>  *<li>language: ISO-639 language code (mandatory)  *<li>country: ISO-3166 country code (optional)  *<li>variant: vendor or browser-specific code (optional)  *<li>strength: 'primary','secondary','tertiary', or 'identical' (optional)  *<li>decomposition: 'no','canonical', or 'full' (optional)  *</ul>  *<p>  * Using a Tailored ruleset:  *<ul>  *<li>custom: UTF-8 text file containing rules supported by RuleBasedCollator (mandatory)  *<li>strength: 'primary','secondary','tertiary', or 'identical' (optional)  *<li>decomposition: 'no','canonical', or 'full' (optional)  *</ul>  *   * @see Collator  * @see Locale  * @see RuleBasedCollator  * @since solr 4.0  */
end_comment
begin_class
DECL|class|CollationField
specifier|public
class|class
name|CollationField
extends|extends
name|FieldType
block|{
DECL|field|analyzer
specifier|private
name|Analyzer
name|analyzer
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
name|properties
operator||=
name|TOKENIZED
expr_stmt|;
comment|// this ensures our analyzer gets hit
name|setup
argument_list|(
name|schema
operator|.
name|getResourceLoader
argument_list|()
argument_list|,
name|args
argument_list|)
expr_stmt|;
name|super
operator|.
name|init
argument_list|(
name|schema
argument_list|,
name|args
argument_list|)
expr_stmt|;
block|}
comment|/**    * Setup the field according to the provided parameters    */
DECL|method|setup
specifier|private
name|void
name|setup
parameter_list|(
name|ResourceLoader
name|loader
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
name|custom
init|=
name|args
operator|.
name|remove
argument_list|(
literal|"custom"
argument_list|)
decl_stmt|;
name|String
name|language
init|=
name|args
operator|.
name|remove
argument_list|(
literal|"language"
argument_list|)
decl_stmt|;
name|String
name|country
init|=
name|args
operator|.
name|remove
argument_list|(
literal|"country"
argument_list|)
decl_stmt|;
name|String
name|variant
init|=
name|args
operator|.
name|remove
argument_list|(
literal|"variant"
argument_list|)
decl_stmt|;
name|String
name|strength
init|=
name|args
operator|.
name|remove
argument_list|(
literal|"strength"
argument_list|)
decl_stmt|;
name|String
name|decomposition
init|=
name|args
operator|.
name|remove
argument_list|(
literal|"decomposition"
argument_list|)
decl_stmt|;
specifier|final
name|Collator
name|collator
decl_stmt|;
if|if
condition|(
name|custom
operator|==
literal|null
operator|&&
name|language
operator|==
literal|null
condition|)
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|"Either custom or language is required."
argument_list|)
throw|;
if|if
condition|(
name|custom
operator|!=
literal|null
operator|&&
operator|(
name|language
operator|!=
literal|null
operator|||
name|country
operator|!=
literal|null
operator|||
name|variant
operator|!=
literal|null
operator|)
condition|)
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|"Cannot specify both language and custom. "
operator|+
literal|"To tailor rules for a built-in language, see the javadocs for RuleBasedCollator. "
operator|+
literal|"Then save the entire customized ruleset to a file, and use with the custom parameter"
argument_list|)
throw|;
if|if
condition|(
name|language
operator|!=
literal|null
condition|)
block|{
comment|// create from a system collator, based on Locale.
name|collator
operator|=
name|createFromLocale
argument_list|(
name|language
argument_list|,
name|country
argument_list|,
name|variant
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// create from a custom ruleset
name|collator
operator|=
name|createFromRules
argument_list|(
name|custom
argument_list|,
name|loader
argument_list|)
expr_stmt|;
block|}
comment|// set the strength flag, otherwise it will be the default.
if|if
condition|(
name|strength
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|strength
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"primary"
argument_list|)
condition|)
name|collator
operator|.
name|setStrength
argument_list|(
name|Collator
operator|.
name|PRIMARY
argument_list|)
expr_stmt|;
elseif|else
if|if
condition|(
name|strength
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"secondary"
argument_list|)
condition|)
name|collator
operator|.
name|setStrength
argument_list|(
name|Collator
operator|.
name|SECONDARY
argument_list|)
expr_stmt|;
elseif|else
if|if
condition|(
name|strength
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"tertiary"
argument_list|)
condition|)
name|collator
operator|.
name|setStrength
argument_list|(
name|Collator
operator|.
name|TERTIARY
argument_list|)
expr_stmt|;
elseif|else
if|if
condition|(
name|strength
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"identical"
argument_list|)
condition|)
name|collator
operator|.
name|setStrength
argument_list|(
name|Collator
operator|.
name|IDENTICAL
argument_list|)
expr_stmt|;
else|else
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|"Invalid strength: "
operator|+
name|strength
argument_list|)
throw|;
block|}
comment|// set the decomposition flag, otherwise it will be the default.
if|if
condition|(
name|decomposition
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|decomposition
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"no"
argument_list|)
condition|)
name|collator
operator|.
name|setDecomposition
argument_list|(
name|Collator
operator|.
name|NO_DECOMPOSITION
argument_list|)
expr_stmt|;
elseif|else
if|if
condition|(
name|decomposition
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"canonical"
argument_list|)
condition|)
name|collator
operator|.
name|setDecomposition
argument_list|(
name|Collator
operator|.
name|CANONICAL_DECOMPOSITION
argument_list|)
expr_stmt|;
elseif|else
if|if
condition|(
name|decomposition
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"full"
argument_list|)
condition|)
name|collator
operator|.
name|setDecomposition
argument_list|(
name|Collator
operator|.
name|FULL_DECOMPOSITION
argument_list|)
expr_stmt|;
else|else
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|"Invalid decomposition: "
operator|+
name|decomposition
argument_list|)
throw|;
block|}
name|analyzer
operator|=
operator|new
name|CollationKeyAnalyzer
argument_list|(
name|Version
operator|.
name|LUCENE_CURRENT
argument_list|,
name|collator
argument_list|)
expr_stmt|;
block|}
comment|/**    * Create a locale from language, with optional country and variant.    * Then return the appropriate collator for the locale.    */
DECL|method|createFromLocale
specifier|private
name|Collator
name|createFromLocale
parameter_list|(
name|String
name|language
parameter_list|,
name|String
name|country
parameter_list|,
name|String
name|variant
parameter_list|)
block|{
name|Locale
name|locale
decl_stmt|;
if|if
condition|(
name|language
operator|!=
literal|null
operator|&&
name|country
operator|==
literal|null
operator|&&
name|variant
operator|!=
literal|null
condition|)
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|"To specify variant, country is required"
argument_list|)
throw|;
elseif|else
if|if
condition|(
name|language
operator|!=
literal|null
operator|&&
name|country
operator|!=
literal|null
operator|&&
name|variant
operator|!=
literal|null
condition|)
name|locale
operator|=
operator|new
name|Locale
argument_list|(
name|language
argument_list|,
name|country
argument_list|,
name|variant
argument_list|)
expr_stmt|;
elseif|else
if|if
condition|(
name|language
operator|!=
literal|null
operator|&&
name|country
operator|!=
literal|null
condition|)
name|locale
operator|=
operator|new
name|Locale
argument_list|(
name|language
argument_list|,
name|country
argument_list|)
expr_stmt|;
else|else
name|locale
operator|=
operator|new
name|Locale
argument_list|(
name|language
argument_list|)
expr_stmt|;
return|return
name|Collator
operator|.
name|getInstance
argument_list|(
name|locale
argument_list|)
return|;
block|}
comment|/**    * Read custom rules from a file, and create a RuleBasedCollator    * The file cannot support comments, as # might be in the rules!    */
DECL|method|createFromRules
specifier|private
name|Collator
name|createFromRules
parameter_list|(
name|String
name|fileName
parameter_list|,
name|ResourceLoader
name|loader
parameter_list|)
block|{
name|InputStream
name|input
init|=
literal|null
decl_stmt|;
try|try
block|{
name|input
operator|=
name|loader
operator|.
name|openResource
argument_list|(
name|fileName
argument_list|)
expr_stmt|;
name|String
name|rules
init|=
name|IOUtils
operator|.
name|toString
argument_list|(
name|input
argument_list|,
literal|"UTF-8"
argument_list|)
decl_stmt|;
return|return
operator|new
name|RuleBasedCollator
argument_list|(
name|rules
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// io error
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|ParseException
name|e
parameter_list|)
block|{
comment|// invalid rules
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeQuietly
argument_list|(
name|input
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
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
name|StorableField
name|f
parameter_list|)
throws|throws
name|IOException
block|{
name|writer
operator|.
name|writeStr
argument_list|(
name|name
argument_list|,
name|f
operator|.
name|stringValue
argument_list|()
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
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
return|return
name|getStringSort
argument_list|(
name|field
argument_list|,
name|top
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getAnalyzer
specifier|public
name|Analyzer
name|getAnalyzer
parameter_list|()
block|{
return|return
name|analyzer
return|;
block|}
annotation|@
name|Override
DECL|method|getQueryAnalyzer
specifier|public
name|Analyzer
name|getQueryAnalyzer
parameter_list|()
block|{
return|return
name|analyzer
return|;
block|}
comment|/**    * analyze the range with the analyzer, instead of the collator.    * because jdk collators might not be thread safe (when they are    * its just that all methods are synced), this keeps things     * simple (we already have a threadlocal clone in the reused TS)    */
DECL|method|getCollationKey
specifier|private
name|BytesRef
name|getCollationKey
parameter_list|(
name|String
name|field
parameter_list|,
name|String
name|text
parameter_list|)
block|{
try|try
init|(
name|TokenStream
name|source
init|=
name|analyzer
operator|.
name|tokenStream
argument_list|(
name|field
argument_list|,
name|text
argument_list|)
init|)
block|{
name|source
operator|.
name|reset
argument_list|()
expr_stmt|;
name|TermToBytesRefAttribute
name|termAtt
init|=
name|source
operator|.
name|getAttribute
argument_list|(
name|TermToBytesRefAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|BytesRef
name|bytes
init|=
name|termAtt
operator|.
name|getBytesRef
argument_list|()
decl_stmt|;
comment|// we control the analyzer here: most errors are impossible
if|if
condition|(
operator|!
name|source
operator|.
name|incrementToken
argument_list|()
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"analyzer returned no terms for text: "
operator|+
name|text
argument_list|)
throw|;
name|termAtt
operator|.
name|fillBytesRef
argument_list|()
expr_stmt|;
assert|assert
operator|!
name|source
operator|.
name|incrementToken
argument_list|()
assert|;
name|source
operator|.
name|end
argument_list|()
expr_stmt|;
return|return
name|BytesRef
operator|.
name|deepCopyOf
argument_list|(
name|bytes
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Unable to analyze text: "
operator|+
name|text
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|getRangeQuery
specifier|public
name|Query
name|getRangeQuery
parameter_list|(
name|QParser
name|parser
parameter_list|,
name|SchemaField
name|field
parameter_list|,
name|String
name|part1
parameter_list|,
name|String
name|part2
parameter_list|,
name|boolean
name|minInclusive
parameter_list|,
name|boolean
name|maxInclusive
parameter_list|)
block|{
name|String
name|f
init|=
name|field
operator|.
name|getName
argument_list|()
decl_stmt|;
name|BytesRef
name|low
init|=
name|part1
operator|==
literal|null
condition|?
literal|null
else|:
name|getCollationKey
argument_list|(
name|f
argument_list|,
name|part1
argument_list|)
decl_stmt|;
name|BytesRef
name|high
init|=
name|part2
operator|==
literal|null
condition|?
literal|null
else|:
name|getCollationKey
argument_list|(
name|f
argument_list|,
name|part2
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|field
operator|.
name|indexed
argument_list|()
operator|&&
name|field
operator|.
name|hasDocValues
argument_list|()
condition|)
block|{
if|if
condition|(
name|field
operator|.
name|multiValued
argument_list|()
condition|)
block|{
return|return
operator|new
name|ConstantScoreQuery
argument_list|(
name|DocTermOrdsRangeFilter
operator|.
name|newBytesRefRange
argument_list|(
name|field
operator|.
name|getName
argument_list|()
argument_list|,
name|low
argument_list|,
name|high
argument_list|,
name|minInclusive
argument_list|,
name|maxInclusive
argument_list|)
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|new
name|ConstantScoreQuery
argument_list|(
name|FieldCacheRangeFilter
operator|.
name|newBytesRefRange
argument_list|(
name|field
operator|.
name|getName
argument_list|()
argument_list|,
name|low
argument_list|,
name|high
argument_list|,
name|minInclusive
argument_list|,
name|maxInclusive
argument_list|)
argument_list|)
return|;
block|}
block|}
else|else
block|{
return|return
operator|new
name|TermRangeQuery
argument_list|(
name|field
operator|.
name|getName
argument_list|()
argument_list|,
name|low
argument_list|,
name|high
argument_list|,
name|minInclusive
argument_list|,
name|maxInclusive
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|checkSchemaField
specifier|public
name|void
name|checkSchemaField
parameter_list|(
name|SchemaField
name|field
parameter_list|)
block|{
comment|// no-op
block|}
annotation|@
name|Override
DECL|method|createFields
specifier|public
name|List
argument_list|<
name|StorableField
argument_list|>
name|createFields
parameter_list|(
name|SchemaField
name|field
parameter_list|,
name|Object
name|value
parameter_list|,
name|float
name|boost
parameter_list|)
block|{
if|if
condition|(
name|field
operator|.
name|hasDocValues
argument_list|()
condition|)
block|{
name|List
argument_list|<
name|StorableField
argument_list|>
name|fields
init|=
operator|new
name|ArrayList
argument_list|<
name|StorableField
argument_list|>
argument_list|()
decl_stmt|;
name|fields
operator|.
name|add
argument_list|(
name|createField
argument_list|(
name|field
argument_list|,
name|value
argument_list|,
name|boost
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|BytesRef
name|bytes
init|=
name|getCollationKey
argument_list|(
name|field
operator|.
name|getName
argument_list|()
argument_list|,
name|value
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|field
operator|.
name|multiValued
argument_list|()
condition|)
block|{
name|fields
operator|.
name|add
argument_list|(
operator|new
name|SortedSetDocValuesField
argument_list|(
name|field
operator|.
name|getName
argument_list|()
argument_list|,
name|bytes
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|fields
operator|.
name|add
argument_list|(
operator|new
name|SortedDocValuesField
argument_list|(
name|field
operator|.
name|getName
argument_list|()
argument_list|,
name|bytes
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|fields
return|;
block|}
else|else
block|{
return|return
name|Collections
operator|.
name|singletonList
argument_list|(
name|createField
argument_list|(
name|field
argument_list|,
name|value
argument_list|,
name|boost
argument_list|)
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|marshalSortValue
specifier|public
name|Object
name|marshalSortValue
parameter_list|(
name|Object
name|value
parameter_list|)
block|{
if|if
condition|(
literal|null
operator|==
name|value
condition|)
block|{
return|return
literal|null
return|;
block|}
specifier|final
name|BytesRef
name|val
init|=
operator|(
name|BytesRef
operator|)
name|value
decl_stmt|;
return|return
name|Base64
operator|.
name|byteArrayToBase64
argument_list|(
name|val
operator|.
name|bytes
argument_list|,
name|val
operator|.
name|offset
argument_list|,
name|val
operator|.
name|length
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|unmarshalSortValue
specifier|public
name|Object
name|unmarshalSortValue
parameter_list|(
name|Object
name|value
parameter_list|)
block|{
if|if
condition|(
literal|null
operator|==
name|value
condition|)
block|{
return|return
literal|null
return|;
block|}
specifier|final
name|String
name|val
init|=
operator|(
name|String
operator|)
name|value
decl_stmt|;
specifier|final
name|byte
index|[]
name|bytes
init|=
name|Base64
operator|.
name|base64ToByteArray
argument_list|(
name|val
argument_list|)
decl_stmt|;
return|return
operator|new
name|BytesRef
argument_list|(
name|bytes
argument_list|)
return|;
block|}
block|}
end_class
end_unit
