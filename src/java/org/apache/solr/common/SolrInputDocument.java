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
name|HashMap
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedHashSet
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
comment|/**  * Represent the field and boost information needed to construct and index  * a Lucene Document.  Like the SolrDocument, the field values need to  * match those specified in schema.xml   *   * By default, this will keep every field value added to the document.  To only  * keep distinct values, use setRemoveDuplicateFieldValues( "fieldname", true );  *  * @author ryan  * @version $Id$  * @since solr 1.3  */
end_comment
begin_class
DECL|class|SolrInputDocument
specifier|public
class|class
name|SolrInputDocument
extends|extends
name|SolrDocument
block|{
DECL|field|_boost
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Float
argument_list|>
name|_boost
init|=
literal|null
decl_stmt|;
DECL|field|_removeDuplicates
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Boolean
argument_list|>
name|_removeDuplicates
init|=
literal|null
decl_stmt|;
comment|/**    * Return a base collection to manage the fields for a given value.  If    * the field is defined to be "distinct", the field will be backed as     * a Set rather then a List.  Adding the same value multiple times will    * only keep a single instance of that value.    */
annotation|@
name|Override
DECL|method|getEmptyCollection
specifier|protected
name|Collection
argument_list|<
name|Object
argument_list|>
name|getEmptyCollection
parameter_list|(
name|String
name|name
parameter_list|)
block|{
if|if
condition|(
name|_removeDuplicates
operator|==
literal|null
operator|||
name|Boolean
operator|.
name|FALSE
operator|==
name|_removeDuplicates
operator|.
name|get
argument_list|(
name|name
argument_list|)
condition|)
block|{
return|return
operator|new
name|ArrayList
argument_list|<
name|Object
argument_list|>
argument_list|()
return|;
block|}
return|return
operator|new
name|LinkedHashSet
argument_list|<
name|Object
argument_list|>
argument_list|()
return|;
comment|// keep the order? -- perhaps HashSet?
block|}
comment|/**    * Remove all fields and boosts from the document    */
annotation|@
name|Override
DECL|method|clear
specifier|public
name|void
name|clear
parameter_list|()
block|{
name|super
operator|.
name|clear
argument_list|()
expr_stmt|;
if|if
condition|(
name|_boost
operator|!=
literal|null
condition|)
block|{
name|_boost
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|_removeDuplicates
operator|!=
literal|null
condition|)
block|{
name|_removeDuplicates
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Set the document boost.  null will remove the boost    */
DECL|method|setDocumentBoost
specifier|public
name|void
name|setDocumentBoost
parameter_list|(
name|Float
name|v
parameter_list|)
block|{
name|this
operator|.
name|setBoost
argument_list|(
literal|null
argument_list|,
name|v
argument_list|)
expr_stmt|;
block|}
comment|/**    * @return the document boost.  or null if not set    */
DECL|method|getDocumentBoost
specifier|public
name|Float
name|getDocumentBoost
parameter_list|()
block|{
return|return
name|this
operator|.
name|getBoost
argument_list|(
literal|null
argument_list|)
return|;
block|}
comment|/**    * Get the lucene document boost for a field.  Passing in<code>null</code> returns the    * document boost, not a field boost.      */
DECL|method|setBoost
specifier|public
name|void
name|setBoost
parameter_list|(
name|String
name|name
parameter_list|,
name|Float
name|boost
parameter_list|)
block|{
if|if
condition|(
name|_boost
operator|==
literal|null
condition|)
block|{
name|_boost
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Float
argument_list|>
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|boost
operator|==
literal|null
condition|)
block|{
name|_boost
operator|.
name|remove
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|_boost
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|boost
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Set the field boost.  All fields with the name will have the same boost.      * Passing in<code>null</code> sets the document boost.    * @param boost    */
DECL|method|getBoost
specifier|public
name|Float
name|getBoost
parameter_list|(
name|String
name|name
parameter_list|)
block|{
if|if
condition|(
name|_boost
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|_boost
operator|.
name|get
argument_list|(
name|name
argument_list|)
return|;
block|}
comment|/**    * Should the Document be able to contain duplicate values for the same field?    *     * By default, all field values are maintained.  If you only want to distinct values    * set setKeepDuplicateFieldValues( "fieldname", false );    *     * To change the default behavior, use<code>null</code> as the fieldname.    *     * NOTE: this must be called before adding any values to the given field.    */
DECL|method|setRemoveDuplicateFieldValues
specifier|public
name|void
name|setRemoveDuplicateFieldValues
parameter_list|(
name|String
name|name
parameter_list|,
name|boolean
name|v
parameter_list|)
block|{
if|if
condition|(
name|this
operator|.
name|getFieldValues
argument_list|(
name|name
argument_list|)
operator|!=
literal|null
condition|)
block|{
comment|// If it was not distinct and changed to distinct, we could, but this seems like a better rule
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"You can't change a fields distinctness after it is initialized."
argument_list|)
throw|;
block|}
if|if
condition|(
name|_removeDuplicates
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|v
operator|==
literal|false
condition|)
block|{
comment|// we only care about 'true'  we don't need to make a map unless
comment|// something does not want multiple values
return|return;
block|}
name|_removeDuplicates
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Boolean
argument_list|>
argument_list|()
expr_stmt|;
block|}
name|_removeDuplicates
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|v
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
