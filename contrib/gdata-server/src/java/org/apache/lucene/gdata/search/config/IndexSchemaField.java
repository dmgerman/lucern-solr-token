begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.gdata.search.config
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|gdata
operator|.
name|search
operator|.
name|config
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
name|document
operator|.
name|Field
operator|.
name|Index
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
operator|.
name|Store
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
name|gdata
operator|.
name|search
operator|.
name|analysis
operator|.
name|ContentStrategy
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
name|gdata
operator|.
name|search
operator|.
name|analysis
operator|.
name|GdataCategoryStrategy
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
name|gdata
operator|.
name|search
operator|.
name|analysis
operator|.
name|GdataDateStrategy
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
name|gdata
operator|.
name|search
operator|.
name|analysis
operator|.
name|HTMLStrategy
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
name|gdata
operator|.
name|search
operator|.
name|analysis
operator|.
name|KeywordStrategy
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
name|gdata
operator|.
name|search
operator|.
name|analysis
operator|.
name|MixedContentStrategy
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
name|gdata
operator|.
name|search
operator|.
name|analysis
operator|.
name|PlainTextStrategy
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
name|gdata
operator|.
name|search
operator|.
name|analysis
operator|.
name|XHtmlStrategy
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
name|gdata
operator|.
name|utils
operator|.
name|ReflectionUtils
import|;
end_import
begin_comment
comment|/**  * Each field in the search index is defined by a instance of  * {@link IndexSchemaField}. The schema definition will be loaded at startup  * and the defined values will be set to instances of this class. Each  * constructed field will be passed to an instance of  * {@link org.apache.lucene.gdata.search.config.IndexSchema}.  *<p>  * IndexSchemaField contains all informations about how the content from  * incoming entries has to be extracted and how the actual content has to be  * index into the lucene index.  *</p>  *<p>  * Each field will have a defined  * {@link org.apache.lucene.gdata.search.analysis.ContentStrategy} which does  * process the extraction of the field content from an incoming entry.  *</p>  * @see org.apache.lucene.gdata.search.analysis.ContentStrategy  * @see org.apache.lucene.gdata.search.config.IndexSchema  *   * @author Simon Willnauer  *   */
end_comment
begin_class
DECL|class|IndexSchemaField
specifier|public
class|class
name|IndexSchemaField
block|{
comment|/**      * Default value for Field.Store       * @see org.apache.lucene.document.Field      */
DECL|field|DEFAULT_STORE_STRATEGY
specifier|public
specifier|static
specifier|final
name|Store
name|DEFAULT_STORE_STRATEGY
init|=
name|Field
operator|.
name|Store
operator|.
name|NO
decl_stmt|;
comment|/**      * Default value for Field.Index      * @see org.apache.lucene.document.Field      */
DECL|field|DEFAULT_INDEX_STRATEGY
specifier|public
specifier|static
specifier|final
name|Index
name|DEFAULT_INDEX_STRATEGY
init|=
name|Field
operator|.
name|Index
operator|.
name|TOKENIZED
decl_stmt|;
DECL|field|DEFAULT_BOOST
specifier|private
specifier|static
specifier|final
name|float
name|DEFAULT_BOOST
init|=
literal|1.0f
decl_stmt|;
DECL|field|MINIMAL_BOOST
specifier|private
specifier|static
specifier|final
name|float
name|MINIMAL_BOOST
init|=
literal|0.1f
decl_stmt|;
DECL|field|boost
specifier|private
name|float
name|boost
init|=
name|DEFAULT_BOOST
decl_stmt|;
DECL|field|name
specifier|private
name|String
name|name
decl_stmt|;
DECL|field|contentType
specifier|private
name|ContentType
name|contentType
decl_stmt|;
DECL|field|index
specifier|private
name|Index
name|index
init|=
name|DEFAULT_INDEX_STRATEGY
decl_stmt|;
DECL|field|store
specifier|private
name|Store
name|store
init|=
name|DEFAULT_STORE_STRATEGY
decl_stmt|;
DECL|field|path
specifier|private
name|String
name|path
decl_stmt|;
DECL|field|typePath
specifier|private
name|String
name|typePath
decl_stmt|;
DECL|field|analyzerClass
specifier|private
name|Class
argument_list|<
name|?
extends|extends
name|Analyzer
argument_list|>
name|analyzerClass
decl_stmt|;
DECL|field|fieldClass
specifier|private
name|Class
argument_list|<
name|?
extends|extends
name|ContentStrategy
argument_list|>
name|fieldClass
decl_stmt|;
comment|/**      * Constructs a new SchemaField<br>      * Default values:      *<ol>      *<li>boost:<i>1.0</i></li>      *<li>index:<i>TOKENIZED</i></li>      *<li>store:<i>NO</i></li>      *</ol>      */
DECL|method|IndexSchemaField
specifier|public
name|IndexSchemaField
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
DECL|method|checkRequieredValues
name|boolean
name|checkRequieredValues
parameter_list|()
block|{
comment|/*          * This class will be inst. by the reg builder.          * Check all values to be set. otherwise return false.          * false will cause a runtime exception in IndexSchema          */
name|boolean
name|returnValue
init|=
operator|(
name|this
operator|.
name|name
operator|!=
literal|null
operator|&&
name|this
operator|.
name|path
operator|!=
literal|null
operator|&&
name|this
operator|.
name|contentType
operator|!=
literal|null
operator|&&
name|this
operator|.
name|index
operator|!=
literal|null
operator|&&
name|this
operator|.
name|store
operator|!=
literal|null
operator|&&
name|this
operator|.
name|boost
operator|>=
name|MINIMAL_BOOST
operator|)
decl_stmt|;
if|if
condition|(
name|this
operator|.
name|contentType
operator|==
name|ContentType
operator|.
name|CUSTOM
condition|)
name|returnValue
operator|&=
name|this
operator|.
name|fieldClass
operator|!=
literal|null
expr_stmt|;
elseif|else
if|if
condition|(
name|this
operator|.
name|contentType
operator|==
name|ContentType
operator|.
name|MIXED
condition|)
name|returnValue
operator|&=
name|this
operator|.
name|typePath
operator|!=
literal|null
expr_stmt|;
return|return
name|returnValue
return|;
block|}
comment|/**      * @return Returns the alanyzerClass.      */
DECL|method|getAnalyzerClass
specifier|public
name|Class
argument_list|<
name|?
extends|extends
name|Analyzer
argument_list|>
name|getAnalyzerClass
parameter_list|()
block|{
return|return
name|this
operator|.
name|analyzerClass
return|;
block|}
comment|/**      * @param alanyzerClass      *            The alanyzerClass to set.      */
DECL|method|setAnalyzerClass
specifier|public
name|void
name|setAnalyzerClass
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|Analyzer
argument_list|>
name|alanyzerClass
parameter_list|)
block|{
name|this
operator|.
name|analyzerClass
operator|=
name|alanyzerClass
expr_stmt|;
block|}
comment|/**      * @return Returns the fieldClass.      */
DECL|method|getFieldClass
specifier|public
name|Class
argument_list|<
name|?
extends|extends
name|ContentStrategy
argument_list|>
name|getFieldClass
parameter_list|()
block|{
return|return
name|this
operator|.
name|fieldClass
return|;
block|}
comment|/**      * Sets the class or strategy is used to extract this field Attention: this      * method set the contentTyp to {@link ContentType#CUSTOM}      *       * @param fieldClass      *            The fieldClass to set.      */
DECL|method|setFieldClass
specifier|public
name|void
name|setFieldClass
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|ContentStrategy
argument_list|>
name|fieldClass
parameter_list|)
block|{
if|if
condition|(
name|fieldClass
operator|==
literal|null
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"ContentStrategy must not be null"
argument_list|)
throw|;
if|if
condition|(
operator|!
name|ReflectionUtils
operator|.
name|extendsType
argument_list|(
name|fieldClass
argument_list|,
name|ContentStrategy
operator|.
name|class
argument_list|)
condition|)
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"The configured ContentStrategy does not extend ContentStrategy, can not use as a custom strategy -- "
operator|+
name|fieldClass
operator|.
name|getName
argument_list|()
argument_list|)
throw|;
if|if
condition|(
operator|!
name|ReflectionUtils
operator|.
name|hasDesiredConstructor
argument_list|(
name|fieldClass
argument_list|,
operator|new
name|Class
index|[]
block|{
name|IndexSchemaField
operator|.
name|class
block|}
block|)
block|)
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Can not create instance of "
operator|+
name|fieldClass
operator|.
name|getName
argument_list|()
argument_list|)
throw|;
end_class
begin_expr_stmt
name|this
operator|.
name|fieldClass
operator|=
name|fieldClass
expr_stmt|;
end_expr_stmt
begin_comment
comment|/*          * set custom - field class is only needed by custom          */
end_comment
begin_expr_stmt
name|this
operator|.
name|contentType
operator|=
name|ContentType
operator|.
name|CUSTOM
expr_stmt|;
end_expr_stmt
begin_comment
unit|}
comment|/**      * @return Returns the index.      */
end_comment
begin_function
unit|public
DECL|method|getIndex
name|Index
name|getIndex
parameter_list|()
block|{
return|return
name|this
operator|.
name|index
return|;
block|}
end_function
begin_comment
comment|/**      * @param index      *            The index to set.      */
end_comment
begin_function
DECL|method|setIndex
specifier|public
name|void
name|setIndex
parameter_list|(
name|Index
name|index
parameter_list|)
block|{
name|this
operator|.
name|index
operator|=
name|index
expr_stmt|;
block|}
end_function
begin_comment
comment|/**      * @return Returns the name.      */
end_comment
begin_function
DECL|method|getName
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|this
operator|.
name|name
return|;
block|}
end_function
begin_comment
comment|/**      * @param name      *            The name to set.      */
end_comment
begin_function
DECL|method|setName
specifier|public
name|void
name|setName
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
end_function
begin_comment
comment|/**      * @return Returns the path.      */
end_comment
begin_function
DECL|method|getPath
specifier|public
name|String
name|getPath
parameter_list|()
block|{
return|return
name|this
operator|.
name|path
return|;
block|}
end_function
begin_comment
comment|/**      * @param path      *            The path to set.      */
end_comment
begin_function
DECL|method|setPath
specifier|public
name|void
name|setPath
parameter_list|(
name|String
name|path
parameter_list|)
block|{
name|this
operator|.
name|path
operator|=
name|path
expr_stmt|;
block|}
end_function
begin_comment
comment|/**      * @return Returns the store.      */
end_comment
begin_function
DECL|method|getStore
specifier|public
name|Store
name|getStore
parameter_list|()
block|{
return|return
name|this
operator|.
name|store
return|;
block|}
end_function
begin_comment
comment|/**      * @param store      *            The store to set.      */
end_comment
begin_function
DECL|method|setStore
specifier|public
name|void
name|setStore
parameter_list|(
name|Store
name|store
parameter_list|)
block|{
name|this
operator|.
name|store
operator|=
name|store
expr_stmt|;
block|}
end_function
begin_comment
comment|/**      * @return Returns the type.      */
end_comment
begin_function
DECL|method|getContentType
specifier|public
name|ContentType
name|getContentType
parameter_list|()
block|{
return|return
name|this
operator|.
name|contentType
return|;
block|}
end_function
begin_comment
comment|/**      * @param type      *            The type to set.      */
end_comment
begin_function
DECL|method|setContentType
specifier|public
name|void
name|setContentType
parameter_list|(
name|ContentType
name|type
parameter_list|)
block|{
name|this
operator|.
name|contentType
operator|=
name|type
expr_stmt|;
block|}
end_function
begin_comment
comment|/**      * Sets the content type of this field by the name of the enum type. This      * method is not case sensitive.      *       * @param type -      *            type name as string      */
end_comment
begin_function
DECL|method|setType
specifier|public
name|void
name|setType
parameter_list|(
name|String
name|type
parameter_list|)
block|{
name|ContentType
index|[]
name|types
init|=
name|ContentType
operator|.
name|class
operator|.
name|getEnumConstants
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
name|types
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|types
index|[
name|i
index|]
operator|.
name|name
argument_list|()
operator|.
name|toLowerCase
argument_list|()
operator|.
name|equals
argument_list|(
name|type
argument_list|)
condition|)
block|{
name|this
operator|.
name|contentType
operator|=
name|types
index|[
name|i
index|]
expr_stmt|;
break|break;
block|}
block|}
block|}
end_function
begin_comment
comment|/**      * Defines the {@link ContentStrategy} to use for a      *<tt>IndexSchemaField</tt> to extract the content from the entry      *       * @author Simon Willnauer      *       */
end_comment
begin_enum
DECL|enum|ContentType
specifier|public
enum|enum
name|ContentType
block|{
comment|/**          * HTML content strategy {@link HTMLStrategy }          */
DECL|enum constant|HTML
name|HTML
block|,
comment|/**          * XHTML content strategy {@link XHtmlStrategy }          */
DECL|enum constant|XHTML
name|XHTML
block|,
comment|/**          * Text content strategy {@link PlainTextStrategy }          */
DECL|enum constant|TEXT
name|TEXT
block|,
comment|/**          * GDataDate content strategy {@link GdataDateStrategy }          */
DECL|enum constant|GDATADATE
name|GDATADATE
block|,
comment|/**          * KEYWORD content strategy {@link KeywordStrategy }          */
DECL|enum constant|KEYWORD
name|KEYWORD
block|,
comment|/**          * Category content strategy {@link GdataCategoryStrategy }          */
DECL|enum constant|CATEGORY
name|CATEGORY
block|,
comment|/**          * Custom content strategy (user defined)          */
DECL|enum constant|CUSTOM
name|CUSTOM
block|,
comment|/**          * Mixed content strategy {@link MixedContentStrategy }          */
DECL|enum constant|MIXED
name|MIXED
block|}
end_enum
begin_comment
comment|/**      * @return Returns the boost.      */
end_comment
begin_function
DECL|method|getBoost
specifier|public
name|float
name|getBoost
parameter_list|()
block|{
return|return
name|this
operator|.
name|boost
return|;
block|}
end_function
begin_comment
comment|/**      * @param boost      *            The boost to set.      */
end_comment
begin_function
DECL|method|setBoost
specifier|public
name|void
name|setBoost
parameter_list|(
name|float
name|boost
parameter_list|)
block|{
if|if
condition|(
name|boost
operator|<=
literal|0
condition|)
return|return;
name|this
operator|.
name|boost
operator|=
name|boost
expr_stmt|;
block|}
end_function
begin_comment
comment|/**      * @see java.lang.Object#toString()      */
end_comment
begin_function
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|builder
init|=
operator|new
name|StringBuilder
argument_list|(
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
decl_stmt|;
name|builder
operator|.
name|append
argument_list|(
literal|"field name: "
argument_list|)
operator|.
name|append
argument_list|(
name|this
operator|.
name|name
argument_list|)
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
literal|"path: "
argument_list|)
operator|.
name|append
argument_list|(
name|this
operator|.
name|path
argument_list|)
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
literal|"content type "
argument_list|)
operator|.
name|append
argument_list|(
name|this
operator|.
name|contentType
argument_list|)
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
literal|"field class: "
argument_list|)
operator|.
name|append
argument_list|(
name|this
operator|.
name|fieldClass
argument_list|)
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
literal|"analyzer: "
argument_list|)
operator|.
name|append
argument_list|(
name|this
operator|.
name|analyzerClass
argument_list|)
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
literal|"boost: "
argument_list|)
operator|.
name|append
argument_list|(
name|this
operator|.
name|boost
argument_list|)
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
literal|"INDEX: "
argument_list|)
operator|.
name|append
argument_list|(
name|this
operator|.
name|index
argument_list|)
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
literal|"STORE: "
argument_list|)
operator|.
name|append
argument_list|(
name|this
operator|.
name|store
argument_list|)
expr_stmt|;
return|return
name|builder
operator|.
name|toString
argument_list|()
return|;
block|}
end_function
begin_comment
comment|/**      * Sets the Store class by simple name      *       * @param name -      *            one of yes, no, compress      */
end_comment
begin_function
DECL|method|setStoreByName
specifier|public
name|void
name|setStoreByName
parameter_list|(
name|String
name|name
parameter_list|)
block|{
if|if
condition|(
name|name
operator|.
name|toLowerCase
argument_list|()
operator|.
name|equals
argument_list|(
literal|"yes"
argument_list|)
condition|)
name|this
operator|.
name|store
operator|=
name|Field
operator|.
name|Store
operator|.
name|YES
expr_stmt|;
elseif|else
if|if
condition|(
name|name
operator|.
name|toLowerCase
argument_list|()
operator|.
name|equals
argument_list|(
literal|"no"
argument_list|)
condition|)
name|this
operator|.
name|store
operator|=
name|Field
operator|.
name|Store
operator|.
name|NO
expr_stmt|;
elseif|else
if|if
condition|(
name|name
operator|.
name|toLowerCase
argument_list|()
operator|.
name|equals
argument_list|(
literal|"compress"
argument_list|)
condition|)
name|this
operator|.
name|store
operator|=
name|Field
operator|.
name|Store
operator|.
name|COMPRESS
expr_stmt|;
block|}
end_function
begin_comment
comment|/**      * Sets the Index class by simple name      *       * @param name -      *            un_tokenized, tokenized, no, no_norms      */
end_comment
begin_function
DECL|method|setIndexByName
specifier|public
name|void
name|setIndexByName
parameter_list|(
name|String
name|name
parameter_list|)
block|{
if|if
condition|(
name|name
operator|.
name|toLowerCase
argument_list|()
operator|.
name|equals
argument_list|(
literal|"un_tokenized"
argument_list|)
condition|)
name|this
operator|.
name|index
operator|=
name|Field
operator|.
name|Index
operator|.
name|UN_TOKENIZED
expr_stmt|;
elseif|else
if|if
condition|(
name|name
operator|.
name|toLowerCase
argument_list|()
operator|.
name|equals
argument_list|(
literal|"tokenized"
argument_list|)
condition|)
name|this
operator|.
name|index
operator|=
name|Field
operator|.
name|Index
operator|.
name|TOKENIZED
expr_stmt|;
elseif|else
if|if
condition|(
name|name
operator|.
name|toLowerCase
argument_list|()
operator|.
name|equals
argument_list|(
literal|"no_norms"
argument_list|)
condition|)
name|this
operator|.
name|index
operator|=
name|Field
operator|.
name|Index
operator|.
name|NO_NORMS
expr_stmt|;
elseif|else
if|if
condition|(
name|name
operator|.
name|toLowerCase
argument_list|()
operator|.
name|equals
argument_list|(
literal|"no"
argument_list|)
condition|)
name|this
operator|.
name|index
operator|=
name|Field
operator|.
name|Index
operator|.
name|NO
expr_stmt|;
block|}
end_function
begin_comment
comment|/**      * @return Returns the typePath.      */
end_comment
begin_function
DECL|method|getTypePath
specifier|public
name|String
name|getTypePath
parameter_list|()
block|{
return|return
name|this
operator|.
name|typePath
return|;
block|}
end_function
begin_comment
comment|/**      * @param typePath      *            The typePath to set.      */
end_comment
begin_function
DECL|method|setTypePath
specifier|public
name|void
name|setTypePath
parameter_list|(
name|String
name|typePath
parameter_list|)
block|{
name|this
operator|.
name|typePath
operator|=
name|typePath
expr_stmt|;
comment|/*          * set Mixed - this property is only needed by mixed type          */
name|setContentType
argument_list|(
name|ContentType
operator|.
name|MIXED
argument_list|)
expr_stmt|;
block|}
end_function
unit|}
end_unit
