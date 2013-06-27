begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.update.processor
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|update
operator|.
name|processor
package|;
end_package
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
name|SolrInputField
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
name|java
operator|.
name|util
operator|.
name|Locale
import|;
end_import
begin_comment
comment|/**  * Abstract subclass of FieldMutatingUpdateProcessor for implementing   * UpdateProcessors that will mutate all individual values of a selected   * field independently.  If not all individual values are acceptable  * - i.e., mutateValue(srcVal) returns {@link #SKIP_FIELD_VALUE_LIST_SINGLETON}  * for at least one value - then none of the values are mutated:  * mutate(srcField) will return srcField.  *  * @see FieldMutatingUpdateProcessorFactory  * @see FieldValueMutatingUpdateProcessor  */
end_comment
begin_class
DECL|class|AllValuesOrNoneFieldMutatingUpdateProcessor
specifier|public
specifier|abstract
class|class
name|AllValuesOrNoneFieldMutatingUpdateProcessor
extends|extends
name|FieldMutatingUpdateProcessor
block|{
DECL|field|log
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|AllValuesOrNoneFieldMutatingUpdateProcessor
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|DELETE_VALUE_SINGLETON
specifier|public
specifier|static
specifier|final
name|Object
name|DELETE_VALUE_SINGLETON
init|=
operator|new
name|Object
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"!!Singleton Object Triggering Value Deletion!!"
return|;
block|}
block|}
decl_stmt|;
DECL|field|SKIP_FIELD_VALUE_LIST_SINGLETON
specifier|public
specifier|static
specifier|final
name|Object
name|SKIP_FIELD_VALUE_LIST_SINGLETON
init|=
operator|new
name|Object
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"!!Singleton Object Triggering Skipping Field Mutation!!"
return|;
block|}
block|}
decl_stmt|;
DECL|method|AllValuesOrNoneFieldMutatingUpdateProcessor
specifier|public
name|AllValuesOrNoneFieldMutatingUpdateProcessor
parameter_list|(
name|FieldNameSelector
name|selector
parameter_list|,
name|UpdateRequestProcessor
name|next
parameter_list|)
block|{
name|super
argument_list|(
name|selector
argument_list|,
name|next
argument_list|)
expr_stmt|;
block|}
comment|/**    * Mutates individual values of a field as needed, or returns the original     * value.    *    * @param srcVal a value from a matched field which should be mutated    * @return the value to use as a replacement for src, or     *<code>DELETE_VALUE_SINGLETON</code> to indicate that the value     *         should be removed completely, or    *<code>SKIP_FIELD_VALUE_LIST_SINGLETON</code> to indicate that    *         a field value is not consistent with     * @see #DELETE_VALUE_SINGLETON    * @see #SKIP_FIELD_VALUE_LIST_SINGLETON    */
DECL|method|mutateValue
specifier|protected
specifier|abstract
name|Object
name|mutateValue
parameter_list|(
specifier|final
name|Object
name|srcVal
parameter_list|)
function_decl|;
DECL|method|mutate
specifier|protected
specifier|final
name|SolrInputField
name|mutate
parameter_list|(
specifier|final
name|SolrInputField
name|srcField
parameter_list|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|messages
init|=
literal|null
decl_stmt|;
name|SolrInputField
name|result
init|=
operator|new
name|SolrInputField
argument_list|(
name|srcField
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
specifier|final
name|Object
name|srcVal
range|:
name|srcField
operator|.
name|getValues
argument_list|()
control|)
block|{
specifier|final
name|Object
name|destVal
init|=
name|mutateValue
argument_list|(
name|srcVal
argument_list|)
decl_stmt|;
if|if
condition|(
name|SKIP_FIELD_VALUE_LIST_SINGLETON
operator|==
name|destVal
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"field '{}' {} value '{}' is not mutatable, so no values will be mutated"
argument_list|,
operator|new
name|Object
index|[]
block|{
name|srcField
operator|.
name|getName
argument_list|()
block|,
name|srcVal
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
block|,
name|srcVal
block|}
argument_list|)
expr_stmt|;
return|return
name|srcField
return|;
block|}
if|if
condition|(
name|DELETE_VALUE_SINGLETON
operator|==
name|destVal
condition|)
block|{
if|if
condition|(
name|log
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
if|if
condition|(
literal|null
operator|==
name|messages
condition|)
block|{
name|messages
operator|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
expr_stmt|;
block|}
name|messages
operator|.
name|add
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"removing value from field '%s': %s '%s'"
argument_list|,
name|srcField
operator|.
name|getName
argument_list|()
argument_list|,
name|srcVal
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
argument_list|,
name|srcVal
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
if|if
condition|(
name|log
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
if|if
condition|(
literal|null
operator|==
name|messages
condition|)
block|{
name|messages
operator|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
expr_stmt|;
block|}
name|messages
operator|.
name|add
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"replace value from field '%s': %s '%s' with %s '%s'"
argument_list|,
name|srcField
operator|.
name|getName
argument_list|()
argument_list|,
name|srcVal
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
argument_list|,
name|srcVal
argument_list|,
name|destVal
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
argument_list|,
name|destVal
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|result
operator|.
name|addValue
argument_list|(
name|destVal
argument_list|,
literal|1.0F
argument_list|)
expr_stmt|;
block|}
block|}
name|result
operator|.
name|setBoost
argument_list|(
name|srcField
operator|.
name|getBoost
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
literal|null
operator|!=
name|messages
operator|&&
name|log
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
for|for
control|(
name|String
name|message
range|:
name|messages
control|)
block|{
name|log
operator|.
name|debug
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
block|}
return|return
literal|0
operator|==
name|result
operator|.
name|getValueCount
argument_list|()
condition|?
literal|null
else|:
name|result
return|;
block|}
block|}
end_class
end_unit
