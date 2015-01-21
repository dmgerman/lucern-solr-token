begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|SortField
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
name|SimpleOrderedMap
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
name|io
operator|.
name|IOException
import|;
end_import
begin_comment
comment|/**  * Encapsulates all information about a Field in a Solr Schema  *  *  */
end_comment
begin_class
DECL|class|SchemaField
specifier|public
specifier|final
class|class
name|SchemaField
extends|extends
name|FieldProperties
block|{
DECL|field|FIELD_NAME
specifier|private
specifier|static
specifier|final
name|String
name|FIELD_NAME
init|=
literal|"name"
decl_stmt|;
DECL|field|TYPE_NAME
specifier|private
specifier|static
specifier|final
name|String
name|TYPE_NAME
init|=
literal|"type"
decl_stmt|;
DECL|field|DEFAULT_VALUE
specifier|private
specifier|static
specifier|final
name|String
name|DEFAULT_VALUE
init|=
literal|"default"
decl_stmt|;
DECL|field|name
specifier|final
name|String
name|name
decl_stmt|;
DECL|field|type
specifier|final
name|FieldType
name|type
decl_stmt|;
DECL|field|properties
specifier|final
name|int
name|properties
decl_stmt|;
DECL|field|defaultValue
specifier|final
name|String
name|defaultValue
decl_stmt|;
DECL|field|required
name|boolean
name|required
init|=
literal|false
decl_stmt|;
comment|// this can't be final since it may be changed dynamically
comment|/** Declared field property overrides */
DECL|field|args
name|Map
argument_list|<
name|String
argument_list|,
name|?
argument_list|>
name|args
init|=
name|Collections
operator|.
name|emptyMap
argument_list|()
decl_stmt|;
comment|/** Create a new SchemaField with the given name and type,    *  using all the default properties from the type.    */
DECL|method|SchemaField
specifier|public
name|SchemaField
parameter_list|(
name|String
name|name
parameter_list|,
name|FieldType
name|type
parameter_list|)
block|{
name|this
argument_list|(
name|name
argument_list|,
name|type
argument_list|,
name|type
operator|.
name|properties
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/** Create a new SchemaField from an existing one by using all    * of the properties of the prototype except the field name.    */
DECL|method|SchemaField
specifier|public
name|SchemaField
parameter_list|(
name|SchemaField
name|prototype
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|this
argument_list|(
name|name
argument_list|,
name|prototype
operator|.
name|type
argument_list|,
name|prototype
operator|.
name|properties
argument_list|,
name|prototype
operator|.
name|defaultValue
argument_list|)
expr_stmt|;
name|args
operator|=
name|prototype
operator|.
name|args
expr_stmt|;
block|}
comment|/** Create a new SchemaField with the given name and type,    * and with the specified properties.  Properties are *not*    * inherited from the type in this case, so users of this    * constructor should derive the properties from type.getSolrProperties()    *  using all the default properties from the type.    */
DECL|method|SchemaField
specifier|public
name|SchemaField
parameter_list|(
name|String
name|name
parameter_list|,
name|FieldType
name|type
parameter_list|,
name|int
name|properties
parameter_list|,
name|String
name|defaultValue
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
name|this
operator|.
name|properties
operator|=
name|properties
expr_stmt|;
name|this
operator|.
name|defaultValue
operator|=
name|defaultValue
expr_stmt|;
comment|// initialize with the required property flag
name|required
operator|=
operator|(
name|properties
operator|&
name|REQUIRED
operator|)
operator|!=
literal|0
expr_stmt|;
name|type
operator|.
name|checkSchemaField
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
DECL|method|getName
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
DECL|method|getType
specifier|public
name|FieldType
name|getType
parameter_list|()
block|{
return|return
name|type
return|;
block|}
DECL|method|getProperties
specifier|public
name|int
name|getProperties
parameter_list|()
block|{
return|return
name|properties
return|;
block|}
DECL|method|indexed
specifier|public
name|boolean
name|indexed
parameter_list|()
block|{
return|return
operator|(
name|properties
operator|&
name|INDEXED
operator|)
operator|!=
literal|0
return|;
block|}
DECL|method|stored
specifier|public
name|boolean
name|stored
parameter_list|()
block|{
return|return
operator|(
name|properties
operator|&
name|STORED
operator|)
operator|!=
literal|0
return|;
block|}
DECL|method|hasDocValues
specifier|public
name|boolean
name|hasDocValues
parameter_list|()
block|{
return|return
operator|(
name|properties
operator|&
name|DOC_VALUES
operator|)
operator|!=
literal|0
return|;
block|}
DECL|method|storeTermVector
specifier|public
name|boolean
name|storeTermVector
parameter_list|()
block|{
return|return
operator|(
name|properties
operator|&
name|STORE_TERMVECTORS
operator|)
operator|!=
literal|0
return|;
block|}
DECL|method|storeTermPositions
specifier|public
name|boolean
name|storeTermPositions
parameter_list|()
block|{
return|return
operator|(
name|properties
operator|&
name|STORE_TERMPOSITIONS
operator|)
operator|!=
literal|0
return|;
block|}
DECL|method|storeTermOffsets
specifier|public
name|boolean
name|storeTermOffsets
parameter_list|()
block|{
return|return
operator|(
name|properties
operator|&
name|STORE_TERMOFFSETS
operator|)
operator|!=
literal|0
return|;
block|}
DECL|method|omitNorms
specifier|public
name|boolean
name|omitNorms
parameter_list|()
block|{
return|return
operator|(
name|properties
operator|&
name|OMIT_NORMS
operator|)
operator|!=
literal|0
return|;
block|}
DECL|method|omitTermFreqAndPositions
specifier|public
name|boolean
name|omitTermFreqAndPositions
parameter_list|()
block|{
return|return
operator|(
name|properties
operator|&
name|OMIT_TF_POSITIONS
operator|)
operator|!=
literal|0
return|;
block|}
DECL|method|omitPositions
specifier|public
name|boolean
name|omitPositions
parameter_list|()
block|{
return|return
operator|(
name|properties
operator|&
name|OMIT_POSITIONS
operator|)
operator|!=
literal|0
return|;
block|}
DECL|method|storeOffsetsWithPositions
specifier|public
name|boolean
name|storeOffsetsWithPositions
parameter_list|()
block|{
return|return
operator|(
name|properties
operator|&
name|STORE_OFFSETS
operator|)
operator|!=
literal|0
return|;
block|}
DECL|method|multiValued
specifier|public
name|boolean
name|multiValued
parameter_list|()
block|{
return|return
operator|(
name|properties
operator|&
name|MULTIVALUED
operator|)
operator|!=
literal|0
return|;
block|}
DECL|method|sortMissingFirst
specifier|public
name|boolean
name|sortMissingFirst
parameter_list|()
block|{
return|return
operator|(
name|properties
operator|&
name|SORT_MISSING_FIRST
operator|)
operator|!=
literal|0
return|;
block|}
DECL|method|sortMissingLast
specifier|public
name|boolean
name|sortMissingLast
parameter_list|()
block|{
return|return
operator|(
name|properties
operator|&
name|SORT_MISSING_LAST
operator|)
operator|!=
literal|0
return|;
block|}
DECL|method|isRequired
specifier|public
name|boolean
name|isRequired
parameter_list|()
block|{
return|return
name|required
return|;
block|}
comment|// things that should be determined by field type, not set as options
DECL|method|isTokenized
name|boolean
name|isTokenized
parameter_list|()
block|{
return|return
operator|(
name|properties
operator|&
name|TOKENIZED
operator|)
operator|!=
literal|0
return|;
block|}
DECL|method|isBinary
name|boolean
name|isBinary
parameter_list|()
block|{
return|return
operator|(
name|properties
operator|&
name|BINARY
operator|)
operator|!=
literal|0
return|;
block|}
DECL|method|createField
specifier|public
name|StorableField
name|createField
parameter_list|(
name|Object
name|val
parameter_list|,
name|float
name|boost
parameter_list|)
block|{
return|return
name|type
operator|.
name|createField
argument_list|(
name|this
argument_list|,
name|val
argument_list|,
name|boost
argument_list|)
return|;
block|}
DECL|method|createFields
specifier|public
name|List
argument_list|<
name|StorableField
argument_list|>
name|createFields
parameter_list|(
name|Object
name|val
parameter_list|,
name|float
name|boost
parameter_list|)
block|{
return|return
name|type
operator|.
name|createFields
argument_list|(
name|this
argument_list|,
name|val
argument_list|,
name|boost
argument_list|)
return|;
block|}
comment|/**    * If true, then use {@link #createFields(Object, float)}, else use {@link #createField} to save an extra allocation    * @return true if this field is a poly field    */
DECL|method|isPolyField
specifier|public
name|boolean
name|isPolyField
parameter_list|()
block|{
return|return
name|type
operator|.
name|isPolyField
argument_list|()
return|;
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
name|name
operator|+
literal|"{type="
operator|+
name|type
operator|.
name|getTypeName
argument_list|()
operator|+
operator|(
operator|(
name|defaultValue
operator|==
literal|null
operator|)
condition|?
literal|""
else|:
operator|(
literal|",default="
operator|+
name|defaultValue
operator|)
operator|)
operator|+
literal|",properties="
operator|+
name|propertiesToString
argument_list|(
name|properties
argument_list|)
operator|+
operator|(
name|required
condition|?
literal|", required=true"
else|:
literal|""
operator|)
operator|+
literal|"}"
return|;
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
name|StorableField
name|val
parameter_list|)
throws|throws
name|IOException
block|{
comment|// name is passed in because it may be null if name should not be used.
name|type
operator|.
name|write
argument_list|(
name|writer
argument_list|,
name|name
argument_list|,
name|val
argument_list|)
expr_stmt|;
block|}
comment|/**    * Delegates to the FieldType for this field    * @see FieldType#getSortField    */
DECL|method|getSortField
specifier|public
name|SortField
name|getSortField
parameter_list|(
name|boolean
name|top
parameter_list|)
block|{
return|return
name|type
operator|.
name|getSortField
argument_list|(
name|this
argument_list|,
name|top
argument_list|)
return|;
block|}
comment|/**     * Sanity checks that the properties of this field type are plausible     * for a field that may be used in sorting, throwing an appropriate     * exception (including the field name) if it is not.  FieldType subclasses     * can choose to call this method in their getSortField implementation    * @see FieldType#getSortField    */
DECL|method|checkSortability
specifier|public
name|void
name|checkSortability
parameter_list|()
throws|throws
name|SolrException
block|{
if|if
condition|(
operator|!
operator|(
name|indexed
argument_list|()
operator|||
name|hasDocValues
argument_list|()
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
literal|"can not sort on a field which is neither indexed nor has doc values: "
operator|+
name|getName
argument_list|()
argument_list|)
throw|;
block|}
if|if
condition|(
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
literal|"can not sort on multivalued field: "
operator|+
name|getName
argument_list|()
argument_list|)
throw|;
block|}
block|}
comment|/**     * Sanity checks that the properties of this field type are plausible     * for a field that may be used to get a FieldCacheSource, throwing    * an appropriate exception (including the field name) if it is not.      * FieldType subclasses can choose to call this method in their     * getValueSource implementation     * @see FieldType#getValueSource    */
DECL|method|checkFieldCacheSource
specifier|public
name|void
name|checkFieldCacheSource
parameter_list|(
name|QParser
name|parser
parameter_list|)
throws|throws
name|SolrException
block|{
if|if
condition|(
operator|!
operator|(
name|indexed
argument_list|()
operator|||
name|hasDocValues
argument_list|()
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
literal|"can not use FieldCache on a field which is neither indexed nor has doc values: "
operator|+
name|getName
argument_list|()
argument_list|)
throw|;
block|}
if|if
condition|(
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
literal|"can not use FieldCache on multivalued field: "
operator|+
name|getName
argument_list|()
argument_list|)
throw|;
block|}
block|}
DECL|method|create
specifier|static
name|SchemaField
name|create
parameter_list|(
name|String
name|name
parameter_list|,
name|FieldType
name|ft
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|?
argument_list|>
name|props
parameter_list|)
block|{
name|String
name|defaultValue
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|props
operator|.
name|containsKey
argument_list|(
name|DEFAULT_VALUE
argument_list|)
condition|)
block|{
name|defaultValue
operator|=
operator|(
name|String
operator|)
name|props
operator|.
name|get
argument_list|(
name|DEFAULT_VALUE
argument_list|)
expr_stmt|;
block|}
name|SchemaField
name|field
init|=
operator|new
name|SchemaField
argument_list|(
name|name
argument_list|,
name|ft
argument_list|,
name|calcProps
argument_list|(
name|name
argument_list|,
name|ft
argument_list|,
name|props
argument_list|)
argument_list|,
name|defaultValue
argument_list|)
decl_stmt|;
name|field
operator|.
name|args
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|(
name|props
argument_list|)
expr_stmt|;
return|return
name|field
return|;
block|}
comment|/**    * Create a SchemaField w/ the props specified.  Does not support a default value.    * @param name The name of the SchemaField    * @param ft The {@link org.apache.solr.schema.FieldType} of the field    * @param props The props.  See {@link #calcProps(String, org.apache.solr.schema.FieldType, java.util.Map)}    * @param defValue The default Value for the field    * @return The SchemaField    *    * @see #create(String, FieldType, java.util.Map)    */
DECL|method|create
specifier|static
name|SchemaField
name|create
parameter_list|(
name|String
name|name
parameter_list|,
name|FieldType
name|ft
parameter_list|,
name|int
name|props
parameter_list|,
name|String
name|defValue
parameter_list|)
block|{
return|return
operator|new
name|SchemaField
argument_list|(
name|name
argument_list|,
name|ft
argument_list|,
name|props
argument_list|,
name|defValue
argument_list|)
return|;
block|}
DECL|method|calcProps
specifier|static
name|int
name|calcProps
parameter_list|(
name|String
name|name
parameter_list|,
name|FieldType
name|ft
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|?
argument_list|>
name|props
parameter_list|)
block|{
name|int
name|trueProps
init|=
name|parseProperties
argument_list|(
name|props
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|int
name|falseProps
init|=
name|parseProperties
argument_list|(
name|props
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|int
name|p
init|=
name|ft
operator|.
name|properties
decl_stmt|;
comment|//
comment|// If any properties were explicitly turned off, then turn off other properties
comment|// that depend on that.
comment|//
if|if
condition|(
name|on
argument_list|(
name|falseProps
argument_list|,
name|STORED
argument_list|)
condition|)
block|{
name|int
name|pp
init|=
name|STORED
operator||
name|BINARY
decl_stmt|;
if|if
condition|(
name|on
argument_list|(
name|pp
argument_list|,
name|trueProps
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"SchemaField: "
operator|+
name|name
operator|+
literal|" conflicting stored field options:"
operator|+
name|props
argument_list|)
throw|;
block|}
name|p
operator|&=
operator|~
name|pp
expr_stmt|;
block|}
if|if
condition|(
name|on
argument_list|(
name|falseProps
argument_list|,
name|INDEXED
argument_list|)
condition|)
block|{
name|int
name|pp
init|=
operator|(
name|INDEXED
operator||
name|STORE_TERMVECTORS
operator||
name|STORE_TERMPOSITIONS
operator||
name|STORE_TERMOFFSETS
operator|)
decl_stmt|;
if|if
condition|(
name|on
argument_list|(
name|pp
argument_list|,
name|trueProps
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"SchemaField: "
operator|+
name|name
operator|+
literal|" conflicting 'true' field options for non-indexed field:"
operator|+
name|props
argument_list|)
throw|;
block|}
name|p
operator|&=
operator|~
name|pp
expr_stmt|;
block|}
if|if
condition|(
name|on
argument_list|(
name|falseProps
argument_list|,
name|INDEXED
argument_list|)
operator|&&
name|on
argument_list|(
name|falseProps
argument_list|,
name|DOC_VALUES
argument_list|)
condition|)
block|{
name|int
name|pp
init|=
operator|(
name|SORT_MISSING_FIRST
operator||
name|SORT_MISSING_LAST
operator|)
decl_stmt|;
if|if
condition|(
name|on
argument_list|(
name|pp
argument_list|,
name|trueProps
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"SchemaField: "
operator|+
name|name
operator|+
literal|" conflicting 'true' field options for non-indexed/non-docValues field:"
operator|+
name|props
argument_list|)
throw|;
block|}
name|p
operator|&=
operator|~
name|pp
expr_stmt|;
block|}
if|if
condition|(
name|on
argument_list|(
name|falseProps
argument_list|,
name|INDEXED
argument_list|)
condition|)
block|{
name|int
name|pp
init|=
operator|(
name|OMIT_NORMS
operator||
name|OMIT_TF_POSITIONS
operator||
name|OMIT_POSITIONS
operator|)
decl_stmt|;
if|if
condition|(
name|on
argument_list|(
name|pp
argument_list|,
name|falseProps
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"SchemaField: "
operator|+
name|name
operator|+
literal|" conflicting 'false' field options for non-indexed field:"
operator|+
name|props
argument_list|)
throw|;
block|}
name|p
operator|&=
operator|~
name|pp
expr_stmt|;
block|}
if|if
condition|(
name|on
argument_list|(
name|trueProps
argument_list|,
name|OMIT_TF_POSITIONS
argument_list|)
condition|)
block|{
name|int
name|pp
init|=
operator|(
name|OMIT_POSITIONS
operator||
name|OMIT_TF_POSITIONS
operator|)
decl_stmt|;
if|if
condition|(
name|on
argument_list|(
name|pp
argument_list|,
name|falseProps
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"SchemaField: "
operator|+
name|name
operator|+
literal|" conflicting tf and position field options:"
operator|+
name|props
argument_list|)
throw|;
block|}
name|p
operator|&=
operator|~
name|pp
expr_stmt|;
block|}
if|if
condition|(
name|on
argument_list|(
name|falseProps
argument_list|,
name|STORE_TERMVECTORS
argument_list|)
condition|)
block|{
name|int
name|pp
init|=
operator|(
name|STORE_TERMVECTORS
operator||
name|STORE_TERMPOSITIONS
operator||
name|STORE_TERMOFFSETS
operator|)
decl_stmt|;
if|if
condition|(
name|on
argument_list|(
name|pp
argument_list|,
name|trueProps
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"SchemaField: "
operator|+
name|name
operator|+
literal|" conflicting termvector field options:"
operator|+
name|props
argument_list|)
throw|;
block|}
name|p
operator|&=
operator|~
name|pp
expr_stmt|;
block|}
comment|// override sort flags
if|if
condition|(
name|on
argument_list|(
name|trueProps
argument_list|,
name|SORT_MISSING_FIRST
argument_list|)
condition|)
block|{
name|p
operator|&=
operator|~
name|SORT_MISSING_LAST
expr_stmt|;
block|}
if|if
condition|(
name|on
argument_list|(
name|trueProps
argument_list|,
name|SORT_MISSING_LAST
argument_list|)
condition|)
block|{
name|p
operator|&=
operator|~
name|SORT_MISSING_FIRST
expr_stmt|;
block|}
name|p
operator|&=
operator|~
name|falseProps
expr_stmt|;
name|p
operator||=
name|trueProps
expr_stmt|;
return|return
name|p
return|;
block|}
DECL|method|getDefaultValue
specifier|public
name|String
name|getDefaultValue
parameter_list|()
block|{
return|return
name|defaultValue
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|name
operator|.
name|hashCode
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
return|return
operator|(
name|obj
operator|instanceof
name|SchemaField
operator|)
operator|&&
name|name
operator|.
name|equals
argument_list|(
operator|(
operator|(
name|SchemaField
operator|)
name|obj
operator|)
operator|.
name|name
argument_list|)
return|;
block|}
comment|/**    * Get a map of property name -> value for this field.  If showDefaults is true,    * include default properties (those inherited from the declared property type and    * not overridden in the field declaration).    */
DECL|method|getNamedPropertyValues
specifier|public
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
name|getNamedPropertyValues
parameter_list|(
name|boolean
name|showDefaults
parameter_list|)
block|{
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
name|properties
init|=
operator|new
name|SimpleOrderedMap
argument_list|<>
argument_list|()
decl_stmt|;
name|properties
operator|.
name|add
argument_list|(
name|FIELD_NAME
argument_list|,
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|properties
operator|.
name|add
argument_list|(
name|TYPE_NAME
argument_list|,
name|getType
argument_list|()
operator|.
name|getTypeName
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|showDefaults
condition|)
block|{
if|if
condition|(
literal|null
operator|!=
name|getDefaultValue
argument_list|()
condition|)
block|{
name|properties
operator|.
name|add
argument_list|(
name|DEFAULT_VALUE
argument_list|,
name|getDefaultValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|properties
operator|.
name|add
argument_list|(
name|getPropertyName
argument_list|(
name|INDEXED
argument_list|)
argument_list|,
name|indexed
argument_list|()
argument_list|)
expr_stmt|;
name|properties
operator|.
name|add
argument_list|(
name|getPropertyName
argument_list|(
name|STORED
argument_list|)
argument_list|,
name|stored
argument_list|()
argument_list|)
expr_stmt|;
name|properties
operator|.
name|add
argument_list|(
name|getPropertyName
argument_list|(
name|DOC_VALUES
argument_list|)
argument_list|,
name|hasDocValues
argument_list|()
argument_list|)
expr_stmt|;
name|properties
operator|.
name|add
argument_list|(
name|getPropertyName
argument_list|(
name|STORE_TERMVECTORS
argument_list|)
argument_list|,
name|storeTermVector
argument_list|()
argument_list|)
expr_stmt|;
name|properties
operator|.
name|add
argument_list|(
name|getPropertyName
argument_list|(
name|STORE_TERMPOSITIONS
argument_list|)
argument_list|,
name|storeTermPositions
argument_list|()
argument_list|)
expr_stmt|;
name|properties
operator|.
name|add
argument_list|(
name|getPropertyName
argument_list|(
name|STORE_TERMOFFSETS
argument_list|)
argument_list|,
name|storeTermOffsets
argument_list|()
argument_list|)
expr_stmt|;
name|properties
operator|.
name|add
argument_list|(
name|getPropertyName
argument_list|(
name|OMIT_NORMS
argument_list|)
argument_list|,
name|omitNorms
argument_list|()
argument_list|)
expr_stmt|;
name|properties
operator|.
name|add
argument_list|(
name|getPropertyName
argument_list|(
name|OMIT_TF_POSITIONS
argument_list|)
argument_list|,
name|omitTermFreqAndPositions
argument_list|()
argument_list|)
expr_stmt|;
name|properties
operator|.
name|add
argument_list|(
name|getPropertyName
argument_list|(
name|OMIT_POSITIONS
argument_list|)
argument_list|,
name|omitPositions
argument_list|()
argument_list|)
expr_stmt|;
name|properties
operator|.
name|add
argument_list|(
name|getPropertyName
argument_list|(
name|STORE_OFFSETS
argument_list|)
argument_list|,
name|storeOffsetsWithPositions
argument_list|()
argument_list|)
expr_stmt|;
name|properties
operator|.
name|add
argument_list|(
name|getPropertyName
argument_list|(
name|MULTIVALUED
argument_list|)
argument_list|,
name|multiValued
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|sortMissingFirst
argument_list|()
condition|)
block|{
name|properties
operator|.
name|add
argument_list|(
name|getPropertyName
argument_list|(
name|SORT_MISSING_FIRST
argument_list|)
argument_list|,
name|sortMissingFirst
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|sortMissingLast
argument_list|()
condition|)
block|{
name|properties
operator|.
name|add
argument_list|(
name|getPropertyName
argument_list|(
name|SORT_MISSING_LAST
argument_list|)
argument_list|,
name|sortMissingLast
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|properties
operator|.
name|add
argument_list|(
name|getPropertyName
argument_list|(
name|REQUIRED
argument_list|)
argument_list|,
name|isRequired
argument_list|()
argument_list|)
expr_stmt|;
name|properties
operator|.
name|add
argument_list|(
name|getPropertyName
argument_list|(
name|TOKENIZED
argument_list|)
argument_list|,
name|isTokenized
argument_list|()
argument_list|)
expr_stmt|;
comment|// The BINARY property is always false
comment|// properties.add(getPropertyName(BINARY), isBinary());
block|}
else|else
block|{
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|?
argument_list|>
name|arg
range|:
name|args
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
name|key
init|=
name|arg
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|Object
name|value
init|=
name|arg
operator|.
name|getValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|key
operator|.
name|equals
argument_list|(
name|DEFAULT_VALUE
argument_list|)
condition|)
block|{
name|properties
operator|.
name|add
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|boolean
name|boolVal
init|=
name|value
operator|instanceof
name|Boolean
condition|?
operator|(
name|Boolean
operator|)
name|value
else|:
name|Boolean
operator|.
name|parseBoolean
argument_list|(
name|value
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|properties
operator|.
name|add
argument_list|(
name|key
argument_list|,
name|boolVal
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|properties
return|;
block|}
block|}
end_class
end_unit
