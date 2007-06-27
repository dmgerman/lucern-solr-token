begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.update
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|update
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
name|Collection
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Date
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
name|Document
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
name|Fieldable
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
name|SolrInputDocument
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
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|schema
operator|.
name|IndexSchema
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
begin_comment
comment|/**  * @author yonik  * @version $Id$  */
end_comment
begin_comment
comment|// Not thread safe - by design.  Create a new builder for each thread.
end_comment
begin_class
DECL|class|DocumentBuilder
specifier|public
class|class
name|DocumentBuilder
block|{
DECL|field|schema
specifier|private
specifier|final
name|IndexSchema
name|schema
decl_stmt|;
DECL|field|doc
specifier|private
name|Document
name|doc
decl_stmt|;
DECL|field|map
specifier|private
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|map
decl_stmt|;
DECL|method|DocumentBuilder
specifier|public
name|DocumentBuilder
parameter_list|(
name|IndexSchema
name|schema
parameter_list|)
block|{
name|this
operator|.
name|schema
operator|=
name|schema
expr_stmt|;
block|}
DECL|method|startDoc
specifier|public
name|void
name|startDoc
parameter_list|()
block|{
name|doc
operator|=
operator|new
name|Document
argument_list|()
expr_stmt|;
name|map
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
expr_stmt|;
block|}
DECL|method|addSingleField
specifier|protected
name|void
name|addSingleField
parameter_list|(
name|SchemaField
name|sfield
parameter_list|,
name|String
name|val
parameter_list|,
name|float
name|boost
parameter_list|)
block|{
comment|//System.out.println("###################ADDING FIELD "+sfield+"="+val);
comment|// we don't check for a null val ourselves because a solr.FieldType
comment|// might actually want to map it to something.  If createField()
comment|// returns null, then we don't store the field.
name|Field
name|field
init|=
name|sfield
operator|.
name|createField
argument_list|(
name|val
argument_list|,
name|boost
argument_list|)
decl_stmt|;
if|if
condition|(
name|field
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
operator|!
name|sfield
operator|.
name|multiValued
argument_list|()
condition|)
block|{
name|String
name|oldValue
init|=
name|map
operator|.
name|put
argument_list|(
name|sfield
operator|.
name|getName
argument_list|()
argument_list|,
name|val
argument_list|)
decl_stmt|;
if|if
condition|(
name|oldValue
operator|!=
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
name|BAD_REQUEST
argument_list|,
literal|"ERROR: multiple values encountered for non multiValued field "
operator|+
name|sfield
operator|.
name|getName
argument_list|()
operator|+
literal|": first='"
operator|+
name|oldValue
operator|+
literal|"' second='"
operator|+
name|val
operator|+
literal|"'"
argument_list|)
throw|;
block|}
block|}
comment|// field.setBoost(boost);
name|doc
operator|.
name|add
argument_list|(
name|field
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|addField
specifier|public
name|void
name|addField
parameter_list|(
name|SchemaField
name|sfield
parameter_list|,
name|String
name|val
parameter_list|,
name|float
name|boost
parameter_list|)
block|{
name|addSingleField
argument_list|(
name|sfield
argument_list|,
name|val
argument_list|,
name|boost
argument_list|)
expr_stmt|;
block|}
DECL|method|addField
specifier|public
name|void
name|addField
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|val
parameter_list|)
block|{
name|addField
argument_list|(
name|name
argument_list|,
name|val
argument_list|,
literal|1.0f
argument_list|)
expr_stmt|;
block|}
DECL|method|addField
specifier|public
name|void
name|addField
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|val
parameter_list|,
name|float
name|boost
parameter_list|)
block|{
name|SchemaField
name|sfield
init|=
name|schema
operator|.
name|getFieldOrNull
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|sfield
operator|!=
literal|null
condition|)
block|{
name|addField
argument_list|(
name|sfield
argument_list|,
name|val
argument_list|,
name|boost
argument_list|)
expr_stmt|;
block|}
comment|// Check if we should copy this field to any other fields.
comment|// This could happen whether it is explicit or not.
name|SchemaField
index|[]
name|destArr
init|=
name|schema
operator|.
name|getCopyFields
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|destArr
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|SchemaField
name|destField
range|:
name|destArr
control|)
block|{
name|addSingleField
argument_list|(
name|destField
argument_list|,
name|val
argument_list|,
name|boost
argument_list|)
expr_stmt|;
block|}
block|}
comment|// error if this field name doesn't match anything
if|if
condition|(
name|sfield
operator|==
literal|null
operator|&&
operator|(
name|destArr
operator|==
literal|null
operator|||
name|destArr
operator|.
name|length
operator|==
literal|0
operator|)
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
name|BAD_REQUEST
argument_list|,
literal|"ERROR:unknown field '"
operator|+
name|name
operator|+
literal|"'"
argument_list|)
throw|;
block|}
block|}
DECL|method|setBoost
specifier|public
name|void
name|setBoost
parameter_list|(
name|float
name|boost
parameter_list|)
block|{
name|doc
operator|.
name|setBoost
argument_list|(
name|boost
argument_list|)
expr_stmt|;
block|}
DECL|method|endDoc
specifier|public
name|void
name|endDoc
parameter_list|()
block|{   }
comment|// specific to this type of document builder
DECL|method|getDoc
specifier|public
name|Document
name|getDoc
parameter_list|()
throws|throws
name|IllegalArgumentException
block|{
comment|// Check for all required fields -- Note, all fields with a
comment|// default value are defacto 'required' fields.
name|List
argument_list|<
name|String
argument_list|>
name|missingFields
init|=
literal|null
decl_stmt|;
for|for
control|(
name|SchemaField
name|field
range|:
name|schema
operator|.
name|getRequiredFields
argument_list|()
control|)
block|{
if|if
condition|(
name|doc
operator|.
name|getField
argument_list|(
name|field
operator|.
name|getName
argument_list|()
argument_list|)
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|field
operator|.
name|getDefaultValue
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|doc
operator|.
name|add
argument_list|(
name|field
operator|.
name|createField
argument_list|(
name|field
operator|.
name|getDefaultValue
argument_list|()
argument_list|,
literal|1.0f
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|missingFields
operator|==
literal|null
condition|)
block|{
name|missingFields
operator|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
name|missingFields
operator|.
name|add
argument_list|(
name|field
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|missingFields
operator|!=
literal|null
condition|)
block|{
name|StringBuilder
name|builder
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
comment|// add the uniqueKey if possible
if|if
condition|(
name|schema
operator|.
name|getUniqueKeyField
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|String
name|n
init|=
name|schema
operator|.
name|getUniqueKeyField
argument_list|()
operator|.
name|getName
argument_list|()
decl_stmt|;
name|String
name|v
init|=
name|doc
operator|.
name|get
argument_list|(
name|n
argument_list|)
decl_stmt|;
name|builder
operator|.
name|append
argument_list|(
literal|"Document ["
operator|+
name|n
operator|+
literal|"="
operator|+
name|v
operator|+
literal|"] "
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|append
argument_list|(
literal|"missing required fields: "
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|field
range|:
name|missingFields
control|)
block|{
name|builder
operator|.
name|append
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
block|}
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
name|builder
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
name|Document
name|ret
init|=
name|doc
decl_stmt|;
name|doc
operator|=
literal|null
expr_stmt|;
return|return
name|ret
return|;
block|}
comment|/**    * Convert a SolrInputDocument to a lucene Document.    *     * This function shoould go elsewhere.  This builds the Document without an    * extra Map<> checking for multiple values.  For more discussion, see:    * http://www.nabble.com/Re%3A-svn-commit%3A-r547493---in--lucene-solr-trunk%3A-.--src-java-org-apache-solr-common--src-java-org-apache-solr-schema--src-java-org-apache-solr-update--src-test-org-apache-solr-common--tf3931539.html    *     * TODO: /!\ NOTE /!\ This semantics of this function are still in flux.      * Something somewhere needs to be able to fill up a SolrDocument from    * a lucene document - this is one place that may happen.  It may also be    * moved to an independent function    *     * @since solr 1.3    */
DECL|method|toDocument
specifier|public
specifier|static
name|Document
name|toDocument
parameter_list|(
name|SolrInputDocument
name|doc
parameter_list|,
name|IndexSchema
name|schema
parameter_list|)
block|{
name|Document
name|out
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
comment|// Load fields from SolrDocument to Document
for|for
control|(
name|String
name|name
range|:
name|doc
operator|.
name|getFieldNames
argument_list|()
control|)
block|{
name|SchemaField
name|sfield
init|=
name|schema
operator|.
name|getFieldOrNull
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|Float
name|b
init|=
name|doc
operator|.
name|getBoost
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|float
name|boost
init|=
operator|(
name|b
operator|==
literal|null
operator|)
condition|?
literal|1.0f
else|:
name|b
operator|.
name|floatValue
argument_list|()
decl_stmt|;
name|boolean
name|used
init|=
literal|false
decl_stmt|;
comment|// Make sure it has the correct number
name|Collection
argument_list|<
name|Object
argument_list|>
name|vals
init|=
name|doc
operator|.
name|getFieldValues
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|vals
operator|.
name|size
argument_list|()
operator|>
literal|1
operator|&&
name|sfield
operator|!=
literal|null
operator|&&
operator|!
name|sfield
operator|.
name|multiValued
argument_list|()
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
name|BAD_REQUEST
argument_list|,
literal|"ERROR: multiple values encountered for non multiValued field "
operator|+
name|sfield
operator|.
name|getName
argument_list|()
operator|+
literal|": "
operator|+
name|vals
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
name|SchemaField
index|[]
name|destArr
init|=
name|schema
operator|.
name|getCopyFields
argument_list|(
name|name
argument_list|)
decl_stmt|;
comment|// load each field value
for|for
control|(
name|Object
name|v
range|:
name|vals
control|)
block|{
name|String
name|val
init|=
literal|null
decl_stmt|;
comment|// HACK -- date conversion
if|if
condition|(
name|sfield
operator|!=
literal|null
operator|&&
name|v
operator|instanceof
name|Date
operator|&&
name|sfield
operator|.
name|getType
argument_list|()
operator|instanceof
name|DateField
condition|)
block|{
name|DateField
name|df
init|=
operator|(
name|DateField
operator|)
name|sfield
operator|.
name|getType
argument_list|()
decl_stmt|;
name|val
operator|=
name|df
operator|.
name|toInternal
argument_list|(
operator|(
name|Date
operator|)
name|v
argument_list|)
operator|+
literal|'Z'
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|v
operator|!=
literal|null
condition|)
block|{
name|val
operator|=
name|v
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|sfield
operator|!=
literal|null
condition|)
block|{
name|used
operator|=
literal|true
expr_stmt|;
name|Field
name|f
init|=
name|sfield
operator|.
name|createField
argument_list|(
name|val
argument_list|,
name|boost
argument_list|)
decl_stmt|;
if|if
condition|(
name|f
operator|!=
literal|null
condition|)
block|{
comment|// null fields are not added
name|out
operator|.
name|add
argument_list|(
name|f
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Add the copy fields
for|for
control|(
name|SchemaField
name|sf
range|:
name|destArr
control|)
block|{
comment|// check if the copy field is a multivalued or not
if|if
condition|(
operator|!
name|sf
operator|.
name|multiValued
argument_list|()
operator|&&
name|out
operator|.
name|get
argument_list|(
name|sf
operator|.
name|getName
argument_list|()
argument_list|)
operator|!=
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
name|BAD_REQUEST
argument_list|,
literal|"ERROR: multiple values encountered for non multiValued copy field "
operator|+
name|sf
operator|.
name|getName
argument_list|()
operator|+
literal|": "
operator|+
name|val
argument_list|)
throw|;
block|}
name|used
operator|=
literal|true
expr_stmt|;
name|Field
name|f
init|=
name|sf
operator|.
name|createField
argument_list|(
name|val
argument_list|,
name|boost
argument_list|)
decl_stmt|;
if|if
condition|(
name|f
operator|!=
literal|null
condition|)
block|{
comment|// null fields are not added
name|out
operator|.
name|add
argument_list|(
name|f
argument_list|)
expr_stmt|;
block|}
block|}
comment|// In lucene, the boost for a given field is the product of the
comment|// document boost and *all* boosts on values of that field.
comment|// For multi-valued fields, we only want to set the boost on the
comment|// first field.
name|boost
operator|=
literal|1.0f
expr_stmt|;
block|}
comment|// make sure the field was used somehow...
if|if
condition|(
operator|!
name|used
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
name|BAD_REQUEST
argument_list|,
literal|"ERROR:unknown field '"
operator|+
name|name
operator|+
literal|"'"
argument_list|)
throw|;
block|}
block|}
comment|// Now validate required fields or add default values
comment|// fields with default values are defacto 'required'
for|for
control|(
name|SchemaField
name|field
range|:
name|schema
operator|.
name|getRequiredFields
argument_list|()
control|)
block|{
if|if
condition|(
name|out
operator|.
name|getField
argument_list|(
name|field
operator|.
name|getName
argument_list|()
argument_list|)
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|field
operator|.
name|getDefaultValue
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|out
operator|.
name|add
argument_list|(
name|field
operator|.
name|createField
argument_list|(
name|field
operator|.
name|getDefaultValue
argument_list|()
argument_list|,
literal|1.0f
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|String
name|id
init|=
name|schema
operator|.
name|printableUniqueKey
argument_list|(
name|out
argument_list|)
decl_stmt|;
name|String
name|msg
init|=
literal|"Document ["
operator|+
name|id
operator|+
literal|"] missing required field: "
operator|+
name|field
operator|.
name|getName
argument_list|()
decl_stmt|;
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
name|msg
argument_list|)
throw|;
block|}
block|}
block|}
comment|// set the full document boost
if|if
condition|(
name|doc
operator|.
name|getBoost
argument_list|(
literal|null
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|out
operator|.
name|setBoost
argument_list|(
name|doc
operator|.
name|getBoost
argument_list|(
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|out
return|;
block|}
comment|/**    * Add fields from the solr document    *     * TODO: /!\ NOTE /!\ This semantics of this function are still in flux.      * Something somewhere needs to be able to fill up a SolrDocument from    * a lucene document - this is one place that may happen.  It may also be    * moved to an independent function    *     * @since solr 1.3    */
DECL|method|loadStoredFields
specifier|public
name|SolrDocument
name|loadStoredFields
parameter_list|(
name|SolrDocument
name|doc
parameter_list|,
name|Document
name|luceneDoc
parameter_list|)
block|{
for|for
control|(
name|Object
name|f
range|:
name|luceneDoc
operator|.
name|getFields
argument_list|()
control|)
block|{
name|Fieldable
name|field
init|=
operator|(
name|Fieldable
operator|)
name|f
decl_stmt|;
if|if
condition|(
name|field
operator|.
name|isStored
argument_list|()
condition|)
block|{
name|SchemaField
name|sf
init|=
name|schema
operator|.
name|getField
argument_list|(
name|field
operator|.
name|name
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|schema
operator|.
name|isCopyFieldTarget
argument_list|(
name|sf
argument_list|)
condition|)
block|{
name|doc
operator|.
name|addField
argument_list|(
name|field
operator|.
name|name
argument_list|()
argument_list|,
name|sf
operator|.
name|getType
argument_list|()
operator|.
name|toObject
argument_list|(
name|field
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|doc
return|;
block|}
block|}
end_class
end_unit
