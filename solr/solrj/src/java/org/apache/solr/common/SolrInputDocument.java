begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.common
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
package|;
end_package
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Serializable
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
name|LinkedHashMap
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
name|Collection
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import
begin_comment
comment|/**  * Represent the field and boost information needed to construct and index  * a Lucene Document.  Like the SolrDocument, the field values should  * match those specified in schema.xml   *  *  * @since solr 1.3  */
end_comment
begin_class
DECL|class|SolrInputDocument
specifier|public
class|class
name|SolrInputDocument
implements|implements
name|Map
argument_list|<
name|String
argument_list|,
name|SolrInputField
argument_list|>
implements|,
name|Iterable
argument_list|<
name|SolrInputField
argument_list|>
implements|,
name|Serializable
block|{
DECL|field|_fields
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|SolrInputField
argument_list|>
name|_fields
decl_stmt|;
DECL|field|_documentBoost
specifier|private
name|float
name|_documentBoost
init|=
literal|1.0f
decl_stmt|;
DECL|method|SolrInputDocument
specifier|public
name|SolrInputDocument
parameter_list|()
block|{
name|_fields
operator|=
operator|new
name|LinkedHashMap
argument_list|<
name|String
argument_list|,
name|SolrInputField
argument_list|>
argument_list|()
expr_stmt|;
block|}
DECL|method|SolrInputDocument
specifier|public
name|SolrInputDocument
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|SolrInputField
argument_list|>
name|fields
parameter_list|)
block|{
name|_fields
operator|=
name|fields
expr_stmt|;
block|}
comment|/**    * Remove all fields and boosts from the document    */
DECL|method|clear
specifier|public
name|void
name|clear
parameter_list|()
block|{
if|if
condition|(
name|_fields
operator|!=
literal|null
condition|)
block|{
name|_fields
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
comment|///////////////////////////////////////////////////////////////////
comment|// Add / Set fields
comment|///////////////////////////////////////////////////////////////////
comment|/**     * Add a field with implied null value for boost.    *     * @see #addField(String, Object, float)    * @param name name of the field to add    * @param value value of the field    */
DECL|method|addField
specifier|public
name|void
name|addField
parameter_list|(
name|String
name|name
parameter_list|,
name|Object
name|value
parameter_list|)
block|{
name|addField
argument_list|(
name|name
argument_list|,
name|value
argument_list|,
literal|1.0f
argument_list|)
expr_stmt|;
block|}
comment|/** Get the first value for a field.    *     * @param name name of the field to fetch    * @return first value of the field or null if not present    */
DECL|method|getFieldValue
specifier|public
name|Object
name|getFieldValue
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|SolrInputField
name|field
init|=
name|getField
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|Object
name|o
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|field
operator|!=
literal|null
condition|)
name|o
operator|=
name|field
operator|.
name|getFirstValue
argument_list|()
expr_stmt|;
return|return
name|o
return|;
block|}
comment|/** Get all the values for a field.    *     * @param name name of the field to fetch    * @return value of the field or null if not set    */
DECL|method|getFieldValues
specifier|public
name|Collection
argument_list|<
name|Object
argument_list|>
name|getFieldValues
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|SolrInputField
name|field
init|=
name|getField
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|field
operator|!=
literal|null
condition|)
block|{
return|return
name|field
operator|.
name|getValues
argument_list|()
return|;
block|}
return|return
literal|null
return|;
block|}
comment|/** Get all field names.    *     * @return Set of all field names.    */
DECL|method|getFieldNames
specifier|public
name|Collection
argument_list|<
name|String
argument_list|>
name|getFieldNames
parameter_list|()
block|{
return|return
name|_fields
operator|.
name|keySet
argument_list|()
return|;
block|}
comment|/** Set a field with implied null value for boost.    *     * @see #setField(String, Object, float)    * @param name name of the field to set    * @param value value of the field    */
DECL|method|setField
specifier|public
name|void
name|setField
parameter_list|(
name|String
name|name
parameter_list|,
name|Object
name|value
parameter_list|)
block|{
name|setField
argument_list|(
name|name
argument_list|,
name|value
argument_list|,
literal|1.0f
argument_list|)
expr_stmt|;
block|}
DECL|method|setField
specifier|public
name|void
name|setField
parameter_list|(
name|String
name|name
parameter_list|,
name|Object
name|value
parameter_list|,
name|float
name|boost
parameter_list|)
block|{
name|SolrInputField
name|field
init|=
operator|new
name|SolrInputField
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|_fields
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|field
argument_list|)
expr_stmt|;
name|field
operator|.
name|setValue
argument_list|(
name|value
argument_list|,
name|boost
argument_list|)
expr_stmt|;
block|}
comment|/**    * Adds a field with the given name, value and boost.  If a field with the name already exists, then it is updated to    * the new value and boost.    *    * @param name Name of the field to add    * @param value Value of the field    * @param boost Boost value for the field    */
DECL|method|addField
specifier|public
name|void
name|addField
parameter_list|(
name|String
name|name
parameter_list|,
name|Object
name|value
parameter_list|,
name|float
name|boost
parameter_list|)
block|{
name|SolrInputField
name|field
init|=
name|_fields
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|field
operator|==
literal|null
operator|||
name|field
operator|.
name|value
operator|==
literal|null
condition|)
block|{
name|setField
argument_list|(
name|name
argument_list|,
name|value
argument_list|,
name|boost
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|field
operator|.
name|addValue
argument_list|(
name|value
argument_list|,
name|boost
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Remove a field from the document    *     * @param name The field name whose field is to be removed from the document    * @return the previous field with<tt>name</tt>, or    *<tt>null</tt> if there was no field for<tt>key</tt>.    */
DECL|method|removeField
specifier|public
name|SolrInputField
name|removeField
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|_fields
operator|.
name|remove
argument_list|(
name|name
argument_list|)
return|;
block|}
comment|///////////////////////////////////////////////////////////////////
comment|// Get the field values
comment|///////////////////////////////////////////////////////////////////
DECL|method|getField
specifier|public
name|SolrInputField
name|getField
parameter_list|(
name|String
name|field
parameter_list|)
block|{
return|return
name|_fields
operator|.
name|get
argument_list|(
name|field
argument_list|)
return|;
block|}
DECL|method|iterator
specifier|public
name|Iterator
argument_list|<
name|SolrInputField
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
name|_fields
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
return|;
block|}
DECL|method|getDocumentBoost
specifier|public
name|float
name|getDocumentBoost
parameter_list|()
block|{
return|return
name|_documentBoost
return|;
block|}
DECL|method|setDocumentBoost
specifier|public
name|void
name|setDocumentBoost
parameter_list|(
name|float
name|documentBoost
parameter_list|)
block|{
name|_documentBoost
operator|=
name|documentBoost
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"SolrInputDocument["
operator|+
name|_fields
operator|+
literal|"]"
return|;
block|}
comment|//---------------------------------------------------
comment|// MAP interface
comment|//---------------------------------------------------
DECL|method|containsKey
specifier|public
name|boolean
name|containsKey
parameter_list|(
name|Object
name|key
parameter_list|)
block|{
return|return
name|_fields
operator|.
name|containsKey
argument_list|(
name|key
argument_list|)
return|;
block|}
DECL|method|containsValue
specifier|public
name|boolean
name|containsValue
parameter_list|(
name|Object
name|value
parameter_list|)
block|{
return|return
name|_fields
operator|.
name|containsValue
argument_list|(
name|value
argument_list|)
return|;
block|}
DECL|method|entrySet
specifier|public
name|Set
argument_list|<
name|Entry
argument_list|<
name|String
argument_list|,
name|SolrInputField
argument_list|>
argument_list|>
name|entrySet
parameter_list|()
block|{
return|return
name|_fields
operator|.
name|entrySet
argument_list|()
return|;
block|}
DECL|method|get
specifier|public
name|SolrInputField
name|get
parameter_list|(
name|Object
name|key
parameter_list|)
block|{
return|return
name|_fields
operator|.
name|get
argument_list|(
name|key
argument_list|)
return|;
block|}
DECL|method|isEmpty
specifier|public
name|boolean
name|isEmpty
parameter_list|()
block|{
return|return
name|_fields
operator|.
name|isEmpty
argument_list|()
return|;
block|}
DECL|method|keySet
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|keySet
parameter_list|()
block|{
return|return
name|_fields
operator|.
name|keySet
argument_list|()
return|;
block|}
DECL|method|put
specifier|public
name|SolrInputField
name|put
parameter_list|(
name|String
name|key
parameter_list|,
name|SolrInputField
name|value
parameter_list|)
block|{
return|return
name|_fields
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
return|;
block|}
DECL|method|putAll
specifier|public
name|void
name|putAll
parameter_list|(
name|Map
argument_list|<
name|?
extends|extends
name|String
argument_list|,
name|?
extends|extends
name|SolrInputField
argument_list|>
name|t
parameter_list|)
block|{
name|_fields
operator|.
name|putAll
argument_list|(
name|t
argument_list|)
expr_stmt|;
block|}
DECL|method|remove
specifier|public
name|SolrInputField
name|remove
parameter_list|(
name|Object
name|key
parameter_list|)
block|{
return|return
name|_fields
operator|.
name|remove
argument_list|(
name|key
argument_list|)
return|;
block|}
DECL|method|size
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|_fields
operator|.
name|size
argument_list|()
return|;
block|}
DECL|method|values
specifier|public
name|Collection
argument_list|<
name|SolrInputField
argument_list|>
name|values
parameter_list|()
block|{
return|return
name|_fields
operator|.
name|values
argument_list|()
return|;
block|}
block|}
end_class
end_unit
