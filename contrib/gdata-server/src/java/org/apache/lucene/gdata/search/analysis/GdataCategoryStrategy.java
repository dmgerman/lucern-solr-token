begin_unit
begin_comment
comment|/**  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.gdata.search.analysis
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
name|analysis
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
name|List
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
name|gdata
operator|.
name|data
operator|.
name|ServerBaseEntry
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
name|config
operator|.
name|IndexSchemaField
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
name|index
operator|.
name|GdataIndexerException
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
name|NamedNodeMap
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
name|Node
import|;
end_import
begin_comment
comment|/**  * This strategy retrieves the category term and and the scheme from a category  * element. The content is represented by the term which can be configured via  * the configuration file.  *<p>  * The category element has at least one attribute with the name "scheme" which  * is not mandatory. The term can be the default attribute "term" or the text  * content of the element, this is configured via the path of the field.  *</p>  *<p>  *<tt>&lt;category scheme="http://www.example.com/type" term="blog.post"/&gt;<tt>  *</p>  * TODO extend javadoc for search info  * @author Simon Willnauer  *  */
end_comment
begin_class
DECL|class|GdataCategoryStrategy
specifier|public
class|class
name|GdataCategoryStrategy
extends|extends
name|ContentStrategy
block|{
DECL|field|categoryScheme
specifier|protected
name|String
name|categoryScheme
decl_stmt|;
DECL|field|categorySchemeField
specifier|protected
name|String
name|categorySchemeField
decl_stmt|;
DECL|field|LABEL
specifier|private
specifier|static
specifier|final
name|String
name|LABEL
init|=
literal|"label"
decl_stmt|;
DECL|field|SCHEME
specifier|private
specifier|static
specifier|final
name|String
name|SCHEME
init|=
literal|"scheme"
decl_stmt|;
DECL|field|TERM
specifier|private
specifier|static
specifier|final
name|String
name|TERM
init|=
literal|"term"
decl_stmt|;
comment|/**      * the string to search a schema if no schema is specified      */
DECL|field|CATEGORY_SCHEMA_NULL_VALUE
specifier|public
specifier|static
specifier|final
name|String
name|CATEGORY_SCHEMA_NULL_VALUE
init|=
literal|"LUCISCHEMANULL"
decl_stmt|;
comment|/**      * Schema field suffix      */
DECL|field|CATEGORY_SCHEMA_FIELD_SUFFIX
specifier|public
specifier|static
specifier|final
name|String
name|CATEGORY_SCHEMA_FIELD_SUFFIX
init|=
literal|"-schema"
decl_stmt|;
DECL|method|GdataCategoryStrategy
specifier|protected
name|GdataCategoryStrategy
parameter_list|(
name|IndexSchemaField
name|fieldConfiguration
parameter_list|)
block|{
name|super
argument_list|(
name|fieldConfiguration
argument_list|)
expr_stmt|;
name|this
operator|.
name|categorySchemeField
operator|=
operator|new
name|StringBuilder
argument_list|(
name|this
operator|.
name|fieldName
argument_list|)
operator|.
name|append
argument_list|(
name|CATEGORY_SCHEMA_FIELD_SUFFIX
argument_list|)
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
comment|/**      * @throws NotIndexableException      * @see org.apache.lucene.gdata.search.analysis.ContentStrategy#processIndexable(org.apache.lucene.gdata.search.analysis.Indexable)      */
annotation|@
name|Override
DECL|method|processIndexable
specifier|public
name|void
name|processIndexable
parameter_list|(
name|Indexable
argument_list|<
name|?
extends|extends
name|Node
argument_list|,
name|?
extends|extends
name|ServerBaseEntry
argument_list|>
name|indexable
parameter_list|)
throws|throws
name|NotIndexableException
block|{
name|String
name|contentPath
init|=
name|this
operator|.
name|config
operator|.
name|getPath
argument_list|()
decl_stmt|;
name|Node
name|node
init|=
literal|null
decl_stmt|;
try|try
block|{
name|node
operator|=
name|indexable
operator|.
name|applyPath
argument_list|(
name|contentPath
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XPathExpressionException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|NotIndexableException
argument_list|(
literal|"Can not apply path"
argument_list|)
throw|;
block|}
if|if
condition|(
name|node
operator|==
literal|null
condition|)
throw|throw
operator|new
name|NotIndexableException
argument_list|(
literal|"Could not retrieve content for schema field: "
operator|+
name|this
operator|.
name|config
argument_list|)
throw|;
name|StringBuilder
name|contentBuilder
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|StringBuilder
name|schemeBuilder
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|String
name|nodeName
init|=
name|node
operator|.
name|getNodeName
argument_list|()
decl_stmt|;
comment|/*          * enable more than one category element -- check the node name if          * category strategy is used with an element not named "category"          */
while|while
condition|(
name|node
operator|!=
literal|null
operator|&&
name|nodeName
operator|!=
literal|null
operator|&&
name|nodeName
operator|.
name|equals
argument_list|(
name|node
operator|.
name|getNodeName
argument_list|()
argument_list|)
condition|)
block|{
name|NamedNodeMap
name|attributeMap
init|=
name|node
operator|.
name|getAttributes
argument_list|()
decl_stmt|;
if|if
condition|(
name|attributeMap
operator|==
literal|null
condition|)
throw|throw
operator|new
name|NotIndexableException
argument_list|(
literal|"category term attribute not present"
argument_list|)
throw|;
comment|/*              * the "term" is the internal string used by the software to              * identify the category, while the "label" is the human-readable              * string presented to a user in a user interface.              */
name|Node
name|termNode
init|=
name|attributeMap
operator|.
name|getNamedItem
argument_list|(
name|TERM
argument_list|)
decl_stmt|;
if|if
condition|(
name|termNode
operator|==
literal|null
condition|)
throw|throw
operator|new
name|NotIndexableException
argument_list|(
literal|"category term attribute not present"
argument_list|)
throw|;
name|contentBuilder
operator|.
name|append
argument_list|(
name|termNode
operator|.
name|getTextContent
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
name|Node
name|labelNode
init|=
name|attributeMap
operator|.
name|getNamedItem
argument_list|(
name|LABEL
argument_list|)
decl_stmt|;
if|if
condition|(
name|labelNode
operator|!=
literal|null
condition|)
name|contentBuilder
operator|.
name|append
argument_list|(
name|labelNode
operator|.
name|getTextContent
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
name|Node
name|schemeNode
init|=
name|attributeMap
operator|.
name|getNamedItem
argument_list|(
name|SCHEME
argument_list|)
decl_stmt|;
if|if
condition|(
name|schemeNode
operator|!=
literal|null
condition|)
name|schemeBuilder
operator|.
name|append
argument_list|(
name|schemeNode
operator|.
name|getTextContent
argument_list|()
argument_list|)
expr_stmt|;
name|node
operator|=
name|node
operator|.
name|getNextSibling
argument_list|()
expr_stmt|;
block|}
name|this
operator|.
name|content
operator|=
name|contentBuilder
operator|.
name|toString
argument_list|()
expr_stmt|;
name|this
operator|.
name|categoryScheme
operator|=
name|schemeBuilder
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
comment|/**      * @see org.apache.lucene.gdata.search.analysis.ContentStrategy#createLuceneField()      */
annotation|@
name|Override
DECL|method|createLuceneField
specifier|public
name|Field
index|[]
name|createLuceneField
parameter_list|()
block|{
name|List
argument_list|<
name|Field
argument_list|>
name|retValue
init|=
operator|new
name|ArrayList
argument_list|<
name|Field
argument_list|>
argument_list|(
literal|2
argument_list|)
decl_stmt|;
if|if
condition|(
name|this
operator|.
name|fieldName
operator|==
literal|null
condition|)
throw|throw
operator|new
name|GdataIndexerException
argument_list|(
literal|"Required field 'name' is null -- "
operator|+
name|this
operator|.
name|config
argument_list|)
throw|;
if|if
condition|(
name|this
operator|.
name|content
operator|==
literal|null
condition|)
throw|throw
operator|new
name|GdataIndexerException
argument_list|(
literal|"Required field 'content' is null -- "
operator|+
name|this
operator|.
name|config
argument_list|)
throw|;
name|Field
name|categoryTerm
init|=
operator|new
name|Field
argument_list|(
name|this
operator|.
name|fieldName
argument_list|,
name|this
operator|.
name|content
argument_list|,
name|this
operator|.
name|store
argument_list|,
name|this
operator|.
name|index
argument_list|)
decl_stmt|;
name|float
name|boost
init|=
name|this
operator|.
name|config
operator|.
name|getBoost
argument_list|()
decl_stmt|;
if|if
condition|(
name|boost
operator|!=
literal|1.0f
condition|)
name|categoryTerm
operator|.
name|setBoost
argument_list|(
name|boost
argument_list|)
expr_stmt|;
name|retValue
operator|.
name|add
argument_list|(
name|categoryTerm
argument_list|)
expr_stmt|;
comment|/*          * if schema is not set index null value to enable search for categories          * without a schema          */
if|if
condition|(
name|this
operator|.
name|categoryScheme
operator|==
literal|null
operator|||
name|this
operator|.
name|categoryScheme
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
name|this
operator|.
name|categoryScheme
operator|=
name|CATEGORY_SCHEMA_NULL_VALUE
expr_stmt|;
block|}
name|Field
name|categoryURN
init|=
operator|new
name|Field
argument_list|(
name|this
operator|.
name|categorySchemeField
argument_list|,
name|this
operator|.
name|categoryScheme
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|UN_TOKENIZED
argument_list|)
decl_stmt|;
name|retValue
operator|.
name|add
argument_list|(
name|categoryURN
argument_list|)
expr_stmt|;
return|return
name|retValue
operator|.
name|toArray
argument_list|(
operator|new
name|Field
index|[
literal|0
index|]
argument_list|)
return|;
block|}
block|}
end_class
end_unit
