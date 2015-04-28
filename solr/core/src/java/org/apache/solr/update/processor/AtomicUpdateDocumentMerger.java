begin_unit
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
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|HashSet
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
name|regex
operator|.
name|Matcher
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
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
name|BytesRefBuilder
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
name|common
operator|.
name|SolrInputField
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
name|SolrQueryRequest
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
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|update
operator|.
name|AddUpdateCommand
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
begin_comment
comment|/**  * @lucene.experimental  */
end_comment
begin_class
DECL|class|AtomicUpdateDocumentMerger
specifier|public
class|class
name|AtomicUpdateDocumentMerger
block|{
DECL|field|log
specifier|private
specifier|final
specifier|static
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|AtomicUpdateDocumentMerger
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|schema
specifier|protected
specifier|final
name|IndexSchema
name|schema
decl_stmt|;
DECL|field|idField
specifier|protected
specifier|final
name|SchemaField
name|idField
decl_stmt|;
DECL|method|AtomicUpdateDocumentMerger
specifier|public
name|AtomicUpdateDocumentMerger
parameter_list|(
name|SolrQueryRequest
name|queryReq
parameter_list|)
block|{
name|schema
operator|=
name|queryReq
operator|.
name|getSchema
argument_list|()
expr_stmt|;
name|idField
operator|=
name|schema
operator|.
name|getUniqueKeyField
argument_list|()
expr_stmt|;
block|}
comment|/**    * Utility method that examines the SolrInputDocument in an AddUpdateCommand    * and returns true if the documents contains atomic update instructions.    */
DECL|method|isAtomicUpdate
specifier|public
specifier|static
name|boolean
name|isAtomicUpdate
parameter_list|(
specifier|final
name|AddUpdateCommand
name|cmd
parameter_list|)
block|{
name|SolrInputDocument
name|sdoc
init|=
name|cmd
operator|.
name|getSolrInputDocument
argument_list|()
decl_stmt|;
for|for
control|(
name|SolrInputField
name|sif
range|:
name|sdoc
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|sif
operator|.
name|getValue
argument_list|()
operator|instanceof
name|Map
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
comment|/**    * Merges the fromDoc into the toDoc using the atomic update syntax.    *     * @param fromDoc SolrInputDocument which will merged into the toDoc    * @param toDoc the final SolrInputDocument that will be mutated with the values from the fromDoc atomic commands    * @return toDoc with mutated values    */
DECL|method|merge
specifier|public
name|SolrInputDocument
name|merge
parameter_list|(
specifier|final
name|SolrInputDocument
name|fromDoc
parameter_list|,
name|SolrInputDocument
name|toDoc
parameter_list|)
block|{
for|for
control|(
name|SolrInputField
name|sif
range|:
name|fromDoc
operator|.
name|values
argument_list|()
control|)
block|{
name|Object
name|val
init|=
name|sif
operator|.
name|getValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|val
operator|instanceof
name|Map
condition|)
block|{
for|for
control|(
name|Entry
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|entry
range|:
operator|(
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|val
operator|)
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
name|key
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|Object
name|fieldVal
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|boolean
name|updateField
init|=
literal|false
decl_stmt|;
switch|switch
condition|(
name|key
condition|)
block|{
case|case
literal|"add"
case|:
name|updateField
operator|=
literal|true
expr_stmt|;
name|doAdd
argument_list|(
name|toDoc
argument_list|,
name|sif
argument_list|,
name|fieldVal
argument_list|)
expr_stmt|;
break|break;
case|case
literal|"set"
case|:
name|updateField
operator|=
literal|true
expr_stmt|;
name|doSet
argument_list|(
name|toDoc
argument_list|,
name|sif
argument_list|,
name|fieldVal
argument_list|)
expr_stmt|;
break|break;
case|case
literal|"remove"
case|:
name|updateField
operator|=
literal|true
expr_stmt|;
name|doRemove
argument_list|(
name|toDoc
argument_list|,
name|sif
argument_list|,
name|fieldVal
argument_list|)
expr_stmt|;
break|break;
case|case
literal|"removeregex"
case|:
name|updateField
operator|=
literal|true
expr_stmt|;
name|doRemoveRegex
argument_list|(
name|toDoc
argument_list|,
name|sif
argument_list|,
name|fieldVal
argument_list|)
expr_stmt|;
break|break;
case|case
literal|"inc"
case|:
name|updateField
operator|=
literal|true
expr_stmt|;
name|doInc
argument_list|(
name|toDoc
argument_list|,
name|sif
argument_list|,
name|fieldVal
argument_list|)
expr_stmt|;
break|break;
default|default:
comment|//Perhaps throw an error here instead?
name|log
operator|.
name|warn
argument_list|(
literal|"Unknown operation for the an atomic update, operation ignored: "
operator|+
name|key
argument_list|)
expr_stmt|;
break|break;
block|}
comment|// validate that the field being modified is not the id field.
if|if
condition|(
name|updateField
operator|&&
name|idField
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|sif
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"Invalid update of id field: "
operator|+
name|sif
argument_list|)
throw|;
block|}
block|}
block|}
else|else
block|{
comment|// normal fields are treated as a "set"
name|toDoc
operator|.
name|put
argument_list|(
name|sif
operator|.
name|getName
argument_list|()
argument_list|,
name|sif
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|toDoc
return|;
block|}
DECL|method|doSet
specifier|protected
name|void
name|doSet
parameter_list|(
name|SolrInputDocument
name|toDoc
parameter_list|,
name|SolrInputField
name|sif
parameter_list|,
name|Object
name|fieldVal
parameter_list|)
block|{
name|toDoc
operator|.
name|setField
argument_list|(
name|sif
operator|.
name|getName
argument_list|()
argument_list|,
name|fieldVal
argument_list|,
name|sif
operator|.
name|getBoost
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|doAdd
specifier|protected
name|void
name|doAdd
parameter_list|(
name|SolrInputDocument
name|toDoc
parameter_list|,
name|SolrInputField
name|sif
parameter_list|,
name|Object
name|fieldVal
parameter_list|)
block|{
name|toDoc
operator|.
name|addField
argument_list|(
name|sif
operator|.
name|getName
argument_list|()
argument_list|,
name|fieldVal
argument_list|,
name|sif
operator|.
name|getBoost
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|doInc
specifier|protected
name|void
name|doInc
parameter_list|(
name|SolrInputDocument
name|toDoc
parameter_list|,
name|SolrInputField
name|sif
parameter_list|,
name|Object
name|fieldVal
parameter_list|)
block|{
name|SolrInputField
name|numericField
init|=
name|toDoc
operator|.
name|get
argument_list|(
name|sif
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|numericField
operator|==
literal|null
condition|)
block|{
name|toDoc
operator|.
name|setField
argument_list|(
name|sif
operator|.
name|getName
argument_list|()
argument_list|,
name|fieldVal
argument_list|,
name|sif
operator|.
name|getBoost
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// TODO: fieldtype needs externalToObject?
name|String
name|oldValS
init|=
name|numericField
operator|.
name|getFirstValue
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|SchemaField
name|sf
init|=
name|schema
operator|.
name|getField
argument_list|(
name|sif
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|BytesRefBuilder
name|term
init|=
operator|new
name|BytesRefBuilder
argument_list|()
decl_stmt|;
name|sf
operator|.
name|getType
argument_list|()
operator|.
name|readableToIndexed
argument_list|(
name|oldValS
argument_list|,
name|term
argument_list|)
expr_stmt|;
name|Object
name|oldVal
init|=
name|sf
operator|.
name|getType
argument_list|()
operator|.
name|toObject
argument_list|(
name|sf
argument_list|,
name|term
operator|.
name|get
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|fieldValS
init|=
name|fieldVal
operator|.
name|toString
argument_list|()
decl_stmt|;
name|Number
name|result
decl_stmt|;
if|if
condition|(
name|oldVal
operator|instanceof
name|Long
condition|)
block|{
name|result
operator|=
operator|(
operator|(
name|Long
operator|)
name|oldVal
operator|)
operator|.
name|longValue
argument_list|()
operator|+
name|Long
operator|.
name|parseLong
argument_list|(
name|fieldValS
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|oldVal
operator|instanceof
name|Float
condition|)
block|{
name|result
operator|=
operator|(
operator|(
name|Float
operator|)
name|oldVal
operator|)
operator|.
name|floatValue
argument_list|()
operator|+
name|Float
operator|.
name|parseFloat
argument_list|(
name|fieldValS
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|oldVal
operator|instanceof
name|Double
condition|)
block|{
name|result
operator|=
operator|(
operator|(
name|Double
operator|)
name|oldVal
operator|)
operator|.
name|doubleValue
argument_list|()
operator|+
name|Double
operator|.
name|parseDouble
argument_list|(
name|fieldValS
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// int, short, byte
name|result
operator|=
operator|(
operator|(
name|Integer
operator|)
name|oldVal
operator|)
operator|.
name|intValue
argument_list|()
operator|+
name|Integer
operator|.
name|parseInt
argument_list|(
name|fieldValS
argument_list|)
expr_stmt|;
block|}
name|toDoc
operator|.
name|setField
argument_list|(
name|sif
operator|.
name|getName
argument_list|()
argument_list|,
name|result
argument_list|,
name|sif
operator|.
name|getBoost
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|doRemove
specifier|protected
name|void
name|doRemove
parameter_list|(
name|SolrInputDocument
name|toDoc
parameter_list|,
name|SolrInputField
name|sif
parameter_list|,
name|Object
name|fieldVal
parameter_list|)
block|{
specifier|final
name|String
name|name
init|=
name|sif
operator|.
name|getName
argument_list|()
decl_stmt|;
name|SolrInputField
name|existingField
init|=
name|toDoc
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|existingField
operator|==
literal|null
condition|)
return|return;
name|SchemaField
name|sf
init|=
name|schema
operator|.
name|getField
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|sf
operator|!=
literal|null
condition|)
block|{
specifier|final
name|Collection
argument_list|<
name|Object
argument_list|>
name|original
init|=
name|existingField
operator|.
name|getValues
argument_list|()
decl_stmt|;
if|if
condition|(
name|fieldVal
operator|instanceof
name|Collection
condition|)
block|{
for|for
control|(
name|Object
name|object
range|:
operator|(
name|Collection
operator|)
name|fieldVal
control|)
block|{
name|original
operator|.
name|remove
argument_list|(
name|sf
operator|.
name|getType
argument_list|()
operator|.
name|toNativeType
argument_list|(
name|object
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|original
operator|.
name|remove
argument_list|(
name|sf
operator|.
name|getType
argument_list|()
operator|.
name|toNativeType
argument_list|(
name|fieldVal
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|toDoc
operator|.
name|setField
argument_list|(
name|name
argument_list|,
name|original
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|doRemoveRegex
specifier|protected
name|void
name|doRemoveRegex
parameter_list|(
name|SolrInputDocument
name|toDoc
parameter_list|,
name|SolrInputField
name|sif
parameter_list|,
name|Object
name|valuePatterns
parameter_list|)
block|{
specifier|final
name|String
name|name
init|=
name|sif
operator|.
name|getName
argument_list|()
decl_stmt|;
specifier|final
name|SolrInputField
name|existingField
init|=
name|toDoc
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|existingField
operator|!=
literal|null
condition|)
block|{
specifier|final
name|Collection
argument_list|<
name|Object
argument_list|>
name|valueToRemove
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|Collection
argument_list|<
name|Object
argument_list|>
name|original
init|=
name|existingField
operator|.
name|getValues
argument_list|()
decl_stmt|;
specifier|final
name|Collection
argument_list|<
name|Pattern
argument_list|>
name|patterns
init|=
name|preparePatterns
argument_list|(
name|valuePatterns
argument_list|)
decl_stmt|;
for|for
control|(
name|Object
name|value
range|:
name|original
control|)
block|{
for|for
control|(
name|Pattern
name|pattern
range|:
name|patterns
control|)
block|{
specifier|final
name|Matcher
name|m
init|=
name|pattern
operator|.
name|matcher
argument_list|(
name|value
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|m
operator|.
name|matches
argument_list|()
condition|)
block|{
name|valueToRemove
operator|.
name|add
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|original
operator|.
name|removeAll
argument_list|(
name|valueToRemove
argument_list|)
expr_stmt|;
name|toDoc
operator|.
name|setField
argument_list|(
name|name
argument_list|,
name|original
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|preparePatterns
specifier|private
name|Collection
argument_list|<
name|Pattern
argument_list|>
name|preparePatterns
parameter_list|(
name|Object
name|fieldVal
parameter_list|)
block|{
specifier|final
name|Collection
argument_list|<
name|Pattern
argument_list|>
name|patterns
init|=
operator|new
name|LinkedHashSet
argument_list|<>
argument_list|(
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|fieldVal
operator|instanceof
name|Collection
condition|)
block|{
name|Collection
argument_list|<
name|String
argument_list|>
name|patternVals
init|=
operator|(
name|Collection
argument_list|<
name|String
argument_list|>
operator|)
name|fieldVal
decl_stmt|;
for|for
control|(
name|String
name|patternVal
range|:
name|patternVals
control|)
block|{
name|patterns
operator|.
name|add
argument_list|(
name|Pattern
operator|.
name|compile
argument_list|(
name|patternVal
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|patterns
operator|.
name|add
argument_list|(
name|Pattern
operator|.
name|compile
argument_list|(
name|fieldVal
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|patterns
return|;
block|}
block|}
end_class
end_unit
