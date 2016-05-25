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
name|DirectoryReader
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
name|IndexableField
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
name|LeafReaderContext
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
name|ReaderUtil
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
name|queries
operator|.
name|function
operator|.
name|FunctionValues
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
name|queries
operator|.
name|function
operator|.
name|ValueSource
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
name|queries
operator|.
name|function
operator|.
name|docvalues
operator|.
name|IntDocValues
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
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|uninverting
operator|.
name|UninvertingReader
operator|.
name|Type
import|;
end_import
begin_comment
comment|/**  * Utility Field used for random sorting.  It should not be passed a value.  *<p>  * This random sorting implementation uses the dynamic field name to set the  * random 'seed'.  To get random sorting order, you need to use a random  * dynamic field name.  For example, you will need to configure schema.xml:  *<pre>  *&lt;types&gt;  *  ...  *&lt;fieldType name="random" class="solr.RandomSortField" /&gt;  *  ...   *&lt;/types&gt;  *&lt;fields&gt;  *  ...  *&lt;dynamicField name="random*" type="random" indexed="true" stored="false"/&gt;  *  ...  *&lt;/fields&gt;  *</pre>  *   * Examples of queries:  *<ul>  *<li>http://localhost:8983/solr/select/?q=*:*&amp;fl=name&amp;sort=random_1234%20desc</li>  *<li>http://localhost:8983/solr/select/?q=*:*&amp;fl=name&amp;sort=random_2345%20desc</li>  *<li>http://localhost:8983/solr/select/?q=*:*&amp;fl=name&amp;sort=random_ABDC%20desc</li>  *<li>http://localhost:8983/solr/select/?q=*:*&amp;fl=name&amp;sort=random_21%20desc</li>  *</ul>  * Note that multiple calls to the same URL will return the same sorting order.  *   *  * @since solr 1.3  */
end_comment
begin_class
DECL|class|RandomSortField
specifier|public
class|class
name|RandomSortField
extends|extends
name|FieldType
block|{
comment|// Thomas Wang's hash32shift function, from http://www.cris.com/~Ttwang/tech/inthash.htm
comment|// slightly modified to return only positive integers.
DECL|method|hash
specifier|private
specifier|static
name|int
name|hash
parameter_list|(
name|int
name|key
parameter_list|)
block|{
name|key
operator|=
operator|~
name|key
operator|+
operator|(
name|key
operator|<<
literal|15
operator|)
expr_stmt|;
comment|// key = (key<< 15) - key - 1;
name|key
operator|=
name|key
operator|^
operator|(
name|key
operator|>>>
literal|12
operator|)
expr_stmt|;
name|key
operator|=
name|key
operator|+
operator|(
name|key
operator|<<
literal|2
operator|)
expr_stmt|;
name|key
operator|=
name|key
operator|^
operator|(
name|key
operator|>>>
literal|4
operator|)
expr_stmt|;
name|key
operator|=
name|key
operator|*
literal|2057
expr_stmt|;
comment|// key = (key + (key<< 3)) + (key<< 11);
name|key
operator|=
name|key
operator|^
operator|(
name|key
operator|>>>
literal|16
operator|)
expr_stmt|;
return|return
name|key
operator|>>>
literal|1
return|;
block|}
comment|/**     * Given a field name and an IndexReader, get a random hash seed.    * Using dynamic fields, you can force the random order to change     */
DECL|method|getSeed
specifier|private
specifier|static
name|int
name|getSeed
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|LeafReaderContext
name|context
parameter_list|)
block|{
specifier|final
name|DirectoryReader
name|top
init|=
operator|(
name|DirectoryReader
operator|)
name|ReaderUtil
operator|.
name|getTopLevelContext
argument_list|(
name|context
argument_list|)
operator|.
name|reader
argument_list|()
decl_stmt|;
comment|// calling getVersion() on a segment will currently give you a null pointer exception, so
comment|// we use the top-level reader.
return|return
name|fieldName
operator|.
name|hashCode
argument_list|()
operator|+
name|context
operator|.
name|docBase
operator|+
operator|(
name|int
operator|)
name|top
operator|.
name|getVersion
argument_list|()
return|;
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
name|reverse
parameter_list|)
block|{
return|return
operator|new
name|SortField
argument_list|(
name|field
operator|.
name|getName
argument_list|()
argument_list|,
name|randomComparatorSource
argument_list|,
name|reverse
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getUninversionType
specifier|public
name|Type
name|getUninversionType
parameter_list|(
name|SchemaField
name|sf
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|getValueSource
specifier|public
name|ValueSource
name|getValueSource
parameter_list|(
name|SchemaField
name|field
parameter_list|,
name|QParser
name|qparser
parameter_list|)
block|{
return|return
operator|new
name|RandomValueSource
argument_list|(
name|field
operator|.
name|getName
argument_list|()
argument_list|)
return|;
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
name|IndexableField
name|f
parameter_list|)
throws|throws
name|IOException
block|{ }
DECL|field|randomComparatorSource
specifier|private
specifier|static
name|FieldComparatorSource
name|randomComparatorSource
init|=
operator|new
name|FieldComparatorSource
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|FieldComparator
argument_list|<
name|Integer
argument_list|>
name|newComparator
parameter_list|(
specifier|final
name|String
name|fieldname
parameter_list|,
specifier|final
name|int
name|numHits
parameter_list|,
name|int
name|sortPos
parameter_list|,
name|boolean
name|reversed
parameter_list|)
block|{
return|return
operator|new
name|SimpleFieldComparator
argument_list|<
name|Integer
argument_list|>
argument_list|()
block|{
name|int
name|seed
decl_stmt|;
specifier|private
specifier|final
name|int
index|[]
name|values
init|=
operator|new
name|int
index|[
name|numHits
index|]
decl_stmt|;
name|int
name|bottomVal
decl_stmt|;
name|int
name|topVal
decl_stmt|;
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
name|int
name|slot1
parameter_list|,
name|int
name|slot2
parameter_list|)
block|{
return|return
name|values
index|[
name|slot1
index|]
operator|-
name|values
index|[
name|slot2
index|]
return|;
comment|// values will be positive... no overflow possible.
block|}
annotation|@
name|Override
specifier|public
name|void
name|setBottom
parameter_list|(
name|int
name|slot
parameter_list|)
block|{
name|bottomVal
operator|=
name|values
index|[
name|slot
index|]
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setTopValue
parameter_list|(
name|Integer
name|value
parameter_list|)
block|{
name|topVal
operator|=
name|value
operator|.
name|intValue
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|compareBottom
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
name|bottomVal
operator|-
name|hash
argument_list|(
name|doc
operator|+
name|seed
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|copy
parameter_list|(
name|int
name|slot
parameter_list|,
name|int
name|doc
parameter_list|)
block|{
name|values
index|[
name|slot
index|]
operator|=
name|hash
argument_list|(
name|doc
operator|+
name|seed
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|doSetNextReader
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|)
block|{
name|seed
operator|=
name|getSeed
argument_list|(
name|fieldname
argument_list|,
name|context
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Integer
name|value
parameter_list|(
name|int
name|slot
parameter_list|)
block|{
return|return
name|values
index|[
name|slot
index|]
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|compareTop
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
comment|// values will be positive... no overflow possible.
return|return
name|topVal
operator|-
name|hash
argument_list|(
name|doc
operator|+
name|seed
argument_list|)
return|;
block|}
block|}
return|;
block|}
block|}
decl_stmt|;
DECL|class|RandomValueSource
specifier|public
class|class
name|RandomValueSource
extends|extends
name|ValueSource
block|{
DECL|field|field
specifier|private
specifier|final
name|String
name|field
decl_stmt|;
DECL|method|RandomValueSource
specifier|public
name|RandomValueSource
parameter_list|(
name|String
name|field
parameter_list|)
block|{
name|this
operator|.
name|field
operator|=
name|field
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|description
specifier|public
name|String
name|description
parameter_list|()
block|{
return|return
name|field
return|;
block|}
annotation|@
name|Override
DECL|method|getValues
specifier|public
name|FunctionValues
name|getValues
parameter_list|(
name|Map
name|context
parameter_list|,
specifier|final
name|LeafReaderContext
name|readerContext
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|IntDocValues
argument_list|(
name|this
argument_list|)
block|{
specifier|private
specifier|final
name|int
name|seed
init|=
name|getSeed
argument_list|(
name|field
argument_list|,
name|readerContext
argument_list|)
decl_stmt|;
annotation|@
name|Override
specifier|public
name|int
name|intVal
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
name|hash
argument_list|(
name|doc
operator|+
name|seed
argument_list|)
return|;
block|}
block|}
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
name|o
parameter_list|)
block|{
if|if
condition|(
operator|!
operator|(
name|o
operator|instanceof
name|RandomValueSource
operator|)
condition|)
return|return
literal|false
return|;
name|RandomValueSource
name|other
init|=
operator|(
name|RandomValueSource
operator|)
name|o
decl_stmt|;
return|return
name|this
operator|.
name|field
operator|.
name|equals
argument_list|(
name|other
operator|.
name|field
argument_list|)
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
name|field
operator|.
name|hashCode
argument_list|()
return|;
block|}
empty_stmt|;
block|}
block|}
end_class
end_unit
